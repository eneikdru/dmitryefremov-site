<script lang="ts">
  import { onMount } from 'svelte';

  export let theme: 'dark' | 'light' = 'dark';

  // Interactive states
  let activePreset: 'segmentation' | 'synthesis' | 'audio' | 'embedding' = 'segmentation';
  let confidenceThreshold: number = 0.78;
  let iouOverlap: number = 0.45;
  let edgeRefinement: boolean = true;
  let gpuAcceleration: boolean = true;
  let drawMaskPolygons: boolean = false;
  let floatLayerState: 'mask' | 'boxes' | 'heatmap' = 'mask';
  let promptText: string = 'studio lighting, reflective surfaces, micro-textures';

  // Mock telemetry data that changes based on states (to simulate interactivity)
  $: latency = (gpuAcceleration ? 18.4 : 54.2) + (edgeRefinement ? 4.1 : 0);
  $: vram = (gpuAcceleration ? 1.2 : 0.8) + (drawMaskPolygons ? 0.1 : 0);
  $: engineStr = gpuAcceleration ? 'WebGPU' : 'WASM';
  $: confidenceScore = (confidenceThreshold * 100 + (edgeRefinement ? 2.1 : -1.2)).toFixed(1);

  // Formatter for slider display
  $: formatConf = confidenceThreshold.toFixed(2);
  $: formatIoU = iouOverlap.toFixed(2);
</script>

