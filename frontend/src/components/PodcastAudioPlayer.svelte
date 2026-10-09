<script>
  export let episode = {
    id: 'pod-1',
    slug: 'ep-01-systems-reality',
    title: 'Системы и реальность: Выпуск 1',
    thesis: 'Дискуссия о системном мышлении, поиске узких мест и техническом брутализме.',
    audioUrl: 'https://dmitryefremov.com/podcasts/ep-01.mp3',
    durationSeconds: 1800,
    timecodes: [
      { id: 1, timeOffsetSeconds: 0, title: 'Введение и концепция системы', description: 'Основные тезисы и манифест.' },
      { id: 2, timeOffsetSeconds: 180, title: 'Теория ограничений и узкие места', description: 'Поиск главных блокеров в архитектуре.' },
      { id: 3, timeOffsetSeconds: 540, title: 'Деонтическая чистота и автономность', description: 'Как строить саморегулируемые процессы.' },
      { id: 4, timeOffsetSeconds: 1100, title: 'Практика и выводы', description: 'Заключительные мысли и вопросы.' }
    ]
  };

  let audioEl;
  let isPlaying = false;
  let currentTime = 0;
  let duration = episode.durationSeconds || 1800;

  export let waveData = [
    25, 40, 60, 30, 80, 95, 70, 50, 85, 60,
    40, 75, 90, 55, 35, 65, 80, 45, 90, 100,
    70, 40, 60, 85, 50, 30, 75, 90, 65, 45,
    80, 60, 35, 70, 95, 50, 40, 85, 75, 60
  ];

  function togglePlay() {
    if (!audioEl) {
      isPlaying = !isPlaying;
      return;
    }
    if (isPlaying) {
      audioEl.pause();
    } else {
      audioEl.play().then(() => {
        isPlaying = true;
      }).catch((err) => {
        // Fallback state update for synthetic/unloaded media
        isPlaying = true;
      });
    }
  }

  export function seekTo(seconds) {
    const target = Math.max(0, Math.min(seconds, duration));
    currentTime = target;
    if (audioEl) {
      audioEl.currentTime = target;
    }
  }

  function handleWaveClick(index) {
    const targetTime = Math.round((index / waveData.length) * duration);
    seekTo(targetTime);
  }

  function formatTime(secs) {
    if (isNaN(secs) || secs == null) return '00:00';
    const m = Math.floor(secs / 60);
    const s = Math.floor(secs % 60);
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  }

  function handleTimeUpdate() {
    if (audioEl) {
      currentTime = audioEl.currentTime;
    }
  }

  function handleLoadedMetadata() {
    if (audioEl && audioEl.duration && !isNaN(audioEl.duration) && audioEl.duration !== Infinity) {
      duration = audioEl.duration;
    }
  }

  function handlePlay() { isPlaying = true; }
  function handlePause() { isPlaying = false; }
  function handleEnded() { isPlaying = false; currentTime = 0; }

  $: progressPercent = duration > 0 ? (currentTime / duration) * 100 : 0;
</script>

