<script lang="ts">
  export let episodeTitle: string = 'EP_08: Deontic Constraints in Systems';
  export let audioUrl: string = 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3'; // Fallback sample audio
  export let timecodes = [
    { label: 'Introduction', time: 0 },
    { label: 'Defining the Constraint', time: 35 },
    { label: 'System Supervenience', time: 120 },
    { label: 'Practical Application', time: 215 }
  ];

  // Seed for deterministic wave heights
  const seededRandom = (seed: number) => {
    let x = Math.sin(seed++) * 10000;
    return x - Math.floor(x);
  };

  export let waveBars = Array.from({ length: 40 }, (_, i) => ({
    id: i,
    height: 20 + Math.floor(seededRandom(i * 123) * 60)
  }));

  let audioEl: HTMLAudioElement;
  let isPlaying = false;
  let currentTime = 0;
  let duration = 0;

  function togglePlay() {
    if (!audioEl) return;
    if (isPlaying) {
      audioEl.pause();
    } else {
      audioEl.play().catch(e => console.error("Audio playback error:", e));
    }
  }

  function handleTimeUpdate() {
    if (!audioEl) return;
    currentTime = audioEl.currentTime;
  }

  function handleLoadedMetadata() {
     if (!audioEl) return;
     duration = audioEl.duration;
  }

  function handlePlay() {
    isPlaying = true;
  }

  function handlePause() {
    isPlaying = false;
  }

  function seek(time: number) {
    if (audioEl) {
      audioEl.currentTime = time;
    }
  }

  function formatTime(seconds: number): string {
    if (isNaN(seconds)) return "00:00";
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  }

  // Calculate active bar based on current time
  $: activeBarIndex = duration > 0 ? Math.floor((currentTime / duration) * waveBars.length) : 0;
</script>

<div id="podcast-episode-root" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] max-w-2xl mx-auto my-4 font-sans">
  <!-- Header -->
  <header id="podcast-header" class="border-b border-[#1f2433] pb-4 mb-6">
    <div class="flex items-center gap-2 mb-2">
      <span class="bg-[#12151e] text-[#a78bfa] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">[SYS_MEDIA]</span>
      <span class="text-xs font-mono text-[#94a3b8]">AUDIO_STREAM</span>
    </div>
    <h2 class="text-2xl font-bold tracking-tight text-white mb-2">{episodeTitle}</h2>
    <div class="text-xs text-[#94a3b8] font-mono">Podcast: Системы и реальность</div>
  </header>

  <!-- Player controls & visualization -->
  <div class="bg-[#12151e] border border-[#1f2433] rounded p-4 mb-6">
    <audio
      bind:this={audioEl}
      src={audioUrl}
      on:timeupdate={handleTimeUpdate}
      on:loadedmetadata={handleLoadedMetadata}
      on:play={handlePlay}
      on:pause={handlePause}
      preload="metadata"
    ></audio>

    <div class="flex items-center gap-4 mb-4">
      <button
        id="play-pause-btn"
        class="w-12 h-12 flex items-center justify-center rounded-full bg-[#a78bfa] text-[#090a0f] hover:bg-[#c4b5fd] transition-colors focus:outline-none focus:ring-2 focus:ring-[#a78bfa] focus:ring-offset-2 focus:ring-offset-[#090a0f]"
        on:click={togglePlay}
        aria-label={isPlaying ? 'Pause' : 'Play'}
      >
        {#if isPlaying}
          <!-- Pause Icon -->
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zM7 8a1 1 0 012 0v4a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v4a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
        {:else}
           <!-- Play Icon -->
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd" />
          </svg>
        {/if}
      </button>

      <div class="flex-grow flex flex-col justify-center">
         <div class="flex justify-between text-xs font-mono text-[#94a3b8] mb-2">
            <span>{formatTime(currentTime)}</span>
            <span>{formatTime(duration)}</span>
         </div>

         <!-- Waveform Visualization -->
         <div id="audio-waveform" class="flex items-end h-10 gap-1 w-full" aria-hidden="true">
            {#each waveBars as bar, index}
              <div
                class="flex-1 rounded-t-sm transition-colors duration-150 {index <= activeBarIndex ? 'bg-[#a78bfa]' : 'bg-[#334155]'}"
                style="height: {bar.height}%"
              ></div>
            {/each}
         </div>
      </div>
    </div>
  </div>

  <!-- Timecodes -->
  <section id="podcast-timecodes">
    <h3 class="text-sm font-bold text-white mb-3 font-mono">[TIMECODES]</h3>
    <div class="space-y-2">
      {#each timecodes as tc, i}
        <button
          id="timecode-{i}"
          class="w-full flex items-center gap-3 p-2 rounded hover:bg-[#1f2433] transition-colors text-left group focus:outline-none focus:ring-1 focus:ring-[#a78bfa]"
          on:click={() => seek(tc.time)}
        >
          <span class="text-[#a78bfa] font-mono text-xs w-12 shrink-0">{formatTime(tc.time)}</span>
          <span class="text-sm text-[#cbd5e1] group-hover:text-white transition-colors">{tc.label}</span>
        </button>
      {/each}
    </div>
  </section>
</div>
