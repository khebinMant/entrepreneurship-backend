package com.project.emprendia.event.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEventInvitation is a Querydsl query type for EventInvitation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventInvitation extends EntityPathBase<EventInvitation> {

    private static final long serialVersionUID = 759451108L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEventInvitation eventInvitation = new QEventInvitation("eventInvitation");

    public final NumberPath<Long> entrepreneurshipId = createNumber("entrepreneurshipId", Long.class);

    public final QEvent event;

    public final QEventSpace eventSpace;

    public final NumberPath<Long> invitationId = createNumber("invitationId", Long.class);

    public final NumberPath<Long> invitationStatusId = createNumber("invitationStatusId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> respondedAt = createDateTime("respondedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> sentAt = createDateTime("sentAt", java.time.LocalDateTime.class);

    public QEventInvitation(String variable) {
        this(EventInvitation.class, forVariable(variable), INITS);
    }

    public QEventInvitation(Path<? extends EventInvitation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEventInvitation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEventInvitation(PathMetadata metadata, PathInits inits) {
        this(EventInvitation.class, metadata, inits);
    }

    public QEventInvitation(Class<? extends EventInvitation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event")) : null;
        this.eventSpace = inits.isInitialized("eventSpace") ? new QEventSpace(forProperty("eventSpace"), inits.get("eventSpace")) : null;
    }

}

