<script lang="ts">
  import { onMount } from 'svelte';

  export let theme: 'dark' | 'light' = 'dark';

  let activeTab: 'overview' | 'requests' | 'consent' | 'audit' = 'overview';
  let isDeletionModalOpen: boolean = false;
  let dsar8821Status: 'CRITICAL SLA' | 'PURGED' = 'CRITICAL SLA';
  let dsar8821Completed: boolean = false;
  let auditDetailsOpen: boolean = false;

  function openDeletionModal() {
    isDeletionModalOpen = true;
  }

  function closeDeletionModal() {
    isDeletionModalOpen = false;
  }

  function confirmDeletion() {
    dsar8821Status = 'PURGED';
    dsar8821Completed = true;
    isDeletionModalOpen = false;
  }

  function handleKeydown(e: KeyboardEvent) {
    if (e.key === 'Escape' && isDeletionModalOpen) {
      closeDeletionModal();
    }
  }

  onMount(() => {
    window.addEventListener('keydown', handleKeydown);
    return () => {
      window.removeEventListener('keydown', handleKeydown);
    };
  });
</script>

<div
  id="admin-dashboard-root"
  class="min-h-screen transition-colors duration-200 pb-36 {theme === 'dark' ? 'bg-[#090a0f] text-[#f8fafc]' : 'bg-[#f8f9ff] text-[#0b1c30]'}"
