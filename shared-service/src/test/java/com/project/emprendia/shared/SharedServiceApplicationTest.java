package com.project.emprendia.shared;

import com.project.emprendia.shared.service.CatalogueTypeService;
import com.project.emprendia.shared.service.CatalogueValueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class SharedServiceApplicationTest {

    @MockBean
    private CatalogueTypeService catalogueTypeService;

    @MockBean
    private CatalogueValueService catalogueValueService;

    @Test
    void contextLoads() {
    }
}
