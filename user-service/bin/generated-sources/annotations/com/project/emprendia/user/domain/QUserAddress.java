package com.project.emprendia.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAddress is a Querydsl query type for UserAddress
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAddress extends EntityPathBase<UserAddress> {

    private static final long serialVersionUID = -2058435973L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAddress userAddress = new QUserAddress("userAddress");

    public final StringPath addressLine = createString("addressLine");

    public final NumberPath<Long> cityId = createNumber("cityId", Long.class);

    public final NumberPath<Long> countryId = createNumber("countryId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final BooleanPath isPrimary = createBoolean("isPrimary");

    public final NumberPath<Long> parishId = createNumber("parishId", Long.class);

    public final NumberPath<Long> provinceId = createNumber("provinceId", Long.class);

    public final StringPath reference = createString("reference");

    public final QAppUser user;

    public final NumberPath<Long> userAddressId = createNumber("userAddressId", Long.class);

    public QUserAddress(String variable) {
        this(UserAddress.class, forVariable(variable), INITS);
    }

    public QUserAddress(Path<? extends UserAddress> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAddress(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAddress(PathMetadata metadata, PathInits inits) {
        this(UserAddress.class, metadata, inits);
    }

    public QUserAddress(Class<? extends UserAddress> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QAppUser(forProperty("user")) : null;
    }

}

