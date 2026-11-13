package com.example.ChasosGuard.Service;

import com.example.ChasosGuard.DTO.UserDto;
import com.example.ChasosGuard.Model.User;
import com.example.ChasosGuard.Repositry.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    public ResponseEntity<UserDto> saveUser(User user) {
        userRepo.save(user);
        UserDto userDto = new UserDto(user.getName(), user.getEmail(),user.getCreatedAt());
        return  ResponseEntity.ok(userDto);
    }

    public ResponseEntity<UserDto> getUserByname(String name) {
        User user = userRepo.findByName(name);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserDto userDTO = new UserDto(
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );

        return ResponseEntity.ok(userDTO);
    }

    public ResponseEntity<UserDto> findbyId(Long id) {
        if(!userRepo.existsById(id)){
            throw new RuntimeException("user not found");
        }
        User user = userRepo.getReferenceById(id);
        UserDto userDto = new UserDto(user.getName(), user.getEmail(),user.getCreatedAt());
        return  ResponseEntity.ok(userDto);
    }

    public void deleteUserByname(String name) {
        userRepo.deleteByName(name);
    }
}
