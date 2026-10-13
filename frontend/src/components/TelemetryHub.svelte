<script lang="ts">
  import { onMount } from 'svelte';
  import ScrollytellingView from './ScrollytellingView.svelte';
  import ResponsivePicture from './ResponsivePicture.svelte';
  import ArticleStreamView from './ArticleStreamView.svelte';
  import PodcastPlayerView from './PodcastPlayerView.svelte';
  import TelemetryContactTerminalView from './TelemetryContactTerminalView.svelte';
  import GqmTelemetryView from './GqmTelemetryView.svelte';
  import ConsentManager from './ConsentManager.svelte';
  import SeoMeta from './SeoMeta.svelte';

  export let initialTab: 'scrollytelling' | 'podcast' | 'article' | 'gallery' | 'contact' | 'gqm' = 'scrollytelling';

  let activeTab: 'scrollytelling' | 'podcast' | 'article' | 'gallery' | 'contact' | 'gqm' = initialTab;
  let theme: 'dark' | 'light' = 'dark';
  let showImprintModal: boolean = false;
  let isCmpModalOpen: boolean = false;

  onMount(() => {
    const savedTheme = localStorage.getItem('theme-preference') as 'dark' | 'light' | null;
    if (savedTheme) {
      theme = savedTheme;
    }
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
</script>

<SeoMeta
  title="Дмитрий Ефремов — High-End Scrollytelling, философия сознания и автономные процессы"
  description="Суверенный интеллектуальный узел Дмитрия Ефремова. High-End Scrollytelling, 9 выпусков подкаста «Системы и реальность», онтология автономии и деонтические инварианты."
  canonicalUrl="https://dmitryefremov.com"
/>

<div
  id="theme-root"
  class="min-h-screen transition-colors duration-200 {theme === 'dark' ? 'dark-void bg-[#0d0e11] text-[#e3e2e6]' : 'light-monochrome bg-[#fafafa] text-[#0f172a]'}"
>
  <!-- Main Navigation Control Bar -->
  <header class="max-w-6xl mx-auto px-4 pt-4">
    <div class="flex items-center justify-between gap-4 border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
      <div class="flex items-center gap-3">
        <span
          id="sys-core-badge"
          class="px-2.5 py-1 rounded text-xs font-mono font-medium tracking-wide {theme === 'dark' ? 'bg-[#12151e] text-[#d1f029] border border-[#1f2433]' : 'bg-[#f1f5f9] text-[#0284c7] border border-[#cbd5e1]'}"
        >
          DMITRY EFREMOV
        </span>
        <span id="sys-domain" class="text-xs font-mono hidden sm:inline {theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">
          dmitryefremov.com
        </span>
      </div>

      <div class="flex items-center gap-3">
        <!-- Theme Toggle Button -->
        <button
          id="theme-toggle-btn"
          type="button"
          on:click={toggleTheme}
          aria-label="Toggle theme"
          class="px-3 py-1.5 rounded text-xs font-mono border transition-all flex items-center gap-2 cursor-pointer {theme === 'dark' ? 'bg-[#12151e] text-[#cbd5e1] border-[#1f2433] hover:border-[#d1f029]' : 'bg-white text-[#334155] border-[#cbd5e1] hover:border-[#0284c7] shadow-sm'}"
        >
          <span>ТЕМА:</span>
          <span class="font-bold text-[#d1f029]">{theme === 'dark' ? 'Dark Void' : 'Light Monochrome'}</span>
        </button>
      </div>
    </div>

    <!-- Navigation Tabs Bar -->
    <nav
      id="nav-tabs"
      aria-label="Main Navigation"
      class="flex flex-wrap gap-2 p-1.5 rounded-xl border font-mono text-xs my-4 w-full sm:w-fit {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#e2e8f0] shadow-sm'}"
    >
      <button
        id="tab-btn-scrollytelling"
        type="button"
        class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'scrollytelling' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md border border-[#d1f029]/50' : 'bg-[#0284c7] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
        on:click={() => (activeTab = 'scrollytelling')}
      >
        <span>📜 SCROLLYTELLING</span>
      </button>

      <button
        id="tab-btn-podcast"
        type="button"
        class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'podcast' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#0284c7] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
        on:click={() => (activeTab = 'podcast')}
      >
        <span>🎙️ ПОДКАСТ</span>
        <span class="px-1.5 py-0.2 rounded text-[10px] {activeTab === 'podcast' ? 'bg-white/20' : 'bg-black/10'}">9 выпусков</span>
      </button>

      <button
        id="tab-btn-article"
        type="button"
        class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'article' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#0284c7] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
        on:click={() => (activeTab = 'article')}
      >
        <span>📖 ЭССЕ & МЫСЛИ</span>
      </button>

      <button
        id="tab-btn-contact"
        type="button"
        class="px-4 py-2 rounded-lg transition-all flex items-center gap-2 cursor-pointer {activeTab === 'contact' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold shadow-md' : 'bg-[#0284c7] text-white font-bold shadow-md') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white hover:bg-[#12151e]' : 'text-[#64748b] hover:text-[#0f172a] hover:bg-[#f1f5f9]')}"
        on:click={() => (activeTab = 'contact')}
      >
        <span>✉️ КОНТАКТЫ</span>
      </button>

      <button
        id="tab-btn-gqm"
        type="button"
        class="px-3 py-2 rounded-lg transition-all flex items-center gap-1.5 cursor-pointer {activeTab === 'gqm' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold' : 'bg-slate-200 text-[#0f172a] font-bold') : (theme === 'dark' ? 'text-[#64748b] hover:text-[#94a3b8]' : 'text-[#94a3b8] hover:text-[#64748b]')}"
        on:click={() => (activeTab = 'gqm')}
      >
        <span class="text-[11px]">TELEMETRY</span>
      </button>
    </nav>
  </header>

  <!-- Content Views -->
  <section id="content-view" class="max-w-6xl mx-auto px-4">
    {#if activeTab === 'scrollytelling'}
      <ScrollytellingView
        {theme}
        onToggleImprint={toggleImprint}
        onToggleCmp={toggleCmpModal}
        onTabSelect={(tab) => (activeTab = tab as any)}
      />
    {:else if activeTab === 'podcast'}
      <div class="py-6 space-y-6">
        <PodcastPlayerView />
      </div>
    {:else if activeTab === 'article'}
      <div class="py-6 space-y-6">
        <ArticleStreamView />
      </div>
    {:else if activeTab === 'contact'}
      <div class="py-6 space-y-6">
        <TelemetryContactTerminalView />
      </div>
    {:else}
      <div class="py-6 space-y-6">
        <GqmTelemetryView />
      </div>
    {/if}
  </section>

  <!-- Imprint Modal -->
  {#if showImprintModal}
    <div
      id="imprint-modal"
      role="dialog"
      aria-labelledby="imprint-title"
      class="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50"
    >
      <div class="max-w-md w-full p-6 rounded-2xl border shadow-2xl space-y-4 font-mono text-xs {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] text-[#e2e8f0]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}">
        <div class="flex justify-between items-center border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          <h2 id="imprint-title" class="text-sm font-bold">ЮРИДИЧЕСКИЕ РЕКВИЗИТЫ / IMPRINT</h2>
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
          <p><strong>Site Operator:</strong> Dmitry Efremov</p>
          <p><strong>Владелец сайта:</strong> Дмитрий Ефремов (Dmitry Efremov)</p>
          <p><strong>Домен:</strong> dmitryefremov.com</p>
          <p><strong>Email:</strong> contact@dmitryefremov.com</p>
          <p><strong>Стандарт конфиденциальности:</strong> Zero-Cookie Telemetry.</p>
          <p><strong>Авторские права:</strong> Все материалы являются интеллектуальной собственностью автора.</p>
        </div>
      </div>
    </div>
  {/if}

  <ConsentManager {theme} bind:isOpen={isCmpModalOpen} />
</div>
