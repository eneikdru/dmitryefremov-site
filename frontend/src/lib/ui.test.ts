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

  it('renders hero title, subtitle, and manifest correctly', () => {
    component = mount(TelemetryHub, { target });

    expect(target.textContent).toContain('Дмитрий Ефремов');
    expect(target.textContent).toContain('Системы, логика мышления и автономные процессы');
    expect(target.textContent).toContain('«Ваш код и ваши решения — это то, кто вы есть на самом деле.»');
  });

  it('switches palette theme when theme toggle button is clicked', async () => {
    component = mount(TelemetryHub, { target });
    const themeBtn = target.querySelector('#theme-toggle-btn') as HTMLButtonElement;
    const themeRoot = target.querySelector('#telemetry-hub-root') as HTMLElement;

    expect(themeRoot.classList.contains('bg-[#090a0f]')).toBe(true);

    themeBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(themeRoot.classList.contains('bg-[#fafafa]')).toBe(true);

    themeBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(themeRoot.classList.contains('bg-[#090a0f]')).toBe(true);
  });

  it('navigates seamlessly between article stream and podcast hub, displaying active view', async () => {
    component = mount(TelemetryHub, { target, props: { initialTab: 'article' } });

    // Initial state: Article Stream view loaded
    expect(target.textContent).toContain('Articles & Thought Stream');

    // Navigate to Podcast Media tab
    const podcastTabBtn = target.querySelector('#tab-btn-podcast') as HTMLButtonElement;
    expect(podcastTabBtn).not.toBeNull();

    podcastTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    // Podcast hub view loaded
    expect(target.textContent).toContain('Подкаст «Системы и реальность»');

    // Navigate back to Article Stream tab
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
    expect(target.textContent).toContain('Владелец сайта:');

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
