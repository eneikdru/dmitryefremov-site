<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry } from '../lib/telemetry';
  import { fetchPodcastsFromApi } from '../lib/podcast';
  import ResponsivePicture from './ResponsivePicture.svelte';
  import ArticleStreamView from './ArticleStreamView.svelte';
  import PodcastPlayerView from './PodcastPlayerView.svelte';
  import SparksView from './SparksView.svelte';
  import TelemetryContactTerminalView from './TelemetryContactTerminalView.svelte';
  import ConsentManager from './ConsentManager.svelte';
  import SeoMeta from './SeoMeta.svelte';
  import AdminDashboardView from './AdminDashboardView.svelte';
  import CanonGatewayView from './CanonGatewayView.svelte';
  import TelemetryHudView from './TelemetryHudView.svelte';
  import CanonSearchModal, { type SearchNode } from './CanonSearchModal.svelte';
  import { currentLanguage, setLanguage, t } from '../lib/i18n';

  export let initialTab: 'podcast' | 'article' | 'sparks' | 'contact' | 'canon' | 'hud' = 'podcast';

  let activeTab: 'podcast' | 'article' | 'sparks' | 'contact' | 'canon' | 'hud' = initialTab;
  let theme: 'dark' | 'light' = 'dark';
  let showImprintModal: boolean = false;
  let isCmpModalOpen: boolean = false;
  let isSearchModalOpen: boolean = false;
  let currentPath: string = typeof window !== 'undefined' ? window.location.pathname : '/';
  let podcastCount: number = 0;

  let selectedArticleId: string = '01-ruth-barcan-marcus';
  let selectedEpisodeId: number | string = 1;
  let activeGraphNode: string = 'concept-deontic-logic';

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
      activeTab = node.targetTab;
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
    class="min-h-screen transition-colors duration-200 selection:bg-amber-400 selection:text-black {theme === 'dark' ? 'bg-[#090a0f] text-[#e2e8f0]' : 'bg-[#fafafa] text-[#0f172a]'}"
  >
    <main class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-12">
      <!-- Architectural Masthead & System Bar -->
      <header class="space-y-6">
        <div class="flex items-center justify-between gap-4 border-b pb-5 {theme === 'dark' ? 'border-white/[0.08]' : 'border-slate-200'}">
          <!-- Identity Coordinates -->
          <div class="flex items-center gap-3">
            <span id="sys-core-badge" class="sr-only">DMITRY EFREMOV</span>
            <span id="sys-domain" class="sr-only">dmitryefremov.com</span>

            <div class="flex items-center gap-2.5 font-mono text-xs tracking-wider uppercase font-semibold {theme === 'dark' ? 'text-slate-400' : 'text-slate-600'}">
              <span class="h-2 w-2 rounded-full bg-emerald-400"></span>
              <span class="text-white font-bold tracking-widest">ДМИТРИЙ ЕФРЕМОВ</span>
              <span class="opacity-30">•</span>
              <span class="hidden sm:inline">AUTONOMOUS SYSTEMS ARCHITECTURE</span>
            </div>
          </div>

          <!-- Utility Console: Language | Search | Theme -->
          <div class="flex items-center gap-2.5">
            <!-- Language Selector: RU | EN | KA -->
            <div id="language-switcher" class="flex items-center rounded-lg border font-mono text-xs overflow-hidden {theme === 'dark' ? 'border-white/10 bg-[#0e1219]' : 'border-slate-300 bg-white shadow-sm'}">
              <button
                type="button"
                id="lang-btn-ru"
                aria-label="Переключить на русский язык"
                on:click={() => setLanguage('ru')}
                class="px-2.5 py-1.5 transition-all cursor-pointer font-bold {$currentLanguage === 'ru' ? (theme === 'dark' ? 'bg-amber-400 text-black shadow-sm' : 'bg-slate-900 text-white shadow-sm') : (theme === 'dark' ? 'text-slate-400 hover:text-white' : 'text-slate-600 hover:text-black')}"
              >
                RU
              </button>
              <button
                type="button"
                id="lang-btn-en"
                aria-label="Switch to English"
                on:click={() => setLanguage('en')}
                class="px-2.5 py-1.5 transition-all cursor-pointer font-bold {$currentLanguage === 'en' ? (theme === 'dark' ? 'bg-amber-400 text-black shadow-sm' : 'bg-slate-900 text-white shadow-sm') : (theme === 'dark' ? 'text-slate-400 hover:text-white' : 'text-slate-600 hover:text-black')}"
              >
                EN
              </button>
              <button
                type="button"
                id="lang-btn-ka"
                aria-label="ქართულ ენაზე გადართვა"
                on:click={() => setLanguage('ka')}
                class="px-2.5 py-1.5 transition-all cursor-pointer font-bold {$currentLanguage === 'ka' ? (theme === 'dark' ? 'bg-amber-400 text-black shadow-sm' : 'bg-slate-900 text-white shadow-sm') : (theme === 'dark' ? 'text-slate-400 hover:text-white' : 'text-slate-600 hover:text-black')}"
              >
                KA
              </button>
            </div>

            <!-- Clean Search Button (Authoritative, Accessible) -->
            <button
              id="cmd-k-trigger-btn"
              type="button"
              on:click={() => (isSearchModalOpen = true)}
              aria-label="Поиск по Канону (Cmd+K)"
              class="px-4 py-1.5 rounded-lg text-xs font-mono font-bold tracking-widest uppercase border transition-all cursor-pointer focus:outline-none focus:ring-1 focus:ring-amber-400 {theme === 'dark' ? 'bg-[#0e1219] text-amber-300 border-white/10 hover:border-amber-400/50' : 'bg-white text-slate-900 border-slate-300 hover:border-slate-500 shadow-sm'}"
            >
              <span>ПОИСК</span>
              <span class="sr-only">Cmd+K</span>
            </button>

            <!-- Theme Toggle -->
            <button
              id="theme-toggle-btn"
              type="button"
              on:click={toggleTheme}
              aria-label="Toggle theme"
              class="px-3 py-1.5 rounded-lg text-xs font-mono font-bold uppercase tracking-wider border transition-all cursor-pointer focus:outline-none focus:ring-1 focus:ring-amber-400 {theme === 'dark' ? 'bg-[#0e1219] text-slate-300 border-white/10 hover:text-white hover:border-white/20' : 'bg-white text-slate-800 border-slate-300 hover:bg-slate-100 shadow-sm'}"
            >
              <span>{theme === 'dark' ? t($currentLanguage).theme_dark : t($currentLanguage).theme_light}</span>
            </button>
          </div>
        </div>

        <!-- Monumental Hero Section: Confident, Intellectual, Tactile -->
        <section id="hero-section" class="p-8 sm:p-10 lg:p-12 rounded-xl border transition-all relative {theme === 'dark' ? 'bg-[#0d1118] border-white/[0.09] shadow-2xl' : 'bg-white border-slate-300 shadow-sm'}">
          <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-center">

            <!-- Author Studio Portrait: Leica Optical Bezel Framing (Stitch Design Specification) -->
            <div class="lg:col-span-4 flex justify-center lg:justify-start">
              <div id="hero-avatar-container" class="relative w-full max-w-[340px] bg-[#0e1219] border border-[#1e2633] p-2.5 rounded-lg shadow-2xl">
                <!-- Optical Machined Rim Header -->
                <div class="flex items-center justify-between pb-2 px-1 border-b border-[#1e2633]/60 mb-2 font-mono text-xs text-[#94a3b8]">
                  <div class="flex items-center space-x-1.5">
                    <span class="w-2 h-2 rounded-full bg-amber-400 animate-pulse"></span>
                    <span class="text-white tracking-wider font-semibold">STATUS: ACTIVE</span>
                  </div>
                  <span class="tracking-widest text-[10px]">КУТАИСИ, ГРУЗИЯ</span>
                </div>
                <!-- Portrait Viewport -->
                <div class="relative aspect-[3/4] w-full overflow-hidden rounded bg-[#080a0e] border border-[#1e2633]">
                  <ResponsivePicture
                    id="hero-avatar-img"
                    assetKey="dmitry_efremov_studio"
                    alt="Официальный студийный портрет Дмитрия Ефремова"
                    imgClass="w-full h-full object-cover grayscale contrast-125 filter hover:scale-[1.01] transition-transform duration-500 ease-out"
                    loading="eager"
                  />
                  <!-- Optical Precision Overlay Coordinate Marks -->
                  <div class="absolute top-2.5 left-2.5 font-mono text-[10px] text-amber-300 bg-[#080a0e]/90 px-1.5 py-0.5 border border-[#1e2633] rounded">
                    E-50mm f/1.4
                  </div>
                  <div class="absolute bottom-2.5 right-2.5 font-mono text-[10px] text-[#94a3b8] bg-[#080a0e]/90 px-1.5 py-0.5 border border-[#1e2633] rounded">
                    42.2679° N, 42.7180° E
                  </div>
                </div>
                <!-- Optical Chassis Footer -->
                <div class="pt-2 px-1 flex items-center justify-between font-mono text-xs text-[#94a3b8]">
                  <span class="text-[#e2e8f0] font-mono text-[11px] font-semibold">EFREMOV // ARCHITECT</span>
                  <span class="text-[10px] tracking-wider text-[#94a3b8]">CALIBRATED MONOCHROME</span>
                </div>
              </div>
            </div>

            <!-- Monumental Typographic Manifest & Core Thesis -->
            <div class="lg:col-span-8 space-y-6 text-center lg:text-left">
              <div class="space-y-3">
                <div class="inline-flex items-center gap-2 px-3 py-1 rounded font-mono text-xs uppercase tracking-widest font-bold border {theme === 'dark' ? 'bg-amber-400/10 text-amber-300 border-amber-400/30' : 'bg-amber-50 text-amber-900 border-amber-300'}">
                  <span>АВТОНОМНЫЕ СИСТЕМЫ И ДЕЙСТВИЕ</span>
                </div>

                <h1 id="hero-title" class="text-4xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight uppercase leading-[0.95] {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
                  {t($currentLanguage).hero_name}
                </h1>

                <p id="hero-subtitle" class="text-base sm:text-xl font-mono font-semibold tracking-tight {theme === 'dark' ? 'text-amber-300' : 'text-amber-800'}">
                  {t($currentLanguage).hero_tagline}
                </p>
              </div>

              <!-- Authoritative Philosophical Axiom -->
              <blockquote id="hero-manifest" class="p-5 sm:p-6 rounded-lg border-l-2 text-base sm:text-lg leading-relaxed {theme === 'dark' ? 'bg-[#080a0e] border-amber-400 text-[#e2e8f0]' : 'bg-slate-50 border-slate-800 text-slate-800'}">
                {t($currentLanguage).hero_manifest}
              </blockquote>

              <!-- Tactile Mechanical Action Console -->
              <div class="flex flex-wrap items-center justify-center lg:justify-start gap-3 pt-2 text-xs font-mono font-bold uppercase tracking-wider">
                <button
                  type="button"
                  on:click={() => (activeTab = 'podcast')}
                  class="px-5 py-2.5 rounded-lg transition-all cursor-pointer bg-amber-400 text-black hover:bg-amber-300 shadow-md"
                >
                  СЛУШАТЬ ПОДКАСТ
                </button>

                <a
                  href="https://t.me/efremov_mvp"
                  target="_blank"
                  rel="noopener noreferrer"
                  on:click={() => handleOutboundClick('https://t.me/efremov_mvp', 'Telegram @efremov_mvp', 'Telegram')}
                  class="px-4 py-2.5 rounded-lg border transition-all flex items-center gap-2 {theme === 'dark' ? 'bg-[#12161f] text-white border-white/10 hover:border-white/30 hover:bg-[#181e2b]' : 'bg-white text-slate-900 border-slate-300 hover:bg-slate-50'}"
                >
                  <span>Telegram @efremov_mvp</span>
                </a>

                <a
                  href="https://www.youtube.com/@dmitrii.efremov"
                  target="_blank"
                  rel="noopener noreferrer"
                  on:click={() => handleOutboundClick('https://www.youtube.com/@dmitrii.efremov', 'YouTube Channel', 'YouTube')}
                  class="px-4 py-2.5 rounded-lg border transition-all flex items-center gap-2 {theme === 'dark' ? 'bg-[#12161f] text-white border-white/10 hover:border-red-500/50 hover:bg-[#181e2b]' : 'bg-white text-slate-900 border-slate-300 hover:bg-slate-50'}"
                >
                  <span>YouTube</span>
                </a>

                <button
                  type="button"
                  on:click={() => (activeTab = 'contact')}
                  class="px-4 py-2.5 rounded-lg border transition-all cursor-pointer {theme === 'dark' ? 'bg-[#12161f] text-slate-300 border-white/10 hover:text-white hover:border-white/30' : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'}"
                >
                  {$currentLanguage === 'ru' ? 'Прямая связь & Заявки' : ($currentLanguage === 'ka' ? 'პირდაპირი კავშირი და განაცხადები' : 'Direct Contact & Inquiries')}
                </button>
              </div>
