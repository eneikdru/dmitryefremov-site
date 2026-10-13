<script lang="ts">
  import { onMount } from 'svelte';

  export let theme: 'dark' | 'light' = 'dark';
  export let onToggleImprint: () => void = () => {};
  export let onToggleCmp: () => void = () => {};
  export let onTabSelect: (tab: string) => void = () => {};

  let scrolled: number = 34;
  let harmonicHz: number = 88.4;
  let spatialCompression: number = 3.4;
  let inspectExpanded: boolean = false;
  let selectedBranch: 'CH.02A' | 'CH.02B' = 'CH.02A';
  let activePhaseName: string = 'THE SYNTHESIS HORIZON';

  function handleScroll() {
    if (typeof window === 'undefined') return;
    const winScroll = document.body.scrollTop || document.documentElement.scrollTop;
    const height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
    if (height > 0) {
      scrolled = Math.min(100, Math.max(0, Math.round((winScroll / height) * 100)));
    }
    if (scrolled < 25) {
      activePhaseName = 'ORIGIN MATRIX';
    } else if (scrolled < 55) {
      activePhaseName = 'THE SYNTHESIS HORIZON';
    } else if (scrolled < 80) {
      activePhaseName = 'MANIFESTO & SCAFFOLD';
    } else {
      activePhaseName = 'CONVERGENCE PATHWAYS';
    }
  }

  onMount(() => {
    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  });

  function toggleInspect() {
    inspectExpanded = !inspectExpanded;
  }
</script>

<div
  id="scrollytelling-root"
  class="min-h-screen transition-colors duration-200 selection:bg-[#d1f029] selection:text-[#0d0e11] {theme === 'dark' ? 'bg-[#121316] text-[#e3e2e6]' : 'bg-[#f8fafc] text-[#0f172a]'}"
