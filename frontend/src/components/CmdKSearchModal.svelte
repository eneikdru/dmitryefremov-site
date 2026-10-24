<script lang="ts">
  import { onMount, createEventDispatcher, tick } from 'svelte';
  import { searchCorpus, type CorpusSearchResult } from '../lib/canon';

  export let isOpen: boolean = false;
  export let theme: 'dark' | 'light' = 'dark';

  const dispatch = createEventDispatcher();

  let searchQuery: string = '';
  let searchResults: CorpusSearchResult[] = [];
  let selectedIndex: number = 0;
  let searchInputEl: HTMLInputElement;
  let modalContainerEl: HTMLDivElement;

  $: {
    if (isOpen) {
      performSearch(searchQuery);
      tick().then(() => {
        if (searchInputEl) {
          searchInputEl.focus();
        }
      });
    }
  }

  async function performSearch(query: string) {
    searchResults = await searchCorpus(query);
    if (selectedIndex >= searchResults.length) {
      selectedIndex = Math.max(0, searchResults.length - 1);
    }
  }

  function handleQueryInput(e: Event) {
    const target = e.target as HTMLInputElement;
    searchQuery = target.value;
    performSearch(searchQuery);
  }

  function closeModal() {
    isOpen = false;
    dispatch('close');
  }

  function handleBackdropKeydown(e: KeyboardEvent) {
    if (e.key === 'Escape') {
      closeModal();
    }
  }

  function handleKeydown(e: KeyboardEvent) {
    if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
      e.preventDefault();
      if (isOpen) {
        closeModal();
      } else {
        isOpen = true;
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
      if (searchResults[selectedIndex]) {
        selectResult(searchResults[selectedIndex]);
      }
    } else if (e.key === 'Tab') {
      // Basic focus trap within modal
      if (modalContainerEl) {
        const focusables = modalContainerEl.querySelectorAll<HTMLElement>(
          'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );
        if (focusables.length > 0) {
          const first = focusables[0];
          const last = focusables[focusables.length - 1];
          if (e.shiftKey && document.activeElement === first) {
            e.preventDefault();
            last.focus();
          } else if (!e.shiftKey && document.activeElement === last) {
            e.preventDefault();
            first.focus();
          }
        }
      }
    }
  }

  function selectResult(result: CorpusSearchResult) {
    dispatch('select', result);
    closeModal();
  }

  onMount(() => {
    window.addEventListener('keydown', handleKeydown);
    return () => {
      window.removeEventListener('keydown', handleKeydown);
    };
  });
</script>

{#if isOpen}
  <div
    id="cmdk-search-modal-backdrop"
    role="presentation"
    class="fixed inset-0 z-50 bg-black/70 backdrop-blur-md flex items-start justify-center pt-16 px-4"
    on:click|self={closeModal}
    on:keydown={handleBackdropKeydown}
  >
    <div
      bind:this={modalContainerEl}
      id="cmdk-search-modal"
      role="dialog"
      aria-modal="true"
      aria-label="Мгновенный поиск по корпусу знаний"
      class="w-full max-w-xl rounded-xl border shadow-2xl overflow-hidden transition-all animate-fadeIn {theme === 'dark' ? 'bg-[#0f131c] border-[#38bdf8]/40 text-[#dfe2ee]' : 'bg-white border-[#0284c7]/40 text-[#0f172a]'}"
    >
      <!-- Search Input Bar -->
      <div class="p-3.5 border-b flex items-center gap-3 {theme === 'dark' ? 'border-[#31353e]' : 'border-[#e2e8f0]'}">
        <span class="text-lg {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#0284c7]'}">🔍</span>
        <input
          bind:this={searchInputEl}
          id="cmdk-search-input"
          type="text"
          value={searchQuery}
          on:input={handleQueryInput}
          placeholder="Поиск по понятиям Канона, подкастам, эссе и заметкам (Cmd+K)..."
          aria-label="Поисковый запрос"
          class="w-full bg-transparent border-none focus:outline-none focus:ring-0 text-sm font-sans placeholder:text-[#908fa0]"
        />
        <kbd class="px-2 py-0.5 rounded text-[10px] font-mono border {theme === 'dark' ? 'bg-[#1c2028] text-[#908fa0] border-[#464554]' : 'bg-[#f1f5f9] text-[#64748b] border-[#cbd5e1]'}">
          ESC
        </kbd>
      </div>

      <!-- Results List -->
      <div class="p-2 space-y-1 max-h-80 overflow-y-auto">
        {#if searchResults.length === 0}
          <div class="p-4 text-center text-xs font-mono text-[#908fa0]">
            Ничего не найдено по запросу «{searchQuery}»
          </div>
        {:else}
          <div class="px-3 py-1.5 text-[11px] font-mono tracking-wider text-[#908fa0] uppercase">
            РЕЗУЛЬТАТЫ ПО КОРПУСУ ZETTELKASTEN ({searchResults.length})
          </div>
          {#each searchResults as item, index}
            <button
              type="button"
              id="cmdk-result-item-{index}"
              class="w-full text-left p-3 rounded-lg flex items-start justify-between gap-3 transition-colors cursor-pointer {index === selectedIndex ? (theme === 'dark' ? 'bg-[#1c2028] ring-1 ring-[#38bdf8]/50' : 'bg-[#f0f9ff] ring-1 ring-[#0284c7]/50') : (theme === 'dark' ? 'hover:bg-[#181c24]' : 'hover:bg-[#f8fafc]')}"
              on:click={() => selectResult(item)}
              on:mouseenter={() => (selectedIndex = index)}
            >
              <div class="space-y-1 flex-1">
                <div class="flex items-center gap-2">
                  <span class="text-xs px-1.5 py-0.5 rounded font-mono text-[10px] uppercase font-bold {item.type === 'canon' ? 'bg-[#38bdf8]/20 text-[#38bdf8]' : item.type === 'podcast' ? 'bg-[#ddb7ff]/20 text-[#ddb7ff]' : item.type === 'essay' ? 'bg-[#7bd0ff]/20 text-[#7bd0ff]' : 'bg-[#ffb4ab]/20 text-[#ffb4ab]'}">
                    {item.type}
                  </span>
                  <span class="font-medium text-sm font-sans line-clamp-1 {index === selectedIndex ? (theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#0284c7]') : ''}">
                    {item.title}
                  </span>
                </div>
                <p class="text-xs text-[#908fa0] line-clamp-2 leading-relaxed">
                  {item.snippet}
                </p>
              </div>

              {#if item.backlinksCount !== undefined}
                <div class="shrink-0 font-mono text-[11px] px-2 py-1 rounded {theme === 'dark' ? 'bg-[#0f131c] text-[#7bd0ff] border border-[#31353e]' : 'bg-[#f1f5f9] text-[#0284c7] border border-[#e2e8f0]'}">
                  {item.backlinksCount} связей
                </div>
              {/if}
            </button>
          {/each}
        {/if}
      </div>

      <!-- Footer Navigation Tips -->
      <div class="p-2.5 border-t flex items-center justify-between text-[11px] font-mono text-[#908fa0] {theme === 'dark' ? 'bg-[#0f131c] border-[#31353e]' : 'bg-[#f8fafc] border-[#e2e8f0]'}">
        <div class="flex items-center gap-3">
          <span><kbd class="px-1 py-0.5 rounded bg-surface-container-high border border-outline-variant/50">↑↓</kbd> Навигация</span>
          <span><kbd class="px-1 py-0.5 rounded bg-surface-container-high border border-outline-variant/50">↵</kbd> Выбор</span>
        </div>
        <div>
          <span>Cmd + K</span>
        </div>
      </div>
    </div>
  </div>
{/if}
