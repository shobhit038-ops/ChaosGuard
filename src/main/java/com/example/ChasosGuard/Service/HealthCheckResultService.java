package com.example.ChasosGuard.Service;
import com.example.ChasosGuard.DTO.HealthcheckDTO;
import com.example.ChasosGuard.Model.*;
import com.example.ChasosGuard.Repositry.ApplicationStatusHistoryRepo;
import com.example.ChasosGuard.Repositry.HealthCheckResultRepo;
import com.example.ChasosGuard.Repositry.MonitoredApplicationRepo;
import com.example.ChasosGuard.Repositry.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class HealthCheckResultService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    @Autowired
    private MonitoredApplicationRepo monitoredApplicationRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ApplicationStatusHistoryRepo applicationStatusHistoryRepo;
    @Autowired
    private HealthCheckResultRepo healthCheckResultRepo;

    public ResponseEntity<List<HealthcheckDTO>> getenabledAllApplications(String name) {
        User user = userRepo.findByName(name);

        if (user == null) {
            throw new RuntimeException("No user found for that name");
        }

        List<MonitoredApplication> apps =
                monitoredApplicationRepo.findByOwnerAndEnabledTrue(user);

        List<HealthcheckDTO> results = new ArrayList<>();

        for (MonitoredApplication app : apps) {

            healthCheckResultRepo
                    .findTopByApplication_IdOrderByCheckedAtDesc(app.getId())
                    .ifPresent(result -> results.add(
                            new HealthcheckDTO(
                                    result.getCheckedAt(),
                                    result.isSuccess(),
                                    result.getHttpStatusCode(),
                                    result.getResponseTimeMs(),
                                    result.getErrorMessage()
                            )
                    ));
        }

        return ResponseEntity.ok(results);
    }

    @Transactional
    public ResponseEntity<HealthcheckDTO> manualCheckApplication(String name, String username) {
        // 1. Find user
        User user = userRepo.findByName(username);

        if (user == null) {
            throw new RuntimeException("No user found");
        }

        // 2. Find monitored application
        MonitoredApplication app =
                monitoredApplicationRepo.findByNameAndOwner(name, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found: " + name
                                )
                        );

        // 3. Check monitoring enabled
        if (!app.isEnabled()) {
            throw new IllegalStateException(
                    "Monitoring is disabled for " + name
            );
        }

        Instant checkedAt = Instant.now();

        long startTime = System.nanoTime();

        boolean success = false;
        Integer statusCode = null;
        String errorMessage = null;

        // 4. Execute HTTP health check
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(app.getHealthUrl()))
                    .timeout(Duration.ofMillis(
                            app.getTimeoutMillis() == null
                                    ? 5000
                                    : app.getTimeoutMillis()
                    ))
                    .GET()
                    .build();

            HttpResponse<Void> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            statusCode = response.statusCode();

            success = statusCode >= 200 && statusCode < 300;

            if (!success) {
                errorMessage = "HTTP status: " + statusCode;
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            errorMessage = "Health check interrupted";

        } catch (Exception e) {

            errorMessage = e.getMessage() != null
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
        }

        // 5. Calculate response time
        long responseTimeMs = Duration.ofNanos(
                System.nanoTime() - startTime
        ).toMillis();


        // 6. Save health check result
        HealthCheckResult result = new HealthCheckResult(
                null,
                checkedAt,
                success,
                statusCode,
                responseTimeMs,
                errorMessage,
                app
        );

        healthCheckResultRepo.save(result);


        // 7. Update application status and failure count
        ApplicationStatus prevStatus = app.getCurrentStatus();

        // Null safety for consecutive failures
        int failures = app.getConsecutiveFailures() == null
                ? 0
                : app.getConsecutiveFailures();

        if (success) {

            // Health check successful
            app.setConsecutiveFailures(0);
            app.setCurrentStatus(ApplicationStatus.UP);

        } else {

            // Health check failed
            failures++;

            app.setConsecutiveFailures(failures);

            // Check failure threshold
            if (failures >= app.getFailureThreshold()) {

                app.setCurrentStatus(ApplicationStatus.DOWN);
            }
        }


        // 8. Save application status history
        ApplicationStatus newStatus = app.getCurrentStatus();

        if (prevStatus != newStatus) {

            ApplicationStatusHistory history =
                    new ApplicationStatusHistory(
                            null,
                            app,
                            newStatus,
                            Instant.now()
                    );

            applicationStatusHistoryRepo.save(history);
        }


        // 9. Save updated monitored application
        monitoredApplicationRepo.save(app);


        // 10. Prepare response DTO
        HealthcheckDTO dto = new HealthcheckDTO();

        dto.setCheckedAt(checkedAt);
        dto.setSuccess(success);
        dto.setHttpStatusCode(statusCode);
        dto.setResponseTimeMs(responseTimeMs);
        dto.setErrorMessage(errorMessage);

        return ResponseEntity.ok(dto);
