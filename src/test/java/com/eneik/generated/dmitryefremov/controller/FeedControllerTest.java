package com.eneik.generated.dmitryefremov.controller;

import com.eneik.generated.dmitryefremov.service.ArticleService;
import com.eneik.generated.dmitryefremov.service.PodcastEpisodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedController.class)
@Import({ArticleService.class, PodcastEpisodeService.class})
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetRssFeed() throws Exception {
        mockMvc.perform(get("/feed.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(containsString("<rss version=\"2.0\"")))
                .andExpect(content().string(containsString("<title>Dmitry Efremov</title>")))
                .andExpect(content().string(containsString("Deontic Logic in Autonomous Systems")))
                .andExpect(content().string(containsString("Systems and Reality: Episode 1")))
                .andExpect(content().string(containsString("https://dmitryefremov.com/articles/systems-logic")))
                .andExpect(content().string(containsString("https://dmitryefremov.com/podcasts/ep-01-systems-reality")));
    }

    @Test
    void testGetSitemap() throws Exception {
        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(containsString("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")))
                .andExpect(content().string(containsString("<loc>https://dmitryefremov.com/</loc>")))
                .andExpect(content().string(containsString("<loc>https://dmitryefremov.com/articles</loc>")))
                .andExpect(content().string(containsString("<loc>https://dmitryefremov.com/podcasts</loc>")))
                .andExpect(content().string(containsString("<loc>https://dmitryefremov.com/articles/systems-logic</loc>")))
                .andExpect(content().string(containsString("<loc>https://dmitryefremov.com/podcasts/ep-01-systems-reality</loc>")));
    }
}
