package com.example.ChasosGuard.Controller;
import com.example.ChasosGuard.DTO.MonitoredApplicationDTO;
import com.example.ChasosGuard.Model.MonitoredApplication;
import com.example.ChasosGuard.Service.MonitoredApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Tag(
        name = "MONITORED APPLICATION CRUD REST API",
        description = "crud rest api for MONITORED APPLICATION"
)
@RestController
@RequestMapping("api")
public class MonitoredApplicationController {
    @Autowired
    private MonitoredApplicationService applicationService;

    @Operation(
            summary = "REGISTER APPLICATION REST API",
            description = "REST API to REGISTER APPLICATION"
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
    @PostMapping("registerapp")
    public ResponseEntity<String> createMonitoredApplications(@Valid @RequestBody MonitoredApplication app, Authentication authentication) {
        MonitoredApplication savedapp = applicationService.registerApplication(app, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body("Application was registered");
    }


    @Operation(
            summary = "Get Monitored Apllication by Name REST API",
            description = "REST API to get Monitored Application"
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
    @GetMapping("getapp/{name}")
    public ResponseEntity<MonitoredApplicationDTO> getMonitoredAppbyName(@Valid @PathVariable String name,Authentication authentication) {
        MonitoredApplication app = applicationService.getAppByName(name, authentication.getName());
        MonitoredApplicationDTO monitoredApplicationDTO = new MonitoredApplicationDTO(
                app.getName(),
                app.getHealthUrl(),
                app.getMonitoringIntervalSeconds(),
                app.getTimeoutMillis(),
                app.getFailureThreshold(),
                app.getCurrentStatus()
        );
        return ResponseEntity.status(HttpStatus.OK).body(monitoredApplicationDTO);
    }

    @Operation(
            summary = "Get All Monitored Apllication of user REST API",
            description = "REST API to get All Monitored Apllication of user"
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
    @GetMapping("getallapp")
    public ResponseEntity<List<MonitoredApplicationDTO>> getAllMonitoredApp(Authentication authentication) {
        String name = authentication.getName();
        return applicationService.getAllApp(name);
    }
    @Operation(
            summary = "UPDATE MONITORS REST API",
            description = "REST API to get UPDATE Monitored Apllication of user"
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
    @PutMapping("updatemonitor/{name}")
    public ResponseEntity<String> updateMonitoredapp(@Valid @PathVariable String name,@RequestBody MonitoredApplication monitoredApplication,Authentication authentication){
        return applicationService.UpdateMonitoredApplication(name,monitoredApplication,authentication.getName());
    }

    @Operation(
            summary = "PAUSE OR RESUME MONITORS API",
            description = "API to PAUSE OR RESUME MONITOR Apllication of user"
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
    @PatchMapping("pausemonitor/{name}")
    public ResponseEntity<String> PauseMonitor(@PathVariable String name, @RequestBody Map<String,Boolean> request,Authentication authentication){
        applicationService.setMonitorEnabled(
                name,
                request.get("enabled"),
                authentication.getName()
        );
        return ResponseEntity.ok(
                request.get("enabled")
                        ? "Monitor resumed successfully"
                        : "Monitor paused successfully"
        );
    }

    @Operation(
            summary = "REST API TO GET ALL ENABLED UP APPLICATION",
            description = "REST API to GET ALL ENABLED UP APPLICATION"
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
    @GetMapping("upapplications")
    public ResponseEntity<List<MonitoredApplicationDTO>> getallUpApplications(Authentication authentication){
        return applicationService.getAllUpApplications(authentication.getName());
    }


    @Operation(
            summary = "REST API TO GET ALL ENABLED DOWN APPLICATION",
            description = "REST API to GET ALL ENABLED DOWN APPLICATION"
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
    @GetMapping("downapplications")
    public ResponseEntity<List<MonitoredApplicationDTO>> getallDownApplications(Authentication authentication){
        return applicationService.getAllDownApplications(authentication.getName());
    }
}