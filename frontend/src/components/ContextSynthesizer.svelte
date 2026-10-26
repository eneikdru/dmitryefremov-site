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

  // Copy State
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
        ? 'Please describe your project goal.'
        : ($currentLanguage === 'ka' ? 'გთხოვთ აღწეროთ თქვენი პროექტის მიზანი.' : 'Опишите цель проекта.');
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
      }, 80);
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
      }, 80);
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
  class="w-full rounded-xl border transition-colors duration-150 overflow-hidden {theme === 'dark' ? 'bg-[#0f1217] border-white/10 text-slate-100' : 'bg-white border-slate-300 text-slate-800'}"
>
  <!-- 1. SOVEREIGN OWNERSHIP MANIFESTO (Zero Visual Noise, Pure Typography) -->
  <div class="px-5 py-3.5 border-b text-xs leading-relaxed {theme === 'dark' ? 'bg-[#0b0e13] border-white/10 text-slate-300' : 'bg-slate-100 border-slate-200 text-slate-700'}">
    <p class="font-mono text-[11px] leading-relaxed">
      {$currentLanguage === 'en'
        ? 'Architectural apparatus by Dmitry Efremov. You remain the sole author and owner of all created code. You may work in your own repository or our managed workspace and export anytime.'
        : ($currentLanguage === 'ka'
          ? 'არქიტექტურული აპარატი — დიმიტრი ეფრემოვის. კოდის სრული ავტორი და მფლობელი რჩებით თქვენ. შეგიძლიათ იმუშაოთ საკუთარ რეპოზიტორიაში ან ნებისმიერ დროს გაიტანოთ პროექტი.'
          : 'Аппарат архитектурного мышления — Дмитрия Ефремова. Автором, правообладателем и владельцем созданного кода на 100% остаётесь вы. Вы можете работать в своём репозитории или использовать выделенный стартер и забрать проект в любой момент.')}
    </p>
  </div>

  <!-- 2. HEADER -->
  <div class="p-6 md:p-7 border-b {theme === 'dark' ? 'border-white/10 bg-[#12161f]' : 'border-slate-200 bg-slate-50'}">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <div class="font-mono text-[11px] uppercase tracking-wider text-[#f2ca50] font-bold mb-1">
          {$currentLanguage === 'en' ? 'ARCHITECTURE GATEWAY' : ($currentLanguage === 'ka' ? 'არქიტექტურული კარიბჭე' : 'АРХИТЕКТУРНЫЙ ШЛЮЗ')}
        </div>
        <h2 class="text-lg md:text-xl font-bold tracking-tight text-white font-mono uppercase">
          {$currentLanguage === 'en' ? 'Connect Your Agent & Start Building' : ($currentLanguage === 'ka' ? 'დააკავშირეთ აგენტი და დაიწყეთ მუშაობა' : 'Подключите агента и сразу начните работу')}
        </h2>
        <p class="text-xs text-slate-400 mt-1 max-w-2xl leading-relaxed">
          {$currentLanguage === 'en'
            ? 'Enter your email, describe what to build, and receive an instant gateway for your agent (Cursor, Claude Code, Cline) with automated secret scanning and reliability audits.'
            : ($currentLanguage === 'ka'
              ? 'მიუთითეთ ელფოსტა, აღწერეთ დავალება და მიიღეთ მზა კარიბჭე თქვენი აგენტისთვის უსაფრთხოების და საიმედოობის ავტომატური აუდიტით.'
              : 'Укажите почту, напишите, что хотите сделать — и сразу получите готовый защитный шлюз для своего агента (Cursor, Claude Code, Cline) со встроенным аудитом надежности и защитой от утечек.')}
        </p>
      </div>

      <div class="font-mono text-[11px] px-3 py-1 rounded border uppercase tracking-wider {theme === 'dark' ? 'bg-[#181c26] border-white/10 text-slate-300' : 'bg-slate-100 border-slate-300 text-slate-700'}">
        {$currentLanguage === 'en' ? 'Pre-Commit Audit Active' : 'Предкоммитный аудит активен'}
      </div>
    </div>
  </div>

  <div class="p-6 md:p-7 space-y-6">
    <!-- 3. SIMPLE 2-FIELD FORM (Strictly No Icons, No Emojis) -->
    <form on:submit|preventDefault={handleConnectAndStart} class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-12 gap-4 items-end">
        <!-- Field 1: Email -->
        <div class="md:col-span-5 space-y-1.5">
          <label for="client-email-input" class="block font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold">
            {$currentLanguage === 'en' ? 'Your Email' : 'Ваша почта'}
          </label>
          <input
            id="client-email-input"
            type="email"
            bind:value={clientEmail}
            placeholder="client@example.com"
            class="w-full px-4 py-2.5 rounded-lg border font-mono text-xs transition-all focus:outline-none focus:ring-1 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#151922] border-white/15 text-white placeholder-slate-500' : 'bg-slate-50 border-slate-300 text-slate-900 placeholder-slate-400'}"
            required
          />
        </div>

        <!-- Field 2: What to build -->
        <div class="md:col-span-7 space-y-1.5">
          <label for="project-goal-input" class="block font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold">
            {$currentLanguage === 'en' ? 'Project Goal / Task' : 'Что нужно сделать?'}
          </label>
          <input
            id="project-goal-input"
            type="text"
            bind:value={projectGoal}
            placeholder={$currentLanguage === 'en' ? 'e.g. Consulting website with blog and appointment booking' : 'Например: Персональный сайт с блогом и контактами'}
            class="w-full px-4 py-2.5 rounded-lg border font-mono text-xs transition-all focus:outline-none focus:ring-1 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#151922] border-white/15 text-white placeholder-slate-500' : 'bg-slate-50 border-slate-300 text-slate-900 placeholder-slate-400'}"
            required
          />
        </div>
      </div>

      <!-- Optional Custom Repo Toggle -->
      <div class="pt-1">
        {#if !showCustomRepoInput}
          <button
            type="button"
            on:click={() => (showCustomRepoInput = true)}
            class="text-[11px] font-mono text-slate-400 hover:text-[#f2ca50] transition-colors cursor-pointer"
          >
            {$currentLanguage === 'en' ? '[+] Use my own existing repository' : '[+] У меня уже есть свой репозиторий (подключить)'}
          </button>
        {:else}
          <div class="p-3 rounded-lg border space-y-2 {theme === 'dark' ? 'bg-[#131720] border-white/10' : 'bg-slate-50 border-slate-200'}">
            <div class="flex items-center justify-between text-xs font-mono">
              <label for="custom-repo-input" class="text-slate-300">
                {$currentLanguage === 'en' ? 'Your Repository URL:' : 'Ссылка на ваш репозиторий:'}
              </label>
              <button
                type="button"
                on:click={() => { showCustomRepoInput = false; customRepoUrl = ''; }}
                class="text-slate-400 hover:text-white"
              >
                [Отмена]
              </button>
            </div>
            <input
              id="custom-repo-input"
              type="text"
              bind:value={customRepoUrl}
              placeholder="https://github.com/my-org/my-project"
              class="w-full px-3 py-2 rounded border font-mono text-xs {theme === 'dark' ? 'bg-black/60 border-white/15 text-white' : 'bg-white border-slate-300 text-slate-900'}"
            />
          </div>
        {/if}
      </div>

      <!-- Action Button (STRICTLY TEXT ONLY, ZERO ICONS / ZERO EMOJIS) -->
      <div>
        <button
          type="submit"
          id="connect-agent-button"
          disabled={isSubmitting}
          class="w-full py-3 px-5 rounded-lg font-mono text-xs uppercase tracking-wider font-bold transition-all duration-150 cursor-pointer text-center {isSubmitting ? 'bg-slate-700 text-slate-400 cursor-not-allowed' : 'bg-[#f2ca50] hover:bg-[#ffe072] text-[#090a0f]'}"
        >
          {isSubmitting
            ? ($currentLanguage === 'en' ? 'PROVISIONING...' : 'ПОДКЛЮЧЕНИЕ...')
            : ($currentLanguage === 'en' ? 'CONNECT AND START' : ($currentLanguage === 'ka' ? 'დაკავშირება და დაწყება' : 'ПОДКЛЮЧИТЬ И НАЧАТЬ РАБОТУ'))}
        </button>
      </div>

      {#if errorMessage}
        <div class="p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 font-mono text-xs">
          {errorMessage}
        </div>
      {/if}
    </form>

    <!-- 4. READY AGENT GATEWAY RESULT CARD (Quiet, Dignified, No Visual Noise) -->
    {#if regResult}
      <div
        id="ready-agent-gateway-result"
        class="p-5 md:p-6 rounded-xl border space-y-5 {theme === 'dark' ? 'bg-[#131722] border-white/15' : 'bg-slate-50 border-slate-300'}"
      >
        <!-- Header -->
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b {theme === 'dark' ? 'border-white/10' : 'border-slate-200'} pb-3 font-mono text-xs">
          <div>
            <span class="font-bold uppercase tracking-wider text-[#f2ca50]">
              {$currentLanguage === 'en' ? 'GATEWAY READY:' : 'ШЛЮЗ АКТИВИРОВАН:'}
            </span>
            <span class="text-slate-300 ml-1">{regResult.client_email}</span>
          </div>
          <span class="text-slate-400 text-[11px]">
            {regResult.session_id}
          </span>
        </div>

        <!-- Target Repository -->
        <div class="space-y-1.5 font-mono text-xs">
          <div class="text-slate-400 uppercase tracking-wider text-[11px]">
            {$currentLanguage === 'en' ? 'Assigned Repository:' : 'Репозиторий проекта:'}
          </div>
          <div class="flex items-center gap-2">
            <a
              href={regResult.assigned_repo}
              target="_blank"
              rel="noreferrer"
              class="flex-1 p-2.5 rounded bg-black/60 border border-white/10 font-mono text-xs text-[#f2ca50] hover:underline select-all truncate"
            >
              {regResult.assigned_repo}
            </a>
            <a
              href={regResult.assigned_repo}
              target="_blank"
              rel="noreferrer"
              class="px-3 py-2.5 rounded border border-white/10 bg-[#1e222d] text-slate-300 hover:text-white text-xs font-mono uppercase whitespace-nowrap"
            >
              {$currentLanguage === 'en' ? 'Open' : 'Открыть'}
            </a>
            <button
              type="button"
              on:click={() => copyText(regResult?.assigned_repo || '', 'repo')}
              class="px-3 py-2.5 rounded border border-white/10 bg-[#1e222d] text-slate-300 hover:text-white text-xs font-mono uppercase whitespace-nowrap cursor-pointer"
            >
              {copyRepoSuccess ? ($currentLanguage === 'en' ? 'Copied' : 'Скопировано') : ($currentLanguage === 'en' ? 'Copy URL' : 'Копировать')}
            </button>
          </div>
        </div>

        <!-- ALL-IN-ONE DIRECTIVE (Zero Emojis, Pure Text Copy Button) -->
        <div class="space-y-2">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <span class="font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold">
              {$currentLanguage === 'en' ? 'DIRECTIVE FOR YOUR AGENT:' : 'ДИРЕКТИВА ДЛЯ ВАШЕГО АГЕНТА:'}
            </span>
            <!-- SINGLE PROMINENT TEXT-ONLY COPY BUTTON -->
            <button
              type="button"
              on:click={() => copyText(unifiedAgentDirective, 'all')}
              class="px-4 py-2 rounded font-mono text-xs uppercase font-bold transition-all cursor-pointer {copyAllSuccess ? 'bg-emerald-500 text-black' : 'bg-[#f2ca50] text-[#090a0f] hover:bg-[#ffe072]'}"
            >
              {copyAllSuccess
                ? ($currentLanguage === 'en' ? 'COPIED TO CLIPBOARD' : 'СКОПИРОВАНО В БУФЕР')
                : ($currentLanguage === 'en' ? 'COPY EVERYTHING FOR AGENT' : 'СКОПИРОВАТЬ ВСЁ ДЛЯ АГЕНТА')}
            </button>
          </div>

          <div class="p-4 rounded-lg border font-mono text-xs leading-relaxed {theme === 'dark' ? 'bg-[#090b10] border-white/10 text-slate-300' : 'bg-slate-900 text-slate-200'}">
            <pre class="whitespace-pre-wrap select-all font-mono">{unifiedAgentDirective}</pre>
          </div>
        </div>

        <!-- 1-Step Explanation -->
        <div class="p-3 rounded border text-xs text-slate-400 font-mono leading-relaxed {theme === 'dark' ? 'bg-[#0e1219] border-white/5' : 'bg-slate-100 border-slate-200'}">
          {$currentLanguage === 'en'
            ? 'Paste this directive into Cursor, Claude Code, or Cline. The agent will write code and submit it via Dmitry Efremov’s Commit Gateway for automated secret scanning and invariant verification.'
            : 'Вставьте скопированный блок в Cursor, Claude Code или Cline. Агент напишет код и отправит его через шлюз коммитов Дмитрия Ефремова: сервер автоматически проверит код на утечки ключей, проведёт аудит надежности и зафиксирует изменения в репозитории.'}
        </div>
      </div>
    {/if}
  </div>
</div>
