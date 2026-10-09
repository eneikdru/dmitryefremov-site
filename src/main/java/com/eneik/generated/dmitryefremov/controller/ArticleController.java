package com.eneik.generated.dmitryefremov.controller;

import com.eneik.generated.dmitryefremov.model.Article;
import com.eneik.generated.dmitryefremov.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ArticleController {

    private static final String BASE_URL = "https://dmitryefremov.com";
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping(value = "/articles/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getArticlePage(@PathVariable String slug) {
        Optional<Article> articleOpt = articleService.findBySlug(slug);
        if (articleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("<html><body><h1>404 Not Found</h1></body></html>");
        }

        Article article = articleOpt.get();
        String articleUrl = BASE_URL + "/articles/" + article.getSlug();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"ru\">\n");
        html.append("<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <title>").append(escapeHtml(article.getTitle())).append(" | Dmitry Efremov</title>\n");
        html.append("  <meta name=\"description\" content=\"").append(escapeHtml(article.getSummary())).append("\">\n");
        html.append("  <!-- OpenGraph SEO Meta Tags -->\n");
        html.append("  <meta property=\"og:title\" content=\"").append(escapeHtml(article.getTitle())).append("\">\n");
        html.append("  <meta property=\"og:description\" content=\"").append(escapeHtml(article.getSummary())).append("\">\n");
        html.append("  <meta property=\"og:type\" content=\"article\">\n");
        html.append("  <meta property=\"og:url\" content=\"").append(escapeHtml(articleUrl)).append("\">\n");
        html.append("  <meta property=\"og:site_name\" content=\"Dmitry Efremov\">\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("  <main>\n");
        html.append("    <h1>").append(escapeHtml(article.getTitle())).append("</h1>\n");
        html.append("    <p>").append(escapeHtml(article.getSummary())).append("</p>\n");
        html.append("    <div>").append(escapeHtml(article.getContent())).append("</div>\n");
        html.append("  </main>\n");
        html.append("</body>\n");
        html.append("</html>");

        return ResponseEntity.ok(html.toString());
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
