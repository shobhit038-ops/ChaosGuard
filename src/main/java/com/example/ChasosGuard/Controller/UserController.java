package com.example.ChasosGuard.Controller;

import com.example.ChasosGuard.DTO.UserDto;
import com.example.ChasosGuard.Model.User;
import com.example.ChasosGuard.Service.JWTservice;
import com.example.ChasosGuard.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "USER CRUD REST API",
        description = "crud rest api for USER delete,fetch,update,read"
)
@RestController
@RequestMapping("user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JWTservice jwTservice;


    @Operation(
            summary = " REST API to register new user",
            description = "REST API to register new user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error"
            )
    }
    )
    @PostMapping("register")
    public ResponseEntity<UserDto> registerUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    @Operation(
            summary = "LOGIN USER REST API",
            description = "REST API to LOGIN USER"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error"
            )
    }
    )
    @PostMapping("login")
    public String lgoinUser(@RequestBody User user){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getName(),user.getPassword()));
        if(authentication.isAuthenticated()){
            return jwTservice.generateToken(user.getName());
        }
        return "fail";
    }


    @Operation(
            summary = "READ USER REST API",
            description = "REST API to find user by username"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error"
            )
    }
    )
    @GetMapping("findbyname/{name}")
    public ResponseEntity<UserDto> findbyname(@PathVariable String name){
       return userService.getUserByname(name);
    }




    @Operation(
            summary = "FIND USER BY ID",
            description = "REST API TO FIND USER BY ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error"
            )
    }
    )
    @GetMapping("find/{id}")
    public ResponseEntity<UserDto> findbyId(@PathVariable Long id){
        return userService.findbyId(id);
    }
}
