<script lang="ts">
  import { onMount } from 'svelte';

  export let theme: 'dark' | 'light' = 'dark';

  let y = 0;

  // Derive animation transforms based on scroll y
  $: scrollProgress = typeof window !== 'undefined' ? Math.min(y / 400, 1) : 0;

  // Transform values for typographical elements
  $: titleTransform = `translateY(${scrollProgress * 30}px) scale(${1 - scrollProgress * 0.05})`;
  $: quoteTransform = `translateX(${scrollProgress * -20}px) opacity(${1 - scrollProgress * 0.5})`;
  $: subtitleTransform = `translateX(${scrollProgress * 20}px) opacity(${1 - scrollProgress * 0.5})`;

</script>

<svelte:window bind:scrollY={y} />

<section id="hero-section" class="relative overflow-hidden p-6 sm:p-8 sm:py-16 rounded-2xl border min-h-[400px] flex flex-col justify-center blueprint-grid {theme === 'dark' ? 'bg-[#0d0e11] border-outline-variant/30 shadow-xl' : 'bg-white border-[#e2e8f0] shadow-sm'}">

  <!-- Ambient Algorithmic Vector Overlay -->
  <div class="absolute top-10 right-[-10%] w-72 h-72 border border-outline-variant/20 rounded-full pointer-events-none opacity-40 flex items-center justify-center will-change-transform" style="transform: rotate({y * 0.1}deg) scale({1 + scrollProgress * 0.2});">
    <div class="w-52 h-52 border border-primary-fixed/20 rotate-45"></div>
    <div class="w-32 h-32 border border-secondary-container/30 rounded-full"></div>
  </div>

  <div class="flex flex-col gap-6 relative z-10 w-full max-w-4xl mx-auto will-change-transform" style="transform: translateY({scrollProgress * 20}px);">
    <!-- Top Metadata Header -->
    <div class="flex items-center gap-2 mb-2 will-change-transform" style="transform: translateY({scrollProgress * 10}px); opacity: {1 - scrollProgress * 0.8}">
      <span class="inline-block px-1.5 py-0.5 bg-surface-container-high border border-outline-variant/40 font-label-code text-label-code text-primary-fixed">001.VEC</span>
      <span class="font-label-meta text-label-meta tracking-widest text-on-surface-variant uppercase">EPISTEMOLOGY OF RAW WIREFRAME</span>
    </div>

    <!-- Hero Typographic Monument - replacing the photo -->
    <div class="flex flex-col gap-4">
      <h1 id="hero-title" class="text-display-xl-mobile md:text-display-xl font-display-xl-mobile md:font-display-xl text-primary tracking-tighter uppercase font-extrabold leading-none will-change-transform" style="transform: {titleTransform}">
        Дмитрий Ефремов
      </h1>

      <p id="hero-subtitle" class="font-body-lead text-body-lead md:text-2xl text-primary-fixed max-w-xl font-normal leading-relaxed will-change-transform" style="transform: {subtitleTransform}">
        Системы, логика мышления и автономные процессы
      </p>

      <blockquote id="hero-manifest" class="mt-4 p-4 rounded-xl border-l-4 italic font-body-md text-body-md leading-relaxed will-change-transform {theme === 'dark' ? 'bg-[#141824] border-primary-fixed text-on-surface-variant' : 'bg-[#f8fafc] border-[#0284c7] text-[#334155]'}" style="transform: {quoteTransform}">
        «Ваш код и ваши решения — это то, кто вы есть на самом деле.»
      </blockquote>

      <!-- Kinetic Callout Pill -->
      <div class="inline-flex items-center gap-2 px-3 py-1 bg-surface-container-low border border-outline-variant/50 w-fit accent-glow mt-2 will-change-transform" style="opacity: {1 - scrollProgress}">
        <span class="w-1.5 h-1.5 bg-secondary-container rounded-full"></span>
        <p class="font-label-code text-label-code text-on-surface tracking-wider uppercase">AXIOM: ARCHITECTURE WITHOUT MIMICRY</p>
      </div>
    </div>

    <!-- Quick Action Links -->
    <div class="flex flex-wrap items-center justify-start gap-3 pt-6 font-label-code text-label-code will-change-transform" style="opacity: {1 - scrollProgress * 1.5}">
      <a
        href="https://t.me/efremov_mvp"
        target="_blank"
        rel="noopener noreferrer"
        class="px-3.5 py-2 rounded-lg font-bold transition-colors flex items-center gap-1.5 bg-primary-fixed text-[#090a0f] hover:bg-surface-tint"
      >
        <span>Telegram @efremov_mvp</span>
        <span class="material-symbols-outlined text-[14px]">arrow_outward</span>
      </a>
      <a
        href="https://youtube.com/@dmitrii.efremov"
        target="_blank"
        rel="noopener noreferrer"
        class="px-3.5 py-2 rounded-lg font-bold transition-colors flex items-center gap-1.5 border {theme === 'dark' ? 'bg-[#141824] text-white border-outline-variant/50 hover:border-error' : 'bg-white text-[#0f172a] border-[#cbd5e1] hover:border-error'}"
      >
        <span class="text-error">YouTube</span>
        <span class="material-symbols-outlined text-[14px]">arrow_outward</span>
      </a>
      <button
        type="button"
        on:click
        class="px-3.5 py-2 rounded-lg transition-colors border {theme === 'dark' ? 'bg-[#141824] text-on-surface-variant border-outline-variant/50 hover:border-primary-fixed hover:text-primary-fixed' : 'bg-white text-[#334155] border-[#cbd5e1] hover:border-[#0284c7]'}"
      >
        Прямая связь & Заявки
      </button>
    </div>
  </div>
</section>
