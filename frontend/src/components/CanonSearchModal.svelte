<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { telemetry } from '../lib/telemetry';

  export let isOpen: boolean = false;
  export let theme: 'dark' | 'light' = 'dark';
  export let onSelectNode: ((node: SearchNode) => void) | undefined = undefined;

  export interface SearchNode {
    id: string;
    title: string;
    type: 'concept' | 'podcast' | 'essay' | 'spark';
    category: string;
    description: string;
    url?: string;
  }

  const CANON_SEARCH_NODES: SearchNode[] = [
    {
      id: 'concept-deontic-logic',
      title: 'Деонтическая логика в автономии',
      type: 'concept',
      category: 'Канон · Логика',
      description: 'Формализация нормативных ограничений и модальных операторов должествования для автономных агентов.'
    },
    {
      id: 'concept-theory-of-constraints',
      title: 'Теория ограничений (TOC)',
      type: 'concept',
      category: 'Канон · Системы',
      description: 'Поиск и расширение ключевого узкого места системы для кратного роста пропускной способности.'
    },
    {
      id: 'concept-extended-mind',
      title: 'Онтология автономии и Расширенный разум',
      type: 'concept',
      category: 'Канон · Философия',
      description: 'Концепция Энди Кларка и Сьюзан Херли: внешняя рабочая память и когнитивное сопряжение человека и агента.'
    },
    {
      id: 'concept-a2a-economy',
      title: 'A2A Экономика и Machine-Readable API',
      type: 'concept',
      category: 'Канон · Экономика',
      description: 'Взаимодействие автономных ИИ-агентов без участия человека: llms.txt, OpenAPI, JSON-схемы.'
    },
    {
      id: 'podcast-ep-01',
      title: 'Выпуск 01: Системы и реальность',
      type: 'podcast',
      category: 'Подкаст · Рут Баркан Маркус',
      description: 'Фундаментальный разбор модальной логики и связности объектов в инженерных системах.'
    },
    {
      id: 'podcast-ep-02',
      title: 'Выпуск 02: Понятийные структуры',
      type: 'podcast',
      category: 'Подкаст · Пол Тагард',
      description: 'Когнитивные концептуальные карты и согласованность мыслительных моделей.'
    },
    {
      id: 'essay-a2a-architecture',
      title: 'Архитектура A2A-взаимодействия',
      type: 'essay',
      category: 'Эссе · Статья',
      description: 'Как создавать интерфейсы, в которых автономные ИИ-агенты ориентируются так же свободно, как человек.'
    },
    {
      id: 'spark-telegram-mvp',
      title: 'Заметка: Выживание MVP в эпоху ИИ',
      type: 'spark',
      category: 'Sparks · Telegram',
      description: 'Прагматичный инжиниринг: почему рабочий код важнее красивых презентаций.'
    }
  ];

  let searchQuery: string = '';
  let selectedIndex: number = 0;
  let searchInputEl: HTMLInputElement | null = null;

  $: filteredResults = searchQuery.trim() === ''
    ? CANON_SEARCH_NODES
    : CANON_SEARCH_NODES.filter(node =>
        node.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
        node.category.toLowerCase().includes(searchQuery.toLowerCase())
      );

  $: if (filteredResults.length > 0 && selectedIndex >= filteredResults.length) {
    selectedIndex = 0;
  }

  function handleKeydown(event: KeyboardEvent) {
    if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') {
      event.preventDefault();
      isOpen = !isOpen;
      if (isOpen) {
        setTimeout(() => searchInputEl?.focus(), 50);
      }
    } else if (event.key === 'Escape' && isOpen) {
      event.preventDefault();
      closeModal();
    }
  }

  function closeModal() {
    isOpen = false;
    searchQuery = '';
    selectedIndex = 0;
  }

  function handleSearchInput() {
    selectedIndex = 0;
    if (searchQuery.trim().length > 1) {
      telemetry.trackSearchUtilization(searchQuery, 'cmd_k', filteredResults.length);
    }
  }

  function selectNode(node: SearchNode) {
    telemetry.trackSearchUtilization(searchQuery || node.title, 'cmd_k', filteredResults.length, node.id);
    if (onSelectNode) {
      onSelectNode(node);
    }
    closeModal();
  }

  function handleListKeydown(event: KeyboardEvent) {
    if (!isOpen) return;
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      selectedIndex = (selectedIndex + 1) % filteredResults.length;
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      selectedIndex = (selectedIndex - 1 + filteredResults.length) % filteredResults.length;
    } else if (event.key === 'Enter' && filteredResults[selectedIndex]) {
      event.preventDefault();
      selectNode(filteredResults[selectedIndex]);
    }
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

  $: if (isOpen && searchInputEl) {
    setTimeout(() => searchInputEl?.focus(), 50);
  }
</script>

