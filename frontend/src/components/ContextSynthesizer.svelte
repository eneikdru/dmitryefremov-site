<script lang="ts">
  import { onMount } from 'svelte';
  import { currentLanguage, t } from '../lib/i18n';

  export let theme: 'dark' | 'light' = 'dark';

  interface ActivatedNode {
    tag: string;
    name: string;
    weight: number;
    level: string;
  }

  interface HandshakeResult {
    sessionId: string;
    status: string;
    agent_type: string;
    repo: string;
    task: string;
    token_budget: number;
    estimated_tokens: number;
    context_reduction_pct: number;
    activated_nodes: ActivatedNode[];
    contract_markdown: string;
    webhook_verify_url: string;
    created_at: string;
  }

  // Agent Form State
  let agentFramework: string = 'Google Jules';
  let customAgentName: string = '';
  let repoContext: string = 'github.com/my-org/core-service';
  let architecturalTask: string = 'Рефакторинг стейт-машины платежей на идемпотентные вебхуки и транзакционные мьютексы без простоя';
  let tokenBudget: number = 1200;
  let clientEmail: string = '';

  let isSubmitting: boolean = false;
  let handshakeResult: HandshakeResult | null = null;
  let errorMessage: string = '';
  let copyContractSuccess: boolean = false;
  let copyDirectiveSuccess: boolean = false;
  let copyCurlSuccess: boolean = false;

  // Directive Command for client agents
  $: effectiveAgentType = agentFramework === 'Custom' ? (customAgentName.trim() || 'Custom Agent') : agentFramework;
  $: agentCurlCommand = `curl -s -X POST "https://dmitryefremov.com/api/v1/bridge/handshake" \\
  -H "Content-Type: application/json" \\
  -d '{"agent_type": "${effectiveAgentType}", "repo": "${repoContext.replace(/'/g, '')}", "task": "${architecturalTask.replace(/'/g, '')}", "token_budget": ${tokenBudget}}'`;

  $: agentSystemPromptDirective = `[АРХИТЕКТУРНЫЙ ИНВАРИАНТ ДЛЯ АГЕНТА]:
Ты работаешь в контуре Eneik Production System. Перед генерацией PR выполни запрос к интеллектуальному узлу Дмитрия Ефремова:
POST https://dmitryefremov.com/api/v1/bridge/handshake
Передай бриф задачи и кодовой базы ("${architecturalTask}").
Получи деонтические гейты (инварианты BARCAN-TAG, критерий фальсификации Поппера D008, изоляция скоупа Zero-Drift) и строго соблюдай их при формировании кода.`;

  async function executeHandshake() {
    if (!architecturalTask.trim()) {
      errorMessage = 'Укажите архитектурную цель или бриф задачи для синтеза инвариантов.';
      return;
    }
    isSubmitting = true;
    errorMessage = '';

    try {
      const res = await fetch('/api/v1/bridge/handshake', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          agent_type: effectiveAgentType,
          repo: repoContext.trim(),
          task: architecturalTask.trim(),
          token_budget: tokenBudget,
          client_email: clientEmail.trim()
        })
      });

      if (!res.ok) {
        throw new Error(`HTTP ${res.status}: ${res.statusText}`);
      }

      const data = await res.json();
      handshakeResult = data;
    } catch (err: any) {
      errorMessage = `Ошибка выполнения рукопожатия: ${err.message}`;
    } finally {
      isSubmitting = false;
    }
  }

  function copyText(text: string, type: 'contract' | 'directive' | 'curl') {
    if (!text) return;
    navigator.clipboard.writeText(text).then(() => {
      if (type === 'contract') {
        copyContractSuccess = true;
        setTimeout(() => { copyContractSuccess = false; }, 2200);
      } else if (type === 'directive') {
        copyDirectiveSuccess = true;
        setTimeout(() => { copyDirectiveSuccess = false; }, 2200);
      } else if (type === 'curl') {
        copyCurlSuccess = true;
        setTimeout(() => { copyCurlSuccess = false; }, 2200);
      }
    });
  }

  function resetHandshake() {
    handshakeResult = null;
    errorMessage = '';
  }

  onMount(() => {
    // Optionally pre-load initial contract if empty
  });
