package com.project.emprendia.entrepreneurship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalAnalyticsResponse {
    private long totalEntrepreneurships;
    private long totalCategories;
    private List<CategoryCount> byCategory;
    private TypeDistribution byType;
    private List<MonthlyCount> monthlyActivity;
    private List<EntrepreneurshipResponse> recentEntrepreneurships;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryCount {
        private Long categoryId;
        private String categoryName;
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeDistribution {
        private long physical;
        private long digital;
        private long both;
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
