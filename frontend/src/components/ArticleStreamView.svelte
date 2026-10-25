<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { parseMarkdown } from '../lib/markdown';
  import { telemetry, calculateScrollPercentage, type TelemetryEvent } from '../lib/telemetry';
  import { fetchEssaysFromApi, type EssayArticleItem } from '../lib/essays';
  import { fetchSparksFromApi } from '../lib/sparks';
  import ResponsivePicture from './ResponsivePicture.svelte';
  import SeoMeta from './SeoMeta.svelte';

  export interface Article {
    id: string;
    title: string;
    topic: 'deontic' | 'toc' | 'autonomous' | 'systems';
    readTime: string;
    publishedAt: string;
    summary: string;
    contentMarkdown: string;
  }

  export interface SparkItem {
    id: string | number;
    telegramMessageId?: number;
    content: string;
    domain?: string;
    tags?: string;
    author?: string;
    publishedAt: string;
  }

  export let articles: Article[] = [];
  export let sparks: SparkItem[] = [];
  export let selectedArticleId: string | undefined = undefined;
  export let filterTopic: 'all' | 'deontic' | 'toc' | 'autonomous' | 'systems' = 'all';

  let scrollContainer: HTMLElement;
  let trackedThresholds: Set<number> = new Set();
  let currentPercentage = 0;
  let loggedEvents: TelemetryEvent[] = [];
  let sparksPollingInterval: ReturnType<typeof setInterval> | null = null;

  $: selectedArticle = articles.find(a => a.id === selectedArticleId) || articles[0] || null;
  $: parsedContent = selectedArticle ? parseMarkdown(selectedArticle.contentMarkdown) : { html: '' };
  $: filteredArticles = filterTopic === 'all'
    ? articles
    : articles.filter(a => a.topic === filterTopic);

  function getArticle(id: string): Article | null {
    return articles.find(a => a.id === id) || articles[0] || null;
  }

  function handleArticleSelect(id: string) {
    selectedArticleId = id;
    trackedThresholds = new Set();
    currentPercentage = 0;
    if (scrollContainer) {
      scrollContainer.scrollTop = 0;
    }
    const article = getArticle(id);
    const evt = telemetry.trackEssayView(article.id, article.title);
    if (evt) {
      loggedEvents = [...loggedEvents, evt];
    }
  }

  function handleCrossLinkClick(targetNodeId: string, targetNodeType: string, label: string) {
    const sourceNodeId = selectedArticleId || 'article-node';
    const evt = telemetry.trackCrossLinkClick(sourceNodeId, targetNodeId, targetNodeType, label);
    if (evt) {
      loggedEvents = [...loggedEvents, evt];
    }
  }

  function handleScroll() {
    if (!scrollContainer) return;
    const { scrollTop, scrollHeight, clientHeight } = scrollContainer;
    currentPercentage = calculateScrollPercentage(scrollTop, scrollHeight, clientHeight);

    const thresholds = [25, 50, 75, 100];
    for (const threshold of thresholds) {
      if (currentPercentage >= threshold && !trackedThresholds.has(threshold)) {
        trackedThresholds.add(threshold);
        const event = telemetry.trackScrollDepth(selectedArticleId, threshold);
        if (event) {
          loggedEvents = [...loggedEvents, event];
        }
      }
    }
  }

  const DEFAULT_FALLBACK_ARTICLES: Article[] = [
    {
      id: 'deontic-process-autonomy',
      title: 'Деонтическая логика в автономии',
      topic: 'deontic',
      readTime: '6 min read',
      publishedAt: '2026-03-12',
      summary: 'Формализация нормативных ограничений и модальных операторов должествования.',
      contentMarkdown: '# Деонтическая логика\n\nМодальные операторы O(p) и P(p) в автономных системах.'
    }
  ];

  onMount(async () => {
    if (articles.length === 0) {
      const fetchedEssays = await fetchEssaysFromApi();
      if (fetchedEssays && fetchedEssays.length > 0) {
        articles = fetchedEssays;
      } else {
        articles = DEFAULT_FALLBACK_ARTICLES;
      }
    }
    if (sparks.length === 0) {
      const fetchedSparks = await fetchSparksFromApi();
      if (fetchedSparks && fetchedSparks.length > 0) {
        sparks = fetchedSparks.map(s => ({
          id: s.id,
          telegramMessageId: s.telegramMessageId,
          content: s.body || '',
          domain: s.domain || 'Telegram',
          tags: s.tag,
          author: s.author,
          publishedAt: s.publishedAt
        }));
      }
    }
    if (articles.length > 0 && !selectedArticleId) {
      selectedArticleId = articles[0].id;
    }
    if (selectedArticleId) {
      const article = getArticle(selectedArticleId);
      if (article) {
        const evt = telemetry.trackEssayView(article.id, article.title);
        if (evt) {
          loggedEvents = [...loggedEvents, evt];
        }
      }
    }
    if (scrollContainer) {
      handleScroll();
    }
    const pollSparks = async () => {
      const fetchedSparks = await fetchSparksFromApi();
      if (fetchedSparks && fetchedSparks.length > 0) {
        sparks = fetchedSparks.map(s => ({
          id: s.id,
          telegramMessageId: s.telegramMessageId,
          content: s.body || '',
          domain: s.domain || 'Telegram',
          tags: s.tag,
          author: s.author,
          publishedAt: s.publishedAt
        }));
      }
      sparksPollingInterval = setTimeout(pollSparks, 5000) as unknown as ReturnType<typeof setInterval>;
    };
    sparksPollingInterval = setTimeout(pollSparks, 5000) as unknown as ReturnType<typeof setInterval>;
  });

  onDestroy(() => {
    if (sparksPollingInterval) clearTimeout(sparksPollingInterval as unknown as ReturnType<typeof setTimeout>);
  });
