package com.project.emprendia.shared.mapping.mapper;

import com.project.emprendia.shared.domain.CatalogueValue;
import com.project.emprendia.shared.dto.CatalogueValueRequest;
import com.project.emprendia.shared.dto.CatalogueValueResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CatalogueValueMapper {

    @Mapping(target = "catalogueTypeId", source = "catalogueType.catalogueTypeId")
    @Mapping(target = "catalogueTypeCode", source = "catalogueType.code")
    @Mapping(target = "parentValueId", source = "parentValue.catalogueValueId")
    @Mapping(target = "parentValueName", source = "parentValue.name")
    CatalogueValueResponse toResponse(CatalogueValue entity);

    @Mapping(target = "catalogueValueId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "catalogueType", ignore = true)
    @Mapping(target = "parentValue", ignore = true)
    CatalogueValue toEntity(CatalogueValueRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "catalogueValueId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "catalogueType", ignore = true)
    @Mapping(target = "parentValue", ignore = true)
    void updateEntityFromRequest(CatalogueValueRequest request, @MappingTarget CatalogueValue entity);
}
