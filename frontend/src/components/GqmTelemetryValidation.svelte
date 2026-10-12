<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { telemetry, type TelemetryEvent } from '../lib/telemetry';

  let events: TelemetryEvent[] = [];
  let unsubscribe: () => void;

  onMount(() => {
    events = telemetry.getEvents();
    unsubscribe = telemetry.subscribe((event) => {
      events = [event, ...events];
    });
  });

  onDestroy(() => {
    if (unsubscribe) unsubscribe();
  });
</script>

<div id="gqm-validation-root">
<main class="max-w-lg mx-auto px-4 pt-3.5 space-y-4">
<!-- ================= GQM FRAMEWORK MASTER CARD ================= -->
<section class="bg-surface-container-lowest rounded-xl border border-outline-variant p-3.5 shadow-sm space-y-3">
<!-- Goal Tier -->
<div class="flex items-start justify-between border-b border-outline-variant pb-2.5">
<div>
<span class="font-caption text-caption text-outline font-semibold uppercase tracking-wider block">Goal 01 : Strategic Telemetry Scope</span>
<h2 class="font-headline-sm text-headline-sm text-on-surface mt-0.5">Canon Section Depth &amp; Form Viability</h2>
</div>
<span class="font-badge-label text-badge-label bg-surface-container text-primary font-medium px-2 py-0.5 rounded border border-outline-variant">GQM MODEL</span>
</div>
<!-- Question Tier -->
<div class="space-y-2 bg-surface-container-low rounded-lg p-2.5 border border-outline-variant">
<div class="flex items-start space-x-2">
<span class="font-badge-label text-badge-label bg-primary text-on-primary rounded px-1.5 py-0.5 font-bold shrink-0 mt-0.5">Q1</span>
<p class="font-body-md text-body-md text-on-surface leading-tight font-medium">
            Does prolonged Canon dwell time (&gt;45s) correlate with higher downstream form completion?
          </p>
</div>
<div class="flex items-start space-x-2 pt-1 border-t border-outline-variant/50">
<span class="font-badge-label text-badge-label bg-primary text-on-primary rounded px-1.5 py-0.5 font-bold shrink-0 mt-0.5">Q2</span>
<p class="font-body-md text-body-md text-on-surface leading-tight font-medium">
            Where do qualified users stall between initial Canon exposure and form completion?
          </p>
