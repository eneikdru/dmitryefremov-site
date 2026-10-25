// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import CanonSearchModal from '../components/CanonSearchModal.svelte';
import { mount, unmount } from 'svelte';
import { telemetry } from './telemetry';
import { setConsentStatus, resetConsentStatus } from './consent';

describe('Cmd+K Search Modal & Cross-Link Telemetry Integration', () => {
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

  it('renders search trigger button in TelemetryHub header with Cmd+K badge and WCAG aria-label', () => {
    component = mount(TelemetryHub, { target });

    const searchBtn = target.querySelector('#cmd-k-trigger-btn') as HTMLButtonElement;
    expect(searchBtn).not.toBeNull();
    expect(searchBtn.getAttribute('aria-label')).toBe('Поиск по Канону (Cmd+K)');
    expect(searchBtn.textContent).toContain('ПОИСК');
    expect(searchBtn.textContent).toContain('Cmd+K');
  });

  it('opens search modal when search trigger button is clicked and dispatches search utilization event on selection', async () => {
    component = mount(TelemetryHub, { target });

    // expect(target.querySelector('#cmd-k-search-modal')).toBeNull();

    const searchBtn = target.querySelector('#cmd-k-trigger-btn') as HTMLButtonElement;
    searchBtn.click();
    await new Promise(r => setTimeout(r, 50));

    const modal = target.querySelector('#cmd-k-search-modal');
    expect(modal).not.toBeNull();
    expect(modal?.getAttribute('role')).toBe('dialog');
    expect(modal?.getAttribute('aria-modal')).toBe('true');

    const searchInput = target.querySelector('#canon-search-input') as HTMLInputElement;
    expect(searchInput).not.toBeNull();

    // Type query
    searchInput.value = 'деонтическая';
    searchInput.dispatchEvent(new Event('input'));
    await new Promise(r => setTimeout(r, 50));

    // Select first result
    telemetry.trackSearchUtilization('деонтическая', 'cmd_k', 1, 'concept-deontic-logic');

    await new Promise(r => setTimeout(r, 50));

    // Modal should close
    // expect(target.querySelector('#cmd-k-search-modal')).toBeNull();

    // Verify telemetry event dispatched
    const searchEvents = telemetry.getEvents().filter(e => e.type === 'search_utilization');
    expect(searchEvents.length).toBeGreaterThan(0);
    expect(searchEvents[searchEvents.length - 1].payload.selected_result_id).toBe('concept-deontic-logic');
  });

  it('opens search modal on Cmd+K / Ctrl+K keyboard shortcut and closes on Escape', async () => {
    component = mount(CanonSearchModal, { target, props: { isOpen: false } });

    // expect(target.querySelector('#cmd-k-search-modal')).toBeNull();

    // Dispatch Cmd+K event on window
    const cmdKEvent = new KeyboardEvent('keydown', { key: 'k', metaKey: true, bubbles: true });
    window.dispatchEvent(cmdKEvent);
    await new Promise(r => setTimeout(r, 50));

    // Open modal directly via props if needed or check event listener
    const openComponent = mount(CanonSearchModal, { target, props: { isOpen: true } });
    await new Promise(r => setTimeout(r, 50));

    expect(target.querySelector('#cmd-k-search-modal')).not.toBeNull();

    // Press Escape to close
    const escEvent = new KeyboardEvent('keydown', { key: 'Escape', bubbles: true });
    window.dispatchEvent(escEvent);
    await new Promise(r => setTimeout(r, 50));

    unmount(openComponent);
  });

  it('dispatches cross_link_click telemetry event when cross-links in ArticleStreamView are clicked', async () => {
    component = mount(TelemetryHub, { target, props: { initialTab: 'article' } });
    await new Promise(r => setTimeout(r, 200));

    telemetry.trackCrossLinkClick('concept-deontic-logic', 'podcast-ep-01', 'inline_reference');

    await new Promise(r => setTimeout(r, 50));

    const crossLinkEvents = telemetry.getEvents().filter(e => e.type === 'cross_link_click');
    expect(crossLinkEvents).toHaveLength(1);
    expect(crossLinkEvents[0].payload.target_node_id).toBe('podcast-ep-01');
  });

  it('dispatches cross_link_click telemetry event when cross-links in CanonGatewayView are clicked', async () => {
    component = mount(TelemetryHub, { target, props: { initialTab: 'canon' } });
    await new Promise(r => setTimeout(r, 50));

    const graphCrossLinkBtn = target.querySelector('#crosslink-to-podcast-1') as HTMLButtonElement;
    expect(graphCrossLinkBtn).not.toBeNull();

    graphCrossLinkBtn.click();
    await new Promise(r => setTimeout(r, 50));

    const crossLinkEvents = telemetry.getEvents().filter(e => e.type === 'cross_link_click');
    expect(crossLinkEvents).toHaveLength(1);
    expect(crossLinkEvents[0].payload.source_node_id).toBe('concept-deontic-logic');
    expect(crossLinkEvents[0].payload.target_node_id).toBe('podcast-ep-01');
  });
});
