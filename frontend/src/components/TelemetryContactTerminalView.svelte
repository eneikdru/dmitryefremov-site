<script lang="ts">
  import CoreSvelteIdentityCard from './CoreSvelteIdentityCard.svelte';
  import VaultIntakeView from './VaultIntakeView.svelte';
  import ContactForm from './ContactForm.svelte';

  let activeSection: 'card' | 'intake' | 'general' = 'card';
</script>

<div id="contact-terminal-root" class="max-w-3xl mx-auto my-4 font-sans space-y-6">
  <!-- Section Switcher -->
  <div class="flex justify-center border-b border-[#1f2433] pb-2">
    <div class="inline-flex flex-wrap justify-center rounded-lg bg-[#12151e] p-1 border border-[#1f2433] text-xs font-mono gap-1">
      <button
        id="contact-tab-card-btn"
        type="button"
        on:click={() => activeSection = 'card'}
        class="px-4 py-2 rounded-md font-semibold transition-all {activeSection === 'card' ? 'bg-[#004ac6] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        🌐 DIRECT CHANNELS & MATRIX
      </button>
      <button
        id="contact-tab-general-btn"
        type="button"
        on:click={() => activeSection = 'general'}
        class="px-4 py-2 rounded-md font-semibold transition-all {activeSection === 'general' ? 'bg-[#004ac6] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        ✉️ GENERAL CONTACT FORM
      </button>
      <button
        id="contact-tab-intake-btn"
        type="button"
        on:click={() => activeSection = 'intake'}
        class="px-4 py-2 rounded-md font-semibold transition-all {activeSection === 'intake' ? 'bg-[#004ac6] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        🔒 SECURE INTAKE FORM
      </button>
    </div>
  </div>

  {#if activeSection === 'card'}
    <CoreSvelteIdentityCard />
  {:else if activeSection === 'general'}
    <ContactForm />
  {:else}
    <VaultIntakeView />
  {/if}
</div>