</script>

{#if selectedArticle}
  <SeoMeta
    title="{selectedArticle.title} | Dmitry Efremov"
    description={selectedArticle.summary}
    type="article"
    url="https://dmitryefremov.com/article/{selectedArticle.id}"
    publishedAt={selectedArticle.publishedAt}
    keywords={[selectedArticle.topic, 'Autonomous Systems', 'Deontic Logic', 'AI Crawlers']}
  />
{/if}

<div id="article-stream-container" class="space-y-6">
  <!-- Section Header & Filter Options -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1f2433] pb-4">
    <div>
      <div class="flex items-center gap-2 mb-1">
        <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
          [THOUGHT_STREAM]
        </span>
        <span class="text-xs font-mono text-[#94a3b8]">Articles: {articles.length} | Telegram Sparks: {sparks.length}</span>
      </div>
      <h2 class="text-xl font-bold tracking-tight text-white">Articles & Thought Stream</h2>
    </div>

    <!-- Topic Filters -->
    <div class="flex flex-wrap gap-1.5 font-mono text-xs" role="toolbar" aria-label="Article Topic Filters">
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'all' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'all')}
      >
        All
      </button>
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'deontic' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'deontic')}
      >
        Deontic Logic
      </button>
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'toc' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'toc')}
      >
        Theory of Constraints
      </button>
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'autonomous' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'autonomous')}
      >
        Autonomous Systems
      </button>
    </div>
  </div>

  <!-- Main Grid: Available Longreads List & Telegram Sparks Stream (Left) & Active Article Reader (Right) -->
  <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
    <!-- Available Longreads List & Telegram Sparks Feed -->
    <div id="longreads-list-section" class="md:col-span-5 space-y-6">
      <div class="space-y-3">
        <div class="text-xs font-mono text-[#94a3b8] flex justify-between items-center mb-2">
          <span>AVAILABLE_LONGREADS ({filteredArticles.length})</span>
          <span>SELECT_TO_READ</span>
        </div>

        {#if filteredArticles.length === 0}
          <div id="articles-empty-state" class="p-8 text-center rounded-xl border border-dashed border-[#1f2433] bg-[#090a0f] font-mono text-sm text-[#94a3b8]">
            Эссе и публикации отсутствуют
          </div>
        {:else}
          <div class="space-y-3" role="list" aria-label="Available Longreads">
            {#each filteredArticles as article}
              <button
                type="button"
                class="article-card w-full p-4 rounded-lg border transition-all cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedArticleId === article.id ? 'bg-[#12151e] border-[#38bdf8] shadow-md' : 'bg-[#090a0f] border-[#1f2433] hover:border-[#34d399]/50 hover:bg-[#12151e]/50'}"
                on:click={() => handleArticleSelect(article.id)}
              >
                <div class="flex justify-between items-center text-xs font-mono mb-2">
                  <span class="px-1.5 py-0.5 rounded bg-[#1f2433] text-[#38bdf8] uppercase">
                    {article.topic}
                  </span>
                  <span class="text-[#64748b]">{article.readTime}</span>
                </div>
                <h3 class="font-bold text-white text-base mb-1 hover:text-[#38bdf8] transition-colors">
                  {article.title}
                </h3>
                <p class="text-xs text-[#94a3b8] line-clamp-2 leading-relaxed">
                  {article.summary}
                </p>
                <div class="mt-3 text-[11px] font-mono text-[#64748b] flex justify-between items-center">
                  <span>{article.publishedAt}</span>
                  <span class="text-[#38bdf8]">{selectedArticleId === article.id ? '● READING' : 'READ →'}</span>
                </div>
              </button>
            {/each}
          </div>
        {/if}
      </div>

      <!-- Telegram Sparks Stream (Tier 3 Quantized Thoughts) -->
      <div id="sparks-stream-section" class="pt-4 border-t border-[#1f2433] space-y-3">
        <div class="text-xs font-mono text-[#94a3b8] flex justify-between items-center mb-2">
          <span class="text-[#38bdf8] font-bold">[TELEGRAM_SPARKS_STREAM]</span>
          <span>QUANTIZED_THOUGHTS ({sparks.length})</span>
        </div>

        {#if sparks.length === 0}
          <div id="sparks-stream-empty-state" class="p-4 text-center rounded-lg border border-dashed border-[#1f2433] font-mono text-xs text-[#94a3b8]">
            Записи Telegram отсутствуют
          </div>
        {:else}
          <div class="space-y-3" role="list" aria-label="Telegram Sparks Stream">
            {#each sparks as spark}
              <div class="spark-card p-3 rounded-lg border border-[#1f2433] bg-[#12151e]/60 font-mono text-xs space-y-2">
                <div class="flex justify-between items-center text-[11px] text-[#94a3b8]">
                  <span class="px-1.5 py-0.5 rounded bg-[#1f2433] text-[#34d399]">
                    {spark.telegramMessageId ? `TG_MSG_#${spark.telegramMessageId}` : 'TG_MSG'}
                  </span>
                  <span class="text-[#64748b]">{spark.domain || 'General'}</span>
                </div>
                <p class="text-[#e2e8f0] font-sans leading-relaxed text-xs">
                  {spark.content}
                </p>
                <div class="flex justify-between items-center text-[10px] text-[#64748b]">
                  <span>{spark.author || '@efremov_mvp'}</span>
                  <span>{spark.publishedAt}</span>
                </div>
              </div>
            {/each}
          </div>
        {/if}
      </div>
    </div>

    <!-- Active Article Reader View -->
    <div id="article-reader-section" class="md:col-span-7 flex flex-col">
      <div id="article-reader-root" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] h-full flex flex-col justify-between font-sans">
        {#if !selectedArticle}
          <div id="article-reader-empty" class="p-8 text-center rounded-lg border border-dashed border-[#1f2433] font-mono text-sm text-[#94a3b8] my-auto">
            Выберите эссе из списка для чтения
          </div>
        {:else}
          <!-- Header -->
          <header id="article-header" class="border-b border-[#1f2433] pb-4 mb-4 space-y-3">
            <div class="flex items-center justify-between gap-2">
              <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
                [SYS_ARTICLE]
              </span>
              <span class="text-xs font-mono text-[#94a3b8]">{selectedArticle.id}</span>
            </div>
            <div id="article-cover-container" class="w-full h-32 sm:h-40 rounded-lg overflow-hidden border border-[#38bdf8]/30">
              <ResponsivePicture
                id="article-cover-img"
                assetKey="article_deontic_studio"
                alt="Иллюстрация к материалу"
                imgClass="w-full h-full object-cover"
                loading="eager"
              />
            </div>
            <h1 class="text-2xl font-bold tracking-tight text-white">{selectedArticle.title}</h1>
            <div class="flex items-center justify-between text-xs text-[#94a3b8] font-mono">
              <span>Author: Dmitry Efremov</span>
              <span>Scroll Depth Logged: {trackedThresholds.size}/4</span>
            </div>

            <!-- Canon Cross-Links for Concept Navigation -->
            <div id="article-cross-links" class="pt-3 border-t border-[#1f2433] space-y-2">
              <span class="text-[10px] font-mono uppercase tracking-wider text-[#38bdf8] block">
                Связанные узлы Канона (Cross-Links)
              </span>
              <div class="flex flex-wrap gap-2 text-xs font-mono">
                <button
                  id={`crosslink-deontic-${selectedArticle.id}`}
                  type="button"
                  on:click={() => handleCrossLinkClick('concept-deontic-logic', 'concept', 'Деонтическая логика')}
                  aria-label="Перейти к узлу Деонтическая логика"
                  class="px-2.5 py-1 rounded bg-[#12151e] text-[#38bdf8] border border-[#1f2433] hover:border-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-colors cursor-pointer flex items-center gap-1"
                >
                  <span>🔗</span>
                  <span>Деонтическая логика</span>
                </button>
                <button
                  id={`crosslink-toc-${selectedArticle.id}`}
                  type="button"
                  on:click={() => handleCrossLinkClick('concept-theory-of-constraints', 'concept', 'Теория ограничений')}
                  aria-label="Перейти к узлу Теория ограничений"
                  class="px-2.5 py-1 rounded bg-[#12151e] text-[#34d399] border border-[#1f2433] hover:border-[#34d399] focus:outline-none focus:ring-2 focus:ring-[#34d399] transition-colors cursor-pointer flex items-center gap-1"
                >
                  <span>🔗</span>
                  <span>Теория ограничений (TOC)</span>
                </button>
                <button
                  id={`crosslink-podcast-${selectedArticle.id}`}
                  type="button"
                  on:click={() => handleCrossLinkClick('podcast-ep-01', 'podcast', 'Выпуск 01: Системы и реальность')}
                  aria-label="Перейти к подкасту Выпуск 01"
                  class="px-2.5 py-1 rounded bg-[#12151e] text-[#a7f3d0] border border-[#1f2433] hover:border-[#a7f3d0] focus:outline-none focus:ring-2 focus:ring-[#a7f3d0] transition-colors cursor-pointer flex items-center gap-1"
                >
                  <span>🎙️</span>
                  <span>Выпуск 01</span>
                </button>
              </div>
            </div>
          </header>

          <!-- Progress Indicator -->
          <div class="mb-4">
            <div class="flex justify-between text-xs font-mono text-[#94a3b8] mb-1">
              <span>READ_PROGRESS</span>
              <span>{currentPercentage}%</span>
            </div>
            <div class="w-full bg-[#12151e] h-2 rounded overflow-hidden border border-[#1f2433]">
              <div
                class="bg-[#38bdf8] h-full transition-all duration-150"
                style="width: {currentPercentage}%"
              ></div>
            </div>
          </div>

          <!-- Rendered Markdown Article Content -->
          <div
            id="article-scroll-container"
            bind:this={scrollContainer}
            on:scroll={handleScroll}
            class="max-h-[420px] overflow-y-auto pr-3 border border-[#1f2433] bg-[#12151e] p-5 rounded text-sm text-[#cbd5e1] leading-relaxed font-serif space-y-3"
            role="region"
            aria-label="Article content scroll area"
          >
            {@html parsedContent.html}
          </div>

          <!-- Telemetry Event Log -->
          <footer id="article-telemetry-log" class="mt-6 pt-4 border-t border-[#1f2433]">
            <div class="text-xs font-mono text-[#94a3b8] mb-2 flex items-center justify-between">
              <span>[TELEMETRY_EVENT_FEED]</span>
              <span class="text-[#34d399]">ZERO_COOKIE_ACTIVE</span>
            </div>
            <div class="bg-[#12151e] p-3 rounded border border-[#1f2433] font-mono text-xs max-h-28 overflow-y-auto space-y-1 text-[#a7f3d0]">
              {#if loggedEvents.length === 0}
                <span class="text-[#64748b]">Scroll the article container above to trigger scroll depth telemetry events...</span>
              {:else}
                {#each loggedEvents as evt}
                  <div class="flex justify-between border-b border-[#1f2433] pb-1">
                    {#if evt.type === 'essay_view'}
                      <span>[VIEW] {evt.payload.articleId}</span>
                    {:else if evt.type === 'cross_link_click'}
                      <span class="text-[#38bdf8]">[CROSS_LINK] {evt.payload.source_node_id} → {evt.payload.target_node_id}</span>
                    {:else}
                      <span>[DEPTH_{evt.payload.depth}%] {evt.payload.articleId}</span>
                    {/if}
                    <span class="text-[#64748b]">{evt.timestamp.slice(11, 19)}</span>
                  </div>
                {/each}
              {/if}
            </div>
          </footer>
        {/if}
      </div>
    </div>
  </div>
</div>
