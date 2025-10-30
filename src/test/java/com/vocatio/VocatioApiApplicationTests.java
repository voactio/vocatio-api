package com.vocatio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.vocatio.repository.TestimonioRepository;
import com.vocatio.repository.CarreraRepository;

@SpringBootTest
@ActiveProfiles("test")
class VocatioApiApplicationTests {

    @MockBean
    private TestimonioRepository testimonioRepository;

    @MockBean
    private CarreraRepository carreraRepository;

    @Test
    void contextLoads() {
    }

}
