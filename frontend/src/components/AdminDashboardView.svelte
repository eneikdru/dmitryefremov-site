<script lang="ts">
  import { onMount, tick } from 'svelte';
  import GqmTelemetryView from './GqmTelemetryView.svelte';

  export let theme: 'dark' | 'light' = 'dark';

  // Navigation tab inside admin
  let activeNav: 'overview' | 'requests' | 'consent' | 'audit' = 'overview';
  let showGqmDetails: boolean = false;

  // DSAR Deletion Modal state
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
    if (e.key === 'Escape' && showDeleteModal) {
      closeDeleteModal();
    }
  }

  interface IntakeMessage {
    id: number;
    claimTicket: string;
    status: string;
    classification: string;
    title: string;
    narrative: string;
    clientEmail: string;
    createdAt: string;
  }

  let messages: IntakeMessage[] = [];
  let isLoadingMessages: boolean = true;

  async function loadMessages() {
    isLoadingMessages = true;
    try {
      const res = await fetch('/api/intake');
      if (res.ok) {
        const data = await res.json();
        messages = (data.messages || []).slice().reverse();
      }
    } catch (e) {
      console.error('Failed to load messages', e);
    } finally {
      isLoadingMessages = false;
    }
  }

  onMount(() => {
    loadMessages();
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
    class="fixed top-0 left-0 w-full z-40 flex justify-between items-center px-4 sm:px-6 h-14 border-b transition-colors shadow-sm {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433] text-[#ffffff]' : 'bg-white border-[#c6c6cd] text-[#0b1c30]'}"
  >
    <div class="flex items-center space-x-3">
      <span class="material-symbols-outlined text-2xl text-sky-400" aria-hidden="true">shield</span>
      <div class="flex flex-col">
        <span class="text-sm font-bold tracking-tight leading-none">Trust & Compliance</span>
        <span class="text-[11px] font-mono font-medium opacity-80">Compliance Ops</span>
      </div>
    </div>

    <div class="flex items-center space-x-2">
      <a
        id="admin-exit-btn"
        href="/"
        class="px-3 py-1.5 rounded text-xs font-mono font-semibold border transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none flex items-center gap-1 {theme === 'dark' ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8]/40 hover:bg-[#2d354b]' : 'bg-[#f1f5f9] text-[#0284c7] border-[#0284c7]/40 hover:bg-[#e2e8f0]'}"
      >
        <span>← Public Portal</span>
      </a>
      <button
        id="admin-filter-btn"
        type="button"
        title="Filter Queue"
        aria-label="Filter Queue"
        class="p-2 rounded hover:bg-black/10 dark:hover:bg-white/10 transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none"
      >
        <span class="material-symbols-outlined text-xl" aria-hidden="true">filter_list</span>
      </button>
      <button
        id="admin-framework-btn"
        type="button"
        title="Statutory Legal Framework"
        aria-label="Statutory Legal Framework"
        class="p-2 rounded hover:bg-black/10 dark:hover:bg-white/10 transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none"
      >
        <span class="material-symbols-outlined text-xl" aria-hidden="true">gavel</span>
      </button>
    </div>
  </header>

  <!-- Main Content Canvas -->
  <main id="admin-main-content" class="w-full max-w-4xl mx-auto pt-20 pb-16 px-4 sm:px-6 space-y-6">
    <!-- Incoming Messages Vault Section (Dmitry's Private Inbox) -->
    <section
      id="messages-inbox-section"
      class="p-5 sm:p-6 rounded-2xl border transition-all shadow-xl {theme === 'dark' ? 'bg-[#101728] border-sky-500/30' : 'bg-white border-sky-400'}"
    >
      <div class="flex items-center justify-between pb-4 border-b {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
        <div class="flex items-center gap-3">
          <div class="w-9 h-9 rounded-xl bg-sky-500/10 flex items-center justify-center text-sky-400 border border-sky-500/20">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          </div>
          <div>
            <h2 class="text-base sm:text-lg font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
              Входящие сообщения с сайта
            </h2>
            <p class="text-xs font-mono text-slate-400">Прямое защищенное хранилище (Vault)</p>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <span class="px-3 py-1 rounded-full text-xs font-mono font-bold bg-sky-500/20 text-sky-400 border border-sky-500/30">
            {messages.length} обращений
          </span>
          <button
            type="button"
            on:click={loadMessages}
            title="Обновить список"
            class="px-3 py-1 text-xs font-mono rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition-colors flex items-center gap-1 active:scale-95"
          >
            <span>↻</span>
            <span class="hidden sm:inline">Обновить</span>
          </button>
        </div>
      </div>

      {#if isLoadingMessages}
        <div class="py-10 text-center text-xs font-mono text-slate-400 flex flex-col items-center gap-2">
          <span class="w-4 h-4 border-2 border-sky-400 border-t-transparent rounded-full animate-spin"></span>
          <span>Чтение реестра сообщений...</span>
        </div>
      {:else if messages.length === 0}
        <div class="py-10 text-center text-xs font-mono text-slate-400">
          Сообщений пока нет. Любое новое обращение через форму сайта появится здесь мгновенно.
        </div>
      {:else}
        <div class="divide-y {theme === 'dark' ? 'divide-[#1f2433]' : 'divide-[#e2e8f0]'} mt-1">
          {#each messages as msg}
            <div class="py-4 space-y-2.5">
              <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-1.5">
                <div class="flex items-center gap-2 flex-wrap">
                  <span class="font-mono text-xs font-bold text-sky-400 bg-sky-500/10 px-2 py-0.5 rounded border border-sky-500/20">
                    {msg.claimTicket}
                  </span>
                  <span class="text-[11px] px-2 py-0.5 rounded font-mono text-slate-300 bg-slate-800 border border-slate-700">
                    {msg.classification || 'general'}
                  </span>
                  <span class="text-[11px] font-mono text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20">
                    {msg.status || 'NEW'}
                  </span>
                </div>
                <span class="text-xs font-mono text-slate-400">
                  {new Date(msg.createdAt).toLocaleString('ru-RU', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })}
                </span>
              </div>

              <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                <div class="space-y-1">
                  <div class="text-sm sm:text-base font-bold {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
                    {msg.title || '(Без темы)'}
                  </div>
                  <div class="text-xs font-mono text-slate-400">
                    Отправитель: <strong class="text-sky-300 font-semibold">{msg.clientEmail || 'Анонимно'}</strong>
                  </div>
                </div>

                {#if msg.clientEmail}
                  <a
                    href="mailto:{msg.clientEmail}?subject=Re: {encodeURIComponent(msg.title || 'Сообщение с dmitryefremov.com')}"
                    class="shrink-0 px-3.5 py-1.5 rounded-lg text-xs font-mono font-semibold bg-sky-500 hover:bg-sky-400 text-[#090a0f] transition-all shadow-sm flex items-center gap-1.5"
                  >
                    <span>Ответить</span>
                    <span>→</span>
                  </a>
                {/if}
              </div>

              {#if msg.narrative}
                <div class="p-3.5 rounded-xl {theme === 'dark' ? 'bg-[#090a0f] border border-white/5' : 'bg-slate-50 border border-slate-200'} font-sans text-xs sm:text-sm text-slate-200 leading-relaxed whitespace-pre-wrap">
                  {msg.narrative}
                </div>
              {/if}
            </div>
          {/each}
        </div>
      {/if}
    </section>

    <!-- Status Anchor & Sub-Header Banner -->
    <section
      id="statutory-benchmark-banner"
      class="p-4 rounded-xl border transition-all shadow-sm {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
    >
      <div class="flex items-center justify-between pb-1">
        <span class="font-mono text-xs font-semibold uppercase tracking-wider text-sky-400">
          STATUTORY BENCHMARK
        </span>
        <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded text-xs font-mono font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
          <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
          ENFORCED
        </span>
      </div>
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pt-2">
        <div>
          <h1 class="text-xl sm:text-2xl font-bold tracking-tight">GDPR / CCPA Compliant: 99.4%</h1>
          <p class="text-xs font-sans opacity-80 mt-1">Audited across 14 sovereign data jurisdictions (UTC synced)</p>
        </div>
        <button
          id="admin-audit-filter-btn"
          type="button"
          title="Quick Audit Filter"
          aria-label="Quick Audit Filter"
          class="p-2 rounded border transition-colors self-start sm:self-center focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {theme === 'dark' ? 'border-[#1f2433] hover:bg-[#1f2433] text-emerald-400' : 'border-[#c6c6cd] hover:bg-slate-100 text-emerald-600'}"
        >
          <span class="material-symbols-outlined text-xl" aria-hidden="true">tune</span>
        </button>
      </div>
    </section>

    <!-- Key Metrics Cards Grid -->
    <section id="admin-metrics-grid" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <!-- Metric 1: Active DSARs -->
      <div class="sm:col-span-2 md:col-span-1 p-4 rounded-xl border shadow-sm flex flex-col justify-between {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
        <div>
          <div class="flex justify-between items-start">
            <span class="font-mono text-xs font-semibold opacity-80">ACTIVE DSARS</span>
            <span class="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[11px] font-mono font-bold bg-red-500/20 text-red-400 border border-red-500/30">
              <span class="material-symbols-outlined text-sm" aria-hidden="true">alarm</span>
              2 due in &lt;48h
            </span>
          </div>
          <div class="flex items-baseline gap-2 mt-2">
            <span class="text-3xl font-extrabold">{dsar8821Status === 'deleted' ? '11' : '12'}</span>
            <span class="text-sm font-medium opacity-80">Pending</span>
          </div>
        </div>
        <div class="mt-4 pt-3 border-t flex items-center justify-between text-xs font-mono opacity-80 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          <span class="flex items-center gap-1">
            <span class="material-symbols-outlined text-sm text-emerald-400" aria-hidden="true">verified_user</span>
            Statutory SLA threshold: 30 days
          </span>
          <span class="font-bold">Avg: 3.4d</span>
        </div>
      </div>

      <!-- Metric 2: Consent Opt-In Rate -->
      <div class="p-4 rounded-xl border shadow-sm flex flex-col justify-between {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
        <div>
          <span class="font-mono text-xs font-semibold opacity-80 block">CONSENT OPT-IN</span>
          <div class="mt-2">
            <span class="text-3xl font-extrabold">84.2%</span>
          </div>
        </div>
        <div class="mt-4 flex items-center justify-between text-xs text-emerald-400">
          <span class="inline-flex items-center gap-1 font-mono font-bold">
            <span class="material-symbols-outlined text-sm" aria-hidden="true">trending_up</span>
            +2.1%
          </span>
          <span class="font-sans text-[11px] opacity-80 {theme === 'dark' ? 'text-slate-300' : 'text-slate-600'}">30d trend</span>
        </div>
      </div>

      <!-- Metric 3: Sub-Processors -->
      <div class="p-4 rounded-xl border shadow-sm flex flex-col justify-between {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
        <div>
          <span class="font-mono text-xs font-semibold opacity-80 block">SUB-PROCESSORS</span>
          <div class="mt-2">
            <span class="text-2xl font-bold">24 Verified</span>
          </div>
        </div>
        <div class="mt-4 pt-1">
          <span class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded text-xs font-mono font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30">
            <span class="w-1.5 h-1.5 rounded-full bg-amber-400"></span>
            1 Review Needed
          </span>
        </div>
      </div>
    </section>

    <!-- Urgent Data Subject Requests (DSAR Queue) -->
    <section id="dsar-queue-section" class="space-y-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="material-symbols-outlined text-emerald-400 text-2xl" aria-hidden="true">assignment_turned_in</span>
          <h2 class="text-lg font-bold tracking-tight">Urgent Data Subject Requests</h2>
        </div>
        <span class="font-mono text-xs opacity-80">Queue: {dsar8821Status === 'deleted' ? '2 Active' : '3 Active'}</span>
      </div>

      <!-- Queue Item 1: Erasure Request (High Priority) -->
      <div
        id="dsar-item-8821"
        class="p-4 rounded-xl border transition-all shadow-sm space-y-3 {dsar8821Status === 'deleted' ? 'opacity-60 bg-slate-800/40 border-slate-700' : (theme === 'dark' ? 'bg-[#131b2e] border-red-500/50' : 'bg-white border-red-400')}"
      >
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
          <div class="flex flex-col">
            <div class="flex items-center gap-2">
              <span class="font-mono text-xs font-bold">#DSAR-8821</span>
              {#if dsar8821Status === 'deleted'}
                <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30">
                  DELETED / PURGED
                </span>
              {:else}
                <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-red-500/20 text-red-400 font-bold border border-red-500/30">
                  CRITICAL SLA
                </span>
              {/if}
            </div>
            <span class="text-base font-bold mt-1">Erasure / Right to be Forgotten</span>
            <span class="font-mono text-xs opacity-80">Subject: u_8910 • Direct consumer tier</span>
          </div>

          <div class="sm:text-right">
            {#if dsar8821Status === 'deleted'}
              <span class="font-mono text-xs font-bold text-emerald-400 block">Completed</span>
            {:else}
              <span class="font-mono text-xs font-bold text-red-400 block">18h left</span>
            {/if}
            <span class="text-[11px] opacity-70">Art. 17 GDPR</span>
          </div>
        </div>

        <p class="text-xs opacity-80 pt-2 border-t {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          Requires cascading purge across customer database, billing shards, and marketing analytics sink.
        </p>

        <!-- Action Buttons -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
          <button
            id="dsar-8821-review-btn"
            type="button"
            class="h-9 px-4 border rounded text-xs font-semibold transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none flex items-center justify-center gap-1.5 {theme === 'dark' ? 'border-[#1f2433] bg-[#1a233a] hover:bg-[#25304e] text-white' : 'border-[#c6c6cd] bg-slate-100 hover:bg-slate-200 text-[#0b1c30]'}"
          >
            <span class="material-symbols-outlined text-sm" aria-hidden="true">visibility</span>
            Review Details
          </button>

          {#if dsar8821Status === 'pending'}
            <button
              id="dsar-8821-delete-btn"
              type="button"
              on:click={(e) => openDeleteModal('DSAR-8821', 'u_8910', e)}
              class="h-9 px-4 rounded text-xs font-bold bg-red-600 text-white hover:bg-red-700 transition-all focus-visible:ring-2 focus-visible:ring-red-400 focus-visible:outline-none flex items-center justify-center gap-1.5 cursor-pointer"
            >
              <span class="material-symbols-outlined text-sm" aria-hidden="true">delete_sweep</span>
              Execute Deletion
            </button>
          {:else}
            <button
              type="button"
              disabled
              class="h-9 px-4 rounded text-xs font-bold bg-emerald-800/40 text-emerald-300 cursor-not-allowed flex items-center justify-center gap-1.5"
            >
              <span class="material-symbols-outlined text-sm" aria-hidden="true">check_circle</span>
              Purge Confirmed
            </button>
          {/if}
        </div>
      </div>

      <!-- Queue Item 2: Data Export (ZIP) -->
      <div
        id="dsar-item-8819"
        class="p-4 rounded-xl border shadow-sm space-y-3 {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
      >
        <div class="flex justify-between items-start">
          <div class="flex flex-col">
            <div class="flex items-center gap-2">
              <span class="font-mono text-xs font-bold">#DSAR-8819</span>
              <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-sky-500/20 text-sky-300 font-bold border border-sky-500/30">
                BATCH PROCESS
              </span>
            </div>
            <span class="text-base font-bold mt-1">Data Export (ZIP)</span>
            <span class="font-mono text-xs opacity-80">Subject: u_4412 • Portability request</span>
          </div>
          <span class="font-mono text-xs font-bold text-sky-400">Processing (92%)</span>
        </div>

        <div class="w-full bg-slate-700/40 h-2 rounded-full overflow-hidden">
          <div class="bg-sky-400 h-full rounded-full transition-all" style="width: 92%;"></div>
        </div>

        <div class="flex items-center justify-between font-mono text-xs opacity-80">
          <span>Target: archive_u4412_payload.enc</span>
          <span>SLA: 26d left</span>
        </div>
      </div>

      <!-- Queue Item 3: Rectification -->
      <div
        id="dsar-item-8814"
        class="p-4 rounded-xl border shadow-sm {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
      >
        <div class="flex justify-between items-start">
          <div class="flex flex-col">
            <div class="flex items-center gap-2">
              <span class="font-mono text-xs font-bold">#DSAR-8814</span>
              <span class="font-mono text-[10px] px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30">
                RECONCILED
              </span>
            </div>
            <span class="text-base font-bold mt-1">Rectification</span>
            <span class="font-mono text-xs opacity-80">Attribute: Legal Identity Name & Tax ID</span>
          </div>
          <div class="flex items-center gap-1 text-emerald-400">
            <span class="material-symbols-outlined text-base" aria-hidden="true">check_circle</span>
            <span class="font-mono text-xs font-bold">Completed</span>
          </div>
        </div>
      </div>
    </section>

    <!-- System Isolation & Audit Section -->
    <section id="system-isolation-section" class="space-y-4 pt-2">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="material-symbols-outlined text-sky-400 text-2xl" aria-hidden="true">lock</span>
          <h2 class="text-lg font-bold tracking-tight">System Isolation & Audit</h2>
        </div>
        <span class="font-mono text-xs text-sky-400 font-semibold">Zero-Trust V2</span>
      </div>

      <div class="p-4 rounded-xl border shadow-sm space-y-4 {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
        <div class="flex items-center justify-between border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          <div class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-emerald-400"></span>
            <span class="font-bold text-sm">Data Silo Isolation Gateways</span>
          </div>
          <span class="font-mono text-xs text-emerald-400 font-bold uppercase">Operational</span>
        </div>

        <div class="space-y-2">
          <!-- Gateway 1 -->
          <div class="p-3 rounded border flex items-center justify-between {theme === 'dark' ? 'bg-[#1a233a] border-[#1f2433]' : 'bg-slate-50 border-[#e2e8f0]'}">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-sky-400 text-xl" aria-hidden="true">key</span>
              <div>
                <span class="text-xs font-bold block">PII Database Tokenization</span>
                <span class="text-[11px] opacity-80 font-sans">AES-256 field-level vault</span>
              </div>
            </div>
            <span class="font-mono text-xs px-2 py-0.5 rounded font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              ACTIVE
            </span>
          </div>

          <!-- Gateway 2 -->
          <div class="p-3 rounded border flex items-center justify-between {theme === 'dark' ? 'bg-[#1a233a] border-[#1f2433]' : 'bg-slate-50 border-[#e2e8f0]'}">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-sky-400 text-xl" aria-hidden="true">sync_alt</span>
              <div>
                <span class="text-xs font-bold block">Isolated Telemetry Pipelines</span>
                <span class="text-[11px] opacity-80 font-sans">Strict IP redaction enforced</span>
              </div>
            </div>
            <span class="font-mono text-xs px-2 py-0.5 rounded font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              ROUTED
            </span>
          </div>

          <!-- Gateway 3 -->
          <div class="p-3 rounded border flex items-center justify-between {theme === 'dark' ? 'bg-[#1a233a] border-[#1f2433]' : 'bg-slate-50 border-[#e2e8f0]'}">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-sky-400 text-xl" aria-hidden="true">history</span>
              <div>
                <span class="text-xs font-bold block">Cold Storage Purge Logs</span>
                <span class="text-[11px] opacity-80 font-sans">Cryptographic SHA-256 chain</span>
              </div>
            </div>
            <span class="font-mono text-xs px-2 py-0.5 rounded font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              SYNCED
            </span>
          </div>
        </div>

        <div class="pt-3 border-t flex flex-wrap items-center justify-between font-mono text-xs opacity-90 gap-2 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          <span>Ledger Hash: <strong class="text-sky-400">#7f8c...3a19</strong></span>
          <div class="flex items-center gap-2">
            <button
              id="admin-view-gqm-btn"
              type="button"
              on:click={() => (showGqmDetails = !showGqmDetails)}
              class="px-2.5 py-1 rounded border text-xs font-mono font-bold transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {showGqmDetails ? 'bg-sky-500 text-white border-sky-400' : 'border-sky-500/40 text-sky-400 hover:bg-sky-500/10'}"
            >
              {showGqmDetails ? 'Hide Telemetry Audit' : 'Show Telemetry Audit'}
            </button>
            <button id="admin-audit-ledger-btn" type="button" class="text-sky-400 hover:underline flex items-center gap-0.5 focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none rounded px-1">
              <span>Audit Ledger</span>
              <span class="material-symbols-outlined text-sm" aria-hidden="true">open_in_new</span>
            </button>
          </div>
        </div>
      </div>
    </section>

    {#if showGqmDetails}
      <section id="admin-telemetry-details" class="p-4 rounded-xl border bg-[#0a0e18] border-sky-500/30 space-y-4">
        <h3 class="text-sm font-bold font-mono text-sky-400">[PRIVATE_ADMIN_TELEMETRY_AUDIT]</h3>
        <GqmTelemetryView />
      </section>
    {/if}
  </main>

  <!-- Bottom Navigation Bar -->
  <nav
    id="admin-bottom-nav"
    aria-label="Admin Navigation Tabs"
    class="fixed bottom-0 left-0 w-full z-40 flex justify-around items-center h-16 border-t shadow-lg transition-colors {theme === 'dark' ? 'bg-[#131b2e] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
  >
    <button
      id="admin-tab-overview"
      type="button"
      on:click={() => (activeNav = 'overview')}
      class="flex flex-col items-center justify-center py-1 px-3 rounded transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {activeNav === 'overview' ? 'text-sky-400 font-bold bg-sky-500/10' : 'opacity-70 hover:opacity-100'}"
    >
      <span class="material-symbols-outlined text-xl" aria-hidden="true">dashboard</span>
      <span class="text-[11px] font-mono">Overview</span>
    </button>

    <button
      id="admin-tab-requests"
      type="button"
      on:click={() => (activeNav = 'requests')}
      class="flex flex-col items-center justify-center py-1 px-3 rounded transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {activeNav === 'requests' ? 'text-sky-400 font-bold bg-sky-500/10' : 'opacity-70 hover:opacity-100'}"
    >
      <span class="material-symbols-outlined text-xl" aria-hidden="true">assignment_turned_in</span>
      <span class="text-[11px] font-mono">Requests</span>
    </button>

    <button
      id="admin-tab-consent"
      type="button"
      on:click={() => (activeNav = 'consent')}
      class="flex flex-col items-center justify-center py-1 px-3 rounded transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {activeNav === 'consent' ? 'text-sky-400 font-bold bg-sky-500/10' : 'opacity-70 hover:opacity-100'}"
    >
      <span class="material-symbols-outlined text-xl" aria-hidden="true">tune</span>
      <span class="text-[11px] font-mono">Consent</span>
    </button>

    <button
      id="admin-tab-audit"
      type="button"
      on:click={() => (activeNav = 'audit')}
      class="flex flex-col items-center justify-center py-1 px-3 rounded transition-colors focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {activeNav === 'audit' ? 'text-sky-400 font-bold bg-sky-500/10' : 'opacity-70 hover:opacity-100'}"
    >
      <span class="material-symbols-outlined text-xl" aria-hidden="true">history_toggle_off</span>
      <span class="text-[11px] font-mono">Audit Logs</span>
    </button>
  </nav>

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
        class="w-full max-w-md p-6 rounded-2xl border shadow-2xl space-y-5 font-sans {theme === 'dark' ? 'bg-[#131b2e] border-red-500/60 text-[#f8f9ff]' : 'bg-white border-red-400 text-[#0b1c30]'}"
      >
        <div class="flex items-center gap-3 border-b pb-3 border-red-500/30">
          <span class="material-symbols-outlined text-3xl text-red-500" aria-hidden="true">warning</span>
          <div>
            <h2 id="delete-modal-title" class="text-lg font-bold text-red-500">
              Confirm Destructive Erasure
            </h2>
            <span class="font-mono text-xs opacity-70">Target: #{targetDsarId} ({targetSubject})</span>
          </div>
        </div>

        <p id="delete-modal-description" class="text-xs leading-relaxed opacity-90">
          This action will permanently purge all customer records, billing shard identifiers, and analytics entries for
          <strong class="font-mono underline text-red-400">{targetSubject}</strong>. This action is irrevocable under Art. 17 GDPR.
        </p>

        <div class="space-y-2">
          <label for="delete-confirm-input" class="block text-xs font-semibold">
            Type <span class="font-mono font-bold text-red-400">DELETE</span> to confirm:
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
            class="px-4 py-2 rounded text-xs font-semibold border transition-all focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none {theme === 'dark' ? 'border-[#1f2433] bg-[#1a233a] hover:bg-[#25304e] text-white' : 'border-[#c6c6cd] bg-slate-100 hover:bg-slate-200 text-[#0b1c30]'}"
          >
            Cancel
          </button>
          <button
            id="confirm-deletion-btn"
            type="button"
            bind:this={confirmBtnRef}
            disabled={confirmInput.trim().toUpperCase() !== 'DELETE'}
            on:click={executeDeletion}
            class="px-5 py-2 rounded text-xs font-bold transition-all focus-visible:ring-2 focus-visible:ring-red-400 focus-visible:outline-none {confirmInput.trim().toUpperCase() === 'DELETE' ? 'bg-red-600 text-white hover:bg-red-700 cursor-pointer shadow-md' : 'bg-slate-700 text-slate-400 cursor-not-allowed opacity-50'}"
          >
            Confirm Deletion
          </button>
        </div>
      </div>
    </div>
  {/if}
</div>
