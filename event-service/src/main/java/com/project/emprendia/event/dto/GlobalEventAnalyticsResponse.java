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
public class GlobalEventAnalyticsResponse {
    private long totalEvents;
    private List<TypeCount> byType;
    private List<TypeCount> byVisibility;
    private long upcomingEvents;
    private long pastEvents;
    private long totalInvitationsSent;
    private long totalParticipants;
    private List<MonthlyCount> monthlyActivity;
    private List<EventResponse> recentEvents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeCount {
        private Long id;
        private String name;
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyCount {
        private int year;
        private int month;
        private long count;
    }
}
