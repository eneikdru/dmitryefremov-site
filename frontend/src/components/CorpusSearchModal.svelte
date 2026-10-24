<script lang="ts">
  import { onMount, tick } from 'svelte';
  import {
    searchCorpus,
    fetchCorpusData,
    DEMO_CORPUS_FALLBACK,
    type SearchResult,
    type CorpusData
  } from '../lib/search';

  export let isOpen: boolean = false;
  export let onClose: () => void = () => {};
  export let onSelectResult: (result: SearchResult) => void = () => {};
  export let theme: 'dark' | 'light' = 'dark';

  let searchQuery: string = '';
  let selectedCategory: 'All' | 'Concepts' | 'Code References' | 'Backlinks' = 'All';
  let selectedIndex: number = 0;
  let corpusData: CorpusData = DEMO_CORPUS_FALLBACK;
  let inputEl: HTMLInputElement | null = null;
  let copiedWikiLinkNotice: boolean = false;

  $: isDark = theme === 'dark';

  // Perform search
  $: filteredResults = searchCorpus(searchQuery, selectedCategory, corpusData);

  $: if (selectedIndex >= filteredResults.length && filteredResults.length > 0) {
    selectedIndex = filteredResults.length - 1;
  }

  // Category counts
  $: allCount = searchCorpus(searchQuery, 'All', corpusData).length;
  $: conceptsCount = searchCorpus(searchQuery, 'Concepts', corpusData).length;
  $: codeCount = searchCorpus(searchQuery, 'Code References', corpusData).length;
  $: backlinksCount = searchCorpus(searchQuery, 'Backlinks', corpusData).length;

  onMount(() => {
    fetchCorpusData().then((data) => {
      corpusData = data;
    });
  });

  $: if (isOpen) {
    tick().then(() => {
      if (inputEl) {
        inputEl.focus();
        inputEl.select();
      }
    });
  }

  function handleKeydown(e: KeyboardEvent) {
    if (!isOpen) return;

    if (e.key === 'Escape') {
      e.preventDefault();
      onClose();
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
      if (filteredResults[selectedIndex]) {
        handleSelect(filteredResults[selectedIndex]);
      }
    } else if (e.key === 'Tab') {
      e.preventDefault();
      const categories: Array<'All' | 'Concepts' | 'Code References' | 'Backlinks'> = [
        'All',
        'Concepts',
        'Code References',
        'Backlinks'
      ];
      const nextIdx = (categories.indexOf(selectedCategory) + 1) % categories.length;
      selectedCategory = categories[nextIdx];
      selectedIndex = 0;
    } else if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'c') {
      if (filteredResults[selectedIndex]) {
        e.preventDefault();
        copyWikiLink(filteredResults[selectedIndex].wikiLink);
      }
    } else if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'g') {
      e.preventDefault();
      selectedCategory = 'Backlinks';
      selectedIndex = 0;
    }
  }

  function handleSelect(result: SearchResult) {
    onSelectResult(result);
    onClose();
  }

  function copyWikiLink(wikiLink: string) {
    if (typeof navigator !== 'undefined' && navigator.clipboard) {
      navigator.clipboard.writeText(wikiLink);
      copiedWikiLinkNotice = true;
      setTimeout(() => {
        copiedWikiLinkNotice = false;
      }, 2000);
    }
  }

  function clearQuery() {
    searchQuery = '';
    selectedIndex = 0;
    if (inputEl) inputEl.focus();
  }
</script>

<svelte:window on:keydown={handleKeydown} />

