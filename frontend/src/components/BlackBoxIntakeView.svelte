<script lang="ts">
  export let theme: 'dark' | 'light' = 'dark';

  export let codename: string = 'Project Chimera // Series VII';
  export let confidentialityTier: 'standard' | 'airgapped' | 'sovereign' = 'airgapped';
  export let engagementScope: 'precision' | 'pilot' | 'courier' = 'precision';
  export let executionWindow: string = 'Q4 2025 Handcrafted Rollout';
  export let ndaAgreed: boolean = true;

  export interface UploadedFile {
    id: string;
    name: string;
    size: string;
    status: string;
  }

  export let uploadedFiles: UploadedFile[] = [
    {
      id: 'file-1',
      name: 'chimera_core_assembly_v7.3.enc',
      size: '142 MB',
      status: 'VERIFIED'
    }
  ];

  export let isSubmitted: boolean = false;
  export let submissionReceiptId: string = 'ESCROW-NDA-88219-X';
  let fileInputRef: HTMLInputElement | null = null;
  let isDragging: boolean = false;

  function handleFileSelect(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      for (let i = 0; i < target.files.length; i++) {
        const file = target.files[i];
        const sizeMb = (file.size / (1024 * 1024)).toFixed(1);
        uploadedFiles = [
          ...uploadedFiles,
          {
            id: `file-${Date.now()}-${i}`,
            name: file.name,
            size: `${sizeMb} MB`,
            status: 'VERIFIED'
          }
        ];
      }
    }
  }

  function removeFile(id: string) {
    uploadedFiles = uploadedFiles.filter(f => f.id !== id);
  }

  function triggerFileInput() {
    if (fileInputRef) {
      fileInputRef.click();
    }
  }

  function handleDrop(event: DragEvent) {
    event.preventDefault();
    isDragging = false;
    if (event.dataTransfer && event.dataTransfer.files.length > 0) {
      for (let i = 0; i < event.dataTransfer.files.length; i++) {
        const file = event.dataTransfer.files[i];
        const sizeMb = (file.size / (1024 * 1024)).toFixed(1);
        uploadedFiles = [
          ...uploadedFiles,
          {
            id: `file-${Date.now()}-${i}`,
            name: file.name,
            size: `${sizeMb} MB`,
            status: 'VERIFIED'
          }
        ];
      }
    }
  }

  function handleDragOver(event: DragEvent) {
    event.preventDefault();
    isDragging = true;
  }

  function handleDragLeave(event: DragEvent) {
    event.preventDefault();
    isDragging = false;
  }

  function handleSubmit(event: Event) {
    event.preventDefault();
    if (!ndaAgreed) return;
    submissionReceiptId = `ESCROW-NDA-${Math.floor(10000 + Math.random() * 90000)}-CONFIRMED`;
    isSubmitted = true;
  }

  function resetForm() {
    isSubmitted = false;
    codename = 'Project Chimera // Series VII';
    confidentialityTier = 'airgapped';
    engagementScope = 'precision';
    executionWindow = 'Q4 2025 Handcrafted Rollout';
    ndaAgreed = true;
  }
</script>

