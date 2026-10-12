<script lang="ts">
  import { onMount } from 'svelte';
  import { parseMarkdown } from '../lib/markdown';
  import {
    type CanonItem,
    type CanonDomain,
    type CanonType,
    INITIAL_CANON_ITEMS,
    DOMAIN_LABELS,
    TYPE_LABELS,
    filterCanonItems,
    fetchCanonItems
  } from '../lib/canon';

  export let initialItems: CanonItem[] = INITIAL_CANON_ITEMS;
  export let selectedSlug: string = INITIAL_CANON_ITEMS[0].slug;

  let canonItems: CanonItem[] = initialItems;
  let domainFilter: CanonDomain | 'ALL' = 'ALL';
  let typeFilter: CanonType | 'ALL' = 'ALL';
  let fontSize: 'sm' | 'base' | 'lg' = 'base';
  let scrollContainer: HTMLElement;
  let scrollProgressPercentage: number = 0;

  $: filteredItems = filterCanonItems(canonItems, domainFilter, typeFilter);
  $: selectedItem = canonItems.find(i => i.slug === selectedSlug) || filteredItems[0] || canonItems[0];
  $: parsedContent = selectedItem ? parseMarkdown(selectedItem.content) : { html: '' };

  function handleSelectSlug(slug: string) {
    selectedSlug = slug;
    scrollProgressPercentage = 0;
    if (scrollContainer) {
      scrollContainer.scrollTop = 0;
    }
  }

  function handleScroll() {
    if (!scrollContainer) return;
    const { scrollTop, scrollHeight, clientHeight } = scrollContainer;
    const totalScrollable = scrollHeight - clientHeight;
    if (totalScrollable <= 0) {
      scrollProgressPercentage = 100;
    } else {
      scrollProgressPercentage = Math.min(100, Math.round((scrollTop / totalScrollable) * 100));
    }
  }

  onMount(async () => {
    try {
      const fetched = await fetchCanonItems();
      if (fetched && fetched.length > 0) {
        canonItems = fetched;
      }
    } catch (_e) {
      // Retain initial items
    }
  });
</script>

