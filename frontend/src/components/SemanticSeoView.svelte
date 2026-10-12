<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import {
    DEFAULT_SEO_STATE,
    injectOpenGraphMeta,
    injectJsonLdSchema,
    removeOpenGraphMeta,
    removeJsonLdSchema,
    validateMetaTags,
    simulateBotCrawl,
    type SeoState
  } from '../lib/seo';

  export let seoState: SeoState = { ...DEFAULT_SEO_STATE };

  let isMetaInjectionActive: boolean = seoState.active;
  let isSchemaModalOpen: boolean = false;
  let isSimulatingCrawl: boolean = false;
  let simulationMessage: string | null = null;
  let activeTab: 'crawlers' | 'metadata' | 'preview' | 'directives' = 'crawlers';

  $: validationResult = validateMetaTags();

  function toggleMetaInjection() {
    isMetaInjectionActive = !isMetaInjectionActive;
    seoState.active = isMetaInjectionActive;
    if (isMetaInjectionActive) {
      injectOpenGraphMeta(seoState.ogMeta);
      injectJsonLdSchema(seoState.jsonLd);
    } else {
      removeOpenGraphMeta();
      removeJsonLdSchema();
    }
  }

  function handleSimulateCrawl() {
    isSimulatingCrawl = true;
    simulationMessage = null;
    const sim = simulateBotCrawl('GPTBot');
    setTimeout(() => {
      isSimulatingCrawl = false;
      simulationMessage = `[200 OK] ${sim.botName} crawl completed in ${sim.latencyMs}ms.`;
      setTimeout(() => {
        simulationMessage = null;
      }, 3500);
    }, 800);
  }

  function handleExportSnippet() {
    const snippet = `# robots.txt snippet
User-agent: PerplexityBot
Allow: /

User-agent: GPTBot
Allow: /

User-agent: ClaudeBot
Allow: /

# Open Graph Meta Tags
<meta property="og:title" content="${seoState.ogMeta.title}">
<meta property="og:description" content="${seoState.ogMeta.description}">
<meta property="og:url" content="${seoState.ogMeta.url}">
<meta property="og:type" content="${seoState.ogMeta.type}">
`;
    navigator.clipboard?.writeText(snippet);
    simulationMessage = 'Snippet copied to clipboard!';
    setTimeout(() => {
      simulationMessage = null;
    }, 2500);
  }

  onMount(() => {
    if (isMetaInjectionActive) {
      injectOpenGraphMeta(seoState.ogMeta);
      injectJsonLdSchema(seoState.jsonLd);
    }
  });

  onDestroy(() => {
    // Clean up DOM head injections when component unmounts
    removeOpenGraphMeta();
    removeJsonLdSchema();
  });
</script>

