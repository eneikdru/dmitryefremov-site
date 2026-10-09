<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export interface ContactLink {
    id: string;
    label: string;
    platform: string;
    url: string;
    tag: string;
  }

  export interface DocumentRecord {
    id: string | number;
    title: string;
    author: string;
    date: string;
    downloadUrl?: string;
    url?: string;
  }

  export let endpoint: string = '/api/v1/materials';

  export let contactLinks: ContactLink[] = [
    { id: 'tg', label: 'Telegram Channel', platform: 'Telegram', url: 'https://t.me/efremov_mvp', tag: '[TG_CORE]' },
    { id: 'yt', label: 'YouTube Channel', platform: 'YouTube', url: 'https://youtube.com/@efremov_dmitriy', tag: '[YT_MEDIA]' },
    { id: 'gh', label: 'GitHub Repository', platform: 'GitHub', url: 'https://github.com/dmitryefremov', tag: '[GH_SRC]' },
    { id: 'li', label: 'LinkedIn Profile', platform: 'LinkedIn', url: 'https://linkedin.com/in/dmitryefremov', tag: '[LI_PROF]' },
    { id: 'em', label: 'Direct Email', platform: 'Email', url: 'mailto:contact@dmitryefremov.com', tag: '[EM_DIRECT]' }
  ];

  let documents: DocumentRecord[] = [];
  let isLoading: boolean = true;
  let error: string | null = null;
  let totalCount: number = 0;

  let loggedEvents: TelemetryEvent[] = [];

  export async function fetchMaterials() {
    isLoading = true;
    error = null;
    try {
      const res = await fetch(endpoint);
      if (!res.ok) {
        throw new Error(`Server returned HTTP ${res.status}: ${res.statusText}`);
      }
      const data = await res.json();
      let list: DocumentRecord[] = [];
      if (Array.isArray(data)) {
        list = data;
      } else if (data && Array.isArray(data.content)) {
        list = data.content;
      } else if (data && Array.isArray(data.items)) {
        list = data.items;
      } else if (data && Array.isArray(data.materials)) {
        list = data.materials;
      } else if (data && Array.isArray(data.documents)) {
        list = data.documents;
      }
      documents = list;
      totalCount = data?.totalElements ?? data?.total ?? list.length;
    } catch (e: any) {
      error = e?.message || 'Failed to connect to backend server';
      documents = [];
      totalCount = 0;
    } finally {
      isLoading = false;
    }
  }

  onMount(() => {
    fetchMaterials();
  });

  function handleOutboundClick(link: ContactLink, e: MouseEvent) {
    const event = telemetry.trackOutboundClick(link.url, link.label, link.platform);
    loggedEvents = [...loggedEvents, event];
  }
</script>

