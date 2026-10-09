package com.eneik.generated.dmitryefremov.controller;

import com.eneik.generated.dmitryefremov.service.ArticleService;
import com.eneik.generated.dmitryefremov.service.PodcastEpisodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedController.class)
@Import({ArticleService.class, PodcastEpisodeService.class})
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRssFeedStructureAndValidationRules() throws Exception {
        MvcResult result = mockMvc.perform(get("/feed.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andReturn();

        String xmlContent = result.getResponse().getContentAsString();
        Document doc = parseXml(xmlContent);

        Element rss = doc.getDocumentElement();
        assertThat(rss.getTagName()).isEqualTo("rss");
        assertThat(rss.getAttribute("version")).isEqualTo("2.0");

        NodeList channelList = rss.getElementsByTagName("channel");
        assertThat(channelList.getLength()).isEqualTo(1);
        Element channel = (Element) channelList.item(0);

        assertThat(getChildText(channel, "title")).isEqualTo("Dmitry Efremov");
        assertThat(getChildText(channel, "link")).isEqualTo("https://dmitryefremov.com");
        assertThat(getChildText(channel, "description")).isEqualTo("Dmitry Efremov - Systems, Logic, and Autonomous Processes");
        assertThat(getChildText(channel, "language")).isEqualTo("ru");

        NodeList items = channel.getElementsByTagName("item");
        assertThat(items.getLength()).isGreaterThanOrEqualTo(2);

        DateTimeFormatter rfc1123Formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            String title = getChildText(item, "title");
            String link = getChildText(item, "link");
            String description = getChildText(item, "description");
            String pubDate = getChildText(item, "pubDate");
            String guid = getChildText(item, "guid");

            assertThat(title).isNotBlank();
            assertThat(link).startsWith("https://dmitryefremov.com/");
            assertThat(description).isNotBlank();
            assertThat(guid).isNotBlank();

            // Validate pubDate format against standard RFC 1123
            assertThat(pubDate).isNotNull();
            assertThat(rfc1123Formatter.parse(pubDate)).isNotNull();
        }
    }

    @Test
    void testSitemapXmlStructureAndValidation() throws Exception {
        MvcResult result = mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andReturn();

        String xmlContent = result.getResponse().getContentAsString();
        Document doc = parseXml(xmlContent);

        Element urlset = doc.getDocumentElement();
        assertThat(urlset.getTagName()).isEqualTo("urlset");
        assertThat(urlset.getAttribute("xmlns")).isEqualTo("http://www.sitemaps.org/schemas/sitemap/0.9");

        NodeList urlList = urlset.getElementsByTagName("url");
        assertThat(urlList.getLength()).isGreaterThanOrEqualTo(4);

        for (int i = 0; i < urlList.getLength(); i++) {
            Element urlElem = (Element) urlList.item(i);
            String loc = getChildText(urlElem, "loc");
            assertThat(loc).startsWith("https://dmitryefremov.com");
        }
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    private String getChildText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }
}
