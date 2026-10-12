<script lang="ts">
  let streamMode: 'HYPER' | 'FOCUS' = 'HYPER';

  function setMode(mode: 'HYPER' | 'FOCUS') {
    streamMode = mode;
  }
</script>

<div class="w-full max-w-[680px] min-h-screen flex flex-col relative pb-32 bg-surface-container-lowest text-on-surface antialiased selection:bg-primary-container selection:text-on-primary-container overflow-x-hidden">
  <!-- TOP BAR -->
  <header class="sticky top-0 z-50 flex justify-between items-center w-full px-margin h-12 max-w-[680px] mx-auto bg-surface-container-lowest dark:bg-surface-container-lowest border-b border-outline-variant/30 backdrop-blur-md">
    <!-- Leading Icon / Brand Logo -->
    <div class="flex items-center space-x-2">
      <span class="material-symbols-outlined text-primary-container leading-none text-xl select-none" data-icon="bolt">bolt</span>
      <h1 class="font-headline-sm text-headline-sm font-bold tracking-tight text-primary-container dark:text-primary-container select-none">SPARKS</h1>
      <!-- Stream Pulse Telemetry -->
      <span class="inline-flex items-center px-1.5 py-0.5 rounded-full bg-primary-container/10 border border-primary-container/30 text-primary-container font-label-sm text-label-sm ml-1.5 animate-pulse">
        <span class="w-1.5 h-1.5 rounded-full bg-primary-container mr-1"></span>
        42 spk/m
      </span>
    </div>
    <!-- Trailing Action Cluster -->
    <div class="flex items-center space-x-2">
      <!-- Stream Mode Switcher -->
      <div class="flex items-center bg-surface-container-low border border-outline-variant/40 rounded-full p-0.5" role="group" aria-label="Stream Mode">
        <button
          class="px-2 py-0.5 rounded-full font-label-sm text-label-sm focus-visible:ring-2 focus-visible:ring-primary focus:outline-none transition-all {streamMode === 'HYPER' ? 'text-primary-container bg-primary-container/20 font-bold' : 'text-on-surface-variant hover:text-primary'}"
          on:click={() => setMode('HYPER')}
          aria-pressed={streamMode === 'HYPER'}
        >
          HYPER
        </button>
        <button
          class="px-2 py-0.5 rounded-full font-label-sm text-label-sm focus-visible:ring-2 focus-visible:ring-primary focus:outline-none transition-all {streamMode === 'FOCUS' ? 'text-primary-container bg-primary-container/20 font-bold' : 'text-on-surface-variant hover:text-primary'}"
          on:click={() => setMode('FOCUS')}
          aria-pressed={streamMode === 'FOCUS'}
        >
          FOCUS
        </button>
      </div>
      <!-- Search Action -->
      <button aria-label="Search Sparks" class="w-8 h-8 rounded-full flex items-center justify-center text-on-surface-variant hover:bg-surface-container-high hover:text-primary active:scale-95 transition-transform duration-100 focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
        <span class="material-symbols-outlined text-lg leading-none" data-icon="search">search</span>
      </button>
      <!-- Insights Trailing Icon Anchor from Shared Components -->
      <button aria-label="Stream Insights" class="w-8 h-8 rounded-full flex items-center justify-center text-primary-container hover:bg-surface-container-high hover:text-primary active:scale-95 transition-transform duration-100 focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
        <span class="material-symbols-outlined text-lg leading-none" data-icon="insights">insights</span>
      </button>
    </div>
  </header>

  <!-- Velocity Status Ribbon & Micro-Counter -->
  <section class="w-full px-margin pt-2.5 pb-2 flex items-center justify-between border-b border-outline-variant/20 bg-surface-container-lowest/80 text-on-surface-variant">
    <div class="flex items-center space-x-2">
      <span class="material-symbols-outlined text-tertiary text-sm" data-icon="speed">speed</span>
      <span class="font-label-sm text-label-sm text-on-surface-variant">THROUGHPUT: <span class="font-bold text-tertiary">142 WPM</span></span>
    </div>
    <div class="flex items-center space-x-1.5 font-label-sm text-label-sm text-on-surface-variant bg-surface-container-high/60 px-2 py-0.5 rounded border border-outline-variant/30">
      <span class="material-symbols-outlined text-primary text-xs" data-icon="electric_bolt" data-weight="fill" style="font-variation-settings: 'FILL' 1;">electric_bolt</span>
      <span class="text-on-surface font-semibold">84 sparks</span>
      <span class="text-outline">consumed today</span>
    </div>
  </section>

  <!-- Fast Horizontal Filter Rail (Categorical Stream Rails) -->
  <nav aria-label="Topic Streams" class="w-full px-margin py-2.5 overflow-x-auto no-scrollbar flex items-center space-x-2 border-b border-outline-variant/20 bg-surface-container-lowest">
    <!-- Active Pill -->
    <button class="flex-shrink-0 px-3 py-1 rounded-full bg-primary-container/15 border border-primary-container text-primary font-label-sm text-label-sm flex items-center space-x-1 amber-glow transition-all focus-visible:ring-2 focus-visible:ring-primary focus:outline-none" aria-pressed="true">
      <span class="material-symbols-outlined text-xs text-primary-container" data-icon="bolt">bolt</span>
      <span>All Sparks</span>
    </button>
    <!-- Inactive Category Pills -->
    <button class="flex-shrink-0 px-3 py-1 rounded-full bg-surface-container-low border border-outline-variant/30 text-on-surface-variant font-label-sm text-label-sm hover:border-primary-container/60 hover:text-primary transition-all focus-visible:ring-2 focus-visible:ring-primary focus:outline-none" aria-pressed="false">
      #mental-models
    </button>
    <button class="flex-shrink-0 px-3 py-1 rounded-full bg-surface-container-low border border-outline-variant/30 text-on-surface-variant font-label-sm text-label-sm hover:border-primary-container/60 hover:text-primary transition-all focus-visible:ring-2 focus-visible:ring-primary focus:outline-none" aria-pressed="false">
      #system-design
    </button>
    <button class="flex-shrink-0 px-3 py-1 rounded-full bg-surface-container-low border border-outline-variant/30 text-on-surface-variant font-label-sm text-label-sm hover:border-primary-container/60 hover:text-primary transition-all focus-visible:ring-2 focus-visible:ring-primary focus:outline-none" aria-pressed="false">
      #ai-futures
    </button>
    <button class="flex-shrink-0 px-3 py-1 rounded-full bg-surface-container-low border border-outline-variant/30 text-on-surface-variant font-label-sm text-label-sm hover:border-primary-container/60 hover:text-primary transition-all focus-visible:ring-2 focus-visible:ring-primary focus:outline-none" aria-pressed="false">
      #philosophy
    </button>
    <button class="flex-shrink-0 px-3 py-1 rounded-full bg-surface-container-low border border-outline-variant/30 text-on-surface-variant font-label-sm text-label-sm hover:border-primary-container/60 hover:text-primary transition-all focus-visible:ring-2 focus-visible:ring-primary focus:outline-none" aria-pressed="false">
      #product-craft
    </button>
    <button class="flex-shrink-0 px-3 py-1 rounded-full bg-surface-container-low border border-outline-variant/30 text-on-surface-variant font-label-sm text-label-sm hover:border-primary-container/60 hover:text-primary transition-all focus-visible:ring-2 focus-visible:ring-primary focus:outline-none" aria-pressed="false">
      #heuristics
    </button>
  </nav>

  <!-- FEED MAIN CONTENT AREA WITH SPEED SCRUBBER RAIL -->
  <div class="relative w-full px-margin mt-3 flex">
    <!-- Main Feed Stream: Cards Stack -->
    <main class="w-full flex flex-col space-y-3 pr-2.5" id="sparkFeed">
      <!-- CARD 1: Core Atomic Insight -->
      <article class="bg-[#14171F] border border-white/10 rounded-xl p-space-md hover:border-primary-container/40 transition-all duration-150 relative overflow-hidden group">
        <!-- Lateral speed indicator hairline -->
        <div class="absolute left-0 top-0 bottom-0 w-[2px] bg-primary-container opacity-80"></div>
        <!-- Header Row -->
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-2">
            <span class="w-6 h-6 rounded-full bg-surface-container-high border border-outline-variant/40 flex items-center justify-center font-label-sm text-label-sm font-bold text-primary">A</span>
            <span class="font-body-sm text-body-sm font-semibold text-on-surface">@alexp</span>
            <span class="w-1 h-1 rounded-full bg-outline-variant"></span>
            <span class="font-label-sm text-label-sm text-outline">14m ago</span>
          </div>
          <div class="flex items-center space-x-1.5 font-label-sm text-label-sm text-tertiary bg-tertiary/10 border border-tertiary/20 px-2 py-0.5 rounded-full">
            <span>Read: 3s</span>
          </div>
        </div>
        <!-- Body -->
        <p class="font-body-lg text-body-lg text-on-surface leading-snug tracking-tight mb-3">
          "Most software complexity isn't accidental or essential; it's social structure fossilized in code."
        </p>
        <!-- Footer Metadata & Reactions -->
        <div class="flex items-center justify-between pt-2 border-t border-white/5">
          <div class="flex items-center space-x-2">
            <span class="font-label-sm text-label-sm text-primary px-2 py-0.5 bg-primary/10 rounded border border-primary/20">#architecture</span>
            <span class="font-label-sm text-label-sm text-on-surface-variant flex items-center">
              <span class="material-symbols-outlined text-xs text-primary-container mr-0.5" data-icon="bolt">bolt</span>
              1.4k sparks
            </span>
          </div>
          <div class="flex items-center space-x-2">
            <button aria-label="Spark this" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary-container hover:border-primary-container/50 active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="local_fire_department">local_fire_department</span>
            </button>
            <button aria-label="Bookmark" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="bookmark">bookmark</span>
            </button>
            <button aria-label="Share" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="ios_share">ios_share</span>
            </button>
          </div>
        </div>
      </article>

      <!-- CARD 2: Micro-Framework Atomic Steps -->
      <article class="bg-[#14171F] border border-white/10 rounded-xl p-space-md hover:border-primary-container/40 transition-all duration-150 relative">
        <!-- Header -->
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-2">
            <span class="w-6 h-6 rounded-full bg-secondary-container/40 border border-secondary/30 flex items-center justify-center font-label-sm text-label-sm font-bold text-secondary">R</span>
            <span class="font-body-sm text-body-sm font-semibold text-on-surface">@rachel.v</span>
            <span class="w-1 h-1 rounded-full bg-outline-variant"></span>
            <span class="font-label-sm text-label-sm text-outline">48m ago</span>
          </div>
          <span class="font-label-sm text-label-sm text-outline-variant px-1.5 py-0.5 bg-surface-container rounded font-mono">FRAMEWORK</span>
        </div>
        <!-- Title / Premise -->
        <h2 class="font-headline-sm text-headline-sm text-on-surface font-semibold mb-2">Atomic Feedback Loop:</h2>
        <!-- Visual Flow Diagram / Pills -->
        <div class="grid grid-cols-1 gap-1.5 mb-3 bg-surface-container-lowest/80 p-2.5 rounded-lg border border-outline-variant/30">
          <div class="flex items-center space-x-2 text-on-surface-variant font-label-md text-label-md">
            <span class="w-4 h-4 rounded-full bg-white/10 flex items-center justify-center text-[10px] text-on-surface">1</span>
            <span>Trigger</span>
          </div>
          <div class="flex items-center justify-between text-error font-label-md text-label-md line-through opacity-80 pl-6">
            <span>2. Frictionless action</span>
            <span class="text-[10px] uppercase font-bold text-error bg-error/10 px-1 rounded">Eliminate</span>
          </div>
          <div class="flex items-center space-x-2 text-primary-container font-label-md text-label-md">
            <span class="w-4 h-4 rounded-full bg-primary-container/20 flex items-center justify-center text-[10px] text-primary-container font-bold">3</span>
            <span class="font-bold">Instant dopamine marker.</span>
          </div>
        </div>
        <!-- Bottom Action / Insight Punchline -->
        <p class="font-body-md text-body-md text-primary font-medium mb-3">
          "Remove step 2. Collapse decision latencies into zero."
        </p>
        <!-- Footer -->
        <div class="flex items-center justify-between pt-2 border-t border-white/5">
          <div class="flex items-center space-x-2">
            <span class="font-label-sm text-label-sm text-secondary px-2 py-0.5 bg-secondary-container/20 rounded border border-secondary/30">#psychology</span>
            <span class="font-label-sm text-label-sm text-on-surface-variant flex items-center">
              <span class="material-symbols-outlined text-xs text-primary-container mr-0.5" data-icon="bolt">bolt</span>
              890 sparks
            </span>
          </div>
          <div class="flex items-center space-x-2">
            <button aria-label="Spark this" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary-container active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="local_fire_department">local_fire_department</span>
            </button>
            <button aria-label="Bookmark" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="bookmark">bookmark</span>
            </button>
            <button aria-label="Share" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="ios_share">ios_share</span>
            </button>
          </div>
        </div>
      </article>

      <!-- CARD 3: Provocative Quote Card with Accent Border Glow -->
      <article class="bg-[#14171F] border-l-2 border-l-primary-container border-y border-r border-white/10 rounded-xl p-space-md amber-glow transition-all duration-150">
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-2">
            <span class="w-6 h-6 rounded-full bg-surface-container-high border border-outline-variant/40 flex items-center justify-center font-label-sm text-label-sm font-bold text-tertiary">N</span>
            <span class="font-body-sm text-body-sm font-semibold text-on-surface">@neuro_dr</span>
            <span class="w-1 h-1 rounded-full bg-outline-variant"></span>
            <span class="font-label-sm text-label-sm text-outline">1h ago</span>
          </div>
          <span class="inline-flex items-center text-primary-container font-label-sm text-label-sm">
            <span class="material-symbols-outlined text-xs mr-0.5" data-icon="psychology">psychology</span>
            HIGH RESONANCE
          </span>
        </div>
        <!-- Provocative Thought Body -->
        <blockquote class="font-headline-sm text-headline-sm text-on-surface font-semibold italic tracking-tight mb-3 pl-2 border-l-2 border-primary-container/40">
          "Speed of thought is bounded by the speed of retrieval, not processing power."
        </blockquote>
        <!-- Footer -->
        <div class="flex items-center justify-between pt-2 border-t border-white/5">
          <div class="flex items-center space-x-2">
            <span class="font-label-sm text-label-sm text-tertiary px-2 py-0.5 bg-tertiary/10 rounded border border-tertiary/20">#cognition</span>
            <span class="font-label-sm text-label-sm text-on-surface-variant flex items-center">
              <span class="material-symbols-outlined text-xs text-primary-container mr-0.5" data-icon="bolt">bolt</span>
              2.1k sparks
            </span>
          </div>
          <div class="flex items-center space-x-2">
            <button aria-label="Sparked" class="w-7 h-7 rounded-full bg-primary-container/20 border border-primary-container/40 flex items-center justify-center text-primary-container active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="local_fire_department" data-weight="fill" style="font-variation-settings: 'FILL' 1;">local_fire_department</span>
            </button>
            <button aria-label="Bookmark" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="bookmark">bookmark</span>
            </button>
            <button aria-label="Share" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="ios_share">ios_share</span>
            </button>
          </div>
        </div>
      </article>

      <!-- CARD 4: Audio / Voice Quantized Spark Pill Badge & Waveform -->
      <article class="bg-[#14171F] border border-white/10 rounded-xl p-space-md hover:border-tertiary/40 transition-all duration-150">
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-2">
            <span class="w-6 h-6 rounded-full bg-surface-container-high border border-outline-variant/40 flex items-center justify-center font-label-sm text-label-sm font-bold text-on-surface">D</span>
            <span class="font-body-sm text-body-sm font-semibold text-on-surface">@dan_k</span>
            <span class="w-1 h-1 rounded-full bg-outline-variant"></span>
            <span class="font-label-sm text-label-sm text-outline">2h ago</span>
          </div>
          <!-- Audio Badge -->
          <span class="inline-flex items-center px-2 py-0.5 rounded-full bg-tertiary/10 border border-tertiary/30 text-tertiary font-label-sm text-label-sm">
            <span class="material-symbols-outlined text-xs mr-1" data-icon="mic">mic</span>
            12s voice snippet
          </span>
        </div>
        <!-- Micro Voice Player Bar -->
        <div class="flex items-center space-x-3 bg-surface-container-lowest p-2 rounded-lg border border-outline-variant/20 mb-3">
          <button aria-label="Play spark audio" class="w-8 h-8 rounded-full bg-tertiary text-on-tertiary flex items-center justify-center shadow hover:opacity-90 active:scale-90 transition-transform flex-shrink-0 focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
            <span class="material-symbols-outlined text-sm leading-none" data-icon="play_arrow" data-weight="fill" style="font-variation-settings: 'FILL' 1;">play_arrow</span>
          </button>
          <!-- Simulated Kinetic Waveform Bars -->
          <div class="flex items-center space-x-1 flex-1 h-6">
            <span class="w-1 h-2 bg-tertiary/40 rounded-full"></span>
            <span class="w-1 h-4 bg-tertiary/70 rounded-full"></span>
            <span class="w-1 h-6 bg-tertiary rounded-full"></span>
            <span class="w-1 h-3 bg-tertiary/80 rounded-full"></span>
            <span class="w-1 h-5 bg-tertiary rounded-full"></span>
            <span class="w-1 h-4 bg-tertiary/60 rounded-full"></span>
            <span class="w-1 h-2 bg-tertiary/40 rounded-full"></span>
            <span class="w-1 h-5 bg-tertiary/90 rounded-full"></span>
            <span class="w-1 h-3 bg-tertiary/50 rounded-full"></span>
            <span class="w-1 h-6 bg-tertiary rounded-full"></span>
            <span class="w-1 h-2 bg-tertiary/30 rounded-full"></span>
            <span class="w-1 h-4 bg-tertiary/60 rounded-full"></span>
            <span class="w-1 h-1 bg-tertiary/20 rounded-full"></span>
          </div>
          <span class="font-label-sm text-label-sm text-on-surface-variant font-mono">0:12</span>
        </div>
        <!-- Transcript Body -->
        <p class="font-body-md text-body-md text-on-surface tracking-tight mb-3">
          <span class="text-tertiary font-label-sm text-label-sm mr-1">[TRANSCRIPT]</span>
          "The best UX is an invisible state transition."
        </p>
        <!-- Footer -->
        <div class="flex items-center justify-between pt-2 border-t border-white/5">
          <div class="flex items-center space-x-2">
            <span class="font-label-sm text-label-sm text-primary-container px-2 py-0.5 bg-primary-container/10 rounded border border-primary-container/20">#ux-heuristics</span>
            <span class="font-label-sm text-label-sm text-on-surface-variant flex items-center">
              <span class="material-symbols-outlined text-xs text-primary-container mr-0.5" data-icon="bolt">bolt</span>
              640 sparks
            </span>
          </div>
          <div class="flex items-center space-x-2">
            <button aria-label="Spark this" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary-container active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="local_fire_department">local_fire_department</span>
            </button>
            <button aria-label="Bookmark" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="bookmark">bookmark</span>
            </button>
            <button aria-label="Share" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="ios_share">ios_share</span>
            </button>
          </div>
        </div>
      </article>

      <!-- CARD 5: Contrast / Theorem Card -->
      <article class="bg-[#14171F] border border-white/10 rounded-xl p-space-md hover:border-primary-container/40 transition-all duration-150">
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-2">
            <span class="w-6 h-6 rounded-full bg-surface-container-high border border-outline-variant/40 flex items-center justify-center font-label-sm text-label-sm font-bold text-primary">S</span>
            <span class="font-body-sm text-body-sm font-semibold text-on-surface">@strat_core</span>
            <span class="w-1 h-1 rounded-full bg-outline-variant"></span>
            <span class="font-label-sm text-label-sm text-outline">3h ago</span>
          </div>
          <span class="font-label-sm text-label-sm text-primary-container font-mono bg-primary-container/10 px-1.5 py-0.5 rounded border border-primary-container/20">THEOREM</span>
        </div>
        <!-- Dual Contrast Box -->
        <div class="grid grid-cols-2 gap-2 mb-3">
          <div class="p-2.5 rounded-lg bg-surface-container-lowest/80 border border-outline-variant/30 flex flex-col justify-between">
            <span class="font-label-sm text-label-sm text-outline mb-1">LINEAR THINKING</span>
            <p class="font-body-sm text-body-sm text-on-surface-variant font-medium">Rewards consensus and known metrics.</p>
          </div>
          <div class="p-2.5 rounded-lg bg-primary-container/10 border border-primary-container/40 flex flex-col justify-between">
            <span class="font-label-sm text-label-sm text-primary-container font-bold mb-1">NON-LINEAR BETS</span>
            <p class="font-body-sm text-body-sm text-primary font-semibold">Rewards pure asymmetric conviction.</p>
          </div>
        </div>
        <!-- Footer -->
        <div class="flex items-center justify-between pt-2 border-t border-white/5">
          <div class="flex items-center space-x-2">
            <span class="font-label-sm text-label-sm text-primary px-2 py-0.5 bg-primary/10 rounded border border-primary/20">#strategy</span>
            <span class="font-label-sm text-label-sm text-on-surface-variant flex items-center">
              <span class="material-symbols-outlined text-xs text-primary-container mr-0.5" data-icon="bolt">bolt</span>
              3.4k sparks
            </span>
          </div>
          <div class="flex items-center space-x-2">
            <button aria-label="Spark this" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary-container active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="local_fire_department">local_fire_department</span>
            </button>
            <button aria-label="Bookmark" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="bookmark">bookmark</span>
            </button>
            <button aria-label="Share" class="w-7 h-7 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-on-surface-variant hover:text-primary active:scale-90 transition-transform focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
              <span class="material-symbols-outlined text-sm leading-none" data-icon="ios_share">ios_share</span>
            </button>
          </div>
        </div>
      </article>
    </main>

    <!-- Tactical Right-Rail Speed Scrubber Dots -->
    <aside aria-label="Stream Velocity Timeline" class="w-3 flex-shrink-0 flex flex-col items-center justify-start space-y-4 pt-4 select-none opacity-60 hover:opacity-100 transition-opacity">
      <div class="w-1.5 h-4 rounded-full bg-primary-container animate-pulse" title="Current stream position"></div>
      <div class="w-1 h-1 rounded-full bg-outline-variant"></div>
      <div class="w-1 h-1 rounded-full bg-outline-variant"></div>
      <div class="w-1 h-1 rounded-full bg-tertiary"></div>
      <div class="w-1 h-1 rounded-full bg-outline-variant"></div>
      <div class="w-1 h-1 rounded-full bg-outline-variant"></div>
      <div class="w-1 h-1 rounded-full bg-outline-variant"></div>
      <div class="w-1 h-2 rounded-full bg-outline-variant/60"></div>
    </aside>
  </div>

  <!-- QUICK-SPARK CAPTURE BAR (Floating Composer Bar above Navigation) -->
  <div class="fixed bottom-16 left-1/2 -translate-x-1/2 w-full max-w-[680px] px-margin z-40 pointer-events-none">
    <div class="pointer-events-auto bg-[#1D222D]/95 backdrop-blur-xl border border-white/15 rounded-full p-1.5 flex items-center shadow-2xl space-x-2">
      <button aria-label="Audio spark record" class="w-8 h-8 rounded-full bg-surface-container-high/80 text-on-surface-variant hover:text-primary flex items-center justify-center flex-shrink-0 transition-colors focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
        <span class="material-symbols-outlined text-base leading-none" data-icon="mic">mic</span>
      </button>
      <input
        class="bg-transparent border-0 text-on-surface placeholder:text-outline text-body-sm font-body-sm focus:ring-0 flex-1 px-1 py-1 focus:outline-none"
        maxlength="180"
        placeholder="Type a spark thought... (max 180 chars)"
        type="text"
        aria-label="Spark Composer"
      />
      <div class="font-label-sm text-label-sm text-outline-variant pr-1 hidden sm:block">180</div>
      <!-- High-contrast kinetic amber send bolt trigger -->
      <button aria-label="Dispatch Spark" class="w-8 h-8 rounded-full bg-primary-container text-surface-container-lowest flex items-center justify-center font-bold amber-glow hover:scale-105 active:scale-95 transition-transform flex-shrink-0 focus-visible:ring-2 focus-visible:ring-primary focus:outline-none">
        <span class="material-symbols-outlined text-base leading-none font-bold" data-icon="bolt">bolt</span>
      </button>
    </div>
  </div>
</div>
