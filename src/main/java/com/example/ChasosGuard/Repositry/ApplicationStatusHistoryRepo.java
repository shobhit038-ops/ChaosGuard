package com.example.ChasosGuard.Repositry;

import com.example.ChasosGuard.Model.ApplicationStatus;
import com.example.ChasosGuard.Model.ApplicationStatusHistory;
import com.example.ChasosGuard.Model.MonitoredApplication;
import com.example.ChasosGuard.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.*;
@Repository
public interface ApplicationStatusHistoryRepo  extends JpaRepository<ApplicationStatusHistory,Long> {
    List<ApplicationStatusHistory>
    findByMonitoredApplicationAndChangedAtBetweenOrderByChangedAtAsc(
            MonitoredApplication application,
            Instant startTime,
            Instant endTime
    );
    Optional<ApplicationStatusHistory>
    findTopByMonitoredApplicationAndChangedAtLessThanEqualOrderByChangedAtDesc(
            MonitoredApplication application,
            Instant startTime
    );
    List<ApplicationStatusHistory>
    findTop20ByMonitoredApplication_OwnerOrderByChangedAtDesc(
            User owner
    );
    Optional<ApplicationStatusHistory>
    findTopByMonitoredApplicationAndStatusOrderByChangedAtDesc(
            MonitoredApplication application,
            ApplicationStatus status
    );
}
