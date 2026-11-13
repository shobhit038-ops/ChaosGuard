package com.example.ChasosGuard.DTO;
import lombok.*;
import java.time.Instant;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecentFailureDTO {
    private String applicationName;
    private Instant checkedAt;
    private Integer httpStatusCode;
    private Long responseTimeMs;
    private String errorMessage;
}