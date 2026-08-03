package com.project.emprendia.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationStatsResponse {
    private long totalInvitations;
    private List<StatusCount> byStatus;
    private long totalParticipations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusCount {
        private Long statusId;
        private String statusName;
        private long count;
    }
}
