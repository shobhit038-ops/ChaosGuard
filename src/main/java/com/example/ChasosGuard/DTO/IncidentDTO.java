package com.example.ChasosGuard.DTO;

import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IncidentDTO {
    private Long id;
    private String applicationName;
    private String healthUrl;
    private Instant startedAt;
    private long durationMinutes;
    private int consecutiveFailures;
}
