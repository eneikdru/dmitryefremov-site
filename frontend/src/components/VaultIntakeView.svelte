<script lang="ts">
  let submitted = false;

  function handleSubmit(event: Event) {
    event.preventDefault();
    submitted = true;
  }
</script>

{#if submitted}
  <main class="w-full max-w-lg mx-auto px-gutter-mobile pt-space-md space-y-space-lg text-center mt-10">
    <div class="p-6 bg-surface-container-low border border-primary-container/40 rounded shadow-sm">
      <span class="material-symbols-outlined text-primary text-[48px] mb-4">verified_user</span>
      <h2 class="font-headline-sm text-headline-sm text-on-surface tracking-wide">Mandate Ratified</h2>
      <p class="font-body-sm text-body-sm text-secondary mt-2">
        Proprietary specifications have been encrypted and submitted via bilateral NDA protocol. Zero vendor exposure.
      </p>
    </div>
  </main>
{:else}
<!-- MAIN SCROLLABLE CONTENT CANVAS -->
<form on:submit={handleSubmit}>
<main class="w-full max-w-lg mx-auto px-gutter-mobile pt-space-md space-y-space-lg">
<!-- SUB-HEADER SECURITY TELEMETRY BAR -->
<div class="flex items-center justify-between border-b border-outline-variant/30 pb-space-xs">
<div class="flex items-center space-x-1.5">
<span class="material-symbols-outlined text-tertiary text-[14px]">lock</span>
<span class="font-label-caps text-label-caps text-tertiary tracking-wider">256-BIT MUTUAL NDA ACTIVE</span>
</div>
<span class="font-label-caps text-label-caps text-outline">SESSION // #VLT-88219-X</span>
</div>
<!-- 1. CONFIDENTIALITY BANNER -->
<section class="relative bg-surface-container-low border border-primary-container/40 p-space-md rounded shadow-sm">
<div class="flex items-start space-x-3">
<div class="p-1.5 bg-surface-container border border-primary-container/30 rounded flex items-center justify-center">
<span class="material-symbols-outlined text-primary text-[18px]">verified_user</span>
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
<section class="space-y-space-lg">
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
<input class="w-full bg-surface-container-low border border-outline-variant/60 focus:border-primary-container focus:ring-0 text-on-surface font-body-lg text-body-lg px-3 py-2.5 rounded transition-colors duration-150 group-hover:border-outline" id="codename" placeholder="Specify codename or identifier" type="text" value="Project Chimera // Series VII" required aria-required="true" />
<div class="absolute right-3 top-1/2 -translate-y-1/2">
<span class="material-symbols-outlined text-outline text-[16px]">fingerprint</span>
</div>
</div>
</div>
<!-- Confidentiality Tier Pill Selector -->
<div class="space-y-2">
<div class="flex justify-between items-center">
<span class="font-label-caps text-label-caps text-on-surface tracking-wider">
            CONFIDENTIALITY TIER
          </span>
<span class="font-label-caps text-label-caps text-outline">ISOLATION LEVEL</span>
</div>
<div class="grid grid-cols-3 gap-1.5 p-1 bg-surface-container-lowest border border-outline-variant/40 rounded">
<button class="py-2 px-1 text-center rounded border border-transparent font-label-caps text-label-caps text-on-surface-variant hover:text-on-surface transition-colors" type="button">
            Standard Private
          </button>
<button class="py-2 px-1 text-center rounded bg-surface-container border border-primary-container/70 font-label-caps text-label-caps text-primary font-medium shadow-sm" type="button">
            Air-Gapped Vault
          </button>
<button class="py-2 px-1 text-center rounded border border-transparent font-label-caps text-label-caps text-on-surface-variant hover:text-on-surface transition-colors" type="button">
            Sovereign NDA
          </button>
</div>
</div>
<!-- Engagement Scope (Reframing Manufacturing to White-Glove) -->
<div class="space-y-2.5">
<div class="flex justify-between items-center">
<span class="font-label-caps text-label-caps text-on-surface tracking-wider">
            ENGAGEMENT SCOPE
          </span>
<span class="font-label-caps text-label-caps text-outline">WHITE-GLOVE LEVEL</span>
</div>
<div class="grid grid-cols-1 gap-2">
<!-- Card 1 -->
<label class="relative flex items-start p-3 bg-surface-container-low border border-primary-container/80 rounded cursor-pointer transition-all hover:bg-surface-container">
<input checked class="mt-1 text-primary-container focus:ring-primary-container border-outline-variant bg-surface" name="scope" type="radio" required/>
<div class="ml-3 space-y-0.5">
<div class="flex items-center space-x-2">
<span class="font-headline-sm text-headline-sm text-on-surface">Artisan Precision Spec</span>
<span class="px-1 py-0.2 rounded bg-primary-container/20 text-primary font-label-caps text-label-caps">SUB-MICRON</span>
</div>
<p class="font-body-sm text-body-sm text-secondary leading-snug">
                Bespoke high-tolerance master tooling, dedicated atelier calibration, and segregated clean-room machining.
              </p>
</div>
</label>
<!-- Card 2 -->
<label class="relative flex items-start p-3 bg-surface-container-low border border-outline-variant/50 rounded cursor-pointer transition-all hover:bg-surface-container hover:border-outline">
<input class="mt-1 text-primary-container focus:ring-primary-container border-outline-variant bg-surface" name="scope" type="radio" required/>
<div class="ml-3 space-y-0.5">
<div class="flex items-center space-x-2">
<span class="font-headline-sm text-headline-sm text-on-surface">Atelier Pilot Run</span>
<span class="font-label-caps text-label-caps text-outline">N=50 TO 500</span>
</div>
<p class="font-body-sm text-body-sm text-secondary leading-snug">
                Discreet initial validation batch fabricated by master artisans under full non-disclosure containment.
              </p>
</div>
</label>
<!-- Card 3 -->
<label class="relative flex items-start p-3 bg-surface-container-low border border-outline-variant/50 rounded cursor-pointer transition-all hover:bg-surface-container hover:border-outline">
<input class="mt-1 text-primary-container focus:ring-primary-container border-outline-variant bg-surface" name="scope" type="radio" required/>
<div class="ml-3 space-y-0.5">
<div class="flex items-center space-x-2">
<span class="font-headline-sm text-headline-sm text-on-surface">Direct Executive Hand-off</span>
<span class="font-label-caps text-label-caps text-outline">SECURE COURIER</span>
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
<label class="font-label-caps text-label-caps text-on-surface tracking-wider" for="execution-window">
          TARGET EXECUTION WINDOW
        </label>
<div class="relative">
<select class="w-full bg-surface-container-low border border-outline-variant/60 focus:border-primary-container focus:ring-0 text-on-surface font-body-md text-body-md px-3 py-2.5 rounded appearance-none cursor-pointer" id="execution-window" required aria-required="true">
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
<section class="space-y-space-sm pt-space-xs">
<div class="flex items-center justify-between border-b border-outline-variant/30 pb-1">
<span class="font-label-caps text-label-caps text-outline">SECTION 02 // PROPRIETARY IP REPOSITORY</span>
<span class="font-label-caps text-label-caps text-tertiary">E2EE ACTIVATED</span>
</div>
<!-- Upload Zone -->
<div class="border border-dashed border-primary-container/60 bg-surface-container-lowest/80 rounded p-space-md text-center hover:bg-surface-container-low transition-colors duration-150 cursor-pointer group">
<div class="mx-auto w-10 h-10 rounded border border-outline-variant/60 bg-surface-container flex items-center justify-center mb-2 group-hover:border-primary-container/60 transition-colors">
<span class="material-symbols-outlined text-primary text-[20px]">lock</span>
</div>
<p class="font-headline-sm text-headline-sm text-on-surface">Drop Proprietary CAD / Schematics</p>
<p class="font-caption text-caption text-outline mt-0.5">STEP, IGES, DXF, or encrypted archives up to 2.5 GB</p>
<div class="mt-3 inline-flex items-center space-x-1.5 px-2.5 py-1 rounded bg-surface border border-outline-variant/40">
<span class="material-symbols-outlined text-tertiary text-[13px]">key</span>
<span class="font-label-caps text-label-caps text-on-surface-variant">Zero vendor exposure — anonymized via cryptographic key</span>
</div>
</div>
<!-- Attached Items Mockup -->
<div class="space-y-1">
<div class="flex items-center justify-between p-2 rounded bg-surface-container border border-outline-variant/30">
<div class="flex items-center space-x-2">
<span class="material-symbols-outlined text-outline text-[16px]">folder_supervised</span>
<span class="font-label-mono text-label-mono text-on-surface">chimera_core_assembly_v7.3.enc</span>
</div>
<span class="font-label-caps text-label-caps text-tertiary">142 MB // VERIFIED</span>
</div>
</div>
</section>
<!-- 4. EXECUTIVE SIGN-OFF & SUBMISSION -->
<section class="space-y-space-md pt-space-xs pb-4">
<div class="flex items-center justify-between border-b border-outline-variant/30 pb-1">
<span class="font-label-caps text-label-caps text-outline">SECTION 03 // RATIFICATION</span>
</div>
<!-- Legal Checkbox -->
<label class="flex items-start space-x-3 cursor-pointer group">
<input checked class="vault-checkbox mt-0.5 w-4 h-4 rounded border-outline-variant bg-surface-container text-primary-container focus:ring-primary-container cursor-pointer" type="checkbox" required aria-required="true" aria-label="Execute mutual binding NDA & initiate private partner briefing protocol."/>
<span class="font-body-md text-body-md text-on-surface group-hover:text-primary transition-colors select-none leading-tight">
          Execute mutual binding NDA &amp; initiate private partner briefing protocol.
        </span>
</label>
<!-- Primary Executive CTA -->
<div class="space-y-2">
<button class="w-full bg-primary-container hover:bg-[#c49f27] text-surface font-headline-sm text-headline-sm py-3 px-4 rounded font-semibold tracking-wide transition-all duration-150 flex items-center justify-center space-x-2 shadow-sm active:opacity-95" type="submit">
<span>Transmit to Executive Partner</span>
<span class="material-symbols-outlined text-[18px]">arrow_forward</span>
</button>
<p class="font-caption text-caption text-center text-outline leading-tight">
          Direct partner routing. No intermediary brokers or factory disclosure.
        </p>
</div>
</section>
</main>
</form>
{/if}
