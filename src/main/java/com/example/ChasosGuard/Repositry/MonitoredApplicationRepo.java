package com.example.ChasosGuard.Repositry;

import com.example.ChasosGuard.Model.ApplicationStatus;
import com.example.ChasosGuard.Model.HealthCheckResult;
import com.example.ChasosGuard.Model.MonitoredApplication;
import com.example.ChasosGuard.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MonitoredApplicationRepo extends JpaRepository<MonitoredApplication,Long> {
    Optional<MonitoredApplication> findByName(String name);
    Optional<MonitoredApplication> findByNameAndOwner(
            String name, User owner);
    List<MonitoredApplication> findByOwner(User user);
    List<MonitoredApplication> findByEnabledTrue();
    List<MonitoredApplication> findByOwnerAndCurrentStatus(
            User owner,
            ApplicationStatus currentStatus
    );
    List<MonitoredApplication> findByOwnerAndEnabledTrue(User owner);
    @Query("""
    SELECT a
    FROM MonitoredApplication a
    JOIN FETCH a.owner
    WHERE a.enabled = true
""")
    List<MonitoredApplication> findEnabledApplicationsWithOwner();
}
