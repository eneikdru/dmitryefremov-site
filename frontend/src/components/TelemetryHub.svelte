<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry } from '../lib/telemetry';
  import { fetchPodcastsFromApi } from '../lib/podcast';
  import ResponsivePicture from './ResponsivePicture.svelte';
  import ArticleStreamView from './ArticleStreamView.svelte';
  import PodcastPlayerView from './PodcastPlayerView.svelte';
  import SparksView from './SparksView.svelte';
  import TelemetryContactTerminalView from './TelemetryContactTerminalView.svelte';
  import SupportView from './SupportView.svelte';
  import ConsentManager from './ConsentManager.svelte';
  import SeoMeta from './SeoMeta.svelte';
  import AdminDashboardView from './AdminDashboardView.svelte';
  import CanonGatewayView from './CanonGatewayView.svelte';
  import TelemetryHudView from './TelemetryHudView.svelte';
  import CanonSearchModal, { type SearchNode } from './CanonSearchModal.svelte';
  import { currentLanguage, setLanguage, t } from '../lib/i18n';

  export let initialTab: 'podcast' | 'article' | 'sparks' | 'contact' | 'canon' | 'hud' | 'support' = 'podcast';

  let activeTab: 'podcast' | 'article' | 'sparks' | 'contact' | 'canon' | 'hud' | 'support' = initialTab;
  let theme: 'dark' | 'light' = 'dark';
  let showImprintModal: boolean = false;
  let isCmpModalOpen: boolean = false;
  let isSearchModalOpen: boolean = false;
  let currentPath: string = typeof window !== 'undefined' ? window.location.pathname : '/';
  let podcastCount: number = 0;

  let selectedArticleId: string = '01-ruth-barcan-marcus';
  let selectedEpisodeId: number | string = 1;
  let activeGraphNode: string = 'concept-deontic-logic';

  function switchTab(tab: 'podcast' | 'article' | 'sparks' | 'contact' | 'canon' | 'hud' | 'support') {
    activeTab = tab;
    if (typeof document !== 'undefined') {
      setTimeout(() => {
        const el = document.getElementById('content-view') || document.getElementById('nav-tabs');
        if (el && typeof el.scrollIntoView === 'function') {
          el.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      }, 50);
    }
  }

  function handleSelectSearchNode(node: SearchNode) {
    if (node.type === 'concept') {
      activeTab = 'canon';
      activeGraphNode = (node.targetId as string) || node.id;
    } else if (node.type === 'podcast') {
      activeTab = 'podcast';
      selectedEpisodeId = node.targetId !== undefined ? (typeof node.targetId === 'number' ? node.targetId : Number(node.targetId) || node.targetId) : 1;
    } else if (node.type === 'essay') {
      activeTab = 'article';
      selectedArticleId = (node.targetId as string) || '01-ruth-barcan-marcus';
    } else if (node.type === 'spark') {
      activeTab = 'sparks';
    } else if (node.targetTab) {
      activeTab = node.targetTab as any;
      if (node.targetTab === 'article' && node.targetId) selectedArticleId = node.targetId as string;
      if (node.targetTab === 'podcast' && node.targetId) selectedEpisodeId = typeof node.targetId === 'number' ? node.targetId : Number(node.targetId) || node.targetId;
      if (node.targetTab === 'canon' && node.targetId) activeGraphNode = node.targetId as string;
    }

    if (typeof document !== 'undefined') {
      setTimeout(() => {
        const el = document.getElementById('content-view') || document.getElementById('nav-tabs');
        if (el && typeof el.scrollIntoView === 'function') {
          el.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      }, 60);
    }
  }

  function updateRoute() {
    if (typeof window !== 'undefined') {
      currentPath = window.location.pathname;
      const hash = window.location.hash.toLowerCase();
      if (
        currentPath === '/canon-gateway' ||
        currentPath === '/canon' ||
        currentPath === '/llms.txt' ||
        currentPath === '/.well-known/llms.txt' ||
        hash === '#/canon-gateway' ||
        hash === '#canon-gateway' ||
        hash === '#canon'
      ) {
        activeTab = 'canon';
      } else if (
        currentPath === '/telemetry-hud' ||
        hash === '#/telemetry-hud' ||
        hash === '#telemetry-hud'
      ) {
        activeTab = 'hud';
      } else if (
        currentPath === '/support' ||
        hash === '#/support' ||
        hash === '#support'
      ) {
        activeTab = 'support';
      }
    }
  }

  function handleGlobalShortcut(e: KeyboardEvent) {
    if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
      e.preventDefault();
      isSearchModalOpen = !isSearchModalOpen;
    }
  }

  onMount(() => {
    window.addEventListener('keydown', handleGlobalShortcut);
    updateRoute();
    const handlePopState = () => updateRoute();
    window.addEventListener('popstate', handlePopState);

    const savedTheme = localStorage.getItem('theme-preference') as 'dark' | 'light' | null;
    if (savedTheme) {
      theme = savedTheme;
    } else {
      theme = 'dark';
    }

    fetchPodcastsFromApi().then(eps => {
      podcastCount = eps.length;
    });

    return () => {
      window.removeEventListener('popstate', handlePopState);
      window.removeEventListener('keydown', handleGlobalShortcut);
    };
  });

  function toggleTheme() {
    theme = theme === 'dark' ? 'light' : 'dark';
    try {
      localStorage.setItem('theme-preference', theme);
    } catch {}
  }

  function toggleImprint() {
    showImprintModal = !showImprintModal;
  }

  function toggleCmpModal() {
    isCmpModalOpen = !isCmpModalOpen;
  }

  function handleOutboundClick(destination: string, label: string, platform: string) {
    telemetry.trackOutboundClick(destination, label, platform);
  }

  function handleListenPodcast() {
    switchTab('podcast');
  }
</script>

{#if currentPath === '/admin' || currentPath === '/inbox' || (typeof window !== 'undefined' && (window.location.hash === '#/admin' || window.location.hash === '#/inbox' || window.location.hash === '#inbox'))}
  <AdminDashboardView {theme} />
{:else if currentPath === '/telemetry-hud' || (typeof window !== 'undefined' && (window.location.hash === '#/telemetry-hud' || window.location.hash === '#telemetry-hud')) || activeTab === 'hud'}
  <TelemetryHudView {theme} />
{:else}
  <SeoMeta
    title="Дмитрий Ефремов — Архитектура систем, философия мышления и автономные процессы"
    description="Суверенный интеллектуальный узел Дмитрия Ефремова. Выпуски подкаста «Системы и реальность», онтология автономии, A2A-экономика и эссе по деонтической логике."
    canonicalUrl="https://dmitryefremov.com"
  />

  <div
    id="telemetry-hub-root"
    class="min-h-screen w-full max-w-full overflow-x-hidden transition-colors duration-200 selection:bg-[#f2ca50] selection:text-[#0c0e12] {theme === 'dark' ? 'bg-[#090a0f] text-[#e2e8f0]' : 'bg-[#fafafa] text-[#0f172a]'}"
  >
    <!-- 1. SOVEREIGN TOPNAVBAR -->
    <header class="sticky top-0 z-50 border-b transition-colors w-full {theme === 'dark' ? 'bg-[#111318]/95 backdrop-blur-md border-white/10' : 'bg-white/95 backdrop-blur-md border-slate-200'}">
      <div class="max-w-[1280px] mx-auto px-2.5 sm:px-6 lg:px-8 h-13 sm:h-16 flex items-center justify-between gap-1 w-full">
        <!-- Wordmark Brand -->
        <a href="#top" on:click|preventDefault={() => switchTab('podcast')} class="flex flex-col group cursor-pointer shrink-0">
          <span class="font-grotesk text-xs sm:text-base font-bold tracking-tight group-hover:text-[#f2ca50] transition-colors {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
            ДМИТРИЙ ЕФРЕМОВ
          </span>
          <span class="hidden sm:inline font-mono text-[10px] tracking-wider uppercase {theme === 'dark' ? 'text-slate-400' : 'text-slate-500'}">
            {t($currentLanguage).topbar_architecture}
          </span>
        </a>

        <!-- Trailing Controls Cluster -->
        <div class="flex items-center space-x-1 sm:space-x-2 shrink-0">
          <!-- Language Selector: RU | EN | KA -->
          <div id="language-switcher" class="flex items-center p-0.5 rounded border font-mono text-[9px] sm:text-xs {theme === 'dark' ? 'bg-[#1e2024] border-white/10' : 'bg-slate-100 border-slate-300'}">
            <button
              type="button"
              id="lang-btn-ru"
              aria-label="RU"
              on:click={() => setLanguage('ru')}
              class="px-1 py-0.5 rounded transition-all cursor-pointer font-bold {$currentLanguage === 'ru' ? 'bg-[#f2ca50] text-[#0c0e12]' : (theme === 'dark' ? 'text-slate-400 hover:text-white' : 'text-slate-600 hover:text-black')}"
            >
              RU
            </button>
            <span class="text-white/20 px-0.5">|</span>
            <button
              type="button"
              id="lang-btn-en"
              aria-label="EN"
              on:click={() => setLanguage('en')}
              class="px-1 py-0.5 rounded transition-all cursor-pointer font-bold {$currentLanguage === 'en' ? 'bg-[#f2ca50] text-[#0c0e12]' : (theme === 'dark' ? 'text-slate-400 hover:text-white' : 'text-slate-600 hover:text-black')}"
            >
              EN
            </button>
            <span class="text-white/20 px-0.5">|</span>
            <button
              type="button"
              id="lang-btn-ka"
              aria-label="KA"
              on:click={() => setLanguage('ka')}
              class="px-1 py-0.5 rounded transition-all cursor-pointer font-bold {$currentLanguage === 'ka' ? 'bg-[#f2ca50] text-[#0c0e12]' : (theme === 'dark' ? 'text-slate-400 hover:text-white' : 'text-slate-600 hover:text-black')}"
            >
              KA
            </button>
          </div>

          <!-- Search Trigger -->
          <button
            id="cmd-k-trigger-btn"
            type="button"
            on:click={() => (isSearchModalOpen = true)}
            aria-label="Search"
            class="w-7 h-7 sm:w-auto sm:h-auto sm:px-2.5 sm:py-1 rounded border transition-colors flex items-center justify-center gap-1 cursor-pointer {theme === 'dark' ? 'bg-[#1e2024] text-slate-300 border-white/10 hover:text-[#f2ca50]' : 'bg-white text-slate-700 border-slate-300 hover:text-black'}"
          >
            <span class="material-symbols-outlined text-[16px] sm:text-[17px]">search</span>
            <span class="hidden sm:inline font-mono text-xs font-bold uppercase tracking-wider">Cmd+K</span>
          </button>

          <!-- Theme Toggle -->
          <button
            id="theme-toggle-btn"
            type="button"
            on:click={toggleTheme}
            aria-label="Toggle theme"
            class="w-7 h-7 sm:w-auto sm:h-auto sm:px-2 sm:py-1 rounded border font-mono text-xs font-bold uppercase transition-colors flex items-center justify-center cursor-pointer {theme === 'dark' ? 'bg-[#1e2024] text-slate-300 border-white/10 hover:text-white' : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'}"
          >
            <span class="material-symbols-outlined text-[15px] sm:text-[16px]">{theme === 'dark' ? 'dark_mode' : 'light_mode'}</span>
          </button>

          <!-- Direct Header Action: Поддержка -->
          <button
            type="button"
            on:click={() => switchTab('support')}
            class="hidden md:inline-flex items-center justify-center px-3 py-1 bg-[#f2ca50] text-[#0c0e12] font-mono text-xs font-bold uppercase tracking-wider rounded hover:bg-[#e7c364] transition-colors cursor-pointer shadow-sm"
          >
            {t($currentLanguage).nav_support}
          </button>
        </div>
      </div>
    </header>

    <main class="w-full max-w-[1280px] mx-auto px-3 sm:px-6 lg:px-8 py-5 sm:py-10 space-y-8" id="top">
      <!-- 2. MONUMENTAL HERO SECTION (Leica Optical Frame & Ergonomic Action Hierarchy) -->
      <section id="hero-section" class="grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-12 items-center">
        <!-- Studio Portrait Column (Order 1 on mobile, Order 2 on desktop) -->
        <div class="lg:col-span-5 flex justify-center order-1 lg:order-2">
          <div id="hero-avatar-container" class="relative w-full max-w-[260px] sm:max-w-[320px] lg:max-w-[380px] bg-[#11141c] border border-white/15 p-2 sm:p-2.5 rounded-2xl shadow-2xl transition-all">
            <!-- Leica optical machined rim top bar -->
            <div class="flex items-center justify-between pb-1.5 px-1 text-[9px] sm:text-[10px] font-mono text-slate-400 border-b border-white/10 mb-2">
              <span class="flex items-center gap-1.5 text-[#f2ca50] font-bold">
                <span class="w-1.5 h-1.5 rounded-full bg-[#f2ca50] animate-pulse"></span>
                LEICA 50MM F/1.4
              </span>
              <span class="tracking-widest">TBILISI // EST. 2026</span>
            </div>
            <!-- Portrait Viewport -->
            <div class="relative aspect-[3/4] w-full overflow-hidden rounded-xl bg-black border border-white/10 shadow-inner">
              <picture>
                <source type="image/avif" srcset="/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_768w.avif" />
                <source type="image/webp" srcset="/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_768w.webp" />
                <img
                  id="hero-avatar-img"
                  src="/images/portrait-hero.png"
                  alt="Дмитрий Ефремов — Официальный студийный портрет"
                  class="w-full h-full object-cover object-top hover:scale-[1.01] transition-transform duration-500 ease-out"
                  loading="eager"
                  fetchpriority="high"
                />
              </picture>
            </div>
          </div>
        </div>

        <!-- Left Column: The Philosophical Core (Order 2 on mobile, Order 1 on desktop) -->
        <div class="lg:col-span-7 space-y-4 sm:space-y-6 order-2 lg:order-1">
          <!-- Epistemic Badge -->
          <div class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded border max-w-full {theme === 'dark' ? 'bg-[#1e2024] border-white/10' : 'bg-amber-50 border-amber-300'}">
            <span class="w-2 h-2 rounded-full bg-[#f2ca50] animate-pulse shrink-0"></span>
            <span class="font-mono text-[9px] sm:text-[11px] font-bold uppercase tracking-wider break-words {theme === 'dark' ? 'text-[#f2ca50]' : 'text-amber-900'}">
              {t($currentLanguage).hero_badge}
            </span>
          </div>

          <!-- Hero Identity & Subtitle -->
          <div class="space-y-1 sm:space-y-2">
            <h1 id="hero-title" class="font-grotesk text-3xl sm:text-5xl lg:text-6xl font-extrabold uppercase tracking-tight leading-[1.0] {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
              {t($currentLanguage).hero_name}
            </h1>
            <p id="hero-subtitle" class="font-mono text-sm sm:text-lg font-semibold tracking-tight {theme === 'dark' ? 'text-[#f2ca50]' : 'text-amber-800'}">
              {t($currentLanguage).hero_tagline}
            </p>
          </div>

          <!-- Authoritative Philosophical Axiom -->
          <blockquote id="hero-manifest" class="p-3.5 sm:p-5 rounded-lg border-l-2 text-xs sm:text-base leading-relaxed break-words {theme === 'dark' ? 'bg-[#111318] border-[#f2ca50] text-[#e2e8f0]' : 'bg-slate-50 border-slate-800 text-slate-800'}">
            {t($currentLanguage).hero_manifest}
          </blockquote>

          <!-- Bio Narrative: Systems & Philosophy -->
          <p class="font-mono text-xs sm:text-sm text-slate-400 leading-relaxed max-w-2xl">
            {t($currentLanguage).hero_bio_1} {t($currentLanguage).hero_bio_2}
          </p>

          <!-- Tactile Action Cluster -->
          <div class="pt-1 flex flex-col sm:flex-row flex-wrap items-stretch sm:items-center gap-2.5 font-mono text-xs font-bold uppercase tracking-wider">
            <!-- Primary Actions Row -->
            <div class="flex items-center gap-2 w-full sm:w-auto">
              <!-- Listen Podcast Button -->
              <button
                type="button"
                id="hero-listen-podcast-btn"
                on:click={handleListenPodcast}
                class="flex-1 sm:flex-none inline-flex items-center justify-center space-x-2 px-5 py-3 bg-[#f2ca50] text-[#0c0e12] rounded hover:bg-[#e7c364] transition-all cursor-pointer shadow-md font-bold"
              >
                <span class="material-symbols-outlined text-[18px]">play_arrow</span>
                <span>{t($currentLanguage).hero_btn_podcast}</span>
              </button>

              <!-- Contact Tab Button -->
              <button
                type="button"
                id="hero-contact-btn"
                on:click={() => switchTab('contact')}
                class="inline-flex items-center justify-center space-x-2 px-4 py-3 rounded border transition-all cursor-pointer {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-slate-300 hover:text-white hover:border-white/30' : 'bg-white border-slate-300 text-slate-700 hover:bg-slate-50'}"
              >
                <span>{t($currentLanguage).nav_contact}</span>
              </button>
            </div>

            <!-- Quick Link Chips -->
            <div class="grid grid-cols-2 sm:flex sm:flex-wrap items-center gap-2 w-full">
              <!-- Telegram Link -->
              <a
                href="https://t.me/efremov_mvp"
                target="_blank"
                rel="noopener noreferrer"
                on:click={() => handleOutboundClick('https://t.me/efremov_mvp', 'Telegram @efremov_mvp', 'Telegram')}
                class="inline-flex items-center space-x-1.5 px-3 py-2 rounded border text-[11px] transition-all {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white hover:border-white/30' : 'bg-white border-slate-300 text-slate-900 hover:bg-slate-50'}"
              >
                <span>TG @efremov_mvp ↗</span>
              </a>

              <!-- YouTube Link -->
              <a
                href="https://www.youtube.com/@dmitrii.efremov"
                target="_blank"
                rel="noopener noreferrer"
                on:click={() => handleOutboundClick('https://www.youtube.com/@dmitrii.efremov', 'YouTube Channel', 'YouTube')}
                class="inline-flex items-center space-x-1.5 px-3 py-2 rounded border text-[11px] transition-all {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white hover:border-red-500/50' : 'bg-white border-slate-300 text-slate-900 hover:bg-slate-50'}"
              >
                <span>YouTube ↗</span>
              </a>

              <!-- Connect Agent Button -->
              <button
                type="button"
                id="hero-connect-agent-btn"
                on:click={() => switchTab('canon')}
                class="inline-flex items-center space-x-1.5 px-3 py-2 rounded border text-[11px] transition-all cursor-pointer {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-slate-300 hover:border-[#f2ca50]' : 'bg-white border-slate-300 text-slate-900 hover:bg-slate-50'}"
              >
                <span>{t($currentLanguage).nav_canon}</span>
              </button>

              <!-- Support Button -->
              <button
                type="button"
                on:click={() => switchTab('support')}
                class="inline-flex items-center space-x-1.5 px-3 py-2 rounded border text-[11px] transition-all cursor-pointer {theme === 'dark' ? 'bg-[#1e2024] border-[#f2ca50]/40 text-[#f2ca50] hover:bg-[#f2ca50]/10' : 'bg-amber-50 border-amber-300 text-amber-900 hover:bg-amber-100'}"
              >
                <span>{t($currentLanguage).nav_support}</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- 3. INTERACTIVE NAVIGATION TABS & FILTER BAR (With Prominent Active Highlight) -->
      <nav id="nav-tabs" aria-label="Main Navigation" class="border-y py-2 sticky top-14 sm:top-16 z-40 transition-colors backdrop-blur-md {theme === 'dark' ? 'bg-[#111318]/90 border-white/10' : 'bg-white/90 border-slate-200'}">
        <div class="flex items-center space-x-2 sm:space-x-4 overflow-x-auto no-scrollbar font-mono text-xs sm:text-sm uppercase tracking-wider">
          <!-- PODCAST TAB BUTTON -->
          <button
            id="tab-btn-podcast"
            type="button"
            on:click={() => (activeTab = 'podcast')}
            class="px-4 py-2.5 rounded transition-all cursor-pointer whitespace-nowrap {activeTab === 'podcast' ? 'bg-[#1e2024] text-[#f2ca50] border-b-2 border-[#f2ca50] font-bold shadow-md' : (theme === 'dark' ? 'text-slate-400 hover:text-white border-b-2 border-transparent hover:bg-white/[0.04]' : 'text-slate-600 hover:text-black border-b-2 border-transparent hover:bg-slate-100')}"
          >
            <span>[{t($currentLanguage).nav_podcast}]</span>
          </button>

          <!-- ESSAYS / ARTICLES TAB BUTTON -->
          <button
            id="tab-btn-article"
            type="button"
            on:click={() => (activeTab = 'article')}
            class="px-4 py-2.5 rounded transition-all cursor-pointer whitespace-nowrap {activeTab === 'article' ? 'bg-[#1e2024] text-[#f2ca50] border-b-2 border-[#f2ca50] font-bold shadow-md' : (theme === 'dark' ? 'text-slate-400 hover:text-white border-b-2 border-transparent hover:bg-white/[0.04]' : 'text-slate-600 hover:text-black border-b-2 border-transparent hover:bg-slate-100')}"
          >
            <span>[{t($currentLanguage).nav_article}]</span>
          </button>

          <!-- SPARKS / THOUGHTS TAB BUTTON -->
          <button
            id="tab-btn-sparks"
            type="button"
            on:click={() => (activeTab = 'sparks')}
            class="px-4 py-2.5 rounded transition-all cursor-pointer whitespace-nowrap {activeTab === 'sparks' ? 'bg-[#1e2024] text-[#f2ca50] border-b-2 border-[#f2ca50] font-bold shadow-md' : (theme === 'dark' ? 'text-slate-400 hover:text-white border-b-2 border-transparent hover:bg-white/[0.04]' : 'text-slate-600 hover:text-black border-b-2 border-transparent hover:bg-slate-100')}"
          >
            <span>[{t($currentLanguage).nav_sparks}]</span>
          </button>

          <!-- AGENTS TAB BUTTON -->
          <button
            id="tab-btn-canon"
            type="button"
            on:click={() => (activeTab = 'canon')}
            class="px-4 py-2.5 rounded transition-all cursor-pointer whitespace-nowrap {activeTab === 'canon' ? 'bg-[#1e2024] text-[#f2ca50] border-b-2 border-[#f2ca50] font-bold shadow-md' : (theme === 'dark' ? 'text-slate-400 hover:text-white border-b-2 border-transparent hover:bg-white/[0.04]' : 'text-slate-600 hover:text-black border-b-2 border-transparent hover:bg-slate-100')}"
          >
            <span>[{t($currentLanguage).nav_canon}]</span>
          </button>

          <!-- SUPPORT (STRIPE) TAB BUTTON -->
          <button
            id="tab-btn-support"
            type="button"
            on:click={() => (activeTab = 'support')}
            class="px-4 py-2.5 rounded transition-all cursor-pointer whitespace-nowrap {activeTab === 'support' ? 'bg-[#1e2024] text-[#f2ca50] border-b-2 border-[#f2ca50] font-bold shadow-md' : (theme === 'dark' ? 'text-slate-400 hover:text-white border-b-2 border-transparent hover:bg-white/[0.04]' : 'text-slate-600 hover:text-black border-b-2 border-transparent hover:bg-slate-100')}"
          >
            <span>[{t($currentLanguage).nav_support}]</span>
          </button>

          <!-- CONTACT TERMINAL TAB BUTTON -->
          <button
            id="tab-btn-contact"
            type="button"
            on:click={() => (activeTab = 'contact')}
            class="px-4 py-2.5 rounded transition-all cursor-pointer whitespace-nowrap {activeTab === 'contact' ? 'bg-[#1e2024] text-[#f2ca50] border-b-2 border-[#f2ca50] font-bold shadow-md' : (theme === 'dark' ? 'text-slate-400 hover:text-white border-b-2 border-transparent hover:bg-white/[0.04]' : 'text-slate-600 hover:text-black border-b-2 border-transparent hover:bg-slate-100')}"
          >
            <span>[{t($currentLanguage).nav_contact}]</span>
          </button>
        </div>
      </nav>

      <!-- 4. MAIN OPERATIONAL VIEWPORT (Seamless Reactive Switching) -->
      <section id="content-view" class="space-y-8 min-h-[500px]">
        {#if activeTab === 'podcast'}
          <div class="space-y-6">
            <PodcastPlayerView bind:selectedEpisodeId />
          </div>
        {:else if activeTab === 'article'}
          <div class="space-y-6">
            <ArticleStreamView bind:selectedArticleId />
          </div>
        {:else if activeTab === 'sparks'}
          <div class="space-y-6">
            <SparksView {theme} />
          </div>
        {:else if activeTab === 'canon'}
          <div class="space-y-6">
            <CanonGatewayView {theme} bind:activeGraphNode />
          </div>
        {:else if activeTab === 'support'}
          <div class="space-y-6">
            <SupportView {theme} />
          </div>
        {:else if activeTab === 'contact'}
          <div class="space-y-6">
            <TelemetryContactTerminalView />
          </div>
        {/if}
      </section>

      <!-- 5. ARCHITECTURAL FOOTER -->
      <footer id="main-footer" class="border-t pt-8 font-mono text-xs flex flex-col sm:flex-row justify-between items-center gap-4 {theme === 'dark' ? 'border-white/10 text-slate-400' : 'border-slate-300 text-slate-600'}">
        <div>
          <span>© {new Date().getFullYear()} {t($currentLanguage).footer_rights}</span>
        </div>
        <div class="flex items-center gap-6">
          <button
            id="cmp-persistent-link"
            type="button"
            on:click={toggleCmpModal}
            class="underline hover:text-white transition-colors cursor-pointer"
          >
            <span class="sr-only">Приватность (CMP)</span>
            <span>{t($currentLanguage).footer_privacy}</span>
          </button>
          <button
            id="imprint-toggle-btn"
            type="button"
            on:click={toggleImprint}
            class="underline hover:text-white transition-colors cursor-pointer"
          >
            {t($currentLanguage).footer_imprint}
          </button>
        </div>
      </footer>

      <!-- Imprint Modal -->
      {#if showImprintModal}
        <div id="imprint-modal" role="dialog" aria-labelledby="imprint-title" class="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div class="max-w-md w-full p-6 rounded-xl border space-y-4 font-mono text-xs {theme === 'dark' ? 'bg-[#111318] border-white/15 text-[#e2e8f0]' : 'bg-white border-slate-300 text-[#0f172a] shadow-2xl'}">
            <div class="flex justify-between items-center border-b pb-3 {theme === 'dark' ? 'border-white/10' : 'border-slate-200'}">
              <h2 id="imprint-title" class="text-sm font-bold uppercase tracking-wider">{t($currentLanguage).imprint_title}</h2>
              <button
                id="imprint-close-btn"
                type="button"
                on:click={toggleImprint}
                class="px-2 py-1 rounded border font-mono text-[10px] font-bold uppercase tracking-wider {theme === 'dark' ? 'bg-[#1e2024] border-white/10 hover:bg-[#282a2e]' : 'bg-slate-100 border-slate-300 hover:bg-slate-200'}"
              >
                CLOSE
              </button>
            </div>
            <div id="imprint-details-content" class="space-y-2 leading-relaxed">
              <p><strong>{t($currentLanguage).imprint_owner_label}:</strong> {t($currentLanguage).imprint_owner_val}</p>
              <p><strong>{t($currentLanguage).imprint_domain_label}:</strong> dmitryefremov.com</p>
              <p><strong>{t($currentLanguage).imprint_email_label}:</strong> dmitrii.eneik@gmail.com</p>
              <p><strong>{t($currentLanguage).imprint_privacy_label}:</strong> {t($currentLanguage).imprint_privacy_val}</p>
              <p><strong>{t($currentLanguage).imprint_copyright_label}:</strong> {t($currentLanguage).imprint_copyright_val}</p>
            </div>
          </div>
        </div>
      {/if}

      <ConsentManager {theme} bind:isOpen={isCmpModalOpen} />
      <CanonSearchModal
        {theme}
        bind:isOpen={isSearchModalOpen}
        onSelectNode={handleSelectSearchNode}
      />
    </main>
  </div>
{/if}
