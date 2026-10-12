<script lang="ts">
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export interface ContactLink {
    id: string;
    label: string;
    platform: string;
    url: string;
    tag: string;
  }

  export let contactLinks: ContactLink[] = [
    { id: 'tg', label: 'Telegram Channel', platform: 'Telegram', url: 'https://t.me/efremov_mvp', tag: '[TG_CORE]' },
    { id: 'yt', label: 'YouTube Channel', platform: 'YouTube', url: 'https://youtube.com/@efremov_dmitriy', tag: '[YT_MEDIA]' },
    { id: 'gh', label: 'GitHub Repository', platform: 'GitHub', url: 'https://github.com/dmitryefremov', tag: '[GH_SRC]' },
    { id: 'li', label: 'LinkedIn Profile', platform: 'LinkedIn', url: 'https://linkedin.com/in/dmitryefremov', tag: '[LI_PROF]' },
    { id: 'em', label: 'Direct Email', platform: 'Email', url: 'mailto:contact@dmitryefremov.com', tag: '[EM_DIRECT]' }
  ];

  let loggedEvents: TelemetryEvent[] = [];

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

  <!-- External Links Terminal Matrix -->
  <div id="contact-links-grid" class="space-y-3 mb-6">
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

</div>
