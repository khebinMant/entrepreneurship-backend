package com.project.emprendia.shared.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCatalogueValue is a Querydsl query type for CatalogueValue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCatalogueValue extends EntityPathBase<CatalogueValue> {

    private static final long serialVersionUID = -252142640L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCatalogueValue catalogueValue = new QCatalogueValue("catalogueValue");

    public final QCatalogueType catalogueType;

    public final NumberPath<Long> catalogueValueId = createNumber("catalogueValueId", Long.class);

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final StringPath name = createString("name");

    public final QCatalogueValue parentValue;

    public QCatalogueValue(String variable) {
        this(CatalogueValue.class, forVariable(variable), INITS);
    }

    public QCatalogueValue(Path<? extends CatalogueValue> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCatalogueValue(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCatalogueValue(PathMetadata metadata, PathInits inits) {
        this(CatalogueValue.class, metadata, inits);
    }

    public QCatalogueValue(Class<? extends CatalogueValue> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.catalogueType = inits.isInitialized("catalogueType") ? new QCatalogueType(forProperty("catalogueType")) : null;
        this.parentValue = inits.isInitialized("parentValue") ? new QCatalogueValue(forProperty("parentValue"), inits.get("parentValue")) : null;
    }

}

