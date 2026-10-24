// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import CanonGraphTelemetryView from '../components/CanonGraphTelemetryView.svelte';
import { telemetry } from './telemetry';
import { setConsentStatus, resetConsentStatus } from './consent';
import { mount, unmount } from 'svelte';

describe('CanonGraphTelemetryView Component Verification', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    resetConsentStatus();
    setConsentStatus('accepted');
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

  it('renders graph navigation section with cross-links', () => {
    component = mount(CanonGraphTelemetryView, { target });
    expect(target.textContent).toContain('Canon Navigation & Telemetry');
    expect(target.textContent).toContain('Узлы Канона и Перекрёстные Ссылки');

    const crossLinks = target.querySelectorAll('a');
    expect(crossLinks.length).toBeGreaterThan(0);
  });

  it('dispatches graph_navigation telemetry event when a cross-link is clicked', async () => {
    component = mount(CanonGraphTelemetryView, { target });
    const crossLinks = target.querySelectorAll('a');

    (crossLinks[0] as HTMLAnchorElement).click();
    await new Promise((r) => setTimeout(r, 20));

    const events = telemetry.getEvents();
    const navEvents = events.filter(e => e.type === 'graph_navigation');
    expect(navEvents).toHaveLength(1);
    expect(navEvents[0].payload).toHaveProperty('sourceNodeId');
    expect(navEvents[0].payload).toHaveProperty('targetNodeId');
  });

  it('opens search dialog when ⌘K button is clicked and dispatches search_utilization on search selection', async () => {
    component = mount(CanonGraphTelemetryView, { target });

    const cmdKBtn = target.querySelector('#cmd-k-trigger') as HTMLButtonElement;
    expect(cmdKBtn).not.toBeNull();
    cmdKBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    const searchInput = target.querySelector('#search-input') as HTMLInputElement;
    expect(searchInput).not.toBeNull();

    searchInput.value = 'JWT';
    searchInput.dispatchEvent(new Event('input'));
    await new Promise((r) => setTimeout(r, 20));

    const searchButtons = target.querySelectorAll('#search-modal button');
    const jwtBtn = Array.from(searchButtons).find(btn => btn.textContent?.includes('JWT Spec')) as HTMLButtonElement;
    expect(jwtBtn).not.toBeNull();

    jwtBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    const events = telemetry.getEvents();
    const searchEvents = events.filter(e => e.type === 'search_utilization');
    expect(searchEvents).toHaveLength(1);
    expect(searchEvents[0].payload.query).toBe('JWT');
    expect(searchEvents[0].payload.selectedResultId).toBe('node-jwt-sec');
  });
});
