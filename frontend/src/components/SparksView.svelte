<script lang="ts">
  import { onMount } from 'svelte';
  import {
    fetchSparksFromApi,
    filterSparks,
    toggleSparkFire,
    toggleSparkBookmark,
    SPARK_CATEGORIES,
    type SparkItem
  } from '../lib/sparks';

  export let theme: 'dark' | 'light' = 'dark';
  export let sparks: SparkItem[] = [];

  onMount(async () => {
    if (!sparks || sparks.length === 0) {
      const fetched = await fetchSparksFromApi();
      if (fetched && fetched.length > 0) {
        sparks = fetched;
      }
    }
  });

  let selectedCategory: string = 'All Sparks';
  let searchQuery: string = '';
  let isSearchVisible: boolean = false;
  let isAudioPlaying: Record<string, boolean> = {};

  $: filteredSparks = filterSparks(sparks, selectedCategory, searchQuery);

  function selectCategory(category: string) {
    selectedCategory = category;
  }

  function handleFire(id: string) {
    sparks = toggleSparkFire(sparks, id);
  }

  function handleBookmark(id: string) {
    sparks = toggleSparkBookmark(sparks, id);
  }

  function toggleAudio(id: string) {
    isAudioPlaying = {
      ...isAudioPlaying,
      [id]: !isAudioPlaying[id]
    };
  }
</script>

<div
  id="sparks-view-root"
  class="w-full max-w-[680px] mx-auto flex flex-col relative pb-16 transition-colors duration-200 {theme === 'dark' ? 'bg-[#0c0e12] text-[#e2e2e8]' : 'bg-[#f8fafc] text-[#0f172a]'}"
