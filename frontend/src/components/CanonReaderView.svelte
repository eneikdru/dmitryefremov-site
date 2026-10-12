<script lang="ts">
  import { parseMarkdown } from '../lib/markdown';
  import SeoMeta from './SeoMeta.svelte';

  interface CanonManifesto {
    id: string;
    domain: string;
    title: string;
    summary: string;
    contentMarkdown: string;
  }

  const CANON_MANIFESTOS: CanonManifesto[] = [
    {
      id: 'canon-01',
      domain: 'Autonomous Engineering',
      title: 'The Principles of Autonomous Engineering',
      summary: 'Foundational principles of self-governing software architectures that heal and regulate without human intervention.',
      contentMarkdown: '# Autonomous Engineering\n\nSystems must self-heal and self-regulate without human intervention. The absence of explicit visual noise is a sign of architectural maturity.'
    },
    {
      id: 'canon-02',
      domain: 'Agentic Economy (A2A)',
      title: 'A2A Economy Manifest',
      summary: 'Agent-to-Agent transactions and the future of programmatic value exchange at machine speed.',
      contentMarkdown: '# Agentic Economy\n\nThe future economy is driven by autonomous agents negotiating and transacting value directly. We build systems for hundreds of thousands of external agents.'
    },
    {
      id: 'canon-03',
      domain: 'Pragmatic Business Strategy',
      title: 'Pragmatic Delivery Strategy',
      summary: 'Aligning technological capabilities with concrete, verifiable business outcomes.',
      contentMarkdown: '# Pragmatic Business Strategy\n\nTechnology is a means to an end. Strategy must be grounded in reality, with measurable outcomes and clear boundaries.'
    },
    {
      id: 'canon-04',
      domain: 'Philosophy of Mind',
      title: 'Ontology of the Extended Mind',
      summary: 'Exploring the boundaries of cognition, distributed awareness, and how tools become part of thought.',
      contentMarkdown: '# Philosophy of Mind\n\nCognition extends beyond the biological brain into the tools and systems we build. Interfaces shape thought.'
    }
  ];

  let selectedManifestoId: string = CANON_MANIFESTOS[0].id;
  $: selectedManifesto = CANON_MANIFESTOS.find(m => m.id === selectedManifestoId) || CANON_MANIFESTOS[0];
  $: parsedContent = parseMarkdown(selectedManifesto.contentMarkdown);

  function handleSelect(id: string) {
    selectedManifestoId = id;
  }
</script>

<SeoMeta
  title="{selectedManifesto.title} | The Canon | Dmitry Efremov"
  description={selectedManifesto.summary}
  type="article"
  url="https://dmitryefremov.com/canon/{selectedManifesto.id}"
  keywords={[selectedManifesto.domain, 'Canon', 'Manifesto', 'Autonomous Systems']}
/>

<div id="canon-reader-root" class="space-y-6">
  <header class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1f2433] pb-4">
    <div>
      <div class="flex items-center gap-2 mb-1">
        <span class="bg-[#12151e] text-[#38bdf8] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono uppercase">
          [TIER_1_CANON]
        </span>
      </div>
      <h2 class="text-xl font-bold tracking-tight text-white">The Canon: Evergreen Manifestos</h2>
    </div>
  </header>

  <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
    <nav id="canon-manifesto-list" class="md:col-span-5 space-y-4" aria-label="Canon Manifestos">
      <div class="text-xs font-mono text-[#94a3b8] mb-2 uppercase">Ontology & Glossary</div>
      <div class="space-y-3" role="list">
        {#each CANON_MANIFESTOS as manifesto}
          <button
            type="button"
            class="w-full p-4 rounded-lg border transition-all cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#38bdf8] {selectedManifestoId === manifesto.id ? 'bg-[#12151e] border-[#38bdf8] shadow-md' : 'bg-[#090a0f] border-[#1f2433] hover:border-[#34d399]/50 hover:bg-[#12151e]/50'}"
            on:click={() => handleSelect(manifesto.id)}
            aria-current={selectedManifestoId === manifesto.id ? 'true' : 'false'}
          >
            <div class="text-[10px] font-mono mb-2 px-1.5 py-0.5 inline-block rounded bg-[#1f2433] text-[#38bdf8] uppercase">
              {manifesto.domain}
            </div>
            <h3 class="font-bold text-white text-base mb-1 hover:text-[#38bdf8] transition-colors">
              {manifesto.title}
            </h3>
            <p class="text-xs text-[#94a3b8] line-clamp-2 leading-relaxed">
              {manifesto.summary}
            </p>
          </button>
        {/each}
      </div>
    </nav>

    <article id="canon-active-content" class="md:col-span-7 bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] flex flex-col font-sans h-full min-h-[400px]">
      <header class="border-b border-[#1f2433] pb-4 mb-4">
        <h1 class="text-2xl font-bold tracking-tight text-white">{selectedManifesto.title}</h1>
        <div class="mt-2 text-xs font-mono text-[#94a3b8]">Domain: {selectedManifesto.domain}</div>
      </header>
      <div class="prose prose-invert prose-sm max-w-none text-[#cbd5e1] leading-relaxed font-serif overflow-y-auto">
        {@html parsedContent.html}
      </div>
    </article>
  </div>
</div>
