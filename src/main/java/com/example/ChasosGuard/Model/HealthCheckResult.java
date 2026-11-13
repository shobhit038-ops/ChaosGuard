package com.example.ChasosGuard.Model;
import java.time.Instant;
import java.util.*;

import jakarta.persistence.*;

@Entity
public class HealthCheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant checkedAt;
    private boolean success;
    private Integer httpStatusCode;
    private Long responseTimeMs;
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private MonitoredApplication application;

    public HealthCheckResult() {
    }

    public HealthCheckResult(Long id, Instant checkedAt, boolean success, Integer httpStatusCode, Long responseTimeMs, String errorMessage, MonitoredApplication application) {
        this.id = id;
        this.checkedAt = checkedAt;
        this.success = success;
        this.httpStatusCode = httpStatusCode;
        this.responseTimeMs = responseTimeMs;
        this.errorMessage = errorMessage;
        this.application = application;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Instant checkedAt) {
        this.checkedAt = checkedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MonitoredApplication getApplication() {
        return application;
    }

    public void setApplication(MonitoredApplication application) {
        this.application = application;
    }
}
