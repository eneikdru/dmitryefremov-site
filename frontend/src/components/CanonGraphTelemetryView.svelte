<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export let theme: 'dark' | 'light' = 'dark';

  let searchOpen: boolean = false;
  let searchQuery: string = '';
  let activeFilter: 'all' | 'graph' | 'search' | 'dwell' = 'all';
  let loggedEvents: TelemetryEvent[] = [];
  let unsubscribeTelemetry: () => void;

  // Mock graph nodes and cross-links data
  const canonNodes = [
    {
      id: 'canon-core',
      title: 'Ядро Канона: Система взглядов',
      type: 'Concept',
      crossLinks: [
        { id: 'podcast-ep-01', title: 'Выпуск 1: Автономия мышления', type: 'Podcast' },
        { id: 'essay-deontic', title: 'Эссе: Деонтическая логика и софт', type: 'Essay' },
        { id: 'spark-a2a', title: 'Искра: A2A Экономика', type: 'Spark' }
      ]
    },
    {
      id: 'podcast-ep-01',
      title: 'Выпуск 1: Автономия мышления',
      type: 'Podcast',
      crossLinks: [
        { id: 'canon-core', title: 'Ядро Канона: Система взглядов', type: 'Concept' },
        { id: 'spark-a2a', title: 'Искра: A2A Экономика', type: 'Spark' }
      ]
    },
    {
      id: 'essay-deontic',
      title: 'Эссе: Деонтическая логика и софт',
      type: 'Essay',
      crossLinks: [
        { id: 'canon-core', title: 'Ядро Канона: Система взглядов', type: 'Concept' }
      ]
    }
  ];

  const searchIndex = [
    { id: 'canon-core', title: 'Ядро Канона: Система взглядов', snippet: 'Центральная онтология и концептуальный градиент автора' },
    { id: 'podcast-ep-01', title: 'Выпуск 1: Автономия мышления', snippet: 'Подкаст "Системы и реальность" о независимых решениях' },
    { id: 'essay-deontic', title: 'Эссе: Деонтическая логика', snippet: 'Применение модальной логики норм и запретов в софте' },
    { id: 'spark-a2a', title: 'Искра: A2A Экономика', snippet: 'Короткая заметка об автономном взаимодействии агентов' },
    { id: 'node-jwt-sec', title: 'SecurityModel: JWT Spec', snippet: 'Спецификация аутентификации и сессионного контекста' }
  ];

  $: searchResults = searchQuery.trim()
    ? searchIndex.filter(item =>
        item.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.snippet.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : searchIndex;

  onMount(() => {
    loggedEvents = telemetry.getEvents();
    unsubscribeTelemetry = telemetry.subscribe(() => {
      loggedEvents = telemetry.getEvents();
    });

    function handleKeyDown(e: KeyboardEvent) {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        searchOpen = !searchOpen;
      } else if (e.key === 'Escape' && searchOpen) {
        searchOpen = false;
      }
    }

    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      if (unsubscribeTelemetry) unsubscribeTelemetry();
    };
  });

  function handleCrossLinkClick(sourceId: string, targetId: string) {
    telemetry.trackGraphNavigation(sourceId, targetId, 'cross_link');
  }

  function handleSearchSubmit() {
    telemetry.trackSearchUtilization(searchQuery, searchResults.length);
  }

  function handleSelectSearchResult(item: { id: string; title: string }) {
    telemetry.trackSearchUtilization(searchQuery, searchResults.length, item.id);
    searchOpen = false;
  }
</script>

<div
  id="canon-telemetry-root"
  class="space-y-6 max-w-5xl mx-auto p-4 sm:p-6 rounded-2xl border transition-colors duration-200 {theme === 'dark' ? 'bg-[#0f131c] text-[#dfe2ef] border-[#31353f]' : 'bg-white text-[#0f172a] border-[#e2e8f0]'}"
