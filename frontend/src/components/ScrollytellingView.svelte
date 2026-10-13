<script lang="ts">
  import { onMount } from 'svelte';

  export let theme: 'dark' | 'light' = 'dark';

  let scrubMetric: number = 0;
  let showImprintModal: boolean = false;
  let isInspectExpanded: boolean = false;

  function handleScroll() {
    if (typeof window === 'undefined') return;
    const winScroll = document.body.scrollTop || document.documentElement.scrollTop;
    const height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
    if (height > 0) {
      scrubMetric = Math.min(100, Math.max(0, Math.round((winScroll / height) * 100)));
    } else {
      scrubMetric = 0;
    }
  }

  onMount(() => {
    window.addEventListener('scroll', handleScroll);
    handleScroll();
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  });

  function toggleImprint() {
    showImprintModal = !showImprintModal;
  }

  function toggleInspect() {
    isInspectExpanded = !isInspectExpanded;
  }
</script>

<div id="scrolly-root" class="min-h-screen bg-background text-on-surface antialiased blueprint-grid selection:bg-primary-fixed selection:text-surface-container-lowest pb-24 relative">
  <!-- ========================================================================= -->
  <!-- 1. TopAppBar / Telemetry Header                                           -->
  <!-- ========================================================================= -->
  <header id="scrolly-header" class="fixed top-0 left-0 w-full z-20 flex justify-between items-center h-12 px-margin border-b border-outline-variant/30 bg-surface-container-lowest/85 backdrop-blur-md">
    <div class="flex items-center gap-space-sm">
      <button aria-label="Audio telemetry toggle" class="flex items-center justify-center text-primary-fixed hover:text-primary-fixed hover:bg-surface-container-high/50 p-1 transition-opacity duration-150 active:opacity-75">
        <span class="material-symbols-outlined text-[18px]">graphic_eq</span>
      </button>
      <span class="font-label-meta text-label-meta tracking-widest uppercase font-bold text-primary-fixed">CH.01 // ORIGIN MATRIX</span>
    </div>
    <div class="flex items-center gap-space-md">
      <!-- Chapter Scrub Meter Micro Display -->
      <div class="hidden sm:flex items-center gap-space-xs font-label-code text-label-code text-on-surface-variant">
        <span>SCRUB</span>
        <span class="text-primary-fixed font-bold" id="scrub-metric">{scrubMetric}%</span>
      </div>
      <button aria-label="Open menu drawer" class="flex items-center justify-center text-primary-fixed hover:text-primary-fixed hover:bg-surface-container-high/50 p-1 transition-opacity duration-150 active:opacity-75">
        <span class="material-symbols-outlined text-[20px]">menu</span>
      </button>
    </div>
    <!-- Active progress edge tracker -->
    <div class="absolute bottom-0 left-0 h-[2px] bg-primary-fixed transition-all duration-300" style="width: {scrubMetric}%;"></div>
  </header>

  <!-- ========================================================================= -->
  <!-- SUB-HEADER TELEMETRY BAR: Segmented Chapter Indicator                     -->
  <!-- ========================================================================= -->
  <div class="pt-12 px-margin border-b border-outline-variant/20 bg-surface-container-lowest/60 backdrop-blur-sm sticky top-12 z-10 flex items-center justify-between py-2 text-on-surface-variant">
    <div class="flex items-center gap-2">
      <span class="w-2 h-2 bg-primary-fixed rounded-full animate-ping"></span>
      <span class="font-label-code text-label-code text-on-surface uppercase tracking-wider">PHASE: THE SYNTHESIS HORIZON</span>
    </div>
    <div class="flex gap-1">
      <div class="w-5 h-1 bg-primary-fixed"></div>
      <div class="w-5 h-1 bg-surface-container-highest"></div>
      <div class="w-5 h-1 bg-surface-container-highest"></div>
      <div class="w-5 h-1 bg-surface-container-highest"></div>
    </div>
  </div>

  <main id="scrolly-main" class="relative z-0">
    <!-- ======================================================================= -->
    <!-- ACT I: KINETIC TYPOGRAPHIC HERO (PURE TYPOGRAPHY, NO STUDIO PHOTOS)      -->
    <!-- ======================================================================= -->
    <section id="act-1-hero" class="min-h-[795px] flex flex-col justify-between px-margin pt-space-xl pb-space-lg border-b border-outline-variant/30 relative overflow-hidden">
      <!-- Ambient Algorithmic Vector Overlay -->
      <div class="absolute top-10 right-[-10%] w-72 h-72 border border-outline-variant/20 rounded-full pointer-events-none opacity-40 flex items-center justify-center">
        <div class="w-52 h-52 border border-primary-fixed/20 rotate-45"></div>
        <div class="w-32 h-32 border border-secondary-container/30 rounded-full"></div>
      </div>

      <!-- Top Metadata Header -->
      <div class="flex flex-col gap-space-xs relative z-10">
        <div class="flex items-center gap-2">
          <span class="inline-block px-1.5 py-0.5 bg-surface-container-high border border-outline-variant/40 font-label-code text-label-code text-primary-fixed">001.VEC</span>
          <span class="font-label-meta text-label-meta tracking-widest text-on-surface-variant uppercase">EPISTEMOLOGY OF RAW WIREFRAME</span>
        </div>
      </div>

      <!-- Hero Typographic Monument -->
      <div class="my-auto py-space-lg relative z-10">
        <h1 class="font-display-xl-mobile text-display-xl-mobile text-primary tracking-tighter uppercase font-extrabold mb-space-md">
          RADICAL<br/>
          <span class="text-primary-fixed inline-block transform -translate-x-1">FORM //</span><br/>
          PURE ESSENCE
        </h1>
        <!-- Kinetic Callout Pill -->
        <div class="inline-flex items-center gap-2 px-3 py-1 bg-surface-container-low border border-outline-variant/50 mb-space-md accent-glow">
          <span class="w-1.5 h-1.5 bg-secondary-container rounded-full"></span>
          <p class="font-label-code text-label-code text-on-surface tracking-wider uppercase">AXIOM: ARCHITECTURE WITHOUT MIMICRY</p>
        </div>
        <p class="font-body-lead text-body-lead text-on-surface-variant max-w-xl font-normal leading-relaxed">
          Identity emerges from structure rather than surface imagery. Stripping away the synthetic veneer reveals an unadorned mathematical skeleton: a topography of vectors, coordinate tension, and uninhibited light.
        </p>
      </div>

      <!-- Scrolly Velocity Stats Plate -->
      <div class="grid grid-cols-2 gap-gutter pt-space-md border-t border-outline-variant/20 relative z-10">
        <div class="flex flex-col">
          <span class="font-label-meta text-label-meta text-on-surface-variant tracking-widest uppercase">LATENCY VECTOR</span>
          <span class="font-headline-md text-headline-md text-primary font-bold">0.0034 <span class="text-primary-fixed text-xs font-label-code">MS</span></span>
        </div>
        <div class="flex flex-col border-l border-outline-variant/20 pl-4">
          <span class="font-label-meta text-label-meta text-on-surface-variant tracking-widest uppercase">CANVAS TOPOLOGY</span>
          <span class="font-headline-md text-headline-md text-primary font-bold">NON-EUCLID</span>
        </div>
      </div>
    </section>

    <!-- ======================================================================= -->
    <!-- ACT II: ABSTRACT STRUCTURAL DIAGRAM & DATA NARRATIVE                    -->
    <!-- ======================================================================= -->
    <section id="act-2-diagram" class="px-margin py-space-xl border-b border-outline-variant/30 bg-surface-container-lowest relative">
      <!-- Section Chapter Tag -->
      <div class="flex justify-between items-end mb-space-lg">
        <div>
          <span class="font-label-meta text-label-meta text-primary-fixed tracking-widest uppercase block mb-1">02 // SUB-SCHEMATICS</span>
          <h2 class="font-headline-lg-mobile text-headline-lg-mobile text-primary font-bold tracking-tight">KINETIC EQUILIBRIUM</h2>
        </div>
        <span class="font-label-code text-label-code text-secondary-container border border-secondary-container/40 px-2 py-0.5">FIG. 4A</span>
      </div>

      <!-- Generative Vector Viewport Card -->
      <div class="w-full bg-surface-container-low border border-outline-variant/50 p-space-md relative overflow-hidden mb-space-lg">
        <div class="flex justify-between items-center pb-3 border-b border-outline-variant/20 mb-4">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-primary-fixed text-sm">hub</span>
            <span class="font-label-meta text-label-meta text-on-surface uppercase tracking-wider">TOPOLOGICAL NODES [8,192]</span>
          </div>
          <span class="font-label-code text-label-code text-on-surface-variant">42.108° N, 71.058° W</span>
        </div>

        <!-- Wireframe & Geometric SVG Render (Zero Photos) -->
        <div class="w-full h-56 bg-surface-container-lowest border border-outline-variant/30 relative flex items-center justify-center overflow-hidden">
          <div class="absolute inset-0 opacity-20 blueprint-grid"></div>
          <!-- Geometric Schematic Lines -->
          <svg class="w-full h-full p-4" fill="none" viewBox="0 0 320 200" xmlns="http://www.w3.org/2000/svg">
            <circle cx="160" cy="100" r="75" stroke="#454934" stroke-dasharray="2 3" stroke-width="1"></circle>
            <circle cx="160" cy="100" r="45" stroke="#d1f029" stroke-opacity="0.8" stroke-width="1.2"></circle>
            <circle cx="160" cy="100" r="20" stroke="#ff7f1c" stroke-width="1"></circle>
            <line stroke="#90937a" stroke-dasharray="4 4" stroke-width="0.8" x1="160" x2="160" y1="10" y2="190"></line>
            <line stroke="#90937a" stroke-dasharray="4 4" stroke-width="0.8" x1="40" x2="280" y1="100" y2="100"></line>
            <line stroke="#d1f029" stroke-opacity="0.6" stroke-width="0.75" x1="75" x2="245" y1="25" y2="175"></line>
            <circle cx="75" cy="25" fill="#d1f029" r="3.5"></circle>
            <circle cx="245" cy="175" fill="#d1f029" r="3.5"></circle>
            <circle cx="160" cy="100" fill="#ffffff" r="4.5"></circle>
            <circle cx="205" cy="65" fill="#ff7f1c" r="2.5"></circle>
            <circle cx="115" cy="135" fill="#ff7f1c" r="2.5"></circle>
            <polygon fill="rgba(209, 240, 41, 0.05)" points="160,55 205,100 160,145 115,100" stroke="#d1f029" stroke-width="1.5"></polygon>
          </svg>
          <div class="absolute bottom-3 left-3 bg-surface-container-high/90 border border-outline-variant px-2 py-1 flex items-center gap-1.5 backdrop-blur-md">
            <span class="w-1.5 h-1.5 rounded-full bg-primary-fixed"></span>
            <span class="font-label-code text-label-code text-on-surface">COHERENCE: 94%</span>
          </div>
          <div class="absolute top-3 right-3 bg-surface-container-high/90 border border-outline-variant px-2 py-1 flex items-center gap-1.5 backdrop-blur-md">
            <span class="font-label-code text-label-code text-secondary">ENTROPY: 0.12</span>
          </div>
        </div>

        <!-- Tactile Parameter Controls -->
        <div class="mt-space-md space-y-3">
          <div>
            <div class="flex justify-between items-center mb-1 font-label-code text-label-code">
              <span class="text-on-surface-variant uppercase">HARMONIC RESONANCE</span>
              <span class="text-primary-fixed">88.4 HZ</span>
            </div>
            <div class="w-full h-1.5 bg-surface-container-highest rounded-none relative">
              <div class="h-full bg-primary-fixed" style="width: 76%;"></div>
              <div class="absolute top-1/2 left-[76%] -translate-y-1/2 w-3 h-3 bg-primary border border-surface-container-lowest"></div>
            </div>
          </div>
          <div>
            <div class="flex justify-between items-center mb-1 font-label-code text-label-code">
              <span class="text-on-surface-variant uppercase">SPATIAL COMPRESSION</span>
              <span class="text-secondary">3.4:1 RATIO</span>
            </div>
            <div class="w-full h-1.5 bg-surface-container-highest rounded-none relative">
              <div class="h-full bg-secondary-container" style="width: 42%;"></div>
              <div class="absolute top-1/2 left-[42%] -translate-y-1/2 w-3 h-3 bg-primary border border-surface-container-lowest"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Scrolly Narrative Annotation -->
      <div class="border-l-2 border-primary-fixed pl-4 my-space-md">
        <p class="font-body-md text-body-md text-on-surface leading-relaxed italic">
          "When pictorial representations are removed, the eye stops looking for recognizable shadows and begins computing relational rhythm. Tension, pitch, and negative space become the sole carriers of truth."
        </p>
        <span class="font-label-meta text-label-meta text-outline tracking-wider uppercase block mt-2">— STRUCTURAL COMPUTATION LOG, V.8</span>
      </div>

      <div class="flex flex-wrap gap-2 pt-2">
        <span class="px-2.5 py-1 bg-surface-container border border-outline-variant/40 font-label-code text-label-code text-primary-fixed">#ZERO_CAMERA</span>
        <span class="px-2.5 py-1 bg-surface-container border border-outline-variant/40 font-label-code text-label-code text-on-surface-variant">#PURE_TYPOGRAPHY</span>
        <span class="px-2.5 py-1 bg-surface-container border border-outline-variant/40 font-label-code text-label-code text-secondary">#ISOMETRIC_GRID</span>
      </div>
    </section>

    <!-- ======================================================================= -->
    <!-- ACT III: MANIFESTO & PULL QUOTE ANCHOR                                  -->
    <!-- ======================================================================= -->
    <section id="act-3-manifesto" class="px-margin py-space-xl border-b border-outline-variant/30 bg-surface-container-low relative overflow-hidden">
      <div class="flex items-center gap-2 mb-space-md">
        <span class="w-1.5 h-1.5 bg-primary-fixed"></span>
        <span class="font-label-meta text-label-meta text-on-surface-variant uppercase tracking-widest">INDEX 04 // MANIFESTO</span>
      </div>

      <div class="relative bg-surface-container-lowest border border-outline-variant/60 p-space-lg accent-glow">
        <span class="font-label-code text-primary-fixed text-4xl block leading-none select-none mb-2 font-bold">“</span>
        <blockquote class="font-headline-lg-mobile text-headline-lg-mobile text-primary font-bold tracking-tight mb-space-md">
          WE DO NOT CAPTURE REALITY; WE CODE THE INTENTIONAL SCAFFOLD THAT PRECEDES IT.
        </blockquote>
        <p class="font-body-md text-body-md text-on-surface-variant mb-space-lg">
          The camera is a retroactive tool. Kinetic brutalism demands prospective generation. By constructing stories through line, weight, and scroll-linked geometry, the reader ceases being a passive spectator and turns into an active navigator.
        </p>
        <div class="border-t border-outline-variant/40 pt-space-md grid grid-cols-2 gap-4 font-label-code text-label-code">
          <div>
            <span class="text-outline uppercase block text-[10px]">ORIGIN SYSTEM</span>
            <span class="text-on-surface font-bold">DIALECTIC ENGINE</span>
          </div>
          <div>
            <span class="text-outline uppercase block text-[10px]">AUTHORED BY</span>
            <span class="text-primary-fixed font-bold">K. VANDERMEER</span>
          </div>
        </div>
      </div>

      <div class="mt-space-md flex flex-col p-3 bg-surface-container border border-outline-variant/50">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-primary-fixed text-base">terminal</span>
            <span class="font-label-code text-label-code text-on-surface">INSPECT MATRIX SPECIFICATION (JSON)</span>
          </div>
          <button id="inspect-expand-btn" type="button" on:click={toggleInspect} class="font-label-code text-label-code text-primary-fixed uppercase tracking-wider underline hover:text-primary transition-colors cursor-pointer">
            {isInspectExpanded ? 'COLLAPSE' : 'EXPAND'}
          </button>
        </div>
        {#if isInspectExpanded}
          <pre class="mt-3 p-3 bg-surface-container-lowest border border-outline-variant/40 font-label-code text-[11px] text-primary-fixed overflow-x-auto">
{JSON.stringify({
  schema: "ScrollytellingUI/3.0",
  photos_count: 0,
  typography_mode: "kinetic_brutalism",
  contrast_ratio: ">= 4.5:1",
  imprint_verified: true,
  scrolly_nodes: 8192
}, null, 2)}
          </pre>
        {/if}
      </div>
    </section>

    <!-- ======================================================================= -->
    <!-- ACT IV: INTERACTIVE EPILOGUE & ACTION                                   -->
    <!-- ======================================================================= -->
    <section id="act-4-epilogue" class="px-margin py-space-xl">
      <div class="mb-space-lg">
        <span class="font-label-meta text-label-meta text-secondary tracking-widest uppercase block mb-1">TERMINAL PHASE</span>
        <h3 class="font-headline-md text-headline-md text-primary font-bold">CONVERGENCE PATHWAYS</h3>
        <p class="font-body-sm text-body-sm text-on-surface-variant mt-1">Select your subsequent inquiry trajectory before passing the horizon.</p>
      </div>

      <div class="flex flex-col gap-3 mb-space-lg">
        <div class="p-space-md bg-surface-container-high border-2 border-primary-fixed flex items-center justify-between group cursor-pointer transition-colors">
          <div>
            <div class="flex items-center gap-2">
              <span class="font-label-code text-label-code text-primary-fixed font-bold">CH.02A</span>
              <span class="font-label-meta text-label-meta text-on-surface-variant tracking-wider uppercase">RECURSIVE SPATIAL CODE</span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface mt-1">Mathematical proof of non-visual UI narrative paradigms.</p>
          </div>
          <span class="material-symbols-outlined text-primary-fixed text-xl group-hover:translate-x-1 transition-transform">arrow_forward</span>
        </div>

        <div class="p-space-md bg-surface-container-low border border-outline-variant/40 flex items-center justify-between group cursor-pointer hover:border-outline transition-colors">
          <div>
            <div class="flex items-center gap-2">
              <span class="font-label-code text-label-code text-outline uppercase font-bold">CH.02B</span>
              <span class="font-label-meta text-label-meta text-on-surface-variant tracking-wider uppercase">SONIC RESONANCE PROFILES</span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant mt-1">Scrollytelling audio design via parametric synth nodes.</p>
          </div>
          <span class="material-symbols-outlined text-on-surface-variant text-xl group-hover:translate-x-1 transition-transform">arrow_forward</span>
        </div>
      </div>

      <button id="engage-btn" type="button" class="w-full bg-primary-fixed text-on-primary-fixed py-4 px-space-md font-label-code text-label-code font-bold uppercase tracking-widest text-center flex items-center justify-center gap-3 active:scale-[0.98] transition-transform saffron-glow cursor-pointer">
        <span>ENGAGE NEXT MOVEMENT</span>
        <span class="material-symbols-outlined text-base">play_arrow</span>
      </button>

      <button id="archive-btn" type="button" class="w-full mt-3 bg-transparent border border-outline-variant/50 hover:bg-surface-container text-primary py-3 px-space-md font-label-code text-label-code uppercase tracking-wider text-center flex items-center justify-center gap-2 transition-colors cursor-pointer">
        <span class="material-symbols-outlined text-sm">bookmark</span>
        <span>ARCHIVE CHAPTER CHECKPOINT</span>
      </button>
    </section>
  </main>

  <!-- ========================================================================= -->
  <!-- Bottom Footer Nav with Imprint Link (AC Mandate)                          -->
  <!-- ========================================================================= -->
  <footer id="scrolly-footer" class="border-t border-outline-variant/30 py-6 px-margin font-label-code text-label-code text-on-surface-variant flex flex-col sm:flex-row justify-between items-center gap-4">
    <div>
      <span>© {new Date().getFullYear()} Dmitry Efremov. Scrollytelling UI Matrix.</span>
    </div>
    <div class="flex items-center gap-6">
      <button
        id="scrolly-imprint-toggle-btn"
        type="button"
        on:click={toggleImprint}
        class="text-primary-fixed underline hover:text-primary transition-colors cursor-pointer"
      >
        Imprint / Реквизиты
      </button>
    </div>
  </footer>

  <!-- Imprint Legal Modal -->
  {#if showImprintModal}
    <div id="imprint-modal" role="dialog" aria-labelledby="imprint-title" class="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div class="max-w-md w-full p-6 rounded-xl border border-outline-variant/60 bg-surface-container-lowest text-on-surface space-y-4 font-label-code text-label-code shadow-2xl">
        <div class="flex justify-between items-center border-b border-outline-variant/30 pb-3">
          <h2 id="imprint-title" class="text-sm font-bold text-primary-fixed uppercase tracking-wider">Site Operator & Imprint Details</h2>
          <button
            id="imprint-close-btn"
            type="button"
            on:click={toggleImprint}
            class="px-2 py-1 border border-outline-variant/40 bg-surface-container hover:bg-surface-container-high text-on-surface rounded cursor-pointer"
          >
            ✕
          </button>
        </div>
        <div id="imprint-details-content" class="space-y-2 leading-relaxed text-body-sm text-on-surface-variant">
          <p><strong class="text-on-surface">Site Operator:</strong> Dmitry Efremov (Дмитрий Ефремов)</p>
          <p><strong class="text-on-surface">Domain:</strong> dmitryefremov.com</p>
          <p><strong class="text-on-surface">Contact Email:</strong> contact@dmitryefremov.com</p>
          <p><strong class="text-on-surface">Architecture Standard:</strong> Scrollytelling Interface without studio photos (Typography & Kinetic Schematic Vectors).</p>
          <p><strong class="text-on-surface">Legal Compliance:</strong> Zero-Cookie Telemetry & WCAG 2.1 Contrast standard (>= 4.5:1).</p>
        </div>
      </div>
    </div>
  {/if}

  <!-- Fixed Bottom Quick Bar -->
  <nav id="scrolly-bottom-nav" class="fixed bottom-0 left-0 w-full z-20 flex justify-around items-center h-14 px-margin border-t border-outline-variant/30 pb-safe bg-surface-container-lowest/90 backdrop-blur-md">
    <button aria-label="Rewind chapter" class="flex flex-col items-center justify-center text-on-surface-variant p-space-xs hover:text-primary-fixed transition-colors duration-150 active:scale-95">
      <span class="material-symbols-outlined text-[20px]">fast_rewind</span>
      <span class="font-label-code text-label-code uppercase tracking-wider text-[9px] mt-0.5">REWIND</span>
    </button>
    <button aria-label="Audio narration track" class="flex flex-col items-center justify-center text-on-surface-variant p-space-xs hover:text-primary-fixed transition-colors duration-150 active:scale-95">
      <span class="material-symbols-outlined text-[20px]">volume_up</span>
      <span class="font-label-code text-label-code uppercase tracking-wider text-[9px] mt-0.5">AUDIO</span>
    </button>
    <button aria-label="Tune story metrics" class="flex flex-col items-center justify-center text-primary-fixed p-space-xs active:scale-95 transition-transform duration-150 relative">
      <span class="material-symbols-outlined text-[22px]">tune</span>
      <span class="font-label-code text-label-code uppercase tracking-wider text-[9px] mt-0.5 font-bold">PARAMS</span>
      <span class="absolute -top-1 w-1 h-1 bg-primary-fixed rounded-full"></span>
    </button>
    <button aria-label="Bookmark chapter" class="flex flex-col items-center justify-center text-on-surface-variant p-space-xs hover:text-primary-fixed transition-colors duration-150 active:scale-95">
      <span class="material-symbols-outlined text-[20px]">bookmark</span>
      <span class="font-label-code text-label-code uppercase tracking-wider text-[9px] mt-0.5">INDEX</span>
    </button>
  </nav>
</div>
