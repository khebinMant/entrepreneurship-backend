package com.project.emprendia.entrepreneurship.mapping.mapper;

import com.project.emprendia.entrepreneurship.domain.Category;
import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-06T21:53:49-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.4.1.jar, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class EntrepreneurshipMapperImpl implements EntrepreneurshipMapper {

    @Override
    public EntrepreneurshipResponse toResponse(Entrepreneurship entity) {
        if ( entity == null ) {
            return null;
        }

        EntrepreneurshipResponse.EntrepreneurshipResponseBuilder entrepreneurshipResponse = EntrepreneurshipResponse.builder();

        entrepreneurshipResponse.categoryId( entityCategoryCategoryId( entity ) );
        entrepreneurshipResponse.categoryName( entityCategoryName( entity ) );
        entrepreneurshipResponse.entrepreneurshipId( entity.getEntrepreneurshipId() );
        entrepreneurshipResponse.userId( entity.getUserId() );
        entrepreneurshipResponse.name( entity.getName() );
        entrepreneurshipResponse.description( entity.getDescription() );
        entrepreneurshipResponse.logoUrl( entity.getLogoUrl() );
        entrepreneurshipResponse.isPhysical( entity.getIsPhysical() );
        entrepreneurshipResponse.isDigital( entity.getIsDigital() );
        entrepreneurshipResponse.createdAt( entity.getCreatedAt() );
        entrepreneurshipResponse.updatedAt( entity.getUpdatedAt() );

        return entrepreneurshipResponse.build();
    }

    @Override
    public Entrepreneurship toEntity(EntrepreneurshipRequest request) {
        if ( request == null ) {
            return null;
        }

        Entrepreneurship.EntrepreneurshipBuilder entrepreneurship = Entrepreneurship.builder();

        entrepreneurship.userId( request.getUserId() );
        entrepreneurship.name( request.getName() );
        entrepreneurship.description( request.getDescription() );
        entrepreneurship.logoUrl( request.getLogoUrl() );
        entrepreneurship.isPhysical( request.getIsPhysical() );
        entrepreneurship.isDigital( request.getIsDigital() );

        return entrepreneurship.build();
    }

    @Override
    public void updateEntityFromRequest(EntrepreneurshipRequest request, Entrepreneurship entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getName() != null ) {
            entity.setName( request.getName() );
        }
        if ( request.getDescription() != null ) {
            entity.setDescription( request.getDescription() );
        }
        if ( request.getLogoUrl() != null ) {
            entity.setLogoUrl( request.getLogoUrl() );
        }
        if ( request.getIsPhysical() != null ) {
            entity.setIsPhysical( request.getIsPhysical() );
        }
        if ( request.getIsDigital() != null ) {
            entity.setIsDigital( request.getIsDigital() );
        }
    }

    private Long entityCategoryCategoryId(Entrepreneurship entrepreneurship) {
        Category category = entrepreneurship.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getCategoryId();
    }

    private String entityCategoryName(Entrepreneurship entrepreneurship) {
        Category category = entrepreneurship.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getName();
    }
}
