package com.example.ChasosGuard.Model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private MonitoredApplication application;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    private String failureReason;
    private Instant startedAt;
    private Instant resolvedAt;

    public Incident() {}

    public Incident(Long id, MonitoredApplication application, IncidentStatus status, String failureReason, Instant startedAt, Instant resolvedAt) {
        this.id = id;
        this.application = application;
        this.status = status;
        this.failureReason = failureReason;
        this.startedAt = startedAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MonitoredApplication getApplication() {
        return application;
    }

    public void setApplication(MonitoredApplication application) {
        this.application = application;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
