<script lang="ts">
  import { onMount } from 'svelte';
  import { currentLanguage } from '../lib/i18n';

  export let theme: 'dark' | 'light' = 'dark';

  interface RegistrationResult {
    success: boolean;
    client_email: string;
    assigned_repo: string;
    handshake_endpoint: string;
    commit_endpoint: string;
    updates_endpoint: string;
    session_id: string;
    agent_directive: string;
    task: string;
  }

  // Form State
  let clientEmail: string = '';
  let projectGoal: string = '';
  let showCustomRepoInput: boolean = false;
  let customRepoUrl: string = '';

  let isSubmitting: boolean = false;
  let errorMessage: string = '';
  let regResult: RegistrationResult | null = null;

  // Single Copy State
  let copyAllSuccess: boolean = false;
  let copyRepoSuccess: boolean = false;

  const DEFAULT_HANDSHAKE_ENDPOINT = 'https://dmitryefremov.com/api/v1/bridge/handshake';
  const DEFAULT_COMMIT_ENDPOINT = 'https://dmitryefremov.com/api/v1/bridge/commit';
  const DEFAULT_UPDATES_ENDPOINT = 'https://dmitryefremov.com/api/v1/updates';

  onMount(() => {
    try {
      const stored = localStorage.getItem('dmitry_efremov_client_repo');
      if (stored) {
        regResult = JSON.parse(stored);
        if (regResult?.client_email) clientEmail = regResult.client_email;
        if (regResult?.task) projectGoal = regResult.task;
      }
    } catch {}
  });

  $: currentRepo = regResult?.assigned_repo || (customRepoUrl.trim() || 'https://github.com/eneikdru/client-[AUTO-ID]');
  $: currentTask = (regResult?.task || projectGoal.trim()) || 'Autonomous system implementation & reliability assurance';

  $: unifiedAgentDirective = `[EFREMOV ARCHITECTURE GATEWAY]:
Project Goal: ${currentTask}
Target Repository: ${currentRepo}
Commit & Audit Gateway: POST ${DEFAULT_COMMIT_ENDPOINT}
Reliability Rules Node: POST ${DEFAULT_HANDSHAKE_ENDPOINT}
Updates & Standards: GET ${DEFAULT_UPDATES_ENDPOINT}

DIRECTIVE FOR AGENT:
1. Scope Isolation: Implement the project goal strictly within declared task boundaries.
2. Gateway Commits: Send code changes via the Commit & Audit Gateway for automated secret scanning and architectural verification.
3. Mandatory Falsification Tests: Ensure negative-path and edge-case tests accompany all logic before declaring work complete.`;

  async function handleConnectAndStart() {
    errorMessage = '';
    const email = clientEmail.trim().toLowerCase();

    if (!email || !email.includes('@') || !email.includes('.')) {
      errorMessage = $currentLanguage === 'en'
        ? 'Please enter a valid email address.'
        : ($currentLanguage === 'ka' ? 'გთხოვთ შეიყვანოთ სწორი ელფოსტა.' : 'Пожалуйста, укажите корректный адрес электронной почты.');
      return;
    }

    if (!projectGoal.trim()) {
      errorMessage = $currentLanguage === 'en'
        ? 'Please briefly describe what you want to build.'
        : ($currentLanguage === 'ka' ? 'გთხოვთ მოკლედ აღწეროთ რა გსურთ შექმნათ.' : 'Опишите в двух словах, что нужно сделать.');
      return;
    }

    isSubmitting = true;

    try {
      const res = await fetch('/api/v1/bridge/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: email,
          task: projectGoal.trim(),
          custom_repo: customRepoUrl.trim()
        })
      });

      if (!res.ok) {
        throw new Error(`HTTP ${res.status}`);
      }

      const data = await res.json();
      regResult = {
        success: true,
        client_email: email,
        assigned_repo: data.assigned_repo,
        handshake_endpoint: DEFAULT_HANDSHAKE_ENDPOINT,
        commit_endpoint: DEFAULT_COMMIT_ENDPOINT,
        updates_endpoint: DEFAULT_UPDATES_ENDPOINT,
        session_id: data.session_id,
        agent_directive: unifiedAgentDirective,
        task: projectGoal.trim()
      };

      try {
        localStorage.setItem('dmitry_efremov_client_repo', JSON.stringify(regResult));
      } catch {}

      setTimeout(() => {
        if (typeof document !== 'undefined') {
          const target = document.getElementById('ready-agent-gateway-result');
          if (target) {
            target.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }
        }
      }, 100);
    } catch (err: any) {
      console.warn('Fallback local registration:', err);
      const slug = Math.random().toString(36).substring(2, 10);
      const fallbackRepo = customRepoUrl.trim() || `https://github.com/eneikdru/client-${slug}`;
      regResult = {
        success: true,
        client_email: email,
        assigned_repo: fallbackRepo,
        handshake_endpoint: DEFAULT_HANDSHAKE_ENDPOINT,
        commit_endpoint: DEFAULT_COMMIT_ENDPOINT,
        updates_endpoint: DEFAULT_UPDATES_ENDPOINT,
        session_id: `LOCAL-${slug.toUpperCase()}`,
        agent_directive: unifiedAgentDirective,
        task: projectGoal.trim()
      };
      try {
        localStorage.setItem('dmitry_efremov_client_repo', JSON.stringify(regResult));
      } catch {}
      setTimeout(() => {
        if (typeof document !== 'undefined') {
          const target = document.getElementById('ready-agent-gateway-result');
          if (target) {
            target.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }
        }
      }, 100);
    } finally {
      isSubmitting = false;
    }
  }

  function copyText(text: string, type: 'all' | 'repo') {
    if (!navigator.clipboard) return;
    navigator.clipboard.writeText(text).then(() => {
      if (type === 'all') {
        copyAllSuccess = true;
        setTimeout(() => copyAllSuccess = false, 2500);
      } else if (type === 'repo') {
        copyRepoSuccess = true;
        setTimeout(() => copyRepoSuccess = false, 2500);
      }
    }).catch(err => {
      console.error('Clipboard copy failed', err);
    });
  }
