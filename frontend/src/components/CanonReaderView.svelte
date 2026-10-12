<script lang="ts">
  import { onMount } from 'svelte';
  import { DEFAULT_CANON_CHAPTER, calculateReadProgress } from '../lib/canonReader';

  export let isAudioPlaying: boolean = false;
  export let activeTab: 'progress' | 'notes' | 'audio' | 'format' = 'progress';
  export let activeAnnotationId: number | null = 1;
  export let isBookmarked: boolean = false;
  export let fontSize: 'sm' | 'md' | 'lg' = 'md';
  export let showSettingsModal: boolean = false;

  let progressPercentage: number = 28;
  let scrollElement: HTMLElement | null = null;

  function toggleAudio() {
    isAudioPlaying = !isAudioPlaying;
  }

  function toggleBookmark() {
    isBookmarked = !isBookmarked;
  }

  function toggleFootnote(id: number) {
    if (activeAnnotationId === id) {
      activeAnnotationId = null;
    } else {
      activeAnnotationId = id;
    }
  }

  function toggleSettings() {
    showSettingsModal = !showSettingsModal;
  }

  function handleScroll() {
    if (scrollElement) {
      progressPercentage = calculateReadProgress(
        scrollElement.scrollTop,
        scrollElement.scrollHeight,
        scrollElement.clientHeight
      );
    } else {
      const scrollTop = window.scrollY;
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      progressPercentage = calculateReadProgress(scrollTop, docHeight + window.innerHeight, window.innerHeight);
    }
  }

  onMount(() => {
    window.addEventListener('scroll', handleScroll);
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  });
</script>

<div
  id="canon-reader-root"
  class="bg-[#fff8f5] dark:bg-[#1e1b19] text-[#1e1b19] dark:text-[#f7efeb] min-h-screen flex flex-col font-serif antialiased selection:bg-[#ffdcbc] selection:text-[#2c1700]"
