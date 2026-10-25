<script lang="ts">
  import { onMount } from 'svelte';

  function getYouTubeEmbedUrl(url?: string): string | null {
    if (!url) return null;
    const match = url.match(/(?:youtu\.be\/|youtube\.com\/(?:watch\?v=|embed\/))([a-zA-Z0-9_-]{11})/);
    return match ? `https://www.youtube-nocookie.com/embed/${match[1]}?rel=0` : null;
  }
  import { telemetry } from '../lib/telemetry';
  import {
    PODCAST_EPISODES,
    fetchPodcastsFromApi,
    formatTime,
    generateWaveformHeights,
    type PodcastEpisode,
    type PodcastTimecode
  } from '../lib/podcast';

  export let episodes: PodcastEpisode[] = PODCAST_EPISODES;
  export let selectedEpisodeId: number | string | undefined = undefined;

  let audioElement: HTMLAudioElement | null = null;
  let isPlaying: boolean = false;
  let currentTime: number = 0;
  let volume: number = 1.0;
  let isMuted: boolean = false;
  let waveformContainer: HTMLElement;
  let isLoading = false;

  onMount(async () => {
    if (!episodes || episodes.length === 0) {
      isLoading = true;
      const fetched = await fetchPodcastsFromApi();
      if (fetched && fetched.length > 0) {
        episodes = fetched;
      }
      isLoading = false;
    }
    if (episodes.length > 0 && selectedEpisodeId === undefined) {
      selectedEpisodeId = episodes[0].id;
    }
  });

  $: currentEpisode = episodes.find(ep => ep.id === selectedEpisodeId || String(ep.id) === String(selectedEpisodeId) || ep.slug === selectedEpisodeId) || episodes[0] || null;
  $: waveBars = currentEpisode ? generateWaveformHeights(40, currentEpisode.slug) : [];
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

  function handleOutboundClick(destination: string, label: string, platform: string) {
    telemetry.trackOutboundClick(destination, label, platform);
  }
</script>

