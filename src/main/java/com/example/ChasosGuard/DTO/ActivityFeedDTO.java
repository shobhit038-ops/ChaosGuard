package com.example.ChasosGuard.DTO;

import com.example.ChasosGuard.Model.ApplicationStatus;
import lombok.*;

import java.time.Instant;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityFeedDTO {
    private String applicationName;
    private ApplicationStatus status;
    private Instant changedAt;
}
