<script lang="ts">
  import { INITIAL_SPARKS, type SparkItem, toggleSparkCount, toggleBookmark, addQuantizedThought } from '../lib/sparks';

  export let sparks: SparkItem[] = [...INITIAL_SPARKS];
  export let activeFilter: 'All' | 'Epiphanies' | 'Tech/AI' | 'Philosophy' | 'Fragments' = 'All';

  let newThoughtText: string = '';
  let liveBuffer: string = '99.4%';
  let sparkCadence: string = '24 spk/m';
  let latencyMs: number = 12;

  $: filteredSparks = sparks.filter(item => {
    if (activeFilter === 'All') return true;
    if (activeFilter === 'Epiphanies') return item.sparksCount > 50 || item.category === 'BIO';
    if (activeFilter === 'Tech/AI') return item.category === 'AI' || item.category === 'SYSTEMS';
    if (activeFilter === 'Philosophy') return item.category === 'BIO' || item.domain === 'Philosophy of Mind';
    if (activeFilter === 'Fragments') return item.category === 'RAW' || item.isDraft;
    return true;
  });

  function handleFilterChange(filter: typeof activeFilter) {
    activeFilter = filter;
  }

  function handleSparkAction(id: string) {
    sparks = toggleSparkCount(sparks, id);
  }

  function handleBookmarkAction(id: string) {
    sparks = toggleBookmark(sparks, id);
  }

  function handleAddThought(e: Event) {
    e.preventDefault();
    if (!newThoughtText.trim()) return;
    sparks = addQuantizedThought(sparks, newThoughtText.trim());
    newThoughtText = '';
  }
</script>

