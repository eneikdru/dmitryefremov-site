<script lang="ts">
  import { onMount } from 'svelte';

  export let theme: 'dark' | 'light' = 'dark';

  let searchQuery = '';

  interface Manifesto {
    id: string;
    year: string;
    type: string;
    title: string;
    author: string;
    organization: string;
    tags: { label: string; class: string }[];
    description?: string;
    quote?: string;
    readTime: string;
    vectors: number;
  }

  const manifestos: Manifesto[] = [
    {
      id: 'cyborg',
      year: '1985',
      type: 'CLASSICAL TREATISE',
      title: 'A Cyborg Manifesto',
      author: 'Donna Haraway',
      organization: 'University of California, Santa Cruz',
      tags: [
        { label: '#Posthumanism', class: 'bg-secondary-container/20 text-secondary border-secondary/30' },
        { label: '#Cyberfeminism', class: 'bg-primary-container/10 text-primary-container border-primary-container/30' },
        { label: '#Techno-Mythology', class: 'bg-surface-container-high text-on-surface-variant border-outline-variant/40' }
      ],
      quote: '"The cyborg is a creature in a post-gender world; it has no truck with bisexuality, pre-oedipal symbiosis, unalienated labour, or other seductions to organic wholeness..."',
      readTime: '14 min read',
      vectors: 8
    },
    {
      id: 'solarpunk',
      year: '2019',
      type: 'CONTEMPORARY DECLARATION',
      title: 'The Solarpunk Manifesto',
      author: 'The Solarpunk Community',
      organization: 'Open Collaborative Framework',
      tags: [
        { label: '#Solarpunk', class: 'bg-surface-tint/10 text-surface-tint border-surface-tint/30' },
        { label: '#Eco-Futurism', class: 'bg-primary-container/10 text-primary-container border-primary-container/30' },
        { label: '#OpenSource', class: 'bg-surface-container-high text-on-surface-variant border-outline-variant/40' }
      ],
      description: 'A declaration advocating for a revolt of hope against ecological climate despair, synthesizing renewable energy systems, localized permaculture, and commons-based digital tooling.',
      readTime: '9 min read',
      vectors: 5
    },
    {
      id: 'cyberspace',
      year: '1996',
      type: 'NET HERITAGE',
      title: 'A Declaration of the Independence of Cyberspace',
      author: 'John Perry Barlow',
      organization: 'Electronic Frontier Foundation (EFF)',
      tags: [
        { label: '#DigitalCommons', class: 'bg-primary-container/10 text-primary-container border-primary-container/30' },
        { label: '#Crypto-Anarchism', class: 'bg-secondary-container/20 text-secondary border-secondary/30' },
        { label: '#Sovereignty', class: 'bg-surface-container-high text-on-surface-variant border-outline-variant/40' }
      ],
      quote: '"Governments of the Industrial World, you weary giants of flesh and steel, I come from Cyberspace, the new home of Mind..."',
      readTime: '6 min read',
      vectors: 11
    }
  ];

  $: filteredManifestos = manifestos.filter(m =>
    m.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    (m.description && m.description.toLowerCase().includes(searchQuery.toLowerCase())) ||
    (m.quote && m.quote.toLowerCase().includes(searchQuery.toLowerCase())) ||
    m.author.toLowerCase().includes(searchQuery.toLowerCase())
  );

  function resetFilters() {
    searchQuery = '';
  }
</script>

