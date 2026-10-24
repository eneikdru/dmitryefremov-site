// @vitest-environment jsdom
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import CanonGraphTelemetryView from '../components/CanonGraphTelemetryView.svelte';
import { telemetry } from './telemetry';
import { setConsentStatus, resetConsentStatus } from './consent';
import { mount, unmount } from 'svelte';

describe('CanonGraphTelemetryView Component & WCAG Compliance', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    resetConsentStatus();
    setConsentStatus('accepted');
    telemetry.clearEvents();

    target = document.createElement('div');
    document.body.appendChild(target);
  });

  afterEach(() => {
    if (component) {
      unmount(component);
    }
    if (target && target.parentNode) {
      document.body.removeChild(target);
    }
  });

  it('renders CANON_TELEMETRY header, KPI grid, and graph cross-link sections', () => {
    component = mount(CanonGraphTelemetryView, { target });

    expect(target.textContent).toContain('CANON_TELEMETRY');
    expect(target.textContent).toContain('STREAM: ACTIVE');
    expect(target.textContent).toContain('PROD-GRAPH-v2.4');
    expect(target.textContent).toContain('142.8k');
    expect(target.textContent).toContain('38.6k');
    expect(target.textContent).toContain('Canon Concept Graph Navigation');
  });

  it('dispatches graph_node_navigation telemetry event on cross-link click', async () => {
    component = mount(CanonGraphTelemetryView, { target });

    const crossLinkBtn = target.querySelector(
      '#crosslink-concept_ontological_autonomy-to-podcast_ep03'
    ) as HTMLButtonElement;

    expect(crossLinkBtn).not.toBeNull();
    crossLinkBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    const events = telemetry.getEvents();
    expect(events.length).toBeGreaterThanOrEqual(1);

    const navEvent = events.find((e) => e.type === 'graph_node_navigation');
    expect(navEvent).toBeDefined();
    expect(navEvent?.payload.source_node_id).toBe('concept_ontological_autonomy');
    expect(navEvent?.payload.target_node_id).toBe('podcast_ep03');
    expect(navEvent?.payload.relation_type).toBe('analyzed_in');
  });

  it('opens Cmd+K search modal on button click and verifies WCAG labelled input', async () => {
    component = mount(CanonGraphTelemetryView, { target });

    const triggerBtn = target.querySelector('#cmd-k-trigger-btn') as HTMLButtonElement;
    expect(triggerBtn).not.toBeNull();

    triggerBtn.click();
    await new Promise((r) => setTimeout(r, 50));

    const modal = target.querySelector('#cmd-k-search-modal');
    expect(modal).not.toBeNull();

    const searchInput = target.querySelector('#cmd-k-search-input') as HTMLInputElement;
    expect(searchInput).not.toBeNull();
    expect(searchInput.getAttribute('aria-label')).toBe('Поисковый запрос Канона');

    const searchLabel = target.querySelector('#cmd-k-search-label');
    expect(searchLabel).not.toBeNull();
    expect(searchLabel?.getAttribute('for')).toBe('cmd-k-search-input');
  });

  it('dispatches canon_search_execute event when executing Cmd+K search', async () => {
    component = mount(CanonGraphTelemetryView, { target });

    const triggerBtn = target.querySelector('#cmd-k-trigger-btn') as HTMLButtonElement;
    triggerBtn.click();
    await new Promise((r) => setTimeout(r, 50));

    const searchInput = target.querySelector('#cmd-k-search-input') as HTMLInputElement;
    searchInput.value = 'деонтическая';
    searchInput.dispatchEvent(new Event('input', { bubbles: true }));
    await new Promise((r) => setTimeout(r, 20));

    const executeBtn = target.querySelector('#cmd-k-execute-btn') as HTMLButtonElement;
    expect(executeBtn).not.toBeNull();

    executeBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    const events = telemetry.getEvents();
    const searchEvent = events.find((e) => e.type === 'canon_search_execute');
    expect(searchEvent).toBeDefined();
    expect(searchEvent?.payload.query).toBe('деонтическая');
    expect(searchEvent?.payload.search_type).toBe('semantic_cmd_k');
  });

  it('handles Cmd+K keyboard shortcut to open search modal', async () => {
    component = mount(CanonGraphTelemetryView, { target });
    await new Promise((r) => setTimeout(r, 50));

    const keydownEvent = new KeyboardEvent('keydown', {
      key: 'k',
      metaKey: true,
      bubbles: true
    });
    window.dispatchEvent(keydownEvent);
    await new Promise((r) => setTimeout(r, 100));

    const modal = target.querySelector('#cmd-k-search-modal');
    expect(modal).not.toBeNull();
  });
});
