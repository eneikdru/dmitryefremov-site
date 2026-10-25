// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount, unmount } from 'svelte';
import TelemetryStreamTrackerView from '../components/TelemetryStreamTrackerView.svelte';
import { telemetry } from './telemetry';
import { setConsentStatus } from './consent';

describe('TelemetryStreamTrackerView Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    localStorage.clear();
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

  it('renders correctly with an empty stream', () => {
    component = mount(TelemetryStreamTrackerView, { target });

    expect(target.textContent).toContain('Epic Telemetry & Events Tracker');
    expect(target.textContent).toContain('No matching events in the current buffer');
  });

  it('displays newly tracked telemetry events', async () => {
    component = mount(TelemetryStreamTrackerView, { target });

    // Simulate event tracking
    telemetry.trackOutboundClick('https://github.com/dmitryefremov', 'GitHub', 'GitHub');

    // wait for derived state to update
    await new Promise(r => setTimeout(r, 50));

    expect(target.textContent).toContain('outbound_click');
    expect(target.textContent).toContain('Schema Valid');
  });

  it('toggles stream pausing and resuming', async () => {
    component = mount(TelemetryStreamTrackerView, { target });

    const toggleBtn = target.querySelector('#toggle-stream-btn') as HTMLButtonElement;
    expect(toggleBtn.textContent).toContain('Pause Stream');

    toggleBtn.click();
    await new Promise(r => setTimeout(r, 50));

    expect(toggleBtn.textContent).toContain('Resume Stream');

    // Track an event while paused
    telemetry.trackScrollDepth('art-123', 50);
    await new Promise(r => setTimeout(r, 50));

    // UI should not show it yet because it's paused
    expect(target.textContent).not.toContain('scroll_depth');

    // Resume stream
    toggleBtn.click();
    await new Promise(r => setTimeout(r, 50));

    expect(target.textContent).toContain('scroll_depth');
  });

  it('filters events based on category', async () => {
    component = mount(TelemetryStreamTrackerView, { target });

    telemetry.trackOutboundClick('link', 'label', 'platform'); // Clicks
    telemetry.trackFormValidationPassed('step_1'); // Validation
    telemetry.trackCanonSubmitConverted('session', 100); // Goals

    await new Promise(r => setTimeout(r, 50));

    expect(target.textContent).toContain('outbound_click');
    expect(target.textContent).toContain('form_validation_passed');
    expect(target.textContent).toContain('canon_submit_converted');

    const filterBtns = Array.from(target.querySelectorAll('button')).filter(b => b.textContent?.includes('Clicks'));
    if (filterBtns.length > 0) {
      filterBtns[0].click();
      await new Promise(r => setTimeout(r, 50));

      expect(target.textContent).toContain('outbound_click');
      expect(target.textContent).not.toContain('form_validation_passed');
      expect(target.textContent).not.toContain('canon_submit_converted');
    }
  });
});
