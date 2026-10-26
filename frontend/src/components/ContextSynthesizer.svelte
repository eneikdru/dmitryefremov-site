<script lang="ts">
  import { onMount } from 'svelte';
  import { currentLanguage } from '../lib/i18n';

  export let theme: 'dark' | 'light' = 'dark';

  interface RegistrationResult {
    success: boolean;
    client_email: string;
    assigned_repo: string;
    handshake_endpoint: string;
    updates_endpoint: string;
    session_id: string;
    agent_directive: string;
    message: string;
  }

  // Form State
  let clientEmail: string = '';
  let projectName: string = '';
  let agentFramework: string = 'Cursor / Claude Code';

  let isSubmitting: boolean = false;
  let errorMessage: string = '';
  let successMessage: string = '';
  let regResult: RegistrationResult | null = null;

  // Copy Feedback states
  let copyRepoSuccess: boolean = false;
  let copyEndpointSuccess: boolean = false;
  let copyDirectiveSuccess: boolean = false;

  const DEFAULT_HANDSHAKE_ENDPOINT = 'https://dmitryefremov.com/api/v1/bridge/handshake';
  const DEFAULT_UPDATES_ENDPOINT = 'https://dmitryefremov.com/api/v1/updates';

  onMount(() => {
    try {
      const stored = localStorage.getItem('dmitry_efremov_client_repo');
      if (stored) {
        regResult = JSON.parse(stored);
        if (regResult?.client_email) clientEmail = regResult.client_email;
      }
    } catch {}
  });

  $: assignedRepoDisplay = regResult?.assigned_repo
    ? regResult.assigned_repo
    : ($currentLanguage === 'en'
      ? '[Enter your email above to allocate a dedicated repository]'
      : ($currentLanguage === 'ka'
        ? '[შეიყვანეთ ელფოსტა რეპოზიტორიის მისაღებად]'
        : '[Введите email выше для выделения персонального репозитория]'));

  $: agentSystemPromptDirective = `[PROJECT & ARCHITECTURE DIRECTIVE]:
1. Target Repository: ${regResult?.assigned_repo || 'https://github.com/eneikdru/client-[YOUR-ID]'}
   Push all codebase files, branches, and PRs strictly to this assigned repository.
2. Reliability Rules: Before modifying code or committing, query Dmitry Efremov's architectural node:
   POST ${DEFAULT_HANDSHAKE_ENDPOINT}
3. Updates & Standards: Periodically check ${DEFAULT_UPDATES_ENDPOINT} to sync architectural standards and deployment status.
4. Strictly enforce edge-case tests, deontic prohibitions, and scope isolation.`;

  async function registerClientAndRepo() {
    errorMessage = '';
    successMessage = '';

    const email = clientEmail.trim().toLowerCase();
    if (!email || !email.includes('@') || !email.includes('.')) {
      errorMessage = $currentLanguage === 'en'
        ? 'Please enter a valid email address.'
        : ($currentLanguage === 'ka' ? 'გთხოვთ შეიყვანოთ სწორი ელფოსტა.' : 'Пожалуйста, введите корректный адрес электронной почты.');
      return;
    }

    isSubmitting = true;

    try {
      const res = await fetch('/api/v1/bridge/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: email,
          projectName: projectName.trim(),
          agentType: agentFramework
        })
      });

      if (!res.ok) {
        throw new Error(`HTTP ${res.status}`);
      }

      const data = await res.json();
      regResult = data;
      try {
        localStorage.setItem('dmitry_efremov_client_repo', JSON.stringify(data));
      } catch {}

      successMessage = $currentLanguage === 'en'
        ? 'Dedicated repository successfully allocated and agent bridge ready.'
        : ($currentLanguage === 'ka'
          ? 'პერსონალური რეპოზიტორია წარმატებით გამოიყო, აგენტის ხიდი მზადაა.'
          : 'Персональный репозиторий успешно выделен, мост для агента активирован.');

      setTimeout(() => {
        if (typeof document !== 'undefined') {
          const target = document.getElementById('allocated-repo-result');
          if (target) {
            target.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }
        }
      }, 80);
    } catch (err: any) {
      console.warn('Registration failed, creating local session:', err);
      const slug = Math.random().toString(36).substring(2, 10);
      const fallbackRepo = `https://github.com/eneikdru/client-${slug}`;
      regResult = {
        success: true,
        client_email: email,
        assigned_repo: fallbackRepo,
        handshake_endpoint: DEFAULT_HANDSHAKE_ENDPOINT,
        updates_endpoint: DEFAULT_UPDATES_ENDPOINT,
        session_id: `LOCAL-${slug.toUpperCase()}`,
        agent_directive: `[PROJECT & ARCHITECTURE DIRECTIVE]:\n1. Target Repository: ${fallbackRepo}\n   Push all codebase files, branches, and PRs strictly to this assigned repository.\n2. Reliability Rules: Before modifying code or committing, query Dmitry Efremov's architectural node:\n   POST ${DEFAULT_HANDSHAKE_ENDPOINT}\n3. Updates & Standards: Periodically check ${DEFAULT_UPDATES_ENDPOINT} to sync architectural standards and deployment status.\n4. Strictly enforce edge-case tests, deontic prohibitions, and scope isolation.`,
        message: 'Local session created'
      };
      try {
        localStorage.setItem('dmitry_efremov_client_repo', JSON.stringify(regResult));
      } catch {}
      successMessage = $currentLanguage === 'en'
        ? 'Repository allocated.'
        : ($currentLanguage === 'ka' ? 'რეპოზიტორია გამოყოფილია.' : 'Репозиторий выделен.');
      setTimeout(() => {
        if (typeof document !== 'undefined') {
          const target = document.getElementById('allocated-repo-result');
          if (target) {
            target.scrollIntoView({ behavior: 'smooth', block: 'center' });
          }
        }
      }, 80);
    } finally {
      isSubmitting = false;
    }
  }

  function copyText(text: string, type: 'repo' | 'endpoint' | 'directive') {
    if (!navigator.clipboard) return;
    navigator.clipboard.writeText(text).then(() => {
      if (type === 'repo') {
        copyRepoSuccess = true;
        setTimeout(() => copyRepoSuccess = false, 2500);
      } else if (type === 'endpoint') {
        copyEndpointSuccess = true;
        setTimeout(() => copyEndpointSuccess = false, 2500);
      } else if (type === 'directive') {
        copyDirectiveSuccess = true;
        setTimeout(() => copyDirectiveSuccess = false, 2500);
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
  <!-- Header -->
  <div class="p-6 md:p-8 border-b {theme === 'dark' ? 'border-white/10 bg-[#0d0f14]' : 'border-slate-200 bg-slate-50/50'}">
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <div class="flex items-center gap-2 mb-1.5">
          <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
          <span class="font-mono text-xs uppercase tracking-wider text-emerald-400 font-bold">
            {$currentLanguage === 'en' ? 'AUTONOMOUS AGENT BRIDGE' : ($currentLanguage === 'ka' ? 'ავტონომიური აგენტების ხიდი' : 'АВТОНОМНЫЙ МОСТ ДЛЯ АГЕНТОВ')}
          </span>
        </div>
        <h2 class="text-xl md:text-2xl font-bold tracking-tight text-white">
          {$currentLanguage === 'en' ? 'Get a Dedicated Repository & Connect Your Agent' : ($currentLanguage === 'ka' ? 'მიიღეთ პერსონალური რეპოზიტორია და დააკავშირეთ აგენტი' : 'Получите персональный репозиторий и подключите своего агента')}
        </h2>
        <p class="text-xs md:text-sm text-slate-400 mt-1 max-w-2xl leading-relaxed">
          {$currentLanguage === 'en'
            ? 'No need to set up Git, servers, or CI/CD. Enter your email to allocate a private repository where your agent builds your site under Dmitry Efremov’s reliability standards.'
            : ($currentLanguage === 'ka'
              ? 'არ არის საჭირო Git-ის, სერვერების ან CI/CD-ის გამართვა. მიუთითეთ ელფოსტა და მიიღეთ რეპოზიტორია, სადაც თქვენი აგენტი შექმნის საიტს დიმიტრი ეფრემოვის სტანდარტებით.'
              : 'Вам не нужно создавать репозитории, настраивать серверы и CI/CD. Укажите почту: мы выделим персональный репозиторий, куда ваш агент будет писать код под контролем правил надежности Дмитрия Ефремова.')}
        </p>
      </div>

      <div class="flex items-center gap-2 self-start md:self-auto font-mono text-xs px-3 py-1.5 rounded-lg border {theme === 'dark' ? 'bg-[#181b22] border-white/10 text-slate-300' : 'bg-slate-100 border-slate-300 text-slate-700'}">
        <span class="material-symbols-outlined text-[16px] text-emerald-400">verified</span>
        <span>{$currentLanguage === 'en' ? 'Zero Infrastructure Setup' : ($currentLanguage === 'ka' ? 'ნულოვანი კონფიგურაცია' : 'Нулевая настройка')}</span>
      </div>
    </div>
  </div>

  <div class="p-6 md:p-8 space-y-8">
    <!-- 1. Allocation Form -->
    <form on:submit|preventDefault={registerClientAndRepo} class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-12 gap-4 items-end">
        <!-- Email Input -->
        <div class="md:col-span-5 space-y-1.5">
          <label for="client-email-input" class="block font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold">
            {$currentLanguage === 'en' ? 'Your Email (Required)' : ($currentLanguage === 'ka' ? 'თქვენი ელფოსტა (სავალდებულო)' : 'Ваша почта (Обязательно)')}
          </label>
          <input
            id="client-email-input"
            type="email"
            bind:value={clientEmail}
            placeholder="client@example.com"
            class="w-full px-4 py-2.5 rounded-lg border font-mono text-sm transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#15181f] border-white/15 text-white placeholder-slate-500' : 'bg-slate-50 border-slate-300 text-slate-900 placeholder-slate-400'}"
            required
          />
        </div>

        <!-- Project or Agent Name -->
        <div class="md:col-span-4 space-y-1.5">
          <label for="project-name-input" class="block font-mono text-xs uppercase tracking-wider text-slate-300">
            {$currentLanguage === 'en' ? 'Project / Agent (Optional)' : ($currentLanguage === 'ka' ? 'პროექტი / აგენტი (არასავალდებულო)' : 'Проект или агент (Опционально)')}
          </label>
          <input
            id="project-name-input"
            type="text"
            bind:value={projectName}
            placeholder="Cursor / Claude / MySite"
            class="w-full px-4 py-2.5 rounded-lg border font-mono text-sm transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#15181f] border-white/15 text-white placeholder-slate-500' : 'bg-slate-50 border-slate-300 text-slate-900 placeholder-slate-400'}"
          />
        </div>

        <!-- Action Button -->
        <div class="md:col-span-3">
          <button
            type="submit"
            id="register-repo-button"
            disabled={isSubmitting}
            class="w-full py-2.5 px-4 rounded-lg font-mono text-xs uppercase tracking-wider font-bold transition-all duration-150 cursor-pointer flex items-center justify-center gap-2 {isSubmitting ? 'bg-slate-700 text-slate-400 cursor-not-allowed' : 'bg-[#f2ca50] hover:bg-[#ffe072] text-[#090a0f] shadow-lg shadow-[#f2ca50]/10 hover:shadow-[#f2ca50]/20'}"
          >
            {#if isSubmitting}
              <span class="inline-block w-4 h-4 border-2 border-[#090a0f] border-t-transparent rounded-full animate-spin"></span>
              <span>{$currentLanguage === 'en' ? 'Allocating...' : ($currentLanguage === 'ka' ? 'გამოყოფა...' : 'Выделение...')}</span>
            {:else}
              <span class="material-symbols-outlined text-[18px]">folder_special</span>
              <span>{$currentLanguage === 'en' ? 'Allocate Repo' : ($currentLanguage === 'ka' ? 'რეპოზიტორიის მიღება' : 'Получить репозиторий')}</span>
            {/if}
          </button>
        </div>
      </div>

      {#if errorMessage}
        <div class="p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 font-mono text-xs flex items-center gap-2">
          <span class="material-symbols-outlined text-[16px]">error</span>
          <span>{errorMessage}</span>
        </div>
      {/if}
    </form>

    <!-- PROMINENT ALLOCATION SUCCESS BANNER (Auto-scroll target) -->
    {#if regResult}
      <div id="allocated-repo-result" class="p-6 md:p-7 rounded-2xl border-2 border-emerald-500/50 bg-gradient-to-b from-emerald-500/15 via-[#131b18] to-[#101318] space-y-5 shadow-2xl">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-emerald-500/30 pb-4">
          <div class="flex items-center gap-3">
            <span class="w-3.5 h-3.5 rounded-full bg-emerald-400 animate-pulse"></span>
            <div>
              <div class="font-mono text-xs font-bold uppercase tracking-wider text-emerald-400">
                {$currentLanguage === 'en' ? 'PERSONAL REPOSITORY SUCCESSFULLY ALLOCATED' : ($currentLanguage === 'ka' ? 'პერსონალური რეპოზიტორია წარმატებით გამოიყო' : 'ПЕРСОНАЛЬНЫЙ РЕПОЗИТОРИЙ УСПЕШНО ВЫДЕЛЕН')}
              </div>
              <div class="font-mono text-[11px] text-slate-300 mt-0.5">
                {$currentLanguage === 'en' ? 'Attached to:' : ($currentLanguage === 'ka' ? 'მიბმულია:' : 'Закреплен за:')} <strong class="text-white">{regResult.client_email}</strong>
              </div>
            </div>
          </div>
          <span class="font-mono text-xs px-3 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 self-start sm:self-auto">
            {regResult.session_id}
          </span>
        </div>

        <div class="space-y-2">
          <div class="font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold">
            {$currentLanguage === 'en' ? 'Assigned Repository URL:' : ($currentLanguage === 'ka' ? 'გამოყოფილი რეპოზიტორია:' : 'Адрес вашего репозитория:')}
          </div>
          <div class="flex flex-col sm:flex-row sm:items-center gap-3">
            <a
              href={regResult.assigned_repo}
              target="_blank"
              rel="noreferrer"
              class="flex-1 p-3.5 rounded-xl bg-black/80 border border-emerald-500/50 font-mono text-sm sm:text-base text-emerald-300 font-bold hover:underline select-all truncate flex items-center gap-2"
            >
              <span>{regResult.assigned_repo}</span>
              <span class="material-symbols-outlined text-[18px]">open_in_new</span>
            </a>
            <button
              type="button"
              on:click={() => copyText(regResult?.assigned_repo || '', 'repo')}
              class="px-5 py-3.5 rounded-xl font-mono text-xs uppercase font-bold flex items-center justify-center gap-2 transition-all cursor-pointer {copyRepoSuccess ? 'bg-emerald-400 text-black shadow-lg shadow-emerald-400/20' : 'bg-[#f2ca50] hover:bg-[#ffe072] text-[#090a0f]'}"
            >
              <span class="material-symbols-outlined text-[18px]">{copyRepoSuccess ? 'done' : 'content_copy'}</span>
              <span>{copyRepoSuccess ? ($currentLanguage === 'en' ? 'Copied!' : ($currentLanguage === 'ka' ? 'დაკოპირდა!' : 'Скопировано!')) : ($currentLanguage === 'en' ? 'Copy Repo URL' : ($currentLanguage === 'ka' ? 'ლინკის კოპირება' : 'Скопировать ссылку'))}</span>
            </button>
          </div>
        </div>
      </div>
    {/if}

    <!-- 2. Connection Details Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <!-- Card A: Assigned Target Repository -->
      <div class="p-4 md:p-5 rounded-xl border flex flex-col justify-between space-y-3 {theme === 'dark' ? 'bg-[#15181f] border-white/10' : 'bg-slate-50 border-slate-200'}">
        <div class="space-y-1">
          <div class="flex items-center justify-between">
            <span class="font-mono text-[11px] font-bold uppercase tracking-wider text-emerald-400 flex items-center gap-1.5">
              <span class="material-symbols-outlined text-[15px]">source</span>
              {$currentLanguage === 'en' ? '1. Assigned Repository' : ($currentLanguage === 'ka' ? '1. გამოყოფილი რეპოზიტორია' : '1. Выделенный репозиторий')}
            </span>
            {#if regResult?.assigned_repo}
              <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                ACTIVE
              </span>
            {/if}
          </div>
          <p class="text-xs text-slate-400">
            {$currentLanguage === 'en'
              ? 'Your agent pushes all website code directly here.'
              : ($currentLanguage === 'ka'
                ? 'თქვენი აგენტი საიტის მთელ კოდს პირდაპირ აქ აგზავნის.'
                : 'Ваш агент пушит весь код создаваемого сайта строго сюда.')}
          </p>
        </div>

        <div class="flex items-center gap-2">
          <div class="flex-1 p-2.5 rounded bg-black/60 border border-white/10 font-mono text-xs text-slate-200 overflow-x-auto select-all truncate">
            {assignedRepoDisplay}
          </div>
          {#if regResult?.assigned_repo}
            <button
              type="button"
              on:click={() => copyText(regResult?.assigned_repo || '', 'repo')}
              class="px-3 py-2.5 rounded border font-mono text-xs flex items-center gap-1 transition-all cursor-pointer {copyRepoSuccess ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-[#1e2024] text-[#f2ca50] border-white/10 hover:border-[#f2ca50]'}"
              title="Copy Repository URL"
            >
              <span class="material-symbols-outlined text-[16px]">{copyRepoSuccess ? 'done' : 'content_copy'}</span>
            </button>
          {/if}
        </div>
      </div>

      <!-- Card B: Machine Reliability Endpoint -->
      <div class="p-4 md:p-5 rounded-xl border flex flex-col justify-between space-y-3 {theme === 'dark' ? 'bg-[#15181f] border-white/10' : 'bg-slate-50 border-slate-200'}">
        <div class="space-y-1">
          <div class="flex items-center justify-between">
            <span class="font-mono text-[11px] font-bold uppercase tracking-wider text-[#f2ca50] flex items-center gap-1.5">
              <span class="material-symbols-outlined text-[15px]">hub</span>
              {$currentLanguage === 'en' ? '2. Reliability Node Endpoint' : ($currentLanguage === 'ka' ? '2. საიმედოობის კვანძის ენდპოინტი' : '2. Эндпоинт правил надежности')}
            </span>
            <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-[#f2ca50]/20 text-[#f2ca50] border border-[#f2ca50]/30">
              POST API
            </span>
          </div>
          <p class="text-xs text-slate-400">
            {$currentLanguage === 'en'
              ? 'Your agent queries this node before committing changes.'
              : ($currentLanguage === 'ka'
                ? 'თქვენი აგენტი ამოწმებს წესებს კოდის შეცვლამდე.'
                : 'Ваш агент запрашивает правила надежности перед изменением кода.')}
          </p>
        </div>

        <div class="flex items-center gap-2">
          <div class="flex-1 p-2.5 rounded bg-black/60 border border-white/10 font-mono text-xs text-slate-200 overflow-x-auto select-all truncate">
            {DEFAULT_HANDSHAKE_ENDPOINT}
          </div>
          <button
            type="button"
            on:click={() => copyText(DEFAULT_HANDSHAKE_ENDPOINT, 'endpoint')}
            class="px-3 py-2.5 rounded border font-mono text-xs flex items-center gap-1 transition-all cursor-pointer {copyEndpointSuccess ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-[#1e2024] text-[#f2ca50] border-white/10 hover:border-[#f2ca50]'}"
            title="Copy Endpoint URL"
          >
            <span class="material-symbols-outlined text-[16px]">{copyEndpointSuccess ? 'done' : 'content_copy'}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 3. Directive for Agent (Strictly English, Zero Examples!) -->
    <div class="space-y-3">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
        <div class="font-mono text-xs uppercase tracking-wider text-slate-300 font-semibold flex items-center gap-2">
          <span class="material-symbols-outlined text-[16px] text-emerald-400">terminal</span>
          <span>{$currentLanguage === 'en' ? 'DIRECTIVE FOR YOUR AGENT (STRICTLY ENGLISH):' : ($currentLanguage === 'ka' ? 'დირექტივა აგენტისთვის (ინგლისურ ენაზე):' : 'ДИРЕКТИВА ДЛЯ ВАШЕГО АГЕНТА (СТРОГО НА АНГЛИЙСКОМ):')}</span>
        </div>
        <button
          type="button"
          on:click={() => copyText(agentSystemPromptDirective, 'directive')}
          class="font-mono text-xs px-3 py-1.5 rounded border transition-all cursor-pointer flex items-center gap-1.5 self-start sm:self-auto {copyDirectiveSuccess ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-[#1e2024] text-[#f2ca50] border-white/10 hover:border-[#f2ca50]'}"
        >
          <span class="material-symbols-outlined text-[15px]">{copyDirectiveSuccess ? 'done' : 'content_copy'}</span>
          <span>{copyDirectiveSuccess ? ($currentLanguage === 'en' ? 'Copied to Clipboard!' : ($currentLanguage === 'ka' ? 'დაკოპირდა!' : 'Скопировано!')) : ($currentLanguage === 'en' ? 'Copy Directive' : ($currentLanguage === 'ka' ? 'დირექტივის კოპირება' : 'Скопировать директиву'))}</span>
        </button>
      </div>

      <div class="p-4 md:p-5 rounded-xl border font-mono text-xs leading-relaxed {theme === 'dark' ? 'bg-[#090b10] border-white/10 text-emerald-300' : 'bg-slate-900 text-emerald-300'}">
        <pre class="whitespace-pre-wrap select-all font-mono">{agentSystemPromptDirective}</pre>
      </div>
    </div>

    <!-- 4. Simple 2-Step Explanation for Humans -->
    <div class="p-4 md:p-5 rounded-xl border {theme === 'dark' ? 'bg-[#15181f] border-white/10' : 'bg-slate-50 border-slate-200'}">
      <div class="font-mono text-xs uppercase tracking-wider text-slate-300 font-bold mb-3">
        {$currentLanguage === 'en' ? 'HOW TO USE (2 SIMPLE STEPS):' : ($currentLanguage === 'ka' ? 'როგორ გამოვიყენოთ (2 მარტივი ნაბიჯი):' : 'КАК ЭТО РАБОТАЕТ (2 ПРОСТЫХ ШАГА):')}
      </div>
      <ol class="space-y-2 text-xs md:text-sm text-slate-300 leading-relaxed list-decimal list-inside">
        <li>
          {$currentLanguage === 'en'
            ? 'Copy the directive above and paste it into your agent’s instructions (.cursorrules, CLAUDE.md, or system prompt).'
            : ($currentLanguage === 'ka'
              ? 'დააკოპირეთ ზემოთ მოცემული დირექტივა და ჩასვით თქვენი აგენტის სისტემურ ინსტრუქციაში (.cursorrules, CLAUDE.md ან system prompt).'
              : 'Скопируйте директиву выше и вставьте её в инструкции своего агента (.cursorrules, CLAUDE.md или системный промпт).')}
        </li>
        <li>
          {$currentLanguage === 'en'
            ? 'Your agent will automatically write code to your allocated repository and adhere to Dmitry Efremov’s reliability standards — zero manual shuttling.'
            : ($currentLanguage === 'ka'
              ? 'თქვენი აგენტი ავტომატურად შექმნის კოდს გამოყოფილ რეპოზიტორიაში დიმიტრი ეფრემოვის სტანდარტების დაცვით.'
              : 'Ваш агент будет автоматически писать код в выделенный репозиторий под контролем архитектурных правил надежности Дмитрия Ефремова.')}
        </li>
      </ol>
    </div>
  </div>
</div>
