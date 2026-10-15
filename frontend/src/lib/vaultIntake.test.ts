// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount, unmount } from 'svelte';
import VaultIntakeView from '../components/VaultIntakeView.svelte';

describe('VaultIntakeView Component', () => {
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
    component = mount(VaultIntakeView, { target });

    const titleInput = target.querySelector('#submission-title-input') as HTMLInputElement;
    expect(titleInput).not.toBeNull();
    const titleLabel = target.querySelector('label[for="submission-title-input"]');
    expect(titleLabel).not.toBeNull();

    const narrativeTextarea = target.querySelector('#narrative-textarea') as HTMLTextAreaElement;
    expect(narrativeTextarea).not.toBeNull();
    const narrativeLabel = target.querySelector('label[for="narrative-textarea"]');
    expect(narrativeLabel).not.toBeNull();

    const emailInput = target.querySelector('#client-email-input') as HTMLInputElement;
    expect(emailInput).not.toBeNull();
    const emailLabel = target.querySelector('label[for="client-email-input"]');
    expect(emailLabel).not.toBeNull();
  });

  it('retains user typed content in localStorage draft on input so data survives disconnects or errors', async () => {
    component = mount(VaultIntakeView, { target });

    const titleInput = target.querySelector('#submission-title-input') as HTMLInputElement;
    const narrativeTextarea = target.querySelector('#narrative-textarea') as HTMLTextAreaElement;

    titleInput.value = 'Critical Security Vulnerability';
    titleInput.dispatchEvent(new Event('input', { bubbles: true }));

    narrativeTextarea.value = 'Detailed technical disclosure text...';
    narrativeTextarea.dispatchEvent(new Event('input', { bubbles: true }));

    await new Promise(r => setTimeout(r, 10));

    const stored = localStorage.getItem('vault_intake_draft');
    expect(stored).not.toBeNull();
    const parsed = JSON.parse(stored!);
    expect(parsed.title).toBe('Critical Security Vulnerability');
    expect(parsed.narrative).toBe('Detailed technical disclosure text...');
  });

  it('handles connection or network drop gracefully while preserving typed input in draft', async () => {
    global.fetch = vi.fn().mockRejectedValue(new TypeError('Failed to fetch'));

    component = mount(VaultIntakeView, { target });

    const titleInput = target.querySelector('#submission-title-input') as HTMLInputElement;
    const narrativeTextarea = target.querySelector('#narrative-textarea') as HTMLTextAreaElement;

    titleInput.value = 'Persistent Draft Title';
    titleInput.dispatchEvent(new Event('input', { bubbles: true }));

    narrativeTextarea.value = 'Persistent Draft Narrative...';
    narrativeTextarea.dispatchEvent(new Event('input', { bubbles: true }));

    const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

    await new Promise(r => setTimeout(r, 50));

    const errorBanner = target.querySelector('#intake-error-banner');
    expect(errorBanner).not.toBeNull();
    expect(errorBanner?.textContent).toContain('Connection issue or validation error');

    // Input survives error
    expect(titleInput.value).toBe('Persistent Draft Title');
    expect(narrativeTextarea.value).toBe('Persistent Draft Narrative...');
    expect(localStorage.getItem('vault_intake_draft')).not.toBeNull();
  });

  it('shows visible success feedback indicating an email acknowledgement was sent on successful submit', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ claimTicket: '0xabc123...456' })
    });

    component = mount(VaultIntakeView, { target });

    const emailInput = target.querySelector('#client-email-input') as HTMLInputElement;
    emailInput.value = 'client@example.com';
    emailInput.dispatchEvent(new Event('input', { bubbles: true }));

    const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

    await new Promise(r => setTimeout(r, 50));

    const successBanner = target.querySelector('#intake-success-feedback');
    expect(successBanner).not.toBeNull();
    expect(successBanner?.textContent).toContain('Payload Encrypted & Submitted Successfully');
    expect(successBanner?.textContent).toContain('An email acknowledgement has been sent to client@example.com');
  });
});