</div>
</div>
<!-- Metric Tier Alignment Pill -->
<div class="flex items-center justify-between text-on-surface-variant pt-0.5">
<div class="flex items-center space-x-1.5">
<span class="w-2 h-2 rounded-full bg-secondary"></span>
<span class="font-caption text-caption font-medium">All 3 GQM Metric Streams Active &amp; Calibrated</span>
</div>
<span class="font-code-metric text-code-metric text-primary font-semibold">T-SYNC: 1.2s</span>
</div>
</section>
<!-- ================= METRIC SUMMARY KPI GRID (3 Cards) ================= -->
<section class="space-y-2.5">
<div class="flex items-center justify-between">
<h3 class="font-headline-sm text-headline-sm text-on-surface">Scalar KPI Telemetry</h3>
<span class="font-badge-label text-badge-label text-on-surface-variant font-medium">5,000 SESSIONS SAMPLED</span>
</div>
<div class="grid grid-cols-2 gap-2.5">
<!-- Card 1: Dwell Time (Spans 1 Col) -->
<div class="bg-surface-container-lowest p-3 rounded-xl border border-outline-variant flex flex-col justify-between">
<div class="flex items-center justify-between mb-1">
<span class="font-caption text-caption text-on-surface-variant font-medium truncate">Avg. Canon Dwell</span>
<span class="font-badge-label text-badge-label text-secondary bg-secondary-container/40 px-1.5 py-0.2 rounded border border-secondary/30">+18%</span>
</div>
<div>
<div class="font-display-kpi text-display-kpi text-on-surface tracking-tight">3m 42s</div>
<div class="mt-1 flex items-center space-x-1 text-on-surface-variant">
<span class="material-symbols-outlined text-[14px] text-tertiary" data-icon="flag">flag</span>
<span class="font-caption text-caption">Target: &gt;2m 30s</span>
</div>
</div>
<div class="mt-2.5 pt-2 border-t border-outline-variant/60 bg-surface-container-low -mx-3 -mb-3 p-2 rounded-b-xl flex items-center justify-between">
<span class="font-badge-label text-[10px] text-outline font-medium">MEDIAN ACTIVE</span>
<span class="font-code-metric text-[11px] text-on-surface font-semibold">222,400ms</span>
</div>
</div>
<!-- Card 2: Conversion Rate -->
<div class="bg-surface-container-lowest p-3 rounded-xl border border-outline-variant flex flex-col justify-between">
<div class="flex items-center justify-between mb-1">
<span class="font-caption text-caption text-on-surface-variant font-medium truncate">Form Conversion Rate</span>
<span class="font-badge-label text-badge-label text-secondary bg-secondary-container/40 px-1.5 py-0.2 rounded border border-secondary/30">+4.2% lift</span>
</div>
<div>
<div class="font-display-kpi text-display-kpi text-on-surface tracking-tight">28.4%</div>
<div class="mt-1 flex items-center space-x-1 text-on-surface-variant">
<span class="material-symbols-outlined text-[14px] text-primary" data-icon="ads_click">ads_click</span>
<span class="font-caption text-caption">1,420 of 5,000 views</span>
</div>
</div>
<div class="mt-2.5 pt-2 border-t border-outline-variant/60 bg-surface-container-low -mx-3 -mb-3 p-2 rounded-b-xl flex items-center justify-between">
<span class="font-badge-label text-[10px] text-outline font-medium">STEP 1 TRIGGER</span>
<span class="font-code-metric text-[11px] text-on-surface font-semibold">48.0% (2,400)</span>
</div>
</div>
<!-- Card 3: Stream Quality & Integrity (Full width across 2 cols) -->
<div class="col-span-2 bg-surface-container-lowest p-3 rounded-xl border border-outline-variant flex items-center justify-between">
<div class="flex items-center space-x-3">
<div class="w-10 h-10 rounded-lg bg-surface-container flex items-center justify-center text-primary">
<span class="material-symbols-outlined text-[22px]" data-icon="vital_signs">vital_signs</span>
</div>
<div>
<div class="flex items-center space-x-1.5">
<span class="font-headline-sm text-headline-sm text-on-surface font-bold">99.94%</span>
<span class="font-badge-label text-badge-label bg-secondary-container/30 text-secondary border border-secondary/20 px-1.5 py-0.2 rounded font-semibold">ZERO LOSS</span>
</div>
<p class="font-caption text-caption text-on-surface-variant">Pipeline Delivery Fidelity • 0 schema violations</p>
</div>
</div>
<div class="text-right">
<span class="font-code-metric text-code-metric text-outline font-medium block">RETRY: 0.02%</span>
<span class="font-caption text-caption text-secondary font-semibold">P99 &lt;45ms</span>
</div>
</div>
</div>
</section>
<!-- ================= DWELL TIME ANALYTICS & DISTRIBUTION ================= -->
<section class="bg-surface-container-lowest rounded-xl border border-outline-variant p-3.5 shadow-sm space-y-3">
<div class="flex items-center justify-between">
<div>
<span class="font-caption text-caption text-outline font-semibold uppercase tracking-wider block">GQM Question 01 Analysis</span>
<h3 class="font-headline-sm text-headline-sm text-on-surface">Canon Dwell Tier Breakdown</h3>
</div>
<span class="font-badge-label text-badge-label bg-primary/10 text-primary px-2 py-0.5 rounded border border-primary/20">HYPOTHESIS CONFIRMED</span>
</div>
<!-- Segmented Bar Visualization -->
<div class="space-y-1.5 pt-1">
<div class="h-4 w-full rounded-md flex overflow-hidden bg-surface-container-high p-0.5 gap-0.5">
<div class="h-full bg-outline-variant rounded-sm" style="width: 18%;" title="0-15s: 18%"></div>
<div class="h-full bg-primary-fixed-dim rounded-sm" style="width: 32%;" title="15-45s: 32%"></div>
<div class="h-full bg-primary-container rounded-sm" style="width: 34%;" title="45s-2m: 34%"></div>
<div class="h-full bg-secondary rounded-sm" style="width: 16%;" title="&gt;2m: 16%"></div>
</div>
<div class="grid grid-cols-4 gap-1 pt-1 text-center font-caption text-caption">
<div class="bg-surface-container-low p-1.5 rounded border border-outline-variant">
<span class="font-code-metric text-code-metric text-on-surface font-semibold block">18%</span>
<span class="text-outline text-[10px] block">0-15s (Skim)</span>
</div>
<div class="bg-surface-container-low p-1.5 rounded border border-outline-variant">
<span class="font-code-metric text-code-metric text-on-surface font-semibold block">32%</span>
<span class="text-outline text-[10px] block">15-45s (Read)</span>
</div>
<div class="bg-surface-container-low p-1.5 rounded border border-outline-variant ring-1 ring-primary/30">
<span class="font-code-metric text-code-metric text-primary font-bold block">34%</span>
<span class="text-on-surface-variant text-[10px] block">45s-2m (High)</span>
</div>
<div class="bg-surface-container-low p-1.5 rounded border border-outline-variant">
<span class="font-code-metric text-code-metric text-secondary font-bold block">16%</span>
<span class="text-outline text-[10px] block">&gt;2m (Deep)</span>
</div>
</div>
</div>
<!-- Key Insight Callout Banner -->
<div class="bg-secondary-container/20 border border-secondary/30 rounded-lg p-2.5 flex items-start space-x-2">
<span class="material-symbols-outlined text-secondary text-[20px] shrink-0 mt-0.5" data-icon="insights" style="font-variation-settings: 'FILL' 1;">insights</span>
<div>
<p class="font-body-md text-body-md font-semibold text-on-surface">
            Users with dwell time &gt;45s convert at <span class="text-secondary font-bold">41.2%</span> (vs <span class="text-error font-medium">11.5%</span> for &lt;15s).
          </p>
