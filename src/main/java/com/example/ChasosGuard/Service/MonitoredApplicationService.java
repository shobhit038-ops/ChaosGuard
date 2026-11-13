package com.example.ChasosGuard.Service;

import com.example.ChasosGuard.DTO.HealthcheckDTO;
import com.example.ChasosGuard.DTO.MonitoredApplicationDTO;
import com.example.ChasosGuard.Model.ApplicationStatus;
import com.example.ChasosGuard.Model.MonitoredApplication;
import com.example.ChasosGuard.Model.User;
import com.example.ChasosGuard.Repositry.MonitoredApplicationRepo;
import com.example.ChasosGuard.Repositry.UserRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonitoredApplicationService {
    @Autowired
    private MonitoredApplicationRepo monitoredApplicationRepo;
    @Autowired
    private UserRepo userRepo;

    public MonitoredApplication registerApplication(@Valid MonitoredApplication app, String name) {
        User user = userRepo.findByName(name);
        app.setOwner(user);
        app.setCurrentStatus(ApplicationStatus.PENDING);
        app.setConsecutiveFailures(0);
        app.setEnabled(true);

        return monitoredApplicationRepo.save(app);
    }

    public MonitoredApplication getAppByName(@Valid String name,String username) {
        User user = userRepo.findByName(username);
        if(user==null){
            throw new RuntimeException(" ");
        }
         return monitoredApplicationRepo.findByNameAndOwner(name,user).orElseThrow(()->new RuntimeException("Application not Found"));
    }

    public ResponseEntity<List<MonitoredApplicationDTO>> getAllApp(String name) {
        User user = userRepo.findByName(name);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        List<MonitoredApplication> applications =
                monitoredApplicationRepo.findByOwner(user);
        List<MonitoredApplicationDTO> result = applications.stream()
                .map(app -> new MonitoredApplicationDTO(
                        app.getName(),
                        app.getHealthUrl(),
                        app.getMonitoringIntervalSeconds(),
                        app.getTimeoutMillis(),
                        app.getFailureThreshold(),
                        app.getCurrentStatus()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<String> UpdateMonitoredApplication(@Valid String name,MonitoredApplication monitoredApplication,String username) {
        User user = userRepo.findByName(username);
        if(user==null){
            throw new RuntimeException("no monitor found for that user");
        }

        MonitoredApplication app = monitoredApplicationRepo.findByNameAndOwner(name,user)
                .orElseThrow(
                        ()->new RuntimeException("Monitored Apllication of that name was not found")
                );
        app.setName(monitoredApplication.getName());
        app.setHealthUrl(monitoredApplication.getHealthUrl());
        app.setMonitoringIntervalSeconds(monitoredApplication.getMonitoringIntervalSeconds());
        app.setTimeoutMillis(monitoredApplication.getTimeoutMillis());
        app.setFailureThreshold(monitoredApplication.getFailureThreshold());
        app.setConsecutiveFailures(monitoredApplication.getConsecutiveFailures());
        monitoredApplicationRepo.save(app);
        return ResponseEntity.ok("your App was Updated");
    }

    public void setMonitorEnabled(String name, Boolean enabled, String name1) {
        if(enabled==null){
            throw new IllegalArgumentException("Enable Value is required");
        }
        User user = userRepo.findByName(name1);
        if(user==null){
            throw new RuntimeException("no user is logged in");
        }

        MonitoredApplication app =
                monitoredApplicationRepo.findByNameAndOwner(name, user)
                        .orElseThrow(() ->
                                new RuntimeException("Monitor not found"));

        app.setEnabled(enabled);
        monitoredApplicationRepo.save(app);
    }

    public ResponseEntity<List<MonitoredApplicationDTO>> getAllUpApplications(String name) {
        User user = userRepo.findByName(name);
        if(user==null){
            throw new RuntimeException("User was not found");
        }
        List<MonitoredApplication> apps = monitoredApplicationRepo.findByOwnerAndCurrentStatus(user,ApplicationStatus.UP);
        List<MonitoredApplicationDTO> result = apps.stream()
                .map(app -> new MonitoredApplicationDTO(
                        app.getName(),
                        app.getHealthUrl(),
                        app.getMonitoringIntervalSeconds(),
                        app.getTimeoutMillis(),
                        app.getFailureThreshold(),
                        app.getCurrentStatus()
                )).toList();
        return ResponseEntity.ok(result);
    }


    public ResponseEntity<List<MonitoredApplicationDTO>> getAllDownApplications(String name) {
        User user = userRepo.findByName(name);
        if(user==null){
            throw new RuntimeException("User was not found");
        }
        List<MonitoredApplication> apps = monitoredApplicationRepo.findByOwnerAndCurrentStatus(user,ApplicationStatus.DOWN);
        List<MonitoredApplicationDTO> result = apps.stream()
                .map(app -> new MonitoredApplicationDTO(
                        app.getName(),
                        app.getHealthUrl(),
                        app.getMonitoringIntervalSeconds(),
                        app.getTimeoutMillis(),
                        app.getFailureThreshold(),
                        app.getCurrentStatus()
                )).toList();
        return ResponseEntity.ok(result);
    }
}