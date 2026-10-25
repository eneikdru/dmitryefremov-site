<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry } from '../lib/telemetry';
  import {
    CANON_ROUTES,
    CANON_PATTERNS,
    type CanonRoute,
    type CanonPattern,
    fetchLlmsTxt,
    fetchWellKnownAiAgentJson,
    fetchAgentTelemetry,
    submitAgentOrder
  } from '../lib/canon';

  export let theme: 'dark' | 'light' = 'dark';

  let activeFilter: 'all' | 'read' | 'query' | 'webhooks' = 'all';
  let isMasterActive: boolean = true;
  let isLiveSync: boolean = true;
  let expandedSchemas: Record<string, boolean> = { 'route-wellknown-json': true };
  let routeTestData: Record<string, string> = {};
  let routeStatusMap: Record<string, boolean> = {
    'route-lore-summary': true,
    'route-wellknown-json': true,
    'route-agent-orders': true,
    'route-events-stream': false
  };

  let copySuccess: boolean = false;
  let testOrderModalOpen: boolean = false;
  let testOrderForm = {
    clientName: 'Agent_Auto_Test',
    clientEmail: 'agent@test.io',
    serviceType: 'MVP',
    requirements: 'Automated architectural review test'
  };
  let testOrderResult: string | null = null;
  let recentInvocations = [
    { agent: 'LangChain_Agent_04', route: 'GET /llms.txt', status: '200 OK', latency: '18ms' },
    { agent: 'Claude_Codex_Sync', route: 'GET /.well-known/ai-agent.json', status: '200 OK', latency: '24ms' },
    { agent: 'AutoGPT_Runner', route: 'POST /api/v1/agent/orders', status: '201 Created', latency: '42ms' }
  ];

  export let activeGraphNode: string = 'concept-deontic-logic';

  function handleGraphCrossLink(sourceId: string, targetId: string, targetType: string, label: string) {
    activeGraphNode = targetId;
    telemetry.trackCrossLinkClick(sourceId, targetId, targetType, label);
  }

  onMount(async () => {
    // Load live telemetry if available
    const telemetry = await fetchAgentTelemetry();
    if (telemetry && telemetry.totalInvocations) {
      recentInvocations = [
        { agent: 'Live_Agent_Service', route: 'GET /api/v1/agent/telemetry', status: '200 OK', latency: '12ms' },
        ...recentInvocations
      ];
    }
  });

  $: filteredRoutes = CANON_ROUTES.filter(route => {
    if (activeFilter === 'all') return true;
    return route.category === activeFilter;
  });

  function toggleSchema(routeId: string) {
    expandedSchemas[routeId] = !expandedSchemas[routeId];
  }

  function toggleRouteStatus(routeId: string) {
    routeStatusMap[routeId] = !routeStatusMap[routeId];
  }

  async function testRoute(route: CanonRoute) {
    if (route.path === '/llms.txt') {
      const text = await fetchLlmsTxt();
      routeTestData[route.id] = text.slice(0, 300) + '...';
      expandedSchemas[route.id] = true;
    } else if (route.path === '/.well-known/ai-agent.json') {
      const json = await fetchWellKnownAiAgentJson();
      routeTestData[route.id] = JSON.stringify(json, null, 2);
      expandedSchemas[route.id] = true;
    } else if (route.path === '/api/v1/agent/orders') {
      testOrderModalOpen = true;
    }
  }

  let copiedPatternId: string | null = null;
  function copyPattern(p: CanonPattern) {
    if (typeof navigator !== 'undefined' && navigator.clipboard) {
      navigator.clipboard.writeText(p.directivePrompt);
    }
    copiedPatternId = p.id;
    setTimeout(() => {
      if (copiedPatternId === p.id) {
        copiedPatternId = null;
      }
    }, 2000);
  }

  async function handleTestOrderSubmit() {
    testOrderResult = 'Отправка...';
    const res = await submitAgentOrder(testOrderForm);
    if (res.success) {
      const orderId = res.data?.orderId || res.data?.id || 'ORD-9821';
      testOrderResult = `Успешно (201 Created)! ID: ${orderId}`;
      telemetry.trackCanonSubmitConverted(orderId, 0, {
        service_type: testOrderForm.serviceType,
        client_name: testOrderForm.clientName,
        source: 'agent_api'
      });
    } else {
      testOrderResult = `Ошибка: ${res.error}`;
    }
  }

  function copyBaseEndpoint() {
    const base = typeof window !== 'undefined' ? `${window.location.origin}/llms.txt` : 'https://dmitryefremov.com/llms.txt';
    navigator.clipboard.writeText(base);
    copySuccess = true;
    setTimeout(() => {
      copySuccess = false;
    }, 2000);
  }
