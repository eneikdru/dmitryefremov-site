<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { telemetry } from '../lib/telemetry';

  export interface CanonNode {
    id: string;
    type: 'concept' | 'podcast' | 'essay' | 'spark';
    title: string;
    summary: string;
    targetTab?: 'podcast' | 'article' | 'sparks';
    crossLinks?: Array<{ id: string; title: string; type: string }>;
  }

  export let isOpen = false;
  export let currentNodeId: string = 'canon-core-node';
  export let onSelectNode: ((node: CanonNode) => void) | null = null;

  let searchQuery = '';
  let searchInputEl: HTMLInputElement | null = null;
  let selectedIndex = 0;

  const CANON_CORPUS: CanonNode[] = [
    {
      id: 'concept-deontic-01',
      type: 'concept',
      title: 'Деонтическая логика и нормативные инварианты',
      summary: 'Ядерное понятие Канона: модальные операторы обязательств O(A), разрешений P(A) и запретов F(A).',
      targetTab: 'article',
      crossLinks: [
        { id: 'art-01-deontic-logic', title: 'Deontic Logic Essay', type: 'essay' },
        { id: 'podcast-ep-01', title: 'Выпуск 1: Автономия систем', type: 'podcast' }
      ]
    },
    {
      id: 'concept-toc-02',
      type: 'concept',
      title: 'Теория ограничений иузловые пропускные способности',
      summary: 'Пять шагов Голдратта в распределенной архитектуре конвейеров данных.',
      targetTab: 'article',
      crossLinks: [
        { id: 'art-02-theory-of-constraints', title: 'TOC Pipeline Essay', type: 'essay' }
      ]
    },
    {
      id: 'art-01-deontic-logic',
      type: 'essay',
      title: 'Deontic Logic in Autonomous Process Architecture',
      summary: 'Эссе о нормативной логике и математическом обосновании безаварийности.',
      targetTab: 'article',
      crossLinks: [
        { id: 'concept-deontic-01', title: 'Понятие: Деонтическая логика', type: 'concept' }
      ]
    },
    {
      id: 'podcast-ep-01',
      type: 'podcast',
      title: 'Эпизод 1: Границы автономии и суверенные узлы',
      summary: 'Разбор взаимодействия суверенных агентов и протокола A2A.',
      targetTab: 'podcast',
      crossLinks: [
        { id: 'concept-deontic-01', title: 'Опорное понятие: Деонтика', type: 'concept' }
      ]
    },
    {
      id: 'spark-4096',
      type: 'spark',
      title: 'Spark #4096: Agentic Economy & A2A',
      summary: 'Заметка о микро-транзакциях агентов и онтологии автономии.',
      targetTab: 'sparks',
      crossLinks: [
        { id: 'podcast-ep-01', title: 'Связано с Выпуском 1', type: 'podcast' }
      ]
    }
  ];

  $: searchResults = searchQuery.trim() === ''
    ? CANON_CORPUS
    : CANON_CORPUS.filter(node =>
        node.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.summary.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.type.toLowerCase().includes(searchQuery.toLowerCase())
      );

  $: if (searchResults.length > 0 && selectedIndex >= searchResults.length) {
    selectedIndex = 0;
  }

  function handleKeydown(e: KeyboardEvent) {
    // Cmd+K or Ctrl+K shortcut toggle
    if ((e.metaKey || e.ctrlKey) && (e.key === 'k' || e.key === 'K')) {
      e.preventDefault();
      isOpen = !isOpen;
      if (isOpen) {
        setTimeout(() => searchInputEl?.focus(), 50);
      }
      return;
    }

    if (!isOpen) return;

    if (e.key === 'Escape') {
      e.preventDefault();
      closeModal();
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      if (searchResults.length > 0) {
        selectedIndex = (selectedIndex + 1) % searchResults.length;
      }
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      if (searchResults.length > 0) {
        selectedIndex = (selectedIndex - 1 + searchResults.length) % searchResults.length;
      }
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (searchResults.length > 0 && searchResults[selectedIndex]) {
        selectNode(searchResults[selectedIndex]);
      }
    }
  }

  function executeSearchTelemetry(selectedNode?: CanonNode) {
    if (searchQuery.trim().length > 0 || selectedNode) {
      telemetry.trackSearchExecution(
        searchQuery.trim() || selectedNode?.title || 'Cmd+K Navigation',
        searchResults.length,
        selectedNode?.id
      );
    }
  }

  function selectNode(node: CanonNode) {
    executeSearchTelemetry(node);
    if (currentNodeId && currentNodeId !== node.id) {
      telemetry.trackGraphNavigation(currentNodeId, node.id, 'search_selection');
    }
    if (onSelectNode) {
      onSelectNode(node);
    }
    closeModal();
  }

  function handleCrossLinkClick(sourceId: string, targetId: string, event?: Event) {
    if (event) event.stopPropagation();
    telemetry.trackGraphNavigation(sourceId, targetId, 'cross_link_click');
    const targetNode = CANON_CORPUS.find(n => n.id === targetId);
    if (targetNode && onSelectNode) {
      onSelectNode(targetNode);
    }
    closeModal();
  }

  function closeModal() {
    isOpen = false;
    searchQuery = '';
    selectedIndex = 0;
  }

  onMount(() => {
    if (typeof window !== 'undefined') {
      window.addEventListener('keydown', handleKeydown);
    }
  });

  onDestroy(() => {
    if (typeof window !== 'undefined') {
      window.removeEventListener('keydown', handleKeydown);
    }
  });
</script>

