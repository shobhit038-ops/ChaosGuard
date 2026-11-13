package com.example.ChasosGuard.DTO;

import com.example.ChasosGuard.Model.ApplicationStatus;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonitoredApplicationDTO {
    String name;
    String healthUrl;
    Integer monitoringIntervalSeconds;
    Integer timeoutMillis;
    Integer failureThreshold;
    ApplicationStatus currentStatus;
}
