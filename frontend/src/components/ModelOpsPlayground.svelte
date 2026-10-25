<script lang="ts">
  import { onMount } from 'svelte';

  // Hyperparameter State
  let hyperParams = {
    temp: 0.70,
    topp: 0.95,
    tokens: 512
  };

  let activeModel = {
    name: 'Text-Extract-XL',
    arch: '7B-MoE',
    provider: 'Cerebras Ultra-Core',
    id: 'text'
  };

  let drawerOpen = true;
  let promptText = ("{\n  \"user_id\": \"usr_9402a\",\n  \"channel\": \"mobile_in_app_chat\",\n  \"transcript\": \"Hello, my order #48192 arrived yesterday but the screen is completely cracked and unresponsive. I need a full immediate refund or expedited replacement sent to 742 Evergreen Terr right away. This is unacceptable.\"\n}");
  let streamingOutput = false;
  let returnConfidence = false;
  let isRunning = false;
  let activeTab = 'json';
  let speedChipText = '114 t/s • eos: stop';
  let speedChipGlow = false;
  let copyButtonText = 'Copy JSON';

  function switchModel(name: string, arch: string, provider: string, id: string) {
    activeModel = { name, arch, provider, id };
  }

  function toggleDrawer() {
    drawerOpen = !drawerOpen;
  }

  const samplePresets: Record<string, string> = {
    support: `{\n  "user_id": "usr_9402a",\n  "channel": "mobile_in_app_chat",\n  "transcript": "Hello, my order #48192 arrived yesterday but the screen is completely cracked and unresponsive. I need a full immediate refund or expedited replacement sent to 742 Evergreen Terr right away. This is unacceptable."\n}`,
    ocr: `{\n  "source_document": "scan_store_receipt_9921.jpg",\n  "raw_ocr_lines": [\n    "WALMART SUPERCENTER #3210",\n    "DATE: 2025-05-12 14:32",\n    "ITEMS: 4K MONITOR $299.99 TAX $24.00",\n    "TOTAL PAID: $323.99 VISA *9912"\n  ]\n}`,
    sentiment: `{\n  "source_feed": "X/Twitter Realtime",\n  "handle": "@dev_builder",\n  "text": "The new 7B-MoE inference throughput is blazing fast! Went from 35ms to 12ms latency on Cerebras nodes. Incredible release!"\n}`
  };

  function loadPreset(key: string) {
    promptText = samplePresets[key];
  }

  function clearPrompt() {
    promptText = '';
  }

  function runInference() {
    isRunning = true;
    setTimeout(() => {
      isRunning = false;
      speedChipText = `${Math.floor(110 + Math.random() * 20)} t/s • eos: stop`;
      speedChipGlow = true;
      setTimeout(() => speedChipGlow = false, 1200);
    }, 420);
  }

  function handleKeydown(e: KeyboardEvent) {
    if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
      e.preventDefault();
      runInference();
    }
  }

  function copyOutput() {
    copyButtonText = 'Copied!';
    setTimeout(() => copyButtonText = 'Copy JSON', 1500);
  }

  function exportCurl() {
    alert("cURL snippet copied: curl -X POST https://api.modelops.io/v2.4/extract -H 'Authorization: Bearer ops_live_key'");
  }

  function shareEmbed() {
    alert("Share URL: https://modelops.io/play/share/7b-moe-48192");
  }

  $: tokenCount = Math.floor(promptText.length / 4);
</script>

<svelte:window on:keydown={handleKeydown} />