>
  <!-- TOP BAR Header -->
  <header
    id="sparks-header"
    class="sticky top-0 z-50 flex justify-between items-center w-full px-4 h-12 max-w-[680px] mx-auto backdrop-blur-md border-b {theme === 'dark' ? 'bg-[#0c0e12]/90 border-[#514532]/30' : 'bg-white/90 border-[#cbd5e1]'}"
  >
    <!-- Brand Logo -->
    <div class="flex items-center space-x-2">
      <h1 class="text-lg font-bold tracking-tight text-[#ffb300] select-none font-mono">SPARKS</h1>
    </div>

    <!-- Search Action Toggle -->
    <div class="flex items-center space-x-2">
      <button
        type="button"
        id="toggleSearchBtn"
        aria-label="Toggle search input"
        class="w-8 h-8 rounded-full flex items-center justify-center transition-transform active:scale-95 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {theme === 'dark' ? 'text-[#d6c4ac] hover:bg-[#282a2e] hover:text-[#ffb300]' : 'text-[#475569] hover:bg-[#e2e8f0]'}"
        on:click={() => (isSearchVisible = !isSearchVisible)}
      >
        <svg class="w-4 h-4 select-none" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
      </button>
    </div>
  </header>

  <!-- Search Input Bar (Expandable) -->
  {#if isSearchVisible}
    <div class="px-4 py-2 border-b {theme === 'dark' ? 'bg-[#1e2024] border-[#514532]/30' : 'bg-[#f1f5f9] border-[#cbd5e1]'}">
      <div class="relative flex items-center">
        <label for="spark-search-input" class="sr-only">Поиск по заметкам</label>
        <input
          id="spark-search-input"
          type="text"
          bind:value={searchQuery}
          placeholder="Поиск по заметкам и мыслям..."
          class="w-full pl-9 pr-4 py-1.5 text-xs rounded-lg focus:outline-none focus:ring-2 focus:ring-[#ffb300] border transition-all {theme === 'dark' ? 'bg-[#121418] border-[#514532]/50 text-white placeholder-[#9e8e78]' : 'bg-white border-[#cbd5e1] text-[#0f172a] placeholder-[#94a3b8]'}"
        />
        <svg class="w-4 h-4 absolute left-2.5 text-[#9e8e78]" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
        {#if searchQuery}
          <button
            type="button"
            class="absolute right-2.5 text-xs text-[#9e8e78] hover:text-white"
            on:click={() => (searchQuery = '')}
          >
            ✕
          </button>
        {/if}
      </div>
    </div>
  {/if}

  <!-- Horizontal Filter Rail (Categorical Stream Rails) -->
  <nav
    id="category-filter-rail"
    aria-label="Topic Streams"
    class="w-full px-4 py-2.5 overflow-x-auto flex items-center space-x-2 border-b no-scrollbar {theme === 'dark' ? 'bg-[#0c0e12] border-[#514532]/20' : 'bg-white border-[#e2e8f0]'}"
  >
    {#each SPARK_CATEGORIES as category}
      <button
        type="button"
        class="shrink-0 px-3 py-1 rounded-full text-xs font-mono transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {selectedCategory === category ? 'bg-[#ffb300]/15 border border-[#ffb300] text-[#ffb300] font-bold shadow-[0_0_12px_rgba(255,179,0,0.3)]' : (theme === 'dark' ? 'bg-[#1a1c20] border border-[#514532]/30 text-[#d6c4ac] hover:border-[#ffb300]/60 hover:text-[#ffb300]' : 'bg-[#f1f5f9] border border-[#cbd5e1] text-[#475569] hover:border-[#0284c7] hover:text-[#0284c7]')}"
        aria-pressed={selectedCategory === category}
        on:click={() => selectCategory(category)}
      >
        {#if category === 'All Sparks'}
          <span class="inline-flex items-center gap-1">
            <span>Все записи</span>
          </span>
        {:else}
          <span>{category}</span>
        {/if}
      </button>
    {/each}
  </nav>

  <!-- Feed Container -->
  <div class="flex w-full">
    <main
      id="sparks-feed-list"
      role="feed"
      aria-busy={false}
      class="flex-1 flex flex-col divide-y transition-all {theme === 'dark' ? 'divide-[#514532]/20' : 'divide-[#e2e8f0]'}"
    >
      {#if filteredSparks.length === 0}
        <div id="sparks-empty-state" class="py-16 text-center space-y-2">
          <p class="text-sm font-mono {theme === 'dark' ? 'text-[#d6c4ac]' : 'text-[#64748b]'}">
            Искры и записи отсутствуют
          </p>
          <p class="text-xs {theme === 'dark' ? 'text-[#9e8e78]' : 'text-[#94a3b8]'}">
            Попробуйте изменить категорию фильтрации или поисковый запрос
          </p>
        </div>
      {:else}
        {#each filteredSparks as spark (spark.id)}
          <article
            class="spark-card relative p-4 transition-all duration-200 {theme === 'dark' ? 'hover:bg-[#14161b]/60' : 'hover:bg-[#f1f5f9]/60'}"
            aria-labelledby="spark-title-{spark.id}"
          >
            <!-- Author & Meta Header Row -->
            <div class="flex items-center justify-between text-xs mb-2">
              <div class="flex items-center space-x-2">
                <div class="w-6 h-6 rounded-full flex items-center justify-center font-bold text-xs select-none bg-[#ffb300] text-[#0c0e12]">
                  {spark.authorInitial || 'E'}
                </div>
                <div class="flex flex-col">
                  <span class="font-bold text-xs font-mono {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                    {spark.author}
                  </span>
                </div>
              </div>

              <div class="flex items-center space-x-3 text-xs font-mono {theme === 'dark' ? 'text-[#9e8e78]' : 'text-[#64748b]'}">
                <span>{spark.timestamp}</span>
              </div>
            </div>

            <!-- Title (if available) -->
            {#if spark.title}
              <h2
                id="spark-title-{spark.id}"
                class="font-mono font-bold text-sm tracking-tight mb-2 text-[#ffb300]"
              >
                {spark.title}
              </h2>
            {/if}

            <!-- Card Body / Quantized Thought Text -->
            <div class="space-y-3 font-sans text-sm leading-relaxed {theme === 'dark' ? 'text-[#e2e2e8]' : 'text-[#1e293b]'}">
              <p class="whitespace-pre-wrap">{spark.body}</p>

              <!-- Theorem Contrast Layout -->
              {#if spark.type === 'theorem' && spark.theoremContrast}
                <div class="rounded-lg p-3 text-xs space-y-2 border font-mono {theme === 'dark' ? 'bg-[#181a1f] border-[#514532]/30' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
                  <div class="flex items-start space-x-2">
                    <span class="text-red-400 font-bold shrink-0">Линейно:</span>
                    <span class="{theme === 'dark' ? 'text-[#d6c4ac]' : 'text-[#475569]'}">{spark.theoremContrast.linear}</span>
                  </div>
                  <div class="flex items-start space-x-2">
                    <span class="text-[#ffb300] font-bold shrink-0">Нелинейно:</span>
                    <span class="{theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">{spark.theoremContrast.nonLinear}</span>
                  </div>
                </div>
              {/if}

              <!-- Framework Steps Layout -->
              {#if spark.type === 'framework' && spark.frameworkSteps}
                <div class="rounded-lg p-3 text-xs space-y-1.5 border font-mono {theme === 'dark' ? 'bg-[#181a1f] border-[#514532]/30' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
                  {#each spark.frameworkSteps as step}
                    <div class="flex items-start space-x-2">
                      <span class="text-[#00d6e3] font-bold shrink-0">{step.id}.</span>
                      <span class="{theme === 'dark' ? 'text-[#d6c4ac]' : 'text-[#475569]'}">{step.text}</span>
                    </div>
                  {/each}
                </div>
              {/if}

              <!-- Audio Snippet Player (if voice memo) -->
              {#if spark.audioUrl}
                <div class="flex items-center space-x-3 p-2 rounded-lg border text-xs font-mono {theme === 'dark' ? 'bg-[#181a1f] border-[#514532]/30 text-[#d6c4ac]' : 'bg-[#f8fafc] border-[#cbd5e1] text-[#475569]'}">
                  <button
                    type="button"
                    aria-label={isAudioPlaying[spark.id] ? "Pause audio snippet" : "Play audio snippet"}
                    class="w-7 h-7 rounded-full flex items-center justify-center bg-[#ffb300] text-[#0c0e12] font-bold cursor-pointer transition-transform active:scale-95"
                    on:click={() => toggleAudio(spark.id)}
                  >
                    {#if isAudioPlaying[spark.id]}
                      ⏸
                    {:else}
                      ▶
                    {/if}
                  </button>
                  <span class="truncate">Голосовая заметка ({spark.audioDuration || '0:45'})</span>
                </div>
              {/if}
            </div>

            <!-- Footer Meta & Actions -->
            <div class="mt-3 pt-2 flex items-center justify-between border-t border-dashed {theme === 'dark' ? 'border-[#514532]/20' : 'border-[#e2e8f0]'}">
              <div class="flex items-center space-x-2">
                <span class="text-[11px] font-mono px-2 py-0.5 rounded {theme === 'dark' ? 'bg-[#1a1c20] text-[#ffb300]/80' : 'bg-[#f1f5f9] text-[#0284c7]'}">
                  {spark.tag}
                </span>
                {#if spark.link}
                  <a
                    href={spark.link}
                    target="_blank"
                    rel="noopener noreferrer"
                    class="text-[11px] font-mono text-[#00d6e3] hover:underline inline-flex items-center gap-1"
                  >
                    <span>LinkedIn</span>
                    <span aria-hidden="true">↗</span>
                  </a>
                {/if}
              </div>

              <div class="flex items-center space-x-2">
                <button
                  type="button"
                  aria-label="Fire spark"
                  class="w-7 h-7 rounded-full flex items-center justify-center border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {spark.isFired ? 'bg-[#ffb300]/20 border-[#ffb300] text-[#ffb300]' : (theme === 'dark' ? 'bg-white/5 border-white/10 text-[#d6c4ac] hover:text-[#ffb300]' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#475569] hover:text-[#0284c7]')}"
                  on:click={() => handleFire(spark.id)}
                >
                  <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20" aria-hidden="true"><path fill-rule="evenodd" d="M12.395 2.553a1 1 0 00-1.45-.385c-.345.23-.614.558-.822.88-.316.492-.533 1.035-.688 1.587a11.95 11.95 0 00-.51 2.87c-.244-.24-.51-.462-.792-.663a1 1 0 00-1.385.253C6.31 7.644 6 8.784 6 10c0 3.314 2.686 6 6 6s6-2.686 6-6c0-2.433-1.01-4.63-2.63-6.19a8.97 8.97 0 00-2.975-1.257z" clip-rule="evenodd"></path></svg>
                </button>
                <button
                  type="button"
                  aria-label="Bookmark spark"
                  class="w-7 h-7 rounded-full flex items-center justify-center border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {spark.isBookmarked ? 'bg-[#00d6e3]/20 border-[#00d6e3] text-[#00d6e3]' : (theme === 'dark' ? 'bg-white/5 border-white/10 text-[#d6c4ac] hover:text-[#ffb300]' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#475569] hover:text-[#0284c7]')}"
                  on:click={() => handleBookmark(spark.id)}
                >
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"></path></svg>
                </button>
              </div>
            </div>
          </article>
        {/each}
      {/if}
    </main>
  </div>
</div>
