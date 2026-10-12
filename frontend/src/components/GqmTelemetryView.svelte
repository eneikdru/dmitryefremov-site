<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { telemetry, calculateScrollPercentage, type TelemetryEvent } from '../lib/telemetry';

  export let epicId: string = 'EPIC-4209: Canon Intake & Conversion';
  export let epicVersion: string = 'v2.4.1-rc';

  // State
  let activeTab: 'gqm' | 'canon_reader' | 'intake_form' = 'gqm';
  let dwellMs: number = 0;
  let heartbeatTimer: ReturnType<typeof setInterval> | null = null;
  let scrollContainer: HTMLElement | null = null;
  let scrollPercentage: number = 0;
  let loggedEvents: TelemetryEvent[] = [];

  // Form State for Intake Conversion
  let clientName: string = '';
  let clientEmail: string = '';
  let projectScope: string = '';
  let formStep: number = 1;
  let formError: string = '';
  let isConverted: boolean = false;
  let focusedField: string | null = null;

  function handleScroll() {
    if (!scrollContainer) return;
    const { scrollTop, scrollHeight, clientHeight } = scrollContainer;
    scrollPercentage = calculateScrollPercentage(scrollTop, scrollHeight, clientHeight);
  }

  function startDwellTimer() {
    if (heartbeatTimer) clearInterval(heartbeatTimer);
    heartbeatTimer = setInterval(() => {
      dwellMs += 5000;
      telemetry.trackCanonDwellHeartbeat(dwellMs, activeTab, scrollPercentage, true);
    }, 5000);
  }

  function handleFieldFocus(fieldId: string) {
    focusedField = fieldId;
    telemetry.trackFormFieldFocused(fieldId, 0, 0);
  }

  function handleStepValidation(stepNum: number): boolean {
    if (stepNum === 1) {
      if (!clientName.trim()) {
        formError = 'Client Name is required.';
        return false;
      }
    } else if (stepNum === 2) {
      if (!clientEmail.includes('@')) {
        formError = 'A valid email address is required.';
        return false;
      }
    }
    formError = '';
    telemetry.trackFormValidationPassed(`step_${stepNum}_verification`, 15, 1);
    return true;
  }

  function nextStep() {
    if (handleStepValidation(formStep)) {
      formStep += 1;
    }
  }

  function submitIntakeForm() {
    if (!handleStepValidation(2)) return;
    if (!projectScope.trim()) {
      formError = 'Project Scope description is required.';
      return;
    }

    isConverted = true;
    formError = '';
    const sessionId = `canon_sess_${Math.random().toString(36).substring(2, 8)}`;
    telemetry.trackCanonSubmitConverted(sessionId, dwellMs, {
      client_name: clientName,
      client_email: clientEmail,
      scope_length: projectScope.length
    });
  }

  let unsubscribeTelemetry: () => void;

  onMount(() => {
    loggedEvents = telemetry.getEvents();
    unsubscribeTelemetry = telemetry.subscribe((event) => {
      loggedEvents = [event, ...loggedEvents];
    });
    startDwellTimer();
  });

  onDestroy(() => {
    if (heartbeatTimer) clearInterval(heartbeatTimer);
    if (unsubscribeTelemetry) unsubscribeTelemetry();
  });
</script>