<div class="podcast-player-container" id="podcast-player-main" data-testid="podcast-player">
  <audio
    bind:this={audioEl}
    src={episode.audioUrl}
    preload="metadata"
    on:timeupdate={handleTimeUpdate}
    on:loadedmetadata={handleLoadedMetadata}
    on:play={handlePlay}
    on:pause={handlePause}
    on:ended={handleEnded}
  ></audio>

  <!-- Episode Header -->
  <header class="player-header" id="header">
    <div class="header-badge">
      <span class="sys-tag">[EP_01]</span>
      <span class="status-indicator" class:active={isPlaying}>
        {isPlaying ? 'PLAYING' : 'READY'}
      </span>
    </div>
    <h1 class="episode-title">{episode.title}</h1>
    {#if episode.thesis}
      <p class="episode-thesis">{episode.thesis}</p>
    {/if}
  </header>

  <!-- Player Controls & Waveform Section -->
  <section class="controls-waveform-section" id="player-controls">
    <div class="primary-controls">
      <button
        class="play-pause-btn"
        on:click={togglePlay}
        aria-label={isPlaying ? "Pause episode" : "Play episode"}
        data-testid="play-pause-button"
      >
        {#if isPlaying}
          <svg class="icon" viewBox="0 0 24 24" fill="currentColor">
            <rect x="6" y="4" width="4" height="16" rx="1" />
            <rect x="14" y="4" width="4" height="16" rx="1" />
          </svg>
        {:else}
          <svg class="icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M8 5v14l11-7z" />
          </svg>
        {/if}
      </button>

      <div class="time-display" data-testid="time-display">
        <span class="current-time">{formatTime(currentTime)}</span>
        <span class="divider">/</span>
        <span class="total-duration">{formatTime(duration)}</span>
      </div>
    </div>

    <!-- Waveform Visualization -->
    <div
      class="waveform-wrapper"
      id="waveform"
      data-testid="waveform-container"
      role="region"
      aria-label="Audio Waveform Visualization"
    >
      <div class="waveform-bars">
        {#each waveData as height, index}
          {@const barPositionPercent = (index / waveData.length) * 100}
          {@const isPassed = barPositionPercent <= progressPercent}
          <button
            type="button"
            class="wave-bar"
            class:passed={isPassed}
            style="height: {height}%;"
            on:click={() => handleWaveClick(index)}
            aria-label="Seek to {formatTime((index / waveData.length) * duration)}"
            data-testid="wave-bar-{index}"
          ></button>
        {/each}
      </div>
      <div class="progress-bar-track">
        <div class="progress-bar-fill" style="width: {progressPercent}%;"></div>
      </div>
    </div>
  </section>

  <!-- Timecodes List -->
  {#if episode.timecodes && episode.timecodes.length > 0}
    <section class="timecodes-section" id="timecodes-list" data-testid="timecodes-section">
      <div class="section-title">
        <span class="sys-icon">►</span>
        <h2>TIMECODES & TOPICS</h2>
      </div>
      <div class="timecodes-grid">
        {#each episode.timecodes as tc, i}
          {@const isActive = currentTime >= tc.timeOffsetSeconds && (i === episode.timecodes.length - 1 || currentTime < episode.timecodes[i + 1].timeOffsetSeconds)}
          <button
            type="button"
            class="timecode-card"
            class:active={isActive}
            on:click={() => seekTo(tc.timeOffsetSeconds)}
            data-testid="timecode-item-{tc.id}"
            data-timestamp={tc.timeOffsetSeconds}
          >
            <div class="tc-header">
              <span class="tc-time">[{formatTime(tc.timeOffsetSeconds)}]</span>
              <span class="tc-title">{tc.title}</span>
            </div>
            {#if tc.description}
              <p class="tc-desc">{tc.description}</p>
            {/if}
          </button>
        {/each}
      </div>
    </section>
  {/if}
</div>

<style>
  :global(:root) {
    --bg-void: #090a0f;
    --bg-card: #12151e;
    --bg-card-hover: #1b202e;
    --border-color: #2a2e3d;
    --border-highlight: #3b82f6;
    --text-primary: #f8fafc;
    --text-secondary: #94a3b8;
    --text-muted: #64748b;
    --accent-blue: #3b82f6;
    --accent-glow: rgba(59, 130, 246, 0.2);
    --font-mono: 'JetBrains Mono', ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
    --font-sans: 'Inter', system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
  }

  .podcast-player-container {
    background-color: var(--bg-void);
    color: var(--text-primary);
    font-family: var(--font-sans);
    padding: 2rem;
    border-radius: 8px;
    border: 1px solid var(--border-color);
    max-width: 960px;
    margin: 0 auto;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  }

  .player-header {
    margin-bottom: 1.5rem;
  }

  .header-badge {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-family: var(--font-mono);
    font-size: 0.8125rem;
    margin-bottom: 0.5rem;
  }

  .sys-tag {
    color: var(--accent-blue);
    font-weight: 600;
  }

  .status-indicator {
    padding: 0.15rem 0.5rem;
    border-radius: 4px;
    background-color: #1e293b;
    color: var(--text-muted);
    font-size: 0.75rem;
    letter-spacing: 0.05em;
  }

  .status-indicator.active {
    background-color: rgba(59, 130, 246, 0.2);
    color: var(--accent-blue);
    border: 1px solid var(--accent-blue);
  }

  .episode-title {
    font-size: 1.75rem;
    font-weight: 700;
    line-height: 1.25;
    margin: 0 0 0.5rem 0;
    color: var(--text-primary);
    letter-spacing: -0.02em;
  }

  .episode-thesis {
    font-size: 0.95rem;
    color: var(--text-secondary);
    line-height: 1.5;
    margin: 0;
  }

  .controls-waveform-section {
    background-color: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: 6px;
    padding: 1.25rem;
    margin-bottom: 2rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .primary-controls {
    display: flex;
    align-items: center;
    gap: 1.25rem;
  }

  .play-pause-btn {
    background-color: var(--accent-blue);
    color: #ffffff;
    border: none;
    border-radius: 50%;
    width: 3rem;
    height: 3rem;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: transform 0.15s ease, background-color 0.15s ease;
    flex-shrink: 0;
  }

  .play-pause-btn:hover {
    transform: scale(1.05);
    background-color: #2563eb;
  }

  .play-pause-btn .icon {
    width: 1.5rem;
    height: 1.5rem;
  }

  .time-display {
    font-family: var(--font-mono);
    font-size: 0.95rem;
    display: flex;
    gap: 0.35rem;
    color: var(--text-secondary);
  }

  .current-time {
    color: var(--text-primary);
    font-weight: 600;
  }

  .waveform-wrapper {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    cursor: pointer;
  }

  .waveform-bars {
    display: flex;
    align-items: flex-end;
    gap: 3px;
    height: 60px;
    padding: 4px 0;
  }

  .wave-bar {
    flex: 1;
    background-color: #262c3d;
    border: none;
    padding: 0;
    margin: 0;
    border-radius: 2px;
    cursor: pointer;
    transition: background-color 0.1s ease, transform 0.1s ease;
    min-width: 2px;
  }

  .wave-bar:hover {
    background-color: #475569;
  }

  .wave-bar.passed {
    background-color: var(--accent-blue);
  }

  .progress-bar-track {
    width: 100%;
    height: 4px;
    background-color: #1e293b;
    border-radius: 2px;
    overflow: hidden;
  }

  .progress-bar-fill {
    height: 100%;
    background-color: var(--accent-blue);
    transition: width 0.1s linear;
  }

  .timecodes-section {
    border-top: 1px solid var(--border-color);
    padding-top: 1.5rem;
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-family: var(--font-mono);
    font-size: 0.875rem;
    color: var(--accent-blue);
    margin-bottom: 1rem;
  }

  .section-title h2 {
    font-size: 0.875rem;
    font-weight: 600;
    margin: 0;
    color: var(--text-primary);
    letter-spacing: 0.05em;
  }

  .timecodes-grid {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .timecode-card {
    background-color: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: 6px;
    padding: 0.875rem 1rem;
    text-align: left;
    color: var(--text-primary);
    cursor: pointer;
    transition: background-color 0.15s ease, border-color 0.15s ease;
    width: 100%;
  }

  .timecode-card:hover {
    background-color: var(--bg-card-hover);
    border-color: var(--text-muted);
  }

  .timecode-card.active {
    border-color: var(--accent-blue);
    background-color: rgba(59, 130, 246, 0.08);
  }

  .tc-header {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-size: 0.95rem;
    margin-bottom: 0.25rem;
  }

  .tc-time {
    font-family: var(--font-mono);
    color: var(--accent-blue);
    font-weight: 600;
    font-size: 0.875rem;
    flex-shrink: 0;
  }

  .tc-title {
    font-weight: 600;
  }

  .tc-desc {
    font-size: 0.85rem;
    color: var(--text-secondary);
    margin: 0;
    line-height: 1.4;
    padding-left: 4.25rem;
  }

  @media (max-width: 600px) {
    .podcast-player-container {
      padding: 1rem;
    }
    .episode-title {
      font-size: 1.35rem;
    }
    .waveform-bars {
      height: 44px;
      gap: 2px;
    }
    .tc-desc {
      padding-left: 0;
      margin-top: 0.25rem;
    }
  }
</style>
