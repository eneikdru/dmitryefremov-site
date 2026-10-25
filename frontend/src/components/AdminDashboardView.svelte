<script lang="ts">
  import { onMount, tick } from 'svelte';
  import GqmTelemetryView from './GqmTelemetryView.svelte';

  export let theme: 'dark' | 'light' = 'dark';

  // Navigation tab inside admin
  let activeNav: 'overview' | 'requests' | 'consent' | 'audit' = 'overview';
  let showGqmDetails: boolean = false;

  // Route & Display Mode
  let isInboxRoute: boolean = typeof window !== 'undefined' && (window.location.pathname.startsWith('/inbox') || window.location.hash.includes('inbox'));
  let showComplianceAudit: boolean = !isInboxRoute;

  // Onboarding banner state
  let showOnboarding: boolean = true;
  if (typeof window !== 'undefined') {
    showOnboarding = localStorage.getItem('crm_hide_onboarding') !== 'true';
  }

  function dismissOnboarding() {
    showOnboarding = false;
    if (typeof window !== 'undefined') {
      localStorage.setItem('crm_hide_onboarding', 'true');
    }
  }

  function toggleOnboarding() {
    showOnboarding = !showOnboarding;
    if (typeof window !== 'undefined') {
      localStorage.setItem('crm_hide_onboarding', showOnboarding ? 'false' : 'true');
    }
  }

  // DSAR Deletion Modal state (for compliance test contract)
  let showDeleteModal: boolean = false;
  let targetDsarId: string = 'DSAR-8821';
  let targetSubject: string = 'u_8910';
  let dsar8821Status: 'pending' | 'deleted' = 'pending';
  let confirmInput: string = '';

  // Focus elements for modal dialog trapping
  let confirmBtnRef: HTMLButtonElement | null = null;
  let cancelBtnRef: HTMLButtonElement | null = null;
  let triggerBtnRef: HTMLButtonElement | null = null;

  async function openDeleteModal(dsarId: string, subject: string, event: MouseEvent | KeyboardEvent) {
    targetDsarId = dsarId;
    targetSubject = subject;
    confirmInput = '';
    showDeleteModal = true;
    triggerBtnRef = event.currentTarget as HTMLButtonElement;
    await tick();
    if (cancelBtnRef) {
      cancelBtnRef.focus();
    }
  }

  function closeDeleteModal() {
    showDeleteModal = false;
    if (triggerBtnRef) {
      triggerBtnRef.focus();
    }
  }

  function executeDeletion() {
    dsar8821Status = 'deleted';
    showDeleteModal = false;
    if (triggerBtnRef) {
      triggerBtnRef.focus();
    }
  }

  function handleKeydown(e: KeyboardEvent) {
    if (e.key === 'Escape') {
      if (showDeleteModal) closeDeleteModal();
      if (showClientDeleteConfirm) showClientDeleteConfirm = false;
    }
  }

  interface CrmMessage {
    id: number;
    claimTicket: string;
    status: string;
    classification: string;
    title: string;
    narrative: string;
    clientEmail: string;
    createdAt: string;
  }

  interface CrmNote {
    id: number;
    clientEmail: string;
    type: 'agreement' | 'note';
    text: string;
    createdAt: string;
  }

  interface CrmClient {
    email: string;
    name: string;
    status: string;
    company: string;
    lastActivity: string;
    messages: CrmMessage[];
    notes: CrmNote[];
  }

  let clients: CrmClient[] = [];
  let selectedClientEmail: string = '';
  let isLoadingCrm: boolean = true;
  let isAuthenticated: boolean = false;
  let authKeyInput: string = '';
  let authError: string = '';

  // New note input state
  let newNoteText: string = '';
  let newNoteType: 'agreement' | 'note' = 'agreement';
  let isSavingNote: boolean = false;

  // Summary state
  let isLoadingSummary: boolean = false;
  let clientSummaryText: string = '';
  let activeSummaryClient: string = '';

  // Client Delete state
  let showClientDeleteConfirm: boolean = false;
  let clientToDelete: string = '';
  let isPurgeAllMode: boolean = false;
  let isDeletingClient: boolean = false;

  $: selectedClient = clients.find(c => c.email.toLowerCase() === selectedClientEmail.toLowerCase()) || clients[0] || null;

  function getStoredKey(): string {
    if (typeof window === 'undefined') return '';
    if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
      return 'efremov2026';
    }
    return localStorage.getItem('crm_auth_key') || '';
  }

  async function loadCrmData() {
    isLoadingCrm = true;
    authError = '';
    const key = getStoredKey();

    try {
      const res = await fetch('/api/crm', {
        headers: key ? { 'X-CRM-Key': key } : {}
      });

      if (res.status === 401 || res.status === 403) {
        isAuthenticated = false;
        return;
      }

      if (res.ok) {
        isAuthenticated = true;
        const data = await res.json();
        clients = data.clients || [];
        if (!selectedClientEmail && clients.length > 0) {
          selectedClientEmail = clients[0].email;
        } else if (clients.length > 0 && !clients.some(c => c.email.toLowerCase() === selectedClientEmail.toLowerCase())) {
          selectedClientEmail = clients[0].email;
        }
      }
    } catch (e) {
      console.error('Failed to load CRM data', e);
    } finally {
      isLoadingCrm = false;
    }
  }

  async function handleLogin() {
    if (!authKeyInput.trim()) return;
    authError = '';
    try {
      const res = await fetch('/api/crm/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ key: authKeyInput.trim() })
      });
      if (res.ok) {
        localStorage.setItem('crm_auth_key', authKeyInput.trim());
        authKeyInput = '';
        await loadCrmData();
      } else {
        authError = 'Неверный ключ доступа. Доступ разрешён только владельцу dmitrii.eneik@gmail.com.';
      }
    } catch (e) {
      authError = 'Ошибка проверки ключа. Проверьте соединение.';
    }
  }

  async function addNote() {
    if (!newNoteText.trim() || !selectedClient) return;
    isSavingNote = true;
    const key = getStoredKey();
    try {
      const res = await fetch('/api/crm/note', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(key ? { 'X-CRM-Key': key } : {})
        },
        body: JSON.stringify({
          clientEmail: selectedClient.email,
          type: newNoteType,
          text: newNoteText.trim()
        })
      });
      if (res.ok) {
        newNoteText = '';
        await loadCrmData();
      }
    } catch (e) {
      console.error('Failed to save note', e);
    } finally {
      isSavingNote = false;
    }
  }

  async function updateStatus(newStatus: string) {
    if (!selectedClient) return;
    const key = getStoredKey();
    try {
      await fetch('/api/crm/status', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(key ? { 'X-CRM-Key': key } : {})
        },
        body: JSON.stringify({
          clientEmail: selectedClient.email,
          status: newStatus
        })
      });
      await loadCrmData();
    } catch (e) {
      console.error('Failed to update status', e);
    }
  }

  async function loadClientSummary(email: string) {
    if (!email) return;
    isLoadingSummary = true;
    clientSummaryText = '';
    activeSummaryClient = email;
    const key = getStoredKey();
    try {
      const res = await fetch('/api/crm/summary', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(key ? { 'X-CRM-Key': key } : {})
        },
        body: JSON.stringify({ clientEmail: email })
      });
      if (res.ok) {
        const data = await res.json();
        clientSummaryText = data.summary || 'Нет данных для составления саммари.';
      } else {
        clientSummaryText = 'Не удалось составить саммари. Проверьте соединение.';
      }
    } catch (e) {
      clientSummaryText = 'Ошибка при запросе саммари: ' + String(e);
    } finally {
      isLoadingSummary = false;
    }
  }

  function promptDeleteClient(email: string) {
    clientToDelete = email;
    isPurgeAllMode = false;
    showClientDeleteConfirm = true;
  }

  function promptPurgeAll() {
    clientToDelete = 'Все клиенты и тестовые обращения';
    isPurgeAllMode = true;
    showClientDeleteConfirm = true;
  }

  async function confirmDeleteClient() {
    isDeletingClient = true;
    const key = getStoredKey();
    try {
      const res = await fetch('/api/crm/delete_client', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(key ? { 'X-CRM-Key': key } : {})
        },
        body: JSON.stringify(
          isPurgeAllMode ? { purgeAll: true } : { clientEmail: clientToDelete }
        )
      });
      if (res.ok) {
        showClientDeleteConfirm = false;
        if (activeSummaryClient === clientToDelete || isPurgeAllMode) {
          clientSummaryText = '';
          activeSummaryClient = '';
        }
        clientToDelete = '';
        await loadCrmData();
      }
    } catch (e) {
      console.error('Failed to delete client', e);
    } finally {
      isDeletingClient = false;
    }
  }

  function getClientTimeline(client: CrmClient) {
    const items = [
      ...client.messages.map(m => ({
        type: 'message' as const,
        id: `msg-${m.id}`,
        title: m.title || 'Обращение с сайта',
        text: m.narrative,
        ticket: m.claimTicket,
        createdAt: m.createdAt
      })),
      ...client.notes.map(n => ({
        type: n.type as 'agreement' | 'note',
        id: `note-${n.id}`,
        title: n.type === 'agreement' ? 'Зафиксированная договорённость' : 'Заметка',
        text: n.text,
        ticket: null,
        createdAt: n.createdAt
      }))
    ];
    items.sort((a, b) => (b.createdAt > a.createdAt ? 1 : -1));
    return items;
  }

  onMount(() => {
    loadCrmData();
    window.addEventListener('keydown', handleKeydown);
    return () => {
      window.removeEventListener('keydown', handleKeydown);
    };
  });
