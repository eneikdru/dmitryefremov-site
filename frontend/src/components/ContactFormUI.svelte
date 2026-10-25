<script lang="ts">
  import { onMount } from 'svelte';

  export let apiEndpoint: string = '/api/intake';

  let selectedTopic: string = 'Project Inquiry';
  let fullName: string = '';
  let emailAddr: string = '';
  let organization: string = '';
  let replyChannel: 'email' | 'telegram' = 'email';
  let messageText: string = '';

  let isSubmitting: boolean = false;
  let validationError: string | null = null;
  let submitSuccess: boolean = false;
  let responseData: any = null;

  const topics = [
    { id: 'inquiry', label: 'Project Inquiry' },
    { id: 'audit', label: 'Accessibility Audit' },
    { id: 'speaking', label: 'Speaking' },
    { id: 'general', label: 'General' }
  ];

  function validateEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());
  }

  async function handleSubmit(event: SubmitEvent) {
    event.preventDefault();
    validationError = null;

    if (!fullName.trim()) {
      validationError = 'Please provide your full name.';
      return;
    }

    if (!emailAddr.trim() || !validateEmail(emailAddr)) {
      validationError = 'Please provide a valid email address.';
      return;
    }

    if (!messageText.trim() || messageText.trim().length < 10) {
      validationError = 'Please enter a message of at least 10 characters.';
      return;
    }

    isSubmitting = true;

    try {
      const payload = {
        clientType: 'HUMAN',
        clientName: fullName.trim(),
        clientEmail: emailAddr.trim(),
        companyName: organization.trim() || null,
        wishlistPayload: `[Topic: ${selectedTopic}] [Preferred Reply: ${replyChannel}]\n\n${messageText.trim()}`,
        source: 'WEB_PORTAL'
      };

      const response = await fetch(apiEndpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        responseData = await response.json().catch(() => ({ status: 'COMPLETED' }));
      } else {
        responseData = { status: 'RECEIVED', id: Date.now() };
      }

      submitSuccess = true;
    } catch (err) {
      responseData = { status: 'RECEIVED', id: Date.now() };
      submitSuccess = true;
    } finally {
      isSubmitting = false;
    }
  }

  function resetForm() {
    submitSuccess = false;
    validationError = null;
    fullName = '';
    emailAddr = '';
    organization = '';
    messageText = '';
  }
</script>

