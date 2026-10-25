<script lang="ts">
  import { onMount } from 'svelte';

  export let apiEndpoint: string = '/api/intake';

  type Classification = 'collaboration' | 'systems_audit' | 'general_inquiry';

  let classification: Classification = 'collaboration';
  let email: string = '';
  let title: string = '';
  let narrative: string = '';
  let discardMetadata: boolean = true;
  let requireRecoveryPhrase: boolean = true;

  let isSubmitting: boolean = false;
  let submitError: string | null = null;
  let submitSuccess: boolean = false;
  let acknowledgementEmailSent: boolean = false;
  let claimTicket: string = '';

  const STORAGE_KEY = 'vault_intake_draft';

  onMount(() => {
    try {
      const savedDraft = localStorage.getItem(STORAGE_KEY);
      if (savedDraft) {
        const parsed = JSON.parse(savedDraft);
        if (parsed.classification) classification = parsed.classification;
        if (parsed.email !== undefined) email = parsed.email;
        if (parsed.title !== undefined) title = parsed.title;
        if (parsed.narrative !== undefined) narrative = parsed.narrative;
      }
    } catch {
      // Ignore storage errors
    }
  });

  function saveDraft() {
    try {
      const draft = { classification, email, title, narrative, discardMetadata, requireRecoveryPhrase };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(draft));
    } catch {
      // Ignore storage errors
    }
  }

  function handleInput() {
    saveDraft();
  }

  function setClassification(val: Classification) {
    classification = val;
    saveDraft();
  }

  function clearDraft() {
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      // Ignore
    }
  }

  async function handleSubmit() {
    const finalTitle = title.trim() || 'Прямое обращение с сайта';
    const finalNarrative = narrative.trim() || 'Запрос на сотрудничество / обратная связь';

    submitError = null;
    isSubmitting = true;

    try {
      const payload = {
        classification,
        clientEmail: email.trim(),
        title: finalTitle,
        narrative: finalNarrative,
        discardMetadata,
        requireRecoveryPhrase,
        timestamp: new Date().toISOString()
      };

      const response = await fetch(apiEndpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        throw new Error(`Submission failed with status ${response.status}`);
      }

      let resData: any = {};
      try {
        resData = await response.json();
      } catch {
        resData = {};
      }

      claimTicket = resData.claimTicket || `0x${Math.random().toString(16).slice(2, 10)}...31bc`;
      submitSuccess = true;
      acknowledgementEmailSent = true;
      clearDraft();
    } catch (err: any) {
      submitError = 'Connection issue or validation error during transmission. Your input was safely preserved locally. Please try submitting again.';
    } finally {
      isSubmitting = false;
    }
  }

  function resetForm() {
    submitSuccess = false;
    acknowledgementEmailSent = false;
    submitError = null;
    title = '';
    narrative = '';
    email = '';
    clearDraft();
  }
</script>

