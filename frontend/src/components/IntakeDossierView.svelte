<script lang="ts">
  export let theme: 'dark' | 'light' = 'dark';

  let commissionMoniker: string = 'Project Obsidian Aegis';
  let creativePremise: string = 'Bespoke timekeeping instrument featuring Damascus titanium housing and celestial Gibeon meteorite core.';
  let selectedArchetype: string = 'horology';
  let selectedMaterial: string = 'damascus';
  let selectedConfidentiality: string = 'ghost';

  let isCovenantModalOpen: boolean = false;
  let isSubmitting: boolean = false;
  let isSubmitted: boolean = false;
  let confirmationRef: string = 'AT-9842-NDA';

  const archetypes = [
    { id: 'horology', num: '01', title: 'Haute Horology', subtitle: 'Tourbillon & Chronometry' },
    { id: 'metallurgy', num: '02', title: 'Fine Metallurgy', subtitle: 'Bespoke Monolith Sculpting' },
    { id: 'joaillerie', num: '03', title: 'Haute Joaillerie', subtitle: 'Rare Gemological Setting' },
    { id: 'relinquary', num: '04', title: 'Private Objet d\'Art', subtitle: 'Curated Kinetic Reliquary' }
  ];

  const materials = [
    { id: 'damascus', name: 'Damascus Titanium & Meteorite', desc: 'Hand-folded pattern, Gibeon celestial fragment' },
    { id: 'vanta', name: 'Vanta Midnight Ceramic', desc: 'Light-absorptive crystalline matrix' },
    { id: 'platinum', name: 'Grade 5 Satin Platinum & Osmium', desc: 'High-density cryogenic finish' }
  ];

  function toggleCovenantModal() {
    isCovenantModalOpen = !isCovenantModalOpen;
  }

  function handleSubmit(event: SubmitEvent) {
    event.preventDefault();
    isSubmitting = true;
    setTimeout(() => {
      isSubmitting = false;
      isSubmitted = true;
      confirmationRef = `AT-${Math.floor(1000 + Math.random() * 9000)}-NDA`;
    }, 600);
  }

  function resetForm() {
    isSubmitted = false;
  }
</script>

<div
  id="intake-dossier-root"
  class="w-full max-w-2xl mx-auto space-y-6 font-sans text-sm transition-colors duration-200"