</script>

<div class="space-y-8">
  <!-- 1. Architectural Header Plate (Authoritative & Honest) -->
  <div class="rounded-xl border p-6 sm:p-8 space-y-6 transition-all {theme === 'dark' ? 'bg-[#111318] border-white/10 shadow-2xl' : 'bg-white border-slate-300 shadow-sm'}">
    <div class="flex flex-col lg:flex-row lg:items-center justify-between pb-5 border-b gap-4 {theme === 'dark' ? 'border-white/10' : 'border-slate-200'}">
      <div class="space-y-1">
        <div class="flex items-center gap-2">
          <span class="w-2 h-2 rounded-full bg-[#f2ca50] animate-pulse"></span>
          <span class="font-mono text-xs font-bold uppercase tracking-widest text-[#f2ca50]">
            AGENT-TO-AGENT (A2A) BRIDGE // СЕССИОННЫЙ ХАБ
          </span>
        </div>
        <h3 class="text-xl sm:text-2xl font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
          Подключение автономных агентов к архитектурному ядру
        </h3>
        <p class="text-xs sm:text-sm font-mono text-slate-400 max-w-2xl">
          Интеллектуальный шлюз для внешних разработчиков и систем: передайте бриф кодовой базы, получите деонтические гейты (инварианты BARCAN-TAG) и зафиксируйте сессионный контракт с вебхуком верификации.
        </p>
      </div>

      <!-- Honest Technical Metric (No False Claims) -->
      <div class="flex flex-col items-start lg:items-end gap-1.5 shrink-0">
        <div class="font-mono text-xs px-3.5 py-2 rounded-lg border font-bold uppercase tracking-wider {theme === 'dark' ? 'bg-[#1e2024] text-[#f2ca50] border-[#f2ca50]/40' : 'bg-amber-50 text-amber-900 border-amber-300'}">
          ЭКОНОМИЯ ДО 85% ТОКЕНОВ // λ = 0.70
        </div>
        <span class="font-mono text-[11px] text-slate-400">
          Графовая редукция: ~600 токенов вместо сырых спецификаций (42 500)
        </span>
      </div>
    </div>

    <!-- 2. Command Directives for Client Agents (Дать команду своему агенту) -->
    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <h4 class="font-mono text-xs font-bold uppercase tracking-wider text-slate-300 flex items-center gap-2">
          <span class="material-symbols-outlined text-[16px] text-[#f2ca50]">terminal</span>
          <span>1. Директива для вашего агента (System Prompt / Terminal)</span>
        </h4>
        <button
          type="button"
          on:click={() => copyText(agentSystemPromptDirective, 'directive')}
          class="font-mono text-xs px-3 py-1 rounded border transition-all cursor-pointer flex items-center gap-1.5 {copyDirectiveSuccess ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-[#1e2024] text-slate-300 border-white/10 hover:text-white hover:border-[#f2ca50]/50'}"
        >
          <span class="material-symbols-outlined text-[14px]">{copyDirectiveSuccess ? 'done' : 'content_copy'}</span>
          <span>{copyDirectiveSuccess ? 'Скопировано!' : 'Скопировать директиву'}</span>
        </button>
      </div>

      <div class="p-4 rounded-lg border font-mono text-xs leading-relaxed overflow-x-auto {theme === 'dark' ? 'bg-[#0c0e12] border-white/10 text-slate-300' : 'bg-slate-900 text-slate-200'}">
        <pre class="whitespace-pre-wrap">{agentSystemPromptDirective}</pre>
      </div>

      <!-- Quick cURL for Terminal -->
      <div class="flex items-center justify-between pt-1">
        <span class="font-mono text-[11px] text-slate-400">Прямой вызов из CI/CD пайплайна или терминала разработчика:</span>
        <button
          type="button"
          on:click={() => copyText(agentCurlCommand, 'curl')}
          class="font-mono text-[11px] text-[#f2ca50] hover:underline cursor-pointer flex items-center gap-1"
        >
          <span class="material-symbols-outlined text-[13px]">{copyCurlSuccess ? 'done' : 'terminal'}</span>
          <span>{copyCurlSuccess ? 'cURL скопирован!' : 'Скопировать cURL'}</span>
        </button>
      </div>
    </div>
  </div>

  <!-- 3. Interactive Agent Registration & Brief Handshake Terminal -->
  <div class="rounded-xl border p-6 sm:p-8 space-y-6 transition-all {theme === 'dark' ? 'bg-[#111318] border-white/10 shadow-2xl' : 'bg-white border-slate-300 shadow-sm'}">
    <div class="flex items-center justify-between pb-4 border-b {theme === 'dark' ? 'border-white/10' : 'border-slate-200'}">
      <div>
        <div class="font-mono text-xs font-bold uppercase tracking-widest text-[#f2ca50]">
          2. Интерактивный терминал брифа и регистрации
        </div>
        <h4 class="text-lg sm:text-xl font-bold tracking-tight mt-0.5 {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
          Ввод данных агента и архитектурной задачи
        </h4>
      </div>
      {#if handshakeResult}
        <button
          type="button"
          on:click={resetHandshake}
          class="font-mono text-xs px-3 py-1.5 rounded border transition-all cursor-pointer bg-[#1e2024] text-slate-300 border-white/10 hover:text-white hover:border-[#f2ca50]"
        >
          ← Новый бриф
        </button>
      {/if}
    </div>

    {#if !handshakeResult}
      <!-- Registration Form -->
      <form on:submit|preventDefault={executeHandshake} class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Field 1: Agent Framework Selection -->
          <div class="space-y-2">
            <label for="agent-framework-select" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
              Фреймворк или среда агента:
            </label>
            <select
              id="agent-framework-select"
              bind:value={agentFramework}
              class="w-full px-3.5 py-2.5 rounded-lg border font-mono text-xs transition-all {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white focus:border-[#f2ca50] focus:ring-1 focus:ring-[#f2ca50]' : 'bg-slate-50 border-slate-300 text-slate-900'}"
            >
              <option value="Google Jules">Google Jules (Autonomous PR Engine)</option>
              <option value="Claude Code">Claude Code (Anthropic CLI)</option>
              <option value="OpenAI Codex">OpenAI Codex / Computer Use</option>
              <option value="Cursor Composer">Cursor Composer Agent</option>
              <option value="Roo Code">Roo Code / Cline</option>
              <option value="Aider">Aider Pair Programmer</option>
              <option value="Custom">Свой автономный агент (A2A Bridge)</option>
            </select>
          </div>

          <!-- Optional Custom Agent Name -->
          {#if agentFramework === 'Custom'}
            <div class="space-y-2">
              <label for="custom-agent-input" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
                Имя вашего агента:
              </label>
              <input
                id="custom-agent-input"
                type="text"
                bind:value={customAgentName}
                placeholder="например: Nova-DevOps-v2"
                class="w-full px-3.5 py-2.5 rounded-lg border font-mono text-xs transition-all {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white focus:border-[#f2ca50]' : 'bg-slate-50 border-slate-300 text-slate-900'}"
              />
            </div>
          {:else}
            <!-- Field 2: Target Repository / Codebase AST -->
            <div class="space-y-2">
              <label for="repo-context-input" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
                Репозиторий или контекст кодовой базы:
              </label>
              <input
                id="repo-context-input"
                type="text"
                bind:value={repoContext}
                placeholder="например: github.com/my-org/core-service"
                class="w-full px-3.5 py-2.5 rounded-lg border font-mono text-xs transition-all {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white focus:border-[#f2ca50]' : 'bg-slate-50 border-slate-300 text-slate-900'}"
              />
            </div>
          {/if}
        </div>

        <!-- Field 3: Architectural Brief & Task Objective -->
        <div class="space-y-2">
          <label for="architectural-task-input" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
            Бриф архитектурной задачи (цель, риски, модули):
          </label>
          <textarea
            id="architectural-task-input"
            rows="3"
            bind:value={architecturalTask}
            placeholder="Опишите задачу: например, миграция стейт-машины платежей на идемпотентные транзакции, мьютекс таймслотов в UTC+4 или рефакторинг с нулевым дрифтом..."
            class="w-full px-3.5 py-2.5 rounded-lg border font-mono text-xs transition-all leading-relaxed {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white focus:border-[#f2ca50]' : 'bg-slate-50 border-slate-300 text-slate-900'}"
          ></textarea>
        </div>

        <!-- Field 4: Token Budget Cap Slider with Live Pruning Indicator -->
        <div class="space-y-3 p-4 rounded-lg border {theme === 'dark' ? 'bg-[#15181f] border-white/5' : 'bg-slate-50 border-slate-200'}">
          <div class="flex items-center justify-between font-mono text-xs">
            <span class="font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
              Лимит бюджета контекста: <span class="text-[#f2ca50] font-bold">{tokenBudget} токенов</span>
            </span>
            <span class="text-slate-400">
              Полнота онтологического графа: ~{Math.round((tokenBudget / 3200) * 100)}%
            </span>
          </div>
          <input
            type="range"
            min="400"
            max="3200"
            step="100"
            bind:value={tokenBudget}
            class="w-full accent-[#f2ca50] cursor-pointer"
          />
          <div class="flex justify-between font-mono text-[10px] text-slate-500">
            <span>400 токенов (Минимальный срез)</span>
            <span>1200 (Рекомендуемый оптимум)</span>
            <span>3200 (Полный графовый шлейф)</span>
          </div>
        </div>

        {#if errorMessage}
          <div class="p-3.5 rounded-lg border font-mono text-xs bg-red-950/40 text-red-300 border-red-500/30">
            {errorMessage}
          </div>
        {/if}

        <!-- Submit Action -->
        <div class="pt-2 flex flex-col sm:flex-row items-center gap-4">
          <button
            type="submit"
            disabled={isSubmitting}
            class="w-full sm:w-auto px-6 py-3.5 rounded-lg font-mono text-xs font-bold uppercase tracking-wider transition-all cursor-pointer flex items-center justify-center gap-2 bg-[#f2ca50] text-[#0c0e12] hover:bg-[#e7c364] disabled:opacity-50 disabled:cursor-not-allowed shadow-lg"
          >
            {#if isSubmitting}
              <span class="material-symbols-outlined text-[16px] animate-spin">refresh</span>
              <span>Синтез онтологических гейтов...</span>
            {:else}
              <span class="material-symbols-outlined text-[16px]">handshake</span>
              <span>СФОРМИРОВАТЬ СЕССИОННЫЙ КОНТРАКТ И ГЕЙТЫ</span>
            {/if}
          </button>

          <span class="font-mono text-xs text-slate-400 text-center sm:text-left">
            Фиксирует контракт сессии в `agent_sessions.jsonl` и генерирует CI-вебхук
          </span>
        </div>
      </form>
    {:else}
      <!-- 4. Active Handshake Contract State (Living Contract) -->
      <div class="space-y-6">
        <!-- Contract Meta Header Plate -->
        <div class="p-5 rounded-lg border space-y-4 {theme === 'dark' ? 'bg-[#0c0e12] border-emerald-500/30' : 'bg-emerald-50 border-emerald-300'}">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div class="flex items-center gap-2.5">
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
              <span class="font-mono text-xs font-bold uppercase tracking-wider text-emerald-400">
                СЕССИЯ ЗАКРЕПЛЕНА: {handshakeResult.sessionId}
              </span>
            </div>
            <span class="font-mono text-xs text-slate-400">
              Статус: <strong>{handshakeResult.status}</strong>
            </span>
          </div>

          <!-- Key Metrics Grid -->
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-2 font-mono text-xs">
            <div class="p-3 rounded border {theme === 'dark' ? 'bg-[#15181f] border-white/5' : 'bg-white border-slate-200'}">
              <div class="text-[10px] text-slate-400 uppercase">Агент:</div>
              <div class="font-bold text-white truncate">{handshakeResult.agent_type}</div>
            </div>
            <div class="p-3 rounded border {theme === 'dark' ? 'bg-[#15181f] border-white/5' : 'bg-white border-slate-200'}">
              <div class="text-[10px] text-slate-400 uppercase">Токены среза:</div>
              <div class="font-bold text-[#f2ca50]">~{handshakeResult.estimated_tokens} токенов</div>
            </div>
            <div class="p-3 rounded border {theme === 'dark' ? 'bg-[#15181f] border-white/5' : 'bg-white border-slate-200'}">
              <div class="text-[10px] text-slate-400 uppercase">Экономия контекста:</div>
              <div class="font-bold text-emerald-400">{handshakeResult.context_reduction_pct}%</div>
            </div>
            <div class="p-3 rounded border {theme === 'dark' ? 'bg-[#15181f] border-white/5' : 'bg-white border-slate-200'}">
              <div class="text-[10px] text-slate-400 uppercase">Инвариантов в гейте:</div>
              <div class="font-bold text-white">{handshakeResult.activated_nodes.length} узлов</div>
            </div>
          </div>
        </div>

        <!-- Deontic Invariant Tags Activated -->
        {#if handshakeResult.activated_nodes && handshakeResult.activated_nodes.length > 0}
          <div class="space-y-2">
            <div class="font-mono text-xs uppercase tracking-wider text-slate-400">
              АКТИВИРОВАННЫЕ ДЕОНТИЧЕСКИЕ ИНВАРИАНТЫ (BARCAN-TAG):
            </div>
            <div class="flex flex-wrap gap-2">
              {#each handshakeResult.activated_nodes as node}
                <div class="px-2.5 py-1 rounded border font-mono text-[11px] flex items-center gap-1.5 {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-slate-200' : 'bg-slate-100 border-slate-300 text-slate-800'}">
                  <span class="text-[#f2ca50] font-bold">{node.tag}</span>
                  <span class="opacity-40">•</span>
                  <span>{node.name}</span>
                  <span class="text-[9px] text-slate-400">({node.weight.toFixed(2)})</span>
                </div>
              {/each}
            </div>
          </div>
        {/if}

        <!-- Contract Markdown Display -->
        <div class="space-y-2">
          <div class="flex items-center justify-between">
            <div class="font-mono text-xs uppercase tracking-wider text-slate-400">
              АРХИТЕКТУРНЫЙ КОНТРАКТ ДЛЯ АГЕНТА (ГОТОВЫЙ СРЕЗ):
            </div>
            <button
              type="button"
              on:click={() => copyText(handshakeResult?.contract_markdown || '', 'contract')}
              class="font-mono text-xs px-3 py-1 rounded border transition-all cursor-pointer flex items-center gap-1.5 {copyContractSuccess ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-[#1e2024] text-[#f2ca50] border-white/10 hover:border-[#f2ca50]'}"
            >
              <span class="material-symbols-outlined text-[14px]">{copyContractSuccess ? 'done' : 'content_copy'}</span>
              <span>{copyContractSuccess ? 'Контракт скопирован!' : 'Скопировать контракт'}</span>
            </button>
          </div>
          <div class="p-4 rounded-lg border font-mono text-xs leading-relaxed max-h-[350px] overflow-y-auto {theme === 'dark' ? 'bg-[#0c0e12] border-white/10 text-slate-300' : 'bg-slate-900 text-slate-200'}">
            <pre class="whitespace-pre-wrap">{handshakeResult.contract_markdown}</pre>
          </div>
        </div>

        <!-- PR Audit Webhook -->
        <div class="p-4 rounded-lg border space-y-2 {theme === 'dark' ? 'bg-[#15181f] border-white/10' : 'bg-slate-50 border-slate-200'}">
          <div class="flex items-center justify-between">
            <div class="font-mono text-xs font-bold uppercase tracking-wider text-slate-300">
              CI/CD Webhook для проверки PR перед мерджем:
            </div>
            <span class="font-mono text-[10px] text-emerald-400">ACTIVE ENDPOINT</span>
          </div>
          <div class="p-2.5 rounded bg-black/60 border border-white/10 font-mono text-[11px] text-slate-300 overflow-x-auto select-all">
            curl -s -X POST "{handshakeResult.webhook_verify_url}" -H "Content-Type: application/json" -d '@pr_diff.json'
          </div>
        </div>
      </div>
    {/if}
  </div>
</div>
