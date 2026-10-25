<script lang="ts">
  import CoreSvelteIdentityCard from './CoreSvelteIdentityCard.svelte';
  import ContactFormUI from './ContactFormUI.svelte';
  import VaultIntakeView from './VaultIntakeView.svelte';

  let activeSection: 'card' | 'form' | 'intake' = 'card';
</script>

<div id="contact-terminal-root" class="max-w-3xl mx-auto my-4 font-sans space-y-6">
  <!-- Section Switcher Navigation Bar -->
  <div class="flex justify-center border-b border-[#1f2433] pb-2">
    <div class="inline-flex rounded-lg bg-[#12151e] p-1 border border-[#1f2433] text-xs font-mono">
      <button
        id="contact-tab-card-btn"
        type="button"
        on:click={() => activeSection = 'card'}
        class="px-4 py-2 rounded-md font-semibold transition-all cursor-pointer {activeSection === 'card' ? 'bg-[#0051d5] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        🌐 DIRECT CHANNELS & MATRIX
      </button>
      <button
        id="contact-tab-form-btn"
        type="button"
        on:click={() => activeSection = 'form'}
        class="px-4 py-2 rounded-md font-semibold transition-all cursor-pointer {activeSection === 'form' ? 'bg-[#0051d5] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        ✉️ INTERACTIVE CONTACT FORM
      </button>
      <button
        id="contact-tab-intake-btn"
        type="button"
        on:click={() => activeSection = 'intake'}
        class="px-4 py-2 rounded-md font-semibold transition-all cursor-pointer {activeSection === 'intake' ? 'bg-[#0051d5] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        🔒 SECURE INTAKE FORM
      </button>
    </div>
  </div>

  {#if activeSection === 'card'}
    <CoreSvelteIdentityCard />
    <div class="mt-8">
      <ContactFormUI />
    </div>
  {:else if activeSection === 'form'}
    <ContactFormUI />
  {:else}
    <VaultIntakeView />
  {/if}
</div>
