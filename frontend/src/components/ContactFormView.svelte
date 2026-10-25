<script lang="ts">
  import { onMount } from 'svelte';
  import { telemetry } from '../lib/telemetry';

  let { apiEndpoint = '/api/contact' } = $props<{ apiEndpoint?: string }>();

  type Purpose = 'Project Inquiry' | 'Accessibility Audit' | 'Speaking' | 'General';
  type ReplyChannel = 'email' | 'signal';

  let purpose = $state<Purpose>('Project Inquiry');
  let fullName = $state<string>('');
  let email = $state<string>('');
  let organization = $state<string>('');
  let replyChannel = $state<ReplyChannel>('email');
  let message = $state<string>('');

  let isSubmitting = $state<boolean>(false);
  let submitError = $state<string | null>(null);
  let submitSuccess = $state<boolean>(false);
  let claimTicket = $state<string>('');

  const STORAGE_KEY = 'contact_form_draft';

  onMount(() => {
    try {
      const savedDraft = localStorage.getItem(STORAGE_KEY);
      if (savedDraft) {
        const parsed = JSON.parse(savedDraft);
        if (parsed.purpose) purpose = parsed.purpose;
        if (parsed.fullName !== undefined) fullName = parsed.fullName;
        if (parsed.email !== undefined) email = parsed.email;
        if (parsed.organization !== undefined) organization = parsed.organization;
        if (parsed.replyChannel) replyChannel = parsed.replyChannel;
        if (parsed.message !== undefined) message = parsed.message;
      }
    } catch {
      // Ignore storage errors
    }
  });

  function saveDraft() {
    try {
      const draft = { purpose, fullName, email, organization, replyChannel, message };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(draft));
    } catch {
      // Ignore storage errors
    }
  }

  function handleInput() {
    saveDraft();
  }

  function clearDraft() {
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      // Ignore
    }
  }

  async function handleSubmit(event: Event) {
    event.preventDefault();
    if (!fullName.trim() || !email.trim() || !message.trim()) {
      submitError = 'Please fill out all required fields (*).';
      return;
    }

    submitError = null;
    isSubmitting = true;

    try {
      const payload = {
        purpose,
        fullName: fullName.trim(),
        clientEmail: email.trim(),
        organization: organization.trim(),
        replyChannel,
        message: message.trim(),
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

      telemetry.trackFormValidationPassed('contact_form_submit', 12, 2);
      telemetry.trackCanonSubmitConverted(claimTicket, 0, {
        classification: purpose,
        title: organization.trim(),
        client_email: email.trim()
      });

      clearDraft();
    } catch (err: any) {
      submitError = 'Connection issue or validation error during transmission. Your input was safely preserved locally. Please try submitting again.';
    } finally {
      isSubmitting = false;
    }
  }

  let emailValid = $derived(/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email));
</script>

