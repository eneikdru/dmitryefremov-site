<script lang="ts">
  import { onMount, onDestroy } from 'svelte';

  // State
  export let activeNavTab: 'enclave' | 'memory' | 'keys' | 'attest' = 'enclave';

  let beaconKilled: boolean = false;
  let rejectCount: number = 14208;
  let gatingMode: 'isolate' | 'route' = 'isolate';
  let rejectInterval: ReturnType<typeof setInterval> | null = null;

  interface LogEntry {
    id: string;
    timestamp: string;
    msg: string;
    colorClass: string;
  }

  let logs: LogEntry[] = [
    {
      id: '1',
      timestamp: '[00:00:00.000]',
      msg: 'SEC_KERNEL: Memory rings initialized. No external I/O bound.',
      colorClass: 'text-[#00ff66]'
    },
    {
      id: '2',
      timestamp: '[00:00:00.042]',
      msg: 'KERNEL: Gating IP sockets. Egress rejection filter active.',
      colorClass: 'text-[#e2e2e6]'
    },
    {
      id: '3',
      timestamp: '[00:00:00.108]',
      msg: 'INVARIANT_CHECK: Hash match verified against hardware fuse.',
      colorClass: 'text-[#e2e2e6]'
    },
    {
      id: '4',
      timestamp: '[00:00:00.312]',
      msg: 'ZERO_TELEMETRY: Background workers silenced. Beacon nullified.',
      colorClass: 'text-[#00e3fd]'
    }
  ];

  let logContainer: HTMLElement | null = null;

  function getTimestampStr(): string {
    const now = new Date();
    const h = String(now.getUTCHours()).padStart(2, '0');
    const m = String(now.getUTCMinutes()).padStart(2, '0');
    const s = String(now.getUTCSeconds()).padStart(2, '0');
    const ms = String(now.getUTCMilliseconds()).padStart(3, '0');
    return `[${h}:${m}:${s}.${ms}]`;
  }

  function addLog(msg: string, colorClass: string = 'text-[#00ff66]') {
    const newEntry: LogEntry = {
      id: Math.random().toString(36).substring(2, 9),
      timestamp: getTimestampStr(),
      msg,
      colorClass
    };
    logs = [...logs, newEntry];
    setTimeout(() => {
      if (logContainer) {
        logContainer.scrollTop = logContainer.scrollHeight;
      }
    }, 10);
  }

  export function killBeacon() {
    if (beaconKilled) return;
    beaconKilled = true;
    addLog('CMD_RECEIVED: KILL_BEACON. All RF carrier signals truncated to ground.', 'text-[#ffb4ab]');
  }

  export function purgeBuffer() {
    addLog('SYS_ACTION: Local volatility ring cleared. 0x0000 written over 128MB heap cache.', 'text-[#00ff66]');
  }

  export function rotateKey() {
    addLog('TRNG_REKEY: Hardware entropy drawn. New Ephemeral Ed25519 root committed.', 'text-[#00e3fd]');
  }

  export function scrambleId() {
    addLog('MASK_GEN: Enclave public measurement rotated. Anti-fingerprint shield enforced.', 'text-[#ffb4ab]');
  }

  function setGatingMode(mode: 'isolate' | 'route') {
    gatingMode = mode;
    addLog(`DATA_BUS_MODE: Gating switch toggled to [ ${mode.toUpperCase()} ].`, mode === 'isolate' ? 'text-[#00e3fd]' : 'text-[#ffb4ab]');
  }

  onMount(() => {
    rejectInterval = setInterval(() => {
      rejectCount += Math.floor(Math.random() * 3) + 1;
    }, 2800);
  });

  onDestroy(() => {
    if (rejectInterval) clearInterval(rejectInterval);
  });
</script>

<div
  id="swiss-editorial-view"
  class="bg-[#111316] text-[#e2e2e6] font-mono min-h-screen pb-20 select-none selection:bg-[#00ff66] selection:text-[#111316]"
