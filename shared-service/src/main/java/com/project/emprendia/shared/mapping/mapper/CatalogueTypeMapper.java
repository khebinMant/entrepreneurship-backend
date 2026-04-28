package com.project.emprendia.shared.mapping.mapper;

import com.project.emprendia.shared.domain.CatalogueType;
import com.project.emprendia.shared.dto.CatalogueTypeRequest;
import com.project.emprendia.shared.dto.CatalogueTypeResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CatalogueTypeMapper {

    CatalogueTypeResponse toResponse(CatalogueType entity);

    @Mapping(target = "catalogueTypeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CatalogueType toEntity(CatalogueTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "catalogueTypeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(CatalogueTypeRequest request, @MappingTarget CatalogueType entity);
}
