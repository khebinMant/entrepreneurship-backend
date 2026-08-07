package com.project.emprendia.event.mapping.mapper;

import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-06T20:58:45-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.4.1.jar, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public EventResponse toResponse(Event entity) {
        if ( entity == null ) {
            return null;
        }

        EventResponse.EventResponseBuilder eventResponse = EventResponse.builder();

        eventResponse.eventId( entity.getEventId() );
        eventResponse.createdByUserId( entity.getCreatedByUserId() );
        eventResponse.name( entity.getName() );
        eventResponse.description( entity.getDescription() );
        eventResponse.eventTypeId( entity.getEventTypeId() );
        eventResponse.eventVisibilityId( entity.getEventVisibilityId() );
        eventResponse.isPaid( entity.getIsPaid() );
        eventResponse.price( entity.getPrice() );
        eventResponse.maxAttendees( entity.getMaxAttendees() );
        eventResponse.maxEntrepreneurships( entity.getMaxEntrepreneurships() );
        eventResponse.virtualLink( entity.getVirtualLink() );
        eventResponse.startDatetime( entity.getStartDatetime() );
        eventResponse.endDatetime( entity.getEndDatetime() );
        eventResponse.countryId( entity.getCountryId() );
        eventResponse.provinceId( entity.getProvinceId() );
        eventResponse.cityId( entity.getCityId() );
        eventResponse.addressLine( entity.getAddressLine() );
        eventResponse.mapsUrl( entity.getMapsUrl() );
        eventResponse.createdAt( entity.getCreatedAt() );

        return eventResponse.build();
    }

    @Override
    public Event toEntity(EventRequest request) {
        if ( request == null ) {
            return null;
        }

        Event.EventBuilder event = Event.builder();

        event.createdByUserId( request.getCreatedByUserId() );
        event.name( request.getName() );
        event.description( request.getDescription() );
        event.eventTypeId( request.getEventTypeId() );
        event.eventVisibilityId( request.getEventVisibilityId() );
        event.isPaid( request.getIsPaid() );
        event.price( request.getPrice() );
        event.maxAttendees( request.getMaxAttendees() );
        event.maxEntrepreneurships( request.getMaxEntrepreneurships() );
        event.virtualLink( request.getVirtualLink() );
        event.startDatetime( request.getStartDatetime() );
        event.endDatetime( request.getEndDatetime() );
        event.countryId( request.getCountryId() );
        event.provinceId( request.getProvinceId() );
        event.cityId( request.getCityId() );
        event.addressLine( request.getAddressLine() );
        event.mapsUrl( request.getMapsUrl() );

        return event.build();
    }

    @Override
    public void updateEntityFromRequest(EventRequest request, Event entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getName() != null ) {
            entity.setName( request.getName() );
        }
        if ( request.getDescription() != null ) {
            entity.setDescription( request.getDescription() );
        }
        if ( request.getEventTypeId() != null ) {
            entity.setEventTypeId( request.getEventTypeId() );
        }
        if ( request.getEventVisibilityId() != null ) {
            entity.setEventVisibilityId( request.getEventVisibilityId() );
        }
        if ( request.getIsPaid() != null ) {
            entity.setIsPaid( request.getIsPaid() );
        }
        if ( request.getPrice() != null ) {
            entity.setPrice( request.getPrice() );
        }
        if ( request.getMaxAttendees() != null ) {
            entity.setMaxAttendees( request.getMaxAttendees() );
        }
        if ( request.getMaxEntrepreneurships() != null ) {
            entity.setMaxEntrepreneurships( request.getMaxEntrepreneurships() );
        }
        if ( request.getVirtualLink() != null ) {
            entity.setVirtualLink( request.getVirtualLink() );
        }
        if ( request.getStartDatetime() != null ) {
            entity.setStartDatetime( request.getStartDatetime() );
        }
        if ( request.getEndDatetime() != null ) {
            entity.setEndDatetime( request.getEndDatetime() );
        }
        if ( request.getCountryId() != null ) {
            entity.setCountryId( request.getCountryId() );
        }
        if ( request.getProvinceId() != null ) {
            entity.setProvinceId( request.getProvinceId() );
        }
        if ( request.getCityId() != null ) {
            entity.setCityId( request.getCityId() );
        }
        if ( request.getAddressLine() != null ) {
            entity.setAddressLine( request.getAddressLine() );
        }
        if ( request.getMapsUrl() != null ) {
            entity.setMapsUrl( request.getMapsUrl() );
        }
    }
}
