<script lang="ts">
  let codename = '';
  let confidentialityTier = 'Air-Gapped Vault';
  let engagementScope = 'Artisan Precision Spec';
  let executionWindow = 'Q4 2025 Handcrafted Rollout';
  let legalAccepted = false;

  let isSubmitted = false;

  function handleSubmit() {
    if (!legalAccepted) return;
    isSubmitted = true;
  }
</script>

{#if isSubmitted}
  <div class="p-6 bg-surface-container-low border border-primary-container/40 rounded shadow-sm space-y-4">
    <div class="flex items-center space-x-3 text-primary">
      <span class="material-symbols-outlined text-[24px]">verified_user</span>
      <h2 class="font-headline-sm text-headline-sm tracking-wide text-on-surface">Secure Protocol Initiated</h2>
    </div>
    <p class="font-body-sm text-body-sm text-secondary leading-relaxed">
      Your specification has been successfully received and securely escrowed.
      Our executive team will review the mandate under strict mutual non-disclosure and contact you via the private channel.
    </p>
    <p class="font-label-mono text-label-mono text-tertiary">
      RECEIPT // #VLT-{Math.floor(Math.random() * 100000)}
    </p>
  </div>
{:else}
  <form on:submit|preventDefault={handleSubmit} class="w-full max-w-2xl mx-auto space-y-6">
    <!-- SUB-HEADER SECURITY TELEMETRY BAR -->
    <div class="flex items-center justify-between border-b border-outline-variant/30 pb-2">
      <div class="flex items-center space-x-1.5">
        <span class="material-symbols-outlined text-tertiary text-[14px]" aria-hidden="true">lock</span>
        <span class="font-label-caps text-label-caps text-tertiary tracking-wider">256-BIT MUTUAL NDA ACTIVE</span>
      </div>
      <span class="font-label-caps text-label-caps text-outline">SESSION // #VLT-88219-X</span>
    </div>

    <!-- 1. CONFIDENTIALITY BANNER -->
    <section class="relative bg-surface-container-low border border-primary-container/40 p-4 rounded shadow-sm">
      <div class="flex items-start space-x-3">
        <div class="p-1.5 bg-surface-container border border-primary-container/30 rounded flex items-center justify-center">
          <span class="material-symbols-outlined text-primary text-[18px]" aria-hidden="true">verified_user</span>
        </div>
        <div class="space-y-1">
          <div class="flex items-center space-x-2">
            <h2 class="font-headline-sm text-headline-sm text-on-surface tracking-wide">Bilateral Non-Disclosure Agreement</h2>
            <span class="px-1.5 py-0.5 rounded bg-surface border border-outline-variant/60 font-label-caps text-label-caps text-primary">ENFORCED</span>
          </div>
          <p class="font-body-sm text-body-sm text-secondary leading-relaxed">
            All uploaded schematics, target volumes, and trade parameters are air-gapped from production vendors. Direct confidential transmission strictly via institutional escrow protocol.
          </p>
        </div>
      </div>
    </section>

    <!-- 2. EXECUTIVE SPECIFICATION FORM -->
    <section class="space-y-6">
      <div class="flex items-center justify-between border-b border-outline-variant/30 pb-1">
        <span class="font-label-caps text-label-caps text-outline">SECTION 01 // MANDATE IDENTIFICATION</span>
        <span class="font-label-caps text-label-caps text-primary">PHASE I OF III</span>
      </div>

      <!-- Initiative Codename -->
      <div class="space-y-1.5">
        <div class="flex justify-between items-center">
          <label class="font-label-caps text-label-caps text-on-surface tracking-wider" for="codename">
            INITIATIVE CODENAME
          </label>
          <span class="font-label-caps text-label-caps text-tertiary/80">ENCRYPTED_FIELD::TLS_1.3</span>
        </div>
        <div class="relative group">
          <input
            bind:value={codename}
            id="codename"
            type="text"
            required
            placeholder="Specify codename or identifier"
            class="w-full bg-surface-container-low border border-outline-variant/60 focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface font-body-lg text-body-lg px-3 py-2.5 rounded transition-colors duration-150 group-hover:border-outline outline-none"
          />
          <div class="absolute right-3 top-1/2 -translate-y-1/2" aria-hidden="true">
            <span class="material-symbols-outlined text-outline text-[16px]">fingerprint</span>
          </div>
        </div>
      </div>

      <!-- Confidentiality Tier Pill Selector -->
      <div class="space-y-2" role="radiogroup" aria-labelledby="confidentiality-tier-label">
        <div class="flex justify-between items-center">
          <span id="confidentiality-tier-label" class="font-label-caps text-label-caps text-on-surface tracking-wider">
            CONFIDENTIALITY TIER
          </span>
          <span class="font-label-caps text-label-caps text-outline">ISOLATION LEVEL</span>
        </div>
        <div class="grid grid-cols-3 gap-1.5 p-1 bg-surface-container-lowest border border-outline-variant/40 rounded">
          <button
            type="button"
            role="radio"
            aria-checked={confidentialityTier === 'Standard Private'}
            on:click={() => confidentialityTier = 'Standard Private'}
            class="py-2 px-1 text-center rounded border {confidentialityTier === 'Standard Private' ? 'bg-surface-container border-primary-container/70 text-primary shadow-sm font-medium' : 'border-transparent text-on-surface-variant hover:text-on-surface'} font-label-caps text-label-caps transition-colors outline-none focus-visible:ring-2 focus-visible:ring-primary-container"
          >
            Standard Private
          </button>
          <button
            type="button"
            role="radio"
            aria-checked={confidentialityTier === 'Air-Gapped Vault'}
            on:click={() => confidentialityTier = 'Air-Gapped Vault'}
            class="py-2 px-1 text-center rounded border {confidentialityTier === 'Air-Gapped Vault' ? 'bg-surface-container border-primary-container/70 text-primary shadow-sm font-medium' : 'border-transparent text-on-surface-variant hover:text-on-surface'} font-label-caps text-label-caps transition-colors outline-none focus-visible:ring-2 focus-visible:ring-primary-container"
          >
            Air-Gapped Vault
          </button>
          <button
            type="button"
            role="radio"
            aria-checked={confidentialityTier === 'Sovereign NDA'}
            on:click={() => confidentialityTier = 'Sovereign NDA'}
            class="py-2 px-1 text-center rounded border {confidentialityTier === 'Sovereign NDA' ? 'bg-surface-container border-primary-container/70 text-primary shadow-sm font-medium' : 'border-transparent text-on-surface-variant hover:text-on-surface'} font-label-caps text-label-caps transition-colors outline-none focus-visible:ring-2 focus-visible:ring-primary-container"
          >
            Sovereign NDA
          </button>
        </div>
      </div>

      <!-- Engagement Scope -->
      <div class="space-y-2.5">
        <div class="flex justify-between items-center">
          <span class="font-label-caps text-label-caps text-on-surface tracking-wider">
            ENGAGEMENT SCOPE
          </span>
          <span class="font-label-caps text-label-caps text-outline">WHITE-GLOVE LEVEL</span>
        </div>
        <div class="grid grid-cols-1 gap-2">
          <!-- Card 1 -->
          <label class="relative flex items-start p-3 bg-surface-container-low border {engagementScope === 'Artisan Precision Spec' ? 'border-primary-container/80 bg-surface-container' : 'border-outline-variant/50'} rounded cursor-pointer transition-all hover:bg-surface-container hover:border-outline focus-within:ring-2 focus-within:ring-primary-container">
            <input
              type="radio"
              name="scope"
              value="Artisan Precision Spec"
              bind:group={engagementScope}
              class="sr-only"
            />
            <div class="flex-shrink-0 mt-1">
               <div class="w-4 h-4 rounded-full border border-outline-variant flex items-center justify-center {engagementScope === 'Artisan Precision Spec' ? 'border-primary-container' : ''}">
                 {#if engagementScope === 'Artisan Precision Spec'}
                   <div class="w-2 h-2 rounded-full bg-primary-container"></div>
                 {/if}
               </div>
            </div>
            <div class="ml-3 space-y-0.5">
              <div class="flex items-center space-x-2">
                <span class="font-headline-sm text-headline-sm text-on-surface">Artisan Precision Spec</span>
                <span class="px-1 py-0.5 rounded bg-primary-container/20 text-primary font-label-caps text-label-caps text-[10px]">SUB-MICRON</span>
              </div>
              <p class="font-body-sm text-body-sm text-secondary leading-snug">
                Bespoke high-tolerance master tooling, dedicated atelier calibration, and segregated clean-room machining.
              </p>
            </div>
          </label>
          <!-- Card 2 -->
          <label class="relative flex items-start p-3 bg-surface-container-low border {engagementScope === 'Atelier Pilot Run' ? 'border-primary-container/80 bg-surface-container' : 'border-outline-variant/50'} rounded cursor-pointer transition-all hover:bg-surface-container hover:border-outline focus-within:ring-2 focus-within:ring-primary-container">
            <input
              type="radio"
              name="scope"
              value="Atelier Pilot Run"
              bind:group={engagementScope}
              class="sr-only"
            />
            <div class="flex-shrink-0 mt-1">
               <div class="w-4 h-4 rounded-full border border-outline-variant flex items-center justify-center {engagementScope === 'Atelier Pilot Run' ? 'border-primary-container' : ''}">
                 {#if engagementScope === 'Atelier Pilot Run'}
                   <div class="w-2 h-2 rounded-full bg-primary-container"></div>
                 {/if}
               </div>
            </div>
            <div class="ml-3 space-y-0.5">
              <div class="flex items-center space-x-2">
                <span class="font-headline-sm text-headline-sm text-on-surface">Atelier Pilot Run</span>
                <span class="font-label-caps text-label-caps text-outline text-[10px]">N=50 TO 500</span>
              </div>
              <p class="font-body-sm text-body-sm text-secondary leading-snug">
                Discreet initial validation batch fabricated by master artisans under full non-disclosure containment.
              </p>
            </div>
          </label>
          <!-- Card 3 -->
          <label class="relative flex items-start p-3 bg-surface-container-low border {engagementScope === 'Direct Executive Hand-off' ? 'border-primary-container/80 bg-surface-container' : 'border-outline-variant/50'} rounded cursor-pointer transition-all hover:bg-surface-container hover:border-outline focus-within:ring-2 focus-within:ring-primary-container">
            <input
              type="radio"
              name="scope"
              value="Direct Executive Hand-off"
              bind:group={engagementScope}
              class="sr-only"
            />
            <div class="flex-shrink-0 mt-1">
               <div class="w-4 h-4 rounded-full border border-outline-variant flex items-center justify-center {engagementScope === 'Direct Executive Hand-off' ? 'border-primary-container' : ''}">
                 {#if engagementScope === 'Direct Executive Hand-off'}
                   <div class="w-2 h-2 rounded-full bg-primary-container"></div>
                 {/if}
               </div>
            </div>
            <div class="ml-3 space-y-0.5">
              <div class="flex items-center space-x-2">
                <span class="font-headline-sm text-headline-sm text-on-surface">Direct Executive Hand-off</span>
                <span class="font-label-caps text-label-caps text-outline text-[10px]">SECURE COURIER</span>
              </div>
              <p class="font-body-sm text-body-sm text-secondary leading-snug">
                Direct peer-to-peer transport bypassing multi-tenant distribution nodes with end-to-end custody chains.
              </p>
            </div>
          </label>
        </div>
      </div>

      <!-- Target Execution Window -->
      <div class="space-y-1.5">
        <label class="font-label-caps text-label-caps text-on-surface tracking-wider block" for="execution-window">
          TARGET EXECUTION WINDOW
        </label>
        <div class="relative">
          <select
            bind:value={executionWindow}
            id="execution-window"
            class="w-full bg-surface-container-low border border-outline-variant/60 focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface font-body-md text-body-md px-3 py-2.5 rounded appearance-none cursor-pointer outline-none"
          >
            <option>Q4 2025 Handcrafted Rollout</option>
            <option>Q1 2026 Sovereign Deployment</option>
            <option>Expedited Atelier Velocity (30-Day Escrow)</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-outline">
            <span class="material-symbols-outlined text-[18px]">unfold_more</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 3. SECURE DOCUMENT & IP VAULT SECTION -->
    <section class="space-y-4 pt-2">
      <div class="flex items-center justify-between border-b border-outline-variant/30 pb-1">
        <span class="font-label-caps text-label-caps text-outline">SECTION 02 // PROPRIETARY IP REPOSITORY</span>
        <span class="font-label-caps text-label-caps text-tertiary">E2EE ACTIVATED</span>
      </div>
      <!-- Upload Zone -->
      <button type="button" class="w-full border border-dashed border-primary-container/60 bg-surface-container-lowest/80 rounded p-4 text-center hover:bg-surface-container-low transition-colors duration-150 cursor-pointer group outline-none focus-visible:ring-2 focus-visible:ring-primary-container">
        <div class="mx-auto w-10 h-10 rounded border border-outline-variant/60 bg-surface-container flex items-center justify-center mb-2 group-hover:border-primary-container/60 transition-colors">
          <span class="material-symbols-outlined text-primary text-[20px]" aria-hidden="true">lock</span>
        </div>
        <p class="font-headline-sm text-headline-sm text-on-surface">Drop Proprietary CAD / Schematics</p>
        <p class="font-caption text-caption text-outline mt-0.5">STEP, IGES, DXF, or encrypted archives up to 2.5 GB</p>
        <div class="mt-3 inline-flex items-center space-x-1.5 px-2.5 py-1 rounded bg-surface border border-outline-variant/40">
          <span class="material-symbols-outlined text-tertiary text-[13px]" aria-hidden="true">key</span>
          <span class="font-label-caps text-label-caps text-on-surface-variant">Zero vendor exposure — anonymized via cryptographic key</span>
        </div>
      </button>
    </section>

    <!-- 4. EXECUTIVE SIGN-OFF & SUBMISSION -->
    <section class="space-y-4 pt-2 pb-4">
      <div class="flex items-center justify-between border-b border-outline-variant/30 pb-1">
        <span class="font-label-caps text-label-caps text-outline">SECTION 03 // RATIFICATION</span>
      </div>
      <!-- Legal Checkbox -->
      <label class="flex items-start space-x-3 cursor-pointer group p-1 outline-none focus-within:ring-2 focus-within:ring-primary-container rounded">
        <input
          type="checkbox"
          bind:checked={legalAccepted}
          required
          class="vault-checkbox mt-0.5 w-4 h-4 rounded border-outline-variant bg-surface-container text-primary-container focus:ring-0 cursor-pointer"
        />
        <span class="font-body-md text-body-md text-on-surface group-hover:text-primary transition-colors select-none leading-tight">
          Execute mutual binding NDA &amp; initiate private partner briefing protocol.
        </span>
      </label>

      <!-- Primary Executive CTA -->
      <div class="space-y-2">
        <button
          type="submit"
          disabled={!legalAccepted}
          class="w-full bg-primary-container hover:bg-[#c49f27] text-surface font-headline-sm text-headline-sm py-3 px-4 rounded font-semibold tracking-wide transition-all duration-150 flex items-center justify-center space-x-2 shadow-sm active:opacity-95 disabled:opacity-50 disabled:cursor-not-allowed outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-offset-surface focus-visible:ring-primary-container"
        >
          <span>Transmit to Executive Partner</span>
          <span class="material-symbols-outlined text-[18px]" aria-hidden="true">arrow_forward</span>
        </button>
        <p class="font-caption text-caption text-center text-outline leading-tight">
          Direct partner routing. No intermediary brokers or factory disclosure.
        </p>
      </div>
    </section>
  </form>
{/if}

<style>
  /* Base Tailwind utility coverage for dynamic class building from the mockup */
  .bg-surface-container-low { background-color: #1b1b1f; }
  .bg-surface-container { background-color: #1f1f23; }
  .bg-surface-container-lowest { background-color: #0d0e11; }
  .bg-surface { background-color: #121316; }
  .border-primary-container { border-color: #d4af37; }
  .border-outline-variant { border-color: #4d4635; }
  .border-outline { border-color: #99907c; }
  .text-on-surface { color: #e3e2e6; }
  .text-on-surface-variant { color: #d0c5af; }
  .text-outline { color: #99907c; }
  .text-primary { color: #f2ca50; }
  .text-primary-container { color: #d4af37; }
  .text-secondary { color: #c2c7cd; }
  .text-tertiary { color: #a5dbb2; }
  .text-surface { color: #121316; }
  .bg-primary-container { background-color: #d4af37; }

  .font-headline-sm { font-family: 'Space Grotesk', sans-serif; font-size: 16px; font-weight: 600; line-height: 24px; }
  .font-body-sm { font-family: 'Manrope', sans-serif; font-size: 12px; font-weight: 400; line-height: 18px; }
  .font-body-md { font-family: 'Manrope', sans-serif; font-size: 14px; font-weight: 400; line-height: 22px; }
  .font-body-lg { font-family: 'Manrope', sans-serif; font-size: 16px; font-weight: 400; line-height: 26px; }
  .font-label-caps { font-family: 'JetBrains Mono', monospace; font-size: 11px; font-weight: 500; line-height: 16px; letter-spacing: 0.12em; text-transform: uppercase; }
  .font-label-mono { font-family: 'JetBrains Mono', monospace; font-size: 12px; font-weight: 400; line-height: 16px; letter-spacing: 0.02em; }
  .font-caption { font-family: 'Manrope', sans-serif; font-size: 11px; font-weight: 400; line-height: 15px; letter-spacing: 0.02em; }

  .vault-checkbox:checked {
    background-color: #d4af37;
    border-color: #d4af37;
    background-image: url("data:image/svg+xml,%3csvg viewBox='0 0 16 16' fill='%23121316' xmlns='http://www.w3.org/2000/svg'%3e%3cpath d='M12.207 4.793a1 1 0 010 1.414l-5 5a1 1 0 01-1.414 0l-2-2a1 1 0 011.414-1.414L6.5 9.086l4.293-4.293a1 1 0 011.414 0z'/%3e%3c/svg%3e");
  }
</style>
