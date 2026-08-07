package com.project.emprendia.event.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEvent is a Querydsl query type for Event
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvent extends EntityPathBase<Event> {

    private static final long serialVersionUID = 1777509355L;

    public static final QEvent event = new QEvent("event");

    public final StringPath addressLine = createString("addressLine");

    public final NumberPath<Long> cityId = createNumber("cityId", Long.class);

    public final NumberPath<Long> countryId = createNumber("countryId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> createdByUserId = createNumber("createdByUserId", Long.class);

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> endDatetime = createDateTime("endDatetime", java.time.LocalDateTime.class);

    public final NumberPath<Long> eventId = createNumber("eventId", Long.class);

    public final NumberPath<Long> eventTypeId = createNumber("eventTypeId", Long.class);

    public final NumberPath<Long> eventVisibilityId = createNumber("eventVisibilityId", Long.class);

    public final ListPath<EventInvitation, QEventInvitation> invitations = this.<EventInvitation, QEventInvitation>createList("invitations", EventInvitation.class, QEventInvitation.class, PathInits.DIRECT2);

    public final BooleanPath isPaid = createBoolean("isPaid");

    public final StringPath mapsUrl = createString("mapsUrl");

    public final NumberPath<Integer> maxAttendees = createNumber("maxAttendees", Integer.class);

    public final NumberPath<Integer> maxEntrepreneurships = createNumber("maxEntrepreneurships", Integer.class);

    public final StringPath name = createString("name");

    public final ListPath<EventEntrepreneurshipParticipant, QEventEntrepreneurshipParticipant> participants = this.<EventEntrepreneurshipParticipant, QEventEntrepreneurshipParticipant>createList("participants", EventEntrepreneurshipParticipant.class, QEventEntrepreneurshipParticipant.class, PathInits.DIRECT2);

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public final NumberPath<Long> provinceId = createNumber("provinceId", Long.class);

    public final ListPath<EventSpace, QEventSpace> spaces = this.<EventSpace, QEventSpace>createList("spaces", EventSpace.class, QEventSpace.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> startDatetime = createDateTime("startDatetime", java.time.LocalDateTime.class);

    public final StringPath virtualLink = createString("virtualLink");

    public QEvent(String variable) {
        super(Event.class, forVariable(variable));
    }

    public QEvent(Path<? extends Event> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEvent(PathMetadata metadata) {
        super(Event.class, metadata);
    }

}

