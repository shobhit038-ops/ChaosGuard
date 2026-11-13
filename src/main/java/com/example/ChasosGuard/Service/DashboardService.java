package com.example.ChasosGuard.Service;

import com.example.ChasosGuard.DTO.ActivityFeedDTO;
import com.example.ChasosGuard.DTO.ApplicationGridDTO;
import com.example.ChasosGuard.DTO.IncidentDTO;
import com.example.ChasosGuard.DTO.RecentFailureDTO;
import com.example.ChasosGuard.Model.*;
import com.example.ChasosGuard.Repositry.ApplicationStatusHistoryRepo;
import com.example.ChasosGuard.Repositry.HealthCheckResultRepo;
import com.example.ChasosGuard.Repositry.MonitoredApplicationRepo;
import com.example.ChasosGuard.Repositry.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MonitoredApplicationRepo monitoredApplicationRepo;
    @Autowired
    private HealthCheckResultRepo healthCheckResultRepo;
    @Autowired
    private ApplicationStatusHistoryRepo applicationStatusHistoryRepo;

    @Transactional
    public ResponseEntity<Map<String, Long>> getsummaryofapplication(String username) {
        User user = userRepo.findByName(username);
        if(user==null){
            throw new RuntimeException("User not found");
        }
        List<MonitoredApplication> apps = monitoredApplicationRepo.findByOwner(user);
        long totalapps = apps.size();

        long enabled = apps.stream()
                .filter(MonitoredApplication::isEnabled)
                .count();

        long upapps = apps.stream()
                .filter(app -> app.getCurrentStatus()
                        == ApplicationStatus.UP)
                .count();

        long downapps = apps.stream()
                .filter(app -> app.getCurrentStatus()
                        == ApplicationStatus.DOWN)
                .count();

        long pendindapps = apps.stream()
                .filter(app -> app.getCurrentStatus()
                        == ApplicationStatus.PENDING)
                .count();

        Map<String,Long> summary = new LinkedHashMap<>();
        summary.put("totalApplications",totalapps);
        summary.put("enabledApplications",enabled);
        summary.put("upApplications",upapps);
        summary.put("downApplications",downapps);
        summary.put("pendingApplications",pendindapps);
        return ResponseEntity.ok(summary);
    }

    public ResponseEntity<List<RecentFailureDTO>> getallRecentFailures(String username) {
        User user = userRepo.findByName(username);
        if(user==null){
            throw new RuntimeException("No user was found");
        }
        List<HealthCheckResult> failures = healthCheckResultRepo.
                findTop10ByApplication_OwnerAndSuccessFalseOrderByCheckedAtDesc(
                        user
                );

        List<RecentFailureDTO> response = failures.stream()
                .map(result -> new RecentFailureDTO(
                        result.getApplication().getName(),
                        result.getCheckedAt(),
                        result.getHttpStatusCode(),
                        result.getResponseTimeMs(),
                        result.getErrorMessage()
                )).toList();
        return ResponseEntity.ok(response);
    }


    @Transactional(readOnly = true)
    public ResponseEntity<List<ApplicationGridDTO>> getApplicationGrid(String username) {
        User user = userRepo.findByName(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Step 2: Fetch all applications owned by this user
        List<MonitoredApplication> applications =
                monitoredApplicationRepo.findByOwner(user);

        // Step 3: Convert application entities into DTOs
        List<ApplicationGridDTO> response = applications.stream()
                .map(app -> {

                    // Fetch latest health check of this application
                    Optional<HealthCheckResult> latestCheck =
                            healthCheckResultRepo
                                    .findTopByApplicationOrderByCheckedAtDesc(app);

                    // Create DTO
                    return new ApplicationGridDTO(
                            app.getId(),
                            app.getName(),
                            app.getHealthUrl(),
                            app.getCurrentStatus(),
                            app.isEnabled(),

                            latestCheck
                                    .map(HealthCheckResult::getCheckedAt)
                                    .orElse(null),

                            latestCheck
                                    .map(HealthCheckResult::getResponseTimeMs)
                                    .orElse(null)
                    );
                })
                .toList();

        // Step 4: Return response
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<IncidentDTO>> getActiveIncidents(String username){
        // Step 1: Find authenticated user
        User user = userRepo.findByName(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Step 2: Fetch user's applications
        List<MonitoredApplication> applications =
                monitoredApplicationRepo.findByOwner(user);
        Instant now = Instant.now();

        // Step 3: Filter only DOWN applications
        List<IncidentDTO> response = applications.stream()
                .filter(app ->
                        app.getCurrentStatus() == ApplicationStatus.DOWN)
                .map(app -> {
                    // Step 4: Find latest DOWN transition
                    Instant startedAt =
                            applicationStatusHistoryRepo
                                    .findTopByMonitoredApplicationAndStatusOrderByChangedAtDesc(
                                            app,
                                            ApplicationStatus.DOWN
                                    )
                                    .map(ApplicationStatusHistory::getChangedAt)
                                    .orElse(null);

                    // Step 5: Calculate incident duration
                    long duration =
                            startedAt == null
                            ? 0 : Duration.between(startedAt, now).toMinutes();

                    // Step 6: Create DTO
                    return new IncidentDTO(
                            app.getId(),
                            app.getName(),
                            app.getHealthUrl(),
                            startedAt,
                            duration,
                            app.getConsecutiveFailures()
                    );
                })
                .toList();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<ActivityFeedDTO>> getActivityFeed(String username) {
       User user = userRepo.findByName(username);
       if(user == null){
           throw new RuntimeException("User not found");
       }
       List<ApplicationStatusHistory> histories = applicationStatusHistoryRepo
               .findTop20ByMonitoredApplication_OwnerOrderByChangedAtDesc(
                       user
               );


       List<ActivityFeedDTO> response = histories.stream()
               .map(history -> new ActivityFeedDTO(
                       history.getMonitoredApplication().getName(),
                       history.getStatus(),
                       history.getChangedAt()
               )).toList();

       return ResponseEntity.ok(response);
    }
}