<div id="sparks-stream-container" class="w-full flex flex-col gap-4 font-sans text-on-surface">
  <!-- Top App Header / Bar -->
  <header class="flex items-center justify-between px-4 py-3 bg-[#131315] border border-[#353437] rounded-xl backdrop-blur-md shadow-sm">
    <div class="flex items-center gap-2">
      <span class="text-[#ff9e00] font-bold text-lg" aria-hidden="true">⚡</span>
      <h2 class="font-bold tracking-tight text-white text-base sm:text-lg">SPARKS // STREAM</h2>
    </div>
    <div class="flex items-center gap-3">
      <div class="flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-[#201f22] border border-[#544433]/40">
        <span class="w-2 h-2 rounded-full bg-[#4cd7f6] animate-pulse"></span>
        <span class="font-mono text-xs text-[#4cd7f6]">{sparkCadence}</span>
      </div>
      <button
        type="button"
        aria-label="Stream Tuning Controls"
        class="w-8 h-8 rounded-lg flex items-center justify-center text-[#dac2ad] hover:bg-[#353437] transition-colors focus:outline-none focus:ring-2 focus:ring-[#ff9e00]"
      >
        <span aria-hidden="true">⚙</span>
      </button>
    </div>
  </header>

  <!-- Sub-Header Live Sync Metrics & Filter Pills -->
  <section class="p-3 border-b border-[#353437] bg-[#0e0e10]/90 rounded-xl space-y-2">
    <div class="flex flex-wrap items-center justify-between text-xs font-mono text-[#dac2ad] gap-2">
      <div class="flex items-center gap-2">
        <span class="text-[#4cd7f6] font-bold">● LIVE SYNC ACTIVE</span>
        <span class="text-[#544433]">•</span>
        <span class="text-[#a28d79]">Q-BUFFER: {liveBuffer}</span>
      </div>
      <div class="text-[#ffc586]">
        <span>LATENCY: {latencyMs}ms</span>
      </div>
    </div>

    <!-- Quantized Filter Pills (Keyboard Operable) -->
    <nav class="flex items-center gap-2 overflow-x-auto py-1" aria-label="Sparks Stream Filters">
      <button
        type="button"
        class="px-3 py-1 rounded font-mono text-xs font-semibold flex items-center gap-1.5 transition-all focus:outline-none focus:ring-2 focus:ring-[#ff9e00] {activeFilter === 'All' ? 'bg-[#ff9e00] text-[#482900]' : 'bg-[#201f22] text-[#dac2ad] border border-white/5 hover:bg-[#353437]'}"
        on:click={() => handleFilterChange('All')}
      >
        <span>All</span>
        <span class="bg-[#482900]/20 px-1 rounded text-[10px]">{sparks.length}</span>
      </button>

      <button
        type="button"
        class="px-3 py-1 rounded font-mono text-xs font-semibold flex items-center gap-1.5 transition-all focus:outline-none focus:ring-2 focus:ring-[#ff9e00] {activeFilter === 'Epiphanies' ? 'bg-[#ff9e00] text-[#482900]' : 'bg-[#201f22] text-[#dac2ad] border border-white/5 hover:bg-[#353437]'}"
        on:click={() => handleFilterChange('Epiphanies')}
      >
        <span class="text-[#ffc586]">⚡</span>
        <span>Epiphanies</span>
      </button>

      <button
        type="button"
        class="px-3 py-1 rounded font-mono text-xs font-semibold flex items-center gap-1.5 transition-all focus:outline-none focus:ring-2 focus:ring-[#ff9e00] {activeFilter === 'Tech/AI' ? 'bg-[#ff9e00] text-[#482900]' : 'bg-[#201f22] text-[#dac2ad] border border-white/5 hover:bg-[#353437]'}"
        on:click={() => handleFilterChange('Tech/AI')}
      >
        <span class="text-[#4cd7f6]">λ</span>
        <span>Tech/AI</span>
      </button>

      <button
        type="button"
        class="px-3 py-1 rounded font-mono text-xs font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#ff9e00] {activeFilter === 'Philosophy' ? 'bg-[#ff9e00] text-[#482900]' : 'bg-[#201f22] text-[#dac2ad] border border-white/5 hover:bg-[#353437]'}"
        on:click={() => handleFilterChange('Philosophy')}
      >
        <span>Philosophy</span>
      </button>

      <button
        type="button"
        class="px-3 py-1 rounded font-mono text-xs font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#ff9e00] {activeFilter === 'Fragments' ? 'bg-[#ff9e00] text-[#482900]' : 'bg-[#201f22] text-[#dac2ad] border border-white/5 hover:bg-[#353437]'}"
        on:click={() => handleFilterChange('Fragments')}
      >
        <span>Fragments</span>
      </button>
    </nav>
  </section>

  <!-- Quick Capture Form Overlay / Input Bar -->
  <form on:submit={handleAddThought} class="p-2 bg-[#201f22] border border-[#544433] rounded-xl flex items-center gap-2 shadow-lg">
    <div class="w-8 h-8 rounded-lg bg-[#353437] flex items-center justify-center text-[#ff9e00] font-bold">
      ⚡
    </div>
    <label for="quantize-input" class="sr-only">Quantize thought input</label>
    <input
      id="quantize-input"
      type="text"
      bind:value={newThoughtText}
      placeholder="+ Quantize thought..."
      class="flex-1 bg-[#0e0e10] text-[#e5e1e4] placeholder-[#a28d79] px-3 py-2 rounded-lg border border-white/5 focus:outline-none focus:ring-2 focus:ring-[#4cd7f6] text-sm font-mono"
    />
    <button
      type="submit"
      class="px-3 py-2 rounded-lg bg-[#ff9e00] text-[#482900] font-bold text-xs flex items-center gap-1 hover:brightness-110 focus:outline-none focus:ring-2 focus:ring-[#ff9e00] active:scale-95 transition-all"
    >
      <span>Spark</span>
    </button>
  </form>

  <!-- Stream Thought Cards Stream -->
  <main class="flex flex-col gap-3" aria-label="Quantized Thought Stream">
    {#each filteredSparks as item (item.id)}
      <article
        class="p-4 rounded-xl flex flex-col gap-3 transition-all duration-200 border {item.category === 'BIO' ? 'border-[#ff9e00]/40 bg-gradient-to-br from-[#1c1b1d] to-[#0e0e10]' : 'border-white/10 bg-[#131315] hover:border-[#ff9e00]/30'}"
      >
        <!-- Top Metadata Row -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="px-2 py-0.5 rounded font-mono text-xs font-bold tracking-wide {item.category === 'BIO' ? 'bg-[#ff9e00] text-[#0e0e10]' : 'bg-[#ff9e00]/10 border border-[#ff9e00]/30 text-[#ffc586]'}">
              [{item.code}]
            </span>
            <span class="font-mono text-xs text-[#a28d79]">{item.timeAgo}</span>
          </div>

          {#if item.signalPercent}
            <div class="flex items-center gap-1 bg-[#2a2a2c] px-2 py-0.5 rounded border border-white/5 text-xs font-mono text-[#4cd7f6]">
              <span>📊</span>
              <span>{item.signalPercent}% Signal</span>
            </div>
          {:else if item.isDraft}
            <span class="font-mono text-[10px] text-[#e9ddff] uppercase bg-[#353437] px-1.5 py-0.5 rounded">
              SYNAPSE DRAFT
            </span>
          {/if}
        </div>

        <!-- Thought Content -->
        {#if item.category === 'BIO'}
          <blockquote class="text-sm sm:text-base text-[#e5e1e4] pl-3 border-l-2 border-[#ff9e00] leading-relaxed">
            {item.content}
          </blockquote>
        {:else if item.isDraft}
          <p class="text-base sm:text-lg font-semibold text-[#ffddbb] tracking-tight pt-1 font-mono">
            {item.content}
          </p>
        {:else}
          <p class="text-sm sm:text-base text-[#e5e1e4] leading-snug">
            {item.content}
          </p>
        {/if}

        <!-- Audio Waveform Indicator -->
        {#if item.hasAudioWaveform}
          <div class="flex items-center gap-2 bg-[#4cd7f6]/10 p-2 rounded border border-[#4cd7f6]/30 text-xs font-mono text-[#4cd7f6]">
            <span>🔊 Audio Snippet Embedded</span>
            <div class="flex items-end gap-1 h-3 ml-auto">
              <span class="w-1 h-full bg-[#4cd7f6] animate-pulse"></span>
              <span class="w-1 h-2/3 bg-[#4cd7f6]"></span>
              <span class="w-1 h-4/5 bg-[#4cd7f6] animate-pulse"></span>
            </div>
          </div>
        {/if}

        <!-- Progress Indicator Gauge -->
        {#if item.progressPercent}
          <div class="w-full bg-[#2a2a2c] h-1 rounded-full overflow-hidden">
            <div
              class="bg-gradient-to-r from-[#ff9e00] to-[#4cd7f6] h-full rounded-full transition-all duration-300"
              style="width: {item.progressPercent}%"
            ></div>
          </div>
        {/if}

        <!-- Tags if existing -->
        {#if item.tags && item.tags.length > 0}
          <div class="flex flex-wrap items-center gap-1.5 pt-1">
            {#each item.tags as tag}
              <span class="font-mono text-xs text-[#4cd7f6] bg-[#4cd7f6]/10 px-2 py-0.5 rounded border border-[#4cd7f6]/20">
                {tag}
              </span>
            {/each}
          </div>
        {/if}

        <!-- Footer Actions -->
        <div class="flex items-center justify-between pt-2 border-t border-white/5 text-xs font-mono text-[#dac2ad]">
          <div class="flex items-center gap-3">
            <button
              type="button"
              class="flex items-center gap-1.5 text-[#ffc586] hover:text-[#ff9e00] focus:outline-none focus:ring-2 focus:ring-[#ff9e00] active:scale-95 transition-transform"
              on:click={() => handleSparkAction(item.id)}
              aria-label="Spark action for {item.code}"
            >
              <span>⚡</span>
              <span>{item.sparksCount} sparks</span>
            </button>
            <button
              type="button"
              class="flex items-center gap-1.5 text-[#dac2ad] hover:text-[#4cd7f6] focus:outline-none focus:ring-2 focus:ring-[#4cd7f6] active:scale-95 transition-transform"
              aria-label="Branch action for {item.code}"
            >
              <span>⌥</span>
              <span>{item.branchesCount} branches</span>
            </button>
          </div>

          <div class="flex items-center gap-3">
            <button
              type="button"
              class="hover:text-[#ffc586] focus:outline-none focus:ring-2 focus:ring-[#ff9e00] transition-colors {item.isBookmarked ? 'text-[#ff9e00]' : 'text-[#dac2ad]'}"
              on:click={() => handleBookmarkAction(item.id)}
              aria-label="Bookmark {item.code}"
            >
              <span>{item.isBookmarked ? '★ Bookmarked' : '☆ Bookmark'}</span>
            </button>
          </div>
        </div>
      </article>
    {/each}
  </main>

  <!-- Footer Telemetry Status -->
  <footer class="py-4 text-center border-t border-[#353437] font-mono text-[10px] text-[#a28d79] tracking-widest uppercase">
    END OF BUFFER • SYNCING PACKETS IN REALTIME
  </footer>
</div>
