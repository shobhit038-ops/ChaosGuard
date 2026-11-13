package com.example.ChasosGuard.Repositry;

import com.example.ChasosGuard.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByName(String name);
    boolean existsByName(String name);
    void deleteByName(String name);
}
