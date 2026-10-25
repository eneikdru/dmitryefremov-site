<script lang="ts">
  let temp = $state(0.70);
  let topp = $state(0.95);
  let tokens = $state(512);

  let drawerOpen = $state(true);

  let modelProvider = $state("Cerebras Ultra-Core");
  let modelArch = $state("7B Mixture-of-Experts");
  let activeModel = $state("Text-Extract-XL");

  let promptInput = $state('{\n  "user_id": "usr_9402a",\n  "channel": "mobile_in_app_chat",\n  "transcript": "Hello, my order #48192 arrived yesterday but the screen is completely cracked and unresponsive. I need a full immediate refund or expedited replacement sent to 742 Evergreen Terr right away. This is unacceptable."\n}');

  let activeTab = $state('json');

  let isInferring = $state(false);
  let speedChipText = $state('114 t/s • eos: stop');
  let speedChipGlow = $state(false);

  let copyText = $state('Copy JSON');

  const samplePresets: Record<string, string> = {
    support: `{\n  "user_id": "usr_9402a",\n  "channel": "mobile_in_app_chat",\n  "transcript": "Hello, my order #48192 arrived yesterday but the screen is completely cracked and unresponsive. I need a full immediate refund or expedited replacement sent to 742 Evergreen Terr right away. This is unacceptable."\n}`,
    ocr: `{\n  "source_document": "scan_store_receipt_9921.jpg",\n  "raw_ocr_lines": [\n    "WALMART SUPERCENTER #3210",\n    "DATE: 2025-05-12 14:32",\n    "ITEMS: 4K MONITOR $299.99 TAX $24.00",\n    "TOTAL PAID: $323.99 VISA *9912"\n  ]\n}`,
    sentiment: `{\n  "source_feed": "X/Twitter Realtime",\n  "handle": "@dev_builder",\n  "text": "The new 7B-MoE inference throughput is blazing fast! Went from 35ms to 12ms latency on Cerebras nodes. Incredible release!"\n}`
  };

  function toggleDrawer() {
    drawerOpen = !drawerOpen;
  }

  function switchModel(name: string, arch: string, provider: string) {
    activeModel = name;
    modelArch = arch;
    modelProvider = provider;
  }

  function loadPreset(key: string) {
    promptInput = samplePresets[key];
  }

  function clearPrompt() {
    promptInput = '';
  }

  function switchTab(tabKey: string) {
    activeTab = tabKey;
  }

  function runInference() {
    if (isInferring) return;
    isInferring = true;

    setTimeout(() => {
      isInferring = false;
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
    copyText = 'Copied!';
    setTimeout(() => copyText = 'Copy JSON', 1500);
  }

  function rerunNoise() {
    runInference();
  }

  function exportCurl() {
    alert("cURL snippet copied: curl -X POST https://api.modelops.io/v2.4/extract -H 'Authorization: Bearer ops_live_key'");
  }

  function shareEmbed() {
    alert("Share URL: https://modelops.io/play/share/7b-moe-48192");
  }

  let streamingOutputActive = $state(false);
  let metadataOutputActive = $state(true);

  function toggleStreamingOutput() {
    streamingOutputActive = !streamingOutputActive;
  }
  function toggleMetadataOutput() {
    metadataOutputActive = !metadataOutputActive;
  }
</script>

<main class="max-w-7xl mx-auto px-4 pt-4 pb-12 space-y-4">
<!-- 1. ACTIVE MODEL SELECTOR HORIZONTAL RIBBON -->
<section class="space-y-2">
<div class="flex items-center justify-between">
<div class="flex items-center space-x-1.5">
<span class="material-symbols-outlined text-primary text-base" data-icon="psychology">psychology</span>
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
<button class="model-pill flex-shrink-0 px-3 py-2 rounded {activeModel === 'Vision-Embed-v2' ? 'bg-primary-container/20 border border-primary text-primary glow-active active' : 'bg-surface-container border border-outline-variant/40 text-on-surface-variant hover:border-primary/50 transition-colors'} flex items-center space-x-2" onclick={() => switchModel('Vision-Embed-v2', '4B-ViT', 'Google Cloud TPU')}>
<span class="material-symbols-outlined text-sm" data-icon="image">image</span>
<span class="text-label-md font-label-md">Vision-Embed-v2</span>
</button>
<!-- Active Pill (Selected in Prompt) -->
<button class="model-pill flex-shrink-0 px-3 py-2 rounded {activeModel === 'Text-Extract-XL' ? 'bg-primary-container/20 border border-primary text-primary glow-active active' : 'bg-surface-container border border-outline-variant/40 text-on-surface-variant hover:border-primary/50 transition-colors'} flex items-center space-x-2" onclick={() => switchModel('Text-Extract-XL', '7B-MoE', 'Cerebras Ultra-Core')}>
<span class="material-symbols-outlined text-sm" data-icon="text_fields">text_fields</span>
<span class="text-label-md font-label-md font-semibold">Text-Extract-XL</span>
<span class="bg-primary/20 text-primary border border-primary/30 px-1.5 py-0.2 rounded text-label-sm font-label-sm">7B-MoE</span>
</button>
<!-- Inactive Pill 2 -->
<button class="model-pill flex-shrink-0 px-3 py-2 rounded {activeModel === 'Vision-Embed-v2' ? 'bg-primary-container/20 border border-primary text-primary glow-active active' : 'bg-surface-container border border-outline-variant/40 text-on-surface-variant hover:border-primary/50 transition-colors'} flex items-center space-x-2" onclick={() => switchModel('Audio-Transcribe', 'Whisper-L', 'AWS Inferentia')}>
<span class="material-symbols-outlined text-sm" data-icon="graphic_eq">graphic_eq</span>
<span class="text-label-md font-label-md">Audio-Transcribe</span>
</button>
<!-- Inactive Pill 3 -->
<button class="model-pill flex-shrink-0 px-3 py-2 rounded {activeModel === 'Vision-Embed-v2' ? 'bg-primary-container/20 border border-primary text-primary glow-active active' : 'bg-surface-container border border-outline-variant/40 text-on-surface-variant hover:border-primary/50 transition-colors'} flex items-center space-x-2" onclick={() => switchModel('Code-Gen-4B', '4B-Dense', 'Local TensorRT')}>
<span class="material-symbols-outlined text-sm" data-icon="terminal">terminal</span>
<span class="text-label-md font-label-md">Code-Gen-4B</span>
</button>
</div>
<!-- Active Model Meta Bar -->
<div class="flex items-center justify-between px-3 py-1.5 bg-surface-container-low rounded border border-outline-variant/30 text-label-sm font-label-sm" id="model-meta-bar">
<div class="flex items-center space-x-3 text-on-surface-variant">
<span>Provider: <strong class="text-on-surface font-label-sm" id="model-provider">{modelProvider}</strong></span>
<span class="text-outline-variant">|</span>
<span>Arch: <strong class="text-primary font-label-sm" id="model-arch">{modelArch}</strong></span>
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
<textarea class="w-full bg-transparent p-3 text-body-md font-body-md text-on-surface placeholder-outline focus:outline-none resize-none border-0 focus:ring-0 leading-relaxed font-code-md text-code-md" id="prompt-input" placeholder="Enter structured prompt or paste raw payload text for extraction..." rows="4" bind:value={promptInput} onkeydown={handleKeydown}></textarea>
<!-- Textarea utility bar -->
<div class="flex items-center justify-between px-3 py-1.5 border-t border-outline-variant/20 text-label-sm font-label-sm bg-surface-container-low/40">
<span class="text-outline" id="char-token-counter">Tokens: <strong class="text-on-surface font-label-sm">{Math.floor(promptInput.length / 4)} / {tokens}</strong></span>
<span class="text-outline">Mode: <strong class="text-secondary font-label-sm">Entity &amp; Sentiment Extraction</strong></span>
</div>
</div>
<!-- 3. INTERACTIVE HYPERPARAMETER CONTROL DRAWER -->
<div class="rounded-lg bg-surface-container-low border border-outline-variant/40 overflow-hidden">
<!-- Accordion trigger -->
<button class="w-full px-3 py-2.5 flex items-center justify-between bg-surface-container-high/40 hover:bg-surface-container-high/70 transition-colors" onclick={toggleDrawer}>
<div class="flex items-center space-x-2">
<span class="material-symbols-outlined text-primary text-base" data-icon="tune">tune</span>
<span class="text-label-md font-label-md text-on-surface font-medium">Hyperparameters &amp; Inference Controls</span>
</div>
<div class="flex items-center space-x-2">
<span class="text-label-sm font-label-sm text-on-surface-variant" id="active-summary">T={temp.toFixed(2)} | P={topp.toFixed(2)} | Max={tokens}</span>
<span class="material-symbols-outlined text-on-surface-variant transition-transform duration-200" style={drawerOpen ? 'transform: rotate(0deg);' : 'transform: rotate(-90deg);'} data-icon="expand_more" id="drawer-arrow">expand_more</span>
</div>
</button>
<!-- Collapsible body -->
<div class="p-3 sm:p-4 space-y-4 border-t border-outline-variant/30 {drawerOpen ? '' : 'hidden'}" id="drawer-body">
<div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
<!-- Temperature Slider -->
<div class="space-y-1.5">
<div class="flex justify-between items-center">
<label class="text-label-sm font-label-sm text-on-surface-variant" for="temp-slider">Temperature</label>
<span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-surface-bright text-primary border border-outline-variant/40" id="temp-val">0.70</span>
</div>
<input class="w-full" id="temp-slider" max="1.5" min="0.0"  step="0.05" type="range" bind:value={temp}/>
<div class="flex justify-between text-[9px] text-outline font-label-sm">
<span>Deterministic</span>
<span>Creative</span>
</div>
</div>
<!-- Top-P Slider -->
<div class="space-y-1.5">
<div class="flex justify-between items-center">
<label class="text-label-sm font-label-sm text-on-surface-variant" for="topp-slider">Top-P (Nucleus)</label>
<span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-surface-bright text-secondary border border-outline-variant/40" id="topp-val">0.95</span>
</div>
<input class="w-full" id="topp-slider" max="1.0" min="0.1"  step="0.05" type="range" bind:value={topp}/>
<div class="flex justify-between text-[9px] text-outline font-label-sm">
<span>0.1 (Focused)</span>
<span>1.0 (Full Corpus)</span>
</div>
</div>
<!-- Max Tokens Slider -->
<div class="space-y-1.5">
<div class="flex justify-between items-center">
<label class="text-label-sm font-label-sm text-on-surface-variant" for="tokens-slider">Max Output Tokens</label>
<span class="text-label-sm font-label-sm px-1.5 py-0.5 rounded bg-surface-bright text-tertiary border border-outline-variant/40" id="tokens-val">512</span>
</div>
<input class="w-full" id="tokens-slider" max="2048" min="64"  step="64" type="range" bind:value={tokens}/>
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
<button class="w-10 h-6 flex items-center bg-primary-container rounded-full p-1 cursor-pointer transition-colors duration-200" id="toggle-stream" role="switch" aria-checked={streamingOutputActive} aria-label="Streaming Output" onclick={toggleStreamingOutput}>
<div class="bg-white w-4 h-4 rounded-full shadow-md transform translate-x-4 transition-transform duration-200"></div>
</button>
</div>
<!-- Toggle 2: Return Confidence Scores -->
<div class="flex items-center justify-between p-2 rounded bg-surface-container border border-outline-variant/30">
<div class="flex flex-col">
<span class="text-label-sm font-label-sm text-on-surface font-semibold">Return Confidence Scores</span>
<span class="text-body-sm font-body-sm text-outline">Logprobs &amp; entity certainty tags</span>
</div>
<button class="w-10 h-6 flex items-center bg-primary-container rounded-full p-1 cursor-pointer transition-colors duration-200" id="toggle-conf" role="switch" aria-checked={metadataOutputActive} aria-label="Return Confidence Scores" onclick={toggleMetadataOutput}>
<div class="bg-white w-4 h-4 rounded-full shadow-md transform translate-x-4 transition-transform duration-200"></div>
</button>
</div>
</div>
</div>
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
<button class="w-full sm:w-auto min-w-[200px] h-11 px-5 rounded-lg bg-primary text-on-primary-fixed font-label-md font-bold text-label-md flex items-center justify-center space-x-2 shadow-lg shadow-primary/20 hover:brightness-110 active:scale-95 transition-all" id="run-trigger" >
<span class="material-symbols-outlined text-xl" data-icon="play_arrow" id="run-icon">play_arrow</span>
<span id="run-label">RUN MODEL</span>
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
<span class="text-label-sm font-label-sm px-2 py-0.5 rounded bg-tertiary-container/30 text-tertiary border border-tertiary/40 {speedChipGlow ? 'glow-cyan' : ''}" id="speed-chip">{speedChipText}</span>
</div>
<!-- Execution Timestamp -->
<span class="text-label-sm font-label-sm text-outline">Latency: 342ms</span>
</div>
<!-- Output Tabs -->
<div class="flex space-x-2 overflow-x-auto no-scrollbar pt-1">
<button class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'json' ? 'border-primary text-primary active' : 'border-transparent text-outline hover:text-on-surface'}" onclick={() => switchTab('json')}>
<span class="material-symbols-outlined text-sm" data-icon="data_object">data_object</span>
<span>Parsed JSON Output</span>
</button>
<button class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'tokens' ? 'border-primary text-primary active' : 'border-transparent text-outline hover:text-on-surface'}" onclick={() => switchTab('tokens')}>
<span class="material-symbols-outlined text-sm" data-icon="bubble_chart">bubble_chart</span>
<span>Raw Tokens &amp; Confidence</span>
</button>
<button class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'tokens' ? 'border-primary text-primary active' : 'border-transparent text-outline hover:text-on-surface'}" onclick={() => switchTab('tree')}>
<span class="material-symbols-outlined text-sm" data-icon="account_tree">account_tree</span>
<span>Interactive Tree</span>
</button>
<button class="output-tab pb-2 border-b-2 text-label-sm font-label-sm flex items-center space-x-1 shrink-0 {activeTab === 'tokens' ? 'border-primary text-primary active' : 'border-transparent text-outline hover:text-on-surface'}" onclick={() => switchTab('embed')}>
<span class="material-symbols-outlined text-sm" data-icon="code">code</span>
<span>Embed Snippet (&lt;/&gt;)</span>
</button>
</div>
</div>
<!-- Tab Content Area -->
<div class="p-3 sm:p-4">
<!-- View 1: Parsed JSON Output (Default) -->
<div class="space-y-3 {activeTab === 'json' ? '' : 'hidden'}" id="tab-view-json">
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
<pre class="text-code-sm font-code-sm text-on-surface leading-5 font-mono"><span class="text-outline">{"{"}</span>
  <span class="text-secondary">"intent"</span>: <span class="text-tertiary">"refund_request"</span>,
  <span class="text-secondary">"confidence"</span>: <span class="text-primary">0.984</span>,
  <span class="text-secondary">"urgency_level"</span>: <span class="text-error">"critical"</span>,
  <span class="text-secondary">"entities"</span>: <span class="text-outline">[</span>
    <span class="text-outline">{"{"}</span>
      <span class="text-secondary">"entity"</span>: <span class="text-tertiary">"order_id"</span>,
      <span class="text-secondary">"value"</span>: <span class="text-primary-fixed">"48192"</span>,
      <span class="text-secondary">"confidence"</span>: <span class="text-primary">0.991</span>,
      <span class="text-secondary">"span"</span>: <span class="text-outline">[20, 26]</span>
    <span class="text-outline">{"}"}</span>,
    <span class="text-outline">{"{"}</span>
      <span class="text-secondary">"entity"</span>: <span class="text-tertiary">"defect_type"</span>,
      <span class="text-secondary">"value"</span>: <span class="text-primary-fixed">"screen_cracked_unresponsive"</span>,
      <span class="text-secondary">"confidence"</span>: <span class="text-primary">0.972</span>
    <span class="text-outline">{"}"}</span>,
    <span class="text-outline">{"{"}</span>
      <span class="text-secondary">"entity"</span>: <span class="text-tertiary">"shipping_address"</span>,
      <span class="text-secondary">"value"</span>: <span class="text-primary-fixed">"742 Evergreen Terr"</span>,
      <span class="text-secondary">"confidence"</span>: <span class="text-primary">0.943</span>
    <span class="text-outline">{"}"}</span>
  <span class="text-outline">]</span>,
  <span class="text-secondary">"sentiment_score"</span>: <span class="text-error">-0.82</span>,
  <span class="text-secondary">"suggested_action"</span>: <span class="text-tertiary">"auto_generate_return_label_tier1"</span>