>
  <!-- Top App Bar -->
  <header
    id="header"
    class="fixed top-0 left-0 w-full z-40 flex items-center justify-between px-5 py-2 bg-[#fff8f5]/90 dark:bg-[#33302d]/90 backdrop-blur-md transition-all duration-200 border-b border-[#dec0b7]/30"
  >
    <!-- Back Control -->
    <button
      id="btn-back"
      type="button"
      aria-label="Go back to Canon overview"
      class="flex items-center justify-center w-10 h-10 rounded-full hover:bg-[#eee7e3] dark:hover:bg-[#57423c] active:opacity-70 transition-colors text-[#57423c] dark:text-[#e9e1dd] focus:outline-none focus:ring-2 focus:ring-[#781f00]"
    >
      <span class="material-symbols-outlined text-[22px]" aria-hidden="true">arrow_back</span>
    </button>

    <!-- Header Title & Context -->
    <div id="header-title-container" class="flex flex-col items-center text-center">
      <span class="text-[12px] font-sans font-semibold tracking-widest uppercase text-[#6c2c06] dark:text-[#ffdbcc]">
        THE CANON
      </span>
      <h1 id="header-main-title" class="text-[22px] font-serif font-medium text-[#1e1b19] dark:text-[#f7efeb] tracking-tight leading-none mt-0.5">
        {DEFAULT_CANON_CHAPTER.title}
      </h1>
      <span class="text-[11px] font-sans text-[#57423c]/80 dark:text-[#e9e1dd]/80 mt-0.5 font-medium tracking-normal">
        {DEFAULT_CANON_CHAPTER.estimatedMinutes} min left · Marcus Aurelius
      </span>
    </div>

    <!-- Right Action Control -->
    <div class="flex items-center gap-1">
      <button
        id="btn-typography-settings"
        type="button"
        aria-label="Typography and tone settings"
        aria-expanded={showSettingsModal}
        on:click={toggleSettings}
        class="flex items-center justify-center w-10 h-10 rounded-full hover:bg-[#eee7e3] dark:hover:bg-[#57423c] active:opacity-70 transition-colors text-[#57423c] dark:text-[#e9e1dd] focus:outline-none focus:ring-2 focus:ring-[#781f00]"
      >
        <span class="material-symbols-outlined text-[22px]" aria-hidden="true">text_fields</span>
      </button>
    </div>
  </header>

  <!-- Reading Progress Bar directly under header -->
  <div
    id="progress-bar-container"
    class="fixed top-[58px] left-0 w-full h-[3px] bg-[#eee7e3]/60 z-40"
    role="progressbar"
    aria-valuenow={progressPercentage}
    aria-valuemin={0}
    aria-valuemax={100}
    aria-label="Reading progress"
  >
    <div
      id="progress-bar-fill"
      class="h-full bg-[#9a3412] transition-all duration-300"
      style="width: {progressPercentage}%"
    ></div>
  </div>

  <!-- Main Reading Canvas -->
  <main
    id="main-reading-canvas"
    class="flex-1 w-full max-w-[680px] mx-auto px-5 pt-24 pb-36 font-serif text-[#1e1b19] dark:text-[#f7efeb] leading-[1.85] {fontSize === 'sm' ? 'text-[15px]' : fontSize === 'lg' ? 'text-[20px]' : 'text-[17px]'}"
  >
    <!-- Chapter Frontispiece -->
    <section id="chapter-frontispiece" class="pt-6 pb-10 border-b border-[#dec0b7]/30 text-center flex flex-col items-center">
      <span id="chapter-number-label" class="text-[12px] font-sans tracking-widest uppercase text-[#781f00] dark:text-[#ffb59f] font-semibold">
        CHAPTER IV
      </span>
      <h2 id="chapter-title" class="text-[28px] sm:text-[36px] font-serif text-[#1e1b19] dark:text-[#f7efeb] mt-3 max-w-[90%] leading-tight font-normal">
        {DEFAULT_CANON_CHAPTER.subtitle}
      </h2>
      <p id="chapter-translator" class="text-[12px] font-sans text-[#57423c] dark:text-[#e9e1dd] mt-2 italic tracking-normal">
        {DEFAULT_CANON_CHAPTER.translator}
      </p>

      <!-- Classical Epigraph -->
      <div id="epigraph-card" class="mt-8 px-6 py-4 bg-[#faf2ee] dark:bg-[#33302d] rounded-lg max-w-sm border border-[#dec0b7]/20">
        <p id="epigraph-text" class="font-serif text-[18px] italic text-[#57423c] dark:text-[#e9e1dd] text-center leading-relaxed">
          {DEFAULT_CANON_CHAPTER.epigraph.text}
        </p>
        <span id="epigraph-source" class="block text-[11px] font-sans text-[#6c2c06] dark:text-[#ffdbcc] mt-2 uppercase tracking-widest text-center">
          {DEFAULT_CANON_CHAPTER.epigraph.source}
        </span>
      </div>
    </section>

    <!-- Article Content -->
    <article id="reading-body-article" class="mt-10 space-y-7">
      <!-- Section 3 Opening with Drop Cap -->
      <div id="section-3-block" class="relative">
        <span id="section-3-marker" class="text-[11px] font-sans text-[#8b716a] dark:text-[#dec0b7] tracking-wider block mb-2 font-semibold select-none">
          § 3 · RETREAT INTO THE INNER CITADEL
        </span>
        <p id="section-3-p1" class="text-[#1e1b19] dark:text-[#f7efeb] leading-[1.85]">
          <span class="float-left text-[4.5rem] leading-[0.82] pt-[0.35rem] pr-[0.75rem] pb-[0.1rem] text-[#781f00] dark:text-[#ffb59f] font-normal font-serif">T</span>hey seek out retreats for themselves in the countryside, by the seashore, or in the mountains, and you yourself are particularly given to craving such solitude. But all this is completely unphilosophical, when it is open to you at any moment to retreat into yourself.
        </p>
      </div>

      <p id="section-3-p2" class="text-[#1e1b19] dark:text-[#f7efeb] leading-[1.85]">
        For nowhere can a man find a retreat more peaceful or more free from trouble than in his own soul, especially if he has within himself the kind of thoughts which, when he contemplates them, immediately bring about complete equanimity—and by equanimity I mean nothing other than a well-ordered mind.
      </p>

      <!-- Highlighted Selection with Marginalia / Scholia -->
      <div id="marginalia-container-block" class="my-6 pl-3 border-l-2 border-[#9a3412] dark:border-[#ffb59f] space-y-3">
        <p id="highlighted-text-p" class="leading-[1.85]">
          <span id="amber-highlight-span" class="bg-[#ffb157]/30 text-[#1e1b19] dark:text-[#f7efeb] font-medium rounded-sm px-1 py-0.5">
            Constantly grant yourself this retreat, then, and renew yourself. Let your basic principles be brief and fundamental, the kind that will at once, on close acquaintance, purge all sorrow and send you back without grievance
          </span>
          to those affairs to which you must return.
        </p>

        <!-- Attached Marginalia Note / Annotation Card -->
        <div id="scholia-card" class="flex items-start gap-2.5 p-3 rounded-lg bg-[#f4ece8] dark:bg-[#33302d] text-[#57423c] dark:text-[#e9e1dd] border border-[#dec0b7]/30 mt-2">
          <span class="material-symbols-outlined text-[18px] text-[#781f00] dark:text-[#ffb59f] shrink-0 mt-0.5" aria-hidden="true">format_quote</span>
          <div class="flex-1">
            <div class="flex items-center justify-between">
              <span id="scholia-title" class="text-[11px] font-sans font-semibold text-[#6c2c06] dark:text-[#ffdbcc]">
                {DEFAULT_CANON_CHAPTER.scholia.title}
              </span>
              <span id="scholia-author" class="text-[11px] font-sans text-[#8b716a] dark:text-[#dec0b7]">
                {DEFAULT_CANON_CHAPTER.scholia.author}
              </span>
            </div>
            <p id="scholia-text" class="text-[14px] text-[#57423c] dark:text-[#e9e1dd] mt-1 leading-snug">
              {DEFAULT_CANON_CHAPTER.scholia.text}
            </p>
          </div>
        </div>
      </div>

      <!-- Section Body Prose -->
      <p id="section-3-p3" class="text-[#1e1b19] dark:text-[#f7efeb] leading-[1.85]">
        For with what are you discontented? With the wickedness of humankind? Recall to your mind the judgment that rational beings exist for one another's sake, that tolerance is part of justice, and that wrong is done involuntarily. Recall how many before you, after living in hostility, suspicion, hatred, and warfare, are now laid out in ash.
      </p>

      <!-- Inline Aphorism / Pull-Quote Card -->
      <figure id="pull-quote-figure" class="my-8 py-5 px-6 rounded-lg bg-[#faf2ee] dark:bg-[#33302d] border border-[#dec0b7]/30 relative">
        <span class="text-[40px] text-[#6c2c06]/20 dark:text-[#ffdbcc]/20 absolute -top-4 left-4 select-none font-serif" aria-hidden="true">“</span>
        <blockquote id="pull-quote-text" class="font-serif text-[22px] italic text-[#1e1b19] dark:text-[#f7efeb] text-center pt-2 leading-relaxed">
          “The universe is change; our life is what our thoughts make it.”
        </blockquote>
        <figcaption id="pull-quote-caption" class="text-center mt-3 text-[11px] font-sans uppercase tracking-widest text-[#781f00] dark:text-[#ffb59f] font-semibold">
          — Meditations IV.3.iv
        </figcaption>
      </figure>

      <!-- Section 4 with Footnote Reference -->
      <div id="section-4-block" class="relative pt-4">
        <span id="section-4-marker" class="text-[11px] font-sans text-[#8b716a] dark:text-[#dec0b7] tracking-wider block mb-2 font-semibold select-none">
          § 4 · THE RULING GOVERNING PRINCIPLE
        </span>
        <p id="section-4-p1" class="text-[#1e1b19] dark:text-[#f7efeb] leading-[1.85]">
          If the power of thought is common to us all, then reason, by which we are rational beings, is also common. If so, then too the reason that tells us what to do and what not to do is common. If so, there is a common law; and if so, we are fellow citizens.
        </p>
        <p id="section-4-p2" class="text-[#1e1b19] dark:text-[#f7efeb] leading-[1.85] mt-4">
          Therefore the cosmos is like a single commonwealth. Whence else could our common intellectual capacity derive, or our reason, or our reverence for law? Just as the terrestrial part of my nature is an offshoot of some earth, so my cognitive faculty stems from the sovereign center
          <button
            id="footnote-btn-1"
            type="button"
            aria-label="Toggle footnote 1: Hegemonikon"
            aria-expanded={activeAnnotationId === 1}
            on:click={() => toggleFootnote(1)}
            class="inline-flex items-center justify-center text-[#ffffff] font-sans font-semibold ml-1 px-1.5 py-0.5 rounded bg-[#781f00] dark:bg-[#ffb59f] dark:text-[#3a0a00] text-xs cursor-pointer active:scale-95 transition-transform focus:outline-none focus:ring-2 focus:ring-[#781f00]"
          >
            1
          </button>.
        </p>
      </div>

      <!-- Expandable Footnote / Annotation Card -->
      {#if activeAnnotationId === 1}
        <div
          id="footnote-card-1"
          class="rounded-lg bg-[#eee7e3] dark:bg-[#33302d] border border-[#dec0b7]/40 p-4 space-y-2 mt-3 shadow-sm"
          role="region"
          aria-label="Footnote 1 detail"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="text-[11px] font-sans font-bold text-[#ffffff] bg-[#781f00] rounded px-1.5 py-0.5">1</span>
              <span id="footnote-term-title" class="text-[14px] font-sans font-bold text-[#1e1b19] dark:text-[#f7efeb] tracking-wide">
                {DEFAULT_CANON_CHAPTER.footnotes[1].term}
              </span>
            </div>
            <span id="footnote-category-tag" class="text-[11px] font-sans text-[#6c2c06] dark:text-[#ffdbcc] font-semibold uppercase">
              {DEFAULT_CANON_CHAPTER.footnotes[1].category}
            </span>
          </div>
          <p id="footnote-definition-text" class="text-[14px] font-serif text-[#57423c] dark:text-[#e9e1dd] leading-relaxed">
            {DEFAULT_CANON_CHAPTER.footnotes[1].definition}
          </p>
        </div>
      {/if}

      <p id="section-4-p3" class="text-[#1e1b19] dark:text-[#f7efeb] leading-[1.85] pt-2">
        Nothing can come from nothing, nor can what exists vanish into non-being. What then is death? A dispensation of nature, nothing more. And nothing natural is evil. Keep this truth ever close at hand when the noise of worldly repute threatens your tranquility.
      </p>
    </article>

    <!-- Scholarly Colophon & Footer -->
    <footer id="chapter-colophon-footer" class="mt-16 pt-8 border-t border-[#dec0b7]/30 flex flex-col items-center text-center space-y-2">
      <div class="w-8 h-px bg-[#dec0b7]"></div>
      <p id="colophon-location" class="text-[12px] font-sans text-[#57423c] dark:text-[#e9e1dd] italic">
        Recorded at Sirmium and Carnuntum on the northern Danube frontier
      </p>
      <p id="colophon-progress" class="text-[11px] font-sans text-[#8b716a] dark:text-[#dec0b7] tracking-wider uppercase">
        End of Section IV.4 · 44 Sections Remaining in Book IV
      </p>
    </footer>
  </main>

  <!-- Contextual Floating Reader HUD / Audio Bar -->
  <aside
    id="audio-hud-aside"
    aria-label="Reading Controls"
    class="fixed bottom-16 inset-x-0 mx-auto w-[92%] max-w-[420px] z-40"
  >
    <div class="bg-[#ffffff]/95 dark:bg-[#33302d]/95 backdrop-blur-md rounded-full px-4 py-2.5 flex items-center justify-between shadow-lg border border-[#dec0b7]/20">
      <!-- Audio status and playback snippet -->
      <div class="flex items-center gap-3">
        <button
          id="btn-play-audio"
          type="button"
          aria-label={isAudioPlaying ? "Pause audio narration" : "Play audio narration"}
          on:click={toggleAudio}
          class="w-9 h-9 rounded-full bg-[#781f00] text-[#ffffff] flex items-center justify-center active:scale-95 transition-transform duration-150 focus:outline-none focus:ring-2 focus:ring-[#781f00]"
        >
          <span class="material-symbols-outlined text-[20px]" aria-hidden="true">
            {isAudioPlaying ? 'pause' : 'play_arrow'}
          </span>
        </button>
        <div class="flex flex-col text-left">
          <span id="audio-narrator-title" class="text-[11px] font-sans font-bold text-[#1e1b19] dark:text-[#f7efeb] leading-tight">
            Narrated by Michael H.
          </span>
          <div class="flex items-center gap-1.5 mt-0.5">
            <!-- Simulated audio waveform micro-bars -->
            <span class="w-0.5 h-2 bg-[#781f00] dark:bg-[#ffb59f] rounded-full {isAudioPlaying ? 'animate-pulse' : ''}"></span>
            <span class="w-0.5 h-3.5 bg-[#781f00] dark:bg-[#ffb59f] rounded-full"></span>
            <span class="w-0.5 h-1.5 bg-[#781f00]/60 dark:bg-[#ffb59f]/60 rounded-full"></span>
            <span class="w-0.5 h-3 bg-[#781f00] dark:bg-[#ffb59f] rounded-full"></span>
            <span id="audio-time-counter" class="text-[11px] font-sans text-[#8b716a] dark:text-[#dec0b7] ml-1">
              04:12 / 18:00
            </span>
          </div>
        </div>
      </div>

      <!-- Quick Context Actions -->
      <div class="flex items-center gap-1">
        <button
          id="btn-bookmark"
          type="button"
          aria-label="Bookmark current passage"
          aria-pressed={isBookmarked}
          on:click={toggleBookmark}
          class="p-2 text-[#57423c] dark:text-[#e9e1dd] hover:text-[#781f00] dark:hover:text-[#ffb59f] active:opacity-70 transition-colors focus:outline-none focus:ring-2 focus:ring-[#781f00] rounded-full"
        >
          <span class="material-symbols-outlined text-[20px] {isBookmarked ? 'text-[#781f00] dark:text-[#ffb59f]' : ''}" aria-hidden="true">
            {isBookmarked ? 'bookmark' : 'bookmark_add'}
          </span>
        </button>
        <button
          id="btn-footnotes-toggle"
          type="button"
          aria-label="View scholarly footnotes"
          on:click={() => toggleFootnote(1)}
          class="p-2 text-[#57423c] dark:text-[#e9e1dd] hover:text-[#781f00] dark:hover:text-[#ffb59f] active:opacity-70 transition-colors focus:outline-none focus:ring-2 focus:ring-[#781f00] rounded-full"
        >
          <span class="material-symbols-outlined text-[20px]" aria-hidden="true">speaker_notes</span>
        </button>
      </div>
    </div>
  </aside>

  <!-- Bottom Nav Bar -->
  <nav
    id="bottom-nav-bar"
    aria-label="Reader Primary Navigation"
    class="fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-5 py-2 pb-safe bg-[#ffffff]/95 dark:bg-[#33302d]/95 backdrop-blur-md shadow-sm border-t border-[#dec0b7]/10 rounded-t-xl"
  >
    <!-- Progress (Active destination for reading stream) -->
    <a
      id="nav-link-progress"
      href="#progress"
      aria-current={activeTab === 'progress' ? 'page' : undefined}
      on:click|preventDefault={() => (activeTab = 'progress')}
      class="flex flex-col items-center justify-center {activeTab === 'progress' ? 'text-[#781f00] dark:text-[#ffb59f] font-semibold' : 'text-[#57423c] dark:text-[#e9e1dd]'} py-1 hover:text-[#6c2c06] transition-colors focus:outline-none focus:ring-2 focus:ring-[#781f00] rounded"
    >
      <span class="material-symbols-outlined text-[22px]" aria-hidden="true">menu_book</span>
      <span class="text-[11px] font-sans mt-0.5">Progress</span>
    </a>

    <!-- Notes (Inactive) -->
    <a
      id="nav-link-notes"
      href="#notes"
      aria-current={activeTab === 'notes' ? 'page' : undefined}
      on:click|preventDefault={() => (activeTab = 'notes')}
      class="flex flex-col items-center justify-center {activeTab === 'notes' ? 'text-[#781f00] dark:text-[#ffb59f] font-semibold' : 'text-[#57423c] dark:text-[#e9e1dd]'} py-1 hover:text-[#6c2c06] transition-colors focus:outline-none focus:ring-2 focus:ring-[#781f00] rounded"
    >
      <span class="material-symbols-outlined text-[22px]" aria-hidden="true">format_quote</span>
      <span class="text-[11px] font-sans mt-0.5">Notes</span>
    </a>

    <!-- Audio (Inactive) -->
    <a
      id="nav-link-audio"
      href="#audio"
      aria-current={activeTab === 'audio' ? 'page' : undefined}
      on:click|preventDefault={() => (activeTab = 'audio')}
      class="flex flex-col items-center justify-center {activeTab === 'audio' ? 'text-[#781f00] dark:text-[#ffb59f] font-semibold' : 'text-[#57423c] dark:text-[#e9e1dd]'} py-1 hover:text-[#6c2c06] transition-colors focus:outline-none focus:ring-2 focus:ring-[#781f00] rounded"
    >
      <span class="material-symbols-outlined text-[22px]" aria-hidden="true">headphones</span>
      <span class="text-[11px] font-sans mt-0.5">Audio</span>
    </a>

    <!-- Format (Inactive) -->
    <a
      id="nav-link-format"
      href="#format"
      aria-current={activeTab === 'format' ? 'page' : undefined}
      on:click|preventDefault={() => (activeTab = 'format')}
      class="flex flex-col items-center justify-center {activeTab === 'format' ? 'text-[#781f00] dark:text-[#ffb59f] font-semibold' : 'text-[#57423c] dark:text-[#e9e1dd]'} py-1 hover:text-[#6c2c06] transition-colors focus:outline-none focus:ring-2 focus:ring-[#781f00] rounded"
    >
      <span class="material-symbols-outlined text-[22px]" aria-hidden="true">tune</span>
      <span class="text-[11px] font-sans mt-0.5">Format</span>
    </a>
  </nav>

  <!-- Typography Settings Modal -->
  {#if showSettingsModal}
    <div
      id="settings-modal"
      role="dialog"
      aria-labelledby="settings-modal-title"
      class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4"
    >
      <div class="bg-[#fff8f5] dark:bg-[#33302d] text-[#1e1b19] dark:text-[#f7efeb] max-w-sm w-full p-6 rounded-lg border border-[#dec0b7]/40 shadow-xl space-y-4">
        <div class="flex justify-between items-center border-b border-[#dec0b7]/30 pb-3">
          <h3 id="settings-modal-title" class="text-[16px] font-sans font-bold uppercase text-[#781f00] dark:text-[#ffb59f]">
            Typography & Options
          </h3>
          <button
            id="btn-close-settings"
            type="button"
            aria-label="Close settings modal"
            on:click={toggleSettings}
            class="p-1 rounded hover:bg-[#eee7e3] dark:hover:bg-[#57423c]"
          >
            ✕
          </button>
        </div>

        <div class="space-y-3 font-sans text-[13px]">
          <div>
            <span id="font-size-label" class="block font-semibold mb-1 text-[#57423c] dark:text-[#e9e1dd]">
              Text Size
            </span>
            <div class="flex gap-2" role="radiogroup" aria-labelledby="font-size-label">
              <button
                type="button"
                aria-checked={fontSize === 'sm'}
                role="radio"
                on:click={() => (fontSize = 'sm')}
                class="flex-1 py-1.5 rounded border text-center font-bold {fontSize === 'sm' ? 'bg-[#781f00] text-white border-[#781f00]' : 'border-[#dec0b7] text-[#57423c]'}"
              >
                Small
              </button>
              <button
                type="button"
                aria-checked={fontSize === 'md'}
                role="radio"
                on:click={() => (fontSize = 'md')}
                class="flex-1 py-1.5 rounded border text-center font-bold {fontSize === 'md' ? 'bg-[#781f00] text-white border-[#781f00]' : 'border-[#dec0b7] text-[#57423c]'}"
              >
                Medium
              </button>
              <button
                type="button"
                aria-checked={fontSize === 'lg'}
                role="radio"
                on:click={() => (fontSize = 'lg')}
                class="flex-1 py-1.5 rounded border text-center font-bold {fontSize === 'lg' ? 'bg-[#781f00] text-white border-[#781f00]' : 'border-[#dec0b7] text-[#57423c]'}"
              >
                Large
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  {/if}
</div>
