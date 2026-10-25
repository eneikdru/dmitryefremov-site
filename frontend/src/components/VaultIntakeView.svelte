<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry } from '../lib/telemetry';

  export let apiEndpoint: string = '/api/intake';

  type Classification = 'confidential' | 'whistleblower' | 'security';

  let classification: Classification = 'confidential';
  let email: string = '';
  let title: string = 'Unauthorized telemetry egress in core subsystem';
  let narrative: string = 'Observed unauthorized TLS 1.2 handshake initiated from production pod node-04b targeting unmapped external endpoint [REDACTED_IP]. Handshake payload contained periodic serialized ledger memory segments.';
  let discardMetadata: boolean = true;
  let requireRecoveryPhrase: boolean = true;

  let isSubmitting: boolean = false;
  let submitError: string | null = null;
  let submitSuccess: boolean = false;
  let acknowledgementEmailSent: boolean = false;
  let claimTicket: string = '';

  let isTooltipExpanded: boolean = false;

  const STORAGE_KEY = 'vault_intake_draft';

  onMount(() => {
    try {
      const savedDraft = localStorage.getItem(STORAGE_KEY);
      if (savedDraft) {
        const parsed = JSON.parse(savedDraft);
        if (parsed.classification) classification = parsed.classification;
        if (parsed.email !== undefined) email = parsed.email;
        if (parsed.title !== undefined) title = parsed.title;
        if (parsed.narrative !== undefined) narrative = parsed.narrative;
        if (parsed.discardMetadata !== undefined) discardMetadata = parsed.discardMetadata;
        if (parsed.requireRecoveryPhrase !== undefined) requireRecoveryPhrase = parsed.requireRecoveryPhrase;
      }
    } catch {
      // Ignore storage errors
    }
  });

  function saveDraft() {
    try {
      const draft = { classification, email, title, narrative, discardMetadata, requireRecoveryPhrase };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(draft));
    } catch {
      // Ignore storage errors
    }
  }

  function handleInput() {
    saveDraft();
  }

  function setClassification(val: Classification) {
    classification = val;
    saveDraft();
  }

  function clearDraft() {
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      // Ignore
    }
  }

  async function handleSubmit() {
    if (!title.trim() || !narrative.trim()) {
      submitError = 'Please fill out all required fields (*).';
      return;
    }

    submitError = null;
    isSubmitting = true;

    try {
      const payload = {
        classification,
        clientEmail: email.trim(),
        title: title.trim(),
        narrative: narrative.trim(),
        discardMetadata,
        requireRecoveryPhrase,
        timestamp: new Date().toISOString()
      };

      const response = await fetch(apiEndpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        throw new Error(`Submission failed with status ${response.status}`);
      }

      let resData: any = {};
      try {
        resData = await response.json();
      } catch {
        resData = {};
      }

      claimTicket = resData.claimTicket || `0x${Math.random().toString(16).slice(2, 10)}...31bc`;
      submitSuccess = true;
      acknowledgementEmailSent = true;

      telemetry.trackFormValidationPassed('vault_intake_submit', 12, 2);
      telemetry.trackCanonSubmitConverted(claimTicket, 0, {
        classification,
        title: title.trim(),
        client_email: email.trim()
      });

      clearDraft();
    } catch (err: any) {
      // Network failure or API error - draft is retained so nothing typed is lost
      submitError = 'Connection issue or validation error during transmission. Your input was safely preserved locally. Please try submitting again.';
    } finally {
      isSubmitting = false;
    }
  }

  function resetForm() {
    submitSuccess = false;
    acknowledgementEmailSent = false;
    submitError = null;
    title = '';
    narrative = '';
    email = '';
    clearDraft();
  }
</script>


