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

  onMount(() => {
    updateRoute();
    const handlePopState = () => updateRoute();
    window.addEventListener('popstate', handlePopState);

    const savedTheme = localStorage.getItem('theme-preference') as 'dark' | 'light' | null;
    if (savedTheme) {
      theme = savedTheme;
    }

    fetchPodcastsFromApi().then(eps => {
      podcastCount = eps.length;
    });

    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  });

  function toggleTheme() {
    theme = theme === 'dark' ? 'light' : 'dark';
    try {
      localStorage.setItem('theme-preference', theme);
    } catch {
      // Storage restricted environments
    }
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
    title="Дмитрий Ефремов — Архитектура систем, философия сознания и автономные процессы"
    description="Суверенный интеллектуальный узел Дмитрия Ефремова. Выпуски подкаста «Системы и реальность», онтология автономии, A2A-экономика и эссе по деонтической логике."
    canonicalUrl="https://dmitryefremov.com"
  />

  <div
    id="telemetry-hub-root"
    class="min-h-screen transition-colors duration-200 {theme === 'dark' ? 'bg-[#090a0f] text-[#e2e8f0]' : 'bg-[#fafafa] text-[#0f172a]'}"
  >
    <main class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-10">
      <!-- Top System & Status Bar -->
      <header class="space-y-6">
        <div class="flex items-center justify-between gap-4 border-b pb-4 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          <div class="flex items-center gap-3">
            <span
              id="sys-core-badge"
              class="sr-only"
            >
              DMITRY EFREMOV
            </span>
            <span id="sys-domain" class="sr-only">
              dmitryefremov.com
            </span>
          </div>

          <div class="flex items-center gap-3">
            <!-- Language Selector: RU | EN | KA -->
            <div id="language-switcher" class="flex items-center rounded-lg border font-mono text-xs overflow-hidden {theme === 'dark' ? 'border-[#1f2433] bg-[#12151e]' : 'border-[#cbd5e1] bg-white'}">
              <button
                type="button"
                id="lang-btn-ru"
                aria-label="Переключить на русский язык"
                on:click={() => setLanguage('ru')}
                class="px-2 py-1 transition-all cursor-pointer {$currentLanguage === 'ru' ? (theme === 'dark' ? 'bg-[#d4af37] text-[#090a0f] font-bold shadow-sm' : 'bg-[#181c26] text-white font-bold shadow-sm') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
              >
                RU
              </button>
              <button
                type="button"
                id="lang-btn-en"
                aria-label="Switch to English"
                on:click={() => setLanguage('en')}
                class="px-2 py-1 transition-all cursor-pointer {$currentLanguage === 'en' ? (theme === 'dark' ? 'bg-[#d4af37] text-[#090a0f] font-bold shadow-sm' : 'bg-[#181c26] text-white font-bold shadow-sm') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
              >
                EN
              </button>
              <button
                type="button"
                id="lang-btn-ka"
                aria-label="ქართულ ენაზე გადართვა"
                on:click={() => setLanguage('ka')}
                class="px-2 py-1 transition-all cursor-pointer {$currentLanguage === 'ka' ? (theme === 'dark' ? 'bg-[#d4af37] text-[#090a0f] font-bold shadow-sm' : 'bg-[#181c26] text-white font-bold shadow-sm') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
              >
                KA
              </button>
            </div>

            <!-- Cmd+K Search Trigger -->
            <button
              id="cmd-k-trigger-btn"
              type="button"
              on:click={() => (isSearchModalOpen = true)}
              aria-label="Поиск по Канону (Cmd+K)"
              class="px-3 py-1.5 rounded text-xs font-mono border transition-all flex items-center gap-2 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#d4af37] {theme === 'dark' ? 'bg-[#12151d] text-[#dfe2ef] border-[#1f2433] hover:border-[#d4af37]/60' : 'bg-white text-[#0f172a] border-[#cbd5e1] hover:border-[#0284c7] shadow-sm'}"
            >
              <span class="text-[#d4af37]">🔍</span>
              <span>{t($currentLanguage).search_btn}</span>
              <kbd class="hidden sm:inline-block px-1.5 py-0.5 text-[10px] rounded font-mono border text-[#849495] {theme === 'dark' ? 'bg-[#0a0e17] border-[#3b494b]/40 text-[#00f0ff]' : 'bg-[#e2e8f0] border-[#cbd5e1]'}">Cmd+K</kbd>
            </button>

            <!-- Theme Toggle -->
            <button
              id="theme-toggle-btn"
              type="button"
              on:click={toggleTheme}
              aria-label="Toggle theme"
              class="px-2.5 py-1.5 rounded-lg text-xs font-mono border transition-all flex items-center gap-2 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#d4af37] {theme === 'dark' ? 'bg-[#12151d] text-[#cbd5e1] border-[#1f2433] hover:border-[#d4af37]/60' : 'bg-white text-[#334155] border-[#cbd5e1] hover:border-[#0284c7] shadow-sm'}"
            >
              <span>{theme === 'dark' ? t($currentLanguage).theme_dark : t($currentLanguage).theme_light}</span>
            </button>
          </div>
        </div>

        <!-- Hero Section with Real 4K Studio Photography -->
        <section id="hero-section" class="p-6 sm:p-8 rounded-2xl border transition-all {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433] shadow-xl' : 'bg-white border-[#e2e8f0] shadow-sm'}">
          <div class="flex flex-col md:flex-row items-center md:items-start gap-8">
            <!-- Author Studio Portrait -->
            <div id="hero-avatar-container" class="w-36 h-36 sm:w-44 sm:h-44 md:w-52 md:h-52 shrink-0 rounded-2xl overflow-hidden border-2 {theme === 'dark' ? 'border-[#d4af37]/30 shadow-2xl shadow-[#d4af37]/5' : 'border-[#b78e20]/30 shadow-md'}">
              <ResponsivePicture
                id="hero-avatar-img"
                assetKey="dmitry_efremov_studio"
                alt="Официальный студийный портрет Дмитрия Ефремова"
                imgClass="w-full h-full object-cover object-top hover:scale-105 transition-transform duration-500"
                loading="eager"
              />
            </div>

            <!-- Bio & Manifest -->
            <div class="space-y-4 flex-1 text-center md:text-left">
              <div class="space-y-1.5">
                <div class="flex flex-wrap items-center justify-center md:justify-start gap-2 text-xs font-mono">
                  <span class="px-2 py-0.5 rounded bg-[#d4af37]/15 text-[#e5c158] font-medium border border-[#d4af37]/25">
                    {$currentLanguage === 'ru' ? 'Архитектор автономных систем' : ($currentLanguage === 'ka' ? 'ავტონომიური სისტემების არქიტექტორი' : 'Autonomous Systems Architect')}
                  </span>
                  <span class="{theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">•</span>
                  <span class="{theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
                    {$currentLanguage === 'ru' ? 'A2A Экономика' : ($currentLanguage === 'ka' ? 'A2A ეკონომიკა' : 'A2A Economy')}
                  </span>
                  <span class="{theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">•</span>
                  <span class="{theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
                    {$currentLanguage === 'ru' ? 'Философия действия' : ($currentLanguage === 'ka' ? 'მოქმედების ფილოსოფია' : 'Philosophy of Action')}
                  </span>
                </div>
                <h1 id="hero-title" class="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                  {t($currentLanguage).hero_name}
                </h1>
                <p id="hero-subtitle" class="text-base sm:text-lg font-mono font-medium {theme === 'dark' ? 'text-[#e5c158]' : 'text-[#9c7816]'}">
                  {t($currentLanguage).hero_tagline}
                </p>
              </div>

              <blockquote id="hero-manifest" class="p-4 rounded-xl border-l-4 italic text-sm sm:text-base leading-relaxed {theme === 'dark' ? 'bg-[#12151d] border-[#d4af37] text-[#cbd5e1]' : 'bg-[#f8fafc] border-[#b78e20] text-[#334155]'}">
                {t($currentLanguage).hero_manifest}
              </blockquote>

              <!-- Quick Action Links -->
              <div class="flex flex-wrap items-center justify-center md:justify-start gap-3 pt-2 text-xs font-mono">
                <a
                  href="https://t.me/efremov_mvp"
                  target="_blank"
                  rel="noopener noreferrer"
                  on:click={() => handleOutboundClick('https://t.me/efremov_mvp', 'Telegram @efremov_mvp', 'Telegram')}
                  class="px-3.5 py-2 rounded-lg font-bold transition-colors flex items-center gap-1.5 bg-[#d4af37] text-[#090a0f] hover:bg-[#e2c15e] shadow-sm"
                >
                  <span>Telegram @efremov_mvp</span>
                  
                </a>
                <a
                  href="https://www.youtube.com/@dmitrii.efremov"
                  target="_blank"
                  rel="noopener noreferrer"
                  on:click={() => handleOutboundClick('https://www.youtube.com/@dmitrii.efremov', 'YouTube Channel', 'YouTube')}
                  class="px-3.5 py-2 rounded-lg font-bold transition-colors flex items-center gap-1.5 border {theme === 'dark' ? 'bg-[#141824] text-white border-[#1f2433] hover:border-[#ef4444]' : 'bg-white text-[#0f172a] border-[#cbd5e1] hover:border-[#ef4444]'}"
                >
                  <span class="text-[#ef4444]">YouTube</span>
                  
                </a>
                <button
                  type="button"
                  on:click={() => (activeTab = 'contact')}
                  class="px-3.5 py-2 rounded-lg transition-colors border {theme === 'dark' ? 'bg-[#141824] text-[#cbd5e1] border-[#1f2433] hover:border-[#38bdf8]' : 'bg-white text-[#334155] border-[#cbd5e1] hover:border-[#0284c7]'}"
                >
                  {$currentLanguage === 'ru' ? 'Прямая связь & Заявки' : ($currentLanguage === 'ka' ? 'პირდაპირი კავშირი და განაცხადები' : 'Direct Contact & Inquiries')}
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Navigation Bar for Public Portal -->
        <nav id="nav-tabs" aria-label="Main Navigation" class="flex flex-wrap gap-2 p-1.5 rounded-xl border font-mono text-xs w-full sm:w-fit {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
          <button
            id="tab-btn-podcast"
            type="button"
            class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'podcast' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#181c26] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'podcast')}
          >
            <span>{t($currentLanguage).nav_podcast}</span>
          </button>
          <button
            id="tab-btn-article"
            type="button"
            class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'article' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#181c26] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'article')}
          >
            <span>{t($currentLanguage).nav_article}</span>
          </button>
          <button
            id="tab-btn-sparks"
            type="button"
            class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'sparks' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#181c26] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'sparks')}
          >
            <span>{t($currentLanguage).nav_sparks}</span>
          </button>
          <button
            id="tab-btn-contact"
            type="button"
            class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'contact' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#181c26] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'contact')}
          >
            <span>{t($currentLanguage).nav_contact}</span>
          </button>
          <button
            id="tab-btn-canon"
            type="button"
            class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'canon' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#181c26] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'canon')}
          >
            <span>{t($currentLanguage).nav_canon}</span>
          </button>
        </nav>
      </header>

      <!-- Main Content Area -->
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

      <!-- Footer -->
      <footer id="main-footer" class="border-t pt-8 font-mono text-xs flex flex-col sm:flex-row justify-between items-center gap-4 {theme === 'dark' ? 'border-[#1f2433] text-[#94a3b8]' : 'border-[#e2e8f0] text-[#64748b]'}">
        <div>
          <span>© {new Date().getFullYear()} {t($currentLanguage).footer_rights}</span>
        </div>
        <div class="flex items-center gap-6">
          <button
            id="cmp-persistent-link"
            type="button"
            on:click={toggleCmpModal}
            class="underline hover:text-current transition-colors cursor-pointer"
          >
            <span class="sr-only">Приватность (CMP)</span>
            <span>{t($currentLanguage).footer_privacy}</span>
          </button>
          <button
            id="imprint-toggle-btn"
            type="button"
            on:click={toggleImprint}
            class="underline hover:text-current transition-colors cursor-pointer"
          >
            {t($currentLanguage).footer_imprint}
          </button>
        </div>
      </footer>

      <!-- Imprint Modal -->
      {#if showImprintModal}
        <div id="imprint-modal" role="dialog" aria-labelledby="imprint-title" class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div class="max-w-md w-full p-6 rounded-2xl border shadow-2xl space-y-4 font-mono text-xs {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] text-[#e2e8f0]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}">
            <div class="flex justify-between items-center border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
              <h2 id="imprint-title" class="text-sm font-bold">{t($currentLanguage).imprint_title}</h2>
              <button
                id="imprint-close-btn"
                type="button"
                on:click={toggleImprint}
                class="px-2 py-1 rounded border {theme === 'dark' ? 'bg-[#1f2433] hover:bg-[#2e364f]' : 'bg-[#f1f5f9] hover:bg-[#e2e8f0]'}"
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