<div id="contact-terminal-root" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] max-w-2xl mx-auto my-4 font-sans space-y-6">
  <!-- Header -->
  <header id="contact-terminal-header" class="border-b border-[#1f2433] pb-4">
    <div class="flex items-center gap-2 mb-2">
      <span class="bg-[#12151e] text-[#f43f5e] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">[SYS_TERMINAL]</span>
      <span class="text-xs font-mono text-[#94a3b8]">CONTACT_GATEWAY</span>
    </div>
    <h2 class="text-2xl font-bold tracking-tight text-white mb-1">Contact & Communications</h2>
    <p class="text-xs text-[#94a3b8] font-mono">Dmitry Efremov · Systems, Thinking Logic & Autonomous Processes</p>
  </header>

  <!-- External Links Terminal Matrix -->
  <div id="contact-links-grid" class="space-y-3">
    <div class="text-xs font-mono text-[#94a3b8] mb-1 flex items-center justify-between">
      <span>[COMMUNICATION_CHANNELS]</span>
    </div>
    {#each contactLinks as link}
      <a
        id="link-{link.id}"
        href={link.url}
        target="_blank"
        rel="noopener noreferrer"
        on:click={(e) => handleOutboundClick(link, e)}
        class="flex flex-col sm:flex-row sm:items-center justify-between p-3 gap-2 rounded bg-[#12151e] border border-[#1f2433] hover:border-[#38bdf8] transition-colors group text-sm font-mono overflow-hidden"
      >
        <div class="flex items-center gap-3 min-w-0">
          <span class="text-[#f43f5e] text-xs font-bold shrink-0">{link.tag}</span>
          <span class="text-white group-hover:text-[#38bdf8] transition-colors truncate">{link.label}</span>
        </div>
        <div class="flex items-center gap-1 text-xs text-[#64748b] group-hover:text-[#38bdf8] min-w-0">
          <span class="truncate">{link.url}</span>
          <span class="shrink-0">↗</span>
        </div>
      </a>
    {/each}
  </div>

  <!-- Domain Records / Materials Search Section -->
  <section id="materials-catalogue-section" class="pt-4 border-t border-[#1f2433]">
    <div class="flex items-center justify-between text-xs font-mono mb-3">
      <span class="text-[#94a3b8]">[MATERIALS_CATALOGUE]</span>
      {#if isLoading}
        <span id="materials-status-badge" class="text-[#38bdf8]">LOADING...</span>
      {:else if error}
        <span id="materials-status-badge" class="text-[#f43f5e]">ERROR</span>
      {:else if documents.length === 0}
        <span id="materials-status-badge" class="text-[#94a3b8]">0 MATERIALS</span>
      {:else}
        <span id="materials-status-badge" class="text-[#34d399]">{documents.length} MATERIALS</span>
      {/if}
    </div>

    <!-- 4 Data States -->
    {#if isLoading}
      <div id="materials-loading" class="p-4 rounded bg-[#12151e] border border-[#1f2433] text-xs font-mono text-[#94a3b8] flex items-center gap-2">
        <span class="animate-pulse text-[#38bdf8]">●</span>
        <span>Loading materials from server...</span>
      </div>
    {:else if error}
      <div id="materials-error" class="p-4 rounded bg-[#12151e] border border-[#f43f5e]/40 text-xs font-mono text-[#f43f5e] space-y-1">
        <div class="font-bold">[SERVER_ERROR] Domain records unavailable</div>
        <div class="text-[#fca5a5]">{error}</div>
      </div>
    {:else if documents.length === 0}
      <div id="materials-empty" class="p-4 rounded bg-[#12151e] border border-[#1f2433] text-xs font-mono text-[#94a3b8] space-y-1">
        <div class="font-bold text-[#e2e8f0]">Showing 0 materials</div>
        <div>No domain records returned by server. Catalogue is empty.</div>
      </div>
    {:else}
      <div id="materials-list" class="space-y-3">
        <div id="materials-count" class="text-xs font-mono text-[#38bdf8]">
          Showing 1-{documents.length} of {totalCount} materials
        </div>
        {#each documents as doc}
          <div id="doc-{doc.id}" class="p-3 rounded bg-[#12151e] border border-[#1f2433] flex flex-col sm:flex-row sm:items-center justify-between gap-2 font-mono text-xs">
            <div class="space-y-1 min-w-0">
              <div class="text-white font-bold truncate">{doc.title}</div>
              <div class="text-[#94a3b8] text-[11px]">Author: {doc.author} · Date: {doc.date}</div>
            </div>
            <a
              href={doc.downloadUrl || doc.url || '#'}
              download
              class="px-3 py-1.5 rounded bg-[#1f2433] hover:bg-[#2e364f] text-[#38bdf8] font-bold border border-[#38bdf8]/30 transition-colors text-center shrink-0 cursor-pointer"
            >
              Download
            </a>
          </div>
        {/each}
      </div>
    {/if}
  </section>

  <!-- Telemetry Event Log -->
  <footer id="contact-telemetry-log" class="pt-4 border-t border-[#1f2433]">
    <div class="text-xs font-mono text-[#94a3b8] mb-2 flex items-center justify-between">
      <span>[OUTBOUND_CLICK_TRACKER]</span>
      <span class="text-[#38bdf8]">PRIVACY_PRESERVED</span>
    </div>
    <div class="bg-[#12151e] p-3 rounded border border-[#1f2433] font-mono text-xs max-h-32 overflow-y-auto space-y-1 text-[#93c5fd]">
      {#if loggedEvents.length === 0}
        <span class="text-[#64748b]">Click any contact link above to instrument outbound click telemetry...</span>
      {:else}
        {#each loggedEvents as evt}
          <div class="flex justify-between border-b border-[#1f2433] pb-1 gap-2">
            <span class="truncate">[CLICK] {evt.payload.platform}: {evt.payload.destination}</span>
            <span class="text-[#64748b] shrink-0">{evt.timestamp.slice(11, 19)}</span>
          </div>
        {/each}
      {/if}
    </div>
  </footer>
</div>
