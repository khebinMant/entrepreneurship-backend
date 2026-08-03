package com.project.emprendia.entrepreneurship.service.impl;

import com.project.emprendia.entrepreneurship.client.SharedServiceClient;
import com.project.emprendia.entrepreneurship.client.UserServiceClient;
import com.project.emprendia.entrepreneurship.domain.Category;
import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.domain.EntrepreneurshipLocation;
import com.project.emprendia.entrepreneurship.domain.EntityPortal;
import com.project.emprendia.entrepreneurship.domain.EntitySocialLink;
import com.project.emprendia.entrepreneurship.dto.CatalogueValueResponse;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipLocationResponse;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipResponse;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipStatsResponse;
import com.project.emprendia.entrepreneurship.dto.EntityPortalResponse;
import com.project.emprendia.entrepreneurship.dto.GlobalAnalyticsResponse;
import com.project.emprendia.entrepreneurship.dto.EntitySocialLinkResponse;
import com.project.emprendia.entrepreneurship.dto.ImageGalleryResponse;
import com.project.emprendia.entrepreneurship.dto.UserBasicResponse;
import com.project.emprendia.entrepreneurship.exception.ResourceNotFoundException;
import com.project.emprendia.entrepreneurship.mapping.mapper.EntrepreneurshipMapper;
import com.project.emprendia.entrepreneurship.repository.CategoryRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipLocationRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipQueryRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipRepository;
import com.project.emprendia.entrepreneurship.repository.EntityPortalRepository;
import com.project.emprendia.entrepreneurship.repository.EntitySocialLinkRepository;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntrepreneurshipServiceImpl implements EntrepreneurshipService {

    private final EntrepreneurshipRepository entrepreneurshipRepository;
    private final EntrepreneurshipQueryRepository entrepreneurshipQueryRepository;
    private final CategoryRepository categoryRepository;
    private final EntrepreneurshipMapper entrepreneurshipMapper;
    private final SharedServiceClient sharedServiceClient;
    private final UserServiceClient userServiceClient;
    private final EntitySocialLinkRepository entitySocialLinkRepository;
    private final EntityPortalRepository entityPortalRepository;
    private final EntrepreneurshipLocationRepository entrepreneurshipLocationRepository;

    @Override
    public List<EntrepreneurshipResponse> findAll() {
        List<EntrepreneurshipResponse> entrepreneurships = entrepreneurshipRepository.findAll().stream()
                .map(entrepreneurshipMapper::toResponse)
                .toList();

        entrepreneurships.forEach(this::enrichWithLogo);
        entrepreneurships.forEach(this::enrichWithRelatedData);
        entrepreneurships.forEach(this::enrichWithCreator);
        return entrepreneurships;
    }

    @Override
    public EntrepreneurshipResponse findById(Long id) {
        EntrepreneurshipResponse entrepreneurshipResponse =entrepreneurshipMapper.toResponse(
                entrepreneurshipRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Entrepreneurship", id)));

        enrichWithLogo(entrepreneurshipResponse);
        enrichWithRelatedData(entrepreneurshipResponse);
        enrichWithCreator(entrepreneurshipResponse);
        return entrepreneurshipResponse;
    }

    @Override
    public List<EntrepreneurshipResponse> findByUserId(Long userId) {
        List<EntrepreneurshipResponse> entrepreneurships = entrepreneurshipRepository.findByUserId(userId).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();

        entrepreneurships.forEach(this::enrichWithLogo);
        entrepreneurships.forEach(this::enrichWithRelatedData);
        entrepreneurships.forEach(this::enrichWithCreator);
        return entrepreneurships;
    }

    @Override
    public List<EntrepreneurshipResponse> findByUserId(Long userId, String name, Long categoryId, Boolean isPhysical, Boolean isDigital) {
        List<EntrepreneurshipResponse> entrepreneurships = entrepreneurshipQueryRepository
            .findByUserId(userId, name, categoryId, isPhysical, isDigital).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();

        entrepreneurships.forEach(this::enrichWithLogo);
        entrepreneurships.forEach(this::enrichWithRelatedData);
        entrepreneurships.forEach(this::enrichWithCreator);
        return entrepreneurships;
    }

    @Override
    public Page<EntrepreneurshipResponse> findByUserIdPaginated(Long userId, String name, Long categoryId, Boolean isPhysical, Boolean isDigital, Pageable pageable) {
        Page<Entrepreneurship> page = entrepreneurshipQueryRepository
            .findByUserIdPaginated(userId, name, categoryId, isPhysical, isDigital, pageable);

        return page.map(entity -> {
            EntrepreneurshipResponse response = entrepreneurshipMapper.toResponse(entity);
            enrichWithLogo(response);
            enrichWithRelatedData(response);
            enrichWithCreator(response);
            return response;
        });
    }

    @Override
    public List<EntrepreneurshipResponse> search(String name, Long categoryId, Boolean isPhysical, Boolean isDigital) {
        List<EntrepreneurshipResponse> entrepreneurships = entrepreneurshipQueryRepository
            .search(name, categoryId, isPhysical, isDigital).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();

        entrepreneurships.forEach(this::enrichWithLogo);
        entrepreneurships.forEach(this::enrichWithRelatedData);
        entrepreneurships.forEach(this::enrichWithCreator);
        return entrepreneurships;
    }

    @Override
    public Page<EntrepreneurshipResponse> searchPaginated(String name, Long categoryId, Boolean isPhysical, Boolean isDigital, Pageable pageable) {
        Page<Entrepreneurship> page = entrepreneurshipQueryRepository
            .searchPaginated(name, categoryId, isPhysical, isDigital, pageable);

        return page.map(entity -> {
            EntrepreneurshipResponse response = entrepreneurshipMapper.toResponse(entity);
            enrichWithLogo(response);
            enrichWithRelatedData(response);
            enrichWithCreator(response);
            return response;
        });
    }

    @Override
    @Transactional
    public EntrepreneurshipResponse create(EntrepreneurshipRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        Entrepreneurship entity = entrepreneurshipMapper.toEntity(request);
        entity.setCategory(category);
        Entrepreneurship saved = entrepreneurshipRepository.save(entity);
        Long entityId = saved.getEntrepreneurshipId();

        if (!CollectionUtils.isEmpty(request.getSocialLinks())) {
            List<EntitySocialLink> links = request.getSocialLinks().stream()
                .map(sl -> EntitySocialLink.builder()
                    .entityId(entityId)
                    .socialPlatformId(sl.getSocialPlatformId())
                    .url(sl.getUrl())
                    .build())
                .toList();
            entitySocialLinkRepository.saveAll(links);
        }

        if (!CollectionUtils.isEmpty(request.getLocations())) {
            List<EntrepreneurshipLocation> locs = request.getLocations().stream()
                .map(l -> EntrepreneurshipLocation.builder()
                    .entrepreneurship(saved)
                    .countryId(l.getCountryId())
                    .provinceId(l.getProvinceId())
                    .cityId(l.getCityId())
                    .parishId(l.getParishId())
                    .addressLine(l.getAddressLine())
                    .latitude(l.getLatitude())
                    .longitude(l.getLongitude())
                    .mapsUrl(l.getMapsUrl())
                    .build())
                .toList();
            entrepreneurshipLocationRepository.saveAll(locs);
        }

        String defaultHtml = String.format(
            "<h1>Bienvenido a %s</h1><p>Portal de presentaci\u00f3n del emprendimiento.</p>",
            saved.getName()
        );
        String portalSubdomain = request.getPortal() != null && request.getPortal().getSubdomain() != null
            ? request.getPortal().getSubdomain() : "emp-" + entityId;
        Long portalThemeId = request.getPortal() != null ? request.getPortal().getThemeId() : null;
        Boolean portalIsActive = request.getPortal() != null && request.getPortal().getIsActive() != null
            ? request.getPortal().getIsActive() : true;
        String portalHtml = request.getPortal() != null && request.getPortal().getHtmlContent() != null
            ? request.getPortal().getHtmlContent() : defaultHtml;

        EntityPortal portal = EntityPortal.builder()
            .entityId(entityId)
            .subdomain(portalSubdomain)
            .themeId(portalThemeId)
            .isActive(portalIsActive)
            .htmlContent(portalHtml)
            .build();
        entityPortalRepository.save(portal);

        return findById(entityId);
    }

    @Override
    @Transactional
    public EntrepreneurshipResponse update(Long id, EntrepreneurshipRequest request) {
        Entrepreneurship entity = entrepreneurshipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrepreneurship", id));
        entrepreneurshipMapper.updateEntityFromRequest(request, entity);
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            entity.setCategory(category);
        }
        entrepreneurshipRepository.save(entity);

        if (request.getSocialLinks() != null) {
            entitySocialLinkRepository.deleteByEntityId(id);
            if (!request.getSocialLinks().isEmpty()) {
                List<EntitySocialLink> links = request.getSocialLinks().stream()
                    .map(sl -> EntitySocialLink.builder()
                        .entityId(id)
                        .socialPlatformId(sl.getSocialPlatformId())
                        .url(sl.getUrl())
                        .build())
                    .toList();
                entitySocialLinkRepository.saveAll(links);
            }
        }

        if (request.getLocations() != null) {
            entrepreneurshipLocationRepository.deleteByEntrepreneurshipEntrepreneurshipId(id);
            if (!request.getLocations().isEmpty()) {
                List<EntrepreneurshipLocation> locs = request.getLocations().stream()
                .map(l -> EntrepreneurshipLocation.builder()
                    .entrepreneurship(entity)
                    .countryId(l.getCountryId())
                    .provinceId(l.getProvinceId())
                    .cityId(l.getCityId())
                    .parishId(l.getParishId())
                    .addressLine(l.getAddressLine())
                    .latitude(l.getLatitude())
                    .longitude(l.getLongitude())
                    .mapsUrl(l.getMapsUrl())
                    .build())
                .toList();
            entrepreneurshipLocationRepository.saveAll(locs);
            }
        }

        if (request.getPortal() != null) {
            EntityPortal portal = entityPortalRepository.findByEntityId(id)
                .orElse(EntityPortal.builder().entityId(id).build());
            if (request.getPortal().getSubdomain() != null) {
                portal.setSubdomain(request.getPortal().getSubdomain());
            }
            if (request.getPortal().getThemeId() != null) {
                portal.setThemeId(request.getPortal().getThemeId());
            }
            portal.setIsActive(request.getPortal().getIsActive() != null ? request.getPortal().getIsActive() : true);
            if (request.getPortal().getHtmlContent() != null) {
                portal.setHtmlContent(request.getPortal().getHtmlContent());
            }
            entityPortalRepository.save(portal);
        }

        return findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!entrepreneurshipRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entrepreneurship", id);
        }
        entitySocialLinkRepository.deleteByEntityId(id);
        entityPortalRepository.deleteByEntityId(id);
        entrepreneurshipLocationRepository.deleteByEntrepreneurshipEntrepreneurshipId(id);
        entrepreneurshipRepository.deleteById(id);
    }

    /**
     * Enriquecer el response con la imagen logo del emprendimiento (displayOrder = 0)
     */
    @Override
    public EntrepreneurshipStatsResponse getStatsByUserId(Long userId) {
        long total = entrepreneurshipRepository.countByUserId(userId);

        List<EntrepreneurshipStatsResponse.CategoryCount> byCategory =
            entrepreneurshipRepository.countByCategoryGroupedByUserId(userId).stream()
                .map(row -> EntrepreneurshipStatsResponse.CategoryCount.builder()
                    .categoryId((Long) row[0])
                    .categoryName((String) row[1])
                    .count((Long) row[2])
                    .build())
                .toList();

        EntrepreneurshipStatsResponse.TypeDistribution byType =
            EntrepreneurshipStatsResponse.TypeDistribution.builder()
                .physical(entrepreneurshipRepository.countPhysicalOnlyByUserId(userId))
                .digital(entrepreneurshipRepository.countDigitalOnlyByUserId(userId))
                .both(entrepreneurshipRepository.countBothByUserId(userId))
                .build();

        List<EntrepreneurshipResponse> recent = entrepreneurshipRepository
            .findTop5ByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();
        recent.forEach(this::enrichWithLogo);
        recent.forEach(this::enrichWithRelatedData);
        recent.forEach(this::enrichWithCreator);

        return EntrepreneurshipStatsResponse.builder()
            .totalEntrepreneurships(total)
            .byCategory(byCategory)
            .byType(byType)
            .recentEntrepreneurships(recent)
            .build();
    }

    @Override
    public GlobalAnalyticsResponse getGlobalAnalytics() {
        long total = entrepreneurshipRepository.count();

        List<GlobalAnalyticsResponse.CategoryCount> byCategory =
            entrepreneurshipRepository.countByCategoryGrouped().stream()
                .map(row -> GlobalAnalyticsResponse.CategoryCount.builder()
                    .categoryId((Long) row[0])
                    .categoryName((String) row[1])
                    .count((Long) row[2])
                    .build())
                .toList();

        GlobalAnalyticsResponse.TypeDistribution byType =
            GlobalAnalyticsResponse.TypeDistribution.builder()
                .physical(entrepreneurshipRepository.countPhysicalOnly())
                .digital(entrepreneurshipRepository.countDigitalOnly())
                .both(entrepreneurshipRepository.countBoth())
                .build();

        List<GlobalAnalyticsResponse.MonthlyCount> monthly =
            entrepreneurshipRepository.countByMonth().stream()
                .map(row -> GlobalAnalyticsResponse.MonthlyCount.builder()
                    .year((Integer) row[0])
                    .month((Integer) row[1])
                    .count((Long) row[2])
                    .build())
                .toList();

        List<EntrepreneurshipResponse> recent = entrepreneurshipRepository
            .findTop5ByOrderByCreatedAtDesc().stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();
        recent.forEach(this::enrichWithLogo);
        recent.forEach(this::enrichWithRelatedData);
        recent.forEach(this::enrichWithCreator);

        return GlobalAnalyticsResponse.builder()
            .totalEntrepreneurships(total)
            .totalCategories(categoryRepository.count())
            .byCategory(byCategory)
            .byType(byType)
            .monthlyActivity(monthly)
            .recentEntrepreneurships(recent)
            .build();
    }

    private void enrichWithLogo(EntrepreneurshipResponse response) {
        try {
            List<ImageGalleryResponse> images = sharedServiceClient.getImagesForEntity(
                "ENTREPRENEURSHIP",
                response.getEntrepreneurshipId()
            );

            // Buscar la imagen con displayOrder = 0 (logo principal)
            images.stream()
                .filter(img -> img.getDisplayOrder() != null && img.getDisplayOrder() == 0)
                .findFirst()
                .ifPresent(logo -> {
                    response.setImageUrl(logo.getImageUrl());
                    response.setImageId(logo.getImageId());
                });

        } catch (Exception e) {
            log.warn("Error al obtener imagen para emprendimiento {}: {}",
                response.getEntrepreneurshipId(), e.getMessage());
        }
    }

    private void enrichWithRelatedData(EntrepreneurshipResponse response) {
        try {
            Long entityId = response.getEntrepreneurshipId();
            response.setLocations(
                entrepreneurshipLocationRepository.findByEntrepreneurshipEntrepreneurshipId(entityId).stream()
                    .map(loc -> EntrepreneurshipLocationResponse.builder()
                        .locationId(loc.getLocationId())
                        .entrepreneurshipId(entityId)
                        .entrepreneurshipName(response.getName())
                        .countryId(loc.getCountryId())
                        .provinceId(loc.getProvinceId())
                        .cityId(loc.getCityId())
                        .parishId(loc.getParishId())
                        .addressLine(loc.getAddressLine())
                        .latitude(loc.getLatitude())
                        .longitude(loc.getLongitude())
                        .mapsUrl(loc.getMapsUrl())
                        .createdAt(loc.getCreatedAt())
                        .build())
                    .toList()
            );
            response.setSocialLinks(
                entitySocialLinkRepository.findByEntityId(entityId).stream()
                    .map(sl -> {
                        String platformName = resolveSocialPlatformName(sl.getSocialPlatformId());
                        return EntitySocialLinkResponse.builder()
                            .socialLinkId(sl.getSocialLinkId())
                            .entityId(entityId)
                            .socialPlatformId(sl.getSocialPlatformId())
                            .socialPlatformName(platformName)
                            .url(sl.getUrl())
                            .createdAt(sl.getCreatedAt())
                            .build();
                    })
                    .toList()
            );
            response.setPortal(
                entityPortalRepository.findByEntityId(entityId)
                    .map(p -> EntityPortalResponse.builder()
                        .portalId(p.getPortalId())
                        .entityId(entityId)
                        .subdomain(p.getSubdomain())
                        .themeId(p.getThemeId())
                        .isActive(p.getIsActive())
                        .htmlContent(p.getHtmlContent())
                        .createdAt(p.getCreatedAt())
                        .build())
                    .orElse(null)
            );
        } catch (Exception e) {
            log.warn("Error al enriquecer datos relacionados para emprendimiento {}: {}",
                response.getEntrepreneurshipId(), e.getMessage());
        }
    }

    private void enrichWithCreator(EntrepreneurshipResponse response) {
        if (response.getUserId() == null) return;
        try {
            UserBasicResponse user = userServiceClient.getUserById(response.getUserId());
            user.setContacts(userServiceClient.getUserContacts(response.getUserId()));
            response.setCreatedByUser(user);
        } catch (Exception e) {
            log.warn("Error al enriquecer creador para emprendimiento {}: {}",
                response.getEntrepreneurshipId(), e.getMessage());
        }
    }

    private String resolveSocialPlatformName(Long socialPlatformId) {
        if (socialPlatformId == null) return null;
        try {
            List<CatalogueValueResponse> values = sharedServiceClient.getValuesByType("SOCIAL_PLATFORM");
            return values.stream()
                .filter(v -> v.getCatalogueValueId().equals(socialPlatformId))
                .map(CatalogueValueResponse::getName)
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            log.warn("Error al resolver nombre de plataforma social {}: {}", socialPlatformId, e.getMessage());
            return null;
        }
    }
}
