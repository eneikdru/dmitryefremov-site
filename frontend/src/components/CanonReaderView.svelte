<script lang="ts">
  import { onMount, createEventDispatcher, onDestroy } from 'svelte';
  import { telemetry, calculateScrollPercentage } from '../lib/telemetry';

  const dispatch = createEventDispatcher();

  let isPlaying = false;
  let scrollPercent = 0;
  let scrollContainer: HTMLElement | null = null;
  let dwellMs = 0;
  let heartbeatTimer: ReturnType<typeof setInterval> | null = null;

  onMount(() => {
    // Note: Since this is the body in the mockup, but a component here,
    // we attach the scroll listener to the window if it's the main scroll container.
    const handleScroll = () => {
      const scrollTop = window.scrollY || document.documentElement.scrollTop;
      const scrollHeight = document.documentElement.scrollHeight;
      const clientHeight = document.documentElement.clientHeight;
      scrollPercent = calculateScrollPercentage(scrollTop, scrollHeight, clientHeight);
    };

    window.addEventListener('scroll', handleScroll);
    handleScroll();

    heartbeatTimer = setInterval(() => {
      dwellMs += 5000;
      telemetry.trackCanonDwellHeartbeat(dwellMs, 'canon_reader', scrollPercent, true);
    }, 5000);

    return () => {
      window.removeEventListener('scroll', handleScroll);
      if (heartbeatTimer) clearInterval(heartbeatTimer);
    };
  });

  function togglePlay() {
    isPlaying = !isPlaying;
  }

  function goBack() {
    dispatch('close');
  }
</script>

  <svelte:head>
    <title>Canon Reader - Marcus Aurelius: Meditations (Book IV)</title>
    <meta property="og:title" content="Canon Reader - Marcus Aurelius: Meditations (Book IV)" />
    <meta property="og:type" content="article" />
    <meta name="description" content="Extended Mind & Extended Epistemology - Marcus Aurelius" />
    <meta property="og:description" content="Extended Mind & Extended Epistemology - Marcus Aurelius" />
  </svelte:head>


<style>
    /* Archival reading enhancements */
    body {
      background-color: #fff8f5;
      color: #1e1b19;
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
      text-rendering: optimizeLegibility;
    }

    .drop-cap::first-letter {
      float: left;
      font-family: 'Newsreader', serif;
      font-size: 4.5rem;
      line-height: 0.82;
      padding-top: 0.35rem;
      padding-right: 0.75rem;
      padding-bottom: 0.1rem;
      color: #781f00;
      font-weight: 400;
    }

    /* Ambient paper tint highlight */
    .amber-highlight {
      background-color: rgba(255, 177, 87, 0.28);
      box-shadow: 0 0 0 2px rgba(255, 177, 87, 0.28);
      border-radius: 2px;
    }

    .pb-safe {
      padding-bottom: calc(1rem + env(safe-area-inset-bottom, 12px));
    }


    body {
      min-height: max(884px, 100dvh);
    }
  </style>

<div class="bg-background text-on-surface min-h-screen flex flex-col antialiased selection:bg-secondary-fixed selection:text-on-secondary-fixed">

<!-- ========================================================================= -->
<!-- TopAppBar (Shared Component Implementation)                               -->
<!-- ========================================================================= -->
<header class="fixed top-0 left-0 w-full z-40 flex items-center justify-between px-margin py-space-sm bg-surface/90 dark:bg-inverse-surface/90 backdrop-blur-md transition-all duration-200">
<!-- Leading Control -->
<button aria-label="Go back" class="flex items-center justify-center w-10 h-10 rounded-full hover:text-primary dark:hover:text-inverse-primary active:opacity-70 transition-colors duration-150 text-on-surface-variant dark:text-surface-variant" type="button" on:click={goBack}>
<span class="material-symbols-outlined text-[22px]" data-icon="arrow_back">arrow_back</span>
</button>
<!-- Center Headline & Context -->
<div class="flex flex-col items-center text-center">
<span class="text-label-md font-label-md font-semibold tracking-widest text-tertiary dark:text-tertiary-fixed">THE CANON</span>
<h1 class="text-headline-sm font-headline-sm text-on-surface dark:text-inverse-on-surface tracking-tight leading-none mt-0.5">
        BOOK IV: MEDITATIONS
      </h1>
<span class="text-label-sm font-label-sm text-on-surface-variant/80 mt-0.5 font-medium tracking-normal">
        18 min left · Marcus Aurelius
      </span>