<div id="blackbox-intake-root" class="w-full max-w-2xl mx-auto space-y-6 font-sans">
  <!-- Header Bar -->
  <header id="intake-header" class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 p-4 rounded-lg border {theme === 'dark' ? 'bg-[#1b1b1f] border-[#4d4635]' : 'bg-white border-[#cbd5e1] shadow-sm'}">
    <div class="flex items-center space-x-2.5">
      <span class="text-[#f2ca50] text-xl font-bold" aria-hidden="true">🛡️</span>
      <h2 id="intake-title" class="font-mono text-sm sm:text-base font-bold tracking-widest uppercase {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#0f172a]'}">
        VAULT // INTAKE PROTOCOL
      </h2>
    </div>
    <div class="flex items-center space-x-3 text-xs font-mono">
      <div class="flex items-center space-x-1.5 px-2.5 py-1 rounded border {theme === 'dark' ? 'bg-[#121316] border-[#1c4e2f] text-[#a5dbb2]' : 'bg-emerald-50 border-emerald-300 text-emerald-800'}">
        <span class="w-2 h-2 rounded-full bg-[#a5dbb2] animate-pulse" aria-hidden="true"></span>
        <span class="font-semibold">TLS 1.3</span>
      </div>
      <button
        id="concierge-btn"
        type="button"
        aria-label="Request Concierge Briefing"
        class="font-semibold tracking-wider transition-colors {theme === 'dark' ? 'text-[#d0c5af] hover:text-[#f2ca50]' : 'text-[#64748b] hover:text-[#0f172a]'}"
      >
        CONCIERGE
      </button>
    </div>
  </header>

  <!-- Sub-header Security Telemetry Bar -->
  <div id="security-telemetry-bar" class="flex items-center justify-between border-b pb-2 font-mono text-xs {theme === 'dark' ? 'border-[#4d4635]/50 text-[#a5dbb2]' : 'border-[#cbd5e1] text-emerald-700'}">
    <div class="flex items-center space-x-2">
      <span aria-hidden="true">🔒</span>
      <span class="font-bold tracking-wider">256-BIT MUTUAL NDA ACTIVE</span>
    </div>
    <span class="text-xs {theme === 'dark' ? 'text-[#99907c]' : 'text-[#64748b]'}">SESSION // #VLT-88219-X</span>
  </div>

  {#if isSubmitted}
    <!-- Turnkey Confirmation State -->
    <div id="intake-confirmation-view" class="p-6 rounded-lg border space-y-6 animate-fadeIn {theme === 'dark' ? 'bg-[#1b1b1f] border-[#d4af37] text-[#e3e2e6]' : 'bg-amber-50/50 border-[#d4af37] text-[#0f172a]'}">
      <div class="flex items-start space-x-4">
        <div class="p-3 rounded-md border flex items-center justify-center shrink-0 {theme === 'dark' ? 'bg-[#121316] border-[#d4af37] text-[#f2ca50]' : 'bg-white border-[#d4af37] text-[#554300]'}">
          <span class="text-3xl" aria-hidden="true">🛡️</span>
        </div>
        <div class="space-y-2 flex-1">
          <div class="flex flex-wrap items-center justify-between gap-2">
            <h3 id="confirmation-heading" class="font-mono text-lg font-bold tracking-wide {theme === 'dark' ? 'text-[#f2ca50]' : 'text-[#554300]'}">
              MANDATE RATIFIED & TRANSMITTED
            </h3>
            <span class="px-2.5 py-0.5 rounded font-mono text-xs font-bold border border-[#1c4e2f] bg-[#1c4e2f]/20 text-[#a5dbb2]">
              CONFIRMED
            </span>
          </div>
          <p id="confirmation-receipt-id" class="font-mono text-xs font-semibold text-[#a5dbb2]">
            RECEIPT HASH: {submissionReceiptId}
          </p>
          <p class="text-sm leading-relaxed {theme === 'dark' ? 'text-[#d0c5af]' : 'text-[#334155]'}">
            Your proprietary initiative specs and attached files have been locked under our 256-bit Bilateral Non-Disclosure Agreement protocol. Direct executive partner briefing is active.
          </p>
        </div>
      </div>

      <!-- Mandate Summary -->
      <div class="p-4 rounded border space-y-2 font-mono text-xs {theme === 'dark' ? 'bg-[#121316] border-[#4d4635]' : 'bg-white border-[#cbd5e1]'}">
        <div class="font-bold text-[#f2ca50] border-b pb-1 mb-2 border-[#4d4635]/40">
          CONFIDENTIAL MANDATE SUMMARY
        </div>
        <div class="flex justify-between py-1">
          <span class="text-[#99907c]">INITIATIVE CODENAME:</span>
          <span class="font-bold text-[#e3e2e6]">{codename}</span>
        </div>
        <div class="flex justify-between py-1">
          <span class="text-[#99907c]">CONFIDENTIALITY TIER:</span>
          <span class="font-bold uppercase text-[#a5dbb2]">{confidentialityTier}</span>
        </div>
        <div class="flex justify-between py-1">
          <span class="text-[#99907c]">ENGAGEMENT SCOPE:</span>
          <span class="font-bold uppercase text-[#e3e2e6]">{engagementScope}</span>
        </div>
        <div class="flex justify-between py-1">
          <span class="text-[#99907c]">TARGET EXECUTION:</span>
          <span class="font-bold text-[#e3e2e6]">{executionWindow}</span>
        </div>
        <div class="flex justify-between py-1">
          <span class="text-[#99907c]">ENCRYPTED ATTACHMENTS:</span>
          <span class="font-bold text-[#a5dbb2]">{uploadedFiles.length} File(s)</span>
        </div>
      </div>

      <div class="pt-2 flex flex-col sm:flex-row gap-3 justify-end">
        <button
          id="reset-mandate-btn"
          type="button"
          on:click={resetForm}
          class="w-full sm:w-auto px-5 py-2.5 rounded font-mono text-xs font-bold transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#d4af37] hover:bg-[#f2ca50] text-[#121316]' : 'bg-[#554300] hover:bg-[#3c2f00] text-white'}"
        >
          SUBMIT ANOTHER MANDATE
        </button>
      </div>
    </div>
  {:else}
    <!-- Main Intake Form -->
    <form id="intake-form" on:submit={handleSubmit} class="space-y-6">
      <!-- 1. Confidentiality Banner -->
      <section id="confidentiality-banner" class="p-4 rounded-lg border shadow-sm space-y-2 {theme === 'dark' ? 'bg-[#1b1b1f] border-[#d4af37]/50' : 'bg-amber-50/80 border-[#d4af37]/60'}">
        <div class="flex items-start space-x-3">
          <div class="p-2 rounded border shrink-0 flex items-center justify-center {theme === 'dark' ? 'bg-[#1f1f23] border-[#d4af37]/40 text-[#f2ca50]' : 'bg-white border-[#d4af37]/60 text-[#554300]'}">
            <span class="text-xl" aria-hidden="true">📜</span>
          </div>
          <div class="space-y-1">
            <div class="flex items-center space-x-2">
              <h3 class="font-mono text-sm sm:text-base font-bold text-on-surface tracking-wide {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#0f172a]'}">
                Bilateral Non-Disclosure Agreement
              </h3>
              <span class="px-2 py-0.5 rounded border font-mono text-[10px] font-bold text-[#f2ca50] border-[#4d4635] bg-[#121316]">
                ENFORCED
              </span>
            </div>
            <p class="text-xs sm:text-sm leading-relaxed {theme === 'dark' ? 'text-[#c2c7cd]' : 'text-[#475569]'}">
              All uploaded schematics, target volumes, and trade parameters are air-gapped from production vendors. Direct confidential transmission strictly via institutional escrow protocol.
            </p>
          </div>
        </div>
      </section>

      <!-- 2. Section 01: Executive Specification Form -->
      <section id="section-mandate-identification" class="space-y-5 p-5 rounded-lg border {theme === 'dark' ? 'bg-[#1b1b1f] border-[#343538]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
        <div class="flex items-center justify-between border-b pb-2 font-mono text-xs {theme === 'dark' ? 'border-[#343538]' : 'border-[#e2e8f0]'}">
          <span class="text-[#99907c] font-semibold">SECTION 01 // MANDATE IDENTIFICATION</span>
          <span class="text-[#f2ca50] font-bold">PHASE I OF III</span>
        </div>

        <!-- Initiative Codename -->
        <div class="space-y-1.5">
          <div class="flex justify-between items-center text-xs font-mono">
            <label for="codename-input" class="font-bold tracking-wider {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#1e293b]'}">
              INITIATIVE CODENAME
            </label>
            <span class="text-[#a5dbb2]">ENCRYPTED_FIELD::TLS_1.3</span>
          </div>
          <div class="relative">
            <input
              id="codename-input"
              name="codename"
              type="text"
              required
              bind:value={codename}
              placeholder="Specify codename or identifier"
              class="w-full px-3.5 py-2.5 rounded font-mono text-sm border transition-colors focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#121316] border-[#4d4635] text-[#e3e2e6] placeholder-[#5A606D]' : 'bg-[#f8fafc] border-[#cbd5e1] text-[#0f172a] placeholder-[#94a3b8]'}"
            />
          </div>
        </div>

        <!-- Confidentiality Tier Pill Selector -->
        <div class="space-y-2">
          <div class="flex justify-between items-center text-xs font-mono">
            <span class="font-bold tracking-wider {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#1e293b]'}">
              CONFIDENTIALITY TIER
            </span>
            <span class="text-[#99907c]">ISOLATION LEVEL</span>
          </div>
          <div class="grid grid-cols-3 gap-2 p-1.5 rounded border font-mono text-xs {theme === 'dark' ? 'bg-[#0d0e11] border-[#343538]' : 'bg-[#f1f5f9] border-[#cbd5e1]'}">
            <button
              id="tier-standard-btn"
              type="button"
              aria-pressed={confidentialityTier === 'standard'}
              on:click={() => (confidentialityTier = 'standard')}
              class="py-2 px-1 text-center rounded transition-all font-semibold focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {confidentialityTier === 'standard' ? (theme === 'dark' ? 'bg-[#1f1f23] border border-[#d4af37] text-[#f2ca50]' : 'bg-white border border-[#d4af37] text-[#554300] shadow-sm') : (theme === 'dark' ? 'text-[#c2c7cd] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
            >
              Standard Private
            </button>
            <button
              id="tier-airgapped-btn"
              type="button"
              aria-pressed={confidentialityTier === 'airgapped'}
              on:click={() => (confidentialityTier = 'airgapped')}
              class="py-2 px-1 text-center rounded transition-all font-semibold focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {confidentialityTier === 'airgapped' ? (theme === 'dark' ? 'bg-[#1f1f23] border border-[#d4af37] text-[#f2ca50]' : 'bg-white border border-[#d4af37] text-[#554300] shadow-sm') : (theme === 'dark' ? 'text-[#c2c7cd] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
            >
              Air-Gapped Vault
            </button>
            <button
              id="tier-sovereign-btn"
              type="button"
              aria-pressed={confidentialityTier === 'sovereign'}
              on:click={() => (confidentialityTier = 'sovereign')}
              class="py-2 px-1 text-center rounded transition-all font-semibold focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {confidentialityTier === 'sovereign' ? (theme === 'dark' ? 'bg-[#1f1f23] border border-[#d4af37] text-[#f2ca50]' : 'bg-white border border-[#d4af37] text-[#554300] shadow-sm') : (theme === 'dark' ? 'text-[#c2c7cd] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
            >
              Sovereign NDA
            </button>
          </div>
        </div>

        <!-- Engagement Scope Cards -->
        <div class="space-y-2.5">
          <div class="flex justify-between items-center text-xs font-mono">
            <span class="font-bold tracking-wider {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#1e293b]'}">
              ENGAGEMENT SCOPE
            </span>
            <span class="text-[#99907c]">WHITE-GLOVE LEVEL</span>
          </div>

          <div class="grid grid-cols-1 gap-2.5">
            <!-- Scope 1 -->
            <label class="relative flex items-start p-3.5 rounded border cursor-pointer transition-all hover:border-[#d4af37] focus-within:ring-2 focus-within:ring-[#f2ca50] {engagementScope === 'precision' ? (theme === 'dark' ? 'bg-[#1f1f23] border-[#d4af37]' : 'bg-amber-50/60 border-[#d4af37]') : (theme === 'dark' ? 'bg-[#121316] border-[#343538]' : 'bg-[#f8fafc] border-[#cbd5e1]')}">
              <input
                type="radio"
                name="scope"
                value="precision"
                checked={engagementScope === 'precision'}
                on:change={() => (engagementScope = 'precision')}
                class="mt-1 accent-[#d4af37] focus:ring-0 cursor-pointer"
              />
              <div class="ml-3 space-y-1">
                <div class="flex items-center space-x-2">
                  <span class="font-semibold text-sm {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#0f172a]'}">Artisan Precision Spec</span>
                  <span class="px-1.5 py-0.2 rounded font-mono text-[10px] font-bold bg-[#d4af37]/20 text-[#f2ca50]">SUB-MICRON</span>
                </div>
                <p class="text-xs leading-relaxed {theme === 'dark' ? 'text-[#c2c7cd]' : 'text-[#64748b]'}">
                  Bespoke high-tolerance master tooling, dedicated atelier calibration, and segregated clean-room machining.
                </p>
              </div>
            </label>

            <!-- Scope 2 -->
            <label class="relative flex items-start p-3.5 rounded border cursor-pointer transition-all hover:border-[#d4af37] focus-within:ring-2 focus-within:ring-[#f2ca50] {engagementScope === 'pilot' ? (theme === 'dark' ? 'bg-[#1f1f23] border-[#d4af37]' : 'bg-amber-50/60 border-[#d4af37]') : (theme === 'dark' ? 'bg-[#121316] border-[#343538]' : 'bg-[#f8fafc] border-[#cbd5e1]')}">
              <input
                type="radio"
                name="scope"
                value="pilot"
                checked={engagementScope === 'pilot'}
                on:change={() => (engagementScope = 'pilot')}
                class="mt-1 accent-[#d4af37] focus:ring-0 cursor-pointer"
              />
              <div class="ml-3 space-y-1">
                <div class="flex items-center space-x-2">
                  <span class="font-semibold text-sm {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#0f172a]'}">Atelier Pilot Run</span>
                  <span class="font-mono text-[10px] text-[#99907c]">N=50 TO 500</span>
                </div>
                <p class="text-xs leading-relaxed {theme === 'dark' ? 'text-[#c2c7cd]' : 'text-[#64748b]'}">
                  Discreet initial validation batch fabricated by master artisans under full non-disclosure containment.
                </p>
              </div>
            </label>

            <!-- Scope 3 -->
            <label class="relative flex items-start p-3.5 rounded border cursor-pointer transition-all hover:border-[#d4af37] focus-within:ring-2 focus-within:ring-[#f2ca50] {engagementScope === 'courier' ? (theme === 'dark' ? 'bg-[#1f1f23] border-[#d4af37]' : 'bg-amber-50/60 border-[#d4af37]') : (theme === 'dark' ? 'bg-[#121316] border-[#343538]' : 'bg-[#f8fafc] border-[#cbd5e1]')}">
              <input
                type="radio"
                name="scope"
                value="courier"
                checked={engagementScope === 'courier'}
                on:change={() => (engagementScope = 'courier')}
                class="mt-1 accent-[#d4af37] focus:ring-0 cursor-pointer"
              />
              <div class="ml-3 space-y-1">
                <div class="flex items-center space-x-2">
                  <span class="font-semibold text-sm {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#0f172a]'}">Direct Executive Hand-off</span>
                  <span class="font-mono text-[10px] text-[#99907c]">SECURE COURIER</span>
                </div>
                <p class="text-xs leading-relaxed {theme === 'dark' ? 'text-[#c2c7cd]' : 'text-[#64748b]'}">
                  Direct peer-to-peer transport bypassing multi-tenant distribution nodes with end-to-end custody chains.
                </p>
              </div>
            </label>
          </div>
        </div>

        <!-- Target Execution Window -->
        <div class="space-y-1.5">
          <label for="execution-window-select" class="block font-mono text-xs font-bold tracking-wider {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#1e293b]'}">
            TARGET EXECUTION WINDOW
          </label>
          <select
            id="execution-window-select"
            name="executionWindow"
            bind:value={executionWindow}
            class="w-full px-3.5 py-2.5 rounded font-mono text-sm border cursor-pointer transition-colors focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {theme === 'dark' ? 'bg-[#121316] border-[#4d4635] text-[#e3e2e6]' : 'bg-[#f8fafc] border-[#cbd5e1] text-[#0f172a]'}"
          >
            <option value="Q4 2025 Handcrafted Rollout">Q4 2025 Handcrafted Rollout</option>
            <option value="Q1 2026 Sovereign Deployment">Q1 2026 Sovereign Deployment</option>
            <option value="Expedited Atelier Velocity (30-Day Escrow)">Expedited Atelier Velocity (30-Day Escrow)</option>
          </select>
        </div>
      </section>

      <!-- 3. Section 02: Secure Document & IP Vault -->
      <section id="section-proprietary-ip" class="space-y-4 p-5 rounded-lg border {theme === 'dark' ? 'bg-[#1b1b1f] border-[#343538]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
        <div class="flex items-center justify-between border-b pb-2 font-mono text-xs {theme === 'dark' ? 'border-[#343538]' : 'border-[#e2e8f0]'}">
          <span class="text-[#99907c] font-semibold">SECTION 02 // PROPRIETARY IP REPOSITORY</span>
          <span class="text-[#a5dbb2] font-bold">E2EE ACTIVATED</span>
        </div>

        <!-- Drop Zone -->
        <div
          id="file-drop-zone"
          role="button"
          tabindex="0"
          aria-label="Upload CAD or schematic documents"
          on:click={triggerFileInput}
          on:keydown={(e) => (e.key === 'Enter' || e.key === ' ') && triggerFileInput()}
          on:dragover={handleDragOver}
          on:dragleave={handleDragLeave}
          on:drop={handleDrop}
          class="border-2 border-dashed rounded-lg p-6 text-center transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {isDragging ? 'border-[#f2ca50] bg-[#f2ca50]/10' : theme === 'dark' ? 'border-[#d4af37]/60 bg-[#0d0e11] hover:bg-[#121316]' : 'border-[#d4af37] bg-slate-50 hover:bg-amber-50/40'}"
        >
          <input
            id="file-upload-input"
            type="file"
            multiple
            ref={(el) => (fileInputRef = el)}
            on:change={handleFileSelect}
            class="hidden"
          />
          <div class="mx-auto w-10 h-10 rounded border flex items-center justify-center mb-2 {theme === 'dark' ? 'bg-[#1f1f23] border-[#4d4635] text-[#f2ca50]' : 'bg-white border-[#cbd5e1] text-[#554300]'}">
            <span class="text-xl" aria-hidden="true">🔒</span>
          </div>
          <p class="font-semibold text-sm {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#0f172a]'}">
            Drop Proprietary CAD / Schematics
          </p>
          <p class="text-xs text-[#99907c] mt-1">
            STEP, IGES, DXF, or encrypted archives up to 2.5 GB
          </p>
          <div class="mt-3 inline-flex items-center space-x-1.5 px-3 py-1 rounded border font-mono text-[11px] {theme === 'dark' ? 'bg-[#121316] border-[#343538] text-[#d0c5af]' : 'bg-white border-[#cbd5e1] text-[#475569]'}">
            <span class="text-[#a5dbb2]" aria-hidden="true">🔑</span>
            <span>Zero vendor exposure — anonymized via cryptographic key</span>
          </div>
        </div>

        <!-- Attached Files List -->
        {#if uploadedFiles.length > 0}
          <div class="space-y-2">
            <span class="block font-mono text-xs font-semibold text-[#99907c]">
              ATTACHED ENCRYPTED FILES ({uploadedFiles.length})
            </span>
            <div class="space-y-1.5">
              {#each uploadedFiles as file (file.id)}
                <div class="flex items-center justify-between p-2.5 rounded border font-mono text-xs {theme === 'dark' ? 'bg-[#1f1f23] border-[#343538]' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
                  <div class="flex items-center space-x-2 truncate pr-2">
                    <span aria-hidden="true">📁</span>
                    <span class="truncate font-semibold {theme === 'dark' ? 'text-[#e3e2e6]' : 'text-[#0f172a]'}">
                      {file.name}
                    </span>
                  </div>
                  <div class="flex items-center space-x-3 shrink-0">
                    <span class="text-[#a5dbb2] font-semibold">{file.size} // {file.status}</span>
                    <button
                      type="button"
                      aria-label="Remove attached file {file.name}"
                      on:click={() => removeFile(file.id)}
                      class="text-red-400 hover:text-red-300 font-bold px-1 focus:outline-none focus:ring-1 focus:ring-red-400"
                    >
                      ✕
                    </button>
                  </div>
                </div>
              {/each}
            </div>
          </div>
        {/if}
      </section>

      <!-- 4. Section 03: Executive Sign-off & Submission -->
      <section id="section-ratification" class="space-y-4 p-5 rounded-lg border {theme === 'dark' ? 'bg-[#1b1b1f] border-[#343538]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
        <div class="flex items-center justify-between border-b pb-2 font-mono text-xs {theme === 'dark' ? 'border-[#343538]' : 'border-[#e2e8f0]'}">
          <span class="text-[#99907c] font-semibold">SECTION 03 // RATIFICATION</span>
        </div>

        <!-- Legal NDA Checkbox -->
        <label class="flex items-start space-x-3 cursor-pointer group">
          <input
            id="nda-agreed-checkbox"
            name="ndaAgreed"
            type="checkbox"
            bind:checked={ndaAgreed}
            class="mt-1 w-4 h-4 rounded accent-[#d4af37] border-[#4d4635] bg-[#121316] cursor-pointer focus:ring-2 focus:ring-[#f2ca50]"
          />
          <span class="text-xs sm:text-sm leading-snug transition-colors {theme === 'dark' ? 'text-[#e3e2e6] group-hover:text-[#f2ca50]' : 'text-[#0f172a] group-hover:text-[#554300]'}">
            Execute mutual binding NDA & initiate private partner briefing protocol.
          </span>
        </label>

        <!-- CTA Submit Button -->
        <div class="space-y-2 pt-2">
          <button
            id="submit-mandate-btn"
            type="submit"
            disabled={!ndaAgreed}
            class="w-full py-3.5 px-4 rounded font-mono text-sm font-bold tracking-wide transition-all flex items-center justify-center space-x-2 shadow-md focus:outline-none focus:ring-2 focus:ring-[#f2ca50] disabled:opacity-50 disabled:cursor-not-allowed {theme === 'dark' ? 'bg-[#d4af37] hover:bg-[#c49f27] text-[#121316]' : 'bg-[#554300] hover:bg-[#3c2f00] text-white'}"
          >
            <span>Transmit to Executive Partner</span>
            <span aria-hidden="true">➔</span>
          </button>
          <p class="text-center font-mono text-[11px] text-[#99907c]">
            Direct partner routing. No intermediary brokers or factory disclosure.
          </p>
        </div>
      </section>
    </form>
  {/if}

  <!-- Bottom Navigation Bar for Intake Protocols -->
  <nav id="intake-bottom-nav" aria-label="Intake Protocol Navigation" class="flex justify-around items-center p-3 rounded-lg border font-mono text-xs {theme === 'dark' ? 'bg-[#1b1b1f] border-[#343538] text-[#99907c]' : 'bg-white border-[#cbd5e1] text-[#64748b]'}">
    <button
      type="button"
      id="intake-nav-mandate"
      class="flex items-center space-x-1.5 font-bold text-[#f2ca50]"
    >
      <span aria-hidden="true">🔒</span>
      <span>MANDATE</span>
    </button>
    <button
      type="button"
      id="intake-nav-documents"
      class="flex items-center space-x-1.5 hover:text-[#f2ca50] transition-colors"
    >
      <span aria-hidden="true">📁</span>
      <span>DOCUMENTS</span>
    </button>
    <button
      type="button"
      id="intake-nav-protocol"
      class="flex items-center space-x-1.5 hover:text-[#f2ca50] transition-colors"
    >
      <span aria-hidden="true">🛡️</span>
      <span>PROTOCOL</span>
    </button>
  </nav>
</div>
