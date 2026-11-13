package com.example.ChasosGuard.DTO;

import com.example.ChasosGuard.Model.ApplicationStatus;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationGridDTO {
    private Long id;
    private String name;
    private String healthUrl;
    private ApplicationStatus status;
    private boolean enabled;
    private Instant lastCheckedAt;
    private Long responseTimeMs;
}
