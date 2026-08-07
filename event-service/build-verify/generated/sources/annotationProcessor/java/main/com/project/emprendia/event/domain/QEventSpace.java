package com.project.emprendia.event.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEventSpace is a Querydsl query type for EventSpace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventSpace extends EntityPathBase<EventSpace> {

    private static final long serialVersionUID = -1190914085L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEventSpace eventSpace = new QEventSpace("eventSpace");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QEvent event;

    public final NumberPath<Long> eventSpaceId = createNumber("eventSpaceId", Long.class);

    public final BooleanPath isAvailable = createBoolean("isAvailable");

    public final StringPath spaceCode = createString("spaceCode");

    public QEventSpace(String variable) {
        this(EventSpace.class, forVariable(variable), INITS);
    }

    public QEventSpace(Path<? extends EventSpace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEventSpace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEventSpace(PathMetadata metadata, PathInits inits) {
        this(EventSpace.class, metadata, inits);
    }

    public QEventSpace(Class<? extends EventSpace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event")) : null;
    }

}

