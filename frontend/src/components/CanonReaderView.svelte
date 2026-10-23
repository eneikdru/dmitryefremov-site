<script lang="ts">
  import { onMount } from 'svelte';
  import {
    CANON_MANIFESTOS,
    getManifestosByDomain,
    formatTimeSeconds,
    type CanonManifesto,
    type DomainCategory
  } from '../lib/canon';

  export let initialDomain: DomainCategory = 'Philosophy of Mind';

  let selectedDomain: DomainCategory = initialDomain;
  let activeManifestoIndex: number = 0;
  let isPlayingAudio: boolean = false;
  let isAnnotationExpanded: boolean = true;
  let activeNavTab: 'progress' | 'notes' | 'audio' | 'format' = 'progress';
  let scrollProgressPercent: number = 28;

  $: availableManifestos = getManifestosByDomain(selectedDomain);
  $: currentManifesto = availableManifestos[activeManifestoIndex] || CANON_MANIFESTOS[0];

  function selectDomain(domain: DomainCategory) {
    selectedDomain = domain;
    activeManifestoIndex = 0;
  }

  function toggleAudio() {
    isPlayingAudio = !isPlayingAudio;
  }

  function toggleAnnotation() {
    isAnnotationExpanded = !isAnnotationExpanded;
  }

  function handleScroll() {
    if (typeof window === 'undefined') return;
    const scrollTop = window.scrollY || document.documentElement.scrollTop;
    const docHeight = document.documentElement.scrollHeight - window.innerHeight;
    if (docHeight > 0) {
      scrollProgressPercent = Math.min(Math.max(Math.round((scrollTop / docHeight) * 100), 5), 100);
    }
  }

  onMount(() => {
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  });
</script>

<div
  id="canon-reader-root"
  class="bg-[#fff8f5] dark:bg-[#090a0f] text-[#1e1b19] dark:text-[#e2e8f0] min-h-screen flex flex-col antialiased selection:bg-[#ffdcbc] selection:text-[#2c1700]"
