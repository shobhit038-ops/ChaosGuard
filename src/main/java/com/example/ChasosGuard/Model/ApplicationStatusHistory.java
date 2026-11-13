package com.example.ChasosGuard.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private MonitoredApplication monitoredApplication;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private Instant changedAt;
}