<div id="gqm-telemetry-root" class="bg-[#0f131d] text-[#dfe2f1] font-sans min-h-screen p-4 sm:p-6 space-y-6 max-w-7xl mx-auto">
  <!-- Top Bar Header -->
  <header class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 pb-4 border-b border-[#3b494b]" aria-label="Canon Telemetry Audit Banner">
    <div class="flex items-center gap-3">
      <span class="px-2 py-1 bg-[#1c1f2a] border border-[#00f0ff]/40 text-[#00f0ff] font-mono text-xs font-semibold rounded">
        [SYS_CORE]
      </span>
      <h1 class="text-xl sm:text-2xl font-bold tracking-tight text-[#dbfcff]">
        Canon Telemetry Audit
      </h1>
    </div>
    <div class="flex items-center gap-3 text-xs font-mono">
      <span class="px-2.5 py-1 rounded bg-[#171b26] text-[#4edea3] border border-[#4edea3]/30 flex items-center gap-1.5">
        <span class="w-2 h-2 rounded-full bg-[#4edea3] animate-pulse"></span>
        INGEST 1.4K EPS
      </span>
      <span class="text-[#849495]">{epicId} ({epicVersion})</span>
    </div>
  </header>

  <!-- Navigation Tabs -->
  <nav aria-label="GQM Navigation Tabs" class="flex flex-wrap gap-2 border-b border-[#3b494b] pb-2">
    <button
      id="tab-gqm-overview"
      type="button"
      class="px-4 py-2 text-xs font-mono rounded transition-colors focus:outline-none focus:ring-2 focus:ring-[#00f0ff] {activeTab === 'gqm' ? 'bg-[#00f0ff] text-[#00363a] font-bold' : 'bg-[#1c1f2a] text-[#dfe2f1] hover:bg-[#313540]'}"
      on:click={() => (activeTab = 'gqm')}
      aria-pressed={activeTab === 'gqm'}
    >
      [GQM_OVERVIEW]
    </button>
    <button
      id="tab-canon-reader"
      type="button"
      class="px-4 py-2 text-xs font-mono rounded transition-colors focus:outline-none focus:ring-2 focus:ring-[#00f0ff] {activeTab === 'canon_reader' ? 'bg-[#00f0ff] text-[#00363a] font-bold' : 'bg-[#1c1f2a] text-[#dfe2f1] hover:bg-[#313540]'}"
      on:click={() => (activeTab = 'canon_reader')}
      aria-pressed={activeTab === 'canon_reader'}
    >
      [CANON_READER]
    </button>
    <button
      id="tab-intake-form"
      type="button"
      class="px-4 py-2 text-xs font-mono rounded transition-colors focus:outline-none focus:ring-2 focus:ring-[#00f0ff] {activeTab === 'intake_form' ? 'bg-[#00f0ff] text-[#00363a] font-bold' : 'bg-[#1c1f2a] text-[#dfe2f1] hover:bg-[#313540]'}"
      on:click={() => (activeTab = 'intake_form')}
      aria-pressed={activeTab === 'intake_form'}
    >
      [INTAKE_CONVERSION_FORM]
    </button>
  </nav>

  <!-- Tab Content Area -->
  {#if activeTab === 'gqm'}
    <!-- GQM Strategic Alignment Section -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
      <!-- Left Column: GQM Framework Tier 1 & Questions -->
      <section class="lg:col-span-7 bg-[#171b26] border border-[#3b494b] rounded-lg p-5 space-y-6">
        <div class="border-b border-[#3b494b] pb-4 space-y-2">
          <div class="flex justify-between items-center">
            <span class="text-xs font-mono text-[#00dbe9] uppercase tracking-wider">GQM Framework Tier 1: Strategic Goal</span>
            <span class="text-xs font-mono text-[#4edea3] bg-[#313540] px-2 py-0.5 rounded border border-[#4edea3]/30">ALIGNMENT 94%</span>
          </div>
          <h2 class="text-lg font-bold text-white">Optimize Canon intake flow engagement and completion rate</h2>
          <p class="text-xs text-[#b9cacb] leading-relaxed">
            Telemetry audit targeting cognitive friction points, field interaction bottlenecks, and user verification drop-offs across native Canon intake modules.
          </p>
        </div>

        <!-- Question 1: Dwell Time -->
        <article class="bg-[#1c1f2a] border border-[#3b494b] rounded p-4 space-y-3">
          <div class="flex justify-between items-start gap-2">
            <div>
              <span class="text-xs font-mono text-[#c0c1ff] uppercase">Question 01</span>
              <h3 class="text-sm font-semibold text-white">What is the median active dwell time across Canon intake steps?</h3>
            </div>
            <span class="text-xs font-mono text-[#00dbe9] bg-[#313540] px-2 py-1 rounded border border-[#3b494b] shrink-0">METRIC M-101</span>
          </div>
          <div class="flex justify-between items-baseline pt-2">
            <div>
              <span class="text-2xl font-bold text-[#dbfcff] font-mono">{Math.floor(dwellMs / 1000)}s</span>
              <span class="text-xs text-[#4edea3] font-mono ml-2">Active Dwell</span>
            </div>
            <span class="text-xs font-mono text-[#b9cacb]">TARGET: &lt; 3m 30s</span>
          </div>
        </article>

        <!-- Question 2: Dropoff & Conversion -->
        <article class="bg-[#1c1f2a] border border-[#3b494b] rounded p-4 space-y-3">
          <div class="flex justify-between items-start gap-2">
            <div>
              <span class="text-xs font-mono text-[#c0c1ff] uppercase">Question 02</span>
              <h3 class="text-sm font-semibold text-white">Where do users drop off prior to conversion submission?</h3>
            </div>
            <span class="text-xs font-mono text-[#4edea3] bg-[#313540] px-2 py-1 rounded border border-[#3b494b] shrink-0">METRIC M-102</span>
          </div>
          <div class="grid grid-cols-3 gap-2 text-center pt-2">
            <div class="p-2 bg-[#171b26] border border-[#3b494b] rounded">
              <span class="text-[10px] font-mono text-[#b9cacb] block">STEP 1: INTAKE</span>
              <span class="text-sm font-bold text-[#dbfcff]">94.2%</span>
            </div>
            <div class="p-2 bg-[#171b26] border border-[#3b494b] rounded">
              <span class="text-[10px] font-mono text-[#b9cacb] block">STEP 2: VERIFY</span>
              <span class="text-sm font-bold text-[#c0c1ff]">78.5%</span>
            </div>
            <div class="p-2 bg-[#171b26] border border-[#3b494b] rounded">
              <span class="text-[10px] font-mono text-[#b9cacb] block">SUBMIT CONVERT</span>
              <span class="text-sm font-bold text-[#4edea3]">{isConverted ? '100%' : '63.8%'}</span>
            </div>
          </div>
        </article>
      </section>

      <!-- Right Column: Dwell Analytics & Benchmark -->
      <section class="lg:col-span-5 space-y-6">
        <div class="bg-[#171b26] border border-[#3b494b] rounded-lg p-5 space-y-4">
          <div class="flex justify-between items-center">
            <h3 class="text-xs font-mono font-semibold text-[#00dbe9] uppercase">Dwell Time Analytics</h3>
            <span class="text-xs font-mono text-[#4edea3] bg-[#1c1f2a] px-2 py-0.5 rounded border border-[#3b494b]">P90 = 3m 12s</span>
          </div>
          <div class="space-y-2">
            <div class="flex justify-between text-xs font-mono text-[#b9cacb]">
              <span>ACTIVE CANON INTERACTION</span>
              <span>100% TOTAL</span>
            </div>
            <div class="h-3 w-full bg-[#313540] rounded overflow-hidden flex">
              <div class="bg-[#00f0ff] h-full" style="width: 81.2%;"></div>
              <div class="bg-[#3b494b] h-full" style="width: 18.8%;"></div>
            </div>
          </div>
          <div class="bg-[#1c1f2a] p-3 rounded border border-[#3b494b] space-y-2 font-mono text-xs">
            <div class="flex justify-between text-[#dfe2f1]">
              <span>Heartbeat Interval:</span>
              <span class="text-[#00f0ff]">5000ms</span>
            </div>
            <div class="flex justify-between text-[#dfe2f1]">
              <span>Total Session Dwell:</span>
              <span class="text-[#4edea3]">{dwellMs} ms</span>
            </div>
          </div>
        </div>
      </section>
    </div>

  {:else if activeTab === 'canon_reader'}
    <!-- Canon Reader Module with Dwell Heartbeats -->
    <section class="bg-[#171b26] border border-[#3b494b] rounded-lg p-6 space-y-6 max-w-3xl mx-auto">
      <header class="border-b border-[#3b494b] pb-4 space-y-2">
        <span class="text-xs font-mono text-[#00f0ff] bg-[#1c1f2a] px-2 py-0.5 rounded border border-[#3b494b]">[CANON_MANIFEST]</span>
        <h2 class="text-2xl font-bold text-white">Extended Mind & Extended Epistemology</h2>
        <p class="text-xs text-[#b9cacb]">Reading active. Telemetry dwell time heartbeat fires every 5 seconds.</p>
      </header>

      <!-- Scroll Progress Bar -->
      <div class="space-y-1">
        <div class="flex justify-between text-xs font-mono text-[#b9cacb]">
          <span>READING_PROGRESS</span>
          <span>{scrollPercentage}%</span>
        </div>
        <div class="w-full bg-[#1c1f2a] h-2 rounded overflow-hidden border border-[#3b494b]">
          <div class="bg-[#00f0ff] h-full transition-all duration-150" style="width: {scrollPercentage}%"></div>
        </div>
      </div>

      <!-- Scrollable Reader Text -->
      <div
        id="canon-scroll-area"
        bind:this={scrollContainer}
        on:scroll={handleScroll}
        class="max-h-64 overflow-y-auto p-4 bg-[#1c1f2a] border border-[#3b494b] rounded space-y-4 text-sm leading-relaxed text-[#dfe2f1] font-serif focus:outline-none focus:ring-2 focus:ring-[#00f0ff]"
        role="region"
        aria-label="Canon Manifesto Text Reader"
      >
        <p>
          In the philosophy of mind and cognitive science, the extended mind thesis asserts that cognitive processes are not bounded strictly by the biological skull or skin of the cognitive agent.
        </p>
        <p>
          When an external entity—such as notebook, terminal, or digital workspace—functions in active tandem with the human central nervous system to execute inferential reasoning, that physical substrate forms a genuine constitutive part of the mental state.
        </p>
        <p>
          Applying this deontic standard to software engineering demands zero-cookie transparency, exact execution traceability, and strict adherence to normative invariants across system boundaries.
        </p>
        <p>
          Continuous dwell time tracking without invasive persistent identifiers provides pure cognitive alignment metrics without compromising client privacy or digital autonomy.
        </p>
      </div>
    </section>

  {:else if activeTab === 'intake_form'}
    <!-- Intake Conversion Form with Field Focus & Submission Telemetry -->
    <section class="bg-[#171b26] border border-[#3b494b] rounded-lg p-6 space-y-6 max-w-xl mx-auto">
      <header class="border-b border-[#3b494b] pb-4 space-y-1">
        <span class="text-xs font-mono text-[#4edea3] bg-[#1c1f2a] px-2 py-0.5 rounded border border-[#3b494b]">[INTAKE_CONVERSION]</span>
        <h2 class="text-xl font-bold text-white">Client Intake & Project Brief Form</h2>
        <p class="text-xs text-[#b9cacb]">Fill out details below to convert. Fields trigger focus and validation telemetry events.</p>
      </header>

      {#if isConverted}
        <div class="p-4 bg-[#1c1f2a] border border-[#4edea3] rounded text-center space-y-3 font-mono text-xs">
          <span class="text-base text-[#4edea3] font-bold block">[CONVERSION_SUCCESS]</span>
          <p class="text-[#dfe2f1]">Intake proposal submitted and telemetry conversion event dispatched.</p>
          <button
            type="button"
            class="px-4 py-2 bg-[#00f0ff] text-[#00363a] font-bold rounded hover:bg-[#7df4ff] transition-colors focus:outline-none focus:ring-2 focus:ring-[#00f0ff]"
            on:click={() => {
              isConverted = false;
              formStep = 1;
              clientName = '';
              clientEmail = '';
              projectScope = '';
            }}
          >
            Submit Another Inquiry
          </button>
        </div>
      {:else}
        <form on:submit|preventDefault={submitIntakeForm} class="space-y-4 font-mono text-xs">
          {#if formError}
            <div class="p-3 bg-[#93000a] text-[#ffdad6] border border-[#ffb4ab] rounded" role="alert">
              {formError}
            </div>
          {/if}

          <!-- Step Indicators -->
          <div class="flex items-center gap-2 mb-4">
            <span class="px-2 py-1 rounded {formStep === 1 ? 'bg-[#00f0ff] text-[#00363a] font-bold' : 'bg-[#1c1f2a] text-[#b9cacb]'}">
              1. Name
            </span>
            <span class="px-2 py-1 rounded {formStep === 2 ? 'bg-[#00f0ff] text-[#00363a] font-bold' : 'bg-[#1c1f2a] text-[#b9cacb]'}">
              2. Email
            </span>
            <span class="px-2 py-1 rounded {formStep === 3 ? 'bg-[#00f0ff] text-[#00363a] font-bold' : 'bg-[#1c1f2a] text-[#b9cacb]'}">
              3. Scope & Submit
            </span>
          </div>

          {#if formStep === 1}
            <div class="space-y-2">
              <label for="intake-name-input" class="block text-[#dfe2f1] font-semibold">Client Name / Entity</label>
              <input
                id="intake-name-input"
                type="text"
                bind:value={clientName}
                on:focus={() => handleFieldFocus('client_name')}
                placeholder="e.g. Dmitry Efremov"
                class="w-full p-2.5 bg-[#1c1f2a] border border-[#3b494b] rounded text-[#dfe2f1] focus:outline-none focus:border-[#00f0ff] focus:ring-1 focus:ring-[#00f0ff]"
                required
              />
              <button
                type="button"
                on:click={nextStep}
                class="mt-3 px-4 py-2 bg-[#00f0ff] text-[#00363a] font-bold rounded hover:bg-[#7df4ff] transition-colors focus:outline-none focus:ring-2 focus:ring-[#00f0ff]"
              >
                Proceed to Email Step →
              </button>
            </div>
          {:else if formStep === 2}
            <div class="space-y-2">
              <label for="intake-email-input" class="block text-[#dfe2f1] font-semibold">Contact Email</label>
              <input
                id="intake-email-input"
                type="email"
                bind:value={clientEmail}
                on:focus={() => handleFieldFocus('client_email')}
                placeholder="contact@dmitryefremov.com"
                class="w-full p-2.5 bg-[#1c1f2a] border border-[#3b494b] rounded text-[#dfe2f1] focus:outline-none focus:border-[#00f0ff] focus:ring-1 focus:ring-[#00f0ff]"
                required
              />
              <div class="flex gap-2 mt-3">
                <button
                  type="button"
                  on:click={() => (formStep = 1)}
                  class="px-3 py-2 bg-[#1c1f2a] text-[#dfe2f1] rounded border border-[#3b494b] hover:bg-[#313540] focus:outline-none focus:ring-2 focus:ring-[#00f0ff]"
                >
                  ← Back
                </button>
                <button
                  type="button"
                  on:click={nextStep}
                  class="px-4 py-2 bg-[#00f0ff] text-[#00363a] font-bold rounded hover:bg-[#7df4ff] transition-colors focus:outline-none focus:ring-2 focus:ring-[#00f0ff]"
                >
                  Proceed to Scope Step →
                </button>
              </div>
            </div>
          {:else if formStep === 3}
            <div class="space-y-2">
              <label for="intake-scope-input" class="block text-[#dfe2f1] font-semibold">Project Scope & Directives</label>
              <textarea
                id="intake-scope-input"
                rows="4"
                bind:value={projectScope}
                on:focus={() => handleFieldFocus('project_scope')}
                placeholder="Outline requirements, autonomous goals, or system specs..."
                class="w-full p-2.5 bg-[#1c1f2a] border border-[#3b494b] rounded text-[#dfe2f1] focus:outline-none focus:border-[#00f0ff] focus:ring-1 focus:ring-[#00f0ff]"
                required
              ></textarea>
              <div class="flex gap-2 mt-3">
                <button
                  type="button"
                  on:click={() => (formStep = 2)}
                  class="px-3 py-2 bg-[#1c1f2a] text-[#dfe2f1] rounded border border-[#3b494b] hover:bg-[#313540] focus:outline-none focus:ring-2 focus:ring-[#00f0ff]"
                >
                  ← Back
                </button>
                <button
                  id="btn-submit-conversion"
                  type="submit"
                  class="px-5 py-2 bg-[#4edea3] text-[#003824] font-bold rounded hover:bg-[#6ffbbe] transition-colors focus:outline-none focus:ring-2 focus:ring-[#4edea3]"
                >
                  Submit & Convert →
                </button>
              </div>
            </div>
          {/if}
        </form>
      {/if}
    </section>
  {/if}

  <!-- Live Telemetry Event Stream Feed (WCAG region & keyboard accessible) -->
  <section class="bg-[#0a0e18] border border-[#3b494b] rounded-lg overflow-hidden font-mono text-xs">
    <header class="bg-[#1c1f2a] px-4 py-2 border-b border-[#3b494b] flex justify-between items-center">
      <div class="flex items-center gap-2">
        <span class="w-2 h-2 rounded-full bg-[#4edea3] animate-pulse"></span>
        <span class="font-bold text-[#dfe2f1] uppercase">[LIVE_TELEMETRY_FEED]</span>
      </div>
      <span class="text-[#849495]">EVENTS_LOGGED: {loggedEvents.length}</span>
    </header>
    <div
      id="telemetry-feed-list"
      class="p-4 space-y-2 max-h-40 overflow-y-auto text-[#b9cacb]"
      role="region"
      aria-label="Live Telemetry Event Log"
    >
      {#if loggedEvents.length === 0}
        <p class="text-[#849495] italic">No telemetry events recorded yet.</p>
      {:else}
        {#each loggedEvents as evt}
          <div class="flex justify-between items-center border-b border-[#1c1f2a] pb-1 gap-2">
            <div class="flex items-center gap-2 truncate">
              <span class="px-1.5 py-0.5 rounded text-[10px] bg-[#1c1f2a] text-[#00f0ff] border border-[#3b494b] shrink-0 font-bold">
                {evt.type}
              </span>
              <span class="text-white truncate">{JSON.stringify(evt.payload)}</span>
            </div>
            <span class="text-[#849495] shrink-0 text-[10px]">{evt.timestamp.slice(11, 19)}</span>
          </div>
        {/each}
      {/if}
    </div>
  </section>
</div>
