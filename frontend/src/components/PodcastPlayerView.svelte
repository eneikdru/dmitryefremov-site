<script lang="ts">
  import { onMount } from 'svelte';
  import SeoMeta from './SeoMeta.svelte';

  function getYouTubeEmbedUrl(url?: string): string | null {
    if (!url) return null;
    const match = url.match(/(?:youtu\.be\/|youtube\.com\/(?:watch\?v=|embed\/))([a-zA-Z0-9_-]{11})/);
    return match ? `https://www.youtube-nocookie.com/embed/${match[1]}?rel=0` : null;
  }
  import { telemetry } from '../lib/telemetry';
  import { currentLanguage, t } from '../lib/i18n';
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
  export let theme: 'dark' | 'light' = 'light';

  let audioElement: HTMLAudioElement | null = null;
  let isPlaying: boolean = false;
  let currentTime: number = 0;
  let volume: number = 1.0;
  let isMuted: boolean = false;
  let waveformContainer: HTMLElement;
  let isLoading = false;
  // A populated bundled fallback is not the current publication catalog.
  let isCatalogLoading = episodes === PODCAST_EPISODES;

  onMount(async () => {
    if (isCatalogLoading) {
      try {
        const fetched = await fetchPodcastsFromApi();
        if (fetched && fetched.length > 0) episodes = fetched;
      } finally {
        isCatalogLoading = false;
      }
    }
    if (episodes.length > 0 && selectedEpisodeId === undefined) {
      selectedEpisodeId = episodes[0].id;
    }
  });

  $: localizedEpisodes = episodes.map(ep => getLocalizedPodcast(ep, $currentLanguage));
  $: legacySlug = PODCAST_EPISODES.find(ep => String(ep.id) === String(selectedEpisodeId))?.slug;
  $: currentEpisode = selectedEpisodeId ? (localizedEpisodes.find(ep => ep.id === selectedEpisodeId || String(ep.id) === String(selectedEpisodeId) || ep.slug === selectedEpisodeId) || localizedEpisodes.find(ep => ep.slug === legacySlug) || null) : (localizedEpisodes[0] || null);
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

  export function seekTo(seconds: number, startPlaying: boolean = true) {
    if (!currentEpisode) return;
    const boundedTime = Math.max(0, Math.min(seconds, currentEpisode.durationSeconds));
    currentTime = boundedTime;
    if (audioElement) {
      audioElement.currentTime = boundedTime;
      if (startPlaying && audioElement.paused) {
        play();
      }
    }
  }

  export function play() {
    if (!audioElement) return;
    isLoading = true;
    const playPromise = audioElement.play();
    if (playPromise !== undefined) {
      playPromise
        .then(() => {
          isPlaying = true;
          isLoading = false;
        })
        .catch(err => {
          console.warn('Playback error:', err);
          isPlaying = false;
          isLoading = false;
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

  function handleSelectEpisode(epId: number | string, autoPlay: boolean = true) {
    const isSame = selectedEpisodeId === epId;
    if (isSame) {
      togglePlay();
      return;
    }

    selectedEpisodeId = epId;
    currentTime = 0;
    isPlaying = false;
    isLoading = true;

    setTimeout(() => {
      if (audioElement) {
        try {
          audioElement.load();
        } catch (e) {
          console.warn('Audio load error:', e);
        }
        if (autoPlay) {
          play();
        }
      }
    }, 60);
  }

  function handleTimecodeClick(tc: PodcastTimecode, event?: MouseEvent | KeyboardEvent) {
    seekTo(tc.timeOffsetSeconds, true);
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

{#if currentEpisode}
  <SeoMeta
    title="{currentEpisode.title} | Дмитрий Ефремов"
    description={currentEpisode.thesis || currentEpisode.description || currentEpisode.title}
    url={`https://dmitryefremov.com/podcasts/${currentEpisode.slug}`}
    alternateRuUrl={`https://dmitryefremov.com/ru/podcasts/${currentEpisode.slug}`}
    alternateEnUrl={`https://dmitryefremov.com/en/podcasts/${currentEpisode.slug}`}
    alternateKaUrl={`https://dmitryefremov.com/ka/podcasts/${currentEpisode.slug}`}
  />
{/if}

{#if isCatalogLoading}
  <div role="status" aria-live="polite" class="px-4 py-12 text-center {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
    {$currentLanguage === 'ka' ? 'იტვირთება…' : $currentLanguage === 'en' ? 'Loading…' : 'Загрузка…'}
  </div>
{:else if selectedEpisodeId && !currentEpisode}
  <div id="podcast-not-found" class="flex flex-col items-center justify-center min-h-[50vh] px-4 text-center">
    <div class="px-3 py-1 rounded-sm border text-xs mb-4 {theme === 'dark' ? 'bg-red-500/10 border-red-500/30 text-red-400' : 'bg-[#F1EFE9] border-[#8C2D19]/30 text-[#8C2D19]'}">
      HTTP 404
    </div>
    <h2 class="font-serif text-3xl font-medium mb-2 {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">Выпуск не найден</h2>
    <p class="text-sm max-w-md mb-6 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
      Запрошенный выпуск «{selectedEpisodeId}» отсутствует в публичном реестре подкаста.
    </p>
    <button
      on:click={() => { selectedEpisodeId = undefined; if (typeof window !== 'undefined') window.history.pushState({}, '', '/podcasts'); }}
      class="text-sm px-5 py-2.5 rounded-sm font-medium transition-colors cursor-pointer {theme === 'dark' ? 'bg-[#C9A461] text-[#141413] hover:bg-[#d8b573]' : 'bg-[#8C2D19] text-[#FAF8F4] hover:bg-[#702313]'}"
    >
      ← Все выпуски
    </button>
  </div>
{:else}
<div id="podcast-player-container" class="space-y-8 font-sans">
  <!-- Editorial Section Header -->
  <div class="flex flex-col sm:flex-row sm:items-baseline justify-between gap-4 border-b pb-5 {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}">
    <div class="space-y-1">
      <h2 class="font-serif text-3xl sm:text-4xl font-medium tracking-[-0.02em] leading-[1.15] {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">
        {$currentLanguage === 'ru'
          ? 'Подкаст «Системы и реальность»'
          : ($currentLanguage === 'ka'
            ? 'პოდკასტი «სისტემები და რეალობა»'
            : 'Podcast "Systems and Reality"')}
      </h2>
    </div>

    <!-- External Media Platforms -->
    <div class="flex flex-wrap items-center gap-5 text-sm font-sans">
      <a
        href={currentEpisode?.youtubeUrl || 'https://www.youtube.com/@dmitrii.efremov'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="YouTube Channel"
        on:click={() => handleOutboundClick(currentEpisode?.youtubeUrl || 'https://www.youtube.com/@dmitrii.efremov', 'YouTube Channel', 'YouTube')}
        class="underline underline-offset-4 decoration-1 transition-colors {theme === 'dark' ? 'text-[#a8a398] hover:text-[#C9A461]' : 'text-[#57554F] hover:text-[#8C2D19]'}"
      >
        <span>YouTube ↗</span>
      </a>
      <a
        href={currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Telegram Channel"
        on:click={() => handleOutboundClick(currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp', 'Telegram Channel', 'Telegram')}
        class="underline underline-offset-4 decoration-1 transition-colors {theme === 'dark' ? 'text-[#a8a398] hover:text-[#C9A461]' : 'text-[#57554F] hover:text-[#8C2D19]'}"
      >
        <span>Telegram @efremov_mvp ↗</span>
      </a>
    </div>
  </div>

  <!-- Main Grid: Editorial Episode Index (Left) & Active Listening Room (Right) -->
  {#if episodes.length === 0}
    <div id="podcast-empty-state" class="p-16 text-center rounded-sm border space-y-3 {theme === 'dark' ? 'border-white/10 bg-[#1c1b18]' : 'border-[#E2DFD7] bg-[#F1EFE9]'}">
      <div class="font-serif text-xl font-medium {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">Выпуски подкаста отсутствуют</div>
      <div class="text-sm {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">На данный момент в системе нет опубликованных выпусков подкаста.</div>
    </div>
  {:else}
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-start">
      <!-- Episode Index Rail -->
      <div id="podcast-episodes-section" class="lg:col-span-5 order-2 lg:order-1">
        <div class="font-sans text-xs font-medium mb-4 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
          {t($currentLanguage).catalog_title}
        </div>

        <div class="border-t divide-y {theme === 'dark' ? 'border-white/10 divide-white/10' : 'border-[#E2DFD7] divide-[#E2DFD7]'}" role="list" aria-label="Podcast Episodes Catalog">
          {#each localizedEpisodes as ep, idx}
            {@const isSelected = selectedEpisodeId === ep.id || (!selectedEpisodeId && idx === 0)}
            <button
              type="button"
              id="episode-card-{ep.id}"
              class="episode-card w-full py-5 px-4 border-l-2 transition-colors cursor-pointer text-left focus:outline-none {isSelected
                ? (theme === 'dark' ? 'bg-[#1c1b18] border-l-[#C9A461]' : 'bg-[#F1EFE9] border-l-[#8C2D19]')
                : (theme === 'dark' ? 'border-l-transparent hover:bg-[#1c1b18]/60' : 'border-l-transparent hover:bg-[#F1EFE9]/60')}"
              on:click={() => handleSelectEpisode(ep.id)}
            >
              <div class="flex justify-between items-center text-xs mb-1.5">
                <span class="font-medium {isSelected ? (theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]') : (theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]')}">
                  {t($currentLanguage).catalog_badge} {String(idx + 1).padStart(2, '0')}
                </span>
                <span class="font-mono text-[11px] {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">{formatTime(ep.durationSeconds)}</span>
              </div>
              <h3 class="font-serif text-lg sm:text-xl font-medium mb-1.5 leading-snug transition-colors {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">
                {ep.title}
              </h3>
              <p class="text-sm line-clamp-2 leading-relaxed {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
                {ep.thesis}
              </p>
              <div class="mt-3 text-xs flex justify-between items-center">
                <span class="{theme === 'dark' ? 'text-[#a8a398]/80' : 'text-[#57554F]/80'}">{ep.publishedAt}</span>
                <span class="font-medium {isSelected ? (theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]') : (theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]')}">
                  {isSelected ? t($currentLanguage).catalog_active : t($currentLanguage).catalog_listen}
                </span>
              </div>
            </button>
          {/each}
        </div>
      </div>

      <!-- Active Listening Room -->
      {#if currentEpisode}
        <div id="podcast-player-section" class="lg:col-span-7 flex flex-col space-y-6 order-1 lg:order-2">
          <div id="podcast-player-card" class="p-5 sm:p-8 rounded-sm border space-y-6 transition-colors {theme === 'dark' ? 'bg-[#1c1b18] text-[#EAE6DF] border-white/10' : 'bg-[#F1EFE9]/65 text-[#151514] border-[#E2DFD7]'}">
            <!-- Episode Header -->
            <header id="podcast-header" class="border-b pb-5 space-y-4 {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}">
              <div class="space-y-2">
                <div class="flex items-center justify-between gap-2 text-xs">
                  <span class="font-medium {theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]'}">
                    {t($currentLanguage).catalog_badge}
                  </span>
                  <span class="{theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">{currentEpisode.publishedAt}</span>
                </div>
                <h2 id="podcast-title" class="text-2xl sm:text-3xl font-serif font-medium tracking-[-0.02em] leading-[1.15] {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">
                  {currentEpisode.title}
                </h2>
              </div>

              <!-- Philosophical Thesis Axiom -->
              <blockquote class="pl-4 py-1 border-l-2 font-serif italic text-base sm:text-lg leading-relaxed {theme === 'dark' ? 'border-[#C9A461] text-[#EAE6DF]/90' : 'border-[#8C2D19] text-[#151514]/90'}">
                «{currentEpisode.thesis}»
              </blockquote>
            </header>

            <!-- YouTube Cinema Video Embed (if available) -->
            {#if currentEpisode && currentEpisode.youtubeUrl}
              {@const ytEmbedUrl = getYouTubeEmbedUrl(currentEpisode.youtubeUrl)}
              {#if ytEmbedUrl}
                <div id="youtube-embed-wrapper" class="rounded-sm overflow-hidden border bg-black aspect-video {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}">
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
              src={currentEpisode?.audioUrl}
              on:play={() => (isPlaying = true)}
              on:pause={() => (isPlaying = false)}
              on:waiting={() => (isLoading = true)}
              on:playing={() => { isPlaying = true; isLoading = false; }}
              on:canplay={() => (isLoading = false)}
              on:timeupdate={handleAudioTimeUpdate}
              on:ended={handleAudioEnded}
              preload="auto"
            ></audio>

            <!-- Audio Controls & Waveform Console -->
            <div id="audio-controls-block" class="p-4 sm:p-5 rounded-sm border space-y-4 {theme === 'dark' ? 'bg-[#141413] border-white/10' : 'bg-[#FAF8F4] border-[#E2DFD7]'}">
              <div class="flex items-center justify-between gap-4 flex-wrap">
                <!-- Tactile Play/Pause Trigger -->
                <button
                  id="play-pause-btn"
                  type="button"
                  on:click={togglePlay}
                  aria-label={isPlaying ? 'Pause Podcast Episode' : 'Play Podcast Episode'}
                  class="w-12 h-12 rounded-sm font-medium flex items-center justify-center transition-colors cursor-pointer focus:outline-none {theme === 'dark' ? 'bg-[#C9A461] hover:bg-[#d8b573] text-[#141413]' : 'bg-[#8C2D19] hover:bg-[#702313] text-[#FAF8F4]'}"
                >
                  {#if isPlaying}
                    <span class="text-base">❚❚</span>
                  {:else}
                    <span class="text-base pl-0.5">▶</span>
                  {/if}
                </button>

                <!-- Precise Time Coordinates -->
                <div id="time-display" class="font-mono text-sm flex items-center gap-2 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
                  <span id="current-time-val" class="font-medium text-base {theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]'}">{formatTime(currentTime)}</span>
                  <span class="opacity-50">/</span>
                  <span id="duration-val" class="text-sm">{formatTime(currentEpisode.durationSeconds)}</span>
                </div>
              </div>

              <!-- Waveform Visualization -->
              <div class="space-y-2">
                <div class="text-xs font-sans {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
                  <span class="sr-only">ВИЗУАЛИЗАЦИЯ ЗВУКОВОЙ ВОЛНЫ</span>
                  <span aria-hidden="true">{$currentLanguage === 'ru' ? 'Аудиодорожка' : ($currentLanguage === 'ka' ? 'აუდიო ბილიკი' : 'Audio waveform')}</span>
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
                  class="h-14 p-2 rounded-sm border flex items-end justify-between gap-1 cursor-pointer transition-colors focus:outline-none {theme === 'dark' ? 'bg-[#1c1b18] border-white/10 hover:border-[#C9A461]/50' : 'bg-[#F1EFE9] border-[#E2DFD7] hover:border-[#8C2D19]/50'}"
                >
                  {#each waveBars as barHeight, i}
                    {@const barProgress = (i / waveBars.length) * 100}
                    {@const isPlayed = barProgress <= progressPercent}
                    <div
                      class="flex-1 rounded-t-sm transition-all duration-75 {isPlayed
                        ? (theme === 'dark' ? 'bg-[#C9A461]' : 'bg-[#8C2D19]')
                        : (theme === 'dark' ? 'bg-white/15 hover:bg-white/30' : 'bg-[#151514]/15 hover:bg-[#151514]/30')}"
                      style="height: {barHeight}%;"
                    ></div>
                  {/each}
                </div>
              </div>
            </div>

            <!-- Deep Context Description -->
            <p id="podcast-description" class="text-sm sm:text-base leading-[1.7] max-w-[65ch] {theme === 'dark' ? 'text-[#EAE6DF]/85' : 'text-[#151514]/85'}">
              {currentEpisode.description}
            </p>

            <!-- Structured Timecodes & Semantic Chapters -->
            {#if currentEpisode.timecodes && currentEpisode.timecodes.length > 0}
              <div id="timecodes-section" class="border-t pt-5 space-y-3 {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}">
                <div class="font-sans text-xs font-medium {theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]'}">
                  <span class="sr-only">ТАЙМКОДЫ И ТЕМЫ</span>
                  <span aria-hidden="true">{$currentLanguage === 'ru' ? 'Таймкоды и темы' : ($currentLanguage === 'ka' ? 'ტაიმკოდები და თემები' : 'Timecodes and chapters')}</span>
                </div>

                <ul id="timecodes-list" class="divide-y border-t {theme === 'dark' ? 'divide-white/10 border-white/10' : 'divide-[#E2DFD7] border-[#E2DFD7]'}" aria-label="Episode Timecodes">
                  {#each currentEpisode.timecodes as tc}
                    {@const isActive = activeTimecodeId === tc.id}
                    <li role="listitem">
                      <button
                        type="button"
                        id="timecode-btn-{tc.id}"
                        on:click={(e) => handleTimecodeClick(tc, e)}
                        class="timecode-item w-full py-3 px-2.5 transition-colors text-left flex items-start gap-3.5 cursor-pointer focus:outline-none {isActive
                          ? (theme === 'dark' ? 'bg-[#141413] text-[#EAE6DF]' : 'bg-[#FAF8F4] text-[#151514]')
                          : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF]' : 'text-[#57554F] hover:text-[#151514]')}"
                      >
                        <span class="font-mono text-xs px-2 py-0.5 rounded-sm shrink-0 {isActive
                          ? (theme === 'dark' ? 'bg-[#C9A461] text-[#141413] font-medium' : 'bg-[#8C2D19] text-[#FAF8F4] font-medium')
                          : (theme === 'dark' ? 'bg-white/10 text-[#C9A461]' : 'bg-[#E2DFD7]/70 text-[#8C2D19]')}">
                          {formatTime(tc.timeOffsetSeconds)}
                        </span>
                        <div class="min-w-0 flex-1">
                          <div class="font-medium text-sm flex items-center justify-between gap-2 {isActive ? (theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]') : ''}">
                            <span class="truncate">{tc.title}</span>
                            {#if isActive}
                              <span class="text-xs shrink-0 font-medium {theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]'}">
                                <span class="sr-only">СЕЙЧАС ИГРАЕТ</span>
                                <span aria-hidden="true">{$currentLanguage === 'ru' ? 'Сейчас играет' : ($currentLanguage === 'ka' ? 'ახლა უკრავს' : 'Now playing')}</span>
                              </span>
                            {/if}
                          </div>
                          {#if tc.description}
                            <p class="text-xs mt-1 line-clamp-1 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">{tc.description}</p>
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

{/if}
