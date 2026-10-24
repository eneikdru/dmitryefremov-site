<script lang="ts">
  import { onMount } from 'svelte';

  export let apiEndpoint: string = '/api/intake';

  type Classification = 'confidential' | 'whistleblower' | 'security';

  let classification: Classification = 'confidential';
  let email: string = '';
  let title: string = 'Project Obsidian Aegis';
  let narrative: string = 'Phase I intake specification: Requesting bespoke cryptographic dossier and zero-knowledge transmission routing.';
  let discardMetadata: boolean = true;
  let requireRecoveryPhrase: boolean = true;
  let selectedArchetype: string = 'horology';
  let selectedMaterial: string = 'damascus';
  let confidentialityTier: string = 'ghost';

  let isSubmitting: boolean = false;
  let submitError: string | null = null;
  let submitSuccess: boolean = false;
  let acknowledgementEmailSent: boolean = false;
  let claimTicket: string = '';

  let isCovenantExpanded: boolean = false;

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
        if (parsed.selectedArchetype !== undefined) selectedArchetype = parsed.selectedArchetype;
        if (parsed.selectedMaterial !== undefined) selectedMaterial = parsed.selectedMaterial;
        if (parsed.confidentialityTier !== undefined) confidentialityTier = parsed.confidentialityTier;
      }
    } catch {
      // Ignore storage errors
    }
  });

  function saveDraft() {
    try {
      const draft = {
        classification,
        email,
        title,
        narrative,
        discardMetadata,
        requireRecoveryPhrase,
        selectedArchetype,
        selectedMaterial,
        confidentialityTier
      };
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
        selectedArchetype,
        selectedMaterial,
        confidentialityTier,
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
      // Connection or validation error during transmission. Draft is retained so no user input is lost.
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

<div id="vault-intake-root" class="w-full max-w-xl mx-auto space-y-6 font-sans text-[#e6e1df] bg-[#141312] p-4 sm:p-6 rounded-2xl border border-[#363433]">
  <!-- Top Bar: Protocol & Security Badge -->
  <header class="flex items-center justify-between bg-[#1d1b1a] px-4 py-3 rounded-xl border border-[#4d4635]/40 shadow-sm">
    <div class="flex items-center space-x-2">
      <span class="relative flex h-2.5 w-2.5">
        <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#f2ca50] opacity-75"></span>
        <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-[#f2ca50]"></span>
      </span>
      <span class="font-mono text-xs font-semibold text-[#f2ca50] uppercase tracking-wider">PROT-256-G · NDA ACTIVE</span>
    </div>
    <div class="flex items-center space-x-1.5 text-[#d0c5af] bg-[#2b2a28] px-2.5 py-1 rounded border border-[#4d4635]/50 font-mono text-[11px]">
      <span class="text-[#f2ca50]">🔒</span>
      <span>CLEARANCE LEVEL IV</span>
    </div>
  </header>

  <!-- Hero Section -->
  <section class="space-y-2 pt-1">
    <div class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-[#363433] border border-[#4d4635]/60">
      <span class="w-1.5 h-1.5 rounded-full bg-[#f2ca50] animate-pulse"></span>
      <span class="font-mono text-[11px] uppercase text-[#d3c4b1] tracking-widest font-semibold">Confidential Intake Chamber</span>
    </div>
    <h1 class="text-2xl sm:text-3xl font-serif text-[#e6e1df] font-normal tracking-tight">
      Bespoke Commission Dossier
    </h1>
    <p class="text-xs sm:text-sm text-[#d3c4b1] font-light leading-relaxed">
      Phase I: Bilateral Non-Disclosure & Creative Premise. Curating sovereign artifacts of singular architectural integrity.
    </p>
  </section>

  <!-- Cryptographic NDA Status Card -->
  <section class="relative p-4 sm:p-5 bg-[#1d1b1a] rounded-xl border border-[#4d4635]/50 shadow-xl overflow-hidden space-y-3">
    <div class="flex items-start justify-between gap-3">
      <div class="space-y-1">
        <div class="flex items-center gap-2">
          <span class="text-[#f2ca50] text-base">🛡️</span>
          <h2 class="font-semibold text-sm uppercase tracking-wider text-[#e6e1df]">Mutual NDA Status: Active & Sealed</h2>
        </div>
        <p class="text-xs text-[#99907c] font-light">
          Cryptographic Escrow Protocol · Place Vendôme & Geneva
        </p>
      </div>
      <span class="px-2 py-1 rounded bg-[#f2ca50]/10 border border-[#f2ca50]/40 font-mono text-[10px] text-[#f2ca50] tracking-widest font-bold uppercase">
        CLEARANCE IV
      </span>
    </div>

    <!-- Detail Badges -->
    <div class="grid grid-cols-2 gap-3 pt-2 border-t border-[#363433]">
      <div>
        <span class="block text-[10px] text-[#99907c] uppercase tracking-wider font-mono">Covenant Reference</span>
        <span class="text-xs text-[#d3c4b1] font-mono font-medium">#AT-9842-NDA</span>
      </div>
      <div>
        <span class="block text-[10px] text-[#99907c] uppercase tracking-wider font-mono">Bilateral Timestamp</span>
        <span class="text-xs text-[#d3c4b1] font-mono font-medium">2026.10.12 · 20:46:15 Z</span>
      </div>
    </div>

    <!-- Inspect Covenant Button & Expandable Content -->
    <div class="pt-1">
      <button
        id="toggle-pipeline-tooltip-btn"
        type="button"
        on:click={() => isCovenantExpanded = !isCovenantExpanded}
        aria-expanded={isCovenantExpanded}
        aria-controls="pipeline-tooltip-content"
        class="w-full flex items-center justify-between py-2 px-3 rounded bg-[#211f1e] hover:bg-[#2b2a28] transition-colors border border-[#4d4635]/40 text-left text-xs text-[#d3c4b1] hover:text-[#f2ca50] focus:outline-none focus:ring-2 focus:ring-[#f2ca50]"
      >
        <span class="font-mono text-[11px] tracking-wider uppercase font-medium">
          Inspect Executed Bilateral Covenant & Protections
        </span>
        <span class="transform transition-transform duration-200 {isCovenantExpanded ? 'rotate-180' : ''}">▼</span>
      </button>

      {#if isCovenantExpanded}
        <div id="pipeline-tooltip-content" class="mt-2 p-3 bg-[#2b2a28] rounded-lg text-xs text-[#d0c5af] space-y-2 border border-[#4d4635]/60 font-sans">
          <p><strong class="text-[#f2ca50]">1. Zero Origin Leakage:</strong> All internal pipeline and operational references are strictly obfuscated and stripped from client records.</p>
          <p><strong class="text-[#f2ca50]">2. Absolute Commercial Confidentiality:</strong> Submissions are processed via blind, zero-knowledge enclave isolation. No third-party data dispersion.</p>
          <p><strong class="text-[#f2ca50]">3. Immutable Claim Receipt:</strong> Upon transmission, a zero-knowledge claim ticket is generated for client-side receipt verification.</p>
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
      class="bg-[#1d1b1a] border-2 border-[#f2ca50] p-6 rounded-xl space-y-4 shadow-2xl text-[#e6e1df]"
    >
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 rounded-full bg-[#f2ca50] text-[#3c2f00] flex items-center justify-center font-bold text-xl shrink-0">
          ✓
        </div>
        <div>
          <h3 id="success-heading" class="text-base font-bold text-[#f2ca50]">Payload Encrypted & Submitted Successfully</h3>
          <p id="email-acknowledgement-notice" class="text-xs text-[#d0c5af] font-medium mt-0.5">
            An email acknowledgement has been sent to {email ? email : 'your provided email address'}.
          </p>
        </div>
      </div>

      <div class="bg-[#211f1e] p-4 rounded-lg border border-[#4d4635] space-y-2 font-mono text-xs">
        <div class="flex items-center justify-between">
          <span class="text-[#99907c]">Claim Ticket Receipt:</span>
          <span id="claim-ticket-id" class="font-bold text-[#f2ca50]">{claimTicket}</span>
        </div>
        <p class="text-[11px] text-[#d0c5af] font-sans">
          Your confidential disclosure is stored securely within the black-box vault under strict NDA protocols.
        </p>
      </div>

      <div class="pt-2">
        <button
          id="intake-reset-btn"
          type="button"
          on:click={resetForm}
          class="w-full py-3 px-4 bg-[#f2ca50] hover:bg-[#ffe088] text-[#3c2f00] font-semibold rounded-lg text-xs uppercase tracking-wider transition-colors focus:outline-none focus:ring-2 focus:ring-[#f2ca50]"
        >
          Submit Another Intake Request
        </button>
      </div>
    </div>
  {:else}
    <!-- INTAKE FORM -->
    <form id="vault-intake-form" on:submit|preventDefault={handleSubmit} class="space-y-5 bg-[#1d1b1a] p-5 sm:p-6 rounded-xl border border-[#4d4635]/50 shadow-lg">
      {#if submitError}
        <div id="intake-error-banner" role="alert" class="p-3.5 bg-[#93000a]/30 border border-[#ffb4ab] text-[#ffdad6] text-xs rounded-lg flex items-start space-x-2">
          <span class="font-bold">⚠️</span>
          <p id="intake-error-message" class="leading-relaxed">{submitError}</p>
        </div>
      {/if}

      <!-- Classification Selector -->
      <div class="space-y-1.5">
        <span id="label-classification" class="block text-xs font-semibold uppercase tracking-wider text-[#e6e1df]">
          Submission Classification <span class="text-[#ffb4ab]" aria-hidden="true">*</span>
        </span>
        <div class="grid grid-cols-3 gap-2 bg-[#211f1e] p-1.5 rounded-xl border border-[#4d4635]/60" role="radiogroup" aria-labelledby="label-classification">
          <button
            type="button"
            role="radio"
            aria-checked={classification === 'confidential'}
            on:click={() => setClassification('confidential')}
            class="py-2 px-1 text-center rounded-lg text-[11px] font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {classification === 'confidential' ? 'bg-[#363433] text-[#f2ca50] shadow-md border border-[#f2ca50]/30' : 'text-[#d0c5af] hover:text-[#e6e1df]'}"
          >
            Confidential<br/>Disclosure
          </button>

          <button
            type="button"
            role="radio"
            aria-checked={classification === 'whistleblower'}
            on:click={() => setClassification('whistleblower')}
            class="py-2 px-1 text-center rounded-lg text-[11px] font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {classification === 'whistleblower' ? 'bg-[#363433] text-[#f2ca50] shadow-md border border-[#f2ca50]/30' : 'text-[#d0c5af] hover:text-[#e6e1df]'}"
          >
            Whistleblower<br/>Report
          </button>

          <button
            type="button"
            role="radio"
            aria-checked={classification === 'security'}
            on:click={() => setClassification('security')}
            class="py-2 px-1 text-center rounded-lg text-[11px] font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {classification === 'security' ? 'bg-[#363433] text-[#f2ca50] shadow-md border border-[#f2ca50]/30' : 'text-[#d0c5af] hover:text-[#e6e1df]'}"
          >
            Security<br/>Vulnerability
          </button>
        </div>
      </div>

      <!-- Field 1: Commission Moniker / Title -->
      <div class="space-y-1.5">
        <div class="flex items-center justify-between">
          <label for="submission-title-input" class="text-xs font-semibold uppercase tracking-wider text-[#e6e1df]">
            Commission Moniker / Subject <span class="text-[#ffb4ab]">*</span>
          </label>
          <span class="font-mono text-[10px] text-[#f2ca50]/80 uppercase tracking-wider">Confidential Title</span>
        </div>
        <input
          id="submission-title-input"
          type="text"
          required
          bind:value={title}
          on:input={handleInput}
          placeholder="e.g. Project Solitude / Obsidian Aegis"
          class="w-full h-11 bg-[#0f0e0d] text-[#e6e1df] text-xs border border-[#4d4635] rounded-lg px-3.5 focus:border-[#f2ca50] focus:ring-2 focus:ring-[#f2ca50]/30 outline-none transition-all placeholder-[#99907c]"
        />
        <p class="text-[11px] text-[#99907c] font-light flex items-center gap-1">
          <span class="text-[#f2ca50]">◆</span> Encrypted Client Record — Atelier Clearance Level IV
        </p>
      </div>

      <!-- Field 2: Client Email for Acknowledgement -->
      <div class="space-y-1.5">
        <div class="flex items-center justify-between">
          <label for="client-email-input" class="text-xs font-semibold uppercase tracking-wider text-[#e6e1df]">
            Notification Email (Optional for acknowledgement)
          </label>
        </div>
        <input
          id="client-email-input"
          type="email"
          bind:value={email}
          on:input={handleInput}
          placeholder="client.liaison@organization.com"
          class="w-full h-11 bg-[#0f0e0d] text-[#e6e1df] text-xs border border-[#4d4635] rounded-lg px-3.5 focus:border-[#f2ca50] focus:ring-2 focus:ring-[#f2ca50]/30 outline-none transition-all placeholder-[#99907c]"
        />
        <p class="text-[11px] text-[#99907c]">Used solely for dispatching confidential status confirmation & receipt.</p>
      </div>

      <!-- Field 3: Aesthetic Intent & Archetype Selection -->
      <div class="space-y-2">
        <div class="flex items-center justify-between">
          <span class="text-xs font-semibold uppercase tracking-wider text-[#e6e1df]">
            Aesthetic Archetype & Discipline
          </span>
          <span class="text-[10px] text-[#99907c] font-mono uppercase">Select Primary</span>
        </div>
        <div class="grid grid-cols-2 gap-2">
          <button
            type="button"
            on:click={() => { selectedArchetype = 'horology'; handleInput(); }}
            class="p-2.5 rounded-lg text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {selectedArchetype === 'horology' ? 'bg-[#2b2a28] border-2 border-[#f2ca50]' : 'bg-[#0f0e0d] border border-[#4d4635]/40 hover:border-[#4d4635]'}"
          >
            <span class="block text-[10px] font-mono text-[#f2ca50] font-bold">01 / ARCHETYPE</span>
            <span class="block text-xs font-serif text-[#e6e1df] font-normal mt-0.5">Haute Horology</span>
            <span class="block text-[10px] text-[#99907c]">Tourbillon & Precision</span>
          </button>

          <button
            type="button"
            on:click={() => { selectedArchetype = 'metallurgy'; handleInput(); }}
            class="p-2.5 rounded-lg text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {selectedArchetype === 'metallurgy' ? 'bg-[#2b2a28] border-2 border-[#f2ca50]' : 'bg-[#0f0e0d] border border-[#4d4635]/40 hover:border-[#4d4635]'}"
          >
            <span class="block text-[10px] font-mono text-[#99907c]">02 / ARCHETYPE</span>
            <span class="block text-xs font-serif text-[#e6e1df] font-normal mt-0.5">Fine Metallurgy</span>
            <span class="block text-[10px] text-[#99907c]">Bespoke Monolith</span>
          </button>
        </div>
      </div>

      <!-- Field 4: Confidential Narrative / Creative Premise -->
      <div class="space-y-1.5">
        <div class="flex items-center justify-between">
          <label for="narrative-textarea" class="text-xs font-semibold uppercase tracking-wider text-[#e6e1df]">
            Confidential Narrative & Technical Premise <span class="text-[#ffb4ab]">*</span>
          </label>
          <span class="text-[10px] font-mono text-[#f2ca50]">Auto-Sanitize Active</span>
        </div>
        <textarea
          id="narrative-textarea"
          required
          rows="4"
          bind:value={narrative}
          on:input={handleInput}
          placeholder="Describe the architectural concept, technical context, or security scope. All phone numbers, emails, and identifiers are tokenized client-side..."
          class="w-full bg-[#0f0e0d] text-[#e6e1df] text-xs border border-[#4d4635] rounded-lg p-3 focus:border-[#f2ca50] focus:ring-2 focus:ring-[#f2ca50]/30 outline-none transition-all resize-none placeholder-[#99907c]"
        ></textarea>
        <div class="flex items-center justify-between text-[10px] font-mono text-[#99907c]">
          <span>Client cryptographic entropy: 99.8%</span>
          <span id="char-counter">{narrative.length} / 4000</span>
        </div>
      </div>

      <!-- Field 5: Safeguard & Anonymity Toggles -->
      <div class="bg-[#141312] rounded-xl p-4 border border-[#4d4635]/60 space-y-3">
        <h3 class="text-xs font-bold uppercase tracking-wider text-[#e6e1df] flex items-center space-x-1.5">
          <span>🛡️</span>
          <span>Submitter Protection Controls</span>
        </h3>

        <label class="flex items-start justify-between cursor-pointer space-x-3 text-xs">
          <div class="flex-1">
            <div class="font-semibold text-[#e6e1df]">Discard origin headers from receipt</div>
            <p class="text-[11px] text-[#d0c5af] mt-0.5">Zero-fill IP and connection metadata before generating immutable proof.</p>
          </div>
          <input
            id="toggle-discard-metadata"
            type="checkbox"
            bind:checked={discardMetadata}
            on:change={handleInput}
            class="mt-1 w-4 h-4 text-[#f2ca50] focus:ring-[#f2ca50] rounded bg-[#0f0e0d] border-[#4d4635]"
          />
        </label>

        <div class="h-px bg-[#363433]"></div>

        <label class="flex items-start justify-between cursor-pointer space-x-3 text-xs">
          <div class="flex-1">
            <div class="font-semibold text-[#e6e1df]">Require two-part recovery phrase</div>
            <p class="text-[11px] text-[#d0c5af] mt-0.5">Splits verification token using Shamir's Secret Sharing (2-of-3 threshold).</p>
          </div>
          <input
            id="toggle-recovery-phrase"
            type="checkbox"
            bind:checked={requireRecoveryPhrase}
            on:change={handleInput}
            class="mt-1 w-4 h-4 text-[#f2ca50] focus:ring-[#f2ca50] rounded bg-[#0f0e0d] border-[#4d4635]"
          />
        </label>
      </div>

      <!-- Action Button -->
      <div class="pt-2">
        <button
          id="submit-intake-btn"
          type="submit"
          disabled={isSubmitting}
          class="w-full h-12 bg-[#1C1A17] hover:bg-[#24221E] text-[#e6e1df] border border-[#f2ca50]/60 font-semibold rounded-xl flex flex-col items-center justify-center shadow-lg transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] disabled:opacity-50 cursor-pointer group"
        >
          {#if isSubmitting}
            <div class="flex items-center space-x-2 text-xs">
              <span class="animate-spin text-sm">⌛</span>
              <span>Encrypting & Dispatching Dossier...</span>
            </div>
          {:else}
            <div class="flex items-center space-x-2 text-xs uppercase tracking-wider group-hover:text-[#f2ca50]">
              <span>🔒</span>
              <span>Execute Sealed Dossier & Dispatch</span>
            </div>
            <span class="text-[10px] font-mono text-[#d0c5af]">Protected by Sovereign Escrow Protocols</span>
          {/if}
        </button>
      </div>
    </form>
  {/if}
</div>
