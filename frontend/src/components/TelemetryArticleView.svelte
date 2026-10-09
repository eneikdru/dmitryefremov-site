<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry, calculateScrollPercentage, type TelemetryEvent } from '../lib/telemetry';

  export let articleId: string = 'art-01-deontic-logic';
  export let title: string = 'Deontic Logic in Autonomous Process Architecture';
  export let contentParagraphs: string[] = [
    'In formal software architecture, deontic logic provides the normative framework for explicit obligations, permissions, and prohibitions.',
    'Traditional imperative systems rely on implicit constraints scattered across imperative routines, leading to drift, unhandled edge cases, and unexpected side effects under load.',
    'By establishing an explicit deontic specification, autonomous process engines can verify invariants before executing state transitions.',
    'System integrity is preserved not through defensive runtime patching, but through mathematical supervenience over state spaces.',
    'When applied to telemetry instrumentation, deontic purity dictates that monitoring must remain unobtrusive, zero-cookie, and strictly functional.'
  ];

  let scrollContainer: HTMLElement;
  let trackedThresholds: Set<number> = new Set();
  let currentPercentage = 0;
  let loggedEvents: TelemetryEvent[] = [];

  function handleScroll() {
    if (!scrollContainer) return;
    const { scrollTop, scrollHeight, clientHeight } = scrollContainer;
    currentPercentage = calculateScrollPercentage(scrollTop, scrollHeight, clientHeight);

    const thresholds = [25, 50, 75, 100];
    for (const threshold of thresholds) {
      if (currentPercentage >= threshold && !trackedThresholds.has(threshold)) {
        trackedThresholds.add(threshold);
        const event = telemetry.trackScrollDepth(articleId, threshold);
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

<div id="article-reader-root" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] max-w-2xl mx-auto my-4 font-sans">
  <!-- Header -->
  <header id="article-header" class="border-b border-[#1f2433] pb-4 mb-6">
    <div class="flex items-center gap-2 mb-2">
      <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">[SYS_ARTICLE]</span>
      <span class="text-xs font-mono text-[#94a3b8]">{articleId}</span>
    </div>
    <h1 class="text-2xl font-bold tracking-tight text-white mb-2">{title}</h1>
    <div class="flex items-center justify-between text-xs text-[#94a3b8] font-mono">
      <span>Author: Dmitry Efremov</span>
      <span>Scroll Depth Logged: {trackedThresholds.size}/4</span>
    </div>
  </header>

  <!-- Scroll Progress Indicator -->
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

  <!-- Scrollable Article Content -->
  <div
    id="article-scroll-container"
    bind:this={scrollContainer}
    on:scroll={handleScroll}
    class="max-h-64 overflow-y-auto pr-4 space-y-4 border border-[#1f2433] bg-[#12151e] p-4 rounded text-sm text-[#cbd5e1] leading-relaxed font-serif"
    tabindex="0"
    role="region"
    aria-label="Article content scroll area"
  >
    {#each contentParagraphs as paragraph, i}
      <p id="para-{i}">{paragraph}</p>
    {/each}
  </div>

  <!-- Telemetry Event Log View -->
  <footer id="article-telemetry-log" class="mt-6 pt-4 border-t border-[#1f2433]">
    <div class="text-xs font-mono text-[#94a3b8] mb-2 flex items-center justify-between">
      <span>[TELEMETRY_EVENT_FEED]</span>
      <span class="text-[#34d399]">ZERO_COOKIE_ACTIVE</span>
    </div>
    <div class="bg-[#12151e] p-3 rounded border border-[#1f2433] font-mono text-xs max-h-32 overflow-y-auto space-y-1 text-[#a7f3d0]">
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
</div>