{#if isOpen}
  <div
    id="cmd-k-modal-backdrop"
    class="fixed inset-0 z-50 flex items-start justify-center pt-16 sm:pt-24 px-4 bg-black/70 backdrop-blur-sm transition-opacity"
    on:click|self={closeModal}
    role="presentation"
  >
    <div
      id="cmd-k-search-modal"
      role="dialog"
      aria-modal="true"
      aria-labelledby="canon-search-title"
      class="w-full max-w-2xl rounded-2xl border shadow-2xl overflow-hidden font-sans transition-all {theme === 'dark' ? 'bg-[#0f131c] border-[#3b494b]/60 text-[#dfe2ef]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}"
    >
      <!-- Search Input Bar -->
      <div class="p-4 border-b flex items-center gap-3 {theme === 'dark' ? 'border-[#3b494b]/40 bg-[#181b25]' : 'border-[#e2e8f0] bg-[#f8fafc]'}">
        <span class="text-lg text-[#00f0ff]">🔍</span>
        <label for="canon-search-input" id="canon-search-title" class="sr-only">
          Поиск по Канону
        </label>
        <input
          id="canon-search-input"
          bind:this={searchInputEl}
          type="search"
          bind:value={searchQuery}
          on:input={handleSearchInput}
          on:keydown={handleListKeydown}
          placeholder="Поиск по Канону, эссе, подкастам (Cmd+K / Ctrl+K)..."
          class="w-full bg-transparent border-0 focus:outline-none focus:ring-2 focus:ring-[#00f0ff] rounded px-2 py-1 text-sm font-mono placeholder:text-[#849495] {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}"
          autocomplete="off"
        />
        <kbd class="hidden sm:inline-block px-2 py-1 text-[10px] font-mono rounded border text-[#849495] {theme === 'dark' ? 'bg-[#0a0e17] border-[#3b494b]/40' : 'bg-[#e2e8f0] border-[#cbd5e1]'}">
          ESC
        </kbd>
        <button
          type="button"
          on:click={closeModal}
          aria-label="Закрыть поиск"
          class="p-1 rounded hover:bg-black/10 focus:outline-none focus:ring-2 focus:ring-[#00f0ff] text-xs font-mono"
        >
          ✕
        </button>
      </div>

      <!-- Search Results Area -->
      <div id="canon-search-results" class="max-h-96 overflow-y-auto p-2 space-y-1" role="listbox" aria-label="Результаты поиска">
        {#if filteredResults.length === 0}
          <div class="p-8 text-center text-xs font-mono text-[#849495]">
            Ничего не найдено по запросу «{searchQuery}»
          </div>
        {:else}
          {#each filteredResults as node, index (node.id)}
            <button
              id={`search-result-${node.id}`}
              type="button"
              role="option"
              aria-selected={index === selectedIndex}
              on:click={() => selectNode(node)}
              on:mouseenter={() => (selectedIndex = index)}
              class="w-full text-left p-3 rounded-xl transition-all cursor-pointer flex flex-col gap-1 focus:outline-none focus:ring-2 focus:ring-[#00f0ff] {index === selectedIndex ? (theme === 'dark' ? 'bg-[#1c1f29] border border-[#00f0ff]/50' : 'bg-[#0284c7]/10 border border-[#0284c7]') : 'hover:bg-[#181b25]/50 border border-transparent'}"
            >
              <div class="flex items-center justify-between text-xs font-mono">
                <span class="px-2 py-0.5 rounded text-[10px] font-bold tracking-wider uppercase {node.type === 'concept' ? 'bg-[#00f0ff]/10 text-[#00f0ff] border border-[#00f0ff]/30' : (node.type === 'podcast' ? 'bg-[#6ffbbe]/10 text-[#6ffbbe] border border-[#6ffbbe]/30' : (node.type === 'essay' ? 'bg-[#c3c0ff]/10 text-[#c3c0ff] border border-[#c3c0ff]/30' : 'bg-[#e2dfff]/10 text-[#e2dfff]'))}">
                  {node.category}
                </span>
                <span class="text-[10px] text-[#849495] font-mono">↵ Выбрать</span>
              </div>
              <h3 class="text-sm font-semibold text-[#dfe2ef] line-clamp-1">{node.title}</h3>
              <p class="text-xs text-[#b9cacb] line-clamp-2 leading-relaxed">{node.description}</p>
            </button>
          {/each}
        {/if}
      </div>

      <!-- Footer Info -->
      <div class="p-3 border-t text-[11px] font-mono flex items-center justify-between text-[#849495] {theme === 'dark' ? 'border-[#3b494b]/30 bg-[#0a0e17]' : 'border-[#e2e8f0] bg-[#f8fafc]'}">
        <span>Навигация: ↑↓ выбор · Enter переход · ESC закрыть</span>
        <span class="text-[#00f0ff]">Zero-Cookie Telemetry Active</span>
      </div>
    </div>
  </div>
{/if}
