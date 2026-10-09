// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import TelemetryContactTerminalView from '../components/TelemetryContactTerminalView.svelte';
import { mount, unmount } from 'svelte';
import { telemetry } from './telemetry';

describe('TelemetryHub Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    telemetry.clearEvents();
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
    const themeRoot = target.querySelector('#theme-root') as HTMLElement;

    expect(themeRoot.classList.contains('dark-void')).toBe(true);

    themeBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(themeRoot.classList.contains('light-monochrome')).toBe(true);

    themeBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(themeRoot.classList.contains('dark-void')).toBe(true);
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

  it('dispatches analytics payload when user clicks contact links and confirms zero tracking cookies', async () => {
    component = mount(TelemetryContactTerminalView, { target });

    const telegramLink = target.querySelector('#link-tg') as HTMLAnchorElement;
    expect(telegramLink).not.toBeNull();

    telegramLink.click();
    await new Promise(r => setTimeout(r, 0));

    const events = telemetry.getEvents();
    expect(events.length).toBe(1);
    expect(events[0].type).toBe('outbound_click');
    expect(events[0].payload).toEqual({
      destination: 'https://t.me/efremov_mvp',
      label: 'Telegram Channel',
      platform: 'Telegram'
    });

    expect(target.textContent).toContain('[CLICK] Telegram: https://t.me/efremov_mvp');
    expect(document.cookie).toBe('');
    expect(telemetry.verifyPrivacyCompliance()).toBe(true);
  });

  it('switches to contact tab in TelemetryHub and dispatches click analytics payload', async () => {
    component = mount(TelemetryHub, { target });

    const contactTabBtn = target.querySelector('#tab-btn-contact') as HTMLButtonElement;
    contactTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#contact-terminal-root')).not.toBeNull();

    const ytLink = target.querySelector('#link-yt') as HTMLAnchorElement;
    expect(ytLink).not.toBeNull();

    ytLink.click();
    await new Promise(r => setTimeout(r, 0));

    const events = telemetry.getEvents();
    expect(events.length).toBe(1);
    expect(events[0].type).toBe('outbound_click');
    expect(events[0].payload.platform).toBe('YouTube');
    expect(events[0].payload.destination).toBe('https://youtube.com/@efremov_dmitriy');

    expect(document.cookie).toBe('');
    expect(telemetry.verifyPrivacyCompliance()).toBe(true);
  });
});
