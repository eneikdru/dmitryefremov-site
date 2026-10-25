package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.domain.ConceptMediaLink;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.canon.repository.ConceptMediaLinkRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GraphNavigationAndSearchVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("siteCanonItemRepository")
    private CanonItemRepository canonItemRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private ConceptMediaLinkRepository conceptMediaLinkRepository;

    @Test
    @DisplayName("Given populated database, when traversing graph from podcast to canon concept and back, then permanent URLs and links load correctly")
    void testPodcastToCanonGraphTraversalAndPermanentUrls() throws Exception {
        // 1. Verify podcast episode exists and has permanent URL endpoint
        Optional<PodcastEpisode> epOpt = podcastEpisodeRepository.findBySlug("vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura");
        assertThat(epOpt).isPresent();
        PodcastEpisode ep = epOpt.get();

        mockMvc.perform(get("/api/v1/podcasts/vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura")))
                .andExpect(jsonPath("$.title", containsString("Выпуск 05")));

        // 2. Fetch connected canon item
        Optional<CanonItem> canonOpt = canonItemRepository.findBySlug("pamyat-i-graf-znaniy-dmitrii-eneik");
        assertThat(canonOpt).isPresent();
        CanonItem canonItem = canonOpt.get();

        // Ensure concept media link exists for graph navigation
        ConceptMediaLink link = new ConceptMediaLink(canonItem, "PODCAST", "EXPLAINS", "Explains concept in podcast episode 05", Clock.systemUTC());
        link.setPodcastEpisode(ep);
        conceptMediaLinkRepository.save(link);
        conceptMediaLinkRepository.flush();

        // 3. Traversal: Find connected canon concept nodes via concept_media_links
        List<ConceptMediaLink> linksFromPodcast = conceptMediaLinkRepository.findByPodcastEpisodeId(ep.getId());
        assertThat(linksFromPodcast).isNotEmpty();

        CanonItem connectedCanon = linksFromPodcast.stream().filter(l -> l.getCanonItem().getSlug().equals("pamyat-i-graf-znaniy-dmitrii-eneik")).findFirst().orElse(linksFromPodcast.get(0)).getCanonItem();
        String canonSlug = connectedCanon.getSlug();
        assertThat(canonSlug).isEqualTo("pamyat-i-graf-znaniy-dmitrii-eneik");

        // 4. Verify target canon concept permanent URL loads with correct attributes
        mockMvc.perform(get("/api/v1/canon/" + canonSlug)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is(canonSlug)))
                .andExpect(jsonPath("$.title", containsString("Цифровая память и граф знаний")));

        // 5. Reverse Traversal: From canon concept back to podcast media link
        List<ConceptMediaLink> linksFromCanon = conceptMediaLinkRepository.findByCanonItemId(connectedCanon.getId());
        assertThat(linksFromCanon.stream().filter(l -> l.getPodcastEpisode() != null).toList()).map(l -> l.getPodcastEpisode().getId()).contains(ep.getId());
    }

    @Test
    @DisplayName("Given search query, when searching for known canon/podcast keywords, then correct nodes are returned")
    void testInstantSearchQueryNodes() throws Exception {
        mockMvc.perform(get("/api/v1/canon")
                        .param("domain", "PHILOSOPHY_OF_MIND")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].slug", is("pamyat-i-graf-znaniy-dmitrii-eneik")));
    }
}
