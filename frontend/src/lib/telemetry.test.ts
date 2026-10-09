import { describe, it, expect, beforeEach } from 'vitest';
import { telemetry, calculateScrollPercentage, type TelemetryEvent } from './telemetry';

describe('Telemetry Service Invariants & Acceptance Criteria', () => {
  beforeEach(() => {
    telemetry.clearEvents();
  });

  it('calculates scroll percentage accurately', () => {
    expect(calculateScrollPercentage(0, 1000, 500)).toBe(0);
    expect(calculateScrollPercentage(250, 1000, 500)).toBe(50);
    expect(calculateScrollPercentage(500, 1000, 500)).toBe(100);
    expect(calculateScrollPercentage(600, 1000, 500)).toBe(100);
  });

  it('records scroll depth telemetry events', () => {
    const evt25 = telemetry.trackScrollDepth('art-101', 25);
    expect(evt25.type).toBe('scroll_depth');
    expect(evt25.payload.articleId).toBe('art-101');
    expect(evt25.payload.depth).toBe(25);

    const events = telemetry.getEvents();
    expect(events.length).toBe(1);
    expect(events[0]).toEqual(evt25);
  });

  describe('Contact Link Analytics Event Dispatching', () => {
    it('dispatches correct analytics payload when a contact link is clicked', () => {
      const dispatchedEvents: TelemetryEvent[] = [];
      const unsubscribe = telemetry.subscribe((evt) => {
        dispatchedEvents.push(evt);
      });

      const evt = telemetry.trackOutboundClick(
        'https://t.me/efremov_mvp',
        'Telegram Channel',
        'Telegram'
      );

      expect(evt.type).toBe('outbound_click');
      expect(evt.payload).toEqual({
        destination: 'https://t.me/efremov_mvp',
        label: 'Telegram Channel',
        platform: 'Telegram'
      });
      expect(evt.timestamp).toBeDefined();

      expect(dispatchedEvents).toHaveLength(1);
      expect(dispatchedEvents[0]).toEqual(evt);

      unsubscribe();
    });

    it('uses fallback values for label and platform if omitted when tracking contact links', () => {
      const evt = telemetry.trackOutboundClick('https://github.com/dmitryefremov');

      expect(evt.type).toBe('outbound_click');
      expect(evt.payload.destination).toBe('https://github.com/dmitryefremov');
      expect(evt.payload.label).toBe('https://github.com/dmitryefremov');
      expect(evt.payload.platform).toBe('external');
    });

    it('dispatches multiple contact link events in sequence', () => {
      const contactLinks = [
        { url: 'https://t.me/efremov_mvp', label: 'Telegram Channel', platform: 'Telegram' },
        { url: 'https://youtube.com/@efremov_dmitriy', label: 'YouTube Channel', platform: 'YouTube' },
        { url: 'mailto:contact@dmitryefremov.com', label: 'Direct Email', platform: 'Email' }
      ];

      contactLinks.forEach(link => {
        telemetry.trackOutboundClick(link.url, link.label, link.platform);
      });

      const events = telemetry.getEvents();
      expect(events).toHaveLength(3);
      expect(events[0].payload.platform).toBe('Telegram');
      expect(events[1].payload.platform).toBe('YouTube');
      expect(events[2].payload.platform).toBe('Email');
    });
  });

  describe('Privacy & Zero Cookie Audit Verification', () => {
    it('maintains zero non-essential cookies privacy default', () => {
      const isCompliant = telemetry.verifyPrivacyCompliance();
      expect(isCompliant).toBe(true);
      expect(document.cookie).toBe('');
    });

    it('verifies no tracking cookies are set during or after event dispatch', () => {
      telemetry.trackOutboundClick('https://t.me/efremov_mvp', 'Telegram Channel', 'Telegram');
      telemetry.trackScrollDepth('art-01', 50);

      expect(document.cookie).toBe('');
      expect(telemetry.verifyPrivacyCompliance()).toBe(true);
    });

    it('detects non-compliance if a tracking cookie is injected', () => {
      Object.defineProperty(document, 'cookie', {
        writable: true,
        value: 'telemetry_id=tracked_user_123'
      });

      expect(telemetry.verifyPrivacyCompliance()).toBe(false);

      // Clean up mock
      Object.defineProperty(document, 'cookie', {
        writable: true,
        value: ''
      });
    });
  });
});
