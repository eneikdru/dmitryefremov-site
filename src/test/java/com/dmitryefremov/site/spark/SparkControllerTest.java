package com.dmitryefremov.site.spark;

import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SparkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SparkRepository sparkRepository;

    @BeforeEach
    void setUp() {
        sparkRepository.deleteAll();
    }

    @Test
    void testListAndFilterSparks() throws Exception {
        Spark spark1 = new Spark("spark-1", "Test Title 1", "Content 1", "AUTONOMOUS_ENGINEERING", "https://t.me/efremov_mvp/1", OffsetDateTime.now());
        Spark spark2 = new Spark("spark-2", "Test Title 2", "Content 2", "PHILOSOPHY_OF_MIND", "https://t.me/efremov_mvp/2", OffsetDateTime.now());
        sparkRepository.save(spark1);
        sparkRepository.save(spark2);

        mockMvc.perform(get("/api/v1/sparks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.items", hasSize(2)));

        mockMvc.perform(get("/api/v1/sparks")
                        .param("domain", "PHILOSOPHY_OF_MIND")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].slug").value("spark-2"));
    }

    @Test
    void testGetSparkBySlugNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/sparks/non-existent-slug"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Spark with slug 'non-existent-slug' was not found"));
    }
}
