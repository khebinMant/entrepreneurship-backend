package com.project.emprendia.shared.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCatalogueType is a Querydsl query type for CatalogueType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCatalogueType extends EntityPathBase<CatalogueType> {

    private static final long serialVersionUID = 1931492603L;

    public static final QCatalogueType catalogueType = new QCatalogueType("catalogueType");

    public final NumberPath<Long> catalogueTypeId = createNumber("catalogueTypeId", Long.class);

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final StringPath name = createString("name");

    public QCatalogueType(String variable) {
        super(CatalogueType.class, forVariable(variable));
    }

    public QCatalogueType(Path<? extends CatalogueType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCatalogueType(PathMetadata metadata) {
        super(CatalogueType.class, metadata);
    }

}

