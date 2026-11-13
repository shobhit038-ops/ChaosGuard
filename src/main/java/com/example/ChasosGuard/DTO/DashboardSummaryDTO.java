package com.example.ChasosGuard.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Data
public class DashboardSummaryDTO {
    long totalMonitors;
    long upMonitors;
    long downMonitors;
    long pausedMonitors;
    long openIncidents;
    double averageResponseTimeMs;

    public DashboardSummaryDTO(long totalMonitors, long upMonitors, long downMonitors, long pausedMonitors, long openIncidents, double averageResponseTimeMs) {
        this.totalMonitors = totalMonitors;
        this.upMonitors = upMonitors;
        this.downMonitors = downMonitors;
        this.pausedMonitors = pausedMonitors;
        this.openIncidents = openIncidents;
        this.averageResponseTimeMs = averageResponseTimeMs;
    }
}