>
  <!-- ========================================================================= -->
  <!-- Domain Navigation Bar (Cross-link Domain Crossings)                      -->
  <!-- ========================================================================= -->
  <nav
    id="canon-domain-nav"
    aria-label="Canon Domains"
    class="bg-[#f4ece8] dark:bg-[#12151e] border-b border-[#dec0b7]/40 dark:border-[#1f2433] px-margin py-2 sticky top-0 z-50 text-xs font-mono"
  >
    <div class="max-w-[680px] mx-auto flex flex-wrap items-center justify-between gap-2">
      <span class="text-[#781f00] dark:text-[#38bdf8] font-bold tracking-wider uppercase shrink-0">
        CANON DOMAINS:
      </span>
      <div class="flex flex-wrap gap-1.5" role="tablist">
        <button
          type="button"
          role="tab"
          aria-selected={selectedDomain === 'Autonomous Engineering'}
          class="px-2.5 py-1 rounded transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'Autonomous Engineering' ? 'bg-[#781f00] text-white font-bold dark:bg-[#38bdf8] dark:text-[#090a0f]' : 'bg-[#e9e1dd] text-[#57423c] dark:bg-[#1f2433] dark:text-[#94a3b8] hover:text-[#1e1b19] dark:hover:text-white'}"
          on:click={() => selectDomain('Autonomous Engineering')}
        >
          Autonomous Engineering
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={selectedDomain === 'Agentic Economy (A2A)'}
          class="px-2.5 py-1 rounded transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'Agentic Economy (A2A)' ? 'bg-[#781f00] text-white font-bold dark:bg-[#38bdf8] dark:text-[#090a0f]' : 'bg-[#e9e1dd] text-[#57423c] dark:bg-[#1f2433] dark:text-[#94a3b8] hover:text-[#1e1b19] dark:hover:text-white'}"
          on:click={() => selectDomain('Agentic Economy (A2A)')}
        >
          Agentic Economy (A2A)
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={selectedDomain === 'Pragmatic Business Strategy'}
          class="px-2.5 py-1 rounded transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'Pragmatic Business Strategy' ? 'bg-[#781f00] text-white font-bold dark:bg-[#38bdf8] dark:text-[#090a0f]' : 'bg-[#e9e1dd] text-[#57423c] dark:bg-[#1f2433] dark:text-[#94a3b8] hover:text-[#1e1b19] dark:hover:text-white'}"
          on:click={() => selectDomain('Pragmatic Business Strategy')}
        >
          Pragmatic Business Strategy
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={selectedDomain === 'Philosophy of Mind'}
          class="px-2.5 py-1 rounded transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedDomain === 'Philosophy of Mind' ? 'bg-[#781f00] text-white font-bold dark:bg-[#38bdf8] dark:text-[#090a0f]' : 'bg-[#e9e1dd] text-[#57423c] dark:bg-[#1f2433] dark:text-[#94a3b8] hover:text-[#1e1b19] dark:hover:text-white'}"
          on:click={() => selectDomain('Philosophy of Mind')}
        >
          Philosophy of Mind
        </button>
      </div>
    </div>
  </nav>

  <!-- ========================================================================= -->
  <!-- TopAppBar (Editorial Header Control)                                     -->
  <!-- ========================================================================= -->
  <header
    id="canon-header"
    class="flex items-center justify-between px-margin py-3 bg-[#fff8f5]/90 dark:bg-[#090a0f]/90 backdrop-blur-md transition-all border-b border-[#dec0b7]/30 dark:border-[#1f2433]"
  >
    <!-- Leading Control -->
    <button
      type="button"
      aria-label="Go back"
      class="flex items-center justify-center w-10 h-10 rounded-full text-[#57423c] dark:text-[#94a3b8] hover:text-[#781f00] dark:hover:text-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-colors"
      on:click={() => history.back()}
    >
      <span class="material-symbols-outlined text-[22px]">arrow_back</span>
    </button>

    <!-- Center Headline & Context -->
    <div class="flex flex-col items-center text-center">
      <span class="text-[11px] font-mono font-semibold tracking-widest text-[#6c2c06] dark:text-[#38bdf8] uppercase">
        THE CANON · {currentManifesto.domain}
      </span>
      <h1 id="canon-title" class="text-base sm:text-lg font-serif font-bold text-[#1e1b19] dark:text-white tracking-tight leading-none mt-0.5">
        {currentManifesto.chapterNumber}: {currentManifesto.chapterTitle.toUpperCase()}
      </h1>
      <span id="canon-meta" class="text-xs text-[#57423c] dark:text-[#94a3b8] mt-0.5 font-medium tracking-normal">
        {currentManifesto.estimatedReadTimeMinutes} min read · {currentManifesto.author}
      </span>
    </div>

    <!-- Trailing Action -->
    <div class="flex items-center gap-1">
      <button
        type="button"
        aria-label="Typography and tone settings"
        class="flex items-center justify-center w-10 h-10 rounded-full text-[#57423c] dark:text-[#94a3b8] hover:text-[#781f00] dark:hover:text-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-colors"
      >
        <span class="material-symbols-outlined text-[22px]">text_fields</span>
      </button>
    </div>
  </header>

  <!-- Dynamic Reading Progress Bar under header -->
  <div id="canon-progress-bar-container" class="w-full h-[3px] bg-[#eee7e3] dark:bg-[#1f2433]">
    <div
      id="canon-progress-bar"
      class="h-full bg-[#781f00] dark:bg-[#38bdf8] transition-all duration-200"
      style="width: {scrollProgressPercent}%"
    ></div>
  </div>

  <!-- ========================================================================= -->
  <!-- Main Reading Canvas (Editorial Architecture)                             -->
  <!-- ========================================================================= -->
  <main
    id="canon-main-canvas"
    class="flex-1 w-full max-w-[680px] mx-auto px-margin pt-8 pb-32 font-serif text-lg text-[#1e1b19] dark:text-[#cbd5e1] leading-[1.85]"
  >
    <!-- Chapter Frontispiece -->
    <header id="canon-chapter-frontispiece" class="pt-2 pb-8 border-b border-[#dec0b7]/40 dark:border-[#1f2433] text-center flex flex-col items-center">
      <span id="canon-chapter-number" class="text-xs font-mono tracking-widest uppercase text-[#781f00] dark:text-[#38bdf8] font-bold">
        {currentManifesto.chapterNumber}
      </span>
      <h2 id="canon-chapter-title" class="text-3xl sm:text-4xl text-[#1e1b19] dark:text-white mt-3 max-w-[90%] leading-tight font-serif font-normal">
        {currentManifesto.chapterTitle}
      </h2>
      <p id="canon-subtitle" class="text-xs font-mono text-[#57423c] dark:text-[#94a3b8] mt-2 italic tracking-normal">
        {currentManifesto.subtitle}
      </p>

      <!-- Classical Epigraph Card -->
      {#if currentManifesto.epigraph}
        <figure id="canon-epigraph" class="mt-6 px-6 py-4 bg-[#faf2ee] dark:bg-[#12151e] rounded-lg max-w-sm border border-[#dec0b7]/30 dark:border-[#1f2433]">
          <blockquote class="font-serif text-base italic text-[#57423c] dark:text-[#e2e8f0] text-center leading-relaxed">
            {currentManifesto.epigraph.quote}
          </blockquote>
          <figcaption id="canon-epigraph-citation" class="block text-xs font-mono text-[#6c2c06] dark:text-[#38bdf8] mt-2 uppercase tracking-widest text-center">
            {currentManifesto.epigraph.citation}
          </figcaption>
        </figure>
      {/if}
    </header>

    <!-- Reading Body Text -->
    <article id="canon-article-body" class="mt-8 space-y-8">
      {#each currentManifesto.sections as section, i}
        <section id="canon-section-{i}" class="space-y-4">
          <!-- Subtle Marginal Section Number Marker -->
          <div class="flex items-center justify-between border-b border-[#dec0b7]/20 dark:border-[#1f2433] pb-1">
            <span class="text-xs font-mono text-[#8b716a] dark:text-[#38bdf8] tracking-wider font-semibold uppercase">
              {section.sectionNumber} · {section.sectionTitle}
            </span>
          </div>

          <!-- Section Opening with Drop Cap or Prose -->
          {#if section.dropCapText}
            <p class="text-[#1e1b19] dark:text-[#cbd5e1] font-serif text-lg leading-[1.85]">
              <span class="float-left text-5xl font-serif leading-none pr-3 pt-1 text-[#781f00] dark:text-[#38bdf8] font-semibold">
                {section.dropCapText.charAt(0)}
              </span>
              {section.dropCapText.slice(1)}
            </p>
          {/if}

          {#each section.bodyParagraphs as paragraph}
            <p class="text-[#1e1b19] dark:text-[#cbd5e1] font-serif text-lg leading-[1.85]">
              {paragraph}
            </p>
          {/each}

          <!-- Highlighted Passage & Scholarly Marginalia Card -->
          {#if section.highlightedPassage}
            <div id="canon-highlight-block" class="my-6 pl-4 border-l-2 border-[#781f00] dark:border-[#38bdf8] space-y-3">
              <p class="font-serif text-lg leading-[1.85]">
                <span class="bg-[#ffdcbc]/60 dark:bg-[#38bdf8]/20 text-[#1e1b19] dark:text-white px-1 py-0.5 rounded font-medium">
                  {section.highlightedPassage.text}
                </span>
              </p>

              {#if section.highlightedPassage.scholium}
                <aside id="canon-scholia-card" aria-label="Scholia marginalia" class="flex items-start gap-3 p-3.5 rounded-lg bg-[#f4ece8] dark:bg-[#12151e] text-[#57423c] dark:text-[#cbd5e1] border border-[#dec0b7]/40 dark:border-[#1f2433]">
                  <span class="material-symbols-outlined text-[20px] text-[#781f00] dark:text-[#38bdf8] shrink-0 mt-0.5">format_quote</span>
                  <div class="flex-1">
                    <div class="flex items-center justify-between text-xs font-mono">
                      <span class="font-bold text-[#6c2c06] dark:text-[#38bdf8]">
                        {section.highlightedPassage.scholium.title}
                      </span>
                      <span class="text-[#8b716a] dark:text-[#64748b]">
                        {section.highlightedPassage.scholium.author}
                      </span>
                    </div>
                    <p class="text-xs font-serif text-[#57423c] dark:text-[#cbd5e1] mt-1 leading-relaxed italic">
                      {section.highlightedPassage.scholium.content}
                    </p>
                  </div>
                </aside>
              {/if}
            </div>
          {/if}

          <!-- Inline Pull Quote Card -->
          {#if section.pullQuote}
            <figure id="canon-pull-quote" class="my-8 py-5 px-6 rounded-lg bg-[#faf2ee] dark:bg-[#12151e] border border-[#dec0b7]/30 dark:border-[#1f2433] relative">
              <blockquote class="font-serif text-xl italic text-[#1e1b19] dark:text-white text-center leading-relaxed">
                {section.pullQuote.quote}
              </blockquote>
              <figcaption id="canon-pull-quote-citation" class="text-center mt-3 text-xs font-mono uppercase tracking-widest text-[#781f00] dark:text-[#38bdf8] font-bold">
                {section.pullQuote.citation}
              </figcaption>
            </figure>
          {/if}

          <!-- Terminology Annotation Card -->
          {#if section.annotation}
            <div id="canon-annotation-card" class="rounded-lg bg-[#eee7e3]/80 dark:bg-[#12151e] border border-[#dec0b7]/50 dark:border-[#1f2433] p-4 space-y-2 mt-4 shadow-sm">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="text-xs font-mono font-bold text-white bg-[#781f00] dark:bg-[#38bdf8] dark:text-[#090a0f] rounded px-2 py-0.5">
                    {section.annotation.id}
                  </span>
                  <span class="text-sm font-bold font-serif text-[#1e1b19] dark:text-white tracking-wide">
                    {section.annotation.term} {section.annotation.transliteration ? `(${section.annotation.transliteration})` : ''}
                  </span>
                </div>
                <button
                  type="button"
                  id="canon-annotation-toggle-btn"
                  aria-expanded={isAnnotationExpanded}
                  class="text-xs font-mono text-[#6c2c06] dark:text-[#38bdf8] font-semibold uppercase hover:underline focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
                  on:click={toggleAnnotation}
                >
                  {isAnnotationExpanded ? 'Hide Note ▲' : 'Expand Note ▼'}
                </button>
              </div>

              {#if isAnnotationExpanded}
                <p class="text-xs font-serif text-[#57423c] dark:text-[#cbd5e1] leading-relaxed pt-1">
                  {section.annotation.definition}
                </p>
              {/if}
            </div>
          {/if}
        </section>
      {/each}
    </article>

    <!-- Scholarly Colophon & Chapter Footnote -->
    <footer id="canon-colophon" class="mt-16 pt-8 border-t border-[#dec0b7]/40 dark:border-[#1f2433] flex flex-col items-center text-center space-y-2">
      <div class="w-8 h-px bg-[#8b716a] dark:bg-[#38bdf8]"></div>
      <p id="canon-location" class="text-xs font-mono text-[#57423c] dark:text-[#94a3b8] italic">
        {currentManifesto.location}
      </p>
      <p id="canon-colophon-text" class="text-xs font-mono text-[#8b716a] dark:text-[#64748b] tracking-wider uppercase">
        {currentManifesto.colophon}
      </p>
    </footer>
  </main>

  <!-- ========================================================================= -->
  <!-- Contextual Floating Reader HUD / Audio Bar                                -->
  <!-- ========================================================================= -->
  <aside
    id="canon-reader-hud"
    aria-label="Reading Controls"
    class="fixed bottom-16 inset-x-0 mx-auto w-[92%] max-w-[420px] z-40"
  >
    <div class="bg-[#fff8f5]/95 dark:bg-[#12151e]/95 backdrop-blur-md rounded-full px-4 py-2.5 flex items-center justify-between shadow-lg border border-[#dec0b7]/30 dark:border-[#1f2433]">
      <!-- Audio status and playback snippet -->
      <div class="flex items-center gap-3">
        <button
          type="button"
          id="canon-play-pause-btn"
          aria-label={isPlayingAudio ? 'Pause audio narration' : 'Play audio narration'}
          class="w-9 h-9 rounded-full bg-[#781f00] dark:bg-[#38bdf8] text-white dark:text-[#090a0f] flex items-center justify-center focus:outline-none focus:ring-2 focus:ring-[#38bdf8] active:scale-95 transition-transform shrink-0"
          on:click={toggleAudio}
        >
          <span class="material-symbols-outlined text-[20px]">
            {isPlayingAudio ? 'pause' : 'play_arrow'}
          </span>
        </button>

        <div class="flex flex-col text-left">
          <span class="text-xs font-mono font-bold text-[#1e1b19] dark:text-white leading-tight">
            Narrated by {currentManifesto.narration.narrator}
          </span>
          <div class="flex items-center gap-1.5 mt-0.5">
            <!-- Audio waveform indicators -->
            <span class="w-0.5 h-2 bg-[#781f00] dark:bg-[#38bdf8] rounded-full {isPlayingAudio ? 'animate-pulse' : ''}"></span>
            <span class="w-0.5 h-3.5 bg-[#781f00] dark:bg-[#38bdf8] rounded-full"></span>
            <span class="w-0.5 h-1.5 bg-[#781f00]/60 dark:bg-[#38bdf8]/60 rounded-full"></span>
            <span class="w-0.5 h-3 bg-[#781f00] dark:bg-[#38bdf8] rounded-full"></span>
            <span id="canon-audio-time" class="text-[11px] font-mono text-[#8b716a] dark:text-[#94a3b8] ml-1">
              {formatTimeSeconds(currentManifesto.narration.currentTimeSeconds)} / {currentManifesto.narration.durationMinutes}:00
            </span>
          </div>
        </div>
      </div>

      <!-- Quick Context Actions -->
      <div class="flex items-center gap-1">
        <button
          type="button"
          aria-label="Bookmark current passage"
          class="p-2 text-[#57423c] dark:text-[#94a3b8] hover:text-[#781f00] dark:hover:text-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-colors"
        >
          <span class="material-symbols-outlined text-[20px]">bookmark_add</span>
        </button>
        <button
          type="button"
          aria-label="View scholarly footnotes"
          class="p-2 text-[#57423c] dark:text-[#94a3b8] hover:text-[#781f00] dark:hover:text-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-colors"
        >
          <span class="material-symbols-outlined text-[20px]">speaker_notes</span>
        </button>
      </div>
    </div>
  </aside>

  <!-- ========================================================================= -->
  <!-- BottomNavBar (Shared Component Implementation)                            -->
  <!-- ========================================================================= -->
  <nav
    id="canon-bottom-nav"
    aria-label="Canon Reader Navigation"
    class="fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-margin py-2 pb-4 bg-[#fff8f5]/95 dark:bg-[#090a0f]/95 backdrop-blur-md shadow-sm border-t border-[#dec0b7]/20 dark:border-[#1f2433] rounded-t-xl"
  >
    <!-- Progress -->
    <button
      type="button"
      id="canon-nav-progress"
      aria-label="Reading progress view"
      class="flex flex-col items-center justify-center py-1 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-all {activeNavTab === 'progress' ? 'text-[#781f00] dark:text-[#38bdf8] font-bold' : 'text-[#57423c] dark:text-[#94a3b8]'}"
      on:click={() => (activeNavTab = 'progress')}
    >
      <span class="material-symbols-outlined text-[22px]">menu_book</span>
      <span class="text-xs font-mono mt-0.5">Progress</span>
    </button>

    <!-- Notes -->
    <button
      type="button"
      id="canon-nav-notes"
      aria-label="Scholarly notes view"
      class="flex flex-col items-center justify-center py-1 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-all {activeNavTab === 'notes' ? 'text-[#781f00] dark:text-[#38bdf8] font-bold' : 'text-[#57423c] dark:text-[#94a3b8]'}"
      on:click={() => (activeNavTab = 'notes')}
    >
      <span class="material-symbols-outlined text-[22px]">format_quote</span>
      <span class="text-xs font-mono mt-0.5">Notes</span>
    </button>

    <!-- Audio -->
    <button
      type="button"
      id="canon-nav-audio"
      aria-label="Audio narration settings view"
      class="flex flex-col items-center justify-center py-1 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-all {activeNavTab === 'audio' ? 'text-[#781f00] dark:text-[#38bdf8] font-bold' : 'text-[#57423c] dark:text-[#94a3b8]'}"
      on:click={() => (activeNavTab = 'audio')}
    >
      <span class="material-symbols-outlined text-[22px]">headphones</span>
      <span class="text-xs font-mono mt-0.5">Audio</span>
    </button>

    <!-- Format -->
    <button
      type="button"
      id="canon-nav-format"
      aria-label="Typography and formatting settings view"
      class="flex flex-col items-center justify-center py-1 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-all {activeNavTab === 'format' ? 'text-[#781f00] dark:text-[#38bdf8] font-bold' : 'text-[#57423c] dark:text-[#94a3b8]'}"
      on:click={() => (activeNavTab = 'format')}
    >
      <span class="material-symbols-outlined text-[22px]">tune</span>
      <span class="text-xs font-mono mt-0.5">Format</span>
    </button>
  </nav>
</div>
