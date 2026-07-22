package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.client.SharedServiceClient;
import com.project.emprendia.event.client.UserServiceClient;
import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.domain.EntityPortal;
import com.project.emprendia.event.domain.EntitySocialLink;
import com.project.emprendia.event.dto.CatalogueValueResponse;
import com.project.emprendia.event.dto.EntityPortalResponse;
import com.project.emprendia.event.dto.EntitySocialLinkResponse;
import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import com.project.emprendia.event.dto.UserBasicResponse;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.mapping.mapper.EventMapper;
import com.project.emprendia.event.repository.EntityPortalRepository;
import com.project.emprendia.event.repository.EntitySocialLinkRepository;
import com.project.emprendia.event.repository.EventQueryRepository;
import com.project.emprendia.event.repository.EventRepository;
import com.project.emprendia.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventQueryRepository eventQueryRepository;
    private final EventMapper eventMapper;
    private final SharedServiceClient sharedServiceClient;
    private final UserServiceClient userServiceClient;
    private final EntitySocialLinkRepository entitySocialLinkRepository;
    private final EntityPortalRepository entityPortalRepository;

    @Override
    public List<EventResponse> findAll() {
        List<EventResponse> events = eventRepository.findAll().stream()
            .map(eventMapper::toResponse)
            .toList();

        events.forEach(this::enrichEvent);
        return events;
    }

    @Override
    public EventResponse findById(Long id) {
        EventResponse response = eventMapper.toResponse(
            eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id)));
        enrichEvent(response);
        return response;
    }

    @Override
    public List<EventResponse> findByCreator(Long userId) {
        List<EventResponse> events = eventRepository.findByCreatedByUserId(userId).stream()
            .map(eventMapper::toResponse)
            .toList();

        events.forEach(this::enrichEvent);
        return events;
    }

    @Override
    public List<EventResponse> findByCreator(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                              LocalDateTime fromDate, LocalDateTime toDate) {
        List<EventResponse> events = eventQueryRepository
            .findByCreator(userId, name, eventTypeId, eventVisibilityId, fromDate, toDate).stream()
            .map(eventMapper::toResponse)
            .toList();

        events.forEach(this::enrichEvent);
        return events;
    }

    @Override
    public Page<EventResponse> findByCreatorPaginated(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                                       LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Page<Event> page = eventQueryRepository.findByCreatorPaginated(
            userId, name, eventTypeId, eventVisibilityId, fromDate, toDate, pageable);

        return page.map(entity -> {
            EventResponse response = eventMapper.toResponse(entity);
            enrichEvent(response);
            return response;
        });
    }

    @Override
    public List<EventResponse> search(String name, Long eventTypeId, Long eventVisibilityId,
                                       LocalDateTime fromDate, LocalDateTime toDate) {
        List<EventResponse> events = eventQueryRepository
            .search(name, eventTypeId, eventVisibilityId, fromDate, toDate).stream()
            .map(eventMapper::toResponse)
            .toList();

        events.forEach(this::enrichEvent);
        return events;
    }

    @Override
    public Page<EventResponse> searchPaginated(String name, Long eventTypeId, Long eventVisibilityId,
                                                LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Page<Event> page = eventQueryRepository.searchPaginated(
            name, eventTypeId, eventVisibilityId, fromDate, toDate, pageable);

        return page.map(entity -> {
            EventResponse response = eventMapper.toResponse(entity);
            enrichEvent(response);
            return response;
        });
    }

    @Override
    @Transactional
    public EventResponse create(EventRequest request) {
        Event entity = eventMapper.toEntity(request);
        Event saved = eventRepository.save(entity);
        Long entityId = saved.getEventId();

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

        String defaultHtml = String.format(
            "<h1>Bienvenido a %s</h1><p>Portal de presentaci\u00f3n del evento.</p>",
            saved.getName()
        );
        String portalSubdomain = request.getPortal() != null && request.getPortal().getSubdomain() != null
            ? request.getPortal().getSubdomain() : "evt-" + entityId;
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
    public EventResponse update(Long id, EventRequest request) {
        Event entity = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event", id));
        eventMapper.updateEntityFromRequest(request, entity);
        eventRepository.save(entity);

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
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", id);
        }
        entitySocialLinkRepository.deleteByEntityId(id);
        entityPortalRepository.deleteByEntityId(id);
        eventRepository.deleteById(id);
    }

    /**
     * Enriquecer el response con datos de usuario, catalogos, ubicacion e imagen
     */
    private void enrichEvent(EventResponse response) {
        enrichWithCreator(response);
        enrichWithCatalogueValues(response);
        enrichWithLocations(response);
        enrichWithCoverImage(response);
        enrichWithRelatedData(response);
    }

    private void enrichWithCreator(EventResponse response) {
        try {
            if (response.getCreatedByUserId() != null) {
                UserBasicResponse user = userServiceClient.getUserById(response.getCreatedByUserId());
                user.setContacts(userServiceClient.getUserContacts(response.getCreatedByUserId()));
                response.setCreatedByUser(user);
            }
        } catch (Exception e) {
            log.warn("Error al obtener creador para evento {}: {}",
                response.getEventId(), e.getMessage());
        }
    }

    private void enrichWithCatalogueValues(EventResponse response) {
        try {
            if (response.getEventTypeId() != null) {
                response.setEventType(sharedServiceClient.getValueById(response.getEventTypeId()));
            }
        } catch (Exception e) {
            log.warn("Error al obtener tipo de evento {}: {}", response.getEventId(), e.getMessage());
        }

        try {
            if (response.getEventVisibilityId() != null) {
                response.setEventVisibility(sharedServiceClient.getValueById(response.getEventVisibilityId()));
            }
        } catch (Exception e) {
            log.warn("Error al obtener visibilidad de evento {}: {}", response.getEventId(), e.getMessage());
        }
    }

    private void enrichWithLocations(EventResponse response) {
        try {
            if (response.getCountryId() != null) {
                response.setCountry(sharedServiceClient.getValueById(response.getCountryId()));
            }
        } catch (Exception e) {
            log.warn("Error al obtener pais para evento {}: {}", response.getEventId(), e.getMessage());
        }

        try {
            if (response.getProvinceId() != null) {
                response.setProvince(sharedServiceClient.getValueById(response.getProvinceId()));
            }
        } catch (Exception e) {
            log.warn("Error al obtener provincia para evento {}: {}", response.getEventId(), e.getMessage());
        }

        try {
            if (response.getCityId() != null) {
                response.setCity(sharedServiceClient.getValueById(response.getCityId()));
            }
        } catch (Exception e) {
            log.warn("Error al obtener ciudad para evento {}: {}", response.getEventId(), e.getMessage());
        }
    }

    /**
     * Enriquecer el response con la imagen de portada del evento (displayOrder = 0)
     */
    private void enrichWithCoverImage(EventResponse response) {
        try {
            List<Map<String, Object>> images = sharedServiceClient.getImagesForEntity(
                "EVENT",
                response.getEventId()
            );

            // Buscar la imagen con displayOrder = 0 (portada principal)
            images.stream()
                .filter(img -> {
                    Object displayOrder = img.get("displayOrder");
                    return displayOrder != null &&
                           (displayOrder instanceof Integer && (Integer) displayOrder == 0);
                })
                .findFirst()
                .ifPresent(coverImage -> {
                    response.setImageUrl((String) coverImage.get("imageUrl"));
                    Object imageId = coverImage.get("imageId");
                    if (imageId instanceof Number) {
                        response.setImageId(((Number) imageId).longValue());
                    }
                });

        } catch (Exception e) {
            log.warn("Error al obtener imagen para evento {}: {}",
                response.getEventId(), e.getMessage());
        }
    }

    private void enrichWithRelatedData(EventResponse response) {
        try {
            Long entityId = response.getEventId();
            List<CatalogueValueResponse> socialPlatforms = sharedServiceClient.getValuesByType("SOCIAL_PLATFORM");
            response.setSocialLinks(
                entitySocialLinkRepository.findByEntityId(entityId).stream()
                    .map(sl -> {
                        String platformName = socialPlatforms.stream()
                            .filter(v -> v.getCatalogueValueId().equals(sl.getSocialPlatformId()))
                            .map(CatalogueValueResponse::getName)
                            .findFirst()
                            .orElse(null);
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
            log.warn("Error al enriquecer datos relacionados para evento {}: {}",
                response.getEventId(), e.getMessage());
        }
    }
}
