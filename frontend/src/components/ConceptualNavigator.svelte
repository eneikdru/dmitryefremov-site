<script lang="ts">
  export let theme: 'dark' | 'light' = 'dark';

  export interface Manifesto {
    id: string;
    title: string;
    author: string;
    description: string;
    year: string;
    category: string;
    citations: number;
    tags: string[];
    isPrimary?: boolean;
    excerpt?: string;
  }

  export interface FilterOption {
    name: string;
    count: number;
  }

  export let manifestos: Manifesto[] = [];
  export let filters: FilterOption[] = [];

  let searchQuery: string = '';
  let activeFilter: string = 'All';

  $: filteredManifestos = manifestos.filter(m => {
    let matchQuery = true;
    if (searchQuery.trim() !== '') {
      const q = searchQuery.toLowerCase();
      matchQuery = (
        m.title.toLowerCase().includes(q) ||
        m.author.toLowerCase().includes(q) ||
        m.description.toLowerCase().includes(q) ||
        (m.tags && m.tags.some(t => t.toLowerCase().includes(q))) ||
        (m.excerpt && m.excerpt.toLowerCase().includes(q))
      );
    }

    let matchFilter = true;
    if (activeFilter !== 'All') {
      matchFilter = m.category.toLowerCase() === activeFilter.toLowerCase();
    }

    return matchQuery && matchFilter;
  });

  function resetFilters() {
    searchQuery = '';
    activeFilter = 'All';
  }

  $: activeVector = activeFilter === 'All' ? 'ALL // CONCEPTS' : `${activeFilter.toUpperCase()} // VECTOR`;
</script>

