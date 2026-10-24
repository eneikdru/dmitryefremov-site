<script lang="ts">
  import { onMount } from 'svelte';
  import {
    type CanonItem,
    type CanonDomain,
    type CanonType,
    DOMAIN_LABELS,
    TYPE_LABELS,
    DEFAULT_CANON_ITEMS,
    fetchCanonItems,
    filterLocalCanonItems
  } from '../lib/canon';
  import { parseMarkdown } from '../lib/markdown';
  import SeoMeta from './SeoMeta.svelte';

  export let theme: 'dark' | 'light' = 'dark';

  let canonItems: CanonItem[] = DEFAULT_CANON_ITEMS;
  let selectedDomain: CanonDomain | 'ALL' = 'ALL';
  let selectedType: CanonType | 'ALL' = 'ALL';
  let activeSlug: string = DEFAULT_CANON_ITEMS[0].slug;

  $: filteredItems = canonItems.filter((item) => {
    if (selectedDomain !== 'ALL' && item.domain !== selectedDomain) return false;
    if (selectedType !== 'ALL' && item.type !== selectedType) return false;
    return true;
  });

  $: activeItem = canonItems.find((i) => i.slug === activeSlug) || filteredItems[0] || DEFAULT_CANON_ITEMS[0];

  $: parsedContent = parseMarkdown(activeItem ? activeItem.content : '');

  function selectItem(slug: string) {
    activeSlug = slug;
  }

  function setDomainFilter(domain: CanonDomain | 'ALL') {
    selectedDomain = domain;
    const available = canonItems.filter((item) => {
      if (domain !== 'ALL' && item.domain !== domain) return false;
      if (selectedType !== 'ALL' && item.type !== selectedType) return false;
      return true;
    });
    if (available.length > 0 && !available.some((i) => i.slug === activeSlug)) {
      activeSlug = available[0].slug;
    }
  }

  function setTypeFilter(type: CanonType | 'ALL') {
    selectedType = type;
    const available = canonItems.filter((item) => {
      if (selectedDomain !== 'ALL' && item.domain !== selectedDomain) return false;
      if (type !== 'ALL' && item.type !== type) return false;
      return true;
    });
    if (available.length > 0 && !available.some((i) => i.slug === activeSlug)) {
      activeSlug = available[0].slug;
    }
  }

  onMount(async () => {
    const fetched = await fetchCanonItems();
    if (fetched && fetched.length > 0) {
      canonItems = fetched;
      if (!canonItems.some((i) => i.slug === activeSlug)) {
        activeSlug = canonItems[0].slug;
      }
    }
  });
</script>

<SeoMeta
  title="{activeItem ? activeItem.title : 'Tier 1: The Canon'} | Dmitry Efremov"
  description={activeItem?.summary || 'Evergreen manifestos, ontology, and glossary of autonomy across 4 practical domains.'}
  type="article"
  url="https://dmitryefremov.com/canon/{activeItem?.slug || ''}"
  keywords={['Canon', 'Autonomous Engineering', 'Agentic Economy', 'Deontic Logic', 'Philosophy of Mind']}
/>

