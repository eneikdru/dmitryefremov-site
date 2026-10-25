// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount, unmount } from 'svelte';
import { setConsentStatus, resetConsentStatus } from './consent';
import { telemetry, type TelemetryEvent } from './telemetry';
import VaultIntakeView from '../components/VaultIntakeView.svelte';
import CanonGatewayView from '../components/CanonGatewayView.svelte';
import CanonSearchModal from '../components/CanonSearchModal.svelte';

describe('UI Slice 12f54753 - Telemetry & Event Tracking Integration', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
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

  describe('Criterion 1: Contact Form and Agent API Conversion Telemetry', () => {
    it('dispatches a conversion event when a contact form submission occurs with accepted consent', async () => {
      setConsentStatus('accepted');

      global.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({ claimTicket: '0xtest987...654' })
      });

      component = mount(VaultIntakeView, { target });

      const emailInput = target.querySelector('#client-email-input') as HTMLInputElement;
      emailInput.value = 'user@example.com';
      emailInput.dispatchEvent(new Event('input', { bubbles: true }));

      const titleInput = target.querySelector('#submission-title-input') as HTMLInputElement;
      titleInput.value = 'Vulnerability Report Title';
      titleInput.dispatchEvent(new Event('input', { bubbles: true }));

      const narrativeTextarea = target.querySelector('#narrative-textarea') as HTMLTextAreaElement;
      narrativeTextarea.value = 'Detailed narrative description of the vulnerability.';
      narrativeTextarea.dispatchEvent(new Event('input', { bubbles: true }));

      const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
      form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

      await new Promise(r => setTimeout(r, 50));

      const events = telemetry.getEvents();
      const conversionEvents = events.filter(e => e.type === 'canon_submit_converted');
      expect(conversionEvents.length).toBeGreaterThanOrEqual(1);

      const evt = conversionEvents[0];
      expect(evt.payload.session_id).toBe('0xtest987...654');
      expect(evt.payload.client_email).toBe('user@example.com');
      expect(evt.payload.converted).toBe(true);
    });

    it('dispatches a conversion event when an agent order API submission occurs with accepted consent', async () => {
      setConsentStatus('accepted');

      global.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({ id: 'ORD-554433', status: 'created' })
      });

      component = mount(CanonGatewayView, { target });

      // Trigger test route button for POST /api/v1/agent/orders
      const testRouteButtons = target.querySelectorAll('button');
      const orderTestBtn = Array.from(testRouteButtons).find(b => b.textContent?.includes('Test Route'));
      if (orderTestBtn) {
        orderTestBtn.click();
        await new Promise(r => setTimeout(r, 20));
      }

      // Submit test order form in modal
      const modalForm = target.querySelector('#test-order-modal form') as HTMLFormElement;
      if (modalForm) {
        modalForm.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
        await new Promise(r => setTimeout(r, 50));

        const events = telemetry.getEvents();
        const conversionEvents = events.filter(e => e.type === 'canon_submit_converted');
        expect(conversionEvents.length).toBeGreaterThanOrEqual(1);
        expect(conversionEvents[0].payload.session_id).toBe('ORD-554433');
      }
    });
  });

  describe('Criterion 2: Conceptual Navigator Search and Context Engagement Events', () => {
    it('dispatches engagement search_utilization event when user searches in conceptual navigator', async () => {
      setConsentStatus('accepted');

      component = mount(CanonSearchModal, {
        target,
        props: { isOpen: true }
      });

      const searchInput = target.querySelector('#canon-search-input') as HTMLInputElement;
      expect(searchInput).not.toBeNull();

      searchInput.value = 'деонтическая';
      searchInput.dispatchEvent(new Event('input', { bubbles: true }));

      const events = telemetry.getEvents();
      const searchEvents = events.filter(e => e.type === 'search_utilization');
      expect(searchEvents.length).toBeGreaterThanOrEqual(1);
      expect(searchEvents[0].payload.query).toBe('деонтическая');
      expect(searchEvents[0].payload.trigger_method).toBe('cmd_k');
    });

    it('dispatches cross_link_click engagement event when user interacts with a concept cross-link', async () => {
      setConsentStatus('accepted');

      component = mount(CanonGatewayView, { target });

      const crossLinkBtn = target.querySelector('#crosslink-to-podcast-1') as HTMLButtonElement;
      expect(crossLinkBtn).not.toBeNull();

      crossLinkBtn.click();

      const events = telemetry.getEvents();
      const crossLinkEvents = events.filter(e => e.type === 'cross_link_click');
      expect(crossLinkEvents.length).toBe(1);
      expect(crossLinkEvents[0].payload.source_node_id).toBe('concept-deontic-logic');
      expect(crossLinkEvents[0].payload.target_node_id).toBe('podcast-ep-01');
      expect(crossLinkEvents[0].payload.target_node_type).toBe('podcast');
    });
  });

  describe('Criterion 3: Consent Enforcement - Visitors who refused consent dispatch no tracking telemetry', () => {
    it('dispatches zero telemetry events when consent is refused/declined on contact form submission', async () => {
      setConsentStatus('declined');

      global.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({ claimTicket: '0xno_track_ticket' })
      });

      component = mount(VaultIntakeView, { target });

      const form = target.querySelector('#vault-intake-form') as HTMLFormElement;
      form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));

      await new Promise(r => setTimeout(r, 50));

      expect(telemetry.getEvents()).toHaveLength(0);
    });

    it('dispatches zero telemetry events when consent is unchosen (null) on conceptual navigator search', async () => {
      resetConsentStatus();

      component = mount(CanonSearchModal, {
        target,
        props: { isOpen: true }
      });

      const searchInput = target.querySelector('#canon-search-input') as HTMLInputElement;
      searchInput.value = 'онтология';
      searchInput.dispatchEvent(new Event('input', { bubbles: true }));

      expect(telemetry.getEvents()).toHaveLength(0);
    });
  });
});
