<script lang="ts">
  import { onMount } from 'svelte';
  import {
    PODCAST_EPISODES,
    formatTime,
    generateWaveformHeights,
    type PodcastEpisode,
    type PodcastTimecode
  } from '../lib/podcast';

  export let episodes: PodcastEpisode[] = PODCAST_EPISODES;
  export let selectedEpisodeId: number | string = PODCAST_EPISODES[0]?.id;

  // Extended episode detail interface for Stream tier (core theses & live transcript)
  export interface CoreThesis {
    id: string | number;
    timeOffsetSeconds: number;
    chapterNumber: number;
    title: string;
    quote?: string;
    author?: string;
    takeaways?: string[];
    isUpcoming?: boolean;
  }

  export interface DialogueLine {
    id: string | number;
    speaker: string;
    timeOffsetSeconds: number;
    text: string;
  }

  let audioElement: HTMLAudioElement | null = null;
  let isPlaying: boolean = false;
  let currentTime: number = 14 * 60 + 28; // Default seed time 14:28
  let playbackRate: number = 1.0;
  let isAutoScrollOn: boolean = true;
  let isTranscriptExpanded: boolean = true;
  let activeChapterFilter: string = 'All';
  let savedTheses: Set<string | number> = new Set();
  let bookmarkedTimes: Set<number> = new Set();
  let waveformContainer: HTMLElement;

  $: currentEpisode = episodes.find(ep => ep.id === selectedEpisodeId) || episodes[0];
  $: waveBars = generateWaveformHeights(36, currentEpisode ? currentEpisode.slug : 'default');
  $: progressPercent = currentEpisode && currentEpisode.durationSeconds > 0
    ? Math.min(100, Math.max(0, (currentTime / currentEpisode.durationSeconds) * 100))
    : 0;

  // Derived Core Theses for active episode
  $: coreTheses = [
    {
      id: 'thesis-01',
      timeOffsetSeconds: 14 * 60 + 28, // 14:28
      chapterNumber: 1,
      title: 'Trustless consensus shifts governance from human discretion to algorithmic verification.',
      quote: '“Decentralization is not merely political resistance; it is an ergonomic necessity for complex network states.”',
      author: 'Elena Vance, 14:42',
      isUpcoming: false
    },
    {
      id: 'thesis-02',
      timeOffsetSeconds: 28 * 60 + 50, // 28:50
      chapterNumber: 2,
      title: 'Liquidity follows clarity: Capital flywheels fail when coordination costs exceed yield.',
      takeaways: [
        'Coordination friction compounds exponentially when governance tokenholders face asymmetric incentive models.',
        'Empirical capital deployment trends demonstrate automated yield distribution outperforms council-managed treasuries by 4.2x.'
      ],
      isUpcoming: true
    }
  ] as CoreThesis[];

  // Derived dialogue stream
  $: dialogueStream = [
    {
      id: 'diag-1',
      speaker: 'Dr. K. Aris',
      timeOffsetSeconds: 14 * 60 + 10,
      text: "When we examined the early ledger configurations, the bottleneck wasn't throughput or block sizes—it was the cognitive friction of participants reconciling contradictory rules."
    },
    {
      id: 'diag-2',
      speaker: 'Elena Vance',
      timeOffsetSeconds: 14 * 60 + 28,
      text: "Exactly. Trustless consensus shifts governance from human discretion to algorithmic verification. If a community must vote on every operational micro-adjustment, you haven’t engineered a protocol; you’ve simply digitized a bureaucratic parliament."
    },
    {
      id: 'diag-3',
      speaker: 'Dr. K. Aris',
      timeOffsetSeconds: 15 * 60 + 2,
      text: 'And that is precisely where mechanism design provides the antidote. By pre-allocating deterministic verification bounds, consensus settles before fatigue sets in...'
    }
  ] as DialogueLine[];

  $: activeTimecodeId = currentEpisode?.timecodes?.reduce((acc: number | string | null, tc, idx, arr) => {
    const nextTc = arr[idx + 1];
    if (currentTime >= tc.timeOffsetSeconds && (!nextTc || currentTime < nextTc.timeOffsetSeconds)) {
      return tc.id;
    }
    return acc;
  }, null);

  $: activeThesis = coreTheses.find((t, i) => {
    const nextT = coreTheses[i + 1];
    return currentTime >= t.timeOffsetSeconds && (!nextT || currentTime < nextT.timeOffsetSeconds);
  }) || coreTheses[0];

  function togglePlay() {
    if (!audioElement) return;
    if (audioElement.paused) {
      isPlaying = true;
      audioElement.play().catch((err) => {
        isPlaying = false;
        console.warn('Audio playback error caught:', err);
      });
    } else {
      audioElement.pause();
      isPlaying = false;
    }
  }

  export function seekTo(seconds: number) {
    if (!currentEpisode) return;
    const boundedTime = Math.max(0, Math.min(seconds, currentEpisode.durationSeconds));
    currentTime = boundedTime;
    if (audioElement) {
      audioElement.currentTime = boundedTime;
    }
  }

  function skipTime(deltaSeconds: number) {
    seekTo(currentTime + deltaSeconds);
  }

  function toggleSpeed() {
    const rates = [1.0, 1.25, 1.5, 2.0];
    const nextIdx = (rates.indexOf(playbackRate) + 1) % rates.length;
    playbackRate = rates[nextIdx];
    if (audioElement) {
      audioElement.playbackRate = playbackRate;
    }
  }

  function handleTimecodeClick(tc: PodcastTimecode, event?: MouseEvent | KeyboardEvent) {
    seekTo(tc.timeOffsetSeconds);
    if (event && event.currentTarget) {
      (event.currentTarget as HTMLElement).focus();
    }
  }

  function handleWaveformClick(e: MouseEvent) {
    if (!waveformContainer || !currentEpisode) return;
    const rect = waveformContainer.getBoundingClientRect();
    const clickX = e.clientX - rect.left;
    const ratio = Math.max(0, Math.min(1, clickX / rect.width));
    const targetSeconds = Math.round(ratio * currentEpisode.durationSeconds);
    seekTo(targetSeconds);
  }

  function handleWaveformKeyDown(e: KeyboardEvent) {
    if (!currentEpisode) return;
    if (e.key === 'ArrowRight' || e.key === 'ArrowUp') {
      e.preventDefault();
      seekTo(currentTime + 5);
    } else if (e.key === 'ArrowLeft' || e.key === 'ArrowDown') {
      e.preventDefault();
      seekTo(currentTime - 5);
    } else if (e.key === 'PageUp') {
      e.preventDefault();
      seekTo(currentTime + 30);
    } else if (e.key === 'PageDown') {
      e.preventDefault();
      seekTo(currentTime - 30);
    } else if (e.key === 'Home') {
      e.preventDefault();
      seekTo(0);
    } else if (e.key === 'End') {
      e.preventDefault();
      seekTo(currentEpisode.durationSeconds);
    } else if (e.key === ' ' || e.key === 'Enter') {
      e.preventDefault();
      togglePlay();
    }
  }

  function handleAudioTimeUpdate() {
    if (audioElement) {
      currentTime = audioElement.currentTime;
    }
  }

  function handleAudioEnded() {
    isPlaying = false;
    currentTime = 0;
  }

  function toggleSaveThesis(id: string | number) {
    if (savedTheses.has(id)) {
      savedTheses.delete(id);
    } else {
      savedTheses.add(id);
    }
    savedTheses = new Set(savedTheses);
  }

  function copyQuote(text: string) {
    if (typeof navigator !== 'undefined' && navigator.clipboard) {
      navigator.clipboard.writeText(text).catch(() => {});
    }
  }
