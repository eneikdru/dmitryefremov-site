<script lang="ts">
  import { onMount } from 'svelte';

  export let theme: 'dark' | 'light' = 'dark';

  interface ActivatedNode {
    tag: string;
    name: string;
    weight: number;
    level: string;
  }

  interface SynthesizerResponse {
    task: string;
    token_budget: number;
    estimated_tokens: number;
    activated_node_count: number;
    activated_nodes: ActivatedNode[];
    markdown: string;
  }

  let taskQuery: string = 'master schedule and break intervals in tbilisi tz utc+4';
  let tokenBudget: number = 1200;
  let estimatedTokens: number = 620;
  let latencyMs: number = 4;
  let activatedNodes: ActivatedNode[] = [];
  let markdownOutput: string = 'Загрузка онтологического синтезатора...';
  let isLoading: boolean = false;
  let copyPromptSuccess: boolean = false;
  let copyCurlSuccess: boolean = false;
  let debounceTimer: any = null;

  const presets = [
    { label: 'Календарь & таймзона (UTC+4)', query: 'master schedule and break intervals in tbilisi tz utc+4' },
    { label: 'Бронирование & Mutex', query: 'beauty booking concurrency slot collision mutex' },
    { label: 'Платежи Stripe & Идемпотентность', query: 'stripe payments webhook idempotency state machine' },
    { label: 'Рефакторинг & Zero-Drift', query: 'refactoring legacy service with strict blast radius and zero drift' }
  ];

  async function synthesize(query: string = taskQuery, budget: number = tokenBudget) {
    if (!query.trim()) return;
    isLoading = true;
    const t0 = performance.now();
    try {
      const url = `/api/v1/canon/context?task=${encodeURIComponent(query)}&budget=${budget}&format=json`;
      const res = await fetch(url);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data: SynthesizerResponse = await res.json();
      const elapsed = Math.round(performance.now() - t0);
      latencyMs = elapsed;
      estimatedTokens = data.estimated_tokens || 600;
      activatedNodes = data.activated_nodes || [];
      markdownOutput = data.markdown || '';
    } catch (err: any) {
      markdownOutput = `Ошибка связи с API: ${err.message}`;
    } finally {
      isLoading = false;
    }
  }

  function handleInput() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      synthesize(taskQuery, tokenBudget);
    }, 350);
  }

  function handleBudgetChange() {
    synthesize(taskQuery, tokenBudget);
  }

  function loadPreset(presetQuery: string) {
    taskQuery = presetQuery;
    synthesize(taskQuery, tokenBudget);
  }

  function copyPrompt() {
    if (!markdownOutput) return;
    navigator.clipboard.writeText(markdownOutput).then(() => {
      copyPromptSuccess = true;
      setTimeout(() => { copyPromptSuccess = false; }, 2000);
    });
  }

  function copyCurl() {
    const cmd = `curl -s "https://dmitryefremov.com/api/v1/canon/context?task=${encodeURIComponent(taskQuery)}&budget=${tokenBudget}"`;
    navigator.clipboard.writeText(cmd).then(() => {
      copyCurlSuccess = true;
      setTimeout(() => { copyCurlSuccess = false; }, 2000);
    });
  }

  onMount(() => {
    synthesize();
  });
</script>

