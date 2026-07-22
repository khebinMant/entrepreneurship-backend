package com.project.emprendia.event.mapping.mapper;

import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface EventMapper {

    @Mapping(target = "socialLinks", ignore = true)
    @Mapping(target = "portal", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "imageId", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "eventVisibility", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "province", ignore = true)
    @Mapping(target = "city", ignore = true)
    EventResponse toResponse(Event entity);

    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Event toEntity(EventRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "participants", ignore = true)
    void updateEntityFromRequest(EventRequest request, @MappingTarget Event entity);
}
