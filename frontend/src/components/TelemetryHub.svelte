<script lang="ts">
  import ArticleStreamView from './ArticleStreamView.svelte';
  import TelemetryContactTerminalView from './TelemetryContactTerminalView.svelte';
  import PodcastPlayerView from './PodcastPlayerView.svelte';

  export let theme: 'dark' | 'light' = 'dark';
  export let activeTab: 'article' | 'contact' | 'podcast' = 'article';
  export let showImprintModal: boolean = false;

  function toggleTheme() {
    theme = theme === 'dark' ? 'light' : 'dark';
  }

  function toggleImprint() {
    showImprintModal = !showImprintModal;
  }
</script>

<div id="theme-root" class={theme === 'dark' ? 'dark-void bg-[#090a0f] text-[#e2e8f0]' : 'light-monochrome bg-[#f8fafc] text-[#0f172a]'}>
  <main id="app-root" class="min-h-screen p-4 sm:p-8 flex flex-col justify-between max-w-5xl mx-auto space-y-8 font-sans transition-colors duration-200">
    <div>
      <!-- Hero & Header Section -->
      <header id="main-header" class="border-b pb-6 flex flex-col gap-6 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
        <div class="flex items-center justify-between gap-4">
          <div class="flex items-center gap-2">
            <span id="sys-core-badge" class="px-2 py-0.5 rounded text-xs font-mono border {theme === 'dark' ? 'bg-[#12151e] text-[#38bdf8] border-[#1f2433]' : 'bg-[#f1f5f9] text-[#0284c7] border-[#cbd5e1]'}">
              [SYS_CORE]
            </span>
            <span id="sys-domain" class="text-xs font-mono {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">dmitryefremov.com</span>
          </div>

          <!-- Theme Toggle Control -->
          <button
            id="theme-toggle-btn"
            type="button"
            on:click={toggleTheme}
            aria-label="Toggle visual theme between Dark Void and Light Monochrome"
            class="px-3 py-1 rounded text-xs font-mono border transition-colors flex items-center gap-2 {theme === 'dark' ? 'bg-[#12151e] text-[#e2e8f0] border-[#1f2433] hover:border-[#38bdf8]' : 'bg-white text-[#0f172a] border-[#cbd5e1] hover:border-[#0284c7] shadow-sm'}"
          >
            <span>THEME:</span>
            <span class="font-bold">{theme === 'dark' ? 'Dark Void' : 'Light Monochrome'}</span>
          </button>
        </div>

        <!-- Hero Branding & Manifest -->
        <section id="hero-section" class="space-y-3">
          <h1 id="hero-title" class="text-3xl sm:text-4xl font-extrabold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Дмитрий Ефремов
          </h1>
          <p id="hero-subtitle" class="text-base sm:text-lg font-mono font-medium {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#0284c7]'}">
            Системы, логика мышления и автономные процессы
          </p>
          <blockquote id="hero-manifest" class="p-4 rounded border-l-4 italic text-sm sm:text-base leading-relaxed {theme === 'dark' ? 'bg-[#12151e] border-[#38bdf8] text-[#cbd5e1]' : 'bg-[#f1f5f9] border-[#0284c7] text-[#334155]'}">
            «Ваш код и ваши решения — это то, кто вы есть на самом деле.»
          </blockquote>
        </section>

        <!-- Navigation Tabs -->
        <nav id="nav-tabs" aria-label="Main Navigation" class="flex flex-wrap gap-2 p-1 rounded border font-mono text-xs w-fit {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-[#f1f5f9] border-[#cbd5e1]'}">
          <button
            id="tab-btn-article"
            type="button"
            class="px-4 py-2 rounded transition-colors {activeTab === 'article' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold' : 'bg-white text-[#0f172a] font-bold shadow-sm') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
            on:click={() => (activeTab = 'article')}
          >
            [ARTICLE_STREAM]
          </button>
          <button
            id="tab-btn-podcast"
            type="button"
            class="px-4 py-2 rounded transition-colors {activeTab === 'podcast' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold' : 'bg-white text-[#0f172a] font-bold shadow-sm') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
            on:click={() => (activeTab = 'podcast')}
          >
            [PODCAST_MEDIA]
          </button>
          <button
            id="tab-btn-contact"
            type="button"
            class="px-4 py-2 rounded transition-colors {activeTab === 'contact' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold' : 'bg-white text-[#0f172a] font-bold shadow-sm') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
            on:click={() => (activeTab = 'contact')}
          >
            [CONTACT_TERMINAL]
          </button>
        </nav>
      </header>

      <!-- Content View -->
      <section id="content-view" class="mt-6">
        {#if activeTab === 'article'}
          <ArticleStreamView />
        {:else if activeTab === 'podcast'}
          <PodcastPlayerView />
        {:else}
          <TelemetryContactTerminalView />
        {/if}
      </section>
    </div>

    <!-- Footer & Imprint Section -->
    <footer id="main-footer" class="border-t pt-6 font-mono text-xs flex flex-col sm:flex-row justify-between items-center gap-4 {theme === 'dark' ? 'border-[#1f2433] text-[#94a3b8]' : 'border-[#e2e8f0] text-[#64748b]'}">
      <div>
        <span>© {new Date().getFullYear()} Dmitry Efremov. All rights reserved.</span>
      </div>
      <div class="flex items-center gap-4">
        <button
          id="imprint-toggle-btn"
          type="button"
          on:click={toggleImprint}
          class="underline hover:text-current transition-colors cursor-pointer"
        >
          Imprint / Legal Details
        </button>
      </div>
    </footer>

    <!-- Imprint Legal Details Modal / Container -->
    {#if showImprintModal}
      <div id="imprint-modal" role="dialog" aria-labelledby="imprint-title" class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
        <div class="max-w-md w-full p-6 rounded-lg border shadow-xl space-y-4 font-mono text-xs {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] text-[#e2e8f0]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}">
          <div class="flex justify-between items-center border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
            <h2 id="imprint-title" class="text-sm font-bold">[LEGAL_IMPRINT]</h2>
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
            <p><strong>Domain:</strong> dmitryefremov.com</p>
            <p><strong>Contact Email:</strong> contact@dmitryefremov.com</p>
            <p><strong>Regulatory Standard:</strong> Telemedia Act & Privacy Regulations (Zero-Cookie Telemetry Standard)</p>
            <p><strong>Disclaimer:</strong> All intellectual content, software architectures, and deontic process formulations contained within this terminal remain the exclusive work of Dmitry Efremov.</p>
          </div>
        </div>
      </div>
    {/if}
  </main>
</div>
