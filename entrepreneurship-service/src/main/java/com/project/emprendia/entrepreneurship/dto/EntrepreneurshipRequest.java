package com.project.emprendia.entrepreneurship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EntrepreneurshipRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long categoryId;

    @NotBlank
    @Size(max = 150)
    private String name;

    private String description;
    private String logoUrl;

    @NotNull
    private Boolean isPhysical;

    @NotNull
    private Boolean isDigital;
}
