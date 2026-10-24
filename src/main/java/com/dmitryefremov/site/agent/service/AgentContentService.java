package com.dmitryefremov.site.agent.service;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import com.dmitryefremov.site.agent.domain.ContentAuditLog;
import com.dmitryefremov.site.agent.dto.ArticleUpdateRequestDto;
import com.dmitryefremov.site.agent.dto.CanonUpdateRequestDto;
import com.dmitryefremov.site.agent.dto.EssayUpdateRequestDto;
import com.dmitryefremov.site.agent.dto.PodcastUpdateRequestDto;
import com.dmitryefremov.site.agent.repository.AgentApiTokenRepository;
import com.dmitryefremov.site.agent.repository.ContentAuditLogRepository;
import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.dto.ArticleDto;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.domain.CanonType;
import com.dmitryefremov.site.canon.dto.CanonItemDto;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.domain.PublishedEssay;
import com.dmitryefremov.site.essay.dto.EssayDto;
import com.dmitryefremov.site.essay.repository.EssayRepository;
import com.dmitryefremov.site.essay.repository.PublishedEssayRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.domain.PodcastQuote;
import com.dmitryefremov.site.podcast.domain.PodcastTimecode;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDto;
import com.dmitryefremov.site.podcast.dto.PodcastQuoteDto;
import com.dmitryefremov.site.podcast.dto.PodcastTimecodeDto;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AgentContentService {

    private static final String BASE_URL = "https://dmitryefremov.com";
    private static final DateTimeFormatter RFC1123_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("UTC"));
    private static final Pattern FRONTMATTER_PATTERN = Pattern.compile("^---\\s*\\n(.*?)\\n---\\s*\\n?(.*)$", Pattern.DOTALL);

    private final AgentApiTokenRepository tokenRepository;
    private final ContentAuditLogRepository auditLogRepository;
    private final ArticleRepository articleRepository;
    private final PodcastEpisodeRepository podcastEpisodeRepository;
    private final PublishedEssayRepository publishedEssayRepository;
    private final EssayRepository essayRepository;
    private final CanonItemRepository canonItemRepository;
    private final JdbcTemplate jdbcTemplate;

    public AgentContentService(
            AgentApiTokenRepository tokenRepository,
            ContentAuditLogRepository auditLogRepository,
            ArticleRepository articleRepository,
            PodcastEpisodeRepository podcastEpisodeRepository,
            PublishedEssayRepository publishedEssayRepository,
            EssayRepository essayRepository,
            @Qualifier("siteCanonItemRepository") CanonItemRepository canonItemRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.tokenRepository = tokenRepository;
        this.auditLogRepository = auditLogRepository;
        this.articleRepository = articleRepository;
        this.podcastEpisodeRepository = podcastEpisodeRepository;
        this.publishedEssayRepository = publishedEssayRepository;
        this.essayRepository = essayRepository;
        this.canonItemRepository = canonItemRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AgentApiToken> authenticateToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String cleanToken = token.trim();
        if (cleanToken.toLowerCase().startsWith("bearer ")) {
            cleanToken = cleanToken.substring(7).trim();
        }

        List<AgentApiToken> tokens = tokenRepository.findAll();
        for (AgentApiToken apiToken : tokens) {
            if (cleanToken.equals(apiToken.getTokenHash()) || cleanToken.equals(apiToken.getAgentName())) {
                return Optional.of(apiToken);
            }
        }

        if (jdbcTemplate != null) {
            try {
                Integer credCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM api_credentials WHERE (client_id = ? OR api_key_hash = ?) AND enabled = true",
                        Integer.class,
                        cleanToken, cleanToken
                );
                if (credCount != null && credCount > 0) {
                    return Optional.of(new AgentApiToken("api-credential-agent", cleanToken));
                }
            } catch (DataAccessException ignored) {
            }

            try {
                Integer keyCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM api_keys WHERE (client_id = ? OR key_identity = ? OR key_hash = ? OR key_prefix = ?) AND (status = 'ACTIVE' OR enabled = true)",
                        Integer.class,
                        cleanToken, cleanToken, cleanToken, cleanToken
                );
                if (keyCount != null && keyCount > 0) {
                    return Optional.of(new AgentApiToken("api-key-agent", cleanToken));
                }
            } catch (DataAccessException ignored) {
            }
        }

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllContentAsJson() {
        List<Article> articles = articleRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"));
        List<PodcastEpisode> episodes = podcastEpisodeRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"));
        List<PublishedEssay> essays = publishedEssayRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"));
        List<CanonItem> canonItems = canonItemRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"));

        List<ArticleDto> articleDtos = articles.stream().map(this::mapArticleToDto).toList();
        List<PodcastEpisodeDto> episodeDtos = episodes.stream().map(this::mapPodcastToDto).toList();
        List<EssayDto> essayDtos = essays.stream().map(this::mapEssayToDto).toList();
        List<CanonItemDto> canonDtos = canonItems.stream().map(this::mapCanonToDto).toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("articles", articleDtos);
        response.put("podcasts", episodeDtos);
        response.put("essays", essayDtos);
        response.put("canon", canonDtos);
        return response;
    }

    @Transactional(readOnly = true)
    public String getAllContentAsRss() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        xml.append("  <channel>\n");
        xml.append("    <title>Dmitry Efremov - Content Feed</title>\n");
        xml.append("    <link>").append(BASE_URL).append("</link>\n");
        xml.append("    <description>Headless Content Management Feed</description>\n");
        xml.append("    <language>ru</language>\n");

        record FeedEntry(String title, String link, String description, OffsetDateTime pubDate, String guid) {}
        List<FeedEntry> entries = new ArrayList<>();

        for (Article article : articleRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"))) {
            OffsetDateTime pubDate = article.getPublishedAt() != null ? article.getPublishedAt() : article.getCreatedAt();
            entries.add(new FeedEntry(
                    article.getTitle(),
                    BASE_URL + "/articles/" + article.getSlug(),
                    article.getSummary() != null ? article.getSummary() : "",
                    pubDate,
                    "article-" + article.getId()
            ));
        }

        for (PodcastEpisode episode : podcastEpisodeRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"))) {
            OffsetDateTime pubDate = episode.getPublishedAt() != null ? episode.getPublishedAt() : episode.getCreatedAt();
            entries.add(new FeedEntry(
                    episode.getTitle(),
                    BASE_URL + "/podcasts/" + episode.getSlug(),
                    episode.getDescription() != null ? episode.getDescription() : "",
                    pubDate,
                    "podcast-" + episode.getId()
            ));
        }

        for (PublishedEssay essay : publishedEssayRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"))) {
            OffsetDateTime pubDate = essay.getPublishedAt() != null ? essay.getPublishedAt() : essay.getCreatedAt();
            entries.add(new FeedEntry(
                    essay.getTitle(),
                    BASE_URL + "/essays/" + essay.getSlug(),
                    essay.getSummary() != null ? essay.getSummary() : "",
                    pubDate,
                    "essay-" + essay.getId()
            ));
        }

        for (CanonItem canon : canonItemRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"))) {
            OffsetDateTime pubDate = canon.getPublishedAt() != null ? canon.getPublishedAt() : canon.getCreatedAt();
            entries.add(new FeedEntry(
                    canon.getTitle(),
                    BASE_URL + "/canon/" + canon.getSlug(),
                    canon.getSummary() != null ? canon.getSummary() : "",
                    pubDate,
                    "canon-" + canon.getId()
            ));
        }

        entries.sort(Comparator.comparing(FeedEntry::pubDate).reversed());

        for (FeedEntry entry : entries) {
            xml.append("    <item>\n");
            xml.append("      <title>").append(escapeXml(entry.title())).append("</title>\n");
            xml.append("      <link>").append(escapeXml(entry.link())).append("</link>\n");
            xml.append("      <description>").append(escapeXml(entry.description())).append("</description>\n");
            xml.append("      <pubDate>").append(RFC1123_FORMATTER.format(entry.pubDate())).append("</pubDate>\n");
            xml.append("      <guid>").append(escapeXml(entry.guid())).append("</guid>\n");
            xml.append("    </item>\n");
        }

        xml.append("  </channel>\n");
        xml.append("</rss>");
        return xml.toString();
    }

    @Transactional
    public ArticleDto updateArticleJson(String slug, ArticleUpdateRequestDto dto, AgentApiToken agent) {
        if (dto == null) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }
        Optional<Article> articleOpt = articleRepository.findBySlug(slug);
        Article article;
        String action;
        if (articleOpt.isPresent()) {
            article = articleOpt.get();
            action = "UPDATE";
        } else {
            article = new Article();
            article.setSlug(slug);
            action = "CREATE";
        }

        if (dto.title() != null && !dto.title().isBlank()) {
            article.setTitle(dto.title());
        } else if (action.equals("CREATE")) {
            throw new IllegalArgumentException("Title is required for creating an article");
        }

        if (dto.summary() != null) {
            article.setSummary(dto.summary());
        }

        if (dto.content() != null) {
            article.setContent(dto.content());
        } else if (action.equals("CREATE")) {
            throw new IllegalArgumentException("Content is required for creating an article");
        }

        if (dto.publishedAt() != null) {
            article.setPublishedAt(dto.publishedAt());
        } else if (article.getPublishedAt() == null) {
            article.setPublishedAt(OffsetDateTime.now());
        }

        Article saved = articleRepository.save(article);
        auditLogRepository.save(new ContentAuditLog(agent, "ARTICLE", saved.getId(), action));
        return mapArticleToDto(saved);
    }

    @Transactional
    public ArticleDto updateArticleMarkdown(String slug, String markdown, AgentApiToken agent) {
        if (markdown == null || markdown.isBlank()) {
            throw new IllegalArgumentException("Markdown payload cannot be empty");
        }

        Matcher matcher = FRONTMATTER_PATTERN.matcher(markdown);
        String title = null;
        String summary = null;
        OffsetDateTime publishedAt = null;
        String body = markdown;

        if (matcher.matches()) {
            String frontmatter = matcher.group(1);
            body = matcher.group(2).trim();

            for (String line : frontmatter.split("\n")) {
                line = line.trim();
                if (line.startsWith("title:")) {
                    title = unquote(line.substring(6).trim());
                } else if (line.startsWith("summary:")) {
                    summary = unquote(line.substring(8).trim());
                } else if (line.startsWith("publishedAt:") || line.startsWith("published_at:")) {
                    String pubStr = unquote(line.substring(line.indexOf(':') + 1).trim());
                    try {
                        publishedAt = OffsetDateTime.parse(pubStr);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (title == null || title.isBlank()) {
            for (String line : body.split("\n")) {
                if (line.trim().startsWith("# ")) {
                    title = line.trim().substring(2).trim();
                    break;
                }
            }
        }

        ArticleUpdateRequestDto dto = new ArticleUpdateRequestDto(
                title != null ? title : slug,
                summary,
                body,
                publishedAt
        );

        return updateArticleJson(slug, dto, agent);
    }

    @Transactional
    public PodcastEpisodeDto updatePodcastJson(String slug, PodcastUpdateRequestDto dto, AgentApiToken agent) {
        if (dto == null) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }
        Optional<PodcastEpisode> episodeOpt = podcastEpisodeRepository.findBySlug(slug);
        PodcastEpisode episode;
        String action;
        if (episodeOpt.isPresent()) {
            episode = episodeOpt.get();
            action = "UPDATE";
        } else {
            episode = new PodcastEpisode();
            episode.setSlug(slug);
            action = "CREATE";
        }

        if (dto.title() != null && !dto.title().isBlank()) {
            episode.setTitle(dto.title());
        } else if (action.equals("CREATE")) {
            throw new IllegalArgumentException("Title is required for creating a podcast episode");
        }

        if (dto.thesis() != null) {
            episode.setThesis(dto.thesis());
        }
        if (dto.description() != null) {
            episode.setDescription(dto.description());
        }
        if (dto.audioUrl() != null) {
            episode.setAudioUrl(dto.audioUrl());
        }
        if (dto.mediaLink() != null) {
            episode.setMediaLink(dto.mediaLink());
        }
        if (dto.youtubeUrl() != null) {
            episode.setYoutubeUrl(dto.youtubeUrl());
        }
        if (dto.telegramUrl() != null) {
            episode.setTelegramUrl(dto.telegramUrl());
        }
        if (dto.spotifyUrl() != null) {
            episode.setSpotifyUrl(dto.spotifyUrl());
        }
        if (dto.applePodcastsUrl() != null) {
            episode.setApplePodcastsUrl(dto.applePodcastsUrl());
        }
        if (dto.platforms() != null) {
            episode.setPlatforms(dto.platforms());
        }
        if (dto.conceptLinks() != null) {
            episode.setConceptLinks(dto.conceptLinks());
        }
        if (dto.metadata() != null) {
            episode.setMetadata(dto.metadata());
        }
        if (dto.durationSeconds() != null) {
            episode.setDurationSeconds(dto.durationSeconds());
        }

        if (dto.publishedAt() != null) {
            episode.setPublishedAt(dto.publishedAt());
        } else if (episode.getPublishedAt() == null) {
            episode.setPublishedAt(OffsetDateTime.now());
        }

        if (dto.timecodes() != null) {
            episode.getTimecodes().clear();
            for (PodcastTimecodeDto tcDto : dto.timecodes()) {
                PodcastTimecode tc = new PodcastTimecode(tcDto.getTimeOffsetSeconds(), tcDto.getTitle(), tcDto.getDescription(), tcDto.getDisplayOrder());
                episode.addTimecode(tc);
            }
        }

        if (dto.quotes() != null) {
            episode.getQuotes().clear();
            for (PodcastQuoteDto qDto : dto.quotes()) {
                PodcastQuote q = new PodcastQuote(qDto.getQuoteText(), qDto.getSpeaker(), qDto.getTimeOffsetSeconds(), qDto.getConceptLinks(), qDto.getDisplayOrder());
                episode.addQuote(q);
            }
        }

        PodcastEpisode saved = podcastEpisodeRepository.save(episode);
        auditLogRepository.save(new ContentAuditLog(agent, "PODCAST", saved.getId(), action));
        return mapPodcastToDto(saved);
    }

    @Transactional
    public PodcastEpisodeDto updatePodcastMarkdown(String slug, String markdown, AgentApiToken agent) {
        if (markdown == null || markdown.isBlank()) {
            throw new IllegalArgumentException("Markdown payload cannot be empty");
        }

        Matcher matcher = FRONTMATTER_PATTERN.matcher(markdown);
        String title = null;
        String thesis = null;
        String description = null;
        String audioUrl = null;
        String mediaLink = null;
        String youtubeUrl = null;
        String telegramUrl = null;
        String spotifyUrl = null;
        String applePodcastsUrl = null;
        String platforms = null;
        String conceptLinks = null;
        Integer durationSeconds = null;
        OffsetDateTime publishedAt = null;
        String body = markdown;

        if (matcher.matches()) {
            String frontmatter = matcher.group(1);
            body = matcher.group(2).trim();

            for (String line : frontmatter.split("\n")) {
                line = line.trim();
                if (line.startsWith("title:")) {
                    title = unquote(line.substring(6).trim());
                } else if (line.startsWith("thesis:")) {
                    thesis = unquote(line.substring(7).trim());
                } else if (line.startsWith("description:")) {
                    description = unquote(line.substring(12).trim());
                } else if (line.startsWith("audioUrl:") || line.startsWith("audio_url:")) {
                    audioUrl = unquote(line.substring(line.indexOf(':') + 1).trim());
                } else if (line.startsWith("mediaLink:") || line.startsWith("media_link:")) {
                    mediaLink = unquote(line.substring(line.indexOf(':') + 1).trim());
                } else if (line.startsWith("youtubeUrl:") || line.startsWith("youtube_url:")) {
                    youtubeUrl = unquote(line.substring(line.indexOf(':') + 1).trim());
                } else if (line.startsWith("telegramUrl:") || line.startsWith("telegram_url:")) {
                    telegramUrl = unquote(line.substring(line.indexOf(':') + 1).trim());
                } else if (line.startsWith("spotifyUrl:") || line.startsWith("spotify_url:")) {
                    spotifyUrl = unquote(line.substring(line.indexOf(':') + 1).trim());
                } else if (line.startsWith("applePodcastsUrl:") || line.startsWith("apple_podcasts_url:")) {
                    applePodcastsUrl = unquote(line.substring(line.indexOf(':') + 1).trim());
                } else if (line.startsWith("platforms:")) {
                    platforms = unquote(line.substring(10).trim());
                } else if (line.startsWith("conceptLinks:") || line.startsWith("concept_links:")) {
                    conceptLinks = unquote(line.substring(line.indexOf(':') + 1).trim());
                } else if (line.startsWith("durationSeconds:") || line.startsWith("duration_seconds:")) {
                    try {
                        durationSeconds = Integer.parseInt(unquote(line.substring(line.indexOf(':') + 1).trim()));
                    } catch (Exception ignored) {
                    }
                } else if (line.startsWith("publishedAt:") || line.startsWith("published_at:")) {
                    String pubStr = unquote(line.substring(line.indexOf(':') + 1).trim());
                    try {
                        publishedAt = OffsetDateTime.parse(pubStr);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (description == null || description.isBlank()) {
            description = body;
        }

        PodcastUpdateRequestDto dto = new PodcastUpdateRequestDto(
                title != null ? title : slug,
                thesis,
                description,
                audioUrl,
                mediaLink,
                youtubeUrl,
                telegramUrl,
                spotifyUrl,
                applePodcastsUrl,
                platforms,
                conceptLinks,
                null,
                durationSeconds,
                publishedAt,
                null,
                null
        );

        return updatePodcastJson(slug, dto, agent);
    }

    @Transactional
    public EssayDto updateEssayJson(String slug, EssayUpdateRequestDto dto, AgentApiToken agent) {
        if (dto == null) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }
        Optional<PublishedEssay> essayOpt = publishedEssayRepository.findBySlug(slug);
        PublishedEssay essay;
        String action;
        if (essayOpt.isPresent()) {
            essay = essayOpt.get();
            action = "UPDATE";
        } else {
            essay = new PublishedEssay();
            essay.setSlug(slug);
            action = "CREATE";
        }

        if (dto.title() != null && !dto.title().isBlank()) {
            essay.setTitle(dto.title());
        } else if (action.equals("CREATE")) {
            throw new IllegalArgumentException("Title is required for creating an essay");
        }

        if (dto.summary() != null) {
            essay.setSummary(dto.summary());
        }

        if (dto.content() != null) {
            essay.setContent(dto.content());
        } else if (action.equals("CREATE")) {
            throw new IllegalArgumentException("Content is required for creating an essay");
        }

        if (dto.curatedBy() != null) {
            essay.setCuratedBy(dto.curatedBy());
        }

        if (dto.publishedAt() != null) {
            essay.setPublishedAt(dto.publishedAt());
        } else if (essay.getPublishedAt() == null) {
            essay.setPublishedAt(OffsetDateTime.now());
        }

        PublishedEssay saved = publishedEssayRepository.save(essay);

        Optional<Essay> draftOpt = essayRepository.findBySlug(slug);
        Essay draft = draftOpt.orElseGet(() -> {
            Essay e = new Essay();
            e.setSlug(slug);
            return e;
        });
        draft.setTitle(saved.getTitle());
        draft.setSummary(saved.getSummary());
        draft.setContent(saved.getContent());
        draft.setPublishedAt(saved.getPublishedAt());
        essayRepository.save(draft);

        auditLogRepository.save(new ContentAuditLog(agent, "ESSAY", saved.getId(), action));
        return mapEssayToDto(saved);
    }

    @Transactional
    public EssayDto updateEssayMarkdown(String slug, String markdown, AgentApiToken agent) {
        if (markdown == null || markdown.isBlank()) {
            throw new IllegalArgumentException("Markdown payload cannot be empty");
        }

        Matcher matcher = FRONTMATTER_PATTERN.matcher(markdown);
        String title = null;
        String summary = null;
        String curatedBy = null;
        OffsetDateTime publishedAt = null;
        String body = markdown;

        if (matcher.matches()) {
            String frontmatter = matcher.group(1);
            body = matcher.group(2).trim();

            for (String line : frontmatter.split("\n")) {
                line = line.trim();
                if (line.startsWith("title:")) {
                    title = unquote(line.substring(6).trim());
                } else if (line.startsWith("summary:")) {
                    summary = unquote(line.substring(8).trim());
                } else if (line.startsWith("curatedBy:") || line.startsWith("curated_by:")) {
                    curatedBy = unquote(line.substring(line.indexOf(':') + 1).trim());
                } else if (line.startsWith("publishedAt:") || line.startsWith("published_at:")) {
                    String pubStr = unquote(line.substring(line.indexOf(':') + 1).trim());
                    try {
                        publishedAt = OffsetDateTime.parse(pubStr);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (title == null || title.isBlank()) {
            for (String line : body.split("\n")) {
                if (line.trim().startsWith("# ")) {
                    title = line.trim().substring(2).trim();
                    break;
                }
            }
        }

        EssayUpdateRequestDto dto = new EssayUpdateRequestDto(
                title != null ? title : slug,
                summary,
                body,
                curatedBy,
                publishedAt
        );

        return updateEssayJson(slug, dto, agent);
    }

    @Transactional
    public CanonItemDto updateCanonJson(String slug, CanonUpdateRequestDto dto, AgentApiToken agent) {
        if (dto == null) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }
        Optional<CanonItem> canonOpt = canonItemRepository.findBySlug(slug);
        CanonItem item;
        String action;
        if (canonOpt.isPresent()) {
            item = canonOpt.get();
            action = "UPDATE";
        } else {
            item = new CanonItem();
            item.setSlug(slug);
            action = "CREATE";
        }

        if (dto.title() != null && !dto.title().isBlank()) {
            item.setTitle(dto.title());
        } else if (action.equals("CREATE")) {
            throw new IllegalArgumentException("Title is required for creating a canon concept");
        }

        if (dto.type() != null) {
            item.setType(dto.type());
        } else if (action.equals("CREATE")) {
            item.setType(CanonType.ONTOLOGY);
        }

        if (dto.domain() != null) {
            item.setDomain(dto.domain());
        } else if (action.equals("CREATE")) {
            item.setDomain(CanonDomain.PHILOSOPHY_OF_MIND);
        }

        if (dto.summary() != null) {
            item.setSummary(dto.summary());
        }

        if (dto.content() != null) {
            item.setContent(dto.content());
        } else if (action.equals("CREATE")) {
            throw new IllegalArgumentException("Content is required for creating a canon concept");
        }

        if (dto.publishedAt() != null) {
            item.setPublishedAt(dto.publishedAt());
        } else if (item.getPublishedAt() == null) {
            item.setPublishedAt(OffsetDateTime.now());
        }

        CanonItem saved = canonItemRepository.save(item);
        auditLogRepository.save(new ContentAuditLog(agent, "CANON", saved.getId(), action));
        return mapCanonToDto(saved);
    }

    @Transactional
    public CanonItemDto updateCanonMarkdown(String slug, String markdown, AgentApiToken agent) {
        if (markdown == null || markdown.isBlank()) {
            throw new IllegalArgumentException("Markdown payload cannot be empty");
        }

        Matcher matcher = FRONTMATTER_PATTERN.matcher(markdown);
        String title = null;
        CanonType type = null;
        CanonDomain domain = null;
        String summary = null;
        OffsetDateTime publishedAt = null;
        String body = markdown;

        if (matcher.matches()) {
            String frontmatter = matcher.group(1);
            body = matcher.group(2).trim();

            for (String line : frontmatter.split("\n")) {
                line = line.trim();
                if (line.startsWith("title:")) {
                    title = unquote(line.substring(6).trim());
                } else if (line.startsWith("summary:")) {
                    summary = unquote(line.substring(8).trim());
                } else if (line.startsWith("type:")) {
                    String typeStr = unquote(line.substring(5).trim());
                    try {
                        type = CanonType.valueOf(typeStr.toUpperCase());
                    } catch (Exception ignored) {
                    }
                } else if (line.startsWith("domain:")) {
                    String domainStr = unquote(line.substring(7).trim());
                    try {
                        domain = CanonDomain.valueOf(domainStr.toUpperCase());
                    } catch (Exception ignored) {
                    }
                } else if (line.startsWith("publishedAt:") || line.startsWith("published_at:")) {
                    String pubStr = unquote(line.substring(line.indexOf(':') + 1).trim());
                    try {
                        publishedAt = OffsetDateTime.parse(pubStr);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (title == null || title.isBlank()) {
            for (String line : body.split("\n")) {
                if (line.trim().startsWith("# ")) {
                    title = line.trim().substring(2).trim();
                    break;
                }
            }
        }

        CanonUpdateRequestDto dto = new CanonUpdateRequestDto(
                title != null ? title : slug,
                type != null ? type : CanonType.ONTOLOGY,
                domain != null ? domain : CanonDomain.PHILOSOPHY_OF_MIND,
                summary,
                body,
                publishedAt
        );

        return updateCanonJson(slug, dto, agent);
    }

    private ArticleDto mapArticleToDto(Article entity) {
        return new ArticleDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getContent(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
        );
    }

    private PodcastEpisodeDto mapPodcastToDto(PodcastEpisode entity) {
        List<PodcastTimecodeDto> timecodes = entity.getTimecodes() != null ?
                entity.getTimecodes().stream().map(tc -> new PodcastTimecodeDto(tc.getId(), tc.getTimeOffsetSeconds(), tc.getTitle(), tc.getDescription(), tc.getDisplayOrder())).toList() : List.of();

        List<PodcastQuoteDto> quotes = entity.getQuotes() != null ?
                entity.getQuotes().stream().map(q -> new PodcastQuoteDto(q.getId(), q.getQuoteText(), q.getSpeaker(), q.getTimeOffsetSeconds(), q.getConceptLinks(), q.getDisplayOrder())).toList() : List.of();

        return new PodcastEpisodeDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getThesis(),
                entity.getDescription(),
                entity.getAudioUrl(),
                entity.getMediaLink(),
                entity.getYoutubeUrl(),
                entity.getTelegramUrl(),
                entity.getSpotifyUrl(),
                entity.getApplePodcastsUrl(),
                entity.getPlatforms(),
                entity.getConceptLinks(),
                entity.getMetadata(),
                entity.getDurationSeconds(),
                entity.getPublishedAt(),
                entity.getCreatedAt(),
                timecodes,
                quotes
        );
    }

    private EssayDto mapEssayToDto(PublishedEssay entity) {
        return new EssayDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getContent(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
        );
    }

    private CanonItemDto mapCanonToDto(CanonItem entity) {
        return new CanonItemDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getType(),
                entity.getDomain(),
                entity.getSummary(),
                entity.getContent(),
                entity.getPublishedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                List.of(),
                List.of()
        );
    }

    private String unquote(String str) {
        if (str == null) return null;
        if ((str.startsWith("\"") && str.endsWith("\"")) || (str.startsWith("'") && str.endsWith("'"))) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
