package com.example.ChasosGuard.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.*;
import java.util.ArrayList;

@Entity
public class MonitoredApplication {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 2048)
    private String healthUrl;

    @NotNull
    @Min(10)
    @Max(3600)
    private Integer monitoringIntervalSeconds = 60;

    @NotNull
    @Min(500)
    @Max(30000)
    private Integer timeoutMillis = 5000;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer failureThreshold = 3;

    private Integer consecutiveFailures = 0;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus currentStatus = ApplicationStatus.PENDING;

    private boolean enabled = true;
    private Instant nextCheckAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "application")
    private List<HealthCheckResult> checkResults = new ArrayList<>();
    @PrePersist
    public void initializeDefaults() {

        if (consecutiveFailures == null) {
            consecutiveFailures = 0;
        }

        if (currentStatus == null) {
            currentStatus = ApplicationStatus.PENDING;
        }
    }

    public MonitoredApplication() {
    }

    public Instant getNextCheckAt() {
        return nextCheckAt;
    }

    public void setNextCheckAt(Instant nextCheckAt) {
        this.nextCheckAt = nextCheckAt;
    }

    public MonitoredApplication(Long id, String name, String healthUrl, Integer monitoringIntervalSeconds, Integer timeoutMillis, Integer failureThreshold, Integer consecutiveFailures, ApplicationStatus currentStatus, boolean enabled, Instant nextCheckAt, User owner, List<HealthCheckResult> checkResults) {
        this.id = id;
        this.name = name;
        this.healthUrl = healthUrl;
        this.monitoringIntervalSeconds = monitoringIntervalSeconds;
        this.timeoutMillis = timeoutMillis;
        this.failureThreshold = failureThreshold;
        this.consecutiveFailures = consecutiveFailures;
        this.currentStatus = currentStatus;
        this.enabled = enabled;
        this.nextCheckAt = nextCheckAt;
        this.owner = owner;
        this.checkResults = checkResults;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHealthUrl() {
        return healthUrl;
    }

    public void setHealthUrl(String healthUrl) {
        this.healthUrl = healthUrl;
    }

    public Integer getMonitoringIntervalSeconds() {
        return monitoringIntervalSeconds;
    }

    public void setMonitoringIntervalSeconds(Integer monitoringIntervalSeconds) {
        this.monitoringIntervalSeconds = monitoringIntervalSeconds;
    }

    public Integer getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(Integer timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public Integer getFailureThreshold() {
        return failureThreshold;
    }

    public void setFailureThreshold(Integer failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public Integer getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void setConsecutiveFailures(Integer consecutiveFailures) {
        this.consecutiveFailures = consecutiveFailures;
    }

    public ApplicationStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ApplicationStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<HealthCheckResult> getCheckResults() {
        return checkResults;
    }

    public void setCheckResults(List<HealthCheckResult> checkResults) {
        this.checkResults = checkResults;
    }

}
