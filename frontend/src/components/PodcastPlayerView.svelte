<script lang="ts">
  import { onMount } from 'svelte';
  import {
    PODCAST_EPISODES,
    formatTime,
    generateWaveformHeights,
    type PodcastEpisode,
    type PodcastTimecode
  } from '../lib/podcast';
  import ResponsivePicture from './ResponsivePicture.svelte';

  export let episodes: PodcastEpisode[] = PODCAST_EPISODES;
  export let selectedEpisodeId: number | string = PODCAST_EPISODES[0]?.id;

  let audioElement: HTMLAudioElement | null = null;
  let isPlaying: boolean = false;
  let currentTime: number = 0;
  let activeChapterFilter: string = 'All';
  let isTranscriptExpanded: boolean = true;
  let autoScrollTranscript: boolean = true;
  let playbackSpeed: number = 1.0;
  let isBookmarked: boolean = false;

  const playbackSpeeds = [1.0, 1.25, 1.5, 2.0];

  $: currentEpisode = episodes.find(ep => ep.id === selectedEpisodeId) || episodes[0];
  $: waveBars = generateWaveformHeights(40, currentEpisode ? currentEpisode.slug : 'default');
  $: progressPercent = currentEpisode && currentEpisode.durationSeconds > 0
    ? Math.min(100, Math.max(0, (currentTime / currentEpisode.durationSeconds) * 100))
    : 0;

  $: activeTimecodeId = currentEpisode?.timecodes?.reduce((acc: number | string | null, tc, idx, arr) => {
    const nextTc = arr[idx + 1];
    if (currentTime >= tc.timeOffsetSeconds && (!nextTc || currentTime < nextTc.timeOffsetSeconds)) {
      return tc.id;
    }
    return acc;
  }, null);

  $: activeTimecode = currentEpisode?.timecodes?.find(tc => tc.id === activeTimecodeId) || currentEpisode?.timecodes[0];

  function handleSelectEpisode(epId: number | string) {
    selectedEpisodeId = epId;
    currentTime = 0;
    isPlaying = false;
    if (audioElement) {
      audioElement.currentTime = 0;
      audioElement.pause();
    }
  }

  function togglePlay() {
    isPlaying = !isPlaying;
    if (audioElement) {
      if (isPlaying) {
        audioElement.play().catch(() => {
          // Audio playback handle for environments without audio hardware
        });
      } else {
        audioElement.pause();
      }
    }
  }

  function cyclePlaybackSpeed() {
    const currentIndex = playbackSpeeds.indexOf(playbackSpeed);
    const nextIndex = (currentIndex + 1) % playbackSpeeds.length;
    playbackSpeed = playbackSpeeds[nextIndex];
    if (audioElement) {
      audioElement.playbackRate = playbackSpeed;
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

  function skipSeconds(delta: number) {
    seekTo(currentTime + delta);
  }

  function handleTimecodeClick(tc: PodcastTimecode) {
    seekTo(tc.timeOffsetSeconds);
  }

  function handleWaveformClick(e: MouseEvent) {
    const target = e.currentTarget as HTMLElement;
    if (!target || !currentEpisode) return;
    const rect = target.getBoundingClientRect();
    const clickX = e.clientX - rect.left;
    const ratio = Math.max(0, Math.min(1, clickX / rect.width));
    const targetSeconds = Math.round(ratio * currentEpisode.durationSeconds);
    seekTo(targetSeconds);
  }

  function handleWaveformKeyDown(e: KeyboardEvent) {
    if (!currentEpisode) return;
    if (e.key === 'ArrowRight') {
      seekTo(currentTime + 5);
    } else if (e.key === 'ArrowLeft') {
      seekTo(currentTime - 5);
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
</script>

<!-- Top Bar: Header Docked Layout matching mockup design -->
<header id="player-top-header" class="w-full flex justify-between items-center px-4 py-3 bg-[#1a1b21]/90 backdrop-blur-md border-b border-[#534434]/30 rounded-t-xl">
  <div class="flex items-center gap-2">
    <button
      type="button"
      aria-label="Go back"
      class="w-9 h-9 rounded-full flex items-center justify-center text-[#e3e1e9] hover:bg-[#34343a]/50 active:scale-95 transition-transform duration-150 focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
    >
      <span class="text-lg">←</span>
    </button>
    <div class="flex flex-col">
      <span id="header-dispatch-label" class="text-[11px] font-mono tracking-wider uppercase text-[#d8c3ad]">
        NOCTURNE DISPATCH • EP. 42
      </span>
      <span id="header-title-label" class="text-sm font-bold text-[#e3e1e9] tracking-tight truncate max-w-[170px] sm:max-w-xs">
        {currentEpisode.title}
      </span>
    </div>
  </div>
  <div class="flex items-center gap-2">
    <span class="hidden sm:inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-mono bg-[#292a2f] text-[#ffc174] border border-[#ffc174]/20">
      48kHz Lossless
    </span>
    <button
      type="button"
      aria-label="Share episode"
      class="w-8 h-8 rounded-full flex items-center justify-center text-[#d8c3ad] hover:bg-[#34343a]/50 active:scale-95 transition-transform duration-150 focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
    >
      <span class="text-sm">↗</span>
    </button>
    <button
      type="button"
      aria-label={isBookmarked ? "Remove bookmark" : "Bookmark episode"}
      on:click={() => (isBookmarked = !isBookmarked)}
      class="w-8 h-8 rounded-full flex items-center justify-center text-[#ffc174] hover:bg-[#34343a]/50 active:scale-95 transition-transform duration-150 focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
    >
      <span>{isBookmarked ? '★' : '☆'}</span>
    </button>
  </div>
</header>

<div id="podcast-player-container" class="space-y-6 bg-[#121318] text-[#e3e1e9] p-4 rounded-b-xl">
  <!-- Hidden Native HTML5 Audio Element for Playback State -->
  <audio
    bind:this={audioElement}
    src={currentEpisode.audioUrl}
    on:timeupdate={handleAudioTimeUpdate}
    on:ended={handleAudioEnded}
    preload="metadata"
  ></audio>

  <!-- Title & Channel Header -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#534434]/30 pb-3">
    <div>
      <div class="flex items-center gap-2 mb-1 flex-wrap">
        <span class="bg-[#1a1b21] text-[#ffc174] border border-[#534434]/30 px-2 py-0.5 rounded text-xs font-mono">
          [PODCAST_CATALOG]
        </span>
        <span class="text-xs font-mono text-[#d8c3ad]">Episodes: {episodes.length}</span>
      </div>
      <h2 class="text-xl font-bold tracking-tight text-white break-words">«Системы и реальность» Podcast</h2>
    </div>

    <!-- External Media Subscription Links -->
    <div class="flex flex-wrap gap-2 text-xs font-mono">
      <a
        href={currentEpisode?.youtubeUrl || (currentEpisode?.mediaLink?.includes('youtube') ? currentEpisode.mediaLink : 'https://youtube.com/@efremov_dmitriy')}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="YouTube Channel"
        class="px-2.5 py-1 rounded bg-[#1a1b21] text-[#ef4444] border border-[#534434]/30 hover:border-[#ef4444] transition-colors flex items-center gap-1"
      >
        <span>YouTube</span>
        <span>↗</span>
      </a>
      <a
        href={currentEpisode?.telegramUrl || (currentEpisode?.mediaLink?.includes('t.me') ? currentEpisode.mediaLink : 'https://t.me/efremov_mvp')}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Telegram Channel"
        class="px-2.5 py-1 rounded bg-[#1a1b21] text-[#38bdf8] border border-[#534434]/30 hover:border-[#38bdf8] transition-colors flex items-center gap-1"
      >
        <span>Telegram</span>
        <span>↗</span>
      </a>
      <a
        href="/feed.xml"
        target="_blank"
        rel="noopener noreferrer"
        aria-label="RSS Feed"
        class="px-2.5 py-1 rounded bg-[#1a1b21] text-[#d8c3ad] border border-[#534434]/30 hover:text-white transition-colors flex items-center gap-1"
      >
        <span>RSS Feed</span>
      </a>
    </div>
  </div>

  <!-- Episode Catalog Carousel / Grid Section -->
  <section id="podcast-episodes-section" class="space-y-3">
    <div class="text-xs font-mono text-[#d8c3ad] flex justify-between items-center">
      <span>AVAILABLE_EPISODES ({episodes.length})</span>
      <span>SELECT_EPISODE</span>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-3" role="list" aria-label="Podcast Episodes Catalog">
      {#each episodes as ep}
        <button
          type="button"
          id="episode-card-{ep.id}"
          class="episode-card w-full p-3.5 rounded-lg border transition-all cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#f59e0b] {selectedEpisodeId === ep.id ? 'bg-[#1a1b21] border-[#f59e0b] shadow-md' : 'bg-[#121318] border-[#534434]/30 hover:border-[#ffc174]/50 hover:bg-[#1a1b21]/50'}"
          on:click={() => handleSelectEpisode(ep.id)}
        >
          <div class="flex justify-between items-center text-xs font-mono mb-2">
            <span class="px-1.5 py-0.5 rounded bg-[#292a2f] text-[#ffc174] uppercase">
              {ep.publishedAt}
            </span>
            <span class="text-[#d8c3ad]">{formatTime(ep.durationSeconds)}</span>
          </div>
          <h3 class="font-bold text-white text-sm mb-1 hover:text-[#ffc174] transition-colors line-clamp-1">
            {ep.title}
          </h3>
          <p class="text-xs text-[#d8c3ad] line-clamp-2 leading-relaxed">
            {ep.thesis}
          </p>
          <div class="mt-2 text-[11px] font-mono text-[#d8c3ad] flex justify-between items-center">
            <span>{ep.timecodes.length} Timecodes</span>
            <span class="text-[#ffc174] font-bold">{selectedEpisodeId === ep.id ? '● LISTENING' : 'LISTEN →'}</span>
          </div>
        </button>
      {/each}
    </div>
  </section>

  <!-- Hero Studio Console / Media Player Card -->
  <section id="media-player-console" class="bg-[#1a1b21] rounded-xl p-4 border border-white/10 shadow-2xl space-y-4">
    <!-- Episode Identity Meta Row -->
    <div class="flex items-start gap-3.5">
      <div id="podcast-cover-container" class="relative w-20 h-20 shrink-0 rounded-lg overflow-hidden border border-[#534434]/30 shadow-inner group">
        <ResponsivePicture
          id="podcast-cover-img"
          assetKey="podcast_studio_cover"
          alt="Episode Cover Artwork"
          imgClass="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-300"
          loading="eager"
        />
        <div class="absolute inset-0 bg-gradient-to-t from-[#121318]/80 via-transparent to-transparent flex items-end p-1">
          <span class="text-[10px] font-mono text-[#ffc174] px-1 bg-[#0d0e13]/80 rounded backdrop-blur-sm">EP. 42</span>
        </div>
      </div>
      <div class="flex-1 min-w-0">
        <div class="flex items-center gap-2 mb-1">
          <span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-semibold bg-[#ffc174]/10 text-[#ffc174] border border-[#ffc174]/25">
            <span class="w-1.5 h-1.5 rounded-full bg-[#ffc174] animate-ping"></span>
            {activeTimecode ? activeTimecode.title : 'Chapter 3: Cognitive Scaffolding'}
          </span>
        </div>
        <h1 id="podcast-title" class="text-lg font-bold text-[#e3e1e9] leading-snug tracking-tight line-clamp-2">
          {currentEpisode.title}
        </h1>
        <p class="text-xs text-[#d8c3ad] truncate mt-0.5">
          Elena Vance & Dr. K. Aris
        </p>
      </div>
    </div>

    <!-- Waveform Visualizer & Scrubber Module -->
    <div id="waveform-visualizer-module" class="pt-3 border-t border-[#534434]/20 space-y-2">
      <!-- Frequency Bars Interactive Canvas -->
      <div
        id="waveform-container"
        on:click={handleWaveformClick}
        on:keydown={handleWaveformKeyDown}
        role="slider"
        tabindex="0"
        aria-label="Interactive Waveform Timeline"
        aria-valuemin="0"
        aria-valuemax={currentEpisode.durationSeconds}
        aria-valuenow={currentTime}
        aria-valuetext="{formatTime(currentTime)} of {formatTime(currentEpisode.durationSeconds)}"
        class="h-10 w-full flex items-end justify-between gap-[2px] px-0.5 cursor-pointer select-none focus:outline-none focus:ring-2 focus:ring-[#f59e0b] rounded"
        title="Interactive Waveform Timeline"
      >
        {#each waveBars as barHeight, i}
          {@const barProgress = (i / waveBars.length) * 100}
          {@const isPlayed = barProgress <= progressPercent}
          <span
            class="w-1 rounded-t-sm transition-colors duration-75 {isPlayed ? 'bg-[#ffc174]' : 'bg-[#34343a]/80 hover:bg-[#ffc174]/50'}"
            style="height: {barHeight}%;"
          ></span>
        {/each}
      </div>

      <!-- Continuous Progress Track Rail -->
      <button
        type="button"
        aria-label="Seek track position"
        class="relative w-full h-1.5 bg-[#34343a] rounded-full overflow-visible group cursor-pointer border-0 p-0 block focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        on:click={handleWaveformClick}
      >
        <span
          class="absolute left-0 top-0 h-full bg-gradient-to-r from-[#d97707] to-[#f59e0b] rounded-full block"
          style="width: {progressPercent}%;"
        ></span>
        <!-- Chapter split markers -->
        <span class="absolute top-0 bottom-0 left-[12%] w-[2px] bg-[#121318] block"></span>
        <span class="absolute top-0 bottom-0 left-[28%] w-[2px] bg-[#121318] block"></span>
        <span class="absolute top-0 bottom-0 left-[55%] w-[2px] bg-[#121318] block"></span>
        <span class="absolute top-0 bottom-0 left-[79%] w-[2px] bg-[#121318] block"></span>
        <!-- Drag Thumb -->
        <span
          class="absolute top-1/2 -translate-x-1/2 -translate-y-1/2 w-3.5 h-3.5 bg-[#e3e1e9] border-2 border-[#f59e0b] rounded-full cursor-grab shadow-[0_0_8px_rgba(245,158,11,0.8)] block"
          style="left: {progressPercent}%;"
        ></span>
      </button>

      <!-- Time Indicators -->
      <div class="flex justify-between items-center text-xs font-mono text-[#d8c3ad]">
        <span id="current-time-val" class="text-[#ffc174] font-semibold">{formatTime(currentTime)}</span>
        <span class="text-[11px] font-sans tracking-wide text-[#d8c3ad]/80">SYNCHRONIZED WITH THESIS 01</span>
        <span id="duration-val">{formatTime(currentEpisode.durationSeconds)}</span>
      </div>
    </div>

    <!-- Tactile Audio Controls Strip -->
    <div id="audio-controls-block" class="flex items-center justify-between pt-2 border-t border-[#534434]/20">
      <!-- Speed & Equalizer -->
      <div class="flex items-center gap-1">
        <button
          type="button"
          on:click={cyclePlaybackSpeed}
          aria-label="Playback Speed"
          class="px-2 py-1 rounded bg-[#292a2f] hover:bg-[#34343a] border border-[#534434]/30 text-xs font-mono font-semibold text-[#ffc174] active:scale-95 transition-transform focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        >
          {playbackSpeed}×
        </button>
        <button
          type="button"
          aria-label="Speech Crispness Boost"
          class="p-1.5 rounded text-[#d8c3ad] hover:text-[#e3e1e9] hover:bg-[#292a2f] transition-colors focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
          title="Speech Crispness Boost"
        >
          <span class="text-sm font-mono">[EQ]</span>
        </button>
      </div>

      <!-- Central Playback Cluster -->
      <div class="flex items-center gap-3">
        <button
          type="button"
          on:click={() => skipSeconds(-15)}
          aria-label="Skip back 15 seconds"
          class="w-9 h-9 rounded-full flex items-center justify-center text-[#d8c3ad] hover:text-[#e3e1e9] active:scale-90 transition-transform focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        >
          <span class="text-xs font-mono">-15s</span>
        </button>

        <!-- Primary Play/Pause Button -->
        <button
          id="play-pause-btn"
          type="button"
          on:click={togglePlay}
          aria-label={isPlaying ? 'Pause Podcast Episode' : 'Play Podcast Episode'}
          class="w-12 h-12 rounded-full bg-[#f59e0b] text-[#2a1700] flex items-center justify-center shadow-[0_0_16px_rgba(245,158,11,0.4)] hover:bg-[#d97707] active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-white"
        >
          {#if isPlaying}
            <span class="text-lg font-bold">❚❚</span>
          {:else}
            <span class="text-lg font-bold pl-0.5">▶</span>
          {/if}
        </button>

        <button
          type="button"
          on:click={() => skipSeconds(30)}
          aria-label="Skip forward 30 seconds"
          class="w-9 h-9 rounded-full flex items-center justify-center text-[#d8c3ad] hover:text-[#e3e1e9] active:scale-90 transition-transform focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        >
          <span class="text-xs font-mono">+30s</span>
        </button>
      </div>

      <!-- Auxiliary Tools -->
      <div class="flex items-center gap-1">
        <button
          type="button"
          aria-label="Sleep Timer Active"
          class="p-1.5 rounded text-[#d8c3ad] hover:text-[#e3e1e9] hover:bg-[#292a2f] transition-colors focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
          title="Sleep Timer Active (45m)"
        >
          <span class="text-xs font-mono text-[#ffc174]">⏲</span>
        </button>
        <button
          type="button"
          aria-label="Cast Audio"
          class="p-1.5 rounded text-[#d8c3ad] hover:text-[#e3e1e9] hover:bg-[#292a2f] transition-colors focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
          title="Cast Audio"
        >
          <span class="text-xs font-mono">⎘</span>
        </button>
      </div>
    </div>
  </section>

  <!-- Synchronized Chapters Carousel Strip -->
  <section id="chapters-section" class="space-y-2">
    <div class="flex items-center justify-between px-1">
      <h2 class="text-xs font-mono text-[#d8c3ad] uppercase tracking-wider flex items-center gap-1.5">
        <span>☰</span>
        TIMECODES & TOPICS ({currentEpisode.timecodes.length})
      </h2>
      <span class="text-xs font-mono text-[#ffc174] font-medium cursor-pointer hover:underline">
        View All
      </span>
    </div>

    <!-- Chapter Filter Pills -->
    <div id="timecodes-list" class="flex items-center gap-2 overflow-x-auto py-1" role="list" aria-label="Episode Timecodes">
      <button
        type="button"
        on:click={() => (activeChapterFilter = 'All')}
        class="shrink-0 px-3 py-1.5 rounded-full text-xs font-medium border transition-colors focus:outline-none focus:ring-2 focus:ring-[#f59e0b] {activeChapterFilter === 'All' ? 'bg-[#f59e0b] text-[#2a1700] border-[#f59e0b] font-bold' : 'bg-[#292a2f] text-[#d8c3ad] border-[#534434]/30 hover:border-[#534434]'}"
      >
        All
      </button>
      {#each currentEpisode.timecodes as tc}
        {@const isActive = activeTimecodeId === tc.id}
        <button
          type="button"
          id="timecode-btn-{tc.id}"
          on:click={() => handleTimecodeClick(tc)}
          class="timecode-item shrink-0 px-3 py-1.5 rounded-full text-xs font-medium border transition-colors flex items-center gap-1.5 focus:outline-none focus:ring-2 focus:ring-[#f59e0b] {isActive ? 'bg-[#f59e0b] text-[#2a1700] border-[#f59e0b] font-bold shadow-sm' : 'bg-[#292a2f] text-[#d8c3ad] border-[#534434]/30 hover:border-[#534434]'}"
        >
          {#if isActive}
            <span class="w-2 h-2 rounded-full bg-[#2a1700] animate-pulse"></span>
            <span class="text-[10px] font-mono text-[#2a1700]">NOW_PLAYING</span>
          {/if}
          <span>[{formatTime(tc.timeOffsetSeconds)}] {tc.title}</span>
        </button>
      {/each}
    </div>
  </section>

  <!-- Core Theses & Executive Syntheses Section -->
  <section id="theses-section" class="space-y-3.5">
    <div class="flex items-end justify-between px-1">
      <div>
        <h2 class="text-base font-bold text-[#e3e1e9] tracking-tight flex items-center gap-2">
          <span class="text-[#ffc174]">💡</span>
          Core Theses
        </h2>
        <p class="text-xs text-[#d8c3ad]">
          Key arguments highlighted and synced to playback
        </p>
      </div>
      <div class="flex items-center gap-1 text-[11px] font-mono text-[#ffc174] bg-[#ffc174]/10 px-2 py-0.5 rounded border border-[#ffc174]/20">
        <span>⚡</span> LIVE SYNC
      </div>
    </div>

    <!-- Thesis Card 1 (Active Synced State) -->
    <article class="bg-[#1a1b21] rounded-xl p-4 border border-white/10 border-l-4 border-l-[#ffc174] relative overflow-hidden shadow-[0_0_12px_rgba(245,158,11,0.25)] transition-all space-y-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-mono font-semibold bg-[#ffc174]/15 text-[#ffc174] border border-[#ffc174]/30">
            <span class="flex items-end gap-0.5 h-3">
              <span class="w-0.5 bg-[#ffc174] h-2 animate-pulse"></span>
              <span class="w-0.5 bg-[#ffc174] h-3 animate-pulse"></span>
              <span class="w-0.5 bg-[#ffc174] h-1.5 animate-pulse"></span>
            </span>
            {formatTime(activeTimecode?.timeOffsetSeconds || 868)} • Thesis 01
          </span>
          <span class="text-xs font-semibold text-[#ffb77d]">Currently Discussed</span>
        </div>
        <button
          type="button"
          aria-label="Bookmark thesis"
          class="text-[#d8c3ad] hover:text-[#e3e1e9] p-1 rounded hover:bg-[#292a2f] transition-colors focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        >
          <span class="text-[#ffc174]">★</span>
        </button>
      </div>

      <h3 class="text-sm font-bold text-[#e3e1e9] leading-snug">
        Trustless consensus shifts governance from human discretion to algorithmic verification.
      </h3>

      <blockquote class="my-2 pl-3.5 py-1 border-l-2 border-[#f59e0b] bg-[#1e1f25]/60 rounded-r-lg italic text-xs text-[#e3e1e9]/90">
        “Decentralization is not merely political resistance; it is an ergonomic necessity for complex network states.”
        <cite class="block text-[11px] text-[#d8c3ad] not-italic mt-1 font-medium">
          — Elena Vance, {formatTime((activeTimecode?.timeOffsetSeconds || 868) + 14)}
        </cite>
      </blockquote>

      <div class="flex items-center justify-between pt-2 border-t border-[#534434]/20">
        <button
          type="button"
          on:click={() => seekTo(activeTimecode?.timeOffsetSeconds || 868)}
          class="inline-flex items-center gap-1.5 text-xs font-semibold text-[#ffc174] hover:text-[#ffb95f] transition-colors focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        >
          <span>▶</span>
          Jump to {formatTime(activeTimecode?.timeOffsetSeconds || 868)}
        </button>
        <div class="flex items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-1 px-2.5 py-1 rounded bg-[#292a2f] hover:bg-[#34343a] text-xs text-[#e3e1e9] border border-[#534434]/30 active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
          >
            Copy Quote
          </button>
          <button
            type="button"
            class="inline-flex items-center gap-1 px-2.5 py-1 rounded bg-[#f59e0b] hover:bg-[#d97707] text-[#2a1700] font-semibold text-xs active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-white"
          >
            Save Thesis
          </button>
        </div>
      </div>
    </article>

    <!-- Thesis Card 2 -->
    <article class="bg-[#1a1b21] rounded-xl p-4 border border-white/10 opacity-90 hover:opacity-100 transition-opacity space-y-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-mono bg-[#34343a] text-[#d8c3ad]">
            <span>⏱</span> 28:50 • Thesis 02
          </span>
          <span class="text-xs text-[#d8c3ad]/80">Upcoming</span>
        </div>
        <button
          type="button"
          aria-label="Bookmark thesis"
          class="text-[#d8c3ad] hover:text-[#e3e1e9] p-1 rounded hover:bg-[#292a2f] transition-colors focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        >
          ☆
        </button>
      </div>

      <h3 class="text-sm font-bold text-[#e3e1e9] leading-snug">
        Liquidity follows clarity: Capital flywheels fail when coordination costs exceed yield.
      </h3>

      <ul class="space-y-1.5 text-xs text-[#d8c3ad] list-none">
        <li class="flex items-start gap-2">
          <span class="text-[#ffc174]">→</span>
          <span>Coordination friction compounds exponentially when governance tokenholders face asymmetric incentive models.</span>
        </li>
        <li class="flex items-start gap-2">
          <span class="text-[#ffc174]">→</span>
          <span>Empirical capital deployment trends demonstrate automated yield distribution outperforms council-managed treasuries by 4.2x.</span>
        </li>
      </ul>

      <div class="flex items-center justify-between pt-2 border-t border-[#534434]/20">
        <button
          type="button"
          on:click={() => seekTo(1730)}
          class="inline-flex items-center gap-1 text-xs font-medium text-[#d8c3ad] hover:text-[#ffc174] transition-colors focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        >
          <span>⏩</span>
          Preview Chapter (28:50)
        </button>
        <button
          type="button"
          class="inline-flex items-center gap-1 px-2.5 py-1 rounded bg-[#292a2f] hover:bg-[#34343a] text-xs text-[#e3e1e9] border border-[#534434]/30 active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-[#f59e0b]"
        >
          Queue Bookmark
        </button>
      </div>
    </article>
  </section>

  <!-- Live Companion Transcript Accordion Section -->
  <section id="transcript-section" class="bg-[#1a1b21] rounded-xl p-4 border border-white/10 space-y-3">
    <div class="w-full flex items-center justify-between pb-2 border-b border-[#534434]/30">
      <button
        type="button"
        on:click={() => (isTranscriptExpanded = !isTranscriptExpanded)}
        class="flex items-center gap-2 text-left focus:outline-none focus:ring-2 focus:ring-[#f59e0b] rounded"
      >
        <span class="text-[#ffc174] font-mono">≡</span>
        <h2 class="text-sm font-bold text-[#e3e1e9]">Live Companion Transcript</h2>
        <span class="text-xs text-[#d8c3ad] ml-1">{isTranscriptExpanded ? '▲' : '▼'}</span>
      </button>
      <button
        type="button"
        on:click={() => (autoScrollTranscript = !autoScrollTranscript)}
        class="inline-flex items-center gap-1 text-xs font-mono text-[#ffc174] font-medium focus:outline-none focus:ring-2 focus:ring-[#f59e0b] rounded px-1"
      >
        <span class="w-1.5 h-1.5 rounded-full bg-[#ffc174] {autoScrollTranscript ? 'animate-ping' : ''}"></span>
        Auto-Scroll {autoScrollTranscript ? 'ON' : 'OFF'}
      </button>
    </div>

    {#if isTranscriptExpanded}
      <div class="space-y-3 text-xs">
        <div class="opacity-60 text-[#d8c3ad] hover:opacity-90 transition-opacity">
          <div class="flex items-center justify-between font-mono text-[11px] mb-0.5">
            <span class="font-bold">Dr. K. Aris</span>
            <span>14:10</span>
          </div>
          <p>
            When we examined the early ledger configurations, the bottleneck wasn't throughput or block sizes—it was the cognitive friction of participants reconciling contradictory rules.
          </p>
        </div>

        <div class="bg-[#292a2f]/90 p-3 rounded-lg border-l-2 border-[#ffc174] border border-white/10 shadow-sm space-y-1">
          <div class="flex items-center justify-between font-mono text-[11px] text-[#ffc174]">
            <span class="font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-[#ffc174] animate-pulse"></span>
              Elena Vance
            </span>
            <span class="bg-[#ffc174]/20 px-1.5 py-0.5 rounded">14:28 (ACTIVE)</span>
          </div>
          <p class="text-[#e3e1e9] font-medium leading-relaxed">
            Exactly. Trustless consensus shifts governance from human discretion to algorithmic verification. If a community must vote on every operational micro-adjustment, you haven’t engineered a protocol; you’ve simply digitized a bureaucratic parliament.
          </p>
          <div class="flex items-center justify-end gap-2 pt-2 border-t border-[#534434]/20">
            <button type="button" class="text-[11px] text-[#d8c3ad] hover:text-[#ffc174] transition-colors focus:outline-none focus:ring-1 focus:ring-[#f59e0b]">
              Add Reading Note
            </button>
            <button type="button" class="text-[11px] text-[#d8c3ad] hover:text-[#ffc174] transition-colors focus:outline-none focus:ring-1 focus:ring-[#f59e0b]">
              Clip Audio (30s)
            </button>
          </div>
        </div>

        <div class="opacity-60 text-[#d8c3ad] hover:opacity-90 transition-opacity">
          <div class="flex items-center justify-between font-mono text-[11px] mb-0.5">
            <span class="font-bold">Dr. K. Aris</span>
            <span>15:02</span>
          </div>
          <p>
            And that is precisely where mechanism design provides the antidote. By pre-allocating deterministic verification bounds, consensus settles before fatigue sets in...
          </p>
        </div>
      </div>
    {/if}
  </section>

  <!-- Sticky Mini-Player Ribbon -->
  <div id="sticky-mini-player" class="sticky bottom-14 left-0 right-0 z-40 bg-[#292a2f]/95 backdrop-blur-lg px-3.5 py-2 rounded-xl border border-white/10 shadow-2xl flex items-center justify-between">
    <div class="flex items-center gap-2.5 min-w-0">
      <div class="w-8 h-8 rounded bg-[#ffc174]/20 text-[#ffc174] flex items-center justify-center shrink-0 font-mono text-xs font-bold">
        ♫
      </div>
      <div class="min-w-0">
        <p class="text-xs font-semibold text-[#e3e1e9] truncate leading-tight">
          {formatTime(currentTime)} • {activeTimecode ? activeTimecode.title : currentEpisode.title}
        </p>
        <p class="text-[11px] font-mono text-[#ffc174] truncate">
          Playing • {playbackSpeed}x
        </p>
      </div>
    </div>
    <div class="flex items-center gap-2 shrink-0">
      <button
        type="button"
        on:click={togglePlay}
        aria-label={isPlaying ? 'Pause' : 'Play'}
        class="w-8 h-8 rounded-full bg-[#f59e0b] text-[#2a1700] flex items-center justify-center font-bold shadow-[0_0_8px_rgba(245,158,11,0.8)] active:scale-90 transition-transform focus:outline-none focus:ring-2 focus:ring-white"
      >
        {#if isPlaying}
          <span class="text-xs">❚❚</span>
        {:else}
          <span class="text-xs pl-0.5">▶</span>
        {/if}
      </button>
    </div>
  </div>

  <!-- Bottom Navigation Bar matching mockup design -->
  <nav id="bottom-nav-bar" aria-label="Media Hub Navigation" class="flex justify-around items-center px-4 py-2 bg-[#1e1f25]/95 backdrop-blur-md rounded-b-xl border-t border-[#534434]/30 shadow-lg text-xs font-mono">
    <a href="#listen" class="flex flex-col items-center text-[#d8c3ad] hover:text-[#ffc174] transition-colors focus:outline-none focus:ring-1 focus:ring-[#f59e0b]">
      <span>📻</span>
      <span class="mt-0.5">Listen</span>
    </a>
    <a href="#chapters" class="flex flex-col items-center text-[#d8c3ad] hover:text-[#ffc174] transition-colors focus:outline-none focus:ring-1 focus:ring-[#f59e0b]">
      <span>☰</span>
      <span class="mt-0.5">Chapters</span>
    </a>
    <a href="#theses" class="flex flex-col items-center text-[#ffc174] font-bold transition-colors focus:outline-none focus:ring-1 focus:ring-[#f59e0b]">
      <span>💡</span>
      <span class="mt-0.5">Theses</span>
    </a>
    <a href="#notes" class="flex flex-col items-center text-[#d8c3ad] hover:text-[#ffc174] transition-colors focus:outline-none focus:ring-1 focus:ring-[#f59e0b]">
      <span>📝</span>
      <span class="mt-0.5">Notes</span>
    </a>
  </nav>
</div>
