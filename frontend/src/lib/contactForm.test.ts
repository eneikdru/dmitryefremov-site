// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount, unmount } from 'svelte';
import ContactFormView from '../components/ContactFormView.svelte';

describe('ContactFormView Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    localStorage.clear();
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

  it('renders input fields with accessible labels (WCAG 2.1 AA)', () => {
    component = mount(ContactFormView, { target });

    const titleInput = target.querySelector('#fullName') as HTMLInputElement;
    expect(titleInput).not.toBeNull();
    const titleLabel = target.querySelector('label[for="fullName"]');
    expect(titleLabel).not.toBeNull();

    const messageTextarea = target.querySelector('#messageText') as HTMLTextAreaElement;
    expect(messageTextarea).not.toBeNull();
    const messageLabel = target.querySelector('label[for="messageText"]');
    expect(messageLabel).not.toBeNull();

    const emailInput = target.querySelector('#emailAddr') as HTMLInputElement;
    expect(emailInput).not.toBeNull();
    const emailLabel = target.querySelector('label[for="emailAddr"]');
    expect(emailLabel).not.toBeNull();
  });

  it('retains user typed content in localStorage draft on input so data survives disconnects or errors', async () => {
    component = mount(ContactFormView, { target });

    const fullNameInput = target.querySelector('#fullName') as HTMLInputElement;
    const messageTextarea = target.querySelector('#messageText') as HTMLTextAreaElement;
    const emailInput = target.querySelector('#emailAddr') as HTMLInputElement;

    fullNameInput.value = 'Test User';
    fullNameInput.dispatchEvent(new Event('input', { bubbles: true }));

    messageTextarea.value = 'Detailed technical disclosure text...';
    messageTextarea.dispatchEvent(new Event('input', { bubbles: true }));

    emailInput.value = 'test@example.com';
    emailInput.dispatchEvent(new Event('input', { bubbles: true }));

    await new Promise(r => setTimeout(r, 10));

    const stored = localStorage.getItem('contact_form_draft');
    expect(stored).not.toBeNull();
    const parsed = JSON.parse(stored!);
    expect(parsed.fullName).toBe('Test User');
    expect(parsed.message).toBe('Detailed technical disclosure text...');
    expect(parsed.email).toBe('test@example.com');
  });

  it('handles connection or network drop gracefully while preserving typed input in draft', async () => {
    global.fetch = vi.fn().mockRejectedValue(new TypeError('Failed to fetch'));

    component = mount(ContactFormView, { target });

    const fullNameInput = target.querySelector('#fullName') as HTMLInputElement;
    const messageTextarea = target.querySelector('#messageText') as HTMLTextAreaElement;
    const emailInput = target.querySelector('#emailAddr') as HTMLInputElement;

    fullNameInput.value = 'Persistent User';
    fullNameInput.dispatchEvent(new Event('input', { bubbles: true }));

    messageTextarea.value = 'Persistent Draft Narrative...';
    messageTextarea.dispatchEvent(new Event('input', { bubbles: true }));

    emailInput.value = 'test@example.com';
    emailInput.dispatchEvent(new Event('input', { bubbles: true }));

    const form = target.querySelector('#contactForm') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

    await new Promise(r => setTimeout(r, 50));

    const errorBanner = target.querySelector('#intake-error-banner');
    expect(errorBanner).not.toBeNull();
    expect(errorBanner?.textContent).toContain('Connection issue or validation error');

    // Input survives error
    expect(fullNameInput.value).toBe('Persistent User');
    expect(messageTextarea.value).toBe('Persistent Draft Narrative...');
    expect(localStorage.getItem('contact_form_draft')).not.toBeNull();
  });

  it('shows visible success feedback indicating an email acknowledgement was sent on successful submit', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ claimTicket: '0xabc123...456' })
    });

    component = mount(ContactFormView, { target });

    const fullNameInput = target.querySelector('#fullName') as HTMLInputElement;
    const messageTextarea = target.querySelector('#messageText') as HTMLTextAreaElement;
    const emailInput = target.querySelector('#emailAddr') as HTMLInputElement;

    fullNameInput.value = 'Test User';
    fullNameInput.dispatchEvent(new Event('input', { bubbles: true }));

    messageTextarea.value = 'Test message here...';
    messageTextarea.dispatchEvent(new Event('input', { bubbles: true }));

    emailInput.value = 'client@example.com';
    emailInput.dispatchEvent(new Event('input', { bubbles: true }));

    const form = target.querySelector('#contactForm') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

    await new Promise(r => setTimeout(r, 50));

    const successBanner = target.querySelector('#submitAlert');
    expect(successBanner).not.toBeNull();
    expect(successBanner?.textContent).toContain('Transmission Complete');
    expect(successBanner?.textContent).toContain('Your encrypted message has been logged');
  });
});
