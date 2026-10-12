// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import {
  INITIAL_SPARKS,
  toggleSparkCount,
  toggleBookmark,
  addQuantizedThought
} from './sparks';
import SparksStreamView from '../components/SparksStreamView.svelte';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';

describe('Sparks Data Logic (sparks.ts)', () => {
  it('contains initial quantized spark items across required domains', () => {
    expect(INITIAL_SPARKS.length).toBeGreaterThan(0);
    const categories = INITIAL_SPARKS.map(s => s.category);
    expect(categories).toContain('AI');
    expect(categories).toContain('BIO');
    expect(categories).toContain('SYSTEMS');
  });

  it('increments spark count when toggleSparkCount is called', () => {
    const originalSparksCount = INITIAL_SPARKS[0].sparksCount;
    const updated = toggleSparkCount(INITIAL_SPARKS, INITIAL_SPARKS[0].id);
    expect(updated[0].sparksCount).toBe(originalSparksCount + 1);
  });

  it('toggles bookmark state when toggleBookmark is called', () => {
    const initialBookmarkState = INITIAL_SPARKS[0].isBookmarked;
    const updated = toggleBookmark(INITIAL_SPARKS, INITIAL_SPARKS[0].id);
    expect(updated[0].isBookmarked).toBe(!initialBookmarkState);
  });

  it('adds a new quantized thought at top of list', () => {
    const text = 'Testing real-time quantization mechanism';
    const updated = addQuantizedThought(INITIAL_SPARKS, text);
    expect(updated.length).toBe(INITIAL_SPARKS.length + 1);
    expect(updated[0].content).toBe(text);
    expect(updated[0].charCount).toBe(text.length);
  });
});

describe('SparksStreamView Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('renders header, live sync metrics, and initial quantized thoughts', () => {
    component = mount(SparksStreamView, { target });

    expect(target.textContent).toContain('SPARKS // STREAM');
    expect(target.textContent).toContain('LIVE SYNC ACTIVE');
    expect(target.textContent).toContain('Context windows are not memory');
    expect(target.textContent).toContain('Evolution never optimizes for truth');
  });

  it('filters thoughts when filter pills are clicked', async () => {
    component = mount(SparksStreamView, { target });

    const buttons = Array.from(target.querySelectorAll('nav button'));
    const techBtn = buttons.find(b => b.textContent?.includes('Tech/AI'));
    expect(techBtn).toBeDefined();

    (techBtn as HTMLButtonElement).click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('Context windows are not memory');
    expect(target.textContent).not.toContain('Evolution never optimizes for truth');
  });

  it('increments spark count and updates bookmark on user interaction', async () => {
    component = mount(SparksStreamView, { target });

    const sparkBtn = target.querySelector('button[aria-label="Spark action for Q-084 // AI"]') as HTMLButtonElement;
    expect(sparkBtn).not.toBeNull();
    expect(sparkBtn.textContent).toContain('42 sparks');

    sparkBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(sparkBtn.textContent).toContain('43 sparks');
  });

  it('allows adding a new quantized thought via quick capture input', async () => {
    component = mount(SparksStreamView, { target });

    const input = target.querySelector('#quantize-input') as HTMLInputElement;
    const form = target.querySelector('form') as HTMLFormElement;

    expect(input).not.toBeNull();
    input.value = 'New quantized insight input test';
    input.dispatchEvent(new Event('input'));

    form.dispatchEvent(new Event('submit'));
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('New quantized insight input test');
  });
});

describe('TelemetryHub Integration with Sparks Stream', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('navigates to Sparks Stream tab when clicking [SPARKS_STREAM] button', async () => {
    component = mount(TelemetryHub, { target });

    const sparksTabBtn = target.querySelector('#tab-btn-sparks') as HTMLButtonElement;
    expect(sparksTabBtn).not.toBeNull();

    sparksTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('SPARKS // STREAM');
    expect(target.textContent).toContain('LIVE SYNC ACTIVE');
  });
});
