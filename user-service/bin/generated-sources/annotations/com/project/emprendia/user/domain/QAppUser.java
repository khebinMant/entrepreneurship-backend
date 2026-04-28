package com.project.emprendia.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAppUser is a Querydsl query type for AppUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAppUser extends EntityPathBase<AppUser> {

    private static final long serialVersionUID = -178626722L;

    public static final QAppUser appUser = new QAppUser("appUser");

    public final ListPath<UserAddress, QUserAddress> addresses = this.<UserAddress, QUserAddress>createList("addresses", UserAddress.class, QUserAddress.class, PathInits.DIRECT2);

    public final ListPath<UserContact, QUserContact> contacts = this.<UserContact, QUserContact>createList("contacts", UserContact.class, QUserContact.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath firstName = createString("firstName");

    public final ListPath<UserIdentification, QUserIdentification> identifications = this.<UserIdentification, QUserIdentification>createList("identifications", UserIdentification.class, QUserIdentification.class, PathInits.DIRECT2);

    public final StringPath keycloakId = createString("keycloakId");

    public final StringPath lastName = createString("lastName");

    public final StringPath profilePictureUrl = createString("profilePictureUrl");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QAppUser(String variable) {
        super(AppUser.class, forVariable(variable));
    }

    public QAppUser(Path<? extends AppUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAppUser(PathMetadata metadata) {
        super(AppUser.class, metadata);
    }

}

