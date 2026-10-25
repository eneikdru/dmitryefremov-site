<script lang="ts">
  import { telemetry } from '../lib/telemetry';

  export let theme: 'dark' | 'light' = 'dark';

  export interface Manifesto {
    id: string;
    year: number;
    badge: string;
    title: string;
    author: string;
    institution: string;
    domain: string;
    tags: string[];
    quote?: string;
    summary?: string;
    readTimeMinutes: number;
    vectorCount: number;
    bookmarked?: boolean;
    relationalNodes?: string[];
  }

  const INITIAL_MANIFESTOS: Manifesto[] = [
    {
      id: 'cyborg-manifesto',
      year: 1985,
      badge: 'CLASSICAL TREATISE',
      title: 'A Cyborg Manifesto',
      author: 'Donna Haraway',
      institution: 'University of California, Santa Cruz',
      domain: 'Critical Theory',
      tags: ['#Posthumanism', '#Cyberfeminism', '#Techno-Mythology'],
      quote: '"The cyborg is a creature in a post-gender world; it has no truck with bisexuality, pre-oedipal symbiosis, unalienated labour, or other seductions to organic wholeness..."',
      readTimeMinutes: 14,
      vectorCount: 8,
      bookmarked: true
    },
    {
      id: 'solarpunk-manifesto',
      year: 2019,
      badge: 'EMERGENT DISCOURSE',
      title: 'The Solarpunk Manifesto',
      author: 'The Solarpunk Community',
      institution: 'Open Collaborative Framework',
      domain: 'Ecological Futures',
      tags: ['#Solarpunk', '#Eco-Futurism', '#OpenSource'],
      summary: 'A declaration advocating for a revolt of hope against ecological climate despair, synthesizing renewable energy systems, localized permaculture, and commons-based digital tooling.',
      readTimeMinutes: 9,
      vectorCount: 5,
      bookmarked: false,
      relationalNodes: ['Regenerative Energy', 'Decentralized Tech']
    },
    {
      id: 'cyberspace-declaration',
      year: 1996,
      badge: 'NET HERITAGE',
      title: 'A Declaration of the Independence of Cyberspace',
      author: 'John Perry Barlow',
      institution: 'Electronic Frontier Foundation (EFF)',
      domain: 'Algorithmic Sovereignty',
      tags: ['#DigitalCommons', '#Crypto-Anarchism', '#Sovereignty'],
      quote: '"Governments of the Industrial World, you weary giants of flesh and steel, I come from Cyberspace, the new home of Mind..."',
      readTimeMinutes: 6,
      vectorCount: 11,
      bookmarked: false
    },
    {
      id: 'post-digital-condition',
      year: 2021,
      badge: 'POST-DIGITAL ESSAY',
      title: 'On the Post-Digital Condition & Algorithmic Agency',
      author: 'Dmitry Efremov',
      institution: 'Epistemic Network Lab',
      domain: 'Post-Digital',
      tags: ['#PostDigital', '#A2AEconomy', '#DeonticLogic'],
      quote: '"When digital interfaces become living cognitive extensions, sovereign human agency depends on transparent machine-readable manifests."',
      readTimeMinutes: 11,
      vectorCount: 12,
      bookmarked: true
    }
  ];

  const DOMAINS = [
    { id: 'all', label: 'All Domains' },
    { id: 'Post-Digital', label: 'Post-Digital' },
    { id: 'Ecological Futures', label: 'Ecological Futures' },
    { id: 'Critical Theory', label: 'Critical Theory' },
    { id: 'Algorithmic Sovereignty', label: 'Algorithmic Sovereignty' }
  ];

  const PROBES = [
    { label: 'Donna Haraway', query: 'Donna Haraway', colorClass: 'bg-primary-container' },
    { label: 'Accelerationism', query: 'Accelerationism', colorClass: 'bg-secondary' },
    { label: 'Solarpunk Core', query: 'Solarpunk', colorClass: 'bg-tertiary-fixed-dim' }
  ];

  let searchQuery: string = '';
  let activeDomain: string = 'all';
  let viewMode: '2d' | 'list' = '2d';
  let isExpandedConstellation: boolean = false;
  let bookmarkedIds: Set<string> = new Set(['cyborg-manifesto', 'post-digital-condition']);

  $: filteredManifestos = INITIAL_MANIFESTOS.filter(m => {
    const query = searchQuery.trim().toLowerCase();
    const matchesQuery = query === '' ||
      m.title.toLowerCase().includes(query) ||
      m.author.toLowerCase().includes(query) ||
      m.institution.toLowerCase().includes(query) ||
      m.domain.toLowerCase().includes(query) ||
      m.tags.some(t => t.toLowerCase().includes(query)) ||
      (m.quote && m.quote.toLowerCase().includes(query)) ||
      (m.summary && m.summary.toLowerCase().includes(query));

    const matchesDomain = activeDomain === 'all' || m.domain === activeDomain;

    return matchesQuery && matchesDomain;
  });

  $: domainCounts = DOMAINS.reduce((acc, domain) => {
    if (domain.id === 'all') {
      acc[domain.id] = INITIAL_MANIFESTOS.length;
    } else {
      acc[domain.id] = INITIAL_MANIFESTOS.filter(m => m.domain === domain.id).length;
    }
    return acc;
  }, {} as Record<string, number>);

  function handleSearchInput() {
    if (searchQuery.trim().length > 1) {
      telemetry.trackSearchUtilization(searchQuery, 'conceptual_navigator', filteredManifestos.length);
    }
  }

  function applyProbe(probeQuery: string) {
    searchQuery = probeQuery;
    handleSearchInput();
  }

  function selectDomain(domainId: string) {
    activeDomain = domainId;
  }

  function resetFilters() {
    searchQuery = '';
    activeDomain = 'all';
  }

  function toggleBookmark(id: string) {
    if (bookmarkedIds.has(id)) {
      bookmarkedIds.delete(id);
    } else {
      bookmarkedIds.add(id);
    }
    bookmarkedIds = new Set(bookmarkedIds);
  }
