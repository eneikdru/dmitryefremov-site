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
    const evt = telemetry.trackOutboundClick('https://t.me/efremov_mvp', 'Telegram Channel', 'Telegram');
    expect(evt.type).toBe('outbound_click');
    expect(evt.payload.destination).toBe('https://t.me/efremov_mvp');
    expect(evt.payload.label).toBe('Telegram Channel');
    expect(evt.payload.platform).toBe('Telegram');

    const events = telemetry.getEvents();
    expect(events.length).toBe(1);
    expect(events[0]).toEqual(evt);
  });

  it('maintains zero tracking cookies during and after telemetry dispatch audit', () => {
    telemetry.trackScrollDepth('art-101', 50);
    telemetry.trackOutboundClick('https://github.com/dmitryefremov', 'GitHub Repository', 'GitHub');
    telemetry.trackOutboundClick('https://linkedin.com/in/dmitryefremov', 'LinkedIn Profile', 'LinkedIn');

    expect(telemetry.getEvents().length).toBe(3);
    const isCompliant = telemetry.verifyPrivacyCompliance();
    expect(isCompliant).toBe(true);
    expect(document.cookie).toBe('');
    expect(document.cookie.includes('telemetry_id')).toBe(false);
  });
});
