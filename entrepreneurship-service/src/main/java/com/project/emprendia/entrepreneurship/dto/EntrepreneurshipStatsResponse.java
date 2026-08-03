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
public class EntrepreneurshipStatsResponse {
    private long totalEntrepreneurships;
    private List<CategoryCount> byCategory;
    private TypeDistribution byType;
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
}