<div id="seo-view-container" class="space-y-6">
  <!-- SUBTITLE HERO STRIP -->
  <div id="hero-subtitle-strip" class="flex flex-col sm:flex-row items-start sm:items-center justify-between bg-[#12151e] px-4 py-3 rounded-lg border border-[#1f2433] gap-2">
    <div class="flex items-center space-x-2 font-mono text-xs">
      <span class="text-[#38bdf8]">⬢</span>
      <span class="text-[#e2e8f0]">TARGET: {seoState.targetUrl}</span>
    </div>
    <span class="font-mono text-xs text-[#34d399] px-2 py-0.5 bg-[#34d399]/10 rounded border border-[#34d399]/30">
      CANONICAL {seoState.canonicalStatus}
    </span>
  </div>

  <!-- SECTION 1: INDEXING HEALTH OVERVIEW -->
  <section id="indexing-health-section" class="bg-[#12151e] rounded-lg p-5 border border-[#1f2433] space-y-4">
    <div class="flex items-start justify-between">
      <div>
        <span class="font-mono text-xs text-[#94a3b8] uppercase tracking-wider">ENGINE READINESS</span>
        <h2 id="engine-readiness-val" class="text-2xl font-bold text-white mt-1">{seoState.engineReadinessPercent}% AI Indexed</h2>
        <p class="text-xs text-[#94a3b8] mt-1">Vector and token structures fully ingested for machine readers.</p>
      </div>
      <!-- Minimalist Radial Score Badge -->
      <div id="readiness-badge" class="w-14 h-14 flex items-center justify-center rounded-full bg-[#090a0f] border-2 border-[#34d399] shadow-inner font-mono font-bold text-[#34d399] text-sm">
        ✓ 98%
      </div>
    </div>

    <!-- Quick Telemetry Row -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3 pt-3 border-t border-[#1f2433]">
      {#each seoState.crawlers as crawler}
        <div class="bg-[#090a0f] p-3 rounded border border-[#1f2433]/80 font-mono text-xs space-y-1">
          <div class="flex items-center justify-between">
            <span class="text-[#94a3b8]">{crawler.botName}</span>
            <span class="w-2 h-2 rounded-full bg-[#34d399]"></span>
          </div>
          <p class="text-[#34d399] font-bold">{crawler.status.split(' ')[0]}</p>
          <p class="text-[10px] text-[#64748b]">{crawler.lastFetch}</p>
        </div>
      {/each}
    </div>
  </section>

  <!-- SECTION 2: DEDICATED BOT CRAWLER STATUS CARDS -->
  <section id="crawlers-section" class="space-y-4">
    <div class="flex items-center justify-between">
      <h3 class="text-base font-bold text-white flex items-center space-x-2">
        <span class="text-[#38bdf8]">🤖</span>
        <span>Active LLM Scrapers</span>
      </h3>
      <span class="font-mono text-xs text-[#94a3b8]">{seoState.crawlers.length} ACTIVE TARGETS</span>
    </div>

    <div class="space-y-4">
      {#each seoState.crawlers as crawler}
        <article id="crawler-card-{crawler.id}" class="bg-[#12151e] rounded-lg p-5 border border-[#1f2433] space-y-3 font-sans">
          <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
            <div class="flex items-center space-x-3">
              <div class="w-9 h-9 rounded bg-[#1f2433] flex items-center justify-center text-[#38bdf8] border border-[#38bdf8]/30 font-bold font-mono text-xs">
                BOT
              </div>
              <div>
                <div class="flex items-center space-x-2">
                  <span class="font-bold text-white text-base">{crawler.botName}</span>
                  <span class="font-mono text-[11px] px-2 py-0.5 rounded bg-[#34d399]/10 text-[#34d399] border border-[#34d399]/30">
                    {crawler.status}
                  </span>
                </div>
                <span class="font-mono text-xs text-[#64748b]">User-Agent: {crawler.userAgent}</span>
              </div>
            </div>
            <div class="text-left sm:text-right font-mono text-xs">
              <span class="text-[#fbbf24] font-bold">{crawler.rankOrToken}</span>
              <p class="text-[10px] text-[#94a3b8]">{crawler.rankSublabel}</p>
            </div>
          </div>

          <div class="bg-[#090a0f] p-3 rounded border border-[#1f2433] font-mono text-xs text-[#cbd5e1] leading-relaxed">
            <span class="text-[#38bdf8] font-bold">&gt; telemetry:</span> {crawler.telemetryMessage}
          </div>

          <div class="flex flex-wrap items-center justify-between font-mono text-xs text-[#64748b] pt-1 gap-2">
            <span>Frequency: <strong class="text-[#e2e8f0]">{crawler.frequency}</strong></span>
            <span>Latency: <strong class="text-[#34d399]">{crawler.latencyMs}ms</strong></span>
            {#if crawler.tokensUsed}
              <span>Tokens: <strong class="text-[#38bdf8]">{crawler.tokensUsed} tk</strong></span>
            {/if}
          </div>
        </article>
      {/each}
    </div>
  </section>

  <!-- SECTION 3: SEMANTIC & OPEN GRAPH TAGS INJECTION MODULE -->
  <section id="metadata-section" class="bg-[#12151e] rounded-lg p-5 border border-[#1f2433] space-y-4">
    <!-- Section Header with Injection Toggle -->
    <div class="flex items-center justify-between border-b border-[#1f2433] pb-3">
      <div>
        <h3 class="text-base font-bold text-white flex items-center space-x-2">
          <span class="text-[#34d399]">⚙</span>
          <span>Metadata & Schema Injection</span>
        </h3>
        <p class="text-xs text-[#94a3b8] mt-0.5">Dynamic Open Graph & JSON-LD schema injection to document head</p>
      </div>

      <!-- Toggle Switch -->
      <label class="relative inline-flex items-center cursor-pointer" aria-label="Toggle Dynamic SEO Head Injection">
        <input
          id="seo-toggle-input"
          type="checkbox"
          checked={isMetaInjectionActive}
          on:change={toggleMetaInjection}
          class="sr-only peer"
        />
        <div class="w-11 h-6 bg-[#1f2433] peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#34d399]"></div>
      </label>
    </div>

    <!-- Tag List -->
    <div class="space-y-3">
      <!-- Item 1: og:title -->
      <div class="bg-[#090a0f] p-3 rounded border border-[#1f2433]">
        <div class="flex items-center justify-between mb-1.5 font-mono text-xs">
          <span class="text-[#38bdf8] font-semibold">property="og:title"</span>
          <span class="text-[10px] text-[#34d399] bg-[#34d399]/10 px-1.5 py-0.5 rounded border border-[#34d399]/30">VALIDATED</span>
        </div>
        <div class="font-mono text-xs text-white bg-[#12151e] p-2.5 rounded border border-[#1f2433] break-words">
          "{seoState.ogMeta.title}"
        </div>
      </div>

      <!-- Item 2: og:description -->
      <div class="bg-[#090a0f] p-3 rounded border border-[#1f2433]">
        <div class="flex items-center justify-between mb-1.5 font-mono text-xs">
          <span class="text-[#38bdf8] font-semibold">property="og:description"</span>
          <span class="text-[10px] text-[#94a3b8]">{seoState.ogMeta.description.length} CHARS</span>
        </div>
        <div class="font-mono text-xs text-[#cbd5e1] bg-[#12151e] p-2.5 rounded border border-[#1f2433] leading-relaxed">
          "{seoState.ogMeta.description}"
        </div>
      </div>

      <!-- Item 3: article:tag -->
      <div class="bg-[#090a0f] p-3 rounded border border-[#1f2433]">
        <div class="flex items-center justify-between mb-2 font-mono text-xs">
          <span class="text-[#38bdf8] font-semibold">property="article:tag"</span>
          <span class="text-[10px] text-[#94a3b8]">{seoState.ogMeta.tags.length} ENTITIES</span>
        </div>
        <div class="flex flex-wrap gap-2">
          {#each seoState.ogMeta.tags as tag}
            <span class="font-mono text-xs px-2.5 py-1 rounded bg-[#12151e] text-[#e2e8f0] border border-[#1f2433]">
              {tag}
            </span>
          {/each}
        </div>
      </div>

      <!-- Item 4: JSON-LD Schema Snippet -->
      <div class="bg-[#090a0f] p-3 rounded border border-[#1f2433] flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div>
          <div class="flex items-center space-x-2 font-mono text-xs">
            <span class="text-[#fbbf24]">⬡</span>
            <span class="text-white font-bold">application/ld+json</span>
          </div>
          <p class="text-xs text-[#94a3b8] mt-0.5">Schema.org / {seoState.jsonLd['@type']} Entity Anchor</p>
        </div>
        <button
          id="view-schema-btn"
          type="button"
          on:click={() => (isSchemaModalOpen = true)}
          class="px-3 py-1.5 rounded bg-[#12151e] hover:bg-[#1f2433] text-white font-mono text-xs border border-[#1f2433] flex items-center space-x-1.5 transition-colors"
        >
          <span>👁</span>
          <span>View Schema</span>
        </button>
      </div>
    </div>
  </section>

  <!-- SECTION 4: SIMULATED AI SEARCH ENGINE CITATION PREVIEW -->
  <section id="preview-section" class="bg-[#12151e] rounded-lg p-5 border border-[#1f2433] space-y-3">
    <div class="flex items-center justify-between">
      <h3 class="text-base font-bold text-white flex items-center space-x-2">
        <span class="text-[#38bdf8]">💬</span>
        <span>Simulated LLM Citation Preview</span>
      </h3>
      <span class="font-mono text-xs px-2.5 py-0.5 rounded bg-[#38bdf8]/10 text-[#38bdf8] border border-[#38bdf8]/30">
        Perplexity / ChatGPT
      </span>
    </div>

    <!-- Preview Mock Bubble -->
    <div class="bg-[#090a0f] p-4 rounded-lg border border-[#1f2433] space-y-3">
      <div class="flex items-center space-x-2">
        <span class="w-2 h-2 rounded-full bg-[#38bdf8]"></span>
        <span class="font-mono text-xs text-[#94a3b8] uppercase tracking-wider">Direct Answer Extraction</span>
      </div>
      <p class="text-sm text-[#e2e8f0] leading-relaxed">
        According to the verified specification, modern discovery architectures prioritize semantic schema mapping to allow real-time crawler ingestion without synthetic latency.
      </p>

      <!-- Bullet Highlights synthesized from Meta -->
      <div class="space-y-1.5 pl-3 border-l-2 border-[#34d399] text-xs text-[#cbd5e1]">
        <div class="flex items-start space-x-2">
          <span class="text-[#34d399] font-bold">•</span>
          <span>Explicit OpenGraph tags provide exact citation titles for LLM summaries.</span>
        </div>
        <div class="flex items-start space-x-2">
          <span class="text-[#34d399] font-bold">•</span>
          <span>Direct JSON-LD entity anchors guarantee attribution placement.</span>
        </div>
      </div>

      <!-- Footnote Citation Pill -->
      <div class="pt-2 flex items-center justify-between border-t border-[#1f2433]">
        <div class="inline-flex items-center space-x-1.5 bg-[#12151e] px-2.5 py-1 rounded border border-[#1f2433] font-mono text-xs text-white">
          <span class="text-[#34d399] font-bold">[1]</span>
          <span>Synthetic Index · dmitryefremov.com</span>
        </div>
        <span class="font-mono text-xs text-[#38bdf8]">OG Card Verified ✓</span>
      </div>
    </div>
  </section>

  <!-- SECTION 5: ACTION BUTTONS -->
  <section id="action-buttons-section" class="space-y-3 pt-2 font-mono">
    {#if simulationMessage}
      <div class="bg-[#34d399]/10 text-[#34d399] border border-[#34d399]/40 p-3 rounded text-xs text-center font-mono">
        {simulationMessage}
      </div>
    {/if}

    <button
      id="test-crawl-btn"
      type="button"
      on:click={handleSimulateCrawl}
      disabled={isSimulatingCrawl}
      class="w-full bg-[#34d399] text-[#090a0f] font-bold text-sm h-11 rounded flex items-center justify-center space-x-2 hover:bg-[#059669] transition-colors disabled:opacity-50"
    >
      <span>🚀</span>
      <span>{isSimulatingCrawl ? 'Simulating Crawler Ping...' : 'Test Crawl Ping (Simulate Bot)'}</span>
    </button>

    <button
      id="export-snippet-btn"
      type="button"
      on:click={handleExportSnippet}
      class="w-full bg-[#12151e] border border-[#1f2433] text-white font-bold text-sm h-10 rounded flex items-center justify-center space-x-2 hover:bg-[#1f2433] transition-colors"
    >
      <span>📋</span>
      <span>Export robots.txt / Meta Snippet</span>
    </button>
  </section>

  <!-- Schema Viewer Modal -->
  {#if isSchemaModalOpen}
    <div id="schema-modal" role="dialog" aria-labelledby="schema-modal-title" class="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50 font-mono text-xs">
      <div class="max-w-lg w-full p-6 bg-[#12151e] border border-[#1f2433] rounded-lg text-[#e2e8f0] space-y-4 shadow-2xl">
        <div class="flex justify-between items-center border-b border-[#1f2433] pb-3">
          <h3 id="schema-modal-title" class="text-sm font-bold text-white">[JSON-LD SCHEMA VIEW]</h3>
          <button
            id="close-schema-modal-btn"
            type="button"
            on:click={() => (isSchemaModalOpen = false)}
            class="px-2 py-1 rounded bg-[#1f2433] hover:bg-[#2e364f] text-white"
          >
            ✕
          </button>
        </div>
        <pre class="bg-[#090a0f] p-4 rounded border border-[#1f2433] overflow-x-auto text-[11px] text-[#34d399] leading-relaxed">
{JSON.stringify(seoState.jsonLd, null, 2)}
        </pre>
      </div>
    </div>
  {/if}
</div>
