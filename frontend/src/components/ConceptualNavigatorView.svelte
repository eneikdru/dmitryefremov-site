<script lang="ts">
  import { telemetry } from '../lib/telemetry';

  export let theme: 'dark' | 'light' = 'dark';

  interface ManifestoItem {
    id: string;
    title: string;
    author: string;
    year: string;
    category: string;
    readTime: string;
    citations?: number;
    quote?: string;
    description: string;
    tags: string[];
    isFeatured?: boolean;
    vector: string;
  }

  const MANIFESTOS: ManifestoItem[] = [
    {
      id: 'manifesto-hacker',
      title: 'A Hacker Manifesto',
      author: 'McKenzie Wark',
      year: '2004',
      category: 'Cybernetics',
      readTime: '14 min read',
      quote: '“Information wants to be free, but everywhere it is in chains. A hacker is not merely one who breaches code, but anyone who produces new abstractions from existing reality.”',
      description: 'Critical abstraction of information asymmetry and vectoralist class dynamics in digital infrastructure.',
      tags: ['Information Asymmetry', 'Abstraction', 'Vectoralist Class'],
      isFeatured: true,
      vector: 'CYBERNETICS // ONTOLOGY'
    },
    {
      id: 'manifesto-cyberfeminism',
      title: 'A Cyberfeminist Manifesto for the 21st Century',
      author: 'VNS Matrix (Virginia Barratt, Julianne Pierce, Francesca da Rimini, Josephine Starrs)',
      year: '1991',
      category: 'Cybernetics',
      readTime: '8 min read',
      citations: 342,
      description: 'An aggressive poetic disruption seizing telecommunications architecture as an immunological space against patriarchal matrix monopoly.',
      tags: ['#wetware', '#techno-subversion', 'Cybernetics'],
      vector: 'CYBERFEMINISM // WETWARE'
    },
    {
      id: 'manifesto-speculative-realism',
      title: 'Speculative Realism & Post-Kantian Correlationalism',
      author: 'Goldsmiths Research Consortium',
      year: '2007',
      category: 'Accelerationism',
      readTime: '18 min read',
      citations: 189,
      description: 'Dismantling anthropocentric consensus through independent object agency and mathematical ungrounding beyond subjective consciousness.',
      tags: ['#object-oriented', '#non-human', 'Accelerationism'],
      vector: 'ONTOLOGY // CORRELATIONALISM'
    },
    {
      id: 'manifesto-ontology-autonomy',
      title: 'Ontology of Autonomy & A2A Economics',
      author: 'Dmitry Efremov',
      year: '2025',
      category: 'Digital Commons',
      readTime: '12 min read',
      citations: 512,
      description: 'Deontic invariants, machine-readable specifications, and self-sovereign agent transaction protocols for multi-million bot traffic.',
      tags: ['A2A Economics', 'Deontic Logic', 'Digital Commons'],
      vector: 'AUTONOMY // A2A-ECONOMICS'
    },
    {
      id: 'manifesto-solarpunk-protocols',
      title: 'Solarpunk Protocols & Regenerative Networks',
      author: 'Noosphere Research Lab',
      year: '2023',
      category: 'Post-Humanism',
      readTime: '10 min read',
      citations: 94,
      description: 'Cyber-ecological feedback loops combining decentralized energy grids, open-source hardware, and commons governance.',
      tags: ['#solarpunk', '#commons', 'Post-Humanism'],
      vector: 'POST-HUMANISM // ECOLOGY'
    },
    {
      id: 'manifesto-solipsism-and-code',
      title: 'Solipsism, Subjectivity and Epistemic Code',
      author: 'Dmitry Efremov & Epistemic Circle',
      year: '2024',
      category: 'Solipsism',
      readTime: '15 min read',
      citations: 78,
      description: 'Reflexive inquiry into first-person conscious perspective versus algorithmic deterministic state machines.',
      tags: ['Solipsism', 'Epistemology', 'Mind'],
      vector: 'SOLIPSISM // FIRST-PERSON'
    }
  ];

  const CATEGORY_TAGS = [
    { name: 'All', count: MANIFESTOS.length },
    { name: 'Cybernetics', count: MANIFESTOS.filter(m => m.category === 'Cybernetics' || m.tags.includes('Cybernetics')).length },
    { name: 'Accelerationism', count: MANIFESTOS.filter(m => m.category === 'Accelerationism' || m.tags.includes('Accelerationism')).length },
    { name: 'Post-Humanism', count: MANIFESTOS.filter(m => m.category === 'Post-Humanism' || m.tags.includes('Post-Humanism')).length },
    { name: 'Digital Commons', count: MANIFESTOS.filter(m => m.category === 'Digital Commons' || m.tags.includes('Digital Commons')).length },
    { name: 'Solipsism', count: MANIFESTOS.filter(m => m.category === 'Solipsism' || m.tags.includes('Solipsism')).length }
  ];

  let searchQuery: string = '';
  let activeTag: string = 'All';
  let activeNodeId: string = 'node-ecology-mind';

  $: filteredManifestos = MANIFESTOS.filter(item => {
    const matchesTag = activeTag === 'All' || item.category === activeTag || item.tags.some(t => t.toLowerCase() === activeTag.toLowerCase());
    if (!matchesTag) return false;

    if (!searchQuery.trim()) return true;

    const q = searchQuery.toLowerCase().trim();
    return (
      item.title.toLowerCase().includes(q) ||
      item.author.toLowerCase().includes(q) ||
      item.description.toLowerCase().includes(q) ||
      item.tags.some(t => t.toLowerCase().includes(q)) ||
      (item.quote && item.quote.toLowerCase().includes(q))
    );
  });

  $: activeVectorText = searchQuery.trim()
    ? `SEARCH: "${searchQuery.toUpperCase()}"`
    : (activeTag !== 'All' ? `${activeTag.toUpperCase()} // VECTOR` : 'CYBERNETICS // ONTOLOGY');

  function handleSearchInput() {
    if (searchQuery.trim().length > 1) {
      telemetry.trackSearchUtilization(searchQuery, 'conceptual_navigator', filteredManifestos.length);
    }
  }

  function handleTagClick(tagName: string) {
    activeTag = tagName;
    telemetry.trackOutboundClick('#tag-filter', tagName, 'ConceptualNavigator');
  }

  function handleNodeClick(nodeId: string, nodeLabel: string, nodeTag: string) {
    activeNodeId = nodeId;
    if (activeTag === nodeTag) {
      activeTag = 'All';
    } else {
      activeTag = nodeTag;
    }
    telemetry.trackCrossLinkClick('semantic-radar', nodeId, 'constellation_node', nodeLabel);
  }

  function resetFilters() {
    searchQuery = '';
    activeTag = 'All';
    activeNodeId = 'node-ecology-mind';
  }
