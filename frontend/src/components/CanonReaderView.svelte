<script lang="ts">
  import { onMount } from 'svelte';
  import { parseMarkdown } from '../lib/markdown';
  import SeoMeta from './SeoMeta.svelte';
  import {
    CANON_DOMAINS,
    CANON_TYPES,
    SEEDED_CANON_ITEMS,
    fetchCanonItems,
    fetchCanonItemBySlug,
    type CanonDomain,
    type CanonType,
    type CanonItem
  } from '../lib/canon';

  export let initialSlug: string = 'autonomy-manifesto';
  export let theme: 'dark' | 'light' = 'dark';

  let selectedDomain: 'ALL' | CanonDomain = 'ALL';
  let selectedType: 'ALL' | CanonType = 'ALL';
  let searchFilter: string = '';
  let activeItem: CanonItem = SEEDED_CANON_ITEMS[0];
  let canonItems: CanonItem[] = SEEDED_CANON_ITEMS;
  let isLoading: boolean = false;

  $: filteredItems = canonItems.filter(item => {
    const matchesDomain = selectedDomain === 'ALL' || item.domain === selectedDomain;
    const matchesType = selectedType === 'ALL' || item.type === selectedType;
    const matchesSearch = !searchFilter.trim() ||
      item.title.toLowerCase().includes(searchFilter.toLowerCase()) ||
      item.slug.toLowerCase().includes(searchFilter.toLowerCase()) ||
      (item.summary && item.summary.toLowerCase().includes(searchFilter.toLowerCase()));
    return matchesDomain && matchesType && matchesSearch;
  });

  $: parsedBody = parseMarkdown(activeItem ? activeItem.content : '');

  async function loadItems() {
    isLoading = true;
    try {
      const pageData = await fetchCanonItems(
        selectedDomain === 'ALL' ? undefined : selectedDomain,
        selectedType === 'ALL' ? undefined : selectedType
      );
      if (pageData && pageData.content) {
        canonItems = pageData.content;
      } else {
        canonItems = SEEDED_CANON_ITEMS;
      }
    } catch {
      canonItems = SEEDED_CANON_ITEMS;
    } finally {
      isLoading = false;
      if (activeItem && !canonItems.some(item => item.slug === activeItem.slug) && canonItems.length > 0) {
        activeItem = canonItems[0];
      }
    }
  }

  export async function selectItemBySlug(slug: string) {
    const foundLocal = canonItems.find(i => i.slug === slug) || SEEDED_CANON_ITEMS.find(i => i.slug === slug);
    if (foundLocal) {
      activeItem = foundLocal;
      return;
    }
    const fetched = await fetchCanonItemBySlug(slug);
    if (fetched) {
      activeItem = fetched;
    }
  }

  function handleDomainChange(domain: 'ALL' | CanonDomain) {
    selectedDomain = domain;
    loadItems();
  }

  function handleTypeChange(type: 'ALL' | CanonType) {
    selectedType = type;
    loadItems();
  }

  function getDomainLabel(domainId: CanonDomain): string {
    const d = CANON_DOMAINS.find(item => item.id === domainId);
    return d ? d.label : domainId;
  }

  function getTypeBadgeColor(type: CanonType): string {
    switch (type) {
      case 'MANIFESTO':
        return 'bg-[#38bdf8]/10 text-[#38bdf8] border-[#38bdf8]/30';
      case 'ONTOLOGY':
        return 'bg-[#a7f3d0]/10 text-[#34d399] border-[#34d399]/30';
      case 'GLOSSARY_TERM':
        return 'bg-[#fef08a]/10 text-[#facc15] border-[#facc15]/30';
      default:
        return 'bg-[#1f2433] text-[#94a3b8] border-[#1f2433]';
    }
  }

  onMount(() => {
    const initial = SEEDED_CANON_ITEMS.find(i => i.slug === initialSlug);
    if (initial) {
      activeItem = initial;
    }
    loadItems();
  });
</script>

<SeoMeta
  title="{activeItem ? activeItem.title : 'The Canon'} | Dmitry Efremov"
  description={activeItem ? (activeItem.summary || activeItem.title) : 'Evergreen manifestos, system ontologies, and glossary of autonomy.'}
  type="article"
  url="https://dmitryefremov.com/canon/{activeItem ? activeItem.slug : ''}"
  keywords={['Canon', activeItem ? activeItem.domain : 'Autonomy', 'Manifesto', 'Extended Mind']}
