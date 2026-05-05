package com.project.emprendia.entrepreneurship.mapping;

import com.project.emprendia.entrepreneurship.domain.Category;
import com.project.emprendia.entrepreneurship.dto.CategoryRequest;
import com.project.emprendia.entrepreneurship.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for Category entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
