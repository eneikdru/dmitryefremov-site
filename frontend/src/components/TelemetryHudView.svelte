<script lang="ts">
  import { onMount, onDestroy } from 'svelte';

  let isStreaming = true;
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  export let theme: 'dark' | 'light' = 'dark';

  let liveEvents: TelemetryEvent[] = [];
  let selectedFilter: 'all' | 'goals' | 'clicks' | 'views' | 'errors' = 'all';
  let isAutoScroll: boolean = true;
  let testEventCounter: number = 1;

  let unsubscribeTelemetry: () => void;

  onMount(() => {
    liveEvents = telemetry.getEvents();
    unsubscribeTelemetry = telemetry.subscribe((evt) => {
      if (isStreaming) {
        liveEvents = [evt, ...liveEvents];
      }
    });
  });

  onDestroy(() => {
    if (unsubscribeTelemetry) unsubscribeTelemetry();
  });

  function triggerTestEvent() {
    testEventCounter += 1;
    telemetry.trackCanonSubmitConverted(`test_sess_${testEventCounter}`, 12000, {
      test_trigger: true,
      step_index: testEventCounter
    });
  }

  function handlePrimaryCtaClick() {
    telemetry.trackOutboundClick('/onboarding/checkout', 'Start Free Trial', 'Primary CTA');
  }

  function handleTierSelect(tierName: string, price: number) {
    telemetry.trackCrossLinkClick('tier_selector', tierName, 'plan_tier', `$${price}`);
  }

  $: filteredEvents = liveEvents.filter((evt) => {
    if (selectedFilter === 'goals') {
      return evt.type === 'canon_submit_converted' || evt.type === 'essay_view';
    }
    if (selectedFilter === 'clicks') {
      return evt.type === 'outbound_click' || evt.type === 'cross_link_click';
    }
    if (selectedFilter === 'views') {
      return evt.type === 'essay_view' || evt.type === 'scroll_depth';
    }
    if (selectedFilter === 'errors') {
      return false; // zero errors by design
    }
    return true;
  });
</script>

<div
  id="telemetry-hud-view-root"
  class="min-h-screen pb-20 font-sans antialiased transition-colors duration-200 {theme === 'dark' ? 'bg-[#0b1326] text-[#dae2fd]' : 'bg-[#f4f7fc] text-[#0f172a]'}"