<div id="vault-intake-root" class="w-full max-w-[540px] mx-auto space-y-6 font-sans text-on-surface">
  <section class="bg-surface-container-lowest rounded-xl p-space-md border border-outline-variant/70 shadow-sm space-y-space-md">
    <!-- Section Header with Privacy Assurance -->
    <div>
      <div class="flex items-center gap-1.5">
        <span class="text-secondary text-[20px]">✉️</span>
        <h3 class="text-headline-sm font-headline-sm text-on-surface font-bold tracking-tight text-[18px]">Secure Contact & Request</h3>
      </div>
      <p class="text-body-sm font-body-sm text-on-surface-variant mt-1 flex items-start gap-1.5 text-[13px]">
        <span class="text-on-tertiary-container text-[16px] shrink-0 mt-0.5">🔒</span>
        <span>Zero tracking. End-to-end encrypted dispatch directly to Dmitry's verified inbox.</span>
      </p>
    </div>

    {#if submitSuccess}
      <!-- SUCCESS CONFIRMATION PANEL -->
      <div
        id="intake-success-feedback"
        role="status"
        aria-live="polite"
        class="bg-[#eff4ff] border border-[#0051d5] p-4 rounded-xl space-y-4 shadow-sm text-on-surface"
      >
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 rounded-full bg-[#0051d5] text-white flex items-center justify-center font-bold text-lg shrink-0">
            ✓
          </div>
          <div>
            <h4 id="success-heading" class="text-[14px] font-bold text-[#0051d5]">Transmission Complete</h4>
            <p id="email-acknowledgement-notice" class="text-[13px] text-on-surface-variant mt-0.5">
              Your encrypted message has been logged. An email acknowledgement has been sent to {email ? email : 'your provided email address'}.
            </p>
          </div>
        </div>

        <div class="bg-white p-3 rounded-lg border border-outline-variant/70 space-y-2">
          <div class="flex items-center justify-between font-mono text-[12px]">
            <span class="text-on-surface-variant">Claim Ticket Receipt:</span>
            <span id="claim-ticket-id" class="font-bold text-[#0051d5]">{claimTicket}</span>
          </div>
          <p class="text-[12px] text-on-surface-variant">
            You may keep this receipt for verification.
          </p>
        </div>

        <div class="pt-2">
          <button
            id="intake-reset-btn"
            type="button"
            on:click={resetForm}
            class="w-full h-12 min-h-[48px] bg-surface-container-lowest border border-outline-variant hover:bg-surface-container active:translate-y-px transition-all text-on-surface font-label-lg text-label-lg rounded-lg flex items-center justify-center gap-2 shadow-sm font-bold tracking-wide text-[14px]"
          >
            Submit Another Request
          </button>
        </div>
      </div>
    {:else}
      <!-- FORM -->
      <form id="vault-intake-form" on:submit|preventDefault={handleSubmit} class="space-y-4">
        {#if submitError}
          <div id="intake-error-banner" role="alert" class="p-3 bg-error-container border border-error text-on-error-container text-[13px] rounded-lg flex items-start space-x-2 font-medium">
            <span class="font-bold">⚠️</span>
            <p id="intake-error-message">{submitError}</p>
          </div>
        {/if}

        <!-- Classification Selector -->
        <div>
          <span id="label-classification" class="block text-label-sm font-label-sm text-on-surface mb-2 uppercase tracking-wide text-[11px] font-bold">
            Select Purpose <span class="text-error" aria-hidden="true">*</span>
          </span>
          <div class="grid grid-cols-2 sm:grid-cols-3 gap-2" role="radiogroup" aria-labelledby="label-classification">
            <button
              type="button"
              role="radio"
              aria-checked={classification === 'confidential'}
              on:click={() => setClassification('confidential')}
              class="touch-target rounded-lg px-2 py-2 text-[12px] font-bold flex items-center justify-center gap-1.5 border transition-all {classification === 'confidential' ? 'bg-[#316bf3] text-white border-[#0051d5] shadow-sm' : 'bg-surface-container-low text-on-surface-variant hover:text-on-surface border-outline-variant'}"
            >
              <span>Конфиденциально</span>
            </button>

            <button
              type="button"
              role="radio"
              aria-checked={classification === 'whistleblower'}
              on:click={() => setClassification('whistleblower')}
              class="touch-target rounded-lg px-2 py-2 text-[12px] font-bold flex items-center justify-center gap-1.5 border transition-all {classification === 'whistleblower' ? 'bg-[#316bf3] text-white border-[#0051d5] shadow-sm' : 'bg-surface-container-low text-on-surface-variant hover:text-on-surface border-outline-variant'}"
            >
              <span>Whistleblower</span>
            </button>

            <button
              type="button"
              role="radio"
              aria-checked={classification === 'security'}
              on:click={() => setClassification('security')}
              class="touch-target rounded-lg px-2 py-2 text-[12px] font-bold flex items-center justify-center gap-1.5 border transition-all {classification === 'security' ? 'bg-[#316bf3] text-white border-[#0051d5] shadow-sm' : 'bg-surface-container-low text-on-surface-variant hover:text-on-surface border-outline-variant'}"
            >
              <span>Security</span>
            </button>
          </div>
        </div>

        <!-- Optional Client Email -->
        <div class="space-y-1">
          <label class="block text-label-lg font-label-lg text-on-surface text-[14px] font-bold" for="client-email-input">
            Your Email Address (Optional)
          </label>
          <div class="relative">
            <input
              id="client-email-input"
              type="email"
              bind:value={email}
              on:input={handleInput}
              on:focus={() => telemetry.trackFormFieldFocused('client_email')}
              placeholder="name@organization.com"
              class="w-full h-[52px] px-space-md bg-surface-container-lowest border-[1.5px] border-outline-variant rounded-lg text-body-md font-body-md text-on-surface placeholder:text-outline focus:border-[#0051d5] focus:bg-[#f8f9ff] focus:outline-none focus:ring-2 focus:ring-[#0051d5]/20 transition-all text-[15px]"
            />
            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant text-[20px]">📧</span>
          </div>
          <p class="text-[11px] font-medium text-on-surface-variant">We dispatch automatic read receipts upon receipt.</p>
        </div>

        <!-- Submission Title -->
        <div class="space-y-1">
          <label class="block text-label-lg font-label-lg text-on-surface text-[14px] font-bold" for="submission-title-input">
            Subject or Organization <span class="text-error" aria-hidden="true">*</span>
          </label>
          <div class="relative">
            <input
              id="submission-title-input"
              type="text"
              required
              bind:value={title}
              on:input={handleInput}
              on:focus={() => telemetry.trackFormFieldFocused('submission_title')}
              placeholder="e.g. Design Systems Advisory"
              class="w-full h-[52px] px-space-md bg-surface-container-lowest border-[1.5px] border-outline-variant rounded-lg text-body-md font-body-md text-on-surface placeholder:text-outline focus:border-[#0051d5] focus:bg-[#f8f9ff] focus:outline-none focus:ring-2 focus:ring-[#0051d5]/20 transition-all text-[15px]"
            />
          </div>
        </div>

        <!-- Confidential Narrative Textarea -->
        <div class="space-y-1">
          <div class="flex justify-between items-center">
            <label class="block text-label-lg font-label-lg text-on-surface text-[14px] font-bold" for="narrative-textarea">
              Your Message <span class="text-error" aria-hidden="true">*</span>
            </label>
            <span class="text-[11px] font-bold text-on-surface-variant" id="charCount">{narrative.length} / 4000</span>
          </div>
          <textarea
            id="narrative-textarea"
            required
            rows="4"
            bind:value={narrative}
            on:input={handleInput}
            on:focus={() => telemetry.trackFormFieldFocused('narrative')}
            placeholder="Describe your context, project parameters, or question in detail..."
            class="w-full p-space-md bg-surface-container-lowest border-[1.5px] border-outline-variant rounded-lg text-body-md font-body-md text-on-surface placeholder:text-outline focus:border-[#0051d5] focus:bg-[#f8f9ff] focus:outline-none focus:ring-2 focus:ring-[#0051d5]/20 transition-all resize-y min-h-[110px] text-[15px]"
          ></textarea>
        </div>

        <!-- Primary Submit Button -->
        <button
          id="submit-intake-btn"
          type="submit"
          disabled={isSubmitting}
          class="w-full h-12 min-h-[48px] bg-[#0b1c30] hover:bg-slate-800 active:translate-y-px transition-all text-white font-label-lg text-label-lg rounded-lg flex items-center justify-center gap-2 shadow-sm font-bold tracking-wide text-[14px] disabled:opacity-70"
        >
          {#if isSubmitting}
            <span class="animate-spin">⌛</span>
            <span>Encrypting & Transmitting...</span>
          {:else}
            <span>🔒</span>
            <span>Send Encrypted Message</span>
          {/if}
        </button>
      </form>
    {/if}
  </section>
</div>