</div>
<!-- Trailing Action -->
<div class="flex items-center gap-1">
<button aria-label="Typography and tone settings" class="flex items-center justify-center w-10 h-10 rounded-full hover:text-primary dark:hover:text-inverse-primary active:opacity-70 transition-colors duration-150 text-on-surface-variant dark:text-surface-variant" type="button">
<span class="material-symbols-outlined text-[22px]" data-icon="text_fields">text_fields</span>
</button>
</div>
</header>
<!-- Reading Progress Bar directly under header -->
<div class="fixed top-[60px] left-0 w-full h-[2.5px] bg-surface-container-high/60 z-40">
<div class="h-full bg-primary-container transition-all duration-300" style="width: {Math.min(Math.max(scrollPercent, 10), 100)}%"></div>
</div>
<!-- ========================================================================= -->
<!-- Main Reading Canvas (Mobile Asymmetrical Editorial Architecture)         -->
<!-- ========================================================================= -->
<main class="flex-1 w-full max-w-[680px] mx-auto px-margin pt-24 pb-36 font-body-md text-body-md text-on-surface leading-[1.85]">
<!-- Editorial Title & Chapter Frontispiece -->
<div class="pt-6 pb-10 border-b border-outline-variant/30 text-center flex flex-col items-center">
<span class="text-label-md font-label-md tracking-widest uppercase text-primary font-semibold">
        CHAPTER IV
      </span>
<h2 class="text-headline-lg-mobile font-headline-lg-mobile text-on-surface mt-3 max-w-[90%] leading-tight font-normal">
        On the Soul &amp; Transitory Nature
      </h2>
<p class="text-label-md font-label-md text-on-surface-variant mt-2 italic tracking-normal">
        Translation by Gregory Hays, with scholarly apparatus
      </p>
<!-- Classical Epigraph -->
<div class="mt-8 px-6 py-4 bg-surface-container-low/70 rounded-lg max-w-sm border border-outline-variant/20">
<p class="font-headline-sm text-headline-sm italic text-on-surface-variant text-center leading-relaxed">
          “Omnia fluxit: nihil in perpetuum manet, sed mens solum quietem praestat.”
        </p>
<span class="block text-label-sm font-label-sm text-tertiary mt-2 uppercase tracking-widest text-center">
          — Medit. Graecae IV.3
        </span>
</div>
</div>
<!-- Reading Body Text -->
<article class="mt-10 space-y-7">
<!-- Section 3 Opening with Literary Drop Cap -->
<div class="relative">
<!-- Subtle Marginal Section Number Marker -->
<span class="text-label-sm font-label-sm text-outline tracking-wider block mb-2 font-semibold select-none">
          § 3 · RETREAT INTO THE INNER CITADEL
        </span>
<p class="drop-cap text-on-surface font-body-md text-body-md leading-[1.85]">
          They seek out retreats for themselves in the countryside, by the seashore, or in the mountains, and you yourself are particularly given to craving such solitude. But all this is completely unphilosophical, when it is open to you at any moment to retreat into yourself.
        </p>
</div>
<p class="text-on-surface font-body-md text-body-md leading-[1.85]">
        For nowhere can a man find a retreat more peaceful or more free from trouble than in his own soul, especially if he has within himself the kind of thoughts which, when he contemplates them, immediately bring about complete equanimity—and by equanimity I mean nothing other than a well-ordered mind.
      </p>
<!-- Highlighted Philosophical Selection with Marginalia Card -->
<div class="my-6 pl-3 border-l-2 border-primary-container space-y-3">
<p class="font-body-md text-body-md leading-[1.85]">
<span class="amber-highlight text-on-surface font-medium">
            Constantly grant yourself this retreat, then, and renew yourself. Let your basic principles be brief and fundamental, the kind that will at once, on close acquaintance, purge all sorrow and send you back without grievance
          </span>
          to those affairs to which you must return.
        </p>
<!-- Attached Marginalia Note / Annotation Pill -->
<div class="flex items-start gap-2.5 p-3 rounded-lg bg-surface-container text-on-surface-variant border border-outline-variant/30 mt-2">
<span class="material-symbols-outlined text-[18px] text-primary shrink-0 mt-0.5" data-icon="format_quote">format_quote</span>
<div class="flex-1">
<div class="flex items-center justify-between">
<span class="text-label-sm font-label-sm font-semibold text-tertiary">SCHOLIA · THE INNER CITADEL</span>
<span class="text-label-sm font-label-sm text-outline">P. Hadot</span>
</div>
<p class="font-body-marginalia text-body-marginalia text-on-surface-variant mt-1 leading-snug">
              Pierre Hadot identifies this sanctuary as the unbreachable fortress of the mind: unaffected by outside impressions unless sanctioned by one's own judgment.
            </p>
