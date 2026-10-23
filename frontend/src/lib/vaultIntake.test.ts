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

    const codenameInput = target.querySelector('#codename') as HTMLInputElement;
    expect(codenameInput).not.toBeNull();
    const codenameLabel = target.querySelector('label[for="codename"]');
    expect(codenameLabel).not.toBeNull();

    const executionSelect = target.querySelector('#execution-window') as HTMLSelectElement;
    expect(executionSelect).not.toBeNull();
    const executionLabel = target.querySelector('label[for="execution-window"]');
    expect(executionLabel).not.toBeNull();

    const emailInput = target.querySelector('#client-email-input') as HTMLInputElement;
    expect(emailInput).not.toBeNull();
    const emailLabel = target.querySelector('label[for="client-email-input"]');
    expect(emailLabel).not.toBeNull();

    const ndaCheckbox = target.querySelector('#nda-agree') as HTMLInputElement;
    expect(ndaCheckbox).not.toBeNull();
  });

  it('retains user typed content in localStorage draft on input so data survives disconnects or errors', async () => {
    component = mount(VaultIntakeView, { target });

    const codenameInput = target.querySelector('#codename') as HTMLInputElement;
    codenameInput.value = 'Project Sovereign Delta';
    codenameInput.dispatchEvent(new Event('input', { bubbles: true }));

    await new Promise(r => setTimeout(r, 10));

    const stored = localStorage.getItem('vault_intake_draft');
    expect(stored).not.toBeNull();
    const parsed = JSON.parse(stored!);
    expect(parsed.codename).toBe('Project Sovereign Delta');
  });

  it('handles connection or network drop gracefully while preserving typed input in draft', async () => {
    global.fetch = vi.fn().mockRejectedValue(new TypeError('Failed to fetch'));

    component = mount(VaultIntakeView, { target });

    const codenameInput = target.querySelector('#codename') as HTMLInputElement;
    codenameInput.value = 'Persistent Mandate Codename';
    codenameInput.dispatchEvent(new Event('input', { bubbles: true }));

    const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

    await new Promise(r => setTimeout(r, 50));

    const errorBanner = target.querySelector('#intake-error-banner');
    expect(errorBanner).not.toBeNull();
    expect(errorBanner?.textContent).toContain('Connection issue or validation error');

    // Input survives error
    expect(codenameInput.value).toBe('Persistent Mandate Codename');
    expect(localStorage.getItem('vault_intake_draft')).not.toBeNull();
  });

  it('shows visible success feedback indicating mandate submission and ratified NDA on successful submit', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ claimTicket: '#VLT-88219-X-9988' })
    });

    component = mount(VaultIntakeView, { target });

    const emailInput = target.querySelector('#client-email-input') as HTMLInputElement;
    emailInput.value = 'partner@enterprise.com';
    emailInput.dispatchEvent(new Event('input', { bubbles: true }));

    const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

    await new Promise(r => setTimeout(r, 50));

    const successBanner = target.querySelector('#intake-success-feedback');
    expect(successBanner).not.toBeNull();
    expect(successBanner?.textContent).toContain('Mandate Submitted & Protocol Ratified');
    expect(successBanner?.textContent).toContain('partner@enterprise.com');
    expect(target.querySelector('#claim-ticket-id')?.textContent).toBe('#VLT-88219-X-9988');
  });
});
