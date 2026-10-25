<script lang="ts">
  import { onMount } from 'svelte';
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
      routeTestData[route.id] = JSON.stringify({
        endpoint: '/api/v1/agent/orders',
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Canon-Agent-Key': '<CANON_AGENT_KEY>'
        },
        payloadContract: {
          clientName: 'string',
          clientEmail: 'string (valid email)',
          serviceType: 'MVP | AUDIT | AUTOMATION',
          requirements: 'string (min 10 chars)'
        }
      }, null, 2);
      expandedSchemas[route.id] = true;
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

  function copyBaseEndpoint() {
    const base = typeof window !== 'undefined' ? `${window.location.origin}/llms.txt` : 'https://dmitrii.eneik.com/llms.txt';
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
          https://dmitrii.eneik.com/llms.txt
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
        <code class="text-[#dfe2ee] truncate max-w-[220px]">agents.mesh.network, dmitrii.eneik.com, localhost:*</code>
      </div>
    </div>

  </section>
</div>
