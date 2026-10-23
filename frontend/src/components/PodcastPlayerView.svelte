<script lang="ts">
  import { onMount } from 'svelte';
  import {
    PODCAST_EPISODES,
    formatTime,
    generateWaveformHeights,
    type PodcastEpisode,
    type PodcastTimecode,
    type CoreThesis
  } from '../lib/podcast';

  export let episodes: PodcastEpisode[] = PODCAST_EPISODES;
  export let selectedEpisodeId: number | string = PODCAST_EPISODES[0]?.id;

  let audioElement: HTMLAudioElement | null = null;
  let isPlaying: boolean = false;
  let currentTime: number = 0;
  let playbackRate: number = 1.2;
  let activeTab: 'theses' | 'chapters' | 'transcript' | 'cited' = 'theses';
  let isSyncReadingMode: boolean = false;
  let sleepTimerActive: boolean = false;
  let dialogueEnhanced: boolean = false;
  let waveformContainer: HTMLElement;

  const speedOptions = [1.0, 1.2, 1.5, 2.0];

  $: currentEpisode = episodes.find(ep => ep.id === selectedEpisodeId) || episodes[0];
  $: waveBars = generateWaveformHeights(40, currentEpisode ? currentEpisode.slug : 'default');
  $: duration = currentEpisode ? currentEpisode.durationSeconds : 1;
  $: progressPercent = Math.min(100, Math.max(0, (currentTime / duration) * 100));
  $: remainingTime = Math.max(0, duration - currentTime);

  $: timecodes = currentEpisode?.timecodes || [];
  $: theses = currentEpisode?.theses || [
    {
      id: 'default-t1',
      categoryTag: 'CENTRAL THESIS',
      timeOffsetSeconds: 340,
      quote: '“Формула Баркан как математический мост между возможными мирами и инвариантами автономных систем.”',
      explanation: 'Необходимость существования сущностей в модальном контуре выражает нормативный запрет невалидных состояний.',
      sectionLabel: 'Раздел I: Модальные инварианты'
    },
    {
      id: 'default-t2',
      categoryTag: 'CORE PREMISE II',
      timeOffsetSeconds: 920,
      quote: '“Деонтический оператор O(p) исключает одновременно возможный P(~p) без обработки исключений.”',
      explanation: 'Противоречивые обязательства разрушают идемпотентность распределенного агента.',
      sectionLabel: 'Раздел II: Деонтические дилеммы'
    },
    {
      id: 'default-t3',
      categoryTag: 'SYNTHESIS CONCLUSION',
      timeOffsetSeconds: 1640,
      quote: '“Инварианты автономии выводятся из математической необходимости, а не runtime-патчей.”',
      explanation: 'Строгость спецификации исключает деградацию логики под нагрузкой.',
      sectionLabel: 'Заключение: Инженерия автономии'
    }
  ];

  $: activeTimecode = timecodes.reduce((acc: PodcastTimecode | null, tc, idx) => {
    const nextTc = timecodes[idx + 1];
    if (currentTime >= tc.timeOffsetSeconds && (!nextTc || currentTime < nextTc.timeOffsetSeconds)) {
      return tc;
    }
    return acc;
  }, null);

  $: activeTimecodeId = activeTimecode?.id || null;

  $: activeThesis = theses.find((th, idx) => {
    const nextTh = theses[idx + 1];
    return currentTime >= th.timeOffsetSeconds && (!nextTh || currentTime < nextTh.timeOffsetSeconds);
  }) || theses[0];

  function handleSelectEpisode(epId: number | string) {
    const wasPlaying = isPlaying;
    selectedEpisodeId = epId;
    currentTime = 0;
    if (audioElement) {
      audioElement.currentTime = 0;
      if (wasPlaying) {
        setTimeout(() => {
          if (audioElement) {
            audioElement.play().catch(() => {});
          }
        }, 60);
      } else {
        audioElement.pause();
        isPlaying = false;
      }
    }
  }

  function togglePlay() {
    if (!audioElement) return;
    if (audioElement.paused) {
      isPlaying = true;
      audioElement.play().catch((err) => {
        isPlaying = false;
        console.warn("Audio playback error caught:", err);
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

  function skipSeconds(delta: number) {
    seekTo(currentTime + delta);
  }

  function skipToNextChapter() {
    if (!timecodes.length) return;
    const nextIndex = timecodes.findIndex(tc => tc.timeOffsetSeconds > currentTime + 1);
    if (nextIndex !== -1) {
      seekTo(timecodes[nextIndex].timeOffsetSeconds);
    } else {
      seekTo(duration);
    }
  }

  function skipToPreviousChapter() {
    if (!timecodes.length) return;
    const prevTimecodes = timecodes.filter(tc => tc.timeOffsetSeconds < currentTime - 3);
    if (prevTimecodes.length > 0) {
      seekTo(prevTimecodes[prevTimecodes.length - 1].timeOffsetSeconds);
    } else {
      seekTo(0);
    }
  }

  function cycleSpeed() {
    const idx = speedOptions.indexOf(playbackRate);
    const nextIdx = (idx + 1) % speedOptions.length;
    playbackRate = speedOptions[nextIdx];
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

  function handleThesisClick(th: CoreThesis, event?: MouseEvent | KeyboardEvent) {
    seekTo(th.timeOffsetSeconds);
    if (event && event.currentTarget) {
      (event.currentTarget as HTMLElement).focus();
    }
  }

  function handleWaveformClick(e: MouseEvent) {
    if (!waveformContainer || !currentEpisode) return;
    const rect = waveformContainer.getBoundingClientRect();
    const clickX = e.clientX - rect.left;
    const ratio = Math.max(0, Math.min(1, clickX / rect.width));
    const targetSeconds = Math.round(ratio * duration);
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
      seekTo(duration);
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

<!-- CSS Animations & Equalizer Styles -->
<style>
  @keyframes barPulse {
    0%, 100% { height: 4px; }
    50% { height: 16px; }
  }
  .eq-bar-1 { animation: barPulse 0.9s ease-in-out infinite; }
  .eq-bar-2 { animation: barPulse 0.7s ease-in-out infinite 0.2s; }
  .eq-bar-3 { animation: barPulse 1.1s ease-in-out infinite 0.4s; }
</style>

<div id="podcast-player-container" class="space-y-6 text-[#e2e2e8] bg-[#111317] p-2 sm:p-4 rounded-2xl pb-28">

  <!-- Hidden Native HTML5 Audio Element -->
  <audio
    bind:this={audioElement}
    src={currentEpisode.audioUrl}
    on:timeupdate={handleAudioTimeUpdate}
    on:ended={handleAudioEnded}
    preload="auto"
  ></audio>

  <!-- Sub-Header Metabar / Main Podcast Title Bar -->
  <div class="py-3 px-4 rounded-xl border border-[#57423b]/50 bg-[#1a1c20] flex flex-wrap items-center justify-between gap-3 shadow-md">
    <div>
      <div class="flex items-center space-x-2 mb-1">
        <span class="inline-block w-2 h-2 rounded-full bg-[#e36f46] animate-ping" aria-hidden="true"></span>
        <span class="font-bold text-xs tracking-wider text-[#f0bd8b] uppercase font-mono">
          ISSUE 48 · THE ALGORITHMIC SUBLIME — {currentEpisode.philosopher}
        </span>
      </div>
      <h2 class="text-xl font-bold tracking-tight text-white font-serif">Подкаст «Системы и реальность»</h2>
    </div>

    <div class="flex items-center space-x-3 text-[#dec0b7]">
      <!-- Speed selector toggle pill -->
      <button
        type="button"
        on:click={cycleSpeed}
        title="Change Playback Speed"
        aria-label="Playback speed {playbackRate}x, click to cycle speed"
        class="font-mono text-xs bg-[#1e2024] px-2.5 py-1 rounded border border-[#57423b] text-[#e2e2e8] hover:text-[#ffb59c] hover:border-[#ffb59c] transition focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
      >
        {playbackRate}x
      </button>

      <!-- External Media Links -->
      <a
        href={currentEpisode?.youtubeUrl || 'https://youtube.com/@dmitrii.efremov'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="YouTube Channel"
        title="YouTube Channel"
        class="text-xs font-mono text-[#ffb59c] hover:underline flex items-center gap-1 focus:outline-none focus:ring-2 focus:ring-[#ffb59c] rounded px-1"
      >
        <span>YouTube</span>
        <span aria-hidden="true">↗</span>
      </a>
      <a
        href={currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Telegram Channel"
        title="Telegram Channel"
        class="text-xs font-mono text-[#bac8d9] hover:underline flex items-center gap-1 focus:outline-none focus:ring-2 focus:ring-[#ffb59c] rounded px-1"
      >
        <span>Telegram</span>
        <span aria-hidden="true">↗</span>
      </a>
    </div>
  </div>

  <!-- Catalog Episode Selector Bar -->
  <div id="podcast-episodes-section" class="space-y-2">
    <div class="text-xs font-mono text-[#a58b83] flex justify-between items-center px-1">
      <span>КАТАЛОГ ВЫПУСКОВ ({episodes.length})</span>
      <span>ВЫБЕРИТЕ ВЫПУСК ДЛЯ ПРОСЛУШИВАНИЯ</span>
    </div>
    <div class="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-thin" role="list" aria-label="Podcast Episodes Catalog">
      {#each episodes as ep}
        <button
          type="button"
          id="episode-card-{ep.id}"
          class="episode-card flex-shrink-0 p-3 rounded-lg border text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {selectedEpisodeId === ep.id ? 'bg-[#1e2024] border-[#e36f46] text-white shadow-lg' : 'bg-[#1a1c20] border-[#333539] text-[#dec0b7] hover:border-[#e36f46]/50 hover:bg-[#1e2024]/60'}"
          on:click={() => handleSelectEpisode(ep.id)}
        >
          <div class="flex items-center justify-between gap-2 text-[10px] font-mono mb-1">
            <span class="px-1.5 py-0.5 rounded bg-[#333539] text-[#ffb59c] font-bold uppercase">{ep.philosopher}</span>
            <span class="text-[#a58b83]">{formatTime(ep.durationSeconds)}</span>
          </div>
          <p class="font-bold text-xs truncate max-w-[200px] text-[#e2e2e8]">{ep.title}</p>
        </button>
      {/each}
    </div>
  </div>

  <!-- MAIN UNIFIED MEDIA PLAYER HUB (Hero Player Card) -->
  <section
    id="podcast-hero-player"
    class="bg-[#1a1c20] border border-[#57423b] rounded-2xl p-5 sm:p-6 relative overflow-hidden shadow-2xl space-y-6"
  >
    <!-- Ambient Glow Behind Controls -->
    <div class="absolute -top-16 -right-16 w-48 h-48 bg-[#e36f46]/10 blur-3xl pointer-events-none rounded-full" aria-hidden="true"></div>

    <!-- Episode Header & Metadata -->
    <div class="relative z-10 space-y-3">
      <div class="flex flex-wrap items-center justify-between gap-2">
        <span class="font-mono text-xs text-[#ffb59c] tracking-widest uppercase font-bold">
          {currentEpisode.philosopher} · Monograph Lecture
        </span>
        <span class="font-mono text-xs text-[#dec0b7] bg-[#1e2024] px-2.5 py-0.5 rounded border border-[#333539]">
          {formatTime(currentEpisode.durationSeconds)} Audio Essay
        </span>
      </div>

      <h1 id="podcast-title" class="text-xl sm:text-2xl font-bold font-serif text-white leading-snug tracking-tight">
        {currentEpisode.title}
      </h1>

      <div class="pt-1 flex items-center space-x-3 text-[#dec0b7]">
        <div class="w-7 h-7 rounded-full bg-[#333539] border border-[#57423b] flex items-center justify-center text-xs font-bold text-[#f0bd8b] shrink-0">
          DE
        </div>
        <p class="text-xs font-mono text-[#dec0b7]">
          Written &amp; Read by <span class="text-white font-medium">Dmitry Efremov</span> · Published <span class="text-white font-medium">{currentEpisode.publishedAt}</span>
        </p>
      </div>

      <blockquote class="p-3 rounded-lg bg-[#0c0e12] border-l-4 border-[#e36f46] text-xs italic text-[#dec0b7] leading-relaxed">
        «{currentEpisode.thesis}»
      </blockquote>
    </div>

    <!-- Waveform Visualizer Display Block -->
    <div class="pt-3 pb-2 bg-[#0c0e12]/90 rounded-xl p-4 border border-[#57423b]/60 space-y-3">
      <div class="flex items-center justify-between text-xs font-mono">
        <div class="flex items-center space-x-2 truncate">
          <span class="w-2 h-2 rounded-full bg-[#e36f46] shrink-0"></span>
          <span class="text-[#e2e2e8] font-medium truncate">
            {activeTimecode ? activeTimecode.title : 'Chapter 1: Intro'}
          </span>
        </div>
        <span id="current-time-val" class="text-[#ffb59c] font-bold shrink-0">{formatTime(currentTime)}</span>
      </div>

      <!-- Interactive Waveform Simulation Bars -->
      <div class="space-y-1">
        <div class="flex justify-between items-center text-[10px] font-mono text-[#a58b83]">
          <span>ВИЗУАЛИЗАЦИЯ ЗВУКОВОЙ ВОЛНЫ</span>
          <span>КЛИКНИТЕ ДЛЯ ПЕРЕМОТКИ</span>
        </div>
        <div
          id="waveform-container"
          bind:this={waveformContainer}
          on:click={handleWaveformClick}
          on:keydown={handleWaveformKeyDown}
          role="slider"
          tabindex="0"
          aria-label="Audio Waveform Visualization Progress Bar"
          aria-valuemin="0"
          aria-valuemax={duration}
          aria-valuenow={currentTime}
          aria-valuetext="{formatTime(currentTime)} of {formatTime(duration)}"
          class="h-14 bg-[#111317] p-2 rounded-lg border border-[#333539] flex items-end justify-between gap-[2px] cursor-pointer hover:border-[#ffb59c] transition-colors focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
        >
          {#each waveBars as barHeight, i}
            {@const barProgress = (i / waveBars.length) * 100}
            {@const isPlayed = barProgress <= progressPercent}
            <div
              class="w-[3px] sm:w-[4px] rounded-t transition-all duration-75 {isPlayed ? 'bg-[#e36f46]' : 'bg-[#333539] hover:bg-[#ffb59c]'}"
              style="height: {barHeight}%;"
            ></div>
          {/each}
        </div>
      </div>

      <!-- Scrubber Track & Precise Timestamps -->
      <div class="relative w-full space-y-2">
        <div class="h-1.5 bg-[#1e2024] w-full rounded overflow-hidden">
          <div
            class="h-full bg-[#e36f46] transition-all duration-75 rounded"
            style="width: {progressPercent}%"
          ></div>
        </div>
        <div class="flex justify-between items-center font-mono text-xs text-[#dec0b7]">
          <span>{formatTime(currentTime)}</span>
          <span class="text-[#a58b83]">Remaining: {formatTime(remainingTime)}</span>
          <span id="duration-val">{formatTime(duration)}</span>
        </div>
      </div>
    </div>

    <!-- Tactile Playback Controls -->
    <div class="flex items-center justify-around py-2">
      <!-- Previous Chapter -->
      <button
        type="button"
        on:click={skipToPreviousChapter}
        class="text-[#dec0b7] hover:text-white transition active:scale-95 p-2 rounded focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
        title="Previous Chapter"
        aria-label="Previous Chapter"
      >
        <span class="text-2xl font-bold font-mono">|&lt;─</span>
      </button>

      <!-- Rewind 15s -->
      <button
        type="button"
        on:click={() => skipSeconds(-15)}
        class="text-[#dec0b7] hover:text-white transition active:scale-95 p-2 rounded flex flex-col items-center focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
        title="Rewind 15 seconds"
        aria-label="Rewind 15 seconds"
      >
        <span class="text-lg font-mono font-bold">↺ 15s</span>
      </button>

      <!-- Main Play/Pause Button with Terracotta Glow -->
      <button
        id="play-pause-btn"
        type="button"
        on:click={togglePlay}
        aria-label={isPlaying ? 'Pause Podcast Episode' : 'Play Podcast Episode'}
        class="w-16 h-16 rounded-full bg-[#e36f46] hover:bg-[#ffb59c] text-[#511500] font-bold flex items-center justify-center shadow-lg shadow-[#e36f46]/30 transition-transform active:scale-95 border border-[#ffb59c]/50 ring-4 ring-[#e36f46]/20 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
      >
        {#if isPlaying}
          <span class="text-2xl font-bold">❚❚</span>
        {:else}
          <span class="text-2xl pl-1 font-bold">▶</span>
        {/if}
      </button>

      <!-- Forward 30s -->
      <button
        type="button"
        on:click={() => skipSeconds(30)}
        class="text-[#dec0b7] hover:text-white transition active:scale-95 p-2 rounded flex flex-col items-center focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
        title="Forward 30 seconds"
        aria-label="Forward 30 seconds"
      >
        <span class="text-lg font-mono font-bold">↻ 30s</span>
      </button>

      <!-- Next Chapter -->
      <button
        type="button"
        on:click={skipToNextChapter}
        class="text-[#dec0b7] hover:text-white transition active:scale-95 p-2 rounded focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
        title="Next Chapter"
        aria-label="Next Chapter"
      >
        <span class="text-2xl font-bold font-mono">─&gt;|</span>
      </button>
    </div>

    <!-- Quick Action Pills Row -->
    <div class="pt-4 border-t border-[#57423b]/60 flex items-center space-x-2 overflow-x-auto pb-1 scrollbar-thin">
      <!-- Sync Reading Mode -->
      <button
        type="button"
        on:click={() => (isSyncReadingMode = !isSyncReadingMode)}
        class="flex items-center space-x-1.5 px-3 py-1.5 rounded text-xs font-mono whitespace-nowrap border transition focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {isSyncReadingMode ? 'bg-[#e36f46] text-[#511500] font-bold border-[#ffb59c]' : 'bg-[#1e2024] text-[#ffb59c] border-[#e36f46]/40 hover:bg-[#282a2e]'}"
        aria-pressed={isSyncReadingMode}
      >
        <span>⚡</span>
        <span>Sync Reading Mode</span>
      </button>

      <!-- Speed Toggle -->
      <button
        type="button"
        on:click={cycleSpeed}
        class="flex items-center space-x-1 px-3 py-1.5 bg-[#1e2024] border border-[#333539] rounded text-xs font-mono text-[#e2e2e8] whitespace-nowrap hover:bg-[#282a2e] focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
        aria-label="Speed {playbackRate}x"
      >
        <span>Speed {playbackRate}x</span>
      </button>

      <!-- Sleep Timer -->
      <button
        type="button"
        on:click={() => (sleepTimerActive = !sleepTimerActive)}
        class="flex items-center space-x-1 px-3 py-1.5 border rounded text-xs font-mono whitespace-nowrap transition focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {sleepTimerActive ? 'bg-[#65411a] text-[#e1af7e] border-[#e1af7e]' : 'bg-[#1e2024] border-[#333539] text-[#e2e2e8] hover:bg-[#282a2e]'}"
        aria-pressed={sleepTimerActive}
      >
        <span>⏱ Sleep 30m</span>
      </button>

      <!-- Enhance Dialogue -->
      <button
        type="button"
        on:click={() => (dialogueEnhanced = !dialogueEnhanced)}
        class="flex items-center space-x-1 px-3 py-1.5 border rounded text-xs font-mono whitespace-nowrap transition focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {dialogueEnhanced ? 'bg-[#24323f] text-[#bac8d9] border-[#bac8d9]' : 'bg-[#1e2024] border-[#333539] text-[#e2e2e8] hover:bg-[#282a2e]'}"
        aria-pressed={dialogueEnhanced}
      >
        <span>🎙 Enhance Dialogue</span>
      </button>
    </div>
  </section>

  <!-- SEGMENTED NAVIGATION TABS -->
  <section aria-label="Media Hub Sections">
    <div class="flex items-center space-x-2 border-b border-[#57423b]/60 overflow-x-auto pb-1 scrollbar-thin font-mono text-xs">
      <button
        type="button"
        id="tab-theses"
        on:click={() => (activeTab = 'theses')}
        class="flex items-center space-x-1.5 pb-2 border-b-2 px-3 font-semibold transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {activeTab === 'theses' ? 'border-[#e36f46] text-[#ffb59c]' : 'border-transparent text-[#dec0b7] hover:text-white'}"
      >
        <span>💡 Core Theses ({theses.length})</span>
      </button>

      <button
        type="button"
        id="tab-chapters"
        on:click={() => (activeTab = 'chapters')}
        class="flex items-center space-x-1.5 pb-2 border-b-2 px-3 font-semibold transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {activeTab === 'chapters' ? 'border-[#e36f46] text-[#ffb59c]' : 'border-transparent text-[#dec0b7] hover:text-white'}"
      >
        <span>📋 Chapters ({timecodes.length})</span>
      </button>

      <button
        type="button"
        id="tab-transcript"
        on:click={() => (activeTab = 'transcript')}
        class="flex items-center space-x-1.5 pb-2 border-b-2 px-3 font-semibold transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {activeTab === 'transcript' ? 'border-[#e36f46] text-[#ffb59c]' : 'border-transparent text-[#dec0b7] hover:text-white'}"
      >
        <span>📜 Full Transcript</span>
      </button>

      <button
        type="button"
        id="tab-cited"
        on:click={() => (activeTab = 'cited')}
        class="flex items-center space-x-1.5 pb-2 border-b-2 px-3 font-semibold transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {activeTab === 'cited' ? 'border-[#e36f46] text-[#ffb59c]' : 'border-transparent text-[#dec0b7] hover:text-white'}"
      >
        <span>📚 Cited Works</span>
      </button>
    </div>
  </section>

  <!-- TAB CONTENT DISPLAY -->
  {#if activeTab === 'theses'}
    <!-- CORE THESES SECTION -->
    <section id="core-theses-section" class="space-y-4">
      <div class="flex items-center justify-between">
        <h2 class="text-base font-bold text-white font-mono flex items-center space-x-2">
          <span>Key Critical Theses</span>
        </h2>
        <span class="text-xs font-mono text-[#f0bd8b] uppercase">Click Thesis to Seek Audio</span>
      </div>

      <div class="space-y-4">
        {#each theses as th, idx}
          {@const isCurrentThesis = activeThesis?.id === th.id}
          <article
            class="bg-[#1a1c20] border rounded-xl p-4 sm:p-5 relative overflow-hidden transition-all shadow-md {isCurrentThesis ? 'border-[#e36f46] ring-1 ring-[#e36f46]/30' : 'border-[#333539] hover:border-[#57423b]'}"
          >
            <div class="absolute left-0 top-0 bottom-0 w-1 {isCurrentThesis ? 'bg-[#e36f46]' : 'bg-[#f0bd8b]'}"></div>

            <div class="flex items-start justify-between gap-2">
              <div class="flex items-center space-x-2 flex-wrap">
                <span class="font-mono text-[10px] text-[#f0bd8b] font-bold uppercase tracking-wider">{th.categoryTag}</span>
                {#if isCurrentThesis && isPlaying}
                  <span class="inline-flex items-center px-1.5 py-0.5 rounded text-[9px] font-mono font-bold bg-[#e36f46] text-[#511500] animate-pulse">
                    NOW PLAYING
                  </span>
                {/if}
              </div>

              <span class="font-mono text-xs text-[#ffb59c] bg-[#1e2024] px-2 py-0.5 rounded border border-[#333539]">
                [{formatTime(th.timeOffsetSeconds)}]
              </span>
            </div>

            <blockquote class="mt-3 font-serif text-base sm:text-lg text-white leading-snug">
              {th.quote}
            </blockquote>

            <p class="mt-2 text-xs sm:text-sm text-[#dec0b7] leading-relaxed">
              {th.explanation}
            </p>

            <div class="mt-4 pt-3 border-t border-[#333539]/80 flex flex-wrap items-center justify-between gap-2">
              <span class="text-xs font-mono text-[#a58b83]">
                {th.sectionLabel || `Part ${idx + 1}`}
              </span>

              <button
                type="button"
                id="seek-thesis-btn-{th.id}"
                on:click={(e) => handleThesisClick(th, e)}
                class="flex items-center space-x-1.5 text-xs font-mono font-bold text-[#ffb59c] hover:text-white bg-[#1e2024] px-3 py-1.5 rounded border border-[#57423b] hover:border-[#ffb59c] active:scale-95 transition focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
                aria-label="Seek audio to thesis timestamp {formatTime(th.timeOffsetSeconds)}"
              >
                <span>▶ Seek {formatTime(th.timeOffsetSeconds)}</span>
              </button>
            </div>
          </article>
        {/each}
      </div>
    </section>
  {/if}

  <!-- SYNCHRONIZED TIMESTAMPS & CHAPTERS STREAM -->
  <section id="timecodes-section" class="space-y-3 pt-4 border-t border-[#333539]">
    <div class="flex items-center justify-between text-xs font-mono text-[#a58b83]">
      <h2 class="text-sm font-bold text-white">ТАЙМКОДЫ И ТЕМЫ ({timecodes.length})</h2>
      <span class="text-[#f0bd8b]">КЛИКНИТЕ ДЛЯ ПЕРЕХОДА</span>
    </div>

    <ul id="timecodes-list" class="divide-y divide-[#333539] border border-[#333539] rounded-xl bg-[#1a1c20] overflow-hidden" aria-label="Episode Timecodes">
      {#each timecodes as tc}
        {@const isActive = activeTimecodeId === tc.id}
        {@const isCompleted = currentTime > tc.timeOffsetSeconds + 60 && !isActive}
        <li role="listitem">
          <button
            type="button"
            id="timecode-btn-{tc.id}"
            on:click={(e) => handleTimecodeClick(tc, e)}
            class="timecode-item w-full p-4 text-left flex flex-col sm:flex-row sm:items-center justify-between gap-3 transition-colors focus:outline-none focus:ring-2 focus:ring-[#ffb59c] {isActive ? 'bg-[#1e2024] border-l-4 border-[#e36f46]' : 'hover:bg-[#1e2024]/50'}"
          >
            <div class="flex items-start space-x-3">
              <span class="mt-0.5 text-sm font-bold shrink-0 {isCompleted ? 'text-[#ffb59c]' : isActive ? 'text-[#e36f46]' : 'text-[#a58b83]'}">
                {isCompleted ? '✓' : isActive ? '▶' : '⏱'}
              </span>

              <div>
                <div class="flex items-center space-x-2">
                  <p class="font-bold text-sm text-white hover:text-[#ffb59c] transition">{tc.title}</p>
                  {#if isActive}
                    <span class="text-[10px] font-mono text-[#e36f46] font-bold uppercase">СЕЙЧАС ИГРАЕТ</span>
                  {/if}
                </div>
                {#if tc.description}
                  <p class="text-xs text-[#dec0b7] mt-1 leading-relaxed">{tc.description}</p>
                {/if}
              </div>
            </div>

            <div class="flex items-center justify-between sm:justify-end gap-3 font-mono text-xs shrink-0">
              <span class="px-2 py-0.5 rounded bg-[#0c0e12] border border-[#333539] text-[#ffb59c] font-bold">
                [{formatTime(tc.timeOffsetSeconds)}]
              </span>

              <span class="px-2.5 py-1 rounded text-[11px] font-bold border transition {isActive ? 'bg-[#e36f46] text-[#511500] border-[#ffb59c]' : 'bg-[#1e2024] text-[#e2e2e8] border-[#333539]'}">
                {isActive ? 'PLAYING' : 'JUMP'}
              </span>
            </div>
          </button>
        </li>
      {/each}
    </ul>
  </section>

  {#if activeTab === 'transcript'}
    <!-- TRANSCRIPT EXCERPT CARD -->
    <section id="transcript-section" class="p-5 rounded-xl bg-[#1a1c20] border border-[#333539] space-y-3">
      <div class="flex items-center justify-between text-xs font-mono text-[#f0bd8b]">
        <span class="font-bold uppercase">SYNCHRONIZED TRANSCRIPT EXCERPT</span>
        <span class="text-[#ffb59c]">Active Chapter Excerpt</span>
      </div>

      <p class="font-serif text-base text-[#dec0b7] leading-relaxed">
        {currentEpisode.transcriptExcerpt || "«...we do not simply read slower for the sake of endurance; friction is the neurological tax required to convert external data into internal dialectic structure. Without the resistance of the sentence...»"}
      </p>

      <div class="pt-2 text-xs font-mono text-[#a58b83]">
        Full verbatim AI-agent transcript available for search indexing at <a href="/.well-known/llms.txt" class="text-[#ffb59c] underline">/.well-known/llms.txt</a>
      </div>
    </section>
  {:else if activeTab === 'cited'}
    <!-- CITED WORKS SECTION -->
    <section id="cited-section" class="p-5 rounded-xl bg-[#1a1c20] border border-[#333539] space-y-3 font-mono text-xs">
      <h2 class="text-sm font-bold text-white">Cited Works &amp; Primary Sources</h2>
      <ul class="space-y-2 text-[#dec0b7] list-disc list-inside">
        <li>Barcan Marcus, Ruth. <em>Modalities: Philosophical Essays</em>. Oxford University Press, 1993.</li>
        <li>Thagard, Paul. <em>Coherence in Thought and Action</em>. MIT Press, 2000.</li>
        <li>Hurley, Susan. <em>Consciousness in Action</em>. Harvard University Press, 1998.</li>
        <li>Ryle, Gilbert. <em>The Concept of Mind</em>. Hutchinson, 1949.</li>
      </ul>
    </section>
  {/if}

  <!-- PERSISTENT MINI-PLAYER DOCKING BAR -->
  <aside
    id="mini-player-bar"
    class="fixed bottom-0 left-0 w-full z-40 bg-[#1e2024] border-t border-[#57423b] shadow-2xl transition-all"
    aria-label="Persistent Mini Audio Player"
  >
    <!-- Top Edge Progress Bar -->
    <div class="w-full h-1 bg-[#111317]">
      <div class="h-full bg-[#e36f46] transition-all duration-75" style="width: {progressPercent}%"></div>
    </div>

    <div class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-3">
      <!-- Media Info -->
      <div class="flex items-center space-x-3 truncate">
        <div class="w-10 h-10 rounded bg-[#1a1c20] border border-[#57423b] flex items-center justify-center shrink-0 text-[#ffb59c] font-bold font-mono">
          {#if isPlaying}
            <div class="flex items-center space-x-0.5 h-4">
              <span class="w-[2px] bg-[#e36f46] eq-bar-1"></span>
              <span class="w-[2px] bg-[#e36f46] eq-bar-2"></span>
              <span class="w-[2px] bg-[#e36f46] eq-bar-3"></span>
            </div>
          {:else}
            🎙
          {/if}
        </div>

        <div class="truncate">
          <p class="text-xs font-bold text-white truncate">{currentEpisode.title}</p>
          <div class="flex items-center space-x-1 text-[11px] font-mono text-[#dec0b7] truncate">
            <span class="text-[#ffb59c] font-bold">{formatTime(currentTime)}</span>
            <span>·</span>
            <span class="truncate">{activeTimecode ? activeTimecode.title : 'Chapter 1'}</span>
          </div>
        </div>
      </div>

      <!-- Quick Controls -->
      <div class="flex items-center space-x-2 shrink-0">
        <button
          type="button"
          on:click={() => skipSeconds(-15)}
          class="p-2 text-[#dec0b7] hover:text-white transition active:scale-95 focus:outline-none focus:ring-2 focus:ring-[#ffb59c] rounded font-mono text-xs font-bold"
          title="Rewind 15s"
          aria-label="Mini-player rewind 15 seconds"
        >
          ↺ 15s
        </button>

        <button
          type="button"
          on:click={togglePlay}
          aria-label={isPlaying ? 'Mini-player Pause' : 'Mini-player Play'}
          class="w-10 h-10 rounded-full bg-[#e36f46] text-[#511500] font-bold flex items-center justify-center hover:bg-[#ffb59c] active:scale-95 transition shadow-md focus:outline-none focus:ring-2 focus:ring-[#ffb59c]"
        >
          {#if isPlaying}
            <span class="text-base font-bold">❚❚</span>
          {:else}
            <span class="text-base pl-0.5 font-bold">▶</span>
          {/if}
        </button>

        <button
          type="button"
          on:click={() => skipSeconds(30)}
          class="p-2 text-[#dec0b7] hover:text-white transition active:scale-95 focus:outline-none focus:ring-2 focus:ring-[#ffb59c] rounded font-mono text-xs font-bold"
          title="Forward 30s"
          aria-label="Mini-player forward 30 seconds"
        >
          ↻ 30s
        </button>
      </div>
    </div>
  </aside>

</div>