>
  <!-- Header / Controls Bar -->
  <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b pb-4 {theme === 'dark' ? 'border-[#31353f]' : 'border-[#e2e8f0]'}">
    <div>
      <div class="flex items-center space-x-2">
        <span class="material-symbols-outlined text-[#00f0ff] text-xl">hub</span>
        <h1 class="text-xl font-bold font-headline-md tracking-tight {theme === 'dark' ? 'text-[#dbfcff]' : 'text-[#006970]'}">
          Canon Navigation & Telemetry
        </h1>
      </div>
      <p class="text-xs text-[#849495] mt-1 font-body-sm">
        Отслеживание графовых переходов и Cmd+K поиска в Каноне
      </p>
    </div>

    <div class="flex items-center gap-3">
      <!-- Cmd+K Trigger Button -->
      <button
        type="button"
        id="cmd-k-trigger"
        aria-label="Invoke Cmd+K Search Modal"
        on:click={() => (searchOpen = true)}
        class="px-3 py-2 rounded-lg text-xs font-mono border flex items-center gap-2 cursor-pointer transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#00f0ff] {theme === 'dark' ? 'bg-[#1c1f29] text-[#dfe2ef] border-[#3b494b] hover:border-[#00f0ff]' : 'bg-[#f1f5f9] text-[#0f172a] border-[#cbd5e1] hover:border-[#006970]'}"
      >
        <span class="material-symbols-outlined text-sm">search</span>
        <span>Поиск по Канону</span>
        <kbd class="px-1.5 py-0.5 rounded text-[10px] font-bold {theme === 'dark' ? 'bg-[#31353f] text-[#dbfcff]' : 'bg-[#e2e8f0] text-[#0f172a]'}">
          ⌘K
        </kbd>
      </button>
    </div>
  </div>

  <!-- Graph Navigation Nodes Section -->
  <section class="space-y-4" aria-labelledby="graph-section-title">
    <div class="flex items-center justify-between">
      <h2 id="graph-section-title" class="text-base font-bold flex items-center gap-2 {theme === 'dark' ? 'text-[#dbfcff]' : 'text-[#006970]'}">
        <span class="material-symbols-outlined text-lg">account_tree</span>
        Узлы Канона и Перекрёстные Ссылки
      </h2>
      <span class="text-xs text-[#849495]">Cross-link graph navigation</span>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      {#each canonNodes as node}
        <div class="p-4 rounded-xl border space-y-3 {theme === 'dark' ? 'bg-[#181b25] border-[#3b494b]' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
          <div class="flex items-center justify-between">
            <span class="px-2 py-0.5 rounded text-[10px] font-mono font-semibold uppercase tracking-wider {theme === 'dark' ? 'bg-[#262a34] text-[#00f0ff] border border-[#00f0ff]/30' : 'bg-[#e0f2fe] text-[#0369a1] border border-[#0369a1]/30'}">
              {node.type}
            </span>
            <span class="text-[10px] font-mono text-[#849495]">{node.id}</span>
          </div>

          <h3 class="font-semibold text-sm leading-snug {theme === 'dark' ? 'text-[#dfe2ef]' : 'text-[#0f172a]'}">
            {node.title}
          </h3>

          <div class="pt-2 border-t space-y-1.5 {theme === 'dark' ? 'border-[#31353f]' : 'border-[#e2e8f0]'}">
            <span class="text-[11px] font-mono text-[#849495] block">Связанные понятия:</span>
            <div class="space-y-1">
              {#each node.crossLinks as link}
                <a
                  href={`#${link.id}`}
                  aria-label={`Перейти к узлу ${link.title}`}
                  on:click|preventDefault={() => handleCrossLinkClick(node.id, link.id)}
                  class="group flex items-center justify-between p-1.5 rounded text-xs transition-colors cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#00f0ff] {theme === 'dark' ? 'hover:bg-[#262a34] text-[#6ffbbe]' : 'hover:bg-[#e2e8f0] text-[#047857]'}"
                >
                  <span class="truncate underline underline-offset-2">{link.title}</span>
                  <span class="material-symbols-outlined text-xs group-hover:translate-x-0.5 transition-transform">
                    east
                  </span>
                </a>
              {/each}
            </div>
          </div>
        </div>
      {/each}
    </div>
  </section>

  <!-- Telemetry Event Stream Feed -->
  <section class="space-y-3 pt-4 border-t {theme === 'dark' ? 'border-[#31353f]' : 'border-[#e2e8f0]'}" aria-labelledby="telemetry-log-title">
    <div class="flex items-center justify-between">
      <h2 id="telemetry-log-title" class="text-sm font-bold flex items-center gap-2 {theme === 'dark' ? 'text-[#dbfcff]' : 'text-[#006970]'}">
        <span class="material-symbols-outlined text-base">stream</span>
        Лог Телеметрии Событий
      </h2>
      <span class="text-xs font-mono text-[#4edea3]">
        {loggedEvents.length} событий зарегистрировано
      </span>
    </div>

    <div
      id="telemetry-events-feed"
      class="max-h-56 overflow-y-auto rounded-xl p-3 font-mono text-xs space-y-2 border {theme === 'dark' ? 'bg-[#0a0e17] border-[#31353f] text-[#dfe2ef]' : 'bg-[#f8fafc] border-[#cbd5e1] text-[#0f172a]'}"
    >
      {#if loggedEvents.length === 0}
        <div class="text-center py-6 text-[#849495]">
          Нажмите на перекрёстные ссылки выше или вызовите ⌘K поиск для отправки событий.
        </div>
      {:else}
        {#each [...loggedEvents].reverse() as event}
          <div class="p-2 rounded border flex flex-col sm:flex-row sm:items-center justify-between gap-1 {theme === 'dark' ? 'bg-[#181b25] border-[#3b494b]' : 'bg-white border-[#e2e8f0]'}">
            <div class="flex items-center gap-2">
              <span class="px-1.5 py-0.5 rounded text-[10px] font-bold uppercase {event.type === 'graph_navigation' ? 'bg-[#65f2b5]/20 text-[#6ffbbe]' : event.type === 'search_utilization' ? 'bg-[#00f0ff]/20 text-[#00f0ff]' : 'bg-[#31353f] text-[#dfe2ef]'}">
                {event.type}
              </span>
              <span class="text-[11px] text-[#849495]">
                {new Date(event.timestamp).toLocaleTimeString()}
              </span>
            </div>

            <div class="text-[11px] truncate max-w-md text-[#dfe2ef]">
              {JSON.stringify(event.payload)}
            </div>
          </div>
        {/each}
      {/if}
    </div>
  </section>

  <!-- Cmd+K Modal Dialog -->
  {#if searchOpen}
    <div
      id="search-modal"
      role="dialog"
      aria-modal="true"
      aria-labelledby="search-dialog-title"
      class="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-start justify-center pt-20 p-4 z-50"
    >
      <div
        class="max-w-xl w-full rounded-2xl border shadow-2xl overflow-hidden font-sans space-y-0 {theme === 'dark' ? 'bg-[#1c1f29] border-[#3b494b] text-[#dfe2ef]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}"
      >
        <form on:submit|preventDefault={handleSearchSubmit} class="p-3 border-b flex items-center gap-3 {theme === 'dark' ? 'border-[#31353f]' : 'border-[#e2e8f0]'}">
          <span class="material-symbols-outlined text-[#00f0ff]">search</span>
          <input
            id="search-input"
            type="search"
            bind:value={searchQuery}
            placeholder="Поиск по всему корпусу Канона (Cmd+K)..."
            aria-label="Search Canon"
            class="w-full bg-transparent border-none text-sm focus:outline-none focus:ring-0 {theme === 'dark' ? 'text-white placeholder-[#849495]' : 'text-[#0f172a] placeholder-[#94a3b8]'}"
          />
          <button
            type="button"
            on:click={() => (searchOpen = false)}
            aria-label="Close search dialog"
            class="p-1 rounded text-xs text-[#849495] hover:text-[#dfe2ef] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#00f0ff]"
          >
            Esc
          </button>
        </form>

        <div class="p-3 max-h-80 overflow-y-auto space-y-2">
          <div id="search-dialog-title" class="text-xs font-mono text-[#849495] px-2">
            Результаты поиска ({searchResults.length}):
          </div>

          {#each searchResults as item}
            <button
              type="button"
              on:click={() => handleSelectSearchResult(item)}
              class="w-full text-left p-2.5 rounded-lg border transition-colors flex items-start justify-between gap-3 cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#00f0ff] {theme === 'dark' ? 'bg-[#181b25] border-[#3b494b] hover:border-[#00f0ff]' : 'bg-[#f8fafc] border-[#e2e8f0] hover:border-[#006970]'}"
            >
              <div>
                <div class="font-semibold text-sm text-[#00f0ff]">
                  {item.title}
                </div>
                <div class="text-xs text-[#849495] mt-0.5">
                  {item.snippet}
                </div>
              </div>
              <span class="material-symbols-outlined text-sm text-[#849495]">
                north_east
              </span>
            </button>
          {/each}
        </div>
      </div>
    </div>
  {/if}
</div>
