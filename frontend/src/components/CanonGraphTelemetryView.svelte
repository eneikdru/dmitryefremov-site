<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export let theme: 'dark' | 'light' = 'dark';

  // State
  let timeWindow: '1h' | '24h' | '7d' | 'custom' = '1h';
  let isSearchModalOpen: boolean = false;
  let searchQuery: string = '';
  let searchInputEl: HTMLInputElement | null = null;
  let loggedEvents: TelemetryEvent[] = [];
  let selectedNodeId: string = 'concept_ontological_autonomy';
  let lastNavTarget: string | null = null;
  let searchPerformed: boolean = false;

  // Mock Graph Nodes for Navigation Cross-Links
  interface GraphNode {
    id: string;
    title: string;
    type: 'CanonConcept' | 'Podcast' | 'Essay' | 'Spark';
    crossLinks: Array<{ targetId: string; targetTitle: string; relation: string }>;
  }

  const nodes: GraphNode[] = [
    {
      id: 'concept_ontological_autonomy',
      title: 'Онтологическая Автономия (Ontological Autonomy)',
      type: 'CanonConcept',
      crossLinks: [
        { targetId: 'podcast_ep03', targetTitle: 'Выпуск 03: Деонтические инварианты', relation: 'analyzed_in' },
        { targetId: 'essay_editorial_minimalism', targetTitle: 'Эссе: Архитектурный минимализм', relation: 'formalized_in' },
        { targetId: 'spark_a2a_proof', targetTitle: 'Искра: A2A Settlement Proof', relation: 'demonstrated_by' }
      ]
    },
    {
      id: 'podcast_ep03',
      title: 'Выпуск 03: Деонтическая логика и системный контекст',
      type: 'Podcast',
      crossLinks: [
        { targetId: 'concept_ontological_autonomy', targetTitle: 'Понятие: Онтологическая Автономия', relation: 'references_concept' },
        { targetId: 'essay_editorial_minimalism', targetTitle: 'Эссе: Архитектурный минимализм', relation: 'complements' }
      ]
    },
    {
      id: 'essay_editorial_minimalism',
      title: 'Эссе: Эстетика инженерного молчания',
      type: 'Essay',
      crossLinks: [
        { targetId: 'concept_ontological_autonomy', targetTitle: 'Понятие: Онтологическая Автономия', relation: 'foundational_concept' },
        { targetId: 'spark_a2a_proof', targetTitle: 'Искра: A2A Settlement Proof', relation: 'derived_spec' }
      ]
    },
    {
      id: 'spark_a2a_proof',
      title: 'Искра: A2A Economic Settlement Invariants',
      type: 'Spark',
      crossLinks: [
        { targetId: 'concept_ontological_autonomy', targetTitle: 'Понятие: Онтологическая Автономия', relation: 'grounded_in' },
        { targetId: 'podcast_ep03', targetTitle: 'Выпуск 03: Деонтические инварианты', relation: 'source_podcast' }
      ]
    }
  ];

  // Cmd+K Search Index
  const searchIndex = [
    { id: 'concept_ontological_autonomy', title: 'Онтологическая Автономия', category: 'Понятия Канона', snippet: 'Суверенное ядро системы и онтологический статус агента' },
    { id: 'podcast_ep03', title: 'Выпуск 03: Деонтические инварианты', category: 'Подкасты', snippet: 'Деонтическая логика, доказательства и контракты' },
    { id: 'essay_editorial_minimalism', title: 'Архитектурный минимализм', category: 'Эссе', snippet: 'Принцип элиминативного молчания в UI/UX' },
    { id: 'spark_a2a_proof', title: 'A2A Economic Settlement', category: 'Искры', snippet: 'Протокол межагентских расчетов с нулевым разглашением' },
    { id: 'concept_state_machine', title: 'Детерминированный конечный автомат', category: 'Понятия Канона', snippet: 'Моделирование переходов состояний и транзакционная изоляция' }
  ];

  $: searchResults = searchQuery.trim() === ''
    ? searchIndex
    : searchIndex.filter(item =>
        item.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.snippet.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.category.toLowerCase().includes(searchQuery.toLowerCase())
      );

  function handleCrossLinkClick(sourceId: string, targetId: string, relation: string, targetType: string) {
    selectedNodeId = targetId;
    lastNavTarget = targetId;
    telemetry.trackGraphNavigation(sourceId, targetId, relation, targetType);
  }

  function openSearchModal() {
    isSearchModalOpen = true;
    setTimeout(() => {
      if (searchInputEl) {
        searchInputEl.focus();
      }
    }, 50);
  }

  function closeSearchModal() {
    isSearchModalOpen = false;
    searchQuery = '';
    searchPerformed = false;
  }

  function executeSearch(queryToExecute?: string) {
    const q = queryToExecute !== undefined ? queryToExecute : searchQuery;
    if (!q.trim()) return;

    const matchedCount = searchResults.length;
    telemetry.trackCanonSearchExecute(q.trim(), matchedCount, 12, 'semantic_cmd_k');
    searchPerformed = true;
  }

  function handleSelectSearchResult(item: typeof searchIndex[0]) {
    executeSearch(item.title);
    if (nodes.some(n => n.id === item.id)) {
      handleCrossLinkClick(selectedNodeId, item.id, 'search_result_jump', item.category);
    }
    closeSearchModal();
  }

  function handleKeydown(e: KeyboardEvent) {
    if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
      e.preventDefault();
      if (isSearchModalOpen) {
        closeSearchModal();
      } else {
        openSearchModal();
      }
    } else if (e.key === 'Escape' && isSearchModalOpen) {
      closeSearchModal();
    }
  }

  let unsubscribeTelemetry: () => void;

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
  id="canon-graph-telemetry-root"
  class="bg-[#111319] font-sans min-h-screen pb-16 space-y-4 antialiased {theme === 'dark' ? 'text-[#e2e2eb]' : 'text-[#111319]'}"