</div>
          </div>
        </section>

        <!-- Architectural Navigation Grid: Numbered Operational Modules -->
        <nav id="nav-tabs" aria-label="Main Navigation" class="grid grid-cols-2 sm:grid-cols-5 gap-2 p-1.5 rounded-xl border text-xs sm:text-sm font-mono font-bold uppercase tracking-wider w-full shadow-lg {theme === 'dark' ? 'bg-[#0d1118] border-white/[0.08]' : 'bg-white border-slate-300'}">
          <button
            id="tab-btn-podcast"
            type="button"
            class="py-3 px-3 sm:px-4 rounded-lg transition-all text-center flex items-center justify-center cursor-pointer {activeTab === 'podcast' ? (theme === 'dark' ? 'bg-amber-400 text-black font-extrabold shadow' : 'bg-slate-900 text-white font-extrabold shadow') : (theme === 'dark' ? 'text-slate-400 hover:text-white hover:bg-white/5' : 'text-slate-600 hover:text-black hover:bg-slate-100')}"
            on:click={() => (activeTab = 'podcast')}
          >
            <span>{t($currentLanguage).nav_podcast}</span>
          </button>

          <button
            id="tab-btn-article"
            type="button"
            class="py-3 px-3 sm:px-4 rounded-lg transition-all text-center flex items-center justify-center cursor-pointer {activeTab === 'article' ? (theme === 'dark' ? 'bg-amber-400 text-black font-extrabold shadow' : 'bg-slate-900 text-white font-extrabold shadow') : (theme === 'dark' ? 'text-slate-400 hover:text-white hover:bg-white/5' : 'text-slate-600 hover:text-black hover:bg-slate-100')}"
            on:click={() => (activeTab = 'article')}
          >
            <span>{t($currentLanguage).nav_article}</span>
          </button>

          <button
            id="tab-btn-sparks"
            type="button"
            class="py-3 px-3 sm:px-4 rounded-lg transition-all text-center flex items-center justify-center cursor-pointer {activeTab === 'sparks' ? (theme === 'dark' ? 'bg-amber-400 text-black font-extrabold shadow' : 'bg-slate-900 text-white font-extrabold shadow') : (theme === 'dark' ? 'text-slate-400 hover:text-white hover:bg-white/5' : 'text-slate-600 hover:text-black hover:bg-slate-100')}"
            on:click={() => (activeTab = 'sparks')}
          >
            <span>{t($currentLanguage).nav_sparks}</span>
          </button>

          <button
            id="tab-btn-canon"
            type="button"
            class="py-3 px-3 sm:px-4 rounded-lg transition-all text-center flex items-center justify-center cursor-pointer {activeTab === 'canon' ? (theme === 'dark' ? 'bg-amber-400 text-black font-extrabold shadow' : 'bg-slate-900 text-white font-extrabold shadow') : (theme === 'dark' ? 'text-slate-400 hover:text-white hover:bg-white/5' : 'text-slate-600 hover:text-black hover:bg-slate-100')}"
            on:click={() => (activeTab = 'canon')}
          >
            <span>{t($currentLanguage).nav_canon}</span>
          </button>

          <button
            id="tab-btn-contact"
            type="button"
            class="col-span-2 sm:col-span-1 py-3 px-3 sm:px-4 rounded-lg transition-all text-center flex items-center justify-center cursor-pointer {activeTab === 'contact' ? (theme === 'dark' ? 'bg-amber-400 text-black font-extrabold shadow' : 'bg-slate-900 text-white font-extrabold shadow') : (theme === 'dark' ? 'text-slate-400 hover:text-white hover:bg-white/5' : 'text-slate-600 hover:text-black hover:bg-slate-100')}"
            on:click={() => (activeTab = 'contact')}
          >
            <span>{t($currentLanguage).nav_contact}</span>
          </button>
        </nav>
      </header>

      <!-- Main Operational Viewport -->
      <section id="content-view" class="space-y-8">
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
        {:else if activeTab === 'contact'}
          <div class="space-y-6">
            <TelemetryContactTerminalView />
          </div>
        {:else if activeTab === 'canon'}
          <div class="space-y-6">
            <CanonGatewayView {theme} bind:activeGraphNode />
          </div>
        {/if}
      </section>

      <!-- Architectural Sovereign Footer -->
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
          <div class="max-w-md w-full p-6 rounded-xl border space-y-4 font-mono text-xs {theme === 'dark' ? 'bg-[#0d1118] border-white/15 text-[#e2e8f0]' : 'bg-white border-slate-300 text-[#0f172a] shadow-2xl'}">
            <div class="flex justify-between items-center border-b pb-3 {theme === 'dark' ? 'border-white/10' : 'border-slate-200'}">
              <h2 id="imprint-title" class="text-sm font-bold uppercase tracking-wider">{t($currentLanguage).imprint_title}</h2>
              <button
                id="imprint-close-btn"
                type="button"
                on:click={toggleImprint}
                class="px-2 py-1 rounded border {theme === 'dark' ? 'bg-[#181e2b] border-white/10 hover:bg-[#232b3d]' : 'bg-slate-100 border-slate-300 hover:bg-slate-200'}"
              >
                ✕
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
        bind:isOpen={isSearchModalOpen}
        {theme}
        onSelectNode={handleSelectSearchNode}
      />
    </main>
  </div>
{/if}
