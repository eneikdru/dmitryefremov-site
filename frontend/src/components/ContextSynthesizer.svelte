<script lang="ts">
  import { onMount } from 'svelte';
  import { currentLanguage } from '../lib/i18n';

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

  // Friendly human names for rules (without BARCAN-TAG internal jargon)
  const RULE_TITLES: Record<string, { ru: string; en: string; ka: string }> = {
    'BARCAN-TAG-00': {
      ru: 'Изоляция изменений и защита кодовой базы',
      en: 'Code Protection & Scope Isolation',
      ka: 'კოდის დაცვა და ცვლილებების იზოლაცია'
    },
    'BARCAN-TAG-01': {
      ru: 'Временные интервалы и слоты без коллизий',
      en: 'Collision-Free Time Intervals',
      ka: 'შეჯახების გარეშე დროითი ინტერვალები'
    },
    'BARCAN-TAG-02': {
      ru: 'Постоянство идентификаторов сущностей',
      en: 'Entity Identifier Stability',
      ka: 'იდენტიფიკატორების მდგრადობა'
    },
    'BARCAN-TAG-03': {
      ru: 'Локализация и клиентские ожидания',
      en: 'Localization & Client Expectations',
      ka: 'ლოკალიზაცია და მომხმარებლის მოლოდინები'
    },
    'BARCAN-TAG-04': {
      ru: 'Атомарные блокировки от гонок данных',
      en: 'Atomic Mutex & Race Condition Prevention',
      ka: 'ატომური მუტექსები და რბოლის პრევენცია'
    },
    'BARCAN-TAG-05': {
      ru: 'Сохранение истории состояний и аудит',
      en: 'State History & Audit Trail',
      ka: 'მდგომარეობათა ისტორია და აუდიტი'
    },
    'BARCAN-TAG-06': {
      ru: 'Логическая проверка переходов состояний',
      en: 'State Machine Consistency Check',
      ka: 'მდგომარეობათა მანქანის ლოგიკური შემოწმება'
    },
    'BARCAN-TAG-07': {
      ru: 'Обязательные тесты на сбои и краевые случаи',
      en: 'Mandatory Failure & Edge-Case Tests',
      ka: 'სავალდებულო ტესტები ზღვრულ შემთხვევებზე'
    },
    'BARCAN-TAG-08': {
      ru: 'Совместимость контрактов публичного API',
      en: 'Public API Contract Compatibility',
      ka: 'საჯარო API კონტრაქტების თავსებადობა'
    },
    'BARCAN-TAG-09': {
      ru: 'Приоритет сохранности данных при конфликтах',
      en: 'Data Integrity Priority',
      ka: 'მონაცემთა დაცვის პრიორიტეტი'
    },
    'BARCAN-TAG-10': {
      ru: 'Строгие запреты недопустимых действий',
      en: 'Strict Prohibitions on Invalid Actions',
      ka: 'დაუშვებელი ქმედებების მკაცრი აკრძალვა'
    },
    'BARCAN-TAG-12': {
      ru: 'Изоляция независимых клиентов и проектов',
      en: 'Multi-Tenant Isolation',
      ka: 'დამოუკიდებელი კლიენტების იზოლაცია'
    }
  };

  function getRuleTitle(tag: string, lang: string): string {
    const entry = RULE_TITLES[tag];
    if (entry) {
      if (lang === 'en') return entry.en;
      if (lang === 'ka') return entry.ka;
      return entry.ru;
    }
    return lang === 'en' ? 'Architectural Reliability Rule' : (lang === 'ka' ? 'საიმედოობის არქიტექტურული წესი' : 'Архитектурное правило надежности');
  }

  // Form State
  let agentFramework: string = 'Google Jules';
  let customAgentName: string = '';
  let repoContext: string = 'github.com/my-org/core-service';
  let architecturalTask: string = 'Рефакторинг обработки платежей с защитой от двойных списаний и потери транзакций';
  let tokenBudget: number = 1200;
  let clientEmail: string = '';

  let isSubmitting: boolean = false;
  let handshakeResult: HandshakeResult | null = null;
  let errorMessage: string = '';
  let copyContractSuccess: boolean = false;
  let copyDirectiveSuccess: boolean = false;
  let copyCurlSuccess: boolean = false;

  $: effectiveAgentType = agentFramework === 'Custom' ? (customAgentName.trim() || 'Custom Agent') : agentFramework;

  $: agentCurlCommand = `curl -s -X POST "https://dmitryefremov.com/api/v1/bridge/handshake" \\
  -H "Content-Type: application/json" \\
  -d '{"agent_type": "${effectiveAgentType}", "repo": "${repoContext.replace(/'/g, '')}", "task": "${architecturalTask.replace(/'/g, '')}", "token_budget": ${tokenBudget}}'`;

  $: agentSystemPromptDirective = $currentLanguage === 'en'
    ? `[ARCHITECTURAL DIRECTIVE FOR AGENT]:
Before making changes to the codebase, request the architectural rules from Dmitry Efremov's hub:
POST https://dmitryefremov.com/api/v1/bridge/handshake
Task: "${architecturalTask}"
Strictly adhere to the returned reliability rules, edge-case tests, and scope boundaries before creating a Pull Request.`
    : ($currentLanguage === 'ka'
      ? `[არქიტექტურული დირექტივა აგენტისთვის]:
კოდის შეცვლამდე მიიღეთ არქიტექტურული წესები დიმიტრი ეფრემოვის კვანძიდან:
POST https://dmitryefremov.com/api/v1/bridge/handshake
დავალება: "${architecturalTask}"
მკაცრად დაიცავით მიღებული წესები, ზღვრული ტესტები და იზოლაცია PR-ის შექმნამდე.`
      : `[АРХИТЕКТУРНАЯ ДИРЕКТИВА ДЛЯ АГЕНТА]:
Перед изменением кодовой базы запросите правила надежности у архитектурного узла Дмитрия Ефремова:
POST https://dmitryefremov.com/api/v1/bridge/handshake
Задача: "${architecturalTask}"
Строго соблюдайте полученные правила надежности, тесты на краевые случаи и границы контекста перед созданием PR.`);

  async function executeHandshake() {
    if (!architecturalTask.trim()) {
      errorMessage = $currentLanguage === 'en'
        ? 'Please describe the task objective.'
        : ($currentLanguage === 'ka' ? 'გთხოვთ აღწეროთ დავალების მიზანი.' : 'Опишите задачу для получения правил.');
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
        throw new Error(`HTTP ${res.status}`);
      }

      const data = await res.json();
      handshakeResult = data;
    } catch (err: any) {
      errorMessage = $currentLanguage === 'en'
        ? `Connection error: ${err.message}`
        : ($currentLanguage === 'ka' ? `კავშირის შეცდომა: ${err.message}` : `Ошибка связи: ${err.message}`);
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
</script>

<div class="space-y-8 font-sans">
  <!-- 1. Value Plate (Clear Benefit, Zero Academic Jargon) -->
  <div class="rounded-xl border p-6 sm:p-8 space-y-6 transition-all {theme === 'dark' ? 'bg-[#111318] border-white/10 shadow-2xl' : 'bg-white border-slate-300 shadow-sm'}">
    <div class="flex flex-col lg:flex-row lg:items-center justify-between pb-5 border-b gap-4 {theme === 'dark' ? 'border-white/10' : 'border-slate-200'}">
      <div class="space-y-1">
        <div class="flex items-center gap-2">
          <span class="w-2 h-2 rounded-full bg-[#f2ca50] animate-pulse"></span>
          <span class="font-mono text-xs font-bold uppercase tracking-widest text-[#f2ca50]">
            {$currentLanguage === 'en' ? 'AI AGENT HUB' : ($currentLanguage === 'ka' ? 'AI აგენტების ჰაბი' : 'ХАБ ДЛЯ AI-АГЕНТОВ')}
          </span>
        </div>
        <h3 class="text-xl sm:text-2xl font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
          {$currentLanguage === 'en' 
            ? 'Connect Your Agents to Dmitry Efremov’s Architecture' 
            : ($currentLanguage === 'ka' 
              ? 'თქვენი აგენტების დაკავშირება დიმიტრი ეფრემოვის არქიტექტურასთან' 
              : 'Подключение ваших агентов к архитектуре Дмитрия Ефремова')}
        </h3>
        <p class="text-xs sm:text-sm font-mono text-slate-400 max-w-2xl">
          {$currentLanguage === 'en'
            ? 'Give your agent clear rules for code reliability and testing before it opens a Pull Request.'
            : ($currentLanguage === 'ka'
              ? 'მიეცით თქვენს აგენტს კოდის საიმედოობისა და ტესტირების მკაფიო წესები PR-ის შექმნამდე.'
              : 'Дайте вашему агенту четкие правила надежности кода и проверки краевых случаев перед созданием Pull Request.')}
        </p>
      </div>

      <!-- Real Benefit Pill -->
      <div class="flex flex-col items-start lg:items-end gap-1 shrink-0">
        <div class="font-mono text-xs px-3.5 py-2 rounded-lg border font-bold uppercase tracking-wider {theme === 'dark' ? 'bg-[#1e2024] text-[#f2ca50] border-[#f2ca50]/40' : 'bg-amber-50 text-amber-900 border-amber-300'}">
          {$currentLanguage === 'en' ? 'UP TO 85% CONTEXT SAVINGS' : ($currentLanguage === 'ka' ? 'კონტექსტის 85%-მდე ეკონომია' : 'ЭКОНОМИЯ КОНТЕКСТА ДО 85%')}
        </div>
        <span class="font-mono text-[11px] text-slate-400">
          {$currentLanguage === 'en'
            ? 'Agent gets only required rules (~600 tokens)'
            : ($currentLanguage === 'ka'
              ? 'აგენტი იღებს მხოლოდ საჭირო წესებს (~600 ტოკენი)'
              : 'Агент получает только нужные правила (~600 токенов)')}
        </span>
      </div>
    </div>

    <!-- 2. Step 1: Ready Directive for Agent -->
    <div class="space-y-3">
      <div class="flex items-center justify-between">
        <h4 class="font-mono text-xs font-bold uppercase tracking-wider text-slate-300 flex items-center gap-2">
          <span class="material-symbols-outlined text-[16px] text-[#f2ca50]">terminal</span>
          <span>{$currentLanguage === 'en' ? 'Step 1: Directive for Your Agent' : ($currentLanguage === 'ka' ? 'ნაბიჯი 1: დირექტივა თქვენი აგენტისთვის' : 'Шаг 1: Инструкция для вашего агента')}</span>
        </h4>
        <button
          type="button"
          on:click={() => copyText(agentSystemPromptDirective, 'directive')}
          class="font-mono text-xs px-3 py-1 rounded border transition-all cursor-pointer flex items-center gap-1.5 {copyDirectiveSuccess ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-[#1e2024] text-slate-300 border-white/10 hover:text-white hover:border-[#f2ca50]/50'}"
        >
          <span class="material-symbols-outlined text-[14px]">{copyDirectiveSuccess ? 'done' : 'content_copy'}</span>
          <span>{copyDirectiveSuccess 
            ? ($currentLanguage === 'en' ? 'Copied!' : ($currentLanguage === 'ka' ? 'დაკოპირდა!' : 'Скопировано!'))
            : ($currentLanguage === 'en' ? 'Copy Directive' : ($currentLanguage === 'ka' ? 'დირექტივის კოპირება' : 'Скопировать инструкцию'))}</span>
        </button>
      </div>

      <div class="p-4 rounded-lg border font-mono text-xs leading-relaxed overflow-x-auto {theme === 'dark' ? 'bg-[#0c0e12] border-white/10 text-slate-300' : 'bg-slate-900 text-slate-200'}">
        <pre class="whitespace-pre-wrap">{agentSystemPromptDirective}</pre>
      </div>

      <div class="flex items-center justify-between pt-1">
        <span class="font-mono text-[11px] text-slate-400">
          {$currentLanguage === 'en' ? 'Direct command via cURL:' : ($currentLanguage === 'ka' ? 'პირდაპირი ბრძანება cURL-ით:' : 'Прямая команда через cURL:')}
        </span>
        <button
          type="button"
          on:click={() => copyText(agentCurlCommand, 'curl')}
          class="font-mono text-[11px] text-[#f2ca50] hover:underline cursor-pointer flex items-center gap-1"
        >
          <span class="material-symbols-outlined text-[13px]">{copyCurlSuccess ? 'done' : 'terminal'}</span>
          <span>{copyCurlSuccess 
            ? ($currentLanguage === 'en' ? 'Copied!' : ($currentLanguage === 'ka' ? 'დაკოპირდა!' : 'Скопировано!')) 
            : ($currentLanguage === 'en' ? 'Copy cURL' : ($currentLanguage === 'ka' ? 'cURL-ის კოპირება' : 'Скопировать cURL'))}</span>
        </button>
      </div>
    </div>
  </div>

  <!-- 3. Step 2: Interactive Brief Form -->
  <div class="rounded-xl border p-6 sm:p-8 space-y-6 transition-all {theme === 'dark' ? 'bg-[#111318] border-white/10 shadow-2xl' : 'bg-white border-slate-300 shadow-sm'}">
    <div class="flex items-center justify-between pb-4 border-b {theme === 'dark' ? 'border-white/10' : 'border-slate-200'}">
      <div>
        <div class="font-mono text-xs font-bold uppercase tracking-widest text-[#f2ca50]">
          {$currentLanguage === 'en' ? 'Step 2: Enter Task Details' : ($currentLanguage === 'ka' ? 'ნაბიჯი 2: დავალების მონაცემების შეყვანა' : 'Шаг 2: Введите задачу')}
        </div>
        <h4 class="text-lg sm:text-xl font-bold tracking-tight mt-0.5 {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
          {$currentLanguage === 'en' ? 'Get Rules & Verification Link' : ($currentLanguage === 'ka' ? 'წესებისა და შემოწმების ბმულის მიღება' : 'Получите правила и ссылку для проверки')}
        </h4>
      </div>
      {#if handshakeResult}
        <button
          type="button"
          on:click={resetHandshake}
          class="font-mono text-xs px-3 py-1.5 rounded border transition-all cursor-pointer bg-[#1e2024] text-slate-300 border-white/10 hover:text-white hover:border-[#f2ca50]"
        >
          ← {$currentLanguage === 'en' ? 'New Task' : ($currentLanguage === 'ka' ? 'ახალი დავალება' : 'Новая задача')}
        </button>
      {/if}
    </div>

    {#if !handshakeResult}
      <form on:submit|preventDefault={executeHandshake} class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Agent Selector -->
          <div class="space-y-2">
            <label for="agent-framework-select" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
              {$currentLanguage === 'en' ? 'Your Agent Framework:' : ($currentLanguage === 'ka' ? 'თქვენი აგენტის გარემო:' : 'Среда или фреймворк вашего агента:')}
            </label>
            <select
              id="agent-framework-select"
              bind:value={agentFramework}
              class="w-full px-3.5 py-2.5 rounded-lg border font-mono text-xs transition-all {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white focus:border-[#f2ca50]' : 'bg-slate-50 border-slate-300 text-slate-900'}"
            >
              <option value="Google Jules">Google Jules</option>
              <option value="Claude Code">Claude Code</option>
              <option value="OpenAI Codex">OpenAI Codex</option>
              <option value="Cursor Composer">Cursor Composer</option>
              <option value="Roo Code">Roo Code / Cline</option>
              <option value="Aider">Aider</option>
              <option value="Custom">{$currentLanguage === 'en' ? 'Other Agent' : ($currentLanguage === 'ka' ? 'სხვა აგენტი' : 'Другой агент')}</option>
            </select>
          </div>

          <!-- Codebase Context -->
          {#if agentFramework === 'Custom'}
            <div class="space-y-2">
              <label for="custom-agent-input" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
                {$currentLanguage === 'en' ? 'Agent Name:' : ($currentLanguage === 'ka' ? 'აგენტის სახელი:' : 'Имя агента:')}
              </label>
              <input
                id="custom-agent-input"
                type="text"
                bind:value={customAgentName}
                placeholder="MyDevAgent"
                class="w-full px-3.5 py-2.5 rounded-lg border font-mono text-xs transition-all {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white focus:border-[#f2ca50]' : 'bg-slate-50 border-slate-300 text-slate-900'}"
              />
            </div>
          {:else}
            <div class="space-y-2">
              <label for="repo-context-input" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
                {$currentLanguage === 'en' ? 'Repository / Project:' : ($currentLanguage === 'ka' ? 'რეპოზიტორია / პროექტი:' : 'Репозиторий или проект:')}
              </label>
              <input
                id="repo-context-input"
                type="text"
                bind:value={repoContext}
                placeholder="github.com/my-org/core-service"
                class="w-full px-3.5 py-2.5 rounded-lg border font-mono text-xs transition-all {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white focus:border-[#f2ca50]' : 'bg-slate-50 border-slate-300 text-slate-900'}"
              />
            </div>
          {/if}
        </div>

        <!-- Task Objective -->
        <div class="space-y-2">
          <label for="architectural-task-input" class="block font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
            {$currentLanguage === 'en' ? 'Task Objective:' : ($currentLanguage === 'ka' ? 'დავალების მიზანი:' : 'Что должен сделать агент:')}
          </label>
          <textarea
            id="architectural-task-input"
            rows="3"
            bind:value={architecturalTask}
            placeholder={$currentLanguage === 'en' ? 'Describe the goal, risks, or key modules...' : ($currentLanguage === 'ka' ? 'აღწერეთ მიზანი, რისკები ან ძირითადი მოდულები...' : 'Опишите задачу: например, миграция стейт-машины платежей, бронирование слотов или рефакторинг...')}
            class="w-full px-3.5 py-2.5 rounded-lg border font-mono text-xs transition-all leading-relaxed {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-white focus:border-[#f2ca50]' : 'bg-slate-50 border-slate-300 text-slate-900'}"
          ></textarea>
        </div>

        {#if errorMessage}
          <div class="p-3.5 rounded-lg border font-mono text-xs bg-red-950/40 text-red-300 border-red-500/30">
            {errorMessage}
          </div>
        {/if}

        <!-- Submit Button -->
        <div class="pt-2 flex flex-col sm:flex-row items-center gap-4">
          <button
            type="submit"
            disabled={isSubmitting}
            class="w-full sm:w-auto px-6 py-3.5 rounded-lg font-mono text-xs font-bold uppercase tracking-wider transition-all cursor-pointer flex items-center justify-center gap-2 bg-[#f2ca50] text-[#0c0e12] hover:bg-[#e7c364] disabled:opacity-50 disabled:cursor-not-allowed shadow-lg"
          >
            {#if isSubmitting}
              <span class="material-symbols-outlined text-[16px] animate-spin">refresh</span>
              <span>{$currentLanguage === 'en' ? 'Generating Rules...' : ($currentLanguage === 'ka' ? 'წესების ფორმირება...' : 'Формирование правил...')}</span>
            {:else}
              <span class="material-symbols-outlined text-[16px]">verified</span>
              <span>{$currentLanguage === 'en' ? 'GET ARCHITECTURAL RULES' : ($currentLanguage === 'ka' ? 'არქიტექტურული წესების მიღება' : 'ПОЛУЧИТЬ ПРАВИЛА ДЛЯ АГЕНТА')}</span>
            {/if}
          </button>
        </div>
      </form>
    {:else}
      <!-- 4. Result View: Only User's Own Session (No exposure of other people's agents) -->
      <div class="space-y-6">
        <div class="p-5 rounded-lg border space-y-4 {theme === 'dark' ? 'bg-[#0c0e12] border-emerald-500/30' : 'bg-emerald-50 border-emerald-300'}">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div class="flex items-center gap-2.5">
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
              <span class="font-mono text-xs font-bold uppercase tracking-wider text-emerald-400">
                {$currentLanguage === 'en' ? 'SESSION READY:' : ($currentLanguage === 'ka' ? 'სესია მზადაა:' : 'СЕССИЯ ГОТОВА:')} {handshakeResult.sessionId}
              </span>
            </div>
            <span class="font-mono text-xs text-slate-400">
              {$currentLanguage === 'en' ? 'Context Reduction:' : ($currentLanguage === 'ka' ? 'კონტექსტის შემცირება:' : 'Экономия контекста:')} <strong class="text-emerald-400">{handshakeResult.context_reduction_pct}%</strong>
            </span>
          </div>
        </div>

        <!-- Selected Rules (Human Friendly) -->
        {#if handshakeResult.activated_nodes && handshakeResult.activated_nodes.length > 0}
          <div class="space-y-2">
            <div class="font-mono text-xs uppercase tracking-wider text-slate-400">
              {$currentLanguage === 'en' ? 'ACTIVE RELIABILITY RULES:' : ($currentLanguage === 'ka' ? 'აქტიური საიმედოობის წესები:' : 'АКТИВНЫЕ ПРАВИЛА НАДЕЖНОСТИ ДЛЯ ВАШЕГО КОДА:')}
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
              {#each handshakeResult.activated_nodes as node}
                <div class="p-3 rounded border font-mono text-xs flex items-center justify-between {theme === 'dark' ? 'bg-[#1e2024] border-white/10 text-slate-200' : 'bg-slate-100 border-slate-300 text-slate-800'}">
                  <span class="font-semibold">{getRuleTitle(node.tag, $currentLanguage)}</span>
                  <span class="material-symbols-outlined text-[16px] text-emerald-400">check_circle</span>
                </div>
              {/each}
            </div>
          </div>
        {/if}

        <!-- Rules Output Markdown -->
        <div class="space-y-2">
          <div class="flex items-center justify-between">
            <div class="font-mono text-xs uppercase tracking-wider text-slate-400">
              {$currentLanguage === 'en' ? 'PROMPT INSTRUCTION FOR YOUR AGENT:' : ($currentLanguage === 'ka' ? 'ინსტრუქცია თქვენი აგენტისთვის:' : 'ГОТОВЫЙ ТЕКСТ ДЛЯ ВСТАВКИ В АГЕНТА:')}
            </div>
            <button
              type="button"
              on:click={() => copyText(handshakeResult?.contract_markdown || '', 'contract')}
              class="font-mono text-xs px-3 py-1 rounded border transition-all cursor-pointer flex items-center gap-1.5 {copyContractSuccess ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-[#1e2024] text-[#f2ca50] border-white/10 hover:border-[#f2ca50]'}"
            >
              <span class="material-symbols-outlined text-[14px]">{copyContractSuccess ? 'done' : 'content_copy'}</span>
              <span>{copyContractSuccess ? ($currentLanguage === 'en' ? 'Copied!' : ($currentLanguage === 'ka' ? 'დაკოპირდა!' : 'Скопировано!')) : ($currentLanguage === 'en' ? 'Copy Text' : ($currentLanguage === 'ka' ? 'ტექსტის კოპირება' : 'Скопировать текст'))}</span>
            </button>
          </div>
          <div class="p-4 rounded-lg border font-mono text-xs leading-relaxed max-h-[300px] overflow-y-auto {theme === 'dark' ? 'bg-[#0c0e12] border-white/10 text-slate-300' : 'bg-slate-900 text-slate-200'}">
            <pre class="whitespace-pre-wrap">{handshakeResult.contract_markdown}</pre>
          </div>
        </div>

        <!-- Verification Webhook -->
        <div class="p-4 rounded-lg border space-y-2 {theme === 'dark' ? 'bg-[#15181f] border-white/10' : 'bg-slate-50 border-slate-200'}">
          <div class="flex items-center justify-between font-mono text-xs text-slate-300">
            <span class="font-bold">{$currentLanguage === 'en' ? 'Automated Verification in CI/CD:' : ($currentLanguage === 'ka' ? 'ავტომატური შემოწმება CI/CD-ში:' : 'Автоматическая проверка в CI/CD:')}</span>
            <span class="text-emerald-400 text-[10px]">VERIFY ENDPOINT</span>
          </div>
          <div class="p-2.5 rounded bg-black/60 border border-white/10 font-mono text-[11px] text-slate-300 overflow-x-auto select-all">
            curl -s -X POST "{handshakeResult.webhook_verify_url}" -H "Content-Type: application/json" -d '@pr_diff.json'
          </div>
        </div>
      </div>
    {/if}
  </div>
</div>