</script>

<div id="episode-player-container" class="w-full max-w-2xl mx-auto flex flex-col gap-6 text-[#e3e1e9] font-sans pb-24">
  <!-- Hidden Native HTML5 Audio Element -->
  <audio
    bind:this={audioElement}
    src={currentEpisode.audioUrl}
    on:timeupdate={handleAudioTimeUpdate}
    on:ended={handleAudioEnded}
    preload="auto"
  ></audio>

  <!-- Top Bar Layout Docked Header -->
  <header class="flex justify-between items-center px-4 h-14 bg-[#1a1b21]/90 backdrop-blur-md rounded-xl border border-white/10 shadow-md">
    <div class="flex items-center gap-2">
      <div class="flex flex-col">
        <span class="text-[11px] font-mono tracking-wider uppercase text-[#d8c3ad]">NOCTURNE DISPATCH • EP. {currentEpisode.id}</span>
        <span class="text-sm font-bold text-[#e3e1e9] tracking-tight truncate max-w-[180px] sm:max-w-xs">{currentEpisode.title}</span>
      </div>
    </div>
    <div class="flex items-center gap-2">
      <span class="hidden sm:inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-mono bg-[#292a2f] text-[#ffc174] border border-[#ffc174]/20">
        48kHz Lossless
      </span>
      <button aria-label="Share episode" class="w-8 h-8 rounded-full flex items-center justify-center text-[#d8c3ad] hover:bg-[#34343a] transition-colors">
        <span class="material-symbols-outlined text-[20px]">share</span>
      </button>
      <button aria-label="Bookmark episode" class="w-8 h-8 rounded-full flex items-center justify-center text-[#ffc174] hover:bg-[#34343a] transition-colors">
        <span class="material-symbols-outlined text-[20px]">bookmark</span>
      </button>
    </div>
  </header>

  <!-- Unified Media Player Header (Hero Studio Console) -->
  <section id="unified-media-player" class="bg-[#1a1b21] rounded-2xl p-4 sm:p-5 border border-white/10 shadow-2xl space-y-4">
    <!-- Episode Identity Meta Row -->
    <div class="flex items-start gap-4">
      <div class="relative w-20 h-20 sm:w-24 sm:h-24 shrink-0 rounded-xl overflow-hidden border border-white/10 bg-[#0d0e13] shadow-inner group">
        <div class="w-full h-full bg-gradient-to-br from-[#f59e0b]/20 to-[#121318] flex items-center justify-center">
          <span class="material-symbols-outlined text-4xl text-[#ffc174]">graphic_eq</span>
        </div>
        <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent flex items-end p-1">
          <span class="text-[10px] font-mono font-bold text-[#ffc174] px-1 bg-[#0d0e13]/90 rounded backdrop-blur-sm">EP. {currentEpisode.id}</span>
        </div>
      </div>

      <div class="flex-1 min-w-0">
        <div class="flex items-center gap-2 mb-1 flex-wrap">
          <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-mono font-semibold bg-[#ffc174]/15 text-[#ffc174] border border-[#ffc174]/30">
            <span class="w-1.5 h-1.5 rounded-full bg-[#ffc174] animate-ping"></span>
            Chapter {activeThesis?.chapterNumber || 1}: Cognitive Scaffolding
          </span>
        </div>
        <h1 id="episode-hero-title" class="text-lg sm:text-xl font-bold text-[#e3e1e9] leading-snug tracking-tight line-clamp-2">
          {currentEpisode.title}
        </h1>
        <p class="text-xs font-mono text-[#d8c3ad] truncate mt-1">
          {currentEpisode.philosopher} • {currentEpisode.publishedAt}
        </p>
      </div>
    </div>

    <!-- Waveform Visualizer & Scrubber Module -->
    <div class="pt-3 border-t border-white/10 space-y-2">
      <!-- Interactive Frequency Bars Scrubber Container -->
      <div
        id="waveform-container"
        bind:this={waveformContainer}
        on:click={handleWaveformClick}
        on:keydown={handleWaveformKeyDown}
        role="slider"
        tabindex="0"
        aria-label="Audio Waveform Scrubber"
        aria-valuemin="0"
        aria-valuemax={currentEpisode.durationSeconds}
        aria-valuenow={currentTime}
        aria-valuetext="{formatTime(currentTime)} of {formatTime(currentEpisode.durationSeconds)}"
        class="h-12 w-full flex items-end justify-between gap-[2px] px-1 cursor-pointer select-none focus:outline-none focus:ring-2 focus:ring-[#ffc174] rounded-lg bg-[#0d0e13]/60 p-1 border border-white/5"
      >
        {#each waveBars as barHeight, i}
          {@const barProgress = (i / waveBars.length) * 100}
          {@const isPlayed = barProgress <= progressPercent}
          <div
            class="flex-1 rounded-t transition-all duration-75 {isPlayed ? 'bg-[#ffc174] shadow-[0_0_8px_rgba(245,158,11,0.5)]' : 'bg-[#34343a] hover:bg-[#ffc174]/50'}"
            style="height: {barHeight}%;"
          ></div>
        {/each}
      </div>

      <!-- Continuous Progress Track Rail with Chapter Split Markers & Drag Thumb -->
      <div class="relative w-full h-1.5 bg-[#34343a] rounded-full overflow-visible">
        <div class="absolute left-0 top-0 h-full bg-gradient-to-r from-[#d97707] to-[#f59e0b] rounded-full" style="width: {progressPercent}%;"></div>
        <!-- Chapter split markers -->
        <div class="absolute top-0 bottom-0 left-[12%] w-[2px] bg-[#121318]"></div>
        <div class="absolute top-0 bottom-0 left-[28%] w-[2px] bg-[#121318]"></div>
        <div class="absolute top-0 bottom-0 left-[55%] w-[2px] bg-[#121318]"></div>
        <div class="absolute top-0 bottom-0 left-[79%] w-[2px] bg-[#121318]"></div>
        <!-- Scrubber Thumb Needle -->
        <div
          class="absolute top-1/2 -translate-x-1/2 -translate-y-1/2 w-3.5 h-3.5 bg-[#e3e1e9] border-2 border-[#f59e0b] rounded-full shadow-[0_0_12px_rgba(245,158,11,0.8)] cursor-grab"
          style="left: {progressPercent}%;"
        ></div>
      </div>

      <!-- Time Indicators -->
      <div class="flex justify-between items-center text-xs font-mono text-[#d8c3ad]">
        <span id="current-time-val" class="text-[#ffc174] font-bold">{formatTime(currentTime)}</span>
        <span class="text-[10px] tracking-wide text-[#d8c3ad]/80 hidden sm:inline">SYNCHRONIZED WITH THESIS 01</span>
        <span id="duration-val">{formatTime(currentEpisode.durationSeconds)}</span>
      </div>
    </div>

    <!-- Studio Audio Controls Strip -->
    <div class="flex items-center justify-between pt-2 border-t border-white/10">
      <!-- Speed & Equalizer -->
      <div class="flex items-center gap-1.5">
        <button
          id="speed-btn"
          type="button"
          on:click={toggleSpeed}
          aria-label="Playback Speed"
          class="px-2.5 py-1 rounded-md bg-[#292a2f] hover:bg-[#34343a] border border-white/10 text-xs font-mono font-bold text-[#ffc174] active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-[#ffc174]"
        >
          {playbackRate}×
        </button>
        <button class="p-1.5 rounded-md text-[#d8c3ad] hover:text-white hover:bg-[#292a2f] transition-colors" title="Speech Crispness Boost" aria-label="Equalizer">
          <span class="material-symbols-outlined text-[20px]">equalizer</span>
        </button>
      </div>

      <!-- Central Playback Cluster -->
      <div class="flex items-center gap-3">
        <button
          id="skip-back-btn"
          type="button"
          on:click={() => skipTime(-15)}
          aria-label="Skip back 15 seconds"
          class="w-9 h-9 rounded-full flex items-center justify-center text-[#d8c3ad] hover:text-white hover:bg-[#292a2f] active:scale-90 transition-all focus:outline-none focus:ring-2 focus:ring-[#ffc174]"
        >
          <span class="material-symbols-outlined text-[22px]">replay_10</span>
        </button>

        <!-- Primary Amber Play/Pause Action -->
        <button
          id="play-pause-btn"
          type="button"
          on:click={togglePlay}
          aria-label={isPlaying ? 'Pause Podcast Episode' : 'Play Podcast Episode'}
          class="w-12 h-12 rounded-full bg-[#f59e0b] hover:bg-[#d97707] text-[#2a1700] font-bold flex items-center justify-center shadow-[0_0_16px_-2px_rgba(245,158,11,0.6)] active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-[#ffc174]"
        >
          {#if isPlaying}
            <span class="material-symbols-outlined text-[28px]">pause</span>
          {:else}
            <span class="material-symbols-outlined text-[28px] pl-0.5">play_arrow</span>
          {/if}
        </button>

        <button
          id="skip-forward-btn"
          type="button"
          on:click={() => skipTime(30)}
          aria-label="Skip forward 30 seconds"
          class="w-9 h-9 rounded-full flex items-center justify-center text-[#d8c3ad] hover:text-white hover:bg-[#292a2f] active:scale-90 transition-all focus:outline-none focus:ring-2 focus:ring-[#ffc174]"
        >
          <span class="material-symbols-outlined text-[22px]">forward_30</span>
        </button>
      </div>

      <!-- Auxiliary Tools -->
      <div class="flex items-center gap-1.5">
        <button class="p-1.5 rounded-md text-[#d8c3ad] hover:text-white hover:bg-[#292a2f] transition-colors" title="Sleep Timer" aria-label="Sleep Timer">
          <span class="material-symbols-outlined text-[20px] text-[#ffc174]">timer</span>
        </button>
        <button class="p-1.5 rounded-md text-[#d8c3ad] hover:text-white hover:bg-[#292a2f] transition-colors" title="Cast Audio" aria-label="Cast Audio">
          <span class="material-symbols-outlined text-[20px]">cast</span>
        </button>
      </div>
    </div>
  </section>

  <!-- Synchronized Chapters Carousel Strip -->
  <section class="space-y-2.5">
    <div class="flex items-center justify-between px-1">
      <h2 class="text-xs font-mono font-bold text-[#d8c3ad] uppercase tracking-wider flex items-center gap-1.5">
        <span class="material-symbols-outlined text-[#ffc174] text-[18px]">format_list_bulleted</span>
        Synchronized Chapters
      </h2>
      <span class="text-xs font-mono text-[#ffc174] font-medium cursor-pointer hover:underline">
        View All ({currentEpisode.timecodes.length})
      </span>
    </div>

    <!-- Horizontal Segment Filter Pills -->
    <div class="flex items-center gap-2 overflow-x-auto py-1 no-scrollbar">
      <button
        type="button"
        class="shrink-0 px-3 py-1 rounded-full text-xs font-mono transition-colors border {activeChapterFilter === 'All' ? 'bg-[#f59e0b] text-[#2a1700] font-bold border-[#ffc174]' : 'bg-[#292a2f] text-[#d8c3ad] border-white/10 hover:border-white/30'}"
        on:click={() => (activeChapterFilter = 'All')}
      >
        All
      </button>

      {#each currentEpisode.timecodes as tc}
        {@const isActive = activeTimecodeId === tc.id}
        <button
          type="button"
          id="timecode-btn-{tc.id}"
          on:click={(e) => handleTimecodeClick(tc, e)}
          class="shrink-0 px-3 py-1 rounded-full text-xs font-mono transition-all flex items-center gap-1.5 border focus:outline-none focus:ring-2 focus:ring-[#ffc174] {isActive ? 'bg-[#f59e0b] text-[#2a1700] font-bold border-[#ffc174] shadow-md' : 'bg-[#292a2f] text-[#d8c3ad] border-white/10 hover:border-white/30'}"
        >
          {#if isActive}
            <span class="w-1.5 h-1.5 rounded-full bg-[#2a1700] animate-pulse"></span>
          {/if}
          <span>{formatTime(tc.timeOffsetSeconds)}</span>
          <span class="truncate max-w-[120px]">{tc.title}</span>
        </button>
      {/each}
    </div>
  </section>

  <!-- Core Theses & Executive Syntheses Section -->
  <section class="space-y-3.5">
    <div class="flex items-end justify-between px-1">
      <div>
        <h2 class="text-base font-bold text-[#e3e1e9] tracking-tight flex items-center gap-2">
          <span class="material-symbols-outlined text-[#ffc174]">lightbulb</span>
          Core Theses
        </h2>
        <p class="text-xs text-[#d8c3ad] mt-0.5">
          Key arguments highlighted and synced to playback
        </p>
      </div>
      <div class="flex items-center gap-1 text-[11px] font-mono text-[#ffc174] bg-[#ffc174]/10 px-2 py-0.5 rounded border border-[#ffc174]/20">
        <span class="material-symbols-outlined text-[14px]">sync</span>
        LIVE SYNC
      </div>
    </div>

    {#each coreTheses as thesis}
      {@const isSaved = savedTheses.has(thesis.id)}
      {@const isActive = activeThesis?.id === thesis.id}
      <article
        id="thesis-card-{thesis.id}"
        class="bg-[#1a1b21] rounded-xl p-4 border border-white/10 relative overflow-hidden transition-all {isActive ? 'border-l-4 border-l-[#ffc174] shadow-[0_0_12px_rgba(245,158,11,0.25)]' : 'opacity-90 hover:opacity-100'}"
      >
        <div class="flex items-center justify-between mb-2.5 flex-wrap gap-2">
          <div class="flex items-center gap-2">
            <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-mono font-semibold {isActive ? 'bg-[#ffc174]/20 text-[#ffc174] border border-[#ffc174]/30' : 'bg-[#34343a] text-[#d8c3ad]'}">
              {#if isActive}
                <span class="flex items-end gap-0.5 h-3">
                  <span class="w-0.5 bg-[#ffc174] rounded-full h-2 animate-bounce"></span>
                  <span class="w-0.5 bg-[#ffc174] rounded-full h-3 animate-bounce delay-100"></span>
                  <span class="w-0.5 bg-[#ffc174] rounded-full h-1.5 animate-bounce delay-200"></span>
                </span>
              {/if}
              {formatTime(thesis.timeOffsetSeconds)} • Thesis 0{thesis.chapterNumber}
            </span>
            {#if isActive}
              <span class="text-xs font-mono text-[#ffb77d] font-medium">Currently Discussed</span>
            {:else if thesis.isUpcoming}
              <span class="text-xs font-mono text-[#d8c3ad]/80">Upcoming</span>
            {/if}
          </div>

          <button
            type="button"
            on:click={() => toggleSaveThesis(thesis.id)}
            class="text-[#d8c3ad] hover:text-[#ffc174] p-1 rounded transition-colors focus:outline-none"
            title="Bookmark thesis"
          >
            <span class="material-symbols-outlined text-[20px] {isSaved ? 'text-[#ffc174]' : ''}">
              {isSaved ? 'bookmark_added' : 'bookmark_border'}
            </span>
          </button>
        </div>

        <h3 class="text-sm sm:text-base font-bold text-[#e3e1e9] leading-snug">
          {thesis.title}
        </h3>

        {#if thesis.quote}
          <blockquote class="my-3 pl-3.5 py-1 border-l-2 border-[#f59e0b] bg-[#1e1f25] rounded-r-lg">
            <p class="text-xs sm:text-sm italic text-[#e3e1e9]/90">
              {thesis.quote}
            </p>
            {#if thesis.author}
              <cite class="block text-xs font-mono text-[#d8c3ad] not-italic mt-1 font-medium">
                — {thesis.author}
              </cite>
            {/if}
          </blockquote>
        {/if}

        {#if thesis.takeaways}
          <ul class="my-2.5 space-y-1.5 text-xs text-[#d8c3ad] list-none">
            {#each thesis.takeaways as item}
              <li class="flex items-start gap-2">
                <span class="text-[#ffc174] mt-0.5 text-[14px] material-symbols-outlined">arrow_right</span>
                <span>{item}</span>
              </li>
            {/each}
          </ul>
        {/if}

        <div class="flex items-center justify-between pt-2 border-t border-white/10 mt-3 flex-wrap gap-2">
          <button
            type="button"
            on:click={() => seekTo(thesis.timeOffsetSeconds)}
            class="inline-flex items-center gap-1.5 text-xs font-mono font-semibold text-[#ffc174] hover:underline"
          >
            <span class="material-symbols-outlined text-[16px]">play_circle</span>
            Jump to {formatTime(thesis.timeOffsetSeconds)}
          </button>

          <div class="flex items-center gap-2">
            {#if thesis.quote}
              <button
                type="button"
                on:click={() => copyQuote(thesis.quote || '')}
                class="inline-flex items-center gap-1 px-2.5 py-1 rounded bg-[#292a2f] hover:bg-[#34343a] text-xs font-mono text-[#e3e1e9] border border-white/10 transition-all active:scale-95"
              >
                <span class="material-symbols-outlined text-[15px]">content_copy</span>
                Copy Quote
              </button>
            {/if}
            <button
              type="button"
              on:click={() => toggleSaveThesis(thesis.id)}
              class="inline-flex items-center gap-1 px-2.5 py-1 rounded bg-[#f59e0b] hover:bg-[#d97707] text-[#2a1700] font-semibold text-xs font-mono transition-all active:scale-95"
            >
              <span class="material-symbols-outlined text-[15px]">save</span>
              {isSaved ? 'Saved' : 'Save Thesis'}
            </button>
          </div>
        </div>
      </article>
    {/each}
  </section>

  <!-- Live Companion Transcript View -->
  <section class="bg-[#1a1b21] rounded-xl p-4 border border-white/10 space-y-3">
    <div
      class="flex items-center justify-between pb-3 border-b border-white/10 cursor-pointer"
      on:click={() => (isTranscriptExpanded = !isTranscriptExpanded)}
      on:keydown={(e) => (e.key === 'Enter' || e.key === ' ') && (isTranscriptExpanded = !isTranscriptExpanded)}
      role="button"
      tabindex="0"
    >
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-[#ffc174]">segment</span>
        <h2 class="text-sm sm:text-base font-bold text-[#e3e1e9]">Live Companion Transcript</h2>
      </div>
      <div class="flex items-center gap-2 text-xs font-mono">
        <span class="inline-flex items-center gap-1 text-[#ffc174]">
          <span class="w-1.5 h-1.5 rounded-full bg-[#ffc174] animate-ping"></span>
          Auto-Scroll ON
        </span>
        <span class="material-symbols-outlined text-[#d8c3ad]">
          {isTranscriptExpanded ? 'expand_less' : 'expand_more'}
        </span>
      </div>
    </div>

    {#if isTranscriptExpanded}
      <div class="space-y-3 pt-1">
        {#each dialogueStream as line}
          {@const isPlayingLine = currentTime >= line.timeOffsetSeconds && currentTime < line.timeOffsetSeconds + 30}
          <div class="p-3 rounded-lg border transition-all {isPlayingLine ? 'bg-[#292a2f] border-[#ffc174] shadow-sm' : 'bg-[#121318]/60 border-transparent opacity-70 hover:opacity-100'}">
            <div class="flex items-center justify-between text-xs font-mono mb-1 text-[#d8c3ad]">
              <span class="font-bold flex items-center gap-1.5 text-[#ffc174]">
                {#if isPlayingLine}
                  <span class="w-2 h-2 rounded-full bg-[#ffc174] animate-pulse"></span>
                {/if}
                {line.speaker}
              </span>
              <span class="bg-black/30 px-1.5 py-0.5 rounded">{formatTime(line.timeOffsetSeconds)}</span>
            </div>
            <p class="text-xs sm:text-sm text-[#e3e1e9] leading-relaxed">
              {line.text}
            </p>
          </div>
        {/each}
      </div>
    {/if}
  </section>
</div>
