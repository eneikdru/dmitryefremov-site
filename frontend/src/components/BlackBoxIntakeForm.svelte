<script lang="ts">
  let isSubmitted = false;
  let submissionError = '';

  // Form fields
  let directives = '';
  let materials = 'Grade 5 Satin Platinum & Osmium';
  let confidentiality = 'Ghost Atelier';

  function handleSubmit(event: SubmitEvent) {
    event.preventDefault();
    if (!directives) {
      submissionError = 'Project Directives & Objectives are required.';
      return;
    }

    // Simulate submission to the orchestrator
    submissionError = '';
    isSubmitted = true;
  }
</script>

<div class="max-w-3xl mx-auto w-full bg-surface-container-lowest text-on-surface font-body-md dark">
  {#if isSubmitted}
    <div class="p-space-lg bg-surface-container border border-primary/40 rounded shadow-lg text-center space-y-4" role="alert" aria-live="assertive">
      <span class="material-symbols-outlined text-primary text-[48px]" style="font-variation-settings: 'FILL' 1;">enhanced_encryption</span>
      <h2 class="font-headline-md text-2xl text-on-surface uppercase tracking-wider">Dossier Executed</h2>
      <p class="font-body-md text-secondary font-light">
        Your bespoke commission dossier has been sealed and dispatched via dual-signatory escrow protocols.
        Zero third-party data dispersion confirmed.
      </p>
      <button
        type="button"
        on:click={() => { isSubmitted = false; directives = ''; }}
        class="mt-4 px-6 py-2 rounded border border-outline-variant hover:bg-surface-container-high transition-colors font-label-md uppercase tracking-wider text-primary"
      >
        Submit Another Commission
      </button>
    </div>
  {:else}
    <form on:submit={handleSubmit} class="space-y-6" aria-label="Bespoke Commission Dossier">
      {#if submissionError}
        <div class="p-3 bg-error-container text-on-error-container rounded text-sm" role="alert">
          {submissionError}
        </div>
      {/if}

      <div class="flex items-center gap-3 border-b border-outline-variant/30 pb-4">
        <span class="material-symbols-outlined text-primary text-[24px]">architecture</span>
        <div>
          <h1 class="font-headline-md text-xl text-on-surface uppercase tracking-[0.1em]">Maison Atelier</h1>
          <p class="font-label-sm text-[10px] text-primary uppercase tracking-[0.2em]">Bespoke Commission Dossier</p>
        </div>
      </div>

      <!-- Field: Project Directives & Objectives -->
      <section class="space-y-2">
        <label for="directives-input" class="block font-label-md text-label-md uppercase tracking-[0.14em] text-on-surface">
          Project Directives &amp; Objectives
        </label>
        <textarea
          id="directives-input"
          bind:value={directives}
          rows="4"
          placeholder="Detail structural invariants, aesthetic mandates, and kinetic expectations..."
          class="w-full bg-surface-container-lowest border border-outline-variant rounded p-3 text-on-surface placeholder:text-outline/50 focus:border-primary focus:ring-1 focus:ring-primary focus:outline-none transition-colors font-body-sm resize-y"
          aria-required="true"
        ></textarea>
      </section>

      <!-- Field: Substrate & Alloy Architecture -->
      <section class="space-y-3 bg-surface-container-low p-space-md rounded border border-outline-variant/30">
        <div class="flex items-center justify-between">
          <span class="font-label-md text-label-md uppercase tracking-[0.14em] text-on-surface">Substrate &amp; Alloy</span>
          <span class="font-label-sm text-[9px] text-outline uppercase tracking-wider">Material</span>
        </div>

        <div class="space-y-2" role="radiogroup" aria-label="Select Material">
          <label class="flex items-center justify-between p-3 rounded {materials === 'Vanta Midnight Ceramic' ? 'bg-surface-container border border-primary/40' : 'bg-surface-container-lowest border border-outline-variant/20 hover:border-outline-variant/50'} cursor-pointer transition-colors focus-within:ring-2 focus-within:ring-primary focus-within:ring-offset-1 focus-within:ring-offset-surface">
            <div class="flex items-center gap-3">
              <input type="radio" bind:group={materials} value="Vanta Midnight Ceramic" class="sr-only" />
              <div class="w-8 h-8 rounded-full bg-[#111112] border {materials === 'Vanta Midnight Ceramic' ? 'border-primary' : 'border-outline-variant/30'} flex items-center justify-center">
                {#if materials === 'Vanta Midnight Ceramic'}
                  <span class="w-2.5 h-2.5 rounded-full bg-primary"></span>
                {:else}
                  <span class="w-2.5 h-2.5 rounded-full bg-surface-variant"></span>
                {/if}
              </div>
              <div>
                <span class="block font-body-md text-body-md {materials === 'Vanta Midnight Ceramic' ? 'text-on-surface font-medium' : 'text-on-surface-variant font-normal'}">Vanta Midnight Ceramic</span>
                <span class="block font-body-sm text-[11px] text-outline">Light-absorptive crystalline matrix</span>
              </div>
            </div>
            {#if materials === 'Vanta Midnight Ceramic'}
              <span class="material-symbols-outlined text-primary text-[18px]" style="font-variation-settings: 'FILL' 1;">check_circle</span>
            {:else}
              <span class="w-4 h-4 rounded-full border border-outline-variant/40"></span>
            {/if}
          </label>

          <label class="flex items-center justify-between p-3 rounded {materials === 'Grade 5 Satin Platinum & Osmium' ? 'bg-surface-container border border-primary/40' : 'bg-surface-container-lowest border border-outline-variant/20 hover:border-outline-variant/50'} cursor-pointer transition-colors focus-within:ring-2 focus-within:ring-primary focus-within:ring-offset-1 focus-within:ring-offset-surface">
            <div class="flex items-center gap-3">
              <input type="radio" bind:group={materials} value="Grade 5 Satin Platinum & Osmium" class="sr-only" />
              <div class="w-8 h-8 rounded-full bg-[#353432] border {materials === 'Grade 5 Satin Platinum & Osmium' ? 'border-primary' : 'border-outline-variant/30'} flex items-center justify-center">
                {#if materials === 'Grade 5 Satin Platinum & Osmium'}
                  <span class="w-2.5 h-2.5 rounded-full bg-primary"></span>
                {:else}
                  <span class="w-2.5 h-2.5 rounded-full bg-secondary"></span>
                {/if}
              </div>
              <div>
                <span class="block font-body-md text-body-md {materials === 'Grade 5 Satin Platinum & Osmium' ? 'text-on-surface font-medium' : 'text-on-surface-variant font-normal'}">Grade 5 Satin Platinum &amp; Osmium</span>
                <span class="block font-body-sm text-[11px] text-outline">High-density cryogenic finish</span>
              </div>
            </div>
            {#if materials === 'Grade 5 Satin Platinum & Osmium'}
              <span class="material-symbols-outlined text-primary text-[18px]" style="font-variation-settings: 'FILL' 1;">check_circle</span>
            {:else}
              <span class="w-4 h-4 rounded-full border border-outline-variant/40"></span>
            {/if}
          </label>
        </div>
      </section>

      <!-- Field: Confidentiality Tier Selection -->
      <section class="space-y-3 bg-surface-container-low p-space-md rounded border border-outline-variant/30">
        <div class="flex items-center justify-between">
          <span class="font-label-md text-label-md uppercase tracking-[0.14em] text-on-surface">Confidentiality Tier</span>
          <span class="font-label-sm text-[9px] text-outline uppercase tracking-wider">Provenance Masking</span>
        </div>

        <div class="space-y-2" role="radiogroup" aria-label="Confidentiality Tier">
          <label class="flex items-start gap-3 p-3 rounded {confidentiality === 'Ghost Atelier' ? 'bg-surface-container border border-primary/50' : 'bg-surface-container-lowest border border-outline-variant/20 hover:border-outline-variant/40'} cursor-pointer focus-within:ring-2 focus-within:ring-primary focus-within:ring-offset-1 focus-within:ring-offset-surface">
            <input type="radio" bind:group={confidentiality} value="Ghost Atelier" class="mt-1 text-primary focus:ring-0 bg-surface-container-lowest border-outline-variant sr-only" />
            <div class="w-4 h-4 mt-0.5 rounded-full border {confidentiality === 'Ghost Atelier' ? 'border-primary bg-primary' : 'border-outline-variant/40'} flex-shrink-0 flex items-center justify-center">
               {#if confidentiality === 'Ghost Atelier'}
                  <span class="w-1.5 h-1.5 rounded-full bg-surface-container-lowest"></span>
               {/if}
            </div>
            <div class="space-y-0.5">
              <span class="block font-label-md text-label-md uppercase {confidentiality === 'Ghost Atelier' ? 'text-on-surface' : 'text-on-surface-variant'} tracking-wider">Ghost Atelier (Total Anonymity)</span>
              <span class="block font-body-sm text-[11px] text-outline font-light">
                Artifact schematics sealed post-delivery. Zero photographic archival or institutional cataloging.
              </span>
            </div>
          </label>

          <label class="flex items-start gap-3 p-3 rounded {confidentiality === 'Private Archive Vault' ? 'bg-surface-container border border-primary/50' : 'bg-surface-container-lowest border border-outline-variant/20 hover:border-outline-variant/40'} cursor-pointer opacity-80 focus-within:ring-2 focus-within:ring-primary focus-within:ring-offset-1 focus-within:ring-offset-surface">
            <input type="radio" bind:group={confidentiality} value="Private Archive Vault" class="mt-1 text-primary focus:ring-0 bg-surface-container-lowest border-outline-variant sr-only" />
            <div class="w-4 h-4 mt-0.5 rounded-full border {confidentiality === 'Private Archive Vault' ? 'border-primary bg-primary' : 'border-outline-variant/40'} flex-shrink-0 flex items-center justify-center">
               {#if confidentiality === 'Private Archive Vault'}
                  <span class="w-1.5 h-1.5 rounded-full bg-surface-container-lowest"></span>
               {/if}
            </div>
            <div class="space-y-0.5">
              <span class="block font-label-md text-label-md uppercase {confidentiality === 'Private Archive Vault' ? 'text-on-surface' : 'text-on-surface-variant'} tracking-wider">Private Archive Vault</span>
              <span class="block font-body-sm text-[11px] text-outline font-light">
                Restricted cataloging accessible strictly via biometrically signed heir credentials.
              </span>
            </div>
          </label>
        </div>
      </section>

      <!-- Action Button -->
      <div class="pt-4 border-t border-outline-variant/20">
        <button
          type="submit"
          class="w-full py-3.5 px-space-md rounded bg-[#1C1A17] border border-primary/60 hover:bg-[#24221E] active:scale-[0.99] transition-all duration-200 flex items-center justify-center gap-2 group shadow-lg focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 focus:ring-offset-surface"
        >
          <span class="material-symbols-outlined text-primary text-[18px]" style="font-variation-settings: 'FILL' 1;">enhanced_encryption</span>
          <span class="font-label-md text-label-md text-on-surface uppercase tracking-[0.14em] font-medium group-hover:text-primary transition-colors">
            Execute Sealed Dossier &amp; Dispatch
          </span>
        </button>
        <p class="mt-3 font-body-sm text-[10px] text-outline/80 text-center font-light tracking-wide">
          Protected by sovereign dual-signatory escrow protocols. No third-party data dispersion.
        </p>
      </div>
    </form>
  {/if}
</div>
