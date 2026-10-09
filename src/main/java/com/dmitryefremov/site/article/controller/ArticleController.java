package com.dmitryefremov.site.article.controller;

import com.dmitryefremov.site.article.dto.ArticlePageDto;
import com.dmitryefremov.site.article.service.ArticleService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController("siteArticleController")
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(@Qualifier("siteArticleService") ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<ArticlePageDto> listArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ArticlePageDto result = articleService.getArticles(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getArticleBySlug(@PathVariable String slug) {
        return articleService.getArticleBySlug(slug)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ErrorResponseDto error = new ErrorResponseDto(
                            HttpStatus.NOT_FOUND.value(),
                            "Article with slug '" + slug + "' was not found",
                            OffsetDateTime.now()
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