>
  <!-- TOP APP BAR -->
  <header
    id="main-header"
    class="flex justify-between items-center w-full px-3 py-2 border-b border-[#3b4b3a] bg-[#111316] sticky top-0 z-40"
  >
    <div class="flex items-center gap-2">
      <span class="text-[#00ff66] text-lg font-bold" aria-hidden="true">＞_</span>
      <span class="text-sm font-bold tracking-widest text-[#edffe8] font-sans">ENCLAVE://SEC-0</span>
    </div>
    <div class="flex items-center gap-2">
      <span class="inline-block w-2 h-2 bg-[#00ff66] animate-pulse"></span>
      <button
        id="killBtn"
        type="button"
        on:click={killBeacon}
        class="px-2 py-1 text-[10px] font-bold border transition-none active:bg-[#282a2d] cursor-pointer {beaconKilled ? 'bg-[#282a2d] text-[#849581] border-[#3b4b3a]' : 'bg-[#93000a] text-[#ffdad6] border-[#ffb4ab] hover:bg-[#ffb4ab] hover:text-[#690005]'}"
      >
        {beaconKilled ? 'BEACON_DEAD' : 'KILL_BEACON'}
      </button>
    </div>
  </header>

  <!-- AIR-GAPPED SYSTEM TELEMETRY STRIP -->
  <section
    id="telemetry-strip"
    class="w-full bg-[#0c0e11] border-b border-[#3b4b3a] px-3 py-2 text-[9px] sm:text-[10px]"
  >
    <div class="grid grid-cols-1 gap-1">
      <div class="flex justify-between items-center text-[#849581]">
        <span>INTERFACE_BUS:</span>
        <span class="text-[#00ff66] font-semibold tracking-wider">NETWORK: SEVERED [AIR-GAP: ACTIVE]</span>
      </div>
      <div class="flex justify-between items-center text-[#849581]">
        <span>EGRESS_RATE:</span>
        <span class="text-[#00e3fd] font-semibold">TELEMETRY EGRESS: 0.00 B/s (BLOCKED)</span>
      </div>
      <div class="flex justify-between items-center text-[#849581]">
        <span>SILICON_STATE:</span>
        <span class="text-[#b9ccb5] font-semibold">HARDWARE INVARIANT: ENFORCED</span>
      </div>
    </div>
  </section>

  <!-- MAIN VIEWPORT CONTAINER -->
  <main id="main-content" class="px-3 pt-3 space-y-3 max-w-4xl mx-auto">
    <!-- HERO BENTO: BLACK-BOX INVARIANT STATUS CARD -->
    <div id="hero-status-card" class="bg-[#1a1c1f] border border-[#3b4b3a] p-3 relative">
      <!-- Crosshair Instrumentation Markers -->
      <div class="absolute -top-[1px] -left-[1px] w-1.5 h-1.5 bg-[#00ff66]"></div>
      <div class="absolute -top-[1px] -right-[1px] w-1.5 h-1.5 bg-[#00ff66]"></div>
      <div class="absolute -bottom-[1px] -left-[1px] w-1.5 h-1.5 bg-[#00ff66]"></div>
      <div class="absolute -bottom-[1px] -right-[1px] w-1.5 h-1.5 bg-[#00ff66]"></div>

      <!-- Card Top Header Ribbon -->
      <div class="flex items-center justify-between pb-2 mb-2 border-b border-[#3b4b3a]">
        <div class="flex items-center gap-2">
          <span class="text-[#00ff66] text-xs font-bold" aria-hidden="true">✓</span>
          <span class="text-[10px] sm:text-xs text-[#edffe8] font-bold">BLACK-BOX INVARIANT STATUS: VERIFIED</span>
        </div>
        <span class="bg-[#1e2023] text-[#00ff66] text-[9px] px-1.5 py-0.5 border border-[#3b4b3a]">ISOLATED</span>
      </div>

      <!-- Attestation & System Invariant Block -->
      <div id="attestation-block" class="bg-[#0c0e11] border border-[#3b4b3a] p-2 mb-3 space-y-1.5">
        <div class="flex justify-between items-start text-[9px] text-[#849581]">
          <span>CRYPTO_ATTESTATION:</span>
          <span class="text-[#b9ccb5] font-semibold">SYS_TIME: UTC-NULL</span>
        </div>
        <!-- Cryptographic Checksum Banner (Chunked) -->
        <div class="p-1.5 bg-[#111316] border border-[#3b4b3a] flex flex-wrap items-center gap-1 text-[10px]">
          <span class="text-[#00ff66] font-semibold">SHA-512:</span>
          <span class="bg-[#282a2d] px-1 text-[#e2e2e6]">8f4e</span>
          <span class="bg-[#282a2d] px-1 text-[#00e3fd]">2a91</span>
          <span class="bg-[#282a2d] px-1 text-[#e2e2e6]">d0c8</span>
          <span class="bg-[#282a2d] px-1 text-[#00e3fd]">71ef</span>
          <span class="bg-[#282a2d] px-1 text-[#e2e2e6]">...</span>
          <span class="bg-[#282a2d] px-1 text-[#00e3fd]">e901a</span>
          <span class="text-[#00ff66] ml-auto text-[9px]">[VALID]</span>
        </div>
        <div class="flex justify-between text-[9px] pt-0.5 text-[#849581]">
          <span>ROOT_OF_TRUST: <span class="text-[#e2e2e6]">TPM_2.0_SEC_ENG</span></span>
          <span>ENTROPY: <span class="text-[#00ff66]">4096-BIT TRNG</span></span>
        </div>
      </div>

      <!-- Live Telemetry Sensor Lockdown Matrix -->
      <div id="lockdown-matrix" class="space-y-1.5">
        <div class="text-[9px] text-[#849581] flex items-center justify-between pb-1">
          <span>LOCKDOWN MATRIX: SENSORS & DAEMONS</span>
          <span>ALL ZEROIZED</span>
        </div>
        <div class="grid grid-cols-1 gap-1 text-[10px]">
          <!-- Row 1 -->
          <div class="flex items-center justify-between p-1.5 bg-[#0c0e11] border border-[#3b4b3a]">
            <span class="text-[#e2e2e6] flex items-center gap-1.5">
              <span class="w-1.5 h-1.5 bg-[#00ff66]"></span>
              Network Beacons
            </span>
            <span class="text-[9px] bg-[#1e2023] px-2 py-0.5 text-[#00ff66] border border-[#3b4b3a]">
              [OFFLINE / SINKHOLED]
            </span>
          </div>
          <!-- Row 2 -->
          <div class="flex items-center justify-between p-1.5 bg-[#0c0e11] border border-[#3b4b3a]">
            <span class="text-[#e2e2e6] flex items-center gap-1.5">
              <span class="w-1.5 h-1.5 bg-[#00ff66]"></span>
              Analytics Daemons
            </span>
            <span class="text-[9px] bg-[#1e2023] px-2 py-0.5 text-[#00ff66] border border-[#3b4b3a]">
              [ZERO_PRESENCE]
            </span>
          </div>
          <!-- Row 3 -->
          <div class="flex items-center justify-between p-1.5 bg-[#0c0e11] border border-[#3b4b3a]">
            <span class="text-[#e2e2e6] flex items-center gap-1.5">
              <span class="w-1.5 h-1.5 bg-[#00ff66]"></span>
              Memory Egress
            </span>
            <span class="text-[9px] bg-[#1e2023] px-2 py-0.5 text-[#00e3fd] border border-[#3b4b3a]">
              [ENCLAVE ISOLATED]
            </span>
          </div>
          <!-- Row 4 -->
          <div class="flex items-center justify-between p-1.5 bg-[#0c0e11] border border-[#3b4b3a]">
            <span class="text-[#e2e2e6] flex items-center gap-1.5">
              <span class="w-1.5 h-1.5 bg-[#00ff66]"></span>
              Crash Reporter
            </span>
            <span class="text-[9px] bg-[#1e2023] px-2 py-0.5 text-[#00ff66] border border-[#3b4b3a]">
              [HARD-DISABLED]
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- HARDWARE QUARANTINE MONITOR -->
    <div id="quarantine-monitor" class="bg-[#1a1c1f] border border-[#3b4b3a] p-3 space-y-2">
      <div class="flex items-center justify-between border-b border-[#3b4b3a] pb-1.5">
        <span class="text-[10px] sm:text-xs text-[#edffe8] font-bold">TELEMETRY QUARANTINE MONITOR</span>
        <span class="text-[9px] text-[#849581]">POLL_INT: 0ms (HARDWARE)</span>
      </div>
      <div class="grid grid-cols-3 gap-2 text-center">
        <div class="p-2 bg-[#0c0e11] border border-[#3b4b3a]">
          <span class="block text-[9px] text-[#849581]">OUTBOUND</span>
          <span id="bytesCounter" class="block text-xs sm:text-sm font-bold text-[#00ff66] mt-1">0 B</span>
          <span class="block text-[8px] sm:text-[9px] text-[#3b4b3a] mt-0.5">PERMANENT_0</span>
        </div>
        <div class="p-2 bg-[#0c0e11] border border-[#3b4b3a]">
          <span class="block text-[9px] text-[#849581]">SYN/ICMP PINGS</span>
          <span class="block text-xs sm:text-sm font-bold text-[#00e3fd] mt-1">0 PINGS</span>
          <span class="block text-[8px] sm:text-[9px] text-[#3b4b3a] mt-0.5">DROP_POLICY</span>
        </div>
        <div class="p-2 bg-[#0c0e11] border border-[#3b4b3a]">
          <span class="block text-[9px] text-[#849581]">SOCKET REJECTS</span>
          <span id="rejectCounter" class="block text-xs sm:text-sm font-bold text-[#edffe8] mt-1">
            {rejectCount.toLocaleString()}
          </span>
          <span class="block text-[8px] sm:text-[9px] text-[#3b4b3a] mt-0.5">HARD_GATE</span>
        </div>
      </div>

      <!-- Segmented Hardware Channel Switches -->
      <div class="pt-1">
        <div class="text-[9px] text-[#849581] mb-1">DATA BUS GATING MODE:</div>
        <div class="grid grid-cols-2 gap-1 text-[10px]">
          <button
            id="btnGatingIsolate"
            type="button"
            on:click={() => setGatingMode('isolate')}
            class="py-1.5 px-2 font-bold flex items-center justify-center gap-1 border transition-none cursor-pointer {gatingMode === 'isolate' ? 'bg-[#00e3fd] text-[#111316] border-[#00e3fd]' : 'bg-[#282a2d] text-[#849581] border-[#3b4b3a] opacity-70'}"
          >
            <span>🔒</span>
            <span>[ 0 | ISOLATE ]</span>
          </button>
          <button
            id="btnGatingRoute"
            type="button"
            on:click={() => setGatingMode('route')}
            class="py-1.5 px-2 font-bold flex items-center justify-center gap-1 border transition-none cursor-pointer {gatingMode === 'route' ? 'bg-[#ffb4ab] text-[#111316] border-[#ffb4ab]' : 'bg-[#282a2d] text-[#849581] border-[#3b4b3a] opacity-60'}"
          >
            <span>🔀</span>
            <span>[ 1 | ROUTE ]</span>
          </button>
        </div>
      </div>
    </div>

    <!-- TELEMETRY NULLIFICATION CONTROLS -->
    <div id="tactical-controls" class="bg-[#1a1c1f] border border-[#3b4b3a] p-3 space-y-2.5">
      <div class="flex items-center justify-between border-b border-[#3b4b3a] pb-1.5">
        <span class="text-[10px] sm:text-xs text-[#edffe8] font-bold">TACTICAL CONTROLS // DESTRUCTIVE & ROTATION</span>
        <span class="text-[9px] text-[#ffb4ab]">LEVEL_0_AUTH</span>
      </div>
      <div class="space-y-2">
        <!-- Action 1: Purge Buffer -->
        <button
          id="btnPurgeBuffer"
          type="button"
          on:click={purgeBuffer}
          class="w-full bg-[#0c0e11] hover:bg-[#1e2023] border border-[#3b4b3a] p-2 flex items-center justify-between text-left group transition-none active:bg-[#282a2d] cursor-pointer"
        >
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 bg-[#282a2d] border border-[#849581] flex items-center justify-center text-[#00ff66] text-[8px]">■</span>
            <div>
              <span class="text-[10px] font-bold text-[#e2e2e6] block group-hover:text-[#00ff66]">PURGE LOCAL BUFFER</span>
              <span class="text-[9px] text-[#849581] block">ZEROIZE RAM CACHE & VOLATILE RUNTIME LOGS</span>
            </div>
          </div>
          <span class="text-[9px] text-[#00ff66] border border-[#3b4b3a] px-1.5 py-0.5 bg-[#1e2023]">EXEC_NULL</span>
        </button>

        <!-- Action 2: Rotate Attestation Key -->
        <button
          id="btnRotateKey"
          type="button"
          on:click={rotateKey}
          class="w-full bg-[#0c0e11] hover:bg-[#1e2023] border border-[#3b4b3a] p-2 flex items-center justify-between text-left group transition-none active:bg-[#282a2d] cursor-pointer"
        >
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 bg-[#282a2d] border border-[#849581] flex items-center justify-center text-[#00e3fd] text-[8px]">◆</span>
            <div>
              <span class="text-[10px] font-bold text-[#e2e2e6] block group-hover:text-[#00e3fd]">ROTATE ATTESTATION KEY</span>
              <span class="text-[9px] text-[#849581] block">RE-DERIVE EPID KEYCHAIN VIA HARDWARE TRNG</span>
            </div>
          </div>
          <span class="text-[9px] text-[#00e3fd] border border-[#3b4b3a] px-1.5 py-0.5 bg-[#1e2023]">RE_KEY</span>
        </button>

        <!-- Action 3: Scramble Enclave ID -->
        <button
          id="btnScrambleId"
          type="button"
          on:click={scrambleId}
          class="w-full bg-[#0c0e11] hover:bg-[#1e2023] border border-[#ffb4ab] p-2 flex items-center justify-between text-left group transition-none active:bg-[#282a2d] cursor-pointer"
        >
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 bg-[#93000a] border border-[#ffb4ab] flex items-center justify-center text-[#ffb4ab] text-[8px]">▲</span>
            <div>
              <span class="text-[10px] font-bold text-[#ffb4ab] block group-hover:text-[#ffdad6]">SCRAMBLE ENCLAVE ID</span>
              <span class="text-[9px] text-[#849581] block">GENERATE RANDOMIZED HARDWARE FINGERPRINT MASK</span>
            </div>
          </div>
          <span class="text-[9px] text-[#ffb4ab] border border-[#ffb4ab] px-1.5 py-0.5 bg-[#1e2023]">ANONYMIZE</span>
        </button>
      </div>
    </div>

    <!-- CONSOLE AUDIT LOG TERMINAL -->
    <div id="audit-log-terminal" class="bg-[#0c0e11] border border-[#3b4b3a] p-2.5 text-[9px] sm:text-[10px]">
      <div class="flex items-center justify-between border-b border-[#3b4b3a] pb-1 mb-2 text-[#849581]">
        <span class="flex items-center gap-1">
          <span class="text-[#00ff66]">＞</span>
          ENCLAVE_KERNEL_AUDIT_LOG
        </span>
        <span class="text-[#00ff66]">LIVE_STREAM</span>
      </div>
      <div
        id="logStream"
        bind:this={logContainer}
        class="space-y-1 text-[#849581] max-h-28 overflow-y-auto pr-1"
        role="region"
        aria-label="Kernel Audit Log Stream"
      >
        {#each logs as log (log.id)}
          <div class={log.colorClass}>
            {log.timestamp} {log.msg}
          </div>
        {/each}
      </div>
    </div>
  </main>

  <!-- BOTTOM NAVBAR -->
  <nav
    id="bottom-nav"
    aria-label="Enclave Subsystem Navigation"
    class="fixed bottom-0 left-0 w-full z-50 flex justify-around items-center px-1 py-1 border-t border-[#3b4b3a] bg-[#0c0e11]"
  >
    <!-- Tab 1: ENCLAVE -->
    <button
      id="nav-tab-enclave"
      type="button"
      on:click={() => (activeNavTab = 'enclave')}
      class="flex flex-col items-center justify-center px-3 py-1 cursor-pointer {activeNavTab === 'enclave' ? 'bg-[#1e2023] text-[#00ff66] border-t-2 border-[#00ff66]' : 'text-[#849581] opacity-70 hover:bg-[#1a1c1f]'}"
    >
      <span class="text-xs" aria-hidden="true">🛡️</span>
      <span class="text-[9px] font-bold mt-0.5">ENCLAVE</span>
    </button>
    <!-- Tab 2: MEMORY -->
    <button
      id="nav-tab-memory"
      type="button"
      on:click={() => (activeNavTab = 'memory')}
      class="flex flex-col items-center justify-center px-3 py-1 cursor-pointer {activeNavTab === 'memory' ? 'bg-[#1e2023] text-[#00ff66] border-t-2 border-[#00ff66]' : 'text-[#849581] opacity-70 hover:bg-[#1a1c1f]'}"
    >
      <span class="text-xs" aria-hidden="true">💾</span>
      <span class="text-[9px] font-bold mt-0.5">MEMORY</span>
    </button>
    <!-- Tab 3: KEYS -->
    <button
      id="nav-tab-keys"
      type="button"
      on:click={() => (activeNavTab = 'keys')}
      class="flex flex-col items-center justify-center px-3 py-1 cursor-pointer {activeNavTab === 'keys' ? 'bg-[#1e2023] text-[#00ff66] border-t-2 border-[#00ff66]' : 'text-[#849581] opacity-70 hover:bg-[#1a1c1f]'}"
    >
      <span class="text-xs" aria-hidden="true">🔑</span>
      <span class="text-[9px] font-bold mt-0.5">KEYS</span>
    </button>
    <!-- Tab 4: ATTEST -->
    <button
      id="nav-tab-attest"
      type="button"
      on:click={() => (activeNavTab = 'attest')}
      class="flex flex-col items-center justify-center px-3 py-1 cursor-pointer {activeNavTab === 'attest' ? 'bg-[#1e2023] text-[#00ff66] border-t-2 border-[#00ff66]' : 'text-[#849581] opacity-70 hover:bg-[#1a1c1f]'}"
    >
      <span class="text-xs" aria-hidden="true">✓</span>
      <span class="text-[9px] font-bold mt-0.5">ATTEST</span>
    </button>
  </nav>
</div>