<div id="canon-view-root" class="space-y-6">
  <!-- Section Header & Overview -->
  <header class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b pb-4 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
    <div>
      <div class="flex items-center gap-2 mb-1">
        <span class="px-2 py-0.5 rounded text-xs font-mono font-medium tracking-wide {theme === 'dark' ? 'bg-[#12151e] text-[#38bdf8] border border-[#1f2433]' : 'bg-[#f1f5f9] text-[#0284c7] border border-[#cbd5e1]'}">
          [TIER_1_CANON]
        </span>
        <span class="text-xs font-mono {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
          Evergreen Manifestos & Ontology
        </span>
      </div>
      <h2 class="text-2xl font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
        The Canon: Autonomous Systems & Extended Mind
      </h2>
    </div>
  </header>

  <!-- Filter Toolbar: 4 Domains & Types -->
  <section aria-label="Canon Category and Domain Filters" class="space-y-3 p-4 rounded-xl border font-mono text-xs {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
    <!-- Domain Filters -->
    <div class="space-y-1.5">
      <span class="block text-[11px] uppercase tracking-wider font-bold {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#0284c7]'}">
        Practical Domains:
      </span>
      <div class="flex flex-wrap gap-1.5" role="toolbar" aria-label="Domain selection filter">
        <button
          id="canon-filter-domain-all"
          type="button"
          aria-pressed={selectedDomain === 'ALL'}
          class="px-3 py-1.5 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'ALL' ? (theme === 'dark' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#0284c7] text-white font-bold border-[#0284c7]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setDomainFilter('ALL')}
        >
          All Domains
        </button>
        <button
          id="canon-filter-domain-auto-eng"
          type="button"
          aria-pressed={selectedDomain === 'AUTONOMOUS_ENGINEERING'}
          class="px-3 py-1.5 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'AUTONOMOUS_ENGINEERING' ? (theme === 'dark' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#0284c7] text-white font-bold border-[#0284c7]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setDomainFilter('AUTONOMOUS_ENGINEERING')}
        >
          Autonomous Engineering
        </button>
        <button
          id="canon-filter-domain-agentic-econ"
          type="button"
          aria-pressed={selectedDomain === 'AGENTIC_ECONOMY'}
          class="px-3 py-1.5 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'AGENTIC_ECONOMY' ? (theme === 'dark' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#0284c7] text-white font-bold border-[#0284c7]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setDomainFilter('AGENTIC_ECONOMY')}
        >
          Agentic Economy (A2A)
        </button>
        <button
          id="canon-filter-domain-biz-strat"
          type="button"
          aria-pressed={selectedDomain === 'PRAGMATIC_BUSINESS_STRATEGY'}
          class="px-3 py-1.5 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'PRAGMATIC_BUSINESS_STRATEGY' ? (theme === 'dark' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#0284c7] text-white font-bold border-[#0284c7]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setDomainFilter('PRAGMATIC_BUSINESS_STRATEGY')}
        >
          Pragmatic Business Strategy
        </button>
        <button
          id="canon-filter-domain-phil-mind"
          type="button"
          aria-pressed={selectedDomain === 'PHILOSOPHY_OF_MIND'}
          class="px-3 py-1.5 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'PHILOSOPHY_OF_MIND' ? (theme === 'dark' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#0284c7] text-white font-bold border-[#0284c7]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setDomainFilter('PHILOSOPHY_OF_MIND')}
        >
          Philosophy of Mind
        </button>
      </div>
    </div>

    <!-- Item Type Filters -->
    <div class="space-y-1.5 pt-2 border-t {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
      <span class="block text-[11px] uppercase tracking-wider font-bold {theme === 'dark' ? 'text-[#34d399]' : 'text-[#059669]'}">
        Content Classification:
      </span>
      <div class="flex flex-wrap gap-1.5" role="toolbar" aria-label="Type selection filter">
        <button
          id="canon-filter-type-all"
          type="button"
          aria-pressed={selectedType === 'ALL'}
          class="px-2.5 py-1 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#34d399] {selectedType === 'ALL' ? (theme === 'dark' ? 'bg-[#34d399] text-[#090a0f] font-bold border-[#34d399]' : 'bg-[#059669] text-white font-bold border-[#059669]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setTypeFilter('ALL')}
        >
          All Types
        </button>
        <button
          id="canon-filter-type-manifesto"
          type="button"
          aria-pressed={selectedType === 'MANIFESTO'}
          class="px-2.5 py-1 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#34d399] {selectedType === 'MANIFESTO' ? (theme === 'dark' ? 'bg-[#34d399] text-[#090a0f] font-bold border-[#34d399]' : 'bg-[#059669] text-white font-bold border-[#059669]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setTypeFilter('MANIFESTO')}
        >
          Manifesto
        </button>
        <button
          id="canon-filter-type-glossary"
          type="button"
          aria-pressed={selectedType === 'GLOSSARY_TERM'}
          class="px-2.5 py-1 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#34d399] {selectedType === 'GLOSSARY_TERM' ? (theme === 'dark' ? 'bg-[#34d399] text-[#090a0f] font-bold border-[#34d399]' : 'bg-[#059669] text-white font-bold border-[#059669]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setTypeFilter('GLOSSARY_TERM')}
        >
          Glossary Term
        </button>
        <button
          id="canon-filter-type-ontology"
          type="button"
          aria-pressed={selectedType === 'ONTOLOGY'}
          class="px-2.5 py-1 rounded border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#34d399] {selectedType === 'ONTOLOGY' ? (theme === 'dark' ? 'bg-[#34d399] text-[#090a0f] font-bold border-[#34d399]' : 'bg-[#059669] text-white font-bold border-[#059669]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-[#f8fafc] text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => setTypeFilter('ONTOLOGY')}
        >
          Ontology
        </button>
      </div>
    </div>
  </section>

  <!-- Main Grid: Canon Items Navigation List (Left) & Canon Reader View (Right) -->
  <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
    <!-- Left List Navigation -->
    <nav id="canon-navigation-list" aria-label="Canon Manifestos and Terms List" class="md:col-span-4 space-y-3">
      <div class="flex justify-between items-center text-xs font-mono {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
        <span>CANON_INDEX ({filteredItems.length})</span>
        <span>SELECT_TO_READ</span>
      </div>

      <div class="space-y-2.5" role="list" aria-label="Available Canon items">
        {#if filteredItems.length === 0}
          <div class="p-4 rounded-lg border text-xs font-mono text-center {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] text-[#64748b]' : 'bg-white border-[#e2e8f0] text-[#94a3b8]'}">
            No items found matching the selected filter criteria.
          </div>
        {:else}
          {#each filteredItems as item (item.slug)}
            <button
              id="canon-item-{item.slug}"
              type="button"
              class="w-full p-3.5 rounded-lg border text-left transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {activeSlug === item.slug ? (theme === 'dark' ? 'bg-[#12151e] border-[#38bdf8] shadow-md' : 'bg-white border-[#0284c7] shadow-md') : (theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433] hover:border-[#38bdf8]/50 hover:bg-[#12151e]/50' : 'bg-[#f8fafc] border-[#e2e8f0] hover:border-[#0284c7]/50 hover:bg-white')}"
              on:click={() => selectItem(item.slug)}
            >
              <div class="flex justify-between items-center text-[10px] font-mono mb-1.5">
                <span class="px-1.5 py-0.5 rounded font-medium {theme === 'dark' ? 'bg-[#1f2433] text-[#38bdf8]' : 'bg-[#e2e8f0] text-[#0284c7]'}">
                  {TYPE_LABELS[item.type]}
                </span>
                <span class="{theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">
                  {DOMAIN_LABELS[item.domain].split(' ')[0]}
                </span>
              </div>
              <h3 class="font-bold text-sm leading-snug mb-1 {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                {item.title}
              </h3>
              {#if item.summary}
                <p class="text-xs line-clamp-2 leading-relaxed {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
                  {item.summary}
                </p>
              {/if}
            </button>
          {/each}
        {/if}
      </div>
    </nav>

    <!-- Right Content Reader Area -->
    <main id="canon-reader-main" class="md:col-span-8">
      {#if activeItem}
        <article id="canon-reader-article" class="p-6 rounded-xl border font-sans space-y-6 {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
          <!-- Header info -->
          <header class="border-b pb-5 space-y-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
            <div class="flex flex-wrap items-center justify-between gap-2 text-xs font-mono">
              <span class="px-2.5 py-1 rounded font-bold {theme === 'dark' ? 'bg-[#12151e] text-[#38bdf8] border border-[#1f2433]' : 'bg-[#f1f5f9] text-[#0284c7] border border-[#cbd5e1]'}">
                {TYPE_LABELS[activeItem.type]}
              </span>
              <span class="px-2.5 py-1 rounded font-medium {theme === 'dark' ? 'bg-[#12151e] text-[#34d399] border border-[#1f2433]' : 'bg-[#f1f5f9] text-[#059669] border border-[#cbd5e1]'}">
                {DOMAIN_LABELS[activeItem.domain]}
              </span>
            </div>

            <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
              {activeItem.title}
            </h1>

            {#if activeItem.summary}
              <p class="text-sm sm:text-base leading-relaxed italic {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#334155]'}">
                {activeItem.summary}
              </p>
            {/if}
          </header>

          <!-- Rendered Markdown Content -->
          <div
            id="canon-content-body"
            class="p-5 rounded-lg border font-serif text-sm sm:text-base leading-relaxed space-y-4 max-h-[480px] overflow-y-auto {theme === 'dark' ? 'bg-[#141824] border-[#1f2433] text-[#e2e8f0]' : 'bg-[#f8fafc] border-[#cbd5e1] text-[#1e293b]'}"
            role="region"
            aria-label="Canon manifesto full text content"
          >
            {@html parsedContent.html}
          </div>

          <!-- Relational Cross-References Navigation -->
          {#if (activeItem.outgoingReferences && activeItem.outgoingReferences.length > 0) || (activeItem.incomingReferences && activeItem.incomingReferences.length > 0)}
            <aside id="canon-cross-references" aria-label="Relational Cross-References" class="pt-4 border-t space-y-4 font-mono text-xs {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
              <h3 class="text-xs font-bold uppercase tracking-wider flex items-center gap-2 {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#0284c7]'}">
                <span>🔗</span>
                <span>Relational Cross-References</span>
              </h3>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <!-- Outgoing References -->
                {#if activeItem.outgoingReferences && activeItem.outgoingReferences.length > 0}
                  <div class="space-y-2">
                    <span class="block text-[11px] font-semibold {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
                      Outgoing References:
                    </span>
                    {#each activeItem.outgoingReferences as ref}
                      <button
                        type="button"
                        class="w-full p-2.5 rounded border text-left transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] flex flex-col gap-1 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] hover:border-[#38bdf8]' : 'bg-[#f1f5f9] border-[#cbd5e1] hover:border-[#0284c7]'}"
                        on:click={() => selectItem(ref.targetSlug)}
                      >
                        <div class="flex justify-between items-center text-[10px]">
                          <span class="px-1.5 py-0.5 rounded font-bold {theme === 'dark' ? 'bg-[#1f2433] text-[#38bdf8]' : 'bg-white text-[#0284c7]'}">
                            [{ref.relationshipType}]
                          </span>
                          <span class="text-[#38bdf8]">NAVIGATE →</span>
                        </div>
                        <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                          {ref.targetTitle || ref.targetSlug}
                        </span>
                        {#if ref.description}
                          <span class="text-[10px] italic {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
                            {ref.description}
                          </span>
                        {/if}
                      </button>
                    {/each}
                  </div>
                {/if}

                <!-- Incoming References -->
                {#if activeItem.incomingReferences && activeItem.incomingReferences.length > 0}
                  <div class="space-y-2">
                    <span class="block text-[11px] font-semibold {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
                      Incoming Referenced By:
                    </span>
                    {#each activeItem.incomingReferences as ref}
                      <button
                        type="button"
                        class="w-full p-2.5 rounded border text-left transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] flex flex-col gap-1 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] hover:border-[#38bdf8]' : 'bg-[#f1f5f9] border-[#cbd5e1] hover:border-[#0284c7]'}"
                        on:click={() => selectItem(ref.sourceSlug)}
                      >
                        <div class="flex justify-between items-center text-[10px]">
                          <span class="px-1.5 py-0.5 rounded font-bold {theme === 'dark' ? 'bg-[#1f2433] text-[#34d399]' : 'bg-white text-[#059669]'}">
                            [{ref.relationshipType}]
                          </span>
                          <span class="text-[#34d399]">NAVIGATE →</span>
                        </div>
                        <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                          {ref.sourceTitle || ref.sourceSlug}
                        </span>
                        {#if ref.description}
                          <span class="text-[10px] italic {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
                            {ref.description}
                          </span>
                        {/if}
                      </button>
                    {/each}
                  </div>
                {/if}
              </div>
            </aside>
          {/if}
        </article>
      {/if}
    </main>
  </div>
</div>
