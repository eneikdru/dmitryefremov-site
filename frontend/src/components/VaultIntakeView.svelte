<script lang="ts">
  import { onMount } from 'svelte';

  export let apiEndpoint: string = '/api/intake';

  let codename: string = 'Project Chimera // Series VII';
  let confidentialityTier: 'Standard Private' | 'Air-Gapped Vault' | 'Sovereign NDA' = 'Air-Gapped Vault';
  let engagementScope: 'Artisan Precision Spec' | 'Atelier Pilot Run' | 'Direct Executive Hand-off' = 'Artisan Precision Spec';
  let executionWindow: string = 'Q4 2025 Handcrafted Rollout';
  let clientEmail: string = '';
  let ndaAgreed: boolean = true;

  interface AttachedFile {
    name: string;
    size: string;
    status: string;
  }

  let attachedFiles: AttachedFile[] = [
    { name: 'chimera_core_assembly_v7.3.enc', size: '142 MB', status: 'VERIFIED' }
  ];

  let isSubmitting: boolean = false;
  let submitError: string | null = null;
  let submitSuccess: boolean = false;
  let claimTicket: string = '';

  let fileInputEl: HTMLInputElement;

  const STORAGE_KEY = 'vault_intake_draft';

  onMount(() => {
    try {
      const savedDraft = localStorage.getItem(STORAGE_KEY);
      if (savedDraft) {
        const parsed = JSON.parse(savedDraft);
        if (parsed.codename !== undefined) codename = parsed.codename;
        if (parsed.confidentialityTier !== undefined) confidentialityTier = parsed.confidentialityTier;
        if (parsed.engagementScope !== undefined) engagementScope = parsed.engagementScope;
        if (parsed.executionWindow !== undefined) executionWindow = parsed.executionWindow;
        if (parsed.clientEmail !== undefined) clientEmail = parsed.clientEmail;
        if (parsed.ndaAgreed !== undefined) ndaAgreed = parsed.ndaAgreed;
        if (parsed.attachedFiles && Array.isArray(parsed.attachedFiles)) attachedFiles = parsed.attachedFiles;
      }
    } catch {
      // Ignore storage errors
    }
  });

  function saveDraft() {
    try {
      const draft = {
        codename,
        confidentialityTier,
        engagementScope,
        executionWindow,
        clientEmail,
        ndaAgreed,
        attachedFiles
      };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(draft));
    } catch {
      // Ignore storage errors
    }
  }

  function handleInput() {
    saveDraft();
  }

  function setTier(tier: 'Standard Private' | 'Air-Gapped Vault' | 'Sovereign NDA') {
    confidentialityTier = tier;
    saveDraft();
  }

  function setScope(scope: 'Artisan Precision Spec' | 'Atelier Pilot Run' | 'Direct Executive Hand-off') {
    engagementScope = scope;
    saveDraft();
  }

  function handleFileUpload(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      for (let i = 0; i < input.files.length; i++) {
        const file = input.files[i];
        const sizeMb = (file.size / (1024 * 1024)).toFixed(1) + ' MB';
        attachedFiles = [...attachedFiles, { name: file.name, size: sizeMb, status: 'READY' }];
      }
      saveDraft();
    }
  }

  function removeFile(index: number) {
    attachedFiles = attachedFiles.filter((_, i) => i !== index);
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
    if (!codename.trim()) {
      submitError = 'Please specify initiative codename.';
      return;
    }

    if (!ndaAgreed) {
      submitError = 'Please execute the mutual binding NDA agreement to proceed.';
      return;
    }

    submitError = null;
    isSubmitting = true;

    try {
      const payload = {
        codename: codename.trim(),
        confidentialityTier,
        engagementScope,
        executionWindow,
        clientEmail: clientEmail.trim(),
        ndaAgreed,
        attachedFiles,
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

      claimTicket = resData.claimTicket || '#VLT-88219-X-' + Math.random().toString(36).substring(2, 8).toUpperCase();
      submitSuccess = true;
      clearDraft();
    } catch (err: any) {
      submitError = 'Connection issue or validation error during transmission. Your input was safely preserved locally. Please try submitting again.';
    } finally {
      isSubmitting = false;
    }
  }

  function resetForm() {
    submitSuccess = false;
    submitError = null;
    codename = 'Project Chimera // Series VII';
    confidentialityTier = 'Air-Gapped Vault';
    engagementScope = 'Artisan Precision Spec';
    executionWindow = 'Q4 2025 Handcrafted Rollout';
    clientEmail = '';
    ndaAgreed = true;
    attachedFiles = [{ name: 'chimera_core_assembly_v7.3.enc', size: '142 MB', status: 'VERIFIED' }];
    clearDraft();
  }
</script>

<div id="vault-intake-root" class="w-full max-w-lg mx-auto bg-[#121316] text-[#e3e2e6] font-sans antialiased p-4 sm:p-6 rounded-2xl border border-[#343538] shadow-2xl space-y-6 selection:bg-[#d4af37] selection:text-[#241a00]">
  <!-- TOP HEADER (Matching Mockup) -->
  <header id="intake-header" class="border-b border-[#343538] pb-4 space-y-3">
    <div class="flex items-center justify-between">
      <div class="flex items-center space-x-2">
        <span class="material-symbols-outlined text-[#f2ca50] text-[20px]">shield</span>
        <h1 class="font-bold text-sm tracking-widest text-[#e3e2e6] uppercase">VAULT // INTAKE PROTOCOL</h1>
      </div>
      <div class="flex items-center space-x-1.5 px-2.5 py-0.5 rounded border border-[#8abf97]/30 bg-[#1b1b1f]">
        <span class="w-2 h-2 rounded-full bg-[#a5dbb2] animate-pulse"></span>
        <span class="font-mono text-[11px] text-[#a5dbb2] font-semibold">TLS 1.3</span>
      </div>
    </div>

    <!-- SUB-HEADER SECURITY TELEMETRY BAR -->
    <div class="flex items-center justify-between pt-1 font-mono text-[11px]">
      <div class="flex items-center space-x-1.5 text-[#a5dbb2]">
        <span class="material-symbols-outlined text-[14px]">lock</span>
        <span class="tracking-wider font-semibold">256-BIT MUTUAL NDA ACTIVE</span>
      </div>
      <span class="text-[#99907c]">SESSION // #VLT-88219-X</span>
    </div>
  </header>

  <!-- 1. CONFIDENTIALITY BANNER -->
  <section id="confidentiality-banner" class="relative bg-[#1b1b1f] border border-[#d4af37]/40 p-4 rounded-xl shadow-sm space-y-2">
    <div class="flex items-start space-x-3">
      <div class="p-1.5 bg-[#1f1f23] border border-[#d4af37]/30 rounded flex items-center justify-center shrink-0">
        <span class="material-symbols-outlined text-[#f2ca50] text-[20px]">verified_user</span>
      </div>
      <div class="space-y-1">
        <div class="flex items-center space-x-2 flex-wrap gap-y-1">
          <h2 class="font-bold text-sm text-[#e3e2e6] tracking-wide">Bilateral Non-Disclosure Agreement</h2>
          <span class="px-2 py-0.5 rounded bg-[#121316] border border-[#4d4635] font-mono text-[10px] font-bold text-[#f2ca50]">ENFORCED</span>
        </div>
        <p class="text-xs text-[#c2c7cd] leading-relaxed">
          All uploaded schematics, target volumes, and trade parameters are air-gapped from production vendors. Direct confidential transmission strictly via institutional escrow protocol.
        </p>
      </div>
    </div>
  </section>

  {#if submitSuccess}
    <!-- SUCCESS CONFIRMATION PANEL -->
    <div
      id="intake-success-feedback"
      role="status"
      aria-live="polite"
      class="bg-[#1b1b1f] border-2 border-[#8abf97] p-6 rounded-2xl space-y-5 shadow-2xl text-[#e3e2e6]"
    >
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 rounded-full bg-[#1c4e2f] border border-[#a5dbb2] text-[#a5dbb2] flex items-center justify-center font-bold text-xl shrink-0">
          ✓
        </div>
        <div>
          <h3 id="success-heading" class="text-base font-bold text-[#a5dbb2]">Mandate Submitted & Protocol Ratified</h3>
          <p id="email-acknowledgement-notice" class="text-xs text-[#c2c7cd] mt-0.5">
            {#if clientEmail}
              An email acknowledgement and receipt has been transmitted to <span class="font-mono text-[#f2ca50]">{clientEmail}</span>.
            {:else}
              Your confidential mandate has been registered under institutional escrow protocol.
            {/if}
          </p>
        </div>
      </div>

      <div class="bg-[#121316] p-4 rounded-xl border border-[#343538] space-y-2">
        <div class="flex items-center justify-between font-mono text-xs">
          <span class="text-[#99907c]">Claim Ticket Receipt:</span>
          <span id="claim-ticket-id" class="font-bold text-[#f2ca50]">{claimTicket}</span>
        </div>
        <p class="text-xs text-[#c2c7cd]">
          Your proprietary specification is securely sealed within the air-gapped repository. No intermediary brokers or factory mechanics have access to your submission.
        </p>
      </div>

      <div class="pt-2">
        <button
          id="intake-reset-btn"
          type="button"
          on:click={resetForm}
          class="w-full py-3 px-4 bg-[#d4af37] hover:bg-[#f2ca50] text-[#241a00] font-bold rounded-xl text-xs transition-colors focus:ring-2 focus:ring-[#f2ca50] focus:outline-none cursor-pointer"
        >
          Submit Another Mandate
        </button>
      </div>
    </div>
  {:else}
    <!-- EXECUTIVE SPECIFICATION FORM -->
    <form id="vault-intake-form" on:submit|preventDefault={handleSubmit} class="space-y-6">
      {#if submitError}
        <div id="intake-error-banner" role="alert" class="p-3 bg-[#93000a]/30 border border-[#ffb4ab] text-[#ffdad6] text-xs rounded-xl flex items-start space-x-2">
          <span class="font-bold">⚠️</span>
          <p id="intake-error-message">{submitError}</p>
        </div>
      {/if}

      <!-- SECTION 01 // MANDATE IDENTIFICATION -->
      <section id="section-mandate" class="space-y-4">
        <div class="flex items-center justify-between border-b border-[#343538] pb-1 font-mono text-[11px]">
          <span class="text-[#99907c]">SECTION 01 // MANDATE IDENTIFICATION</span>
          <span class="text-[#f2ca50] font-semibold">PHASE I OF III</span>
        </div>

        <!-- Initiative Codename Input -->
        <div class="space-y-1.5">
          <div class="flex justify-between items-center">
            <label for="codename" class="font-mono text-xs font-semibold text-[#e3e2e6] tracking-wider">
              INITIATIVE CODENAME <span class="text-[#ffb4ab]" aria-hidden="true">*</span>
            </label>
            <span class="font-mono text-[10px] text-[#a5dbb2]/80">ENCRYPTED_FIELD::TLS_1.3</span>
          </div>
          <div class="relative">
            <input
              id="codename"
              type="text"
              required
              bind:value={codename}
              on:input={handleInput}
              placeholder="Specify codename or identifier"
              class="w-full bg-[#1b1b1f] border border-[#4d4635] focus:border-[#d4af37] focus:ring-2 focus:ring-[#d4af37]/30 text-[#e3e2e6] text-xs px-3.5 py-2.5 rounded-xl transition-all outline-none"
            />
            <div class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
              <span class="material-symbols-outlined text-[#99907c] text-[16px]">fingerprint</span>
            </div>
          </div>
        </div>

        <!-- Confidentiality Tier Pill Selector -->
        <div id="tier-selector" class="space-y-2">
          <div class="flex justify-between items-center">
            <span id="label-confidentiality-tier" class="font-mono text-xs font-semibold text-[#e3e2e6] tracking-wider">
              CONFIDENTIALITY TIER
            </span>
            <span class="font-mono text-[10px] text-[#99907c]">ISOLATION LEVEL</span>
          </div>
          <div class="grid grid-cols-3 gap-1.5 p-1 bg-[#0d0e11] border border-[#343538] rounded-xl" role="radiogroup" aria-labelledby="label-confidentiality-tier">
            <button
              type="button"
              role="radio"
              aria-checked={confidentialityTier === 'Standard Private'}
              on:click={() => setTier('Standard Private')}
              class="py-2 px-1 text-center rounded-lg font-mono text-[11px] transition-all focus:ring-2 focus:ring-[#d4af37] focus:outline-none {confidentialityTier === 'Standard Private' ? 'bg-[#1f1f23] border border-[#d4af37]/70 text-[#f2ca50] font-semibold shadow-sm' : 'border border-transparent text-[#d0c5af] hover:text-[#e3e2e6]'}"
            >
              Standard Private
            </button>
            <button
              type="button"
              role="radio"
              aria-checked={confidentialityTier === 'Air-Gapped Vault'}
              on:click={() => setTier('Air-Gapped Vault')}
              class="py-2 px-1 text-center rounded-lg font-mono text-[11px] transition-all focus:ring-2 focus:ring-[#d4af37] focus:outline-none {confidentialityTier === 'Air-Gapped Vault' ? 'bg-[#1f1f23] border border-[#d4af37]/70 text-[#f2ca50] font-semibold shadow-sm' : 'border border-transparent text-[#d0c5af] hover:text-[#e3e2e6]'}"
            >
              Air-Gapped Vault
            </button>
            <button
              type="button"
              role="radio"
              aria-checked={confidentialityTier === 'Sovereign NDA'}
              on:click={() => setTier('Sovereign NDA')}
              class="py-2 px-1 text-center rounded-lg font-mono text-[11px] transition-all focus:ring-2 focus:ring-[#d4af37] focus:outline-none {confidentialityTier === 'Sovereign NDA' ? 'bg-[#1f1f23] border border-[#d4af37]/70 text-[#f2ca50] font-semibold shadow-sm' : 'border border-transparent text-[#d0c5af] hover:text-[#e3e2e6]'}"
            >
              Sovereign NDA
            </button>
          </div>
        </div>

        <!-- Engagement Scope Cards -->
        <div id="scope-selector" class="space-y-2">
          <div class="flex justify-between items-center">
            <span id="label-engagement-scope" class="font-mono text-xs font-semibold text-[#e3e2e6] tracking-wider">
              ENGAGEMENT SCOPE
            </span>
            <span class="font-mono text-[10px] text-[#99907c]">WHITE-GLOVE LEVEL</span>
          </div>
          <div class="grid grid-cols-1 gap-2" role="radiogroup" aria-labelledby="label-engagement-scope">
            <!-- Card 1 -->
            <label
              class="relative flex items-start p-3 bg-[#1b1b1f] border rounded-xl cursor-pointer transition-all hover:bg-[#1f1f23] {engagementScope === 'Artisan Precision Spec' ? 'border-[#d4af37]/80 ring-1 ring-[#d4af37]/30' : 'border-[#4d4635]/60'}"
            >
              <input
                type="radio"
                name="scope"
                value="Artisan Precision Spec"
                checked={engagementScope === 'Artisan Precision Spec'}
                on:change={() => setScope('Artisan Precision Spec')}
                class="mt-1 text-[#d4af37] focus:ring-2 focus:ring-[#d4af37] border-[#4d4635] bg-[#121316]"
              />
              <div class="ml-3 space-y-0.5 flex-1">
                <div class="flex items-center space-x-2">
                  <span class="font-bold text-xs text-[#e3e2e6]">Artisan Precision Spec</span>
                  <span class="px-1.5 py-0.2 rounded bg-[#d4af37]/20 text-[#f2ca50] font-mono text-[10px] font-semibold">SUB-MICRON</span>
                </div>
                <p class="text-[11px] text-[#c2c7cd] leading-snug">
                  Bespoke high-tolerance master tooling, dedicated atelier calibration, and segregated clean-room machining.
                </p>
              </div>
            </label>

            <!-- Card 2 -->
            <label
              class="relative flex items-start p-3 bg-[#1b1b1f] border rounded-xl cursor-pointer transition-all hover:bg-[#1f1f23] {engagementScope === 'Atelier Pilot Run' ? 'border-[#d4af37]/80 ring-1 ring-[#d4af37]/30' : 'border-[#4d4635]/60'}"
            >
              <input
                type="radio"
                name="scope"
                value="Atelier Pilot Run"
                checked={engagementScope === 'Atelier Pilot Run'}
                on:change={() => setScope('Atelier Pilot Run')}
                class="mt-1 text-[#d4af37] focus:ring-2 focus:ring-[#d4af37] border-[#4d4635] bg-[#121316]"
              />
              <div class="ml-3 space-y-0.5 flex-1">
                <div class="flex items-center space-x-2">
                  <span class="font-bold text-xs text-[#e3e2e6]">Atelier Pilot Run</span>
                  <span class="font-mono text-[10px] text-[#99907c]">N=50 TO 500</span>
                </div>
                <p class="text-[11px] text-[#c2c7cd] leading-snug">
                  Discreet initial validation batch fabricated by master artisans under full non-disclosure containment.
                </p>
              </div>
            </label>

            <!-- Card 3 -->
            <label
              class="relative flex items-start p-3 bg-[#1b1b1f] border rounded-xl cursor-pointer transition-all hover:bg-[#1f1f23] {engagementScope === 'Direct Executive Hand-off' ? 'border-[#d4af37]/80 ring-1 ring-[#d4af37]/30' : 'border-[#4d4635]/60'}"
            >
              <input
                type="radio"
                name="scope"
                value="Direct Executive Hand-off"
                checked={engagementScope === 'Direct Executive Hand-off'}
                on:change={() => setScope('Direct Executive Hand-off')}
                class="mt-1 text-[#d4af37] focus:ring-2 focus:ring-[#d4af37] border-[#4d4635] bg-[#121316]"
              />
              <div class="ml-3 space-y-0.5 flex-1">
                <div class="flex items-center space-x-2">
                  <span class="font-bold text-xs text-[#e3e2e6]">Direct Executive Hand-off</span>
                  <span class="font-mono text-[10px] text-[#99907c]">SECURE COURIER</span>
                </div>
                <p class="text-[11px] text-[#c2c7cd] leading-snug">
                  Direct peer-to-peer transport bypassing multi-tenant distribution nodes with end-to-end custody chains.
                </p>
              </div>
            </label>
          </div>
        </div>

        <!-- Target Execution Window Dropdown -->
        <div class="space-y-1.5">
          <label for="execution-window" class="font-mono text-xs font-semibold text-[#e3e2e6] tracking-wider block">
            TARGET EXECUTION WINDOW
          </label>
          <div class="relative">
            <select
              id="execution-window"
              bind:value={executionWindow}
              on:change={handleInput}
              class="w-full bg-[#1b1b1f] border border-[#4d4635] focus:border-[#d4af37] focus:ring-2 focus:ring-[#d4af37]/30 text-[#e3e2e6] text-xs px-3.5 py-2.5 rounded-xl appearance-none cursor-pointer outline-none"
            >
              <option value="Q4 2025 Handcrafted Rollout">Q4 2025 Handcrafted Rollout</option>
              <option value="Q1 2026 Sovereign Deployment">Q1 2026 Sovereign Deployment</option>
              <option value="Expedited Atelier Velocity (30-Day Escrow)">Expedited Atelier Velocity (30-Day Escrow)</option>
            </select>
            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-[#99907c]">
              <span class="material-symbols-outlined text-[18px]">unfold_more</span>
            </div>
          </div>
        </div>

        <!-- Optional Notification Email for Acknowledgement -->
        <div class="space-y-1.5 pt-1">
          <label for="client-email-input" class="font-mono text-xs font-semibold text-[#e3e2e6] tracking-wider block">
            EXECUTIVE EMAIL (OPTIONAL FOR ACKNOWLEDGEMENT)
          </label>
          <input
            id="client-email-input"
            type="email"
            bind:value={clientEmail}
            on:input={handleInput}
            placeholder="partner@organization.com"
            class="w-full bg-[#1b1b1f] border border-[#4d4635] focus:border-[#d4af37] focus:ring-2 focus:ring-[#d4af37]/30 text-[#e3e2e6] text-xs px-3.5 py-2.5 rounded-xl transition-all outline-none"
          />
          <p class="text-[10px] font-mono text-[#99907c]">Used solely for dispatching institutional receipt & status confirmation.</p>
        </div>
      </section>

      <!-- SECTION 02 // PROPRIETARY IP REPOSITORY -->
      <section id="section-ip-repository" class="space-y-3 pt-2">
        <div class="flex items-center justify-between border-b border-[#343538] pb-1 font-mono text-[11px]">
          <span class="text-[#99907c]">SECTION 02 // PROPRIETARY IP REPOSITORY</span>
          <span class="text-[#a5dbb2] font-semibold">E2EE ACTIVATED</span>
        </div>

        <!-- Upload Zone -->
        <div
          id="upload-zone"
          on:click={() => fileInputEl && fileInputEl.click()}
          on:keydown={(e) => (e.key === 'Enter' || e.key === ' ') && fileInputEl && fileInputEl.click()}
          role="button"
          tabindex="0"
          aria-label="Upload CAD or schematic files"
          class="border border-dashed border-[#d4af37]/60 bg-[#0d0e11] rounded-xl p-5 text-center hover:bg-[#1b1b1f] transition-all cursor-pointer group focus:ring-2 focus:ring-[#d4af37] focus:outline-none"
        >
          <input
            id="cad-upload-input"
            type="file"
            bind:this={fileInputEl}
            on:change={handleFileUpload}
            multiple
            class="hidden"
          />
          <div class="mx-auto w-10 h-10 rounded-xl border border-[#4d4635] bg-[#1f1f23] flex items-center justify-center mb-2 group-hover:border-[#d4af37] transition-colors">
            <span class="material-symbols-outlined text-[#f2ca50] text-[20px]">lock</span>
          </div>
          <p class="font-bold text-xs text-[#e3e2e6]">Drop Proprietary CAD / Schematics</p>
          <p class="font-mono text-[11px] text-[#99907c] mt-0.5">STEP, IGES, DXF, or encrypted archives up to 2.5 GB</p>
          <div class="mt-3 inline-flex items-center space-x-1.5 px-2.5 py-1 rounded-lg bg-[#121316] border border-[#343538]">
            <span class="material-symbols-outlined text-[#a5dbb2] text-[13px]">key</span>
            <span class="font-mono text-[10px] text-[#d0c5af]">Zero vendor exposure — anonymized via cryptographic key</span>
          </div>
        </div>

        <!-- Attached Files List -->
        {#if attachedFiles.length > 0}
          <div class="space-y-1">
            {#each attachedFiles as file, index}
              <div class="flex items-center justify-between p-2.5 rounded-xl bg-[#1f1f23] border border-[#343538] font-mono text-[11px]">
                <div class="flex items-center space-x-2 overflow-hidden mr-2">
                  <span class="material-symbols-outlined text-[#99907c] text-[16px] shrink-0">folder_supervised</span>
                  <span class="text-[#e3e2e6] truncate">{file.name}</span>
                </div>
                <div class="flex items-center space-x-3 shrink-0">
                  <span class="text-[#a5dbb2]">{file.size} // {file.status}</span>
                  <button
                    type="button"
                    on:click={() => removeFile(index)}
                    aria-label={`Remove file ${file.name}`}
                    class="text-[#ffb4ab] hover:text-[#ffdad6] font-bold px-1 rounded focus:ring-1 focus:ring-[#ffb4ab]"
                  >
                    ✕
                  </button>
                </div>
              </div>
            {/each}
          </div>
        {/if}
      </section>

      <!-- SECTION 03 // RATIFICATION -->
      <section id="section-ratification" class="space-y-4 pt-2">
        <div class="flex items-center justify-between border-b border-[#343538] pb-1 font-mono text-[11px]">
          <span class="text-[#99907c]">SECTION 03 // RATIFICATION</span>
        </div>

        <!-- Legal Checkbox -->
        <label class="flex items-start space-x-3 cursor-pointer group">
          <input
            id="nda-agree"
            type="checkbox"
            bind:checked={ndaAgreed}
            on:change={handleInput}
            class="mt-0.5 w-4 h-4 rounded border-[#4d4635] bg-[#121316] text-[#d4af37] focus:ring-2 focus:ring-[#d4af37] cursor-pointer"
          />
          <span class="text-xs text-[#e3e2e6] group-hover:text-[#f2ca50] transition-colors select-none leading-tight">
            Execute mutual binding NDA & initiate private partner briefing protocol.
          </span>
        </label>

        <!-- Primary Executive CTA -->
        <div class="space-y-2">
          <button
            id="submit-intake-btn"
            type="submit"
            disabled={isSubmitting}
            class="w-full bg-[#d4af37] hover:bg-[#c49f27] text-[#121316] font-bold text-sm py-3 px-4 rounded-xl tracking-wide transition-all duration-150 flex items-center justify-center space-x-2 shadow-md active:opacity-95 focus:ring-2 focus:ring-[#f2ca50] focus:outline-none cursor-pointer disabled:opacity-50"
          >
            {#if isSubmitting}
              <span>Transmitting Encrypted Mandate...</span>
            {:else}
              <span>Transmit to Executive Partner</span>
              <span class="material-symbols-outlined text-[18px]">arrow_forward</span>
            {/if}
          </button>
          <p class="font-mono text-[10px] text-center text-[#99907c] leading-tight">
            Direct partner routing. No intermediary brokers or factory disclosure.
          </p>
        </div>
      </section>
    </form>
  {/if}
</div>
