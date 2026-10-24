<script lang="ts">
  import { onMount, createEventDispatcher } from 'svelte';
  import {
    fetchCanonItems,
    fetchCanonItemBySlug,
    SEEDED_CANON_ITEMS,
    type CanonItem,
    type CanonCrossReference
  } from '../lib/canon';

  export let activeSlug: string = 'raft-protocol-state-machine';
  export let theme: 'dark' | 'light' = 'dark';

  const dispatch = createEventDispatcher();

  let canonItems: CanonItem[] = [];
  let currentItem: CanonItem | null = null;
  let activePreviewRef: { ref: CanonCrossReference; snippet: string } | null = null;

  async function loadData() {
    canonItems = await fetchCanonItems();
    currentItem = await fetchCanonItemBySlug(activeSlug);
  }

  $: {
    if (activeSlug) {
      fetchCanonItemBySlug(activeSlug).then(item => {
        currentItem = item;
      });
    }
  }

  onMount(() => {
    loadData();
  });

  function selectNode(slug: string) {
    activeSlug = slug;
    activePreviewRef = null;
    dispatch('selectNode', { slug });
  }

  function showPreview(ref: CanonCrossReference) {
    const targetItem = canonItems.find(i => i.slug === ref.targetSlug || i.slug === ref.sourceSlug);
    activePreviewRef = {
      ref,
      snippet: targetItem?.summary || targetItem?.content.slice(0, 150) || ref.description || 'Детали узла графа загружаются из базы данных...'
    };
  }

  function hidePreview() {
    activePreviewRef = null;
  }
</script>

<div
  id="canon-graph-view"
  class="space-y-8 font-sans {theme === 'dark' ? 'text-[#dfe2ee]' : 'text-[#0f172a]'}"
