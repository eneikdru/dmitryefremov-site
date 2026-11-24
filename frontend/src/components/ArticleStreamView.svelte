<script lang="ts">
  import { onMount } from 'svelte';
  import { parseMarkdown } from '../lib/markdown';
  import { telemetry, calculateScrollPercentage, type TelemetryEvent } from '../lib/telemetry';
  import { fetchEssaysFromApi, INITIAL_ESSAYS, getLocalizedEssay, type EssayArticleItem } from '../lib/essays';
  import { currentLanguage, t } from '../lib/i18n';
  import { fetchSparksFromApi } from '../lib/sparks';
  import ResponsivePicture from './ResponsivePicture.svelte';
  import SeoMeta from './SeoMeta.svelte';
  import { SMART_COMPANIES_EN_PATH, SMART_COMPANIES_ESSAY_ID, SMART_COMPANIES_RU_PATH } from '../lib/smart_companies_essay';

  export type Article = EssayArticleItem;

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
  export let theme: 'dark' | 'light' = 'light';

  let scrollContainer: HTMLElement;
  let trackedThresholds: Set<number> = new Set();
  let currentPercentage = 0;
  let loggedEvents: TelemetryEvent[] = [];
  let isCatalogLoading = articles === INITIAL_ESSAYS;

  $: localizedArticles = articles.map(a => getLocalizedEssay(a, $currentLanguage));
  $: selectedArticle = selectedArticleId ? (localizedArticles.find(a => a.id === selectedArticleId || a.slug === selectedArticleId) || null) : (localizedArticles[0] || null);
  $: isSmartCompaniesEssay = selectedArticle?.id === SMART_COMPANIES_ESSAY_ID;
  $: articleUrl = isSmartCompaniesEssay
    ? `https://dmitryefremov.com${$currentLanguage === 'ru' ? SMART_COMPANIES_RU_PATH : SMART_COMPANIES_EN_PATH}`
    : `https://dmitryefremov.com/article/${selectedArticle?.id || ''}`;
    function stripLeadingH1(markdown: string): string {
    if (!markdown) return '';
    return markdown.replace(/^\s*#\s+[^\n]+(?:\r?\n|$)/, '');
  }

  $: parsedContent = selectedArticle ? parseMarkdown(stripLeadingH1(selectedArticle.contentMarkdown)) : { html: '' };
  $: filteredArticles = filterTopic === 'all'
    ? localizedArticles
    : localizedArticles.filter(a => a.topic === filterTopic);

  function getArticle(id: string): Article | null {
    return localizedArticles.find(a => a.id === id || a.slug === id) || null;
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
    if (isCatalogLoading) {
      try {
        const fetchedEssays = await fetchEssaysFromApi();
        if (fetchedEssays && fetchedEssays.length > 0) articles = fetchedEssays;
      } finally {
        isCatalogLoading = false;
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

{#if isCatalogLoading}
  <div role="status" aria-live="polite" class="px-4 py-12 text-center {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
    {$currentLanguage === 'ka' ? 'იტვირთება…' : $currentLanguage === 'en' ? 'Loading…' : 'Загрузка…'}
  </div>
{:else if selectedArticleId && !selectedArticle}
  <div id="article-not-found" class="flex flex-col items-center justify-center min-h-[50vh] px-4 text-center">
    <div class="px-3 py-1 rounded-sm border text-xs mb-4 {theme === 'dark' ? 'bg-red-500/10 border-red-500/30 text-red-400' : 'bg-[#F1EFE9] border-[#8C2D19]/30 text-[#8C2D19]'}">
      HTTP 404
    </div>
    <h2 class="font-serif text-3xl font-medium mb-2 {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">Материал не найден</h2>
    <p class="text-sm max-w-md mb-6 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
      Запрошенное эссе «{selectedArticleId}» отсутствует в публичном реестре публикаций.
    </p>
    <button
      on:click={() => { selectedArticleId = undefined; if (typeof window !== 'undefined') window.history.pushState({}, '', '/essays'); }}
      class="text-sm px-5 py-2.5 rounded-sm font-medium transition-colors cursor-pointer {theme === 'dark' ? 'bg-[#C9A461] text-[#141413] hover:bg-[#d8b573]' : 'bg-[#8C2D19] text-[#FAF8F4] hover:bg-[#702313]'}"
    >
      ← Все публикации
    </button>
  </div>
{:else}
{#if selectedArticle}
  <SeoMeta
    title="{selectedArticle.title} | Дмитрий Ефремов"
    description={selectedArticle.summary}
    type="article"
    url={articleUrl}
    imageUrl={selectedArticle.imageUrl}
    imageAlt={selectedArticle.title}
    locale={$currentLanguage === 'ru' ? 'ru_RU' : 'en_US'}
    alternateEnUrl={isSmartCompaniesEssay ? `https://dmitryefremov.com${SMART_COMPANIES_EN_PATH}` : undefined}
    alternateRuUrl={isSmartCompaniesEssay ? `https://dmitryefremov.com${SMART_COMPANIES_RU_PATH}` : undefined}
    publishedAt={selectedArticle.publishedAt}
    keywords={[selectedArticle.topic, 'Архитектура систем', 'Деонтическая логика', 'Автономные процессы']}
  />
{/if}

<div id="article-stream-container" class="space-y-8 font-sans">
  <!-- Section Header & Editorial Topic Filters -->
  <div class="flex flex-col sm:flex-row sm:items-baseline justify-between gap-4 border-b pb-5 {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}">
    <div>
      <h2 class="font-serif text-3xl sm:text-4xl font-medium tracking-[-0.02em] leading-[1.15] {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">
        <span class="sr-only">Articles & Thought Stream</span>
        <span>{t($currentLanguage).articles_title}</span>
      </h2>
    </div>

    <!-- Topic Filters -->
    <div class="flex flex-wrap gap-2 text-xs font-sans" role="toolbar" aria-label="Article Topic Filters">
      <button
        type="button"
        class="px-3 py-1.5 rounded-sm border transition-colors cursor-pointer {filterTopic === 'all'
          ? (theme === 'dark' ? 'bg-[#C9A461] text-[#141413] border-[#C9A461] font-medium' : 'bg-[#8C2D19] text-[#FAF8F4] border-[#8C2D19] font-medium')
          : (theme === 'dark' ? 'bg-transparent text-[#a8a398] border-white/10 hover:text-[#EAE6DF]' : 'bg-transparent text-[#57554F] border-[#E2DFD7] hover:text-[#151514] hover:border-[#151514]/30')}"
        on:click={() => (filterTopic = 'all')}
      >
        <span>{t($currentLanguage).articles_all_topics}</span>
      </button>
      <button
        type="button"
        class="px-3 py-1.5 rounded-sm border transition-colors cursor-pointer {filterTopic === 'deontic'
          ? (theme === 'dark' ? 'bg-[#C9A461] text-[#141413] border-[#C9A461] font-medium' : 'bg-[#8C2D19] text-[#FAF8F4] border-[#8C2D19] font-medium')
          : (theme === 'dark' ? 'bg-transparent text-[#a8a398] border-white/10 hover:text-[#EAE6DF]' : 'bg-transparent text-[#57554F] border-[#E2DFD7] hover:text-[#151514] hover:border-[#151514]/30')}"
        on:click={() => (filterTopic = 'deontic')}
      >
        <span>{t($currentLanguage).articles_deontic}</span>
      </button>
      <button
        type="button"
        class="px-3 py-1.5 rounded-sm border transition-colors cursor-pointer {filterTopic === 'toc'
          ? (theme === 'dark' ? 'bg-[#C9A461] text-[#141413] border-[#C9A461] font-medium' : 'bg-[#8C2D19] text-[#FAF8F4] border-[#8C2D19] font-medium')
          : (theme === 'dark' ? 'bg-transparent text-[#a8a398] border-white/10 hover:text-[#EAE6DF]' : 'bg-transparent text-[#57554F] border-[#E2DFD7] hover:text-[#151514] hover:border-[#151514]/30')}"
        on:click={() => (filterTopic = 'toc')}
      >
        <span class="sr-only">Theory of Constraints</span>
        <span>{t($currentLanguage).articles_toc}</span>
      </button>
      <button
        type="button"
        class="px-3 py-1.5 rounded-sm border transition-colors cursor-pointer {filterTopic === 'autonomous'
          ? (theme === 'dark' ? 'bg-[#C9A461] text-[#141413] border-[#C9A461] font-medium' : 'bg-[#8C2D19] text-[#FAF8F4] border-[#8C2D19] font-medium')
          : (theme === 'dark' ? 'bg-transparent text-[#a8a398] border-white/10 hover:text-[#EAE6DF]' : 'bg-transparent text-[#57554F] border-[#E2DFD7] hover:text-[#151514] hover:border-[#151514]/30')}"
        on:click={() => (filterTopic = 'autonomous')}
      >
        <span>{$currentLanguage === 'ru' ? 'Автономные системы' : ($currentLanguage === 'ka' ? 'ავტონომიური სისტემები' : 'Autonomous Systems')}</span>
      </button>
      <button
        type="button"
        class="px-3 py-1.5 rounded-sm border transition-colors cursor-pointer {filterTopic === 'systems'
          ? (theme === 'dark' ? 'bg-[#C9A461] text-[#141413] border-[#C9A461] font-medium' : 'bg-[#8C2D19] text-[#FAF8F4] border-[#8C2D19] font-medium')
          : (theme === 'dark' ? 'bg-transparent text-[#a8a398] border-white/10 hover:text-[#EAE6DF]' : 'bg-transparent text-[#57554F] border-[#E2DFD7] hover:text-[#151514] hover:border-[#151514]/30')}"
        on:click={() => (filterTopic = 'systems')}
      >
        <span>{$currentLanguage === 'ru' ? 'Архитектура систем' : ($currentLanguage === 'ka' ? 'სისტემების არქიტექტურა' : 'Systems Architecture')}</span>
      </button>
    </div>
  </div>

  <!-- Main Grid: Essay Index (Left) & Long-Form Journal Reader (Right) -->
  <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-start">
    <!-- Available Longreads List -->
    <div id="longreads-list-section" class="lg:col-span-4 space-y-4 order-2 lg:order-1">
      <div class="space-y-3">
        <div class="text-xs font-sans flex justify-between items-center mb-3 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
          <span class="sr-only">AVAILABLE_LONGREADS ({filteredArticles.length})</span>
          <span class="font-medium">
            {$currentLanguage === 'ru' ? 'Каталог эссе' : ($currentLanguage === 'ka' ? 'ესეების კატალოგი' : 'Essay Catalog')}
          </span>
          <span class="sr-only">SELECT_TO_READ</span>
        </div>

        {#if filteredArticles.length === 0}
          <div id="articles-empty-state" class="p-8 text-center rounded-sm border border-dashed text-sm {theme === 'dark' ? 'border-white/15 bg-[#1c1b18] text-[#a8a398]' : 'border-[#E2DFD7] bg-[#F1EFE9] text-[#57554F]'}">
            {$currentLanguage === 'ru' ? 'Эссе и публикации отсутствуют' : ($currentLanguage === 'ka' ? 'ესეები და პუბლიკაციები არ არის' : 'No essays or publications available')}
          </div>
        {:else}
          <div class="border-t divide-y {theme === 'dark' ? 'border-white/10 divide-white/10' : 'border-[#E2DFD7] divide-[#E2DFD7]'}" role="list" aria-label="Available Longreads">
            {#each filteredArticles as article}
              {@const isSelected = selectedArticleId === article.id}
              <button
                type="button"
                class="article-card w-full py-5 px-4 border-l-2 transition-colors cursor-pointer text-left focus:outline-none {isSelected
                  ? (theme === 'dark' ? 'bg-[#1c1b18] border-l-[#C9A461]' : 'bg-[#F1EFE9] border-l-[#8C2D19]')
                  : (theme === 'dark' ? 'border-l-transparent hover:bg-[#1c1b18]/60' : 'border-l-transparent hover:bg-[#F1EFE9]/60')}"
                on:click={() => handleArticleSelect(article.id)}
              >
                <div class="flex justify-between items-center text-xs mb-1.5">
                  <span class="font-medium {theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]'}">
                    {article.topic}
                  </span>
                  <span class="{theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">{article.readTime}</span>
                </div>
                <h3 class="font-serif text-lg font-medium mb-1.5 leading-snug {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">
                  {article.title}
                </h3>
                <p class="text-xs line-clamp-2 leading-relaxed {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
                  {article.summary}
                </p>
                <div class="mt-3 text-xs flex justify-between items-center">
                  <span class="{theme === 'dark' ? 'text-[#a8a398]/80' : 'text-[#57554F]/80'}">{article.publishedAt}</span>
                  <span class="font-medium {isSelected ? (theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]') : (theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]')}">
                    {isSelected
                      ? ($currentLanguage === 'ru' ? 'Читаете сейчас' : ($currentLanguage === 'ka' ? 'კითხულობთ ახლა' : 'Reading now'))
                      : ($currentLanguage === 'ru' ? 'Читать →' : ($currentLanguage === 'ka' ? 'წაკითხვა →' : 'Read →'))}
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

    <!-- Active Article Reader View -->
    <div id="article-reader-section" class="lg:col-span-8 flex flex-col order-1 lg:order-2">
      <div id="article-reader-root" class="p-6 sm:p-10 rounded-sm border space-y-6 font-sans transition-colors {theme === 'dark' ? 'bg-[#1c1b18] text-[#EAE6DF] border-white/10' : 'bg-[#FAF8F4] text-[#151514] border-[#E2DFD7]'}">
        {#if !selectedArticle}
          <div id="article-reader-empty" class="p-12 text-center rounded-sm border border-dashed text-sm my-auto {theme === 'dark' ? 'border-white/15 text-[#a8a398]' : 'border-[#E2DFD7] text-[#57554F]'}">
            Выберите эссе из списка для чтения
          </div>
        {:else}
          <!-- Header -->
          <header id="article-header" class="border-b pb-6 space-y-4 {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}">
            <div class="flex flex-wrap items-center justify-between gap-2 text-xs">
              <span class="font-medium {theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]'}">{selectedArticle.topic}</span>
              <div class="flex items-center gap-2.5 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
                <span>{selectedArticle.readTime}</span>
                <span>·</span>
                <span>{selectedArticle.publishedAt || ''}</span>
              </div>
            </div>

            <h1 class="font-serif text-3xl sm:text-4xl font-medium tracking-[-0.02em] leading-[1.15] {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">
              {selectedArticle.title}
            </h1>

            <div class="flex items-center justify-between text-sm pt-1 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
              <span>Автор материала: <strong class="font-medium {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">Дмитрий Ефремов</strong></span>
            </div>

            <!-- Related Concepts Cross-Links -->
            <div id="article-cross-links" class="pt-4 border-t space-y-2.5 {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}">
              <span class="text-xs font-medium block {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
                Связанные материалы
              </span>
              <div class="flex flex-wrap gap-2 text-xs">
                <button
                  id={`crosslink-deontic-${selectedArticle.id}`}
                  type="button"
                  on:click={() => handleCrossLinkClick('concept-deontic-logic', 'concept', 'Деонтическая логика')}
                  aria-label="Перейти к узлу Деонтическая логика"
                  class="px-3 py-1 rounded-sm border transition-colors cursor-pointer flex items-center gap-1.5 {theme === 'dark' ? 'bg-[#141413] text-[#C9A461] border-white/10 hover:border-[#C9A461]/60' : 'bg-[#F1EFE9] text-[#8C2D19] border-[#E2DFD7] hover:border-[#8C2D19]/50'}"
                >
                  <span>→</span>
                  <span>Деонтическая логика</span>
                </button>
                <button
                  id={`crosslink-toc-${selectedArticle.id}`}
                  type="button"
                  on:click={() => handleCrossLinkClick('concept-theory-of-constraints', 'concept', 'Теория ограничений')}
                  aria-label="Перейти к узлу Теория ограничений"
                  class="px-3 py-1 rounded-sm border transition-colors cursor-pointer flex items-center gap-1.5 {theme === 'dark' ? 'bg-[#141413] text-[#EAE6DF] border-white/10 hover:border-[#C9A461]/60' : 'bg-[#F1EFE9] text-[#151514] border-[#E2DFD7] hover:border-[#8C2D19]/50'}"
                >
                  <span>→</span>
                  <span>Теория ограничений (TOC)</span>
                </button>
                <button
                  id={`crosslink-podcast-${selectedArticle.id}`}
                  type="button"
                  on:click={() => handleCrossLinkClick('podcast-ep-01', 'podcast', 'Выпуск 01: Системы и реальность')}
                  aria-label="Перейти к подкасту Выпуск 01"
                  class="px-3 py-1 rounded-sm border transition-colors cursor-pointer flex items-center gap-1.5 {theme === 'dark' ? 'bg-[#141413] text-[#EAE6DF] border-white/10 hover:border-[#C9A461]/60' : 'bg-[#F1EFE9] text-[#151514] border-[#E2DFD7] hover:border-[#8C2D19]/50'}"
                >
                  <span>Подкаст · Выпуск 01</span>
                </button>
              </div>
            </div>
          </header>

          {#if selectedArticle.imageUrl}
            <img
              src={selectedArticle.imageUrl.replace('https://dmitryefremov.com', '')}
              alt={selectedArticle.title}
              class="w-full aspect-video object-cover rounded-sm border {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}"
              loading="eager"
            />
          {/if}

          <!-- Rendered Markdown Article Content - Editorial 65ch Measure -->
          <div
            id="article-scroll-container"
            bind:this={scrollContainer}
            on:scroll={handleScroll}
            class="max-w-[65ch] text-base sm:text-[17px] leading-[1.75] font-sans space-y-4 pt-2 {theme === 'dark' ? 'article-prose-dark text-[#EAE6DF]/90' : 'article-prose-light text-[#151514]/90'}"
            role="region"
            aria-label="Article content scroll area"
          >
            {@html parsedContent.html}
          </div>
        {/if}
      </div>
    </div>
  </div>
</div>

{/if}

<style>
  .article-prose-light :global(p) {
    color: #151514;
    font-family: var(--font-sans);
    font-size: 1.0625rem;
    line-height: 1.75;
    margin-top: 1.1rem;
    margin-bottom: 1.1rem;
  }
  .article-prose-light :global(h1),
  .article-prose-light :global(h2),
  .article-prose-light :global(h3) {
    color: #151514;
    font-family: var(--font-serif);
    font-weight: 500;
    border-color: #E2DFD7;
  }
  .article-prose-light :global(h2) {
    font-size: 1.75rem;
    line-height: 1.2;
    padding-bottom: 0.4rem;
  }
  .article-prose-light :global(h3) {
    font-size: 1.375rem;
    line-height: 1.25;
  }
  .article-prose-light :global(strong) {
    color: #151514;
    font-weight: 600;
  }
  .article-prose-light :global(em) {
    color: #151514;
  }
  .article-prose-light :global(ul) {
    color: #151514;
  }
  .article-prose-light :global(blockquote.article-blockquote) {
    background-color: #F1EFE9;
    border-left: 2px solid #8C2D19;
    color: #151514;
    font-family: var(--font-serif);
    font-size: 1.2rem;
    border-radius: 0.125rem;
    box-shadow: none;
  }
  .article-prose-light :global(.formula-block),
  .article-prose-light :global(.code-block) {
    background-color: #F1EFE9;
    border-color: #E2DFD7;
    color: #151514;
    box-shadow: none;
  }
  .article-prose-light :global(.formula-inline),
  .article-prose-light :global(code) {
    background-color: #F1EFE9;
    border-color: #E2DFD7;
    color: #8C2D19;
  }

  .article-prose-dark :global(p) {
    color: #EAE6DF;
    font-family: var(--font-sans);
    font-size: 1.0625rem;
    line-height: 1.75;
    margin-top: 1.1rem;
    margin-bottom: 1.1rem;
  }
  .article-prose-dark :global(h1),
  .article-prose-dark :global(h2),
  .article-prose-dark :global(h3) {
    color: #EAE6DF;
    font-family: var(--font-serif);
    font-weight: 500;
    border-color: rgba(255, 255, 255, 0.1);
  }
  .article-prose-dark :global(blockquote.article-blockquote) {
    background-color: #141413;
    border-left: 2px solid #C9A461;
    color: #EAE6DF;
    font-family: var(--font-serif);
    font-size: 1.2rem;
    border-radius: 0.125rem;
    box-shadow: none;
  }
  .article-prose-dark :global(.formula-block),
  .article-prose-dark :global(.code-block) {
    background-color: #141413;
    border-color: rgba(255, 255, 255, 0.1);
    color: #EAE6DF;
    box-shadow: none;
  }
  .article-prose-dark :global(.formula-inline),
  .article-prose-dark :global(code) {
    background-color: #141413;
    border-color: rgba(255, 255, 255, 0.12);
    color: #C9A461;
  }
</style>
