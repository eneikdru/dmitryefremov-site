<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export interface DocumentRecord {
    id: string | number;
    title: string;
    author: string;
    date: string;
    downloadUrl: string;
  }

  export interface ContactLink {
    id: string;
    label: string;
    platform: string;
    url: string;
    tag: string;
  }

  export let endpointUrl: string = '/api/v1/documents';
  export let autoFetch: boolean = true;

  export let contactLinks: ContactLink[] = [
    { id: 'tg', label: 'Telegram Channel', platform: 'Telegram', url: 'https://t.me/efremov_mvp', tag: '[TG_CORE]' },
    { id: 'yt', label: 'YouTube Channel', platform: 'YouTube', url: 'https://youtube.com/@efremov_dmitriy', tag: '[YT_MEDIA]' },
    { id: 'gh', label: 'GitHub Repository', platform: 'GitHub', url: 'https://github.com/dmitryefremov', tag: '[GH_SRC]' },
    { id: 'li', label: 'LinkedIn Profile', platform: 'LinkedIn', url: 'https://linkedin.com/in/dmitryefremov', tag: '[LI_PROF]' },
    { id: 'em', label: 'Direct Email', platform: 'Email', url: 'mailto:contact@dmitryefremov.com', tag: '[EM_DIRECT]' }
  ];

  export let documents: DocumentRecord[] = [];
  export let loading: boolean = true;
  export let error: string | null = null;

  let loggedEvents: TelemetryEvent[] = [];

  export async function fetchDocuments() {
    loading = true;
    error = null;
    try {
      const response = await fetch(endpointUrl);
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      const data = await response.json();
      const items = Array.isArray(data) ? data : (data?.content || data?.documents || []);
      documents = items.map((item: any, idx: number) => ({
        id: item.id ?? `doc-${idx}`,
        title: item.title ?? item.name ?? 'Untitled Document',
        author: item.author ?? item.authors ?? item.authorName ?? 'Dmitry Efremov',
        date: item.date ?? item.publishedAt ?? item.createdAt ?? '',
        downloadUrl: item.downloadUrl ?? item.url ?? item.mediaLink ?? '#'
      }));
    } catch (err: any) {
      error = err?.message || 'Failed to connect to server';
      documents = [];
    } finally {
      loading = false;
    }
  }

  onMount(() => {
    if (autoFetch) {
      fetchDocuments();
    } else {
      loading = false;
    }
  });

  function handleOutboundClick(link: ContactLink, e: MouseEvent) {
    const event = telemetry.trackOutboundClick(link.url, link.label, link.platform);
    loggedEvents = [...loggedEvents, event];
  }
</script>

<div id="contact-terminal-root" class="bg-[#090a0f] text-[#e2e8f0] p-6 rounded-lg border border-[#1f2433] max-w-2xl mx-auto my-4 font-sans">
  <!-- Header -->
  <header id="contact-terminal-header" class="border-b border-[#1f2433] pb-4 mb-6">
    <div class="flex items-center gap-2 mb-2">
      <span class="bg-[#12151e] text-[#f43f5e] border border-[#1f2433] px-2 py-0.5 rounded text-xs font-mono">[SYS_TERMINAL]</span>
      <span class="text-xs font-mono text-[#94a3b8]">CONTACT_GATEWAY</span>
    </div>
    <h2 class="text-2xl font-bold tracking-tight text-white mb-1">Contact & Communications</h2>
    <p class="text-xs text-[#94a3b8] font-mono">Dmitry Efremov · Systems, Thinking Logic & Autonomous Processes</p>
  </header>

  <!-- Materials / Documents Catalog Matrix -->
  <section id="materials-catalog-section" class="mb-6">
    <div class="flex items-center justify-between mb-3">
      <h3 class="text-lg font-bold text-white tracking-tight">System Materials & Documents</h3>
      {#if !loading && !error}
        <span id="materials-count" class="text-xs font-mono text-[#94a3b8]">
          Showing {documents.length} materials
        </span>
      {/if}
    </div>

    {#if loading}
      <!-- Loading State -->
      <div id="materials-loading-state" class="bg-[#12151e] border border-[#1f2433] rounded p-6 text-center font-mono text-xs text-[#38bdf8] flex items-center justify-center gap-2">
        <span class="animate-pulse">[SYS_LOADING]</span>
        <span>Fetching materials from server...</span>
      </div>
    {:else if error}
      <!-- Error State -->
      <div id="materials-error-state" class="bg-[#12151e] border border-[#f43f5e] rounded p-6 text-center font-mono text-xs text-[#f43f5e]">
        <div class="font-bold mb-1">[SYS_ERROR] Request Failed</div>
        <div class="text-[#94a3b8]">{error}</div>
      </div>
    {:else if documents.length === 0}
      <!-- Empty State -->
      <div id="materials-empty-state" class="bg-[#12151e] border border-[#1f2433] rounded p-6 text-center font-mono text-xs text-[#94a3b8]">
        <span class="text-[#f43f5e] font-bold mr-2">[SYS_EMPTY]</span>
        <span>No materials available from server.</span>
      </div>
    {:else}
      <!-- Present State -->
      <div id="materials-list" class="space-y-3">
        {#each documents as doc (doc.id)}
          <div id="doc-{doc.id}" class="flex flex-col sm:flex-row sm:items-center justify-between p-3 gap-2 rounded bg-[#12151e] border border-[#1f2433] text-sm font-mono">
            <div class="space-y-1 min-w-0">
              <div class="text-white font-bold truncate">{doc.title}</div>
              <div class="text-xs text-[#94a3b8] flex items-center gap-3">
                <span>Author: {doc.author}</span>
                {#if doc.date}
                  <span>Date: {doc.date}</span>
                {/if}
              </div>
            </div>
            <a
              id="download-btn-{doc.id}"
              href={doc.downloadUrl}
              download
              class="shrink-0 px-3 py-1 rounded bg-[#1f2433] text-[#38bdf8] hover:bg-[#38bdf8] hover:text-[#090a0f] transition-colors text-xs font-bold text-center"
            >
              Download
            </a>
          </div>
        {/each}
      </div>
    {/if}
  </section>

  <!-- External Links Terminal Matrix -->
  <div id="contact-links-grid" class="space-y-3 mb-6">
    <h3 class="text-sm font-bold text-[#94a3b8] font-mono mb-2">[EXTERNAL_GATEWAYS]</h3>
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
