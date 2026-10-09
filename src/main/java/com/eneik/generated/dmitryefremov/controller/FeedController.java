package com.eneik.generated.dmitryefremov.controller;

import com.eneik.generated.dmitryefremov.model.Article;
import com.eneik.generated.dmitryefremov.model.PodcastEpisode;
import com.eneik.generated.dmitryefremov.service.ArticleService;
import com.eneik.generated.dmitryefremov.service.PodcastEpisodeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class FeedController {

    private static final String BASE_URL = "https://dmitryefremov.com";
    private static final DateTimeFormatter RFC1123_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("UTC"));

    private final ArticleService articleService;
    private final PodcastEpisodeService podcastEpisodeService;

    public FeedController(ArticleService articleService, PodcastEpisodeService podcastEpisodeService) {
        this.articleService = articleService;
        this.podcastEpisodeService = podcastEpisodeService;
    }

    @GetMapping(value = "/feed.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String getRssFeed() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        xml.append("  <channel>\n");
        xml.append("    <title>Dmitry Efremov</title>\n");
        xml.append("    <link>").append(BASE_URL).append("</link>\n");
        xml.append("    <description>Dmitry Efremov - Systems, Logic, and Autonomous Processes</description>\n");
        xml.append("    <language>ru</language>\n");

        List<FeedItem> items = new ArrayList<>();

        for (Article article : articleService.getAllArticles()) {
            items.add(new FeedItem(
                article.getTitle(),
                BASE_URL + "/articles/" + article.getSlug(),
                article.getSummary(),
                article.getPublishedAt(),
                "article-" + article.getId()
            ));
        }

        for (PodcastEpisode episode : podcastEpisodeService.getAllEpisodes()) {
            items.add(new FeedItem(
                episode.getTitle(),
                BASE_URL + "/podcasts/" + episode.getSlug(),
                episode.getSummary(),
                episode.getPublishedAt(),
                "podcast-" + episode.getId()
            ));
        }

        items.sort(Comparator.comparing(FeedItem::publishedAt).reversed());

        for (FeedItem item : items) {
            xml.append("    <item>\n");
            xml.append("      <title>").append(escapeXml(item.title())).append("</title>\n");
            xml.append("      <link>").append(escapeXml(item.link())).append("</link>\n");
            xml.append("      <description>").append(escapeXml(item.description())).append("</description>\n");
            xml.append("      <pubDate>").append(RFC1123_FORMATTER.format(item.publishedAt())).append("</pubDate>\n");
            xml.append("      <guid>").append(escapeXml(item.guid())).append("</guid>\n");
            xml.append("    </item>\n");
        }

        xml.append("  </channel>\n");
        xml.append("</rss>");

        return xml.toString();
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String getSitemap() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        List<String> urls = new ArrayList<>();
        urls.add(BASE_URL + "/");
        urls.add(BASE_URL + "/articles");
        urls.add(BASE_URL + "/podcasts");
        urls.add(BASE_URL + "/schedule");

        for (Article article : articleService.getAllArticles()) {
            urls.add(BASE_URL + "/articles/" + article.getSlug());
        }

        for (PodcastEpisode episode : podcastEpisodeService.getAllEpisodes()) {
            urls.add(BASE_URL + "/podcasts/" + episode.getSlug());
        }

        for (String url : urls) {
            xml.append("  <url>\n");
            xml.append("    <loc>").append(escapeXml(url)).append("</loc>\n");
            xml.append("  </url>\n");
        }

        xml.append("</urlset>");

        return xml.toString();
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }

    private record FeedItem(String title, String link, String description, java.time.Instant publishedAt, String guid) {}
}