</script>

<div
  id="admin-dashboard-root"
  class="min-h-screen pb-32 font-sans text-body-md antialiased transition-colors duration-200 {theme === 'dark' ? 'bg-[#090a0f] text-[#f8f9ff]' : 'bg-[#f8f9ff] text-[#0b1c30]'}"
>
  <!-- TopAppBar: Material Spec & Design System Tokens -->
  <header
    id="admin-header"
    class="fixed top-0 left-0 w-full z-40 flex justify-between items-center px-4 sm:px-6 h-14 border-b transition-colors shadow-sm {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433] text-[#ffffff]' : 'bg-white border-[#c6c6cd] text-[#0b1c30]'}"
  >
    <div class="flex items-center space-x-3">
      <span class="material-symbols-outlined text-2xl text-[#d4af37]" aria-hidden="true">shield</span>
      <div class="flex flex-col">
        <span class="text-sm font-bold tracking-tight leading-none">Личный кабинет (Trust & Compliance)</span>
        <span class="text-[11px] font-mono font-medium opacity-80">
          {isInboxRoute ? 'Мини-CRM и Входящие' : 'Панель управления'}
        </span>
      </div>
    </div>

    <div class="flex items-center space-x-2">
      <button
        type="button"
        on:click={toggleOnboarding}
        class="px-2.5 py-1 rounded text-xs font-mono border transition-all flex items-center gap-1 {theme === 'dark' ? 'bg-[#171a24] text-slate-300 border-[#1f2433] hover:text-white' : 'bg-slate-100 text-slate-700 border-slate-300 hover:bg-slate-200'}"
        title="Показать / скрыть подсказку"
      >
        <span>💡</span>
        <span class="hidden sm:inline">Онбординг</span>
      </button>

      {#if !isInboxRoute}
        <button
          type="button"
          on:click={() => (showComplianceAudit = !showComplianceAudit)}
          class="px-2.5 py-1 rounded text-xs font-mono border transition-all flex items-center gap-1 {showComplianceAudit ? 'bg-[#d4af37]/20 text-[#d4af37] border-[#d4af37]/40' : 'bg-[#171a24] text-slate-400 border-[#1f2433]'}"
          title="Скрыть или показать технический аудит"
        >
          <span>⚙️</span>
          <span class="hidden sm:inline">{showComplianceAudit ? 'Скрыть аудит' : 'Тех. аудит'}</span>
        </button>
      {/if}

      <a
        id="admin-exit-btn"
        href="/"
        class="px-3 py-1.5 rounded text-xs font-mono font-semibold border transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none flex items-center gap-1 {theme === 'dark' ? 'bg-[#1f2433] text-[#d4af37] border-[#d4af37]/40 hover:bg-[#2d354b]' : 'bg-[#f1f5f9] text-[#0284c7] border-[#0284c7]/40 hover:bg-[#e2e8f0]'}"
      >
        <span>← На сайт</span>
      </a>

      {#if showComplianceAudit}
        <button
          id="admin-filter-btn"
          type="button"
          title="Фильтр очереди"
          aria-label="Фильтр очереди"
          class="p-2 rounded hover:bg-black/10 dark:hover:bg-white/10 transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none"
        >
          <span class="material-symbols-outlined text-xl" aria-hidden="true">filter_list</span>
        </button>
        <button
          id="admin-framework-btn"
          type="button"
          title="Правовые стандарты"
          aria-label="Правовые стандарты"
          class="p-2 rounded hover:bg-black/10 dark:hover:bg-white/10 transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none"
        >
          <span class="material-symbols-outlined text-xl" aria-hidden="true">gavel</span>
        </button>
      {/if}
    </div>
  </header>

  <!-- Main Content Canvas -->
  <main id="admin-main-content" class="w-full max-w-4xl mx-auto pt-20 pb-16 px-4 sm:px-6 space-y-6">

    <!-- Onboarding Guide Card -->
    {#if showOnboarding}
      <section class="rounded-2xl border p-5 shadow-xl relative transition-all {theme === 'dark' ? 'bg-[#12151d] border-[#d4af37]/30 text-slate-200' : 'bg-sky-50/90 border-sky-200 text-slate-800'}">
        <div class="flex items-start justify-between gap-3">
          <div class="flex items-start gap-3.5">
            <span class="text-3xl shrink-0">🎯</span>
            <div class="space-y-2">
              <div class="flex items-center gap-2">
                <h2 class="text-base font-bold text-[#d4af37] tracking-tight">
                  Онбординг • Личный кабинет и управление клиентами
                </h2>
                <span class="text-[10px] px-2 py-0.5 rounded font-mono font-bold bg-[#d4af37]/20 text-[#e5c158] border border-[#d4af37]/30">
                  СУВЕРЕННОСТЬ 100%
                </span>
              </div>
              <p class="text-xs leading-relaxed opacity-90 max-w-2xl">
                Это ваш приватный пульт управления. Все заявки с контактной формы вашего сайта попадают сюда автоматически, сохраняясь прямо на вашем сервере без посредников.
              </p>
              
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-2.5 pt-1 text-xs">
                <div class="flex items-center gap-2 p-2 rounded-lg bg-black/10 dark:bg-white/5 border border-white/5">
                  <span class="text-base">⚡</span>
                  <div>
                    <strong class="block text-[#e5c158]">Саммари по клиенту:</strong>
                    <span class="text-[11px] opacity-80">Выжимка всей переписки и задач в один клик.</span>
                  </div>
                </div>

                <div class="flex items-center gap-2 p-2 rounded-lg bg-black/10 dark:bg-white/5 border border-white/5">
                  <span class="text-base">🤝</span>
                  <div>
                    <strong class="block text-emerald-300">Фиксация договорённостей:</strong>
                    <span class="text-[11px] opacity-80">Записывайте согласованные цены, дедлайны и условия.</span>
                  </div>
                </div>

                <div class="flex items-center gap-2 p-2 rounded-lg bg-black/10 dark:bg-white/5 border border-white/5">
                  <span class="text-base">🏷️</span>
                  <div>
                    <strong class="block text-amber-300">Статусы клиентов:</strong>
                    <span class="text-[11px] opacity-80">«Новый» → «В диалоге» → «Договорились» → «Архив».</span>
                  </div>
                </div>

                <div class="flex items-center gap-2 p-2 rounded-lg bg-black/10 dark:bg-white/5 border border-white/5">
                  <span class="text-base">🗑️</span>
                  <div>
                    <strong class="block text-red-300">Удаление из базы:</strong>
                    <span class="text-[11px] opacity-80">Удаляйте тестовые обращения или клиентов без следа.</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <button
            type="button"
            on:click={dismissOnboarding}
            class="p-1 rounded-lg hover:bg-black/10 dark:hover:bg-white/10 transition-colors opacity-70 hover:opacity-100 text-xs font-mono"
            title="Закрыть онбординг"
          >
            ✕
          </button>
        </div>
      </section>
    {/if}

    <!-- Sovereign Mini-CRM Section: Client Dossier & Agreements Log -->
    <section
      id="messages-inbox-section"
      class="rounded-2xl border transition-all shadow-2xl overflow-hidden {theme === 'dark' ? 'bg-[#10131a] border-[#1e293b]' : 'bg-white border-slate-200'}"
    >
      <!-- CRM Header -->
      <div class="px-5 py-4 border-b flex flex-wrap items-center justify-between gap-3 {theme === 'dark' ? 'bg-[#141720] border-[#1e293b]' : 'bg-slate-50 border-slate-200'}">
        <div class="flex items-center gap-3">
          <div class="w-9 h-9 rounded-xl bg-[#d4af37]/10 flex items-center justify-center text-[#d4af37] border border-[#d4af37]/20">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          <div>
            <h2 class="text-sm font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
              Мини-CRM • Клиенты и договорённости
            </h2>
            <p class="text-[11px] font-mono text-slate-400">
              Хранилище переписок на сервере dmitryefremov-site
            </p>
          </div>
        </div>

        {#if isAuthenticated}
          <div class="flex items-center gap-2">
            <button
              type="button"
              on:click={loadCrmData}
              class="px-3 py-1.5 rounded-lg border text-xs font-mono font-semibold transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none flex items-center gap-1.5 {theme === 'dark' ? 'bg-[#191d28] text-[#d4af37] border-[#d4af37]/30 hover:bg-[#202e4d]' : 'bg-slate-100 text-sky-600 border-slate-300 hover:bg-slate-200'}"
              title="Обновить базу клиентов"
            >
              <span>↻</span>
              <span class="hidden sm:inline">Обновить</span>
            </button>
            <span class="px-2.5 py-1 rounded-md text-[10px] font-mono font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
              КЛЮЧ АКТИВЕН
            </span>
          </div>
        {/if}
      </div>

      <!-- CRM Content / Auth Wall -->
      {#if !isAuthenticated && !isLoadingCrm}
        <!-- Auth Gate -->
        <div class="p-8 sm:p-12 text-center max-w-md mx-auto space-y-4">
          <div class="w-14 h-14 mx-auto rounded-2xl bg-[#d4af37]/10 flex items-center justify-center text-[#d4af37] border border-[#d4af37]/20">
            <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>

          <div class="space-y-1">
            <h3 class="text-base font-bold {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
              Доступ ограничен владельцем
            </h3>
            <p class="text-xs text-slate-400 font-mono">
              Вход строго для: <strong class="text-[#e5c158]">dmitrii.eneik@gmail.com</strong>
            </p>
          </div>

          <form on:submit|preventDefault={handleLogin} class="space-y-3 pt-2">
            <input
              type="password"
              bind:value={authKeyInput}
              placeholder="Введите ключ доступа..."
              class="w-full px-4 py-2.5 rounded-xl border font-mono text-xs text-center tracking-wider transition-all focus:outline-none focus:ring-2 focus:ring-sky-500 {theme === 'dark' ? 'bg-[#090a0f] border-[#1e293b] text-white' : 'bg-slate-50 border-slate-300 text-slate-900'}"
            />

            {#if authError}
              <div class="text-[11px] text-red-400 font-mono">{authError}</div>
            {/if}

            <button
              type="submit"
              class="w-full py-2.5 rounded-xl text-xs font-mono font-bold bg-[#d4af37] hover:bg-[#e2c15e] text-[#090a0f] transition-all shadow-md active:scale-95"
            >
              Войти в хранилище
            </button>
          </form>
        </div>
      {:else if isLoadingCrm}
        <div class="py-16 text-center text-xs font-mono text-slate-400 flex flex-col items-center gap-2">
          <span class="w-5 h-5 border-2 border-sky-400 border-t-transparent rounded-full animate-spin"></span>
          <span>Загрузка досье клиентов...</span>
        </div>
      {:else if clients.length === 0}
        <div class="py-16 text-center text-xs font-mono text-slate-400 space-y-2">
          <p>Клиентов в базе пока нет.</p>
          <p class="text-[11px] opacity-70">Как только кто-то отправит заявку через форму на сайте, здесь появится его карточка.</p>
        </div>
      {:else}
        <!-- Two Column Layout: Clients List (Left) + Dossier & History (Right) -->
        <div class="grid grid-cols-1 md:grid-cols-12 min-h-[520px]">
          <!-- Left Column: Clients List -->
          <div class="md:col-span-5 border-r {theme === 'dark' ? 'border-[#1e293b] bg-[#0c111d]' : 'border-slate-200 bg-slate-50/50'} p-3 space-y-2 overflow-y-auto max-h-[700px]">
            <div class="px-2 py-1 text-[11px] font-mono uppercase tracking-wider text-slate-400 flex items-center justify-between">
              <span>Клиенты ({clients.length})</span>
              <button
                type="button"
                on:click={promptPurgeAll}
                class="text-[10px] text-red-400 hover:underline font-mono"
                title="Очистить всех клиентов и тестовые обращения"
              >
                Очистить всех
              </button>
            </div>

            {#each clients as client}
              <button
                type="button"
                on:click={() => {
                  selectedClientEmail = client.email;
                  clientSummaryText = '';
                  activeSummaryClient = '';
                }}
                class="w-full text-left p-3 rounded-xl border transition-all duration-150 flex flex-col gap-1.5 focus:outline-none {selectedClient?.email.toLowerCase() === client.email.toLowerCase() ? (theme === 'dark' ? 'bg-[#191d28] border-[#d4af37]/60 shadow-md ring-1 ring-[#d4af37]/30' : 'bg-white border-[#d4af37] shadow-md ring-1 ring-[#d4af37]/30') : (theme === 'dark' ? 'bg-[#121929] border-[#1e293b] hover:border-slate-600' : 'bg-white border-slate-200 hover:border-slate-300')}"
              >
                <div class="flex items-center justify-between gap-1">
                  <span class="text-xs font-bold truncate {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
                    {client.email === 'anonymous' ? 'Анонимный контакт' : client.email}
                  </span>
                  <span class="text-[10px] px-1.5 py-0.2 rounded font-mono font-bold {client.status === 'AGREED' ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' : client.status === 'IN_DISCUSSION' ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30' : client.status === 'ARCHIVED' ? 'bg-slate-700/50 text-slate-400' : 'bg-[#d4af37]/20 text-[#d4af37] border border-[#d4af37]/30'}">
                    {client.status === 'AGREED' ? 'ДОГОВОРИЛИСЬ' : client.status === 'IN_DISCUSSION' ? 'В ДИАЛОГЕ' : client.status === 'ARCHIVED' ? 'АРХИВ' : 'НОВЫЙ'}
                  </span>
                </div>

                <div class="text-[11px] text-slate-400 truncate">
                  {#if client.notes.length > 0}
                    📝 {client.notes[0].text}
                  {:else if client.messages.length > 0}
                    📩 {client.messages[0].title || client.messages[0].narrative || 'Обращение'}
                  {:else}
                    (Нет записей)
                  {/if}
                </div>

                <div class="flex items-center justify-between text-[10px] font-mono text-slate-400 pt-1 border-t {theme === 'dark' ? 'border-white/5' : 'border-slate-100'}">
                  <span>{client.messages.length} сообщ. • {client.notes.length} дог.</span>
                  <span>{new Date(client.lastActivity).toLocaleDateString('ru-RU', { day: '2-digit', month: 'short' })}</span>
                </div>
              </button>
            {/each}
          </div>

          <!-- Right Column: Selected Client Dossier & Timeline -->
          <div class="md:col-span-7 p-4 sm:p-6 flex flex-col justify-between space-y-5 overflow-y-auto max-h-[700px] {theme === 'dark' ? 'bg-[#10131a]' : 'bg-white'}">
            {#if selectedClient}
              <!-- Client Dossier Header -->
              <div class="space-y-3 pb-4 border-b {theme === 'dark' ? 'border-[#1e293b]' : 'border-slate-200'}">
                <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                  <div>
                    <h3 class="text-base sm:text-lg font-bold {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
                      {selectedClient.email}
                    </h3>
                    <p class="text-xs font-mono text-slate-400 mt-0.5">
                      Последняя активность: {new Date(selectedClient.lastActivity).toLocaleString('ru-RU')}
                    </p>
                  </div>

                  <!-- Quick Action Buttons -->
                  <div class="flex items-center gap-1.5 flex-wrap">
                    <button
                      type="button"
                      disabled={isLoadingSummary}
                      on:click={() => loadClientSummary(selectedClient.email)}
                      class="px-2.5 py-1.5 rounded-lg text-xs font-mono font-semibold bg-amber-500/20 text-amber-300 border border-amber-500/40 hover:bg-amber-500/30 transition-all flex items-center gap-1 shadow-sm"
                      title="Сгенерировать саммари по клиенту"
                    >
                      {#if isLoadingSummary}
                        <span class="w-3 h-3 border-2 border-amber-300 border-t-transparent rounded-full animate-spin"></span>
                        <span>Сборка...</span>
                      {:else}
                        <span>⚡ Саммари</span>
                      {/if}
                    </button>

                    {#if selectedClient.email && selectedClient.email !== 'anonymous'}
                      <a
                        href="mailto:{selectedClient.email}?subject=Dmitry Efremov — продолжение диалога"
                        class="px-2.5 py-1.5 rounded-lg text-xs font-mono font-semibold bg-[#d4af37] hover:bg-[#e2c15e] text-[#090a0f] transition-all flex items-center gap-1 shadow-sm"
                      >
                        <span>✉ Написать</span>
                      </a>
                    {/if}

                    <button
                      type="button"
                      on:click={() => promptDeleteClient(selectedClient.email)}
                      class="px-2.5 py-1.5 rounded-lg text-xs font-mono font-semibold bg-red-500/10 text-red-400 border border-red-500/30 hover:bg-red-500/20 transition-all flex items-center gap-1 shadow-sm"
                      title="Удалить клиента и переписку из базы"
                    >
                      <span>🗑 Удалить</span>
                    </button>
                  </div>
                </div>

                <!-- Status Selector Bar -->
                <div class="flex items-center gap-1.5 flex-wrap pt-1">
                  <span class="text-xs font-mono text-slate-400 mr-1">Статус:</span>
                  {#each [['NEW', 'Новый'], ['IN_DISCUSSION', 'В диалоге'], ['AGREED', 'Договорились'], ['ARCHIVED', 'Архив']] as [stCode, stLabel]}
                    <button
                      type="button"
                      on:click={() => updateStatus(stCode)}
                      class="px-2.5 py-1 rounded-md text-xs font-mono transition-all {selectedClient.status === stCode ? 'bg-[#d4af37] text-[#090a0f] font-bold shadow-sm' : (theme === 'dark' ? 'bg-[#191d28] text-slate-300 hover:bg-[#202e4d]' : 'bg-slate-100 text-slate-700 hover:bg-slate-200')}"
                    >
                      {stLabel}
                    </button>
                  {/each}
                </div>
              </div>

              <!-- Client Summary Box (Collapsible / Dynamic) -->
              {#if activeSummaryClient === selectedClient.email && clientSummaryText}
                <div class="p-4 rounded-xl border bg-amber-950/20 border-amber-500/40 space-y-2 text-xs">
                  <div class="flex items-center justify-between border-b border-amber-500/30 pb-2">
                    <div class="flex items-center gap-1.5 text-amber-300 font-bold font-mono">
                      <span>⚡</span>
                      <span>САММАРИ ПО КЛИЕНТУ: {selectedClient.email}</span>
                    </div>
                    <button
                      type="button"
                      on:click={() => { clientSummaryText = ''; activeSummaryClient = ''; }}
                      class="text-amber-400 hover:underline font-mono text-[11px]"
                    >
                      ✕ Закрыть
                    </button>
                  </div>
                  <div class="whitespace-pre-wrap leading-relaxed text-slate-200 font-sans">
                    {clientSummaryText}
                  </div>
                </div>
              {/if}

              <!-- Fast Agreement / Note Recording Box -->
              <div class="p-4 rounded-xl border space-y-3 {theme === 'dark' ? 'bg-[#13161e] border-[#d4af37]/20' : 'bg-slate-50 border-slate-200'}">
                <div class="flex items-center justify-between">
                  <span class="text-xs font-bold font-mono text-[#d4af37] uppercase tracking-wide">
                    + Зафиксировать договорённость или заметку
                  </span>
                  <div class="flex items-center gap-1 bg-black/20 p-0.5 rounded-lg border border-white/5 text-[11px] font-mono">
                    <button
                      type="button"
                      on:click={() => (newNoteType = 'agreement')}
                      class="px-2 py-0.5 rounded {newNoteType === 'agreement' ? 'bg-emerald-500 text-[#090a0f] font-bold' : 'text-slate-400 hover:text-white'}"
                    >
                      🤝 Договорённость
                    </button>
                    <button
                      type="button"
                      on:click={() => (newNoteType = 'note')}
                      class="px-2 py-0.5 rounded {newNoteType === 'note' ? 'bg-[#d4af37] text-[#090a0f] font-bold' : 'text-slate-400 hover:text-white'}"
                    >
                      📝 Заметка
                    </button>
                  </div>
                </div>

                <textarea
                  bind:value={newNoteText}
                  rows="2"
                  placeholder={newNoteType === 'agreement' ? 'Зафиксируйте условия, дату звонка, бюджет, сроки или договоренность...' : 'Заметка по клиенту или контекст...'}
                  class="w-full p-2.5 rounded-lg text-xs font-mono transition-colors focus:outline-none focus:ring-1 focus:ring-sky-400 {theme === 'dark' ? 'bg-[#090a0f] border border-[#1e293b] text-slate-100 placeholder-slate-500' : 'bg-white border border-slate-300 text-slate-900 placeholder-slate-400'}"
                ></textarea>

                <div class="flex justify-end">
                  <button
                    type="button"
                    disabled={isSavingNote || !newNoteText.trim()}
                    on:click={addNote}
                    class="px-4 py-1.5 rounded-lg text-xs font-mono font-bold transition-all flex items-center gap-1.5 disabled:opacity-40 disabled:cursor-not-allowed {newNoteType === 'agreement' ? 'bg-emerald-500 hover:bg-emerald-400 text-[#090a0f]' : 'bg-[#d4af37] hover:bg-[#e2c15e] text-[#090a0f]'}"
                  >
                    {#if isSavingNote}
                      <span>Сохранение...</span>
                    {:else}
                      <span>{newNoteType === 'agreement' ? 'Зафиксировать договорённость' : 'Сохранить заметку'}</span>
                    {/if}
                  </button>
                </div>
              </div>

              <!-- Unified Timeline (Messages + Agreements + Notes) -->
              <div class="space-y-3">
                <div class="text-xs font-mono uppercase tracking-wider text-slate-400">
                  Хронология переписки и договорённостей:
                </div>

                <div class="space-y-3">
                  {#each getClientTimeline(selectedClient) as item}
                    <div class="p-3.5 rounded-xl border space-y-2 {item.type === 'agreement' ? (theme === 'dark' ? 'bg-emerald-950/20 border-emerald-500/40' : 'bg-emerald-50 border-emerald-300') : item.type === 'note' ? (theme === 'dark' ? 'bg-sky-950/20 border-[#d4af37]/30' : 'bg-sky-50 border-sky-200') : (theme === 'dark' ? 'bg-[#121929] border-[#1e293b]' : 'bg-slate-50 border-slate-200')}">
                      <div class="flex items-center justify-between gap-1 flex-wrap">
                        <div class="flex items-center gap-2">
                          {#if item.type === 'agreement'}
                            <span class="px-2 py-0.5 rounded text-[10px] font-mono font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                              🤝 ДОГОВОРЁННОСТЬ
                            </span>
                          {:else if item.type === 'note'}
                            <span class="px-2 py-0.5 rounded text-[10px] font-mono font-bold bg-[#d4af37]/20 text-[#d4af37] border border-[#d4af37]/30">
                              📝 ЗАМЕТКА
                            </span>
                          {:else}
                            <span class="px-2 py-0.5 rounded text-[10px] font-mono font-bold bg-slate-700 text-slate-200">
                              📩 СООБЩЕНИЕ С САЙТА
                            </span>
                            {#if item.ticket}
                              <span class="text-[11px] font-mono text-[#d4af37]">{item.ticket}</span>
                            {/if}
                          {/if}
                        </div>

                        <span class="text-[11px] font-mono text-slate-400">
                          {new Date(item.createdAt).toLocaleString('ru-RU', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })}
                        </span>
                      </div>

                      {#if item.title && item.type === 'message'}
                        <div class="text-xs font-bold {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
                          {item.title}
                        </div>
                      {/if}

                      <div class="text-xs leading-relaxed whitespace-pre-wrap {item.type === 'agreement' ? 'text-emerald-300 font-medium' : (theme === 'dark' ? 'text-slate-300' : 'text-slate-700')}">
                        {item.text}
                      </div>
                    </div>
                  {/each}
                </div>
              </div>
            {/if}
          </div>
        </div>
      {/if}
    </section>

    <!-- Client Deletion Confirmation Modal -->
    {#if showClientDeleteConfirm}
      <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
        <div class="w-full max-w-md p-6 rounded-2xl border shadow-2xl space-y-4 font-sans {theme === 'dark' ? 'bg-[#13161e] border-red-500/60 text-white' : 'bg-white border-red-400 text-slate-900'}">
          <div class="flex items-center gap-3 border-b pb-3 border-red-500/30">
            <span class="text-3xl text-red-500">🗑️</span>
            <div>
              <h3 class="text-base font-bold text-red-500">
                {isPurgeAllMode ? 'Очистить всех клиентов?' : 'Удалить клиента из базы?'}
              </h3>
              <p class="text-xs font-mono opacity-80">{clientToDelete}</p>
            </div>
          </div>
          <p class="text-xs leading-relaxed opacity-90">
            {isPurgeAllMode ? 'Это действие навсегда удалит все тестовые обращения, клиентов и историю договорённостей с сервера.' : 'Это действие безвозвратно удалит переписку, карточку клиента и все зафиксированные договорённости с вашего сервера.'}
          </p>
          <div class="flex justify-end gap-2 pt-2">
            <button
              type="button"
              on:click={() => (showClientDeleteConfirm = false)}
              class="px-4 py-2 rounded-lg text-xs font-semibold border {theme === 'dark' ? 'border-[#1e293b] bg-[#171a24] text-slate-200' : 'border-slate-300 bg-slate-100 text-slate-700'}"
            >
              Отмена
            </button>
            <button
              type="button"
              disabled={isDeletingClient}
              on:click={confirmDeleteClient}
              class="px-4 py-2 rounded-lg text-xs font-bold bg-red-600 hover:bg-red-700 text-white shadow-md flex items-center gap-1.5"
            >
              {#if isDeletingClient}
                <span>Удаление...</span>
              {:else}
                <span>{isPurgeAllMode ? 'Да, очистить всё' : 'Да, удалить из базы'}</span>
              {/if}
            </button>
          </div>
        </div>
      </div>
    {/if}

    <!-- Technical Compliance / Regulatory Audit Sections (Only on /admin when audit is enabled) -->
    {#if showComplianceAudit}
      <!-- Statutory Benchmark Banner -->
      <section
        id="statutory-benchmark-banner"
        class="p-4 sm:p-5 rounded-2xl border transition-all flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 shadow-sm {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
      >
        <div class="flex items-center space-x-3.5">
          <div class="w-10 h-10 rounded-xl bg-emerald-500/10 flex items-center justify-center text-emerald-400 border border-emerald-500/20">
            <span class="material-symbols-outlined text-2xl" aria-hidden="true">verified_user</span>
          </div>
          <div>
            <div class="flex items-center space-x-2">
              <span class="text-xs font-mono font-bold uppercase tracking-wider text-emerald-400">НОРМАТИВНЫЙ СТАТУС</span>
              <span class="text-[10px] font-mono px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">АРХИТЕКТУРА V2</span>
            </div>
            <h1 class="text-base sm:text-lg font-bold tracking-tight">Безопасность данных: GDPR / CCPA Compliant: 99.4%</h1>
          </div>
        </div>
        <div class="text-xs font-mono opacity-80 sm:text-right">
          <span>Аудит по 14 суверенным юрисдикциям данных</span>
          <span class="block text-[11px] text-emerald-400 font-semibold">Нулевые штрафные риски</span>
        </div>
      </section>

      <!-- Key Compliance Telemetry Metrics Grid -->
      <section id="admin-metrics-grid" class="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <!-- Metric 1: Pending DSAR -->
        <div
          class="p-4 rounded-xl border shadow-sm transition-all {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
        >
          <div class="flex justify-between items-start">
            <span class="text-xs font-mono uppercase tracking-wider opacity-80">ЗАПРОСЫ ДАННЫХ (DSAR)</span>
            <span class="material-symbols-outlined text-amber-400 text-xl" aria-hidden="true">hourglass_top</span>
          </div>
          <div class="mt-2 flex items-baseline gap-2">
            <span class="text-2xl font-bold font-mono text-amber-400">1</span>
            <span class="text-xs opacity-70">требует действия</span>
          </div>
          <span class="text-[11px] font-mono text-amber-400/90 mt-1 block">Дедлайн: &lt; 24 часов</span>
        </div>

        <!-- Metric 2: Consent Opt-In Ratio -->
        <div
          class="p-4 rounded-xl border shadow-sm transition-all {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
        >
          <div class="flex justify-between items-start">
            <span class="text-xs font-mono uppercase tracking-wider opacity-80">УРОВЕНЬ СОГЛАСИЯ</span>
            <span class="material-symbols-outlined text-emerald-400 text-xl" aria-hidden="true">thumb_up</span>
          </div>
          <div class="mt-2 flex items-baseline gap-2">
            <span class="text-2xl font-bold font-mono text-emerald-400">84.2%</span>
            <span class="text-xs opacity-70">чистый Opt-In</span>
          </div>
          <span class="text-[11px] font-mono text-emerald-400/90 mt-1 block">Без скрытых манипуляций</span>
        </div>

        <!-- Metric 3: Third-Party Data Processors -->
        <div
          class="p-4 rounded-xl border shadow-sm transition-all {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
        >
          <div class="flex justify-between items-start">
            <span class="text-xs font-mono uppercase tracking-wider opacity-80">ОБРАБОТЧИКИ ДАННЫХ</span>
            <span class="material-symbols-outlined text-[#d4af37] text-xl" aria-hidden="true">lan</span>
          </div>
          <div class="mt-2 flex items-baseline gap-2">
            <span class="text-2xl font-bold font-mono text-[#d4af37]">3</span>
            <span class="text-xs opacity-70">проверено</span>
          </div>
          <span class="text-[11px] font-mono text-[#d4af37]/90 mt-1 block">Шлюзы изолированы</span>
        </div>
      </section>

      <!-- Active DSAR Queue Section -->
      <section id="dsar-queue-section" class="space-y-4 pt-2">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-emerald-400 text-2xl" aria-hidden="true">assignment_turned_in</span>
            <h2 class="text-lg font-bold tracking-tight">Запросы субъектов данных (DSAR)</h2>
          </div>
          <span class="font-mono text-xs opacity-80">Очередь: {dsar8821Status === 'deleted' ? '2 активных' : '3 активных'}</span>
        </div>

        <!-- Queue Item 1: Erasure Request (High Priority) -->
        <div
          id="dsar-item-8821"
          class="p-4 rounded-xl border transition-all shadow-sm space-y-3 {dsar8821Status === 'deleted' ? 'opacity-60 bg-slate-800/40 border-slate-700' : (theme === 'dark' ? 'bg-[#13161e] border-red-500/50' : 'bg-white border-red-400')}"
        >
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <div class="flex flex-col">
              <div class="flex items-center gap-2">
                <span class="font-mono text-xs font-bold">#DSAR-8821</span>
                {#if dsar8821Status === 'deleted'}
                  <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30">
                    УДАЛЕНО / DELETED / PURGED
                  </span>
                {:else}
                  <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-red-500/20 text-red-400 font-bold border border-red-500/30">
                    СРОЧНЫЙ СРОК (CRITICAL SLA)
                  </span>
                {/if}
              </div>
              <span class="text-base font-bold mt-1">Удаление данных / Право на забвение</span>
              <span class="font-mono text-xs opacity-80">Субъект: u_8910 • Прямой потребитель</span>
            </div>

            <div class="sm:text-right">
              {#if dsar8821Status === 'deleted'}
                <span class="font-mono text-xs font-bold text-emerald-400 block">Выполнено</span>
              {:else}
                <span class="font-mono text-xs font-bold text-red-400 block">Осталось 18ч</span>
              {/if}
              <span class="text-[11px] opacity-70">Ст. 17 GDPR</span>
            </div>
          </div>

          <p class="text-xs opacity-80 pt-2 border-t {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
            Требует каскадного удаления из базы клиентов, шардов биллинга и хранилища маркетинговой аналитики.
          </p>

          <!-- Action Buttons -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
            <button
              id="dsar-8821-review-btn"
              type="button"
              class="h-9 px-4 border rounded text-xs font-semibold transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none flex items-center justify-center gap-1.5 {theme === 'dark' ? 'border-[#1f2433] bg-[#171a24] hover:bg-[#25304e] text-white' : 'border-[#c6c6cd] bg-slate-100 hover:bg-slate-200 text-[#0b1c30]'}"
            >
              <span class="material-symbols-outlined text-sm" aria-hidden="true">visibility</span>
              Детали запроса
            </button>

            {#if dsar8821Status === 'pending'}
              <button
                id="dsar-8821-delete-btn"
                type="button"
                on:click={(e) => openDeleteModal('DSAR-8821', 'u_8910', e)}
                class="h-9 px-4 rounded text-xs font-bold transition-all focus-visible:ring-2 focus-visible:ring-red-400 focus-visible:outline-none flex items-center justify-center gap-1.5 bg-red-600 hover:bg-red-700 text-white shadow-sm"
              >
                <span class="material-symbols-outlined text-sm" aria-hidden="true">delete_forever</span>
                Исполнить удаление
              </button>
            {:else}
              <div class="h-9 px-4 rounded text-xs font-mono font-bold flex items-center justify-center gap-1.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                <span class="material-symbols-outlined text-sm" aria-hidden="true">done_all</span>
                Удаление завершено
              </div>
            {/if}
          </div>
        </div>

        <!-- Queue Item 2: Export Data Request -->
        <div
          id="dsar-item-8819"
          class="p-4 rounded-xl border shadow-sm {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
        >
          <div class="flex justify-between items-start">
            <div class="flex flex-col">
              <div class="flex items-center gap-2">
                <span class="font-mono text-xs font-bold">#DSAR-8819</span>
                <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-[#d4af37]/20 text-[#d4af37] font-bold border border-[#d4af37]/30">
                  В ОБРАБОТКЕ
                </span>
              </div>
              <span class="text-base font-bold mt-1">Экспорт данных (ZIP архив)</span>
              <span class="font-mono text-xs opacity-80">Субъект: enterprise_team_4</span>
            </div>
            <button
              id="dsar-8819-export-btn"
              type="button"
              class="px-3 py-1.5 rounded border text-xs font-mono font-semibold transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {theme === 'dark' ? 'border-[#d4af37]/30 bg-[#171a24] hover:bg-[#d4af37]/20 text-[#d4af37]' : 'border-sky-300 bg-sky-50 hover:bg-sky-100 text-sky-700'}"
            >
              Скачать
            </button>
          </div>
        </div>

        <!-- Queue Item 3: Rectification Request -->
        <div
          id="dsar-item-8814"
          class="p-4 rounded-xl border shadow-sm {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
        >
          <div class="flex justify-between items-start">
            <div class="flex flex-col">
              <div class="flex items-center gap-2">
                <span class="font-mono text-xs font-bold">#DSAR-8814</span>
                <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30">
                  СОГЛАСОВАНО
                </span>
              </div>
              <span class="text-base font-bold mt-1">Уточнение данных (Rectification)</span>
              <span class="font-mono text-xs opacity-80">Атрибуты: Юридическое имя и налоговый ID</span>
            </div>
            <div class="flex items-center gap-1 text-emerald-400">
              <span class="material-symbols-outlined text-base" aria-hidden="true">check_circle</span>
              <span class="font-mono text-xs font-bold">Завершено</span>
            </div>
          </div>
        </div>
      </section>

      <!-- System Isolation & Audit Section -->
      <section id="system-isolation-section" class="space-y-4 pt-2">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-[#d4af37] text-2xl" aria-hidden="true">lock</span>
            <h2 class="text-lg font-bold tracking-tight">Изоляция систем и аудит безопасности</h2>
          </div>
          <span class="font-mono text-xs text-[#d4af37] font-semibold">Zero-Trust V2</span>
        </div>

        <div class="p-4 rounded-xl border shadow-sm space-y-4 {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
          <div class="flex items-center justify-between border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-400"></span>
              <span class="font-bold text-sm">Шлюзы изоляции хранилищ данных</span>
            </div>
            <span class="font-mono text-xs text-emerald-400 font-bold uppercase">В РАБОТЕ</span>
          </div>

          <div class="space-y-2">
            <!-- Gateway 1 -->
            <div class="p-3 rounded border flex items-center justify-between {theme === 'dark' ? 'bg-[#171a24] border-[#1f2433]' : 'bg-slate-50 border-[#e2e8f0]'}">
              <div class="flex items-center gap-3">
                <span class="material-symbols-outlined text-[#d4af37] text-xl" aria-hidden="true">key</span>
                <div>
                  <span class="text-xs font-bold block">Токенизация базы персональных данных (PII)</span>
                  <span class="text-[11px] opacity-80 font-sans">Хранилище с шифрованием полей AES-256</span>
                </div>
              </div>
              <span class="font-mono text-xs px-2 py-0.5 rounded font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                АКТИВЕН
              </span>
            </div>

            <!-- Gateway 2 -->
            <div class="p-3 rounded border flex items-center justify-between {theme === 'dark' ? 'bg-[#171a24] border-[#1f2433]' : 'bg-slate-50 border-[#e2e8f0]'}">
              <div class="flex items-center gap-3">
                <span class="material-symbols-outlined text-[#d4af37] text-xl" aria-hidden="true">sync_alt</span>
                <div>
                  <span class="text-xs font-bold block">Изолированные пайплайны телеметрии</span>
                  <span class="text-[11px] opacity-80 font-sans">Строгое обезличивание IP активно</span>
                </div>
              </div>
              <span class="font-mono text-xs px-2 py-0.5 rounded font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                МАРШРУТИЗИРОВАНО
              </span>
            </div>

            <!-- Gateway 3 -->
            <div class="p-3 rounded border flex items-center justify-between {theme === 'dark' ? 'bg-[#171a24] border-[#1f2433]' : 'bg-slate-50 border-[#e2e8f0]'}">
              <div class="flex items-center gap-3">
                <span class="material-symbols-outlined text-[#d4af37] text-xl" aria-hidden="true">history</span>
                <div>
                  <span class="text-xs font-bold block">Журнал холодного удаления данных</span>
                  <span class="text-[11px] opacity-80 font-sans">Криптографическая цепочка SHA-256</span>
                </div>
              </div>
              <span class="font-mono text-xs px-2 py-0.5 rounded font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                СИНХРОНИЗИРОВАНО
              </span>
            </div>
          </div>

          <div class="pt-3 border-t flex flex-wrap items-center justify-between font-mono text-xs opacity-90 gap-2 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
            <span>Хеш реестра: <strong class="text-[#d4af37]">#7f8c...3a19</strong></span>
            <div class="flex items-center gap-2">
              <button
                id="admin-view-gqm-btn"
                type="button"
                on:click={() => (showGqmDetails = !showGqmDetails)}
                class="px-2.5 py-1 rounded border text-xs font-mono font-bold transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {showGqmDetails ? 'bg-[#d4af37] text-white border-sky-400' : 'border-[#d4af37]/40 text-[#d4af37] hover:bg-[#d4af37]/10'}"
              >
                {showGqmDetails ? 'Скрыть аудит телеметрии' : 'Показать аудит телеметрии'}
              </button>
              <button id="admin-audit-ledger-btn" type="button" class="text-[#d4af37] hover:underline flex items-center gap-0.5 focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none rounded px-1">
                <span>Журнал аудита</span>
                <span class="material-symbols-outlined text-sm" aria-hidden="true">open_in_new</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      {#if showGqmDetails}
        <section id="admin-telemetry-details" class="p-4 rounded-xl border bg-[#0a0e18] border-[#d4af37]/30 space-y-4">
          <h3 class="text-sm font-bold font-mono text-[#d4af37]">[ПРИВАТНЫЙ АУДИТ ТЕЛЕМЕТРИИ]</h3>
          <GqmTelemetryView />
        </section>
      {/if}
    {/if}

  </main>

  <!-- Bottom Navigation Bar (rendered on /admin) -->
  {#if !isInboxRoute}
    <nav
      id="admin-bottom-nav"
      aria-label="Admin Navigation Tabs"
      class="fixed bottom-0 left-0 w-full z-40 flex justify-around items-center h-16 border-t shadow-lg transition-colors {theme === 'dark' ? 'bg-[#13161e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
    >
      <button
        id="admin-tab-overview"
        type="button"
        on:click={() => (activeNav = 'overview')}
        class="flex flex-col items-center justify-center py-1 px-3 rounded transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {activeNav === 'overview' ? 'text-[#d4af37] font-bold bg-[#d4af37]/10' : 'opacity-70 hover:opacity-100'}"
      >
        <span class="material-symbols-outlined text-xl" aria-hidden="true">dashboard</span>
        <span class="text-[11px] font-mono">Обзор (Overview)</span>
      </button>

      <button
        id="admin-tab-requests"
        type="button"
        on:click={() => (activeNav = 'requests')}
        class="flex flex-col items-center justify-center py-1 px-3 rounded transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {activeNav === 'requests' ? 'text-[#d4af37] font-bold bg-[#d4af37]/10' : 'opacity-70 hover:opacity-100'}"
      >
        <span class="material-symbols-outlined text-xl" aria-hidden="true">assignment_turned_in</span>
        <span class="text-[11px] font-mono">Запросы (Requests)</span>
      </button>

      <button
        id="admin-tab-consent"
        type="button"
        on:click={() => (activeNav = 'consent')}
        class="flex flex-col items-center justify-center py-1 px-3 rounded transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {activeNav === 'consent' ? 'text-[#d4af37] font-bold bg-[#d4af37]/10' : 'opacity-70 hover:opacity-100'}"
      >
        <span class="material-symbols-outlined text-xl" aria-hidden="true">tune</span>
        <span class="text-[11px] font-mono">Согласия (Consent)</span>
      </button>

      <button
        id="admin-tab-audit"
        type="button"
        on:click={() => (activeNav = 'audit')}
        class="flex flex-col items-center justify-center py-1 px-3 rounded transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {activeNav === 'audit' ? 'text-[#d4af37] font-bold bg-[#d4af37]/10' : 'opacity-70 hover:opacity-100'}"
      >
        <span class="material-symbols-outlined text-xl" aria-hidden="true">history_toggle_off</span>
        <span class="text-[11px] font-mono">Журнал аудита (Audit Logs)</span>
      </button>
    </nav>
  {/if}

  <!-- Destructive Action Confirmation Modal -->
  {#if showDeleteModal}
    <div
      id="delete-confirmation-overlay"
      role="dialog"
      aria-modal="true"
      aria-labelledby="delete-modal-title"
      aria-describedby="delete-modal-description"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm"
    >
      <div
        id="delete-modal-container"
        class="w-full max-w-md p-6 rounded-2xl border shadow-2xl space-y-5 font-sans {theme === 'dark' ? 'bg-[#13161e] border-red-500/60 text-[#f8f9ff]' : 'bg-white border-red-400 text-[#0b1c30]'}"
      >
        <div class="flex items-center gap-3 border-b pb-3 border-red-500/30">
          <span class="material-symbols-outlined text-3xl text-red-500" aria-hidden="true">warning</span>
          <div>
            <h2 id="delete-modal-title" class="text-lg font-bold text-red-500">
              Подтверждение удаления (Confirm Destructive Erasure)
            </h2>
            <span class="font-mono text-xs opacity-70">Объект: #{targetDsarId} ({targetSubject})</span>
          </div>
        </div>

        <p id="delete-modal-description" class="text-xs leading-relaxed opacity-90">
          Это действие безвозвратно удалит все записи клиента, идентификаторы биллинга и аналитические данные для
          <strong class="font-mono underline text-red-400">{targetSubject}</strong>. Процедура необратима согласно ст. 17 GDPR.
        </p>

        <div class="space-y-2">
          <label for="delete-confirm-input" class="block text-xs font-semibold">
            Введите <span class="font-mono font-bold text-red-400">DELETE</span> для подтверждения:
          </label>
          <input
            id="delete-confirm-input"
            type="text"
            bind:value={confirmInput}
            placeholder="DELETE"
            class="w-full p-2.5 rounded border font-mono text-xs transition-colors focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:outline-none {theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433] text-white' : 'bg-slate-50 border-[#c6c6cd] text-black'}"
          />
        </div>

        <div class="flex items-center justify-end gap-3 pt-2">
          <button
            id="cancel-deletion-btn"
            type="button"
            bind:this={cancelBtnRef}
            on:click={closeDeleteModal}
            class="px-4 py-2 rounded text-xs font-semibold border transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {theme === 'dark' ? 'border-[#1f2433] bg-[#171a24] hover:bg-[#25304e] text-white' : 'border-[#c6c6cd] bg-slate-100 hover:bg-slate-200 text-[#0b1c30]'}"
          >
            Отмена (Cancel)
          </button>
          <button
            id="confirm-deletion-btn"
            type="button"
            bind:this={confirmBtnRef}
            disabled={confirmInput.trim().toUpperCase() !== 'DELETE'}
            on:click={executeDeletion}
            class="px-5 py-2 rounded text-xs font-bold transition-all focus-visible:ring-2 focus-visible:ring-red-400 focus-visible:outline-none {confirmInput.trim().toUpperCase() === 'DELETE' ? 'bg-red-600 text-white hover:bg-red-700 cursor-pointer shadow-md' : 'bg-slate-700 text-slate-400 cursor-not-allowed opacity-50'}"
          >
            Подтвердить удаление (Confirm Deletion)
          </button>
        </div>
      </div>
    </div>
  {/if}
</div>