<p class="font-caption text-caption text-on-surface-variant mt-0.5">
            Statistical correlation coefficient r = 0.78 across validated cohort sessions.
          </p>
</div>
</div>
</section>
<!-- ================= MULTI-STEP FORM CONVERSION FUNNEL ================= -->
<section class="bg-surface-container-lowest rounded-xl border border-outline-variant p-3.5 shadow-sm space-y-3">
<div class="flex items-center justify-between">
<div>
<span class="font-caption text-caption text-outline font-semibold uppercase tracking-wider block">GQM Question 02 Diagnostic</span>
<h3 class="font-headline-sm text-headline-sm text-on-surface">Canon-to-Form Funnel Steps</h3>
</div>
<button class="font-badge-label text-badge-label text-primary flex items-center space-x-0.5 bg-surface-container px-2 py-0.5 rounded">
<span>SORT BY DROP</span>
<span class="material-symbols-outlined text-[14px]" data-icon="unfold_more">unfold_more</span>
</button>
</div>
<!-- Funnel Step Progression Items -->
<div class="space-y-2.5 pt-1">
<!-- Step 1 -->
<div class="space-y-1">
<div class="flex justify-between items-center text-body-sm font-body-sm">
<span class="text-on-surface font-semibold flex items-center gap-1.5">
<span class="w-4 h-4 rounded-full bg-surface-container text-primary font-badge-label text-[10px] flex items-center justify-center font-bold">1</span>
              Canon Exposure
            </span>
<div class="space-x-1.5">
<span class="font-code-metric text-code-metric text-on-surface font-medium">5,000 views</span>
<span class="font-badge-label text-badge-label text-on-surface-variant font-semibold">100%</span>
</div>
</div>
<div class="w-full bg-surface-container-high h-2 rounded-full overflow-hidden">
<div class="bg-primary h-full rounded-full" style="width: 100%;"></div>
</div>
</div>
<!-- Step 2 -->
<div class="space-y-1">
<div class="flex justify-between items-center text-body-sm font-body-sm">
<span class="text-on-surface font-semibold flex items-center gap-1.5">
<span class="w-4 h-4 rounded-full bg-surface-container text-primary font-badge-label text-[10px] flex items-center justify-center font-bold">2</span>
              Dwell Threshold ≥30s
            </span>
<div class="space-x-1.5">
<span class="font-code-metric text-code-metric text-on-surface font-medium">3,700 users</span>
<span class="font-badge-label text-badge-label text-on-surface font-semibold">74.0%</span>
<span class="text-outline text-[11px]">(-26%)</span>
</div>
</div>
<div class="w-full bg-surface-container-high h-2 rounded-full overflow-hidden">
<div class="bg-primary-container h-full rounded-full" style="width: 74%;"></div>
</div>
</div>
<!-- Step 3 (Friction Point) -->
<div class="space-y-1 bg-surface-container-low p-2 rounded-lg border border-outline-variant">
<div class="flex justify-between items-center text-body-sm font-body-sm">
<span class="text-on-surface font-semibold flex items-center gap-1.5">
<span class="w-4 h-4 rounded-full bg-tertiary-container/20 text-tertiary font-badge-label text-[10px] flex items-center justify-center font-bold">3</span>
              Form Interaction Triggered
            </span>
