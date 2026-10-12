import { describe, it, expect, beforeEach } from 'vitest';
import {
  getConsentStatus,
  setConsentStatus,
  resetConsentStatus,
  isTelemetryAllowed
} from './consent';
import { telemetry } from './telemetry';

describe('Consent Management Platform (CMP) Unit Tests', () => {
  beforeEach(() => {
    resetConsentStatus();
    telemetry.clearEvents();
  });

  it('defaults consent status to null when no preference is saved', () => {
    expect(getConsentStatus()).toBeNull();
  });

  it('blocks tracking requests when a visitor has not consented (null consent status)', () => {
    expect(getConsentStatus()).toBeNull();
    expect(isTelemetryAllowed()).toBe(false);

    const scrollEvt = telemetry.trackScrollDepth('art-101', 50);
    expect(scrollEvt).toBeNull();

    const clickEvt = telemetry.trackOutboundClick('https://t.me/efremov_mvp');
    expect(clickEvt).toBeNull();

    expect(telemetry.getEvents()).toHaveLength(0);
  });

  it('persists accepted consent status', () => {
    setConsentStatus('accepted');
    expect(getConsentStatus()).toBe('accepted');
    expect(isTelemetryAllowed()).toBe(true);
  });

  it('persists declined consent status', () => {
    setConsentStatus('declined');
    expect(getConsentStatus()).toBe('declined');
    expect(isTelemetryAllowed()).toBe(false);
  });

  it('resets consent status correctly', () => {
    setConsentStatus('accepted');
    expect(getConsentStatus()).toBe('accepted');

    resetConsentStatus();
    expect(getConsentStatus()).toBeNull();
  });

  it('blocks telemetry when consent is declined', () => {
    setConsentStatus('declined');

    const scrollEvt = telemetry.trackScrollDepth('art-101', 50);
    expect(scrollEvt).toBeNull();

    const clickEvt = telemetry.trackOutboundClick('https://t.me/efremov_mvp');
    expect(clickEvt).toBeNull();

    expect(telemetry.getEvents()).toHaveLength(0);
  });

  it('allows telemetry when consent is accepted', () => {
    setConsentStatus('accepted');

    const scrollEvt = telemetry.trackScrollDepth('art-101', 50);
    expect(scrollEvt).not.toBeNull();
    expect(scrollEvt?.payload.articleId).toBe('art-101');

    const clickEvt = telemetry.trackOutboundClick('https://t.me/efremov_mvp');
    expect(clickEvt).not.toBeNull();

    expect(telemetry.getEvents()).toHaveLength(2);
  });

  it('allows updating consent preferences dynamically', () => {
    setConsentStatus('declined');
    expect(telemetry.trackScrollDepth('art-101', 25)).toBeNull();

    // User changes preference via CMP
    setConsentStatus('accepted');
    expect(telemetry.trackScrollDepth('art-101', 25)).not.toBeNull();
    expect(telemetry.getEvents()).toHaveLength(1);
  });

  it('immediately ceases tracking requests when consent is revoked after being accepted', () => {
    // Visitor accepts consent
    setConsentStatus('accepted');
    expect(isTelemetryAllowed()).toBe(true);

    const initialEvt = telemetry.trackScrollDepth('art-101', 25);
    expect(initialEvt).not.toBeNull();
    expect(telemetry.getEvents()).toHaveLength(1);

    // Visitor revokes consent (e.g., sets to declined or resets preference)
    setConsentStatus('declined');
    expect(isTelemetryAllowed()).toBe(false);

    const revokedEvt1 = telemetry.trackScrollDepth('art-101', 50);
    expect(revokedEvt1).toBeNull();

    const revokedEvt2 = telemetry.trackOutboundClick('https://t.me/efremov_mvp');
    expect(revokedEvt2).toBeNull();

    // Total events remain at 1 from when consent was active
    expect(telemetry.getEvents()).toHaveLength(1);

    // Revoking via reset Consent
    setConsentStatus('accepted');
    expect(telemetry.trackScrollDepth('art-101', 75)).not.toBeNull();
    expect(telemetry.getEvents()).toHaveLength(2);

    resetConsentStatus(); // Status reverts to null
    expect(isTelemetryAllowed()).toBe(false);
    expect(telemetry.trackScrollDepth('art-101', 100)).toBeNull();
    expect(telemetry.getEvents()).toHaveLength(2);
  });
});
