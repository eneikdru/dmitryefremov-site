// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import { mount, unmount } from 'svelte';
import CanonSearchModal from '../components/CanonSearchModal.svelte';
import CanonGatewayView from '../components/CanonGatewayView.svelte';
import { telemetry, type TelemetryEvent } from './telemetry';
import { setConsentStatus, resetConsentStatus } from './consent';

describe('Telemetry Precision & Noise Isolation Test Suite (BARCAN-TAG-06)', () => {
  let target: HTMLElement;
  let component: any;
  let mockAnalyticsBackend: TelemetryEvent[];

  beforeEach(() => {
    resetConsentStatus();
    setConsentStatus('accepted');
    telemetry.clearEvents();
    mockAnalyticsBackend = [];

    // Mock analytics backend subscriber receiving dispatched events
    telemetry.subscribe((event) => {
      mockAnalyticsBackend.push(event);
    });

    target = document.createElement('div');
    document.body.appendChild(target);

    return () => {
      if (component) {
        unmount(component);
      }
      if (target && target.parentNode) {
        document.body.removeChild(target);
      }
    };
  });

  it('verifies graph traversal dispatches exact cross_link_click events without extraneous noise', async () => {
    component = mount(CanonGatewayView, { target });
    await new Promise((r) => setTimeout(r, 50));

    expect(mockAnalyticsBackend).toHaveLength(0);

    // Simulate clicking graph cross link to Podcast 01
    const podcastCrossLink = target.querySelector('#crosslink-to-podcast-1') as HTMLButtonElement;
    expect(podcastCrossLink).not.toBeNull();
    podcastCrossLink.click();
    await new Promise((r) => setTimeout(r, 50));

    expect(mockAnalyticsBackend).toHaveLength(1);
    expect(mockAnalyticsBackend[0].type).toBe('cross_link_click');
    expect(mockAnalyticsBackend[0].payload).toEqual({
      source_node_id: 'concept-deontic-logic',
      target_node_id: 'podcast-ep-01',
      target_node_type: 'podcast',
      label: 'Выпуск 01'
    });

    // Simulate clicking graph cross link to Essay
    const essayCrossLink = target.querySelector('#crosslink-to-essay-1') as HTMLButtonElement;
    expect(essayCrossLink).not.toBeNull();
    essayCrossLink.click();
    await new Promise((r) => setTimeout(r, 50));

    expect(mockAnalyticsBackend).toHaveLength(2);
    expect(mockAnalyticsBackend[1].type).toBe('cross_link_click');
    expect(mockAnalyticsBackend[1].payload.target_node_id).toBe('essay-deontic');

    // Confirm no extraneous telemetry events were generated
    const unknownEvents = mockAnalyticsBackend.filter(
      (e) => e.type !== 'cross_link_click'
    );
    expect(unknownEvents).toHaveLength(0);
  });

  it('verifies Cmd+K search invocation dispatches exact search_utilization events without noise', async () => {
    component = mount(CanonSearchModal, { target, props: { isOpen: true } });
    await new Promise((r) => setTimeout(r, 50));

    // Select search result item directly
    const searchResult = target.querySelector('#search-result-concept-deontic-logic') as HTMLButtonElement;
    expect(searchResult).not.toBeNull();
    searchResult.click();
    await new Promise((r) => setTimeout(r, 50));

    const searchEvents = mockAnalyticsBackend.filter(e => e.type === 'search_utilization');
    expect(searchEvents.length).toBeGreaterThan(0);
    const lastEvent = searchEvents[searchEvents.length - 1];
    expect(lastEvent.type).toBe('search_utilization');
    expect(lastEvent.payload).toEqual({
      query: 'Деонтическая логика в автономии',
      trigger_method: 'cmd_k',
      results_count: 8,
      selected_result_id: 'concept-deontic-logic'
    });
  });

  it('verifies zero non-essential tracking cookies are created during graph traversal and search', async () => {
    component = mount(CanonGatewayView, { target });
    await new Promise((r) => setTimeout(r, 50));

    const podcastCrossLink = target.querySelector('#crosslink-to-podcast-1') as HTMLButtonElement;
    if (podcastCrossLink) podcastCrossLink.click();

    expect(telemetry.verifyPrivacyCompliance()).toBe(true);
    expect(document.cookie).not.toContain('telemetry_id');
  });
});
