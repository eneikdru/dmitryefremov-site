package com.dmitryefremov.site;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SeededDataApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHealthEndpointIsReachableAndUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void testSeededArticlesAreReachableAndPresent() throws Exception {
        mockMvc.perform(get("/api/v1/articles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.content[*].slug", hasItem("deontic-logic-autonomous-systems")))
                .andExpect(jsonPath("$.content[*].slug", hasItem("theory-of-constraints-engineering")));
    }

    @Test
    void testSeededPodcastsAreReachableAndPresent() throws Exception {
        mockMvc.perform(get("/api/v1/podcasts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.content[*].slug", hasItem("systems-and-reality-ep1")))
                .andExpect(jsonPath("$.content[*].slug", hasItem("systems-and-reality-ep2")));
    }

    @Test
    void testFeedXmlIsReachableAndReturnsXmlContent() throws Exception {
        mockMvc.perform(get("/feed.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }

    @Test
    void testSitemapXmlIsReachableAndReturnsXmlContent() throws Exception {
        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }
}