>
  <!-- Node List Header & Domain Filters -->
  <header class="p-6 rounded-2xl border space-y-4 {theme === 'dark' ? 'bg-[#0f131c] border-[#31353e]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b pb-4 {theme === 'dark' ? 'border-[#31353e]' : 'border-[#e2e8f0]'}">
      <div class="space-y-1">
        <div class="flex items-center gap-2">
          <span class="text-xs font-mono font-bold px-2 py-0.5 rounded uppercase {theme === 'dark' ? 'bg-[#38bdf8]/10 text-[#38bdf8] border border-[#38bdf8]/30' : 'bg-[#0284c7]/10 text-[#0284c7] border border-[#0284c7]/30'}">
            КАНОН :: ЕДИНЫЙ ГРАФ ПОНЯТИЙ
          </span>
          <span class="text-xs font-mono text-[#908fa0]">v2.4 CORPUS</span>
        </div>
        <h2 class="text-xl sm:text-2xl font-bold tracking-tight">
          Интерактивная онтология и перекрёстные ссылки
        </h2>
      </div>

      <!-- Quick Selector Pills -->
      <div class="flex flex-wrap gap-2">
        {#each canonItems.length > 0 ? canonItems : SEEDED_CANON_ITEMS as item}
          <button
            type="button"
            class="px-3 py-1.5 rounded-lg text-xs font-mono transition-all border cursor-pointer {item.slug === activeSlug ? (theme === 'dark' ? 'bg-[#38bdf8] text-[#0f131c] font-bold border-[#38bdf8]' : 'bg-[#0284c7] text-white font-bold border-[#0284c7]') : (theme === 'dark' ? 'bg-[#181c24] text-[#c7c4d7] border-[#31353e] hover:border-[#38bdf8]' : 'bg-[#f8fafc] text-[#334155] border-[#cbd5e1] hover:border-[#0284c7]')}"
            on:click={() => selectNode(item.slug)}
          >
            {item.title.split(' ')[0]}
          </button>
        {/each}
      </div>
    </div>

    <!-- Active Item Header Card -->
    {#if currentItem}
      <div class="space-y-3 pt-2">
        <div class="flex items-center gap-2 text-xs font-mono">
          <span class="px-2 py-0.5 rounded bg-[#c4e7ff]/10 text-[#7bd0ff] border border-[#7bd0ff]/30">
            {currentItem.type}
          </span>
          <span class="text-[#908fa0]">•</span>
          <span class="text-[#908fa0]">{currentItem.domain}</span>
          <span class="text-[#908fa0]">•</span>
          <span class="text-[#38bdf8]">
            {(currentItem.incomingReferences?.length || 0) + (currentItem.outgoingReferences?.length || 0)} обратных связей
          </span>
        </div>

        <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
          {currentItem.title}
        </h1>

        {#if currentItem.summary}
          <p class="text-sm text-[#908fa0] leading-relaxed">
            {currentItem.summary}
          </p>
        {/if}
      </div>
    {/if}
  </header>

  <!-- Reading Body with Dynamic Inline Cross-Links -->
  {#if currentItem}
    <article class="p-6 rounded-2xl border space-y-4 leading-relaxed text-sm sm:text-base {theme === 'dark' ? 'bg-[#0f131c] border-[#31353e]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
      <div class="prose dark:prose-invert max-w-none space-y-4">
        {#each currentItem.content.split('\n\n') as paragraph}
          <p class="text-base leading-relaxed">
            {paragraph}
          </p>
        {/each}
      </div>

      <!-- Preview Popup Card for Interactive Crosslink Inspection -->
      {#if activePreviewRef}
        <div
          id="previewCard"
          class="my-4 p-4 rounded-xl border shadow-2xl relative transition-all animate-fadeIn {theme === 'dark' ? 'bg-[#1c2028] border-[#38bdf8]/40 text-[#dfe2ee]' : 'bg-[#f0f9ff] border-[#0284c7]/40 text-[#0f172a]'}"
        >
          <div class="flex items-start justify-between mb-2">
            <div class="flex items-center gap-2">
              <span class="text-lg text-[#38bdf8]">🔗</span>
              <h3 class="font-bold text-sm font-mono">
                [[{activePreviewRef.ref.targetTitle || activePreviewRef.ref.sourceTitle}]]
              </h3>
            </div>
            <button
              type="button"
              class="text-xs text-[#908fa0] hover:text-current px-2 py-1"
              on:click={hidePreview}
            >
              ✕
            </button>
          </div>
          <p class="text-xs text-[#908fa0] mb-3 leading-relaxed">
            {activePreviewRef.snippet}
          </p>
          <div class="flex items-center justify-between pt-2 border-t text-xs font-mono {theme === 'dark' ? 'border-[#31353e]' : 'border-[#e2e8f0]'}">
            <span class="text-[#7bd0ff]">Тип связи: {activePreviewRef.ref.relationshipType}</span>
            <button
              type="button"
              class="underline text-[#38bdf8] cursor-pointer"
              on:click={() => selectNode(activePreviewRef.ref.targetSlug || activePreviewRef.ref.sourceSlug)}
            >
              Перейти к узлу →
            </button>
          </div>
        </div>
      {/if}
    </article>
  {/if}

  <!-- Section: Bidirectional Cross-Links Matrix -->
  {#if currentItem}
    <section class="space-y-4 pt-4 border-t {theme === 'dark' ? 'border-[#31353e]' : 'border-[#e2e8f0]'}">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="text-lg text-[#38bdf8]">🕸️</span>
          <h3 class="text-lg font-bold tracking-tight">
            Двунаправленные перекрёстные ссылки и входящие упоминания
          </h3>
        </div>
        <span class="text-xs font-mono px-2 py-1 rounded border {theme === 'dark' ? 'bg-[#181c24] border-[#31353e] text-[#908fa0]' : 'bg-[#f8fafc] border-[#cbd5e1] text-[#64748b]'}">
          {(currentItem.outgoingReferences?.length || 0) + (currentItem.incomingReferences?.length || 0)} Связанных узлов
        </span>
      </div>

      <!-- Bento-style Relationship Matrix -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- Outgoing References -->
        {#if currentItem.outgoingReferences && currentItem.outgoingReferences.length > 0}
          {#each currentItem.outgoingReferences as ref}
            <div
              class="p-4 rounded-xl border transition-all text-left group {theme === 'dark' ? 'bg-[#0f131c] border-[#31353e]' : 'bg-white border-[#e2e8f0] shadow-sm'}"
            >
              <div class="flex items-center justify-between mb-2">
                <span class="text-xs font-mono px-2 py-0.5 rounded font-bold uppercase bg-[#38bdf8]/10 text-[#38bdf8] border border-[#38bdf8]/30">
                  {ref.relationshipType}
                </span>
                <button
                  type="button"
                  class="text-xs font-mono text-[#908fa0] hover:text-[#38bdf8] transition-colors cursor-pointer"
                  on:click={() => showPreview(ref)}
                >
                  Превью ↗
                </button>
              </div>
              <button
                type="button"
                class="w-full text-left font-bold text-base mb-1 hover:text-[#38bdf8] transition-colors cursor-pointer"
                on:click={() => selectNode(ref.targetSlug)}
              >
                {ref.targetTitle}
              </button>
              <p class="text-xs text-[#908fa0] line-clamp-2">
                {ref.description || 'Прямая гиперссылка из текста Канона.'}
              </p>
            </div>
          {/each}
        {/if}

        <!-- Incoming References (Backlinks) -->
        {#if currentItem.incomingReferences && currentItem.incomingReferences.length > 0}
          {#each currentItem.incomingReferences as ref}
            <div
              class="p-4 rounded-xl border transition-all text-left group {theme === 'dark' ? 'bg-[#0f131c] border-[#31353e]' : 'bg-white border-[#e2e8f0] shadow-sm'}"
            >
              <div class="flex items-center justify-between mb-2">
                <span class="text-xs font-mono px-2 py-0.5 rounded font-bold uppercase bg-[#7bd0ff]/10 text-[#7bd0ff] border border-[#7bd0ff]/30">
                  Входящая ссылка (Backlink)
                </span>
                <button
                  type="button"
                  class="text-xs font-mono text-[#908fa0] hover:text-[#7bd0ff] transition-colors cursor-pointer"
                  on:click={() => showPreview(ref)}
                >
                  Превью ↗
                </button>
              </div>
              <button
                type="button"
                class="w-full text-left font-bold text-base mb-1 hover:text-[#7bd0ff] transition-colors cursor-pointer"
                on:click={() => selectNode(ref.sourceSlug)}
              >
                {ref.sourceTitle}
              </button>
              <p class="text-xs text-[#908fa0] line-clamp-2">
                {ref.description || 'Узел ссылается на данное понятие Канона.'}
              </p>
            </div>
          {/each}
        {/if}
      </div>

      <!-- Graph Density Metric Card -->
      <div class="p-4 rounded-xl border flex flex-col sm:flex-row items-center justify-between gap-4 font-mono text-xs {theme === 'dark' ? 'bg-[#181c24] border-[#31353e]' : 'bg-[#f8fafc] border-[#cbd5e1]'}">
        <div class="flex items-center gap-3">
          <span class="w-2.5 h-2.5 rounded-full bg-[#38bdf8] animate-pulse"></span>
          <span>ПЛОТНОСТЬ СВЯЗЕЙ В КОРПУСЕ: High Cohesion</span>
        </div>
        <div class="flex items-center gap-4 text-[#908fa0]">
          <span>Входящих: <strong class="text-[#38bdf8]">{currentItem.incomingReferences?.length || 0}</strong></span>
          <span>Исходящих: <strong class="text-[#7bd0ff]">{currentItem.outgoingReferences?.length || 0}</strong></span>
        </div>
      </div>
    </section>
  {/if}
</div>