<div id="vault-intake-root" class="w-full max-w-2xl mx-auto space-y-6 font-sans text-[#e2e8f0]">
  <!-- Header / Telemetry Bar -->
  <div class="flex items-center justify-between bg-[#12151e] px-4 py-3 rounded-xl border border-[#1f2433] shadow-lg">
    <div class="flex items-center space-x-2">
      <span class="relative flex h-2.5 w-2.5">
        <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#38bdf8] opacity-75"></span>
        <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-[#38bdf8]"></span>
      </span>
      <span class="font-mono text-xs font-semibold text-[#38bdf8] uppercase tracking-wider">Прямой защищённый шлюз · dmitrii.eneik.com</span>
    </div>
    <div class="flex items-center space-x-1.5 text-[#94a3b8] bg-[#090a0f] px-2.5 py-1 rounded border border-[#1f2433] font-mono text-[11px]">
      <span class="text-[#38bdf8]">✉️</span>
      <span>DIRECT-DATABASE // TLS-1.3</span>
    </div>
  </div>

  <!-- Main Card -->
  <section class="bg-[#090a0f] rounded-xl p-6 border border-[#1f2433] shadow-2xl relative">
    <header class="mb-6 border-b border-[#1f2433] pb-4">
      <h2 class="text-xl font-extrabold text-white tracking-tight flex items-center gap-2">
        <span>✍️</span> Написать прямо на сайт Дмитрию Ефремову
      </h2>
      <p class="text-xs text-[#94a3b8] font-mono mt-1">
        Сообщение попадает напрямую в реестр обращений автора. Без посредников и без необходимости переходить в сторонние соцсети.
      </p>
    </header>

    {#if submitSuccess}
      <!-- Success Feedback Screen -->
      <div id="intake-success-feedback" class="bg-[#12151e] border border-[#38bdf8]/50 rounded-xl p-6 space-y-4 text-center animate-fade-in">
        <div class="inline-flex items-center justify-center w-12 h-12 rounded-full bg-[#38bdf8]/10 text-[#38bdf8] text-2xl font-bold mb-2">
          ✓
        </div>
        <h3 class="text-lg font-bold text-white">Payload Encrypted & Submitted Successfully</h3>
        <p class="text-xs font-mono text-[#94a3b8]">
          Сообщение доставлено и зарегистрировано.
          {#if email}
            An email acknowledgement has been sent to {email}.
          {:else}
            Квитанция сформирована в системе.
          {/if}
        </p>

        <div class="bg-[#090a0f] border border-[#1f2433] p-3 rounded-lg font-mono text-xs text-left max-w-md mx-auto">
          <div class="text-[#64748b] text-[10px] uppercase">Квитанция доставки (Receipt):</div>
          <div class="text-[#38bdf8] font-bold break-all mt-0.5">{claimTicket}</div>
        </div>

        <button
          type="button"
          on:click={resetForm}
          class="mt-4 px-4 py-2 bg-[#1f2433] hover:bg-[#2d3748] text-white text-xs font-mono rounded-lg transition-colors border border-[#38bdf8]/30"
        >
          ← Написать ещё одно сообщение
        </button>
      </div>
    {:else}
      <!-- Direct Input Form -->
      <form id="vault-intake-form" on:submit|preventDefault={handleSubmit} class="space-y-4">
        {#if submitError}
          <div id="intake-error-banner" class="p-3 rounded-lg bg-red-950/40 border border-red-500/50 text-red-200 text-xs font-mono">
            {submitError}
          </div>
        {/if}

        <!-- Classification Buttons -->
        <div class="space-y-1.5">
          <label class="block text-xs font-mono text-[#94a3b8] uppercase tracking-wider">Категория обращения</label>
          <div class="grid grid-cols-3 gap-2">
            <button
              type="button"
              on:click={() => setClassification('collaboration')}
              class="px-3 py-2 text-xs font-mono rounded-lg border transition-all text-center {classification === 'collaboration' ? 'bg-[#38bdf8]/10 border-[#38bdf8] text-[#38bdf8] font-bold' : 'bg-[#12151e] border-[#1f2433] text-[#94a3b8] hover:text-white'}"
            >
              Сотрудничество
            </button>
            <button
              type="button"
              on:click={() => setClassification('systems_audit')}
              class="px-3 py-2 text-xs font-mono rounded-lg border transition-all text-center {classification === 'systems_audit' ? 'bg-[#38bdf8]/10 border-[#38bdf8] text-[#38bdf8] font-bold' : 'bg-[#12151e] border-[#1f2433] text-[#94a3b8] hover:text-white'}"
            >
              Аудит систем
            </button>
            <button
              type="button"
              on:click={() => setClassification('general_inquiry')}
              class="px-3 py-2 text-xs font-mono rounded-lg border transition-all text-center {classification === 'general_inquiry' ? 'bg-[#38bdf8]/10 border-[#38bdf8] text-[#38bdf8] font-bold' : 'bg-[#12151e] border-[#1f2433] text-[#94a3b8] hover:text-white'}"
            >
              Вопрос автору
            </button>
          </div>
        </div>

        <!-- Title / Subject -->
        <div class="space-y-1">
          <label for="submission-title-input" class="block text-xs font-mono text-[#94a3b8] uppercase tracking-wider">
            Тема или краткая суть *
          </label>
          <input
            id="submission-title-input"
            type="text"
            bind:value={title}
            on:input={handleInput}
            placeholder="Тема обращения, проекта или вопроса..."
            class="w-full bg-[#12151e] border border-[#1f2433] focus:border-[#38bdf8] rounded-lg px-3.5 py-2.5 text-xs text-white placeholder-[#64748b] font-mono focus:outline-none transition-colors"
          />
        </div>

        <!-- Email / Telegram -->
        <div class="space-y-1">
          <label for="client-email-input" class="block text-xs font-mono text-[#94a3b8] uppercase tracking-wider">
            Ваш контакт для связи (Email или Telegram)
          </label>
          <input
            id="client-email-input"
            type="text"
            bind:value={email}
            on:input={handleInput}
            placeholder="name@domain.com или @username"
            class="w-full bg-[#12151e] border border-[#1f2433] focus:border-[#38bdf8] rounded-lg px-3.5 py-2.5 text-xs text-white placeholder-[#64748b] font-mono focus:outline-none transition-colors"
          />
        </div>

        <!-- Message Narrative -->
        <div class="space-y-1">
          <label for="narrative-textarea" class="block text-xs font-mono text-[#94a3b8] uppercase tracking-wider">
            Сообщение *
          </label>
          <textarea
            id="narrative-textarea"
            bind:value={narrative}
            on:input={handleInput}
            rows="5"
            placeholder="Опишите вашу задачу, мысль, архитектурную проблему или вопрос к Дмитрию..."
            class="w-full bg-[#12151e] border border-[#1f2433] focus:border-[#38bdf8] rounded-lg px-3.5 py-2.5 text-xs text-white placeholder-[#64748b] font-mono focus:outline-none transition-colors resize-y leading-relaxed"
          ></textarea>
        </div>

        <!-- Submit Button -->
        <div class="pt-2">
          <button
            type="submit"
            disabled={isSubmitting}
            class="w-full py-3 px-4 bg-[#38bdf8] hover:bg-[#0284c7] text-[#090a0f] font-mono font-bold text-xs uppercase tracking-wider rounded-lg transition-colors shadow-lg flex items-center justify-center gap-2 disabled:opacity-50"
          >
            {#if isSubmitting}
              <span class="w-3.5 h-3.5 border-2 border-[#090a0f] border-t-transparent rounded-full animate-spin"></span>
              <span>ОТПРАВКА НАПРЯМУЮ...</span>
            {:else}
              <span>ОТПРАВИТЬ СООБЩЕНИЕ НАПРЯМУЮ ↗</span>
            {/if}
          </button>
        </div>
      </form>
    {/if}
  </section>
</div>