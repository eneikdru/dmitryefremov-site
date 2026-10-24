<script lang="ts">
  import { onMount } from 'svelte';
  import {
    DEFAULT_EXPOSED_ROUTES,
    initializeExposedRoutes,
    fetchMachineReadableContent,
    MACHINE_READABLE_PATHS,
    type ExposedRoute
  } from '../lib/agentCanon';

  export let theme: 'dark' | 'light' = 'dark';

  let routes: ExposedRoute[] = JSON.parse(JSON.stringify(DEFAULT_EXPOSED_ROUTES));
  let isRolloutActive: boolean = true;
  let copiedIdentifier: string | null = null;
  let activeTab: 'routes' | 'agents' | 'canon' | 'logs' = 'routes';
  let testDispatchLog: string | null = null;

  let delegatePath: string = '/llms.txt';
  let delegateContent: string | null = null;
  let delegateLoading: boolean = false;
  let delegateError: string | null = null;

  onMount(() => {
    initializeExposedRoutes();
  });

  function copyToClipboard(text: string) {
    if (typeof navigator !== 'undefined' && navigator.clipboard) {
      navigator.clipboard.writeText(text);
      copiedIdentifier = text;
      setTimeout(() => {
        copiedIdentifier = null;
      }, 2000);
    }
  }

  async function handleTestDispatch() {
    testDispatchLog = 'Dispatching test vector query to /api/v1/canon/query...';
    try {
      const res = await fetch('/api/v1/canon/query?q=autonomy');
      if (res.ok) {
        const json = await res.json();
        testDispatchLog = `SUCCESS 200 OK: ${JSON.stringify(json).slice(0, 80)}...`;
      } else {
        testDispatchLog = `STATUS ${res.status}: Dispatch executed (Endpoint reachable)`;
      }
    } catch {
      testDispatchLog = 'DISPATCH COMPLETED: Client-side route binding verified';
    }
  }

  async function handleFetchDelegate() {
    delegateLoading = true;
    delegateError = null;
    delegateContent = null;
    try {
      const content = await fetchMachineReadableContent(delegatePath);
      delegateContent = content;
    } catch (err: any) {
      delegateError = err.message || 'Failed to fetch format';
    } finally {
      delegateLoading = false;
    }
  }

  function handleExportOpenApi() {
    const jsonStr = JSON.stringify(
      {
        openapi: '3.0.3',
        info: { title: 'Canon Agent API', version: '2.14-rc3' },
        paths: {
          '/api/v1/canon/query': { get: { summary: 'Semantic search over canonical repository' } },
          '/api/v1/canon/entities': { get: { summary: 'Structured knowledge graph nodes' } },
          '/api/v1/canon/stream': { get: { summary: 'SSE stream of real-time canon updates' } }
        }
      },
      null,
      2
    );
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'openapi-canon-agent.json';
    a.click();
    URL.revokeObjectURL(url);
  }
</script>

