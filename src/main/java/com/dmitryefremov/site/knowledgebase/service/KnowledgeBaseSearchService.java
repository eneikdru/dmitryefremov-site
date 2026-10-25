package com.dmitryefremov.site.knowledgebase.service;

import com.dmitryefremov.site.canon.domain.CanonCrossReference;
import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.repository.CanonCrossReferenceRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.essay.domain.PublishedEssay;
import com.dmitryefremov.site.essay.repository.PublishedEssayRepository;
import com.dmitryefremov.site.knowledgebase.domain.AppliedModelConfiguration;
import com.dmitryefremov.site.knowledgebase.domain.KnowledgeBaseConcept;
import com.dmitryefremov.site.knowledgebase.dto.*;
import com.dmitryefremov.site.knowledgebase.repository.AppliedModelConfigurationRepository;
import com.dmitryefremov.site.knowledgebase.repository.KnowledgeBaseConceptRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class KnowledgeBaseSearchService {

    private final KnowledgeBaseConceptRepository conceptRepository;
    private final AppliedModelConfigurationRepository modelConfigurationRepository;
    private final CanonItemRepository canonItemRepository;
    private final CanonCrossReferenceRepository canonCrossReferenceRepository;
    private final PodcastEpisodeRepository podcastEpisodeRepository;
    private final PublishedEssayRepository essayRepository;
    private final SparkRepository sparkRepository;
    private final Clock clock;

    public KnowledgeBaseSearchService(
            KnowledgeBaseConceptRepository conceptRepository,
            AppliedModelConfigurationRepository modelConfigurationRepository,
            CanonItemRepository canonItemRepository,
            CanonCrossReferenceRepository canonCrossReferenceRepository,
            PodcastEpisodeRepository podcastEpisodeRepository,
            PublishedEssayRepository essayRepository,
            SparkRepository sparkRepository,
            Clock clock) {
        this.conceptRepository = conceptRepository;
        this.modelConfigurationRepository = modelConfigurationRepository;
        this.canonItemRepository = canonItemRepository;
        this.canonCrossReferenceRepository = canonCrossReferenceRepository;
        this.podcastEpisodeRepository = podcastEpisodeRepository;
        this.essayRepository = essayRepository;
        this.sparkRepository = sparkRepository;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public SearchResponse search(String query, List<SearchDomain> domains, int limit, int offset) {
        if (query == null || query.trim().isEmpty()) {
            return new SearchResponse("", 0, Collections.emptyList());
        }

        String searchTerm = query.trim().toLowerCase();
        Set<SearchDomain> targetDomains = (domains == null || domains.isEmpty())
                ? EnumSet.allOf(SearchDomain.class)
                : new HashSet<>(domains);

        List<SearchResultItem> allResults = new ArrayList<>();

        // 1. Search CANON items (and knowledge base concepts)
        if (targetDomains.contains(SearchDomain.CANON)) {
            List<CanonItem> canonItems = canonItemRepository.findAll();
            for (CanonItem item : canonItems) {
                double score = calculateRelevance(searchTerm, item.getTitle(), item.getSummary(), item.getContent());
                if (score > 0) {
                    int connCount = item.getOutgoingReferences().size() + item.getIncomingReferences().size();
                    allResults.add(new SearchResultItem(
                            item.getId(),
                            item.getSlug(),
                            item.getTitle(),
                            SearchDomain.CANON,
                            item.getSummary(),
                            createSnippet(item.getContent(), searchTerm),
                            "/canon/" + item.getSlug(),
                            score,
                            item.getPublishedAt(),
                            connCount
                    ));
                }
            }

            // Knowledge base concepts as CANON search results if not already present
            List<KnowledgeBaseConcept> kbConcepts = conceptRepository.searchByQuery(searchTerm);
            for (KnowledgeBaseConcept kb : kbConcepts) {
                if (allResults.stream().noneMatch(r -> r.getSlug().equals(kb.getSlug()))) {
                    double score = calculateRelevance(searchTerm, kb.getTitle(), kb.getDescription(), kb.getContent());
                    allResults.add(new SearchResultItem(
                            kb.getId(),
                            kb.getSlug(),
                            kb.getTitle(),
                            SearchDomain.CANON,
                            kb.getDescription(),
                            createSnippet(kb.getContent(), searchTerm),
                            "/concept/" + kb.getSlug(),
                            score > 0 ? score : 0.8,
                            kb.getCreatedAt(),
                            0
                    ));
                }
            }
        }

        // 2. Search PODCAST episodes
        if (targetDomains.contains(SearchDomain.PODCAST)) {
            List<PodcastEpisode> episodes = podcastEpisodeRepository.findAll();
            for (PodcastEpisode ep : episodes) {
                double score = calculateRelevance(searchTerm, ep.getTitle(), ep.getThesis(), ep.getDescription());
                if (score > 0) {
                    allResults.add(new SearchResultItem(
                            ep.getId(),
                            ep.getSlug(),
                            ep.getTitle(),
                            SearchDomain.PODCAST,
                            ep.getThesis(),
                            createSnippet(ep.getDescription(), searchTerm),
                            "/podcast/" + ep.getSlug(),
                            score,
                            ep.getPublishedAt(),
                            ep.getConceptLinks() != null && !ep.getConceptLinks().isEmpty() ? ep.getConceptLinks().split(",").length : 0
                    ));
                }
            }
        }

        // 3. Search ESSAY items
        if (targetDomains.contains(SearchDomain.ESSAY)) {
            List<PublishedEssay> essays = essayRepository.findAll();
            for (PublishedEssay essay : essays) {
                double score = calculateRelevance(searchTerm, essay.getTitle(), essay.getSummary(), essay.getContent());
                if (score > 0) {
                    allResults.add(new SearchResultItem(
                            essay.getId(),
                            essay.getSlug(),
                            essay.getTitle(),
                            SearchDomain.ESSAY,
                            essay.getSummary(),
                            createSnippet(essay.getContent(), searchTerm),
                            "/essays/" + essay.getSlug(),
                            score,
                            essay.getPublishedAt(),
                            0
                    ));
                }
            }
        }

        // 4. Search SPARK items
        if (targetDomains.contains(SearchDomain.SPARK)) {
            List<Spark> sparks = sparkRepository.findAll();
            for (Spark spark : sparks) {
                double score = calculateRelevance(searchTerm, spark.getTags(), spark.getDomain(), spark.getContent());
                if (score > 0) {
                    String slug = "spark-" + (spark.getTelegramMessageId() != null ? spark.getTelegramMessageId() : spark.getId());
                    allResults.add(new SearchResultItem(
                            spark.getId(),
                            slug,
                            spark.getContent() != null && spark.getContent().length() > 50 ? spark.getContent().substring(0, 50) + "..." : spark.getContent(),
                            SearchDomain.SPARK,
                            spark.getTags(),
                            createSnippet(spark.getContent(), searchTerm),
                            "/sparks/" + spark.getId(),
                            score,
                            spark.getPublishedAt(),
                            0
                    ));
                }
            }
        }

        // Sort by relevanceScore DESC, then publishedAt DESC
        allResults.sort((a, b) -> {
            int comp = Double.compare(b.getRelevanceScore(), a.getRelevanceScore());
            if (comp != 0) return comp;
            if (a.getPublishedAt() == null) return 1;
            if (b.getPublishedAt() == null) return -1;
            return b.getPublishedAt().compareTo(a.getPublishedAt());
        });

        int totalResults = allResults.size();
        int fromIndex = Math.min(offset, totalResults);
        int toIndex = Math.min(fromIndex + limit, totalResults);

        List<SearchResultItem> pagedItems = allResults.subList(fromIndex, toIndex);
        return new SearchResponse(query, totalResults, pagedItems);
    }

    public GraphNodePage listGraphNodes(SearchDomain domain, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        List<GraphNode> nodes = new ArrayList<>();
        long totalElements = 0;

        if (domain == null) {
            Page<CanonItem> canonPage = canonItemRepository.findAll(pageable);
            for (CanonItem item : canonPage.getContent()) {
                nodes.add(new GraphNode(
                        item.getId(),
                        item.getSlug(),
                        item.getTitle(),
                        SearchDomain.CANON,
                        item.getSummary(),
                        "/canon/" + item.getSlug(),
                        item.getPublishedAt()
                ));
            }
            totalElements = canonPage.getTotalElements();
        } else if (domain == SearchDomain.CANON) {
            Page<CanonItem> canonPage = canonItemRepository.findAll(pageable);
            for (CanonItem item : canonPage.getContent()) {
                nodes.add(new GraphNode(
                        item.getId(),
                        item.getSlug(),
                        item.getTitle(),
                        SearchDomain.CANON,
                        item.getSummary(),
                        "/canon/" + item.getSlug(),
                        item.getPublishedAt()
                ));
            }
            totalElements = canonPage.getTotalElements();
        } else if (domain == SearchDomain.PODCAST) {
            Page<PodcastEpisode> podPage = podcastEpisodeRepository.findAll(pageable);
            for (PodcastEpisode ep : podPage.getContent()) {
                nodes.add(new GraphNode(
                        ep.getId(),
                        ep.getSlug(),
                        ep.getTitle(),
                        SearchDomain.PODCAST,
                        ep.getThesis(),
                        "/podcast/" + ep.getSlug(),
                        ep.getPublishedAt()
                ));
            }
            totalElements = podPage.getTotalElements();
        } else if (domain == SearchDomain.ESSAY) {
            Page<PublishedEssay> essayPage = essayRepository.findAll(pageable);
            for (PublishedEssay essay : essayPage.getContent()) {
                nodes.add(new GraphNode(
                        essay.getId(),
                        essay.getSlug(),
                        essay.getTitle(),
                        SearchDomain.ESSAY,
                        essay.getSummary(),
                        "/essays/" + essay.getSlug(),
                        essay.getPublishedAt()
                ));
            }
            totalElements = essayPage.getTotalElements();
        } else if (domain == SearchDomain.SPARK) {
            Page<Spark> sparkPage = sparkRepository.findAllByOrderByPublishedAtDesc(pageable);
            for (Spark spark : sparkPage.getContent()) {
                String slug = "spark-" + (spark.getTelegramMessageId() != null ? spark.getTelegramMessageId() : spark.getId());
                nodes.add(new GraphNode(
                        spark.getId(),
                        slug,
                        spark.getContent() != null && spark.getContent().length() > 50 ? spark.getContent().substring(0, 50) + "..." : spark.getContent(),
                        SearchDomain.SPARK,
                        spark.getTags(),
                        "/sparks/" + spark.getId(),
                        spark.getPublishedAt()
                ));
            }
            totalElements = sparkPage.getTotalElements();
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new GraphNodePage(nodes, page, size, totalElements, totalPages);
    }

    public Optional<GraphNodeDetail> getGraphNodeBySlug(String slug) {
        // First check CanonItem
        Optional<CanonItem> canonOpt = canonItemRepository.findBySlug(slug);
        if (canonOpt.isPresent()) {
            CanonItem item = canonOpt.get();
            GraphNode node = new GraphNode(
                    item.getId(),
                    item.getSlug(),
                    item.getTitle(),
                    SearchDomain.CANON,
                    item.getSummary(),
                    "/canon/" + item.getSlug(),
                    item.getPublishedAt()
            );

            List<GraphLink> outgoing = item.getOutgoingReferences().stream()
                    .map(ref -> new GraphLink(
                            ref.getId(),
                            item.getSlug(),
                            item.getTitle(),
                            ref.getTarget().getSlug(),
                            ref.getTarget().getTitle(),
                            ref.getRelationshipType().name(),
                            ref.getDescription()
                    ))
                    .collect(Collectors.toList());

            List<GraphLink> incoming = item.getIncomingReferences().stream()
                    .map(ref -> new GraphLink(
                            ref.getId(),
                            ref.getSource().getSlug(),
                            ref.getSource().getTitle(),
                            item.getSlug(),
                            item.getTitle(),
                            ref.getRelationshipType().name(),
                            ref.getDescription()
                    ))
                    .collect(Collectors.toList());

            Map<String, String> urls = new HashMap<>();
            urls.put("llmsTxt", "/llms.txt");
            urls.put("aiAgentJson", "/ai-agent.json");
            urls.put("markdownUrl", "/api/v1/canon/" + item.getSlug() + ".md");

            return Optional.of(new GraphNodeDetail(node, item.getContent(), outgoing, incoming, urls));
        }

        // Check KnowledgeBaseConcept
        Optional<KnowledgeBaseConcept> kbOpt = conceptRepository.findBySlug(slug);
        if (kbOpt.isPresent()) {
            KnowledgeBaseConcept kb = kbOpt.get();
            GraphNode node = new GraphNode(
                    kb.getId(),
                    kb.getSlug(),
                    kb.getTitle(),
                    SearchDomain.CANON,
                    kb.getDescription(),
                    "/concept/" + kb.getSlug(),
                    kb.getCreatedAt()
            );

            // Format model configuration content if present
            StringBuilder fullContent = new StringBuilder();
            if (kb.getContent() != null) {
                fullContent.append(kb.getContent()).append("\n\n");
            }
            List<AppliedModelConfiguration> configs = modelConfigurationRepository.findByConceptIdAndActiveTrue(kb.getId());
            if (!configs.isEmpty()) {
                fullContent.append("### Applied Model Configurations\n");
                for (AppliedModelConfiguration config : configs) {
                    fullContent.append("- **").append(config.getModelName()).append("** (v").append(config.getVersion()).append("):\n");
                    fullContent.append("```json\n").append(config.getConfigurationJson()).append("\n```\n");
                }
            }

            Map<String, String> urls = new HashMap<>();
            urls.put("llmsTxt", "/llms.txt");
            urls.put("aiAgentJson", "/ai-agent.json");

            return Optional.of(new GraphNodeDetail(node, fullContent.toString().trim(), Collections.emptyList(), Collections.emptyList(), urls));
        }

        // Check PodcastEpisode
        Optional<PodcastEpisode> podOpt = podcastEpisodeRepository.findBySlug(slug);
        if (podOpt.isPresent()) {
            PodcastEpisode ep = podOpt.get();
            GraphNode node = new GraphNode(
                    ep.getId(),
                    ep.getSlug(),
                    ep.getTitle(),
                    SearchDomain.PODCAST,
                    ep.getThesis(),
                    "/podcast/" + ep.getSlug(),
                    ep.getPublishedAt()
            );
            Map<String, String> urls = new HashMap<>();
            urls.put("llmsTxt", "/llms.txt");
            urls.put("aiAgentJson", "/ai-agent.json");

            return Optional.of(new GraphNodeDetail(node, ep.getDescription(), Collections.emptyList(), Collections.emptyList(), urls));
        }

        // Check PublishedEssay
        Optional<PublishedEssay> essayOpt = essayRepository.findBySlug(slug);
        if (essayOpt.isPresent()) {
            PublishedEssay essay = essayOpt.get();
            GraphNode node = new GraphNode(
                    essay.getId(),
                    essay.getSlug(),
                    essay.getTitle(),
                    SearchDomain.ESSAY,
                    essay.getSummary(),
                    "/essays/" + essay.getSlug(),
                    essay.getPublishedAt()
            );
            Map<String, String> urls = new HashMap<>();
            urls.put("llmsTxt", "/llms.txt");
            urls.put("aiAgentJson", "/ai-agent.json");

            return Optional.of(new GraphNodeDetail(node, essay.getContent(), Collections.emptyList(), Collections.emptyList(), urls));
        }

        return Optional.empty();
    }

    public GraphLinkPage listGraphLinks(String sourceSlug, String targetSlug, String relationshipType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CanonCrossReference> crossRefPage = canonCrossReferenceRepository.searchLinks(
                sourceSlug != null && !sourceSlug.trim().isEmpty() ? sourceSlug.trim() : null,
                targetSlug != null && !targetSlug.trim().isEmpty() ? targetSlug.trim() : null,
                relationshipType != null && !relationshipType.trim().isEmpty() ? relationshipType.trim() : null,
                pageable
        );

        List<GraphLink> links = crossRefPage.getContent().stream()
                .map(ref -> new GraphLink(
                        ref.getId(),
                        ref.getSource().getSlug(),
                        ref.getSource().getTitle(),
                        ref.getTarget().getSlug(),
                        ref.getTarget().getTitle(),
                        ref.getRelationshipType().name(),
                        ref.getDescription()
                ))
                .collect(Collectors.toList());

        return new GraphLinkPage(links, page, size, crossRefPage.getTotalElements(), crossRefPage.getTotalPages());
    }

    public String generateLlmsTxt() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Dmitry Efremov - Knowledge Base & Concept Graph Index\n\n");
        sb.append("> Machine-readable index of Canon concepts, essays, podcasts, and applied models.\n\n");

        sb.append("## Canon Concepts\n");
        List<CanonItem> canonItems = canonItemRepository.findAll();
        for (CanonItem item : canonItems) {
            sb.append("- [").append(item.getTitle()).append("](/canon/").append(item.getSlug()).append("): ")
              .append(item.getSummary() != null ? item.getSummary() : "").append("\n");
        }

        List<KnowledgeBaseConcept> kbConcepts = conceptRepository.findAll();
        for (KnowledgeBaseConcept kb : kbConcepts) {
            sb.append("- [").append(kb.getTitle()).append("](/concept/").append(kb.getSlug()).append("): ")
              .append(kb.getDescription() != null ? kb.getDescription() : "").append("\n");
        }

        sb.append("\n## Essays & Articles\n");
        List<PublishedEssay> essays = essayRepository.findAll();
        for (PublishedEssay essay : essays) {
            sb.append("- [").append(essay.getTitle()).append("](/essays/").append(essay.getSlug()).append("): ")
              .append(essay.getSummary() != null ? essay.getSummary() : "").append("\n");
        }

        sb.append("\n## Podcasts\n");
        List<PodcastEpisode> episodes = podcastEpisodeRepository.findAll();
        for (PodcastEpisode ep : episodes) {
            sb.append("- [").append(ep.getTitle()).append("](/podcast/").append(ep.getSlug()).append("): ")
              .append(ep.getThesis() != null ? ep.getThesis() : "").append("\n");
        }

        return sb.toString();
    }

    public AiAgentGraphRepresentation generateAiAgentGraph() {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphLink> edges = new ArrayList<>();

        List<CanonItem> canonItems = canonItemRepository.findAll();
        for (CanonItem item : canonItems) {
            nodes.add(new GraphNode(
                    item.getId(),
                    item.getSlug(),
                    item.getTitle(),
                    SearchDomain.CANON,
                    item.getSummary(),
                    "/canon/" + item.getSlug(),
                    item.getPublishedAt()
            ));
            for (CanonCrossReference ref : item.getOutgoingReferences()) {
                edges.add(new GraphLink(
                        ref.getId(),
                        item.getSlug(),
                        item.getTitle(),
                        ref.getTarget().getSlug(),
                        ref.getTarget().getTitle(),
                        ref.getRelationshipType().name(),
                        ref.getDescription()
                ));
            }
        }

        List<KnowledgeBaseConcept> kbConcepts = conceptRepository.findAll();
        for (KnowledgeBaseConcept kb : kbConcepts) {
            if (nodes.stream().noneMatch(n -> n.getSlug().equals(kb.getSlug()))) {
                nodes.add(new GraphNode(
                        kb.getId(),
                        kb.getSlug(),
                        kb.getTitle(),
                        SearchDomain.CANON,
                        kb.getDescription(),
                        "/concept/" + kb.getSlug(),
                        kb.getCreatedAt()
                ));
            }
        }

        return new AiAgentGraphRepresentation(
                "1.0.0",
                OffsetDateTime.now(clock),
                nodes.size(),
                edges.size(),
                nodes,
                edges
        );
    }

    private double calculateRelevance(String searchTerm, String... textFields) {
        if (searchTerm == null || searchTerm.isEmpty()) return 0.0;
        double score = 0.0;
        for (int i = 0; i < textFields.length; i++) {
            String text = textFields[i];
            if (text != null && !text.isEmpty()) {
                String lowerText = text.toLowerCase();
                if (lowerText.contains(searchTerm)) {
                    // Title gets higher score
                    score += (i == 0) ? 1.0 : 0.5;
                }
            }
        }
        return score;
    }

    private String createSnippet(String content, String searchTerm) {
        if (content == null || content.isEmpty()) return null;
        int idx = content.toLowerCase().indexOf(searchTerm);
        if (idx == -1) {
            return content.length() > 100 ? content.substring(0, 100) + "..." : content;
        }
        int start = Math.max(0, idx - 30);
        int end = Math.min(content.length(), idx + searchTerm.length() + 70);
        String snippet = content.substring(start, end);
        if (start > 0) snippet = "..." + snippet;
        if (end < content.length()) snippet = snippet + "...";
        return snippet;
    }
}
