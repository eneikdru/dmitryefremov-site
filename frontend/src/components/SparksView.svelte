<script lang="ts">
  import {
    getInitialSparks,
    filterSparks,
    createSpark,
    toggleSparkFire,
    toggleSparkBookmark,
    calculateTelemetry,
    SPARK_CATEGORIES,
    type SparkItem,
    type StreamMode
  } from '../lib/sparks';

  export let theme: 'dark' | 'light' = 'dark';

  let sparks: SparkItem[] = getInitialSparks();
  let streamMode: StreamMode = 'HYPER';
  let selectedCategory: string = 'All Sparks';
  let searchQuery: string = '';
  let isSearchVisible: boolean = false;
  let newSparkText: string = '';
  let isAudioPlaying: Record<string, boolean> = {};

  $: filteredSparks = filterSparks(sparks, selectedCategory, searchQuery);
  $: telemetry = calculateTelemetry(sparks);
  $: charsRemaining = 180 - newSparkText.length;

  function setMode(mode: StreamMode) {
    streamMode = mode;
  }

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

  function handleSubmitSpark() {
    if (!newSparkText.trim()) return;
    const created = createSpark(newSparkText);
    sparks = [created, ...sparks];
    newSparkText = '';
  }

  function handleKeydownComposer(e: KeyboardEvent) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmitSpark();
    }
  }

  function formatSparksCount(count: number): string {
    if (count >= 1000) {
      return (count / 1000).toFixed(1).replace(/\.0$/, '') + 'k';
    }
    return count.toString();
  }
</script>

<div
  id="sparks-view-root"
  class="w-full max-w-[680px] mx-auto flex flex-col relative pb-32 transition-colors duration-200 {theme === 'dark' ? 'bg-[#0c0e12] text-[#e2e2e8]' : 'bg-[#f8fafc] text-[#0f172a]'}"