<div class="space-y-8">
  <!-- 1. Client Onboarding Guide (Simple, Authoritative, Clear) -->
  <div class="rounded-xl border p-6 sm:p-8 space-y-6 transition-all {theme === 'dark' ? 'bg-[#0d1118] border-white/10 shadow-2xl' : 'bg-white border-slate-300 shadow-sm'}">
    <div class="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b gap-3 {theme === 'dark' ? 'border-white/10' : 'border-slate-200'}">
      <div>
        <div class="font-mono text-xs font-bold uppercase tracking-widest text-amber-400">
          ОНБОРДИНГ // ИНТЕЛЛЕКТУАЛЬНЫЙ КОНТЕКСТ ДЛЯ АГЕНТОВ
        </div>
        <h3 class="text-xl sm:text-2xl font-bold tracking-tight mt-1 {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
          Как подключать своих AI-агентов к архитектуре Дмитрия Ефремова
        </h3>
      </div>
      <div class="font-mono text-xs px-3 py-1.5 rounded-lg border font-bold uppercase tracking-wider {theme === 'dark' ? 'bg-amber-400/10 text-amber-300 border-amber-400/30' : 'bg-amber-50 text-amber-900 border-amber-300'}">
        FREE TIER: БЕЗ РАСХОДА ВАШИХ ТОКЕНОВ
      </div>
    </div>

    <!-- 3-Step Flow Grid -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Step 1 -->
      <div class="p-5 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#121620] border-white/5' : 'bg-slate-50 border-slate-200'}">
        <div class="flex items-center gap-3">
          <span class="w-7 h-7 rounded-full bg-amber-400 text-black font-mono font-bold text-xs flex items-center justify-center">1</span>
          <h4 class="font-bold text-sm uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
            Формулировка задачи
          </h4>
        </div>
        <p class="text-xs leading-relaxed {theme === 'dark' ? 'text-slate-400' : 'text-slate-600'}">
          Опишите техническую задачу агента (например: <em>«интервалы перерывов мастера в таймзоне Тбилиси UTC+4»</em>) или выберите готовый пресет ниже.
        </p>
      </div>

      <!-- Step 2 -->
      <div class="p-5 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#121620] border-white/5' : 'bg-slate-50 border-slate-200'}">
        <div class="flex items-center gap-3">
          <span class="w-7 h-7 rounded-full bg-amber-400 text-black font-mono font-bold text-xs flex items-center justify-center">2</span>
          <h4 class="font-bold text-sm uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
            Графовый синтез (λ=0.70)
          </h4>
        </div>
        <p class="text-xs leading-relaxed {theme === 'dark' ? 'text-slate-400' : 'text-slate-600'}">
          Алгоритм затухания автоматически выделяет только нужные деонтические запреты и тесты фальсификации, отсекая до 85% шума и экономя ваш бюджет токенов.
        </p>
      </div>

      <!-- Step 3 -->
      <div class="p-5 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#121620] border-white/5' : 'bg-slate-50 border-slate-200'}">
        <div class="flex items-center gap-3">
          <span class="w-7 h-7 rounded-full bg-amber-400 text-black font-mono font-bold text-xs flex items-center justify-center">3</span>
          <h4 class="font-bold text-sm uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
            Внедрение в агента
          </h4>
        </div>
        <p class="text-xs leading-relaxed {theme === 'dark' ? 'text-slate-400' : 'text-slate-600'}">
          Нажмите <strong>«Скопировать промпт»</strong> для Google Jules, Cursor или Claude Code, либо дергайте эндпоинт по cURL/OpenAPI в своих CI/CD скриптах.
        </p>
      </div>
    </div>
  </div>

  <!-- 2. Interactive Synthesizer Console -->
  <div class="rounded-xl border p-6 sm:p-8 space-y-6 transition-all {theme === 'dark' ? 'bg-[#0d1118] border-white/10 shadow-2xl' : 'bg-white border-slate-300 shadow-sm'}">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b gap-3 {theme === 'dark' ? 'border-white/10' : 'border-slate-200'}">
      <div>
        <div class="font-mono text-xs font-bold uppercase tracking-widest text-amber-400">
          ENEIK GRAPH ENGINE // АВТОНОМНЫЙ ХАБ
        </div>
        <h3 class="text-xl sm:text-2xl font-bold tracking-tight mt-1 {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
          Динамический верстак архитектурных инвариантов
        </h3>
      </div>
      <div class="flex items-center gap-2 text-xs font-mono">
        <a href="/api" target="_blank" class="px-3 py-1.5 rounded-lg border font-bold uppercase tracking-wider transition-all {theme === 'dark' ? 'bg-[#141923] text-slate-300 border-white/10 hover:border-amber-400' : 'bg-slate-100 text-slate-800 border-slate-300 hover:border-slate-500'}">
          OpenAPI 3.0
        </a>
      </div>
    </div>

    <!-- Architectural Presets -->
    <div class="space-y-2">
      <div class="font-mono text-xs uppercase tracking-wider {theme === 'dark' ? 'text-slate-400' : 'text-slate-600'}">
        БЫСТРЫЕ АРХИТЕКТУРНЫЕ ПРЕСЕТЫ:
      </div>
      <div class="flex flex-wrap gap-2">
        {#each presets as p}
          <button
            type="button"
            on:click={() => loadPreset(p.query)}
            class="px-3 py-1.5 rounded-lg border font-mono text-xs font-semibold transition-all cursor-pointer {taskQuery === p.query ? (theme === 'dark' ? 'bg-amber-400 text-black border-amber-400' : 'bg-slate-900 text-white border-slate-900') : (theme === 'dark' ? 'bg-[#121620] text-slate-300 border-white/10 hover:border-amber-400/50' : 'bg-slate-50 text-slate-700 border-slate-200 hover:border-slate-400')}"
          >
            {p.label}
          </button>
        {/each}
      </div>
    </div>

    <!-- Input Form & Budget Range -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-4 items-end">
      <div class="lg:col-span-8 space-y-2">
        <label for="synth-task-input" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
          Техническая задача для агента
        </label>
        <input
          id="synth-task-input"
          type="text"
          bind:value={taskQuery}
          on:input={handleInput}
          placeholder="Например: stripe webhook idempotency, calendar booking mutex..."
          class="w-full px-4 py-3 rounded-lg border font-mono text-xs sm:text-sm focus:outline-none focus:ring-1 focus:ring-amber-400 transition-all {theme === 'dark' ? 'bg-[#121620] text-white border-white/10 placeholder:text-slate-600' : 'bg-white text-slate-900 border-slate-300 placeholder:text-slate-400'}"
        />
      </div>

      <div class="lg:col-span-4 space-y-2">
        <div class="flex items-center justify-between font-mono text-xs font-bold">
          <span class="{theme === 'dark' ? 'text-slate-400' : 'text-slate-600'}">ЛИМИТ ТОКЕНОВ:</span>
          <span class="text-amber-400">{tokenBudget} tkn</span>
        </div>
        <input
          type="range"
          min="500"
          max="2500"
          step="100"
          bind:value={tokenBudget}
          on:input={handleBudgetChange}
          class="w-full accent-amber-400 cursor-pointer"
        />
      </div>
    </div>

    <!-- Telemetry Bar & Activated Nodes -->
    <div class="p-4 rounded-lg border flex flex-col md:flex-row md:items-center justify-between gap-4 font-mono text-xs {theme === 'dark' ? 'bg-[#121620] border-white/10' : 'bg-slate-50 border-slate-200'}">
      <div class="flex flex-wrap items-center gap-2">
        <span class="font-bold text-amber-400">АКТИВНЫЕ УЗЛЫ:</span>
        {#if activatedNodes.length > 0}
          <div class="flex flex-wrap gap-1.5">
            {#each activatedNodes as node}
              <span
                title="{node.name} ({node.level})"
                class="px-2 py-0.5 rounded border text-[11px] font-bold {node.weight >= 0.99 ? 'bg-amber-400/20 text-amber-300 border-amber-400/40' : (node.weight >= 0.65 ? 'bg-white/10 text-white border-white/20' : 'bg-white/5 text-slate-400 border-white/10')}"
              >
                {node.tag}: {node.weight.toFixed(2)}
              </span>
            {/each}
          </div>
        {:else}
          <span class="text-slate-500">Нет активных узлов</span>
        {/if}
      </div>

      <div class="flex items-center gap-4 text-xs">
        <span>РАЗМЕР: <strong class="text-amber-400">~{estimatedTokens} токенов</strong> (-83%)</span>
        <span>ОТКЛИК: <strong class="{theme === 'dark' ? 'text-white' : 'text-black'}">{latencyMs} ms</strong></span>
      </div>
    </div>

    <!-- Output Box with Copy Triggers -->
    <div class="space-y-3">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <span class="font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-400' : 'text-slate-600'}">
          СИНТЕЗИРОВАННЫЙ СРЕЗ (ГОТОВ К ПЕРЕДАЧЕ В АГЕНТА):
        </span>
        <div class="flex flex-wrap items-center gap-2">
          <!-- Copy Prompt -->
          <button
            type="button"
            on:click={copyPrompt}
            class="px-3.5 py-1.5 rounded-lg border font-mono text-xs font-bold uppercase tracking-wider transition-all cursor-pointer flex items-center gap-1.5 {copyPromptSuccess ? 'bg-green-600 text-white border-green-600' : (theme === 'dark' ? 'bg-amber-400 text-black border-amber-400 hover:bg-amber-300' : 'bg-slate-900 text-white border-slate-900 hover:bg-slate-800')}"
          >
            <span class="material-symbols-outlined text-[16px]">{copyPromptSuccess ? 'check' : 'content_copy'}</span>
            <span>{copyPromptSuccess ? 'Скопировано!' : 'Скопировать промпт'}</span>
          </button>

          <!-- Copy cURL -->
          <button
            type="button"
            on:click={copyCurl}
            class="px-3.5 py-1.5 rounded-lg border font-mono text-xs font-bold uppercase tracking-wider transition-all cursor-pointer flex items-center gap-1.5 {copyCurlSuccess ? 'bg-green-600 text-white border-green-600' : (theme === 'dark' ? 'bg-[#141923] text-slate-300 border-white/10 hover:border-amber-400/50' : 'bg-white text-slate-800 border-slate-300 hover:border-slate-500')}"
          >
            <span class="material-symbols-outlined text-[16px]">{copyCurlSuccess ? 'check' : 'terminal'}</span>
            <span>{copyCurlSuccess ? 'cURL скопирован!' : 'Скопировать cURL'}</span>
          </button>

          <!-- Raw JSON -->
          <a
            href="/api/v1/canon/context?task={encodeURIComponent(taskQuery)}&format=json"
            target="_blank"
            class="px-3 py-1.5 rounded-lg border font-mono text-xs font-bold uppercase tracking-wider transition-all flex items-center gap-1 {theme === 'dark' ? 'bg-[#141923] text-slate-400 border-white/10 hover:text-white' : 'bg-white text-slate-600 border-slate-300 hover:text-black'}"
          >
            <span>JSON</span>
          </a>
        </div>
      </div>

      <!-- Markdown Preview Terminal Box -->
      <div class="relative">
        <pre class="w-full max-h-80 overflow-y-auto p-5 rounded-lg border font-mono text-xs leading-relaxed select-all whitespace-pre-wrap {theme === 'dark' ? 'bg-[#090c12] text-slate-200 border-white/10' : 'bg-slate-900 text-slate-100 border-slate-800'}">{markdownOutput}</pre>
      </div>
    </div>
  </div>
</div>
