package com.example.ChasosGuard.Repositry;

import com.example.ChasosGuard.Model.HealthCheckResult;
import com.example.ChasosGuard.Model.MonitoredApplication;
import com.example.ChasosGuard.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthCheckResultRepo extends JpaRepository<HealthCheckResult,Long>{
    List<HealthCheckResult> findTop20ByApplication_IdOrderByCheckedAtDesc(Long applicationId);
    List<HealthCheckResult>
    findTop20ByApplicationOrderByCheckedAtDesc(
            MonitoredApplication application
    );
    Optional<HealthCheckResult> findTopByApplication_IdOrderByCheckedAtDesc(MonitoredApplication app);
    long countByApplication(MonitoredApplication application);
    long countByApplicationAndSuccess(
            MonitoredApplication application,
            boolean success
    );
    Optional<HealthCheckResult> findTopByApplicationOrderByCheckedAtDesc(MonitoredApplication application);
    List<HealthCheckResult> findTop10ByApplication_OwnerAndSuccessFalseOrderByCheckedAtDesc(
            User owner
    );
    Optional<HealthCheckResult>
    findTopByApplication_IdOrderByCheckedAtDesc(Long applicationId);

    List<HealthCheckResult>
    findTopByApplication_OwnerAndSuccessFalseOrderByCheckedAtDesc(
            User owner, Pageable pageable
    );

}