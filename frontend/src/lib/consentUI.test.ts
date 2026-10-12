// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import ConsentManager from '../components/ConsentManager.svelte';
import { resetConsentStatus, getConsentStatus, isTelemetryAllowed, setConsentStatus } from './consent';
import { mount, unmount } from 'svelte';

describe('ConsentManager UI Accessibility & Interaction Unit Tests', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    resetConsentStatus();
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('presents consent banner on new visitor session and blocks telemetry by default', () => {
    component = mount(ConsentManager, { target });

    expect(getConsentStatus()).toBeNull();
    expect(isTelemetryAllowed()).toBe(false);

    const banner = target.querySelector('#cmp-modal-overlay');
    expect(banner).not.toBeNull();
    expect(target.textContent).toContain('Consent & Privacy Management');
    expect(target.textContent).toContain('GDPR and ePrivacy rights');
  });

  it('provides labelled form controls for consent categories compliant with WCAG 2.1 AA', () => {
    component = mount(ConsentManager, { target });

    const necessaryToggle = target.querySelector('#cmp-toggle-necessary') as HTMLInputElement;
    expect(necessaryToggle).not.toBeNull();
    expect(necessaryToggle.disabled).toBe(true);
    expect(necessaryToggle.getAttribute('aria-label')).toContain('Strictly Necessary');

    const analyticsToggle = target.querySelector('#cmp-toggle-analytics') as HTMLInputElement;
    expect(analyticsToggle).not.toBeNull();
    expect(analyticsToggle.getAttribute('aria-label')).toContain('Analytics and Performance');

    const functionalToggle = target.querySelector('#cmp-toggle-functional') as HTMLInputElement;
    expect(functionalToggle).not.toBeNull();
    expect(functionalToggle.getAttribute('aria-label')).toContain('Functional Preferences');
  });

  it('accepts tracking and enables telemetry when ACCEPT button is clicked', async () => {
    component = mount(ConsentManager, { target });

    const acceptBtn = target.querySelector('#cmp-accept-btn') as HTMLButtonElement;
    expect(acceptBtn).not.toBeNull();

    acceptBtn.click();
    await new Promise((r) => setTimeout(r, 0));

    expect(getConsentStatus()).toBe('accepted');
    expect(isTelemetryAllowed()).toBe(true);
  });

  it('declines tracking and blocks telemetry when DECLINE button is clicked', async () => {
    component = mount(ConsentManager, { target });

    const declineBtn = target.querySelector('#cmp-decline-btn') as HTMLButtonElement;
    expect(declineBtn).not.toBeNull();

    declineBtn.click();
    await new Promise((r) => setTimeout(r, 0));

    expect(getConsentStatus()).toBe('declined');
    expect(isTelemetryAllowed()).toBe(false);
  });

  it('allows reopening preferences when isOpen prop is set', async () => {
    setConsentStatus('accepted');
    component = mount(ConsentManager, { target, props: { isOpen: true } });

    const banner = target.querySelector('#cmp-modal-overlay');
    expect(banner).not.toBeNull();
    expect(target.textContent).toContain('Active Choice:');
    expect(target.textContent).toContain('TRACKERS_ALLOWED');
  });
});
