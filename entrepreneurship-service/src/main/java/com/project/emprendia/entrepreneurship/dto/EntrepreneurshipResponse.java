package com.project.emprendia.entrepreneurship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepreneurshipResponse {
    private Long entrepreneurshipId;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String logoUrl;
    private Boolean isPhysical;
    private Boolean isDigital;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String imageUrl;
    private Long imageId;

    private List<EntitySocialLinkResponse> socialLinks;
    private List<EntrepreneurshipLocationResponse> locations;
    private EntityPortalResponse portal;
    private UserBasicResponse createdByUser;
}
