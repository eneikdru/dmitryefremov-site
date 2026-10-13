// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';
import { resetConsentStatus, getConsentStatus } from './consent';

describe('TelemetryHub Component & CMP Integration', () => {
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

  it('renders hero title, subtitle, and scrollytelling manifest correctly', () => {
    component = mount(TelemetryHub, { target });

    expect(target.textContent).toContain('DMITRY EFREMOV');
    expect(target.textContent).toContain('RADICAL');
    expect(target.textContent).toContain('FORM //');
    expect(target.textContent).toContain('AXIOM: ARCHITECTURE WITHOUT MIMICRY');
  });

  it('switches palette theme when theme toggle button is clicked', async () => {
    component = mount(TelemetryHub, { target });
    const themeBtn = target.querySelector('#theme-toggle-btn') as HTMLButtonElement;
    const themeRoot = target.querySelector('#theme-root') as HTMLElement;

    expect(themeRoot.classList.contains('dark-void')).toBe(true);

    themeBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(themeRoot.classList.contains('light-monochrome')).toBe(true);

    themeBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(themeRoot.classList.contains('dark-void')).toBe(true);
  });

  it('navigates seamlessly between scrollytelling, article stream and podcast hub', async () => {
    component = mount(TelemetryHub, { target });

    // Initial state: Scrollytelling interface at root path
    expect(target.textContent).toContain('CH.01 // ORIGIN MATRIX');
    expect(target.textContent).toContain('KINETIC EQUILIBRIUM');

    // Navigate to Podcast Media tab
    const podcastTabBtn = target.querySelector('#tab-btn-podcast') as HTMLButtonElement;
    expect(podcastTabBtn).not.toBeNull();

    podcastTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    // Podcast hub loads
    expect(target.textContent).toContain('Подкаст «Системы и реальность»');
    expect(target.textContent).toContain('ТАЙМКОДЫ И ТЕМЫ');

    // Navigate to Article Stream tab
    const articleTabBtn = target.querySelector('#tab-btn-article') as HTMLButtonElement;
    articleTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('Articles & Thought Stream');
  });

  it('opens and closes Imprint legal modal when clicking footer Imprint trigger', async () => {
    component = mount(TelemetryHub, { target });

    expect(target.querySelector('#imprint-modal')).toBeNull();

    const imprintToggle = target.querySelector('#imprint-toggle-btn') as HTMLButtonElement;
    imprintToggle.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#imprint-modal')).not.toBeNull();
    expect(target.textContent).toContain('Site Operator:');

    const closeBtn = target.querySelector('#imprint-close-btn') as HTMLButtonElement;
    closeBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#imprint-modal')).toBeNull();
  });

  it('renders TrustGuard CMP banner on load for new visitor and responds to Accept button', async () => {
    expect(getConsentStatus()).toBeNull();
    component = mount(TelemetryHub, { target });

    const overlay = target.querySelector('#cmp-modal-overlay');
    expect(overlay).not.toBeNull();
    expect(overlay?.getAttribute('role')).toBe('dialog');
    expect(overlay?.getAttribute('aria-modal')).toBe('true');
    expect(target.textContent).toContain('Privacy & Consent Management');
    expect(target.textContent).toContain('IAB TCF v2.2 Compliant');
    expect(target.textContent).toContain('Category Permissions');

    const acceptBtn = target.querySelector('#cmp-accept-btn') as HTMLButtonElement;
    expect(acceptBtn).not.toBeNull();

    acceptBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(getConsentStatus()).toBe('accepted');
    expect(target.querySelector('#cmp-modal-overlay')).toBeNull();
  });

  it('allows declining trackers and re-opening CMP banner via footer persistent privacy link', async () => {
    expect(getConsentStatus()).toBeNull();
    component = mount(TelemetryHub, { target });

    const declineBtn = target.querySelector('#cmp-decline-btn') as HTMLButtonElement;
    expect(declineBtn).not.toBeNull();

    declineBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(getConsentStatus()).toBe('declined');
    expect(target.querySelector('#cmp-modal-overlay')).toBeNull();

    // Click persistent privacy link in footer
    const privacyLink = target.querySelector('#cmp-persistent-link') as HTMLButtonElement;
    expect(privacyLink).not.toBeNull();

    privacyLink.click();
    await new Promise(r => setTimeout(r, 0));

    const reOpenedOverlay = target.querySelector('#cmp-modal-overlay');
    expect(reOpenedOverlay).not.toBeNull();
    expect(target.textContent).toContain('TRACKERS_BLOCKED');
  });
});