<div class="bg-background text-on-surface antialiased font-body-md min-h-screen pb-24 selection:bg-primary-container selection:text-on-primary-container">
  <!-- MAIN PLAYGROUND CANVAS -->
  <main class="max-w-7xl mx-auto px-4 pt-4 pb-12 space-y-4">
    <!-- 1. ACTIVE MODEL SELECTOR HORIZONTAL RIBBON -->
    <section class="space-y-2">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-1.5">
          <span class="material-symbols-outlined text-primary text-base" aria-hidden="true">psychology</span>
          <span class="text-label-sm font-label-sm text-on-surface-variant">ACTIVE INFERENCE ENGINE</span>
        </div>
        <div class="flex items-center space-x-1 text-label-sm font-label-sm text-tertiary">
          <span class="h-1.5 w-1.5 rounded-full bg-tertiary"></span>
          <span>GPU CLUSTER ACTIVE</span>
        </div>
      </div>
      <!-- Segmented Pill Switch -->
      <div class="flex space-x-2 overflow-x-auto no-scrollbar py-1">
        <!-- Inactive Pill 1 -->
        <button
          class="model-pill flex-shrink-0 px-3 py-2 rounded transition-colors flex items-center space-x-2 {activeModel.id === 'vision' ? 'bg-primary-container/20 border-primary text-primary glow-active' : 'bg-surface-container border-outline-variant/40 text-on-surface-variant hover:border-primary/50'}"
          style={activeModel.id === 'vision' ? 'box-shadow: 0 0 0 1px #8083ff, 0 4px 20px -2px rgba(128, 131, 255, 0.25);' : 'border: 1px solid rgba(70, 69, 84, 0.4);'}
          on:click={() => switchModel('Vision-Embed-v2', '4B-ViT', 'Google Cloud TPU', 'vision')}>
          <span class="material-symbols-outlined text-sm" aria-hidden="true">image</span>
          <span class="text-label-md font-label-md">Vision-Embed-v2</span>
        </button>
        <!-- Active Pill (Selected in Prompt) -->
        <button
          class="model-pill flex-shrink-0 px-3 py-2 rounded transition-colors flex items-center space-x-2 {activeModel.id === 'text' ? 'bg-primary-container/20 border border-primary text-primary glow-active' : 'bg-surface-container border border-outline-variant/40 text-on-surface-variant hover:border-primary/50'}"
          style={activeModel.id === 'text' ? 'box-shadow: 0 0 0 1px #8083ff, 0 4px 20px -2px rgba(128, 131, 255, 0.25);' : 'border: 1px solid rgba(70, 69, 84, 0.4);'}
          on:click={() => switchModel('Text-Extract-XL', '7B-MoE', 'Cerebras Ultra-Core', 'text')}>
          <span class="material-symbols-outlined text-sm" aria-hidden="true">text_fields</span>
          <span class="text-label-md font-label-md {activeModel.id === 'text' ? 'font-semibold' : ''}">Text-Extract-XL</span>
          {#if activeModel.id === 'text'}
            <span class="bg-primary/20 text-primary border border-primary/30 px-1.5 py-0.5 rounded text-label-sm font-label-sm">7B-MoE</span>
          {/if}
        </button>
        <!-- Inactive Pill 2 -->
        <button
          class="model-pill flex-shrink-0 px-3 py-2 rounded transition-colors flex items-center space-x-2 {activeModel.id === 'audio' ? 'bg-primary-container/20 border border-primary text-primary glow-active' : 'bg-surface-container border border-outline-variant/40 text-on-surface-variant hover:border-primary/50'}"
          style={activeModel.id === 'audio' ? 'box-shadow: 0 0 0 1px #8083ff, 0 4px 20px -2px rgba(128, 131, 255, 0.25);' : 'border: 1px solid rgba(70, 69, 84, 0.4);'}
          on:click={() => switchModel('Audio-Transcribe', 'Whisper-L', 'AWS Inferentia', 'audio')}>
          <span class="material-symbols-outlined text-sm" aria-hidden="true">graphic_eq</span>
          <span class="text-label-md font-label-md">Audio-Transcribe</span>
        </button>
        <!-- Inactive Pill 3 -->
        <button
          class="model-pill flex-shrink-0 px-3 py-2 rounded transition-colors flex items-center space-x-2 {activeModel.id === 'code' ? 'bg-primary-container/20 border border-primary text-primary glow-active' : 'bg-surface-container border border-outline-variant/40 text-on-surface-variant hover:border-primary/50'}"
          style={activeModel.id === 'code' ? 'box-shadow: 0 0 0 1px #8083ff, 0 4px 20px -2px rgba(128, 131, 255, 0.25);' : 'border: 1px solid rgba(70, 69, 84, 0.4);'}
          on:click={() => switchModel('Code-Gen-4B', '4B-Dense', 'Local TensorRT', 'code')}>
          <span class="material-symbols-outlined text-sm" aria-hidden="true">terminal</span>
          <span class="text-label-md font-label-md">Code-Gen-4B</span>
        </button>
      </div>
      <!-- Active Model Meta Bar -->
      <div class="flex items-center justify-between px-3 py-1.5 bg-surface-container-low rounded border border-outline-variant/30 text-label-sm font-label-sm">
        <div class="flex items-center space-x-3 text-on-surface-variant">
          <span>Provider: <strong class="text-on-surface font-label-sm">{activeModel.provider}</strong></span>
          <span class="text-outline-variant">|</span>
          <span>Arch: <strong class="text-primary font-label-sm">{activeModel.arch}</strong></span>
        </div>
        <div class="text-secondary font-label-sm">FP8 Quantized</div>
      </div>
    </section>

    <!-- 2. WORKBENCH & INPUT AREA -->
    <section class="bg-surface-container rounded-xl p-3 sm:p-4 border border-outline-variant/30 space-y-3">
      <!-- Section header with sample presets -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
        <div class="flex items-center space-x-2">
          <span class="material-symbols-outlined text-secondary text-base" aria-hidden="true">edit_note</span>
          <h2 class="text-label-md font-label-md text-on-surface font-semibold m-0">PROMPT CONSOLE & PAYLOAD</h2>
        </div>
        <!-- Quick Sample Presets -->
        <div class="flex items-center space-x-1.5 overflow-x-auto no-scrollbar py-0.5">
          <span class="text-label-sm font-label-sm text-outline shrink-0">Presets:</span>
          <button class="px-2 py-1 rounded bg-surface-container-high border border-outline-variant/40 hover:border-secondary text-on-surface-variant hover:text-on-surface text-label-sm font-label-sm transition-all shrink-0 active:scale-95" on:click={() => loadPreset('support')}>
            Customer Support QA
          </button>
          <button class="px-2 py-1 rounded bg-surface-container-high border border-outline-variant/40 hover:border-secondary text-on-surface-variant hover:text-on-surface text-label-sm font-label-sm transition-all shrink-0 active:scale-95" on:click={() => loadPreset('ocr')}>
            Receipt OCR Parse
          </button>
          <button class="px-2 py-1 rounded bg-surface-container-high border border-outline-variant/40 hover:border-secondary text-on-surface-variant hover:text-on-surface text-label-sm font-label-sm transition-all shrink-0 active:scale-95" on:click={() => loadPreset('sentiment')}>
            Sentiment & Intent
          </button>
        </div>
      </div>

      <!-- Textarea input field -->
      <div class="relative rounded-lg bg-surface-container-lowest border border-outline-variant/40 focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-all">
        <div class="flex items-center justify-between px-3 py-1.5 border-b border-outline-variant/20 bg-surface-container-low/60">
          <div class="flex items-center space-x-2">
            <span class="text-label-sm font-label-sm text-outline">SYSTEM:</span>
            <span class="text-label-sm font-label-sm text-tertiary">Extraction Engine v4.2 [Strict-JSON]</span>
          </div>
          <button class="text-outline hover:text-error text-label-sm font-label-sm flex items-center space-x-1 transition-colors" on:click={clearPrompt}>
            <span class="material-symbols-outlined text-xs" aria-hidden="true">backspace</span>
            <span>Clear</span>
          </button>
        </div>
        <textarea
          class="w-full bg-transparent p-3 text-body-md font-body-md text-on-surface placeholder-outline focus:outline-none resize-none border-0 focus:ring-0 leading-relaxed font-code-md text-code-md"
          placeholder="Enter structured prompt or paste raw payload text for extraction..."
          rows="4"
          bind:value={promptText}
          aria-label="Prompt payload"
        ></textarea>
        <!-- Textarea utility bar -->
        <div class="flex items-center justify-between px-3 py-1.5 border-t border-outline-variant/20 text-label-sm font-label-sm bg-surface-container-low/40">
          <span class="text-outline">Tokens: <strong class="text-on-surface font-label-sm">{tokenCount} / 512</strong></span>
          <span class="text-outline">Mode: <strong class="text-secondary font-label-sm">Entity & Sentiment Extraction</strong></span>
        </div>
      </div>

      <!-- 3. INTERACTIVE HYPERPARAMETER CONTROL DRAWER -->
      <div class="rounded-lg bg-surface-container-low border border-outline-variant/40 overflow-hidden">
        <!-- Accordion trigger -->
        <button class="w-full px-3 py-2.5 flex items-center justify-between bg-surface-container-high/40 hover:bg-surface-container-high/70 transition-colors" on:click={toggleDrawer} aria-expanded={drawerOpen}>
          <div class="flex items-center space-x-2">
            <span class="material-symbols-outlined text-primary text-base" aria-hidden="true">tune</span>
            <span class="text-label-md font-label-md text-on-surface font-medium">Hyperparameters & Inference Controls</span>
          </div>
          <div class="flex items-center space-x-2">
            <span class="text-label-sm font-label-sm text-on-surface-variant">T={hyperParams.temp.toFixed(2)} | P={hyperParams.topp.toFixed(2)} | Max={hyperParams.tokens}</span>
            <span class="material-symbols-outlined text-on-surface-variant transition-transform duration-200" aria-hidden="true" style={drawerOpen ? 'transform: rotate(0deg)' : 'transform: rotate(-90deg)'}>expand_more</span>
          </div>
        </button>

        <!-- Collapsible body -->
        {#if drawerOpen}
          <div class="p-3 sm:p-4 space-y-4 border-t border-outline-variant/30">
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <!-- Temperature Slider -->
              <div class="space-y-1.5">
                <div class="flex justify-between items-center">
                  <label class="text-label-sm font-label-sm text-on-surface-variant" for="temp-slider">Temperature</label>
                  <span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-surface-bright text-primary border border-outline-variant/40">{hyperParams.temp.toFixed(2)}</span>
                </div>
                <input class="w-full custom-range" id="temp-slider" max="1.5" min="0.0" step="0.05" type="range" bind:value={hyperParams.temp}/>
                <div class="flex justify-between text-[9px] text-outline font-label-sm">
                  <span>Deterministic</span>
                  <span>Creative</span>
                </div>
              </div>

              <!-- Top-P Slider -->
              <div class="space-y-1.5">
                <div class="flex justify-between items-center">
                  <label class="text-label-sm font-label-sm text-on-surface-variant" for="topp-slider">Top-P (Nucleus)</label>
                  <span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-surface-bright text-secondary border border-outline-variant/40">{hyperParams.topp.toFixed(2)}</span>
                </div>
                <input class="w-full custom-range" id="topp-slider" max="1.0" min="0.1" step="0.05" type="range" bind:value={hyperParams.topp}/>
                <div class="flex justify-between text-[9px] text-outline font-label-sm">
                  <span>0.1 (Focused)</span>
                  <span>1.0 (Full Corpus)</span>
                </div>
              </div>

              <!-- Max Tokens Slider -->
              <div class="space-y-1.5">
                <div class="flex justify-between items-center">
                  <label class="text-label-sm font-label-sm text-on-surface-variant" for="tokens-slider">Max Output Tokens</label>
                  <span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-surface-bright text-tertiary border border-outline-variant/40">{hyperParams.tokens}</span>
                </div>
                <input class="w-full custom-range" id="tokens-slider" max="2048" min="64" step="64" type="range" bind:value={hyperParams.tokens}/>
                <div class="flex justify-between text-[9px] text-outline font-label-sm">
                  <span>64 tokens</span>
                  <span>2048 tokens</span>
                </div>
              </div>
            </div>

            <!-- Feature Toggles -->
            <div class="pt-2 border-t border-outline-variant/20 grid grid-cols-1 sm:grid-cols-2 gap-3">
              <!-- Toggle 1: Streaming Output -->
              <div class="flex items-center justify-between p-2 rounded bg-surface-container border border-outline-variant/30">
                <div class="flex flex-col">
                  <span class="text-label-sm font-label-sm text-on-surface font-semibold">Streaming Output</span>
                  <span class="text-body-sm font-body-sm text-outline">SSE token stream emission</span>
                </div>
                <button
                  class="w-10 h-6 flex items-center rounded-full p-1 cursor-pointer transition-colors duration-200 {streamingOutput ? 'bg-primary-container' : 'bg-surface-container-high'}"
                  on:click={() => streamingOutput = !streamingOutput}
                  aria-pressed={streamingOutput}
                  aria-label="Toggle streaming output"
                >
                  <div class="bg-white w-4 h-4 rounded-full shadow-md transform transition-transform duration-200 {streamingOutput ? 'translate-x-4' : 'translate-x-0'}"></div>
                </button>
              </div>

              <!-- Toggle 2: Return Confidence Scores -->
              <div class="flex items-center justify-between p-2 rounded bg-surface-container border border-outline-variant/30">
                <div class="flex flex-col">
                  <span class="text-label-sm font-label-sm text-on-surface font-semibold">Return Confidence Scores</span>
                  <span class="text-body-sm font-body-sm text-outline">Logprobs & entity certainty tags</span>
                </div>
                <button
                  class="w-10 h-6 flex items-center rounded-full p-1 cursor-pointer transition-colors duration-200 {returnConfidence ? 'bg-primary-container' : 'bg-surface-container-high'}"
                  on:click={() => returnConfidence = !returnConfidence}
                  aria-pressed={returnConfidence}
                  aria-label="Toggle return confidence scores"
                >
                  <div class="bg-white w-4 h-4 rounded-full shadow-md transform transition-transform duration-200 {returnConfidence ? 'translate-x-4' : 'translate-x-0'}"></div>
                </button>
              </div>
            </div>
          </div>
        {/if}
      </div>
    </section>

    <!-- 4. EXECUTION BAR WITH BENCHMARK STATS -->
    <section class="bg-surface-container-high rounded-xl p-3 border border-outline-variant/40 flex flex-col sm:flex-row items-center justify-between gap-3">
      <!-- Runtime stats telemetry -->
      <div class="flex items-center justify-between w-full sm:w-auto space-x-4 text-label-sm font-label-sm">
        <div class="flex items-center space-x-1.5">
          <span class="material-symbols-outlined text-outline text-base" aria-hidden="true">hourglass_top</span>
          <span class="text-outline">Est. Latency: <strong class="text-on-surface font-label-sm">~380ms</strong></span>
        </div>
        <div class="flex items-center space-x-1.5">
          <span class="material-symbols-outlined text-secondary text-base" aria-hidden="true">toll</span>
          <span class="text-outline">Cost: <strong class="text-secondary font-label-sm">12 credits/run</strong></span>
        </div>
      </div>

      <!-- Primary Run Trigger Button -->
      <button
        class="w-full sm:w-auto min-w-[200px] h-11 px-5 rounded-lg bg-primary text-on-primary-fixed font-label-md font-bold text-label-md flex items-center justify-center space-x-2 shadow-lg hover:brightness-110 active:scale-95 transition-all {isRunning ? 'opacity-80 pointer-events-none' : ''}"
        style="box-shadow: 0 4px 14px 0 rgba(192, 193, 255, 0.2);"
        on:click={runInference}>
        <span class="material-symbols-outlined text-xl {isRunning ? 'animate-spin' : ''}" aria-hidden="true">{isRunning ? 'sync' : 'play_arrow'}</span>
        <span>{isRunning ? 'INFERRING...' : 'RUN MODEL'}</span>
        <span class="text-label-sm font-label-sm opacity-70 border border-on-primary-fixed/30 px-1 rounded ml-1">⌘ + ↵</span>
      </button>
    </section>

    <!-- 5. DYNAMIC INTERACTIVE OUTPUT PREVIEW -->
    <section class="bg-surface-container rounded-xl border border-outline-variant/30 overflow-hidden space-y-0">
      <!-- Output Header & Tab Bar -->
      <div class="border-b border-outline-variant/30 bg-surface-container-low px-3 pt-3 flex flex-col space-y-2">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-2">
            <span class="h-2 w-2 rounded-full bg-tertiary"></span>
            <span class="text-label-md font-label-md font-bold text-on-surface">INFERENCE RESULTS</span>
            <span class="text-label-sm font-label-sm px-2 py-0.5 rounded bg-tertiary-container/30 text-tertiary border border-tertiary/40 transition-shadow duration-300" style={speedChipGlow ? 'box-shadow: 0 0 0 1px #4cd7f6, 0 4px 16px -2px rgba(76, 215, 246, 0.2);' : ''}>{speedChipText}</span>
          </div>
          <!-- Execution Timestamp -->
          <span class="text-label-sm font-label-sm text-outline">Latency: 342ms</span>
        </div>

        <!-- Output Tabs -->
        <div class="flex space-x-2 overflow-x-auto no-scrollbar pt-1">
          <button class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'json' ? 'border-primary text-primary active' : 'border-transparent text-outline hover:text-on-surface'}" on:click={() => activeTab = 'json'}>
            <span class="material-symbols-outlined text-sm" aria-hidden="true">data_object</span>
            <span>Parsed JSON Output</span>
          </button>
          <button class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'tokens' ? 'border-primary text-primary active' : 'border-transparent text-outline hover:text-on-surface'}" on:click={() => activeTab = 'tokens'}>
            <span class="material-symbols-outlined text-sm" aria-hidden="true">bubble_chart</span>
            <span>Raw Tokens & Confidence</span>
          </button>
          <button class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'tree' ? 'border-primary text-primary active' : 'border-transparent text-outline hover:text-on-surface'}" on:click={() => activeTab = 'tree'}>
            <span class="material-symbols-outlined text-sm" aria-hidden="true">account_tree</span>
            <span>Interactive Tree</span>
          </button>
          <button class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'embed' ? 'border-primary text-primary active' : 'border-transparent text-outline hover:text-on-surface'}" on:click={() => activeTab = 'embed'}>
            <span class="material-symbols-outlined text-sm" aria-hidden="true">code</span>
            <span>Embed Snippet (&lt;/&gt;)</span>
          </button>
        </div>
      </div>

      <!-- Tab Content Area -->
      <div class="p-3 sm:p-4">
        <!-- View 1: Parsed JSON Output -->
        {#if activeTab === 'json'}
          <div class="space-y-3">
            <!-- Structured Extraction Highlights Cards (High Density Bento) -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-2">
              <!-- Card 1: Intent -->
              <div class="p-2.5 rounded-lg bg-surface-container-high border border-outline-variant/40 space-y-1">
                <span class="text-label-sm font-label-sm text-outline">PRIMARY INTENT</span>
                <div class="flex items-center justify-between">
                  <span class="text-body-md font-body-md font-semibold text-primary">Refund Request</span>
                  <span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-tertiary-container/40 text-tertiary border border-tertiary/40">98.4%</span>
                </div>
              </div>
              <!-- Card 2: Entity ID -->
              <div class="p-2.5 rounded-lg bg-surface-container-high border border-outline-variant/40 space-y-1">
                <span class="text-label-sm font-label-sm text-outline">DETECTED ENTITIES</span>
                <div class="flex items-center justify-between">
                  <span class="text-code-md font-code-md font-semibold text-secondary">Order #48192</span>
                  <span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-tertiary-container/40 text-tertiary border border-tertiary/40">99.1%</span>
                </div>
              </div>
              <!-- Card 3: Sentiment -->
              <div class="p-2.5 rounded-lg bg-surface-container-high border border-outline-variant/40 space-y-1">
                <span class="text-label-sm font-label-sm text-outline">SENTIMENT POLARITY</span>
                <div class="flex items-center justify-between">
                  <span class="text-body-md font-body-md font-semibold text-error">Frustrated (-0.82)</span>
                  <span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-error-container/60 text-error border border-error/40">High Urgency</span>
                </div>
              </div>
            </div>

            <!-- Code View Area -->
            <div class="relative bg-surface-container-lowest rounded-lg p-3 border border-outline-variant/40 overflow-x-auto">
              <pre class="text-code-sm font-code-sm text-on-surface leading-5 font-mono"><span class="text-outline">{`{`}</span>
  <span class="text-secondary">"intent"</span>: <span class="text-tertiary">"refund_request"</span>,
  <span class="text-secondary">"confidence"</span>: <span class="text-primary">0.984</span>,
  <span class="text-secondary">"urgency_level"</span>: <span class="text-error">"critical"</span>,
  <span class="text-secondary">"entities"</span>: <span class="text-outline">[</span>
    <span class="text-outline">{`{`}</span>
      <span class="text-secondary">"entity"</span>: <span class="text-tertiary">"order_id"</span>,
      <span class="text-secondary">"value"</span>: <span class="text-primary-fixed">"48192"</span>,
      <span class="text-secondary">"confidence"</span>: <span class="text-primary">0.991</span>,
      <span class="text-secondary">"span"</span>: <span class="text-outline">[20, 26]</span>
    <span class="text-outline">{`}`}</span>,
    <span class="text-outline">{`{`}</span>
      <span class="text-secondary">"entity"</span>: <span class="text-tertiary">"defect_type"</span>,
      <span class="text-secondary">"value"</span>: <span class="text-primary-fixed">"screen_cracked_unresponsive"</span>,
      <span class="text-secondary">"confidence"</span>: <span class="text-primary">0.972</span>
    <span class="text-outline">{`}`}</span>,
    <span class="text-outline">{`{`}</span>
      <span class="text-secondary">"entity"</span>: <span class="text-tertiary">"shipping_address"</span>,
      <span class="text-secondary">"value"</span>: <span class="text-primary-fixed">"742 Evergreen Terr"</span>,
      <span class="text-secondary">"confidence"</span>: <span class="text-primary">0.943</span>
    <span class="text-outline">{`}`}</span>
  <span class="text-outline">]</span>,
  <span class="text-secondary">"sentiment_score"</span>: <span class="text-error">-0.82</span>,
  <span class="text-secondary">"suggested_action"</span>: <span class="text-tertiary">"auto_generate_return_label_tier1"</span>
<span class="text-outline">{`}`}</span></pre>
            </div>
          </div>
        {/if}

        <!-- View 2: Raw Tokens & Confidence -->
        {#if activeTab === 'tokens'}
          <div class="space-y-3">
            <div class="text-label-sm font-label-sm text-outline mb-1">LOGPROB DENSITY (Tokens color-coded by confidence):</div>
            <div class="p-3 bg-surface-container-lowest rounded-lg border border-outline-variant/40 flex flex-wrap gap-1.5 font-code-sm text-code-sm">
              <span class="px-1.5 py-0.5 rounded bg-tertiary/20 text-tertiary border border-tertiary/40">refund_request (0.984)</span>
              <span class="px-1.5 py-0.5 rounded bg-tertiary/20 text-tertiary border border-tertiary/40">order_id (0.991)</span>
              <span class="px-1.5 py-0.5 rounded bg-secondary/20 text-secondary border border-secondary/40">48192 (0.992)</span>
              <span class="px-1.5 py-0.5 rounded bg-primary/20 text-primary border border-primary/40">screen_cracked (0.972)</span>
              <span class="px-1.5 py-0.5 rounded bg-primary/20 text-primary border border-primary/40">unresponsive (0.965)</span>
              <span class="px-1.5 py-0.5 rounded bg-secondary/20 text-secondary border border-secondary/40">expedited_replacement (0.923)</span>
              <span class="px-1.5 py-0.5 rounded bg-tertiary/20 text-tertiary border border-tertiary/40">742_Evergreen_Terr (0.943)</span>
              <span class="px-1.5 py-0.5 rounded bg-error/20 text-error border border-error/40">sentiment:negative (-0.82)</span>
            </div>
          </div>
        {/if}

        <!-- View 3: Interactive Tree -->
        {#if activeTab === 'tree'}
          <div class="space-y-2 font-code-sm text-code-sm">
            <div class="p-3 bg-surface-container-lowest rounded-lg border border-outline-variant/40 space-y-2 text-on-surface">
              <div class="flex items-center space-x-1 cursor-pointer text-primary">
                <span class="material-symbols-outlined text-sm" aria-hidden="true">folder_open</span>
                <span>Root: ExtractionResponse</span>
              </div>
              <div class="pl-4 border-l border-outline-variant/40 space-y-1.5">
                <div class="text-tertiary">├─ intent: "refund_request" <span class="text-outline text-label-sm">(float 0.984)</span></div>
                <div class="text-secondary">├─ urgency: "critical"</div>
                <div>
                  <span class="text-primary">└─ entities [3 items]</span>
                  <div class="pl-4 border-l border-outline-variant/40 space-y-1 text-on-surface-variant">
                    <div>[0] {`{`} type: "order_id", val: "48192" {`}`}</div>
                    <div>[1] {`{`} type: "defect", val: "screen_cracked" {`}`}</div>
                    <div>[2] {`{`} type: "address", val: "742 Evergreen Terr" {`}`}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        {/if}

        <!-- View 4: Embed Snippet -->
        {#if activeTab === 'embed'}
          <div class="space-y-2">
            <div class="text-label-sm font-label-sm text-outline">COPY EMBED SCRIPT OR SDK INTEGRATION:</div>
            <div class="p-3 bg-surface-container-lowest rounded-lg border border-outline-variant/40 overflow-x-auto font-code-sm text-code-sm text-secondary leading-relaxed">
              <code>&lt;script src="https://cdn.modelops.io/v2.4/embed.js"&gt;&lt;/script&gt;<br/>
&lt;model-runner model="Text-Extract-XL" endpoint="cerebras-7b" temp="{hyperParams.temp}"&gt;&lt;/model-runner&gt;</code>
            </div>
          </div>
        {/if}
      </div>

      <!-- Quick Action Bar -->
      <div class="p-3 bg-surface-container-low border-t border-outline-variant/30 flex items-center justify-between flex-wrap gap-2">
        <div class="flex items-center space-x-2">
          <button class="px-2.5 py-1.5 rounded bg-surface-container border border-outline-variant/40 hover:border-primary text-on-surface hover:text-primary text-label-sm font-label-sm flex items-center space-x-1.5 transition-all active:scale-95" on:click={copyOutput}>
            <span class="material-symbols-outlined text-sm" aria-hidden="true">content_copy</span>
            <span>{copyButtonText}</span>
          </button>
          <button class="px-2.5 py-1.5 rounded bg-surface-container border border-outline-variant/40 hover:border-secondary text-on-surface hover:text-secondary text-label-sm font-label-sm flex items-center space-x-1.5 transition-all active:scale-95" on:click={runInference}>
            <span class="material-symbols-outlined text-sm" aria-hidden="true">casino</span>
            <span>Re-run with Noise</span>
          </button>
        </div>
        <div class="flex items-center space-x-2">
          <button class="px-2.5 py-1.5 rounded bg-surface-container border border-outline-variant/40 hover:border-tertiary text-on-surface hover:text-tertiary text-label-sm font-label-sm flex items-center space-x-1.5 transition-all active:scale-95" on:click={exportCurl}>
            <span class="material-symbols-outlined text-sm" aria-hidden="true">terminal</span>
            <span>Export cURL</span>
          </button>
          <button class="px-2.5 py-1.5 rounded bg-primary-container/20 border border-primary/40 text-primary text-label-sm font-label-sm flex items-center space-x-1.5 hover:bg-primary-container/30 transition-all active:scale-95" on:click={shareEmbed}>
            <span class="material-symbols-outlined text-sm" aria-hidden="true">share</span>
            <span>Share</span>
          </button>
        </div>
      </div>
    </section>
  </main>
</div>

<style>
  .custom-range {
    -webkit-appearance: none;
    background: transparent;
  }
  .custom-range::-webkit-slider-thumb {
    -webkit-appearance: none;
    height: 18px;
    width: 18px;
    border-radius: 9999px;
    background: #c0c1ff;
    border: 2px solid #060e20;
    cursor: pointer;
    box-shadow: 0 0 8px rgba(192, 193, 255, 0.6);
    margin-top: -7px;
  }
  .custom-range::-webkit-slider-runnable-track {
    width: 100%;
    height: 4px;
    cursor: pointer;
    background: #222a3d;
    border-radius: 2px;
  }
  .no-scrollbar::-webkit-scrollbar {
    display: none;
  }
  .no-scrollbar {
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
  /* Ambient glow for active AI runtime elements */
  .glow-active {
    box-shadow: 0 0 0 1px #8083ff, 0 4px 20px -2px rgba(128, 131, 255, 0.25);
  }
</style>
