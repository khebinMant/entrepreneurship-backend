package com.project.emprendia.shared.mapping.mapper;

import com.project.emprendia.shared.domain.CatalogueType;
import com.project.emprendia.shared.dto.CatalogueTypeRequest;
import com.project.emprendia.shared.dto.CatalogueTypeResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-27T21:29:06-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CatalogueTypeMapperImpl implements CatalogueTypeMapper {

    @Override
    public CatalogueTypeResponse toResponse(CatalogueType entity) {
        if ( entity == null ) {
            return null;
        }

        CatalogueTypeResponse.CatalogueTypeResponseBuilder catalogueTypeResponse = CatalogueTypeResponse.builder();

        catalogueTypeResponse.catalogueTypeId( entity.getCatalogueTypeId() );
        catalogueTypeResponse.code( entity.getCode() );
        catalogueTypeResponse.description( entity.getDescription() );
        catalogueTypeResponse.name( entity.getName() );

        return catalogueTypeResponse.build();
    }

    @Override
    public CatalogueType toEntity(CatalogueTypeRequest request) {
        if ( request == null ) {
            return null;
        }

        CatalogueType.CatalogueTypeBuilder catalogueType = CatalogueType.builder();

        catalogueType.code( request.getCode() );
        catalogueType.description( request.getDescription() );
        catalogueType.name( request.getName() );

        return catalogueType.build();
    }

    @Override
    public void updateEntityFromRequest(CatalogueTypeRequest request, CatalogueType entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getCode() != null ) {
            entity.setCode( request.getCode() );
        }
        if ( request.getDescription() != null ) {
            entity.setDescription( request.getDescription() );
        }
        if ( request.getName() != null ) {
            entity.setName( request.getName() );
        }
    }
}