<div class="space-y-6">
  <section class="mt-space-sm">
    <div class="relative group">
      <div class="absolute -inset-0.5 bg-gradient-to-r from-primary-container/30 to-secondary/30 rounded-xl blur-sm opacity-50 group-focus-within:opacity-100 transition duration-300"></div>
      <div class="relative flex items-center bg-surface-container-low border border-outline-variant/60 rounded-xl px-space-md py-space-sm shadow-inner transition-colors duration-200 group-focus-within:border-primary-container/80">
        <span class="material-symbols-outlined text-primary-container text-[20px] mr-space-sm">travel_explore</span>
        <input
          bind:value={searchQuery}
          class="bg-transparent border-0 p-0 text-on-surface placeholder:text-outline font-body-md text-body-md focus:ring-0 focus:outline-none w-full"
          placeholder="Search concepts, authors, theses..."
          type="text"
        />
        <div class="flex items-center gap-1.5 ml-2 shrink-0">
          <button class="text-on-surface-variant hover:text-primary p-0.5 active:scale-95" aria-label="Filter Preferences">
            <span class="material-symbols-outlined text-[18px]">filter_list</span>
          </button>
        </div>
      </div>
    </div>

    <div class="flex items-center gap-1.5 mt-2 overflow-x-auto no-scrollbar py-0.5">
      <span class="font-label-sm text-label-sm text-outline uppercase tracking-wider shrink-0 mr-1">Probes:</span>
      <button class="inline-flex items-center gap-1 text-on-surface-variant bg-surface-container-low/70 border border-outline-variant/30 rounded-full px-2.5 py-1 font-label-sm text-label-sm hover:border-primary-container/50 hover:text-primary transition-all">
        <span class="w-1.5 h-1.5 rounded-full bg-primary-container"></span>
        Donna Haraway
      </button>
      <button class="inline-flex items-center gap-1 text-on-surface-variant bg-surface-container-low/70 border border-outline-variant/30 rounded-full px-2.5 py-1 font-label-sm text-label-sm hover:border-secondary/50 hover:text-secondary transition-all">
        <span class="w-1.5 h-1.5 rounded-full bg-secondary"></span>
        Accelerationism
      </button>
      <button class="inline-flex items-center gap-1 text-on-surface-variant bg-surface-container-low/70 border border-outline-variant/30 rounded-full px-2.5 py-1 font-label-sm text-label-sm hover:border-primary-container/50 hover:text-primary transition-all">
        <span class="w-1.5 h-1.5 rounded-full bg-tertiary-fixed-dim"></span>
        Solarpunk Core
      </button>
    </div>
  </section>

  <section>
    <div class="flex items-center gap-2 overflow-x-auto no-scrollbar py-1 -mx-margin-mobile px-margin-mobile sm:mx-0 sm:px-0">
      <button class="flex items-center gap-1.5 bg-primary-container text-on-primary font-label-sm text-label-sm px-3.5 py-1.5 rounded-full font-semibold shadow-sm shrink-0 active:scale-95 transition-all">
        <span class="w-1.5 h-1.5 rounded-full bg-on-primary"></span>
        All Domains (142)
      </button>
      <button class="flex items-center gap-1.5 bg-surface-container-low text-on-surface-variant border border-outline-variant/40 hover:border-primary-container/40 hover:text-primary font-label-sm text-label-sm px-3.5 py-1.5 rounded-full shrink-0 active:scale-95 transition-all">
        Post-Digital (28)
      </button>
      <button class="flex items-center gap-1.5 bg-surface-container-low text-on-surface-variant border border-outline-variant/40 hover:border-primary-container/40 hover:text-primary font-label-sm text-label-sm px-3.5 py-1.5 rounded-full shrink-0 active:scale-95 transition-all">
        Ecological Futures (19)
      </button>
      <button class="flex items-center gap-1.5 bg-surface-container-low text-on-surface-variant border border-outline-variant/40 hover:border-primary-container/40 hover:text-primary font-label-sm text-label-sm px-3.5 py-1.5 rounded-full shrink-0 active:scale-95 transition-all">
        Critical Theory (34)
      </button>
    </div>
  </section>

  <section class="space-y-space-md pt-2">
    <div class="flex items-baseline justify-between border-b border-outline-variant/20 pb-2">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-secondary text-[20px]">auto_stories</span>
        <h3 class="font-headline-sm text-headline-sm text-on-surface font-semibold">Trending Manifestos & Treatises</h3>
      </div>
      <span class="font-label-sm text-label-sm text-outline tracking-wide font-medium">{filteredManifestos.length} works</span>
    </div>

    {#if filteredManifestos.length === 0}
      <div class="flex flex-col items-center justify-center p-8 bg-surface-container-low/50 border border-outline-variant/30 rounded-xl text-center space-y-4">
        <span class="material-symbols-outlined text-[48px] text-outline-variant">search_off</span>
        <h4 class="font-headline-sm text-on-surface">No search results found</h4>
        <p class="font-body-md text-on-surface-variant">We couldn't find any manifestos or treatises matching "{searchQuery}".</p>
        <button on:click={resetFilters} type="button" class="bg-primary-container text-surface-container-lowest font-label-sm text-label-sm uppercase font-bold px-4 py-2 rounded-lg shadow-sm hover:shadow-cyan-500/20 active:scale-95 transition-all">
          Reset Filters
        </button>
      </div>
    {:else}
      {#each filteredManifestos as manifesto}
        <article class="bg-surface-container-low/80 backdrop-blur-md border border-outline-variant/40 hover:border-primary-container/40 rounded-xl p-space-md transition-all duration-200 hover:shadow-lg">
          <div class="flex items-start justify-between gap-2">
            <div class="space-y-1">
              <div class="flex items-center gap-2">
                <span class="bg-surface-container-high border border-outline-variant/50 text-primary-container font-label-sm text-label-sm px-2 py-0.5 rounded">{manifesto.year}</span>
                <span class="font-label-sm text-label-sm text-outline">{manifesto.type}</span>
              </div>
              <h4 class="font-headline-md text-headline-md text-on-surface font-semibold tracking-tight leading-snug">
                {manifesto.title}
              </h4>
              <p class="font-body-md text-body-md text-on-surface-variant">
                by <span class="text-on-surface font-medium">{manifesto.author}</span> · <span class="text-outline">{manifesto.organization}</span>
              </p>
            </div>
            <button aria-label="Bookmark Manifesto" class="w-8 h-8 rounded-full flex items-center justify-center text-secondary hover:bg-surface-container-high transition-colors">
              <span class="material-symbols-outlined text-[20px]">star</span>
            </button>
          </div>

          <div class="flex flex-wrap gap-1.5 my-space-sm">
            {#each manifesto.tags as tag}
              <span class="{tag.class} border rounded-full px-2.5 py-0.5 font-label-sm text-label-sm">{tag.label}</span>
            {/each}
          </div>

          {#if manifesto.description}
            <p class="font-body-md text-body-md text-on-surface-variant line-clamp-2 my-2">
              {manifesto.description}
            </p>
          {/if}

          {#if manifesto.quote}
            <blockquote class="my-space-sm pl-3.5 border-l-2 border-primary-container/80 bg-surface-container-lowest/40 py-2 pr-2 rounded-r-lg">
              <p class="font-headline-sm text-[16px] italic leading-relaxed text-on-surface/90 font-serif">
                {manifesto.quote}
              </p>
            </blockquote>
          {/if}

          <div class="flex items-center justify-between pt-2 border-t border-outline-variant/20">
            <div class="flex items-center gap-1.5 text-on-surface-variant font-label-sm text-label-sm">
              <span class="material-symbols-outlined text-[15px]">schedule</span>
              <span>{manifesto.readTime}</span>
            </div>
            <div class="flex items-center gap-2">
              <button class="text-primary-container hover:text-primary font-label-sm text-label-sm font-semibold flex items-center gap-1 bg-surface-container-high/60 px-2.5 py-1.5 rounded-lg border border-outline-variant/40 hover:border-primary-container/40 active:scale-95 transition-all">
                <span class="material-symbols-outlined text-[15px]">hub</span>
                <span>{manifesto.vectors} Vectors</span>
              </button>
              <button class="bg-primary-container text-surface-container-lowest font-label-sm text-label-sm uppercase font-bold px-3 py-1.5 rounded-lg shadow-sm hover:shadow-cyan-500/20 active:scale-95 transition-all">
                Read Treatise
              </button>
            </div>
          </div>
        </article>
      {/each}
    {/if}
  </section>
</div>
