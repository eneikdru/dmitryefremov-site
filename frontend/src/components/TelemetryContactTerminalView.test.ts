import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { mount, unmount } from 'svelte';
import TelemetryContactTerminalView from './TelemetryContactTerminalView.svelte';
import { telemetry } from '../lib/telemetry';

describe('TelemetryContactTerminalView UI Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    telemetry.clearEvents();
    // Clear cookies in jsdom document
    document.cookie.split(';').forEach(cookie => {
      const eqPos = cookie.indexOf('=');
      const name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
      document.cookie = `${name.trim()}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/`;
    });
    target = document.createElement('div');
    document.body.appendChild(target);
  });

  afterEach(() => {
    if (component) {
      unmount(component);
    }
    if (target && target.parentNode) {
      target.parentNode.removeChild(target);
    }
  });

  it('dispatches correct analytics payload when a user clicks a contact link', async () => {
    component = mount(TelemetryContactTerminalView, { target });

    const tgLink = target.querySelector('#link-tg') as HTMLAnchorElement;
    expect(tgLink).not.toBeNull();

    tgLink.click();

    const events = telemetry.getEvents();
    expect(events.length).toBe(1);
    expect(events[0].type).toBe('outbound_click');
    expect(events[0].payload.destination).toBe('https://t.me/efremov_mvp');
    expect(events[0].payload.label).toBe('Telegram Channel');
    expect(events[0].payload.platform).toBe('Telegram');
  });

  it('audits analytics setup to confirm no tracking cookies are present', async () => {
    component = mount(TelemetryContactTerminalView, { target });

    // Click link
    const ytLink = target.querySelector('#link-yt') as HTMLAnchorElement;
    expect(ytLink).not.toBeNull();
    ytLink.click();

    // Verify no tracking cookies
    expect(document.cookie).toBe('');
    expect(telemetry.verifyPrivacyCompliance()).toBe(true);
  });
});
