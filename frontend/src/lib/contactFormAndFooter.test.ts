// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount, unmount } from 'svelte';
import VaultIntakeView from '../components/VaultIntakeView.svelte';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { resetConsentStatus } from './consent';

describe('Contact Form & Footer Identity Integration', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    localStorage.clear();
    resetConsentStatus();
    vi.restoreAllMocks();
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('renders immediate success feedback without page redirection on successful form submit', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ claimTicket: '0x38bdf8...99402' })
    });

    component = mount(VaultIntakeView, { target });

    const titleInput = target.querySelector('#submission-title-input') as HTMLInputElement;
    const narrativeTextarea = target.querySelector('#narrative-textarea') as HTMLTextAreaElement;
    const emailInput = target.querySelector('#client-email-input') as HTMLInputElement;

    titleInput.value = 'Inquiry regarding A2A protocols';
    titleInput.dispatchEvent(new Event('input', { bubbles: true }));

    narrativeTextarea.value = 'Requesting specification details on autonomous agent identity verifications.';
    narrativeTextarea.dispatchEvent(new Event('input', { bubbles: true }));

    emailInput.value = 'agent@domain.org';
    emailInput.dispatchEvent(new Event('input', { bubbles: true }));

    const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

    await new Promise(r => setTimeout(r, 50));

    const successBanner = target.querySelector('#intake-success-feedback');
    expect(successBanner).not.toBeNull();
    expect(successBanner?.getAttribute('aria-live')).toBe('polite');
    expect(successBanner?.textContent).toContain('Payload Encrypted & Submitted Successfully');
    expect(successBanner?.textContent).toContain('An email acknowledgement has been sent to agent@domain.org');
    expect(target.querySelector('#claim-ticket-id')?.textContent).toBe('0x38bdf8...99402');
  });

  it('retains typed text in form fields when a validation or connection error occurs', async () => {
    global.fetch = vi.fn().mockRejectedValue(new Error('Network offline'));

    component = mount(VaultIntakeView, { target });

    const titleInput = target.querySelector('#submission-title-input') as HTMLInputElement;
    const narrativeTextarea = target.querySelector('#narrative-textarea') as HTMLTextAreaElement;

    const testTitle = 'Retained Title Text';
    const testNarrative = 'Retained Narrative Body Text';

    titleInput.value = testTitle;
    titleInput.dispatchEvent(new Event('input', { bubbles: true }));

    narrativeTextarea.value = testNarrative;
    narrativeTextarea.dispatchEvent(new Event('input', { bubbles: true }));

    const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

    await new Promise(r => setTimeout(r, 50));

    // Error banner is present
    expect(target.querySelector('#intake-error-banner')).not.toBeNull();

    // Typed inputs remain unchanged
    expect(titleInput.value).toBe(testTitle);
    expect(narrativeTextarea.value).toBe(testNarrative);
  });

  it('meets WCAG 2.1 AA requirements with properly labelled controls and aria attributes', () => {
    component = mount(VaultIntakeView, { target });

    const controls = [
      { id: 'submission-title-input', labelText: 'Subject / Brief Title' },
      { id: 'narrative-textarea', labelText: 'Confidential Narrative & Technical Context' },
      { id: 'client-email-input', labelText: 'Notification Email' }
    ];

    for (const ctrl of controls) {
      const inputEl = target.querySelector(`#${ctrl.id}`);
      expect(inputEl).not.toBeNull();
      const labelEl = target.querySelector(`label[for="${ctrl.id}"]`);
      expect(labelEl).not.toBeNull();
      expect(labelEl?.textContent).toContain(ctrl.labelText);
    }
  });

  it('renders footer social links pointing to Dmitry Efremov and accessible Impressum', async () => {
    component = mount(TelemetryHub, { target });

    const footer = target.querySelector('#main-footer');
    expect(footer).not.toBeNull();

    const tgLink = target.querySelector('#footer-link-tg') as HTMLAnchorElement;
    expect(tgLink).not.toBeNull();
    expect(tgLink.getAttribute('href')).toBe('https://t.me/efremov_mvp');

    const ytLink = target.querySelector('#footer-link-yt') as HTMLAnchorElement;
    expect(ytLink).not.toBeNull();
    expect(ytLink.getAttribute('href')).toBe('https://youtube.com/@dmitrii.efremov');

    const ghLink = target.querySelector('#footer-link-gh') as HTMLAnchorElement;
    expect(ghLink).not.toBeNull();
    expect(ghLink.getAttribute('href')).toBe('https://github.com/dmitryefremov');

    const liLink = target.querySelector('#footer-link-li') as HTMLAnchorElement;
    expect(liLink).not.toBeNull();
    expect(liLink.getAttribute('href')).toBe('https://linkedin.com/in/dmitryefremov');

    // Impressum modal check
    const imprintBtn = target.querySelector('#imprint-toggle-btn') as HTMLButtonElement;
    expect(imprintBtn).not.toBeNull();

    imprintBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#imprint-modal')).not.toBeNull();
    expect(target.textContent).toContain('Дмитрий Ефремов (Dmitry Efremov)');
    expect(target.textContent).toContain('contact@dmitryefremov.com');
  });
});
