<script lang="ts">
  import { onMount } from 'svelte';

  let isPlaying = $state(false);
  let scrollPercent = $state(0);
  let progressBar: HTMLDivElement;

  function togglePlay() {
    isPlaying = !isPlaying;
  }

  onMount(() => {
    const handleScroll = () => {
      const scrollTop = window.scrollY;
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      if (docHeight > 0) {
        scrollPercent = (scrollTop / docHeight) * 100;
        if (progressBar) {
          progressBar.style.width = `${Math.min(Math.max(scrollPercent, 10), 100)}%`;
        }
      }
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  });
</script>

<div class="bg-background text-on-surface min-h-screen flex flex-col antialiased selection:bg-secondary-fixed selection:text-on-secondary-fixed">
  <!-- TopAppBar -->
  <header class="fixed top-0 left-0 w-full z-40 flex items-center justify-between px-margin py-space-sm bg-surface/90 dark:bg-inverse-surface/90 backdrop-blur-md transition-all duration-200">
    <!-- Leading Control -->
    <button aria-label="Go back" class="flex items-center justify-center w-10 h-10 rounded-full hover:text-primary dark:hover:text-inverse-primary active:opacity-70 transition-colors duration-150 text-on-surface-variant dark:text-surface-variant" type="button">
      <span class="material-symbols-outlined text-[22px]" style="font-family: 'Material Symbols Outlined';">arrow_back</span>
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
        <span class="material-symbols-outlined text-[22px]" style="font-family: 'Material Symbols Outlined';">text_fields</span>
      </button>
    </div>
  </header>

  <!-- Reading Progress Bar -->
  <div class="fixed top-[60px] left-0 w-full h-[2.5px] bg-surface-container-high/60 z-40">
    <div bind:this={progressBar} class="h-full bg-primary-container w-[28%] transition-all duration-300"></div>
  </div>

  <!-- Main Reading Canvas -->
  <main class="flex-1 w-full max-w-[680px] mx-auto px-margin pt-24 pb-36 font-body-md text-body-md text-on-surface leading-[1.85]">
    <!-- Editorial Title & Chapter Frontispiece -->
    <div class="pt-6 pb-10 border-b border-outline-variant/30 text-center flex flex-col items-center">
      <span class="text-label-md font-label-md tracking-widest uppercase text-primary font-semibold">
        CHAPTER IV
      </span>
      <h2 class="text-headline-lg-mobile font-headline-lg-mobile text-on-surface mt-3 max-w-[90%] leading-tight font-normal">
        On the Soul &amp; Transitory Nature
      </h2>
    </div>

    <!-- Core Text Content -->
    <article class="mt-12 space-y-7">
      <div class="flex gap-4 items-start">
        <div class="flex-1">
          <p class="text-on-surface font-body-md text-body-md leading-[1.85] first-letter:float-left first-letter:font-['Newsreader'] first-letter:text-[4.5rem] first-letter:leading-[0.82] first-letter:pt-[0.35rem] first-letter:pr-[0.75rem] first-letter:pb-[0.1rem] first-letter:text-primary first-letter:font-normal">
            That which rules within, when it is according to nature, is so affected with respect to the events which happen, that it always easily adapts itself to that which is possible and is presented to it.
          </p>
          <div class="mt-6 p-4 rounded bg-surface-container/50 border-l-2 border-primary">
            <p class="text-on-surface-variant font-body-marginalia text-body-marginalia italic">
              It requires no specific material, but it moves towards its purpose under certain conditions; and it makes a material for itself out of that which opposes it, as a fire lays hold of what falls into it.
            </p>
          </div>
          <p class="mt-6 text-on-surface font-body-md text-body-md leading-[1.85]">
            A small fire would be extinguished; but when the fire is strong, it soon appropriates to itself the matter which is heaped on it, and consumes it, and rises higher by means of this very material. Let no act be done without a purpose, nor otherwise than according to the perfect principles of art.
          </p>
          <p class="mt-6 text-on-surface font-body-md text-body-md leading-[1.85]">
            Men seek retreats for themselves, houses in the country, sea-shores, and mountains; and thou too art wont to desire such things very much. But this is altogether a mark of the most common sort of men, for it is in thy power whenever thou shalt choose to retire into thyself.
          </p>
          <div class="my-8 py-5 px-6 rounded-lg bg-surface-container-low border border-outline-variant/30 relative">
            <span class="font-headline-lg text-headline-lg text-tertiary/20 absolute -top-4 left-4 select-none">“</span>
            <blockquote class="font-headline-sm text-headline-sm italic text-on-surface text-center pt-2 leading-relaxed">
              “Nowhere either with more quiet or more freedom from trouble does a man retire than into his own soul.”
            </blockquote>
            <figcaption class="text-center mt-3 text-label-sm font-label-sm uppercase tracking-widest text-primary font-semibold">
              — Meditations IV.3.i
            </figcaption>
          </div>
          <p class="mt-6 text-on-surface font-body-md text-body-md leading-[1.85]">
            Particularly when he has within him such thoughts that by looking into them he is immediately in perfect tranquility.
          </p>
        </div>
      </div>
    </article>

    <!-- Scholarly Colophon -->
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

  <!-- Contextual Floating Reader HUD -->
  <aside aria-label="Reading Controls" class="fixed bottom-16 inset-x-0 mx-auto w-[92%] max-w-[420px] z-40">
    <div class="bg-surface-container-lowest/95 dark:bg-inverse-surface/95 backdrop-blur-md rounded-full px-4 py-2.5 flex items-center justify-between shadow-lg border border-outline-variant/20">
      <div class="flex items-center gap-3">
        <button aria-label="Play audio narration" onclick={togglePlay} class="w-8 h-8 rounded-full bg-primary text-on-primary flex items-center justify-center active:scale-95 transition-transform duration-150" type="button">
          <span class="material-symbols-outlined text-[18px]" style="font-family: 'Material Symbols Outlined';">{isPlaying ? 'pause' : 'play_arrow'}</span>
        </button>
        <div class="flex flex-col text-left">
          <span class="text-label-sm font-label-sm font-bold text-on-surface leading-tight">Narrated by Michael H.</span>
          <div class="flex items-center gap-1.5 mt-0.5">
            <span class="w-0.5 h-2 bg-primary rounded-full animate-pulse"></span>
            <span class="w-0.5 h-3.5 bg-primary rounded-full"></span>
            <span class="w-0.5 h-1.5 bg-primary/60 rounded-full"></span>
            <span class="w-0.5 h-3 bg-primary rounded-full"></span>
            <span class="text-label-sm font-label-sm text-outline ml-1">04:12 / 18:00</span>
          </div>
        </div>
      </div>
      <div class="flex items-center gap-1">
        <button aria-label="Bookmark current passage" class="p-2 text-on-surface-variant hover:text-primary active:opacity-70 transition-colors" type="button">
          <span class="material-symbols-outlined text-[20px]" style="font-family: 'Material Symbols Outlined';">bookmark_add</span>
        </button>
        <button aria-label="View scholarly footnotes" class="p-2 text-on-surface-variant hover:text-primary active:opacity-70 transition-colors" type="button">
          <span class="material-symbols-outlined text-[20px]" style="font-family: 'Material Symbols Outlined';">speaker_notes</span>
        </button>
      </div>
    </div>
  </aside>

  <!-- BottomNavBar -->
  <nav class="fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-margin py-space-xs pb-[calc(1rem+env(safe-area-inset-bottom,12px))] bg-surface-container-lowest/95 dark:bg-inverse-surface/95 backdrop-blur-md shadow-sm border-t border-outline-variant/10 rounded-t-xl">
    <a class="flex flex-col items-center justify-center text-primary dark:text-inverse-primary font-semibold py-1 hover:text-tertiary dark:hover:text-tertiary-fixed transition-colors duration-150 active:scale-95" href="#progress">
      <span class="material-symbols-outlined text-[22px]" style="font-family: 'Material Symbols Outlined';">menu_book</span>
      <span class="text-label-sm font-label-sm mt-0.5">Progress</span>
    </a>
    <a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-surface-variant py-1 hover:text-tertiary dark:hover:text-tertiary-fixed transition-colors duration-150 active:scale-95" href="#notes">
      <span class="material-symbols-outlined text-[22px]" style="font-family: 'Material Symbols Outlined';">format_quote</span>
      <span class="text-label-sm font-label-sm mt-0.5 font-medium">Notes</span>
    </a>
    <a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-surface-variant py-1 hover:text-tertiary dark:hover:text-tertiary-fixed transition-colors duration-150 active:scale-95" href="#audio">
      <span class="material-symbols-outlined text-[22px]" style="font-family: 'Material Symbols Outlined';">headphones</span>
      <span class="text-label-sm font-label-sm mt-0.5 font-medium">Audio</span>
    </a>
    <a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-surface-variant py-1 hover:text-tertiary dark:hover:text-tertiary-fixed transition-colors duration-150 active:scale-95" href="#format">
      <span class="material-symbols-outlined text-[22px]" style="font-family: 'Material Symbols Outlined';">tune</span>
      <span class="text-label-sm font-label-sm mt-0.5 font-medium">Format</span>
    </a>
  </nav>
</div>