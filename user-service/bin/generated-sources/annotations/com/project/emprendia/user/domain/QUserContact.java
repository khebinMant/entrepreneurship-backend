package com.project.emprendia.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserContact is a Querydsl query type for UserContact
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserContact extends EntityPathBase<UserContact> {

    private static final long serialVersionUID = 40782503L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserContact userContact = new QUserContact("userContact");

    public final NumberPath<Long> contactTypeId = createNumber("contactTypeId", Long.class);

    public final StringPath contactValue = createString("contactValue");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final BooleanPath isPrimary = createBoolean("isPrimary");

    public final QAppUser user;

    public final NumberPath<Long> userContactId = createNumber("userContactId", Long.class);

    public QUserContact(String variable) {
        this(UserContact.class, forVariable(variable), INITS);
    }

    public QUserContact(Path<? extends UserContact> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserContact(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserContact(PathMetadata metadata, PathInits inits) {
        this(UserContact.class, metadata, inits);
    }

    public QUserContact(Class<? extends UserContact> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QAppUser(forProperty("user")) : null;
    }

}

