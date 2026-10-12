<script lang="ts">
  import { onMount } from 'svelte';
  import { consentStore, setConsentStatus, type ConsentStatus } from '../lib/consent';

  export let theme: 'dark' | 'light' = 'dark';
  export let isOpen: boolean = false;

  let currentStatus: ConsentStatus = null;
  let analyticsEnabled: boolean = false;
  let functionalEnabled: boolean = false;
  let showDetails: boolean = false;

  const unsubscribe = consentStore.subscribe((value) => {
    currentStatus = value;
    if (value === 'accepted') {
      analyticsEnabled = true;
      functionalEnabled = true;
    } else if (value === 'declined') {
      analyticsEnabled = false;
      functionalEnabled = false;
    }
  });

  onMount(() => {
    return () => {
      unsubscribe();
    };
  });

  // Show banner if status is null (new visitor) or if isOpen is true (reopened via persistent link)
  $: showBannerOrModal = isOpen || currentStatus === null;

  function handleAcceptAll() {
    analyticsEnabled = true;
    functionalEnabled = true;
    setConsentStatus('accepted');
    isOpen = false;
  }

  function handleDeclineAll() {
    analyticsEnabled = false;
    functionalEnabled = false;
    setConsentStatus('declined');
    isOpen = false;
  }

  function handleSavePreferences() {
    if (analyticsEnabled) {
      setConsentStatus('accepted');
    } else {
      setConsentStatus('declined');
    }
    isOpen = false;
  }

  function handleClose() {
    isOpen = false;
  }

  function toggleDetails() {
    showDetails = !showDetails;
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
      class="rounded-xl border shadow-2xl p-5 font-mono text-xs space-y-4 backdrop-blur-md transition-colors duration-200 {theme === 'dark' ? 'bg-[#12151e]/98 border-[#1f2433] text-[#ffffff]' : 'bg-white/98 border-[#cbd5e1] text-[#0f172a]'}"
    >
      <!-- Header Bar -->
      <div class="flex items-center justify-between border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
        <div class="flex items-center gap-2">
          <span
            id="cmp-badge"
            class="px-2.5 py-1 rounded text-[10px] font-bold uppercase tracking-wider border {theme === 'dark' ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8]/40' : 'bg-[#e0f2fe] text-[#0369a1] border-[#0284c7]/40'}"
          >
            [PRIVACY_CMP]
          </span>
          <h2 id="cmp-title" class="font-bold text-sm tracking-tight {theme === 'dark' ? 'text-[#ffffff]' : 'text-[#0f172a]'}">
            Consent & Privacy Management
          </h2>
        </div>
        {#if currentStatus !== null}
          <button
            id="cmp-close-btn"
            type="button"
            on:click={handleClose}
            aria-label="Close CMP Preferences"
            class="px-2.5 py-1 rounded border font-bold hover:bg-opacity-80 transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 {theme === 'dark' ? 'bg-[#1f2433] border-[#2e364f] text-[#94a3b8] focus:ring-[#38bdf8] focus:ring-offset-[#12151e]' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#334155] focus:ring-[#0284c7] focus:ring-offset-white'}"
          >
            ✕
          </button>
        {/if}
      </div>

      <!-- Description & Compliance Banner -->
      <p id="cmp-description" class="leading-relaxed text-xs {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#334155]'}">
        We respect your GDPR and ePrivacy rights. Third-party telemetry and non-essential cookies are strictly blocked until explicitly authorized. Equal prominence is provided for accepting or declining tracking.
      </p>

      <!-- Status Indicator for Returning Visitors -->
      {#if currentStatus !== null}
        <div
          id="cmp-current-status-display"
          class="text-[11px] p-2.5 rounded border font-mono flex items-center justify-between {theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433] text-[#e2e8f0]' : 'bg-[#f8fafc] border-[#e2e8f0] text-[#0f172a]'}"
        >
          <span>Active Choice:</span>
          <span class="font-bold uppercase tracking-wider px-2 py-0.5 rounded text-[10px] {currentStatus === 'accepted' ? (theme === 'dark' ? 'bg-[#064e3b] text-[#6ee7b7]' : 'bg-[#dcfce7] text-[#15803d]') : (theme === 'dark' ? 'bg-[#7f1d1d] text-[#fca5a5]' : 'bg-[#fee2e2] text-[#b91c1c]')}">
            {currentStatus === 'accepted' ? 'TRACKERS_ALLOWED' : 'TRACKERS_BLOCKED'}
          </span>
        </div>
      {/if}

      <!-- Granular Category Controls Toggle Accordion -->
      <div class="space-y-2 border-t border-b py-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
        <div class="flex items-center justify-between">
          <span class="font-bold text-xs uppercase tracking-wider {theme === 'dark' ? 'text-[#e2e8f0]' : 'text-[#1e293b]'}">
            Consent Categories
          </span>
          <button
            id="cmp-toggle-details-btn"
            type="button"
            on:click={toggleDetails}
            aria-expanded={showDetails}
            aria-controls="cmp-categories-container"
            class="text-[11px] font-bold underline transition-colors focus:outline-none focus:ring-2 focus:ring-offset-1 rounded px-1 {theme === 'dark' ? 'text-[#38bdf8] focus:ring-[#38bdf8] focus:ring-offset-[#12151e]' : 'text-[#0284c7] focus:ring-[#0284c7] focus:ring-offset-white'}"
          >
            {showDetails ? 'Hide Details' : 'Configure Granular Preferences'}
          </button>
        </div>

        <div id="cmp-categories-container" class="space-y-2 pt-1 {showDetails ? 'block' : 'block'}">
          <!-- 1. Necessary (Always Active) -->
          <div class="p-2.5 rounded border flex items-center justify-between gap-3 {theme === 'dark' ? 'bg-[#090a0f]/60 border-[#1f2433]' : 'bg-[#f8fafc] border-[#e2e8f0]'}">
            <div>
              <div class="flex items-center gap-2">
                <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">Strictly Necessary</span>
                <span class="text-[9px] px-1.5 py-0.5 rounded font-bold uppercase {theme === 'dark' ? 'bg-[#1f2433] text-[#38bdf8]' : 'bg-[#e2e8f0] text-[#0369a1]'}">Required</span>
              </div>
              <p class="text-[11px] mt-0.5 {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#475569]'}">
                Security, session state, and consent preference storage.
              </p>
            </div>
            <input
              type="checkbox"
              id="cmp-toggle-necessary"
              aria-label="Strictly Necessary cookies and telemetry - Always Active"
              checked
              disabled
              class="h-4 w-4 rounded border-gray-300 text-[#0284c7] cursor-not-allowed opacity-75"
            />
          </div>

          <!-- 2. Analytics & Performance -->
          <div class="p-2.5 rounded border flex items-center justify-between gap-3 {theme === 'dark' ? 'bg-[#090a0f]/60 border-[#1f2433]' : 'bg-[#f8fafc] border-[#e2e8f0]'}">
            <label for="cmp-toggle-analytics" class="cursor-pointer flex-1">
              <div class="flex items-center gap-2">
                <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">Analytics & Performance</span>
              </div>
              <p class="text-[11px] mt-0.5 {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#475569]'}">
                Anonymized scroll depth telemetry and outbound terminal interactions.
              </p>
            </label>
            <input
              type="checkbox"
              id="cmp-toggle-analytics"
              bind:checked={analyticsEnabled}
              aria-label="Enable Analytics and Performance Telemetry"
              class="h-4 w-4 rounded border-gray-300 text-[#0284c7] focus:ring-2 focus:ring-[#38bdf8] focus:ring-offset-2 cursor-pointer"
            />
          </div>

          <!-- 3. Functional Preferences -->
          <div class="p-2.5 rounded border flex items-center justify-between gap-3 {theme === 'dark' ? 'bg-[#090a0f]/60 border-[#1f2433]' : 'bg-[#f8fafc] border-[#e2e8f0]'}">
            <label for="cmp-toggle-functional" class="cursor-pointer flex-1">
              <div class="flex items-center gap-2">
                <span class="font-bold text-xs {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">Functional Preferences</span>
              </div>
              <p class="text-[11px] mt-0.5 {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#475569]'}">
                Remembers custom theme selections and layout density across sessions.
              </p>
            </label>
            <input
              type="checkbox"
              id="cmp-toggle-functional"
              bind:checked={functionalEnabled}
              aria-label="Enable Functional Preferences"
              class="h-4 w-4 rounded border-gray-300 text-[#0284c7] focus:ring-2 focus:ring-[#38bdf8] focus:ring-offset-2 cursor-pointer"
            />
          </div>
        </div>
      </div>

      <!-- Equal Action Buttons Tray -->
      <div id="cmp-actions" class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
        <button
          id="cmp-decline-btn"
          type="button"
          on:click={handleDeclineAll}
          class="w-full py-2.5 px-4 rounded font-bold border transition-all text-center focus:outline-none focus:ring-2 focus:ring-offset-2 {theme === 'dark' ? 'bg-[#12151e] border-[#ef4444] text-[#fca5a5] hover:bg-[#ef4444]/10 focus:ring-red-400 focus:ring-offset-[#12151e]' : 'bg-red-50 border-red-400 text-[#991b1b] hover:bg-red-100 focus:ring-red-500 focus:ring-offset-white'}"
        >
          [DECLINE_TRACKERS]
        </button>

        <button
          id="cmp-accept-btn"
          type="button"
          on:click={handleAcceptAll}
          class="w-full py-2.5 px-4 rounded font-bold border transition-all text-center focus:outline-none focus:ring-2 focus:ring-offset-2 {theme === 'dark' ? 'bg-[#1f2433] border-[#38bdf8] text-[#38bdf8] hover:bg-[#38bdf8]/10 focus:ring-[#38bdf8] focus:ring-offset-[#12151e]' : 'bg-sky-50 border-[#0284c7] text-[#0369a1] hover:bg-sky-100 focus:ring-[#0284c7] focus:ring-offset-white'}"
        >
          [ACCEPT_TRACKERS]
        </button>
      </div>

      {#if showDetails}
        <div class="pt-1">
          <button
            id="cmp-save-custom-btn"
            type="button"
            on:click={handleSavePreferences}
            class="w-full py-2 px-3 rounded font-bold border text-center transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 {theme === 'dark' ? 'bg-[#1f2433] border-[#2e364f] text-[#e2e8f0] hover:bg-[#2e364f] focus:ring-[#38bdf8] focus:ring-offset-[#12151e]' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#0f172a] hover:bg-[#e2e8f0] focus:ring-[#0284c7] focus:ring-offset-white'}"
          >
            Save Custom Preferences
          </button>
        </div>
      {/if}
    </div>
  </div>
{/if}