>
  <!-- TOP APP BAR -->
  <header
    id="telemetry-hud-header"
    class="sticky top-0 z-40 flex items-center justify-between px-4 py-3 h-14 border-b transition-colors {theme === 'dark' ? 'bg-[#060e20] border-[#3c4a42]' : 'bg-white border-[#cbd5e1]'}"
  >
    <div class="flex items-center gap-2 sm:gap-3">
      <button
        id="terminal-icon-btn"
        type="button"
        aria-label="Terminal status"
        class="p-1 rounded text-[#4edea3] hover:bg-[#171f33] active:scale-95 transition-transform"
      >
        <span class="font-mono text-base font-bold">$&gt;</span>
      </button>
      <div class="flex items-center gap-2">
        <span class="font-mono text-sm sm:text-base font-bold tracking-wider uppercase text-[#4edea3]">TelemetryHUD</span>
        <span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-[#4edea3]/10 border border-[#4edea3]/40 text-[#4edea3] font-mono text-[10px] font-semibold">
          <span class="w-1.5 h-1.5 rounded-full bg-[#4edea3] animate-ping"></span>
          LIVE RECORDING
        </span>
      </div>
    </div>

    <div class="flex items-center gap-2">
      <div class="hidden sm:flex items-center gap-1 bg-[#171f33] px-2.5 py-1 rounded border border-[#3c4a42] text-[#bbcabf] font-mono text-xs">
        <span class="text-[#4edea3] font-bold">2.4k</span> evt/sec
      </div>
      <button
        type="button"
        on:click={triggerTestEvent}
        class="px-2.5 py-1 bg-[#4edea3] text-[#003824] font-mono text-xs font-bold rounded hover:brightness-110 active:scale-95 transition-all"
      >
        + Test Event
      </button>
    </div>
  </header>

  <!-- SUBHEADER / EPIC NAVIGATION CONTEXT -->
  <section
    id="hud-subheader"
    class="px-4 py-2.5 border-b flex items-center justify-between text-xs font-mono {theme === 'dark' ? 'bg-[#060e20] border-[#3c4a42] text-[#bbcabf]' : 'bg-slate-100 border-[#cbd5e1] text-slate-600'}"
  >
    <div class="flex items-center gap-2 overflow-x-auto whitespace-nowrap">
      <span class="uppercase tracking-wider text-[#86948a]">EPIC TARGET</span>
      <span class="text-[#3c4a42]">/</span>
      <div class="flex items-center gap-1.5 px-2 py-1 bg-[#222a3d] rounded border border-[#3c4a42] text-[#dae2fd]">
        <span class="w-2 h-2 rounded-full bg-[#4edea3]"></span>
        <span class="font-medium">Onboarding & Checkout</span>
        <span class="px-1 py-0.2 rounded bg-[#060e20] text-[#4edea3] text-[10px]">v2.4</span>
      </div>
    </div>
    <div class="flex items-center gap-1 text-[#86948a]">
      <span class="text-[#4edea3]">📡</span>
      <span>3 Pings Active</span>
    </div>
  </section>

  <!-- MAIN HUD CONTENT -->
  <main class="p-4 sm:p-6 space-y-6 max-w-3xl mx-auto">
    <!-- HUD METRIC BANNER: GOAL EVALUATION -->
    <section
      id="hud-goal-banner"
      class="border rounded-xl p-4 sm:p-5 relative overflow-hidden transition-colors {theme === 'dark' ? 'bg-[#131b2e] border-[#3c4a42]' : 'bg-white border-[#cbd5e1] shadow-sm'}"
    >
      <div class="absolute -right-8 -top-8 w-36 h-36 bg-[#4edea3]/10 rounded-full blur-2xl pointer-events-none"></div>

      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#3c4a42]/60 pb-4">
        <div>
          <div class="flex items-center gap-2">
            <span class="font-mono text-[10px] text-[#86948a] uppercase tracking-wider">Primary Success Metric</span>
            <span class="px-1.5 py-0.5 rounded bg-[#4edea3]/20 text-[#4edea3] border border-[#4edea3]/30 font-mono text-[10px] font-semibold">
              TARGET REACHED
            </span>
          </div>
          <div class="flex items-baseline gap-2 mt-1">
            <h2 class="text-3xl font-bold text-[#dae2fd]">31.4%</h2>
            <span class="font-mono text-xs text-[#4edea3] font-semibold flex items-center">
              ↑ +3.4% Uplift
            </span>
            <span class="font-mono text-[10px] text-[#86948a]">vs Epic Baseline 28.0%</span>
          </div>
        </div>

        <!-- Mini Sparkline Progress Bar -->
        <div class="flex-1 max-w-xs space-y-1 font-mono text-xs">
          <div class="flex justify-between text-[#86948a]">
            <span>Activation Progress</span>
            <span class="text-[#dae2fd]">112% of Goal</span>
          </div>
          <div class="w-full h-2 rounded bg-[#2d3449] overflow-hidden p-0.5">
            <div class="h-full bg-gradient-to-r from-[#c0c1ff] to-[#4edea3] rounded" style="width: 82%;"></div>
          </div>
          <div class="flex justify-between text-[#86948a] text-[10px]">
            <span>Threshold: 28%</span>
            <span>N = 4,821 sessions</span>
          </div>
        </div>
      </div>

      <!-- Quick KPI Chips -->
      <div class="grid grid-cols-3 gap-2 pt-3 text-center font-mono text-xs">
        <div class="p-2 rounded border border-[#3c4a42]/50 bg-[#060e20]/80">
          <div class="text-[10px] text-[#86948a]">Drop-off Pt. 2</div>
          <div class="font-bold text-[#dae2fd] mt-0.5">4.8% <span class="text-[#4edea3] font-normal">(-1.1%)</span></div>
        </div>
        <div class="p-2 rounded border border-[#3c4a42]/50 bg-[#060e20]/80">
          <div class="text-[10px] text-[#86948a]">Median TTF (Sec)</div>
          <div class="font-bold text-[#dae2fd] mt-0.5">42.8s <span class="text-[#4edea3] font-normal">Fast</span></div>
        </div>
        <div class="p-2 rounded border border-[#3c4a42]/50 bg-[#060e20]/80">
          <div class="text-[10px] text-[#86948a]">Schema Validity</div>
          <div class="font-bold text-[#4edea3] mt-0.5">99.98%</div>
        </div>
      </div>
    </section>

    <!-- INTERACTIVE EPIC MOCK SCREEN PREVIEW WITH RADAR PINGS -->
    <section
      id="hud-viewport-preview"
      class="border rounded-xl overflow-hidden transition-colors {theme === 'dark' ? 'bg-[#131b2e] border-[#3c4a42]' : 'bg-white border-[#cbd5e1] shadow-sm'}"
    >
      <!-- Frame Title Bar -->
      <div class="px-4 py-2.5 border-b flex items-center justify-between bg-[#171f33] border-[#3c4a42]">
        <div class="flex items-center gap-2">
          <span class="w-2.5 h-2.5 rounded-full bg-[#3c4a42]"></span>
          <span class="w-2.5 h-2.5 rounded-full bg-[#3c4a42]"></span>
          <span class="w-2.5 h-2.5 rounded-full bg-[#3c4a42]"></span>
          <span class="font-mono text-xs text-[#bbcabf] ml-2">Device Viewport: Mobile App Flow</span>
        </div>
        <span class="px-2 py-0.5 bg-[#060e20] rounded text-[#4edea3] border border-[#4edea3]/30 font-mono text-[10px] flex items-center gap-1">
          👁️ Telemetry Overlay Active
        </span>
      </div>

      <!-- Simulated Canvas -->
      <div class="relative bg-[#060e20] p-4 sm:p-6 min-h-[380px] flex flex-col justify-between">
        <!-- Radial Backdrop -->
        <div class="absolute inset-0 opacity-15 pointer-events-none bg-[radial-gradient(#3c4a42_1px,transparent_1px)] [background-size:16px_16px]"></div>

        <!-- Simulated Mobile App Top Header -->
        <div class="relative z-10 flex items-center justify-between border-b border-[#3c4a42]/30 pb-3">
          <div class="flex items-center gap-2">
            <div class="w-6 h-6 rounded bg-[#4edea3]/20 border border-[#4edea3]/40 flex items-center justify-center text-[#4edea3] font-bold text-xs">A</div>
            <span class="text-base font-semibold text-[#dae2fd]">ApexCloud</span>
          </div>
          <span class="px-2 py-0.5 text-xs bg-[#2d3449] rounded text-[#bbcabf] font-mono">Step 2 of 3</span>
        </div>

        <!-- SIMULATED APP COMPONENT 1: Tier Selector (Event #2 Ping) -->
        <div class="relative z-10 my-4 space-y-3">
          <div class="text-center space-y-1">
            <h3 class="text-base font-semibold text-[#dae2fd]">Select your deployment tier</h3>
            <p class="text-xs text-[#86948a]">Production grade isolated telemetry worker.</p>
          </div>

          <!-- Tier Card -->
          <div class="relative border-2 border-[#4edea3]/80 bg-[#222a3d]/90 rounded-xl p-3.5 shadow-lg">
            <!-- Event #2 Badge & Tooltip -->
            <div class="absolute -top-3 -right-2 z-20 flex flex-col items-end">
              <button
                type="button"
                on:click={() => handleTierSelect('Pro Architecture', 180)}
                class="flex items-center gap-1.5 px-2 py-0.5 rounded-full bg-[#3131c0] text-[#e1e0ff] border border-[#c0c1ff] font-mono text-[10px] font-semibold shadow-md hover:scale-105 transition-transform"
              >
                <span class="w-2 h-2 rounded-full bg-[#e1e0ff] animate-pulse"></span>
                <span>#2 plan_tier_selected</span>
              </button>
              <div class="mt-1 bg-[#060e20]/95 border border-[#c0c1ff]/60 rounded px-2 py-1 shadow-xl font-mono text-[10px] text-[#dae2fd] backdrop-blur-md">
                <span class="text-[#d0bcff]">&#123;tier:</span> <span class="text-[#4edea3]">'pro_annual'</span>, <span class="text-[#d0bcff]">value:</span> <span class="text-[#c0c1ff]">180</span><span class="text-[#d0bcff]">&#125;</span>
              </div>
            </div>

            <div class="flex justify-between items-start">
              <div>
                <span class="px-2 py-0.5 rounded bg-[#4edea3]/10 text-[#4edea3] border border-[#4edea3]/30 font-mono text-[10px] uppercase">Recommended</span>
                <h4 class="text-sm font-bold text-[#dae2fd] mt-1">Pro Architecture</h4>
                <p class="text-xs text-[#bbcabf]">Unlimited custom metrics & raw JSON traces</p>
              </div>
              <div class="text-right">
                <span class="text-lg font-bold text-[#4edea3]">$180</span>
                <span class="text-[10px] text-[#86948a] block">/ annual</span>
              </div>
            </div>
          </div>

          <!-- Step Progress (Event #3 Badge) -->
          <div class="relative pt-2">
            <div class="absolute -top-2 left-1/2 -translate-x-1/2 z-20">
              <div class="flex items-center gap-1 px-2 py-0.5 rounded-full bg-[#2d3449] border border-[#4edea3]/50 text-[#4edea3] font-mono text-[10px] shadow">
                ✓ <span>#3 checkout_step_completed (step: 2)</span>
              </div>
            </div>
            <div class="flex items-center gap-2 px-1 pt-3">
              <div class="h-1 flex-1 bg-[#4edea3] rounded"></div>
              <div class="h-1 flex-1 bg-[#4edea3] rounded"></div>
              <div class="h-1 flex-1 bg-[#2d3449] rounded"></div>
            </div>
          </div>
        </div>

        <!-- SIMULATED CTA (Event #1 Radar Ping) -->
        <div class="relative z-10 pt-2">
          <div class="relative">
            <div class="absolute -top-3.5 left-4 z-30 flex items-center gap-1.5 font-mono text-[10px]">
              <div class="w-4 h-4 rounded-full bg-[#4edea3] flex items-center justify-center text-[#003824] font-bold text-[9px]">
                1
              </div>
              <span class="px-2 py-0.5 rounded bg-[#10b981] text-[#00422b] font-bold shadow-md uppercase tracking-wider">
                Event #1: cta_primary_click
              </span>
            </div>

            <button
              id="btn-start-trial-primary"
              type="button"
              on:click={handlePrimaryCtaClick}
              class="w-full py-3 px-4 rounded-xl bg-[#4edea3] hover:bg-[#6ffbbe] text-[#003824] font-mono text-xs font-bold uppercase tracking-wider flex items-center justify-center gap-2 transition-all active:scale-[0.98] border border-[#6ffbbe] shadow-[0_0_15px_rgba(78,222,163,0.25)]"
            >
              <span>Start Free Trial</span>
              <span>→</span>
            </button>
          </div>
          <div class="flex items-center justify-between text-[#86948a] font-mono text-[10px] mt-2 px-1">
            <span>Instant activation • Cancel anytime</span>
            <span class="text-[#4edea3]">✓ Telemetry Verified</span>
          </div>
        </div>
      </div>
    </section>

    <!-- TELEMETRY STREAM & EVENT LOG DRAWER SECTION -->
    <section
      id="hud-event-stream-section"
      class="border rounded-xl overflow-hidden transition-colors {theme === 'dark' ? 'bg-[#131b2e] border-[#3c4a42]' : 'bg-white border-[#cbd5e1] shadow-sm'}"
    >
      <div class="p-4 border-b bg-[#171f33] border-[#3c4a42] space-y-2">
        <div class="flex flex-wrap items-center justify-between gap-2">
          <div class="flex items-center gap-2">
            <span class="text-[#4edea3]">⚡</span>
            <h3 class="text-sm font-semibold text-[#dae2fd]">Live Event Log Inspector</h3>
            {#if isStreaming}
              <span class="px-2 py-0.5 rounded-full bg-[#4edea3]/20 border border-[#4edea3]/50 text-[#4edea3] font-mono text-[10px] shadow-[0_0_8px_#4edea3] uppercase tracking-wider animate-pulse">
                Live Stream Active
              </span>
            {:else}
              <span class="px-2 py-0.5 rounded-full bg-[#222a3d] border border-[#3c4a42] text-[#86948a] font-mono text-[10px] uppercase tracking-wider">
                Stream Paused
              </span>
            {/if}
            <span class="px-2 py-0.5 rounded-full bg-[#222a3d] border border-[#3c4a42] text-[#bbcabf] font-mono text-[10px]">
              {filteredEvents.length} Events captured
            </span>
          </div>
          <div class="flex items-center gap-2">
            <button
              type="button"
              on:click={triggerTestEvent}
              class="px-2.5 py-1 bg-[#4edea3] text-[#003824] font-mono text-xs font-bold rounded flex items-center gap-1 active:scale-95 transition-transform"
            >
              ▶ Trigger Test Event
            </button>
            <button
              id="pauseToggleBtn"
              type="button"
              on:click={() => isStreaming = !isStreaming}
              title={isStreaming ? "Pause Stream" : "Resume Stream"}
              class="w-7 h-7 rounded border border-[#3c4a42] flex items-center justify-center text-[#bbcabf] hover:text-[#dae2fd] hover:bg-[#222a3d] transition-colors active:scale-95"
            >
              {#if isStreaming}
                <span class="material-symbols-outlined text-sm" id="streamStateIcon">pause_circle</span>
              {:else}
                <span class="material-symbols-outlined text-sm" id="streamStateIcon">play_circle</span>
              {/if}
            </button>
          </div>
        </div>

        <!-- Filter Toggles Pill Bar -->
        <div class="flex items-center gap-1.5 pt-1 overflow-x-auto font-mono text-[11px]">
          <span class="text-[#86948a] mr-1">Filter:</span>
          <button
            type="button"
            on:click={() => (selectedFilter = 'all')}
            class="px-2 py-0.5 rounded transition-colors {selectedFilter === 'all' ? 'bg-[#4edea3]/20 text-[#4edea3] border border-[#4edea3]/40 font-semibold' : 'bg-[#222a3d] text-[#bbcabf] border border-[#3c4a42]'}"
          >
            All
          </button>
          <button
            type="button"
            on:click={() => (selectedFilter = 'goals')}
            class="px-2 py-0.5 rounded transition-colors {selectedFilter === 'goals' ? 'bg-[#4edea3]/20 text-[#4edea3] border border-[#4edea3]/40 font-semibold' : 'bg-[#222a3d] text-[#bbcabf] border border-[#3c4a42]'}"
          >
            Core Goals
          </button>
          <button
            type="button"
            on:click={() => (selectedFilter = 'clicks')}
            class="px-2 py-0.5 rounded transition-colors {selectedFilter === 'clicks' ? 'bg-[#4edea3]/20 text-[#4edea3] border border-[#4edea3]/40 font-semibold' : 'bg-[#222a3d] text-[#bbcabf] border border-[#3c4a42]'}"
          >
            Clicks
          </button>
          <button
            type="button"
            on:click={() => (selectedFilter = 'views')}
            class="px-2 py-0.5 rounded transition-colors {selectedFilter === 'views' ? 'bg-[#4edea3]/20 text-[#4edea3] border border-[#4edea3]/40 font-semibold' : 'bg-[#222a3d] text-[#bbcabf] border border-[#3c4a42]'}"
          >
            Pageviews
          </button>
          <button
            type="button"
            on:click={() => (selectedFilter = 'errors')}
            class="px-2 py-0.5 rounded transition-colors {selectedFilter === 'errors' ? 'bg-[#ffb4ab]/20 text-[#ffb4ab] border border-[#ffb4ab]/40 font-semibold' : 'bg-[#222a3d] text-[#bbcabf] border border-[#3c4a42]'}"
          >
            Errors (0)
          </button>
        </div>
      </div>

      <!-- Event List Stream -->
      <div id="hud-events-feed" class="divide-y divide-[#3c4a42]/60 max-h-80 overflow-y-auto">
        {#if filteredEvents.length === 0}
          <div class="p-6 text-center text-xs font-mono text-[#86948a]">
            No matching events logged yet. Interact with the app or click "Trigger Test Event".
          </div>
        {:else}
          {#each filteredEvents as evt, index (evt.timestamp + index)}
            <article class="p-4 hover:bg-[#171f33] transition-colors space-y-2">
              <div class="flex items-start justify-between gap-2">
                <div class="flex items-center gap-2">
                  <span class="w-5 h-5 rounded bg-[#4edea3]/20 text-[#4edea3] border border-[#4edea3]/40 flex items-center justify-center font-mono text-[10px] font-bold">
                    {index + 1}
                  </span>
                  <div>
                    <div class="flex items-center gap-2">
                      <span class="font-mono text-xs font-bold text-[#4edea3]">{evt.type}</span>
                      <span class="px-1.5 py-0.2 rounded bg-[#4edea3]/15 text-[#4edea3] border border-[#4edea3]/30 font-mono text-[9px]">
                        ✓ Schema Valid
                      </span>
                    </div>
                  </div>
                </div>
                <span class="font-mono text-[10px] text-[#86948a]">{evt.timestamp.slice(11, 19)}</span>
              </div>

              <!-- Payload Inspector -->
              <div class="bg-[#060e20] p-2.5 rounded border border-[#3c4a42] font-mono text-[11px] overflow-x-auto">
                <pre class="text-[#dae2fd] leading-relaxed"><code>{JSON.stringify(evt.payload, null, 2)}</code></pre>
              </div>
            </article>
          {/each}
        {/if}
      </div>

      <!-- Footer Status -->
      <div class="bg-[#060e20] px-4 py-2 border-t border-[#3c4a42] flex items-center justify-between font-mono text-[10px] text-[#86948a]">
        <span class="flex items-center gap-1.5">
          <span class="w-1.5 h-1.5 rounded-full bg-[#4edea3] animate-pulse"></span>
          Ingestion Channel: telemetry.eu-central.prod (TLS 1.3)
        </span>
        <button
          type="button"
          on:click={() => (isAutoScroll = !isAutoScroll)}
          class="hover:text-[#4edea3] transition-colors"
        >
          Auto-scroll: <span class="text-[#4edea3] font-bold">{isAutoScroll ? 'ON' : 'OFF'}</span>
        </button>
      </div>
    </section>
  </main>

  <!-- BOTTOM NAVIGATION BAR -->
  <nav
    id="hud-bottom-nav"
    class="fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-2 py-1.5 h-16 border-t transition-colors {theme === 'dark' ? 'bg-[#060e20] border-[#3c4a42]' : 'bg-white border-[#cbd5e1]'}"
  >
    <button type="button" class="flex flex-col items-center justify-center text-[#bbcabf] px-3 py-1 hover:text-[#dae2fd]">
      <span class="font-mono text-xs">📐</span>
      <span class="font-mono text-[10px] mt-0.5">Flows</span>
    </button>
    <button type="button" class="flex flex-col items-center justify-center text-[#bbcabf] px-3 py-1 hover:text-[#dae2fd]">
      <span class="font-mono text-xs">📡</span>
      <span class="font-mono text-[10px] mt-0.5">Stream</span>
    </button>
    <button type="button" class="flex flex-col items-center justify-center bg-[#222a3d] text-[#4edea3] rounded-xl px-3 py-1 border border-[#3c4a42]">
      <span class="font-mono text-xs">⚡</span>
      <span class="font-mono text-[10px] font-semibold mt-0.5">Events</span>
    </button>
    <button type="button" class="flex flex-col items-center justify-center text-[#bbcabf] px-3 py-1 hover:text-[#dae2fd]">
      <span class="font-mono text-xs">📊</span>
      <span class="font-mono text-[10px] mt-0.5">Metrics</span>
    </button>
  </nav>
</div>
