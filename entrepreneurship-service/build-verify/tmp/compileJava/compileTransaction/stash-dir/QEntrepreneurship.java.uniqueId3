package com.project.emprendia.entrepreneurship.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEntrepreneurship is a Querydsl query type for Entrepreneurship
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEntrepreneurship extends EntityPathBase<Entrepreneurship> {

    private static final long serialVersionUID = -802780979L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEntrepreneurship entrepreneurship = new QEntrepreneurship("entrepreneurship");

    public final QCategory category;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> entrepreneurshipId = createNumber("entrepreneurshipId", Long.class);

    public final BooleanPath isDigital = createBoolean("isDigital");

    public final BooleanPath isPhysical = createBoolean("isPhysical");

    public final ListPath<EntrepreneurshipLocation, QEntrepreneurshipLocation> locations = this.<EntrepreneurshipLocation, QEntrepreneurshipLocation>createList("locations", EntrepreneurshipLocation.class, QEntrepreneurshipLocation.class, PathInits.DIRECT2);

    public final StringPath logoUrl = createString("logoUrl");

    public final StringPath name = createString("name");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QEntrepreneurship(String variable) {
        this(Entrepreneurship.class, forVariable(variable), INITS);
    }

    public QEntrepreneurship(Path<? extends Entrepreneurship> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEntrepreneurship(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEntrepreneurship(PathMetadata metadata, PathInits inits) {
        this(Entrepreneurship.class, metadata, inits);
    }

    public QEntrepreneurship(Class<? extends Entrepreneurship> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
    }

}