<span class="text-outline">{"}"}</span></pre>
</div>
</div>
<!-- View 2: Raw Tokens & Confidence (Hidden initially) -->
<div class="space-y-3 {activeTab === 'tokens' ? '' : 'hidden'}" id="tab-view-tokens">
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
<!-- View 3: Interactive Tree (Hidden initially) -->
<div class="hidden space-y-2 font-code-sm text-code-sm" id="tab-view-tree">
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
<div>[0] {"{"} type: "order_id", val: "48192" {"}"}</div>
<div>[1] {"{"} type: "defect", val: "screen_cracked" {"}"}</div>
<div>[2] {"{"} type: "address", val: "742 Evergreen Terr" {"}"}</div>
</div>
</div>
</div>
</div>
</div>
<!-- View 4: Embed Snippet (Hidden initially) -->
<div class="hidden space-y-2" id="tab-view-embed">
<div class="text-label-sm font-label-sm text-outline">COPY EMBED SCRIPT OR SDK INTEGRATION:</div>
<div class="p-3 bg-surface-container-lowest rounded-lg border border-outline-variant/40 overflow-x-auto font-code-sm text-code-sm text-secondary leading-relaxed">
<code>&lt;script src="https://cdn.modelops.io/v2.4/embed.js"&gt;&lt;/script&gt;<br/>
&lt;model-runner model="Text-Extract-XL" endpoint="cerebras-7b" temp="0.7"&gt;&lt;/model-runner&gt;</code>
</div>
</div>
</div>
<!-- Quick Action Bar -->
<div class="p-3 bg-surface-container-low border-t border-outline-variant/30 flex items-center justify-between flex-wrap gap-2">
<div class="flex items-center space-x-2">
<button class="px-2.5 py-1.5 rounded bg-surface-container border border-outline-variant/40 hover:border-primary text-on-surface hover:text-primary text-label-sm font-label-sm flex items-center space-x-1.5 transition-all active:scale-95" id="copy-btn" onclick={copyOutput}>
<span class="material-symbols-outlined text-sm" data-icon="content_copy">content_copy</span>
<span id="copy-text">{copyText}</span>
</button>
<button class="px-2.5 py-1.5 rounded bg-surface-container border border-outline-variant/40 hover:border-secondary text-on-surface hover:text-secondary text-label-sm font-label-sm flex items-center space-x-1.5 transition-all active:scale-95" onclick={rerunNoise}>
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
</main>