>
  <!-- Hero Section -->
  <section id="intake-hero-section" class="space-y-2">
    <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#f2ca50]/10 border border-[#f2ca50]/30">
      <span class="w-2 h-2 rounded-full bg-[#f2ca50] animate-pulse"></span>
      <span class="text-xs font-mono font-semibold uppercase tracking-widest text-[#f2ca50]">
        Confidential Intake Chamber
      </span>
    </div>
    <h1 id="intake-dossier-title" class="text-2xl sm:text-3xl font-serif font-normal tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
      Bespoke Commission Dossier
    </h1>
    <p class="text-xs sm:text-sm leading-relaxed {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#475569]'}">
      Phase I: Bilateral Non-Disclosure &amp; Creative Premise. Curating sovereign artifacts of singular architectural integrity.
    </p>
  </section>

  {#if isSubmitted}
    <!-- Sealed Confirmation View -->
    <section id="submission-confirmation" role="alert" class="p-6 rounded-lg border shadow-xl space-y-4 text-center {theme === 'dark' ? 'bg-[#12151e] border-[#f2ca50]/50 text-white' : 'bg-white border-[#f2ca50]/70 text-[#0f172a]'}">
      <div class="inline-flex items-center justify-center w-12 h-12 rounded-full bg-[#f2ca50]/20 text-[#f2ca50] border border-[#f2ca50]/40 mx-auto">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
        </svg>
      </div>
      <h2 id="confirmation-heading" class="text-xl font-serif font-semibold">
        Dossier Sealed &amp; Transmitted
      </h2>
      <p class="text-xs sm:text-sm max-w-lg mx-auto leading-relaxed {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#334155]'}">
        Your bespoke commission dossier <strong class="font-mono text-[#f2ca50]">{confirmationRef}</strong> has been encrypted with zero-knowledge protocols and dispatched directly to your assigned principal liaison, <strong>Henri de Valois</strong>.
      </p>
      <div class="p-3 rounded bg-[#090a0f]/60 border border-[#1f2433] text-xs font-mono text-[#94a3b8] text-left space-y-1">
        <div><strong class="text-white">Moniker:</strong> {commissionMoniker}</div>
        <div><strong class="text-white">Archetype:</strong> {archetypes.find(a => a.id === selectedArchetype)?.title}</div>
        <div><strong class="text-white">Confidentiality:</strong> {selectedConfidentiality === 'ghost' ? 'Ghost Atelier (Total Anonymity)' : 'Private Archive Vault'}</div>
        <div><strong class="text-white">Status:</strong> Bilateral Covenant Executed</div>
      </div>
      <button
        id="reset-dossier-btn"
        type="button"
        on:click={resetForm}
        class="px-5 py-2.5 rounded font-mono text-xs font-semibold uppercase tracking-wider bg-[#f2ca50] text-[#141312] hover:bg-[#ffe088] focus:outline-none focus:ring-2 focus:ring-[#f2ca50] focus:ring-offset-2 transition-colors"
      >
        Submit New Commission Premise
      </button>
    </section>
  {:else}
    <!-- Intake Form -->
    <form id="intake-form" on:submit={handleSubmit} class="space-y-6">
      <!-- Cryptographic NDA Status Card -->
      <section id="nda-status-card" class="p-4 rounded-lg border relative overflow-hidden shadow-md {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-white border-[#cbd5e1]'}">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-3">
          <div>
            <div class="flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-[#f2ca50]"></span>
              <h2 class="text-xs font-mono uppercase tracking-wider font-bold {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                Mutual NDA Status: Active &amp; Sealed
              </h2>
            </div>
            <p class="text-xs text-[#94a3b8] mt-0.5">
              Cryptographic Escrow Protocol · Place Vendôme &amp; Geneva
            </p>
          </div>
          <span class="self-start sm:self-auto px-2.5 py-1 rounded bg-[#f2ca50]/10 border border-[#f2ca50]/40 text-xs font-mono text-[#f2ca50] font-semibold tracking-wider uppercase">
            CLEARANCE IV
          </span>
        </div>

        <div class="grid grid-cols-2 gap-3 pt-3 border-t border-[#1f2433]/50 mb-3 text-xs font-mono">
          <div>
            <span class="block text-[#64748b] text-[10px] uppercase">Covenant Reference</span>
            <span class="font-bold {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#1e293b]'}">#AT-9842-NDA</span>
          </div>
          <div>
            <span class="block text-[#64748b] text-[10px] uppercase">Bilateral Timestamp</span>
            <span class="font-bold {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#1e293b]'}">2025.04.18 · 14:02:19 Z</span>
          </div>
        </div>

        <button
          id="inspect-covenant-btn"
          type="button"
          on:click={toggleCovenantModal}
          class="w-full flex items-center justify-between py-2 px-3 rounded text-xs font-mono uppercase tracking-wider transition-colors border {theme === 'dark' ? 'bg-[#090a0f] text-[#cbd5e1] border-[#1f2433] hover:border-[#f2ca50]' : 'bg-[#f8fafc] text-[#334155] border-[#cbd5e1] hover:border-[#f2ca50]'}"
        >
          <span>Inspect Executed Bilateral Covenant</span>
          <span aria-hidden="true">→</span>
        </button>
      </section>

      <!-- Field 1: Commission Moniker -->
      <section id="section-moniker" class="p-4 rounded-lg border space-y-2 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-white border-[#cbd5e1]'}">
        <div class="flex items-center justify-between">
          <label for="commission-name" class="font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Commission Moniker
          </label>
          <span class="text-[10px] font-mono text-[#f2ca50] uppercase tracking-wider">Confidential Title</span>
        </div>
        <input
          id="commission-name"
          name="commissionMoniker"
          type="text"
          bind:value={commissionMoniker}
          placeholder="e.g. Project Solitude"
          required
          aria-required="true"
          class="w-full px-3 py-2 rounded font-sans text-sm border focus:outline-none focus:ring-2 focus:ring-[#f2ca50] transition-colors {theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433] text-white focus:border-[#f2ca50]' : 'bg-white border-[#cbd5e1] text-[#0f172a] focus:border-[#f2ca50]'}"
        />
        <p class="text-[11px] text-[#94a3b8] flex items-center gap-1">
          <span class="text-[#f2ca50]">◆</span> Zero-Knowledge Encrypted Client Record — Atelier Clearance Level IV
        </p>
      </section>

      <!-- Field 2: Creative Premise / Idea Description -->
      <section id="section-premise" class="p-4 rounded-lg border space-y-2 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-white border-[#cbd5e1]'}">
        <div class="flex items-center justify-between">
          <label for="creative-premise" class="font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Creative Premise &amp; Vision
          </label>
          <span class="text-[10px] font-mono text-[#94a3b8] uppercase tracking-wider">Bespoke Requirements</span>
        </div>
        <textarea
          id="creative-premise"
          name="creativePremise"
          rows="3"
          bind:value={creativePremise}
          placeholder="Describe your vision, aesthetic requirements, or initial premise..."
          required
          aria-required="true"
          class="w-full px-3 py-2 rounded font-sans text-sm border focus:outline-none focus:ring-2 focus:ring-[#f2ca50] transition-colors {theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433] text-white focus:border-[#f2ca50]' : 'bg-white border-[#cbd5e1] text-[#0f172a] focus:border-[#f2ca50]'}"
        ></textarea>
      </section>

      <!-- Field 3: Aesthetic Intent & Archetype -->
      <section id="section-archetypes" class="p-4 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-white border-[#cbd5e1]'}">
        <div class="flex items-center justify-between">
          <h2 class="font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Aesthetic Intent &amp; Archetype
          </h2>
          <span class="text-[10px] font-mono text-[#94a3b8] uppercase tracking-wider">Select One</span>
        </div>
        <p class="text-xs text-[#94a3b8]">
          Curatorial discipline dictates sculptural methodology and master artisan assignment.
        </p>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
          {#each archetypes as item}
            <button
              id="archetype-btn-{item.id}"
              type="button"
              aria-pressed={selectedArchetype === item.id}
              on:click={() => (selectedArchetype = item.id)}
              class="p-3 rounded border text-left transition-all flex flex-col justify-between h-24 focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {selectedArchetype === item.id ? (theme === 'dark' ? 'bg-[#090a0f] border-[#f2ca50] text-white' : 'bg-[#fffbeb] border-[#f2ca50] text-[#0f172a]') : (theme === 'dark' ? 'bg-[#090a0f]/60 border-[#1f2433] text-[#94a3b8] hover:border-[#f2ca50]/50' : 'bg-[#f8fafc] border-[#cbd5e1] text-[#64748b] hover:border-[#f2ca50]/50')}"
            >
              <div class="flex justify-between items-start">
                <span class="text-[10px] font-mono font-bold tracking-widest uppercase {selectedArchetype === item.id ? 'text-[#f2ca50]' : 'text-[#64748b]'}">
                  {item.num} / ARCHETYPE
                </span>
                {#if selectedArchetype === item.id}
                  <span class="text-[#f2ca50] text-xs">◆</span>
                {/if}
              </div>
              <div>
                <span class="block font-serif text-sm font-semibold {selectedArchetype === item.id ? (theme === 'dark' ? 'text-white' : 'text-[#0f172a]') : (theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#334155]')}">
                  {item.title}
                </span>
                <span class="block text-[11px] text-[#94a3b8]">{item.subtitle}</span>
              </div>
            </button>
          {/each}
        </div>
      </section>

      <!-- Field 4: Materiality Curation -->
      <section id="section-materiality" class="p-4 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-white border-[#cbd5e1]'}">
        <div class="flex items-center justify-between">
          <h2 class="font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Materiality Curation
          </h2>
          <span class="text-[10px] font-mono text-[#94a3b8] uppercase tracking-wider">Provenance Certified</span>
        </div>
        <p class="text-xs text-[#94a3b8]">
          Mineral and alloy sources extracted through ethical, bilateral private reserves.
        </p>

        <div class="space-y-2">
          {#each materials as mat}
            <button
              id="material-btn-{mat.id}"
              type="button"
              aria-pressed={selectedMaterial === mat.id}
              on:click={() => (selectedMaterial = mat.id)}
              class="w-full text-left p-3 rounded border transition-all flex items-center justify-between focus:outline-none focus:ring-2 focus:ring-[#f2ca50] {selectedMaterial === mat.id ? (theme === 'dark' ? 'bg-[#090a0f] border-[#f2ca50] text-white' : 'bg-[#fffbeb] border-[#f2ca50] text-[#0f172a]') : (theme === 'dark' ? 'bg-[#090a0f]/60 border-[#1f2433] text-[#94a3b8] hover:border-[#f2ca50]/50' : 'bg-[#f8fafc] border-[#cbd5e1] text-[#64748b] hover:border-[#f2ca50]/50')}"
            >
              <div class="flex items-center gap-3 min-w-0">
                <span class="w-3 h-3 rounded-full border border-[#f2ca50] shrink-0 flex items-center justify-center {selectedMaterial === mat.id ? 'bg-[#f2ca50]' : ''}"></span>
                <div class="min-w-0">
                  <span class="block font-medium text-xs sm:text-sm truncate {selectedMaterial === mat.id ? (theme === 'dark' ? 'text-white' : 'text-[#0f172a]') : (theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#334155]')}">
                    {mat.name}
                  </span>
                  <span class="block text-[11px] text-[#94a3b8] truncate">{mat.desc}</span>
                </div>
              </div>
              {#if selectedMaterial === mat.id}
                <span class="text-[#f2ca50] text-xs font-mono font-bold shrink-0">SELECTED</span>
              {/if}
            </button>
          {/each}
        </div>
      </section>

      <!-- Field 5: Craftsmanship Caliber -->
      <section id="section-craftsmanship" class="p-4 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-white border-[#cbd5e1]'}">
        <div class="flex items-center justify-between">
          <h2 class="font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Craftsmanship Caliber
          </h2>
          <span class="text-[10px] font-mono text-[#f2ca50] uppercase tracking-wider">Geneva Seal Standard</span>
        </div>
        <p class="text-xs text-[#94a3b8]">
          Hand-burnished tolerances verified via optical interferometry under Master Atelier supervision.
        </p>

        <div class="p-3 rounded border space-y-2 {theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433]' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
          <div class="flex justify-between items-center text-xs">
            <span class={theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#334155]'}>Artisan Hand-Burnished (&lt;0.01mm Chiseled Fit)</span>
            <span class="font-mono text-[10px] text-[#f2ca50] font-bold">SPEC 0.008µm</span>
          </div>
          <div class="w-full bg-[#1f2433] h-1.5 rounded-full overflow-hidden">
            <div class="bg-[#f2ca50] h-full w-[94%]"></div>
          </div>
          <div class="flex justify-between text-[10px] text-[#94a3b8]">
            <span>Anglage &amp; Mirror Black Polish</span>
            <span>Dual-Axis Guilloché</span>
          </div>
        </div>
      </section>

      <!-- Field 6: Confidentiality Tier Selection -->
      <section id="section-confidentiality" class="p-4 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-white border-[#cbd5e1]'}">
        <div class="flex items-center justify-between">
          <h2 class="font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Confidentiality Tier
          </h2>
          <span class="text-[10px] font-mono text-[#94a3b8] uppercase tracking-wider">Provenance Masking</span>
        </div>

        <fieldset class="space-y-2">
          <legend class="sr-only">Choose Confidentiality Tier</legend>
          <label class="flex items-start gap-3 p-3 rounded border cursor-pointer transition-colors focus-within:ring-2 focus-within:ring-[#f2ca50] {selectedConfidentiality === 'ghost' ? (theme === 'dark' ? 'bg-[#090a0f] border-[#f2ca50]' : 'bg-[#fffbeb] border-[#f2ca50]') : (theme === 'dark' ? 'bg-[#090a0f]/60 border-[#1f2433]' : 'bg-[#f8fafc] border-[#cbd5e1]')}">
            <input
              type="radio"
              name="confidentialityTier"
              value="ghost"
              bind:group={selectedConfidentiality}
              class="mt-0.5 text-[#f2ca50] focus:ring-[#f2ca50]"
            />
            <div>
              <span class="block text-xs font-mono font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                Ghost Atelier (Total Anonymity)
              </span>
              <span class="block text-[11px] text-[#94a3b8] mt-0.5">
                Artifact schematics sealed post-delivery. Zero photographic archival or institutional cataloging.
              </span>
            </div>
          </label>

          <label class="flex items-start gap-3 p-3 rounded border cursor-pointer transition-colors focus-within:ring-2 focus-within:ring-[#f2ca50] {selectedConfidentiality === 'archive' ? (theme === 'dark' ? 'bg-[#090a0f] border-[#f2ca50]' : 'bg-[#fffbeb] border-[#f2ca50]') : (theme === 'dark' ? 'bg-[#090a0f]/60 border-[#1f2433]' : 'bg-[#f8fafc] border-[#cbd5e1]')}">
            <input
              type="radio"
              name="confidentialityTier"
              value="archive"
              bind:group={selectedConfidentiality}
              class="mt-0.5 text-[#f2ca50] focus:ring-[#f2ca50]"
            />
            <div>
              <span class="block text-xs font-mono font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                Private Archive Vault
              </span>
              <span class="block text-[11px] text-[#94a3b8] mt-0.5">
                Restricted cataloging accessible strictly via biometrically signed heir credentials.
              </span>
            </div>
          </label>
        </fieldset>
      </section>

      <!-- Field 7: Assigned Senior Liaison -->
      <section id="section-liaison" class="p-4 rounded-lg border space-y-3 {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433]' : 'bg-white border-[#cbd5e1]'}">
        <div class="flex items-center justify-between">
          <h2 class="font-mono text-xs font-bold uppercase tracking-wider {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            Assigned Senior Liaison
          </h2>
          <span class="inline-flex items-center gap-1 font-mono text-[10px] text-[#f2ca50] uppercase">
            <span class="w-1.5 h-1.5 rounded-full bg-[#f2ca50]"></span> Line Secure
          </span>
        </div>

        <div class="flex items-center justify-between gap-3 p-3 rounded border {theme === 'dark' ? 'bg-[#090a0f] border-[#1f2433]' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
          <div class="flex items-center gap-3 min-w-0">
            <div class="w-10 h-10 rounded bg-[#1f2433] border border-[#f2ca50]/40 flex items-center justify-center text-[#f2ca50] font-serif font-bold shrink-0">
              HV
            </div>
            <div class="min-w-0">
              <span class="block font-serif font-semibold text-sm truncate {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
                Henri de Valois
              </span>
              <span class="block text-[11px] text-[#94a3b8] truncate">Curator Principal · Geneva Salon Private Desk</span>
            </div>
          </div>
          <div class="flex items-center gap-2 shrink-0">
            <button
              type="button"
              aria-label="Encrypted Audio Link"
              class="px-2.5 py-1 rounded text-xs font-mono border border-[#1f2433] bg-[#12151e] text-[#cbd5e1] hover:border-[#f2ca50] hover:text-[#f2ca50] transition-colors"
            >
              AUDIO
            </button>
            <button
              type="button"
              aria-label="Encrypted Chat Link"
              class="px-2.5 py-1 rounded text-xs font-mono border border-[#1f2433] bg-[#12151e] text-[#cbd5e1] hover:border-[#f2ca50] hover:text-[#f2ca50] transition-colors"
            >
              CHAT
            </button>
          </div>
        </div>
      </section>

      <!-- Execution Action Button -->
      <section id="section-submit" class="pt-2">
        <button
          id="execute-dossier-btn"
          type="submit"
          disabled={isSubmitting}
          class="w-full py-3.5 px-6 rounded font-mono text-xs font-bold uppercase tracking-widest bg-[#f2ca50] text-[#141312] hover:bg-[#ffe088] active:scale-[0.99] disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-[#f2ca50] focus:ring-offset-2 transition-all shadow-lg flex items-center justify-center gap-2"
        >
          {#if isSubmitting}
            <span class="inline-block w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin"></span>
            <span>Encrypting &amp; Dispatching...</span>
          {:else}
            <span>Execute Sealed Dossier &amp; Dispatch</span>
          {/if}
        </button>
        <p class="text-[10px] text-[#94a3b8] text-center mt-2 font-mono">
          Protected by sovereign dual-signatory escrow protocols. No third-party data dispersion.
        </p>
      </section>
    </form>
  {/if}

  <!-- Bilateral Covenant Modal -->
  {#if isCovenantModalOpen}
    <div
      id="covenant-modal"
      role="dialog"
      aria-labelledby="covenant-title"
      class="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50"
    >
      <div class="max-w-lg w-full p-6 rounded-lg border shadow-2xl space-y-4 font-mono text-xs {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] text-[#e2e8f0]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}">
        <div class="flex justify-between items-center border-b pb-3 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
          <h2 id="covenant-title" class="text-sm font-bold text-[#f2ca50]">[BILATERAL_COVENANT_REF_#AT-9842-NDA]</h2>
          <button
            id="covenant-close-btn"
            type="button"
            on:click={toggleCovenantModal}
            class="px-2 py-1 rounded border {theme === 'dark' ? 'bg-[#1f2433] hover:bg-[#2e364f]' : 'bg-[#f1f5f9] hover:bg-[#e2e8f0]'}"
          >
            ✕
          </button>
        </div>
        <div class="space-y-3 text-xs leading-relaxed max-h-80 overflow-y-auto pr-1">
          <p class="text-[#cbd5e1]">
            This Non-Disclosure Covenant ("Covenant") governs all communications, schematics, and material specifications exchanged between the Client and Maison Atelier.
          </p>
          <p class="text-[#cbd5e1]">
            <strong>1. Absolute Confidentiality:</strong> All intellectual properties, aesthetic premises, and technical requests submitted under clearance Level IV are strictly bound to zero-knowledge storage standards.
          </p>
          <p class="text-[#cbd5e1]">
            <strong>2. Sovereign Data Isolation:</strong> Under no circumstances will client data or commission specifics be transferred, analyzed for telemetry, or processed through external conduits.
          </p>
          <p class="text-[#cbd5e1]">
            <strong>3. Execution Standard:</strong> Bilateral sign-off guarantees physical and digital destruction of transient schematics upon final delivery, according to chosen Ghost Atelier or Private Archive parameters.
          </p>
        </div>
      </div>
    </div>
  {/if}
</div>