</script>

<div
  id="conceptual-navigator-root"
  class="space-y-6 transition-colors duration-200 {theme === 'dark' ? 'text-[#e1e2ec]' : 'text-[#10131a]'}"
>
  <!-- Section Header -->
  <header id="conceptual-navigator-header" class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-[#1d1f27] border border-[#3b494b]/40 rounded-xl p-4 shadow-md">
    <div class="flex items-center gap-3">
      <div class="p-2.5 rounded-lg bg-[#00f0ff]/10 border border-[#00f0ff]/30 text-[#00f0ff]">
        <span class="material-symbols-outlined text-2xl">travel_explore</span>
      </div>
      <div>
        <h1 id="navigator-title" class="text-xl font-bold tracking-tight text-[#dbfcff]">
          Conceptual Navigator & Epistemic Archive
        </h1>
        <p class="text-xs font-mono text-[#b9cacb] mt-0.5">
          Интерактивный поиск манифестов, исследование категорий и констелляция понятий
        </p>
      </div>
    </div>

    <div class="flex items-center gap-2">
      <span class="px-3 py-1 rounded-full text-xs font-mono bg-[#10131a] border border-[#3b494b]/40 text-[#7df4ff]">
        {filteredManifestos.length} {filteredManifestos.length === 1 ? 'manifesto' : 'manifestos'} visible
      </span>
    </div>
  </header>

  <!-- Omni-Search Bar & Probe Suggestions -->
  <section id="omni-search-section" class="space-y-2">
    <div class="relative group">
      <div class="absolute -inset-0.5 bg-gradient-to-r from-[#00f0ff]/30 to-[#a855f7]/30 rounded-xl blur-sm opacity-50 group-focus-within:opacity-100 transition duration-300"></div>
      <div class="relative flex items-center bg-[#191b23] border border-[#3b494b]/60 rounded-xl px-4 py-3 shadow-inner transition-colors duration-200 group-focus-within:border-[#00f0ff]/80">
        <span class="material-symbols-outlined text-[#00f0ff] text-[22px] mr-3">travel_explore</span>
        <input
          id="omni-search-input"
          type="text"
          bind:value={searchQuery}
          on:input={handleSearchInput}
          placeholder="Search concepts, authors, theses..."
          class="bg-transparent border-0 p-0 text-[#e1e2ec] placeholder:text-[#849495] font-sans text-base focus:ring-0 focus:outline-none w-full"
        />
        <div class="flex items-center gap-2 ml-2 shrink-0">
          {#if searchQuery}
            <button
              id="clear-search-btn"
              type="button"
              on:click={() => searchQuery = ''}
              aria-label="Clear search"
              class="text-[#849495] hover:text-[#00f0ff] p-1 transition-colors"
            >
              ✕
            </button>
          {/if}
          <span class="bg-[#272a32] border border-[#3b494b]/60 text-[#b9cacb] rounded-md px-2 py-0.5 text-xs font-mono">⌘K</span>
        </div>
      </div>
    </div>

    <!-- Quick Concept Suggestions Chips underneath omni-search -->
    <div id="probes-bar" class="flex items-center gap-2 overflow-x-auto py-1 font-mono text-xs">
      <span class="text-[#849495] uppercase tracking-wider shrink-0 mr-1 text-[11px]">Probes:</span>
      {#each PROBES as probe}
        <button
          type="button"
          on:click={() => applyProbe(probe.query)}
          class="inline-flex items-center gap-1.5 text-[#b9cacb] bg-[#191b23]/80 border border-[#3b494b]/40 rounded-full px-3 py-1 hover:border-[#00f0ff]/50 hover:text-[#dbfcff] transition-all cursor-pointer shrink-0"
        >
          <span class="w-2 h-2 rounded-full {probe.colorClass}"></span>
          <span>{probe.label}</span>
        </button>
      {/each}
    </div>
  </section>

  <!-- Horizontal Conceptual Filter Pills Bar -->
  <section id="domain-filters-bar" class="py-1">
    <div class="flex items-center gap-2 overflow-x-auto py-1 font-mono text-xs no-scrollbar">
      {#each DOMAINS as domain}
        <button
          id={`domain-pill-${domain.id}`}
          type="button"
          on:click={() => selectDomain(domain.id)}
          class="flex items-center gap-1.5 font-mono text-xs px-3.5 py-1.5 rounded-full shrink-0 transition-all cursor-pointer active:scale-95 {activeDomain === domain.id ? 'bg-[#00f0ff] text-[#002022] font-bold shadow-md' : 'bg-[#191b23] text-[#b9cacb] border border-[#3b494b]/40 hover:border-[#00f0ff]/40 hover:text-[#dbfcff]'}"
        >
          {#if activeDomain === domain.id}
            <span class="w-1.5 h-1.5 rounded-full bg-[#002022]"></span>
          {/if}
          <span>{domain.label} ({domainCounts[domain.id] || 0})</span>
        </button>
      {/each}
    </div>
  </section>

  <!-- Interactive Conceptual Constellation / Radar Card -->
  <section
    id="radar-field-card"
    class="relative bg-[#191b23]/90 backdrop-blur-md border border-[#00f0ff]/30 rounded-xl p-5 shadow-lg overflow-hidden"
  >
    <!-- Crosshair corner indicators -->
    <div class="absolute top-2 left-2 text-[10px] font-mono text-[#3b494b] select-none">+</div>
    <div class="absolute top-2 right-2 text-[10px] font-mono text-[#3b494b] select-none">+</div>
    <div class="absolute bottom-2 left-2 text-[10px] font-mono text-[#3b494b] select-none">+</div>
    <div class="absolute bottom-2 right-2 text-[10px] font-mono text-[#3b494b] select-none">+</div>

    <!-- Card Header & Toggle -->
    <div class="flex items-center justify-between mb-4 relative z-10 flex-wrap gap-2">
      <div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2 h-2 rounded-full bg-[#00f0ff] animate-ping"></span>
          <span class="font-mono text-xs text-[#00f0ff] uppercase tracking-widest font-bold">RADAR FIELD</span>
        </div>
        <h2 class="text-lg font-semibold text-[#e1e2ec] mt-0.5">Active Conceptual Constellation</h2>
      </div>

      <!-- Toggle Pill -->
      <div class="bg-[#272a32]/80 p-0.5 rounded-full border border-[#3b494b]/40 flex items-center font-mono text-xs">
        <button
          id="toggle-2d"
          type="button"
          on:click={() => viewMode = '2d'}
          class="px-3 py-1 rounded-full transition-all cursor-pointer {viewMode === '2d' ? 'text-[#00f0ff] bg-[#0b0e15] font-bold shadow-sm' : 'text-[#b9cacb] font-medium hover:text-[#00f0ff]'}"
        >
          2D Graph
        </button>
        <button
          id="toggle-list"
          type="button"
          on:click={() => viewMode = 'list'}
          class="px-3 py-1 rounded-full transition-all cursor-pointer {viewMode === 'list' ? 'text-[#00f0ff] bg-[#0b0e15] font-bold shadow-sm' : 'text-[#b9cacb] font-medium hover:text-[#00f0ff]'}"
        >
          List
        </button>
      </div>
    </div>

    {#if viewMode === '2d'}
      <!-- Visual Interconnected Node Canvas -->
      <div class="relative w-full h-56 bg-[#0b0e15]/90 rounded-lg border border-[#3b494b]/30 flex items-center justify-center overflow-hidden my-3">
        <!-- Radial coordinate rings -->
        <div class="absolute w-48 h-48 rounded-full border border-[#3b494b]/20 pointer-events-none"></div>
        <div class="absolute w-32 h-32 rounded-full border border-[#3b494b]/25 pointer-events-none"></div>
        <div class="absolute w-16 h-16 rounded-full border border-[#00f0ff]/20 pointer-events-none"></div>

        <!-- Connection vectors (SVG Lines) -->
        <svg class="absolute inset-0 w-full h-full pointer-events-none stroke-current" xmlns="http://www.w3.org/2000/svg">
          <line stroke="rgba(0, 240, 255, 0.45)" stroke-dasharray="3,3" stroke-width="1.5" x1="50%" x2="22%" y1="50%" y2="28%"></line>
          <line stroke="rgba(168, 85, 247, 0.5)" stroke-width="2" x1="50%" x2="78%" y1="50%" y2="25%"></line>
          <line stroke="rgba(0, 240, 255, 0.4)" stroke-width="1.2" x1="50%" x2="68%" y1="50%" y2="78%"></line>
          <line stroke="rgba(255, 255, 255, 0.15)" stroke-width="1" x1="78%" x2="68%" y1="25%" y2="78%"></line>
        </svg>

        <!-- Vector Strength Badges along links -->
        <span class="absolute top-[37%] left-[33%] bg-[#272a32]/90 text-[#00f0ff] border border-[#00f0ff]/40 rounded px-1 text-[9px] font-mono">89%</span>
        <span class="absolute top-[34%] right-[32%] bg-[#272a32]/90 text-[#ddb7ff] border border-[#ddb7ff]/40 rounded px-1 text-[9px] font-mono">94%</span>
        <span class="absolute bottom-[36%] right-[38%] bg-[#272a32]/90 text-[#b9cacb] border border-[#3b494b]/40 rounded px-1 text-[9px] font-mono">76%</span>

        <!-- Center Active Node: Cyberfeminism -->
        <div class="absolute z-20 flex flex-col items-center">
          <div class="relative cursor-pointer group">
            <div class="w-10 h-10 rounded-full bg-[#00f0ff]/20 border-2 border-[#00f0ff] flex items-center justify-center group-hover:scale-110 transition-transform">
              <span class="material-symbols-outlined text-[#00f0ff] text-[18px]">grain</span>
            </div>
            <span class="absolute -top-1 -right-1 bg-[#7df4ff] text-[#002022] rounded-full text-[9px] font-bold px-1">12</span>
          </div>
          <span class="font-mono text-xs text-[#00f0ff] font-bold mt-1 bg-[#0b0e15]/90 px-1.5 py-0.5 rounded border border-[#00f0ff]/30">Cyberfeminism</span>
        </div>

        <!-- Node 2: Techno-Ecology (Top Left) -->
        <div class="absolute top-4 left-4 z-20 flex flex-col items-center">
          <div class="w-7 h-7 rounded-full bg-[#272a32] border border-[#00f0ff]/60 flex items-center justify-center hover:scale-110 transition-transform cursor-pointer">
            <span class="material-symbols-outlined text-[#00f0ff] text-[14px]">eco</span>
          </div>
          <span class="font-mono text-[10px] text-[#b9cacb] mt-0.5">Techno-Ecology</span>
        </div>

        <!-- Node 3: Posthumanism (Top Right) -->
        <div class="absolute top-3 right-6 z-20 flex flex-col items-center">
          <div class="w-8 h-8 rounded-full bg-[#6f00be]/40 border border-[#ddb7ff] flex items-center justify-center hover:scale-110 transition-transform cursor-pointer">
            <span class="material-symbols-outlined text-[#ddb7ff] text-[16px]">neurology</span>
          </div>
          <span class="font-mono text-[10px] text-[#ddb7ff] font-medium mt-0.5">Posthumanism</span>
        </div>

        <!-- Node 4: Digital Commons (Bottom Right) -->
        <div class="absolute bottom-4 right-8 z-20 flex flex-col items-center">
          <div class="w-7 h-7 rounded-full bg-[#272a32] border border-[#3b494b] flex items-center justify-center hover:scale-110 transition-transform cursor-pointer">
            <span class="material-symbols-outlined text-[#b9cacb] text-[14px]">public</span>
          </div>
          <span class="font-mono text-[10px] text-[#b9cacb] mt-0.5">Digital Commons</span>
        </div>

        <!-- Spatial Axis Indicator -->
        <div class="absolute bottom-1.5 left-2 text-[9px] font-mono text-[#849495] tracking-wider">
          COORDS: [0.72x, -0.41y]
        </div>
      </div>
    {:else}
      <!-- Alternative List Mode View for Nodes -->
      <div class="bg-[#0b0e15]/90 rounded-lg border border-[#3b494b]/30 p-3 space-y-2 my-3 font-mono text-xs">
        <div class="p-2 bg-[#191b23] border border-[#00f0ff]/40 rounded flex items-center justify-between">
          <span class="text-[#00f0ff] font-bold">Cyberfeminism (Center Node)</span>
          <span class="text-[10px] text-[#849495]">12 Connections · Weight 0.94</span>
        </div>
        <div class="p-2 bg-[#191b23] border border-[#3b494b]/30 rounded flex items-center justify-between">
          <span class="text-[#b9cacb]">Techno-Ecology</span>
          <span class="text-[10px] text-[#849495]">8 Connections · Weight 0.89</span>
        </div>
        <div class="p-2 bg-[#191b23] border border-[#3b494b]/30 rounded flex items-center justify-between">
          <span class="text-[#ddb7ff]">Posthumanism</span>
          <span class="text-[10px] text-[#849495]">10 Connections · Weight 0.92</span>
        </div>
        <div class="p-2 bg-[#191b23] border border-[#3b494b]/30 rounded flex items-center justify-between">
          <span class="text-[#b9cacb]">Digital Commons</span>
          <span class="text-[10px] text-[#849495]">6 Connections · Weight 0.76</span>
        </div>
      </div>
    {/if}

    <!-- Constellation Footer Metric and Expand Button -->
    <div class="flex items-center justify-between pt-1 flex-wrap gap-2 font-mono text-xs">
      <div class="flex items-center gap-2 text-[#b9cacb]">
        <span class="material-symbols-outlined text-[16px] text-[#00f0ff]">alt_route</span>
        <span>Core Density: <strong class="text-[#e1e2ec]">3.8 n/rad</strong></span>
      </div>
      <button
        id="expand-constellation-btn"
        type="button"
        on:click={() => isExpandedConstellation = !isExpandedConstellation}
        class="inline-flex items-center gap-1.5 bg-[#272a32] hover:bg-[#363941] text-[#00f0ff] border border-[#3b494b]/60 rounded-lg px-3 py-1.5 font-mono text-xs font-semibold active:scale-95 transition-all cursor-pointer"
      >
        <span>{isExpandedConstellation ? 'Collapse View' : 'Expand Constellation View'}</span>
        <span class="material-symbols-outlined text-[14px]">{isExpandedConstellation ? 'close_fullscreen' : 'open_in_full'}</span>
      </button>
    </div>
  </section>

  <!-- Curated Manifestos Stream Section -->
  <section id="manifestos-stream-section" class="space-y-4 pt-2">
    <!-- Section Header with Meta Count -->
    <div class="flex items-baseline justify-between border-b border-[#3b494b]/30 pb-3">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-[#ddb7ff] text-[20px]">auto_stories</span>
        <h3 class="text-lg font-semibold text-[#e1e2ec]">Manifestos & Treatises Stream</h3>
      </div>
      <span class="font-mono text-xs text-[#849495] font-medium">{filteredManifestos.length} works catalogued</span>
    </div>

    {#if filteredManifestos.length === 0}
      <!-- Clear Empty State with Reset Option -->
      <div
        id="empty-state-card"
        class="bg-[#191b23]/80 border border-[#3b494b]/40 rounded-xl p-8 text-center space-y-4 shadow-md"
      >
        <div class="w-12 h-12 mx-auto rounded-full bg-[#272a32] border border-[#3b494b]/60 flex items-center justify-center text-[#00f0ff]">
          <span class="material-symbols-outlined text-2xl">search_off</span>
        </div>

        <div class="space-y-1">
          <h4 class="text-base font-bold text-[#e1e2ec]">No matching manifestos found</h4>
          <p class="text-xs text-[#b9cacb] max-w-md mx-auto leading-relaxed">
            No results matched your search term <strong class="text-[#00f0ff]">"{searchQuery}"</strong>
            {#if activeDomain !== 'all'} and domain filter <strong class="text-[#00f0ff]">"{activeDomain}"</strong>{/if}.
          </p>
        </div>

        <button
          id="reset-filters-btn"
          type="button"
          on:click={resetFilters}
          class="inline-flex items-center gap-2 bg-[#00f0ff] hover:brightness-110 text-[#002022] font-mono text-xs font-bold px-4 py-2 rounded-lg transition-all cursor-pointer shadow-md active:scale-95"
        >
          <span class="material-symbols-outlined text-[16px]">restart_alt</span>
          <span>Reset Filters</span>
        </button>
      </div>
    {:else}
      <!-- Manifesto Cards List -->
      {#each filteredManifestos as manifesto (manifesto.id)}
        <article
          id={`manifesto-card-${manifesto.id}`}
          class="bg-[#191b23]/80 backdrop-blur-md border border-[#3b494b]/40 hover:border-[#00f0ff]/40 rounded-xl p-5 transition-all duration-200 hover:shadow-lg space-y-3"
        >
          <div class="flex items-start justify-between gap-3">
            <div class="space-y-1">
              <div class="flex items-center gap-2 flex-wrap font-mono text-xs">
                <span class="bg-[#272a32] border border-[#3b494b]/50 text-[#00f0ff] px-2 py-0.5 rounded font-bold">{manifesto.year}</span>
                <span class="text-[#849495]">{manifesto.badge}</span>
              </div>
              <h4 class="text-lg font-semibold text-[#e1e2ec] tracking-tight leading-snug">
                {manifesto.title}
              </h4>
              <p class="text-sm text-[#b9cacb]">
                by <span class="text-[#e1e2ec] font-medium">{manifesto.author}</span> · <span class="text-[#849495]">{manifesto.institution}</span>
              </p>
            </div>

            <button
              id={`bookmark-btn-${manifesto.id}`}
              type="button"
              on:click={() => toggleBookmark(manifesto.id)}
              aria-label={`Bookmark ${manifesto.title}`}
              class="w-8 h-8 rounded-full flex items-center justify-center text-[#ddb7ff] hover:bg-[#272a32] transition-colors cursor-pointer shrink-0"
            >
              <span class="material-symbols-outlined text-[20px]">{bookmarkedIds.has(manifesto.id) ? 'star' : 'star_border'}</span>
            </button>
          </div>

          <!-- Concept Taxonomy Tags -->
          <div class="flex flex-wrap gap-1.5 font-mono text-xs">
            {#each manifesto.tags as tag}
              <span class="bg-[#272a32] text-[#b9cacb] border border-[#3b494b]/40 rounded-full px-2.5 py-0.5">{tag}</span>
            {/each}
          </div>

          {#if manifesto.quote}
            <blockquote class="my-2 pl-3.5 border-l-2 border-[#00f0ff]/80 bg-[#0b0e15]/40 py-2 pr-2 rounded-r-lg">
              <p class="text-sm italic leading-relaxed text-[#e1e2ec]/90 font-serif">
                {manifesto.quote}
              </p>
            </blockquote>
          {/if}

          {#if manifesto.summary}
            <p class="text-sm text-[#b9cacb] line-clamp-2 leading-relaxed">
              {manifesto.summary}
            </p>
          {/if}

          {#if manifesto.relationalNodes}
            <div class="flex items-center gap-2 p-2 bg-[#0b0e15]/60 border border-[#3b494b]/30 rounded-lg font-mono text-xs">
              <span class="text-[#849495] shrink-0">Relational Nodes:</span>
              {#each manifesto.relationalNodes as node}
                <span class="text-[#00f0ff] bg-[#272a32] px-2 py-0.5 rounded border border-[#3b494b]/30 flex items-center gap-1">
                  <span class="material-symbols-outlined text-[12px]">hub</span> {node}
                </span>
              {/each}
            </div>
          {/if}

          <!-- Footer Actions and Metadata -->
          <div class="flex items-center justify-between pt-3 border-t border-[#3b494b]/20 flex-wrap gap-2 font-mono text-xs">
            <div class="flex items-center gap-1.5 text-[#b9cacb]">
              <span class="material-symbols-outlined text-[15px]">schedule</span>
              <span>{manifesto.readTimeMinutes} min read</span>
            </div>
            <div class="flex items-center gap-2">
              <button
                type="button"
                class="text-[#00f0ff] hover:text-[#7df4ff] font-semibold flex items-center gap-1 bg-[#272a32]/60 px-2.5 py-1.5 rounded-lg border border-[#3b494b]/40 hover:border-[#00f0ff]/40 active:scale-95 transition-all cursor-pointer"
              >
                <span class="material-symbols-outlined text-[15px]">hub</span>
                <span>{manifesto.vectorCount} Vectors</span>
              </button>
              <button
                type="button"
                class="bg-[#00f0ff] hover:bg-[#7df4ff] text-[#002022] font-mono text-xs uppercase font-bold px-3 py-1.5 rounded-lg shadow-sm active:scale-95 transition-all cursor-pointer"
              >
                Read Treatise
              </button>
            </div>
          </div>
        </article>
      {/each}
    {/if}
  </section>
</div>