//        User user = userRepo.findByName(username);
//        if(user==null){
//            throw new RuntimeException("no user found");
//        }
//        MonitoredApplication app = monitoredApplicationRepo.findByNameAndOwner(name,user)
//                .orElseThrow(
//                        ()->new RuntimeException("Application not found: "+name)
//                );
//        if(!app.isEnabled()){
//            throw new IllegalStateException("Monitoring is disabled for "+name);
//        }
//        Instant checkedAt = Instant.now();
//        long startTime = System.nanoTime();
//
//        boolean success = false;
//        Integer statusCode = null;
//        String errorMessage = null;
//
//        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(app.getHealthUrl()))
//                    .timeout(Duration.ofMillis(
//                            app.getTimeoutMillis()))
//                    .GET()
//                    .build();
//
//            HttpResponse<Void> response =httpClient.send(
//                    request,
//                    HttpResponse.BodyHandlers.discarding());
//
//            statusCode = response.statusCode();
//            success = statusCode>=200 && statusCode<300;
//
//            if(!success){
//                errorMessage = "HTTP status: " + statusCode;
//            }
//        }
//        catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            errorMessage = "Health check interrupted";
//
//        } catch (Exception e) {
//            errorMessage = e.getMessage() != null
//                    ? e.getMessage()
//                    : e.getClass().getSimpleName();
//        }
//
//        // 3. Response time calculate karo
//        long responseTimeMs = Duration.ofNanos(
//                System.nanoTime() - startTime).toMillis();
//
//        // 4. Tere HealthCheckResult model mein result save karo
//        HealthCheckResult result = new HealthCheckResult(
//                null,
//                checkedAt,
//                success,
//                statusCode,
//                responseTimeMs,
//                errorMessage,
//                app
//        );
//
//        healthCheckResultRepo.save(result);
//
//        // 5. Application ka status aur failure count update karo aur applicationstaushistory update kro
//        ApplicationStatus prevStatus = app.getCurrentStatus();
//        if (success) {
//            app.setConsecutiveFailures(0);
//            app.setCurrentStatus(ApplicationStatus.UP);
//        } else {
//            app.setConsecutiveFailures(
//                    app.getConsecutiveFailures() + 1);
//
//            if (app.getConsecutiveFailures()
//                    >= app.getFailureThreshold()) {
//                app.setCurrentStatus(ApplicationStatus.DOWN);
//            }
//        }
//        ApplicationStatus newStatus = app.getCurrentStatus();
//        if(prevStatus!=newStatus){
//            ApplicationStatusHistory history =
//                    new ApplicationStatusHistory(
//                            null,
//                            app,
//                            newStatus,
//                            Instant.now()
//                    );
//
//            applicationStatusHistoryRepo.save(history);
//        }
//
//        monitoredApplicationRepo.save(app);
//
//        HealthcheckDTO dto = new HealthcheckDTO();
//        dto.setCheckedAt(checkedAt);
//        dto.setSuccess(success);
//        dto.setHttpStatusCode(statusCode);
//        dto.setResponseTimeMs(responseTimeMs);
//        dto.setErrorMessage(errorMessage);
//
//        return ResponseEntity.ok(dto);
    }
    @Transactional
    public ResponseEntity<String> getStatusofApplication(String name, String username){
        User user = userRepo.findByName(username);
        if(user==null){
            throw new RuntimeException("User not found");
        }
        MonitoredApplication app = monitoredApplicationRepo.findByNameAndOwner(name,user).orElseThrow(
                () -> new RuntimeException("application name not found")
        );

        String status = app.getCurrentStatus().toString();

        return ResponseEntity.ok(status);
    }
    public ResponseEntity<List<HealthcheckDTO>> CheckAllEnabledApplication(String name) {
        User user = userRepo.findByName(name);
        if(user==null){
            throw new RuntimeException("User Not found");
        }
        List<MonitoredApplication> applications = monitoredApplicationRepo.
                findByOwnerAndEnabledTrue(user);
        List<HealthcheckDTO> results = new ArrayList<>();
        for(MonitoredApplication app : applications){
            ResponseEntity<HealthcheckDTO> response =
                    manualCheckApplication(app.getName(),name);
            results.add(response.getBody());
        }
        return ResponseEntity.ok(results);
    }

    public ResponseEntity<List<HealthcheckDTO>> getHistoryofHealthOfapplication(String name, String username) {
        User user = userRepo.findByName(username);
        if(user==null){
            throw new RuntimeException("User not found");
        }
        MonitoredApplication apps = monitoredApplicationRepo.findByNameAndOwner(name,user).orElseThrow(
                () -> new RuntimeException("Application not found: "+name));
        List<HealthCheckResult> history = healthCheckResultRepo.findTop20ByApplicationOrderByCheckedAtDesc(apps);
        List<HealthcheckDTO> results = history.stream()
                .map(result -> new HealthcheckDTO(
                        result.getCheckedAt(),
                        result.isSuccess(),
                        result.getHttpStatusCode(),
                        result.getResponseTimeMs(),
                        result.getErrorMessage()
                ))
                .toList();
        return ResponseEntity.ok(results);
    }



    public ResponseEntity<Map<String, Object>> getUptime(String name, String username) {
        User user = userRepo.findByName(username);
        if(user==null){
            throw new RuntimeException("User not found");
        }
        MonitoredApplication app = monitoredApplicationRepo.findByNameAndOwner(name,user)
                           .orElseThrow(() -> new RuntimeException("Application not found"));
        long totalChecks = healthCheckResultRepo.countByApplication(app);
        long successfulChecks = healthCheckResultRepo.countByApplicationAndSuccess(app,true);
        double uptime = 0.0;

        if(totalChecks>0){
            uptime = (double) (successfulChecks * 100) /totalChecks;
        }
        Map<String,Object> response = new LinkedHashMap<>();
        response.put("applicationName",app.getName());
        response.put("TotalChecks",totalChecks);
        response.put("succesfulChecks",successfulChecks);
        response.put("failedChecks",totalChecks-successfulChecks);
        response.put("uptimePercentage",Math.round(uptime*100.0)/100.0);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getAvailability(String name, String period, String username) {
        User user = userRepo.findByName(username);
        if(user==null){
            throw new RuntimeException("user was not found");
        }
        MonitoredApplication app = monitoredApplicationRepo.findByNameAndOwner(name,user).orElseThrow(
                                                               () -> new RuntimeException("Application not found: "+name));

        Instant endTime = Instant.now();
        Instant startTime;

        switch (period.toLowerCase()) {
            case "24h":
                startTime = endTime.minus(Duration.ofHours(24));
                break;

            case "7d":
                startTime = endTime.minus(Duration.ofDays(7));
                break;

            case "30d":
                startTime = endTime.minus(Duration.ofDays(30));
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid period. Use 24h, 7d or 30d");
        }

        // 4. Start time se pehle ka latest status find karo
        Optional<ApplicationStatusHistory> previousHistory =
                applicationStatusHistoryRepo
                        .findTopByMonitoredApplicationAndChangedAtLessThanEqualOrderByChangedAtDesc(
                                app, startTime);

        ApplicationStatus currentStatus = previousHistory
                .map(ApplicationStatusHistory::getStatus)
                .orElse(null);

        // 5. Selected period ke andar ke status changes fetch karo
        List<ApplicationStatusHistory> histories =
                applicationStatusHistoryRepo
                        .findByMonitoredApplicationAndChangedAtBetweenOrderByChangedAtAsc(
                                app, startTime, endTime);

        // 6. Duration counters
        long upMillis = 0;
        long downMillis = 0;
        long pendingMillis = 0;

        Instant cursor = startTime;

        // 7. Har status kitni der raha, calculate karo
        for (ApplicationStatusHistory history : histories) {

            Instant changedAt = history.getChangedAt();

            if (currentStatus != null) {

                long duration = Duration.between(
                        cursor, changedAt).toMillis();

                if (currentStatus == ApplicationStatus.UP) {
                    upMillis += duration;

                } else if (currentStatus == ApplicationStatus.DOWN) {
                    downMillis += duration;

                } else if (currentStatus == ApplicationStatus.PENDING) {
                    pendingMillis += duration;
                }
            }

            // Ab next interval ka status update karo
            currentStatus = history.getStatus();
            cursor = changedAt;
        }

        // 8. Last status ki duration end time tak calculate karo
        if (currentStatus != null) {

            long duration = Duration.between(
                    cursor, endTime).toMillis();

            if (currentStatus == ApplicationStatus.UP) {
                upMillis += duration;

            } else if (currentStatus == ApplicationStatus.DOWN) {
                downMillis += duration;

            } else if (currentStatus == ApplicationStatus.PENDING) {
                pendingMillis += duration;
            }
        }

        // 9. Total known duration
        long knownMillis = upMillis + downMillis + pendingMillis;

        long totalPeriodMillis = Duration.between(
                startTime, endTime).toMillis();

        long unknownMillis = totalPeriodMillis - knownMillis;

        // 10. Availability calculate karo
        double availability = 0.0;

        if (knownMillis > 0) {
            availability = (upMillis * 100.0) / knownMillis;
        }

        // 11. Response prepare karo
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("applicationName", app.getName());
        response.put("period", period);
        response.put("upDurationHours",
                Math.round(upMillis / 3600000.0 * 100.0) / 100.0);
        response.put("downDurationHours",
                Math.round(downMillis / 3600000.0 * 100.0) / 100.0);
        response.put("pendingDurationHours",
                Math.round(pendingMillis / 3600000.0 * 100.0) / 100.0);
        response.put("unknownDurationHours",
                Math.round(unknownMillis / 3600000.0 * 100.0) / 100.0);
        response.put("availabilityPercentage",
                Math.round(availability * 100.0) / 100.0);

        return ResponseEntity.ok(response);
    }

    public void runScheduledCheck(MonitoredApplication app) {

    }
}