</div>
</div>
</div>
<!-- Philosophical Exposition with Inline Footnote Anchor -->
<p class="text-on-surface font-body-md text-body-md leading-[1.85]">
        For with what are you discontented? With the wickedness of humankind? Recall to your mind the judgment that rational beings exist for one another's sake, that tolerance is part of justice, and that wrong is done involuntarily. Recall how many before you, after living in hostility, suspicion, hatred, and warfare, are now laid out in ash.
      </p>
<!-- Inline Aphorism / Pull-Quote Card -->
<figure class="my-8 py-5 px-6 rounded-lg bg-surface-container-low border border-outline-variant/30 relative">
<span class="font-headline-lg text-headline-lg text-tertiary/20 absolute -top-4 left-4 select-none">“</span>
<blockquote class="font-headline-sm text-headline-sm italic text-on-surface text-center pt-2 leading-relaxed">
          “The universe is change; our life is what our thoughts make it.”
        </blockquote>
<figcaption class="text-center mt-3 text-label-sm font-label-sm uppercase tracking-widest text-primary font-semibold">
          — Meditations IV.3.iv
        </figcaption>
</figure>
<!-- Section 4 with Footnote Term -->
<div class="relative pt-4">
<span class="text-label-sm font-label-sm text-outline tracking-wider block mb-2 font-semibold select-none">
          § 4 · THE RULING GOVERNING PRINCIPLE
        </span>
<p class="text-on-surface font-body-md text-body-md leading-[1.85]">
          If the power of thought is common to us all, then reason, by which we are rational beings, is also common. If so, then too the reason that tells us what to do and what not to do is common. If so, there is a common law; and if so, we are fellow citizens.
        </p>
<p class="text-on-surface font-body-md text-body-md leading-[1.85] mt-4">
          Therefore the cosmos is like a single commonwealth. Whence else could our common intellectual capacity derive, or our reason, or our reverence for law? Just as the terrestrial part of my nature is an offshoot of some earth, so my cognitive faculty stems from the sovereign center
          <sup class="inline-flex items-center justify-center text-primary font-semibold ml-0.5 px-1 py-0.5 rounded bg-primary-fixed text-xs cursor-pointer active:scale-95 transition-transform" title="Expand annotation">1</sup>.
        </p>
</div>
<!-- Expandable Inline Annotation Card for Terminology -->
<div class="rounded-lg bg-surface-container-high/60 border border-outline-variant/40 p-4 space-y-2 mt-3 shadow-sm">
<div class="flex items-center justify-between">
<div class="flex items-center gap-2">
<span class="text-label-sm font-label-sm font-bold text-on-primary bg-primary rounded px-1.5 py-0.5">1</span>
<span class="text-label-md font-label-md font-bold text-on-surface tracking-wide">Hēgemonikon (ἡγεμονικόν)</span>
</div>
<span class="text-label-sm font-label-sm text-tertiary font-semibold uppercase">Philosophical Term</span>
</div>
<p class="font-body-marginalia text-body-marginalia text-on-surface-variant leading-relaxed">
          Literally, <em class="font-body-marginalia">"The Ruling Center"</em> or directive faculty in Stoic psychology. The intellectual nucleus of the soul capable of assenting to or denying presentations, entirely immune to bodily illness or external ruin unless it surrenders its sovereign reason.
        </p>
</div>
<!-- Additional Prose continuation -->
<p class="text-on-surface font-body-md text-body-md leading-[1.85] pt-2">
        Nothing can come from nothing, nor can what exists vanish into non-being. What then is death? A dispensation of nature, nothing more. And nothing natural is evil. Keep this truth ever close at hand when the noise of worldly repute threatens your tranquility.
      </p>
</article>
<!-- Scholarly Colophon & Chapter Footnote -->
<footer class="mt-16 pt-8 border-t border-outline-variant/30 flex flex-col items-center text-center space-y-2">
<div class="w-8 h-px bg-outline-variant"></div>
<p class="text-label-md font-label-md text-on-surface-variant italic">
        Recorded at Sirmium and Carnuntum on the northern Danube frontier
      </p>
<p class="text-label-sm font-label-sm text-outline tracking-wider uppercase">
        End of Section IV.4 · 44 Sections Remaining in Book IV
      </p>
