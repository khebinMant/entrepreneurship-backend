package com.project.emprendia.shared.mapping.mapper;

import com.project.emprendia.shared.domain.CatalogueType;
import com.project.emprendia.shared.domain.CatalogueValue;
import com.project.emprendia.shared.dto.CatalogueValueRequest;
import com.project.emprendia.shared.dto.CatalogueValueResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-27T21:43:37-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CatalogueValueMapperImpl implements CatalogueValueMapper {

    @Override
    public CatalogueValueResponse toResponse(CatalogueValue entity) {
        if ( entity == null ) {
            return null;
        }

        CatalogueValueResponse.CatalogueValueResponseBuilder catalogueValueResponse = CatalogueValueResponse.builder();

        catalogueValueResponse.catalogueTypeId( entityCatalogueTypeCatalogueTypeId( entity ) );
        catalogueValueResponse.catalogueTypeCode( entityCatalogueTypeCode( entity ) );
        catalogueValueResponse.parentValueId( entityParentValueCatalogueValueId( entity ) );
        catalogueValueResponse.parentValueName( entityParentValueName( entity ) );
        catalogueValueResponse.catalogueValueId( entity.getCatalogueValueId() );
        catalogueValueResponse.code( entity.getCode() );
        catalogueValueResponse.name( entity.getName() );
        catalogueValueResponse.description( entity.getDescription() );

        return catalogueValueResponse.build();
    }

    @Override
    public CatalogueValue toEntity(CatalogueValueRequest request) {
        if ( request == null ) {
            return null;
        }

        CatalogueValue.CatalogueValueBuilder catalogueValue = CatalogueValue.builder();

        catalogueValue.code( request.getCode() );
        catalogueValue.name( request.getName() );
        catalogueValue.description( request.getDescription() );

        return catalogueValue.build();
    }

    @Override
    public void updateEntityFromRequest(CatalogueValueRequest request, CatalogueValue entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getCode() != null ) {
            entity.setCode( request.getCode() );
        }
        if ( request.getName() != null ) {
            entity.setName( request.getName() );
        }
        if ( request.getDescription() != null ) {
            entity.setDescription( request.getDescription() );
        }
    }

    private Long entityCatalogueTypeCatalogueTypeId(CatalogueValue catalogueValue) {
        CatalogueType catalogueType = catalogueValue.getCatalogueType();
        if ( catalogueType == null ) {
            return null;
        }
        return catalogueType.getCatalogueTypeId();
    }

    private String entityCatalogueTypeCode(CatalogueValue catalogueValue) {
        CatalogueType catalogueType = catalogueValue.getCatalogueType();
        if ( catalogueType == null ) {
            return null;
        }
        return catalogueType.getCode();
    }

    private Long entityParentValueCatalogueValueId(CatalogueValue catalogueValue) {
        CatalogueValue parentValue = catalogueValue.getParentValue();
        if ( parentValue == null ) {
            return null;
        }
        return parentValue.getCatalogueValueId();
    }

    private String entityParentValueName(CatalogueValue catalogueValue) {
        CatalogueValue parentValue = catalogueValue.getParentValue();
        if ( parentValue == null ) {
            return null;
        }
        return parentValue.getName();
    }
}
