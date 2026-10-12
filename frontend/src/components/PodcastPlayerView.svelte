<script lang="ts">
  import { onMount } from 'svelte';
  import {
    PODCAST_EPISODES,
    formatTime,
    generateWaveformHeights,
    type PodcastEpisode,
    type PodcastTimecode,
    type PodcastCoreThesis
  } from '../lib/podcast';
  import ResponsivePicture from './ResponsivePicture.svelte';

  export let episodes: PodcastEpisode[] = PODCAST_EPISODES;
  export let selectedEpisodeId: number | string = PODCAST_EPISODES[0]?.id;

  let audioElement: HTMLAudioElement | null = null;
  let isPlaying: boolean = false;
  let currentTime: number = 0;
  let playbackRate: number = 1.0;
  let activeTab: 'theses' | 'chapters' | 'transcript' | 'cited' = 'theses';
  let isSyncReadingMode: boolean = false;
  let isDialogueEnhanced: boolean = false;
  let waveformContainer: HTMLElement;

  const speedOptions = [1.0, 1.2, 1.5, 2.0];

  $: currentEpisode = episodes.find(ep => ep.id === selectedEpisodeId) || episodes[0];
  $: waveBars = generateWaveformHeights(40, currentEpisode ? currentEpisode.slug : 'default');
  $: progressPercent = currentEpisode && currentEpisode.durationSeconds > 0
    ? Math.min(100, Math.max(0, (currentTime / currentEpisode.durationSeconds) * 100))
    : 0;

  $: activeTimecode = currentEpisode?.timecodes?.reduce((acc: PodcastTimecode | null, tc, idx, arr) => {
    const nextTc = arr[idx + 1];
    if (currentTime >= tc.timeOffsetSeconds && (!nextTc || currentTime < nextTc.timeOffsetSeconds)) {
      return tc;
    }
    return acc;
  }, null);

  $: activeTimecodeId = activeTimecode?.id ?? null;

  $: activeThesis = currentEpisode?.coreTheses?.reduce((acc: PodcastCoreThesis | null, thesis, idx, arr) => {
    const nextThesis = arr[idx + 1];
    if (currentTime >= thesis.timestampSeconds && (!nextThesis || currentTime < nextThesis.timestampSeconds)) {
      return thesis;
    }
    return acc;
  }, null);

  $: activeThesisId = activeThesis?.id ?? null;

  $: remainingTime = currentEpisode
    ? Math.max(0, currentEpisode.durationSeconds - currentTime)
    : 0;

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
          // Audio playback fallback for environments without audio hardware
        });
      } else {
        audioElement.pause();
      }
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

  function rewind(seconds: number = 15) {
    seekTo(currentTime - seconds);
  }

  function fastForward(seconds: number = 30) {
    seekTo(currentTime + seconds);
  }

  function skipPreviousChapter() {
    if (!currentEpisode || currentEpisode.timecodes.length === 0) return;
    const currentIdx = currentEpisode.timecodes.findIndex(tc => tc.id === activeTimecodeId);
    if (currentIdx > 0) {
      seekTo(currentEpisode.timecodes[currentIdx - 1].timeOffsetSeconds);
    } else {
      seekTo(0);
    }
  }

  function skipNextChapter() {
    if (!currentEpisode || currentEpisode.timecodes.length === 0) return;
    const currentIdx = currentEpisode.timecodes.findIndex(tc => tc.id === activeTimecodeId);
    if (currentIdx >= 0 && currentIdx < currentEpisode.timecodes.length - 1) {
      seekTo(currentEpisode.timecodes[currentIdx + 1].timeOffsetSeconds);
    }
  }

  function togglePlaybackSpeed() {
    const currentIdx = speedOptions.indexOf(playbackRate);
    const nextIdx = (currentIdx + 1) % speedOptions.length;
    playbackRate = speedOptions[nextIdx];
    if (audioElement) {
      audioElement.playbackRate = playbackRate;
    }
  }

  function handleTimecodeClick(tc: PodcastTimecode) {
    seekTo(tc.timeOffsetSeconds);
  }

  function handleThesisClick(thesis: PodcastCoreThesis) {
    seekTo(thesis.timestampSeconds);
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

<div id="podcast-player-container" class="space-y-6 text-[#e2e8f0]">
  <!-- Podcast Hub Main Title Header -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1f2433] pb-4">
    <div>
      <div class="flex items-center gap-2 mb-1 flex-wrap">
        <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">
          [PODCAST_CATALOG]
        </span>
        <span class="text-xs font-mono text-[#94a3b8]">Episodes: {episodes.length}</span>
      </div>
      <h2 class="text-xl font-bold tracking-tight text-white">«Системы и реальность» Podcast</h2>
    </div>

    <!-- External Links -->
    <div class="flex flex-wrap gap-2 text-xs font-mono">
      <a
        href={currentEpisode?.youtubeUrl || 'https://youtube.com/@efremov_dmitriy'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="YouTube Channel"
        class="px-2.5 py-1 rounded bg-[#12151e] text-[#ef4444] border border-[#1f2433] hover:border-[#ef4444] transition flex items-center gap-1 focus:outline-none focus:ring-2 focus:ring-[#ef4444]"
      >
        <span>YouTube</span>
        <span aria-hidden="true">↗</span>
      </a>
      <a
        href={currentEpisode?.telegramUrl || 'https://t.me/efremov_mvp'}
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Telegram Channel"
        class="px-2.5 py-1 rounded bg-[#12151e] text-[#38bdf8] border border-[#1f2433] hover:border-[#38bdf8] transition flex items-center gap-1 focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
      >
        <span>Telegram</span>
        <span aria-hidden="true">↗</span>
      </a>
    </div>
  </div>

  <!-- Sub-Header Metabar -->
  <div class="border border-[#1f2433] bg-[#12151e] rounded-lg p-3 sm:p-4 flex flex-wrap items-center justify-between gap-3 shadow-sm">
    <div class="flex items-center space-x-3">
      <span class="inline-block w-2 h-2 rounded-full bg-[#38bdf8] animate-ping" aria-hidden="true"></span>
      <span class="font-mono text-xs text-[#38bdf8] font-bold tracking-wider uppercase">
        {currentEpisode?.issueTag || 'ISSUE 48 · THE ALGORITHMIC SUBLIME'}
      </span>
    </div>

    <div class="flex items-center space-x-3 text-xs font-mono text-[#94a3b8]">
      <button
        type="button"
        on:click={togglePlaybackSpeed}
        aria-label="Toggle playback speed (Currently {playbackRate}x)"
        class="bg-[#090a0f] hover:bg-[#1f2433] px-2.5 py-1 rounded border border-[#1f2433] text-white focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition"
      >
        Speed {playbackRate}x
      </button>
    </div>
  </div>

  <!-- Main Grid Layout: Episode Catalog (Left) & Unified Media Player Canvas (Right) -->
  <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
    <!-- Episodes Selector Panel -->
    <div id="podcast-episodes-section" class="md:col-span-4 space-y-3">
      <div class="text-xs font-mono text-[#94a3b8] flex justify-between items-center mb-1">
        <span>STREAM_EPISODES ({episodes.length})</span>
        <span>SELECT_EPISODE</span>
      </div>

      <div class="space-y-3" role="list" aria-label="Podcast Episodes Catalog">
        {#each episodes as ep}
          <button
            type="button"
            id="episode-card-{ep.id}"
            class="episode-card w-full p-4 rounded-lg border transition-all cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedEpisodeId === ep.id ? 'bg-[#12151e] border-[#38bdf8] shadow-md' : 'bg-[#090a0f] border-[#1f2433] hover:border-[#38bdf8]/50 hover:bg-[#12151e]/50'}"
            on:click={() => handleSelectEpisode(ep.id)}
          >
            <div class="flex justify-between items-center text-xs font-mono mb-2">
              <span class="px-1.5 py-0.5 rounded bg-[#1f2433] text-[#38bdf8] uppercase">
                {ep.publishedAt}
              </span>
              <span class="text-[#64748b]">{formatTime(ep.durationSeconds)}</span>
            </div>
            <h3 class="font-bold text-white text-sm mb-1 hover:text-[#38bdf8] transition-colors leading-snug">
              {ep.title}
            </h3>
            <p class="text-xs text-[#94a3b8] line-clamp-2 leading-relaxed">
              {ep.thesis}
            </p>
            <div class="mt-3 text-[11px] font-mono text-[#64748b] flex justify-between items-center">
              <span>{ep.timecodes.length} Chapters</span>
              <span class="text-[#38bdf8]">{selectedEpisodeId === ep.id ? '● LISTENING' : 'LISTEN →'}</span>
            </div>
          </button>
        {/each}
      </div>
    </div>

    <!-- Unified Media Player Hub -->
    <div id="podcast-player-section" class="md:col-span-8 space-y-6">
      <div id="podcast-player-card" class="bg-[#090a0f] text-[#e2e8f0] p-5 sm:p-6 rounded-xl border border-[#1f2433] space-y-6 relative overflow-hidden shadow-2xl">
        <!-- Episode Header & Metadata -->
        <header id="podcast-header" class="relative z-10 space-y-3 border-b border-[#1f2433] pb-4">
          <div class="flex items-center justify-between">
            <span class="font-mono text-xs uppercase text-[#38bdf8] tracking-widest font-semibold">
              Monograph Lecture
            </span>
            <span class="font-mono text-xs text-[#94a3b8] bg-[#12151e] px-2.5 py-1 rounded border border-[#1f2433]">
              {formatTime(currentEpisode.durationSeconds)} Audio Essay
            </span>
          </div>

          <div class="flex flex-col sm:flex-row items-start sm:items-center gap-4 pt-1">
            <div id="podcast-cover-container" class="w-16 h-16 sm:w-20 sm:h-20 shrink-0 rounded-lg overflow-hidden border border-[#38bdf8]/40 shadow">
              <ResponsivePicture
                id="podcast-cover-img"
                assetKey="podcast_studio_cover"
                alt="Studio Cover Illustration"
                imgClass="w-full h-full object-cover"
                loading="eager"
              />
            </div>

            <div class="space-y-1">
              <h1 id="podcast-title" class="text-xl sm:text-2xl font-bold tracking-tight text-white leading-snug">
                {currentEpisode.title}
              </h1>
              <div class="flex items-center space-x-2 text-xs text-[#94a3b8] pt-0.5">
                <span class="w-5 h-5 rounded-full bg-[#1f2433] border border-[#38bdf8]/40 flex items-center justify-center text-[9px] font-bold text-[#38bdf8]">EV</span>
                <p>
                  Written by <span class="text-white font-medium">{currentEpisode.authorName || 'Elena Vance'}</span> · Read by <span class="text-white font-medium">{currentEpisode.narratorName || 'Marcus Sterling'}</span>
                </p>
              </div>
            </div>
          </div>
        </header>

        <!-- Hidden Native HTML5 Audio Element -->
        <audio
          bind:this={audioElement}
          src={currentEpisode.audioUrl}
          on:timeupdate={handleAudioTimeUpdate}
          on:ended={handleAudioEnded}
          preload="metadata"
        ></audio>

        <!-- Waveform Visualizer Display & Scrubber Section -->
        <div id="audio-controls-block" class="bg-[#12151e] p-4 rounded-lg border border-[#1f2433] space-y-3">
          <div class="flex items-center justify-between text-xs font-mono">
            <div class="flex items-center space-x-2 truncate pr-2">
              <span class="w-2 h-2 rounded-full bg-[#38bdf8] shrink-0" aria-hidden="true"></span>
              <span class="text-white font-medium truncate">
                {activeTimecode?.title || 'Chapter 1: Introduction'}
              </span>
            </div>
            <span class="text-[11px] font-mono text-[#64748b]">WAVE_VISUALIZATION</span>
          </div>

          <!-- Interactive Waveform Visualizer Bar -->
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
            class="h-14 bg-[#090a0f] p-2 rounded border border-[#1f2433] flex items-end justify-between gap-1 cursor-pointer hover:border-[#38bdf8] transition focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
          >
            {#each waveBars as barHeight, i}
              {@const barProgress = (i / waveBars.length) * 100}
              {@const isPlayed = barProgress <= progressPercent}
              <div
                class="flex-1 rounded-t transition-all duration-75 {isPlayed ? 'bg-[#38bdf8]' : 'bg-[#1f2433] hover:bg-[#38bdf8]/60'}"
                style="height: {barHeight}%;"
              ></div>
            {/each}
          </div>

          <!-- Progress Track Scrubber & Precise Timestamps -->
          <div class="space-y-1">
            <div class="h-1.5 bg-[#090a0f] w-full rounded overflow-hidden border border-[#1f2433] relative">
              <div
                class="h-full bg-[#38bdf8] transition-all duration-100 rounded"
                style="width: {progressPercent}%"
              ></div>
            </div>
            <div id="time-display" class="flex justify-between items-center font-mono text-[11px] text-[#94a3b8]">
              <span id="current-time-val" class="text-[#38bdf8] font-bold">{formatTime(currentTime)}</span>
              <span class="text-[#64748b]">Remaining: {formatTime(remainingTime)}</span>
              <span id="duration-val">{formatTime(currentEpisode.durationSeconds)}</span>
            </div>
          </div>

          <!-- Tactile Playback Controls -->
          <div class="pt-2 flex items-center justify-around">
            <button
              type="button"
              on:click={skipPreviousChapter}
              title="Previous Chapter"
              aria-label="Skip to previous chapter"
              class="p-2.5 rounded text-[#94a3b8] hover:text-white transition active:scale-95 focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
            >
              <span class="font-mono text-base font-bold">⏮</span>
            </button>

            <button
              type="button"
              on:click={() => rewind(15)}
              title="Rewind 15 Seconds"
              aria-label="Rewind 15 seconds"
              class="p-2.5 rounded text-[#94a3b8] hover:text-white transition active:scale-95 flex flex-col items-center focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
            >
              <span class="font-mono text-xs font-bold">↺ 15s</span>
            </button>

            <!-- Main Play/Pause Button -->
            <button
              id="play-pause-btn"
              type="button"
              on:click={togglePlay}
              aria-label={isPlaying ? 'Pause Podcast Episode' : 'Play Podcast Episode'}
              class="w-14 h-14 rounded-full bg-[#38bdf8] hover:bg-[#0284c7] text-[#090a0f] font-bold flex items-center justify-center shadow-lg shadow-[#38bdf8]/20 transition active:scale-95 focus:outline-none focus:ring-2 focus:ring-[#38bdf8] border border-[#38bdf8]"
            >
              {#if isPlaying}
                <span class="text-xl">❚❚</span>
              {:else}
                <span class="text-xl pl-1">▶</span>
              {/if}
            </button>

            <button
              type="button"
              on:click={() => fastForward(30)}
              title="Forward 30 Seconds"
              aria-label="Forward 30 seconds"
              class="p-2.5 rounded text-[#94a3b8] hover:text-white transition active:scale-95 flex flex-col items-center focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
            >
              <span class="font-mono text-xs font-bold">30s ↻</span>
            </button>

            <button
              type="button"
              on:click={skipNextChapter}
              title="Next Chapter"
              aria-label="Skip to next chapter"
              class="p-2.5 rounded text-[#94a3b8] hover:text-white transition active:scale-95 focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
            >
              <span class="font-mono text-base font-bold">⏭</span>
            </button>
          </div>

          <!-- Quick Action Pills Row -->
          <div class="pt-3 border-t border-[#1f2433] flex items-center space-x-2 overflow-x-auto no-scrollbar pb-1 text-xs font-mono">
            <button
              type="button"
              on:click={() => (isSyncReadingMode = !isSyncReadingMode)}
              class="px-3 py-1.5 rounded border transition flex items-center space-x-1 whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {isSyncReadingMode ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#090a0f] text-[#38bdf8] border-[#1f2433] hover:bg-[#1f2433]'}"
            >
              <span>⚙ Sync Reading Mode</span>
            </button>

            <button
              type="button"
              on:click={togglePlaybackSpeed}
              class="px-3 py-1.5 rounded border bg-[#090a0f] text-white border-[#1f2433] hover:bg-[#1f2433] transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
            >
              <span>⚡ Speed {playbackRate}x</span>
            </button>

            <button
              type="button"
              on:click={() => (isDialogueEnhanced = !isDialogueEnhanced)}
              class="px-3 py-1.5 rounded border transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {isDialogueEnhanced ? 'bg-[#38bdf8] text-[#090a0f] font-bold border-[#38bdf8]' : 'bg-[#090a0f] text-white border-[#1f2433] hover:bg-[#1f2433]'}"
            >
              <span>🔊 Enhance Dialogue</span>
            </button>
          </div>
        </div>

        <!-- Segmented Navigation Tabs -->
        <div class="border-b border-[#1f2433]" role="tablist" aria-label="Media Player Content Tabs">
          <div class="flex space-x-2 overflow-x-auto pb-1 text-xs font-mono">
            <button
              id="tab-btn-theses"
              type="button"
              role="tab"
              aria-selected={activeTab === 'theses'}
              on:click={() => (activeTab = 'theses')}
              class="pb-2 px-3 border-b-2 font-semibold transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {activeTab === 'theses' ? 'border-[#38bdf8] text-[#38bdf8]' : 'border-transparent text-[#94a3b8] hover:text-white'}"
            >
              Core Theses ({currentEpisode.coreTheses?.length || 0})
            </button>

            <button
              id="tab-btn-chapters"
              type="button"
              role="tab"
              aria-selected={activeTab === 'chapters'}
              on:click={() => (activeTab = 'chapters')}
              class="pb-2 px-3 border-b-2 font-semibold transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {activeTab === 'chapters' ? 'border-[#38bdf8] text-[#38bdf8]' : 'border-transparent text-[#94a3b8] hover:text-white'}"
            >
              Timestamps & Chapters ({currentEpisode.timecodes.length})
            </button>

            <button
              id="tab-btn-transcript"
              type="button"
              role="tab"
              aria-selected={activeTab === 'transcript'}
              on:click={() => (activeTab = 'transcript')}
              class="pb-2 px-3 border-b-2 font-semibold transition whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {activeTab === 'transcript' ? 'border-[#38bdf8] text-[#38bdf8]' : 'border-transparent text-[#94a3b8] hover:text-white'}"
            >
              Full Transcript
            </button>
          </div>
        </div>

        <!-- TAB CONTENT 1: CORE THESES -->
        {#if activeTab === 'theses'}
          <div id="core-theses-section" class="space-y-4">
            <div class="flex items-center justify-between">
              <h2 class="font-bold text-base text-white">Key Critical Theses</h2>
              <span class="text-xs font-mono text-[#38bdf8]">Click Timestamp To Seek</span>
            </div>

            {#if currentEpisode.coreTheses && currentEpisode.coreTheses.length > 0}
              <div class="space-y-3" role="list" aria-label="Episode Core Theses">
                {#each currentEpisode.coreTheses as thesis}
                  {@const isActive = activeThesisId === thesis.id}
                  <article
                    id="thesis-card-{thesis.id}"
                    class="p-4 rounded-lg border transition-all relative overflow-hidden {isActive ? 'bg-[#12151e] border-[#38bdf8] ring-1 ring-[#38bdf8]/30 shadow-lg' : 'bg-[#090a0f] border-[#1f2433] hover:border-[#38bdf8]/50'}"
                  >
                    <div class="absolute left-0 top-0 bottom-0 w-1 {isActive ? 'bg-[#38bdf8]' : 'bg-[#1f2433]'}"></div>

                    <div class="flex items-start justify-between gap-2">
                      <div class="flex items-center space-x-2">
                        <span class="text-[10px] font-mono font-bold uppercase tracking-wider text-[#38bdf8] bg-[#1f2433] px-2 py-0.5 rounded">
                          {thesis.category || 'CENTRAL THESIS'}
                        </span>
                        {#if isActive}
                          <span class="inline-flex items-center px-1.5 py-0.5 rounded text-[9px] font-mono font-bold bg-[#38bdf8] text-[#090a0f] animate-pulse">
                            NOW PLAYING
                          </span>
                        {/if}
                      </div>

                      <button
                        type="button"
                        id="thesis-seek-btn-{thesis.id}"
                        on:click={() => handleThesisClick(thesis)}
                        aria-label="Seek audio to {formatTime(thesis.timestampSeconds)} for {thesis.title}"
                        class="font-mono text-xs font-bold text-[#38bdf8] bg-[#12151e] px-2.5 py-1 rounded border border-[#1f2433] hover:border-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition flex items-center space-x-1"
                      >
                        <span>▶ [{formatTime(thesis.timestampSeconds)}]</span>
                      </button>
                    </div>

                    <blockquote class="mt-3 font-serif text-base italic text-white leading-relaxed">
                      {thesis.quote}
                    </blockquote>

                    <p class="mt-2 text-xs text-[#94a3b8] leading-relaxed">
                      {thesis.description}
                    </p>

                    <div class="mt-3 pt-3 border-t border-[#1f2433] flex items-center justify-between text-xs font-mono">
                      <span class="text-[#64748b]">{thesis.sectionTitle || 'Episode Analysis'}</span>
                      <button
                        type="button"
                        on:click={() => handleThesisClick(thesis)}
                        class="text-[#38bdf8] hover:underline focus:outline-none focus:ring-1 focus:ring-[#38bdf8]"
                      >
                        Seek {formatTime(thesis.timestampSeconds)} →
                      </button>
                    </div>
                  </article>
                {/each}
              </div>
            {/if}
          </div>
        {/if}

        <!-- TAB CONTENT 2: TIMESTAMPS & CHAPTERS -->
        {#if activeTab === 'chapters' || activeTab === 'theses'}
          <div id="timecodes-section" class="space-y-3 pt-2 border-t border-[#1f2433]">
            <div class="flex items-center justify-between text-xs font-mono text-[#94a3b8]">
              <span>TIMECODES & TOPICS ({currentEpisode.timecodes.length})</span>
              <span class="text-[#38bdf8]">CLICK_TIMESTAMP_TO_SEEK</span>
            </div>

            <div id="timecodes-list" class="space-y-2" role="list" aria-label="Episode Timecodes">
              {#each currentEpisode.timecodes as tc}
                {@const isActive = activeTimecodeId === tc.id}
                <button
                  type="button"
                  id="timecode-btn-{tc.id}"
                  on:click={() => handleTimecodeClick(tc)}
                  aria-label="Seek to {tc.title} at {formatTime(tc.timeOffsetSeconds)}"
                  class="timecode-item w-full p-3 rounded-lg border transition-all text-left flex items-start gap-3 focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {isActive ? 'bg-[#12151e] border-[#38bdf8] text-white shadow-sm' : 'bg-[#090a0f] border-[#1f2433] text-[#94a3b8] hover:border-[#1f2433]/80 hover:text-white hover:bg-[#12151e]/60'}"
                >
                  <span class="font-mono text-xs font-bold px-2 py-1 rounded shrink-0 {isActive ? 'bg-[#38bdf8] text-[#090a0f]' : 'bg-[#1f2433] text-[#38bdf8]'}">
                    [{formatTime(tc.timeOffsetSeconds)}]
                  </span>
                  <div class="min-w-0 flex-1">
                    <div class="font-bold text-xs flex items-center justify-between">
                      <span class="truncate text-white">{tc.title}</span>
                      {#if isActive}
                        <span class="text-[10px] font-mono text-[#38bdf8] shrink-0 font-bold ml-2">NOW_PLAYING</span>
                      {/if}
                    </div>
                    {#if tc.description}
                      <p class="text-[11px] text-[#94a3b8] mt-1 line-clamp-1">{tc.description}</p>
                    {/if}
                  </div>
                </button>
              {/each}
            </div>
          </div>
        {/if}

        <!-- TAB CONTENT 3: TRANSCRIPT -->
        {#if activeTab === 'transcript'}
          <div id="transcript-section" class="p-4 rounded-lg bg-[#12151e] border border-[#1f2433] space-y-3 font-serif">
            <div class="flex items-center justify-between font-mono text-xs text-[#38bdf8]">
              <span>SYNCHRONIZED TRANSCRIPT EXCERPT</span>
              <span>{formatTime(currentTime)}</span>
            </div>
            <p class="text-sm text-[#cbd5e1] leading-relaxed">
              <span class="text-white">"...we do not simply read slower for the sake of endurance;</span>
              <mark class="bg-[#38bdf8]/20 text-white px-1 py-0.5 rounded border-b border-[#38bdf8]">
                friction is the neurological tax required to convert external data into internal dialectic structure.
              </mark>
              <span class="text-[#94a3b8]"> Without the resistance of the sentence, memory becomes shallow..."</span>
            </p>
          </div>
        {/if}

        <p id="podcast-description" class="text-xs text-[#94a3b8] leading-relaxed pt-2 border-t border-[#1f2433]">
          {currentEpisode.description}
        </p>
      </div>
    </div>
  </div>
</div>
