import { describe, it, expect, beforeEach } from 'vitest';
import { telemetry, calculateScrollPercentage } from './telemetry';

describe('Telemetry Service Invariants', () => {
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

  it('records outbound click telemetry events', () => {
    const evt = telemetry.trackOutboundClick('https://t.me/efremov_mvp', 'Telegram', 'Telegram');
    expect(evt.type).toBe('outbound_click');
    expect(evt.payload.destination).toBe('https://t.me/efremov_mvp');
    expect(evt.payload.label).toBe('Telegram');
    expect(evt.payload.platform).toBe('Telegram');

    const events = telemetry.getEvents();
    expect(events.length).toBe(1);
    expect(events[0]).toEqual(evt);
  });

  it('maintains zero non-essential cookies privacy default', () => {
    const isCompliant = telemetry.verifyPrivacyCompliance();
    expect(isCompliant).toBe(true);
    expect(document.cookie).toBe('');
  });
});
