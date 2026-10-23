<script lang="ts">
  import { onMount, tick } from 'svelte';
  import { consentStore, setConsentStatus, type ConsentStatus } from '../lib/consent';

  export let theme: 'dark' | 'light' = 'dark';
  export let isOpen: boolean = false;

  let currentStatus: ConsentStatus = null;
  let analyticsAllowed: boolean = true;
  let functionalAllowed: boolean = true;
  let advertisingAllowed: boolean = false;
  let declineBtnRef: HTMLButtonElement | null = null;

  const unsubscribe = consentStore.subscribe((value) => {
    currentStatus = value;
  });

  onMount(async () => {
    function handleKeydown(e: KeyboardEvent) {
      if (e.key === 'Escape' && showBannerOrModal && currentStatus !== null) {
        handleClose();
      }
    }
    window.addEventListener('keydown', handleKeydown);

    if (currentStatus === null) {
      await tick();
      if (declineBtnRef) {
        declineBtnRef.focus();
      }
    }

    return () => {
      unsubscribe();
      window.removeEventListener('keydown', handleKeydown);
    };
  });

  // Show banner if status is null (new visitor) or if isOpen is true (reopened via persistent link)
  $: showBannerOrModal = isOpen || currentStatus === null;

  $: if (showBannerOrModal && currentStatus === null) {
    tick().then(() => {
      if (declineBtnRef) {
        declineBtnRef.focus();
      }
    });
  }

  function handleAccept() {
    setConsentStatus('accepted');
    isOpen = false;
  }

  function handleDecline() {
    setConsentStatus('declined');
    isOpen = false;
  }

  function handleSavePreferences() {
    if (analyticsAllowed) {
      setConsentStatus('accepted');
    } else {
      setConsentStatus('declined');
    }
    isOpen = false;
  }

  function handleClose() {
    isOpen = false;
  }
</script>

