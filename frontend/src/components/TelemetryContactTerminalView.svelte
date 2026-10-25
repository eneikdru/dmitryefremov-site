<script lang="ts">
  import CoreSvelteIdentityCard from './CoreSvelteIdentityCard.svelte';
  import SecureContactForm from './SecureContactForm.svelte';

  let activeSection: 'card' | 'contact' = 'card';
</script>

<div id="contact-terminal-root" class="max-w-3xl mx-auto my-4 font-sans space-y-6">
  <!-- Section Switcher -->
  <div class="flex justify-center border-b border-[#1f2433] pb-2">
    <div class="inline-flex rounded-lg bg-[#12151e] p-1 border border-[#1f2433] text-xs font-mono">
      <button
        id="contact-tab-card-btn"
        type="button"
        on:click={() => activeSection = 'card'}
        class="px-4 py-2 rounded-md font-semibold transition-all {activeSection === 'card' ? 'bg-[#004ac6] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        🌐 DIRECT CHANNELS & MATRIX
      </button>
      <button
        id="contact-tab-contact-btn"
        type="button"
        on:click={() => activeSection = 'contact'}
        class="px-4 py-2 rounded-md font-semibold transition-all {activeSection === 'contact' ? 'bg-[#004ac6] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        🔒 SECURE CONTACT FORM
      </button>
    </div>
  </div>

  {#if activeSection === 'card'}
    <CoreSvelteIdentityCard />
  {:else}
    <SecureContactForm />
  {/if}
</div>