<!-- Search trigger button -->
<button
  id="cmdk-search-trigger"
  type="button"
  on:click={() => {
    isOpen = true;
    setTimeout(() => searchInputEl?.focus(), 50);
  }}
  aria-label="Open Canon Instant Search (Cmd+K)"
  class="px-3 py-1.5 rounded-lg border border-[#1f2433] bg-[#12151e] hover:bg-[#1a202c] text-[#38bdf8] hover:text-white font-mono text-xs flex items-center gap-2 focus:outline-none focus:ring-2 focus:ring-[#38bdf8] cursor-pointer transition-colors"
>
  <span class="material-symbols-outlined text-[16px]">search</span>
  <span>Мгновенный поиск Канона</span>
  <kbd class="px-1.5 py-0.5 rounded bg-[#090a0f] border border-[#1f2433] text-[10px] text-[#94a3b8]">⌘K</kbd>
</button>

<!-- Search Modal Backdrop & Dialog -->
{#if isOpen}
  <div
    id="cmdk-modal-backdrop"
    role="button"
    tabindex="0"
    on:click={closeModal}
    on:keydown={(e) => { if (e.key === 'Escape') closeModal(); }}
    class="fixed inset-0 bg-[#000000]/70 backdrop-blur-sm z-50 flex items-start justify-center pt-16 sm:pt-24 px-4 cursor-default"
  >
    <div
      id="cmdk-modal-dialog"
      role="dialog"
      tabindex="-1"
      aria-modal="true"
      aria-label="Canon Instant Search"
      on:click|stopPropagation
      on:keydown|stopPropagation
      class="w-full max-w-2xl bg-[#0d111a] border border-[#1f2433] rounded-xl shadow-2xl overflow-hidden font-sans text-[#dfe2ef]"
    >
      <!-- Search Input Header -->
      <div class="p-4 border-b border-[#1f2433] flex items-center gap-3 bg-[#121622]" role="search">
        <span class="material-symbols-outlined text-[#38bdf8] text-xl">search</span>
        <input
          id="cmdk-search-input"
          bind:this={searchInputEl}
          bind:value={searchQuery}
          type="text"
          placeholder="Поиск по Канону (понятия, эссе, выпуски, искры)..."
          aria-label="Search query"
          class="w-full bg-transparent border-none text-white placeholder-[#64748b] text-sm focus:outline-none focus:ring-0 font-body-md"
        />
        <button
          id="cmdk-close-btn"
          type="button"
          on:click={closeModal}
          aria-label="Close search modal"
          class="px-2 py-1 rounded bg-[#1c2230] text-[#94a3b8] hover:text-white text-xs font-mono border border-[#2d3748] focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
        >
          ESC
        </button>
      </div>

      <!-- Results List -->
      <div id="cmdk-results-list" class="max-h-96 overflow-y-auto p-2 space-y-1" role="listbox">
        {#if searchResults.length === 0}
          <div class="p-6 text-center text-xs font-mono text-[#64748b]">
            Ничего не найдено по запросу "{searchQuery}". Попробуйте другой термин.
          </div>
        {:else}
          {#each searchResults as node, index}
            <div
              id="cmdk-result-item-{node.id}"
              role="option"
              aria-selected={index === selectedIndex}
              tabindex="0"
              on:click={() => selectNode(node)}
              on:keydown={(e) => { if (e.key === 'Enter') selectNode(node); }}
              class="p-3 rounded-lg cursor-pointer border transition-all text-left focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {index === selectedIndex ? 'bg-[#1a2333] border-[#38bdf8] shadow-md' : 'bg-[#0f1420] border-transparent hover:bg-[#161d2b]'}"
            >
              <div class="flex items-center justify-between text-xs font-mono mb-1">
                <span class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider {node.type === 'concept' ? 'bg-[#38bdf8]/20 text-[#38bdf8] border border-[#38bdf8]/40' : node.type === 'podcast' ? 'bg-[#a855f7]/20 text-[#c084fc] border border-[#a855f7]/40' : node.type === 'essay' ? 'bg-[#34d399]/20 text-[#34d399] border border-[#34d399]/40' : 'bg-[#f59e0b]/20 text-[#fbbf24] border border-[#f59e0b]/40'}">
                  {node.type}
                </span>
                <span class="text-[#64748b]">id: {node.id}</span>
              </div>
              <div class="font-bold text-white text-sm hover:text-[#38bdf8] transition-colors">
                {node.title}
              </div>
              <p class="text-xs text-[#94a3b8] mt-1 leading-relaxed">
                {node.summary}
              </p>

              <!-- Cross Links -->
              {#if node.crossLinks && node.crossLinks.length > 0}
                <div class="mt-2.5 pt-2 border-t border-[#1f2433]/60 flex flex-wrap items-center gap-2 text-[11px] font-mono">
                  <span class="text-[#64748b]">Перекрёстные ссылки:</span>
                  {#each node.crossLinks as link}
                    <button
                      type="button"
                      on:click={(e) => handleCrossLinkClick(node.id, link.id, e)}
                      class="px-2 py-0.5 rounded bg-[#161e2e] text-[#38bdf8] hover:bg-[#38bdf8] hover:text-[#0a0e17] border border-[#2d3748] transition-colors focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
                    >
                      🔗 {link.title}
                    </button>
                  {/each}
                </div>
              {/if}
            </div>
          {/each}
        {/if}
      </div>

      <!-- Modal Footer Keyboard Instruction -->
      <div class="p-3 border-t border-[#1f2433] bg-[#0a0d14] text-[11px] font-mono text-[#64748b] flex justify-between items-center">
        <span>Навигация: <kbd class="px-1 rounded bg-[#161d2b] text-[#dfe2ef]">↑↓</kbd> выбор · <kbd class="px-1 rounded bg-[#161d2b] text-[#dfe2ef]">↵</kbd> перейти</span>
        <span class="text-[#38bdf8]">Zero-Cookie Telemetry Enabled</span>
      </div>
    </div>
  </div>
{/if}