<div id="canon-reader-container" class="space-y-6">
  <!-- Section Header & Filter Options -->
  <header class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1f2433] pb-4">
    <div>
      <div class="flex items-center gap-2 mb-1">
        <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
          [TIER_1_CANON]
        </span>
        <span class="text-xs font-mono text-[#94a3b8]">Items Loaded: {filteredItems.length}</span>
      </div>
      <h2 class="text-xl font-bold tracking-tight text-white">The Canon (Evergreen Manifestos & Ontology)</h2>
    </div>

    <!-- Domain Filters -->
    <div class="flex flex-wrap gap-1.5 font-mono text-xs" role="toolbar" aria-label="Canon Domain Filters">
      <button
        type="button"
        id="domain-filter-all"
        class="px-2.5 py-1 rounded border transition-colors focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {domainFilter === 'ALL' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (domainFilter = 'ALL')}
      >
        All Domains
      </button>
      <button
        type="button"
        id="domain-filter-autonomous"
        class="px-2.5 py-1 rounded border transition-colors focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {domainFilter === 'AUTONOMOUS_ENGINEERING' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (domainFilter = 'AUTONOMOUS_ENGINEERING')}
      >
        Autonomous Eng.
      </button>
      <button
        type="button"
        id="domain-filter-economy"
        class="px-2.5 py-1 rounded border transition-colors focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {domainFilter === 'AGENTIC_ECONOMY' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (domainFilter = 'AGENTIC_ECONOMY')}
      >
        Agentic Economy (A2A)
      </button>
      <button
        type="button"
        id="domain-filter-strategy"
        class="px-2.5 py-1 rounded border transition-colors focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {domainFilter === 'PRAGMATIC_BUSINESS_STRATEGY' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (domainFilter = 'PRAGMATIC_BUSINESS_STRATEGY')}
      >
        Business Strategy
      </button>
      <button
        type="button"
        id="domain-filter-philosophy"
        class="px-2.5 py-1 rounded border transition-colors focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {domainFilter === 'PHILOSOPHY_OF_MIND' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (domainFilter = 'PHILOSOPHY_OF_MIND')}
      >
        Philosophy of Mind
      </button>
    </div>
  </header>

  <!-- Secondary Type Filter Bar & Reading Controls -->
  <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 bg-[#12151e] p-3 rounded-lg border border-[#1f2433] text-xs font-mono">
    <div class="flex items-center gap-2" role="group" aria-label="Canon Content Type Filters">
      <span class="text-[#94a3b8]">FILTER_TYPE:</span>
      <button
        type="button"
        class="px-2 py-0.5 rounded border {typeFilter === 'ALL' ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8]' : 'text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (typeFilter = 'ALL')}
      >
        All Types
      </button>
      <button
        type="button"
        class="px-2 py-0.5 rounded border {typeFilter === 'MANIFESTO' ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8]' : 'text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (typeFilter = 'MANIFESTO')}
      >
        Manifestos
      </button>
      <button
        type="button"
        class="px-2 py-0.5 rounded border {typeFilter === 'GLOSSARY_TERM' ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8]' : 'text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (typeFilter = 'GLOSSARY_TERM')}
      >
        Glossary Terms
      </button>
      <button
        type="button"
        class="px-2 py-0.5 rounded border {typeFilter === 'ONTOLOGY' ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8]' : 'text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => (typeFilter = 'ONTOLOGY')}
      >
        Ontology
      </button>
    </div>

    <!-- Editorial Font Size Adjuster -->
    <div class="flex items-center gap-2" role="group" aria-label="Reading Typography Controls">
      <span class="text-[#94a3b8]">FONT_SIZE:</span>
      <button
        type="button"
        aria-label="Set font size to small"
        class="px-2 py-0.5 rounded border {fontSize === 'sm' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#090a0f] text-[#94a3b8] border-[#1f2433]'}"
        on:click={() => (fontSize = 'sm')}
      >
        A-
      </button>
      <button
        type="button"
        aria-label="Set font size to default"
        class="px-2 py-0.5 rounded border {fontSize === 'base' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#090a0f] text-[#94a3b8] border-[#1f2433]'}"
        on:click={() => (fontSize = 'base')}
      >
        A
      </button>
      <button
        type="button"
        aria-label="Set font size to large"
        class="px-2 py-0.5 rounded border {fontSize === 'lg' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#090a0f] text-[#94a3b8] border-[#1f2433]'}"
        on:click={() => (fontSize = 'lg')}
      >
        A+
      </button>
    </div>
  </div>

  <!-- Main Grid: Available Canon Index (Left) & Active Canon Article Reader (Right) -->
  <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
    <!-- Available Canon Navigation List -->
    <section id="canon-list-section" class="md:col-span-4 space-y-3" aria-label="Canon Index Directory">
      <div class="text-xs font-mono text-[#94a3b8] flex justify-between items-center mb-2">
        <span>CANON_INDEX ({filteredItems.length})</span>
        <span>SELECT_TO_READ</span>
      </div>

      <div class="space-y-3" role="list" aria-label="Canon Items">
        {#each filteredItems as item}
          <button
            type="button"
            class="canon-card w-full p-4 rounded-lg border transition-all cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedSlug === item.slug ? 'bg-[#12151e] border-[#38bdf8] shadow-md' : 'bg-[#090a0f] border-[#1f2433] hover:border-[#38bdf8]/50 hover:bg-[#12151e]/50'}"
            on:click={() => handleSelectSlug(item.slug)}
          >
            <div class="flex flex-wrap justify-between items-center text-[10px] font-mono gap-1 mb-2">
              <span class="px-1.5 py-0.5 rounded bg-[#1f2433] text-[#38bdf8] uppercase">
                {TYPE_LABELS[item.type]}
              </span>
              <span class="text-[#64748b]">{DOMAIN_LABELS[item.domain]}</span>
            </div>
            <h3 class="font-bold text-white text-sm mb-1 hover:text-[#38bdf8] transition-colors leading-snug">
              {item.title}
            </h3>
            {#if item.summary}
              <p class="text-xs text-[#94a3b8] line-clamp-2 leading-relaxed mt-1">
                {item.summary}
              </p>
            {/if}
            <div class="mt-3 text-[10px] font-mono text-[#64748b] flex justify-between items-center">
              <span>{item.publishedAt ? item.publishedAt.slice(0, 10) : 'EVERGREEN'}</span>
              <span class="text-[#38bdf8]">{selectedSlug === item.slug ? '● READING' : 'OPEN →'}</span>
            </div>
          </button>
        {/each}
      </div>
    </section>

    <!-- Active Canon Reader Main Container -->
    <section id="canon-reader-section" class="md:col-span-8 flex flex-col" aria-label="Canon Article Content">
      {#if selectedItem}
        <article
          id="canon-reader-article"
          class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] h-full flex flex-col justify-between font-sans"
        >
          <!-- Article Header -->
          <header id="canon-header" class="border-b border-[#1f2433] pb-4 mb-4 space-y-3">
            <div class="flex items-center justify-between gap-2">
              <div class="flex items-center gap-2">
                <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
                  [{selectedItem.type}]
                </span>
                <span class="text-xs font-mono text-[#38bdf8]">
                  {DOMAIN_LABELS[selectedItem.domain]}
                </span>
              </div>
              <span class="text-xs font-mono text-[#94a3b8]">{selectedItem.slug}</span>
            </div>

            <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight text-white leading-tight">
              {selectedItem.title}
            </h1>

            <div class="flex flex-wrap items-center justify-between text-xs text-[#94a3b8] font-mono gap-2 pt-1">
              <span>Domain: {selectedItem.domain}</span>
              <span>Reading Progress: {scrollProgressPercentage}%</span>
            </div>
          </header>

          <!-- Reading Progress Bar -->
          <div class="mb-4">
            <div class="w-full bg-[#12151e] h-1.5 rounded overflow-hidden border border-[#1f2433]">
              <div
                class="bg-[#38bdf8] h-full transition-all duration-150"
                style="width: {scrollProgressPercentage}%"
              ></div>
            </div>
          </div>

          <!-- Editorial Article Body -->
          <div
            id="canon-scroll-container"
            bind:this={scrollContainer}
            on:scroll={handleScroll}
            class="max-h-[480px] overflow-y-auto pr-3 border border-[#1f2433] bg-[#12151e] p-6 rounded text-[#cbd5e1] leading-relaxed font-serif space-y-4 {fontSize === 'sm' ? 'text-xs' : fontSize === 'lg' ? 'text-base' : 'text-sm'}"
            role="region"
            aria-label="Canon article text content area"
          >
            {@html parsedContent.html}
          </div>

          <!-- Relational Cross-References Footer Section -->
          <footer id="canon-cross-references" class="mt-6 pt-4 border-t border-[#1f2433] space-y-3">
            <div class="text-xs font-mono text-[#94a3b8] flex items-center justify-between">
              <span>[RELATIONAL_CROSS_REFERENCES]</span>
              <span class="text-[#38bdf8]">CROSS_LINKING_ACTIVE</span>
            </div>

            <!-- Outgoing References -->
            {#if selectedItem.outgoingReferences && selectedItem.outgoingReferences.length > 0}
              <nav aria-label="Outgoing References" class="space-y-1.5 font-mono text-xs">
                <div class="text-[#94a3b8] text-[11px]">OUTGOING_LINKS:</div>
                <div class="flex flex-wrap gap-2">
                  {#each selectedItem.outgoingReferences as xref}
                    <button
                      type="button"
                      class="px-2.5 py-1 rounded bg-[#12151e] border border-[#1f2433] hover:border-[#38bdf8] text-[#38bdf8] hover:text-white transition-colors focus:outline-none focus:ring-2 focus:ring-[#38bdf8] flex items-center gap-1.5"
                      on:click={() => handleSelectSlug(xref.targetSlug)}
                    >
                      <span class="text-[10px] text-[#94a3b8]">[{xref.relationshipType}]</span>
                      <span>{xref.targetTitle} →</span>
                    </button>
                  {/each}
                </div>
              </nav>
            {/if}

            <!-- Incoming References -->
            {#if selectedItem.incomingReferences && selectedItem.incomingReferences.length > 0}
              <nav aria-label="Incoming References" class="space-y-1.5 font-mono text-xs">
                <div class="text-[#94a3b8] text-[11px]">INCOMING_REFERENCED_BY:</div>
                <div class="flex flex-wrap gap-2">
                  {#each selectedItem.incomingReferences as xref}
                    <button
                      type="button"
                      class="px-2.5 py-1 rounded bg-[#12151e] border border-[#1f2433] hover:border-[#38bdf8] text-[#34d399] hover:text-white transition-colors focus:outline-none focus:ring-2 focus:ring-[#38bdf8] flex items-center gap-1.5"
                      on:click={() => handleSelectSlug(xref.sourceSlug)}
                    >
                      <span class="text-[10px] text-[#94a3b8]">[{xref.relationshipType}]</span>
                      <span>← {xref.sourceTitle}</span>
                    </button>
                  {/each}
                </div>
              </nav>
            {/if}

            {#if (!selectedItem.outgoingReferences || selectedItem.outgoingReferences.length === 0) && (!selectedItem.incomingReferences || selectedItem.incomingReferences.length === 0)}
              <div class="text-xs font-mono text-[#64748b]">No active cross-references registered for this canon item.</div>
            {/if}
          </footer>
        </article>
      {/if}
    </section>
  </div>
</div>
