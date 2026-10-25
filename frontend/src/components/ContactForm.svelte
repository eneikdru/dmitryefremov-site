<script lang="ts">
  import { telemetry } from '../lib/telemetry';

  let fullName = '';
  let emailAddr = '';
  let organization = '';
  let replyChannel = 'email';
  let messageText = '';

  let submitSuccess = false;
  let isSubmitting = false;

  function handleFormSubmit(event: Event) {
    event.preventDefault();
    if (!fullName || !emailAddr || !messageText) {
      return; // Handled by HTML5 required attributes natively but extra safeguard
    }

    isSubmitting = true;

    // Simulate API submission
    setTimeout(() => {
      isSubmitting = false;
      submitSuccess = true;
      telemetry.trackFormValidationPassed('contact_form_submit', 0, 0);
    }, 1000);
  }
</script>

<div class="max-w-2xl mx-auto p-6 {submitSuccess ? 'bg-[#eff4ff] border-[#006c49]' : 'bg-white border-[#c3c6d7]'} rounded-xl border shadow-sm font-sans text-[#0b1c30]">
  {#if submitSuccess}
    <div id="submitAlert" class="p-3.5 bg-[#eff4ff] border border-[#004ac6] rounded-lg flex items-start gap-2.5">
      <span class="text-[#006c49] text-xl shrink-0">✓</span>
      <div>
        <h4 class="text-sm font-bold text-[#0b1c30]">Transmission Complete</h4>
        <p class="text-xs text-[#434655] mt-0.5">Your message has been received successfully.</p>
      </div>
    </div>
  {:else}
    <form id="contactForm" on:submit={handleFormSubmit} class="space-y-6">
      <div class="space-y-1">
        <label for="fullName" class="block text-sm font-bold text-[#0b1c30]">
          Full Name <span class="text-[#ba1a1a]" aria-hidden="true">*</span>
        </label>
        <input
          id="fullName"
          bind:value={fullName}
          required
          type="text"
          placeholder="e.g. Marcus Aurelius"
          class="w-full px-3 py-2 border border-[#c3c6d7] rounded-lg focus:ring-2 focus:ring-[#004ac6] focus:border-[#004ac6] outline-none text-sm text-[#0b1c30]"
        />
      </div>

      <div class="space-y-1">
        <label for="emailAddr" class="block text-sm font-bold text-[#0b1c30]">
          Your Email Address <span class="text-[#ba1a1a]" aria-hidden="true">*</span>
        </label>
        <input
          id="emailAddr"
          bind:value={emailAddr}
          required
          type="email"
          placeholder="name@organization.com"
          class="w-full px-3 py-2 border border-[#c3c6d7] rounded-lg focus:ring-2 focus:ring-[#004ac6] focus:border-[#004ac6] outline-none text-sm text-[#0b1c30]"
        />
      </div>

      <div class="space-y-1">
        <label for="organization" class="block text-sm font-bold text-[#0b1c30]">
          Subject or Organization
        </label>
        <input
          id="organization"
          bind:value={organization}
          type="text"
          placeholder="e.g. Design Systems Advisory"
          class="w-full px-3 py-2 border border-[#c3c6d7] rounded-lg focus:ring-2 focus:ring-[#004ac6] focus:border-[#004ac6] outline-none text-sm text-[#0b1c30]"
        />
      </div>

      <div class="space-y-1">
        <label for="messageText" class="block text-sm font-bold text-[#0b1c30]">
          Your Message <span class="text-[#ba1a1a]" aria-hidden="true">*</span>
        </label>
        <div class="flex justify-between items-center text-xs text-[#434655]">
          <span id="charCount">{messageText.length} / 1000</span>
        </div>
        <textarea
          id="messageText"
          bind:value={messageText}
          required
          maxlength="1000"
          rows="4"
          placeholder="Describe your context..."
          class="w-full px-3 py-2 border border-[#c3c6d7] rounded-lg focus:ring-2 focus:ring-[#004ac6] focus:border-[#004ac6] outline-none text-sm text-[#0b1c30] resize-y"
        ></textarea>
      </div>

      <button
        id="submitBtn"
        type="submit"
        disabled={isSubmitting}
        class="w-full py-3 bg-[#004ac6] hover:bg-[#2563eb] text-white font-bold rounded-lg transition-colors flex items-center justify-center gap-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-[#004ac6] focus:ring-offset-2 disabled:opacity-75"
      >
        <span>{isSubmitting ? 'Sending...' : 'Send Message'}</span>
      </button>
    </form>
  {/if}
</div>