<nav class="fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-2 py-1 h-16 bg-surface-container-lowest/90 dark:bg-surface-container-lowest/90 backdrop-blur-md border-t border-outline-variant/20 dark:border-outline-variant/20">
<!-- Item 1: Playground (ACTIVE) -->
<a class="flex flex-col items-center justify-center text-primary dark:text-primary py-1 px-3 border-t-2 border-primary dark:border-primary rounded-none active:scale-95 transition-transform duration-100" href="/">
<span class="material-symbols-outlined text-xl" data-icon="tune">tune</span>
<span class="text-label-sm font-label-sm mt-0.5">Playground</span>
</a>
<!-- Item 2: Metrics (INACTIVE) -->
<a class="flex flex-col items-center justify-center text-outline dark:text-outline py-1 px-3 hover:text-on-surface dark:hover:text-on-surface transition-colors duration-150 active:scale-95" href="/">
<span class="material-symbols-outlined text-xl" data-icon="speed">speed</span>
<span class="text-label-sm font-label-sm mt-0.5">Metrics</span>
</a>
<!-- Item 3: Inspect (INACTIVE) -->
<a class="flex flex-col items-center justify-center text-outline dark:text-outline py-1 px-3 hover:text-on-surface dark:hover:text-on-surface transition-colors duration-150 active:scale-95" href="/">
<span class="material-symbols-outlined text-xl" data-icon="data_object">data_object</span>
<span class="text-label-sm font-label-sm mt-0.5">Inspect</span>
</a>
<!-- Item 4: Embed (INACTIVE) -->
<a class="flex flex-col items-center justify-center text-outline dark:text-outline py-1 px-3 hover:text-on-surface dark:hover:text-on-surface transition-colors duration-150 active:scale-95" href="/">
<span class="material-symbols-outlined text-xl" data-icon="code">code</span>
<span class="text-label-sm font-label-sm mt-0.5">Embed</span>
</a>
</nav>