</footer>
</main>
<!-- ========================================================================= -->
<!-- Contextual Floating Reader HUD / Audio Bar                                -->
<!-- ========================================================================= -->
<aside aria-label="Reading Controls" class="fixed bottom-16 inset-x-0 mx-auto w-[92%] max-w-[420px] z-40">
<div class="bg-surface-container-lowest/95 dark:bg-inverse-surface/95 backdrop-blur-md rounded-full px-4 py-2.5 flex items-center justify-between shadow-lg border border-outline-variant/20">
<!-- Audio status and playback snippet -->
<div class="flex items-center gap-3">
<button aria-label="Play audio narration" class="w-8 h-8 rounded-full bg-primary text-on-primary flex items-center justify-center active:scale-95 transition-transform duration-150" type="button" on:click={togglePlay}>
<span class="material-symbols-outlined text-[18px]" data-icon={isPlaying ? 'pause' : 'play_arrow'} style="font-variation-settings: 'FILL' 1;">{isPlaying ? 'pause' : 'play_arrow'}</span>
</button>
<div class="flex flex-col text-left">
<span class="text-label-sm font-label-sm font-bold text-on-surface leading-tight">Narrated by Michael H.</span>
<div class="flex items-center gap-1.5 mt-0.5">
<!-- Simulated audio waveform micro-bars -->
<span class="w-0.5 h-2 bg-primary rounded-full animate-pulse"></span>
<span class="w-0.5 h-3.5 bg-primary rounded-full"></span>
<span class="w-0.5 h-1.5 bg-primary/60 rounded-full"></span>
<span class="w-0.5 h-3 bg-primary rounded-full"></span>
<span class="text-label-sm font-label-sm text-outline ml-1">04:12 / 18:00</span>
</div>
</div>
</div>
<!-- Quick Context Actions -->
<div class="flex items-center gap-1">
<button aria-label="Bookmark current passage" class="p-2 text-on-surface-variant hover:text-primary active:opacity-70 transition-colors" type="button">
<span class="material-symbols-outlined text-[20px]" data-icon="bookmark_add">bookmark_add</span>
</button>
<button aria-label="View scholarly footnotes" class="p-2 text-on-surface-variant hover:text-primary active:opacity-70 transition-colors" type="button">
<span class="material-symbols-outlined text-[20px]" data-icon="speaker_notes">speaker_notes</span>
</button>
</div>
</div>
</aside>
<!-- ========================================================================= -->
<!-- BottomNavBar (Shared Component Implementation)                            -->
<!-- ========================================================================= -->
<nav class="fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-margin py-space-xs pb-safe bg-surface-container-lowest/95 dark:bg-inverse-surface/95 backdrop-blur-md shadow-sm border-t border-outline-variant/10 rounded-t-xl">
<!-- Progress (Active destination for reading stream) -->
<a class="flex flex-col items-center justify-center text-primary dark:text-inverse-primary font-semibold py-1 hover:text-tertiary dark:hover:text-tertiary-fixed transition-colors duration-150 active:scale-95" href="#progress">
<span class="material-symbols-outlined text-[22px]" data-icon="menu_book" style="font-variation-settings: 'FILL' 1;">menu_book</span>
<span class="text-label-sm font-label-sm mt-0.5">Progress</span>
</a>
<!-- Notes (Inactive) -->
<a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-surface-variant py-1 hover:text-tertiary dark:hover:text-tertiary-fixed transition-colors duration-150 active:scale-95" href="#notes">
<span class="material-symbols-outlined text-[22px]" data-icon="format_quote">format_quote</span>
<span class="text-label-sm font-label-sm mt-0.5 font-medium">Notes</span>
</a>
<!-- Audio (Inactive) -->
<a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-surface-variant py-1 hover:text-tertiary dark:hover:text-tertiary-fixed transition-colors duration-150 active:scale-95" href="#audio">
<span class="material-symbols-outlined text-[22px]" data-icon="headphones">headphones</span>
<span class="text-label-sm font-label-sm mt-0.5 font-medium">Audio</span>
</a>
<!-- Format (Inactive) -->
<a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-surface-variant py-1 hover:text-tertiary dark:hover:text-tertiary-fixed transition-colors duration-150 active:scale-95" href="#format">
<span class="material-symbols-outlined text-[22px]" data-icon="tune">tune</span>
<span class="text-label-sm font-label-sm mt-0.5 font-medium">Format</span>
</a>
</nav>
<!-- Interactive Reader Micro-interaction Script -->


</div>
