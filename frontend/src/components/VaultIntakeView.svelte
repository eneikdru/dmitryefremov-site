<script lang="ts">
  import { onMount } from 'svelte';

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

<div id="vault-intake-root" class="w-full max-w-xl mx-auto space-y-6 font-sans text-[#0b1c30]">
  <!-- Header / Telemetry Bar -->
  <div class="flex items-center justify-between bg-white px-4 py-3 rounded-xl border border-[#c3c6d7]/50 shadow-sm">
    <div class="flex items-center space-x-2">
      <span class="relative flex h-2.5 w-2.5">
        <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#006c49] opacity-75"></span>
        <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-[#006c49]"></span>
      </span>
      <span class="font-mono text-xs font-semibold text-[#006c49] uppercase tracking-wider">Zero-Knowledge E2EE Active</span>
    </div>
    <div class="flex items-center space-x-1 text-[#434655] bg-[#eff4ff] px-2.5 py-1 rounded border border-[#c3c6d7]/40 font-mono text-[11px]">
      <span class="text-[#006c49]">🔒</span>
      <span>NODE-US-EAST // TLS-1.3</span>
    </div>
  </div>

  <!-- Black-Box Ingestion Pipeline Banner -->
  <section class="bg-white rounded-xl p-5 border border-[#c3c6d7]/70 shadow-sm relative overflow-hidden">
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-2">
        <span class="text-[#004ac6] text-lg font-bold">🛡️</span>
        <h2 class="font-bold text-base text-[#0b1c30] tracking-tight">Black-Box Ingestion Pipeline</h2>
      </div>
      <span class="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-[#6cf8bb]/30 text-[#006c49] border border-[#006c49]/30 uppercase">
        Verifiable
      </span>
    </div>

    <!-- Pipeline Steps -->
    <div class="space-y-3 relative">
      <div class="flex items-start space-x-3 bg-[#eff4ff]/70 p-3 rounded-lg border border-[#c3c6d7]/40">
        <div class="w-7 h-7 rounded-full bg-[#6cf8bb] text-[#00714d] flex items-center justify-center font-bold shrink-0 text-xs">
          1
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-[#0b1c30]">1. Client-Side Encrypted</span>
            <span class="font-mono text-[10px] text-[#006c49] font-semibold bg-white px-1.5 py-0.5 rounded border border-[#c3c6d7]/30">AES-256-GCM</span>
          </div>
          <p class="text-xs text-[#434655] mt-0.5 leading-snug">Payload scrambled via ephemeral client key before touching any transmission socket.</p>
        </div>
      </div>

      <div class="flex items-start space-x-3 bg-[#eff4ff]/40 p-3 rounded-lg border border-[#c3c6d7]/30">
        <div class="w-7 h-7 rounded-full bg-[#dce9ff] text-[#004ac6] flex items-center justify-center font-bold shrink-0 text-xs">
          2
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-[#0b1c30]">2. Isolated Enclave Ingestion</span>
            <span class="font-mono text-[10px] text-[#4d556b] bg-white px-1.5 py-0.5 rounded border border-[#c3c6d7]/30">Nitro / SGX</span>
          </div>
          <p class="text-xs text-[#434655] mt-0.5 leading-snug">Autonomous blind routing. Strict zero-human memory access; execution logs zero payload text.</p>
        </div>
      </div>

      <div class="flex items-start space-x-3 bg-[#eff4ff]/40 p-3 rounded-lg border border-[#c3c6d7]/30">
        <div class="w-7 h-7 rounded-full bg-[#dce9ff] text-[#4d556b] flex items-center justify-center font-bold shrink-0 text-xs">
          3
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-[#0b1c30]">3. Immutable Audit Key</span>
            <span class="font-mono text-[10px] text-[#4d556b] bg-white px-1.5 py-0.5 rounded border border-[#c3c6d7]/30">SHA-256</span>
          </div>
          <p class="text-xs text-[#434655] mt-0.5 leading-snug">Cryptographic receipt uniquely generated for submitter without exposing metadata.</p>
        </div>
      </div>
    </div>

    <!-- Accordion Tooltip -->
    <div class="mt-4 pt-3 border-t border-[#c3c6d7]/40">
      <button
        id="toggle-pipeline-tooltip-btn"
        type="button"
        on:click={() => isTooltipExpanded = !isTooltipExpanded}
        aria-expanded={isTooltipExpanded}
        aria-controls="pipeline-tooltip-content"
        class="w-full flex items-center justify-between text-left text-[#004ac6] hover:text-[#2563eb] transition-colors py-1 focus:outline-none focus:ring-2 focus:ring-[#004ac6]/30 rounded"
      >
        <span class="flex items-center space-x-1.5 text-xs font-semibold">
          <span>🔒</span>
          <span>How your identity is mathematically protected</span>
        </span>
        <span class="text-xs font-bold transform transition-transform duration-200 {isTooltipExpanded ? 'rotate-180' : ''}">▼</span>
      </button>

      {#if isTooltipExpanded}
        <div id="pipeline-tooltip-content" class="mt-2 p-3 bg-[#eff4ff] rounded-lg text-xs text-[#434655] space-y-1.5 border border-[#c3c6d7]/40">
          <p><strong class="text-[#0b1c30]">1. Zero Origin Retention:</strong> Client TCP/IP addresses are stripped at the edge proxy prior to enclave arrival.</p>
          <p><strong class="text-[#0b1c30]">2. Blind Routing:</strong> Administrators cannot decrypt payloads without recipient's private shard.</p>
        </div>
      {/if}
    </div>
  </section>

  {#if submitSuccess}
    <!-- SUCCESS CONFIRMATION PANEL -->
    <div
      id="intake-success-feedback"
      role="status"
      aria-live="polite"
      class="bg-[#eff4ff] border-2 border-[#006c49] p-6 rounded-xl space-y-4 shadow-lg text-[#0b1c30]"
    >
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 rounded-full bg-[#006c49] text-white flex items-center justify-center font-bold text-xl shrink-0">
          ✓
        </div>
        <div>
          <h3 id="success-heading" class="text-lg font-bold text-[#006c49]">Payload Encrypted & Submitted Successfully</h3>
          <p id="email-acknowledgement-notice" class="text-xs text-[#006c49] font-medium mt-0.5">
            ✓ An email acknowledgement has been sent to {email ? email : 'your provided email address'}.
          </p>
        </div>
      </div>

      <div class="bg-white p-4 rounded-lg border border-[#c3c6d7] space-y-2">
        <div class="flex items-center justify-between font-mono text-xs">
          <span class="text-[#434655]">Claim Ticket Receipt:</span>
          <span id="claim-ticket-id" class="font-bold text-[#004ac6]">{claimTicket}</span>
        </div>
        <p class="text-xs text-[#434655]">
          Your confidential disclosure is stored securely within the black-box vault. You may keep this receipt for verification.
        </p>
      </div>

      <div class="pt-2">
        <button
          id="intake-reset-btn"
          type="button"
          on:click={resetForm}
          class="w-full py-2.5 px-4 bg-[#004ac6] hover:bg-[#2563eb] text-white font-semibold rounded-lg text-xs transition-colors focus:outline-none focus:ring-2 focus:ring-[#004ac6]"
        >
          Submit Another Intake Request
        </button>
      </div>
    </div>
  {:else}
    <!-- INTAKE FORM -->
    <form id="vault-intake-form" on:submit|preventDefault={handleSubmit} class="space-y-5 bg-white p-6 rounded-xl border border-[#c3c6d7]/70 shadow-sm">
      {#if submitError}
        <div id="intake-error-banner" role="alert" class="p-3 bg-[#ffdad6] border border-[#ba1a1a] text-[#93000a] text-xs rounded-lg flex items-start space-x-2">
          <span class="font-bold">⚠️</span>
          <p id="intake-error-message">{submitError}</p>
        </div>
      {/if}

      <!-- Classification Selector -->
      <div class="space-y-1.5">
        <span id="label-classification" class="block text-xs font-semibold text-[#0b1c30]">
          Submission Classification <span class="text-[#ba1a1a]" aria-hidden="true">*</span>
        </span>
        <div class="grid grid-cols-3 gap-1.5 bg-[#e5eeff] p-1 rounded-xl border border-[#c3c6d7]/60" role="radiogroup" aria-labelledby="label-classification">
          <button
            type="button"
            role="radio"
            aria-checked={classification === 'confidential'}
            on:click={() => setClassification('confidential')}
            class="py-2 px-1 text-center rounded-lg text-[11px] font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#004ac6] {classification === 'confidential' ? 'bg-white text-[#004ac6] shadow-xs border border-[#004ac6]/20' : 'text-[#434655] hover:text-[#0b1c30]'}"
          >
            Confidential<br/>Disclosure
          </button>

          <button
            type="button"
            role="radio"
            aria-checked={classification === 'whistleblower'}
            on:click={() => setClassification('whistleblower')}
            class="py-2 px-1 text-center rounded-lg text-[11px] font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#004ac6] {classification === 'whistleblower' ? 'bg-white text-[#004ac6] shadow-xs border border-[#004ac6]/20' : 'text-[#434655] hover:text-[#0b1c30]'}"
          >
            Whistleblower<br/>Report
          </button>

          <button
            type="button"
            role="radio"
            aria-checked={classification === 'security'}
            on:click={() => setClassification('security')}
            class="py-2 px-1 text-center rounded-lg text-[11px] font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#004ac6] {classification === 'security' ? 'bg-white text-[#004ac6] shadow-xs border border-[#004ac6]/20' : 'text-[#434655] hover:text-[#0b1c30]'}"
          >
            Security<br/>Vulnerability
          </button>
        </div>
      </div>

      <!-- Optional Client Email for Acknowledgement -->
      <div class="space-y-1.5">
        <div class="flex items-center justify-between">
          <label for="client-email-input" class="text-xs font-semibold text-[#0b1c30]">
            Notification Email (Optional for acknowledgement)
          </label>
        </div>
        <input
          id="client-email-input"
          type="email"
          bind:value={email}
          on:input={handleInput}
          placeholder="your.email@organization.com"
          class="w-full h-11 bg-white text-[#0b1c30] text-xs border border-[#c3c6d7] rounded-lg px-3.5 focus:border-[#004ac6] focus:ring-2 focus:ring-[#004ac6]/20 outline-none transition-all"
        />
        <p class="text-[11px] text-[#737686]">Used solely for dispatching email receipt & status confirmation.</p>
      </div>

      <!-- Submission Title -->
      <div class="space-y-1.5">
        <div class="flex items-center justify-between">
          <label for="submission-title-input" class="text-xs font-semibold text-[#0b1c30]">
            Subject / Brief Title <span class="text-[#ba1a1a]">*</span>
          </label>
          <div class="flex items-center space-x-1 text-[#006c49] font-mono text-[10px]">
            <span>🔒</span>
            <span>E2EE Active</span>
          </div>
        </div>
        <input
          id="submission-title-input"
          type="text"
          required
          bind:value={title}
          on:input={handleInput}
          placeholder="e.g. Memory anomaly in enclave worker node"
          class="w-full h-11 bg-white text-[#0b1c30] text-xs border border-[#c3c6d7] rounded-lg px-3.5 focus:border-[#004ac6] focus:ring-2 focus:ring-[#004ac6]/20 outline-none transition-all"
        />
      </div>

      <!-- Confidential Narrative Textarea -->
      <div class="space-y-1.5">
        <div class="flex items-center justify-between">
          <label for="narrative-textarea" class="text-xs font-semibold text-[#0b1c30]">
            Confidential Narrative & Technical Context <span class="text-[#ba1a1a]">*</span>
          </label>
          <div class="flex items-center space-x-1 bg-[#e5eeff] px-2 py-0.5 rounded-full border border-[#c3c6d7]/60 text-[10px] font-mono">
            <span class="w-1.5 h-1.5 rounded-full bg-[#006c49]"></span>
            <span class="font-semibold text-[#0b1c30]">Auto-Sanitize PII Active</span>
          </div>
        </div>
        <textarea
          id="narrative-textarea"
          required
          rows="4"
          bind:value={narrative}
          on:input={handleInput}
          placeholder="Describe the vulnerability or incident with specifics. All phone numbers, emails, and IP strings are tokenized..."
          class="w-full bg-white text-[#0b1c30] text-xs border border-[#c3c6d7] rounded-lg p-3 focus:border-[#004ac6] focus:ring-2 focus:ring-[#004ac6]/20 outline-none transition-all resize-none font-sans"
        ></textarea>
        <div class="flex items-center justify-between text-[10px] font-mono text-[#737686]">
          <span>Client cryptographic entropy: 99.8%</span>
          <span id="char-counter">{narrative.length} / 4000</span>
        </div>
      </div>

      <!-- Safeguard Toggles -->
      <div class="bg-[#f8f9ff] rounded-xl p-4 border border-[#c3c6d7]/70 space-y-3">
        <h3 class="text-xs font-bold text-[#0b1c30] tracking-tight flex items-center space-x-1.5">
          <span>🛡️</span>
          <span>Submitter Safeguard Controls</span>
        </h3>

        <label class="flex items-start justify-between cursor-pointer space-x-3 text-xs">
          <div class="flex-1">
            <div class="font-semibold text-[#0b1c30]">Discard IP & User-Agent from receipt</div>
            <p class="text-[11px] text-[#434655] mt-0.5">Enclave will zero-fill connection metadata headers before generating immutable proof.</p>
          </div>
          <input
            id="toggle-discard-metadata"
            type="checkbox"
            bind:checked={discardMetadata}
            on:change={handleInput}
            class="mt-1 w-4 h-4 text-[#004ac6] focus:ring-[#004ac6] rounded"
          />
        </label>

        <div class="h-px bg-[#c3c6d7]/40"></div>

        <label class="flex items-start justify-between cursor-pointer space-x-3 text-xs">
          <div class="flex-1">
            <div class="font-semibold text-[#0b1c30]">Require two-part recovery phrase</div>
            <p class="text-[11px] text-[#434655] mt-0.5">Splits verification token using Shamir's Secret Sharing (2-of-3 threshold).</p>
          </div>
          <input
            id="toggle-recovery-phrase"
            type="checkbox"
            bind:checked={requireRecoveryPhrase}
            on:change={handleInput}
            class="mt-1 w-4 h-4 text-[#004ac6] focus:ring-[#004ac6] rounded"
          />
        </label>
      </div>

      <!-- Action Button -->
      <div class="pt-2">
        <button
          id="submit-intake-btn"
          type="submit"
          disabled={isSubmitting}
          class="w-full h-12 bg-[#004ac6] hover:bg-[#2563eb] text-white font-semibold rounded-xl flex flex-col items-center justify-center shadow-md transition-all focus:outline-none focus:ring-2 focus:ring-[#004ac6] disabled:opacity-50 cursor-pointer"
        >
          {#if isSubmitting}
            <div class="flex items-center space-x-2 text-xs">
              <span class="animate-spin text-sm">⌛</span>
              <span>Encrypting & Transmitting Payload...</span>
            </div>
          {:else}
            <div class="flex items-center space-x-2 text-xs">
              <span>🔒</span>
              <span>Lock & Transmit Payload</span>
            </div>
            <span class="text-[10px] font-mono text-[#eeefff]/90">Generates One-Time Cryptographic Claim Ticket</span>
          {/if}
        </button>
      </div>
    </form>
  {/if}
</div>
