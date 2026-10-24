<script lang="ts">
  export let nodeTitle: string = '';
  export let nodeType: 'concept' | 'podcast' | 'essay' | 'spark' = 'concept';
  export let domain: string = 'AUTONOMOUS_ENGINEERING';
  export let theme: 'dark' | 'light' = 'dark';
  export let incomingReferences: Array<{ sourceTitle: string; sourceSlug?: string; relationshipType?: string }> = [];
  export let outgoingReferences: Array<{ targetTitle: string; targetSlug?: string; relationshipType?: string }> = [];
  export let onNavigate: (target: string) => void = () => {};

  $: isDark = theme === 'dark';

  // Default cross-references if none passed
  $: effectiveIncoming = incomingReferences.length > 0 ? incomingReferences : [
    { sourceTitle: 'Consensus Raft', sourceSlug: 'consensus-raft', relationshipType: 'REFERENCES' },
    { sourceTitle: 'Term Safety Invariant', sourceSlug: 'term-safety-invariant', relationshipType: 'DEPENDS_ON' }
  ];

  $: effectiveOutgoing = outgoingReferences.length > 0 ? outgoingReferences : [
    { targetTitle: 'Raft Split Vote Recovery', targetSlug: 'raft-split-vote-recovery', relationshipType: 'RELATED' },
    { targetTitle: 'Deontic Agent Safety', targetSlug: 'deontic-agent-safety', relationshipType: 'EXTENDS' }
  ];

  function handleKeydown(e: KeyboardEvent, target: string) {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      onNavigate(target);
    }
  }
</script>

<div
  id="content-cross-links-card"
  class="p-4 sm:p-5 rounded-xl border transition-all space-y-4 {isDark ? 'bg-[#12151e] border-[#1f2433] text-[#e2e8f0]' : 'bg-white border-[#cbd5e1] text-[#0f172a]'}"
  role="region"
  aria-label="Перекрёстные ссылки графа знаний"
>
  <!-- Card Header -->
  <div class="flex items-center justify-between border-b pb-3 {isDark ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
    <div class="flex items-center gap-2">
      <span class="font-mono text-xs px-2 py-0.5 rounded font-medium bg-[#38bdf8]/10 text-[#38bdf8]">
        ГРАФ СВЯЗЕЙ
      </span>
      {#if nodeTitle}
        <span class="text-xs font-mono font-bold {isDark ? 'text-white' : 'text-[#0f172a]'}">
          {nodeType.toUpperCase()}: {nodeTitle}
        </span>
      {:else}
        <span class="text-xs font-mono {isDark ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
          {domain.replace(/_/g, ' ')}
        </span>
      {/if}
    </div>
    <div class="flex items-center gap-2 text-xs font-mono">
      <span class="px-2 py-0.5 rounded-full border {isDark ? 'bg-[#1a1f2c] border-[#38bdf8]/30 text-[#38bdf8]' : 'bg-[#f0f9ff] border-[#0284c7]/30 text-[#0284c7]'}">
        {effectiveIncoming.length} входящих
      </span>
      <span class="px-2 py-0.5 rounded-full border {isDark ? 'bg-[#1a1f2c] border-[#818cf8]/30 text-[#818cf8]' : 'bg-[#f5f3ff] border-[#6366f1]/30 text-[#6366f1]'}">
        {effectiveOutgoing.length} исходящих
      </span>
    </div>
  </div>

  <!-- Bidirectional Node Navigation -->
  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
    <!-- Incoming References Section -->
    <div class="space-y-2">
      <h4 class="text-xs font-mono font-bold uppercase tracking-wider text-[#38bdf8] flex items-center gap-1.5">
        <span>← Входящие ссылки (Backlinks)</span>
      </h4>
      <div class="space-y-1.5">
        {#each effectiveIncoming as ref}
          <button
            type="button"
            class="w-full text-left p-2.5 rounded-lg border transition-all flex items-center justify-between group focus:outline-none focus:ring-2 focus:ring-[#38bdf8] focus:ring-offset-2 {isDark ? 'bg-[#0f1118] border-[#1f2433] hover:border-[#38bdf8]/50' : 'bg-[#f8fafc] border-[#e2e8f0] hover:border-[#0284c7]/50'}"
            on:click={() => onNavigate(ref.sourceSlug || ref.sourceTitle)}
            on:keydown={(e) => handleKeydown(e, ref.sourceSlug || ref.sourceTitle)}
          >
            <div class="flex items-center gap-2 overflow-hidden">
              <span class="text-xs font-mono font-medium text-[#38bdf8] shrink-0">[[</span>
              <span class="text-xs font-medium truncate group-hover:underline {isDark ? 'text-white' : 'text-[#0f172a]'}">
                {ref.sourceTitle}
              </span>
              <span class="text-xs font-mono text-[#38bdf8] shrink-0">]]</span>
            </div>
            <span class="text-[10px] font-mono px-1.5 py-0.5 rounded border uppercase shrink-0 {isDark ? 'bg-[#1f2433] border-[#31353e] text-[#94a3b8]' : 'bg-[#e2e8f0] border-[#cbd5e1] text-[#475569]'}">
              {ref.relationshipType || 'REFERENCES'}
            </span>
          </button>
        {/each}
      </div>
    </div>

    <!-- Outgoing References Section -->
    <div class="space-y-2">
      <h4 class="text-xs font-mono font-bold uppercase tracking-wider text-[#818cf8] flex items-center gap-1.5">
        <span>Исходящие узлы (Forward Links) →</span>
      </h4>
      <div class="space-y-1.5">
        {#each effectiveOutgoing as ref}
          <button
            type="button"
            class="w-full text-left p-2.5 rounded-lg border transition-all flex items-center justify-between group focus:outline-none focus:ring-2 focus:ring-[#818cf8] focus:ring-offset-2 {isDark ? 'bg-[#0f1118] border-[#1f2433] hover:border-[#818cf8]/50' : 'bg-[#f8fafc] border-[#e2e8f0] hover:border-[#6366f1]/50'}"
            on:click={() => onNavigate(ref.targetSlug || ref.targetTitle)}
            on:keydown={(e) => handleKeydown(e, ref.targetSlug || ref.targetTitle)}
          >
            <div class="flex items-center gap-2 overflow-hidden">
              <span class="text-xs font-mono font-medium text-[#818cf8] shrink-0">[[</span>
              <span class="text-xs font-medium truncate group-hover:underline {isDark ? 'text-white' : 'text-[#0f172a]'}">
                {ref.targetTitle}
              </span>
              <span class="text-xs font-mono text-[#818cf8] shrink-0">]]</span>
            </div>
            <span class="text-[10px] font-mono px-1.5 py-0.5 rounded border uppercase shrink-0 {isDark ? 'bg-[#1f2433] border-[#31353e] text-[#94a3b8]' : 'bg-[#e2e8f0] border-[#cbd5e1] text-[#475569]'}">
              {ref.relationshipType || 'RELATED'}
            </span>
          </button>
        {/each}
      </div>
    </div>
  </div>
</div>
