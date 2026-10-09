<script lang="ts">
  import TelemetryArticleView from './TelemetryArticleView.svelte';
  import TelemetryContactTerminalView from './TelemetryContactTerminalView.svelte';

  let activeTab: 'article' | 'contact' = 'article';
</script>

<main id="app-root" class="min-h-screen bg-[#090a0f] text-[#e2e8f0] font-sans p-4 sm:p-8">
  <div class="max-w-4xl mx-auto space-y-6">
    <!-- Main Banner / Header -->
    <header id="main-header" class="border-b border-[#1f2433] pb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <div class="flex items-center gap-2 mb-1">
          <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">[SYS_CORE]</span>
          <span class="text-xs font-mono text-[#94a3b8]">dmitryefremov.com</span>
        </div>
        <h1 class="text-3xl font-extrabold text-white tracking-tight">Дмитрий Ефремов</h1>
        <p class="text-sm text-[#94a3b8] font-mono mt-1">Системы, логика мышления и автономные процессы</p>
      </div>

      <nav id="nav-tabs" class="flex gap-2 bg-[#12151e] p-1 rounded border border-[#1f2433] font-mono text-xs">
        <button
          id="tab-btn-article"
          class="px-4 py-2 rounded transition-colors {activeTab === 'article' ? 'bg-[#1f2433] text-white font-bold' : 'text-[#94a3b8] hover:text-white'}"
          on:click={() => (activeTab = 'article')}
        >
          [ARTICLE_READER]
        </button>
        <button
          id="tab-btn-contact"
          class="px-4 py-2 rounded transition-colors {activeTab === 'contact' ? 'bg-[#1f2433] text-white font-bold' : 'text-[#94a3b8] hover:text-white'}"
          on:click={() => (activeTab = 'contact')}
        >
          [CONTACT_TERMINAL]
        </button>
      </nav>
    </header>

    <!-- Content View -->
    <section id="content-view">
      {#if activeTab === 'article'}
        <TelemetryArticleView />
      {:else}
        <TelemetryContactTerminalView />
      {/if}
    </section>
  </div>
</main>
