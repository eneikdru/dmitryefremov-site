<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry } from '../lib/telemetry';
  import ResponsivePicture from './ResponsivePicture.svelte';
  import ArticleStreamView from './ArticleStreamView.svelte';
  import PodcastPlayerView from './PodcastPlayerView.svelte';
  import SparksView from './SparksView.svelte';
  import TelemetryContactTerminalView from './TelemetryContactTerminalView.svelte';
  import ConsentManager from './ConsentManager.svelte';
  import SeoMeta from './SeoMeta.svelte';
  import AdminDashboardView from './AdminDashboardView.svelte';

  export let initialTab: 'podcast' | 'article' | 'sparks' | 'contact' = 'podcast';

  let activeTab: 'podcast' | 'article' | 'sparks' | 'contact' = initialTab;
  let theme: 'dark' | 'light' = 'dark';
  let showImprintModal: boolean = false;
  let isCmpModalOpen: boolean = false;
  let currentPath: string = typeof window !== 'undefined' ? window.location.pathname : '/';

  function updateRoute() {
    if (typeof window !== 'undefined') {
      currentPath = window.location.pathname;
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

{#if currentPath === '/admin' || (typeof window !== 'undefined' && window.location.hash === '#/admin')}
  <AdminDashboardView {theme} />
{:else}
  <SeoMeta
    title="Дмитрий Ефремов — Архитектура систем, философия сознания и автономные процессы"
    description="Суверенный интеллектуальный узел Дмитрия Ефремова. 9 выпусков подкаста «Системы и реальность», онтология автономии, A2A-экономика и эссе по деонтической логике."
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
              class="px-2.5 py-1 rounded text-xs font-mono font-medium tracking-wide {theme === 'dark' ? 'bg-[#12151e] text-[#38bdf8] border border-[#1f2433]' : 'bg-[#f1f5f9] text-[#0284c7] border border-[#cbd5e1]'}"
            >
              DMITRY EFREMOV
            </span>
            <span id="sys-domain" class="text-xs font-mono hidden sm:inline {theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">
              dmitryefremov.com
            </span>
          </div>

          <div class="flex items-center gap-3">
            <!-- Theme Toggle -->
            <button
              id="theme-toggle-btn"
              type="button"
              on:click={toggleTheme}
              aria-label="Toggle theme"
              class="px-3 py-1.5 rounded text-xs font-mono border transition-all flex items-center gap-2 cursor-pointer {theme === 'dark' ? 'bg-[#12151e] text-[#cbd5e1] border-[#1f2433] hover:border-[#38bdf8]' : 'bg-white text-[#334155] border-[#cbd5e1] hover:border-[#0284c7] shadow-sm'}"
            >
              <span>ТЕМА:</span>
              <span class="font-bold text-[#38bdf8]">{theme === 'dark' ? 'Dark Void' : 'Light Monochrome'}</span>
            </button>
          </div>
        </div>

        <!-- Hero Section with Real 4K Studio Photography -->
        <section id="hero-section" class="p-6 sm:p-8 rounded-2xl border transition-all {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433] shadow-xl' : 'bg-white border-[#e2e8f0] shadow-sm'}">
          <div class="flex flex-col md:flex-row items-center md:items-start gap-8">
            <!-- Author Studio Portrait -->
            <div id="hero-avatar-container" class="w-36 h-36 sm:w-44 sm:h-44 md:w-52 md:h-52 shrink-0 rounded-2xl overflow-hidden border-2 {theme === 'dark' ? 'border-[#38bdf8]/40 shadow-2xl shadow-[#38bdf8]/10' : 'border-[#0284c7]/40 shadow-lg'}">
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
                  <span class="px-2 py-0.5 rounded bg-[#38bdf8]/10 text-[#38bdf8] font-medium">Архитектор автономных систем</span>
                  <span class="{theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">•</span>
                  <span class="{theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">A2A Экономика</span>
                  <span class="{theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">•</span>
                  <span class="{theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">Философия действия</span>
                </div>
                <h1 id="hero-title" class="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                  Дмитрий Ефремов
                </h1>
                <p id="hero-subtitle" class="text-base sm:text-lg font-mono font-medium {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#0284c7]'}">
                  Системы, логика мышления и автономные процессы
                </p>
              </div>

              <blockquote id="hero-manifest" class="p-4 rounded-xl border-l-4 italic text-sm sm:text-base leading-relaxed {theme === 'dark' ? 'bg-[#141824] border-[#38bdf8] text-[#cbd5e1]' : 'bg-[#f8fafc] border-[#0284c7] text-[#334155]'}">
                «Ваш код и ваши решения — это то, кто вы есть на самом деле.»
              </blockquote>

              <!-- Quick Action Links -->
              <div class="flex flex-wrap items-center justify-center md:justify-start gap-3 pt-2 text-xs font-mono">
                <a
                  href="https://t.me/efremov_mvp"
                  target="_blank"
                  rel="noopener noreferrer"
                  on:click={() => handleOutboundClick('https://t.me/efremov_mvp', 'Telegram @efremov_mvp', 'Telegram')}
                  class="px-3.5 py-2 rounded-lg font-bold transition-colors flex items-center gap-1.5 bg-[#38bdf8] text-[#090a0f] hover:bg-[#7dd3fc]"
                >
                  <span>Telegram @efremov_mvp</span>
                  <span>↗</span>
                </a>
                <a
                  href="https://youtube.com/@dmitrii.efremov"
                  target="_blank"
                  rel="noopener noreferrer"
                  on:click={() => handleOutboundClick('https://youtube.com/@dmitrii.efremov', 'YouTube Channel', 'YouTube')}
                  class="px-3.5 py-2 rounded-lg font-bold transition-colors flex items-center gap-1.5 border {theme === 'dark' ? 'bg-[#141824] text-white border-[#1f2433] hover:border-[#ef4444]' : 'bg-white text-[#0f172a] border-[#cbd5e1] hover:border-[#ef4444]'}"
                >
                  <span class="text-[#ef4444]">YouTube</span>
                  <span>↗</span>
                </a>
                <button
                  type="button"
                  on:click={() => (activeTab = 'contact')}
                  class="px-3.5 py-2 rounded-lg transition-colors border {theme === 'dark' ? 'bg-[#141824] text-[#cbd5e1] border-[#1f2433] hover:border-[#38bdf8]' : 'bg-white text-[#334155] border-[#cbd5e1] hover:border-[#0284c7]'}"
                >
                  Прямая связь & Заявки
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
            class="px-4 py-2.5 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'podcast' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#0284c7] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'podcast')}
          >
            <span>🎙️ ПОДКАСТ</span>
            <span class="px-1.5 py-0.2 rounded text-[10px] {activeTab === 'podcast' ? 'bg-white/20' : 'bg-black/10'}">9 выпусков</span>
          </button>
          <button
            id="tab-btn-article"
            type="button"
            class="px-4 py-2.5 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'article' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#0284c7] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'article')}
          >
            <span>📖 ЭССЕ & МЫСЛИ</span>
          </button>
          <button
            id="tab-btn-sparks"
            type="button"
            class="px-4 py-2.5 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'sparks' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#0284c7] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'sparks')}
          >
            <span>⚡ SPARKS (TIER 3)</span>
          </button>
          <button
            id="tab-btn-contact"
            type="button"
            class="px-4 py-2.5 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'contact' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#0284c7] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
            on:click={() => (activeTab = 'contact')}
          >
            <span>✉️ КОНТАКТЫ & ИНТЕЙК</span>
          </button>
        </nav>
      </header>

      <!-- Main Content Area -->
      <section id="content-view" class="space-y-8">
        {#if activeTab === 'podcast'}
          <div class="space-y-6">
            <PodcastPlayerView />
          </div>
        {:else if activeTab === 'article'}
          <div class="space-y-6">
            <ArticleStreamView />
          </div>
        {:else if activeTab === 'sparks'}
          <div class="space-y-6">
            <SparksView {theme} />
          </div>
        {:else if activeTab === 'contact'}
          <div class="space-y-6">
            <TelemetryContactTerminalView />
          </div>
        {/if}
      </section>

      <!-- Footer -->
      <footer id="main-footer" class="border-t pt-8 font-mono text-xs flex flex-col sm:flex-row justify-between items-center gap-4 {theme === 'dark' ? 'border-[#1f2433] text-[#94a3b8]' : 'border-[#e2e8f0] text-[#64748b]'}">
        <div>
          <span>© {new Date().getFullYear()} Дмитрий Ефремов (Dmitry Efremov). Все права защищены.</span>
        </div>
        <div class="flex items-center gap-6">
          <button
            id="cmp-persistent-link"
            type="button"
            on:click={toggleCmpModal}
            class="underline hover:text-current transition-colors cursor-pointer"
          >
            Приватность (CMP)
          </button>
          <button
            id="imprint-toggle-btn"
            type="button"
            on:click={toggleImprint}
            class="underline hover:text-current transition-colors cursor-pointer"
          >
            Реквизиты / Imprint
          </button>
        </div>
      </footer>

      <!-- Imprint Modal -->
      {#if showImprintModal}
        <div id="imprint-modal" role="dialog" aria-labelledby="imprint-title" class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div class="max-w-md w-full p-6 rounded-2xl border shadow-2xl space-y-4 font-mono text-xs {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] text-[#e2e8f0]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}">
            <div class="flex justify-between items-center border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
              <h2 id="imprint-title" class="text-sm font-bold">ЮРИДИЧЕСКИЕ РЕКВИЗИТЫ</h2>
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
              <p><strong>Владелец сайта:</strong> Дмитрий Ефремов (Dmitry Efremov)</p>
              <p><strong>Домен:</strong> dmitryefremov.com</p>
              <p><strong>Email:</strong> contact@dmitryefremov.com</p>
              <p><strong>Стандарт конфиденциальности:</strong> Нулевые сторонние трекеры до явного согласия (Zero-Cookie Telemetry).</p>
              <p><strong>Авторские права:</strong> Все материалы, манифесты и формулировки процессов являются интеллектуальной собственностью автора.</p>
            </div>
          </div>
        </div>
      {/if}

      <ConsentManager {theme} bind:isOpen={isCmpModalOpen} />
    </main>
  </div>
{/if}