<div class="w-full max-w-md mx-auto pt-4 px-4 flex flex-col gap-4 {theme === 'dark' ? 'dark' : ''}">
  <!-- PRESET SELECTOR (Horizontal Pill Matrix) -->
  <section class="mt-1">
    <div class="flex items-center justify-between mb-1.5">
      <span class="font-mono text-[0.6875rem] text-[#c7c4d7]">PIPELINE PRESET</span>
      <span class="font-mono text-[0.6875rem] text-[#4cd7f6]">FP16 / WebGPU</span>
    </div>
    <div class="flex items-center gap-1 overflow-x-auto no-scrollbar py-1 -mx-4 px-4" role="tablist" aria-label="Pipeline presets">
      <button
        type="button"
        role="tab"
        aria-selected={activePreset === 'segmentation'}
        class="flex items-center gap-1.5 px-3 py-1.5 rounded-full whitespace-nowrap transition-colors {activePreset === 'segmentation' ? 'bg-[#262a35] border border-[#4cd7f6] text-[#4cd7f6] shadow-sm shadow-[#4cd7f6]/20' : 'bg-[#171b26] border border-white/10 text-[#c7c4d7] hover:text-[#dfe2f1]'}"
        on:click={() => activePreset = 'segmentation'}
      >
        <span class="material-symbols-outlined text-sm" style={activePreset === 'segmentation' ? "font-variation-settings: 'FILL' 1;" : ""}>crop_free</span>
        <span class="font-mono text-xs font-medium">Object Segmentation</span>
      </button>
      <button
        type="button"
        role="tab"
        aria-selected={activePreset === 'synthesis'}
        class="flex items-center gap-1.5 px-3 py-1.5 rounded-full whitespace-nowrap transition-colors {activePreset === 'synthesis' ? 'bg-[#262a35] border border-[#4cd7f6] text-[#4cd7f6] shadow-sm shadow-[#4cd7f6]/20' : 'bg-[#171b26] border border-white/10 text-[#c7c4d7] hover:text-[#dfe2f1]'}"
        on:click={() => activePreset = 'synthesis'}
      >
        <span class="material-symbols-outlined text-sm">view_in_ar</span>
        <span class="font-mono text-xs font-medium">Text-to-3D Synthesis</span>
      </button>
      <button
        type="button"
        role="tab"
        aria-selected={activePreset === 'audio'}
        class="flex items-center gap-1.5 px-3 py-1.5 rounded-full whitespace-nowrap transition-colors {activePreset === 'audio' ? 'bg-[#262a35] border border-[#4cd7f6] text-[#4cd7f6] shadow-sm shadow-[#4cd7f6]/20' : 'bg-[#171b26] border border-white/10 text-[#c7c4d7] hover:text-[#dfe2f1]'}"
        on:click={() => activePreset = 'audio'}
      >
        <span class="material-symbols-outlined text-sm">graphic_eq</span>
        <span class="font-mono text-xs font-medium">Audio Stem Separator</span>
      </button>
      <button
        type="button"
        role="tab"
        aria-selected={activePreset === 'embedding'}
        class="flex items-center gap-1.5 px-3 py-1.5 rounded-full whitespace-nowrap transition-colors {activePreset === 'embedding' ? 'bg-[#262a35] border border-[#4cd7f6] text-[#4cd7f6] shadow-sm shadow-[#4cd7f6]/20' : 'bg-[#171b26] border border-white/10 text-[#c7c4d7] hover:text-[#dfe2f1]'}"
        on:click={() => activePreset = 'embedding'}
      >
        <span class="material-symbols-outlined text-sm">hub</span>
        <span class="font-mono text-xs font-medium">Embedding Search</span>
      </button>
    </div>
  </section>

  <!-- INTERACTIVE INPUT PLAYGROUND (Sample Card with Segmentation Mask) -->
  <section class="relative rounded-xl bg-[#0a0e18]/80 backdrop-blur-md border border-white/10 overflow-hidden shadow-lg">
    <!-- Media Header Bar -->
    <div class="flex items-center justify-between px-3 py-2 bg-[#171b26]/90 border-b border-white/10">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-[#c0c1ff] text-sm">dataset</span>
        <span class="font-mono text-[0.6875rem] text-[#dfe2f1]">Sample_Tensors_049.raw</span>
      </div>
      <div class="flex items-center gap-2">
        <span class="font-mono text-[0.6875rem] text-[#c7c4d7]">512×512</span>
        <button type="button" class="text-[#c7c4d7] hover:text-[#4cd7f6] transition-colors" title="Reload tensor" aria-label="Reload tensor">
          <span class="material-symbols-outlined text-base">refresh</span>
        </button>
      </div>
    </div>

    <!-- Canvas Viewport with Bounding Box & Poly Overlay -->
    <div class="relative w-full aspect-square bg-[#0f131d] overflow-hidden flex items-center justify-center">
      <!-- Sample Image Subject -->
      <img class="w-full h-full object-cover opacity-85" alt="A macro studio shot of an advanced industrial robotic sensor node" src="https://lh3.googleusercontent.com/aida-public/AB6AXuC-KswKZ0r_0jvwl_weFAs9-UvAiuSOWjYL2b_dXfv3VeKBZLJXQqs-alRKeOo-1Zp7G2BxNoNg4zyaA1OpZoOulOPGqfm-yExYtHZMg8sNa3HVHmXX2dPKDSf5PMWpug_q_f9jbCqTbkF2zTANMPrC-57xNus3-m1RmZ-yGxUO-yKmFbwi09FN8aptOOecJFJxGhFmbAapUANi-9lPlCaL7cSQ82K6d8LA7RFZyeMQhx-dP2h7e79RM5Ew2bqjN4mSGbjYxNuIVw"/>

      <!-- Visual AI Interactive Segmentation Mask (Overlay Canvas) -->
      {#if floatLayerState === 'mask' || floatLayerState === 'boxes'}
        <div class="absolute inset-0 pointer-events-none p-6 flex flex-col justify-between">
          <!-- Bounding Box 1 -->
          <div class="relative w-3/4 h-3/5 border-2 border-[#4cd7f6]/90 bg-[#4cd7f6]/15 rounded-lg flex flex-col justify-between p-1.5 animate-pulse" style="opacity: {confidenceThreshold > 0.4 ? 1 : 0.3}; transition: opacity 0.3s;">
            <div class="flex items-center justify-between">
              <span class="px-1.5 py-0.5 rounded bg-[#4cd7f6] text-[#003640] font-mono text-[0.6875rem] font-semibold tracking-wider">
                ROBOTIC_ACTUATOR [{formatConf}]
              </span>
              <span class="material-symbols-outlined text-[#4cd7f6] text-sm">filter_center_focus</span>
            </div>
            <!-- Polygonal Vector Dot Matrix Simulation -->
            {#if drawMaskPolygons}
              <div class="flex justify-between items-end">
                <span class="font-mono text-[0.6875rem] text-[#4cd7f6] bg-[#0f131d]/80 px-1 rounded">x:124 y:88 w:340 h:260</span>
                <span class="w-2 h-2 rounded-full bg-[#4cd7f6] ring-2 ring-[#0f131d]"></span>
              </div>
            {/if}
          </div>
          <!-- Bounding Box 2 (Secondary Object) -->
          <div class="self-end w-2/5 h-1/4 border border-[#a078ff] bg-[#a078ff]/15 rounded flex items-start p-1" style="opacity: {confidenceThreshold > 0.6 ? 1 : 0}; transition: opacity 0.3s;">
            <span class="px-1 py-0.2 rounded bg-[#a078ff] text-[#340080] font-mono text-[0.6875rem] font-medium">
              OPTIC_LENS [{Math.max(0, confidenceThreshold - 0.04).toFixed(2)}]
            </span>
          </div>
        </div>
      {/if}

      {#if floatLayerState === 'heatmap'}
        <div class="absolute inset-0 pointer-events-none bg-gradient-to-tr from-transparent via-[#4cd7f6]/20 to-[#a078ff]/30 mix-blend-screen mix-blend-overlay"></div>
      {/if}

      <!-- Float Layer Toggle Overlay -->
      <div class="absolute bottom-2 right-2 flex items-center gap-1 bg-[#0a0e18]/90 border border-white/20 rounded-lg p-1 backdrop-blur-md" role="radiogroup" aria-label="Visualization mode">
        <button
          type="button"
          role="radio"
          aria-checked={floatLayerState === 'mask'}
          class="px-2 py-1 rounded font-mono text-[0.6875rem] flex items-center gap-1 transition-colors {floatLayerState === 'mask' ? 'bg-[#4cd7f6]/20 text-[#4cd7f6]' : 'text-[#c7c4d7] hover:text-[#dfe2f1]'}"
          on:click={() => floatLayerState = 'mask'}
        >
          <span class="material-symbols-outlined text-xs">layers</span>
          <span>Mask</span>
        </button>
        <button
          type="button"
          role="radio"
          aria-checked={floatLayerState === 'boxes'}
          class="px-2 py-1 rounded font-mono text-[0.6875rem] transition-colors {floatLayerState === 'boxes' ? 'bg-[#4cd7f6]/20 text-[#4cd7f6]' : 'text-[#c7c4d7] hover:text-[#dfe2f1]'}"
          on:click={() => floatLayerState = 'boxes'}
        >
          <span>Boxes</span>
        </button>
        <button
          type="button"
          role="radio"
          aria-checked={floatLayerState === 'heatmap'}
          class="px-2 py-1 rounded font-mono text-[0.6875rem] transition-colors {floatLayerState === 'heatmap' ? 'bg-[#4cd7f6]/20 text-[#4cd7f6]' : 'text-[#c7c4d7] hover:text-[#dfe2f1]'}"
          on:click={() => floatLayerState = 'heatmap'}
        >
          <span>Heatmap</span>
        </button>
      </div>
    </div>

    <!-- Prompt Console Field -->
    <div class="p-2 bg-[#171b26] border-t border-white/10 flex flex-col gap-1.5">
      <label for="prompt-input" class="font-mono text-[0.6875rem] text-[#c7c4d7] flex items-center justify-between">
        <span>ZERO-SHOT CLASS / TARGET SPECIFIER</span>
        <span class="text-[#4cd7f6]">3 tokens</span>
      </label>
      <div class="relative flex items-center">
        <div class="absolute left-3 text-[#908fa0]">
          <span class="material-symbols-outlined text-base">search</span>
        </div>
        <input
          id="prompt-input"
          class="w-full bg-[#0a0e18] border border-white/20 focus:border-[#4cd7f6] focus:ring-1 focus:ring-[#4cd7f6] text-[#dfe2f1] font-mono text-xs rounded-lg pl-9 pr-16 py-2 transition-all outline-none"
          placeholder="Type detection classes or prompt..."
          type="text"
          bind:value={promptText}
        />
        <div class="absolute right-2 flex items-center gap-1">
          <button type="button" class="p-1 text-[#c7c4d7] hover:text-[#ffb4ab] transition-colors" title="Clear input" aria-label="Clear input" on:click={() => promptText = ''}>
            <span class="material-symbols-outlined text-sm">close</span>
          </button>
          <button type="button" class="p-1 text-[#4cd7f6] hover:text-[#c0c1ff] transition-colors" title="Run single prompt" aria-label="Run single prompt">
            <span class="material-symbols-outlined text-base">send</span>
          </button>
        </div>
      </div>
    </div>
  </section>

  <!-- LIVE HYPERPARAMETER & TUNING CONTROLS -->
  <section class="rounded-xl bg-[#0a0e18]/70 backdrop-blur-md border border-white/10 p-4 flex flex-col gap-4">
    <div class="flex items-center justify-between border-b border-white/10 pb-2">
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined text-[#4cd7f6] text-lg">tune</span>
        <span class="font-sans text-xl font-medium text-[#dfe2f1] tracking-tight">Hyperparameters</span>
      </div>
      <button
        type="button"
        class="text-[#c7c4d7] hover:text-[#c0c1ff] font-mono text-[0.6875rem] flex items-center gap-1"
        on:click={() => { confidenceThreshold = 0.78; iouOverlap = 0.45; edgeRefinement = true; gpuAcceleration = true; drawMaskPolygons = false; }}
      >
        <span class="material-symbols-outlined text-xs">restart_alt</span>
        <span>Reset Defaults</span>
      </button>
    </div>

    <!-- Slider 1: Confidence Threshold -->
    <div class="flex flex-col gap-1.5">
      <div class="flex items-center justify-between">
        <label for="conf-slider" class="font-mono text-xs text-[#dfe2f1]">Confidence Threshold</label>
        <span class="font-mono text-xs text-[#4cd7f6] font-semibold bg-[#262a35] px-1.5 py-0.5 rounded border border-[#4cd7f6]/30">{formatConf}</span>
      </div>
      <input
        id="conf-slider"
        type="range"
        min="0.10"
        max="0.95"
        step="0.01"
        bind:value={confidenceThreshold}
        class="w-full h-1.5 bg-[#313540] rounded-full appearance-none cursor-pointer accent-[#4cd7f6]"
      />
      <div class="flex justify-between font-mono text-[0.6875rem] text-[#908fa0]" aria-hidden="true">
        <span>0.10 (High Recall)</span>
        <span>0.95 (High Precision)</span>
      </div>
    </div>

    <!-- Slider 2: IoU Overlap Tolerance -->
    <div class="flex flex-col gap-1.5">
      <div class="flex items-center justify-between">
        <label for="iou-slider" class="font-mono text-xs text-[#dfe2f1]">IoU Overlap NMS</label>
        <span class="font-mono text-xs text-[#d0bcff] font-semibold bg-[#262a35] px-1.5 py-0.5 rounded border border-[#d0bcff]/30">{formatIoU}</span>
      </div>
      <input
        id="iou-slider"
        type="range"
        min="0.10"
        max="0.90"
        step="0.05"
        bind:value={iouOverlap}
        class="w-full h-1.5 bg-[#313540] rounded-full appearance-none cursor-pointer accent-[#a078ff]"
      />
    </div>

    <!-- Precision Toggles (3 rows) -->
    <div class="grid grid-cols-1 gap-2 pt-1 border-t border-white/10">
      <!-- Toggle 1: Edge Refinement -->
      <div class="flex items-center justify-between p-2 rounded-lg bg-[#171b26]/60 hover:bg-[#171b26] transition-colors">
        <div class="flex flex-col">
          <span class="font-sans text-sm text-[#dfe2f1] font-medium" id="toggle-edge-label">Edge Refinement</span>
          <span class="font-mono text-[0.6875rem] text-[#908fa0]" id="toggle-edge-desc">Bilinear sub-pixel anti-aliasing</span>
        </div>
        <button
          type="button"
          role="switch"
          aria-checked={edgeRefinement}
          aria-labelledby="toggle-edge-label"
          aria-describedby="toggle-edge-desc"
          class="w-9 h-5 rounded-full relative p-0.5 cursor-pointer flex items-center transition-colors {edgeRefinement ? 'bg-[#8083ff] justify-end' : 'bg-[#313540] justify-start'}"
          on:click={() => edgeRefinement = !edgeRefinement}
        >
          <div class="w-4 h-4 rounded-full shadow-sm {edgeRefinement ? 'bg-white' : 'bg-[#908fa0]'}"></div>
        </button>
      </div>

      <!-- Toggle 2: WebGPU Acceleration -->
      <div class="flex items-center justify-between p-2 rounded-lg bg-[#171b26]/60 hover:bg-[#171b26] transition-colors">
        <div class="flex flex-col">
          <div class="flex items-center gap-1.5">
            <span class="font-sans text-sm text-[#dfe2f1] font-medium" id="toggle-gpu-label">GPU Acceleration</span>
            <span class="px-1 py-0.2 rounded text-[10px] bg-[#03b5d3]/20 text-[#4cd7f6] font-mono">WebGPU</span>
          </div>
          <span class="font-mono text-[0.6875rem] text-[#908fa0]" id="toggle-gpu-desc">Hardware-accelerated compute shaders</span>
        </div>
        <button
          type="button"
          role="switch"
          aria-checked={gpuAcceleration}
          aria-labelledby="toggle-gpu-label"
          aria-describedby="toggle-gpu-desc"
          class="w-9 h-5 rounded-full relative p-0.5 cursor-pointer flex items-center transition-colors {gpuAcceleration ? 'bg-[#4cd7f6] justify-end' : 'bg-[#313540] justify-start'}"
          on:click={() => gpuAcceleration = !gpuAcceleration}
        >
          <div class="w-4 h-4 rounded-full shadow-sm {gpuAcceleration ? 'bg-[#003640]' : 'bg-[#908fa0]'}"></div>
        </button>
      </div>

      <!-- Toggle 3: Draw Mask Polygons -->
      <div class="flex items-center justify-between p-2 rounded-lg bg-[#171b26]/60 hover:bg-[#171b26] transition-colors">
        <div class="flex flex-col">
          <span class="font-sans text-sm text-[#dfe2f1] font-medium" id="toggle-poly-label">Draw Mask Polygons</span>
          <span class="font-mono text-[0.6875rem] text-[#908fa0]" id="toggle-poly-desc">Render raw boundary coordinates</span>
        </div>
        <button
          type="button"
          role="switch"
          aria-checked={drawMaskPolygons}
          aria-labelledby="toggle-poly-label"
          aria-describedby="toggle-poly-desc"
          class="w-9 h-5 rounded-full relative p-0.5 cursor-pointer flex items-center transition-colors {drawMaskPolygons ? 'bg-[#8083ff] justify-end' : 'bg-[#313540] justify-start'}"
          on:click={() => drawMaskPolygons = !drawMaskPolygons}
        >
          <div class="w-4 h-4 rounded-full shadow-sm {drawMaskPolygons ? 'bg-white' : 'bg-[#908fa0]'}"></div>
        </button>
      </div>
    </div>
  </section>

  <!-- LIVE INFERENCE TELEMETRY INSTRUMENTATION -->
  <section class="rounded-xl bg-[#0a0e18]/80 backdrop-blur-md border border-white/10 p-2 flex flex-col gap-2">
    <div class="flex items-center justify-between px-1">
      <span class="font-mono text-[0.6875rem] text-[#c7c4d7] flex items-center gap-1">
        <span class="w-1.5 h-1.5 rounded-full bg-[#4cd7f6] animate-ping"></span>
        REAL-TIME TELEMETRY
      </span>
      <span class="font-mono text-[0.6875rem] text-[#908fa0]">DEVICE BENCHMARK</span>
    </div>

    <!-- Metrics Grid (4 Bento Units) -->
    <div class="grid grid-cols-4 gap-2 text-center">
      <!-- Metric 1: Latency -->
      <div class="p-2 rounded bg-[#171b26] border border-white/10 flex flex-col items-center justify-center">
        <span class="font-mono text-[0.6875rem] text-[#908fa0]">LATENCY</span>
        <span class="font-mono text-sm text-[#4cd7f6] font-semibold mt-0.5">{latency.toFixed(1)}ms</span>
      </div>
      <!-- Metric 2: VRAM -->
      <div class="p-2 rounded bg-[#171b26] border border-white/10 flex flex-col items-center justify-center">
        <span class="font-mono text-[0.6875rem] text-[#908fa0]">VRAM</span>
        <span class="font-mono text-sm text-[#c0c1ff] font-semibold mt-0.5">{vram.toFixed(1)} GB</span>
      </div>
      <!-- Metric 3: Compute Runtime -->
      <div class="p-2 rounded bg-[#171b26] border border-white/10 flex flex-col items-center justify-center">
        <span class="font-mono text-[0.6875rem] text-[#908fa0]">ENGINE</span>
        <span class="font-mono text-xs text-[#dfe2f1] font-medium mt-1">{engineStr}</span>
      </div>
      <!-- Metric 4: Confidence Score -->
      <div class="p-2 rounded bg-[#171b26] border border-white/10 flex flex-col items-center justify-center">
        <span class="font-mono text-[0.6875rem] text-[#908fa0]">CONF.</span>
        <span class="font-mono text-sm text-[#acedff] font-semibold mt-0.5">{confidenceScore}%</span>
      </div>
    </div>
  </section>

  <!-- QUICK ACTION UTILITY DRAWER STRIP -->
  <section class="flex items-center justify-between gap-2 pt-1 pb-16">
    <button type="button" class="flex-1 flex items-center justify-center gap-1.5 py-2.5 px-2 rounded-lg bg-[#171b26] hover:bg-[#0f131d] border border-white/10 text-[#dfe2f1] font-mono text-[0.6875rem] transition-all active:scale-95 duration-100">
      <span class="material-symbols-outlined text-sm text-[#4cd7f6]">data_object</span>
      <span>Export JSON</span>
    </button>
    <button type="button" class="flex-1 flex items-center justify-center gap-1.5 py-2.5 px-2 rounded-lg bg-[#171b26] hover:bg-[#0f131d] border border-white/10 text-[#dfe2f1] font-mono text-[0.6875rem] transition-all active:scale-95 duration-100">
      <span class="material-symbols-outlined text-sm text-[#c0c1ff]">view_quilt</span>
      <span>Weights Map</span>
    </button>
    <button type="button" class="flex-1 flex items-center justify-center gap-1.5 py-2.5 px-2 rounded-lg bg-[#171b26] hover:bg-[#0f131d] border border-white/10 text-[#dfe2f1] font-mono text-[0.6875rem] transition-all active:scale-95 duration-100">
      <span class="material-symbols-outlined text-sm text-[#d0bcff]">terminal</span>
      <span>cURL / SDK</span>
    </button>
  </section>
</div>
