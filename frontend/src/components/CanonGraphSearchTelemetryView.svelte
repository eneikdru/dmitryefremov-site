<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export let theme: 'dark' | 'light' = 'dark';

  // Search Modal state
  let isSearchModalOpen: boolean = false;
  let searchQuery: string = '';
  let searchInputRef: HTMLInputElement | null = null;
  let searchResults: Array<{ id: string; title: string; type: string; nodeType: string; summary: string }> = [];

  // Selected Node / Graph navigation state
  let selectedNodeId: string = 'concept_deontic_logic';
  let loggedEvents: TelemetryEvent[] = [];
  let unsubscribeTelemetry: (() => void) | null = null;

  // Canon Node Corpus Data
  const canonNodes: Record<string, {
    id: string;
    title: string;
    category: string;
    description: string;
    crossLinks: Array<{ targetId: string; title: string; relationType: string; typeBadge: string }>;
  }> = {
    concept_deontic_logic: {
      id: 'concept_deontic_logic',
      title: 'Деонтическая логика в автономии',
      category: 'CanonConcept',
      description: 'Нормативная система правил, определяющая разрешённые, обязательные и запрещённые состояния автономных ИИ-агентов.',
      crossLinks: [
        { targetId: 'podcast_ep_01', title: 'Выпуск 01: Системы и реальность', relationType: 'analyzed_in', typeBadge: 'Podcast' },
        { targetId: 'essay_autonomy_ontology', title: 'Эссе: Онтология автономии', relationType: 'foundational_for', typeBadge: 'Essay' },
        { targetId: 'spark_a2a_protocol', title: 'Искра: A2A-Протокол взаимодействия', relationType: 'implements', typeBadge: 'Spark' }
      ]
    },
    podcast_ep_01: {
      id: 'podcast_ep_01',
      title: 'Выпуск 01: Системы и реальность',
      category: 'PodcastEpisode',
      description: 'Разбор деонтической логики, причинно-следственной связности и суверенных архитектур.',
      crossLinks: [
        { targetId: 'concept_deontic_logic', title: 'Понятие: Деонтическая логика', relationType: 'references', typeBadge: 'Concept' },
        { targetId: 'essay_autonomy_ontology', title: 'Эссе: Онтология автономии', relationType: 'extends', typeBadge: 'Essay' }
      ]
    },
    essay_autonomy_ontology: {
      id: 'essay_autonomy_ontology',
      title: 'Онтология автономии и деонтический инвариант',
      category: 'EssayNode',
      description: 'Формальный анализ субъектности и деонтических границ в автономном программном коде.',
      crossLinks: [
        { targetId: 'concept_deontic_logic', title: 'Понятие: Деонтическая логика', relationType: 'defines', typeBadge: 'Concept' },
        { targetId: 'spark_a2a_protocol', title: 'Искра: A2A-Протокол взаимодействия', relationType: 'complements', typeBadge: 'Spark' }
      ]
    },
    spark_a2a_protocol: {
      id: 'spark_a2a_protocol',
      title: 'A2A-Протокол взаимодействия агентов',
      category: 'SparkNode',
      description: 'Короткая заметка о пиринговых транзакционных связях между ИИ-агентами.',
      crossLinks: [
        { targetId: 'concept_deontic_logic', title: 'Понятие: Деонтическая логика', relationType: 'governed_by', typeBadge: 'Concept' },
        { targetId: 'podcast_ep_01', title: 'Выпуск 01: Системы и реальность', relationType: 'discussed_in', typeBadge: 'Podcast' }
      ]
    }
  };

  const corpusItems = [
    { id: 'concept_deontic_logic', title: 'Деонтическая логика в автономии', type: 'Concept', nodeType: 'CanonConcept', summary: 'Нормативная система правил автономных ИИ-агентов' },
    { id: 'podcast_ep_01', title: 'Выпуск 01: Системы и реальность', type: 'Podcast', nodeType: 'PodcastEpisode', summary: 'Деонтическая логика и суверенные архитектуры' },
    { id: 'essay_autonomy_ontology', title: 'Онтология автономии', type: 'Essay', nodeType: 'EssayNode', summary: 'Формальный анализ субъектности и деонтических границ' },
    { id: 'spark_a2a_protocol', title: 'A2A-Протокол взаимодействия', type: 'Spark', nodeType: 'SparkNode', summary: 'Пиринговые транзакционные связи между ИИ-агентами' }
  ];

  $: activeNode = canonNodes[selectedNodeId] || canonNodes['concept_deontic_logic'];

  function handleKeydown(event: KeyboardEvent) {
    if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') {
      event.preventDefault();
      openSearchModal();
    } else if (event.key === 'Escape' && isSearchModalOpen) {
      closeSearchModal();
    }
  }

  function openSearchModal() {
    isSearchModalOpen = true;
    setTimeout(() => {
      if (searchInputRef) searchInputRef.focus();
    }, 50);
  }

  function closeSearchModal() {
    isSearchModalOpen = false;
    searchQuery = '';
    searchResults = [];
  }

  function handleSearchInput() {
    if (!searchQuery.trim()) {
      searchResults = [];
      return;
    }
    const q = searchQuery.toLowerCase().trim();
    searchResults = corpusItems.filter(item =>
      item.title.toLowerCase().includes(q) || item.summary.toLowerCase().includes(q) || item.type.toLowerCase().includes(q)
    );
  }

  function executeSearchSubmit() {
    if (!searchQuery.trim()) return;
    const start = performance.now();
    handleSearchInput();
    const latency = Math.round(performance.now() - start);

    telemetry.trackSearchUtilization(searchQuery, searchResults.length, latency, 'cmd_k');
  }

  function selectSearchResult(nodeId: string) {
    if (canonNodes[nodeId]) {
      const sourceId = selectedNodeId;
      selectedNodeId = nodeId;
      telemetry.trackGraphNavigation(sourceId, nodeId, 'search_result_select', `Search Result Selection -> ${canonNodes[nodeId].title}`);
    }
    closeSearchModal();
  }

  function handleCrossLinkClick(targetId: string, title: string, relationType: string) {
    const sourceId = selectedNodeId;
    telemetry.trackGraphNavigation(sourceId, targetId, relationType, `${activeNode.title} -> ${title}`);
    if (canonNodes[targetId]) {
      selectedNodeId = targetId;
    }
  }

  onMount(() => {
    loggedEvents = telemetry.getEvents();
    unsubscribeTelemetry = telemetry.subscribe((evt) => {
      loggedEvents = [evt, ...loggedEvents];
    });

    if (typeof window !== 'undefined') {
      window.addEventListener('keydown', handleKeydown);
    }
  });

  onDestroy(() => {
    if (unsubscribeTelemetry) unsubscribeTelemetry();
    if (typeof window !== 'undefined') {
      window.removeEventListener('keydown', handleKeydown);
    }
  });
