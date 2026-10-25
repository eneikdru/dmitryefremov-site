<script lang="ts">
  import CoreSvelteIdentityCard from './CoreSvelteIdentityCard.svelte';
  import VaultIntakeView from './VaultIntakeView.svelte';

  let activeSection: 'card' | 'intake' = 'card';
</script>

<div id="contact-terminal-root" class="max-w-3xl mx-auto my-4 font-sans space-y-6">
  <!-- Section Switcher -->
  <div class="flex justify-center border-b border-[#1f2433] pb-2">
    <div class="inline-flex rounded-lg bg-[#12151e] p-1 border border-[#1f2433] text-xs font-mono">
      <button
        id="contact-tab-card-btn"
        type="button"
        on:click={() => activeSection = 'card'}
        class="px-4 py-2 rounded-md font-medium transition-all {activeSection === 'card' ? 'bg-[#004ac6] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        Прямые контакты
      </button>
      <button
        id="contact-tab-intake-btn"
        type="button"
        on:click={() => activeSection = 'intake'}
        class="px-4 py-2 rounded-md font-medium transition-all {activeSection === 'intake' ? 'bg-[#004ac6] text-white shadow-sm' : 'text-[#94a3b8] hover:text-white'}"
      >
        Написать сообщение
      </button>
    </div>
  </div>

  {#if activeSection === 'card'}
    <CoreSvelteIdentityCard />
  {:else}
    <VaultIntakeView />
  {/if}
</div>