>
  <!-- Top App Bar -->
  <header
    id="header"
    class="fixed top-0 left-0 w-full z-30 flex justify-between items-center h-12 px-5 border-b backdrop-blur-md transition-colors {theme === 'dark' ? 'border-[#454934]/40 bg-[#0d0e11]/85 text-[#e3e2e6]' : 'border-[#cbd5e1] bg-white/85 text-[#0f172a]'}"
  >
    <div class="flex items-center gap-2">
      <button
        type="button"
        aria-label="Audio telemetry toggle"
        class="flex items-center justify-center p-1 rounded transition-opacity active:opacity-75 text-[#d1f029]"
      >
        <span class="font-mono text-sm font-bold">∿</span>
      </button>
      <span class="font-mono text-xs tracking-widest uppercase font-bold text-[#d1f029]">
        CH.01 // ORIGIN MATRIX
      </span>
    </div>

    <div class="flex items-center gap-4">
      <div class="flex items-center gap-1 font-mono text-xs text-[#c6c9ad]">
        <span>SCRUB</span>
        <span id="scrub-metric" class="text-[#d1f029] font-bold">{scrolled}%</span>
      </div>
      <button
        type="button"
        aria-label="Open menu drawer"
        on:click={() => onTabSelect('podcast')}
        class="flex items-center justify-center p-1 rounded transition-opacity active:opacity-75 text-[#d1f029]"
      >
        <span class="font-mono text-xs font-bold uppercase">MENU ☰</span>
      </button>
    </div>

    <!-- Active progress edge tracker -->
    <div
      id="progress-edge-bar"
      class="absolute bottom-0 left-0 h-[2px] bg-[#d1f029] transition-all duration-150"
      style="width: {scrolled}%;"
    ></div>
  </header>

  <!-- Sub-Header Telemetry Bar -->
  <div
    id="sub-header-telemetry"
    class="pt-12 px-5 border-b sticky top-12 z-20 flex items-center justify-between py-2 backdrop-blur-sm transition-colors {theme === 'dark' ? 'border-[#454934]/30 bg-[#0d0e11]/70 text-[#c6c9ad]' : 'border-[#e2e8f0] bg-slate-100/80 text-[#475569]'}"
  >
    <div class="flex items-center gap-2">
      <span class="w-2 h-2 bg-[#d1f029] rounded-full animate-ping"></span>
      <span class="font-mono text-xs uppercase tracking-wider font-semibold text-[#e3e2e6]">
        PHASE: {activePhaseName}
      </span>
    </div>

    <div class="flex gap-1" aria-label="Chapter Indicators">
      <div class="w-5 h-1 transition-colors {scrolled < 25 ? 'bg-[#d1f029]' : 'bg-[#343538]'}"></div>
      <div class="w-5 h-1 transition-colors {scrolled >= 25 && scrolled < 55 ? 'bg-[#d1f029]' : 'bg-[#343538]'}"></div>
      <div class="w-5 h-1 transition-colors {scrolled >= 55 && scrolled < 80 ? 'bg-[#d1f029]' : 'bg-[#343538]'}"></div>
      <div class="w-5 h-1 transition-colors {scrolled >= 80 ? 'bg-[#d1f029]' : 'bg-[#343538]'}"></div>
    </div>
  </div>

  <main class="relative z-0 max-w-5xl mx-auto pb-20">
    <!-- ACT I: KINETIC TYPOGRAPHIC HERO (Zero Photos) -->
    <section
      id="hero-section"
      class="min-h-[640px] flex flex-col justify-between px-5 pt-8 pb-8 border-b relative overflow-hidden transition-colors {theme === 'dark' ? 'border-[#454934]/40' : 'border-[#e2e8f0]'}"
    >
      <!-- Ambient Algorithmic Vector Overlay -->
      <div class="absolute top-10 right-[-5%] w-72 h-72 border border-[#454934]/30 rounded-full pointer-events-none opacity-40 flex items-center justify-center">
        <div class="w-52 h-52 border border-[#d1f029]/30 rotate-45"></div>
        <div class="w-32 h-32 border border-[#ff7f1c]/30 rounded-full"></div>
      </div>

      <!-- Top Metadata Header -->
      <div class="flex flex-col gap-1 relative z-10">
        <div class="flex items-center gap-2">
          <span class="inline-block px-2 py-0.5 bg-[#292a2d] border border-[#454934]/50 font-mono text-xs text-[#d1f029] font-bold">
            001.VEC
          </span>
          <span class="font-mono text-xs tracking-widest text-[#c6c9ad] uppercase">
            EPISTEMOLOGY OF RAW WIREFRAME
          </span>
        </div>
      </div>

      <!-- Hero Typographic Monument -->
      <div class="my-auto py-6 relative z-10">
        <h1
          id="hero-title"
          class="text-4xl sm:text-6xl font-extrabold tracking-tighter uppercase mb-4 leading-none {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}"
        >
          RADICAL<br />
          <span class="text-[#d1f029] inline-block transform -translate-x-1">FORM //</span><br />
          PURE ESSENCE
        </h1>

        <!-- Kinetic Callout Pill -->
        <div id="hero-subtitle" class="inline-flex items-center gap-2 px-3 py-1 bg-[#1b1b1f] border border-[#454934]/60 mb-4 shadow-lg">
          <span class="w-2 h-2 bg-[#ff7f1c] rounded-full"></span>
          <p class="font-mono text-xs text-[#e3e2e6] tracking-wider uppercase font-semibold">
            AXIOM: ARCHITECTURE WITHOUT MIMICRY
          </p>
        </div>

        <p id="hero-manifest" class="text-lg sm:text-xl text-[#c6c9ad] max-w-2xl font-serif font-normal leading-relaxed">
          Identity emerges from structure rather than surface imagery. Stripping away the synthetic veneer reveals an unadorned mathematical skeleton: a topography of vectors, coordinate tension, and uninhibited light.
        </p>
      </div>

      <!-- Scrolly Velocity Stats Plate -->
      <div class="grid grid-cols-2 gap-4 pt-4 border-t border-[#454934]/30 relative z-10">
        <div class="flex flex-col">
          <span class="font-mono text-xs text-[#c6c9ad] tracking-widest uppercase">LATENCY VECTOR</span>
          <span class="text-2xl font-bold font-mono text-white">0.0034 <span class="text-[#d1f029] text-xs">MS</span></span>
        </div>
        <div class="flex flex-col border-l border-[#454934]/30 pl-4">
          <span class="font-mono text-xs text-[#c6c9ad] tracking-widest uppercase">CANVAS TOPOLOGY</span>
          <span class="text-2xl font-bold font-mono text-white">NON-EUCLID</span>
        </div>
      </div>
    </section>

    <!-- ACT II: ABSTRACT STRUCTURAL DIAGRAM & DATA NARRATIVE -->
    <section
      id="act-2-schematics"
      class="px-5 py-10 border-b relative transition-colors {theme === 'dark' ? 'border-[#454934]/40 bg-[#0d0e11]' : 'border-[#e2e8f0] bg-white'}"
    >
      <div class="flex justify-between items-end mb-6">
        <div>
          <span class="font-mono text-xs text-[#d1f029] tracking-widest uppercase block mb-1">
            02 // SUB-SCHEMATICS
          </span>
          <h2 class="text-2xl sm:text-3xl font-bold tracking-tight text-white">
            KINETIC EQUILIBRIUM
          </h2>
        </div>
        <span class="font-mono text-xs text-[#ff7f1c] border border-[#ff7f1c]/40 px-2 py-0.5">
          FIG. 4A
        </span>
      </div>

      <!-- Generative Vector Viewport Card -->
      <div
        id="vector-schematic-card"
        class="w-full bg-[#1b1b1f] border border-[#454934]/60 p-4 sm:p-6 relative overflow-hidden mb-6 rounded-lg"
      >
        <div class="flex justify-between items-center pb-3 border-b border-[#454934]/30 mb-4 font-mono text-xs">
          <div class="flex items-center gap-2">
            <span class="text-[#d1f029]">☩</span>
            <span class="text-[#e3e2e6] uppercase font-bold tracking-wider">TOPOLOGICAL NODES [8,192]</span>
          </div>
          <span class="text-[#c6c9ad]">42.108° N, 71.058° W</span>
        </div>

        <!-- Wireframe & Geometric SVG Render (Zero Photos) -->
        <div class="w-full h-64 bg-[#0d0e11] border border-[#454934]/40 relative flex items-center justify-center overflow-hidden rounded">
          <svg class="w-full h-full p-4" viewBox="0 0 320 200" xmlns="http://www.w3.org/2000/svg">
            <!-- Concentric Coordinate Rings -->
            <circle cx="160" cy="100" r="75" stroke="#454934" stroke-dasharray="2 3" stroke-width="1" />
            <circle cx="160" cy="100" r="45" stroke="#d1f029" stroke-opacity="0.8" stroke-width="1.2" />
            <circle cx="160" cy="100" r="20" stroke="#ff7f1c" stroke-width="1" />
            <!-- Isometric Axis Rays -->
            <line x1="160" y1="10" x2="160" y2="190" stroke="#90937a" stroke-dasharray="4 4" stroke-width="0.8" />
            <line x1="40" y1="100" x2="280" y2="100" stroke="#90937a" stroke-dasharray="4 4" stroke-width="0.8" />
            <line x1="75" y1="25" x2="245" y2="175" stroke="#d1f029" stroke-opacity="0.6" stroke-width="0.75" />
            <!-- Dynamic Nodes -->
            <circle cx="75" cy="25" r="3.5" fill="#d1f029" />
            <circle cx="245" cy="175" r="3.5" fill="#d1f029" />
            <circle cx="160" cy="100" r="4.5" fill="#ffffff" />
            <circle cx="205" cy="65" r="2.5" fill="#ff7f1c" />
            <circle cx="115" cy="135" r="2.5" fill="#ff7f1c" />
            <!-- Dynamic polygon field -->
            <polygon points="160,55 205,100 160,145 115,100" fill="rgba(209, 240, 41, 0.08)" stroke="#d1f029" stroke-width="1.5" />
          </svg>

          <!-- Floating Data Badge Pin -->
          <div class="absolute bottom-3 left-3 bg-[#292a2d]/90 border border-[#454934] px-2.5 py-1 flex items-center gap-1.5 backdrop-blur-md rounded">
            <span class="w-1.5 h-1.5 rounded-full bg-[#d1f029]"></span>
            <span class="font-mono text-xs text-[#e3e2e6] font-bold">COHERENCE: 94%</span>
          </div>
          <div class="absolute top-3 right-3 bg-[#292a2d]/90 border border-[#454934] px-2.5 py-1 flex items-center gap-1.5 backdrop-blur-md rounded">
            <span class="font-mono text-xs text-[#ff7f1c] font-bold">ENTROPY: 0.12</span>
          </div>
        </div>

        <!-- Tactile Parameter Controls -->
        <div class="mt-6 space-y-4 font-mono text-xs">
          <div>
            <div class="flex justify-between items-center mb-1">
              <span class="text-[#c6c9ad] uppercase">HARMONIC RESONANCE</span>
              <span class="text-[#d1f029] font-bold">{harmonicHz.toFixed(1)} HZ</span>
            </div>
            <input
              type="range"
              min="40"
              max="160"
              step="0.5"
              bind:value={harmonicHz}
              class="w-full accent-[#d1f029] bg-[#343538] h-1.5 cursor-pointer"
            />
          </div>

          <div>
            <div class="flex justify-between items-center mb-1">
              <span class="text-[#c6c9ad] uppercase">SPATIAL COMPRESSION</span>
              <span class="text-[#ff7f1c] font-bold">{spatialCompression.toFixed(1)}:1 RATIO</span>
            </div>
            <input
              type="range"
              min="1.0"
              max="8.0"
              step="0.1"
              bind:value={spatialCompression}
              class="w-full accent-[#ff7f1c] bg-[#343538] h-1.5 cursor-pointer"
            />
          </div>
        </div>
      </div>

      <!-- Scrolly Narrative Annotation -->
      <div class="border-l-2 border-[#d1f029] pl-4 my-6">
        <p class="font-serif text-base text-[#e3e2e6] italic leading-relaxed">
          "When pictorial representations are removed, the eye stops looking for recognizable shadows and begins computing relational rhythm. Tension, pitch, and negative space become the sole carriers of truth."
        </p>
        <span class="font-mono text-xs text-[#90937a] tracking-wider uppercase block mt-2">
          — STRUCTURAL COMPUTATION LOG, V.8
        </span>
      </div>

      <!-- Technical Micro Chips -->
      <div class="flex flex-wrap gap-2 pt-2 font-mono text-xs">
        <span class="px-2.5 py-1 bg-[#1f1f23] border border-[#454934]/40 text-[#d1f029]">#ZERO_CAMERA</span>
        <span class="px-2.5 py-1 bg-[#1f1f23] border border-[#454934]/40 text-[#c6c9ad]">#PURE_TYPOGRAPHY</span>
        <span class="px-2.5 py-1 bg-[#1f1f23] border border-[#454934]/40 text-[#ff7f1c]">#ISOMETRIC_GRID</span>
      </div>
    </section>

    <!-- ACT III: MANIFESTO & PULL QUOTE ANCHOR -->
    <section
      id="act-3-manifesto"
      class="px-5 py-10 border-b relative transition-colors {theme === 'dark' ? 'border-[#454934]/40 bg-[#1b1b1f]' : 'border-[#e2e8f0] bg-slate-50'}"
    >
      <div class="flex items-center gap-2 mb-4 font-mono text-xs">
        <span class="w-1.5 h-1.5 bg-[#d1f029]"></span>
        <span class="text-[#c6c9ad] uppercase tracking-widest">INDEX 04 // MANIFESTO</span>
      </div>

      <!-- Colossal Pull Quote Block -->
      <div id="pull-quote-block" class="relative bg-[#0d0e11] border border-[#454934]/60 p-6 sm:p-8 rounded-lg shadow-2xl">
        <span class="font-mono text-[#d1f029] text-5xl block leading-none select-none mb-2 font-bold">“</span>
        <blockquote class="text-2xl sm:text-4xl font-extrabold tracking-tight text-white mb-4 leading-snug">
          WE DO NOT CAPTURE REALITY; WE CODE THE INTENTIONAL SCAFFOLD THAT PRECEDES IT.
        </blockquote>
        <p class="font-serif text-base text-[#c6c9ad] mb-6 leading-relaxed">
          The camera is a retroactive tool. Kinetic brutalism demands prospective generation. By constructing stories through line, weight, and scroll-linked geometry, the reader ceases being a passive spectator and turns into an active navigator.
        </p>

        <!-- Structured Metadata Spec Grid -->
        <div class="border-t border-[#454934]/40 pt-4 grid grid-cols-2 gap-4 font-mono text-xs">
          <div>
            <span class="text-[#90937a] uppercase block text-[10px]">ORIGIN SYSTEM</span>
            <span class="text-[#e3e2e6] font-bold">DIALECTIC ENGINE</span>
          </div>
          <div>
            <span class="text-[#90937a] uppercase block text-[10px]">AUTHORED BY</span>
            <span class="text-[#d1f029] font-bold">DMITRY EFREMOV</span>
          </div>
        </div>
      </div>

      <!-- Interactive Micro-Inspect Chip -->
      <div class="mt-4 p-3 bg-[#1f1f23] border border-[#454934]/50 rounded font-mono text-xs">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-[#d1f029]">⚡</span>
            <span class="text-[#e3e2e6]">INSPECT MATRIX SPECIFICATION (JSON)</span>
          </div>
          <button
            type="button"
            on:click={toggleInspect}
            class="text-[#d1f029] uppercase tracking-wider underline hover:text-white transition-colors"
          >
            {inspectExpanded ? 'COLLAPSE' : 'EXPAND'}
          </button>
        </div>

        {#if inspectExpanded}
          <pre class="mt-3 p-3 bg-[#0d0e11] text-[#c6c9ad] rounded overflow-x-auto text-[11px] leading-normal border border-[#454934]/30">{JSON.stringify(
            {
              matrixVersion: "4.8.0-SCROLLY",
              renderer: "VectorTypographyEngine",
              studioPhotosAllowed: false,
              contrastRatioTarget: ">= 4.5:1",
              scrollyParameters: {
                harmonicResonance: `${harmonicHz} HZ`,
                spatialCompression: `${spatialCompression}:1`
              },
              author: "Dmitry Efremov",
              domain: "dmitryefremov.com"
            },
            null,
            2
          )}</pre>
        {/if}
      </div>
    </section>

    <!-- ACT IV: INTERACTIVE EPILOGUE & ACTION -->
    <section id="act-4-epilogue" class="px-5 py-10">
      <div class="mb-6">
        <span class="font-mono text-xs text-[#ff7f1c] tracking-widest uppercase block mb-1">
          TERMINAL PHASE
        </span>
        <h3 class="text-2xl font-bold text-white">CONVERGENCE PATHWAYS</h3>
        <p class="font-serif text-sm text-[#c6c9ad] mt-1">
          Select your subsequent inquiry trajectory before passing the horizon.
        </p>
      </div>

      <!-- Branch Selection Stack -->
      <div class="flex flex-col gap-3 mb-6 font-mono text-xs">
        <!-- Option 1 -->
        <button
          type="button"
          on:click={() => (selectedBranch = 'CH.02A')}
          class="p-4 bg-[#292a2d] border-2 text-left flex items-center justify-between transition-colors rounded {selectedBranch === 'CH.02A' ? 'border-[#d1f029]' : 'border-[#454934]/40 hover:border-[#90937a]'}"
        >
          <div>
            <div class="flex items-center gap-2">
              <span class="text-[#d1f029] font-bold">CH.02A</span>
              <span class="text-[#c6c9ad] tracking-wider uppercase">RECURSIVE SPATIAL CODE</span>
            </div>
            <p class="font-serif text-xs text-[#e3e2e6] mt-1">
              Mathematical proof of non-visual UI narrative paradigms.
            </p>
          </div>
          <span class="text-[#d1f029] text-lg font-bold">→</span>
        </button>

        <!-- Option 2 -->
        <button
          type="button"
          on:click={() => (selectedBranch = 'CH.02B')}
          class="p-4 bg-[#1b1b1f] border text-left flex items-center justify-between transition-colors rounded {selectedBranch === 'CH.02B' ? 'border-[#ff7f1c]' : 'border-[#454934]/40 hover:border-[#90937a]'}"
        >
          <div>
            <div class="flex items-center gap-2">
              <span class="text-[#90937a] font-bold">CH.02B</span>
              <span class="text-[#c6c9ad] tracking-wider uppercase">SONIC RESONANCE PROFILES</span>
            </div>
            <p class="font-serif text-xs text-[#c6c9ad] mt-1">
              Scrollytelling audio design via parametric synth nodes.
            </p>
          </div>
          <span class="text-[#c6c9ad] text-lg font-bold">→</span>
        </button>
      </div>

      <!-- Primary Action -->
      <button
        id="engage-next-btn"
        type="button"
        on:click={() => onTabSelect('podcast')}
        class="w-full bg-[#d1f029] text-[#181e00] py-4 px-4 font-mono text-xs font-bold uppercase tracking-widest text-center flex items-center justify-center gap-3 active:scale-[0.98] transition-transform shadow-xl rounded hover:bg-[#b6d300] cursor-pointer"
      >
        <span>ENGAGE NEXT MOVEMENT ({selectedBranch})</span>
        <span class="font-mono text-sm">▶</span>
      </button>

      <!-- Secondary Action -->
      <button
        type="button"
        on:click={() => onTabSelect('article')}
        class="w-full mt-3 bg-transparent border border-[#454934]/60 hover:bg-[#1f1f23] text-white py-3 px-4 font-mono text-xs uppercase tracking-wider text-center flex items-center justify-center gap-2 transition-colors rounded cursor-pointer"
      >
        <span>ARCHIVE CHAPTER CHECKPOINT</span>
      </button>
    </section>
  </main>

  <!-- Bottom Navigation Bar -->
  <nav
    id="bottom-nav"
    aria-label="Chapter Controls"
    class="fixed bottom-0 left-0 w-full z-30 flex justify-around items-center h-14 px-4 border-t backdrop-blur-md transition-colors {theme === 'dark' ? 'border-[#454934]/40 bg-[#0d0e11]/90 text-[#c6c9ad]' : 'border-[#e2e8f0] bg-white/90 text-[#475569]'}"
  >
    <button
      type="button"
      on:click={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
      class="flex flex-col items-center justify-center p-1 hover:text-[#d1f029] transition-colors"
    >
      <span class="font-mono text-sm">⏮</span>
      <span class="font-mono text-[9px] uppercase tracking-wider mt-0.5">REWIND</span>
    </button>

    <button
      type="button"
      on:click={() => onTabSelect('podcast')}
      class="flex flex-col items-center justify-center p-1 hover:text-[#d1f029] transition-colors"
    >
      <span class="font-mono text-sm">🔊</span>
      <span class="font-mono text-[9px] uppercase tracking-wider mt-0.5">AUDIO</span>
    </button>

    <button
      type="button"
      class="flex flex-col items-center justify-center p-1 text-[#d1f029] relative"
    >
      <span class="font-mono text-sm font-bold">⚙</span>
      <span class="font-mono text-[9px] uppercase tracking-wider mt-0.5 font-bold">PARAMS</span>
      <span class="absolute -top-1 w-1 h-1 bg-[#d1f029] rounded-full"></span>
    </button>

    <button
      type="button"
      on:click={() => onTabSelect('article')}
      class="flex flex-col items-center justify-center p-1 hover:text-[#d1f029] transition-colors"
    >
      <span class="font-mono text-sm">🔖</span>
      <span class="font-mono text-[9px] uppercase tracking-wider mt-0.5">INDEX</span>
    </button>
  </nav>

  <!-- Footer -->
  <footer
    id="main-footer"
    class="border-t pt-6 pb-20 px-5 font-mono text-xs flex flex-col sm:flex-row justify-between items-center gap-4 transition-colors {theme === 'dark' ? 'border-[#454934]/40 text-[#c6c9ad]' : 'border-[#e2e8f0] text-[#64748b]'}"
  >
    <div>
      <span>© 2026 Дмитрий Ефремов (Dmitry Efremov). Все права защищены.</span>
    </div>
    <div class="flex items-center gap-6">
      <button
        id="cmp-persistent-link"
        type="button"
        on:click={onToggleCmp}
        class="underline hover:text-[#d1f029] transition-colors cursor-pointer"
      >
        Приватность (CMP)
      </button>
      <button
        id="imprint-toggle-btn"
        type="button"
        on:click={onToggleImprint}
        class="underline hover:text-[#d1f029] transition-colors cursor-pointer"
      >
        Реквизиты / Imprint
      </button>
    </div>
  </footer>
</div>