</script>

<div
  id="conceptual-navigator-root"
  class="max-w-md sm:max-w-2xl lg:max-w-4xl mx-auto flex flex-col gap-6 transition-colors duration-200 overflow-x-hidden {theme === 'dark' ? 'text-[#dfe2ee]' : 'text-[#0f131c]'}"
>
  <!-- Navigator Sub-Header Banner -->
  <header id="navigator-header" class="flex items-center justify-between border-b pb-3 {theme === 'dark' ? 'border-[#31353e]' : 'border-[#e2e8f0]'}">
    <div class="flex items-center gap-2">
      <span class="material-symbols-outlined text-[#03b5d3] text-2xl" data-icon="hub">hub</span>
      <div>
        <h1 id="navigator-header-title" class="font-mono text-lg font-bold tracking-wider text-[#03b5d3]">
          CONCEPTUAL NAVIGATOR
        </h1>
        <p class="font-sans text-xs text-[#908fa0]">
          Интерактивный поиск, манифесты и онтологический локатор Дмитрия Ефремова
        </p>
      </div>
    </div>
    <div class="hidden sm:flex items-center gap-2 font-mono text-xs text-[#03b5d3] bg-[#03b5d3]/10 border border-[#03b5d3]/30 px-2.5 py-1 rounded">
      <span class="w-2 h-2 rounded-full bg-[#03b5d3] animate-pulse"></span>
      <span>A2A VECTOR ONLINE</span>
    </div>
  </header>

  <!-- Concept Query Bar -->
  <section id="concept-query-section" class="space-y-2">
    <div
      id="query-input-container"
      class="relative flex items-center bg-[#181c24] border border-[#464554]/40 rounded-lg px-3.5 py-3 shadow-inner focus-within:border-[#03b5d3] focus-within:ring-1 focus-within:ring-[#03b5d3]/40 transition-all"
    >
      <span class="material-symbols-outlined text-[#908fa0] text-[20px] mr-2.5 shrink-0" data-icon="terminal">terminal</span>
      <input
        id="concept-search-input"
        type="text"
        bind:value={searchQuery}
        on:input={handleSearchInput}
        placeholder="Search concepts, treatises, authors..."
        class="w-full bg-transparent border-none p-0 text-[#dfe2ee] placeholder:text-[#908fa0] font-mono text-sm focus:ring-0 focus:outline-none"
        autocomplete="off"
      />
      {#if searchQuery}
        <button
          id="clear-search-btn"
          type="button"
          on:click={() => (searchQuery = '')}
          aria-label="Clear search text"
          class="text-[#908fa0] hover:text-[#03b5d3] mr-2 text-xs font-mono px-1.5 py-0.5 rounded bg-[#31353e] min-h-[36px] min-w-[36px] flex items-center justify-center cursor-pointer"
        >
          ✕
        </button>
      {/if}
      <div class="flex items-center gap-2 ml-1 shrink-0">
        <button
          type="button"
          class="text-[#908fa0] hover:text-[#03b5d3] transition-colors min-h-[40px] min-w-[40px] flex items-center justify-center rounded"
          title="Voice Query"
          aria-label="Voice Query"
        >
          <span class="material-symbols-outlined text-[20px]" data-icon="mic">mic</span>
        </button>
        <div class="h-4 w-px bg-[#464554]/40 mx-0.5 hidden sm:block"></div>
        <span class="hidden sm:inline-flex items-center px-2 py-1 rounded bg-[#31353e] text-[#03b5d3] font-mono text-[11px] tracking-tight">
          ⌘K
        </span>
      </div>
    </div>

    <!-- Dynamic Filter Telemetry -->
    <div id="filter-telemetry-bar" class="flex items-center justify-between px-1 text-xs font-mono">
      <div class="flex items-center gap-2 truncate">
        <span class="w-2 h-2 rounded-full bg-[#03b5d3] animate-pulse shrink-0"></span>
        <span class="text-[#03b5d3] font-bold">ACTIVE VECTOR:</span>
        <span id="concept-active-vector" class="text-[#c7c4d7] font-medium truncate">
          {activeVectorText}
        </span>
      </div>
      {#if searchQuery || activeTag !== 'All'}
        <button
          id="reset-vector-btn"
          type="button"
          on:click={resetFilters}
          class="min-h-[40px] px-2 flex items-center text-[11px] text-[#908fa0] hover:text-[#03b5d3] underline decoration-dotted transition-colors cursor-pointer shrink-0"
        >
          RESET
        </button>
      {/if}
    </div>
  </section>

  <!-- Thematic Concept Tags (Horizontal Scroll) -->
  <section id="concept-tags-section" class="no-scrollbar overflow-x-auto -mx-2 px-2 py-1 flex items-center gap-2">
    {#each CATEGORY_TAGS as tag}
      <button
        id={`tag-btn-${tag.name.toLowerCase().replace(/\s+/g, '-')}`}
        type="button"
        on:click={() => handleTagClick(tag.name)}
        class="flex-shrink-0 inline-flex items-center gap-2 px-3.5 py-2.5 rounded-lg font-mono text-xs transition-all cursor-pointer min-h-[44px] {activeTag === tag.name ? 'bg-[#262a33] border border-[#03b5d3] text-[#03b5d3] shadow-[0_0_15px_rgba(3,181,211,0.25)] font-bold' : 'bg-[#181c24] border border-[#464554]/30 text-[#c7c4d7] hover:border-[#03b5d3]/60 hover:text-white'}"
      >
        {#if activeTag === tag.name}
          <span class="w-1.5 h-1.5 rounded-full bg-[#03b5d3]"></span>
        {/if}
        <span>{tag.name}</span>
        <span class="px-1.5 py-0.5 rounded text-[10px] {activeTag === tag.name ? 'bg-[#03b5d3]/20 text-[#03b5d3]' : 'bg-[#31353e] text-[#908fa0]'}">
          {tag.count}
        </span>
      </button>
    {/each}
  </section>

  <!-- Interactive Concept Radar / Constellation Canvas Card -->
  <section id="concept-radar-card" class="bg-[#0a0e16] border border-[#464554]/40 rounded-xl overflow-hidden p-4 sm:p-5 shadow-2xl space-y-3">
    <div class="flex items-center justify-between border-b border-[#464554]/20 pb-2.5">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-[#03b5d3] text-lg" data-icon="grain">grain</span>
        <h2 id="radar-title" class="font-mono text-xs tracking-wider uppercase text-[#dfe2ee] font-bold">
          SEMANTIC RADAR // LVL 01
        </h2>
      </div>
      <div class="flex items-center gap-1.5 text-[#908fa0] font-mono text-[11px]">
        <span class="material-symbols-outlined text-xs text-[#03b5d3]" data-icon="sync">sync</span>
        <span>5 SYNAPSES ACTIVE</span>
      </div>
    </div>

    <!-- Constellation Simulation Canvas -->
    <div id="constellation-canvas-container" class="relative w-full h-64 bg-[#0f131c]/90 rounded-lg border border-[#464554]/30 overflow-hidden flex items-center justify-center">
      <!-- Background Grid Rings -->
      <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
        <div class="w-56 h-56 rounded-full border border-[#464554]/15 border-dashed"></div>
        <div class="absolute w-36 h-36 rounded-full border border-[#464554]/20"></div>
        <div class="absolute w-20 h-20 rounded-full border border-[#03b5d3]/20 bg-[#03b5d3]/5"></div>
        <div class="absolute w-full h-px bg-[#464554]/10"></div>
        <div class="absolute h-full w-px bg-[#464554]/10"></div>
      </div>

      <!-- Node Connections (SVG Overlays) -->
      <svg class="absolute inset-0 w-full h-full pointer-events-none" xmlns="http://www.w3.org/2000/svg">
        <line x1="50%" y1="50%" x2="20%" y2="22%" stroke="#03b5d3" stroke-dasharray="2 2" stroke-opacity="0.6" stroke-width="1.2" />
        <line x1="50%" y1="50%" x2="80%" y2="24%" stroke="#8083ff" stroke-opacity="0.7" stroke-width="1.5" />
        <line x1="50%" y1="50%" x2="18%" y2="80%" stroke="#8083ff" stroke-opacity="0.5" stroke-width="1" />
        <line x1="50%" y1="50%" x2="78%" y2="80%" stroke="#03b5d3" stroke-opacity="0.7" stroke-width="1.2" />
        <line x1="80%" y1="24%" x2="78%" y2="80%" stroke="#464554" stroke-opacity="0.5" stroke-width="0.8" />
      </svg>

      <!-- Central Core Node: Ecology of Mind -->
      <div
        id="node-ecology-mind"
        type="button"
        role="button"
        tabindex="0"
        on:click={() => handleNodeClick('node-ecology-mind', 'Ecology of Mind', 'Cybernetics')}
        on:keydown={(e) => e.key === 'Enter' && handleNodeClick('node-ecology-mind', 'Ecology of Mind', 'Cybernetics')}
        class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-10 flex flex-col items-center cursor-pointer group min-h-[44px] min-w-[44px] justify-center"
      >
        <div class="w-4 h-4 rounded-full bg-[#03b5d3] border-2 border-[#0f131c] shadow-[0_0_12px_#03b5d3] group-hover:scale-125 transition-transform"></div>
        <span class="mt-1 px-2 py-0.5 rounded bg-[#0f131c]/95 border border-[#03b5d3]/50 font-mono text-[10px] text-[#03b5d3] whitespace-nowrap shadow-md">
          Ecology of Mind
        </span>
      </div>

      <!-- Node 1: Decentralized Intelligence -->
      <div
        id="node-decentralized-intel"
        type="button"
        role="button"
        tabindex="0"
        on:click={() => handleNodeClick('node-decentralized-intel', 'Decentralized Intel.', 'Digital Commons')}
        on:keydown={(e) => e.key === 'Enter' && handleNodeClick('node-decentralized-intel', 'Decentralized Intel.', 'Digital Commons')}
        class="absolute top-4 left-4 z-10 flex flex-col items-start cursor-pointer group min-h-[44px] min-w-[44px] justify-center"
      >
        <div class="flex items-center gap-1.5">
          <div class="w-3 h-3 rounded-full bg-[#8083ff] border border-[#0f131c] shadow-[0_0_10px_#8083ff] group-hover:scale-125 transition-transform"></div>
          <span class="px-2 py-0.5 rounded bg-[#1c2028]/90 border border-[#464554]/30 font-mono text-[9.5px] text-[#dfe2ee] whitespace-nowrap">
            Decentralized Intel.
          </span>
        </div>
      </div>

      <!-- Node 2: Algorithmic Poetics -->
      <div
        id="node-algorithmic-poetics"
        type="button"
        role="button"
        tabindex="0"
        on:click={() => handleNodeClick('node-algorithmic-poetics', 'Algorithmic Poetics', 'Cybernetics')}
        on:keydown={(e) => e.key === 'Enter' && handleNodeClick('node-algorithmic-poetics', 'Algorithmic Poetics', 'Cybernetics')}
        class="absolute top-5 right-4 z-10 flex flex-col items-end cursor-pointer group min-h-[44px] min-w-[44px] justify-center"
      >
        <div class="flex items-center gap-1.5">
          <span class="px-2 py-0.5 rounded bg-[#1c2028]/90 border border-[#8083ff]/40 font-mono text-[9.5px] text-[#c0c1ff] whitespace-nowrap">
            Algorithmic Poetics
          </span>
          <div class="w-3 h-3 rounded-full bg-[#e1e0ff] border border-[#0f131c] shadow-[0_0_10px_#8083ff] group-hover:scale-125 transition-transform"></div>
        </div>
      </div>

      <!-- Node 3: Technological Subversion -->
      <div
        id="node-tech-subversion"
        type="button"
        role="button"
        tabindex="0"
        on:click={() => handleNodeClick('node-tech-subversion', 'Tech Subversion', 'Accelerationism')}
        on:keydown={(e) => e.key === 'Enter' && handleNodeClick('node-tech-subversion', 'Tech Subversion', 'Accelerationism')}
        class="absolute bottom-5 left-3 z-10 flex flex-col items-start cursor-pointer group min-h-[44px] min-w-[44px] justify-center"
      >
        <div class="flex items-center gap-1.5">
          <div class="w-2.5 h-2.5 rounded-full bg-[#908fa0] group-hover:scale-125 transition-transform"></div>
          <span class="px-2 py-0.5 rounded bg-[#1c2028]/90 border border-[#464554]/30 font-mono text-[9.5px] text-[#c7c4d7] whitespace-nowrap">
            Tech Subversion
          </span>
        </div>
      </div>

      <!-- Node 4: Solarpunk Protocols -->
      <div
        id="node-solarpunk-protocols"
        type="button"
        role="button"
        tabindex="0"
        on:click={() => handleNodeClick('node-solarpunk-protocols', 'Solarpunk Protocols', 'Post-Humanism')}
        on:keydown={(e) => e.key === 'Enter' && handleNodeClick('node-solarpunk-protocols', 'Solarpunk Protocols', 'Post-Humanism')}
        class="absolute bottom-5 right-4 z-10 flex flex-col items-end cursor-pointer group min-h-[44px] min-w-[44px] justify-center"
      >
        <div class="flex items-center gap-1.5">
          <span class="px-2 py-0.5 rounded bg-[#1c2028]/90 border border-[#03b5d3]/40 font-mono text-[9.5px] text-[#03b5d3] whitespace-nowrap">
            Solarpunk Protocols
          </span>
          <div class="w-3 h-3 rounded-full bg-[#acedff] border border-[#0f131c] shadow-[0_0_10px_#03b5d3] group-hover:scale-125 transition-transform"></div>
        </div>
      </div>

      <!-- Guidance Hint -->
      <div class="absolute bottom-1.5 left-1/2 -translate-x-1/2 flex items-center gap-1 text-[9.5px] font-mono text-[#908fa0] bg-[#0f131c]/80 px-2.5 py-1 rounded-full backdrop-blur-sm pointer-events-none">
        <span class="material-symbols-outlined text-[12px] text-[#03b5d3]" data-icon="pinch">pinch</span>
        <span>Tap nodes to highlight cluster vector</span>
      </div>
    </div>

    <!-- Radar Footer / Quick Stats -->
    <div class="flex items-center justify-between pt-1 font-mono text-xs">
      <span class="text-[#908fa0]">CLUSTER: <strong class="text-[#dfe2ee] font-medium">#SYN-088</strong></span>
      <span class="text-[#03b5d3] text-xs font-bold">NODE: {activeNodeId}</span>
    </div>
  </section>

  <!-- Section Header: Manifestos Stream -->
  <div class="flex items-baseline justify-between pt-2 px-1">
    <h3 class="font-serif text-xl text-[#dfe2ee] italic font-semibold">
      Knowledge Base Manifestos & Concepts
    </h3>
    <span id="manifestos-count-badge" class="font-mono text-xs text-[#908fa0] uppercase tracking-wider">
      {filteredManifestos.length} {filteredManifestos.length === 1 ? 'ENTRY' : 'ENTRIES'}
    </span>
  </div>

  <!-- Search Results Stream or Empty State -->
  {#if filteredManifestos.length === 0}
    <!-- Acceptance Criteria #2: Clear empty state with a reset filters option -->
    <div
      id="conceptual-navigator-empty-state"
      class="bg-[#181c24] border border-[#464554]/40 rounded-xl p-8 text-center space-y-4 shadow-lg my-4"
    >
      <div class="w-12 h-12 mx-auto rounded-full bg-[#31353e] flex items-center justify-center text-[#03b5d3]">
        <span class="material-symbols-outlined text-2xl" data-icon="terminal">terminal</span>
      </div>
      <div class="space-y-1.5 max-w-md mx-auto">
        <h4 class="font-mono text-base font-bold text-[#dfe2ee]">
          No concepts or manifestos found
        </h4>
        <p class="font-sans text-xs text-[#908fa0] leading-relaxed">
          {#if searchQuery}
            No entries matched search query <span class="text-[#03b5d3] font-mono">"{searchQuery}"</span> under tag <span class="text-[#03b5d3] font-mono">"{activeTag}"</span>.
          {:else}
            No entries available under active filter <span class="text-[#03b5d3] font-mono">"{activeTag}"</span>.
          {/if}
        </p>
      </div>

      <div class="pt-2">
        <button
          id="reset-search-filters-btn"
          type="button"
          on:click={resetFilters}
          class="inline-flex items-center justify-center gap-2 px-5 py-3 rounded-lg bg-[#8083ff] hover:bg-[#8083ff]/90 text-[#1000a9] font-mono text-xs uppercase font-bold transition-all shadow-[0_0_15px_rgba(128,131,255,0.3)] cursor-pointer min-h-[44px]"
        >
          <span>RESET FILTERS</span>
          <span class="material-symbols-outlined text-sm" data-icon="sync">sync</span>
        </button>
      </div>
    </div>
  {:else}
    <!-- Results Stream -->
    <div id="manifestos-list" class="space-y-4">
      {#each filteredManifestos as manifesto (manifesto.id)}
        {#if manifesto.isFeatured}
          <!-- Featured Manifesto Card -->
          <article
            id={manifesto.id}
            class="bg-[#181c24] border border-[#464554]/40 rounded-xl p-5 relative overflow-hidden transition-all hover:border-[#03b5d3]/50 shadow-xl space-y-3"
          >
            <!-- Accent aura line -->
            <div class="absolute top-0 left-0 w-full h-[2px] bg-gradient-to-r from-transparent via-[#03b5d3] to-transparent"></div>

            <!-- Metadata Header -->
            <div class="flex items-center justify-between text-[#908fa0] text-xs font-mono">
              <span class="text-[#03b5d3] tracking-widest uppercase font-bold">
                MANIFESTO // {manifesto.year}
              </span>
              <div class="flex items-center gap-1.5 text-[#c7c4d7]">
                <span class="material-symbols-outlined text-[14px]" data-icon="schedule">schedule</span>
                <span>{manifesto.readTime}</span>
              </div>
            </div>

            <!-- Headline -->
            <h4 class="font-serif text-2xl text-[#dfe2ee] font-medium leading-snug">
              {manifesto.title}
            </h4>
            <p class="font-sans text-xs text-[#c7c4d7] italic">
              {manifesto.author}
            </p>

            <!-- Excerpt Quote -->
            {#if manifesto.quote}
              <blockquote class="border-l-2 border-[#8083ff]/50 pl-3.5 my-3">
                <p class="font-serif text-sm sm:text-base text-[#dfe2ee]/90 italic leading-relaxed">
                  {manifesto.quote}
                </p>
              </blockquote>
            {/if}

            <p class="font-sans text-xs text-[#c7c4d7] leading-relaxed">
              {manifesto.description}
            </p>

            <!-- Core Concept Badges -->
            <div class="flex flex-wrap gap-1.5 pt-1">
              {#each manifesto.tags as tag}
                <span class="px-2.5 py-1 rounded bg-[#1c2028] border border-[#464554]/30 text-[#03b5d3] font-mono text-[10px]">
                  {tag}
                </span>
              {/each}
            </div>

            <!-- Explore Action -->
            <div class="pt-3 border-t border-[#464554]/20 flex items-center justify-between">
              <button
                type="button"
                on:click={() => telemetry.trackOutboundClick(`#explore-${manifesto.id}`, manifesto.title, 'ManifestoStream')}
                class="w-full bg-[#8083ff] hover:bg-[#8083ff]/90 text-[#1000a9] font-mono text-xs uppercase font-bold py-3 px-4 rounded-lg transition-all flex items-center justify-center gap-2 shadow-[0_0_15px_rgba(128,131,255,0.3)] cursor-pointer min-h-[44px]"
              >
                <span>EXPLORE SYNTHESIS</span>
                <span class="material-symbols-outlined text-sm" data-icon="explore">explore</span>
              </button>
            </div>
          </article>
        {:else}
          <!-- Regular Manifesto Feed Card -->
          <article
            id={manifesto.id}
            class="bg-[#181c24] border border-[#464554]/30 rounded-xl p-4 sm:p-5 transition-all hover:border-[#464554] space-y-2 shadow-md"
          >
            <div class="flex items-center justify-between text-xs font-mono">
              <span class="text-[#03b5d3] uppercase tracking-wider text-[11px] font-bold">
                {manifesto.category.toUpperCase()} // {manifesto.year}
              </span>
              {#if manifesto.citations}
                <span class="text-[#908fa0] flex items-center gap-1 text-[11px]">
                  <span>{manifesto.citations} Citations</span>
                </span>
              {/if}
            </div>

            <h5 class="font-serif text-lg sm:text-xl text-[#dfe2ee] leading-tight font-medium">
              {manifesto.title}
            </h5>
            <p class="font-sans text-xs text-[#c7c4d7] italic">
              {manifesto.author}
            </p>

            <p class="font-sans text-xs sm:text-sm text-[#dfe2ee]/80 line-clamp-2 leading-relaxed">
              {manifesto.description}
            </p>

            <!-- Connected Cluster Tags & Footnote -->
            <div class="flex items-center justify-between pt-2 border-t border-[#464554]/20 gap-2">
              <div class="flex flex-wrap gap-1.5 overflow-hidden">
                {#each manifesto.tags as tag}
                  <span class="px-2 py-0.5 rounded bg-[#0f131c] border border-[#464554]/30 text-[#03b5d3] font-mono text-[10px]">
                    {tag}
                  </span>
                {/each}
              </div>
              <button
                type="button"
                aria-label="Bookmark Manifesto"
                on:click={() => telemetry.trackOutboundClick(`#bookmark-${manifesto.id}`, manifesto.title, 'ManifestoStream')}
                class="text-[#03b5d3] hover:text-[#acedff] transition-colors p-2 min-h-[44px] min-w-[44px] flex items-center justify-center shrink-0"
              >
                <span class="material-symbols-outlined text-[20px]" data-icon="bookmark_add">bookmark_add</span>
              </button>
            </div>
          </article>
        {/if}
      {/each}
    </div>
  {/if}
</div>