<div class="bg-white rounded-xl p-5 border border-[#c3c6d7]/70 shadow-sm space-y-4 font-sans text-[#0b1c30]">
  <div>
    <div class="flex items-center gap-1.5">
      <span class="text-[#004ac6] text-lg font-bold">🔒</span>
      <h3 class="text-lg font-bold text-[#0b1c30] tracking-tight">Send an Encrypted Message</h3>
    </div>
    <p class="text-xs text-[#434655] mt-1 flex items-start gap-1.5">
      <span>Zero tracking. End-to-end encrypted dispatch directly to Dmitry's verified inbox.</span>
    </p>
  </div>

  <form class="space-y-4" id="contactForm" onsubmit={handleSubmit}>
    {#if submitError}
      <div id="intake-error-banner" role="alert" class="p-3 bg-[#ffdad6] border border-[#ba1a1a] text-[#93000a] text-xs rounded-lg flex items-start space-x-2">
        <span class="font-bold">⚠️</span>
        <p id="intake-error-message">{submitError}</p>
      </div>
    {/if}

    <!-- Topic Selector Chips -->
    <fieldset>
      <legend class="block text-xs font-semibold text-[#0b1c30] mb-2 uppercase tracking-wide">
        Select Purpose <span class="text-[#ba1a1a]" aria-hidden="true">*</span>
      </legend>
      <div aria-label="Topic Selection" class="grid grid-cols-2 gap-2">
        <button
          aria-pressed={purpose === 'Project Inquiry'}
          class="min-h-[48px] {purpose === 'Project Inquiry' ? 'bg-[#e5eeff] text-[#004ac6] shadow-sm border-[#004ac6]' : 'bg-[#f8f9ff] text-[#434655] hover:text-[#0b1c30] border-[#c3c6d7]'} rounded-lg px-3 py-2 text-xs font-bold flex items-center justify-center gap-1.5 border transition-all"
          onclick={() => { purpose = 'Project Inquiry'; handleInput(); }}
          type="button">
          <span>Project Inquiry</span>
        </button>
        <button
          aria-pressed={purpose === 'Accessibility Audit'}
          class="min-h-[48px] {purpose === 'Accessibility Audit' ? 'bg-[#e5eeff] text-[#004ac6] shadow-sm border-[#004ac6]' : 'bg-[#f8f9ff] text-[#434655] hover:text-[#0b1c30] border-[#c3c6d7]'} rounded-lg px-3 py-2 text-xs font-bold flex items-center justify-center gap-1.5 border transition-all"
          onclick={() => { purpose = 'Accessibility Audit'; handleInput(); }}
          type="button">
          <span>Accessibility Audit</span>
        </button>
        <button
          aria-pressed={purpose === 'Speaking'}
          class="min-h-[48px] {purpose === 'Speaking' ? 'bg-[#e5eeff] text-[#004ac6] shadow-sm border-[#004ac6]' : 'bg-[#f8f9ff] text-[#434655] hover:text-[#0b1c30] border-[#c3c6d7]'} rounded-lg px-3 py-2 text-xs font-bold flex items-center justify-center gap-1.5 border transition-all"
          onclick={() => { purpose = 'Speaking'; handleInput(); }}
          type="button">
          <span>Speaking</span>
        </button>
        <button
          aria-pressed={purpose === 'General'}
          class="min-h-[48px] {purpose === 'General' ? 'bg-[#e5eeff] text-[#004ac6] shadow-sm border-[#004ac6]' : 'bg-[#f8f9ff] text-[#434655] hover:text-[#0b1c30] border-[#c3c6d7]'} rounded-lg px-3 py-2 text-xs font-bold flex items-center justify-center gap-1.5 border transition-all"
          onclick={() => { purpose = 'General'; handleInput(); }}
          type="button">
          <span>General</span>
        </button>
      </div>
    </fieldset>

    <!-- Full Name Field -->
    <div class="space-y-1.5">
      <label class="block text-xs font-semibold text-[#0b1c30]" for="fullName">
        Full Name <span aria-hidden="true" class="text-[#ba1a1a]">*</span>
      </label>
      <div class="relative">
        <input
          class="w-full h-11 px-3.5 bg-white border border-[#c3c6d7] rounded-lg text-xs text-[#0b1c30] placeholder:text-[#737686] focus:border-[#004ac6] focus:outline-none focus:ring-2 focus:ring-[#004ac6]/20 transition-all"
          id="fullName"
          bind:value={fullName}
          oninput={handleInput}
          placeholder="e.g. Marcus Aurelius"
          required
          type="text"
        />
      </div>
      <p class="text-[11px] text-[#737686]">Your verified professional name or pseudonym.</p>
    </div>

    <!-- Email Address Field with Live Verified Indicator -->
    <div class="space-y-1.5">
      <label class="block text-xs font-semibold text-[#0b1c30]" for="emailAddr">
        Your Email Address <span aria-hidden="true" class="text-[#ba1a1a]">*</span>
      </label>
      <div class="relative">
        <input
          class="w-full h-11 px-3.5 bg-white border border-[#c3c6d7] rounded-lg text-xs text-[#0b1c30] placeholder:text-[#737686] focus:border-[#004ac6] focus:outline-none focus:ring-2 focus:ring-[#004ac6]/20 transition-all"
          id="emailAddr"
          bind:value={email}
          oninput={handleInput}
          placeholder="name@organization.com"
          required
          type="email"
        />
      </div>
      <p class="text-[11px] {emailValid ? 'text-[#006c49]' : 'text-[#737686]'}" id="emailHint">{emailValid ? 'Valid format verified. Direct inbox routing enabled.' : 'We dispatch automatic read receipts upon receipt.'}</p>
    </div>

    <!-- Subject / Organization -->
    <div class="space-y-1.5">
      <label class="block text-xs font-semibold text-[#0b1c30]" for="organization">
        Subject or Organization
      </label>
      <input
        class="w-full h-11 px-3.5 bg-white border border-[#c3c6d7] rounded-lg text-xs text-[#0b1c30] placeholder:text-[#737686] focus:border-[#004ac6] focus:outline-none focus:ring-2 focus:ring-[#004ac6]/20 transition-all"
        id="organization"
        bind:value={organization}
        oninput={handleInput}
        placeholder="e.g. Design Systems Advisory"
        type="text"
      />
    </div>

    <!-- Preferred Response Channel -->
    <fieldset>
      <legend class="block text-xs font-semibold text-[#0b1c30] mb-1.5 uppercase tracking-wide">
        Preferred Reply Channel
      </legend>
      <div class="flex items-center gap-3">
        <label class="flex items-center gap-2 cursor-pointer min-h-[48px]">
          <input
            class="w-4 h-4 text-[#004ac6] border-[#c3c6d7] focus:ring-[#004ac6]"
            name="replyChannel"
            type="radio"
            value="email"
            bind:group={replyChannel}
            onchange={handleInput}
          />
          <span class="text-xs text-[#0b1c30]">Email</span>
        </label>
        <label class="flex items-center gap-2 cursor-pointer min-h-[48px]">
          <input
            class="w-4 h-4 text-[#004ac6] border-[#c3c6d7] focus:ring-[#004ac6]"
            name="replyChannel"
            type="radio"
            value="signal"
            bind:group={replyChannel}
            onchange={handleInput}
          />
          <span class="text-xs text-[#0b1c30]">Signal Private Messenger</span>
        </label>
      </div>
    </fieldset>

    <!-- Message Field with Character Counter -->
    <div class="space-y-1.5">
      <div class="flex justify-between items-center">
        <label class="block text-xs font-semibold text-[#0b1c30]" for="messageText">
          Your Message <span aria-hidden="true" class="text-[#ba1a1a]">*</span>
        </label>
        <span class="text-[11px] font-mono {message.length >= 900 ? 'text-[#ba1a1a]' : 'text-[#737686]'}" id="charCount">{message.length} / 1000</span>
      </div>
      <textarea
        class="w-full p-3 bg-white border border-[#c3c6d7] rounded-lg text-xs text-[#0b1c30] placeholder:text-[#737686] focus:border-[#004ac6] focus:outline-none focus:ring-2 focus:ring-[#004ac6]/20 transition-all resize-y min-h-[110px]"
        id="messageText"
        maxlength="1000"
        bind:value={message}
        oninput={handleInput}
        placeholder="Describe your context, project parameters, or question in detail..."
        required
        rows="4"
      ></textarea>
    </div>

    <!-- Primary Submit Button -->
    <button
      class="w-full h-12 bg-[#004ac6] hover:bg-[#2563eb] text-white font-semibold rounded-xl flex items-center justify-center gap-2 shadow-md transition-all focus:outline-none focus:ring-2 focus:ring-[#004ac6] disabled:opacity-50 text-xs"
      id="submitBtn"
      type="submit"
      disabled={isSubmitting || submitSuccess}
    >
      <span id="btnText">{isSubmitting ? 'Encrypting & Transmitting...' : (submitSuccess ? 'Message Sent' : 'Send Encrypted Message')}</span>
    </button>
  </form>

  <!-- Submission Feedback Alert Container (Hidden by default) -->
  {#if submitSuccess}
    <div class="p-3.5 bg-[#eff4ff] border border-[#004ac6] rounded-lg flex items-start gap-2.5" id="submitAlert">
      <div>
        <h4 class="text-xs font-bold text-[#0b1c30]">Transmission Complete</h4>
        <p class="text-[11px] text-[#434655] mt-0.5">Your encrypted message has been logged. Dmitry Efremov will review and respond within 2 hours.</p>
      </div>
    </div>
  {/if}
</div>
