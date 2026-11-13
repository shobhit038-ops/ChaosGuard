//package com.example.ChasosGuard.Service;
//
//import com.example.ChasosGuard.Model.HealthCheckResult;
//import com.example.ChasosGuard.Model.MonitoredApplication;
//import com.example.ChasosGuard.Repositry.HealthCheckResultRepo;
//import com.example.ChasosGuard.Repositry.MonitoredApplicationRepo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class HealthCheckScheduler {
//    @Autowired
//    private MonitoredApplicationRepo monitoredApplicationRepo;
//    @Autowired
//    private HealthCheckResultRepo healthCheckResultRepo;
//    @Autowired
//    private HealthCheckResultService healthCheckResultService;
//
//    @Scheduled(fixedDelay = 5000)
//    public void runScheduledChecks(){
//        List<MonitoredApplication> applicaions = monitoredApplicationRepo.findByEnabledTrue();
//
//        Instant now = Instant.now();
//
//        for(MonitoredApplication app: applicaions) {
//            try {
//                Optional<HealthCheckResult> latestCheck = healthCheckResultRepo.findTopByApplicationOrderByCheckedAtDesc(app);
//
//                // Agar pehle koi health check nhi hua
//                if (latestCheck.isEmpty()) {
//                    healthCheckResultService.manualCheckApplication(
//                            app.getName(),
//                            app.getOwner().getName()
//                    );
//                    continue;
//                }
//                Instant lastCheckedAt = latestCheck.get().getCheckedAt();
//                long elapsedSeconds = Duration.between(lastCheckedAt, now).getSeconds();
//
//                if (elapsedSeconds >= app.getMonitoringIntervalSeconds()) {
//                    healthCheckResultService.manualCheckApplication(
//                            app.getName(),
//                            app.getOwner().getName()
//                    );
//                }
//            } catch (Exception e){
//                System.err.println(
//                        "Health check failed for " + app.getName()+": "+e.getMessage()
//                );
//            }
//        }
//
//    }
//}

package com.example.ChasosGuard.Service;

import com.example.ChasosGuard.Model.HealthCheckResult;
import com.example.ChasosGuard.Model.MonitoredApplication;
import com.example.ChasosGuard.Repositry.HealthCheckResultRepo;
import com.example.ChasosGuard.Repositry.MonitoredApplicationRepo;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class HealthCheckScheduler {
    @Autowired
    private MonitoredApplicationRepo monitoredApplicationRepo;
    @Autowired
    private HealthCheckResultRepo healthCheckResultRepo;
    @Autowired
    private HealthCheckResultService healthCheckResultService;


    @Scheduled(fixedDelay = 5000)
    public void runScheduledChecks() {
        // Step 1: Fetch all enabled applications with their owners
        List<MonitoredApplication> applications =
                monitoredApplicationRepo.findEnabledApplicationsWithOwner();
        Instant now = Instant.now();
        // Step 2: Iterate over every monitored application
        for (MonitoredApplication app : applications) {
            try {
                // Step 3: Find the latest health check result
                Optional<HealthCheckResult> latestCheck =
                        healthCheckResultRepo
                                .findTopByApplicationOrderByCheckedAtDesc(app);
                // Step 4: If no previous health check exists
                if (latestCheck.isEmpty()) {
                    healthCheckResultService.manualCheckApplication(
                            app.getName(),
                            app.getOwner().getName()
                    );
                    continue;
                }
                // Step 5: Get the last checked time
                Instant lastCheckedAt =
                        latestCheck.get().getCheckedAt();
                // Step 6: Calculate elapsed time
                long elapsedSeconds =
                        Duration.between(lastCheckedAt, now).getSeconds();
                // Step 7: Check monitoring interval
                if (elapsedSeconds >= app.getMonitoringIntervalSeconds()) {
                    healthCheckResultService.manualCheckApplication(
                            app.getName(),
                            app.getOwner().getName()
                    );
                }
            } catch (Exception e) {
                // Step 8: Handle errors for individual applications
                System.err.println(
                        "Health check failed for "
                                + app.getName()
                                + ": "
                                + e.getMessage()
                );
                e.printStackTrace();
            }
        }
    }
}