>
  <!-- TOP BAR Header with Live Pulse & Metrics -->
  <header
    id="sparks-header"
    class="sticky top-0 z-50 flex justify-between items-center w-full px-4 h-12 max-w-[680px] mx-auto backdrop-blur-md border-b {theme === 'dark' ? 'bg-[#0c0e12]/90 border-[#514532]/30' : 'bg-white/90 border-[#cbd5e1]'}"
  >
    <!-- Brand Logo & Live Pulse -->
    <div class="flex items-center space-x-2">
      <span class="text-[#ffb300] font-bold text-xl select-none" aria-hidden="true">⚡</span>
      <h1 class="text-lg font-bold tracking-tight text-[#ffb300] select-none font-mono">SPARKS</h1>
      <!-- Stream Pulse Telemetry Badge -->
      <span
        id="pulse-badge"
        class="inline-flex items-center px-2 py-0.5 rounded-full bg-[#ffb300]/10 border border-[#ffb300]/30 text-[#ffb300] text-xs font-mono font-medium ml-1.5 animate-pulse"
      >
        <span class="w-1.5 h-1.5 rounded-full bg-[#ffb300] mr-1.5" aria-hidden="true"></span>
        {telemetry.sparksPerMinute} spk/m
      </span>
    </div>

    <!-- Trailing Actions & Stream Switcher -->
    <div class="flex items-center space-x-2">
      <!-- Stream Mode Switcher -->
      <div
        class="flex items-center rounded-full p-0.5 border {theme === 'dark' ? 'bg-[#1a1c20] border-[#514532]/40' : 'bg-[#f1f5f9] border-[#cbd5e1]'}"
        role="group"
        aria-label="Stream Display Mode"
      >
        <button
          type="button"
          id="hyperModeBtn"
          class="px-2.5 py-0.5 rounded-full text-xs font-mono font-bold transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {streamMode === 'HYPER' ? 'bg-[#ffb300]/20 text-[#ffb300]' : 'text-[#d6c4ac] hover:text-[#ffb300]'}"
          aria-pressed={streamMode === 'HYPER'}
          on:click={() => setMode('HYPER')}
        >
          HYPER
        </button>
        <button
          type="button"
          id="focusModeBtn"
          class="px-2.5 py-0.5 rounded-full text-xs font-mono font-bold transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {streamMode === 'FOCUS' ? 'bg-[#ffb300]/20 text-[#ffb300]' : 'text-[#d6c4ac] hover:text-[#ffb300]'}"
          aria-pressed={streamMode === 'FOCUS'}
          on:click={() => setMode('FOCUS')}
        >
          FOCUS
        </button>
      </div>

      <!-- Search Action Toggle -->
      <button
        type="button"
        id="toggleSearchBtn"
        aria-label="Toggle search input"
        class="w-8 h-8 rounded-full flex items-center justify-center transition-transform active:scale-95 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {theme === 'dark' ? 'text-[#d6c4ac] hover:bg-[#282a2e] hover:text-[#ffb300]' : 'text-[#475569] hover:bg-[#e2e8f0]'}"
        on:click={() => (isSearchVisible = !isSearchVisible)}
      >
        <span class="text-base select-none" aria-hidden="true">🔍</span>
      </button>
    </div>
  </header>

  <!-- Search Input Bar (Expandable) -->
  {#if isSearchVisible}
    <div class="px-4 py-2 border-b {theme === 'dark' ? 'bg-[#1e2024] border-[#514532]/30' : 'bg-[#f1f5f9] border-[#cbd5e1]'}">
      <div class="relative flex items-center">
        <label for="spark-search-input" class="sr-only">Search Sparks</label>
        <input
          id="spark-search-input"
          type="text"
          bind:value={searchQuery}
          placeholder="Filter sparks by keyword, author, or tag..."
          class="w-full px-3 py-1.5 rounded-lg text-sm font-mono border focus:outline-none focus:ring-2 focus:ring-[#ffb300] {theme === 'dark' ? 'bg-[#111317] text-[#e2e2e8] border-[#514532]/50 placeholder-[#9e8e78]' : 'bg-white text-[#0f172a] border-[#cbd5e1] placeholder-[#94a3b8]'}"
        />
        {#if searchQuery}
          <button
            type="button"
            aria-label="Clear search"
            class="absolute right-2 text-xs font-mono px-1.5 py-0.5 rounded hover:bg-[#ffb300]/20 text-[#ffb300]"
            on:click={() => (searchQuery = '')}
          >
            Clear
          </button>
        {/if}
      </div>
    </div>
  {/if}

  <!-- Velocity Status Ribbon & Micro-Counter -->
  <section
    id="velocity-ribbon"
    class="w-full px-4 py-2 flex items-center justify-between border-b text-xs font-mono {theme === 'dark' ? 'bg-[#0c0e12]/80 border-[#514532]/20 text-[#d6c4ac]' : 'bg-[#f8fafc]/80 border-[#e2e8f0] text-[#475569]'}"
  >
    <div class="flex items-center space-x-2">
      <span class="text-[#00d6e3] font-bold" aria-hidden="true">⚡</span>
      <span>THROUGHPUT: <strong class="text-[#00d6e3] font-bold">{telemetry.throughputWpm} WPM</strong></span>
    </div>
    <div class="flex items-center space-x-1.5 px-2 py-0.5 rounded border {theme === 'dark' ? 'bg-[#282a2e]/60 border-[#514532]/30 text-[#e2e2e8]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}">
      <span class="text-[#ffb300] font-bold text-xs" aria-hidden="true">⚡</span>
      <span class="font-bold">{telemetry.consumedToday} sparks</span>
      <span class="opacity-70">consumed today</span>
    </div>
  </section>

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
            <span class="text-[#ffb300]" aria-hidden="true">⚡</span>
            <span>All Sparks</span>
          </span>
        {:else}
          <span>{category}</span>
        {/if}
      </button>
    {/each}
  </nav>

  <!-- FEED MAIN CONTENT AREA WITH SPEED SCRUBBER RAIL -->
  <div class="relative w-full px-4 mt-3 flex">
    <!-- Main Feed Stream Cards Stack -->
    <main class="w-full flex flex-col space-y-3 pr-2.5" id="sparkFeed" aria-label="Sparks Stream Feed">
      {#if filteredSparks.length === 0}
        <div class="p-8 text-center rounded-xl border border-dashed font-mono text-sm opacity-70">
          No sparks found matching current filters.
        </div>
      {:else}
        {#each filteredSparks as spark (spark.id)}
          <article
            id="spark-card-{spark.id}"
            class="rounded-xl p-4 transition-all duration-150 relative overflow-hidden group border {spark.type === 'quote' ? 'border-l-4 border-l-[#ffb300] border-t border-r border-b' : ''} {theme === 'dark' ? 'bg-[#14171F] border-[#333539] hover:border-[#ffb300]/40' : 'bg-white border-[#e2e8f0] shadow-sm hover:border-[#0284c7]'}"
          >
            <!-- Lateral speed indicator hairline for HYPER mode -->
            {#if streamMode === 'HYPER'}
              <div class="absolute left-0 top-0 bottom-0 w-[3px] bg-[#ffb300] opacity-80" aria-hidden="true"></div>
            {/if}

            <!-- Header Row -->
            <div class="flex items-center justify-between mb-2 font-mono">
              <div class="flex items-center space-x-2 text-xs">
                <span class="w-6 h-6 rounded-full flex items-center justify-center font-bold border {theme === 'dark' ? 'bg-[#282a2e] border-[#514532]/40 text-[#ffb300]' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#0284c7]'}">
                  {spark.authorInitial}
                </span>
                <span class="font-bold font-sans text-sm {theme === 'dark' ? 'text-[#e2e2e8]' : 'text-[#0f172a]'}">{spark.author}</span>
                <span class="w-1 h-1 rounded-full bg-[#9e8e78]/60" aria-hidden="true"></span>
                <span class="text-[#9e8e78]">{spark.timestamp}</span>
              </div>

              {#if spark.readTime}
                <div class="flex items-center space-x-1 text-xs text-[#00d6e3] bg-[#00d6e3]/10 border border-[#00d6e3]/20 px-2 py-0.5 rounded-full">
                  <span>Read: {spark.readTime}</span>
                </div>
              {:else if spark.type === 'framework'}
                <span class="text-[10px] font-bold px-1.5 py-0.5 rounded bg-[#1a1c20] text-[#9e8e78] border border-[#514532]/30">FRAMEWORK</span>
              {:else if spark.type === 'quote'}
                <span class="inline-flex items-center text-[#ffb300] text-xs font-bold">
                  <span class="mr-1" aria-hidden="true">🧠</span>
                  HIGH RESONANCE
                </span>
              {:else if spark.type === 'audio' && spark.audioSnippet}
                <span class="inline-flex items-center px-2 py-0.5 rounded-full bg-[#00d6e3]/10 border border-[#00d6e3]/30 text-[#00d6e3] text-xs">
                  <span class="mr-1" aria-hidden="true">🎙️</span>
                  {spark.audioSnippet.duration} voice snippet
                </span>
              {:else if spark.type === 'theorem'}
                <span class="text-xs font-bold px-1.5 py-0.5 rounded bg-[#ffb300]/10 text-[#ffb300] border border-[#ffb300]/20">THEOREM</span>
              {/if}
            </div>

            <!-- Body Content according to Spark Type -->
            {#if spark.type === 'insight'}
              <p class="text-base font-medium leading-snug tracking-tight mb-3 {theme === 'dark' ? 'text-[#e2e2e8]' : 'text-[#0f172a]'}">
                {spark.body}
              </p>
            {:else if spark.type === 'framework'}
              {#if spark.title}
                <h2 class="text-base font-bold mb-2 {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">{spark.title}</h2>
              {/if}

              {#if spark.frameworkSteps}
                <div class="grid grid-cols-1 gap-1.5 mb-3 p-2.5 rounded-lg border text-xs font-mono {theme === 'dark' ? 'bg-[#0c0e12]/80 border-[#514532]/30' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
                  {#each spark.frameworkSteps as step}
                    {#if step.isEliminated}
                      <div class="flex items-center justify-between text-[#ff5252] line-through opacity-80 pl-6">
                        <span>{step.text}</span>
                        {#if step.action}
                          <span class="text-[10px] uppercase font-bold text-[#ff5252] bg-[#ff5252]/10 px-1 rounded">{step.action}</span>
                        {/if}
                      </div>
                    {:else if step.id === 3}
                      <div class="flex items-center space-x-2 text-[#ffb300]">
                        <span class="w-4 h-4 rounded-full bg-[#ffb300]/20 flex items-center justify-center text-[10px] font-bold">{step.id}</span>
                        <span class="font-bold">{step.text}</span>
                      </div>
                    {:else}
                      <div class="flex items-center space-x-2 {theme === 'dark' ? 'text-[#d6c4ac]' : 'text-[#475569]'}">
                        <span class="w-4 h-4 rounded-full bg-white/10 flex items-center justify-center text-[10px]">{step.id}</span>
                        <span>{step.text}</span>
                      </div>
                    {/if}
                  {/each}
                </div>
              {/if}

              {#if spark.body}
                <p class="text-sm font-semibold text-[#ffb300] mb-3">{spark.body}</p>
              {/if}
            {:else if spark.type === 'quote'}
              <blockquote class="text-base font-bold italic tracking-tight mb-3 pl-3 border-l-2 border-[#ffb300]/50 {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                {spark.body}
              </blockquote>
            {:else if spark.type === 'audio' && spark.audioSnippet}
              <div class="flex items-center space-x-3 p-2 rounded-lg border mb-3 {theme === 'dark' ? 'bg-[#0c0e12] border-[#514532]/30' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
                <button
                  type="button"
                  aria-label={isAudioPlaying[spark.id] ? 'Pause spark audio' : 'Play spark audio'}
                  class="w-8 h-8 rounded-full bg-[#00d6e3] text-[#002022] flex items-center justify-center shadow hover:opacity-90 active:scale-95 transition-transform shrink-0 font-bold cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#00d6e3]"
                  on:click={() => toggleAudio(spark.id)}
                >
                  {#if isAudioPlaying[spark.id]}
                    ⏸
                  {:else}
                    ▶
                  {/if}
                </button>

                <!-- Kinetic Waveform Simulator -->
                <div class="flex items-center space-x-1 flex-1 h-6" aria-hidden="true">
                  <span class="w-1 h-2 bg-[#00d6e3]/40 rounded-full"></span>
                  <span class="w-1 h-4 bg-[#00d6e3]/70 rounded-full"></span>
                  <span class="w-1 h-6 bg-[#00d6e3] rounded-full"></span>
                  <span class="w-1 h-3 bg-[#00d6e3]/80 rounded-full"></span>
                  <span class="w-1 h-5 bg-[#00d6e3] rounded-full"></span>
                  <span class="w-1 h-4 bg-[#00d6e3]/60 rounded-full"></span>
                  <span class="w-1 h-2 bg-[#00d6e3]/40 rounded-full"></span>
                  <span class="w-1 h-5 bg-[#00d6e3]/90 rounded-full"></span>
                  <span class="w-1 h-3 bg-[#00d6e3]/50 rounded-full"></span>
                  <span class="w-1 h-6 bg-[#00d6e3] rounded-full"></span>
                  <span class="w-1 h-2 bg-[#00d6e3]/30 rounded-full"></span>
                  <span class="w-1 h-4 bg-[#00d6e3]/60 rounded-full"></span>
                  <span class="w-1 h-1 bg-[#00d6e3]/20 rounded-full"></span>
                </div>

                <span class="text-xs font-mono text-[#9e8e78]">{spark.audioSnippet.duration}</span>
              </div>

              <p class="text-sm tracking-tight mb-3 {theme === 'dark' ? 'text-[#e2e2e8]' : 'text-[#0f172a]'}">
                <span class="text-[#00d6e3] font-mono text-xs mr-1">[TRANSCRIPT]</span>
                {spark.audioSnippet.transcript}
              </p>
            {:else if spark.type === 'theorem' && spark.theoremContrast}
              <div class="grid grid-cols-2 gap-2 mb-3">
                <div class="p-2.5 rounded-lg border flex flex-col justify-between {theme === 'dark' ? 'bg-[#0c0e12]/80 border-[#514532]/30' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
                  <span class="text-[10px] font-mono text-[#9e8e78] mb-1 font-bold">LINEAR THINKING</span>
                  <p class="text-xs font-medium {theme === 'dark' ? 'text-[#d6c4ac]' : 'text-[#475569]'}">{spark.theoremContrast.linear}</p>
                </div>
                <div class="p-2.5 rounded-lg border border-[#ffb300]/40 flex flex-col justify-between bg-[#ffb300]/10">
                  <span class="text-[10px] font-mono text-[#ffb300] font-bold mb-1">NON-LINEAR BETS</span>
                  <p class="text-xs font-semibold text-[#ffb300]">{spark.theoremContrast.nonLinear}</p>
                </div>
              </div>
            {/if}

            <!-- Footer Metadata & Interactive Actions -->
            <div class="flex items-center justify-between pt-2 border-t font-mono text-xs {theme === 'dark' ? 'border-white/5' : 'border-[#e2e8f0]'}">
              <div class="flex items-center space-x-2">
                <span class="px-2 py-0.5 rounded border text-[#ffb300] bg-[#ffb300]/10 border-[#ffb300]/20">
                  {spark.tag}
                </span>
                <span class="text-[#9e8e78] flex items-center">
                  <span class="text-[#ffb300] mr-0.5" aria-hidden="true">⚡</span>
                  {formatSparksCount(spark.sparksCount)} sparks
                </span>
              </div>

              <div class="flex items-center space-x-2">
                <button
                  type="button"
                  aria-label="Fire spark"
                  class="w-7 h-7 rounded-full flex items-center justify-center border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {spark.isFired ? 'bg-[#ffb300]/20 border-[#ffb300] text-[#ffb300]' : (theme === 'dark' ? 'bg-white/5 border-white/10 text-[#d6c4ac] hover:text-[#ffb300]' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#475569] hover:text-[#0284c7]')}"
                  on:click={() => handleFire(spark.id)}
                >
                  🔥
                </button>
                <button
                  type="button"
                  aria-label="Bookmark spark"
                  class="w-7 h-7 rounded-full flex items-center justify-center border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {spark.isBookmarked ? 'bg-[#00d6e3]/20 border-[#00d6e3] text-[#00d6e3]' : (theme === 'dark' ? 'bg-white/5 border-white/10 text-[#d6c4ac] hover:text-[#ffb300]' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#475569] hover:text-[#0284c7]')}"
                  on:click={() => handleBookmark(spark.id)}
                >
                  🔖
                </button>
              </div>
            </div>
          </article>
        {/each}
      {/if}
    </main>

    <!-- Tactical Right-Rail Speed Scrubber Dots -->
    <aside
      aria-label="Stream Velocity Timeline"
      class="w-3 shrink-0 flex flex-col items-center justify-start space-y-4 pt-4 select-none opacity-60 hover:opacity-100 transition-opacity"
    >
      <div class="w-1.5 h-4 rounded-full bg-[#ffb300] animate-pulse" title="Current stream position"></div>
      <div class="w-1 h-1 rounded-full bg-[#9e8e78]"></div>
      <div class="w-1 h-1 rounded-full bg-[#9e8e78]"></div>
      <div class="w-1 h-1 rounded-full bg-[#00d6e3]"></div>
      <div class="w-1 h-1 rounded-full bg-[#9e8e78]"></div>
      <div class="w-1 h-1 rounded-full bg-[#9e8e78]"></div>
      <div class="w-1 h-1 rounded-full bg-[#9e8e78]"></div>
      <div class="w-1 h-2 rounded-full bg-[#9e8e78]/60"></div>
    </aside>
  </div>

  <!-- QUICK-SPARK CAPTURE BAR (Floating Composer Bar) -->
  <div class="fixed bottom-16 left-1/2 -translate-x-1/2 w-full max-w-[680px] px-4 z-40 pointer-events-none">
    <form
      id="quick-spark-form"
      on:submit|preventDefault={handleSubmitSpark}
      class="pointer-events-auto backdrop-blur-xl border rounded-full p-1.5 flex items-center shadow-2xl space-x-2 {theme === 'dark' ? 'bg-[#1D222D]/95 border-white/15' : 'bg-white/95 border-[#cbd5e1]'}"
    >
      <button
        type="button"
        aria-label="Audio spark record"
        class="w-8 h-8 rounded-full flex items-center justify-center shrink-0 transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300] {theme === 'dark' ? 'bg-[#282a2e]/80 text-[#d6c4ac] hover:text-[#ffb300]' : 'bg-[#f1f5f9] text-[#475569] hover:text-[#0284c7]'}"
      >
        🎙️
      </button>

      <label for="spark-composer-input" class="sr-only">Type a spark thought</label>
      <input
        id="spark-composer-input"
        type="text"
        maxlength="180"
        bind:value={newSparkText}
        on:keydown={handleKeydownComposer}
        placeholder="Type a spark thought... (max 180 chars)"
        class="bg-transparent border-0 text-sm focus:ring-0 flex-1 px-1 py-1 focus:outline-none {theme === 'dark' ? 'text-[#e2e2e8] placeholder-[#9e8e78]' : 'text-[#0f172a] placeholder-[#94a3b8]'}"
      />

      <div class="text-xs font-mono opacity-60 pr-1 hidden sm:block">
        {charsRemaining}
      </div>

      <!-- Send Bolt Trigger Button -->
      <button
        type="submit"
        id="dispatch-spark-btn"
        aria-label="Dispatch Spark"
        class="w-8 h-8 rounded-full bg-[#ffb300] text-[#0c0e12] flex items-center justify-center font-bold shadow-[0_0_16px_rgba(255,179,0,0.35)] hover:scale-105 active:scale-95 transition-transform shrink-0 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb300]"
      >
        ⚡
      </button>
    </form>
  </div>
</div>
