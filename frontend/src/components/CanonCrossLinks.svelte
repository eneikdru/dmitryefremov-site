<script lang="ts">
  import { telemetry } from '../lib/telemetry';

  export interface CrossLinkItem {
    targetId: string;
    title: string;
    category: 'Concept' | 'Podcast' | 'Essay' | 'Spark';
    relation: string;
  }

  export let currentNodeId: string = 'current_node';
  export let currentNodeType: string = 'essay';
  export let crossLinks: CrossLinkItem[] = [
    { targetId: 'concept-deontic-logic', title: 'Деонтическая логика', category: 'Concept', relation: 'explains_invariants' },
    { targetId: 'podcast-ep01', title: 'Выпуск 01: Контуры управления', category: 'Podcast', relation: 'discussed_in' },
    { targetId: 'spark-4096', title: 'A2A Квантованная мысль', category: 'Spark', relation: 'referenced_by' }
  ];
  export let onNavigate: ((targetId: string, category: string) => void) | undefined = undefined;
  export let theme: 'dark' | 'light' = 'dark';

  function handleCrossLinkClick(link: CrossLinkItem) {
    telemetry.trackGraphNavigation(currentNodeId, link.targetId, link.relation, link.category);
    if (onNavigate) {
      onNavigate(link.targetId, link.category);
    }
  }
</script>

<div
  id="canon-cross-links-container"
  class="p-4 rounded-xl border font-sans space-y-3 {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#e2e8f0] shadow-sm'}"
>
  <div class="flex items-center justify-between border-b pb-2 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'}">
    <div class="flex items-center gap-2">
      <span class="text-xs font-mono px-2 py-0.5 rounded border {theme === 'dark' ? 'bg-[#12151e] text-[#38bdf8] border-[#1f2433]' : 'bg-[#f1f5f9] text-[#0284c7] border-[#cbd5e1]'}">
        [GRAPH_CROSS_LINKS]
      </span>
      <h3 class="text-xs font-mono font-bold uppercase tracking-wider {theme === 'dark' ? 'text-[#e2e8f0]' : 'text-[#0f172a]'}">
        Связанные узлы Канона ({crossLinks.length})
      </h3>
    </div>
    <span class="text-[10px] font-mono {theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">
      {currentNodeType}:{currentNodeId}
    </span>
  </div>

  <div class="grid grid-cols-1 sm:grid-cols-3 gap-2.5" role="list" aria-label="Связанные перекрёстные узлы Канона">
    {#each crossLinks as link}
      <button
        type="button"
        class="p-3 rounded-lg border text-left flex flex-col justify-between transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {theme === 'dark' ? 'bg-[#12151e] border-[#1f2433] hover:border-[#38bdf8] hover:bg-[#1f2433]/50' : 'bg-[#f8fafc] border-[#cbd5e1] hover:border-[#0284c7] hover:bg-[#f1f5f9]'}"
        aria-label="Перейти к узлу {link.title} ({link.category})"
        on:click={() => handleCrossLinkClick(link)}
      >
        <div class="space-y-1">
          <div class="flex items-center justify-between text-[10px] font-mono">
            <span class="px-1.5 py-0.2 rounded uppercase font-semibold {link.category === 'Concept' ? 'bg-[#38bdf8]/15 text-[#38bdf8]' : link.category === 'Podcast' ? 'bg-[#a855f7]/15 text-[#a855f7]' : link.category === 'Essay' ? 'bg-[#34d399]/15 text-[#34d399]' : 'bg-[#f59e0b]/15 text-[#f59e0b]'}">
              {link.category}
            </span>
            <span class="{theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">{link.relation}</span>
          </div>
          <p class="font-bold text-xs line-clamp-2 {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
            {link.title}
          </p>
        </div>
        <div class="mt-2 text-[10px] font-mono text-[#38bdf8] flex items-center gap-1 font-bold">
          <span>Перейти к узлу</span>
          <span>→</span>
        </div>
      </button>
    {/each}
  </div>
</div>
