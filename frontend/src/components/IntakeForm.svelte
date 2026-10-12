<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry } from '../lib/telemetry';

  let ideaText: string = '';
  let formState: 'idle' | 'step1' | 'submitting' | 'success' = 'idle';
  let startTime: number = 0;

  const formId = 'canon_lead_v1';

  function handleFocus() {
    if (formState === 'idle') {
      formState = 'step1';
      startTime = Date.now();
      telemetry.trackFormStep(formId, 1, 0, 'Form Opened');
    }
  }

  function handleSubmit(e: Event) {
    e.preventDefault();
    if (!ideaText.trim()) return;

    formState = 'submitting';
    const completionMs = Date.now() - startTime;
    telemetry.trackFormStep(formId, 2, completionMs, 'Step 1 Details Completed');

    // Mock submission process
    setTimeout(() => {
      formState = 'success';
      telemetry.trackFormStep(formId, 3, Date.now() - startTime, '200 OK • schema: verified');
    }, 800);
  }
</script>

<div id="intake-form-root" class="bg-[#090a0f] text-[#e2e8f0] p-6 sm:p-8 rounded-xl border border-[#1f2433] max-w-3xl mx-auto my-6 font-sans shadow-2xl transition-all duration-300">
  <header class="border-b border-[#1f2433] pb-4 mb-4">
    <div class="flex items-center gap-2 mb-2">
      <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono font-semibold tracking-wide">[INTAKE_TERMINAL]</span>
      <span class="text-xs font-mono text-[#94a3b8]">BLACK_BOX_ARCHITECTURE</span>
    </div>
    <h2 class="text-xl sm:text-2xl font-extrabold tracking-tight text-white">Client Intake Request</h2>
    <p class="text-xs sm:text-sm text-[#94a3b8] font-mono leading-relaxed mt-1">Input idea/wishlist → output turnkey result. NDA guaranteed.</p>
  </header>

  {#if formState === 'success'}
    <div class="bg-[#12151e] border border-[#34d399] rounded-lg p-6 text-center space-y-3">
      <span class="material-symbols-outlined text-[#34d399] text-4xl block mx-auto">check_circle</span>
      <h3 class="text-lg font-bold text-white">Transmission Verified</h3>
      <p class="text-sm text-[#94a3b8]">Your input has been securely synchronized with the Black Box Architecture.</p>
      <button
        type="button"
        on:click={() => { formState = 'idle'; ideaText = ''; }}
        class="mt-4 px-4 py-2 bg-[#1f2433] hover:bg-[#2d3748] text-white text-sm font-mono rounded border border-[#38bdf8] transition-colors"
      >
        [NEW_INTAKE]
      </button>
    </div>
  {:else}
    <form on:submit={handleSubmit} class="space-y-4">
      <div class="space-y-2">
        <label for="idea-input" class="block text-xs font-mono text-[#94a3b8] uppercase tracking-wider">Concept / Wishlist</label>
        <textarea
          id="idea-input"
          bind:value={ideaText}
          on:focus={handleFocus}
          disabled={formState === 'submitting'}
          placeholder="Describe your idea or autonomous system requirements..."
          class="w-full bg-[#12151e] border border-[#1f2433] rounded-lg p-3 text-sm text-white placeholder-[#475569] focus:outline-none focus:border-[#38bdf8] focus:ring-1 focus:ring-[#38bdf8] min-h-[120px] resize-y transition-colors"
          required
        ></textarea>
      </div>

      <div class="flex justify-end">
        <button
          type="submit"
          disabled={formState === 'submitting' || !ideaText.trim()}
          class="px-6 py-2.5 bg-[#38bdf8] hover:bg-[#0284c7] disabled:bg-[#1f2433] disabled:text-[#64748b] text-[#0f172a] font-bold text-sm font-mono rounded transition-colors flex items-center gap-2"
        >
          {#if formState === 'submitting'}
            <span class="w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin"></span>
            <span>TRANSMITTING...</span>
          {:else}
            <span>[SUBMIT_SECURELY]</span>
          {/if}
        </button>
      </div>
    </form>
  {/if}
</div>
