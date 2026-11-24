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
  import { SMART_COMPANIES_EN_PATH, SMART_COMPANIES_ESSAY_ID, SMART_COMPANIES_RU_PATH } from '../lib/smart_companies_essay';

  export let initialTab: 'podcast' | 'article' | 'sparks' | 'contact' | 'canon' | 'hud' | 'support' = 'podcast';

  let activeTab: 'podcast' | 'article' | 'sparks' | 'contact' | 'canon' | 'hud' | 'support' = initialTab;
  let theme: 'dark' | 'light' = 'light';
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

  function changeLanguage(lang: 'ru' | 'en' | 'ka') {
    setLanguage(lang);
    if (typeof window !== 'undefined') {
      const segments = window.location.pathname.split('/').filter(Boolean);
      if (segments.length > 0 && (segments[0] === 'ru' || segments[0] === 'en' || segments[0] === 'ka')) {
        segments.shift();
      }
      const newPath = '/' + lang + (segments.length > 0 ? '/' + segments.join('/') : '');
      window.history.pushState({}, '', newPath);
    }
  }

  function updateRoute() {
    if (typeof window !== 'undefined') {
      currentPath = window.location.pathname;
      const hash = window.location.hash.toLowerCase();

      // Check locale prefix: /ru, /en, /ka
      const segments = currentPath.split('/').filter(Boolean);
      let detectedLang: 'ru' | 'en' | 'ka' | null = null;
      let pathSegments = [...segments];

      if (pathSegments.length > 0 && (pathSegments[0] === 'ru' || pathSegments[0] === 'en' || pathSegments[0] === 'ka')) {
        detectedLang = pathSegments[0] as 'ru' | 'en' | 'ka';
        pathSegments.shift();
      }

      if (detectedLang) {
        setLanguage(detectedLang);
      }

      const cleanPath = '/' + pathSegments.join('/');

      if (
        cleanPath === '/smart-companies-foolish-speeches' ||
        currentPath === SMART_COMPANIES_EN_PATH ||
        currentPath === SMART_COMPANIES_RU_PATH
      ) {
        activeTab = 'article';
        selectedArticleId = SMART_COMPANIES_ESSAY_ID;
        if (!detectedLang) {
          setLanguage(currentPath === SMART_COMPANIES_RU_PATH ? 'ru' : 'en');
        }
      } else if (
        cleanPath === '/essays' ||
        cleanPath.startsWith('/essays/') ||
        cleanPath.startsWith('/article/')
      ) {
        activeTab = 'article';
        const routeId = cleanPath === '/essays'
          ? ''
          : pathSegments[pathSegments.length - 1] || '';
        if (routeId) selectedArticleId = decodeURIComponent(routeId);
      } else if (
        cleanPath === '/podcast' ||
        cleanPath === '/podcasts' ||
        cleanPath.startsWith('/podcast/') ||
        cleanPath.startsWith('/podcasts/')
      ) {
        activeTab = 'podcast';
        const routeSlug = (cleanPath === '/podcast' || cleanPath === '/podcasts')
          ? ''
          : pathSegments[pathSegments.length - 1] || '';
        if (routeSlug) selectedEpisodeId = decodeURIComponent(routeSlug);
      } else if (
        cleanPath === '/canon-gateway' ||
        cleanPath === '/canon' ||
        cleanPath === '/agents' ||
        cleanPath === '/llms.txt' ||
        cleanPath === '/.well-known/llms.txt' ||
        hash === '#/canon-gateway' ||
        hash === '#canon-gateway' ||
        hash === '#canon'
      ) {
        activeTab = 'canon';
      } else if (cleanPath === '/sparks') {
        activeTab = 'sparks';
      } else if (cleanPath === '/contact') {
        activeTab = 'contact';
      } else if (cleanPath === '/support' || hash === '#/support' || hash === '#support') {
        activeTab = 'support';
      } else if (
        cleanPath === '/telemetry-hud' ||
        hash === '#/telemetry-hud' ||
        hash === '#telemetry-hud'
      ) {
        activeTab = 'hud';
      } else if (cleanPath === '' || cleanPath === '/') {
        activeTab = 'podcast';
      }
    }
  }

  function handleGlobalShortcut(e: KeyboardEvent) {
    if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
      e.preventDefault();
      isSearchModalOpen = !isSearchModalOpen;
    }
  }

  function applyDocumentTheme(nextTheme: 'dark' | 'light') {
    if (typeof document !== 'undefined') {
      document.documentElement.classList.toggle('dark', nextTheme === 'dark');
    }
  }

  onMount(() => {
    window.addEventListener('keydown', handleGlobalShortcut);
    updateRoute();
    const handlePopState = () => updateRoute();
    window.addEventListener('popstate', handlePopState);

    const savedTheme = localStorage.getItem('theme-preference') as 'dark' | 'light' | null;
    if (savedTheme === 'dark' || savedTheme === 'light') {
      theme = savedTheme;
    } else {
      theme = 'light';
    }
    applyDocumentTheme(theme);

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
    applyDocumentTheme(theme);
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
  {#if activeTab !== 'article'}
    <SeoMeta
      title="Дмитрий Ефремов — Архитектура систем, философия мышления и автономные процессы"
      description="Суверенный интеллектуальный узел Дмитрия Ефремова. Выпуски подкаста «Системы и реальность», онтология автономии, A2A-экономика и эссе по деонтической логике."
      url="https://dmitryefremov.com"
    />
  {/if}

  <div
    id="telemetry-hub-root"
    class="min-h-screen w-full max-w-full overflow-x-hidden transition-colors duration-200 {theme === 'dark' ? 'bg-[#141413] text-[#EAE6DF] selection:bg-[#C9A461] selection:text-[#141413]' : 'bg-[#FAF8F4] text-[#151514] selection:bg-[#8C2D19] selection:text-[#FAF8F4]'}"
  >
    <!-- 1. EDITORIAL MASTHEAD -->
    <header class="sticky top-0 z-50 border-b transition-colors w-full {theme === 'dark' ? 'bg-[#141413]/95 backdrop-blur-md border-white/10' : 'bg-[#FAF8F4]/95 backdrop-blur-md border-[#E2DFD7]'}">
      <div class="max-w-[1280px] mx-auto px-3 sm:px-6 lg:px-8 h-14 sm:h-16 flex items-center justify-between gap-2 w-full">
        <!-- Wordmark Brand -->
        <a href="#top" on:click|preventDefault={() => switchTab('podcast')} class="flex flex-col group cursor-pointer shrink-0">
          <span class="font-serif text-lg sm:text-2xl font-semibold tracking-[-0.02em] leading-none transition-colors {theme === 'dark' ? 'text-[#EAE6DF] group-hover:text-[#C9A461]' : 'text-[#151514] group-hover:text-[#8C2D19]'}">
            {t($currentLanguage).hero_name}
          </span>
          <span class="hidden sm:inline text-[11px] tracking-normal mt-0.5 {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
            {t($currentLanguage).topbar_architecture}
          </span>
        </a>

        <!-- Trailing Controls Cluster -->
        <div class="flex items-center space-x-1.5 sm:space-x-2 shrink-0">
          <!-- Language Selector: RU | EN | KA -->
          <div id="language-switcher" class="flex items-center p-0.5 rounded-sm border text-[11px] sm:text-xs {theme === 'dark' ? 'bg-[#1c1b18] border-white/10' : 'bg-[#F1EFE9] border-[#E2DFD7]'}">
            <button
              type="button"
              id="lang-btn-ru"
              aria-label="RU"
              on:click={() => changeLanguage('ru')}
              class="px-1.5 py-0.5 rounded-sm transition-colors cursor-pointer font-medium {$currentLanguage === 'ru' ? (theme === 'dark' ? 'text-[#C9A461] underline underline-offset-4' : 'text-[#8C2D19] underline underline-offset-4') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF]' : 'text-[#57554F] hover:text-[#151514]')}"
            >
              RU
            </button>
            <span class="px-0.5 {theme === 'dark' ? 'text-white/15' : 'text-[#D3CFC5]'}">|</span>
            <button
              type="button"
              id="lang-btn-en"
              aria-label="EN"
              on:click={() => changeLanguage('en')}
              class="px-1.5 py-0.5 rounded-sm transition-colors cursor-pointer font-medium {$currentLanguage === 'en' ? (theme === 'dark' ? 'text-[#C9A461] underline underline-offset-4' : 'text-[#8C2D19] underline underline-offset-4') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF]' : 'text-[#57554F] hover:text-[#151514]')}"
            >
              EN
            </button>
            <span class="px-0.5 {theme === 'dark' ? 'text-white/15' : 'text-[#D3CFC5]'}">|</span>
            <button
              type="button"
              id="lang-btn-ka"
              aria-label="KA"
              on:click={() => changeLanguage('ka')}
              class="px-1.5 py-0.5 rounded-sm transition-colors cursor-pointer font-medium {$currentLanguage === 'ka' ? (theme === 'dark' ? 'text-[#C9A461] underline underline-offset-4' : 'text-[#8C2D19] underline underline-offset-4') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF]' : 'text-[#57554F] hover:text-[#151514]')}"
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
            class="w-8 h-8 sm:w-auto sm:h-auto sm:px-2.5 sm:py-1 rounded-sm border transition-colors flex items-center justify-center gap-1 cursor-pointer {theme === 'dark' ? 'bg-[#1c1b18] text-[#a8a398] border-white/10 hover:text-[#C9A461]' : 'bg-[#FAF8F4] text-[#57554F] border-[#E2DFD7] hover:text-[#8C2D19] hover:border-[#8C2D19]/40'}"
          >
            <span class="material-symbols-outlined text-[16px] sm:text-[17px]">search</span>
          </button>

          <!-- Theme Toggle -->
          <button
            id="theme-toggle-btn"
            type="button"
            on:click={toggleTheme}
            aria-label="Toggle theme"
            class="w-8 h-8 sm:w-auto sm:h-auto sm:px-2.5 sm:py-1 rounded-sm border transition-colors flex items-center justify-center cursor-pointer {theme === 'dark' ? 'bg-[#1c1b18] text-[#a8a398] border-white/10 hover:text-[#EAE6DF]' : 'bg-[#FAF8F4] text-[#57554F] border-[#E2DFD7] hover:text-[#151514] hover:border-[#151514]/30'}"
          >
            <span class="material-symbols-outlined text-[16px] sm:text-[17px]">{theme === 'dark' ? 'light_mode' : 'dark_mode'}</span>
          </button>

        </div>
      </div>
    </header>

    <main class="w-full max-w-[1280px] mx-auto px-4 sm:px-6 lg:px-8 py-10 sm:py-16 space-y-12 sm:space-y-16" id="top">
      <!-- 2. EDITORIAL HERO SECTION -->
      <section id="hero-section" class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-14 items-center">
        <!-- Studio Portrait Column (Order 1 on mobile, Order 2 on desktop) -->
        <div class="lg:col-span-5 flex justify-center order-1 lg:order-2">
          <div id="hero-avatar-container" class="relative w-full max-w-[260px] sm:max-w-[320px] lg:max-w-[380px] aspect-[3/4] rounded-sm overflow-hidden border transition-all {theme === 'dark' ? 'border-white/10 bg-[#1c1b18]' : 'border-[#E2DFD7] bg-[#F1EFE9]'}">
            <picture>
              <source type="image/avif" srcset="/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_768w.avif" />
              <source type="image/webp" srcset="/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_768w.webp" />
              <img
                id="hero-avatar-img"
                src="/images/portrait-hero.png"
                alt="Дмитрий Ефремов — Официальный студийный портрет"
                class="w-full h-full object-cover object-top hover:scale-[1.02] transition-transform duration-500 ease-out"
                loading="eager"
                fetchpriority="high"
              />
            </picture>
          </div>
        </div>

        <!-- Left Column: The Philosophical Core (Order 2 on mobile, Order 1 on desktop) -->
        <div class="lg:col-span-7 space-y-5 sm:space-y-6 order-2 lg:order-1">
          <!-- Hero Identity & Subtitle -->
          <div class="space-y-1.5 sm:space-y-2">
            <h1 id="hero-title" class="font-serif text-4xl sm:text-6xl lg:text-[4.5rem] font-medium tracking-[-0.025em] leading-[1.06] {theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">
              {t($currentLanguage).hero_name}
            </h1>
            <p id="hero-subtitle" class="font-serif italic text-xl sm:text-2xl font-normal leading-snug {theme === 'dark' ? 'text-[#C9A461]' : 'text-[#8C2D19]'}">
              {t($currentLanguage).hero_tagline}
            </p>
          </div>

          <!-- Authoritative Philosophical Axiom -->
          <blockquote id="hero-manifest" class="pl-5 py-1 font-serif text-xl sm:text-2xl leading-snug border-l-2 {theme === 'dark' ? 'border-[#C9A461] text-[#EAE6DF]' : 'border-[#8C2D19] text-[#151514]'}">
            {t($currentLanguage).hero_manifest}
          </blockquote>

          <!-- Bio Narrative: Systems & Philosophy -->
          <p class="text-base sm:text-[17px] leading-[1.7] max-w-[62ch] {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
            {t($currentLanguage).hero_bio_1} {t($currentLanguage).hero_bio_2}
          </p>

          <!-- Tactile Action Cluster -->
          <div class="pt-1 flex flex-col gap-4 text-sm font-medium">
            <!-- Primary Actions Row -->
            <div class="flex items-center gap-3 w-full sm:w-auto">
              <!-- Listen Podcast Button -->
              <button
                type="button"
                id="hero-listen-podcast-btn"
                on:click={handleListenPodcast}
                class="flex-1 sm:flex-none inline-flex items-center justify-center space-x-2 px-6 py-3 rounded-sm transition-colors cursor-pointer font-medium {theme === 'dark' ? 'bg-[#C9A461] text-[#141413] hover:bg-[#d8b573]' : 'bg-[#8C2D19] text-[#FAF8F4] hover:bg-[#702313]'}"
              >
                <span class="material-symbols-outlined text-[18px]">play_arrow</span>
                <span>{t($currentLanguage).hero_btn_podcast}</span>
              </button>

              <!-- Contact Tab Button -->
              <button
                type="button"
                id="hero-contact-btn"
                on:click={() => switchTab('contact')}
                class="inline-flex items-center justify-center px-5 py-3 rounded-sm border transition-colors cursor-pointer {theme === 'dark' ? 'border-white/20 text-[#EAE6DF] hover:border-white/50' : 'border-[#151514]/25 text-[#151514] hover:border-[#8C2D19] hover:text-[#8C2D19]'}"
              >
                <span>{t($currentLanguage).nav_contact}</span>
              </button>
            </div>

            <!-- Quiet Editorial Links -->
            <div class="flex flex-wrap items-center gap-5">
              <!-- Telegram Link -->
              <a
                href="https://t.me/efremov_mvp"
                target="_blank"
                rel="noopener noreferrer"
                on:click={() => handleOutboundClick('https://t.me/efremov_mvp', 'Telegram @efremov_mvp', 'Telegram')}
                class="inline-flex items-center text-sm underline underline-offset-4 decoration-1 transition-colors {theme === 'dark' ? 'text-[#a8a398] hover:text-[#C9A461]' : 'text-[#57554F] hover:text-[#8C2D19]'}"
              >
                <span>Telegram ↗</span>
              </a>

              <!-- YouTube Link -->
              <a
                href="https://www.youtube.com/@dmitrii.efremov"
                target="_blank"
                rel="noopener noreferrer"
                on:click={() => handleOutboundClick('https://www.youtube.com/@dmitrii.efremov', 'YouTube Channel', 'YouTube')}
                class="inline-flex items-center text-sm underline underline-offset-4 decoration-1 transition-colors {theme === 'dark' ? 'text-[#a8a398] hover:text-[#C9A461]' : 'text-[#57554F] hover:text-[#8C2D19]'}"
              >
                <span>YouTube ↗</span>
              </a>

            </div>
          </div>
        </div>
      </section>

      <!-- 3. INTERACTIVE NAVIGATION TABS -->
      <nav id="nav-tabs" aria-label="Main Navigation" class="border-y py-0 sticky top-14 sm:top-16 z-40 transition-colors backdrop-blur-md {theme === 'dark' ? 'bg-[#141413]/95 border-white/10' : 'bg-[#FAF8F4]/95 border-[#E2DFD7]'}">
        <div class="flex items-center space-x-2 sm:space-x-5 overflow-x-auto no-scrollbar text-sm sm:text-[15px] tracking-normal">
          <!-- PODCAST TAB BUTTON -->
          <button
            id="tab-btn-podcast"
            type="button"
            on:click={() => (activeTab = 'podcast')}
            class="px-2 sm:px-3 py-3.5 transition-colors cursor-pointer whitespace-nowrap {activeTab === 'podcast' ? ((theme === 'dark' ? 'text-[#C9A461] border-[#C9A461]' : 'text-[#8C2D19] border-[#8C2D19]') + ' border-b-2 font-medium') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF] border-b-2 border-transparent' : 'text-[#57554F] hover:text-[#151514] border-b-2 border-transparent')}"
          >
            <span>{t($currentLanguage).nav_podcast}</span>
          </button>

          <!-- ESSAYS / ARTICLES TAB BUTTON -->
          <button
            id="tab-btn-article"
            type="button"
            on:click={() => (activeTab = 'article')}
            class="px-2 sm:px-3 py-3.5 transition-colors cursor-pointer whitespace-nowrap {activeTab === 'article' ? ((theme === 'dark' ? 'text-[#C9A461] border-[#C9A461]' : 'text-[#8C2D19] border-[#8C2D19]') + ' border-b-2 font-medium') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF] border-b-2 border-transparent' : 'text-[#57554F] hover:text-[#151514] border-b-2 border-transparent')}"
          >
            <span>{t($currentLanguage).nav_article}</span>
          </button>

          <!-- SPARKS / THOUGHTS TAB BUTTON -->
          <button
            id="tab-btn-sparks"
            type="button"
            on:click={() => (activeTab = 'sparks')}
            class="px-2 sm:px-3 py-3.5 transition-colors cursor-pointer whitespace-nowrap {activeTab === 'sparks' ? ((theme === 'dark' ? 'text-[#C9A461] border-[#C9A461]' : 'text-[#8C2D19] border-[#8C2D19]') + ' border-b-2 font-medium') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF] border-b-2 border-transparent' : 'text-[#57554F] hover:text-[#151514] border-b-2 border-transparent')}"
          >
            <span>{t($currentLanguage).nav_sparks}</span>
          </button>

          <!-- AGENTS TAB BUTTON -->
          <button
            id="tab-btn-canon"
            type="button"
            on:click={() => (activeTab = 'canon')}
            class="px-2 sm:px-3 py-3.5 transition-colors cursor-pointer whitespace-nowrap {activeTab === 'canon' ? ((theme === 'dark' ? 'text-[#C9A461] border-[#C9A461]' : 'text-[#8C2D19] border-[#8C2D19]') + ' border-b-2 font-medium') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF] border-b-2 border-transparent' : 'text-[#57554F] hover:text-[#151514] border-b-2 border-transparent')}"
          >
            <span>{t($currentLanguage).nav_canon}</span>
          </button>

          <!-- SUPPORT (STRIPE) TAB BUTTON -->
          <button
            id="tab-btn-support"
            type="button"
            on:click={() => (activeTab = 'support')}
            class="px-2 sm:px-3 py-3.5 transition-colors cursor-pointer whitespace-nowrap {activeTab === 'support' ? ((theme === 'dark' ? 'text-[#C9A461] border-[#C9A461]' : 'text-[#8C2D19] border-[#8C2D19]') + ' border-b-2 font-medium') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF] border-b-2 border-transparent' : 'text-[#57554F] hover:text-[#151514] border-b-2 border-transparent')}"
          >
            <span>{t($currentLanguage).nav_support}</span>
          </button>

          <!-- CONTACT TERMINAL TAB BUTTON -->
          <button
            id="tab-btn-contact"
            type="button"
            on:click={() => (activeTab = 'contact')}
            class="px-2 sm:px-3 py-3.5 transition-colors cursor-pointer whitespace-nowrap {activeTab === 'contact' ? ((theme === 'dark' ? 'text-[#C9A461] border-[#C9A461]' : 'text-[#8C2D19] border-[#8C2D19]') + ' border-b-2 font-medium') : (theme === 'dark' ? 'text-[#a8a398] hover:text-[#EAE6DF] border-b-2 border-transparent' : 'text-[#57554F] hover:text-[#151514] border-b-2 border-transparent')}"
          >
            <span>{t($currentLanguage).nav_contact}</span>
          </button>
        </div>
      </nav>

      <!-- 4. MAIN OPERATIONAL VIEWPORT (Seamless Reactive Switching) -->
      <section id="content-view" class="space-y-8 min-h-[500px]">
        {#if activeTab === 'podcast'}
          <div class="space-y-6">
            <PodcastPlayerView {theme} bind:selectedEpisodeId />
          </div>
        {:else if activeTab === 'article'}
          <div class="space-y-6">
            <ArticleStreamView {theme} bind:selectedArticleId />
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

      <!-- 5. EDITORIAL FOOTER -->
      <footer id="main-footer" class="border-t pt-8 font-sans text-xs flex flex-col sm:flex-row justify-between items-center gap-4 {theme === 'dark' ? 'border-white/10 text-[#a8a398]' : 'border-[#E2DFD7] text-[#57554F]'}">
        <div>
          <span>© {new Date().getFullYear()} {t($currentLanguage).footer_rights}</span>
        </div>
        <div class="flex items-center gap-6">
          <button
            id="cmp-persistent-link"
            type="button"
            on:click={toggleCmpModal}
            class="underline underline-offset-4 transition-colors cursor-pointer {theme === 'dark' ? 'hover:text-[#EAE6DF]' : 'hover:text-[#8C2D19]'}"
          >
            <span class="sr-only">Приватность (CMP)</span>
            <span>{t($currentLanguage).footer_privacy}</span>
          </button>
          <button
            id="imprint-toggle-btn"
            type="button"
            on:click={toggleImprint}
            class="underline underline-offset-4 transition-colors cursor-pointer {theme === 'dark' ? 'hover:text-[#EAE6DF]' : 'hover:text-[#8C2D19]'}"
          >
            {t($currentLanguage).footer_imprint}
          </button>
        </div>
      </footer>

      <!-- Imprint Modal -->
      {#if showImprintModal}
        <div id="imprint-modal" role="dialog" aria-labelledby="imprint-title" class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div class="max-w-md w-full p-6 rounded-sm border space-y-4 font-sans text-sm {theme === 'dark' ? 'bg-[#1c1b18] border-white/15 text-[#EAE6DF]' : 'bg-[#FAF8F4] border-[#E2DFD7] text-[#151514] shadow-xl'}">
            <div class="flex justify-between items-center border-b pb-3 {theme === 'dark' ? 'border-white/10' : 'border-[#E2DFD7]'}">
              <h2 id="imprint-title" class="font-serif text-xl font-medium">{t($currentLanguage).imprint_title}</h2>
              <button
                id="imprint-close-btn"
                type="button"
                on:click={toggleImprint}
                class="px-2.5 py-1 rounded-sm border text-xs font-medium cursor-pointer transition-colors {theme === 'dark' ? 'bg-[#141413] border-white/15 text-[#a8a398] hover:text-[#EAE6DF]' : 'bg-[#F1EFE9] border-[#E2DFD7] text-[#57554F] hover:text-[#151514]'}"
              >
                ✕
              </button>
            </div>
            <div id="imprint-details-content" class="space-y-2 text-xs leading-relaxed {theme === 'dark' ? 'text-[#a8a398]' : 'text-[#57554F]'}">
              <p><strong class="{theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">{t($currentLanguage).imprint_owner_label}:</strong> {t($currentLanguage).imprint_owner_val}</p>
              <p><strong class="{theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">{t($currentLanguage).imprint_domain_label}:</strong> dmitryefremov.com</p>
              <p><strong class="{theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">{t($currentLanguage).imprint_email_label}:</strong> dmitrii.eneik@gmail.com</p>
              <p><strong class="{theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">{t($currentLanguage).imprint_privacy_label}:</strong> {t($currentLanguage).imprint_privacy_val}</p>
              <p><strong class="{theme === 'dark' ? 'text-[#EAE6DF]' : 'text-[#151514]'}">{t($currentLanguage).imprint_copyright_label}:</strong> {t($currentLanguage).imprint_copyright_val}</p>
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
