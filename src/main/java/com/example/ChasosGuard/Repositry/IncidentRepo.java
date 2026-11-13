package com.example.ChasosGuard.Repositry;

import com.example.ChasosGuard.Model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepo extends JpaRepository<Incident,Long> {
}