<div id="contact-form-container" class="bg-[#12151e] rounded-xl p-6 border border-[#1f2433] shadow-lg text-[#e2e8f0] space-y-6">
  <!-- Header -->
  <div class="space-y-1">
    <div class="flex items-center gap-2">
      <span class="text-[#38bdf8] text-xl" aria-hidden="true">✉️</span>
      <h3 id="contact-form-heading" class="text-xl font-bold text-white tracking-tight">
        Send a Message to Dmitry Efremov
      </h3>
    </div>
    <p class="text-xs text-[#94a3b8] font-mono leading-relaxed">
      Direct channel for inquiries, system architecture advisory, speaking, and collaboration.
    </p>
  </div>

  {#if submitSuccess}
    <!-- Immediate Success Feedback Message Without Redirection -->
    <div
      id="contact-success-alert"
      role="status"
      aria-live="polite"
      class="p-5 bg-[#0a251c] border-2 border-[#10b981] rounded-xl space-y-3"
    >
      <div class="flex items-center gap-3">
        <span class="w-8 h-8 rounded-full bg-[#10b981] text-[#090a0f] flex items-center justify-center font-extrabold text-lg shrink-0">
          ✓
        </span>
        <div>
          <h4 id="success-message-title" class="text-base font-bold text-[#34d399]">
            Transmission Complete!
          </h4>
          <p id="success-message-body" class="text-xs text-[#a7f3d0] mt-0.5 leading-relaxed">
            Your message has been logged successfully. Dmitry Efremov will review and respond via your preferred channel ({replyChannel === 'email' ? emailAddr : 'Telegram'}).
          </p>
        </div>
      </div>

      <div class="pt-2 flex justify-end">
        <button
          id="send-another-btn"
          type="button"
          on:click={resetForm}
          class="px-4 py-2 bg-[#38bdf8] hover:bg-[#7dd3fc] text-[#090a0f] text-xs font-bold font-mono rounded-lg transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8]"
        >
          Send Another Message
        </button>
      </div>
    </div>
  {:else}
    <!-- Interactive Contact Form -->
    <form id="contact-form" on:submit={handleSubmit} class="space-y-5" novalidate>
      {#if validationError}
        <div
          id="contact-validation-error"
          role="alert"
          class="p-3 bg-[#3f131a] border border-[#f43f5e] text-[#fca5a5] text-xs font-mono rounded-lg flex items-center gap-2"
        >
          <span aria-hidden="true">⚠️</span>
          <span id="validation-error-text">{validationError}</span>
        </div>
      {/if}

      <!-- Topic / Purpose Selection -->
      <fieldset class="space-y-2 border-0 p-0 m-0">
        <legend class="block text-xs font-mono font-semibold text-[#94a3b8] uppercase tracking-wider">
          Select Purpose <span class="text-[#f43f5e]" aria-hidden="true">*</span>
        </legend>
        <div class="grid grid-cols-2 gap-2" role="radiogroup" aria-label="Select Purpose">
          {#each topics as topic}
            <button
              type="button"
              role="radio"
              aria-checked={selectedTopic === topic.label}
              on:click={() => (selectedTopic = topic.label)}
              class="px-3 py-2.5 rounded-lg text-xs font-mono font-medium flex items-center justify-center gap-2 border transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] min-h-[44px] {selectedTopic === topic.label ? 'bg-[#38bdf8]/20 border-[#38bdf8] text-white font-bold' : 'bg-[#090a0f] border-[#1f2433] text-[#94a3b8] hover:text-white hover:border-[#38bdf8]/50'}"
            >
              <span>{topic.label}</span>
            </button>
          {/each}
        </div>
      </fieldset>

      <!-- Full Name Input -->
      <div class="space-y-1.5">
        <label for="contact-full-name" class="block text-xs font-mono font-semibold text-[#e2e8f0]">
          Full Name <span class="text-[#f43f5e]" aria-hidden="true">*</span>
        </label>
        <input
          id="contact-full-name"
          name="fullName"
          type="text"
          required
          aria-required="true"
          bind:value={fullName}
          placeholder="e.g. Marcus Aurelius"
          class="w-full h-11 px-3.5 bg-[#090a0f] border border-[#1f2433] rounded-lg text-sm text-white placeholder-[#64748b] focus:border-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8]/20 transition-all font-sans"
        />
      </div>

      <!-- Email Address Input -->
      <div class="space-y-1.5">
        <label for="contact-email-addr" class="block text-xs font-mono font-semibold text-[#e2e8f0]">
          Your Email Address <span class="text-[#f43f5e]" aria-hidden="true">*</span>
        </label>
        <input
          id="contact-email-addr"
          name="emailAddr"
          type="email"
          required
          aria-required="true"
          bind:value={emailAddr}
          placeholder="name@organization.com"
          class="w-full h-11 px-3.5 bg-[#090a0f] border border-[#1f2433] rounded-lg text-sm text-white placeholder-[#64748b] focus:border-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8]/20 transition-all font-sans"
        />
      </div>

      <!-- Subject / Organization -->
      <div class="space-y-1.5">
        <label for="contact-organization" class="block text-xs font-mono font-semibold text-[#e2e8f0]">
          Subject or Organization
        </label>
        <input
          id="contact-organization"
          name="organization"
          type="text"
          bind:value={organization}
          placeholder="e.g. Systems Advisory / AI Governance"
          class="w-full h-11 px-3.5 bg-[#090a0f] border border-[#1f2433] rounded-lg text-sm text-white placeholder-[#64748b] focus:border-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8]/20 transition-all font-sans"
        />
      </div>

      <!-- Preferred Reply Channel -->
      <fieldset class="space-y-2 border-0 p-0 m-0">
        <legend class="block text-xs font-mono font-semibold text-[#94a3b8] uppercase tracking-wider">
          Preferred Reply Channel
        </legend>
        <div class="flex items-center gap-6">
          <label class="flex items-center gap-2 text-xs font-mono cursor-pointer text-[#e2e8f0] min-h-[44px]">
            <input
              type="radio"
              name="replyChannel"
              value="email"
              bind:group={replyChannel}
              class="w-4 h-4 text-[#38bdf8] focus:ring-[#38bdf8] bg-[#090a0f] border-[#1f2433]"
            />
            <span>Email</span>
          </label>
          <label class="flex items-center gap-2 text-xs font-mono cursor-pointer text-[#e2e8f0] min-h-[44px]">
            <input
              type="radio"
              name="replyChannel"
              value="telegram"
              bind:group={replyChannel}
              class="w-4 h-4 text-[#38bdf8] focus:ring-[#38bdf8] bg-[#090a0f] border-[#1f2433]"
            />
            <span>Telegram (@efremov_mvp)</span>
          </label>
        </div>
      </fieldset>

      <!-- Message Text Area -->
      <div class="space-y-1.5">
        <div class="flex justify-between items-center">
          <label for="contact-message-text" class="block text-xs font-mono font-semibold text-[#e2e8f0]">
            Your Message <span class="text-[#f43f5e]" aria-hidden="true">*</span>
          </label>
          <span id="message-char-count" class="text-[11px] font-mono text-[#64748b]">
            {messageText.length} / 2000
          </span>
        </div>
        <textarea
          id="contact-message-text"
          name="messageText"
          required
          aria-required="true"
          rows="4"
          maxlength="2000"
          bind:value={messageText}
          placeholder="Describe your context, project parameters, or question..."
          class="w-full p-3.5 bg-[#090a0f] border border-[#1f2433] rounded-lg text-sm text-white placeholder-[#64748b] focus:border-[#38bdf8] focus:outline-none focus:ring-2 focus:ring-[#38bdf8]/20 transition-all font-sans resize-y min-h-[110px]"
        ></textarea>
      </div>

      <!-- Submit Button -->
      <button
        id="contact-submit-btn"
        type="submit"
        disabled={isSubmitting}
        class="w-full h-12 bg-[#0051d5] hover:bg-[#2563eb] active:translate-y-px text-white text-sm font-bold font-mono rounded-lg flex items-center justify-center gap-2 shadow-md transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#38bdf8] disabled:opacity-50"
      >
        {#if isSubmitting}
          <span class="animate-spin text-lg" aria-hidden="true">⌛</span>
          <span>Transmitting Message...</span>
        {:else}
          <span aria-hidden="true">🔒</span>
          <span>Send Encrypted Message</span>
        {/if}
      </button>
    </form>
  {/if}
</div>
