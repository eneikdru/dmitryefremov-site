<script lang="ts">
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export interface ContactLink {
    id: string;
    label: string;
    platform: string;
    url: string;
    description: string;
  }

  export let contactLinks: ContactLink[] = [
    { id: 'tg', label: 'Telegram Channel', platform: 'Telegram', url: 'https://t.me/efremov_mvp', description: 'Main broadcast channel for systems & philosophy' },
    { id: 'yt', label: 'YouTube Channel', platform: 'YouTube', url: 'https://www.youtube.com/@dmitrii.efremov', description: 'Video essays & Podcast "Systems & Reality"' },
    { id: 'gh', label: 'GitHub Repository', platform: 'GitHub', url: 'https://github.com/dmitriieneik-rgb', description: 'Open-source software, architectures & tools' },
    { id: 'li', label: 'LinkedIn Profile', platform: 'LinkedIn', url: 'https://www.linkedin.com/in/dmitrii-efremov-87b816349/', description: 'Professional network & technology leadership' },
    { id: 'em', label: 'Direct Email', platform: 'Email', url: 'mailto:dmitrii.eneik@gmail.com', description: 'Direct contact for inquiries & collaborations' }
  ];

  let activeFeedbackId: string | null = null;
  let copiedFeedbackId: string | null = null;

  function handleOutboundClick(link: ContactLink, e: MouseEvent) {
    telemetry.trackOutboundClick(link.url, link.label, link.platform);
    activeFeedbackId = link.id;
    setTimeout(() => {
      if (activeFeedbackId === link.id) {
        activeFeedbackId = null;
      }
    }, 1200);
  }

  async function copyToClipboard(link: ContactLink, e: MouseEvent) {
    e.preventDefault();
    e.stopPropagation();
    try {
      if (navigator.clipboard && navigator.clipboard.writeText) {
        await navigator.clipboard.writeText(link.url);
      }
      copiedFeedbackId = link.id;
      setTimeout(() => {
        if (copiedFeedbackId === link.id) {
          copiedFeedbackId = null;
        }
      }, 1500);
    } catch (err) {
      // Fallback feedback on error
      copiedFeedbackId = link.id;
      setTimeout(() => {
        if (copiedFeedbackId === link.id) {
          copiedFeedbackId = null;
        }
      }, 1500);
    }
  }
</script>

<div id="core-identity-card" class="bg-[#090a0f] text-[#e2e8f0] p-6 sm:p-8 rounded-xl border border-[#1f2433] max-w-3xl mx-auto my-6 font-sans shadow-2xl transition-all duration-300">
  <!-- Header / Visual Identity Branding -->
  <header id="identity-card-header" class="border-b border-[#1f2433] pb-6 mb-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
    <div class="space-y-1">
      
      <h2 id="identity-name" class="text-2xl sm:text-3xl font-extrabold tracking-tight text-white">Дмитрий Ефремов</h2>
      <p id="identity-bio" class="text-xs sm:text-sm text-[#94a3b8] font-mono leading-relaxed">Systems Architect & Deontic Logic Researcher</p>
    </div>
    
  </header>

  <!-- Interactive Contact Links Grid -->
  <section id="identity-links-section" aria-label="Contact Channels" class="space-y-4 mb-6">
    <h3 class="text-xs font-mono text-[#94a3b8] uppercase tracking-wider mb-2">Каналы связи</h3>
    <div id="identity-links-grid" class="grid grid-cols-1 gap-3">
      {#each contactLinks as link}
        <div
          id="contact-card-{link.id}"
          class="group relative flex flex-col sm:flex-row sm:items-center justify-between p-4 rounded-lg bg-[#12151e] border border-[#1f2433] hover:border-[#38bdf8] focus-within:border-[#38bdf8] transition-all duration-200 gap-3"
        >
          <a
            id="link-{link.id}"
            href={link.url}
            target="_blank"
            rel="noopener noreferrer"
            aria-label="{link.label} - {link.description}"
            on:click={(e) => handleOutboundClick(link, e)}
            class="flex-1 flex flex-col sm:flex-row sm:items-center justify-between gap-2 text-sm font-mono focus:outline-none"
          >
            <div class="flex items-center gap-3 min-w-0">
              
              <div class="min-w-0">
                <span class="text-white font-semibold group-hover:text-[#38bdf8] transition-colors block truncate">{link.label}</span>
                <span class="text-xs text-[#64748b] group-hover:text-[#94a3b8] transition-colors block truncate">{link.description}</span>
              </div>
            </div>
            <div class="flex items-center gap-1 text-xs text-[#64748b] group-hover:text-[#38bdf8] shrink-0">
              <span class="truncate max-w-[180px] hidden md:inline">{link.url}</span>
              <span class="font-bold">↗</span>
            </div>
          </a>

          <!-- Interactive Action Buttons / Feedback -->
          <div class="flex items-center gap-2 border-t sm:border-t-0 pt-2 sm:pt-0 border-[#1f2433] justify-end shrink-0">
            <button
              id="copy-btn-{link.id}"
              type="button"
              aria-label="Copy URL for {link.label}"
              on:click={(e) => copyToClipboard(link, e)}
              class="px-2.5 py-1 rounded text-xs font-mono border border-[#1f2433] bg-[#090a0f] text-[#94a3b8] hover:text-white hover:border-[#38bdf8] transition-colors flex items-center gap-1 focus:outline-none focus:ring-1 focus:ring-[#38bdf8]"
            >
              {#if copiedFeedbackId === link.id}
                <span class="text-[#38bdf8] font-bold">
                  <span class="sr-only">COPIED ✓</span>
                  <span>Скопировано ✓</span>
                </span>
              {:else}
                <span>
                  <span class="sr-only">COPY</span>
                  <span>Копировать</span>
                </span>
              {/if}
            </button>
            {#if activeFeedbackId === link.id}
              <span class="text-xs font-mono text-[#38bdf8] font-bold animate-fade-in"><span class="sr-only">[OPENING...]</span><span>Переход...</span></span>
            {/if}
          </div>
        </div>
      {/each}
    </div>
  </section>

  <!-- High-End Dark Clean Footer / Identity Note -->
  
</div>
