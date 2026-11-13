package com.example.ChasosGuard.Controller;

import com.example.ChasosGuard.DTO.ActivityFeedDTO;
import com.example.ChasosGuard.DTO.ApplicationGridDTO;
import com.example.ChasosGuard.DTO.IncidentDTO;
import com.example.ChasosGuard.DTO.RecentFailureDTO;
import com.example.ChasosGuard.Repositry.UserRepo;
import com.example.ChasosGuard.Service.DashboardService;
import com.example.ChasosGuard.Service.HealthCheckResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/dashboard")
@Tag(
        name = "DASHBOARD REST API",
        description = "rest api for DASHBOARD for ALL application"
)
public class DashboardController {
    @Autowired
    private HealthCheckResultService healthCheckService;

    @Autowired
    private DashboardService dashboardService;


    @Operation(
            summary = "REST API TO GET  OVERALL SUMMARY OF ALL APPLICATION ",
            description = "REST API to GET SUMMARY OF ALL APLICATION "
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
    @GetMapping("overview")
    public ResponseEntity<Map<String,Long>> getOverViewOfAllApplication(Authentication authentication){
        return dashboardService.getsummaryofapplication(authentication.getName());
    }

    @Operation(
            summary = "REST API TO GET RECENT FAILURES OF ALL APPLICATION ",
            description = "REST API to GET RECENT FAILURES OF ALL APLICATION "
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
    @GetMapping("recent-failures")
    public ResponseEntity<List<RecentFailureDTO>> getrecentFailures(Authentication authentication){
        return dashboardService.getallRecentFailures(authentication.getName());
    }

    @Operation(
            summary = "REST API TO GET STATUS GRID OF ALL APPLICATION ",
            description = "REST API to GET SATUS GRID OF ALL APLICATION "
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
    @GetMapping("status-grid")
    public ResponseEntity<List<ApplicationGridDTO>> getstatusgrid(Authentication authentication){
        return dashboardService.getApplicationGrid(authentication.getName());
    }

    @Operation(
            summary = "REST API TO GET ACTIVE INCIDENT OF APPLICATION ",
            description = "REST API to GET ACTIVE INCIDENT OF APLICATION "
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
    @GetMapping("active-incidents")
    public ResponseEntity<List<IncidentDTO>> getActiveIncidents(Authentication authentication){
        return dashboardService.getActiveIncidents(authentication.getName());
    }

    @Operation(
            summary = "REST API TO GET STATUS TRANSITION OF ALL APPLICATION ",
            description = "REST API to  GET STATUS TRANSITION OF ALL APLICATION "
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
    @GetMapping("activity-feed")
    public ResponseEntity<List<ActivityFeedDTO>> getActivityFeed(Authentication authentication){
        return dashboardService.getActivityFeed(authentication.getName());
    }
}
