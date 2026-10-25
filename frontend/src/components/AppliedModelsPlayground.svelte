<script lang="ts">
  // Applied Models Playground interactive functional component based on the mockup.
  let activeTab: 'json' | 'tokens' | 'tree' | 'embed' = 'json';

  let hyperParams = {
    temp: 0.70,
    topp: 0.95,
    tokens: 512
  };

  let isRunning = false;
  let runLabel = 'RUN MODEL';
  let speedChipClass = '';
  let speedChipText = '114 t/s • eos: stop';

  const samplePresets: Record<string, string> = {
    support: `{\n  "user_id": "usr_9402a",\n  "channel": "mobile_in_app_chat",\n  "transcript": "Hello, my order #48192 arrived yesterday but the screen is completely cracked and unresponsive. I need a full immediate refund or expedited replacement sent to 742 Evergreen Terr right away. This is unacceptable."\n}`,
    ocr: `{\n  "source_document": "scan_store_receipt_9921.jpg",\n  "raw_ocr_lines": [\n    "WALMART SUPERCENTER #3210",\n    "DATE: 2025-05-12 14:32",\n    "ITEMS: 4K MONITOR $299.99 TAX $24.00",\n    "TOTAL PAID: $323.99 VISA *9912"\n  ]\n}`,
    sentiment: `{\n  "source_feed": "X/Twitter Realtime",\n  "handle": "@dev_builder",\n  "text": "The new 7B-MoE inference throughput is blazing fast! Went from 35ms to 12ms latency on Cerebras nodes. Incredible release!"\n}`
  };

  let promptText = samplePresets.support;

  let streamEnabled = true;
  let confEnabled = true;

  function updateParam(param: keyof typeof hyperParams, event: Event) {
    const val = (event.target as HTMLInputElement).value;
    hyperParams[param] = parseFloat(val);
  }

  let drawerOpen = true;
  function toggleDrawer() {
    drawerOpen = !drawerOpen;
  }

  let activeModel = '7B-MoE';
  let modelProvider = 'Cerebras Ultra-Core';
  let modelArch = '7B Mixture-of-Experts';

  function switchModel(name: string, arch: string, provider: string) {
    activeModel = name;
    modelArch = arch;
    modelProvider = provider;
  }

  function loadPreset(key: string) {
    promptText = samplePresets[key];
  }

  function clearPrompt() {
    promptText = '';
  }

  function toggleSwitch(type: 'stream' | 'conf') {
    if (type === 'stream') streamEnabled = !streamEnabled;
    if (type === 'conf') confEnabled = !confEnabled;
  }

  function switchTab(tabKey: typeof activeTab) {
    activeTab = tabKey;
  }

  function runInference() {
    if (isRunning) return;
    isRunning = true;
    runLabel = 'INFERRING...';

    setTimeout(() => {
      isRunning = false;
      runLabel = 'RUN MODEL';
      speedChipText = `${Math.floor(110 + Math.random() * 20)} t/s • eos: stop`;
      speedChipClass = 'glow-cyan';
      setTimeout(() => speedChipClass = '', 1200);
    }, 420);
  }

  function handleKeydown(e: KeyboardEvent) {
    if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
      e.preventDefault();
      runInference();
    }
  }

  let copyBtnText = 'Copy JSON';
  function copyOutput() {
    copyBtnText = 'Copied!';
    setTimeout(() => copyBtnText = 'Copy JSON', 1500);
  }

  function exportCurl() {
    alert("cURL snippet copied: curl -X POST https://api.modelops.io/v2.4/extract -H 'Authorization: Bearer ops_live_key'");
  }

  function shareEmbed() {
    alert("Share URL: https://modelops.io/play/share/7b-moe-48192");
  }
</script>


