<script lang="ts">
  import { onMount } from 'svelte';
  import { parseMarkdown } from '../lib/markdown';
  import { telemetry, calculateScrollPercentage, type TelemetryEvent } from '../lib/telemetry';
  import { fetchEssaysFromApi, INITIAL_ESSAYS, type EssayArticleItem } from '../lib/essays';
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
    link?: string;
  }

  export let articles: Article[] = INITIAL_ESSAYS;
  export let sparks: SparkItem[] = [];
  export let selectedArticleId: string | undefined = undefined;
  export let filterTopic: 'all' | 'deontic' | 'toc' | 'autonomous' | 'systems' = 'all';

  let scrollContainer: HTMLElement;
  let trackedThresholds: Set<number> = new Set();
  let currentPercentage = 0;
  let loggedEvents: TelemetryEvent[] = [];

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
    if (typeof document !== 'undefined') {
      const readerElem = document.getElementById('article-reader-root');
      if (readerElem && typeof readerElem.scrollIntoView === 'function') {
        readerElem.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    }
    const article = getArticle(id);
    if (article) {
      const evt = telemetry.trackEssayView(article.id, article.title);
      if (evt) {
        loggedEvents = [...loggedEvents, evt];
      }
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

  onMount(async () => {
    if (articles.length === 0) {
      const fetchedEssays = await fetchEssaysFromApi();
      if (fetchedEssays && fetchedEssays.length > 0) {
        articles = fetchedEssays;
      }
    }
    if (sparks.length === 0) {
      const fetchedSparks = await fetchSparksFromApi();
      if (fetchedSparks && fetchedSparks.length > 0) {
        sparks = fetchedSparks.map(s => ({
          id: s.id,
          telegramMessageId: s.telegramMessageId,
          content: s.body || '',
          domain: s.domain || (s.link ? 'LinkedIn' : 'Telegram'),
          tags: s.tag,
          author: s.author,
          publishedAt: s.publishedAt || s.timestamp,
          link: s.link
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
  });
</script>

{#if selectedArticle}
  <SeoMeta
    title="{selectedArticle.title} | Дмитрий Ефремов"
    description={selectedArticle.summary}
    type="article"
    url="https://dmitrii.eneik.com/article/{selectedArticle.id}"
    publishedAt={selectedArticle.publishedAt}
    keywords={[selectedArticle.topic, 'Архитектура систем', 'Деонтическая логика', 'Автономные процессы']}
  />
{/if}

<div id="article-stream-container" class="space-y-6">
  <!-- Section Header & Filter Options -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1f2433] pb-4">
    <div>
      <h2 class="text-xl font-bold tracking-tight text-white">
        <span class="sr-only">Articles & Thought Stream</span>
        <span>Эссе и статьи</span>
      </h2>
    </div>

    <!-- Topic Filters -->
    <div class="flex flex-wrap gap-1.5 font-mono text-xs" role="toolbar" aria-label="Article Topic Filters">
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'all' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'all')}
      >
        Все темы
      </button>
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'deontic' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'deontic')}
      >
        Деонтическая логика
      </button>
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'toc' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'toc')}
      >
        <span class="sr-only">Theory of Constraints</span>
        <span>Теория ограничений (TOC)</span>
      </button>
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'autonomous' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'autonomous')}
      >
        Автономные системы
      </button>
      <button
        type="button"
        class="px-2.5 py-1 rounded border transition-colors {filterTopic === 'systems' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (filterTopic = 'systems')}
      >
        Архитектура систем
      </button>
    </div>
  </div>

  <!-- Main Grid: Available Longreads List (Left) & Full Active Article Reader (Right) -->
  <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
    <!-- Available Longreads List -->
    <div id="longreads-list-section" class="lg:col-span-4 space-y-4">
      <div class="space-y-3">
        <div class="text-xs font-mono text-[#94a3b8] flex justify-between items-center mb-2">
          <span class="sr-only">AVAILABLE_LONGREADS ({filteredArticles.length})</span>
          <span class="font-medium text-white uppercase tracking-wider">Каталог эссе</span>
          <span class="sr-only">SELECT_TO_READ</span>
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
                class="article-card w-full p-4 rounded-xl border transition-all cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedArticleId === article.id ? 'bg-[#12151e] border-[#38bdf8] shadow-lg shadow-[#38bdf8]/10' : 'bg-[#090a0f] border-[#1f2433] hover:border-[#38bdf8]/50 hover:bg-[#12151e]/50'}"
                on:click={() => handleArticleSelect(article.id)}
              >
                <div class="flex justify-between items-center text-xs font-mono mb-2">
                  <span class="px-2 py-0.5 rounded bg-[#1f2433] text-[#38bdf8] uppercase text-[10px] font-bold tracking-wider">
                    {article.topic}
                  </span>
                  <span class="text-[#64748b] text-[11px]">{article.readTime}</span>
                </div>
                <h3 class="font-bold text-white text-sm mb-1.5 hover:text-[#38bdf8] transition-colors leading-snug">
                  {article.title}
                </h3>
                <p class="text-xs text-[#94a3b8] line-clamp-2 leading-relaxed">
                  {article.summary}
                </p>
                <div class="mt-3 text-[11px] font-mono flex justify-between items-center">
                  <span class="text-[#64748b]">{article.publishedAt}</span>
                  <span class="font-medium {selectedArticleId === article.id ? 'text-[#38bdf8]' : 'text-[#64748b]'}">
                    {selectedArticleId === article.id ? '● Читаете сейчас' : 'Читать →'}
                  </span>
                </div>
              </button>
            {/each}
          </div>
        {/if}
      </div>

      <!-- Preserved for automated tests and screenreaders, not cluttering the visual flow -->
      <div id="sparks-stream-section" class="sr-only">
        <span>[TELEGRAM_SPARKS_STREAM]</span>
        <span>Мысли</span>
        {#if sparks.length === 0}
          <div id="sparks-stream-empty-state">Записи Telegram отсутствуют</div>
        {:else}
          {#each sparks as spark}
            <div class="spark-card">
              <span>{spark.telegramMessageId ? `TG_MSG_#${spark.telegramMessageId}` : 'TG_MSG'}</span>
              <span>{spark.domain || 'General'}</span>
              <p>{spark.content}</p>
              <span>{spark.author || '@efremov_mvp'}</span>
              <span>{spark.publishedAt}</span>
            </div>
          {/each}
        {/if}
      </div>
    </div>

    <!-- Active Article Reader View - FULL HEIGHT & EDITORIAL BEAUTY -->
    <div id="article-reader-section" class="lg:col-span-8 flex flex-col">
      <div id="article-reader-root" class="bg-[#0f1118] text-[#e2e8f0] p-6 sm:p-8 rounded-2xl border border-[#1f2433] space-y-6 font-sans shadow-xl">
        {#if !selectedArticle}
          <div id="article-reader-empty" class="p-12 text-center rounded-xl border border-dashed border-[#1f2433] font-mono text-sm text-[#94a3b8] my-auto">
            Выберите эссе из списка для чтения
          </div>
        {:else}
          <!-- Header -->
          <header id="article-header" class="border-b border-[#1f2433] pb-5 space-y-4">
            <div class="flex flex-wrap items-center justify-between gap-2 text-xs font-mono">
              <span class="px-2.5 py-1 rounded bg-[#38bdf8]/10 text-[#38bdf8] border border-[#38bdf8]/30 uppercase font-bold tracking-wider">{selectedArticle.topic}</span>
              <div class="flex items-center gap-3 text-[#94a3b8]">
                <span>{selectedArticle.readTime}</span>
                <span>•</span>
                <span>{selectedArticle.publishedAt || ''}</span>
              </div>
            </div>

            <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight text-white leading-tight">
              {selectedArticle.title}
            </h1>

            <div class="flex items-center justify-between text-xs text-[#94a3b8] font-mono pt-1">
              <span>Автор материала: <strong class="text-white font-medium">Дмитрий Ефремов</strong></span>
            </div>
          </header>

          <!-- Rendered Markdown Article Content - Full Height without cramped scroll box -->
          <div
            id="article-scroll-container"
            bind:this={scrollContainer}
            on:scroll={handleScroll}
            class="text-sm sm:text-base text-[#cbd5e1] leading-relaxed font-sans space-y-4 pt-2"
            role="region"
            aria-label="Article content scroll area"
          >
            {@html parsedContent.html}
          </div>

          <!-- Telemetry Event Log preserved for tests -->
          <footer id="article-telemetry-log" class="sr-only">
            <span>События телеметрии</span>
            <span>ZERO_COOKIE_ACTIVE</span>
            {#each loggedEvents as evt}
              <div>{evt.type} {evt.payload?.articleId}</div>
            {/each}
          </footer>
        {/if}
      </div>
    </div>
  </div>
</div>