>
  <!-- TOP APP BAR -->
  <header
    id="main-header"
    class="sticky top-0 z-40 flex justify-between items-center w-full px-4 py-2 border-b border-[#464554] bg-[#0c0e14]"
    aria-label="Canon Telemetry Main Header"
  >
    <div class="flex items-center space-x-2">
      <span class="font-mono text-sm text-[#c0c1ff] tracking-wider uppercase font-semibold">
        CANON_TELEMETRY
      </span>
    </div>

    <div class="flex items-center space-x-3">
      <div class="flex items-center space-x-1.5 px-2 py-0.5 rounded bg-[#00885d]/20 border border-[#00885d]/40">
        <span class="w-1.5 h-1.5 rounded-full bg-[#4edea3] animate-pulse"></span>
        <span class="font-mono text-[10px] text-[#4edea3] tracking-wide uppercase">STREAM: ACTIVE</span>
      </div>

      <!-- Cmd+K Trigger Button -->
      <button
        id="cmd-k-trigger-btn"
        type="button"
        on:click={openSearchModal}
        aria-label="Invoke Cmd+K Search Modal"
        class="flex items-center space-x-1.5 px-2.5 py-1 rounded bg-[#1e1f26] border border-[#464554] text-[#e2e2eb] hover:border-[#c0c1ff] focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] transition-all text-xs font-mono"
      >
        <span>🔍 Поиск</span>
        <kbd class="px-1.5 py-0.5 rounded bg-[#33343b] text-[#c0c1ff] text-[10px] font-bold border border-[#464554]">
          ⌘K
        </kbd>
      </button>
    </div>
  </header>

  <!-- SUBHEADER: CONTEXT & TIME SELECTOR -->
  <div class="px-4 py-2 border-b border-[#464554]/60 bg-[#191b22] flex flex-col gap-2">
    <div class="flex items-center justify-between text-[#c7c4d7]">
      <div class="flex items-center space-x-2">
        <span class="font-mono text-[10px] text-[#908fa0] uppercase">ENV:</span>
        <span class="font-mono text-[10px] text-[#d0bcff] bg-[#1e1f26] px-1.5 py-0.5 rounded border border-[#464554]/50">
          PROD-GRAPH-v2.4
        </span>
      </div>
      <div class="flex items-center space-x-1 font-mono text-[10px] text-[#c7c4d7]">
        <span>SYNC_RATE: 200ms</span>
      </div>
    </div>

    <!-- Time Window Selector Tabs -->
    <nav aria-label="Time Window Selector" class="grid grid-cols-4 gap-1 p-0.5 bg-[#0c0e14] rounded border border-[#464554]/40 font-mono text-xs">
      <button
        type="button"
        on:click={() => (timeWindow = '1h')}
        class="py-1 text-center rounded transition-all focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] {timeWindow === '1h' ? 'bg-[#c0c1ff] text-[#1000a9] font-medium' : 'text-[#c7c4d7] hover:text-white'}"
        aria-pressed={timeWindow === '1h'}
      >
        1h
      </button>
      <button
        type="button"
        on:click={() => (timeWindow = '24h')}
        class="py-1 text-center rounded transition-all focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] {timeWindow === '24h' ? 'bg-[#c0c1ff] text-[#1000a9] font-medium' : 'text-[#c7c4d7] hover:text-white'}"
        aria-pressed={timeWindow === '24h'}
      >
        24h
      </button>
      <button
        type="button"
        on:click={() => (timeWindow = '7d')}
        class="py-1 text-center rounded transition-all focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] {timeWindow === '7d' ? 'bg-[#c0c1ff] text-[#1000a9] font-medium' : 'text-[#c7c4d7] hover:text-white'}"
        aria-pressed={timeWindow === '7d'}
      >
        7d
      </button>
      <button
        type="button"
        on:click={() => (timeWindow = 'custom')}
        class="py-1 text-center rounded transition-all focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] {timeWindow === 'custom' ? 'bg-[#c0c1ff] text-[#1000a9] font-medium' : 'text-[#c7c4d7] hover:text-white'}"
        aria-pressed={timeWindow === 'custom'}
      >
        Custom
      </button>
    </nav>
  </div>

  <!-- MAIN CANVAS -->
  <main class="px-4 pt-2 space-y-4 max-w-7xl mx-auto">
    <!-- KPI 2x2 GRID -->
    <section id="kpi-grid" aria-label="Key Performance Telemetry Indicators" class="grid grid-cols-2 lg:grid-cols-4 gap-2">
      <!-- Card 1: Graph Navigations -->
      <div id="kpi-card-graph-nav" class="bg-[#191b22] p-3 rounded border border-[#464554]/50 flex flex-col justify-between">
        <div class="flex items-center justify-between mb-1">
          <span class="font-mono text-[10px] text-[#908fa0] uppercase tracking-wider">GRAPH_NAV</span>
        </div>
        <div>
          <div class="flex items-baseline space-x-1">
            <span class="text-xl font-bold text-[#e2e2eb] tracking-tight">142.8k</span>
            <span class="font-mono text-[10px] text-[#4edea3]">+18.4%</span>
          </div>
          <div class="flex items-center justify-between mt-1 pt-1 border-t border-[#464554]/30 font-mono text-[10px]">
            <span class="text-[#908fa0]">AVG_DEPTH</span>
            <span class="text-[#e1e0ff]">4.2 nodes</span>
          </div>
        </div>
      </div>

      <!-- Card 2: Search Volume -->
      <div id="kpi-card-canon-search" class="bg-[#191b22] p-3 rounded border border-[#464554]/50 flex flex-col justify-between">
        <div class="flex items-center justify-between mb-1">
          <span class="font-mono text-[10px] text-[#908fa0] uppercase tracking-wider">CANON_SEARCH</span>
        </div>
        <div>
          <div class="flex items-baseline space-x-1">
            <span class="text-xl font-bold text-[#e2e2eb] tracking-tight">38.6k</span>
            <span class="font-mono text-[10px] text-[#464554]">vol</span>
          </div>
          <div class="flex items-center justify-between mt-1 pt-1 border-t border-[#464554]/30 font-mono text-[10px]">
            <span class="text-[#908fa0]">ENGAGE_RATE</span>
            <span class="text-[#4edea3] font-semibold">89.2%</span>
          </div>
        </div>
      </div>

      <!-- Card 3: Node Pivot Rate -->
      <div id="kpi-card-pivot-rate" class="bg-[#191b22] p-3 rounded border border-[#464554]/50 flex flex-col justify-between">
        <div class="flex items-center justify-between mb-1">
          <span class="font-mono text-[10px] text-[#908fa0] uppercase tracking-wider">NODE_PIVOT_RT</span>
        </div>
        <div>
          <div class="flex items-baseline space-x-1">
            <span class="text-xl font-bold text-[#e2e2eb] tracking-tight">64.7%</span>
            <span class="font-mono text-[10px] text-[#4edea3]">↑ 3.1%</span>
          </div>
          <div class="flex items-center justify-between mt-1 pt-1 border-t border-[#464554]/30 font-mono text-[10px]">
            <span class="text-[#908fa0]">CROSS_EDGE</span>
            <span class="text-[#c7c4d7]">Low drop</span>
          </div>
        </div>
      </div>

      <!-- Card 4: Active Explorers -->
      <div id="kpi-card-active-explorers" class="bg-[#191b22] p-3 rounded border border-[#464554]/50 flex flex-col justify-between">
        <div class="flex items-center justify-between mb-1">
          <span class="font-mono text-[10px] text-[#908fa0] uppercase tracking-wider">ACTIVE_EXPLORERS</span>
        </div>
        <div>
          <div class="flex items-baseline space-x-1">
            <span class="text-xl font-bold text-[#e2e2eb] tracking-tight">3,410</span>
            <span class="font-mono text-[10px] text-[#908fa0]">DAU</span>
          </div>
          <div class="flex items-center justify-between mt-1 pt-1 border-t border-[#464554]/30 font-mono text-[10px]">
            <span class="text-[#908fa0]">GRAPH_AFFINITY</span>
            <span class="text-[#c0c1ff]">76.4%</span>
          </div>
        </div>
      </div>
    </section>

    <!-- SECTION: CANON CROSS-LINK GRAPH NAVIGATION (JTBD Requirement) -->
    <section
      id="graph-crosslink-section"
      class="bg-[#191b22] rounded border border-[#464554]/50 p-4 space-y-3"
      aria-label="Canon Content Graph Cross-Link Navigation"
    >
      <header class="flex items-center justify-between border-b border-[#464554]/30 pb-2">
        <div class="flex items-center space-x-2">
          <span class="font-mono text-xs text-[#c0c1ff] uppercase tracking-wider font-semibold">
            🔗 Canon Concept Graph Navigation
          </span>
        </div>
        <span class="font-mono text-[10px] text-[#908fa0]">
          ACTIVE_NODE: <code class="text-[#4edea3]">{selectedNodeId}</code>
        </span>
      </header>

      <p class="text-xs text-[#c7c4d7]">
        Клик по перекрёстным ссылкам (cross-links) мгновенно переключает связный узел Канона и отправляет событие <code class="text-[#c0c1ff]">graph_node_navigation</code>.
      </p>

      <!-- Active Node Graph View Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-3 pt-1">
        {#each nodes as node}
          <div
            id="node-card-{node.id}"
            class="bg-[#0c0e14] p-3 rounded border transition-colors {selectedNodeId === node.id ? 'border-[#c0c1ff] ring-1 ring-[#c0c1ff]' : 'border-[#464554]/40 hover:border-[#464554]'}"
          >
            <div class="flex justify-between items-start mb-2">
              <span class="font-mono text-[10px] px-1.5 py-0.5 rounded font-semibold {node.type === 'CanonConcept' ? 'bg-[#571bc1]/40 text-[#d0bcff] border border-[#d0bcff]/30' : node.type === 'Podcast' ? 'bg-[#00885d]/30 text-[#4edea3] border border-[#4edea3]/30' : 'bg-[#33343b] text-[#c0c1ff] border border-[#464554]'}">
                {node.type}
              </span>
              {#if selectedNodeId === node.id}
                <span class="font-mono text-[10px] text-[#4edea3] font-bold">[SELECTED_NODE]</span>
              {/if}
            </div>

            <h3 class="text-sm font-semibold text-[#e2e2eb] mb-2">{node.title}</h3>

            <!-- Cross-Links List -->
            <div class="space-y-1.5 pt-2 border-t border-[#464554]/30">
              <span class="font-mono text-[10px] text-[#908fa0] uppercase block">Cross-Links:</span>
              <div class="flex flex-col gap-1.5">
                {#each node.crossLinks as link}
                  <button
                    id="crosslink-{node.id}-to-{link.targetId}"
                    type="button"
                    on:click={() => handleCrossLinkClick(node.id, link.targetId, link.relation, 'CanonNode')}
                    class="flex items-center justify-between p-1.5 rounded bg-[#1e1f26] hover:bg-[#33343b] border border-[#464554]/50 text-left focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] transition-all group"
                  >
                    <span class="font-mono text-xs text-[#c0c1ff] group-hover:text-white truncate">
                      → {link.targetTitle}
                    </span>
                    <span class="font-mono text-[10px] text-[#908fa0] ml-2 shrink-0">
                      [{link.relation}]
                    </span>
                  </button>
                {/each}
              </div>
            </div>
          </div>
        {/each}
      </div>
    </section>

    <!-- SECTION: TOP TRAVERSAL PATHWAYS -->
    <section class="bg-[#191b22] rounded border border-[#464554]/50 p-3 space-y-3">
      <div class="flex items-center justify-between border-b border-[#464554]/30 pb-2">
        <h2 class="font-mono text-xs uppercase text-[#e2e2eb] tracking-wider font-semibold">Top Traversal Pathways</h2>
        <span class="font-mono text-[10px] text-[#908fa0]">SORT: FLOW_VOL</span>
      </div>

      <div class="space-y-2">
        <div class="bg-[#0c0e14] p-2 rounded border border-[#464554]/40 font-mono text-xs">
          <div class="flex items-center justify-between text-[#908fa0] mb-1">
            <span class="text-[#c0c1ff]">SEQ_PATH_01</span>
            <span class="text-[#4edea3] font-semibold">42% SHARE</span>
          </div>
          <div class="flex flex-wrap items-center gap-1 text-[11px]">
            <span class="bg-[#1e1f26] text-[#e2e2eb] px-1.5 py-0.5 rounded border border-[#464554]/30">Search: "Protocols"</span>
            <span class="text-[#908fa0]">→</span>
            <span class="bg-[#571bc1]/40 text-[#e9ddff] px-1.5 py-0.5 rounded border border-[#d0bcff]/30">Entity:[Core_Spec]</span>
            <span class="text-[#908fa0]">→</span>
            <span class="bg-[#8083ff]/20 text-[#e1e0ff] px-1.5 py-0.5 rounded border border-[#c0c1ff]/30">Node:[Auth_V2]</span>
          </div>
        </div>

        <div class="bg-[#0c0e14] p-2 rounded border border-[#464554]/40 font-mono text-xs">
          <div class="flex items-center justify-between text-[#908fa0] mb-1">
            <span class="text-[#c0c1ff]">SEQ_PATH_02</span>
            <span class="text-[#c7c4d7] font-semibold">28% SHARE</span>
          </div>
          <div class="flex flex-wrap items-center gap-1 text-[11px]">
            <span class="bg-[#1e1f26] text-[#e2e2eb] px-1.5 py-0.5 rounded border border-[#464554]/30">Direct Search</span>
            <span class="text-[#908fa0]">→</span>
            <span class="bg-[#1e1f26] text-[#4edea3] px-1.5 py-0.5 rounded border border-[#464554]/30">Schema Graph Expand</span>
          </div>
        </div>
      </div>

      <!-- Hop Depth Segmented Distribution Bar -->
      <div class="pt-2 border-t border-[#464554]/30 space-y-1 font-mono text-xs">
        <div class="flex items-center justify-between text-[10px]">
          <span class="text-[#908fa0] uppercase">Hop Depth Spectrum</span>
          <span class="text-[#c7c4d7]">TRAVERSAL_SPREAD</span>
        </div>
        <div class="h-2 w-full bg-[#0c0e14] rounded-full overflow-hidden flex">
          <div class="bg-[#c0c1ff] h-full" style="width: 34%" title="1 Hop: 34%"></div>
          <div class="bg-[#8083ff] h-full" style="width: 48%" title="2-3 Hops: 48%"></div>
          <div class="bg-[#571bc1] h-full" style="width: 18%" title="4+ Hops: 18%"></div>
        </div>
      </div>
    </section>

    <!-- SECTION: REAL-TIME TELEMETRY EVENT LOG STREAM -->
    <section
      id="audit-event-trace-section"
      class="bg-[#191b22] rounded border border-[#464554]/50 p-3 space-y-2"
      aria-label="Real-time Audit Telemetry Event Stream"
    >
      <div class="flex items-center justify-between border-b border-[#464554]/30 pb-2">
        <div class="flex items-center space-x-1.5">
          <h2 class="font-mono text-xs uppercase text-[#e2e2eb] tracking-wider font-semibold">Audit / Event Trace</h2>
        </div>
        <div class="flex items-center space-x-2 font-mono text-[10px]">
          <span class="w-2 h-2 rounded-full bg-[#4edea3] animate-pulse"></span>
          <span class="text-[#908fa0]">EVENTS: {loggedEvents.length}</span>
        </div>
      </div>

      <!-- Real-time Event Lines Terminal Style -->
      <div
        id="audit-trace-log"
        class="bg-[#0c0e14] p-3 rounded border border-[#464554]/40 space-y-2 max-h-48 overflow-y-auto text-left font-mono text-[11px]"
        role="region"
        aria-label="Event Log Output"
      >
        {#if loggedEvents.length === 0}
          <div class="text-[#908fa0] italic">No telemetry events recorded yet. Click a cross-link or perform Cmd+K search.</div>
        {:else}
          {#each loggedEvents as evt}
            <div class="flex flex-col space-y-0.5 pb-1.5 border-b border-[#464554]/20">
              <div class="flex items-center justify-between">
                <span class="font-bold {evt.type === 'graph_node_navigation' ? 'text-[#c0c1ff]' : evt.type === 'canon_search_execute' ? 'text-[#4edea3]' : 'text-[#d0bcff]'}">
                  {evt.type}
                </span>
                <span class="text-[#908fa0] text-[10px]">{evt.timestamp.slice(11, 19)}</span>
              </div>
              <div class="text-[#c7c4d7] break-all">
                {JSON.stringify(evt.payload)}
              </div>
            </div>
          {/each}
        {/if}
      </div>
    </section>
  </main>

  <!-- CMD+K SEARCH MODAL (WCAG 2.1 AA Compliant) -->
  {#if isSearchModalOpen}
    <div
      id="cmd-k-search-modal"
      role="dialog"
      aria-modal="true"
      aria-labelledby="cmd-k-search-label"
      class="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-start justify-center pt-16 px-4"
    >
      <div
        class="bg-[#191b22] border border-[#c0c1ff]/60 rounded-xl max-w-xl w-full p-4 shadow-2xl space-y-3 font-sans text-[#e2e2eb]"
      >
        <div class="flex justify-between items-center border-b border-[#464554]/50 pb-2">
          <label id="cmd-k-search-label" for="cmd-k-search-input" class="font-mono text-xs font-semibold text-[#c0c1ff] uppercase">
            ⚡ Мгновенный Поиск по Канону (Cmd+K / Ctrl+K)
          </label>
          <button
            id="cmd-k-close-btn"
            type="button"
            on:click={closeSearchModal}
            aria-label="Закрыть поиск"
            class="px-2 py-0.5 rounded bg-[#33343b] text-[#e2e2eb] hover:bg-[#464554] focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] font-mono text-xs"
          >
            Esc
          </button>
        </div>

        <form on:submit|preventDefault={() => executeSearch()} class="space-y-3">
          <div class="relative">
            <input
              id="cmd-k-search-input"
              type="search"
              bind:this={searchInputEl}
              bind:value={searchQuery}
              placeholder="Поиск по понятию, выпуску, эссе или искре..."
              class="w-full px-3 py-2.5 bg-[#0c0e14] border border-[#464554] rounded-lg text-sm text-[#e2e2eb] placeholder-[#908fa0] focus:outline-none focus:border-[#c0c1ff] focus:ring-2 focus:ring-[#c0c1ff] font-mono"
              aria-label="Поисковый запрос Канона"
            />
          </div>

          <!-- Search Execution Controls -->
          <div class="flex justify-between items-center text-xs font-mono text-[#908fa0]">
            <span>Результатов: {searchResults.length}</span>
            <button
              id="cmd-k-execute-btn"
              type="submit"
              class="px-3 py-1 bg-[#c0c1ff] text-[#1000a9] font-bold rounded hover:bg-[#e1e0ff] focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] transition-colors"
            >
              Выполнить Поиск ↵
            </button>
          </div>
        </form>

        <!-- Search Results List -->
        <div
          id="cmd-k-results-list"
          class="space-y-2 max-h-60 overflow-y-auto pt-2 border-t border-[#464554]/40"
          role="listbox"
          aria-label="Результаты поиска по смыслу"
        >
          {#if searchResults.length === 0}
            <div class="p-3 text-center text-xs font-mono text-[#908fa0]">
              Ничего не найдено по запросу "{searchQuery}".
            </div>
          {:else}
            {#each searchResults as item}
              <button
                id="search-result-item-{item.id}"
                type="button"
                on:click={() => handleSelectSearchResult(item)}
                class="w-full p-2.5 rounded bg-[#0c0e14] hover:bg-[#1e1f26] border border-[#464554]/40 text-left focus:outline-none focus:ring-2 focus:ring-[#c0c1ff] transition-all space-y-1 group"
                role="option"
                aria-selected="false"
              >
                <div class="flex justify-between items-center">
                  <span class="font-semibold text-xs text-[#e2e2eb] group-hover:text-[#c0c1ff]">
                    {item.title}
                  </span>
                  <span class="font-mono text-[10px] px-1.5 py-0.5 rounded bg-[#1e1f26] text-[#d0bcff] border border-[#464554]/50">
                    {item.category}
                  </span>
                </div>
                <p class="text-[11px] text-[#c7c4d7] line-clamp-1">
                  {item.snippet}
                </p>
              </button>
            {/each}
          {/if}
        </div>
      </div>
    </div>
  {/if}
</div>
