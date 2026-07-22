package com.project.emprendia.entrepreneurship.mapping.mapper;

import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EntrepreneurshipMapper {

    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "socialLinks", ignore = true)
    @Mapping(target = "portal", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "imageId", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    EntrepreneurshipResponse toResponse(Entrepreneurship entity);

    @Mapping(target = "entrepreneurshipId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "locations", ignore = true)
    Entrepreneurship toEntity(EntrepreneurshipRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "entrepreneurshipId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "locations", ignore = true)
    void updateEntityFromRequest(EntrepreneurshipRequest request, @MappingTarget Entrepreneurship entity);
}
