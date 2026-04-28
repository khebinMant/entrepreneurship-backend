package com.project.emprendia.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserIdentification is a Querydsl query type for UserIdentification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserIdentification extends EntityPathBase<UserIdentification> {

    private static final long serialVersionUID = 2001602055L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserIdentification userIdentification = new QUserIdentification("userIdentification");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath identificationNumber = createString("identificationNumber");

    public final NumberPath<Long> identificationTypeId = createNumber("identificationTypeId", Long.class);

    public final NumberPath<Long> issuedCountryId = createNumber("issuedCountryId", Long.class);

    public final QAppUser user;

    public final NumberPath<Long> userIdentificationId = createNumber("userIdentificationId", Long.class);

    public QUserIdentification(String variable) {
        this(UserIdentification.class, forVariable(variable), INITS);
    }

    public QUserIdentification(Path<? extends UserIdentification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserIdentification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserIdentification(PathMetadata metadata, PathInits inits) {
        this(UserIdentification.class, metadata, inits);
    }

    public QUserIdentification(Class<? extends UserIdentification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QAppUser(forProperty("user")) : null;
    }

}

