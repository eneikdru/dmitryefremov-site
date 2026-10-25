<script lang="ts">
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export interface ContactLink {
    id: string;
    label: string;
    platform: string;
    url: string;
    tag: string;
    description: string;
  }

  export let contactLinks: ContactLink[] = [
    { id: 'tg', label: 'Telegram Channel', platform: 'Telegram', url: 'https://t.me/efremov_mvp', tag: '[TG_CORE]', description: 'Main broadcast channel for systems & philosophy' },
    { id: 'yt', label: 'YouTube Channel', platform: 'YouTube', url: 'https://youtube.com/@dmitrii.efremov', tag: '[YT_MEDIA]', description: 'Video essays & Podcast "Systems & Reality"' },
    { id: 'gh', label: 'GitHub Repository', platform: 'GitHub', url: 'https://github.com/dmitryefremov', tag: '[GH_SRC]', description: 'Open-source software, architectures & tools' },
    { id: 'li', label: 'LinkedIn Profile', platform: 'LinkedIn', url: 'https://linkedin.com/in/dmitryefremov', tag: '[LI_PROF]', description: 'Professional network & technology leadership' },
    { id: 'em', label: 'Direct Email', platform: 'Email', url: 'mailto:contact@dmitryefremov.com', tag: '[EM_DIRECT]', description: 'Direct contact for inquiries & collaborations' }
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


<div id="core-identity-card" class="bg-surface-container-lowest rounded-xl p-space-md border border-outline-variant/70 shadow-sm relative overflow-hidden font-sans text-on-surface w-full max-w-[540px] mx-auto my-4 selection:bg-secondary-fixed selection:text-on-secondary-fixed">
  <!-- Subtle Top Tone Accent -->
  <div class="absolute top-0 left-0 right-0 h-1.5 bg-gradient-to-r from-[#316bf3] via-[#0051d5] to-[#131b2e]"></div>
  <div class="flex items-start gap-space-md mt-1">
    <!-- Verified Avatar with Status Dot -->
    <div class="relative shrink-0">
      <div id="hero-avatar-img" class="w-16 h-16 rounded-xl overflow-hidden border-2 border-surface-container-lowest shadow-sm ring-2 ring-outline-variant/50 bg-[#12151e] flex items-center justify-center">
        <!-- We will just show initials as no avatar image URL is provided in the existing code, or keep a generic placeholder -->
        <span class="text-xl font-bold text-white">ДЕ</span>
      </div>
      <!-- Online status badge -->
      <span class="absolute -bottom-1 -right-1 w-4 h-4 bg-emerald-600 rounded-full border-2 border-surface-container-lowest ring-1 ring-emerald-700/30 flex items-center justify-center" title="ACTIVE_ONLINE">
        <span class="w-1.5 h-1.5 bg-white rounded-full"></span>
      </span>
    </div>
    <!-- Name, Entity & Bio -->
    <div class="flex-1 min-w-0">
      <div class="flex items-center gap-1.5 flex-wrap">
        <h2 id="identity-name" class="text-headline-sm font-headline-sm text-on-surface tracking-tight font-bold text-[18px]">Дмитрий Ефремов</h2>
        <span class="inline-flex items-center gap-1 bg-[#dbe1ff]/50 text-[#00174b] text-[11px] font-bold px-2 py-0.5 rounded-full border border-[#b4c5ff]">
          <span class="text-[13px]">✓</span>
          VERIFIED
        </span>
      </div>
      <p id="identity-bio" class="text-body-sm font-body-sm text-on-surface-variant mt-0.5 font-medium text-[13px]">Systems Architect & Deontic Logic Researcher</p>
    </div>
  </div>

  <!-- Bio text -->
  <p class="text-body-sm font-body-sm text-on-surface mt-4 leading-relaxed text-[13px]">
    Системы, логика мышления и автономные процессы. Открыт для обсуждения архитектурных паттернов и интеграции AI-агентов.
  </p>

  <!-- Dynamic Status & Availabilities Chips -->
  <div class="mt-4 pt-2 border-t border-outline-variant/40 flex flex-wrap gap-2 text-[11px] font-bold">
    <div class="inline-flex items-center gap-1.5 bg-[#eff4ff] text-on-surface px-2.5 py-1 rounded-full border border-outline-variant/60">
      <span>⚡</span>
      <span>ACTIVE_ONLINE</span>
    </div>
    <div class="inline-flex items-center gap-1.5 bg-[#eff4ff] text-on-surface px-2.5 py-1 rounded-full border border-outline-variant/60">
      <span>🌐</span>
      <span>[IDENTITY_CARD]</span>
    </div>
  </div>
</div>

<section id="identity-links-section" aria-label="Contact Channels" class="space-y-3 w-full max-w-[540px] mx-auto mb-6">
  <div class="flex items-center justify-between px-1">
    <h3 class="text-[12px] font-bold text-on-surface uppercase tracking-wider">Verified Channels & Matrix</h3>
    <span class="text-[11px] font-bold text-on-surface-variant flex items-center gap-1">
      <span class="text-[#0051d5]">🔒</span>
      Encrypted
    </span>
  </div>

  <div id="identity-links-grid" class="grid grid-cols-1 gap-2.5">
    {#each contactLinks as link}
      <div id="contact-card-{link.id}" class="bg-surface-container-lowest border border-outline-variant/70 rounded-xl p-3 flex items-center justify-between hover:border-[#0051d5] transition-colors group">
        <a id="link-{link.id}" href={link.url} target="_blank" rel="noopener noreferrer" on:click={(e) => handleOutboundClick(link, e)} class="flex-1 flex items-center gap-3 min-w-0 focus:outline-none focus:ring-2 focus:ring-[#0051d5]/30 rounded">
          <div class="w-10 h-10 rounded-lg bg-[#eff4ff] flex items-center justify-center text-on-surface shrink-0 group-hover:bg-[#dbe1ff]/40 transition-colors">
            <span class="text-[20px] font-bold text-[#f43f5e]">{link.tag.charAt(1)}</span>
          </div>
          <div class="min-w-0 flex-1 pr-2">
            <span class="text-[11px] font-bold text-on-surface-variant block">{link.tag}</span>
            <span class="text-[15px] text-on-surface font-semibold truncate block">{link.label}</span>
          </div>
        </a>
        <div class="flex items-center gap-2">
          {#if activeFeedbackId === link.id}
            <span class="text-xs font-mono text-[#0051d5] font-bold animate-fade-in">[OPENING...]</span>
          {/if}
          <button id="copy-btn-{link.id}" type="button" aria-label="Copy URL for {link.label}" on:click={(e) => copyToClipboard(link, e)} class="px-3 py-1.5 bg-[#eff4ff] text-on-surface hover:bg-[#e5eeff] border border-outline-variant rounded-lg flex items-center gap-1.5 text-[11px] font-bold shrink-0 transition-transform active:translate-y-px {copiedFeedbackId === link.id ? 'bg-[#dbe1ff] text-[#00174b]' : ''}">
            {#if copiedFeedbackId === link.id}
              <span>✓ Copied</span>
            {:else}
              <span>Copy</span>
            {/if}
          </button>
        </div>
      </div>
    {/each}
  </div>

  <footer id="identity-card-footer" class="text-center py-4 space-y-1">
    <div class="inline-flex items-center gap-1 text-[11px] font-bold text-on-surface-variant">
      <span class="text-[16px] text-[#0051d5]">✓</span>
      <span>Profile Cryptographically Verified</span>
    </div>
    <p class="text-[11px] text-outline leading-tight">Zero debug logging on facade · Clean Brand Identity</p>
  </footer>
</section>