</script>

<div
  id="canon-telemetry-dashboard"
  class="space-y-6 transition-colors duration-200 {theme === 'dark' ? 'text-[#e2e2eb]' : 'text-[#111319]'}"
>
  <!-- TOP APP BAR -->
  <header
    id="telemetry-dashboard-header"
    class="flex justify-between items-center w-full px-4 py-3 rounded-xl border bg-[#12151e] border-[#2e3037] shadow-md"
  >
    <div class="flex items-center space-x-2">
      <span class="text-[#c0c1ff] font-mono text-lg font-bold">🕸️</span>
      <span class="font-mono text-xs text-[#c0c1ff] tracking-wider uppercase font-semibold">
        CANON_GRAPH_TELEMETRY
      </span>
    </div>
    <div class="flex items-center space-x-3">
      <!-- Cmd+K Search Trigger Button -->
      <button
        id="cmd-k-trigger-btn"
        type="button"
        on:click={openSearchModal}
        aria-label="Invoke Cmd+K Search Modal"
        class="flex items-center space-x-2 px-3 py-1.5 rounded bg-[#1e1f26] border border-[#464554] hover:border-[#c0c1ff] text-[#e2e2eb] font-mono text-xs focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] transition-all cursor-pointer"
      >
        <span class="text-[#908fa0]">🔍</span>
        <span class="hidden sm:inline">Поиск по канону...</span>
        <kbd class="px-1.5 py-0.5 rounded bg-[#33343b] text-[#c0c1ff] text-[10px] font-mono font-bold border border-[#464554]">
          ⌘K
        </kbd>
      </button>

      <div class="flex items-center space-x-1.5 px-2 py-1 rounded bg-[#00885d]/20 border border-[#4edea3]/40">
        <span class="w-2 h-2 rounded-full bg-[#4edea3] animate-pulse"></span>
        <span class="font-mono text-[10px] text-[#4edea3] tracking-wide uppercase font-semibold">STREAM: ACTIVE</span>
      </div>
    </div>
  </header>

  <!-- SUBHEADER: CONTEXT & TIME WINDOW SELECTOR -->
  <div class="px-4 py-2.5 rounded-xl border bg-[#191b22] border-[#33343b] flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-xs font-mono">
    <div class="flex items-center space-x-2">
      <span class="text-[#908fa0]">ENV:</span>
      <span class="text-[#d0bcff] bg-[#1e1f26] px-2 py-0.5 rounded border border-[#464554]">PROD-GRAPH-v2.4</span>
      <span class="text-[#908fa0] ml-2 hidden sm:inline">SYNC_RATE: 200ms</span>
    </div>

    <!-- Time Window Selector Tabs -->
    <div class="grid grid-cols-4 gap-1 p-0.5 bg-[#0c0e14] rounded border border-[#464554]/50 w-full sm:w-auto">
      <button type="button" class="px-2.5 py-1 text-center rounded bg-[#c0c1ff] text-[#1000a9] font-bold focus:outline-none focus:ring-1 focus:ring-[#c0c1ff]">1h</button>
      <button type="button" class="px-2.5 py-1 text-center rounded text-[#908fa0] hover:text-[#e2e2eb] focus:outline-none focus:ring-1 focus:ring-[#c0c1ff]">24h</button>
      <button type="button" class="px-2.5 py-1 text-center rounded text-[#908fa0] hover:text-[#e2e2eb] focus:outline-none focus:ring-1 focus:ring-[#c0c1ff]">7d</button>
      <button type="button" class="px-2.5 py-1 text-center rounded text-[#908fa0] hover:text-[#e2e2eb] focus:outline-none focus:ring-1 focus:ring-[#c0c1ff]">Custom</button>
    </div>
  </div>

  <!-- KPI 2x2 GRID -->
  <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
    <!-- Card 1: Graph Navigations -->
    <div class="bg-[#1e1f26] p-4 rounded-xl border border-[#33343b] flex flex-col justify-between space-y-2">
      <div class="flex items-center justify-between">
        <span class="font-mono text-[10px] text-[#908fa0] uppercase tracking-wider">GRAPH_NAV</span>
        <span class="text-[#c0c1ff] font-mono text-xs">🔗</span>
      </div>
      <div>
        <div class="flex items-baseline space-x-1.5">
          <span class="text-2xl font-bold font-mono text-[#e2e2eb]">142.8k</span>
          <span class="text-xs font-mono text-[#4edea3]">+18.4%</span>
        </div>
        <div class="flex items-center justify-between mt-2 pt-1 border-t border-[#33343b] text-[11px] font-mono">
          <span class="text-[#908fa0]">AVG_DEPTH</span>
          <span class="text-[#e1e0ff]">4.2 nodes</span>
        </div>
      </div>
    </div>

    <!-- Card 2: Search Volume -->
    <div class="bg-[#1e1f26] p-4 rounded-xl border border-[#33343b] flex flex-col justify-between space-y-2">
      <div class="flex items-center justify-between">
        <span class="font-mono text-[10px] text-[#908fa0] uppercase tracking-wider">CANON_SEARCH</span>
        <span class="text-[#d0bcff] font-mono text-xs">🔍</span>
      </div>
      <div>
        <div class="flex items-baseline space-x-1.5">
          <span class="text-2xl font-bold font-mono text-[#e2e2eb]">38.6k</span>
          <span class="text-xs font-mono text-[#908fa0]">vol</span>
        </div>
        <div class="flex items-center justify-between mt-2 pt-1 border-t border-[#33343b] text-[11px] font-mono">
          <span class="text-[#908fa0]">ENGAGE_RATE</span>
          <span class="text-[#4edea3] font-semibold">89.2%</span>
        </div>
      </div>
    </div>

    <!-- Card 3: Node Pivot Rate -->
    <div class="bg-[#1e1f26] p-4 rounded-xl border border-[#33343b] flex flex-col justify-between space-y-2">
      <div class="flex items-center justify-between">
        <span class="font-mono text-[10px] text-[#908fa0] uppercase tracking-wider">NODE_PIVOT_RT</span>
        <span class="text-[#4edea3] font-mono text-xs">🔀</span>
      </div>
      <div>
        <div class="flex items-baseline space-x-1.5">
          <span class="text-2xl font-bold font-mono text-[#e2e2eb]">64.7%</span>
          <span class="text-xs font-mono text-[#4edea3]">↑ 3.1%</span>
        </div>
        <div class="flex items-center justify-between mt-2 pt-1 border-t border-[#33343b] text-[11px] font-mono">
          <span class="text-[#908fa0]">CROSS_EDGE</span>
          <span class="text-[#e2e2eb]">Low drop</span>
        </div>
      </div>
    </div>

    <!-- Card 4: Active Explorers -->
    <div class="bg-[#1e1f26] p-4 rounded-xl border border-[#33343b] flex flex-col justify-between space-y-2">
      <div class="flex items-center justify-between">
        <span class="font-mono text-[10px] text-[#908fa0] uppercase tracking-wider">ACTIVE_EXPLORERS</span>
        <span class="text-[#c0c1ff] font-mono text-xs">🌐</span>
      </div>
      <div>
        <div class="flex items-baseline space-x-1.5">
          <span class="text-2xl font-bold font-mono text-[#e2e2eb]">3,410</span>
          <span class="text-xs font-mono text-[#908fa0]">DAU</span>
        </div>
        <div class="flex items-center justify-between mt-2 pt-1 border-t border-[#33343b] text-[11px] font-mono">
          <span class="text-[#908fa0]">GRAPH_AFFINITY</span>
          <span class="text-[#c0c1ff]">76.4%</span>
        </div>
      </div>
    </div>
  </section>

  <!-- INTERACTIVE CANON NODE GRAPH CROSS-LINK EXPLORER SECTION -->
  <section id="canon-graph-explorer-section" class="bg-[#1e1f26] rounded-xl border border-[#33343b] p-5 space-y-4 shadow-lg">
    <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-3 border-b border-[#33343b] gap-2">
      <div class="flex items-center space-x-2">
        <span class="text-[#c0c1ff] text-base">🌱</span>
        <h2 id="explorer-section-title" class="font-mono text-xs uppercase tracking-wider text-[#e2e2eb] font-bold">
          Canon Node & Graph Cross-Links Explorer
        </h2>
      </div>
      <span class="font-mono text-[10px] text-[#908fa0]">
        ACTIVE_NODE_ID: <code class="text-[#c0c1ff] font-bold">{activeNode.id}</code>
      </span>
    </div>

    <!-- Current Node Focus Display -->
    <div class="bg-[#12151e] border border-[#2e3037] rounded-lg p-4 space-y-3">
      <div class="flex items-center justify-between flex-wrap gap-2">
        <span class="px-2 py-0.5 rounded bg-[#c0c1ff]/20 text-[#c0c1ff] border border-[#c0c1ff]/40 font-mono text-[10px] font-bold uppercase">
          {activeNode.category}
        </span>
        <span class="font-mono text-[10px] text-[#4edea3]">✓ Node State Loaded</span>
      </div>

      <h3 id="active-node-title" class="text-lg font-bold text-white font-mono">
        {activeNode.title}
      </h3>
      <p class="text-xs font-mono text-[#908fa0] leading-relaxed">
        {activeNode.description}
      </p>

      <!-- Connected Cross-Links -->
      <div class="pt-3 border-t border-[#2e3037] space-y-2">
        <span class="block font-mono text-[10px] text-[#908fa0] uppercase tracking-wider font-semibold">
          CROSS-LINKED CANON NODES (Click to Navigate & Dispatch Telemetry):
        </span>

        <div class="flex flex-wrap gap-2">
          {#each activeNode.crossLinks as link}
            <button
              type="button"
              id={`cross-link-${link.targetId}`}
              on:click={() => handleCrossLinkClick(link.targetId, link.title, link.relationType)}
              aria-label={`Navigate cross-link to ${link.title}`}
              class="flex items-center space-x-2 px-3 py-2 rounded-lg bg-[#1e1f26] border border-[#464554] hover:border-[#c0c1ff] hover:bg-[#282a30] text-[#e2e2eb] font-mono text-xs focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] transition-all cursor-pointer"
            >
              <span class="px-1.5 py-0.2 rounded bg-[#33343b] text-[#d0bcff] text-[10px] font-bold">
                {link.typeBadge}
              </span>
              <span>{link.title}</span>
              <span class="text-[#908fa0] text-[10px]">({link.relationType}) →</span>
            </button>
          {/each}
        </div>
      </div>
    </div>
  </section>

  <!-- REAL-TIME TELEMETRY EVENT AUDIT LOG STREAM -->
  <section id="telemetry-audit-log-section" class="bg-[#1e1f26] rounded-xl border border-[#33343b] p-5 space-y-3 shadow-lg">
    <div class="flex items-center justify-between pb-2 border-b border-[#33343b]">
      <div class="flex items-center space-x-2">
        <span class="text-[#4edea3] text-base">💻</span>
        <h2 class="font-mono text-xs uppercase tracking-wider text-[#e2e2eb] font-bold">
          Audit / Event Trace Stream
        </h2>
      </div>
      <div class="flex items-center space-x-2">
        <span class="w-2 h-2 rounded-full bg-[#4edea3] animate-pulse"></span>
        <span class="font-mono text-[10px] text-[#908fa0]">LOGGED_EVENTS: {loggedEvents.length}</span>
      </div>
    </div>

    <div
      id="telemetry-event-trace-list"
      class="bg-[#0c0e14] p-3 rounded-lg border border-[#2e3037] space-y-2 max-h-48 overflow-y-auto font-mono text-xs text-[#e2e2eb]"
      role="region"
      aria-label="Real-time Telemetry Audit Stream"
    >
      {#if loggedEvents.length === 0}
        <p class="text-[#908fa0] italic">No telemetry events recorded yet. Click a cross-link or invoke Cmd+K search.</p>
      {:else}
        {#each loggedEvents as evt}
          <div class="flex flex-col space-y-1 pb-1.5 border-b border-[#1e1f26]">
            <div class="flex items-center justify-between text-[11px]">
              <span class="font-bold {evt.type === 'graph_navigation' ? 'text-[#c0c1ff]' : (evt.type === 'search_utilization' ? 'text-[#d0bcff]' : 'text-[#4edea3]')}">
                {evt.type}
              </span>
              <span class="text-[#908fa0] text-[10px]">{evt.timestamp.slice(11, 19)}</span>
            </div>
            <div class="text-[#908fa0] break-all text-[10px]">
              <code>{JSON.stringify(evt.payload)}</code>
            </div>
          </div>
        {/each}
      {/if}
    </div>
  </section>

  <!-- CMD+K SEARCH MODAL DIALOG (WCAG 2.1 AA Compliant) -->
  {#if isSearchModalOpen}
    <div
      id="cmd-k-search-modal"
      role="dialog"
      aria-modal="true"
      aria-label="Cmd+K Canon Search Modal"
      class="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-start justify-center pt-16 p-4 z-50"
    >
      <div class="max-w-xl w-full bg-[#12151e] border border-[#464554] rounded-2xl p-5 space-y-4 shadow-2xl font-mono text-xs text-[#e2e2eb]">
        <!-- Search Header -->
        <div class="flex items-center justify-between border-b border-[#2e3037] pb-3">
          <div class="flex items-center space-x-2">
            <span class="text-[#c0c1ff] font-bold text-sm">🔍</span>
            <h3 id="search-modal-title" class="font-bold text-sm text-[#e2e2eb]">
              Мгновенный поиск по Канону (Cmd+K)
            </h3>
          </div>
          <button
            type="button"
            id="close-search-modal-btn"
            on:click={closeSearchModal}
            aria-label="Close search modal"
            class="px-2 py-1 rounded bg-[#1e1f26] text-[#908fa0] hover:text-white border border-[#464554] focus:outline-none focus:ring-2 focus:ring-[#c0c1ff]"
          >
            ✕
          </button>
        </div>

        <!-- Search Input Form -->
        <form on:submit|preventDefault={executeSearchSubmit} class="space-y-3">
          <div class="space-y-1">
            <label for="cmd-k-search-input" class="block font-mono text-[10px] text-[#908fa0] uppercase">
              Поисковый запрос (подкасты, эссэ, искры, понятия)
            </label>
            <div class="relative">
              <input
                id="cmd-k-search-input"
                type="text"
                bind:this={searchInputRef}
                bind:value={searchQuery}
                on:input={handleSearchInput}
                placeholder="Введение запроса (e.g. state_machine, деонтика, протокол)..."
                class="w-full p-3 bg-[#0c0e14] border border-[#464554] rounded-xl text-white font-mono text-xs focus:outline-none focus:border-[#c0c1ff] focus:ring-2 focus:ring-[#c0c1ff]"
                required
              />
              <button
                type="submit"
                id="cmd-k-search-submit-btn"
                class="absolute right-2 top-2 px-3 py-1 rounded-lg bg-[#c0c1ff] text-[#1000a9] font-bold hover:brightness-110 focus:outline-none focus:ring-2 focus:ring-[#c0c1ff]"
              >
                Найти
              </button>
            </div>
          </div>
        </form>

        <!-- Search Results List -->
        <div class="space-y-2 pt-2 border-t border-[#2e3037]">
          <span class="block font-mono text-[10px] text-[#908fa0] uppercase">
            РЕЗУЛЬТАТЫ ПОИСКА ({searchResults.length}):
          </span>

          {#if searchResults.length === 0}
            <div class="p-4 text-center text-[#908fa0] italic border border-dashed border-[#2e3037] rounded-lg">
              {searchQuery ? 'Ничего не найдено по данному запросу.' : 'Введите ключевое слово или нажмите Найти.'}
            </div>
          {:else}
            <div class="space-y-2 max-h-60 overflow-y-auto pr-1">
              {#each searchResults as item}
                <button
                  type="button"
                  id={`search-result-item-${item.id}`}
                  on:click={() => selectSearchResult(item.id)}
                  aria-label={`Select search result ${item.title}`}
                  class="w-full text-left p-3 rounded-xl bg-[#1e1f26] border border-[#33343b] hover:border-[#c0c1ff] transition-all flex items-start justify-between gap-2 focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] cursor-pointer"
                >
                  <div>
                    <div class="flex items-center space-x-2">
                      <span class="px-1.5 py-0.2 rounded bg-[#33343b] text-[#d0bcff] text-[10px] font-bold">
                        {item.type}
                      </span>
                      <span class="font-bold text-white text-xs">{item.title}</span>
                    </div>
                    <p class="text-[11px] text-[#908fa0] mt-1">{item.summary}</p>
                  </div>
                  <span class="text-[#c0c1ff] text-xs shrink-0">Перейти →</span>
                </button>
              {/each}
            </div>
          {/if}
        </div>
      </div>
    </div>
  {/if}
</div>
