package com.project.emprendia.event.mapping.mapper;

import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventResponse toResponse(Event entity);

    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    Event toEntity(EventRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    void updateEntityFromRequest(EventRequest request, @MappingTarget Event entity);
}
