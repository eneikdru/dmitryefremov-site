<script lang="ts">
  import { onMount } from 'svelte';

  export let theme: 'dark' | 'light' = 'dark';
  export let onOpenImprint: () => void = () => {};

  let scrubPercent: number = 0;
  let isInspectExpanded: boolean = false;
  let harmonicResonance: number = 88.4;
  let spatialCompression: number = 42;

  function handleScroll() {
    const winScroll = document.body.scrollTop || document.documentElement.scrollTop;
    const height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
    if (height > 0) {
      scrubPercent = Math.round((winScroll / height) * 100);
    } else {
      scrubPercent = 0;
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
    isInspectExpanded = !isInspectExpanded;
  }
</script>

<div
  id="scrollytelling-root"
  class="relative min-h-screen pb-20 font-sans transition-colors duration-200 {theme === 'dark' ? 'bg-[#121316] text-[#e3e2e6]' : 'bg-[#fafafa] text-[#121316]'}"
>
  <!-- Active progress edge tracker -->
  <div
    id="scrub-progress-bar"
    class="fixed top-0 left-0 h-[3px] bg-[#d1f029] z-50 transition-all duration-150"
    style="width: {scrubPercent}%;"
  ></div>

  <!-- Telemetry Header Sub-bar -->
  <div
    id="telemetry-subbar"
    class="sticky top-0 z-30 px-4 py-2 border-b flex items-center justify-between text-xs font-mono backdrop-blur-md {theme === 'dark' ? 'bg-[#121316]/80 border-[#343538] text-[#c6c9ad]' : 'bg-white/80 border-[#e2e8f0] text-[#475569]'}"
  >
    <div class="flex items-center gap-2">
      <span class="w-2 h-2 bg-[#d1f029] rounded-full animate-ping"></span>
      <span class="uppercase tracking-wider font-bold">PHASE: THE SYNTHESIS HORIZON</span>
    </div>
    <div class="flex items-center gap-3">
      <div class="flex items-center gap-1">
        <span class="text-[10px] uppercase text-[#90937a]">SCRUB</span>
        <span id="scrub-metric" class="text-[#d1f029] font-bold">{scrubPercent}%</span>
      </div>
      <div class="hidden sm:flex gap-1">
        <div class="w-4 h-1 bg-[#d1f029]"></div>
        <div class="w-4 h-1 bg-[#343538]"></div>
        <div class="w-4 h-1 bg-[#343538]"></div>
        <div class="w-4 h-1 bg-[#343538]"></div>
      </div>
    </div>
  </div>

  <main class="max-w-4xl mx-auto px-4 sm:px-6 space-y-12 pt-6">
    <!-- ACT I: KINETIC TYPOGRAPHIC HERO -->
    <section id="act-1-hero" class="min-h-[600px] flex flex-col justify-between py-8 border-b border-[#343538]/40 relative overflow-hidden">
      <!-- Ambient Algorithmic Vector Overlay -->
      <div class="absolute top-4 right-[-5%] w-64 h-64 border border-[#90937a]/20 rounded-full pointer-events-none opacity-40 flex items-center justify-center">
        <div class="w-48 h-48 border border-[#d1f029]/20 rotate-45"></div>
        <div class="w-28 h-28 border border-[#ff7f1c]/30 rounded-full"></div>
      </div>

      <!-- Top Metadata Header -->
      <div class="flex flex-col gap-1 relative z-10 font-mono text-xs">
        <div class="flex items-center gap-2">
          <span class="px-2 py-0.5 bg-[#1f1f23] border border-[#454934] text-[#d1f029] font-bold">001.VEC</span>
          <span class="text-[#c6c9ad] tracking-widest uppercase">EPISTEMOLOGY OF RAW WIREFRAME</span>
        </div>
      </div>

      <!-- Hero Typographic Monument -->
      <div class="my-auto py-8 relative z-10">
        <h1 class="text-4xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight uppercase leading-none mb-6 text-white">
          RADICAL<br />
          <span class="text-[#d1f029] inline-block transform -translate-x-1">FORM //</span><br />
          PURE ESSENCE
        </h1>

        <!-- Kinetic Callout Pill -->
        <div class="inline-flex items-center gap-2 px-3 py-1.5 bg-[#1b1b1f] border border-[#454934] mb-6 font-mono text-xs">
          <span class="w-2 h-2 bg-[#ff7f1c] rounded-full"></span>
          <p class="text-white tracking-wider uppercase font-semibold">AXIOM: ARCHITECTURE WITHOUT MIMICRY</p>
        </div>

        <p class="text-lg sm:text-xl text-[#c6c9ad] max-w-2xl font-serif leading-relaxed">
          Identity emerges from structure rather than surface imagery. Stripping away the synthetic veneer reveals an unadorned mathematical skeleton: a topography of vectors, coordinate tension, and uninhibited light.
        </p>
      </div>

      <!-- Scrolly Velocity Stats Plate -->
      <div class="grid grid-cols-2 gap-4 pt-6 border-t border-[#343538]/40 relative z-10 font-mono">
        <div class="flex flex-col">
          <span class="text-[10px] text-[#90937a] tracking-widest uppercase">LATENCY VECTOR</span>
          <span class="text-xl sm:text-2xl text-white font-bold">0.0034 <span class="text-[#d1f029] text-xs">MS</span></span>
        </div>
        <div class="flex flex-col border-l border-[#343538]/40 pl-4">
          <span class="text-[10px] text-[#90937a] tracking-widest uppercase">CANVAS TOPOLOGY</span>
          <span class="text-xl sm:text-2xl text-white font-bold">NON-EUCLID</span>
        </div>
      </div>
    </section>

    <!-- ACT II: ABSTRACT STRUCTURAL DIAGRAM & DATA NARRATIVE -->
    <section id="act-2-schematics" class="py-8 border-b border-[#343538]/40 relative space-y-6">
      <div class="flex justify-between items-end">
        <div>
          <span class="text-xs font-mono text-[#d1f029] tracking-widest uppercase block mb-1">02 // SUB-SCHEMATICS</span>
          <h2 class="text-2xl sm:text-3xl font-bold tracking-tight text-white">KINETIC EQUILIBRIUM</h2>
        </div>
        <span class="font-mono text-xs text-[#ff7f1c] border border-[#ff7f1c]/40 px-2 py-0.5">FIG. 4A</span>
      </div>

      <!-- Generative Vector Viewport Card -->
      <div class="w-full bg-[#1b1b1f] border border-[#454934] p-4 sm:p-6 relative overflow-hidden rounded-xl space-y-4">
        <div class="flex justify-between items-center pb-3 border-b border-[#343538] font-mono text-xs">
          <div class="flex items-center gap-2">
            <span class="text-[#d1f029]">✦</span>
            <span class="text-white uppercase tracking-wider font-bold">TOPOLOGICAL NODES [8,192]</span>
          </div>
          <span class="text-[#c6c9ad]">42.108° N, 71.058° W</span>
        </div>

        <!-- Wireframe & Geometric SVG Render (Zero Photos) -->
        <div class="w-full h-64 bg-[#0d0e11] border border-[#343538] relative flex items-center justify-center overflow-hidden rounded-lg">
          <svg class="w-full h-full p-4" viewBox="0 0 320 200" xmlns="http://www.w3.org/2000/svg" aria-label="Abstract Geometric Schematic Vector Diagram">
            <!-- Concentric Coordinate Rings -->
            <circle cx="160" cy="100" r="75" stroke="#454934" stroke-dasharray="2 3" stroke-width="1"></circle>
            <circle cx="160" cy="100" r="45" stroke="#d1f029" stroke-opacity="0.8" stroke-width="1.2"></circle>
            <circle cx="160" cy="100" r="20" stroke="#ff7f1c" stroke-width="1"></circle>

            <!-- Isometric Axis Rays -->
            <line x1="160" y1="10" x2="160" y2="190" stroke="#90937a" stroke-dasharray="4 4" stroke-width="0.8"></line>
            <line x1="40" y1="100" x2="280" y2="100" stroke="#90937a" stroke-dasharray="4 4" stroke-width="0.8"></line>
            <line x1="75" y1="25" x2="245" y2="175" stroke="#d1f029" stroke-opacity="0.6" stroke-width="0.75"></line>

            <!-- Nodes -->
            <circle cx="75" cy="25" r="3.5" fill="#d1f029"></circle>
            <circle cx="245" cy="175" r="3.5" fill="#d1f029"></circle>
            <circle cx="160" cy="100" r="4.5" fill="#ffffff"></circle>
            <circle cx="205" cy="65" r="2.5" fill="#ff7f1c"></circle>
            <circle cx="115" cy="135" r="2.5" fill="#ff7f1c"></circle>

            <!-- Dynamic polygon field -->
            <polygon points="160,55 205,100 160,145 115,100" fill="rgba(209, 240, 41, 0.08)" stroke="#d1f029" stroke-width="1.5"></polygon>
          </svg>

          <!-- Floating Data Badges -->
          <div class="absolute bottom-3 left-3 bg-[#1f1f23]/90 border border-[#454934] px-2.5 py-1 flex items-center gap-1.5 backdrop-blur-md font-mono text-[11px]">
            <span class="w-1.5 h-1.5 rounded-full bg-[#d1f029]"></span>
            <span class="text-white">COHERENCE: 94%</span>
          </div>
          <div class="absolute top-3 right-3 bg-[#1f1f23]/90 border border-[#454934] px-2.5 py-1 flex items-center gap-1.5 backdrop-blur-md font-mono text-[11px]">
            <span class="text-[#ffb68b]">ENTROPY: 0.12</span>
          </div>
        </div>

        <!-- Tactile Parameter Controls -->
        <div class="space-y-4 pt-2 font-mono text-xs">
          <div>
            <div class="flex justify-between items-center mb-1">
              <span class="text-[#c6c9ad] uppercase">HARMONIC RESONANCE</span>
              <span class="text-[#d1f029] font-bold">{harmonicResonance} HZ</span>
            </div>
            <div class="w-full h-2 bg-[#343538] relative">
              <div class="h-full bg-[#d1f029]" style="width: 76%;"></div>
            </div>
          </div>
          <div>
            <div class="flex justify-between items-center mb-1">
              <span class="text-[#c6c9ad] uppercase">SPATIAL COMPRESSION</span>
              <span class="text-[#ff7f1c] font-bold">3.4:1 RATIO</span>
            </div>
            <div class="w-full h-2 bg-[#343538] relative">
              <div class="h-full bg-[#ff7f1c]" style="width: 42%;"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Scrolly Narrative Annotation -->
      <div class="border-l-2 border-[#d1f029] pl-4 my-6">
        <p class="text-base sm:text-lg text-white font-serif italic leading-relaxed">
          "When pictorial representations are removed, the eye stops looking for recognizable shadows and begins computing relational rhythm. Tension, pitch, and negative space become the sole carriers of truth."
        </p>
        <span class="font-mono text-xs text-[#90937a] tracking-wider uppercase block mt-2">— STRUCTURAL COMPUTATION LOG, V.8</span>
      </div>

      <!-- Technical Micro Chips -->
      <div class="flex flex-wrap gap-2 pt-2 font-mono text-xs">
        <span class="px-2.5 py-1 bg-[#1f1f23] border border-[#454934] text-[#d1f029]">#ZERO_CAMERA</span>
        <span class="px-2.5 py-1 bg-[#1f1f23] border border-[#454934] text-[#c6c9ad]">#PURE_TYPOGRAPHY</span>
        <span class="px-2.5 py-1 bg-[#1f1f23] border border-[#454934] text-[#ffb68b]">#ISOMETRIC_GRID</span>
      </div>
    </section>

    <!-- ACT III: MANIFESTO & PULL QUOTE ANCHOR -->
    <section id="act-3-manifesto" class="py-8 border-b border-[#343538]/40 relative space-y-6">
      <div class="flex items-center gap-2 font-mono text-xs">
        <span class="w-2 h-2 bg-[#d1f029]"></span>
        <span class="text-[#c6c9ad] uppercase tracking-widest">INDEX 04 // MANIFESTO</span>
      </div>

      <!-- Colossal Pull Quote Block -->
      <div class="bg-[#0d0e11] border border-[#454934] p-6 sm:p-8 rounded-xl space-y-4">
        <span class="font-mono text-[#d1f029] text-4xl block leading-none select-none font-bold">“</span>
        <blockquote class="text-2xl sm:text-3xl font-extrabold text-white uppercase tracking-tight leading-snug">
          WE DO NOT CAPTURE REALITY; WE CODE THE INTENTIONAL SCAFFOLD THAT PRECEDES IT.
        </blockquote>
        <p class="text-base text-[#c6c9ad] font-serif leading-relaxed">
          The camera is a retroactive tool. Kinetic brutalism demands prospective generation. By constructing stories through line, weight, and scroll-linked geometry, the reader ceases being a passive spectator and turns into an active navigator.
        </p>

        <!-- Structured Metadata Spec Grid -->
        <div class="border-t border-[#343538] pt-4 grid grid-cols-2 gap-4 font-mono text-xs">
          <div>
            <span class="text-[#90937a] uppercase block text-[10px]">ORIGIN SYSTEM</span>
            <span class="text-white font-bold">DIALECTIC ENGINE</span>
          </div>
          <div>
            <span class="text-[#90937a] uppercase block text-[10px]">AUTHORED BY</span>
            <span class="text-[#d1f029] font-bold">K. VANDERMEER</span>
          </div>
        </div>
      </div>

      <!-- Interactive Micro-Inspect Chip -->
      <div class="p-4 bg-[#1f1f23] border border-[#454934] rounded-lg font-mono text-xs space-y-2">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-[#d1f029]">$&gt;</span>
            <span class="text-white">INSPECT MATRIX SPECIFICATION (JSON)</span>
          </div>
          <button
            id="inspect-expand-btn"
            type="button"
            on:click={toggleInspect}
            class="text-[#d1f029] uppercase tracking-wider underline hover:text-white transition-colors cursor-pointer"
          >
            {isInspectExpanded ? 'COLLAPSE' : 'EXPAND'}
          </button>
        </div>

        {#if isInspectExpanded}
          <pre id="inspect-json-spec" class="p-3 bg-[#0d0e11] border border-[#343538] text-[#d1f029] overflow-x-auto text-[11px] rounded leading-relaxed">
{JSON.stringify({
  "paradigm": "scrollytelling",
  "studio_photos": false,
  "vector_schematics": true,
  "wcag_contrast": ">= 4.5:1",
  "a2a_infrastructure": "autonomous",
  "typography": ["Syne", "Newsreader", "Space Mono"]
}, null, 2)}
          </pre>
        {/if}
      </div>
    </section>

    <!-- ACT IV: CONVERGENCE PATHWAYS -->
    <section id="act-4-epilogue" class="py-8 space-y-6">
      <div>
        <span class="font-mono text-xs text-[#ff7f1c] tracking-widest uppercase block mb-1">TERMINAL PHASE</span>
        <h3 class="text-2xl font-bold text-white">CONVERGENCE PATHWAYS</h3>
        <p class="text-sm text-[#c6c9ad] font-mono mt-1">Select your subsequent inquiry trajectory before passing the horizon.</p>
      </div>

      <!-- Branch Selection Stack -->
      <div class="flex flex-col gap-3 font-mono">
        <div class="p-4 bg-[#292a2d] border-2 border-[#d1f029] rounded-lg flex items-center justify-between">
          <div>
            <div class="flex items-center gap-2 text-xs">
              <span class="text-[#d1f029] font-bold">CH.02A</span>
              <span class="text-[#c6c9ad] uppercase tracking-wider">RECURSIVE SPATIAL CODE</span>
            </div>
            <p class="text-xs text-white font-sans mt-1">Mathematical proof of non-visual UI narrative paradigms.</p>
          </div>
          <span class="text-[#d1f029] font-bold text-lg">→</span>
        </div>

        <div class="p-4 bg-[#1b1b1f] border border-[#454934] rounded-lg flex items-center justify-between">
          <div>
            <div class="flex items-center gap-2 text-xs">
              <span class="text-[#90937a] uppercase font-bold">CH.02B</span>
              <span class="text-[#c6c9ad] uppercase tracking-wider">SONIC RESONANCE PROFILES</span>
            </div>
            <p class="text-xs text-[#c6c9ad] font-sans mt-1">Scrollytelling audio design via parametric synth nodes.</p>
          </div>
          <span class="text-[#c6c9ad] text-lg">→</span>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="space-y-3 font-mono text-xs">
        <button
          type="button"
          class="w-full bg-[#d1f029] text-[#181e00] py-4 px-4 font-bold uppercase tracking-widest text-center flex items-center justify-center gap-2 rounded-lg hover:bg-[#b6d300] transition-colors cursor-pointer"
        >
          <span>ENGAGE NEXT MOVEMENT</span>
          <span>▶</span>
        </button>

        <button
          type="button"
          on:click={onOpenImprint}
          id="scrolly-imprint-btn"
          class="w-full bg-transparent border border-[#454934] text-white py-3 px-4 uppercase tracking-wider text-center flex items-center justify-center gap-2 rounded-lg hover:bg-[#1f1f23] transition-colors cursor-pointer"
        >
          <span>ЮРИДИЧЕСКИЕ РЕКВИЗИТЫ / IMPRINT</span>
        </button>
      </div>
    </section>
  </main>
</div>
