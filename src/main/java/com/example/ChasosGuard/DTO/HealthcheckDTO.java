package com.example.ChasosGuard.DTO;

import lombok.*;

import java.time.Instant;

@Data
public class HealthcheckDTO {
    private Instant checkedAt;
    private boolean success;
    private Integer httpStatusCode;
    private Long responseTimeMs;
    private String errorMessage;
    public HealthcheckDTO(){}
    public HealthcheckDTO(Instant checkedAt, boolean success, Integer httpStatusCode, Long responseTimeMs, String errorMessage) {
        this.checkedAt = checkedAt;
        this.success = success;
        this.httpStatusCode = httpStatusCode;
        this.responseTimeMs = responseTimeMs;
        this.errorMessage = errorMessage;
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
}