</script>

<div
  id="canon-gateway-root"
  class="space-y-6 transition-colors duration-200 {theme === 'dark' ? 'text-[#dfe2ee]' : 'text-[#0f131c]'}"
>
  <!-- Header / Subheader -->
  <header id="canon-gateway-header" class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-[#181c24] border border-[#3a494b]/40 rounded-xl p-4 shadow-md">
    <div class="flex items-center gap-3">
      <div class="p-2 rounded-lg bg-[#00f2fe]/10 border border-[#00f2fe]/30 text-[#00f2fe]">
        
      </div>
      <div>
        <h1 id="gateway-header-title" class="text-xl font-bold tracking-tight text-[#e0fdff]">
          Canon Gateway & Machine-Readable AI Access
        </h1>
        <p class="text-xs font-mono text-[#b9cacb] mt-0.5">
          Публичная экспозиция канона и машиночитаемых эндпоинтов (llms.txt, JSON, OpenAPI)
        </p>
      </div>
    </div>

    <div class="flex items-center gap-3">
      
    </div>
  </header>

  <!-- CANON PROMPT PATTERNS FOR AGENTS (TIER 1 ARCHITECTURE) -->
  <section id="canon-patterns-section" class="bg-[#12151e] border border-[#3a494b]/50 rounded-2xl p-6 space-y-6">
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-[#1f2433] pb-4">
      <div>
        <h2 class="text-lg font-bold text-white tracking-tight">Системные директивы для автономных агентов</h2>
      </div>
      
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      {#each CANON_PATTERNS as pattern}
        <div class="bg-[#0a0e16] border border-[#1f2433] hover:border-[#38bdf8]/50 rounded-xl p-4 flex flex-col justify-between space-y-3 transition-colors">
          <div class="space-y-2">
            <div class="flex items-center justify-between text-xs font-mono"><span class="text-[#38bdf8] font-bold">{pattern.category || pattern.directiveName}</span></div>
            <h3 class="font-bold text-white text-sm leading-snug">{pattern.title}</h3>
            <p class="text-xs text-[#94a3b8] leading-relaxed">{pattern.useCase}</p>
            <div class="p-2.5 rounded bg-[#07090e] border border-[#1f2433] font-mono text-[11px] text-[#cbd5e1] leading-relaxed break-words whitespace-pre-wrap">
              {pattern.directivePrompt}
            </div>
          </div>
          <button
            type="button"
            on:click={() => copyPattern(pattern)}
            class="w-full py-2 px-3 rounded-lg text-xs font-mono font-bold border transition-all flex items-center justify-center gap-2 {copiedPatternId === pattern.id ? 'bg-[#34d399] text-[#090a0f] border-[#34d399]' : 'bg-[#181c24] text-[#38bdf8] border-[#3a494b] hover:bg-[#38bdf8] hover:text-[#090a0f]'}"
          >
            {#if copiedPatternId === pattern.id}
              <span>Директива скопирована</span>
            {:else}
              <span>Скопировать директиву</span>
            {/if}
          </button>
        </div>
      {/each}
    </div>
  </section>



  <!-- Canon Concept Graph & Interactive Cross-Links -->
  <section id="canon-graph-section" class="bg-[#1c2028] border border-[#3a494b]/40 rounded-xl p-5 space-y-4 shadow-xl">
    <div class="flex items-center justify-between border-b border-[#3a494b]/20 pb-3">
      <div class="flex items-center gap-2">
        
        <div>
          <h2 id="canon-graph-title" class="text-base font-semibold text-[#dfe2ee]">
            Единый граф понятий Канона (Canon Cross-Links)
          </h2>
          <p class="text-xs font-mono text-[#b9cacb]">
            Перекрёстные ссылки между понятиями, подкастами и эссе с отправкой телеметрии навигации.
          </p>
        </div>
      </div>
      <span class="text-xs font-mono text-[#67f4b7] bg-[#0a0e16] px-2.5 py-1 rounded border border-[#3a494b]/30">
        ACTIVE_NODE: {activeGraphNode}
      </span>
    </div>

    <!-- Node Navigation Matrix -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-3 font-mono text-xs">
      <div class="p-3 bg-[#0a0e16] border border-[#3a494b]/30 rounded-lg space-y-2">
        <div class="flex items-center justify-between text-[#00f2fe] text-[11px] font-bold">
          <span>ПОНЯТИЕ КАНОНА</span>
          <span>[CORE]</span>
        </div>
        <h3 class="font-bold text-[#dfe2ee] text-sm">Деонтическая логика</h3>
        <p class="text-[11px] text-[#b9cacb] leading-relaxed">
          Модальная логика нормативных требований и предписаний в автономных системах.
        </p>
        <div class="pt-2 border-t border-[#3a494b]/20 flex flex-wrap gap-1.5">
          <button
            id="crosslink-to-podcast-1"
            type="button"
            on:click={() => handleGraphCrossLink('concept-deontic-logic', 'podcast-ep-01', 'podcast', 'Выпуск 01')}
            aria-label="Перейти к подкасту Выпуск 01"
            class="px-2 py-1 rounded bg-[#262a33] text-[#67f4b7] hover:border-[#67f4b7] focus:outline-none focus:ring-2 focus:ring-[#00f2fe] border border-[#3a494b]/40 cursor-pointer text-[10px]"
          >
            Выпуск 01 →
          </button>
          <button
            id="crosslink-to-essay-1"
            type="button"
            on:click={() => handleGraphCrossLink('concept-deontic-logic', 'essay-deontic', 'essay', 'Эссе по логике')}
            aria-label="Перейти к Эссе по логике"
            class="px-2 py-1 rounded bg-[#262a33] text-[#00f2fe] hover:border-[#00f2fe] focus:outline-none focus:ring-2 focus:ring-[#00f2fe] border border-[#3a494b]/40 cursor-pointer text-[10px]"
          >
            Эссе →
          </button>
        </div>
      </div>

      <div class="p-3 bg-[#0a0e16] border border-[#3a494b]/30 rounded-lg space-y-2">
        <div class="flex items-center justify-between text-[#67f4b7] text-[11px] font-bold">
          <span>ПОНЯТИЕ КАНОНА</span>
          <span>[SYSTEMS]</span>
        </div>
        <h3 class="font-bold text-[#dfe2ee] text-sm">Теория ограничений (TOC)</h3>
        <p class="text-[11px] text-[#b9cacb] leading-relaxed">
          Локализация узких мест в архитектуре распределённых агентов.
        </p>
        <div class="pt-2 border-t border-[#3a494b]/20 flex flex-wrap gap-1.5">
          <button
            id="crosslink-to-podcast-2"
            type="button"
            on:click={() => handleGraphCrossLink('concept-toc', 'podcast-ep-02', 'podcast', 'Выпуск 02')}
            aria-label="Перейти к подкасту Выпуск 02"
            class="px-2 py-1 rounded bg-[#262a33] text-[#67f4b7] hover:border-[#67f4b7] focus:outline-none focus:ring-2 focus:ring-[#00f2fe] border border-[#3a494b]/40 cursor-pointer text-[10px]"
          >
            Выпуск 02 →
          </button>
          <button
            id="crosslink-to-spark-1"
            type="button"
            on:click={() => handleGraphCrossLink('concept-toc', 'spark-telegram-mvp', 'spark', 'Telegram Spark')}
            aria-label="Перейти к Заметке в Telegram"
            class="px-2 py-1 rounded bg-[#262a33] text-[#c3c0ff] hover:border-[#c3c0ff] focus:outline-none focus:ring-2 focus:ring-[#00f2fe] border border-[#3a494b]/40 cursor-pointer text-[10px]"
          >
            Spark →
          </button>
        </div>
      </div>

      <div class="p-3 bg-[#0a0e16] border border-[#3a494b]/30 rounded-lg space-y-2">
        <div class="flex items-center justify-between text-[#c3c0ff] text-[11px] font-bold">
          <span>ПОНЯТИЕ КАНОНА</span>
          <span>[A2A]</span>
        </div>
        <h3 class="font-bold text-[#dfe2ee] text-sm">A2A Экономика</h3>
        <p class="text-[11px] text-[#b9cacb] leading-relaxed">
          Автономные транзакции и машиночитаемая спецификация сервисов.
        </p>
        <div class="pt-2 border-t border-[#3a494b]/20 flex flex-wrap gap-1.5">
          <button
            id="crosslink-to-canon-llmstxt"
            type="button"
            on:click={() => handleGraphCrossLink('concept-a2a', 'route-lore-summary', 'api', 'llms.txt')}
            aria-label="Перейти к llms.txt"
            class="px-2 py-1 rounded bg-[#262a33] text-[#00f2fe] hover:border-[#00f2fe] focus:outline-none focus:ring-2 focus:ring-[#00f2fe] border border-[#3a494b]/40 cursor-pointer text-[10px]"
          >
            llms.txt →
          </button>
        </div>
      </div>
    </div>
  </section>

  <!-- Ingress Gateway Overview Card -->
  <section id="ingress-gateway-card" class="bg-[#1c2028] border border-[#3a494b]/40 rounded-xl p-5 space-y-4 shadow-xl">
    <div class="flex items-start justify-between gap-4">
      <div>
        <span class="text-[10px] font-mono font-bold text-[#00f2fe] uppercase tracking-widest block">
          INGRESS GATEWAY
        </span>
        <h2 id="ingress-card-title" class="text-lg font-semibold text-[#dfe2ee] mt-1">
          External Agent Canon Access
        </h2>
      </div>

      <!-- Master Switch -->
      <label class="relative inline-flex items-center cursor-pointer" aria-label="Toggle Ingress Gateway">
        <input type="checkbox" bind:checked={isMasterActive} class="sr-only peer" />
        <div class="w-10 h-5 bg-[#31353e] peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-[#00f2fe]"></div>
      </label>
    </div>

    <!-- Base Routing Endpoint preview & copy -->
    <div id="base-endpoint-box" class="bg-[#0a0e16] border border-[#3a494b]/50 rounded-lg p-3 flex items-center justify-between gap-3">
      <div class="truncate">
        <span class="text-[10px] font-mono text-[#b9cacb] block">BASE ROUTING ENDPOINT</span>
        <code class="text-xs font-mono text-[#00f2fe] tracking-tight block truncate mt-0.5">
          https://dmitryefremov.com/llms.txt
        </code>
      </div>
      <button
        type="button"
        on:click={copyBaseEndpoint}
        aria-label="Copy Base Endpoint"
        class="px-3 py-1.5 bg-[#262a33] hover:bg-[#31353e] text-[#00f2fe] text-xs font-mono rounded border border-[#3a494b]/40 transition-colors cursor-pointer shrink-0"
      >
        {copySuccess ? 'Скопировано!' : 'Скопировать URL'}
      </button>
    </div>

    <!-- Machine-Readable Spec Note -->
    <div class="flex flex-wrap items-center justify-between pt-2 border-t border-[#3a494b]/20 gap-2">
      <span class="text-xs font-mono text-[#b9cacb]">
        Машиночитаемый индекс канона стандартизирован для интеграции с автономными агентами.
      </span>
      <span class="text-xs font-mono text-[#67f4b7] flex items-center gap-1">
        ✓ Стандарт OpenAPI / llms.txt
      </span>
    </div>
  </section>

  <!-- Route Category Filter Pills -->
  <div id="filter-pills-bar" class="flex items-center gap-2 overflow-x-auto pb-1 font-mono text-xs">
    <button
      type="button"
      on:click={() => (activeFilter = 'all')}
      class="px-3 py-1 rounded-full transition-colors cursor-pointer whitespace-nowrap {activeFilter === 'all' ? 'bg-[#00f2fe]/20 text-[#00f2fe] border border-[#00f2fe]/50 font-bold' : 'bg-[#181c24] text-[#b9cacb] border border-[#3a494b]/30 hover:text-white'}"
    >
      <span class="sr-only">All Routes</span>
      <span>Все маршруты ({CANON_ROUTES.length})</span>
    </button>
    <button
      type="button"
      on:click={() => (activeFilter = 'read')}
      class="px-3 py-1 rounded-full transition-colors cursor-pointer whitespace-nowrap {activeFilter === 'read' ? 'bg-[#00f2fe]/20 text-[#00f2fe] border border-[#00f2fe]/50 font-bold' : 'bg-[#181c24] text-[#b9cacb] border border-[#3a494b]/30 hover:text-white'}"
    >
      <span class="sr-only">Read-Only</span>
      <span>Чтение (2)</span>
    </button>
    <button
      type="button"
      on:click={() => (activeFilter = 'query')}
      class="px-3 py-1 rounded-full transition-colors cursor-pointer whitespace-nowrap {activeFilter === 'query' ? 'bg-[#00f2fe]/20 text-[#00f2fe] border border-[#00f2fe]/50 font-bold' : 'bg-[#181c24] text-[#b9cacb] border border-[#3a494b]/30 hover:text-white'}"
    >
      <span class="sr-only">Query / Orders</span>
      <span>Запросы (1)</span>
    </button>
    <button
      type="button"
      on:click={() => (activeFilter = 'webhooks')}
      class="px-3 py-1 rounded-full transition-colors cursor-pointer whitespace-nowrap {activeFilter === 'webhooks' ? 'bg-[#00f2fe]/20 text-[#00f2fe] border border-[#00f2fe]/50 font-bold' : 'bg-[#181c24] text-[#b9cacb] border border-[#3a494b]/30 hover:text-white'}"
    >
      <span class="sr-only">Telemetry</span>
      <span>Телеметрия (1)</span>
    </button>
  </div>

  <!-- Route Cards Section -->
  <section id="routes-list-section" class="space-y-4">
    {#each filteredRoutes as route (route.id)}
      <article
        id={route.id}
        class="bg-[#181c24] border-l-4 {routeStatusMap[route.id] ? 'border-l-[#00f2fe]' : 'border-l-[#849495]'} border-y border-r border-[#3a494b]/30 rounded-xl p-4 space-y-3 shadow-md transition-all"
      >
        <div class="flex items-center justify-between gap-2 flex-wrap">
          <div class="flex items-center gap-2 flex-wrap font-mono text-xs">
            <span class="px-2 py-0.5 rounded font-bold tracking-wider {route.method === 'GET' ? 'bg-[#67f4b7]/20 text-[#e1ffec]' : (route.method === 'POST' ? 'bg-[#00f2fe]/20 text-[#00f2fe]' : 'bg-[#3131c0]/40 text-white')}">
              {route.method}
            </span>
            <span class="px-2 py-0.5 rounded bg-[#31353e] text-[#b9cacb] border border-[#3a494b]/40">
              {route.scope}
            </span>
            <span class="px-2 py-0.5 rounded {routeStatusMap[route.id] ? 'bg-[#00f2fe]/10 text-[#00f2fe] border border-[#00f2fe]/30' : 'bg-[#31353e] text-[#849495] border border-[#3a494b]/20'}">
              {routeStatusMap[route.id] ? 'Active' : 'Paused'}
            </span>
          </div>

          <!-- Circuit Breaker Toggle -->
          <label class="relative inline-flex items-center cursor-pointer" aria-label={`Toggle status for ${route.path}`}>
            <input
              type="checkbox"
              checked={routeStatusMap[route.id]}
              on:change={() => toggleRouteStatus(route.id)}
              class="sr-only peer"
            />
            <div class="w-8 h-4 bg-[#31353e] rounded-full peer peer-checked:bg-[#00f2fe] after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-3 after:w-3 after:transition-all peer-checked:after:translate-x-4"></div>
          </label>
        </div>

        <div>
          <a
            href={route.path}
            target="_blank"
            rel="noopener noreferrer"
            class="text-sm font-mono font-semibold text-[#00f2fe] hover:underline block"
          >
            {route.path}
          </a>
          <p class="text-xs font-mono text-[#b9cacb] mt-1 leading-relaxed">
            {route.description}
          </p>
        </div>

        <div class="flex items-center justify-between text-xs font-mono text-[#b9cacb] pt-2 border-t border-[#3a494b]/20 flex-wrap gap-2">
          <span class="flex items-center gap-1 text-[11px]">
            {route.rateLimit}
          </span>

          <div class="flex items-center gap-2">
            {#if route.schema}
              <button
                type="button"
                on:click={() => toggleSchema(route.id)}
                class="px-2.5 py-1 bg-[#262a33] hover:bg-[#31353e] text-[#dfe2ee] rounded border border-[#3a494b]/30 transition-colors cursor-pointer"
              >
                {expandedSchemas[route.id] ? 'Скрыть Schema' : 'Schema (JSON)'}
              </button>
            {/if}

            <button
              type="button"
              on:click={() => testRoute(route)}
              class="px-2.5 py-1 bg-[#00f2fe] hover:brightness-110 text-[#00373a] font-bold rounded transition-all cursor-pointer"
            >
              <span class="sr-only">Test Route</span>
              <span>Проверить маршрут</span>
            </button>
          </div>
        </div>

        <!-- Toggled Open Schema Preview Box -->
        {#if expandedSchemas[route.id] && (route.schema || routeTestData[route.id])}
          <div class="bg-[#0a0e16] border border-[#3a494b]/40 rounded-lg p-3 font-mono text-xs overflow-x-auto space-y-1">
            <div class="flex items-center justify-between pb-1 border-b border-[#3a494b]/20 text-[10px] text-[#b9cacb]">
              <span>RESPONSE_PAYLOAD_SCHEMA / PREVIEW</span>
              <span class="text-[#c0c1ff]">JSON / Plain Text</span>
            </div>
            <pre class="text-[#dfe2ee] leading-relaxed text-[11px]"><code>{routeTestData[route.id] || route.schema}</code></pre>
          </div>
        {/if}
      </article>
    {/each}
  </section>

  <!-- Security & External Agent Auth Card -->
  <section id="security-auth-card" class="bg-[#1c2028] border border-[#3a494b]/40 rounded-xl p-5 space-y-4 shadow-lg">
    <div class="flex items-center justify-between">
      <h2 id="security-card-title" class="text-sm font-bold font-mono text-[#dfe2ee] flex items-center gap-2">
        <svg class="w-4 h-4 text-[#67f4b7]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
        Agent Scopes & Headers
      </h2>
      <span class="text-xs font-mono text-[#67f4b7] font-semibold">Strict Mode Active</span>
    </div>

    <div class="space-y-2 font-mono text-xs">
      <div class="bg-[#0a0e16] border border-[#3a494b]/30 rounded p-2.5 flex items-center justify-between">
        <span class="text-[#b9cacb]">Auth Header:</span>
        <code class="text-[#00f2fe]">X-Canon-Agent-Key</code>
      </div>
      <div class="bg-[#0a0e16] border border-[#3a494b]/30 rounded p-2.5 flex items-center justify-between">
        <span class="text-[#b9cacb]">CORS Origins:</span>
        <code class="text-[#dfe2ee] truncate max-w-[220px]">agents.mesh.network, dmitryefremov.com, localhost:*</code>
      </div>
    </div>

  </section>
  <!-- Interactive Test Order Modal -->
  {#if testOrderModalOpen}
    <div id="test-order-modal" class="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div class="max-w-md w-full bg-[#12151e] border border-[#3a494b] rounded-2xl p-6 space-y-4 font-mono text-xs text-[#e2e8f0] shadow-2xl">
        <div class="flex justify-between items-center border-b border-[#1f2433] pb-3">
          <h3 class="text-sm font-bold text-[#00f2fe]">TEST API ROUTE: POST /api/v1/agent/orders</h3>
          <button
            type="button"
            on:click={() => (testOrderModalOpen = false)}
            class="px-2 py-1 rounded bg-[#1f2433] hover:bg-[#2e364f]"
          >
            ✕
          </button>
        </div>

        <form on:submit|preventDefault={handleTestOrderSubmit} class="space-y-3">
          <div>
            <label class="block text-[#b9cacb] text-[10px] mb-1" for="input-client-name">CLIENT / AGENT NAME</label>
            <input
              id="input-client-name"
              type="text"
              bind:value={testOrderForm.clientName}
              class="w-full bg-[#0a0e16] border border-[#3a494b] rounded p-2 text-xs text-white"
            />
          </div>
          <div>
            <label class="block text-[#b9cacb] text-[10px] mb-1" for="input-client-email">CONTACT EMAIL</label>
            <input
              id="input-client-email"
              type="email"
              bind:value={testOrderForm.clientEmail}
              class="w-full bg-[#0a0e16] border border-[#3a494b] rounded p-2 text-xs text-white"
            />
          </div>
          <div>
            <label class="block text-[#b9cacb] text-[10px] mb-1" for="input-service-type">SERVICE TYPE</label>
            <select
              id="input-service-type"
              bind:value={testOrderForm.serviceType}
              class="w-full bg-[#0a0e16] border border-[#3a494b] rounded p-2 text-xs text-white"
            >
              <option value="MVP">MVP (Autonomous Architecture)</option>
              <option value="AUDIT">AUDIT (Systems & Knowledge Graph)</option>
              <option value="AUTOMATION">AUTOMATION (Agent Protocol)</option>
            </select>
          </div>
          <div>
            <label class="block text-[#b9cacb] text-[10px] mb-1" for="input-requirements">REQUIREMENTS</label>
            <textarea
              id="input-requirements"
              bind:value={testOrderForm.requirements}
              rows="2"
              class="w-full bg-[#0a0e16] border border-[#3a494b] rounded p-2 text-xs text-white"
            ></textarea>
          </div>

          {#if testOrderResult}
            <div class="p-2 rounded bg-[#0a0e16] border border-[#00f2fe]/40 text-[#67f4b7] text-[11px]">
              {testOrderResult}
            </div>
          {/if}

          <div class="flex justify-end gap-2 pt-2">
            <button
              type="button"
              on:click={() => (testOrderModalOpen = false)}
              class="px-3 py-1.5 rounded bg-[#262a33] text-[#b9cacb]"
            >
              Закрыть
            </button>
            <button
              type="submit"
              class="px-3 py-1.5 rounded bg-[#00f2fe] text-[#00373a] font-bold hover:brightness-110"
            >
              Отправить Test Order
            </button>
          </div>
        </form>
      </div>
    </div>
  {/if}
</div>