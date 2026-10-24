<script lang="ts">
  import { onMount, onDestroy, tick } from 'svelte';
  import { telemetry } from '../lib/telemetry';

  export let isOpen: boolean = false;
  export let theme: 'dark' | 'light' = 'dark';
  export let onSelectNode: ((nodeId: string, nodeType: string) => void) | undefined = undefined;

  let searchQuery = '';
  let selectedIndex = 0;
  let inputElement: HTMLInputElement | null = null;
  let searchStartTime = 0;

  export interface CanonSearchItem {
    id: string;
    title: string;
    category: 'Concept' | 'Podcast' | 'Essay' | 'Spark';
    description: string;
    crossLinkTarget?: string;
  }

  const CORPUS: CanonSearchItem[] = [
    {
      id: 'concept-deontic-logic',
      title: 'Деонтическая логика (O, P, F)',
      category: 'Concept',
      description: 'Формальный аппарат нормативных операторов: Обязанность, Разрешение, Запрет.',
      crossLinkTarget: 'art-01-deontic-logic'
    },
    {
      id: 'concept-toc',
      title: 'Теория ограничений (TOC)',
      category: 'Concept',
      description: 'Пять направляющих шагов Голдратта и поиск узких мест в потоках.',
      crossLinkTarget: 'art-02-theory-of-constraints'
    },
    {
      id: 'concept-autonomous-agents',
      title: 'Детерминизм автономных агентов',
      category: 'Concept',
      description: 'Изоляция случайности, воспроизводимые сиды и A2A-протоколы.',
      crossLinkTarget: 'art-03-autonomous-agents'
    },
    {
      id: 'podcast-ep01',
      title: 'Подкаст 01: Архитектура систем и обратная связь',
      category: 'Podcast',
      description: 'Фундаментальный разбор контуров управления и инвариантов.',
      crossLinkTarget: 'podcast-ep-01'
    },
    {
      id: 'essay-editorial-minimalism',
      title: 'Эссе: Эстетика инженерного молчания',
      category: 'Essay',
      description: 'Редакционный минимализм и отказ от визуального шума.',
      crossLinkTarget: 'high-end-editorial-minimalism'
    },
    {
      id: 'spark-a2a-economy',
      title: 'Spark: A2A Экономика и агенты',
      category: 'Spark',
      description: 'Квантованная мысль о взаимодействии агентов без посредников.',
      crossLinkTarget: 'spark-4096'
    }
  ];

  $: filteredResults = searchQuery.trim() === ''
    ? CORPUS
    : CORPUS.filter(item =>
        item.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.category.toLowerCase().includes(searchQuery.toLowerCase())
      );

  $: if (filteredResults.length > 0 && selectedIndex >= filteredResults.length) {
    selectedIndex = 0;
  }

  export function openModal() {
    isOpen = true;
    searchQuery = '';
    selectedIndex = 0;
    searchStartTime = Date.now();
    tick().then(() => {
      inputElement?.focus();
    });
  }

  export function closeModal() {
    isOpen = false;
  }

  function handleKeydown(e: KeyboardEvent) {
    if ((e.metaKey || e.ctrlKey) && (e.key === 'k' || e.key === 'K')) {
      e.preventDefault();
      if (isOpen) {
        closeModal();
      } else {
        openModal();
      }
      return;
    }

    if (!isOpen) return;

    if (e.key === 'Escape') {
      e.preventDefault();
      closeModal();
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      if (filteredResults.length > 0) {
        selectedIndex = (selectedIndex + 1) % filteredResults.length;
      }
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      if (filteredResults.length > 0) {
        selectedIndex = (selectedIndex - 1 + filteredResults.length) % filteredResults.length;
      }
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (filteredResults.length > 0 && filteredResults[selectedIndex]) {
        executeSelect(filteredResults[selectedIndex]);
      } else if (searchQuery.trim() !== '') {
        executeSearchQuery();
      }
    }
  }

  function handleInput() {
    selectedIndex = 0;
  }

  function executeSearchQuery() {
    const latencyMs = searchStartTime > 0 ? Date.now() - searchStartTime : 12;
    telemetry.trackSearchExecution(searchQuery, filteredResults.length, latencyMs, 'cmd_k');
  }

  function executeSelect(item: CanonSearchItem) {
    const latencyMs = searchStartTime > 0 ? Date.now() - searchStartTime : 18;
    telemetry.trackSearchExecution(searchQuery || item.title, filteredResults.length, latencyMs, 'cmd_k');

    if (item.crossLinkTarget) {
      telemetry.trackGraphNavigation('cmd_k_search', item.crossLinkTarget, 'search_selection', item.category);
    }

    if (onSelectNode) {
      onSelectNode(item.crossLinkTarget || item.id, item.category);
    }

    closeModal();
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

{#if isOpen}
  <div
    id="cmd-k-modal-backdrop"
    role="dialog"
    tabindex="-1"
    aria-modal="true"
    aria-labelledby="cmd-k-modal-title"
    class="fixed inset-0 z-50 bg-black/75 backdrop-blur-sm flex items-start justify-center pt-16 sm:pt-24 px-4 overflow-y-auto"
    on:click|self={closeModal}
    on:keydown|self={(e) => { if (e.key === 'Escape') closeModal(); }}
  >
    <div
      id="cmd-k-modal-container"
      class="w-full max-w-2xl rounded-2xl border shadow-2xl overflow-hidden transition-all duration-150 font-sans {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433] text-white' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}"
    >
      <!-- Header / Search Bar -->
      <div class="p-4 border-b flex items-center gap-3 {theme === 'dark' ? 'border-[#1f2433] bg-[#12151e]' : 'border-[#e2e8f0] bg-[#f8fafc]'}">
        <span class="text-[#38bdf8] font-mono text-lg select-none">⌘K</span>
        <div class="flex-1 relative">
          <label for="cmd-k-search-input" id="cmd-k-modal-title" class="sr-only">
            Поиск по Канону, подкастам, эссе и искрам
          </label>
          <input
            id="cmd-k-search-input"
            bind:this={inputElement}
            bind:value={searchQuery}
            on:input={handleInput}
            type="text"
            role="searchbox"
            aria-label="Поиск по Канону"
            aria-autocomplete="list"
            placeholder="Поиск по Канону (Cmd+K / Ctrl+K)..."
            class="w-full px-3 py-2 text-sm sm:text-base font-mono rounded-lg border focus:outline-none focus:ring-2 focus:ring-[#38bdf8] transition-all {theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433] text-white placeholder-[#64748b]' : 'bg-white border-[#cbd5e1] text-[#0f172a] placeholder-[#94a3b8]'}"
          />
        </div>
        <button
          id="cmd-k-close-btn"
          type="button"
          aria-label="Закрыть окно поиска"
          on:click={closeModal}
          class="px-2.5 py-1 text-xs font-mono rounded border transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {theme === 'dark' ? 'bg-[#1f2433] text-[#cbd5e1] border-[#334155] hover:bg-[#334155]' : 'bg-[#f1f5f9] text-[#334155] border-[#cbd5e1] hover:bg-[#e2e8f0]'}"
        >
          ESC
        </button>
      </div>

      <!-- Results List -->
      <div id="cmd-k-results-container" class="max-h-96 overflow-y-auto p-2 space-y-1" role="listbox" aria-label="Результаты поиска">
        {#if filteredResults.length === 0}
          <div class="p-6 text-center text-sm font-mono {theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">
            Ничего не найдено по запросу «<span class="text-[#38bdf8]">{searchQuery}</span>»
          </div>
        {:else}
          {#each filteredResults as item, index}
            <button
              type="button"
              id="cmd-k-result-item-{index}"
              role="option"
              aria-selected={index === selectedIndex}
              class="w-full p-3 rounded-xl border transition-all text-left flex items-start justify-between gap-4 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {index === selectedIndex ? (theme === 'dark' ? 'bg-[#1f2433] border-[#38bdf8] text-white' : 'bg-[#e0f2fe] border-[#0284c7] text-[#0f172a]') : (theme === 'dark' ? 'bg-transparent border-transparent text-[#cbd5e1] hover:bg-[#12151e]' : 'bg-transparent border-transparent text-[#334155] hover:bg-[#f1f5f9]')}"
              on:click={() => executeSelect(item)}
            >
              <div class="space-y-1 flex-1">
                <div class="flex items-center gap-2">
                  <span class="text-xs font-mono px-2 py-0.5 rounded font-medium border {item.category === 'Concept' ? 'bg-[#38bdf8]/10 text-[#38bdf8] border-[#38bdf8]/30' : item.category === 'Podcast' ? 'bg-[#a855f7]/10 text-[#a855f7] border-[#a855f7]/30' : item.category === 'Essay' ? 'bg-[#34d399]/10 text-[#34d399] border-[#34d399]/30' : 'bg-[#f59e0b]/10 text-[#f59e0b] border-[#f59e0b]/30'}">
                    {item.category}
                  </span>
                  <span class="font-bold text-sm tracking-tight">{item.title}</span>
                </div>
                <p class="text-xs leading-relaxed opacity-80">{item.description}</p>
              </div>
              <div class="text-xs font-mono text-[#38bdf8] shrink-0 self-center">
                Перейти ↵
              </div>
            </button>
          {/each}
        {/if}
      </div>

      <!-- Footer Help Bar -->
      <div class="px-4 py-2 border-t flex items-center justify-between text-[11px] font-mono {theme === 'dark' ? 'border-[#1f2433] bg-[#090a0f] text-[#64748b]' : 'border-[#e2e8f0] bg-[#f8fafc] text-[#94a3b8]'}">
        <div class="flex items-center gap-3">
          <span><kbd class="px-1 py-0.5 rounded bg-[#1f2433] text-white text-[10px]">↑↓</kbd> навигация</span>
          <span><kbd class="px-1 py-0.5 rounded bg-[#1f2433] text-white text-[10px]">↵</kbd> выбрать</span>
          <span><kbd class="px-1 py-0.5 rounded bg-[#1f2433] text-white text-[10px]">esc</kbd> закрыть</span>
        </div>
        <span class="text-[#38bdf8]">Zero-Cookie Analytics Active</span>
      </div>
    </div>
  </div>
{/if}
