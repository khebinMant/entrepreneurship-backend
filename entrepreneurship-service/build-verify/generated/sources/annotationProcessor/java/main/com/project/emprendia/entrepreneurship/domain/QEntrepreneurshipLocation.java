package com.project.emprendia.entrepreneurship.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEntrepreneurshipLocation is a Querydsl query type for EntrepreneurshipLocation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEntrepreneurshipLocation extends EntityPathBase<EntrepreneurshipLocation> {

    private static final long serialVersionUID = -720015454L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEntrepreneurshipLocation entrepreneurshipLocation = new QEntrepreneurshipLocation("entrepreneurshipLocation");

    public final StringPath addressLine = createString("addressLine");

    public final NumberPath<Long> cityId = createNumber("cityId", Long.class);

    public final NumberPath<Long> countryId = createNumber("countryId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QEntrepreneurship entrepreneurship;

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<Long> locationId = createNumber("locationId", Long.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    public final StringPath mapsUrl = createString("mapsUrl");

    public final NumberPath<Long> parishId = createNumber("parishId", Long.class);

    public final NumberPath<Long> provinceId = createNumber("provinceId", Long.class);

    public QEntrepreneurshipLocation(String variable) {
        this(EntrepreneurshipLocation.class, forVariable(variable), INITS);
    }

    public QEntrepreneurshipLocation(Path<? extends EntrepreneurshipLocation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEntrepreneurshipLocation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEntrepreneurshipLocation(PathMetadata metadata, PathInits inits) {
        this(EntrepreneurshipLocation.class, metadata, inits);
    }

    public QEntrepreneurshipLocation(Class<? extends EntrepreneurshipLocation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.entrepreneurship = inits.isInitialized("entrepreneurship") ? new QEntrepreneurship(forProperty("entrepreneurship"), inits.get("entrepreneurship")) : null;
    }

}