<div class="space-x-1.5">
<span class="font-code-metric text-code-metric text-on-surface font-medium">2,400 users</span>
<span class="font-badge-label text-badge-label text-on-surface font-semibold">48.0%</span>
<span class="text-error font-medium text-[11px]">(-26% drop)</span>
</div>
</div>
<div class="w-full bg-surface-container-high h-2 rounded-full overflow-hidden">
<div class="bg-tertiary h-full rounded-full" style="width: 48%;"></div>
</div>
<div class="text-[11px] text-tertiary font-caption flex items-center gap-1 pt-0.5">
<span class="material-symbols-outlined text-[13px]" data-icon="warning">warning</span>
<span>Primary friction: 35.1% abandon Canon CTA before opening step 1</span>
</div>
</div>
<!-- Step 4 -->
<div class="space-y-1">
<div class="flex justify-between items-center text-body-sm font-body-sm">
<span class="text-on-surface font-semibold flex items-center gap-1.5">
<span class="w-4 h-4 rounded-full bg-surface-container text-primary font-badge-label text-[10px] flex items-center justify-center font-bold">4</span>
              Step 1 Details Completed
            </span>
<div class="space-x-1.5">
<span class="font-code-metric text-code-metric text-on-surface font-medium">1,900 users</span>
<span class="font-badge-label text-badge-label text-on-surface font-semibold">38.0%</span>
<span class="text-outline text-[11px]">(-10%)</span>
</div>
</div>
<div class="w-full bg-surface-container-high h-2 rounded-full overflow-hidden">
<div class="bg-primary-container h-full rounded-full" style="width: 38%;"></div>
</div>
</div>
<!-- Step 5 -->
<div class="space-y-1">
<div class="flex justify-between items-center text-body-sm font-body-sm">
<span class="text-on-surface font-semibold flex items-center gap-1.5">
<span class="w-4 h-4 rounded-full bg-secondary-container/40 text-secondary font-badge-label text-[10px] flex items-center justify-center font-bold">5</span>
              Final Form Submission
            </span>
<div class="space-x-1.5">
<span class="font-code-metric text-code-metric text-secondary font-bold">1,420 submits</span>
<span class="font-badge-label text-badge-label text-secondary font-bold">28.4%</span>
</div>
</div>
<div class="w-full bg-surface-container-high h-2 rounded-full overflow-hidden">
<div class="bg-secondary h-full rounded-full" style="width: 28.4%;"></div>
</div>
</div>
</div>
</section>
<!-- ================= REAL-TIME EVENT INSPECTOR ================= -->
<section class="bg-inverse-surface rounded-xl border border-outline-variant p-3.5 shadow-sm text-inverse-on-surface space-y-2.5">
<div class="flex items-center justify-between">
<div class="flex items-center space-x-2">
<span class="material-symbols-outlined text-secondary-fixed text-[18px]" data-icon="terminal">terminal</span>
<h3 class="font-headline-sm text-headline-sm text-inverse-on-surface">Telemetry Wire Inspector</h3>
</div>
<div class="flex items-center space-x-2">
<span class="font-badge-label text-[10px] text-secondary-fixed bg-secondary-container/20 px-1.5 py-0.5 rounded border border-secondary-fixed/30 font-semibold">STABLE</span>
<button class="text-outline-variant hover:text-inverse-on-surface transition-colors" id="copy-log-btn">
<span class="material-symbols-outlined text-[16px]" data-icon="content_copy">content_copy</span>
</button>
</div>
</div>
<!-- JSON Log Window -->
<div class="bg-[#171D2D] rounded-lg p-2.5 font-code-metric text-[12px] space-y-2 border border-outline-variant/30 overflow-x-auto custom-scroll">
  {#each events as event}
    <div class="space-y-0.5 border-b border-outline-variant/20 pb-1.5 mb-1.5">
      <div class="flex items-center justify-between text-outline-variant text-[10px]">
        <span class="text-secondary-fixed font-bold">EVENT • {event.type}</span>
        <span>{new Date(event.timestamp).toLocaleTimeString()}</span>
      </div>
      <pre class="text-[#A5B4FC] leading-tight font-mono">{JSON.stringify(event.payload, null, 2)}</pre>
    </div>
  {/each}
  {#if events.length === 0}
    <div class="text-outline-variant text-center">Waiting for telemetry events...</div>
  {/if}
</div>
<div class="flex items-center justify-between text-[11px] font-caption text-outline-variant pt-0.5">
<span>Active listener: <code class="font-code-metric text-inverse-on-surface">ws://telemetry.canon.internal/stream</code></span>
<span class="text-secondary-fixed">Payload: 284 B</span>
</div>
</section>
<!-- Bottom Spacing for Fixed Nav -->
<div class="h-6"></div>
</main>

</div>
