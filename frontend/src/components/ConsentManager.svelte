<script lang="ts">
  import { onMount } from 'svelte';
  import { consentStore, setConsentStatus, type ConsentStatus } from '../lib/consent';

  export let theme: 'dark' | 'light' = 'dark';
  export let isOpen: boolean = false;

  let currentStatus: ConsentStatus = null;

  const unsubscribe = consentStore.subscribe((value) => {
    currentStatus = value;
  });

  onMount(() => {
    return () => {
      unsubscribe();
    };
  });

  // Show banner if status is null (new visitor) or if isOpen is true (reopened via persistent link)
  $: showBannerOrModal = isOpen || currentStatus === null;

  function handleAccept() {
    setConsentStatus('accepted');
    isOpen = false;
  }

  function handleDecline() {
    setConsentStatus('declined');
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
      class="rounded-lg border shadow-2xl p-5 font-mono text-xs space-y-4 backdrop-blur-md {theme === 'dark' ? 'bg-[#12151e]/95 border-[#1f2433] text-[#e2e8f0]' : 'bg-white/95 border-[#cbd5e1] text-[#0f172a]'}"
    >
      <!-- Header -->
      <div class="flex items-center justify-between border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
        <div class="flex items-center gap-2">
          <span id="cmp-badge" class="px-2 py-0.5 rounded text-[10px] uppercase border {theme === 'dark' ? 'bg-[#1f2433] text-[#38bdf8] border-[#38bdf8]/30' : 'bg-[#f1f5f9] text-[#0284c7] border-[#cbd5e1]'}">
            [PRIVACY_CMP]
          </span>
          <h2 id="cmp-title" class="font-bold text-sm tracking-tight">
            Consent & Privacy Management
          </h2>
        </div>
        {#if currentStatus !== null}
          <button
            id="cmp-close-btn"
            type="button"
            on:click={handleClose}
            aria-label="Close CMP Preferences"
            class="px-2 py-1 rounded border hover:bg-opacity-80 transition-colors {theme === 'dark' ? 'bg-[#1f2433] border-[#2e364f] text-[#94a3b8]' : 'bg-[#f1f5f9] border-[#cbd5e1] text-[#64748b]'}"
          >
            ✕
          </button>
        {/if}
      </div>

      <!-- Description -->
      <p id="cmp-description" class="leading-relaxed {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
        We respect your privacy choices. Select whether to allow essential scroll depth and interaction telemetry to help improve system ergonomics. Equal options are provided in accordance with GDPR and deontic privacy standards.
      </p>

      <!-- Status indicator if returning visitor -->
      {#if currentStatus !== null}
        <div id="cmp-current-status-display" class="text-[11px] p-2 rounded border {theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433]' : 'bg-[#f8fafc] border-[#e2e8f0]'}">
          Current Setting: <span class="font-bold underline">{currentStatus === 'accepted' ? 'TRACKERS_ALLOWED' : 'TRACKERS_BLOCKED'}</span>
        </div>
      {/if}

      <!-- Equal Action Buttons -->
      <div id="cmp-actions" class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
        <button
          id="cmp-decline-btn"
          type="button"
          on:click={handleDecline}
          class="w-full py-2.5 px-4 rounded font-bold border transition-all text-center focus:outline-none focus:ring-2 focus:ring-red-500/50 {theme === 'dark' ? 'bg-[#12151e] border-[#ef4444]/60 text-[#fca5a5] hover:bg-[#ef4444]/10' : 'bg-red-50 border-red-300 text-red-700 hover:bg-red-100'}"
        >
          [DECLINE_TRACKERS]
        </button>

        <button
          id="cmp-accept-btn"
          type="button"
          on:click={handleAccept}
          class="w-full py-2.5 px-4 rounded font-bold border transition-all text-center focus:outline-none focus:ring-2 focus:ring-[#38bdf8]/50 {theme === 'dark' ? 'bg-[#1f2433] border-[#38bdf8] text-[#38bdf8] hover:bg-[#38bdf8]/10' : 'bg-sky-50 border-sky-400 text-sky-800 hover:bg-sky-100'}"
        >
          [ACCEPT_TRACKERS]
        </button>
      </div>
    </div>
  </div>
{/if}
