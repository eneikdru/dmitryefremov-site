<script lang="ts">
  import { onMount } from 'svelte';

  function getYouTubeEmbedUrl(url?: string): string | null {
    if (!url) return null;
    const match = url.match(/(?:youtu\.be\/|youtube\.com\/(?:watch\?v=|embed\/))([a-zA-Z0-9_-]{11})/);
    return match ? `https://www.youtube-nocookie.com/embed/${match[1]}?rel=0` : null;
  }
  import { telemetry } from '../lib/telemetry';
  import { currentLanguage } from '../lib/i18n';
  import {
    PODCAST_EPISODES,
    fetchPodcastsFromApi,
    formatTime,
    generateWaveformHeights,
    getLocalizedPodcast,
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

  $: localizedEpisodes = episodes.map(ep => getLocalizedPodcast(ep, $currentLanguage));
  $: currentEpisode = localizedEpisodes.find(ep => ep.id === selectedEpisodeId || String(ep.id) === String(selectedEpisodeId) || ep.slug === selectedEpisodeId) || localizedEpisodes[0] || null;
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

  export function seekTo(seconds: number) {
    if (!currentEpisode) return;
    const boundedTime = Math.max(0, Math.min(seconds, currentEpisode.durationSeconds));
    currentTime = boundedTime;
    if (audioElement) {
      audioElement.currentTime = boundedTime;
    }
  }

  export function play() {
    if (audioElement && audioElement.paused) {
      audioElement.play().then(() => {
        isPlaying = true;
      }).catch(err => {
        console.warn('Playback error:', err);
      });
    }
  }

  export function pause() {
    if (audioElement && !audioElement.paused) {
      audioElement.pause();
      isPlaying = false;
    }
  }

  function togglePlay() {
    if (isPlaying) {
      pause();
    } else {
      play();
    }
  }

  function handleSelectEpisode(epId: number | string) {
    selectedEpisodeId = epId;
    currentTime = 0;
    isPlaying = false;
    if (audioElement) {
      audioElement.currentTime = 0;
      audioElement.pause();
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

<div id="podcast-player-container" class="space-y-8 font-sans">
  <!-- Architectural Header: Title & Subscription Coordinates -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-white/[0.08] pb-5">
    <div class="space-y-1">

      <h2 class="text-2xl sm:text-3xl font-extrabold tracking-tight text-white uppercase">
        {$currentLanguage === 'ru' 
          ? 'Подкаст «Системы и реальность»' 
          : ($currentLanguage === 'ka' 
            ? 'პოდკასტი «სისტემები და რეალობა»' 
            : 'Podcast "Systems and Reality"')}
      </h2>
    </div>

    <!-- External Media Platforms (Tactile, Zero /feed.xml noise) -->
    <div class="flex flex-wrap items-center gap-2.5 text-xs font-mono font-bold uppercase tracking-wider">
      <a
        href={currentEpisode?.youtubeUrl || 'https://www.youtube.com/@dmitrii.efremov'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="YouTube Channel"
        on:click={() => handleOutboundClick(currentEpisode?.youtubeUrl || 'https://www.youtube.com/@dmitrii.efremov', 'YouTube Channel', 'YouTube')}
        class="px-4 py-2 rounded-lg bg-[#0e1219] text-white border border-white/10 hover:border-amber-400/60 hover:bg-[#141a24] transition-all flex items-center gap-2 shadow-sm"
      >
        <span>YouTube ↗</span>
      </a>
      <a
        href={currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Telegram Channel"
        on:click={() => handleOutboundClick(currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp', 'Telegram Channel', 'Telegram')}
        class="px-4 py-2 rounded-lg bg-[#0e1219] text-white border border-white/10 hover:border-amber-400/60 hover:bg-[#141a24] transition-all flex items-center gap-2 shadow-sm"
      >
        <span>Telegram @efremov_mvp ↗</span>
      </a>
    </div>
  </div>

  <!-- Main Grid: Episodes List (Left) & Active Player / Timecodes (Right) -->
  {#if episodes.length === 0}
    <div id="podcast-empty-state" class="p-16 text-center rounded-xl border border-white/10 bg-[#0d1118] space-y-3 font-mono">
      <div class="text-white font-bold text-base uppercase tracking-wider">Выпуски подкаста отсутствуют</div>
      <div class="text-xs text-slate-400">На данный момент в системе нет опубликованных выпусков подкаста.</div>
    </div>
  {:else}
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
      <!-- Episode Catalog Rail -->
      <div id="podcast-episodes-section" class="lg:col-span-5 space-y-3">
        <div class="font-mono text-xs uppercase tracking-widest text-slate-400 font-bold mb-3">
          КАТАЛОГ ВЫПУСКОВ
        </div>

        <div class="space-y-3" role="list" aria-label="Podcast Episodes Catalog">
          {#each episodes as ep}
            <button
              type="button"
              id="episode-card-{ep.id}"
              class="episode-card w-full p-5 rounded-xl border transition-all cursor-pointer text-left focus:outline-none focus:ring-1 focus:ring-amber-400 {selectedEpisodeId === ep.id ? 'bg-[#141a26] border-amber-400/60 shadow-xl ring-1 ring-amber-400/30' : 'bg-[#0d1118] border-white/[0.08] hover:border-white/20 hover:bg-[#111620]'}"
              on:click={() => handleSelectEpisode(ep.id)}
            >
              <div class="flex justify-between items-center text-xs font-mono mb-2">
                <span class="font-bold text-amber-400 uppercase tracking-widest">ВЫПУСК</span>
                <span class="text-slate-400">{formatTime(ep.durationSeconds)}</span>
              </div>
              <h3 class="font-bold text-white text-base mb-2 hover:text-amber-300 transition-colors leading-snug">
                {ep.title}
              </h3>
              <p class="text-sm text-slate-300 line-clamp-2 leading-relaxed">
                {ep.thesis}
              </p>
              <div class="mt-4 text-xs font-mono flex justify-between items-center border-t border-white/[0.06] pt-3">
                <span class="text-slate-500">{ep.publishedAt}</span>
                <span class="font-bold uppercase tracking-wider {selectedEpisodeId === ep.id ? 'text-amber-400' : 'text-slate-400'}">
                  {selectedEpisodeId === ep.id ? 'АКТИВЕН' : 'СЛУШАТЬ →'}
                </span>
              </div>
            </button>
          {/each}
        </div>
      </div>

      <!-- Active Player Console -->
      {#if currentEpisode}
        <div id="podcast-player-section" class="lg:col-span-7 flex flex-col space-y-6">
          <div id="podcast-player-card" class="bg-[#0d1118] text-[#e2e8f0] p-6 sm:p-8 rounded-xl border border-white/[0.08] space-y-6 shadow-2xl">
            <!-- Episode Header -->
            <header id="podcast-header" class="border-b border-white/[0.08] pb-5 space-y-4">
              <div class="space-y-2">
                <div class="flex items-center justify-between gap-2">
                  <span class="font-mono text-xs uppercase tracking-widest text-amber-400 font-bold">
                    ВЫПУСК
                  </span>
                  <span class="text-xs font-mono text-slate-400">{currentEpisode.publishedAt}</span>
                </div>
                <h1 id="podcast-title" class="text-xl sm:text-2xl lg:text-3xl font-extrabold tracking-tight text-white leading-tight">
                  {currentEpisode.title}
                </h1>
              </div>

              <!-- Philosophical Thesis Axiom -->
              <blockquote class="p-4 rounded-lg bg-[#080a0e] border-l-2 border-amber-400 text-sm sm:text-base text-slate-200 leading-relaxed font-sans">
                «{currentEpisode.thesis}»
              </blockquote>
            </header>

            <!-- YouTube Cinema Video Embed (if available) -->
            {#if currentEpisode && currentEpisode.youtubeUrl}
              {@const ytEmbedUrl = getYouTubeEmbedUrl(currentEpisode.youtubeUrl)}
              {#if ytEmbedUrl}
                <div id="youtube-embed-wrapper" class="rounded-lg overflow-hidden border border-white/10 bg-black aspect-video shadow-2xl">
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

            <!-- Hidden Native HTML5 Audio Element -->
            <audio
              bind:this={audioElement}
              src={currentEpisode.audioUrl}
              on:timeupdate={handleAudioTimeUpdate}
              on:ended={handleAudioEnded}
              preload="auto"
            ></audio>

            <!-- Mechanical Audio Controls & Waveform Console -->
            <div id="audio-controls-block" class="bg-[#080a0e] p-5 sm:p-6 rounded-lg border border-white/[0.08] space-y-5">
              <div class="flex items-center justify-between gap-4 flex-wrap">
                <!-- Tactile Mechanical Play/Pause Trigger -->
                <button
                  id="play-pause-btn"
                  type="button"
                  on:click={togglePlay}
                  aria-label={isPlaying ? 'Pause Podcast Episode' : 'Play Podcast Episode'}
                  class="w-14 h-14 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-extrabold flex items-center justify-center transition-all transform active:scale-95 shadow-lg shadow-amber-400/20 cursor-pointer focus:outline-none focus:ring-2 focus:ring-amber-400"
                >
                  {#if isPlaying}
                    <span class="text-xl">❚❚</span>
                  {:else}
                    <span class="text-xl pl-1">▶</span>
                  {/if}
                </button>

                <!-- Precise Time Coordinates -->
                <div id="time-display" class="font-mono text-sm flex items-center gap-2 text-slate-300">
                  <span id="current-time-val" class="text-amber-400 font-bold text-base sm:text-lg">{formatTime(currentTime)}</span>
                  <span class="text-slate-500">/</span>
                  <span id="duration-val" class="text-slate-400 text-sm">{formatTime(currentEpisode.durationSeconds)}</span>
                </div>
              </div>

              <!-- Precision Waveform Visualization -->
              <div class="space-y-2">
                <div class="text-[11px] font-mono uppercase tracking-widest text-slate-400 font-bold">
                  ВИЗУАЛИЗАЦИЯ ЗВУКОВОЙ ВОЛНЫ
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
                  class="h-16 bg-[#0d1118] p-2.5 rounded-lg border border-white/10 flex items-end justify-between gap-1 cursor-pointer hover:border-amber-400/50 transition-colors focus:outline-none focus:ring-1 focus:ring-amber-400"
                >
                  {#each waveBars as barHeight, i}
                    {@const barProgress = (i / waveBars.length) * 100}
                    {@const isPlayed = barProgress <= progressPercent}
                    <div
                      class="flex-1 rounded-t transition-all duration-75 {isPlayed ? 'bg-amber-400' : 'bg-white/10 hover:bg-white/20'}"
                      style="height: {barHeight}%;"
                    ></div>
                  {/each}
                </div>
              </div>
            </div>

            <!-- Deep Context Description -->
            <p id="podcast-description" class="text-sm sm:text-base text-slate-300 leading-relaxed">
              {currentEpisode.description}
            </p>

            <!-- Structured Timecodes & Semantic Chapters -->
            {#if currentEpisode.timecodes && currentEpisode.timecodes.length > 0}
              <div id="timecodes-section" class="border-t border-white/[0.08] pt-5 space-y-3">
                <div class="font-mono text-xs uppercase tracking-widest text-amber-400 font-bold">
                  {$currentLanguage === 'ru' ? 'ТАЙМКОДЫ И ТЕМЫ' : ($currentLanguage === 'ka' ? 'ტაიმკოდები და თემები' : 'TIMECODES & TOPICS')}
                </div>

                <ul id="timecodes-list" class="space-y-2" aria-label="Episode Timecodes">
                  {#each currentEpisode.timecodes as tc}
                    {@const isActive = activeTimecodeId === tc.id}
                    <li role="listitem">
                      <button
                        type="button"
                        id="timecode-btn-{tc.id}"
                        on:click={(e) => handleTimecodeClick(tc, e)}
                        class="timecode-item w-full p-3 rounded-lg border transition-all text-left flex items-start gap-3.5 cursor-pointer focus:outline-none focus:ring-1 focus:ring-amber-400 {isActive ? 'bg-[#181e2b] border-amber-400/60 text-white shadow-md' : 'bg-[#080a0e] border-white/10 text-slate-300 hover:border-white/20 hover:text-white'}"
                      >
                        <span class="font-mono text-xs font-bold px-2 py-0.5 rounded shrink-0 {isActive ? 'bg-amber-400 text-black' : 'bg-white/10 text-amber-300'}">
                          {formatTime(tc.timeOffsetSeconds)}
                        </span>
                        <div class="min-w-0 flex-1">
                          <div class="font-bold text-sm flex items-center justify-between gap-2">
                            <span class="truncate">{tc.title}</span>
                            {#if isActive}
                              <span class="text-[10px] font-mono text-amber-400 shrink-0 font-bold uppercase tracking-wider">
                                {$currentLanguage === 'ru' ? 'СЕЙЧАС ИГРАЕТ' : ($currentLanguage === 'ka' ? 'ახლა უკრავს' : 'NOW PLAYING')}
                              </span>
                            {/if}
                          </div>
                          {#if tc.description}
                            <p class="text-xs text-slate-400 mt-1 line-clamp-1">{tc.description}</p>
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