{#if isOpen}
  <!-- Command Palette Modal Backdrop -->
  <div
    id="corpus-search-modal"
    role="dialog"
    aria-modal="true"
    aria-label="Corpus Search Modal"
    class="fixed inset-0 z-50 flex flex-col justify-between bg-[#0a0e16]/80 backdrop-blur-md transition-opacity duration-200"
  >
    <!-- Active Modal Container -->
    <main class="flex-1 flex flex-col max-w-lg mx-auto w-full h-full justify-between p-2 sm:p-4">
      <!-- Top Half: Search Head, Shortcuts, Filters, and List Content -->
      <div class="flex-1 flex flex-col overflow-y-auto rounded-2xl border shadow-2xl {isDark ? 'bg-[#0f131c] border-[#31353e] text-[#dfe2ee]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}">
        <!-- Search Input Bar Header -->
        <header class="sticky top-0 z-20 p-3 sm:p-4 flex flex-col gap-2.5 border-b shadow-lg backdrop-blur-xl {isDark ? 'bg-[#181c24]/95 border-[#31353e]' : 'bg-[#f8fafc]/95 border-[#e2e8f0]'}">
          <div class="flex items-center gap-2 bg-[#0a0e16] border border-[#464554]/40 rounded-xl px-3 py-2 focus-within:border-[#c0c1ff] focus-within:ring-1 focus-within:ring-[#c0c1ff]/40 transition-all">
            <span class="text-[#c0c1ff] text-lg font-mono font-bold">🔍</span>
            <input
              bind:this={inputEl}
              id="corpus-search-input"
              type="text"
              aria-label="Search corpus"
              placeholder="Type to search entire corpus..."
              bind:value={searchQuery}
              on:input={() => (selectedIndex = 0)}
              class="flex-1 bg-transparent border-none text-sm font-mono text-white placeholder-slate-400 focus:outline-none"
            />

            <!-- Clear Action -->
            {#if searchQuery}
              <button
                id="corpus-search-clear-btn"
                aria-label="Clear query"
                type="button"
                on:click={clearQuery}
                class="text-[#c7c4d7] hover:text-white p-1 rounded active:scale-95 transition-transform"
              >
                ✕
              </button>
            {/if}

            <!-- Close Action Badge -->
            <button
              id="corpus-search-close-btn"
              type="button"
              on:click={onClose}
              class="flex items-center gap-1 bg-[#1c2028] border border-[#464554]/40 rounded px-2 py-0.5 text-xs text-[#c7c4d7] hover:text-white transition-colors"
            >
              <span class="font-mono text-[11px]">Esc</span>
            </button>
          </div>

          <!-- Shortcuts Pill Row -->
          <div class="flex items-center justify-between overflow-x-auto no-scrollbar pt-1 text-[#c7c4d7] text-xs font-mono">
            <div class="flex items-center gap-3 whitespace-nowrap">
              <span class="inline-flex items-center gap-1">
                <kbd class="bg-[#1c2028] px-1 py-0.5 rounded border border-[#464554]/30 text-white font-mono text-[10px]">↑↓</kbd>
                <span>Navigate</span>
              </span>
              <span class="inline-flex items-center gap-1">
                <kbd class="bg-[#1c2028] px-1 py-0.5 rounded border border-[#464554]/30 text-white font-mono text-[10px]">↵</kbd>
                <span>Open</span>
              </span>
              <span class="inline-flex items-center gap-1">
                <kbd class="bg-[#1c2028] px-1 py-0.5 rounded border border-[#464554]/30 text-white font-mono text-[10px]">tab</kbd>
                <span>Filter</span>
              </span>
              <span class="inline-flex items-center gap-1">
                <kbd class="bg-[#1c2028] px-1 py-0.5 rounded border border-[#464554]/30 text-white font-mono text-[10px]">⌘C</kbd>
                <span>Wiki-Link</span>
              </span>
            </div>
          </div>

          <!-- Category Filter Pills -->
          <div class="flex items-center gap-2 overflow-x-auto no-scrollbar pt-1">
            <button
              id="search-filter-all"
              type="button"
              on:click={() => { selectedCategory = 'All'; selectedIndex = 0; }}
              class="flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono font-medium transition-transform active:scale-95 {selectedCategory === 'All' ? 'bg-[#8083ff]/20 border border-[#c0c1ff]/40 text-[#c0c1ff]' : 'bg-[#1c2028] border border-[#464554]/20 text-[#c7c4d7] hover:text-white'}"
            >
              <span>All</span>
              <span class="px-1.5 py-0.2 bg-[#c0c1ff]/20 rounded-full font-mono text-[10px]">{allCount}</span>
            </button>

            <button
              id="search-filter-concepts"
              type="button"
              on:click={() => { selectedCategory = 'Concepts'; selectedIndex = 0; }}
              class="flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono transition-transform active:scale-95 {selectedCategory === 'Concepts' ? 'bg-[#8083ff]/20 border border-[#c0c1ff]/40 text-[#c0c1ff]' : 'bg-[#1c2028] border border-[#464554]/20 text-[#c7c4d7] hover:text-white'}"
            >
              <span>Concepts</span>
              <span class="px-1.5 py-0.2 bg-[#31353e] rounded-full font-mono text-[10px]">{conceptsCount}</span>
            </button>

            <button
              id="search-filter-code"
              type="button"
              on:click={() => { selectedCategory = 'Code References'; selectedIndex = 0; }}
              class="flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono transition-transform active:scale-95 {selectedCategory === 'Code References' ? 'bg-[#8083ff]/20 border border-[#c0c1ff]/40 text-[#c0c1ff]' : 'bg-[#1c2028] border border-[#464554]/20 text-[#c7c4d7] hover:text-white'}"
            >
              <span>Code References</span>
              <span class="px-1.5 py-0.2 bg-[#31353e] rounded-full font-mono text-[10px]">{codeCount}</span>
            </button>

            <button
              id="search-filter-backlinks"
              type="button"
              on:click={() => { selectedCategory = 'Backlinks'; selectedIndex = 0; }}
              class="flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono transition-transform active:scale-95 {selectedCategory === 'Backlinks' ? 'bg-[#8083ff]/20 border border-[#c0c1ff]/40 text-[#c0c1ff]' : 'bg-[#1c2028] border border-[#464554]/20 text-[#c7c4d7] hover:text-white'}"
            >
              <span>Backlinks</span>
              <span class="px-1.5 py-0.2 bg-[#31353e] rounded-full font-mono text-[10px]">{backlinksCount}</span>
            </button>
          </div>
        </header>

        <!-- Search Results List Canvas -->
        <div class="p-3 sm:p-4 flex flex-col gap-2.5">
          <div class="flex items-center justify-between px-1">
            <span id="search-results-header" class="font-mono text-xs text-[#c7c4d7] tracking-wider uppercase">
              SEARCH RESULTS ({filteredResults.length})
            </span>
            <span class="font-mono text-xs text-[#908fa0]">ranked by resonance</span>
          </div>

          {#if copiedWikiLinkNotice}
            <div class="p-2 bg-[#38bdf8]/20 border border-[#38bdf8] text-[#38bdf8] text-xs font-mono rounded-lg text-center">
              ✓ Wiki-link скопирован в буфер обмена!
            </div>
          {/if}

          {#if filteredResults.length === 0}
            <div class="p-8 text-center text-xs font-mono text-[#908fa0] space-y-2">
              <p>Записей по вашему запросу не найдено.</p>
              <p class="text-[11px] text-[#64748b]">Попробуйте ввести другое ключевое слово или сбросить фильтры.</p>
            </div>
          {:else}
            {#each filteredResults as result, idx}
              <button
                id="search-result-item-{idx}"
                type="button"
                class="w-full text-left relative flex flex-col gap-1.5 p-3 sm:p-4 rounded-xl border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] {idx === selectedIndex ? (isDark ? 'bg-[#31353e]/90 border-[#c0c1ff]/50 shadow-[0_0_20px_rgba(192,193,255,0.12)]' : 'bg-slate-100 border-[#0284c7]/50') : (isDark ? 'bg-[#1c2028]/60 border-[#464554]/20 hover:border-[#464554]/50' : 'bg-slate-50 border-slate-200 hover:border-slate-300')}"
                on:click={() => handleSelect(result)}
              >
                <!-- Vibrant Left Rail Indicator for Selected -->
                {#if idx === selectedIndex}
                  <div class="absolute inset-y-2 left-0 w-1 bg-[#c0c1ff] rounded-r"></div>
                {/if}

                <div class="flex items-center justify-between gap-2 pl-1">
                  <div class="flex items-center gap-1.5 text-[#c7c4d7] font-mono text-xs">
                    <span>{result.breadcrumb}</span>
                  </div>
                  <div class="flex items-center gap-1 bg-[#0a0e16] px-2 py-0.5 rounded-full border border-[#464554]/30 text-[#4cd7f6] font-mono text-[11px]">
                    <span>🔗 {result.incomingLinksCount} incoming</span>
                  </div>
                </div>

                <!-- Title with Double Bracket Syntax -->
                <h3 class="pl-1 text-sm font-semibold flex items-center gap-1">
                  <span class="text-[#4cd7f6] font-mono text-xs">[[</span>
                  <span class="text-[#c0c1ff] font-semibold">{result.title}</span>
                  <span class="text-[#4cd7f6] font-mono text-xs">]]</span>
                </h3>

                <!-- Matched Snippet with Highlight Tags -->
                <p class="pl-1 font-mono text-xs text-[#c7c4d7] leading-relaxed">
                  {@html result.highlightedSnippet}
                </p>

                <!-- Bottom Metadata Row -->
                {#if result.metadata}
                  <div class="pl-1 pt-1 flex items-center gap-2 text-[11px] font-mono text-[#908fa0]">
                    <span class="px-2 py-0.5 rounded bg-[#1c2028] border border-[#464554]/30 text-[#c7c4d7]">
                      {result.badge || result.typeLabel}
                    </span>
                    <span>• {result.metadata}</span>
                  </div>
                {/if}
              </button>
            {/each}
          {/if}

          <!-- Quick Command Actions Section -->
          <section class="mt-2 flex flex-col gap-1.5">
            <div class="px-1 font-mono text-[11px] text-[#c7c4d7] tracking-wider uppercase">
              QUICK COMMANDS
            </div>
            <div class="flex flex-col rounded-xl border border-[#464554]/30 bg-[#181c24] overflow-hidden">
              <button
                id="quick-cmd-jump"
                type="button"
                on:click={() => { selectedCategory = 'Backlinks'; selectedIndex = 0; }}
                class="flex items-center justify-between px-3 py-2 hover:bg-[#31353e] transition-colors border-b border-[#464554]/20 text-left"
              >
                <div class="flex items-center gap-2">
                  <span class="font-mono text-[#c0c1ff] font-semibold">&gt;</span>
                  <span class="text-xs font-mono text-white">Jump to Backlinks Graph</span>
                </div>
                <kbd class="font-mono text-[11px] text-[#c7c4d7] bg-[#1c2028] px-1.5 py-0.5 rounded border border-[#464554]/30">⌘G</kbd>
              </button>

              <button
                id="quick-cmd-copy"
                type="button"
                on:click={() => {
                  if (filteredResults[selectedIndex]) {
                    copyWikiLink(filteredResults[selectedIndex].wikiLink);
                  }
                }}
                class="flex items-center justify-between px-3 py-2 hover:bg-[#31353e] transition-colors text-left"
              >
                <div class="flex items-center gap-2">
                  <span class="font-mono text-[#c0c1ff] font-semibold">&gt;</span>
                  <span class="text-xs font-mono text-white">
                    Copy Markdown Wiki-Link
                    <span class="text-[#4cd7f6]">{filteredResults[selectedIndex]?.wikiLink || '[[Concept]]'}</span>
                  </span>
                </div>
                <kbd class="font-mono text-[11px] text-[#c7c4d7] bg-[#1c2028] px-1.5 py-0.5 rounded border border-[#464554]/30">⌘C</kbd>
              </button>
            </div>
          </section>
        </div>

        <!-- Bottom Keyboard Navigation Accessory Strip -->
        <footer class="sticky bottom-0 z-30 bg-[#181c24] border-t border-[#464554]/30 p-2.5 backdrop-blur-md">
          <div class="flex items-center justify-between">
            <!-- Prev / Next Micro Navigation Bar -->
            <div class="flex items-center gap-1.5">
              <button
                id="search-prev-btn"
                aria-label="Previous Result"
                type="button"
                on:click={() => {
                  if (filteredResults.length > 0) {
                    selectedIndex = (selectedIndex - 1 + filteredResults.length) % filteredResults.length;
                  }
                }}
                class="flex items-center gap-1 px-2.5 py-1 rounded bg-[#1c2028] hover:bg-[#31353e] border border-[#464554]/30 text-white text-xs font-mono active:scale-95 transition-transform"
              >
                <span>↑</span>
                <span class="hidden sm:inline">Prev</span>
              </button>

              <button
                id="search-next-btn"
                aria-label="Next Result"
                type="button"
                on:click={() => {
                  if (filteredResults.length > 0) {
                    selectedIndex = (selectedIndex + 1) % filteredResults.length;
                  }
                }}
                class="flex items-center gap-1 px-2.5 py-1 rounded bg-[#1c2028] hover:bg-[#31353e] border border-[#464554]/30 text-white text-xs font-mono active:scale-95 transition-transform"
              >
                <span>↓</span>
                <span class="hidden sm:inline">Next</span>
              </button>

              <span class="font-mono text-xs text-[#c7c4d7] pl-1">
                {filteredResults.length > 0 ? selectedIndex + 1 : 0} / {filteredResults.length}
              </span>
            </div>

            <!-- Direct Execution Buttons -->
            <div class="flex items-center gap-2">
              <button
                id="search-jump-btn"
                type="button"
                on:click={() => { selectedCategory = 'Backlinks'; selectedIndex = 0; }}
                class="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-[#31353e] hover:bg-[#353942] text-white font-mono text-xs border border-[#464554]/40 active:scale-95 transition-all"
              >
                <span class="text-[#4cd7f6]">⚡</span>
                <span>Jump</span>
              </button>

              <button
                id="search-open-btn"
                type="button"
                on:click={() => {
                  if (filteredResults[selectedIndex]) {
                    handleSelect(filteredResults[selectedIndex]);
                  }
                }}
                class="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-[#c0c1ff] text-[#1000a9] font-mono text-xs font-medium hover:bg-[#c0c1ff]/90 active:scale-95 transition-all shadow-[0_0_12px_rgba(192,193,255,0.3)]"
              >
                <span>Open Document</span>
                <span class="font-mono text-[11px] opacity-80">↵</span>
              </button>
            </div>
          </div>
        </footer>
      </div>
    </main>
  </div>
{/if}