<div class="modelops-playground max-w-7xl mx-auto px-4 pt-4 pb-12 space-y-4 text-on-surface antialiased font-body-md min-h-screen">
  <!-- 1. ACTIVE MODEL SELECTOR HORIZONTAL RIBBON -->
  <section class="space-y-2">
    <div class="flex items-center justify-between">
      <div class="flex items-center space-x-1.5">
        <span class="material-symbols-outlined text-primary text-base" data-icon="psychology">psychology</span>
        <span class="text-label-sm font-label-sm text-on-surface-variant">ACTIVE INFERENCE ENGINE</span>
      </div>
      <div class="flex items-center space-x-1 text-label-sm font-label-sm text-tertiary">
        <span class="h-1.5 w-1.5 rounded-full bg-tertiary"></span>
        <span>Online</span>
      </div>
    </div>

    <!-- Ribbon Container -->
    <div class="flex space-x-2 overflow-x-auto no-scrollbar pb-2 pt-1 items-center">
      <button class="model-pill flex-shrink-0 px-3 py-2 rounded {activeModel === '7B-MoE' ? 'bg-primary-container/20 border-primary text-primary glow-active' : 'bg-surface-container border-outline-variant/40 text-on-surface-variant hover:border-primary/50'} border transition-colors flex items-center space-x-2" onclick={() => switchModel('7B-MoE', '7B Mixture-of-Experts', 'Cerebras Ultra-Core')}>
        <span class="material-symbols-outlined text-sm" data-icon="speed">speed</span>
        <span class="text-label-md font-label-md">Text-Extract-XL (7B MoE)</span>
        {#if activeModel === '7B-MoE'}
        <span class="bg-primary/20 text-primary border border-primary/30 px-1.5 py-0.2 rounded text-label-sm font-label-sm ml-2">Active</span>
        {/if}
      </button>

      <button class="model-pill flex-shrink-0 px-3 py-2 rounded {activeModel === 'Audio-Transcribe' ? 'bg-primary-container/20 border-primary text-primary glow-active' : 'bg-surface-container border-outline-variant/40 text-on-surface-variant hover:border-primary/50'} border transition-colors flex items-center space-x-2" onclick={() => switchModel('Audio-Transcribe', 'Whisper-v3', 'OpenAI Cloud')}>
        <span class="material-symbols-outlined text-sm" data-icon="mic">mic</span>
        <span class="text-label-md font-label-md">Audio-Transcribe</span>
      </button>

      <button class="model-pill flex-shrink-0 px-3 py-2 rounded {activeModel === 'Code-Gen-4B' ? 'bg-primary-container/20 border-primary text-primary glow-active' : 'bg-surface-container border-outline-variant/40 text-on-surface-variant hover:border-primary/50'} border transition-colors flex items-center space-x-2" onclick={() => switchModel('Code-Gen-4B', '4B-Dense', 'Local TensorRT')}>
        <span class="material-symbols-outlined text-sm" data-icon="terminal">terminal</span>
        <span class="text-label-md font-label-md">Code-Gen-4B</span>
      </button>
    </div>

    <!-- Active Model Meta Bar -->
    <div class="flex items-center justify-between px-3 py-1.5 bg-surface-container-low rounded border border-outline-variant/30 text-label-sm font-label-sm">
      <div class="flex items-center space-x-3 text-on-surface-variant">
        <span>Provider: <strong class="text-on-surface font-label-sm">{modelProvider}</strong></span>
        <span class="text-outline-variant">|</span>
        <span>Arch: <strong class="text-primary font-label-sm">{modelArch}</strong></span>
      </div>
      <div class="text-secondary font-label-sm">FP8 Quantized</div>
    </div>
  </section>

  <!-- 2. WORKBENCH & INPUT AREA -->
  <section class="bg-surface-container rounded-xl p-3 sm:p-4 border border-outline-variant/30 space-y-3">
    <!-- Section header with sample presets -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
      <div class="flex items-center space-x-2">
        <span class="material-symbols-outlined text-secondary text-base" data-icon="edit_note">edit_note</span>
        <span class="text-label-md font-label-md text-on-surface font-semibold">PROMPT CONSOLE &amp; PAYLOAD</span>
      </div>
      <!-- Quick Sample Presets -->
      <div class="flex items-center space-x-1.5 overflow-x-auto no-scrollbar py-0.5">
        <span class="text-label-sm font-label-sm text-outline shrink-0">Presets:</span>
        <button class="px-2 py-1 rounded bg-surface-container-high border border-outline-variant/40 hover:border-secondary text-on-surface-variant hover:text-on-surface text-label-sm font-label-sm transition-all shrink-0 active:scale-95" onclick={() => loadPreset('support')}>
          Customer Support QA
        </button>
        <button class="px-2 py-1 rounded bg-surface-container-high border border-outline-variant/40 hover:border-secondary text-on-surface-variant hover:text-on-surface text-label-sm font-label-sm transition-all shrink-0 active:scale-95" onclick={() => loadPreset('ocr')}>
          Receipt OCR Parse
        </button>
        <button class="px-2 py-1 rounded bg-surface-container-high border border-outline-variant/40 hover:border-secondary text-on-surface-variant hover:text-on-surface text-label-sm font-label-sm transition-all shrink-0 active:scale-95" onclick={() => loadPreset('sentiment')}>
          Sentiment &amp; Intent
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
        <button class="text-outline hover:text-error text-label-sm font-label-sm flex items-center space-x-1 transition-colors" onclick={clearPrompt}>
          <span class="material-symbols-outlined text-xs" data-icon="backspace">backspace</span>
          <span>Clear</span>
        </button>
      </div>
      <textarea bind:value={promptText} class="w-full bg-transparent p-3 text-body-md font-body-md text-on-surface placeholder-outline focus:outline-none resize-none border-0 focus:ring-0 leading-relaxed font-code-md text-code-md" placeholder="Enter structured prompt or paste raw payload text for extraction..." rows="4"></textarea>

      <!-- Textarea utility bar -->
      <div class="flex items-center justify-between px-3 py-1.5 border-t border-outline-variant/20 text-label-sm font-label-sm bg-surface-container-low/40">
        <span class="text-outline">Tokens: <strong class="text-on-surface font-label-sm">{Math.floor(promptText.length / 4)} / 512</strong></span>
        <span class="text-outline">Mode: <strong class="text-secondary font-label-sm">Entity &amp; Sentiment Extraction</strong></span>
      </div>
    </div>

    <!-- 3. INTERACTIVE HYPERPARAMETER CONTROL DRAWER -->
    <div class="rounded-lg bg-surface-container-low border border-outline-variant/40 overflow-hidden">
      <!-- Accordion trigger -->
      <button aria-expanded={drawerOpen} aria-controls="drawer-body" class="w-full px-3 py-2.5 flex items-center justify-between bg-surface-container-high/40 hover:bg-surface-container-high/70 transition-colors" onclick={toggleDrawer}>
        <div class="flex items-center space-x-2">
          <span class="material-symbols-outlined text-primary text-base" data-icon="tune">tune</span>
          <span class="text-label-md font-label-md text-on-surface font-medium">Hyperparameters &amp; Inference Controls</span>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-label-sm font-label-sm text-on-surface-variant">T={hyperParams.temp.toFixed(2)} | P={hyperParams.topp.toFixed(2)} | Max={hyperParams.tokens}</span>
          <span class="material-symbols-outlined text-on-surface-variant transition-transform duration-200 {drawerOpen ? '' : 'rotate-[-90deg]'}" data-icon="expand_more">expand_more</span>
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
            <input class="w-full" id="temp-slider" max="1.5" min="0.0" oninput={(e) => updateParam('temp', e)} step="0.05" type="range" value={hyperParams.temp}/>
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
            <input class="w-full" id="topp-slider" max="1.0" min="0.1" oninput={(e) => updateParam('topp', e)} step="0.05" type="range" value={hyperParams.topp}/>
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
            <input class="w-full" id="tokens-slider" max="2048" min="64" oninput={(e) => updateParam('tokens', e)} step="64" type="range" value={hyperParams.tokens}/>
            <div class="flex justify-between text-[9px] text-outline font-label-sm">
              <span>64 tokens</span>
              <span>2048 tokens</span>
            </div>
          </div>
        </div>

        <!-- Feature Toggles -->
        <div class="pt-2 border-t border-outline-variant/20 grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div class="flex items-center justify-between p-2 rounded bg-surface-container border border-outline-variant/30">
            <div class="flex flex-col">
              <span class="text-label-sm font-label-sm text-on-surface font-semibold">Streaming Output</span>
              <span class="text-body-sm font-body-sm text-outline">SSE token stream emission</span>
            </div>
            <button aria-label="Toggle Streaming Output" class="w-10 h-6 flex items-center rounded-full p-1 cursor-pointer transition-colors duration-200 {streamEnabled ? 'bg-primary-container' : 'bg-surface-container-high'}" onclick={() => toggleSwitch('stream')}>
              <div class="bg-white w-4 h-4 rounded-full shadow-md transition-transform duration-200 {streamEnabled ? 'translate-x-4' : 'translate-x-0'}"></div>
            </button>
          </div>
          <div class="flex items-center justify-between p-2 rounded bg-surface-container border border-outline-variant/30">
            <div class="flex flex-col">
              <span class="text-label-sm font-label-sm text-on-surface font-semibold">Return Confidence Scores</span>
              <span class="text-body-sm font-body-sm text-outline">Logprobs &amp; entity certainty tags</span>
            </div>
            <button aria-label="Toggle Confidence Scores" class="w-10 h-6 flex items-center rounded-full p-1 cursor-pointer transition-colors duration-200 {confEnabled ? 'bg-primary-container' : 'bg-surface-container-high'}" onclick={() => toggleSwitch('conf')}>
              <div class="bg-white w-4 h-4 rounded-full shadow-md transition-transform duration-200 {confEnabled ? 'translate-x-4' : 'translate-x-0'}"></div>
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
        <span class="material-symbols-outlined text-outline text-base" data-icon="hourglass_top">hourglass_top</span>
        <span class="text-outline">Est. Latency: <strong class="text-on-surface font-label-sm">~380ms</strong></span>
      </div>
      <div class="flex items-center space-x-1.5">
        <span class="material-symbols-outlined text-secondary text-base" data-icon="toll">toll</span>
        <span class="text-outline">Cost: <strong class="text-secondary font-label-sm">12 credits/run</strong></span>
      </div>
    </div>

    <!-- Primary Run Trigger Button -->
    <button class="w-full sm:w-auto min-w-[200px] h-11 px-5 rounded-lg bg-primary text-on-primary-fixed font-label-md font-bold text-label-md flex items-center justify-center space-x-2 shadow-lg shadow-primary/20 hover:brightness-110 active:scale-95 transition-all {isRunning ? 'opacity-80 pointer-events-none' : ''}" onclick={runInference}>
      <span class="material-symbols-outlined text-xl {isRunning ? 'animate-spin' : ''}" data-icon={isRunning ? "sync" : "play_arrow"}>{isRunning ? "sync" : "play_arrow"}</span>
      <span>{runLabel}</span>
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
          <span class="text-label-sm font-label-sm px-2 py-0.5 rounded bg-tertiary-container/30 text-tertiary border border-tertiary/40 {speedChipClass} transition-shadow">{speedChipText}</span>
        </div>
        <span class="text-label-sm font-label-sm text-outline">Latency: 342ms</span>
      </div>

      <!-- Output Tabs -->
      <div class="flex space-x-2 overflow-x-auto no-scrollbar pt-1" role="tablist">
        <button role="tab" aria-selected={activeTab === 'json'} class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'json' ? 'border-primary text-primary' : 'border-transparent text-outline'}" onclick={() => switchTab('json')}>
          <span class="material-symbols-outlined text-sm" data-icon="data_object">data_object</span>
          <span>Parsed JSON Output</span>
        </button>
        <button role="tab" aria-selected={activeTab === 'tokens'} class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'tokens' ? 'border-primary text-primary' : 'border-transparent text-outline'}" onclick={() => switchTab('tokens')}>
          <span class="material-symbols-outlined text-sm" data-icon="scatter_plot">scatter_plot</span>
          <span>Raw Tokens &amp; Conf</span>
        </button>
        <button role="tab" aria-selected={activeTab === 'tree'} class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'tree' ? 'border-primary text-primary' : 'border-transparent text-outline'}" onclick={() => switchTab('tree')}>
          <span class="material-symbols-outlined text-sm" data-icon="account_tree">account_tree</span>
          <span>Interactive Tree</span>
        </button>
        <button role="tab" aria-selected={activeTab === 'embed'} class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'embed' ? 'border-primary text-primary' : 'border-transparent text-outline'}" onclick={() => switchTab('embed')}>
          <span class="material-symbols-outlined text-sm" data-icon="code">code</span>
          <span>Embed Snippet</span>
        </button>
      </div>
    </div>

    <!-- Tab Views -->
    <div class="p-3 sm:p-4 min-h-[220px]">
      {#if activeTab === 'json'}
      <div class="space-y-2">
        <div class="flex items-center justify-between text-label-sm font-label-sm text-outline">
          <span>payload_response.json</span>
          <span class="text-tertiary">Schema: valid</span>
        </div>
        <div class="p-3 bg-surface-container-lowest rounded-lg border border-outline-variant/40 overflow-x-auto">
          <pre class="text-code-sm font-code-sm text-on-surface leading-5 font-mono"><span class="text-outline">{'{\n  "intent": "refund_request",\n  "confidence": 0.984,\n  "urgency_level": "critical",\n  "entities": [\n    {\n      "entity": "order_id",\n      "value": "48192",\n      "confidence": 0.991,\n      "span": [20, 26]\n    },\n    {\n      "entity": "defect_type",\n      "value": "screen_cracked_unresponsive",\n      "confidence": 0.972\n    },\n    {\n      "entity": "shipping_address",\n      "value": "742 Evergreen Terr",\n      "confidence": 0.943\n    }\n  ],\n  "sentiment_score": -0.82,\n  "suggested_action": "auto_generate_return_label_tier1"\n}'}</span></pre>
        </div>
      </div>
      {/if}

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

      {#if activeTab === 'tree'}
      <div class="space-y-2 font-code-sm text-code-sm">
        <div class="p-3 bg-surface-container-lowest rounded-lg border border-outline-variant/40 space-y-2 text-on-surface">
          <div class="flex items-center space-x-1 cursor-pointer text-primary">
            <span class="material-symbols-outlined text-sm" data-icon="folder_open">folder_open</span>
            <span>Root: ExtractionResponse</span>
          </div>
          <div class="pl-4 border-l border-outline-variant/40 space-y-1.5">
            <div class="text-tertiary">├─ intent: "refund_request" <span class="text-outline text-label-sm">(float 0.984)</span></div>
            <div class="text-secondary">├─ urgency: "critical"</div>
            <div>
              <span class="text-primary">└─ entities [3 items]</span>
              <div class="pl-4 border-l border-outline-variant/40 space-y-1 text-on-surface-variant">
                <div>[0] &lcub; type: "order_id", val: "48192" &rcub;</div>
                <div>[1] &lcub; type: "defect", val: "screen_cracked" &rcub;</div>
                <div>[2] &lcub; type: "address", val: "742 Evergreen Terr" &rcub;</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      {/if}

      {#if activeTab === 'embed'}
      <div class="space-y-2">
        <div class="text-label-sm font-label-sm text-outline">COPY EMBED SCRIPT OR SDK INTEGRATION:</div>
        <div class="p-3 bg-surface-container-lowest rounded-lg border border-outline-variant/40 overflow-x-auto font-code-sm text-code-sm text-secondary leading-relaxed">
          <code>&lt;script src="https://cdn.modelops.io/v2.4/embed.js"&gt;&lt;/script&gt;<br/>
&lt;model-runner model="Text-Extract-XL" endpoint="cerebras-7b" temp="0.7"&gt;&lt;/model-runner&gt;</code>
        </div>
      </div>
      {/if}
    </div>

    <!-- Quick Action Bar -->
    <div class="p-3 bg-surface-container-low border-t border-outline-variant/30 flex items-center justify-between flex-wrap gap-2">
      <div class="flex items-center space-x-2">
        <button class="px-2.5 py-1.5 rounded bg-surface-container border border-outline-variant/40 hover:border-primary text-on-surface hover:text-primary text-label-sm font-label-sm flex items-center space-x-1.5 transition-all active:scale-95" onclick={copyOutput}>
          <span class="material-symbols-outlined text-sm" data-icon="content_copy">content_copy</span>
          <span>{copyBtnText}</span>
        </button>
        <button class="px-2.5 py-1.5 rounded bg-surface-container border border-outline-variant/40 hover:border-secondary text-on-surface hover:text-secondary text-label-sm font-label-sm flex items-center space-x-1.5 transition-all active:scale-95" onclick={runInference}>
          <span class="material-symbols-outlined text-sm" data-icon="casino">casino</span>
          <span>Re-run with Noise</span>
        </button>
      </div>
      <div class="flex items-center space-x-2">
        <button class="px-2.5 py-1.5 rounded bg-surface-container border border-outline-variant/40 hover:border-tertiary text-on-surface hover:text-tertiary text-label-sm font-label-sm flex items-center space-x-1.5 transition-all active:scale-95" onclick={exportCurl}>
          <span class="material-symbols-outlined text-sm" data-icon="terminal">terminal</span>
          <span>Export cURL</span>
        </button>
        <button class="px-2.5 py-1.5 rounded bg-primary-container/20 border border-primary/40 text-primary text-label-sm font-label-sm flex items-center space-x-1.5 hover:bg-primary-container/30 transition-all active:scale-95" onclick={shareEmbed}>
          <span class="material-symbols-outlined text-sm" data-icon="share">share</span>
          <span>Share</span>
        </button>
      </div>
    </div>
  </section>
</div>

<style>
  .material-symbols-outlined {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
    display: inline-block;
    vertical-align: middle;
    line-height: 1;
  }
  .glow-active {
    box-shadow: 0 0 0 1px #8083ff, 0 4px 20px -2px rgba(128, 131, 255, 0.25);
  }
  .glow-cyan {
    box-shadow: 0 0 0 1px #4cd7f6, 0 4px 16px -2px rgba(76, 215, 246, 0.2);
  }
  input[type=range] {
    -webkit-appearance: none;
    background: transparent;
  }
  input[type=range]::-webkit-slider-thumb {
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
  input[type=range]::-webkit-slider-runnable-track {
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
</style>
