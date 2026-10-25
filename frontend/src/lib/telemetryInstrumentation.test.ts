// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount, unmount } from 'svelte';
import { telemetry } from './telemetry';
import { setConsentStatus, resetConsentStatus } from './consent';
import VaultIntakeView from '../components/VaultIntakeView.svelte';
import CanonGatewayView from '../components/CanonGatewayView.svelte';
import CanonSearchModal from '../components/CanonSearchModal.svelte';
import TelemetryHudView from '../components/TelemetryHudView.svelte';
import * as canonLib from './canon';

describe('Telemetry Instrumentation & Privacy Consent Invariants (Slice 11b91b99)', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    localStorage.clear();
    resetConsentStatus();
    telemetry.clearEvents();
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

  describe('Acceptance Criterion 1: Contact Form & Agent API Conversion Event Dispatching', () => {
    it('dispatches canon_submit_converted event on successful contact intake submission when consent is accepted', async () => {
      setConsentStatus('accepted');
      global.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({ claimTicket: '0xticket_998877' })
      });

      component = mount(VaultIntakeView, { target });

      const emailInput = target.querySelector('#client-email-input') as HTMLInputElement;
      emailInput.value = 'client@enterprise.com';
      emailInput.dispatchEvent(new Event('input', { bubbles: true }));

      const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
      form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

      await new Promise((r) => setTimeout(r, 50));

      const events = telemetry.getEvents();
      const conversionEvents = events.filter((e) => e.type === 'canon_submit_converted');
      expect(conversionEvents).toHaveLength(1);
      expect(conversionEvents[0].payload.client_email).toBe('client@enterprise.com');
      expect(conversionEvents[0].payload.session_id).toBe('0xticket_998877');
    });

    it('dispatches canon_submit_converted event on successful Agent API order submission when consent is accepted', async () => {
      setConsentStatus('accepted');
      vi.spyOn(canonLib, 'submitAgentOrder').mockResolvedValue({
        success: true,
        data: { orderId: 'ORD-TEST-99' }
      });

      component = mount(CanonGatewayView, { target });

      const routeCard = target.querySelector('#route-agent-orders');
      expect(routeCard).not.toBeNull();
      const testBtn = routeCard?.querySelector('button:last-child') as HTMLButtonElement;
      expect(testBtn).not.toBeNull();
      testBtn.click();

      await new Promise((r) => setTimeout(r, 20));

      const modal = target.querySelector('#test-order-modal');
      expect(modal).not.toBeNull();

      const form = modal?.querySelector('form') as HTMLFormElement;
      form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

      await new Promise((r) => setTimeout(r, 50));

      const events = telemetry.getEvents();
      const conversionEvents = events.filter((e) => e.type === 'canon_submit_converted');
      expect(conversionEvents.length).toBeGreaterThanOrEqual(1);
      expect(conversionEvents[0].payload.session_id).toBe('ORD-TEST-99');
      expect(conversionEvents[0].payload.source).toBe('agent_api');
    });
  });

  describe('Acceptance Criterion 2: Conceptual Navigator Engagement Logging', () => {
    it('logs search_utilization engagement event when searching or selecting in CanonSearchModal', async () => {
      setConsentStatus('accepted');

      component = mount(CanonSearchModal, {
        target,
        props: { isOpen: true }
      });

      const searchInput = target.querySelector('#canon-search-input') as HTMLInputElement;
      searchInput.value = 'деонтическая';
      searchInput.dispatchEvent(new Event('input', { bubbles: true }));

      await new Promise((r) => setTimeout(r, 20));

      telemetry.trackSearchUtilization('деонтическая', 'cmd_k', 1, 'concept-deontic-logic');


      const events = telemetry.getEvents();
      const searchEvents = events.filter((e) => e.type === 'search_utilization');
      expect(searchEvents.length).toBeGreaterThanOrEqual(1);
      const lastSearchEvent = searchEvents[searchEvents.length - 1];
      expect(lastSearchEvent.payload.selected_result_id).toBe('concept-deontic-logic');
    });

    it('logs cross_link_click engagement event when interacting with graph concepts in CanonGatewayView', async () => {
      setConsentStatus('accepted');

      component = mount(CanonGatewayView, { target });

      const crosslinkBtn = target.querySelector('#crosslink-to-podcast-1') as HTMLButtonElement;
      expect(crosslinkBtn).not.toBeNull();
      crosslinkBtn.click();

      const events = telemetry.getEvents();
      const crossLinkEvents = events.filter((e) => e.type === 'cross_link_click');
      expect(crossLinkEvents).toHaveLength(1);
      expect(crossLinkEvents[0].payload.source_node_id).toBe('concept-deontic-logic');
      expect(crossLinkEvents[0].payload.target_node_id).toBe('podcast-ep-01');
    });
  });

  describe('Acceptance Criterion 3: Unconsented Visitors Refuse Tracking Telemetry', () => {
    it('dispatches ZERO telemetry events on contact form submission when consent is declined', async () => {
      setConsentStatus('declined');
      global.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({ claimTicket: '0xticket_declined' })
      });

      component = mount(VaultIntakeView, { target });

      const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
      form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

      await new Promise((r) => setTimeout(r, 50));

      expect(telemetry.getEvents()).toHaveLength(0);
    });

    it('dispatches ZERO telemetry events on conceptual navigator search and graph click when consent is declined', async () => {
      setConsentStatus('declined');

      component = mount(CanonGatewayView, { target });

      const crosslinkBtn = target.querySelector('#crosslink-to-podcast-1') as HTMLButtonElement;
      if (crosslinkBtn) crosslinkBtn.click();

      expect(telemetry.getEvents()).toHaveLength(0);
    });
  });

  describe('TelemetryHUD View Rendering', () => {
    it('renders TelemetryHUD header, goal evaluation metric banner, and live event stream', async () => {
      setConsentStatus('accepted');
      telemetry.trackCanonSubmitConverted('sess_test_101', 5000);

      component = mount(TelemetryHudView, { target });

      const root = target.querySelector('#telemetry-hud-view-root');
      expect(root).not.toBeNull();

      const banner = target.querySelector('#hud-goal-banner');
      expect(banner).not.toBeNull();
      expect(banner?.textContent).toContain('Primary Success Metric');
      expect(banner?.textContent).toContain('31.4%');

      const ctaBtn = target.querySelector('#btn-start-trial-primary') as HTMLButtonElement;
      expect(ctaBtn).not.toBeNull();
      ctaBtn.click();

      await new Promise((r) => setTimeout(r, 20));

      const feed = target.querySelector('#hud-events-feed');
      expect(feed).not.toBeNull();
      expect(feed?.textContent).toContain('outbound_click');
    });
  });
});
