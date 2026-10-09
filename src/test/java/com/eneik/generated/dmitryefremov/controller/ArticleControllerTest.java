package com.eneik.generated.dmitryefremov.controller;

import com.eneik.generated.dmitryefremov.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
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
        MvcResult result = mockMvc.perform(get("/articles/systems-logic"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        String html = result.getResponse().getContentAsString();

        assertThat(extractMetaProperty(html, "og:title")).isEqualTo("Deontic Logic in Autonomous Systems");
        assertThat(extractMetaProperty(html, "og:description")).isEqualTo("An exploration of deontic logic principles and application in software architectures.");
        assertThat(extractMetaProperty(html, "og:type")).isEqualTo("article");
        assertThat(extractMetaProperty(html, "og:url")).isEqualTo("https://dmitryefremov.com/articles/systems-logic");
        assertThat(extractMetaProperty(html, "og:site_name")).isEqualTo("Dmitry Efremov");
    }

    @Test
    void testGetNonExistentArticlePage() throws Exception {
        mockMvc.perform(get("/articles/non-existent-article"))
                .andExpect(status().isNotFound());
    }

    private String extractMetaProperty(String html, String property) {
        Pattern pattern = Pattern.compile("<meta\\s+property=\"" + Pattern.quote(property) + "\"\\s+content=\"([^\"]*)\"\\s*/?>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
