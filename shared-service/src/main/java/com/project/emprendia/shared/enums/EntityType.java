package com.project.emprendia.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing the types of entities that can have image galleries.
 * Used in the image_gallery table to identify the owner entity.
 */
@Getter
@RequiredArgsConstructor
public enum EntityType {
    
    USER("USER", "User Profile and Gallery"),
    ENTREPRENEURSHIP("ENTREPRENEURSHIP", "Entrepreneurship Products and Gallery"),
    EVENT("EVENT", "Event Moments and Gallery");
    
    private final String code;
    private final String description;
    
    /**
     * Get EntityType from code
     * @param code the code to lookup
     * @return EntityType matching the code
     * @throws IllegalArgumentException if code doesn't match any EntityType
     */
    public static EntityType fromCode(String code) {
        for (EntityType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid entity type code: " + code);
    }
}
