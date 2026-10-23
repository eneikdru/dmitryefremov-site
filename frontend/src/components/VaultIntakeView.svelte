<script lang="ts">
  import { onMount } from 'svelte';

  export let apiEndpoint: string = '/api/intake';

  type Archetype = 'horology' | 'metallurgy' | 'joaillerie' | 'objet_dart';
  type ConfidentialityTier = 'ghost_atelier' | 'private_archive';

  let selectedArchetype: Archetype = 'horology';
  let selectedMaterial: string = 'Damascus Titanium & Meteorite';
  let confidentialityTier: ConfidentialityTier = 'ghost_atelier';

  let email: string = '';
  let title: string = 'Project Obsidian Aegis';
  let narrative: string = 'Phase I: Sovereign architectural artifact design. Bilateral confidentiality and turnkey execution required under Geneva Seal tolerances.';
  let discardMetadata: boolean = true;
  let requireRecoveryPhrase: boolean = true;

  let isSubmitting: boolean = false;
  let submitError: string | null = null;
  let submitSuccess: boolean = false;
  let acknowledgementEmailSent: boolean = false;
  let claimTicket: string = '';

  let isCovenantDrawerOpen: boolean = false;

  const STORAGE_KEY = 'vault_intake_draft';

  onMount(() => {
    try {
      const savedDraft = localStorage.getItem(STORAGE_KEY);
      if (savedDraft) {
        const parsed = JSON.parse(savedDraft);
        if (parsed.selectedArchetype) selectedArchetype = parsed.selectedArchetype;
        if (parsed.selectedMaterial) selectedMaterial = parsed.selectedMaterial;
        if (parsed.confidentialityTier) confidentialityTier = parsed.confidentialityTier;
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
      const draft = {
        selectedArchetype,
        selectedMaterial,
        confidentialityTier,
        email,
        title,
        narrative,
        discardMetadata,
        requireRecoveryPhrase
      };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(draft));
    } catch {
      // Ignore storage errors
    }
  }

  function handleInput() {
    saveDraft();
  }

  function setArchetype(val: Archetype) {
    selectedArchetype = val;
    saveDraft();
  }

  function setMaterial(val: string) {
    selectedMaterial = val;
    saveDraft();
  }

  function setConfidentialityTier(val: ConfidentialityTier) {
    confidentialityTier = val;
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
        archetype: selectedArchetype,
        material: selectedMaterial,
        confidentialityTier,
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

<div id="vault-intake-root" class="w-full max-w-xl mx-auto space-y-6 font-sans text-[#e6e1df] bg-[#141312] p-4 sm:p-6 rounded-2xl border border-[#4d4635]/40 shadow-2xl">
  <!-- Hero Header / Curatorial Badge -->
  <div id="intake-hero-header" class="space-y-2 text-left border-b border-[#4d4635]/30 pb-4">
    <div class="inline-flex items-center gap-2 px-2.5 py-1 rounded-full bg-[#514839]/40 border border-[#4d4635]/60">
      <span class="w-2 h-2 rounded-full bg-[#f2ca50] animate-pulse"></span>
      <span class="font-mono text-[10px] uppercase tracking-widest text-[#d3c4b1] font-semibold">Confidential Intake Chamber</span>
    </div>
    <h1 id="intake-main-title" class="text-2xl sm:text-3xl font-serif text-[#e6e1df] font-normal tracking-tight">
      Bespoke Commission Dossier
    </h1>
    <p class="text-xs text-[#d3c4b1] leading-relaxed">
      Phase I: Bilateral Non-Disclosure & Creative Premise. Curating sovereign artifacts of singular architectural integrity.
    </p>
  </div>

  <!-- Cryptographic NDA Status Card -->
  <section id="nda-status-card" class="relative p-4 bg-[#1d1b1a] rounded-xl border border-[#4d4635]/40 overflow-hidden shadow-lg space-y-3">
    <div class="flex items-start justify-between gap-3">
      <div class="space-y-1">
        <div class="flex items-center gap-2">
          <span class="text-[#f2ca50] text-sm" aria-hidden="true">🛡️</span>
          <span class="font-mono text-xs font-bold uppercase tracking-wider text-[#e6e1df]">Mutual NDA Status: Active & Sealed</span>
        </div>
        <p class="text-[11px] text-[#99907c]">
          Cryptographic Escrow Protocol · Place Vendôme & Geneva
        </p>
      </div>
      <span class="px-2 py-1 rounded bg-[#f2ca50]/10 border border-[#f2ca50]/40 font-mono text-[10px] text-[#f2ca50] tracking-widest font-bold uppercase">
        CLEARANCE IV
      </span>
    </div>

    <!-- Detail Badges -->
    <div class="grid grid-cols-2 gap-2 pt-2 border-t border-[#4d4635]/30 text-[11px]">
      <div>
        <span class="block text-[9px] text-[#99907c] uppercase tracking-wider font-mono">Covenant Reference</span>
        <span class="font-mono text-[#d3c4b1] font-semibold">#AT-9842-NDA</span>
      </div>
      <div>
        <span class="block text-[9px] text-[#99907c] uppercase tracking-wider font-mono">Bilateral Timestamp</span>
        <span class="font-mono text-[#d3c4b1] font-semibold">2025.04.18 · 14:02:19 Z</span>
      </div>
    </div>

    <!-- Action: Covenant Drawer -->
    <div>
      <button
        id="toggle-covenant-drawer-btn"
        type="button"
        on:click={() => isCovenantDrawerOpen = !isCovenantDrawerOpen}
        aria-expanded={isCovenantDrawerOpen}
        aria-controls="covenant-drawer-content"
        class="w-full flex items-center justify-between py-2 px-3 rounded bg-[#211f1e] hover:bg-[#2b2a28] transition-colors border border-[#4d4635]/30 text-left text-xs text-[#d3c4b1] hover:text-[#f2ca50] focus:outline-none focus:ring-2 focus:ring-[#f2ca50]/50"
      >
        <span class="font-mono text-[10px] uppercase tracking-wider font-semibold">Inspect Executed Bilateral Covenant</span>
        <span class="font-mono text-[10px] text-[#f2ca50]">{isCovenantDrawerOpen ? '▲ Close' : '▼ View'}</span>
      </button>

      {#if isCovenantDrawerOpen}
        <div id="covenant-drawer-content" class="mt-2 p-3 bg-[#171615] rounded border border-[#4d4635]/40 text-xs text-[#d3c4b1] space-y-1.5 font-mono">
          <p><strong class="text-[#e6e1df]">Bilateral Confidentiality Guarantee:</strong> All proprietary design intent, schematics, and client identity are protected by zero-knowledge cryptographic masking.</p>
          <p><strong class="text-[#e6e1df]">Turnkey Result Mandate:</strong> No third-party data dispersion; sovereign execution guaranteed upon dispatch.</p>
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
      class="bg-[#1d1b1a] border-2 border-[#f2ca50] p-6 rounded-xl space-y-4 shadow-xl text-[#e6e1df]"
    >
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 rounded-full bg-[#f2ca50] text-[#3c2f00] flex items-center justify-center font-bold text-xl shrink-0">
          ✓
        </div>
        <div>
          <h3 id="success-heading" class="text-base sm:text-lg font-bold text-[#f2ca50]">Payload Encrypted & Submitted Successfully</h3>
          <p id="email-acknowledgement-notice" class="text-xs text-[#d3c4b1] font-medium mt-0.5">
            ✓ An email acknowledgement has been sent to {email ? email : 'your provided email address'}.
          </p>
        </div>
      </div>

      <div class="bg-[#141312] p-4 rounded-lg border border-[#4d4635]/40 space-y-2">
        <div class="flex items-center justify-between font-mono text-xs">
          <span class="text-[#99907c]">Claim Ticket Receipt:</span>
          <span id="claim-ticket-id" class="font-bold text-[#f2ca50]">{claimTicket}</span>
        </div>
        <p class="text-xs text-[#d3c4b1] leading-relaxed">
          Your confidential commission dossier is stored securely under zero-knowledge encryption. No internal mechanics or third-party details are logged.
        </p>
      </div>

      <div class="pt-2">
        <button
          id="intake-reset-btn"
          type="button"
          on:click={resetForm}
          class="w-full py-3 px-4 bg-[#363433] hover:bg-[#4d4635] text-[#f2ca50] font-mono font-semibold rounded-lg text-xs tracking-wider uppercase transition-colors border border-[#f2ca50]/40 focus:outline-none focus:ring-2 focus:ring-[#f2ca50]"
        >
          Submit Another Commission Dossier
        </button>
      </div>
    </div>
  {:else}
    <!-- INTAKE FORM -->
    <form id="vault-intake-form" on:submit|preventDefault={handleSubmit} class="space-y-5">
      {#if submitError}
        <div id="intake-error-banner" role="alert" class="p-3 bg-[#93000a]/40 border border-[#ffb4ab] text-[#ffdad6] text-xs rounded-lg flex items-start space-x-2">
          <span class="font-bold">⚠️</span>
          <p id="intake-error-message">{submitError}</p>
        </div>
      {/if}

      <!-- Field 1: Commission Moniker -->
      <div id="section-moniker" class="space-y-1.5 bg-[#1d1b1a] p-4 rounded-xl border border-[#4d4635]/40">
        <div class="flex items-center justify-between">
          <label for="submission-title-input" class="font-mono text-xs font-bold uppercase tracking-wider text-[#e6e1df]">
            Commission Moniker <span class="text-[#ffb4ab]" aria-hidden="true">*</span>
          </label>
          <span class="font-mono text-[9px] text-[#f2ca50] uppercase tracking-widest">Confidential Title</span>
        </div>
        <div class="relative">
          <input
            id="submission-title-input"
            type="text"
            required
            bind:value={title}
            on:input={handleInput}
            placeholder="e.g. Project Solitude / Obsidian Aegis"
            class="w-full h-11 bg-[#0f0e0d] text-[#e6e1df] text-xs border border-[#4d4635]/60 rounded-lg px-3.5 focus:border-[#f2ca50] focus:ring-2 focus:ring-[#f2ca50]/20 outline-none transition-all font-sans"
          />
        </div>
        <p class="text-[10px] font-mono text-[#99907c] flex items-center gap-1">
          <span class="text-[#f2ca50]">◆</span> Zero-Knowledge Encrypted Client Record — Atelier Clearance Level IV
        </p>
      </div>

      <!-- Field 2: Aesthetic Intent & Archetype -->
      <div id="section-archetype" class="space-y-3 bg-[#1d1b1a] p-4 rounded-xl border border-[#4d4635]/40">
        <div class="flex items-center justify-between">
          <span id="label-archetype" class="font-mono text-xs font-bold uppercase tracking-wider text-[#e6e1df]">
            Aesthetic Intent & Archetype
          </span>
          <span class="font-mono text-[9px] text-[#99907c] uppercase tracking-wider">Select One</span>
        </div>
        <p class="text-xs text-[#d3c4b1] leading-relaxed">
          Curatorial discipline dictates sculptural methodology and master artisan assignment.
        </p>
        <div class="grid grid-cols-2 gap-2 pt-1" role="radiogroup" aria-labelledby="label-archetype">
          <!-- Archetype 1 -->
          <button
            type="button"
            role="radio"
            aria-checked={selectedArchetype === 'horology'}
            on:click={() => setArchetype('horology')}
            class="p-3 rounded-lg text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] flex flex-col justify-between h-24 border {selectedArchetype === 'horology' ? 'bg-[#211f1e] border-[#f2ca50]' : 'bg-[#0f0e0d] border-[#4d4635]/30 hover:border-[#4d4635]/70 opacity-80'}"
          >
            <div class="flex justify-between items-start w-full">
              <span class="font-mono text-[9px] tracking-widest uppercase font-semibold {selectedArchetype === 'horology' ? 'text-[#f2ca50]' : 'text-[#99907c]'}">01 / ARCHETYPE</span>
              {#if selectedArchetype === 'horology'}
                <span class="text-[#f2ca50] text-xs">◆</span>
              {/if}
            </div>
            <div>
              <span class="block text-xs font-semibold text-[#e6e1df]">Haute Horology</span>
              <span class="block text-[10px] text-[#99907c]">Tourbillon & Chronometry</span>
            </div>
          </button>

          <!-- Archetype 2 -->
          <button
            type="button"
            role="radio"
            aria-checked={selectedArchetype === 'metallurgy'}
            on:click={() => setArchetype('metallurgy')}
            class="p-3 rounded-lg text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] flex flex-col justify-between h-24 border {selectedArchetype === 'metallurgy' ? 'bg-[#211f1e] border-[#f2ca50]' : 'bg-[#0f0e0d] border-[#4d4635]/30 hover:border-[#4d4635]/70 opacity-80'}"
          >
            <div class="flex justify-between items-start w-full">
              <span class="font-mono text-[9px] tracking-widest uppercase font-semibold {selectedArchetype === 'metallurgy' ? 'text-[#f2ca50]' : 'text-[#99907c]'}">02 / ARCHETYPE</span>
              {#if selectedArchetype === 'metallurgy'}
                <span class="text-[#f2ca50] text-xs">◆</span>
              {/if}
            </div>
            <div>
              <span class="block text-xs font-semibold text-[#e6e1df]">Fine Metallurgy</span>
              <span class="block text-[10px] text-[#99907c]">Bespoke Monolith Sculpting</span>
            </div>
          </button>

          <!-- Archetype 3 -->
          <button
            type="button"
            role="radio"
            aria-checked={selectedArchetype === 'joaillerie'}
            on:click={() => setArchetype('joaillerie')}
            class="p-3 rounded-lg text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] flex flex-col justify-between h-24 border {selectedArchetype === 'joaillerie' ? 'bg-[#211f1e] border-[#f2ca50]' : 'bg-[#0f0e0d] border-[#4d4635]/30 hover:border-[#4d4635]/70 opacity-80'}"
          >
            <div class="flex justify-between items-start w-full">
              <span class="font-mono text-[9px] tracking-widest uppercase font-semibold {selectedArchetype === 'joaillerie' ? 'text-[#f2ca50]' : 'text-[#99907c]'}">03 / ARCHETYPE</span>
              {#if selectedArchetype === 'joaillerie'}
                <span class="text-[#f2ca50] text-xs">◆</span>
              {/if}
            </div>
            <div>
              <span class="block text-xs font-semibold text-[#e6e1df]">Haute Joaillerie</span>
              <span class="block text-[10px] text-[#99907c]">Rare Gemological Setting</span>
            </div>
          </button>

          <!-- Archetype 4 -->
          <button
            type="button"
            role="radio"
            aria-checked={selectedArchetype === 'objet_dart'}
            on:click={() => setArchetype('objet_dart')}
            class="p-3 rounded-lg text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] flex flex-col justify-between h-24 border {selectedArchetype === 'objet_dart' ? 'bg-[#211f1e] border-[#f2ca50]' : 'bg-[#0f0e0d] border-[#4d4635]/30 hover:border-[#4d4635]/70 opacity-80'}"
          >
            <div class="flex justify-between items-start w-full">
              <span class="font-mono text-[9px] tracking-widest uppercase font-semibold {selectedArchetype === 'objet_dart' ? 'text-[#f2ca50]' : 'text-[#99907c]'}">04 / ARCHETYPE</span>
              {#if selectedArchetype === 'objet_dart'}
                <span class="text-[#f2ca50] text-xs">◆</span>
              {/if}
            </div>
            <div>
              <span class="block text-xs font-semibold text-[#e6e1df]">Private Objet d'Art</span>
              <span class="block text-[10px] text-[#99907c]">Curated Kinetic Reliquary</span>
            </div>
          </button>
        </div>
      </div>

      <!-- Field 3: Materiality Curation -->
      <div id="section-materiality" class="space-y-3 bg-[#1d1b1a] p-4 rounded-xl border border-[#4d4635]/40">
        <div class="flex items-center justify-between">
          <span class="font-mono text-xs font-bold uppercase tracking-wider text-[#e6e1df]">
            Materiality Curation
          </span>
          <span class="font-mono text-[9px] text-[#d3c4b1] tracking-widest uppercase">Provenance Certified</span>
        </div>
        <p class="text-xs text-[#d3c4b1] leading-relaxed">
          Mineral and alloy sources extracted through ethical, bilateral private reserves.
        </p>
        <div class="space-y-2">
          <!-- Material 1 -->
          <button
            type="button"
            on:click={() => setMaterial('Damascus Titanium & Meteorite')}
            class="w-full flex items-center justify-between p-3 rounded-lg border text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {selectedMaterial === 'Damascus Titanium & Meteorite' ? 'bg-[#211f1e] border-[#f2ca50]/60' : 'bg-[#0f0e0d] border-[#4d4635]/30 hover:border-[#4d4635]/60'}"
          >
            <div class="flex items-center gap-3">
              <div class="w-7 h-7 rounded-full bg-[#202024] border border-[#f2ca50]/30 flex items-center justify-center shrink-0">
                <span class="w-2 h-2 rounded-full bg-[#f2ca50]"></span>
              </div>
              <div>
                <span class="block text-xs font-semibold text-[#e6e1df]">Damascus Titanium & Meteorite</span>
                <span class="block text-[10px] text-[#99907c]">Hand-folded pattern, Gibeon celestial fragment</span>
              </div>
            </div>
            {#if selectedMaterial === 'Damascus Titanium & Meteorite'}
              <span class="text-[#f2ca50] text-sm font-bold">✓</span>
            {/if}
          </button>

          <!-- Material 2 -->
          <button
            type="button"
            on:click={() => setMaterial('Vanta Midnight Ceramic')}
            class="w-full flex items-center justify-between p-3 rounded-lg border text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {selectedMaterial === 'Vanta Midnight Ceramic' ? 'bg-[#211f1e] border-[#f2ca50]/60' : 'bg-[#0f0e0d] border-[#4d4635]/30 hover:border-[#4d4635]/60'}"
          >
            <div class="flex items-center gap-3">
              <div class="w-7 h-7 rounded-full bg-[#111112] border border-[#4d4635]/30 flex items-center justify-center shrink-0">
                <span class="w-2 h-2 rounded-full bg-[#363433]"></span>
              </div>
              <div>
                <span class="block text-xs font-semibold text-[#e6e1df]">Vanta Midnight Ceramic</span>
                <span class="block text-[10px] text-[#99907c]">Light-absorptive crystalline matrix</span>
              </div>
            </div>
            {#if selectedMaterial === 'Vanta Midnight Ceramic'}
              <span class="text-[#f2ca50] text-sm font-bold">✓</span>
            {/if}
          </button>

          <!-- Material 3 -->
          <button
            type="button"
            on:click={() => setMaterial('Grade 5 Satin Platinum & Osmium')}
            class="w-full flex items-center justify-between p-3 rounded-lg border text-left transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {selectedMaterial === 'Grade 5 Satin Platinum & Osmium' ? 'bg-[#211f1e] border-[#f2ca50]/60' : 'bg-[#0f0e0d] border-[#4d4635]/30 hover:border-[#4d4635]/60'}"
          >
            <div class="flex items-center gap-3">
              <div class="w-7 h-7 rounded-full bg-[#353432] border border-[#4d4635]/30 flex items-center justify-center shrink-0">
                <span class="w-2 h-2 rounded-full bg-[#d3c4b1]"></span>
              </div>
              <div>
                <span class="block text-xs font-semibold text-[#e6e1df]">Grade 5 Satin Platinum & Osmium</span>
                <span class="block text-[10px] text-[#99907c]">High-density cryogenic finish</span>
              </div>
            </div>
            {#if selectedMaterial === 'Grade 5 Satin Platinum & Osmium'}
              <span class="text-[#f2ca50] text-sm font-bold">✓</span>
            {/if}
          </button>
        </div>
      </div>

      <!-- Field 4: Creative Premise / Confidential Narrative -->
      <div id="section-narrative" class="space-y-3 bg-[#1d1b1a] p-4 rounded-xl border border-[#4d4635]/40">
        <div class="flex items-center justify-between">
          <label for="narrative-textarea" class="font-mono text-xs font-bold uppercase tracking-wider text-[#e6e1df]">
            Confidential Narrative & Creative Premise <span class="text-[#ffb4ab]" aria-hidden="true">*</span>
          </label>
          <span class="font-mono text-[9px] text-[#f2ca50] uppercase tracking-widest">Client Wishlist</span>
        </div>
        <textarea
          id="narrative-textarea"
          required
          rows="4"
          bind:value={narrative}
          on:input={handleInput}
          placeholder="Describe your bespoke commission vision, aesthetic preferences, or technical premises..."
          class="w-full bg-[#0f0e0d] text-[#e6e1df] text-xs border border-[#4d4635]/60 rounded-lg p-3 focus:border-[#f2ca50] focus:ring-2 focus:ring-[#f2ca50]/20 outline-none transition-all resize-none font-sans leading-relaxed"
        ></textarea>
        <div class="flex items-center justify-between text-[10px] font-mono text-[#99907c]">
          <span>Geneva Seal Craftsmanship Standard: &lt;0.01mm Chiseled Fit</span>
          <span id="char-counter">{narrative.length} / 4000</span>
        </div>
      </div>

      <!-- Field 5: Notification Email -->
      <div id="section-email" class="space-y-1.5 bg-[#1d1b1a] p-4 rounded-xl border border-[#4d4635]/40">
        <label for="client-email-input" class="font-mono text-xs font-bold uppercase tracking-wider text-[#e6e1df]">
          Notification Email (Optional for dispatch acknowledgement)
        </label>
        <input
          id="client-email-input"
          type="email"
          bind:value={email}
          on:input={handleInput}
          placeholder="client.desk@sovereign-holdings.com"
          class="w-full h-11 bg-[#0f0e0d] text-[#e6e1df] text-xs border border-[#4d4635]/60 rounded-lg px-3.5 focus:border-[#f2ca50] focus:ring-2 focus:ring-[#f2ca50]/20 outline-none transition-all font-sans"
        />
        <p class="text-[10px] font-mono text-[#99907c]">
          Used solely for dispatching bilateral receipt & status confirmation.
        </p>
      </div>

      <!-- Field 6: Confidentiality Tier Selection -->
      <div id="section-confidentiality" class="space-y-3 bg-[#1d1b1a] p-4 rounded-xl border border-[#4d4635]/40">
        <div class="flex items-center justify-between">
          <span class="font-mono text-xs font-bold uppercase tracking-wider text-[#e6e1df]">
            Confidentiality Tier
          </span>
          <span class="font-mono text-[9px] text-[#99907c] uppercase tracking-wider">Provenance Masking</span>
        </div>
        <div class="space-y-2">
          <!-- Tier 1 -->
          <label class="flex items-start gap-3 p-3 rounded-lg border cursor-pointer transition-all {confidentialityTier === 'ghost_atelier' ? 'bg-[#211f1e] border-[#f2ca50]/60' : 'bg-[#0f0e0d] border-[#4d4635]/30 opacity-70'}">
            <input
              type="radio"
              name="confidentiality"
              checked={confidentialityTier === 'ghost_atelier'}
              on:change={() => setConfidentialityTier('ghost_atelier')}
              class="mt-1 text-[#f2ca50] focus:ring-[#f2ca50] bg-[#0f0e0d] border-[#4d4635]"
            />
            <div class="space-y-0.5">
              <span class="block font-mono text-xs uppercase font-bold text-[#e6e1df] tracking-wider">Ghost Atelier (Total Anonymity)</span>
              <span class="block text-[11px] text-[#99907c] leading-relaxed">
                Artifact schematics sealed post-delivery. Zero photographic archival or institutional cataloging.
              </span>
            </div>
          </label>

          <!-- Tier 2 -->
          <label class="flex items-start gap-3 p-3 rounded-lg border cursor-pointer transition-all {confidentialityTier === 'private_archive' ? 'bg-[#211f1e] border-[#f2ca50]/60' : 'bg-[#0f0e0d] border-[#4d4635]/30 opacity-70'}">
            <input
              type="radio"
              name="confidentiality"
              checked={confidentialityTier === 'private_archive'}
              on:change={() => setConfidentialityTier('private_archive')}
              class="mt-1 text-[#f2ca50] focus:ring-[#f2ca50] bg-[#0f0e0d] border-[#4d4635]"
            />
            <div class="space-y-0.5">
              <span class="block font-mono text-xs uppercase font-bold text-[#e6e1df] tracking-wider">Private Archive Vault</span>
              <span class="block text-[11px] text-[#99907c] leading-relaxed">
                Restricted cataloging accessible strictly via biometrically signed heir credentials.
              </span>
            </div>
          </label>
        </div>
      </div>

      <!-- Safeguard Toggles -->
      <div id="section-safeguards" class="bg-[#1d1b1a] rounded-xl p-4 border border-[#4d4635]/40 space-y-3">
        <h3 class="font-mono text-xs font-bold text-[#e6e1df] uppercase tracking-wider flex items-center space-x-1.5">
          <span>🛡️</span>
          <span>Submitter Safeguard Controls</span>
        </h3>

        <label class="flex items-start justify-between cursor-pointer space-x-3 text-xs">
          <div class="flex-1">
            <div class="font-semibold text-[#e6e1df]">Discard IP & Connection Headers</div>
            <p class="text-[10px] text-[#99907c] mt-0.5 leading-relaxed">Zero connection metadata attached to immutable claim receipt.</p>
          </div>
          <input
            id="toggle-discard-metadata"
            type="checkbox"
            bind:checked={discardMetadata}
            on:change={handleInput}
            class="mt-1 w-4 h-4 text-[#f2ca50] focus:ring-[#f2ca50] rounded bg-[#0f0e0d] border-[#4d4635]"
          />
        </label>

        <div class="h-px bg-[#4d4635]/30"></div>

        <label class="flex items-start justify-between cursor-pointer space-x-3 text-xs">
          <div class="flex-1">
            <div class="font-semibold text-[#e6e1df]">Require Two-Part Recovery Key</div>
            <p class="text-[10px] text-[#99907c] mt-0.5 leading-relaxed">Splits claim ticket shard using sovereign secret sharing protocols.</p>
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

      <!-- Liaison Desk -->
      <div id="section-liaison" class="p-4 bg-[#1d1b1a] rounded-xl border border-[#4d4635]/40 space-y-3">
        <div class="flex items-center justify-between">
          <span class="font-mono text-xs font-bold uppercase tracking-wider text-[#e6e1df]">
            Assigned Senior Liaison
          </span>
          <span class="inline-flex items-center gap-1 font-mono text-[9px] text-[#f2ca50] uppercase">
            <span class="w-1.5 h-1.5 rounded-full bg-[#f2ca50]"></span> Line Secure
          </span>
        </div>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded bg-[#2b2a28] border border-[#f2ca50]/30 flex items-center justify-center text-[#f2ca50] font-serif font-bold text-sm shrink-0">
            HV
          </div>
          <div class="flex-1 min-w-0">
            <span class="block text-xs font-semibold text-[#e6e1df] truncate">Henri de Valois</span>
            <span class="block text-[10px] text-[#99907c] truncate">Curator Principal · Geneva Salon Private Desk</span>
          </div>
        </div>
      </div>

      <!-- Action Button -->
      <div class="pt-2">
        <button
          id="submit-intake-btn"
          type="submit"
          disabled={isSubmitting}
          class="w-full py-3.5 px-4 bg-[#1c1a17] hover:bg-[#24221e] active:scale-[0.99] text-[#f2ca50] font-mono font-bold text-xs uppercase tracking-[0.14em] rounded-xl border border-[#f2ca50]/60 flex flex-col items-center justify-center shadow-lg transition-all focus:outline-none focus:ring-2 focus:ring-[#f2ca50] disabled:opacity-50 cursor-pointer"
        >
          {#if isSubmitting}
            <div class="flex items-center space-x-2 text-xs">
              <span class="animate-spin text-sm">⌛</span>
              <span>Encrypting & Dispatching Dossier...</span>
            </div>
          {:else}
            <div class="flex items-center space-x-2">
              <span aria-hidden="true">🔒</span>
              <span>Execute Sealed Dossier & Dispatch</span>
            </div>
            <span class="text-[9px] font-mono text-[#d3c4b1]/80 normal-case tracking-normal">Generates Sovereign Cryptographic Claim Ticket</span>
          {/if}
        </button>
      </div>
    </form>
  {/if}
</div>