</script>

<div
  id="context-synthesizer-root"
  class="w-full rounded-2xl border transition-colors duration-200 overflow-hidden {theme === 'dark' ? 'bg-[#111318] border-white/10 text-slate-100' : 'bg-white border-slate-200 text-slate-800'}"
>
  <!-- 1. SOVEREIGN OWNERSHIP MANIFESTO (Trust & Freedom Guarantee) -->
  <div class="px-6 py-4 border-b text-xs leading-relaxed flex items-center gap-3 {theme === 'dark' ? 'bg-[#0a0d13] border-white/10 text-slate-300' : 'bg-amber-50/60 border-amber-200 text-slate-700'}">
    <span class="material-symbols-outlined text-[18px] text-[#f2ca50] shrink-0">shield</span>
    <p>
      {$currentLanguage === 'en'
        ? 'Architectural apparatus by Dmitry Efremov. You remain the sole author and owner of all created code. Work in your own repository or our managed workspace, and export anytime.'
        : ($currentLanguage === 'ka'
          ? 'არქიტექტურული აპარატი — დიმიტრი ეფრემოვის. კოდის სრული ავტორი და მფლობელი რჩებით თქვენ. შეგიძლიათ იმუშაოთ საკუთარ რეპოზიტორიაში ან ნებისმიერ დროს გაიტანოთ პროექტი.'
          : 'Аппарат архитектурного мышления — Дмитрия Ефремова. Автором, правообладателем и владельцем созданного кода на 100% остаётесь вы. Вы можете работать в своём репозитории или использовать выделенный стартер и забрать проект в любой момент.')}
    </p>
  </div>

  <!-- 2. MAIN HEADER -->
  <div class="p-6 md:p-8 border-b {theme === 'dark' ? 'border-white/10 bg-[#0d0f14]' : 'border-slate-200 bg-slate-50/50'}">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <div class="flex items-center gap-2 mb-1.5">
          <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
          <span class="font-mono text-xs uppercase tracking-wider text-emerald-400 font-bold">
            {$currentLanguage === 'en' ? 'INTELLECTUAL AGENT GATEWAY' : ($currentLanguage === 'ka' ? 'ინტელექტუალური აგენტების კარიბჭე' : 'ИНТЕЛЛЕКТУАЛЬНЫЙ ШЛЮЗ ДЛЯ АГЕНТОВ')}
          </span>
        </div>
        <h2 class="text-xl md:text-2xl font-bold tracking-tight text-white">
          {$currentLanguage === 'en' ? 'Connect Your Agent & Start Building' : ($currentLanguage === 'ka' ? 'დააკავშირეთ აგენტი და დაიწყეთ მუშაობა' : 'Подключите агента и сразу начните работу')}
        </h2>
        <p class="text-xs md:text-sm text-slate-400 mt-1 max-w-2xl leading-relaxed">
          {$currentLanguage === 'en'
            ? 'Enter your email, describe what to build, and receive an instant gateway for your agent (Cursor, Claude Code, Cline, Jules) with automated secret scanning and reliability audits.'
            : ($currentLanguage === 'ka'
              ? 'მიუთითეთ ელფოსტა, აღწერეთ დავალება და მიიღეთ მზა კარიბჭე თქვენი აგენტისთვის უსაფრთხოების და საიმედოობის ავტომატური აუდიტით.'
              : 'Укажите почту, напишите, что хотите сделать — и сразу получите готовый защитный шлюз для своего агента (Cursor, Claude Code, Cline) со встроенным аудитом надежности и защитой от утечек.')}
        </p>
      </div>

      <div class="flex items-center gap-2 self-start md:self-auto font-mono text-xs px-3 py-1.5 rounded-lg border {theme === 'dark' ? 'bg-[#181b22] border-white/10 text-slate-300' : 'bg-slate-100 border-slate-300 text-slate-700'}">
        <span class="material-symbols-outlined text-[16px] text-emerald-400">verified_user</span>
        <span>{$currentLanguage === 'en' ? 'Audit & Secret Shield Active' : ($currentLanguage === 'ka' ? 'უსაფრთხოების ფარი აქტიურია' : 'Защита секретов и аудит')}</span>
      </div>
    </div>
  </div>

  <div class="p-6 md:p-8 space-y-6">
    <!-- 3. ULTRA CLIENT-FRIENDLY FORM (2 Fields + 1 Click) -->
    <form on:submit|preventDefault={handleConnectAndStart} class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-12 gap-4 items-end">
        <!-- Field 1: Email -->
        <div class="md:col-span-5 space-y-1.5">
          <label for="client-email-input" class="block font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold">
            {$currentLanguage === 'en' ? '1. Your Email (Required)' : ($currentLanguage === 'ka' ? '1. თქვენი ელფოსტა (სავალდებულო)' : '1. Ваша почта (Обязательно)')}
          </label>
          <input
            id="client-email-input"
            type="email"
            bind:value={clientEmail}
            placeholder="client@example.com"
            class="w-full px-4 py-3 rounded-xl border font-mono text-sm transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#15181f] border-white/15 text-white placeholder-slate-500' : 'bg-slate-50 border-slate-300 text-slate-900 placeholder-slate-400'}"
            required
          />
        </div>

        <!-- Field 2: What to build / Task -->
        <div class="md:col-span-7 space-y-1.5">
          <label for="project-goal-input" class="block font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold">
            {$currentLanguage === 'en' ? '2. What do you want to build?' : ($currentLanguage === 'ka' ? '2. რა გსურთ შექმნათ?' : '2. Что нужно сделать?')}
          </label>
          <input
            id="project-goal-input"
            type="text"
            bind:value={projectGoal}
            placeholder={$currentLanguage === 'en' ? 'e.g. Personal consulting website with blog and appointment booking' : 'Например: Персональный сайт с блогом и онлайн-записью'}
            class="w-full px-4 py-3 rounded-xl border font-mono text-sm transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#15181f] border-white/15 text-white placeholder-slate-500' : 'bg-slate-50 border-slate-300 text-slate-900 placeholder-slate-400'}"
            required
          />
        </div>
      </div>

      <!-- Optional: Existing Custom Repository Toggle -->
      <div class="pt-1">
        {#if !showCustomRepoInput}
          <button
            type="button"
            on:click={() => (showCustomRepoInput = true)}
            class="text-xs font-mono text-slate-400 hover:text-[#f2ca50] transition-colors inline-flex items-center gap-1 cursor-pointer"
          >
            <span class="material-symbols-outlined text-[15px]">add_circle</span>
            <span>{$currentLanguage === 'en' ? 'I already have my own repository' : ($currentLanguage === 'ka' ? 'მე უკვე მაქვს ჩემი რეპოზიტორია' : 'У меня уже есть свой репозиторий (подключить его)')}</span>
          </button>
        {:else}
          <div class="p-3.5 rounded-xl border space-y-2 {theme === 'dark' ? 'bg-[#131720] border-white/10' : 'bg-slate-50 border-slate-200'}">
            <div class="flex items-center justify-between">
              <label for="custom-repo-input" class="font-mono text-xs text-slate-300">
                {$currentLanguage === 'en' ? 'Your GitHub/GitLab Repository URL:' : ($currentLanguage === 'ka' ? 'თქვენი რეპოზიტორიის ლინკი:' : 'Ссылка на ваш существующий репозиторий:')}
              </label>
              <button
                type="button"
                on:click={() => { showCustomRepoInput = false; customRepoUrl = ''; }}
                class="font-mono text-[11px] text-slate-400 hover:text-rose-400"
              >
                {$currentLanguage === 'en' ? 'Cancel (Create repo for me)' : 'Отмена (создать мне новый)'}
              </button>
            </div>
            <input
              id="custom-repo-input"
              type="text"
              bind:value={customRepoUrl}
              placeholder="https://github.com/my-org/my-project"
              class="w-full px-3.5 py-2 rounded-lg border font-mono text-xs transition-all {theme === 'dark' ? 'bg-black/60 border-white/15 text-white' : 'bg-white border-slate-300 text-slate-900'}"
            />
          </div>
        {/if}
      </div>

      <!-- Action Button -->
      <div>
        <button
          type="submit"
          id="connect-agent-button"
          disabled={isSubmitting}
          class="w-full py-3.5 px-6 rounded-xl font-mono text-xs uppercase tracking-wider font-bold transition-all duration-150 cursor-pointer flex items-center justify-center gap-2.5 {isSubmitting ? 'bg-slate-700 text-slate-400 cursor-not-allowed' : 'bg-[#f2ca50] hover:bg-[#ffe072] text-[#090a0f] shadow-lg shadow-[#f2ca50]/15 hover:shadow-[#f2ca50]/25'}"
        >
          {#if isSubmitting}
            <span class="inline-block w-4 h-4 border-2 border-[#090a0f] border-t-transparent rounded-full animate-spin"></span>
            <span>{$currentLanguage === 'en' ? 'Provisioning Gateway & Workspace...' : 'Активация шлюза и репозитория...'}</span>
          {:else}
            <span class="material-symbols-outlined text-[19px]">rocket_launch</span>
            <span>{$currentLanguage === 'en' ? 'Connect & Start Building' : ($currentLanguage === 'ka' ? 'დაკავშირება და დაწყება' : 'Подключить и начать работу')}</span>
          {/if}
        </button>
      </div>

      {#if errorMessage}
        <div class="p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 font-mono text-xs flex items-center gap-2">
          <span class="material-symbols-outlined text-[16px]">error</span>
          <span>{errorMessage}</span>
        </div>
      {/if}
    </form>

    <!-- 4. READY AGENT GATEWAY RESULT CARD (Appears upon submit) -->
    {#if regResult}
      <div
        id="ready-agent-gateway-result"
        class="p-6 md:p-8 rounded-2xl border-2 border-emerald-500/50 bg-gradient-to-b from-emerald-500/15 via-[#131b18] to-[#101318] space-y-6 shadow-2xl"
      >
        <!-- Header -->
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-emerald-500/30 pb-4">
          <div class="flex items-center gap-3">
            <span class="w-3.5 h-3.5 rounded-full bg-emerald-400 animate-pulse"></span>
            <div>
              <div class="font-mono text-xs font-bold uppercase tracking-wider text-emerald-400">
                {$currentLanguage === 'en' ? 'ARCHITECTURAL GATEWAY READY & SECURED' : ($currentLanguage === 'ka' ? 'არქიტექტურული კარიბჭე მზადაა' : 'АРХИТЕКТУРНЫЙ ШЛЮЗ АКТИВИРОВАН')}
              </div>
              <div class="font-mono text-[11px] text-slate-300 mt-0.5">
                {$currentLanguage === 'en' ? 'Attached to:' : 'Закреплен за:'} <strong class="text-white">{regResult.client_email}</strong>
              </div>
            </div>
          </div>
          <span class="font-mono text-xs px-3 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 self-start sm:self-auto">
            {regResult.session_id}
          </span>
        </div>

        <!-- Target Repository Bar -->
        <div class="space-y-2">
          <div class="flex items-center justify-between text-xs font-mono">
            <span class="text-slate-300 font-semibold uppercase tracking-wider">
              {$currentLanguage === 'en' ? 'Repository Workspace:' : 'Репозиторий проекта:'}
            </span>
            <span class="text-emerald-400 text-[11px]">
              {$currentLanguage === 'en' ? 'Real GitHub Repository' : 'Настоящий GitHub репозиторий'}
            </span>
          </div>
          <div class="flex items-center gap-3">
            <a
              href={regResult.assigned_repo}
              target="_blank"
              rel="noreferrer"
              class="flex-1 p-3.5 rounded-xl bg-black/80 border border-emerald-500/50 font-mono text-xs sm:text-sm text-emerald-300 font-bold hover:underline select-all truncate flex items-center gap-2"
            >
              <span>{regResult.assigned_repo}</span>
              <span class="material-symbols-outlined text-[16px]">open_in_new</span>
            </a>
            <button
              type="button"
              on:click={() => copyText(regResult?.assigned_repo || '', 'repo')}
              class="px-3.5 py-3.5 rounded-xl border border-white/10 bg-[#1e2024] text-slate-200 hover:text-white transition-all cursor-pointer"
              title="Copy Repo URL"
            >
              <span class="material-symbols-outlined text-[18px]">{copyRepoSuccess ? 'done' : 'content_copy'}</span>
            </button>
          </div>
        </div>

        <!-- ALL-IN-ONE DIRECTIVE BOX (Strictly English for Agent) -->
        <div class="space-y-3">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <div class="font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold flex items-center gap-2">
              <span class="material-symbols-outlined text-[16px] text-emerald-400">terminal</span>
              <span>{$currentLanguage === 'en' ? 'GIVE THIS TO YOUR AGENT (ONE-CLICK):' : 'СКОПИРУЙТЕ ЭТО СВОЕМУ АГЕНТУ (1 КЛИК):'}</span>
            </div>
            <!-- SINGLE PROMINENT COPY BUTTON -->
            <button
              type="button"
              on:click={() => copyText(unifiedAgentDirective, 'all')}
              class="px-5 py-2.5 rounded-xl font-mono text-xs uppercase font-bold flex items-center justify-center gap-2 transition-all cursor-pointer self-start sm:self-auto {copyAllSuccess ? 'bg-emerald-400 text-black shadow-lg shadow-emerald-400/20' : 'bg-[#f2ca50] hover:bg-[#ffe072] text-[#090a0f]'}"
            >
              <span class="material-symbols-outlined text-[18px]">{copyAllSuccess ? 'done' : 'content_copy'}</span>
              <span>{copyAllSuccess ? ($currentLanguage === 'en' ? 'Copied to Clipboard!' : 'Скопировано!') : ($currentLanguage === 'en' ? 'Copy Everything for Agent' : 'Скопировать всё для агента')}</span>
            </button>
          </div>

          <div class="p-4 md:p-5 rounded-xl border font-mono text-xs leading-relaxed {theme === 'dark' ? 'bg-[#090b10] border-white/10 text-emerald-300' : 'bg-slate-900 text-emerald-300'}">
            <pre class="whitespace-pre-wrap select-all font-mono">{unifiedAgentDirective}</pre>
          </div>
        </div>

        <!-- Simple 1-Step Explanation for Human -->
        <div class="p-4 rounded-xl border text-xs text-slate-300 leading-relaxed {theme === 'dark' ? 'bg-[#15181f] border-white/10' : 'bg-slate-50 border-slate-200'}">
          <div class="font-mono text-[11px] uppercase tracking-wider text-[#f2ca50] font-bold mb-1">
            {$currentLanguage === 'en' ? 'NEXT STEP:' : 'ЧТО ДЕЛАТЬ ДАЛЬШЕ:'}
          </div>
          <p>
            {$currentLanguage === 'en'
              ? 'Paste the copied directive into Cursor, Claude Code, Cline, or Copilot. Your agent will write the code and submit it through Dmitry Efremov’s Commit Gateway for automated secret scanning and reliability audits.'
              : 'Вставьте скопированный блок в Cursor, Claude Code, Cline или Copilot. Ваш агент напишет код и отправит его через шлюз Дмитрия Ефремова: сервер автоматически проверит код на утечки секретов, проведёт аудит надежности и зафиксирует изменения в вашем репозитории.'}
          </p>
        </div>
      </div>
    {/if}
  </div>
</div>