<div id="podcast-player-container" class="space-y-6">
  <!-- Header & Domain Indicator -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1f2433] pb-4">
    <div>
      <h2 class="text-xl font-bold tracking-tight text-white break-words">Подкаст «Системы и реальность»</h2>
    </div>

    <!-- External Media Subscription Links -->
    <div class="flex flex-wrap gap-2 text-xs font-mono">
      <a
        href={currentEpisode?.youtubeUrl || 'https://www.youtube.com/@dmitrii.efremov'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="YouTube Channel"
        on:click={() => handleOutboundClick(currentEpisode?.youtubeUrl || 'https://www.youtube.com/@dmitrii.efremov', 'YouTube Channel', 'YouTube')}
        class="px-2.5 py-1 rounded bg-[#12151e] text-[#ef4444] border border-[#1f2433] hover:border-[#ef4444] transition-colors flex items-center gap-1"
      >
        <span>YouTube</span>
        
      </a>
      <a
        href={currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Telegram Channel"
        on:click={() => handleOutboundClick(currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp', 'Telegram Channel', 'Telegram')}
        class="px-2.5 py-1 rounded bg-[#12151e] text-[#38bdf8] border border-[#1f2433] hover:border-[#38bdf8] transition-colors flex items-center gap-1"
      >
        <span>Telegram</span>
        
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
  {#if episodes.length === 0}
    <div id="podcast-empty-state" class="p-12 text-center rounded-2xl border border-dashed border-[#1f2433] bg-[#090a0f] space-y-3 font-mono">
      
      <div class="text-white font-bold text-base">Выпуски подкаста отсутствуют</div>
      <div class="text-xs text-[#94a3b8]">На данный момент в системе нет опубликованных выпусков подкаста.</div>
    </div>
  {:else}
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
      <!-- Episode List Section -->
      <div id="podcast-episodes-section" class="md:col-span-5 space-y-3">
        <div class="text-xs font-mono text-[#94a3b8] mb-2">Каталог выпусков</div>

        <div class="space-y-3" role="list" aria-label="Podcast Episodes Catalog">
          {#each episodes as ep}
            <button
              type="button"
              id="episode-card-{ep.id}"
              class="episode-card w-full p-4 rounded-xl border transition-all cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#34d399] {selectedEpisodeId === ep.id ? 'bg-[#12151e] border-[#34d399] shadow-lg' : 'bg-[#090a0f] border-[#1f2433] hover:border-[#38bdf8]/50 hover:bg-[#12151e]/50'}"
              on:click={() => handleSelectEpisode(ep.id)}
            >
              <div class="flex justify-between items-center text-xs font-mono mb-2">
                
                <span class="text-[#64748b]">{formatTime(ep.durationSeconds)}</span>
              </div>
              <h3 class="font-bold text-white text-sm mb-1.5 hover:text-[#34d399] transition-colors leading-snug">
                {ep.title}
              </h3>
              <p class="text-xs text-[#94a3b8] line-clamp-2 leading-relaxed">
                {ep.thesis}
              </p>
              <div class="mt-3 text-[11px] font-mono text-[#64748b] flex justify-between items-center">
                <span></span>
                <span class="text-[#34d399] font-medium">{selectedEpisodeId === ep.id ? 'Активен' : 'Слушать'}</span>
              </div>
            </button>
          {/each}
        </div>
      </div>

      <!-- Active Audio Player & Waveform Section -->
      {#if currentEpisode}
        <div id="podcast-player-section" class="md:col-span-7 flex flex-col space-y-6">
          <div id="podcast-player-card" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-2xl border border-[#1f2433] space-y-5 font-sans shadow-xl">
            <!-- Episode Header -->
            <header id="podcast-header" class="border-b border-[#1f2433] pb-4 space-y-3">
              <div class="flex flex-col sm:flex-row items-start sm:items-center gap-4">
                <div class="flex-1 space-y-1">
                  <div class="flex items-center justify-between gap-2 mb-1">
                    
                    <span class="text-xs font-mono text-[#94a3b8]">{currentEpisode.publishedAt}</span>
                  </div>
                  <h1 id="podcast-title" class="text-lg sm:text-xl font-bold tracking-tight text-white leading-tight">{currentEpisode.title}</h1>
                </div>
              </div>
              <blockquote class="p-3 rounded-lg bg-[#12151e] border-l-4 border-[#34d399] text-xs italic text-[#cbd5e1] leading-relaxed">
                «{currentEpisode.thesis}»
              </blockquote>
            </header>

            <!-- YouTube Video Embed -->
            {#if currentEpisode && currentEpisode.youtubeUrl}
              {@const ytEmbedUrl = getYouTubeEmbedUrl(currentEpisode.youtubeUrl)}
              {#if ytEmbedUrl}
                <div id="youtube-embed-wrapper" class="rounded-xl overflow-hidden border border-[#1f2433] bg-black aspect-video shadow-lg">
                  <iframe
                    src={ytEmbedUrl}
                    title={currentEpisode.title}
                    class="w-full h-full border-0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen
                  ></iframe>
                </div>
              {/if}
            {/if}

            <!-- Hidden Native HTML5 Audio Element for Playback State -->
            <audio
              bind:this={audioElement}
              src={currentEpisode.audioUrl}
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
                
              </div>

              <!-- Waveform Visualization -->
              <div class="space-y-1.5">
                <div class="text-[11px] font-mono text-[#64748b]">ВИЗУАЛИЗАЦИЯ ЗВУКОВОЙ ВОЛНЫ</div>

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
                      class="flex-1 rounded-t transition-all duration-75 {isPlayed ? 'bg-[#34d399]' : 'bg-[#1f2433] hover:bg-[#38bdf8]'}"
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
            {#if currentEpisode.timecodes && currentEpisode.timecodes.length > 0}
              <div id="timecodes-section" class="border-t border-[#1f2433] pt-4 space-y-3">
                <div class="text-xs font-mono text-[#94a3b8]">ТАЙМКОДЫ И ТЕМЫ</div>

                <ul id="timecodes-list" class="space-y-2" aria-label="Episode Timecodes">
                  {#each currentEpisode.timecodes as tc}
                    {@const isActive = activeTimecodeId === tc.id}
                    <li role="listitem">
                      <button
                        type="button"
                        id="timecode-btn-{tc.id}"
                        on:click={(e) => handleTimecodeClick(tc, e)}
                        class="timecode-item w-full p-2.5 rounded-lg border transition-all text-left flex items-start gap-3 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] focus:ring-offset-2 focus:ring-offset-[#090a0f] {isActive ? 'bg-[#12151e] border-[#38bdf8] text-white shadow-sm' : 'bg-[#090a0f] border-[#1f2433] text-[#94a3b8] hover:border-[#38bdf8]/50 hover:text-white hover:bg-[#12151e]/60'}"
                      >
                        <span class="font-mono text-xs font-bold px-2 py-0.5 rounded shrink-0 {isActive ? 'bg-[#38bdf8] text-[#090a0f]' : 'bg-[#1f2433] text-[#34d399]'}">
                          {formatTime(tc.timeOffsetSeconds)}
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
                    </li>
                  {/each}
                </ul>
              </div>
            {/if}
          </div>
        </div>
      {/if}
    </div>
  {/if}
</div>
