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
  let volume: number = 1.0;
  let isMuted: boolean = false;
  let waveformContainer: HTMLElement;

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
    if (isPlaying) {
      isPlaying = false;
      audioElement.pause();
    } else {
      isPlaying = true;
      audioElement.play().catch((err) => {
        isPlaying = false;
        console.warn("Audio playback interaction:", err);
      });
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

  function handleTimecodeClick(tc: PodcastTimecode) {
    seekTo(tc.timeOffsetSeconds);
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

<div id="podcast-player-container" class="space-y-6">
  <!-- Header & Domain Indicator -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1f2433] pb-4">
    <div>
      <div class="flex items-center gap-2 mb-1 flex-wrap">
        <span class="bg-[#12151e] text-[#34d399] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
          [PODCAST_CATALOG]
        </span>
        <span class="text-xs font-mono text-[#94a3b8]">Выпусков: {episodes.length}</span>
      </div>
      <h2 class="text-xl font-bold tracking-tight text-white break-words">Подкаст «Системы и реальность»</h2>
    </div>

    <!-- External Media Subscription Links -->
    <div class="flex flex-wrap gap-2 text-xs font-mono">
      <a
        href={currentEpisode?.youtubeUrl || 'https://youtube.com/@dmitrii.efremov'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="YouTube Channel"
        class="px-2.5 py-1 rounded bg-[#12151e] text-[#ef4444] border border-[#1f2433] hover:border-[#ef4444] transition-colors flex items-center gap-1"
      >
        <span>YouTube</span>
        <span>↗</span>
      </a>
      <a
        href={currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Telegram Channel"
        class="px-2.5 py-1 rounded bg-[#12151e] text-[#38bdf8] border border-[#1f2433] hover:border-[#38bdf8] transition-colors flex items-center gap-1"
      >
        <span>Telegram</span>
        <span>↗</span>
      </a>
      <a
        href="/feed.xml"
        target="_blank"
        rel="noopener noreferrer"
        aria-label="RSS Feed"
        class="px-2.5 py-1 rounded bg-[#12151e] text-[#94a3b8] border border-[#1f2433] hover:text-white transition-colors flex items-center gap-1"
      >
        <span>RSS Feed</span>
      </a>
    </div>
  </div>

  <!-- Main Grid: Episodes List (Left) & Active Player / Timecodes (Right) -->
  <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
    <!-- Episode List Section -->
    <div id="podcast-episodes-section" class="md:col-span-5 space-y-3">
      <div class="text-xs font-mono text-[#94a3b8] flex justify-between items-center mb-2">
        <span>КАТАЛОГ ВЫПУСКОВ ({episodes.length})</span>
        <span>ВЫБЕРИТЕ ВЫПУСК</span>
      </div>

      <div class="space-y-3" role="list" aria-label="Podcast Episodes Catalog">
        {#each episodes as ep}
          <button
            type="button"
            id="episode-card-{ep.id}"
            class="episode-card w-full p-4 rounded-xl border transition-all cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#34d399] {selectedEpisodeId === ep.id ? 'bg-[#12151e] border-[#34d399] shadow-lg' : 'bg-[#090a0f] border-[#1f2433] hover:border-[#38bdf8]/50 hover:bg-[#12151e]/50'}"
            on:click={() => handleSelectEpisode(ep.id)}
          >
            <div class="flex justify-between items-center text-xs font-mono mb-2">
              <span class="px-2 py-0.5 rounded bg-[#1f2433] text-[#34d399] uppercase font-bold text-[11px]">
                {ep.philosopher}
              </span>
              <span class="text-[#64748b]">{formatTime(ep.durationSeconds)}</span>
            </div>
            <h3 class="font-bold text-white text-sm mb-1.5 hover:text-[#34d399] transition-colors leading-snug">
              {ep.title}
            </h3>
            <p class="text-xs text-[#94a3b8] line-clamp-2 leading-relaxed">
              {ep.thesis}
            </p>
            <div class="mt-3 text-[11px] font-mono text-[#64748b] flex justify-between items-center">
              <span>{ep.timecodes.length} таймкодов</span>
              <span class="text-[#34d399] font-medium">{selectedEpisodeId === ep.id ? '● АКТИВЕН' : 'СЛУШАТЬ →'}</span>
            </div>
          </button>
        {/each}
      </div>
    </div>

    <!-- Active Audio Player & Waveform Section -->
    <div id="podcast-player-section" class="md:col-span-7 flex flex-col space-y-6">
      <div id="podcast-player-card" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-2xl border border-[#1f2433] space-y-5 font-sans shadow-xl">
        <!-- Episode Header -->
        <header id="podcast-header" class="border-b border-[#1f2433] pb-4 space-y-3">
          <div class="flex flex-col sm:flex-row items-start sm:items-center gap-4">
            <div id="podcast-cover-container" class="w-20 h-20 sm:w-24 sm:h-24 shrink-0 rounded-xl overflow-hidden border border-[#34d399]/40 shadow-lg">
              <ResponsivePicture
                id="podcast-cover-img"
                assetKey="podcast_studio_cover"
                alt="Обложка подкаста Системы и реальность — Студийное оформление"
                imgClass="w-full h-full object-cover"
                loading="eager"
              />
            </div>
            <div class="flex-1 space-y-1">
              <div class="flex items-center justify-between gap-2 mb-1">
                <span class="bg-[#12151e] text-[#34d399] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono font-medium">
                  {currentEpisode.philosopher}
                </span>
                <span class="text-xs font-mono text-[#94a3b8]">{currentEpisode.publishedAt}</span>
              </div>
              <h1 id="podcast-title" class="text-lg sm:text-xl font-bold tracking-tight text-white leading-tight">{currentEpisode.title}</h1>
            </div>
          </div>
          <blockquote class="p-3 rounded-lg bg-[#12151e] border-l-4 border-[#34d399] text-xs italic text-[#cbd5e1] leading-relaxed">
            «{currentEpisode.thesis}»
          </blockquote>
        </header>

        <!-- Hidden Native HTML5 Audio Element for Playback State -->
        <audio
          bind:this={audioElement}
          src={currentEpisode.audioUrl}
          on:play={() => (isPlaying = true)}
          on:pause={() => (isPlaying = false)}
          on:timeupdate={handleAudioTimeUpdate}
          on:ended={handleAudioEnded}
          preload="auto"
        ></audio>

        <!-- Interactive Audio Controls & Visualizer Bar -->
        <div id="audio-controls-block" class="bg-[#12151e] p-5 rounded-xl border border-[#1f2433] space-y-4">
          <div class="flex items-center justify-between gap-4">
            <!-- Play / Pause Button -->
            <button
              id="play-pause-btn"
              type="button"
              on:click={togglePlay}
              aria-label={isPlaying ? 'Pause Podcast Episode' : 'Play Podcast Episode'}
              class="w-14 h-14 rounded-full bg-[#34d399] hover:bg-[#10b981] text-[#090a0f] font-bold flex items-center justify-center transition-all transform active:scale-95 shadow-lg shadow-[#34d399]/20 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#34d399]"
            >
              {#if isPlaying}
                <span class="text-xl">❚❚</span>
              {:else}
                <span class="text-xl pl-1">▶</span>
              {/if}
            </button>

            <!-- Time Display -->
            <div id="time-display" class="font-mono text-sm flex items-center gap-2 text-[#cbd5e1]">
              <span id="current-time-val" class="text-[#34d399] font-bold text-base">{formatTime(currentTime)}</span>
              <span class="text-[#64748b]">/</span>
              <span id="duration-val" class="text-[#94a3b8]">{formatTime(currentEpisode.durationSeconds)}</span>
            </div>

            <!-- Volume / Progress Indicator -->
            <div class="flex items-center gap-2 text-xs font-mono text-[#94a3b8]">
              <span class="bg-[#1f2433] px-2.5 py-1 rounded-md text-[#38bdf8] font-bold">
                {progressPercent.toFixed(0)}%
              </span>
            </div>
          </div>

          <!-- Waveform Visualization -->
          <div class="space-y-1.5">
            <div class="flex justify-between items-center text-[11px] font-mono text-[#64748b]">
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
              aria-valuemax={currentEpisode.durationSeconds}
              aria-valuenow={currentTime}
              aria-valuetext="{formatTime(currentTime)} of {formatTime(currentEpisode.durationSeconds)}"
              class="h-16 bg-[#090a0f] p-2 rounded-lg border border-[#1f2433] flex items-end justify-between gap-1 cursor-pointer hover:border-[#34d399] transition-colors focus:outline-none focus:ring-2 focus:ring-[#34d399]"
            >
              {#each waveBars as barHeight, i}
                {@const barProgress = (i / waveBars.length) * 100}
                {@const isPlayed = barProgress <= progressPercent}
                <div
                  class="flex-1 rounded-t transition-all duration-75 {isPlayed ? 'bg-[#34d399]' : 'bg-[#1f2433]'}"
                  style="height: {barHeight}%;"
                ></div>
              {/each}
            </div>
          </div>
        </div>

        <!-- Description Paragraph -->
        <p id="podcast-description" class="text-xs text-[#cbd5e1] leading-relaxed">
          {currentEpisode.description}
        </p>

        <!-- Timecodes List Section -->
        <div id="timecodes-section" class="border-t border-[#1f2433] pt-4 space-y-3">
          <div class="flex items-center justify-between text-xs font-mono text-[#94a3b8]">
            <span>ТАЙМКОДЫ И ТЕМЫ ({currentEpisode.timecodes.length})</span>
            <span class="text-[#38bdf8]">КЛИКНИТЕ ДЛЯ ПЕРЕХОДА</span>
          </div>

          <div id="timecodes-list" class="space-y-2" role="list" aria-label="Episode Timecodes">
            {#each currentEpisode.timecodes as tc}
              {@const isActive = activeTimecodeId === tc.id}
              <button
                type="button"
                id="timecode-btn-{tc.id}"
                on:click={() => handleTimecodeClick(tc)}
                class="timecode-item w-full p-2.5 rounded-lg border transition-all text-left flex items-start gap-3 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {isActive ? 'bg-[#12151e] border-[#38bdf8] text-white shadow-sm' : 'bg-[#090a0f] border-[#1f2433] text-[#94a3b8] hover:border-[#38bdf8]/50 hover:text-white hover:bg-[#12151e]/60'}"
              >
                <span class="font-mono text-xs font-bold px-2 py-0.5 rounded shrink-0 {isActive ? 'bg-[#38bdf8] text-[#090a0f]' : 'bg-[#1f2433] text-[#34d399]'}">
                  [{formatTime(tc.timeOffsetSeconds)}]
                </span>
                <div class="min-w-0 flex-1">
                  <div class="font-bold text-xs flex items-center justify-between">
                    <span class="truncate">{tc.title}</span>
                    {#if isActive}
                      <span class="text-[10px] font-mono text-[#38bdf8] shrink-0 font-bold">СЕЙЧАС ИГРАЕТ</span>
                    {/if}
                  </div>
                  {#if tc.description}
                    <p class="text-[11px] text-[#64748b] mt-0.5 line-clamp-1">{tc.description}</p>
                  {/if}
                </div>
              </button>
            {/each}
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