>
  <!-- TopAppBar Header -->
  <header
    id="admin-header"
    class="sticky top-0 left-0 w-full z-40 flex justify-between items-center px-4 h-14 border-b transition-colors {theme === 'dark' ? 'bg-[#0f1118]/95 border-[#1f2433]' : 'bg-white/95 border-[#c6c6cd]'}"
  >
    <div class="flex items-center space-x-2">
      <svg class="w-5 h-5 {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
      </svg>
      <div class="flex flex-col">
        <span class="text-sm font-bold tracking-tight leading-none {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Trust & Compliance</span>
        <span class="text-[11px] font-mono font-medium {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">Compliance Ops</span>
      </div>
    </div>
    <div class="flex items-center space-x-2">
      <button
        id="admin-filter-btn"
        type="button"
        aria-label="Filter Queue"
        class="p-1.5 rounded focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors {theme === 'dark' ? 'hover:bg-[#1f2433] text-[#cbd5e1]' : 'hover:bg-[#e5eeff] text-[#0b1c30]'}"
        title="Filter Queue"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
        </svg>
      </button>
      <button
        id="admin-legal-framework-btn"
        type="button"
        aria-label="Statutory Legal Framework"
        class="p-1.5 rounded focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors {theme === 'dark' ? 'hover:bg-[#1f2433] text-[#38bdf8]' : 'hover:bg-[#e5eeff] text-[#006a61]'}"
        title="Statutory Legal Framework"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 6l3 1m0 0l-3 9a5.002 5.002 0 006.001 0M6 7l3 9M6 7l6-2m6 2l3-1m-3 1l-3 9a5.002 5.002 0 006.001 0M18 7l3 9m-3-9l-6-2m0-2v2m0 16V5m0 16H9m3 0h3" />
        </svg>
      </button>
    </div>
  </header>

  <!-- Main Admin Canvas -->
  <main class="w-full max-w-2xl mx-auto pt-4 px-4 pb-12 flex flex-col space-y-6">
    <!-- Status Anchor Banner -->
    <section
      id="admin-status-banner"
      class="flex flex-col gap-2 p-4 rounded-xl border transition-colors {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
    >
      <div class="flex items-center justify-between">
        <span class="font-mono text-[11px] font-medium tracking-wide {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">
          STATUTORY BENCHMARK
        </span>
        <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded font-mono text-[11px] font-bold border {theme === 'dark' ? 'bg-[#002113] text-[#34d399] border-[#009668]/40' : 'bg-[#e6f4f1] text-[#006a61] border-[#86f2e4]'}">
          <span class="w-1.5 h-1.5 rounded-full bg-[#006a61] dark:bg-[#34d399] animate-pulse"></span>
          ENFORCED
        </span>
      </div>
      <div class="flex items-center justify-between pt-1">
        <div>
          <h1 class="text-lg font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">
            GDPR / CCPA Compliant: 99.4%
          </h1>
          <p class="text-[11px] mt-0.5 {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">
            Audited across 14 sovereign data jurisdictions (UTC synced)
          </p>
        </div>
        <button
          id="admin-audit-filter-btn"
          type="button"
          aria-label="Quick Audit Filter"
          class="p-2 rounded focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors {theme === 'dark' ? 'hover:bg-[#1f2433] text-[#38bdf8]' : 'hover:bg-[#e5eeff] text-[#006a61]'}"
          title="Quick Audit Filter"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4" />
          </svg>
        </button>
      </div>
    </section>

    <!-- Key Metrics Bento Grid -->
    <section id="admin-metrics-section" class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <!-- Active DSARs -->
      <div class="sm:col-span-2 p-4 rounded-xl border transition-colors {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
        <div class="flex justify-between items-start">
          <div>
            <span class="font-mono text-[11px] block {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">ACTIVE DSARS</span>
            <div class="flex items-baseline gap-2 mt-1">
              <span class="text-3xl font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">
                {dsar8821Completed ? '11' : '12'}
              </span>
              <span class="text-xs font-medium {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#45464d]'}">Pending</span>
            </div>
          </div>
          <div class="inline-flex items-center gap-1.5 px-2.5 py-1 border rounded text-xs font-mono font-bold {theme === 'dark' ? 'bg-[#450a0a]/60 border-[#ef4444]/40 text-[#fca5a5]' : 'bg-[#ffdad6] border-[#ba1a1a]/30 text-[#93000a]'}">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>2 due in &lt;48h</span>
          </div>
        </div>
        <div class="mt-3 pt-2 border-t flex items-center justify-between text-[11px] font-mono {theme === 'dark' ? 'border-[#1f2433] text-[#94a3b8]' : 'border-[#c6c6cd] text-[#45464d]'}">
          <span class="flex items-center gap-1.5">
            <svg class="w-3.5 h-3.5 text-[#006a61] dark:text-[#38bdf8]" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
            </svg>
            Statutory SLA threshold: 30 days
          </span>
          <span class="font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Avg: 3.4d</span>
        </div>
      </div>

      <!-- Consent Opt-in Rate -->
      <div class="p-4 rounded-xl border flex flex-col justify-between transition-colors {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
        <div>
          <span class="font-mono text-[11px] block {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">CONSENT OPT-IN</span>
          <div class="flex items-baseline gap-1 mt-1">
            <span class="text-2xl font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">84.2%</span>
          </div>
        </div>
        <div class="mt-3 flex items-center justify-between text-xs font-mono">
          <span class="inline-flex items-center gap-1 font-bold {theme === 'dark' ? 'text-[#34d399]' : 'text-[#006a61]'}">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
            </svg>
            +2.1%
          </span>
          <span class="text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">30d trend</span>
        </div>
      </div>

      <!-- Sub-Processor Audits -->
      <div class="p-4 rounded-xl border flex flex-col justify-between transition-colors {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
        <div>
          <span class="font-mono text-[11px] block {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">SUB-PROCESSORS</span>
          <div class="mt-1 flex flex-col">
            <span class="text-base font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">24 Verified</span>
          </div>
        </div>
        <div class="mt-3 pt-1">
          <span class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded font-mono text-[11px] font-semibold border {theme === 'dark' ? 'bg-[#450a0a]/50 border-[#ef4444]/30 text-[#fca5a5]' : 'bg-[#ffdad6] border-[#ba1a1a]/30 text-[#93000a]'}">
            <span class="w-1.5 h-1.5 rounded-full bg-[#ba1a1a] dark:bg-[#ef4444]"></span>
            1 Review Needed
          </span>
        </div>
      </div>
    </section>

    <!-- Urgent Data Subject Requests (DSAR Queue) -->
    <section id="dsar-queue-section" class="flex flex-col space-y-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <svg class="w-5 h-5 {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
          </svg>
          <h2 class="text-base font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Urgent Data Subject Requests</h2>
        </div>
        <span class="font-mono text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">
          Queue: {dsar8821Completed ? '2 Active' : '3 Active'}
        </span>
      </div>

      <!-- Queue Item 1: Erasure Request -->
      <div
        id="dsar-item-8821"
        class="p-4 rounded-xl border relative transition-colors {dsar8821Completed ? (theme === 'dark' ? 'bg-[#0f1118]/60 border-[#009668]/40' : 'bg-[#f0fdf4] border-[#006a61]/30') : (theme === 'dark' ? 'bg-[#0f1118] border-[#ef4444]/40' : 'bg-white border-[#ba1a1a]/40')}"
      >
        <div class="flex justify-between items-start">
          <div class="flex flex-col">
            <div class="flex items-center gap-2">
              <span class="font-mono text-[11px] font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">#DSAR-8821</span>
              <span class="font-mono text-[10px] px-1.5 py-0.5 rounded font-bold border {dsar8821Completed ? 'bg-[#002113] text-[#34d399] border-[#009668]/40' : (theme === 'dark' ? 'bg-[#450a0a] text-[#fca5a5] border-[#ef4444]/40' : 'bg-[#ffdad6] text-[#93000a] border-[#ba1a1a]/30')}">
                {dsar8821Status}
              </span>
            </div>
            <span class="text-sm font-bold mt-1 {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Erasure / Right to be Forgotten</span>
            <span class="font-mono text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">Subject: u_8910 • Direct consumer tier</span>
          </div>
          <div class="text-right">
            {#if dsar8821Completed}
              <span class="font-mono text-[11px] font-bold text-[#006a61] dark:text-[#34d399] block">PURGED</span>
            {:else}
              <span class="font-mono text-[11px] font-bold text-[#ba1a1a] dark:text-[#fca5a5] block">18h left</span>
            {/if}
            <span class="text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">Art. 17 GDPR</span>
          </div>
        </div>

        <p class="text-[11px] leading-relaxed mt-2 pt-2 border-t {theme === 'dark' ? 'border-[#1f2433] text-[#cbd5e1]' : 'border-[#c6c6cd] text-[#45464d]'}">
          Requires cascading purge across customer database, billing shards, and marketing analytics sink.
        </p>

        <!-- Action Buttons -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-3">
          <button
            id="dsar-review-btn-8821"
            type="button"
            class="h-9 px-3 border rounded text-xs font-semibold focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors flex items-center justify-center gap-1.5 {theme === 'dark' ? 'border-[#1f2433] text-white hover:bg-[#1f2433]' : 'border-[#c6c6cd] text-[#0b1c30] hover:bg-[#e5eeff]'}"
          >
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
            Review Details
          </button>

          {#if !dsar8821Completed}
            <button
              id="execute-deletion-btn"
              type="button"
              on:click={openDeletionModal}
              class="h-9 px-3 rounded text-xs font-bold focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:outline-none transition-colors flex items-center justify-center gap-1.5 bg-[#ba1a1a] hover:bg-[#93000a] text-white dark:bg-[#ef4444] dark:hover:bg-[#dc2626]"
            >
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              Execute Deletion
            </button>
          {:else}
            <div class="h-9 px-3 rounded text-xs font-bold flex items-center justify-center gap-1.5 bg-[#006a61]/20 text-[#006a61] dark:text-[#34d399] border border-[#006a61]/30">
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              Deletion Executed
            </div>
          {/if}
        </div>
      </div>

      <!-- Queue Item 2: Data Export -->
      <div
        id="dsar-item-8819"
        class="p-4 rounded-xl border transition-colors {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
      >
        <div class="flex justify-between items-start">
          <div class="flex flex-col">
            <div class="flex items-center gap-2">
              <span class="font-mono text-[11px] font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">#DSAR-8819</span>
              <span class="font-mono text-[10px] px-1.5 py-0.5 rounded font-medium border {theme === 'dark' ? 'bg-[#1f2433] text-[#cbd5e1] border-[#334155]' : 'bg-[#e5eeff] text-[#0b1c30] border-[#c6c6cd]'}">
                BATCH PROCESS
              </span>
            </div>
            <span class="text-sm font-bold mt-1 {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Data Export (ZIP)</span>
            <span class="font-mono text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">Subject: u_4412 • Portability request</span>
          </div>
          <span class="font-mono text-[11px] font-bold {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}">Processing (92%)</span>
        </div>
        <div class="w-full bg-[#dce9ff] dark:bg-[#1f2433] h-1.5 rounded-full mt-3 overflow-hidden">
          <div class="bg-[#006a61] dark:bg-[#38bdf8] h-full rounded-full" style="width: 92%;"></div>
        </div>
        <div class="mt-2 flex items-center justify-between font-mono text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">
          <span>Target: archive_u4412_payload.enc</span>
          <span>SLA: 26d left</span>
        </div>
      </div>

      <!-- Queue Item 3: Rectification -->
      <div
        id="dsar-item-8814"
        class="p-4 rounded-xl border transition-colors {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}"
      >
        <div class="flex justify-between items-start">
          <div class="flex flex-col">
            <div class="flex items-center gap-2">
              <span class="font-mono text-[11px] font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">#DSAR-8814</span>
              <span class="font-mono text-[10px] px-1.5 py-0.5 rounded font-medium border {theme === 'dark' ? 'bg-[#002113] text-[#34d399] border-[#009668]/40' : 'bg-[#e6f4f1] text-[#006a61] border-[#86f2e4]'}">
                RECONCILED
              </span>
            </div>
            <span class="text-sm font-bold mt-1 {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Rectification</span>
            <span class="font-mono text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">Attribute: Legal Identity Name & Tax ID</span>
          </div>
          <div class="flex items-center gap-1 font-mono text-[11px] font-bold {theme === 'dark' ? 'text-[#34d399]' : 'text-[#006a61]'}">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>Completed</span>
          </div>
        </div>
      </div>
    </section>

    <!-- System Isolation Gateways & Audit Section -->
    <section id="system-isolation-section" class="flex flex-col space-y-3 pt-2">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <svg class="w-5 h-5 {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
          <h2 class="text-base font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">System Isolation & Audit</h2>
        </div>
        <span class="font-mono text-[11px] font-bold {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}">Zero-Trust V2</span>
      </div>

      <div class="p-4 rounded-xl border space-y-3 transition-colors {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#c6c6cd]'}">
        <div class="flex items-center justify-between border-b pb-2 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#c6c6cd]'}">
          <div class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-[#006a61] dark:bg-[#38bdf8]"></span>
            <span class="text-sm font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Data Silo Isolation Gateways</span>
          </div>
          <span class="font-mono text-[11px] font-bold uppercase tracking-wider {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}">Operational</span>
        </div>

        <div class="space-y-2">
          <!-- Gateway 1 -->
          <div class="flex items-center justify-between p-2.5 rounded border transition-colors {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-[#eff4ff] border-[#c6c6cd]'}">
            <div class="flex items-center gap-2.5">
              <svg class="w-4 h-4 {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
              </svg>
              <div>
                <span class="text-xs font-semibold block {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">PII Database Tokenization</span>
                <span class="text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">AES-256 field-level vault</span>
              </div>
            </div>
            <span class="font-mono text-[10px] font-bold px-2 py-0.5 rounded border {theme === 'dark' ? 'bg-[#002113] text-[#34d399] border-[#009668]/40' : 'bg-[#e6f4f1] text-[#006a61] border-[#86f2e4]'}">ACTIVE</span>
          </div>

          <!-- Gateway 2 -->
          <div class="flex items-center justify-between p-2.5 rounded border transition-colors {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-[#eff4ff] border-[#c6c6cd]'}">
            <div class="flex items-center gap-2.5">
              <svg class="w-4 h-4 {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
              </svg>
              <div>
                <span class="text-xs font-semibold block {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Isolated Telemetry Pipelines</span>
                <span class="text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">Strict IP redaction & zero leakage enforced</span>
              </div>
            </div>
            <span class="font-mono text-[10px] font-bold px-2 py-0.5 rounded border {theme === 'dark' ? 'bg-[#002113] text-[#34d399] border-[#009668]/40' : 'bg-[#e6f4f1] text-[#006a61] border-[#86f2e4]'}">ROUTED</span>
          </div>

          <!-- Gateway 3 -->
          <div class="flex items-center justify-between p-2.5 rounded border transition-colors {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-[#eff4ff] border-[#c6c6cd]'}">
            <div class="flex items-center gap-2.5">
              <svg class="w-4 h-4 {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <span class="text-xs font-semibold block {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">Cold Storage Purge Logs</span>
                <span class="text-[11px] {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#45464d]'}">Cryptographic SHA-256 chain</span>
              </div>
            </div>
            <span class="font-mono text-[10px] font-bold px-2 py-0.5 rounded border {theme === 'dark' ? 'bg-[#002113] text-[#34d399] border-[#009668]/40' : 'bg-[#e6f4f1] text-[#006a61] border-[#86f2e4]'}">SYNCED</span>
          </div>
        </div>

        <div class="pt-2 border-t flex items-center justify-between font-mono text-[11px] {theme === 'dark' ? 'border-[#1f2433] text-[#94a3b8]' : 'border-[#c6c6cd] text-[#45464d]'}">
          <span>Ledger Hash: <span class="font-bold {theme === 'dark' ? 'text-white' : 'text-[#0b1c30]'}">#7f8c...3a19</span></span>
          <button
            id="audit-ledger-btn"
            type="button"
            on:click={() => (auditDetailsOpen = !auditDetailsOpen)}
            class="hover:underline flex items-center gap-1 focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none rounded px-1 {theme === 'dark' ? 'text-[#38bdf8]' : 'text-[#006a61]'}"
          >
            <span>Audit Ledger</span>
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
            </svg>
          </button>
        </div>

        {#if auditDetailsOpen}
          <div id="audit-details-panel" class="p-3 rounded border font-mono text-[11px] space-y-1 bg-[#020617] border-[#1e293b] text-[#cbd5e1]">
            <p class="font-bold text-[#38bdf8]">[ZERO_TRUST_AUDIT_LEDGER]</p>
            <p>Block #492811 • Hash: 7f8c12a04891bca29381e4284b39a19</p>
            <p>Verification: Cryptographically signed by Dmitry Efremov Root Key</p>
            <p>Status: All internal telemetry strictly isolated to /admin route.</p>
          </div>
        {/if}
      </div>
    </section>
  </main>

  <!-- Bottom Navigation Bar -->
  <nav
    id="admin-bottom-nav"
    aria-label="Admin Navigation"
    class="fixed bottom-0 left-0 w-full z-40 flex justify-around items-center px-2 h-16 border-t transition-colors {theme === 'dark' ? 'bg-[#0f1118]/95 border-[#1f2433]' : 'bg-white/95 border-[#c6c6cd]'}"
  >
    <button
      id="admin-tab-overview"
      type="button"
      on:click={() => (activeTab = 'overview')}
      class="flex flex-col items-center justify-center py-1 px-3 rounded focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors {activeTab === 'overview' ? (theme === 'dark' ? 'text-[#38bdf8] bg-[#1f2433]' : 'text-[#006a61] bg-[#eff4ff]') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#45464d] hover:text-[#0b1c30]')}"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
      </svg>
      <span class="text-[11px] font-mono font-medium">Overview</span>
    </button>

    <button
      id="admin-tab-requests"
      type="button"
      on:click={() => (activeTab = 'requests')}
      class="flex flex-col items-center justify-center py-1 px-3 rounded focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors {activeTab === 'requests' ? (theme === 'dark' ? 'text-[#38bdf8] bg-[#1f2433]' : 'text-[#006a61] bg-[#eff4ff]') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#45464d] hover:text-[#0b1c30]')}"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
      </svg>
      <span class="text-[11px] font-mono font-medium">Requests</span>
    </button>

    <button
      id="admin-tab-consent"
      type="button"
      on:click={() => (activeTab = 'consent')}
      class="flex flex-col items-center justify-center py-1 px-3 rounded focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors {activeTab === 'consent' ? (theme === 'dark' ? 'text-[#38bdf8] bg-[#1f2433]' : 'text-[#006a61] bg-[#eff4ff]') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#45464d] hover:text-[#0b1c30]')}"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4" />
      </svg>
      <span class="text-[11px] font-mono font-medium">Consent</span>
    </button>

    <button
      id="admin-tab-audit"
      type="button"
      on:click={() => (activeTab = 'audit')}
      class="flex flex-col items-center justify-center py-1 px-3 rounded focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors {activeTab === 'audit' ? (theme === 'dark' ? 'text-[#38bdf8] bg-[#1f2433]' : 'text-[#006a61] bg-[#eff4ff]') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#45464d] hover:text-[#0b1c30]')}"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <span class="text-[11px] font-mono font-medium">Audit Logs</span>
    </button>
  </nav>

  <!-- Destructive Action Confirmation Modal -->
  {#if isDeletionModalOpen}
    <div
      id="deletion-confirm-modal"
      role="dialog"
      aria-modal="true"
      aria-labelledby="deletion-modal-title"
      aria-describedby="deletion-modal-description"
      class="fixed inset-0 bg-black/75 backdrop-blur-sm flex items-center justify-center p-4 z-50"
    >
      <div
        id="deletion-modal-card"
        class="max-w-md w-full p-6 rounded-2xl border shadow-2xl space-y-4 font-sans text-xs transition-colors {theme === 'dark' ? 'bg-[#0f172a] border-[#334155] text-white' : 'bg-white border-[#c6c6cd] text-[#0b1c30]'}"
      >
        <div class="flex items-center gap-3 border-b pb-3 border-red-500/30">
          <div class="p-2 rounded-full bg-red-500/20 text-red-500">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <div>
            <h2 id="deletion-modal-title" class="text-sm font-bold text-red-500">
              Confirm Destructive Erasure Request
            </h2>
            <span class="font-mono text-[10px] text-gray-400">#DSAR-8821 • Art. 17 GDPR</span>
          </div>
        </div>

        <div id="deletion-modal-description" class="space-y-2 leading-relaxed">
          <p class="font-semibold">
            Are you sure you want to execute cascading deletion for Subject <span class="font-mono text-red-400 font-bold">u_8910</span>?
          </p>
          <p class="{theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#45464d]'}">
            This destructive action will trigger permanent purge routines across customer databases, billing shards, and analytics data sinks. This operation cannot be reversed.
          </p>
        </div>

        <div class="grid grid-cols-2 gap-3 pt-2">
          <button
            id="cancel-purge-btn"
            type="button"
            on:click={closeDeletionModal}
            class="h-10 px-4 rounded-lg font-bold border focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors text-center {theme === 'dark' ? 'bg-[#1e293b] border-[#334155] text-[#cbd5e1] hover:bg-[#334155]' : 'bg-[#f1f5f9] border-[#c6c6cd] text-[#0b1c30] hover:bg-[#e2e8f0]'}"
          >
            Cancel
          </button>
          <button
            id="confirm-purge-btn"
            type="button"
            on:click={confirmDeletion}
            class="h-10 px-4 rounded-lg font-bold bg-[#ba1a1a] hover:bg-[#93000a] dark:bg-[#ef4444] dark:hover:bg-[#dc2626] text-white border border-red-500/40 focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:outline-none transition-colors text-center"
          >
            Confirm & Execute Purge
          </button>
        </div>
      </div>
    </div>
  {/if}
</div>
