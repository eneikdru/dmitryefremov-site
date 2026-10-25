<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  let isStreaming = $state(true);
  let activeFilter = $state('all');
  let expandedPayloads = $state<Record<string, boolean>>({});
  let rawEvents = $state<TelemetryEvent[]>([]);

  let filteredEvents = $derived.by(() => {
    return rawEvents.filter(evt => {
      if (activeFilter === 'all') return true;
      if (activeFilter === 'validation' && (evt.type === 'form_validation_passed' || evt.type === 'form_field_focused')) return true;
      if (activeFilter === 'clicks' && (evt.type === 'outbound_click' || evt.type === 'cross_link_click')) return true;
      if (activeFilter === 'goals' && (evt.type === 'canon_submit_converted' || evt.type === 'canon_dwell_heartbeat')) return true;
      return false;
    });
  });

  let unsubscribe: () => void;

  onMount(() => {
    rawEvents = telemetry.getEvents();
    unsubscribe = telemetry.subscribe((evt) => {
      if (isStreaming) {
        rawEvents = [...rawEvents, evt];
      }
    });
  });

  onDestroy(() => {
    if (unsubscribe) unsubscribe();
  });

  function toggleStream() {
    isStreaming = !isStreaming;
    if (isStreaming) {
      // Fetch latest on resume
      rawEvents = telemetry.getEvents();
    }
  }

  function togglePayload(eventId: string) {
    expandedPayloads[eventId] = !expandedPayloads[eventId];
  }

  function copySnippet(payload: any) {
    if (typeof navigator !== 'undefined' && navigator.clipboard) {
      navigator.clipboard.writeText(JSON.stringify(payload, null, 2)).catch(() => {});
    }
  }

  function setFilter(filter: string) {
    activeFilter = filter;
  }

  function getMethodColor(method: string) {
    switch(method) {
      case 'POST': return 'bg-[#8B5CF6]/20 text-[#d0bcff] border-[#8B5CF6]/40';
      case 'PUT': return 'bg-tertiary-container/30 text-tertiary border-tertiary/40';
      case 'WARN': return 'bg-[#F59E0B]/20 text-[#F59E0B] border-[#F59E0B]/40';
      default: return 'bg-secondary/10 text-secondary border-secondary/30';
    }
  }
</script>