/>

<div id="canon-reader-root" class="space-y-6 font-sans">
  <!-- Top Section Title and Description -->
  <header id="canon-header" class="border-b pb-4 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#cbd5e1]'} space-y-2">
    <div class="flex items-center justify-between flex-wrap gap-2">
      <div class="flex items-center gap-2">
        <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2.5 py-1 rounded text-xs font-mono font-medium">
          [TIER_1_CANON]
        </span>
        <span class="text-xs font-mono {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
          Evergreen Manifestos, Ontology & Glossary across 4 Domains
        </span>
      </div>
      <span class="text-xs font-mono px-2 py-0.5 rounded bg-[#38bdf8]/10 text-[#38bdf8]">
        WCAG 2.1 AA Compliant
      </span>
    </div>
    <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
      The Canon (Канон Автономии)
    </h1>
    <p class="text-sm font-serif {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#334155]'} max-w-3xl leading-relaxed">
      Суверенный свод вечнозелёных манифестов, онтологии системной логики и терминологического словаря расширенного разума.
    </p>
  </header>

  <!-- Domain Navigation Tabs -->
  <nav id="canon-domain-nav" aria-label="Canon Practical Domains Navigation" class="space-y-2">
    <div class="text-xs font-mono {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
      PRACTICAL DOMAINS (ПРАКТИЧЕСКИЕ ДОМЕНЫ):
    </div>
    <div class="flex flex-wrap gap-2" role="tablist">
      <button
        id="domain-tab-all"
        type="button"
        role="tab"
        aria-selected={selectedDomain === 'ALL'}
        class="px-3 py-1.5 rounded-lg text-xs font-mono transition-all border cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'ALL' ? (theme === 'dark' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#0284c7] text-white font-bold border-[#0284c7]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-white text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
        on:click={() => handleDomainChange('ALL')}
      >
        ALL DOMAINS
      </button>
      {#each CANON_DOMAINS as dom}
        <button
          id="domain-tab-{dom.id.toLowerCase()}"
          type="button"
          role="tab"
          aria-selected={selectedDomain === dom.id}
          class="px-3 py-1.5 rounded-lg text-xs font-mono transition-all border cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === dom.id ? (theme === 'dark' ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#0284c7] text-white font-bold border-[#0284c7]') : (theme === 'dark' ? 'bg-[#12151e] text-[#94a3b8] border-[#1f2433] hover:text-white' : 'bg-white text-[#64748b] border-[#cbd5e1] hover:text-[#0f172a]')}"
          on:click={() => handleDomainChange(dom.id)}
        >
          {dom.label}
        </button>
      {/each}
    </div>
  </nav>

  <!-- Filter Controls: Content Type Chips & Search -->
  <section id="canon-filters-section" class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 p-3 rounded-xl border {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#e2e8f0]'}">
    <!-- Type Filter Chips -->
    <div class="flex flex-wrap items-center gap-2" role="group" aria-label="Canon Content Type Filter">
      <span class="text-xs font-mono {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">TYPE:</span>
      <button
        id="type-filter-all"
        type="button"
        class="px-2.5 py-1 rounded text-xs font-mono border transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedType === 'ALL' ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8] font-bold' : 'bg-transparent text-[#94a3b8] border-[#1f2433] hover:text-white'}"
        on:click={() => handleTypeChange('ALL')}
      >
        ALL TYPES
      </button>
      {#each CANON_TYPES as ct}
        <button
          id="type-filter-{ct.id.toLowerCase()}"
          type="button"
          class="px-2.5 py-1 rounded text-xs font-mono border transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedType === ct.id ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8] font-bold' : 'bg-transparent text-[#94a3b8] border-[#1f2433] hover:text-white'}"
          on:click={() => handleTypeChange(ct.id)}
        >
          {ct.label}
        </button>
      {/each}
    </div>

    <!-- Search Input -->
    <div class="flex items-center gap-2">
      <label for="canon-search-input" class="sr-only">Search Canon Items</label>
      <input
        id="canon-search-input"
        type="search"
        placeholder="Search manifestos & terms..."
        bind:value={searchFilter}
        class="px-3 py-1.5 rounded-lg text-xs font-mono border w-full sm:w-64 focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {theme === 'dark' ? 'bg-[#12151e] text-white border-[#1f2433] placeholder-[#64748b]' : 'bg-white text-[#0f172a] border-[#cbd5e1] placeholder-[#94a3b8]'}"
      />
    </div>
  </section>

  <!-- Main Grid: Catalog Navigation List (Left) & Active Canon Reader (Right) -->
  <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
    <!-- Canon Items List -->
    <aside id="canon-catalog-aside" class="md:col-span-4 space-y-3">
      <div class="flex items-center justify-between text-xs font-mono {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
        <span>CANON INDEX ({filteredItems.length})</span>
        {#if isLoading}
          <span class="text-[#38bdf8] animate-pulse">LOADING...</span>
        {/if}
      </div>

      <div id="canon-item-list" class="space-y-2.5 max-h-[600px] overflow-y-auto pr-1" role="list" aria-label="Canon Items Index">
        {#if filteredItems.length === 0}
          <div class="p-4 rounded-lg border border-dashed text-center font-mono text-xs {theme === 'dark' ? 'border-[#1f2433] text-[#64748b]' : 'border-[#cbd5e1] text-[#94a3b8]'}">
            No items found matching selected filters.
          </div>
        {:else}
          {#each filteredItems as item}
            <button
              id="canon-item-btn-{item.slug}"
              type="button"
              class="w-full text-left p-3.5 rounded-xl border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {activeItem.slug === item.slug ? (theme === 'dark' ? 'bg-[#12151e] border-[#38bdf8] shadow-lg shadow-[#38bdf8]/5' : 'bg-[#f0f9ff] border-[#0284c7] shadow-sm') : (theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433] hover:border-[#38bdf8]/50 hover:bg-[#12151e]' : 'bg-white border-[#e2e8f0] hover:border-[#0284c7]/50')}"
              on:click={() => selectItemBySlug(item.slug)}
            >
              <div class="flex items-center justify-between gap-2 mb-1.5 text-[11px] font-mono">
                <span class="px-2 py-0.5 rounded border font-semibold {getTypeBadgeColor(item.type)}">
                  {item.type}
                </span>
                <span class="text-[#64748b] text-[10px] truncate max-w-[120px]">{item.domain}</span>
              </div>

              <h2 class="font-bold text-sm mb-1 leading-snug {activeItem.slug === item.slug ? (theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#0284c7]') : (theme === 'dark' ? 'text-white' : 'text-[#0f172a]')}">
                {item.title}
              </h2>

              {#if item.summary}
                <p class="text-xs line-clamp-2 leading-relaxed {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
                  {item.summary}
                </p>
              {/if}

              <div class="mt-2.5 pt-2 border-t flex items-center justify-between text-[10px] font-mono {theme === 'dark' ? 'border-[#1f2433] text-[#64748b]' : 'border-[#e2e8f0] text-[#94a3b8]'}">
                <span>{item.slug}</span>
                <span class="{activeItem.slug === item.slug ? 'text-[#38bdf8] font-bold' : ''}">
                  {activeItem.slug === item.slug ? 'READING →' : 'SELECT'}
                </span>
              </div>
            </button>
          {/each}
        {/if}
      </div>
    </aside>

    <!-- Active Canon Item Reader -->
    <main id="canon-reader-article-container" class="md:col-span-8">
      <article
        id="canon-reader-article"
        class="p-6 sm:p-8 rounded-2xl border flex flex-col justify-between h-full space-y-6 {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433] text-[#e2e8f0] shadow-xl' : 'bg-white border-[#e2e8f0] text-[#0f172a] shadow-sm'}"
      >
        <!-- Article Header -->
        <header id="canon-article-header" class="border-b pb-5 space-y-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          <div class="flex flex-wrap items-center justify-between gap-2 font-mono text-xs">
            <div class="flex items-center gap-2">
              <span class="px-2.5 py-1 rounded border font-bold {getTypeBadgeColor(activeItem.type)}">
                {activeItem.type}
              </span>
              <span class="px-2.5 py-1 rounded bg-[#12151e] text-[#38bdf8] border border-[#1f2433]">
                {getDomainLabel(activeItem.domain)}
              </span>
            </div>
            <span class="text-[#64748b]">SLUG: {activeItem.slug}</span>
          </div>

          <h1 id="canon-article-title" class="text-2xl sm:text-3xl font-extrabold tracking-tight leading-snug {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            {activeItem.title}
          </h1>

          {#if activeItem.summary}
            <p id="canon-article-summary" class="text-base italic leading-relaxed {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#475569]'}">
              {activeItem.summary}
            </p>
          {/if}

          <div class="flex items-center justify-between text-xs font-mono text-[#64748b] pt-1">
            <span>Author: Dmitry Efremov</span>
            {#if activeItem.publishedAt}
              <span>Published: {activeItem.publishedAt.slice(0, 10)}</span>
            {/if}
          </div>
        </header>

        <!-- Article Body Content -->
        <div
          id="canon-article-content"
          class="prose max-w-none text-base leading-relaxed font-serif space-y-4 {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#334155]'}"
          role="region"
          aria-label="Canon Manifesto Reading Area"
        >
          {@html parsedBody.html}
        </div>

        <!-- Cross-Reference Navigation Section (Relational Deep Links) -->
        <footer id="canon-references-footer" class="pt-6 border-t space-y-4 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          <div class="flex items-center justify-between font-mono text-xs text-[#38bdf8]">
            <span class="font-bold">[RELATIONAL_CROSS_REFERENCES]</span>
            <span class="text-[#64748b]">Connected Canon Nodes</span>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <!-- Outgoing References -->
            <div id="outgoing-references-block" class="space-y-2 p-3.5 rounded-xl border {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
              <div class="text-xs font-mono font-bold text-[#38bdf8] flex items-center justify-between">
                <span>OUTGOING REFERENCES ({activeItem.outgoingReferences.length})</span>
                <span>→</span>
              </div>
              {#if activeItem.outgoingReferences.length === 0}
                <div class="text-xs font-mono text-[#64748b] italic">No outgoing references.</div>
              {:else}
                <div class="space-y-2">
                  {#each activeItem.outgoingReferences as ref}
                    <button
                      id="xref-out-{ref.targetSlug}"
                      type="button"
                      class="w-full text-left p-2 rounded-lg border border-[#1f2433] bg-[#0f1118] hover:border-[#38bdf8] transition-colors flex flex-col gap-1 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
                      on:click={() => selectItemBySlug(ref.targetSlug)}
                    >
                      <div class="flex items-center justify-between text-[10px] font-mono">
                        <span class="text-[#38bdf8] font-semibold">{ref.relationshipType}</span>
                        <span class="text-[#64748b]">slug: {ref.targetSlug}</span>
                      </div>
                      <div class="text-xs font-bold text-white hover:text-[#38bdf8]">
                        {ref.targetTitle}
                      </div>
                      {#if ref.description}
                        <p class="text-[11px] text-[#94a3b8]">{ref.description}</p>
                      {/if}
                    </button>
                  {/each}
                </div>
              {/if}
            </div>

            <!-- Incoming References -->
            <div id="incoming-references-block" class="space-y-2 p-3.5 rounded-xl border {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
              <div class="text-xs font-mono font-bold text-[#34d399] flex items-center justify-between">
                <span>INCOMING REFERENCES ({activeItem.incomingReferences.length})</span>
                <span>←</span>
              </div>
              {#if activeItem.incomingReferences.length === 0}
                <div class="text-xs font-mono text-[#64748b] italic">No incoming references.</div>
              {:else}
                <div class="space-y-2">
                  {#each activeItem.incomingReferences as ref}
                    <button
                      id="xref-in-{ref.sourceSlug}"
                      type="button"
                      class="w-full text-left p-2 rounded-lg border border-[#1f2433] bg-[#0f1118] hover:border-[#34d399] transition-colors flex flex-col gap-1 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#34d399]"
                      on:click={() => selectItemBySlug(ref.sourceSlug)}
                    >
                      <div class="flex items-center justify-between text-[10px] font-mono">
                        <span class="text-[#34d399] font-semibold">{ref.relationshipType}</span>
                        <span class="text-[#64748b]">slug: {ref.sourceSlug}</span>
                      </div>
                      <div class="text-xs font-bold text-white hover:text-[#34d399]">
                        {ref.sourceTitle}
                      </div>
                      {#if ref.description}
                        <p class="text-[11px] text-[#94a3b8]">{ref.description}</p>
                      {/if}
                    </button>
                  {/each}
                </div>
              {/if}
            </div>
          </div>
        </footer>
      </article>
    </main>
  </div>
</div>
