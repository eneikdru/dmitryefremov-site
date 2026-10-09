package com.eneik.generated.dmitryefremov.controller;

import com.eneik.generated.dmitryefremov.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@Import(ArticleService.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetArticlePageWithOpenGraphTags() throws Exception {
        mockMvc.perform(get("/articles/systems-logic"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("<meta property=\"og:title\" content=\"Deontic Logic in Autonomous Systems\">")))
                .andExpect(content().string(containsString("<meta property=\"og:description\" content=\"An exploration of deontic logic principles and application in software architectures.\">")))
                .andExpect(content().string(containsString("<meta property=\"og:type\" content=\"article\">")))
                .andExpect(content().string(containsString("<meta property=\"og:url\" content=\"https://dmitryefremov.com/articles/systems-logic\">")))
                .andExpect(content().string(containsString("<meta property=\"og:site_name\" content=\"Dmitry Efremov\">")));
    }

    @Test
    void testGetNonExistentArticlePage() throws Exception {
        mockMvc.perform(get("/articles/non-existent-article"))
                .andExpect(status().isNotFound());
    }
}
