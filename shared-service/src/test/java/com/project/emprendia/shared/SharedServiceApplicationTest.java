package com.project.emprendia.shared;

import com.project.emprendia.shared.service.CatalogueTypeService;
import com.project.emprendia.shared.service.CatalogueValueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class SharedServiceApplicationTest {

    @MockitoBean
    private CatalogueTypeService catalogueTypeService;

    @MockitoBean
    private CatalogueValueService catalogueValueService;

    @Test
    void contextLoads() {
    }
}
