<script lang="ts">
  import { onMount } from 'svelte';
  import { parseMarkdown } from '../lib/markdown';
  import { telemetry, calculateScrollPercentage, type TelemetryEvent } from '../lib/telemetry';

  export interface Article {
    id: string;
    title: string;
    topic: 'deontic' | 'toc' | 'autonomous' | 'systems';
    readTime: string;
    publishedAt: string;
    summary: string;
    contentMarkdown: string;
  }

  export const ARTICLES: Article[] = [
    {
      id: 'art-01-deontic-logic',
      title: 'Deontic Logic in Autonomous Process Architecture',
      topic: 'deontic',
      readTime: '8 min read',
      publishedAt: '2026-03-15',
      summary: 'Exploring normative modal logic (Obligation, Permission, Prohibition) as a foundational formal model for autonomous agent state space safety.',
      contentMarkdown: `# Deontic Logic in Autonomous Process Architecture

In formal software architecture, **deontic logic** provides the normative framework for explicit obligations, permissions, and prohibitions.

> "Your code and your decisions are who you truly are."
> — Manifest of Autonomous Systems

Traditional imperative systems rely on implicit constraints scattered across imperative routines, leading to drift, unhandled edge cases, and unexpected side effects under load.

## Mathematical Formulation of Normative Invariants

We represent deontic operators over transition states $S$:

$$
\mathcal{O}(\varphi) \iff \neg \mathcal{P}(\neg \varphi) \quad \text{and} \quad \mathcal{F}(\varphi) \iff \mathcal{O}(\neg \varphi)
$$

Where:
- $\mathcal{O}(\varphi)$ denotes the **Obligation** to satisfy invariant $\varphi$.
- $\mathcal{P}(\varphi)$ denotes the **Permission** to execute transition $\varphi$.
- $\mathcal{F}(\varphi)$ denotes the **Forbidden** state transition $\varphi$.

### System Invariants
- State transitions must satisfy $\mathcal{O}(\text{Invariant Compliance})$.
- Unsanitized inputs are strictly $\mathcal{F}(\text{Direct Database Mutation})$.

By establishing an explicit deontic specification, autonomous process engines can verify invariants before executing state transitions. System integrity is preserved not through defensive runtime patching, but through mathematical supervenience over state spaces.`
    },
    {
      id: 'art-02-theory-of-constraints',
      title: 'Theory of Constraints in Distributed Pipeline Throughput',
      topic: 'toc',
      readTime: '12 min read',
      publishedAt: '2026-02-28',
      summary: 'Applying Goldratt\'s Theory of Constraints (TOC) and Five Focusing Steps to software delivery loops and queue bottlenecks.',
      contentMarkdown: `# Theory of Constraints in Distributed Pipeline Throughput

Any manageable system is limited in achieving more of its goals by a very small number of constraints.

> "The capacity of the system is strictly determined by the bottleneck. Local optimization at non-bottlenecks is an illusion."

## The Five Focusing Steps for Systems Engineering

1. **Identify** the system constraint.
2. **Exploit** the system constraint (ensure 100% productive utilization).
3. **Subordinate** everything else to the above decision.
4. **Elevate** the system constraint.
5. **Prevent Inertia** from becoming the constraint.

### Flow Dynamics Formula

$$
\text{Throughput (T)} = \frac{\text{Completed Work Units}}{\text{Cycle Time}} \quad \text{subject to} \quad W_{\text{VIP}} \le \text{Queue Capacity}
$$

When applying TOC to event stream processing, buffering work before the constraint protects the system from starvation without overloading downstream workers.`
    },
    {
      id: 'art-03-autonomous-agents',
      title: 'Deterministic State Determinism in Autonomous Loops',
      topic: 'autonomous',
      readTime: '10 min read',
      publishedAt: '2026-01-14',
      summary: 'Ensuring total determinism, reproducible seeds, and zero side-effect leakage in autonomous feedback agent cycles.',
      contentMarkdown: `# Deterministic State Determinism in Autonomous Loops

Autonomous agents require strict reproducibility to operate reliably in untrusted execution environments.

> "If an execution cannot be deterministically replayed with a fixed random seed, it cannot be formally audited."

## Replayability Condition

$$
\text{State}_{t+1} = \mathcal{f}(\text{State}_t, \text{Input}_t, \text{Seed}_\sigma)
$$

All non-deterministic sources (timestamps, random number generators, network responses) must be injected via deterministic interfaces.`
    }
  ];

  export let selectedArticleId: string = ARTICLES[0].id;
  export let filterTopic: 'all' | 'deontic' | 'toc' | 'autonomous' = 'all';

  let scrollContainer: HTMLElement;
  let trackedThresholds: Set<number> = new Set();
  let currentPercentage = 0;
  let loggedEvents: TelemetryEvent[] = [];

  $: selectedArticle = ARTICLES.find(a => a.id === selectedArticleId) || ARTICLES[0];
  $: parsedContent = parseMarkdown(selectedArticle.contentMarkdown);
  $: filteredArticles = filterTopic === 'all'
    ? ARTICLES
    : ARTICLES.filter(a => a.topic === filterTopic);

  function handleArticleSelect(id: string) {
    selectedArticleId = id;
    trackedThresholds = new Set();
    currentPercentage = 0;
    if (scrollContainer) {
      scrollContainer.scrollTop = 0;
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

<div id="article-stream-container" class="space-y-6">
  <!-- Section Header & Filter Options -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1f2433] pb-4">
    <div>
      <div class="flex items-center gap-2 mb-1">
        <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
          [THOUGHT_STREAM]
        </span>
        <span class="text-xs font-mono text-[#94a3b8]">Articles: {ARTICLES.length}</span>
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

  <!-- Main Grid: Available Longreads List (Left) & Active Article Reader (Right) -->
  <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
    <!-- Available Longreads List -->
    <div id="longreads-list-section" class="md:col-span-5 space-y-3">
      <div class="text-xs font-mono text-[#94a3b8] flex justify-between items-center mb-2">
        <span>AVAILABLE_LONGREADS ({filteredArticles.length})</span>
        <span>SELECT_TO_READ</span>
      </div>

      <div class="space-y-3" role="list" aria-label="Available Longreads">
        {#each filteredArticles as article}
          <div role="listitem">
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
          </div>
        {/each}
      </div>
    </div>

    <!-- Active Article Reader View -->
    <div id="article-reader-section" class="md:col-span-7 flex flex-col">
      <div id="article-reader-root" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] h-full flex flex-col justify-between font-sans">
        <!-- Header -->
        <header id="article-header" class="border-b border-[#1f2433] pb-4 mb-4">
          <div class="flex items-center justify-between gap-2 mb-2">
            <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
              [SYS_ARTICLE]
            </span>
            <span class="text-xs font-mono text-[#94a3b8]">{selectedArticle.id}</span>
          </div>
          <h1 class="text-2xl font-bold tracking-tight text-white mb-2">{selectedArticle.title}</h1>
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
                  <span>[DEPTH_{evt.payload.depth}%] {evt.payload.articleId}</span>
                  <span class="text-[#64748b]">{evt.timestamp.slice(11, 19)}</span>
                </div>
              {/each}
            {/if}
          </div>
        </footer>
      </div>
    </div>
  </div>
</div>