<div class="px-gutter-sm max-w-md mx-auto flex flex-col gap-space-md w-full {theme === 'dark' ? 'dark' : ''}">
  <section class="mt-2">
    <div class="relative flex items-center bg-surface-container-low border border-outline-variant/40 rounded px-3 py-2.5 shadow-inner focus-within:border-secondary focus-within:ring-1 focus-within:ring-secondary/40 transition-all">
      <span class="material-symbols-outlined text-outline text-lg mr-2" data-icon="terminal">terminal</span>
      <input
        bind:value={searchQuery}
        class="w-full bg-transparent border-none p-0 text-on-surface placeholder:text-outline-variant font-label-md text-label-md focus:ring-0 focus:outline-none"
        placeholder="Search concepts, treatises, authors..."
        type="text"
      />
      <div class="flex items-center gap-1.5 ml-1">
        <button class="text-outline hover:text-secondary transition-colors" aria-label="Voice Query" title="Voice Query">
          <span class="material-symbols-outlined text-lg" data-icon="mic">mic</span>
        </button>
        <div class="h-3 w-px bg-outline-variant/40 mx-0.5"></div>
        <span class="inline-flex items-center px-1.5 py-0.5 rounded bg-surface-variant text-secondary font-label-sm text-[0.625rem] tracking-tight">
          ⌘K
        </span>
      </div>
    </div>

    <div class="flex items-center justify-between mt-2 px-1">
      <div class="flex items-center gap-1.5">
        <span class="w-1.5 h-1.5 rounded-full bg-secondary animate-pulse"></span>
        <span class="font-label-sm text-label-sm text-secondary">ACTIVE VECTOR:</span>
        <span class="font-label-sm text-label-sm text-on-surface-variant font-medium">{activeVector}</span>
      </div>
      <button
        on:click={resetFilters}
        class="font-label-sm text-[0.625rem] text-outline hover:text-secondary underline decoration-dotted transition-colors"
      >
        RESET
      </button>
    </div>
  </section>

  <section class="no-scrollbar overflow-x-auto -mx-gutter-sm px-gutter-sm py-1 flex items-center gap-2">
    {#each filters as filter}
      <button
        on:click={() => activeFilter = filter.name}
        class="flex-shrink-0 inline-flex items-center gap-1.5 px-3 py-1.5 rounded font-label-sm text-label-sm transition-all {activeFilter === filter.name ? 'bg-surface-container-high border border-secondary text-secondary glow-cyan' : 'bg-surface-container-low border border-outline-variant/30 text-on-surface-variant hover:border-secondary/60 hover:text-on-surface'}"
      >
        {#if activeFilter === filter.name}
          <span class="w-1 h-1 rounded-full bg-secondary"></span>
        {/if}
        <span>{filter.name}</span>
        <span class="{activeFilter === filter.name ? 'bg-secondary-container/20 text-secondary' : 'text-outline'} px-1 rounded text-[0.625rem]">{filter.count}</span>
      </button>
    {/each}
  </section>

  <section class="relative bg-surface-container-lowest border border-outline-variant/40 rounded overflow-hidden p-4">
    <div class="flex items-center justify-between mb-3 border-b border-outline-variant/20 pb-2">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-secondary text-base" data-icon="grain">grain</span>
        <h2 class="font-label-sm text-label-sm tracking-wider uppercase text-on-surface">SEMANTIC RADAR // LVL 01</h2>
      </div>
      <div class="flex items-center gap-1 text-outline font-label-sm text-[0.625rem]">
        <span class="material-symbols-outlined text-xs" data-icon="sync">sync</span>
        <span>5 SYNAPSES</span>
      </div>
    </div>

    <div class="relative w-full h-56 bg-surface-dim/80 rounded border border-outline-variant/20 overflow-hidden flex items-center justify-center">
      <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
        <div class="w-48 h-48 rounded-full border border-outline-variant/15 border-dashed"></div>
        <div class="absolute w-32 h-32 rounded-full border border-outline-variant/20"></div>
        <div class="absolute w-16 h-16 rounded-full border border-secondary/20 bg-secondary/5"></div>
        <div class="absolute w-full h-px bg-outline-variant/10"></div>
        <div class="absolute h-full w-px bg-outline-variant/10"></div>
      </div>

      <svg class="absolute inset-0 w-full h-full pointer-events-none" xmlns="http://www.w3.org/2000/svg">
        <line stroke="#03b5d3" stroke-dasharray="2 2" stroke-opacity="0.5" stroke-width="1.2" x1="50%" x2="25%" y1="50%" y2="28%"></line>
        <line stroke="#8083ff" stroke-opacity="0.6" stroke-width="1.5" x1="50%" x2="78%" y1="50%" y2="30%"></line>
        <line stroke="#8083ff" stroke-opacity="0.4" stroke-width="1" x1="50%" x2="20%" y1="50%" y2="76%"></line>
        <line stroke="#03b5d3" stroke-opacity="0.6" stroke-width="1.2" x1="50%" x2="72%" y1="50%" y2="72%"></line>
        <line stroke="#464554" stroke-opacity="0.5" stroke-width="0.8" x1="78%" x2="72%" y1="30%" y2="72%"></line>
      </svg>

      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-10 flex flex-col items-center cursor-pointer group">
        <div class="w-3.5 h-3.5 rounded-full bg-secondary border-2 border-background glow-cyan group-hover:scale-125 transition-transform"></div>
        <span class="mt-1 px-1.5 py-0.5 rounded bg-surface/90 border border-secondary/40 font-label-sm text-[0.5625rem] text-secondary whitespace-nowrap shadow-sm">
          Ecology of Mind
        </span>
      </div>

      <div class="absolute top-4 left-4 z-10 flex flex-col items-start cursor-pointer group">
        <div class="flex items-center gap-1">
          <div class="w-2.5 h-2.5 rounded-full bg-primary border border-background glow-indigo group-hover:scale-125 transition-transform"></div>
          <span class="px-1.5 py-0.5 rounded bg-surface-container/90 border border-outline-variant/30 font-label-sm text-[0.53125rem] text-on-surface whitespace-nowrap">
            Decentralized Intel.
          </span>
        </div>
      </div>

      <div class="absolute top-5 right-4 z-10 flex flex-col items-end cursor-pointer group">
        <div class="flex items-center gap-1">
          <span class="px-1.5 py-0.5 rounded bg-surface-container/90 border border-primary/40 font-label-sm text-[0.53125rem] text-primary whitespace-nowrap">
            Algorithmic Poetics
          </span>
          <div class="w-2.5 h-2.5 rounded-full bg-primary-fixed border border-background glow-indigo group-hover:scale-125 transition-transform"></div>
        </div>
      </div>

      <div class="absolute bottom-5 left-3 z-10 flex flex-col items-start cursor-pointer group">
        <div class="flex items-center gap-1">
          <div class="w-2 h-2 rounded-full bg-outline group-hover:scale-125 transition-transform"></div>
          <span class="px-1.5 py-0.5 rounded bg-surface-container/90 border border-outline-variant/30 font-label-sm text-[0.53125rem] text-on-surface-variant whitespace-nowrap">
            Tech Subversion
          </span>
        </div>
      </div>

      <div class="absolute bottom-5 right-4 z-10 flex flex-col items-end cursor-pointer group">
        <div class="flex items-center gap-1">
          <span class="px-1.5 py-0.5 rounded bg-surface-container/90 border border-secondary/40 font-label-sm text-[0.53125rem] text-secondary whitespace-nowrap">
            Solarpunk Protocols
          </span>
          <div class="w-2.5 h-2.5 rounded-full bg-secondary-fixed border border-background glow-cyan group-hover:scale-125 transition-transform"></div>
        </div>
      </div>

      <div class="absolute bottom-1 left-1/2 -translate-x-1/2 flex items-center gap-1 text-[0.5625rem] font-label-sm text-outline-variant tracking-tight bg-surface/70 px-2 py-0.5 rounded backdrop-blur-sm pointer-events-none">
        <span class="material-symbols-outlined text-[0.6875rem]" data-icon="pinch">pinch</span>
        <span>Drag or pinch to navigate constellation</span>
      </div>
    </div>

    <div class="flex items-center justify-between mt-3 pt-2 text-xs font-label-sm">
      <span class="text-outline">SEMANTIC CLUSTER: <strong class="text-on-surface font-normal">#SYN-088</strong></span>
      <button class="inline-flex items-center gap-1 text-secondary hover:underline">
        <span>Full Graph</span>
        <span class="material-symbols-outlined text-sm" data-icon="arrow_outward">arrow_outward</span>
      </button>
    </div>
  </section>

  {#if filteredManifestos.length === 0}
    <div class="py-12 flex flex-col items-center justify-center text-center bg-surface-container-low border border-outline-variant/20 rounded mt-4">
      <span class="material-symbols-outlined text-4xl text-outline mb-2" data-icon="search_off">search_off</span>
      <h3 class="font-headline-sm text-headline-sm text-on-surface mb-1">No Concept Results</h3>
      <p class="font-body-sm text-body-sm text-on-surface-variant mb-4 max-w-xs">
        No active trajectories matched your semantic query. Adjust parameters or clear the vector stream.
      </p>
      <button
        on:click={resetFilters}
        class="bg-surface-variant hover:bg-surface-bright text-on-surface font-label-sm text-label-sm px-4 py-2 rounded transition-colors"
      >
        Reset Filters
      </button>
    </div>
  {:else}
    <div class="flex items-baseline justify-between mt-1 px-1">
      <h3 class="font-headline-sm text-headline-sm text-on-surface italic">Dynamic Trajectories</h3>
      <span class="font-label-sm text-label-sm text-outline uppercase tracking-wider">{filteredManifestos.length} ENTRIES</span>
    </div>

    {#each filteredManifestos as manifesto}
      {#if manifesto.isPrimary}
        <article class="bg-surface-container-low border border-outline-variant/40 rounded p-4 relative overflow-hidden transition-all hover:border-secondary/50">
          <div class="absolute top-0 left-0 w-full h-[2px] bg-gradient-to-r from-transparent via-secondary to-transparent"></div>
          <div class="flex items-center justify-between text-outline mb-2">
            <span class="font-label-sm text-label-sm text-secondary tracking-widest uppercase">{manifesto.category} // {manifesto.year}</span>
            <div class="flex items-center gap-1 font-label-sm text-label-sm text-on-surface-variant">
              <span class="material-symbols-outlined text-sm" data-icon="schedule">schedule</span>
              <span>14 min read</span>
            </div>
          </div>
          <h4 class="font-headline-md text-headline-md text-on-surface font-medium leading-snug">
            {manifesto.title}
          </h4>
          <p class="font-body-sm text-body-sm text-on-surface-variant italic mb-3">
            {manifesto.author}
          </p>
          {#if manifesto.excerpt}
            <blockquote class="border-l-2 border-primary/40 pl-3 my-3">
              <p class="font-headline-md text-body-md text-on-surface/90 italic leading-relaxed">
                {manifesto.excerpt}
              </p>
            </blockquote>
          {/if}
          <div class="flex flex-wrap gap-1.5 my-3">
            {#each manifesto.tags as tag, i}
              <span class="px-2 py-0.5 rounded bg-surface-container border border-outline-variant/30 font-label-sm text-[0.625rem] {i === 1 ? 'text-secondary' : i === 2 ? 'text-primary' : 'text-on-surface'}">
                {tag}
              </span>
            {/each}
          </div>
          <div class="mt-4 pt-2 border-t border-outline-variant/20 flex items-center justify-between">
            <button class="w-full bg-primary hover:bg-primary-container text-on-primary font-label-md text-label-md uppercase font-bold py-2.5 px-4 rounded transition-all duration-150 flex items-center justify-center gap-2 group glow-indigo">
              <span>Explore Synthesis</span>
              <span class="material-symbols-outlined text-sm group-hover:translate-x-0.5 transition-transform" data-icon="explore">explore</span>
            </button>
          </div>
        </article>
      {:else}
        <article class="bg-surface-container-low border border-outline-variant/30 rounded p-4 transition-all hover:border-outline-variant">
          <div class="flex items-center justify-between mb-1.5">
            <span class="font-label-sm text-[0.625rem] {manifesto.category === 'ONTOLOGY' ? 'text-primary' : 'text-tertiary'} tracking-wider uppercase">{manifesto.category} // {manifesto.year}</span>
            <span class="font-label-sm text-[0.625rem] text-outline flex items-center gap-1">
              <span class="material-symbols-outlined text-xs" data-icon="format_quote">format_quote</span>
              <span>{manifesto.citations} Citations</span>
            </span>
          </div>
          <h5 class="font-headline-sm text-headline-sm text-on-surface leading-tight mb-1">
            {manifesto.title}
          </h5>
          <p class="font-body-sm text-body-sm text-on-surface-variant italic mb-2">
            {manifesto.author}
          </p>
          <p class="font-body-sm text-body-sm text-on-surface/80 line-clamp-2 leading-relaxed mb-3">
            {manifesto.description}
          </p>
          <div class="flex items-center justify-between pt-2 border-t border-outline-variant/20">
            <div class="flex gap-1.5 overflow-hidden">
              {#each manifesto.tags as tag, i}
                <span class="px-1.5 py-0.5 rounded bg-surface border font-label-sm text-[0.5625rem] {manifesto.category === 'ONTOLOGY' && i === 0 ? 'border-primary/30 text-primary' : manifesto.category === 'CYBERFEMINISM' && i === 0 ? 'border-tertiary-container/40 text-tertiary' : 'border-outline-variant/30 text-outline'}">
                  {tag}
                </span>
              {/each}
            </div>
            <button aria-label="Bookmark Manifesto" class="text-secondary hover:text-secondary-fixed transition-colors p-1">
              <span class="material-symbols-outlined text-lg" data-icon="bookmark_add">bookmark_add</span>
            </button>
          </div>
        </article>
      {/if}
    {/each}
  {/if}
</div>