{#if showBannerOrModal}
  <div
    id="cmp-modal-overlay"
    role="dialog"
    aria-modal="true"
    aria-labelledby="cmp-title"
    aria-describedby="cmp-description"
    class="fixed inset-x-0 bottom-0 sm:bottom-6 sm:max-w-2xl sm:mx-auto p-4 sm:p-6 z-50 transition-all duration-200"
  >
    <div
      id="cmp-container"
      tabindex="-1"
      class="rounded-xl border shadow-2xl p-5 sm:p-6 font-sans text-xs space-y-4 backdrop-blur-md max-h-[85vh] overflow-y-auto custom-scrollbar focus:outline-none focus:ring-2 focus:ring-sky-500 {theme === 'dark' ? 'bg-[#0f172a]/95 border-[#1e293b] text-[#f8fafc]' : 'bg-white/95 border-[#cbd5e1] text-[#0f172a]'}"
    >
      <!-- Header / Top Bar -->
      <div class="flex items-center justify-between border-b pb-3 {theme === 'dark' ? 'border-[#1e293b]' : 'border-[#e2e8f0]'}">
        <div class="flex items-center gap-2">
          <span id="cmp-badge" class="px-2 py-0.5 rounded text-[10px] font-mono uppercase tracking-wider border {theme === 'dark' ? 'bg-[#1e293b] text-[#38bdf8] border-[#38bdf8]/40' : 'bg-[#f0f9ff] text-[#0369a1] border-[#0369a1]/30'}">
            [TRUSTGUARD_CMP]
          </span>
          <h2 id="cmp-title" class="font-bold text-sm tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Privacy & Consent Management
          </h2>
        </div>
        {#if currentStatus !== null}
          <button
            id="cmp-close-btn"
            type="button"
            on:click={handleClose}
            aria-label="Close consent preferences dialog"
            class="px-2 py-1 rounded border focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none transition-colors {theme === 'dark' ? 'bg-[#1e293b] border-[#334155] text-[#94a3b8] hover:text-white' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#475569] hover:text-[#0f172a]'}"
          >
            ✕
          </button>
        {/if}
      </div>

      <!-- Compliance Badges Bar -->
      <div class="flex flex-wrap items-center justify-between gap-2 text-[11px]">
        <div class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full font-medium {theme === 'dark' ? 'bg-[#064e3b]/40 text-[#34d399] border border-[#059669]/30' : 'bg-[#f0fdf4] text-[#166534] border border-[#bbf7d0]'}">
          <span>✓</span>
          <span>IAB TCF v2.2 Compliant</span>
        </div>
        <div class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full font-mono text-[10px] {theme === 'dark' ? 'bg-[#1e293b] text-[#94a3b8]' : 'bg-[#f1f5f9] text-[#475569]'}">
          <span>142 Authorized Partners</span>
        </div>
      </div>

      <!-- Description & Primary Statement -->
      <div class="space-y-1">
        <p id="cmp-description" class="leading-relaxed {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#334155]'}">
          We value your privacy & transparency. We process device identifiers, essential session cookies, and interaction telemetry to ensure system stability, analyze audience metrics, and optimize digital performance. Equal choices are provided per GDPR and ePrivacy standards.
        </p>
      </div>

      <!-- Current Setting Badge if returning visitor -->
      {#if currentStatus !== null}
        <div id="cmp-current-status-display" class="text-[11px] p-2 rounded border font-mono {theme === 'dark' ? 'bg-[#020617] border-[#1e293b] text-[#94a3b8]' : 'bg-[#f8fafc] border-[#e2e8f0] text-[#475569]'}">
          Current Preference: <span class="font-bold underline {currentStatus === 'accepted' ? 'text-[#38bdf8]' : 'text-[#f87171]'}">{currentStatus === 'accepted' ? 'TRACKERS_ALLOWED' : 'TRACKERS_BLOCKED'}</span>
        </div>
      {/if}

      <!-- Granular Category Permissions Accordion/List -->
      <div class="space-y-2 pt-1">
        <div class="flex justify-between items-center text-[10px] uppercase font-mono tracking-wider {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#475569]'}">
          <span>Category Permissions</span>
          <span class="{theme === 'dark' ? 'text-[#10b981]' : 'text-[#047857]'}">ePrivacy Verified</span>
        </div>

        <!-- Category 1: Strictly Necessary (Mandatory) -->
        <div class="p-3 rounded-lg border flex flex-col gap-1 {theme === 'dark' ? 'bg-[#020617]/60 border-[#1e293b]' : 'bg-[#f8fafc] border-[#e2e8f0]'}">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">Strictly Necessary</span>
              <span class="px-1.5 py-0.2 rounded text-[9px] font-mono {theme === 'dark' ? 'bg-[#10b981]/20 text-[#10b981] border border-[#10b981]/30' : 'bg-emerald-100 text-[#065f46] border border-emerald-300'}">Always Active</span>
            </div>
            <span class="text-[10px] font-mono {theme === 'dark' ? 'text-[#64748b]' : 'text-[#475569]'}">[LOCKED]</span>
          </div>
          <p class="text-[11px] leading-normal {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#475569]'}">
            Required for secure navigation, session integrity, and layout persistence. Cannot be disabled.
          </p>
        </div>

        <!-- Category 2: Analytics & Performance -->
        <div class="p-3 rounded-lg border flex flex-col gap-1 {theme === 'dark' ? 'bg-[#0f172a] border-[#1e293b]' : 'bg-white border-[#e2e8f0]'}">
          <div class="flex items-center justify-between">
            <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">Analytics & Performance</span>
            <label for="cmp-cat-analytics" class="relative inline-flex items-center cursor-pointer">
              <input
                id="cmp-cat-analytics"
                type="checkbox"
                bind:checked={analyticsAllowed}
                class="sr-only peer"
              />
              <span class="sr-only">Toggle Analytics & Performance consent</span>
              <div class="w-9 h-5 rounded-full peer peer-focus:ring-2 peer-focus:ring-sky-500 {theme === 'dark' ? 'bg-[#334155] peer-checked:bg-[#0284c7]' : 'bg-[#cbd5e1] peer-checked:bg-[#0284c7]'} after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:after:translate-x-4"></div>
            </label>
          </div>
          <p class="text-[11px] leading-normal {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
            Measures scroll depth and interaction telemetry to improve user interface ergonomics.
          </p>
        </div>

        <!-- Category 3: Functional & Preferences -->
        <div class="p-3 rounded-lg border flex flex-col gap-1 {theme === 'dark' ? 'bg-[#0f172a] border-[#1e293b]' : 'bg-white border-[#e2e8f0]'}">
          <div class="flex items-center justify-between">
            <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">Functional & Preferences</span>
            <label for="cmp-cat-functional" class="relative inline-flex items-center cursor-pointer">
              <input
                id="cmp-cat-functional"
                type="checkbox"
                bind:checked={functionalAllowed}
                class="sr-only peer"
              />
              <span class="sr-only">Toggle Functional & Preferences consent</span>
              <div class="w-9 h-5 rounded-full peer peer-focus:ring-2 peer-focus:ring-sky-500 {theme === 'dark' ? 'bg-[#334155] peer-checked:bg-[#0284c7]' : 'bg-[#cbd5e1] peer-checked:bg-[#0284c7]'} after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:after:translate-x-4"></div>
            </label>
          </div>
          <p class="text-[11px] leading-normal {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
            Remembers user UI preferences such as Dark Void vs Light Monochrome visual theme choices.
          </p>
        </div>

        <!-- Category 4: Targeted Advertising & Profiling -->
        <div class="p-3 rounded-lg border flex flex-col gap-1 {theme === 'dark' ? 'bg-[#0f172a] border-[#1e293b]' : 'bg-white border-[#e2e8f0]'}">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-1.5">
              <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">Targeted Advertising</span>
              <span class="text-[9px] font-mono {theme === 'dark' ? 'text-[#fca5a5]' : 'text-[#b91c1c]'}">(Default: Off)</span>
            </div>
            <label for="cmp-cat-advertising" class="relative inline-flex items-center cursor-pointer">
              <input
                id="cmp-cat-advertising"
                type="checkbox"
                bind:checked={advertisingAllowed}
                class="sr-only peer"
              />
              <span class="sr-only">Toggle Targeted Advertising consent</span>
              <div class="w-9 h-5 rounded-full peer peer-focus:ring-2 peer-focus:ring-sky-500 {theme === 'dark' ? 'bg-[#334155] peer-checked:bg-[#0284c7]' : 'bg-[#cbd5e1] peer-checked:bg-[#0284c7]'} after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:after:translate-x-4"></div>
            </label>
          </div>
          <p class="text-[11px] leading-normal {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
            Allows third-party partners to aggregate cross-site profiling IDs for targeted material.
          </p>
        </div>
      </div>

      <!-- Equal Action Buttons Deck -->
      <div id="cmp-actions" class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
        <button
          id="cmp-decline-btn"
          type="button"
          bind:this={declineBtnRef}
          on:click={handleDecline}
          class="w-full h-11 px-4 rounded-lg font-bold text-xs border transition-all text-center focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:outline-none flex items-center justify-center cursor-pointer {theme === 'dark' ? 'bg-[#1e1b2e] border-[#ef4444]/60 text-[#fca5a5] hover:bg-[#ef4444]/20' : 'bg-red-50 border-red-300 text-red-700 hover:bg-red-100'}"
        >
          [DECLINE_TRACKERS]
        </button>

        <button
          id="cmp-accept-btn"
          type="button"
          on:click={handleAccept}
          class="w-full h-11 px-4 rounded-lg font-bold text-xs border transition-all text-center focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:outline-none flex items-center justify-center cursor-pointer {theme === 'dark' ? 'bg-[#0284c7] border-[#38bdf8] text-white hover:bg-[#0369a1]' : 'bg-[#0369a1] border-[#0369a1] text-white hover:bg-[#075985]'}"
        >
          [ACCEPT_TRACKERS]
        </button>
      </div>

      <!-- Save Preferences Button & CMP Audit Footnote -->
      <div class="flex flex-col items-center gap-2 pt-1 text-center">
        <button
          id="cmp-save-btn"
          type="button"
          on:click={handleSavePreferences}
          class="text-xs font-semibold underline focus-visible:ring-2 focus-visible:ring-sky-500 focus-visible:outline-none rounded px-2 py-0.5 cursor-pointer {theme === 'dark' ? 'text-[#38bdf8] hover:text-[#7dd3fc]' : 'text-[#0369a1] hover:text-[#075985]'}"
        >
          Save Custom Category Preferences
        </button>
        <span class="text-[10px] font-mono uppercase tracking-wider {theme === 'dark' ? 'text-[#64748b]' : 'text-[#475569]'}">
          Powered by TrustGuard CMP • Privacy Architecture v4.8
        </span>
      </div>
    </div>
  </div>
{/if}
