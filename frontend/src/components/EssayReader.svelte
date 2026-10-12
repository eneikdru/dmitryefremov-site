<script lang="ts">
  import { onMount } from 'svelte';
  import { parseMarkdown } from '../lib/markdown';
  import { telemetry, calculateScrollPercentage, type TelemetryEvent } from '../lib/telemetry';
  import ResponsivePicture from './ResponsivePicture.svelte';

  export interface Essay {
    id: string;
    slug?: string;
    title: string;
    topic?: string;
    readTime?: string;
    publishedAt?: string;
    summary?: string;
    contentMarkdown: string;
    coverAssetKey?: string;
  }

  export let article: Essay | null = null;
  export let slug: string | null = null;
  export let fetchError: string | null = null;
  export let onReturnToIndex: (() => void) | null = null;

  let loadedArticle: Essay | null = null;
  let internalError: string | null = null;
  let isLoading = false;

  let scrollContainer: HTMLElement;
  let trackedThresholds: Set<number> = new Set();
  let currentPercentage = 0;
  let loggedEvents: TelemetryEvent[] = [];

  $: currentArticle = article || loadedArticle;
  $: activeError = fetchError || internalError;

  $: parsedContent = currentArticle
    ? parseMarkdown(currentArticle.contentMarkdown)
    : { html: '', hasFormulas: false, hasQuotes: false };

  async function fetchEssayBySlug(targetSlug: string) {
    isLoading = true;
    internalError = null;
    try {
      const response = await fetch(`/api/v1/articles/${targetSlug}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch article (Status: ${response.status})`);
      }
      const data = await response.json();
      loadedArticle = {
        id: data.slug || String(data.id),
        slug: data.slug,
        title: data.title,
        summary: data.summary,
        publishedAt: data.publishedAt ? data.publishedAt.slice(0, 10) : undefined,
        contentMarkdown: data.content || ''
      };
    } catch (err: any) {
      internalError = err.message || 'Error loading essay data';
    } finally {
      isLoading = false;
    }
  }

  $: if (slug && !article) {
    fetchEssayBySlug(slug);
  }

  function handleReturnToIndex() {
    internalError = null;
    fetchError = null;
    if (onReturnToIndex) {
      onReturnToIndex();
    }
  }

  function handleScroll() {
    if (!scrollContainer || !currentArticle) return;
    const { scrollTop, scrollHeight, clientHeight } = scrollContainer;
    currentPercentage = calculateScrollPercentage(scrollTop, scrollHeight, clientHeight);

    const thresholds = [25, 50, 75, 100];
    for (const threshold of thresholds) {
      if (currentPercentage >= threshold && !trackedThresholds.has(threshold)) {
        trackedThresholds.add(threshold);
        const event = telemetry.trackScrollDepth(currentArticle.id, threshold);
        loggedEvents = [...loggedEvents, event];
      }
    }
  }

  onMount(() => {
    if (scrollContainer) {
      handleScroll();
    }
  });
</script>

<div id="article-reader-root" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] h-full flex flex-col justify-between font-sans">
  {#if activeError}
    <!-- Stylized Error State -->
    <div id="essay-reader-error" class="flex flex-col items-center justify-center py-12 px-6 text-center space-y-6 my-auto bg-[#12151e] rounded-lg border border-[#ef4444]/30 shadow-lg">
      <div class="flex items-center gap-2">
        <span class="bg-[#ef4444]/10 text-[#ef4444] border border-[#ef4444]/30 px-2.5 py-1 rounded text-xs font-mono font-bold">
          [FETCH_ERROR]
        </span>
      </div>
      <div class="space-y-2 max-w-md">
        <h2 class="text-xl font-sans font-bold tracking-tight text-white">
          Essay Unreachable
        </h2>
        <p class="text-xs text-[#94a3b8] font-mono leading-relaxed">
          {activeError}
        </p>
      </div>
      <button
        id="return-to-index-btn"
        type="button"
        on:click={handleReturnToIndex}
        class="px-4 py-2 bg-[#38bdf8] hover:bg-[#0284c7] text-[#090a0f] font-mono text-xs font-bold rounded transition-colors shadow focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
      >
        ← Return to Index
      </button>
    </div>
  {:else if isLoading}
    <!-- Loading State -->
    <div id="essay-reader-loading" class="flex flex-col items-center justify-center py-16 text-center space-y-3 font-mono text-xs text-[#94a3b8]">
      <div class="animate-spin text-[#38bdf8]">↻</div>
      <span>FETCHING_ESSAY_DATA...</span>
    </div>
  {:else if currentArticle}
    <!-- Active Article Reader View -->
    <div>
      <!-- Header -->
      <header id="article-header" class="border-b border-[#1f2433] pb-4 mb-4 space-y-3">
        <div class="flex items-center justify-between gap-2">
          <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
            [SYS_ARTICLE]
          </span>
          <span class="text-xs font-mono text-[#94a3b8]">{currentArticle.id}</span>
        </div>
        {#if currentArticle.coverAssetKey}
          <div id="article-cover-container" class="w-full h-32 sm:h-40 rounded-lg overflow-hidden border border-[#38bdf8]/30">
            <ResponsivePicture
              id="article-cover-img"
              assetKey={currentArticle.coverAssetKey}
              alt={`Cover illustration for ${currentArticle.title}`}
              imgClass="w-full h-full object-cover"
              loading="eager"
            />
          </div>
        {/if}
        <!-- Grotesque Header Typography -->
        <h1 class="text-2xl font-sans font-extrabold tracking-tight text-white leading-tight">
          {currentArticle.title}
        </h1>
        <div class="flex items-center justify-between text-xs text-[#94a3b8] font-mono">
          <span>Author: Dmitry Efremov</span>
          <span>Scroll Depth Logged: {trackedThresholds.size}/4</span>
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

      <!-- Rendered Markdown Article Content with Optimized Reading Typography -->
      <div
        id="article-scroll-container"
        bind:this={scrollContainer}
        on:scroll={handleScroll}
        class="max-h-[420px] overflow-y-auto pr-3 border border-[#1f2433] bg-[#12151e] p-5 rounded text-sm text-[#cbd5e1] leading-relaxed font-serif space-y-3 shadow-inner"
        role="region"
        aria-label="Article content scroll area"
      >
        {@html parsedContent.html}
      </div>
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
              <span>[DEPTH_{evt.payload.depth}%] {evt.payload.articleId}</span>
              <span class="text-[#64748b]">{evt.timestamp.slice(11, 19)}</span>
            </div>
          {/each}
        {/if}
      </div>
    </footer>
  {/if}
</div>
