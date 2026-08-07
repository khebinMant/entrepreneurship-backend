package com.project.emprendia.event.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEventEntrepreneurshipParticipant is a Querydsl query type for EventEntrepreneurshipParticipant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventEntrepreneurshipParticipant extends EntityPathBase<EventEntrepreneurshipParticipant> {

    private static final long serialVersionUID = -1310802509L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEventEntrepreneurshipParticipant eventEntrepreneurshipParticipant = new QEventEntrepreneurshipParticipant("eventEntrepreneurshipParticipant");

    public final NumberPath<Long> entrepreneurshipId = createNumber("entrepreneurshipId", Long.class);

    public final QEvent event;

    public final NumberPath<Long> eventParticipantId = createNumber("eventParticipantId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> invitedAt = createDateTime("invitedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> participationStatusId = createNumber("participationStatusId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> respondedAt = createDateTime("respondedAt", java.time.LocalDateTime.class);

    public final StringPath spaceCode = createString("spaceCode");

    public QEventEntrepreneurshipParticipant(String variable) {
        this(EventEntrepreneurshipParticipant.class, forVariable(variable), INITS);
    }

    public QEventEntrepreneurshipParticipant(Path<? extends EventEntrepreneurshipParticipant> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEventEntrepreneurshipParticipant(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEventEntrepreneurshipParticipant(PathMetadata metadata, PathInits inits) {
        this(EventEntrepreneurshipParticipant.class, metadata, inits);
    }

    public QEventEntrepreneurshipParticipant(Class<? extends EventEntrepreneurshipParticipant> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event")) : null;
    }

}

