package com.project.emprendia.shared.mapping;

import com.project.emprendia.shared.domain.ImageGallery;
import com.project.emprendia.shared.dto.ImageGalleryRequest;
import com.project.emprendia.shared.dto.ImageGalleryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for ImageGallery entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface ImageGalleryMapper {

    /**
     * Convert ImageGallery entity to response DTO
     */
    ImageGalleryResponse toResponse(ImageGallery imageGallery);

    /**
     * Convert list of ImageGallery entities to list of response DTOs
     */
    List<ImageGalleryResponse> toResponseList(List<ImageGallery> imageGalleries);

    /**
     * Convert request DTO to ImageGallery entity
     */
    @Mapping(target = "imageId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ImageGallery toEntity(ImageGalleryRequest request);

    /**
     * Update existing ImageGallery entity from request DTO
     */
    @Mapping(target = "imageId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ImageGalleryRequest request, @MappingTarget ImageGallery imageGallery);
}
