package com.example.ChasosGuard.Controller;
import com.example.ChasosGuard.DTO.HealthcheckDTO;
import com.example.ChasosGuard.Service.HealthCheckResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@Tag(
        name = "HEALTH CHECK CRUD REST API",
        description = "crud rest api for HEALTH check for monitored application"
)
@RestController
@RequestMapping("api/health-checks")
public class HealthCheckController {
    @Autowired
    private HealthCheckResultService healthCheckService;

    @Operation(
            summary = "MANUAL HEALTH CHECK REST API",
            description = "REST API to GET MANUAL HEALTH CHECK"
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
    @PostMapping("manual/{name}/check")
    public ResponseEntity<HealthcheckDTO> manualHealthCheck(@PathVariable String name, Authentication authentication){
        return healthCheckService.manualCheckApplication(name,authentication.getName());
    }

 //   http://localhost:8080/api/health-checks/applications/1/status
    @Operation(
            summary = "REST API FOR STAUS OF APPLICATION",
            description = "REST API to GET STATUS OF APPLICATION"
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
    @GetMapping("/applications/{name}/status")
    public ResponseEntity<String> getStatusofApplication(@PathVariable String name,Authentication authentication){
        return healthCheckService.getStatusofApplication(name,authentication.getName());
    }


    @Operation(
            summary = "REST API FOR GET HEALTH CHECK OF ALL APPLICATION",
            description = "REST API to GET HEALTH CHECK OF ALL ENABLED APPLICATION"
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
    @PostMapping("check-all")
    public ResponseEntity<List<HealthcheckDTO>> checkAllApplication(Authentication authentication){
        return healthCheckService.CheckAllEnabledApplication(authentication.getName());
    }

    @Operation(
            summary = "REST API TO GET ALL ENABLED APPLICATION",
            description = "REST API to GET ALL ENABLED APPLICATION"
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
    @GetMapping("getallenableapplications")
    public ResponseEntity<List<HealthcheckDTO>> getallenabledApplications(Authentication authentication){
        return healthCheckService.getenabledAllApplications(authentication.getName());
    }


    @Operation(
            summary = "REST API TO GET APPLICATION HISTORY",
            description = "REST API to GET APLICATION HISTORY"
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
    @GetMapping("{name}/history")
    public ResponseEntity<List<HealthcheckDTO>> getHealthCheckHistory(
            @PathVariable String name,Authentication authentication
    ){
        return healthCheckService.getHistoryofHealthOfapplication(name,authentication.getName());
    }


    @Operation(
            summary = "REST API TO GET UPTIME PERCENTAGE OF APPLICATION ",
            description = "REST API to GET UPTIME OF APLICATION "
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
    @GetMapping("{name}/uptime")
    public ResponseEntity<Map<String,Object>> getuptime(@PathVariable String name,Authentication authentication){
        return healthCheckService.getUptime(name,authentication.getName());
    }

    @Operation(
            summary = "REST API TO GET AVAILABILITY OF APPLICATION ",
            description = "REST API to GET AVAILABILITY OF APLICATION "
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
    @GetMapping("{name}/availability")
    public ResponseEntity<Map<String,Object>> getAvailability(
            @PathVariable String name,
            @RequestParam(defaultValue = "24h") String period,
            Authentication authentication
    ){
        return healthCheckService.getAvailability(name,period,authentication.getName());
    }
}