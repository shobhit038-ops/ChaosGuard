package com.example.ChasosGuard.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Data
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
     String name;
     String email;
     Date createdAt;

    public UserDto(String name, String email, Date createdAt) {
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }
}