<div class="dark w-full min-h-screen bg-surface-container-lowest font-sans selection:bg-primary/30 text-on-surface flex flex-col">
  <!-- Top App Bar -->
  <header class="sticky top-0 z-40 bg-surface-container-lowest/80 backdrop-blur-md border-b border-outline-variant/30">
    <div class="px-space-md py-space-sm flex items-center justify-between">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-lg bg-surface-container flex items-center justify-center border border-outline-variant/50 shadow-sm">
          <span class="material-symbols-outlined text-[18px] text-primary" aria-hidden="true">hub</span>
        </div>
        <div>
          <h1 class="text-label-lg font-label-lg font-bold tracking-tight">Epic Telemetry &amp; Events Tracker</h1>
          <div class="flex items-center gap-1.5 text-[10px] font-label-sm text-outline mt-0.5 uppercase tracking-wider">
            <span class="w-1.5 h-1.5 rounded-full {isStreaming ? 'bg-secondary animate-pulse' : 'bg-outline'} shadow-[0_0_8px_rgba(78,222,163,0.6)]"></span>
            <span>{isStreaming ? 'Live Connection Active' : 'Connection Paused'}</span>
          </div>
        </div>
      </div>
      <button
        type="button"
        class="w-8 h-8 rounded-full flex items-center justify-center hover:bg-surface-container transition-colors focus:outline-none focus:ring-2 focus:ring-primary/50"
        aria-label="Settings"
      >
        <span class="material-symbols-outlined text-[20px] text-outline" aria-hidden="true">settings</span>
      </button>
    </div>
  </header>

  <!-- Main Scrollable Area -->
  <main class="flex-1 overflow-y-auto pb-24">
    <!-- Stream Metrics & Controls -->
    <section class="p-space-md border-b border-outline-variant/20 bg-surface-container-low/50 space-y-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <div>
            <span class="text-[10px] font-label-sm text-outline uppercase tracking-wider block mb-1">Ingestion Rate</span>
            <div class="flex items-center gap-2 text-label-md font-label-md">
              <span class="text-on-surface font-semibold">{isStreaming ? '12 evt/s' : 'PAUSED'}</span>
            </div>
          </div>
          <div class="w-px h-8 bg-outline-variant/30"></div>
          <div>
            <span class="text-[10px] font-label-sm text-outline uppercase tracking-wider block mb-1">Dropped</span>
            <div class="flex items-center gap-2 text-label-md font-label-md text-error">
              <span class="font-semibold">0.00%</span>
            </div>
          </div>
        </div>

        <button
          id="toggle-stream-btn"
          type="button"
          onclick={toggleStream}
          class="flex items-center gap-2 px-3 py-1.5 rounded-lg border {isStreaming ? 'bg-surface-container border-outline-variant/50 text-outline hover:text-on-surface' : 'bg-primary/10 border-primary/30 text-primary hover:bg-primary/20'} transition-all active:scale-95"
        >
          <span class="material-symbols-outlined text-[18px]" aria-hidden="true">{isStreaming ? 'pause_circle' : 'play_circle'}</span>
          <span class="text-label-sm font-label-sm font-medium">{isStreaming ? 'Pause Stream' : 'Resume Stream'}</span>
        </button>
      </div>

      <!-- Filter Pills -->
      <div class="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-hide">
        <button
          onclick={() => setFilter('all')}
          class="flex-1 py-1.5 px-2 rounded text-label-sm font-label-sm transition-all text-center {activeFilter === 'all' ? 'bg-surface-container-high text-on-surface font-medium border border-outline-variant/50' : 'text-outline hover:text-on-surface'}"
        >
          All Events
        </button>
        <button
          onclick={() => setFilter('validation')}
          class="flex-1 py-1.5 px-2 rounded text-label-sm font-label-sm transition-all text-center {activeFilter === 'validation' ? 'bg-surface-container-high text-on-surface font-medium border border-outline-variant/50' : 'text-outline hover:text-on-surface'}"
        >
          Validation
        </button>
        <button
          onclick={() => setFilter('clicks')}
          class="flex-1 py-1.5 px-2 rounded text-label-sm font-label-sm transition-all text-center {activeFilter === 'clicks' ? 'bg-surface-container-high text-on-surface font-medium border border-outline-variant/50' : 'text-outline hover:text-on-surface'}"
        >
          Clicks
        </button>
        <button
          onclick={() => setFilter('goals')}
          class="flex-1 py-1.5 px-2 rounded text-label-sm font-label-sm transition-all text-center {activeFilter === 'goals' ? 'bg-surface-container-high text-on-surface font-medium border border-outline-variant/50' : 'text-outline hover:text-on-surface'}"
        >
          Core Goals
        </button>
      </div>
    </section>

    <!-- Telemetry Event Stream Feed -->
    <section class="p-space-md space-y-3" aria-label="Event Stream">
      {#if filteredEvents.length === 0}
        <div class="text-center py-8 text-outline text-label-sm font-label-sm">
          No matching events in the current buffer.
        </div>
      {/if}

      {#each filteredEvents as evt, index (evt.timestamp + '-' + index)}
        <article class="bg-surface-container rounded-lg border border-outline-variant/30 overflow-hidden hover:border-outline-variant/80 transition-all {evt.type.startsWith('error') ? 'bg-[#F59E0B]/[0.02] border-[#F59E0B]/30 hover:border-[#F59E0B]/60' : ''}">
          <div
            class="p-space-sm cursor-pointer select-none"
            onclick={() => togglePayload(`${evt.timestamp}-${index}`)}
            onkeydown={(e) => e.key === 'Enter' && togglePayload(`${evt.timestamp}-${index}`)}
            role="button"
            tabindex="0"
          >
            <div class="flex items-center justify-between text-label-sm font-label-sm text-outline mb-1">
              <span class="font-mono text-tertiary">{evt.timestamp.split('T')[1]?.replace('Z', ' UTC') || evt.timestamp}</span>
              <div class="flex items-center gap-1.5">
                {#if evt.type.startsWith('error')}
                  <span class="px-1.5 py-0.5 rounded text-[9px] font-label-sm bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30 flex items-center gap-0.5 font-mono">
                    <span class="material-symbols-outlined text-[11px]" aria-hidden="true">error</span> Handled Fail
                  </span>
                {:else}
                  <span class="px-1.5 py-0.5 rounded text-[9px] font-label-sm bg-secondary/10 text-secondary border border-secondary/30 flex items-center gap-0.5 font-mono">
                    <span class="material-symbols-outlined text-[11px]" aria-hidden="true">check_circle</span> Schema Valid ✓
                  </span>
                {/if}
                <span class="material-symbols-outlined text-[16px] text-outline transition-transform duration-200" style="transform: {expandedPayloads[`${evt.timestamp}-${index}`] ? 'rotate(180deg)' : 'rotate(0deg)'}" aria-hidden="true">expand_more</span>
              </div>
            </div>

            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <span class="px-1.5 py-0.5 rounded text-[10px] font-label-sm font-bold {getMethodColor(evt.type.startsWith('error') ? 'WARN' : 'POST')} font-mono uppercase">
                  {evt.type.startsWith('error') ? 'WARN' : 'POST'}
                </span>
                <span class="font-label-md text-label-md {evt.type.startsWith('error') ? 'text-[#F59E0B]' : 'text-on-surface'} font-semibold font-mono">{evt.type}</span>
              </div>
              <span class="text-label-sm font-label-sm text-outline font-mono">/stream</span>
            </div>
          </div>

          {#if expandedPayloads[`${evt.timestamp}-${index}`]}
            <div class="border-t border-outline-variant/20 bg-surface-container-lowest p-space-sm font-mono text-[11px] space-y-1">
              <div class="text-outline text-[10px] uppercase font-semibold mb-1 flex items-center justify-between">
                <span>Telemetry Payload Context</span>
                <button
                  type="button"
                  class="text-primary cursor-pointer hover:underline focus:outline-none"
                  onclick={(e) => { e.stopPropagation(); copySnippet(evt.payload); }}
                >
                  Copy JSON
                </button>
              </div>
              <div class="text-on-surface-variant leading-relaxed">
                <pre class="whitespace-pre-wrap"><code>{JSON.stringify(evt.payload, null, 2)}</code></pre>
              </div>
            </div>
          {/if}
        </article>
      {/each}

      <!-- Action Load / Buffer Indicator -->
      <div class="p-space-sm text-center">
        <button class="w-full py-2 px-space-md rounded bg-surface-container-low border border-outline-variant/40 text-on-surface-variant hover:text-on-surface hover:bg-surface-container text-label-sm font-label-sm flex items-center justify-center gap-2 transition-colors active:scale-[0.99]">
          <span class="material-symbols-outlined text-[16px]" aria-hidden="true">history</span>
          Fetch Previous Buffer (500 events)
        </button>
      </div>
    </section>
  </main>

  <!-- BottomNavBar (Shared Component Implementation) -->
  <nav class="fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-space-xs py-space-xs bg-surface-container-lowest/95 backdrop-blur-md border-t border-outline-variant/30">
    <a class="flex flex-col items-center justify-center text-secondary py-space-xs active:scale-95 transition-transform duration-100 flex-1" href="#stream">
      <span class="material-symbols-outlined text-[22px] drop-shadow-[0_0_8px_#4edea3]" aria-hidden="true">sensors</span>
      <span class="text-label-sm font-label-sm mt-0.5 font-medium">Stream</span>
    </a>
    <a class="flex flex-col items-center justify-center text-outline py-space-xs hover:text-on-surface active:scale-95 transition-transform duration-100 flex-1" href="#metrics">
      <span class="material-symbols-outlined text-[22px]" aria-hidden="true">monitoring</span>
      <span class="text-label-sm font-label-sm mt-0.5">Metrics</span>
    </a>
    <a class="flex flex-col items-center justify-center text-outline py-space-xs hover:text-on-surface active:scale-95 transition-transform duration-100 flex-1" href="#triggers">
      <span class="material-symbols-outlined text-[22px]" aria-hidden="true">ads_click</span>
      <span class="text-label-sm font-label-sm mt-0.5">Triggers</span>
    </a>
    <a class="flex flex-col items-center justify-center text-outline py-space-xs hover:text-on-surface active:scale-95 transition-transform duration-100 flex-1" href="#validation">
      <span class="material-symbols-outlined text-[22px]" aria-hidden="true">verified</span>
      <span class="text-label-sm font-label-sm mt-0.5">Validation</span>
    </a>
  </nav>
</div>