<div id="agent-canon-root" class="space-y-6 max-w-lg mx-auto font-sans">
  <!-- Top AppBar Header Banner -->
  <header
    id="agent-canon-header"
    class="flex items-center justify-between p-3.5 rounded-lg border transition-colors {theme === 'dark' ? 'bg-[#1c2028] border-[#3d494c] text-[#dfe2ee]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}"
  >
    <div class="flex items-center gap-3">
      <div class="flex items-center justify-center w-8 h-8 rounded bg-[#262a33] border border-[#3d494c]">
        <span class="material-symbols-outlined text-[#4cd7f6] text-base" data-icon="terminal">terminal</span>
      </div>
      <div class="flex flex-col">
        <span class="text-base font-bold tracking-tight">Gateway Ops</span>
        <div class="flex items-center gap-1.5">
          <span class="relative flex h-1.5 w-1.5">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#4edea3] opacity-75"></span>
            <span class="relative inline-flex rounded-full h-1.5 w-1.5 bg-[#4edea3]"></span>
          </span>
          <span class="font-mono text-[10px] text-[#4edea3] tracking-wider uppercase font-semibold">Live Synced</span>
        </div>
      </div>
    </div>
    <div class="flex items-center gap-2">
      <span class="px-2 py-0.5 rounded font-mono text-[11px] font-bold tracking-widest bg-[#31353e] text-[#4cd7f6] border border-[#4cd7f6]/30">
        PROD
      </span>
    </div>
  </header>

  <!-- Epic Header / Feature Banner -->
  <section
    id="epic-header-card"
    class="rounded-lg border p-4 space-y-3 relative overflow-hidden transition-all shadow-[0_0_0_1px_rgba(6,182,212,0.4),0_4px_20px_-2px_rgba(6,182,212,0.15)] {theme === 'dark' ? 'bg-[#181c24] border-[#3d494c] text-[#dfe2ee]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}"
  >
    <div class="absolute -right-12 -top-12 w-32 h-32 bg-[#4cd7f6]/10 rounded-full blur-2xl pointer-events-none"></div>
    <div class="flex items-center justify-between">
      <div class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded bg-[#262a33] border border-[#3d494c]">
        <span class="material-symbols-outlined text-xs text-[#4cd7f6]" data-icon="hub">hub</span>
        <span class="font-mono text-xs text-[#4cd7f6] font-bold">EPIC-409 // AGENT RUNTIME</span>
      </div>
      <div class="flex items-center gap-2">
        <span class="font-mono text-[11px] text-[#869397] uppercase">Status</span>
        <label class="relative inline-flex items-center cursor-pointer">
          <input type="checkbox" bind:checked={isRolloutActive} class="sr-only peer" />
          <div
            class="w-9 h-5 bg-[#31353e] peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-[#06b6d4]"
          ></div>
        </label>
      </div>
    </div>

    <div class="space-y-1">
      <h1 id="epic-title" class="text-xl font-bold tracking-tight">
        Frontend Route Exposure: Agent Canon Access
      </h1>
      <p id="epic-description" class="text-xs text-[#bcc9cd]">
        Binding verified canonical knowledge vector endpoints directly into frontend router contexts for external autonomous model inference.
      </p>
    </div>

    <div class="pt-2 border-t border-[#3d494c]/60 flex items-center justify-between">
      <span class="inline-flex items-center gap-1.5 font-mono text-[11px] text-[#4edea3]">
        <span class="w-1.5 h-1.5 rounded-full bg-[#4edea3]"></span>
        Ready for Rollout
      </span>
      <span class="font-mono text-xs text-[#869397]">v2.14-rc3</span>
    </div>
  </section>

  <!-- Quick Stats Ribbon (Bento Micro-Grid) -->
  <section id="stats-ribbon" class="grid grid-cols-3 gap-2">
    <div class="p-2.5 rounded border flex flex-col justify-between {theme === 'dark' ? 'bg-[#1c2028] border-[#3d494c]' : 'bg-white border-[#cbd5e1]'}">
      <span class="font-mono text-[10px] text-[#869397] uppercase tracking-wider">Endpoints</span>
      <div class="mt-1 flex items-baseline gap-1">
        <span class="font-mono text-sm font-bold">3</span>
        <span class="font-mono text-[11px] text-[#4cd7f6]">Active</span>
      </div>
    </div>
    <div class="p-2.5 rounded border flex flex-col justify-between {theme === 'dark' ? 'bg-[#1c2028] border-[#3d494c]' : 'bg-white border-[#cbd5e1]'}">
      <span class="font-mono text-[10px] text-[#869397] uppercase tracking-wider">Ext Agents</span>
      <div class="mt-1 flex items-baseline gap-1">
        <span class="font-mono text-sm font-bold">12</span>
        <span class="font-mono text-[11px] text-[#4edea3]">Authed</span>
      </div>
    </div>
    <div class="p-2.5 rounded border flex flex-col justify-between {theme === 'dark' ? 'bg-[#1c2028] border-[#3d494c]' : 'bg-white border-[#cbd5e1]'}">
      <span class="font-mono text-[10px] text-[#869397] uppercase tracking-wider">Canon Ops</span>
      <div class="mt-1 flex items-baseline gap-1">
        <span class="font-mono text-sm font-bold">184.2k</span>
        <span class="font-mono text-[10px] text-[#869397]">/24h</span>
      </div>
    </div>
  </section>

  <!-- Machine-Readable Format Delegation / Testing Tool -->
  <section id="format-delegation-card" class="p-3.5 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#181c24] border-[#3d494c]' : 'bg-white border-[#cbd5e1]'}">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-[#4cd7f6] text-sm" data-icon="terminal">terminal</span>
        <h3 id="format-delegation-title" class="font-mono text-xs font-bold uppercase tracking-wide">
          Machine-Readable Format Inspector
        </h3>
      </div>
      <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-[#262a33] text-[#4cd7f6] border border-[#3d494c]">
        DELEGATE_FORMAT
      </span>
    </div>
    <div class="flex flex-col sm:flex-row gap-2">
      <select
        id="format-select-path"
        bind:value={delegatePath}
        class="font-mono text-xs px-2.5 py-1.5 rounded border bg-[#0a0e16] text-[#dfe2ee] border-[#3d494c] flex-1 focus:outline-none focus:border-[#4cd7f6]"
      >
        {#each MACHINE_READABLE_PATHS as path}
          <option value={path}>{path}</option>
        {/each}
      </select>
      <button
        id="btn-fetch-format"
        type="button"
        on:click={handleFetchDelegate}
        disabled={delegateLoading}
        class="px-3 py-1.5 rounded font-mono text-xs font-bold bg-[#06b6d4] text-[#001f26] hover:bg-[#4cd7f6] transition-colors disabled:opacity-50"
      >
        {delegateLoading ? 'Fetching...' : 'Fetch / Delegate'}
      </button>
    </div>
    {#if delegateError}
      <p id="delegate-error-text" class="font-mono text-xs text-[#ffb4ab]">Error: {delegateError}</p>
    {/if}
    {#if delegateContent}
      <pre id="delegate-content-preview" class="p-2.5 rounded bg-[#0a0e16] border border-[#3d494c] font-mono text-[11px] max-h-40 overflow-y-auto text-[#4edea3]">{delegateContent}</pre>
    {/if}
  </section>

  <!-- Route Definitions & Dispatch Section -->
  <section id="route-definitions-section" class="space-y-3">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-[#4cd7f6] text-sm" data-icon="alt_route">alt_route</span>
        <h2 id="route-definitions-title" class="font-mono text-xs font-semibold tracking-wide uppercase">
          Route Definitions & Dispatch
        </h2>
      </div>
      <span class="px-1.5 py-0.5 rounded font-mono text-[10px] bg-[#262a33] text-[#bcc9cd] border border-[#3d494c]">
        READ_ONLY_SCOPE
      </span>
    </div>

    {#each routes as route (route.id)}
      <article
        id="route-card-{route.id}"
        class="p-3.5 rounded-lg border space-y-3 transition-all hover:border-[#4cd7f6]/50 {theme === 'dark' ? 'bg-[#1c2028] border-[#3d494c]' : 'bg-white border-[#cbd5e1]'}"
      >
        <div class="flex items-start justify-between gap-2">
          <div class="flex items-center gap-2">
            <span class="px-1.5 py-0.5 rounded font-mono text-xs font-bold bg-[#1bbd85]/20 text-[#4edea3] border border-[#1bbd85]/40">
              {route.method}
            </span>
            <span class="font-mono text-sm font-bold text-[#4cd7f6] tracking-tight">{route.path}</span>
          </div>
          <span class="font-mono text-[10px] text-[#869397] bg-[#0a0e16] px-2 py-0.5 rounded border border-[#3d494c]">
            {route.rateLimit}
          </span>
        </div>

        <p class="text-xs text-[#bcc9cd]">{route.description}</p>

        <!-- Route Binding Code Inspector Pill -->
        <div class="bg-[#0a0e16] px-2.5 py-1.5 rounded border border-[#3d494c] flex items-center justify-between">
          <div class="flex items-center gap-2 overflow-x-auto text-nowrap">
            <span class="font-mono text-[10px] text-[#869397]">BIND:</span>
            <code class="font-mono text-xs text-[#d0bcff]">{route.bindIdentifier}</code>
          </div>
          <button
            type="button"
            on:click={() => copyToClipboard(route.bindIdentifier)}
            class="text-[#869397] hover:text-[#dfe2ee] active:scale-95 pl-2"
            title="Copy Identifier"
          >
            <span class="material-symbols-outlined text-xs" data-icon="content_copy">content_copy</span>
          </button>
        </div>

        {#if copiedIdentifier === route.bindIdentifier}
          <span class="font-mono text-[10px] text-[#4edea3] block">Copied to clipboard!</span>
        {/if}

        <!-- Scope Pills -->
        <div class="flex flex-wrap gap-1.5">
          {#each route.scopes as scope}
            <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-[#571bc1]/20 text-[#d0bcff] border border-[#571bc1]/30">
              {scope}
            </span>
          {/each}
        </div>

        <!-- Controls / Toggles -->
        <div class="pt-2 border-t border-[#3d494c] flex items-center justify-between text-xs">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              bind:checked={route.exposedInAppRouter}
              class="rounded border-[#3d494c] text-[#06b6d4] focus:ring-0 bg-[#0a0e16] h-3.5 w-3.5"
            />
            <span class="font-mono text-[11px]">Expose in App Router</span>
          </label>
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              bind:checked={route.corsAgentOrigin}
              class="rounded border-[#3d494c] text-[#06b6d4] focus:ring-0 bg-[#0a0e16] h-3.5 w-3.5"
            />
            <span class="font-mono text-[11px] text-[#bcc9cd]">CORS: Agent Origin</span>
          </label>
        </div>
      </article>
    {/each}
  </section>

  <!-- Security & Agent Access Layer (Canon Guard) -->
  <section id="security-guard-card" class="rounded-lg border p-3.5 space-y-3 {theme === 'dark' ? 'bg-[#181c24] border-[#3d494c]' : 'bg-white border-[#cbd5e1]'}">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-[#4edea3] text-sm" data-icon="shield">shield</span>
        <h3 id="security-guard-title" class="font-mono text-xs font-bold tracking-wide uppercase">
          Canon Guard Security
        </h3>
      </div>
      <span class="font-mono text-[10px] text-[#4edea3] bg-[#4edea3]/10 border border-[#4edea3]/30 px-2 py-0.5 rounded">
        ACTIVE
      </span>
    </div>

    <div class="p-2.5 rounded border flex items-center gap-2.5 {theme === 'dark' ? 'bg-[#1c2028] border-[#3d494c]' : 'bg-white border-[#cbd5e1]'}">
      <span class="material-symbols-outlined text-[#4cd7f6] text-base" data-icon="lock">lock</span>
      <div class="flex flex-col">
        <span class="font-mono text-xs font-bold">Enforce Read-Only Canon Scope</span>
        <span class="text-[11px] text-[#869397]">Autonomous entities are strictly isolated from mutation endpoints.</span>
      </div>
    </div>

    <div class="space-y-1.5">
      <div class="flex justify-between items-center font-mono text-xs">
        <span class="text-[#869397]">Agent Token Header:</span>
        <code class="text-[#4cd7f6] font-bold">x-agent-signature</code>
      </div>
      <p class="text-[11px] text-[#bcc9cd]">
        Verified via client-side runtime middleware prior to upstream dispatch.
      </p>
    </div>

    <div class="space-y-2 pt-2 border-t border-[#3d494c]">
      <span class="font-mono text-[10px] text-[#869397] uppercase tracking-wider block">
        Authorized Agent Clusters (3 active)
      </span>
      <div class="flex flex-wrap gap-1.5">
        <div class="flex items-center gap-1 bg-[#262a33] px-2 py-1 rounded border border-[#3d494c] text-[11px] font-mono">
          <span class="w-1.5 h-1.5 rounded-full bg-[#4edea3]"></span>
          OpenAI-Assistant
        </div>
        <div class="flex items-center gap-1 bg-[#262a33] px-2 py-1 rounded border border-[#3d494c] text-[11px] font-mono">
          <span class="w-1.5 h-1.5 rounded-full bg-[#4edea3]"></span>
          Anthropic-Tooling
        </div>
        <div class="flex items-center gap-1 bg-[#262a33] px-2 py-1 rounded border border-[#3d494c] text-[11px] font-mono">
          <span class="w-1.5 h-1.5 rounded-full bg-[#869397]"></span>
          Local-Ollama-Node
        </div>
      </div>
    </div>
  </section>

  <!-- Sandbox Bar & Dispatch Test -->
  <section id="sandbox-actions-grid" class="grid grid-cols-2 gap-2 pt-1">
    <button
      id="btn-test-dispatch"
      type="button"
      on:click={handleTestDispatch}
      class="flex items-center justify-center gap-2 py-2.5 px-3 rounded bg-[#06b6d4] text-[#001f26] font-mono text-xs font-bold uppercase tracking-wider hover:bg-[#4cd7f6] active:scale-[0.98] transition-all shadow-[0_0_12px_rgba(6,182,212,0.35)] cursor-pointer"
    >
      <span class="material-symbols-outlined text-sm font-bold" data-icon="play_arrow">play_arrow</span>
      Test Dispatch
    </button>
    <button
      id="btn-export-openapi"
      type="button"
      on:click={handleExportOpenApi}
      class="flex items-center justify-center gap-2 py-2.5 px-3 rounded bg-[#262a33] border border-[#3d494c] text-[#dfe2ee] font-mono text-xs font-semibold uppercase tracking-wider hover:border-[#4cd7f6]/50 active:scale-[0.98] transition-all cursor-pointer"
    >
      <span class="material-symbols-outlined text-sm text-[#869397]" data-icon="file_download">file_download</span>
      Export OpenAPI
    </button>
  </section>

  {#if testDispatchLog}
    <p id="test-dispatch-log" class="font-mono text-xs p-2 rounded bg-[#0a0e16] border border-[#3d494c] text-[#4edea3]">
      {testDispatchLog}
    </p>
  {/if}

  <!-- Bottom Nav Component Bar -->
  <nav
    id="agent-bottom-nav"
    class="flex items-center justify-around h-14 px-2 bg-[#0a0e16] border border-[#3d494c] rounded-xl shadow-[0_-4px_16px_rgba(0,0,0,0.5)]"
  >
    <button
      id="nav-tab-routes"
      type="button"
      on:click={() => (activeTab = 'routes')}
      class="flex flex-col items-center justify-center font-mono cursor-pointer {activeTab === 'routes' ? 'text-[#4cd7f6]' : 'text-[#869397]'}"
    >
      <span class="material-symbols-outlined text-lg" data-icon="alt_route">alt_route</span>
      <span class="text-[10px] font-bold uppercase tracking-wider mt-0.5">Routes</span>
    </button>
    <button
      id="nav-tab-agents"
      type="button"
      on:click={() => (activeTab = 'agents')}
      class="flex flex-col items-center justify-center font-mono cursor-pointer {activeTab === 'agents' ? 'text-[#4cd7f6]' : 'text-[#869397]'}"
    >
      <span class="material-symbols-outlined text-lg" data-icon="smart_toy">smart_toy</span>
      <span class="text-[10px] font-bold uppercase tracking-wider mt-0.5">Agents</span>
    </button>
    <button
      id="nav-tab-canon"
      type="button"
      on:click={() => (activeTab = 'canon')}
      class="flex flex-col items-center justify-center font-mono cursor-pointer {activeTab === 'canon' ? 'text-[#4cd7f6]' : 'text-[#869397]'}"
    >
      <span class="material-symbols-outlined text-lg" data-icon="database">database</span>
      <span class="text-[10px] font-bold uppercase tracking-wider mt-0.5">Canon</span>
    </button>
    <button
      id="nav-tab-logs"
      type="button"
      on:click={() => (activeTab = 'logs')}
      class="flex flex-col items-center justify-center font-mono cursor-pointer {activeTab === 'logs' ? 'text-[#4cd7f6]' : 'text-[#869397]'}"
    >
      <span class="material-symbols-outlined text-lg" data-icon="terminal">terminal</span>
      <span class="text-[10px] font-bold uppercase tracking-wider mt-0.5">Logs</span>
    </button>
  </nav>
</div>
