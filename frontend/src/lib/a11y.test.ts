// @vitest-environment jsdom
import { describe, it, expect, beforeEach, afterEach, beforeAll } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import TelemetryArticleView from '../components/TelemetryArticleView.svelte';
import TelemetryContactTerminalView from '../components/TelemetryContactTerminalView.svelte';
import { mount, unmount } from 'svelte';
import axe from 'axe-core';

describe('WCAG 2.1 AA Automated Accessibility Scans', () => {
  let target: HTMLElement;
  let component: any;

  beforeAll(() => {
    // Stub canvas getContext to avoid jsdom not-implemented warnings in axe-core
    HTMLCanvasElement.prototype.getContext = (() => null) as any;
  });

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
  });

  afterEach(() => {
    if (component) {
      unmount(component);
      component = null;
    }
    if (target && target.parentNode) {
      target.parentNode.removeChild(target);
    }
  });

  it('reports zero WCAG 2.1 AA violations on TelemetryHub component in dark mode', async () => {
    component = mount(TelemetryHub, { target, props: { theme: 'dark' } });
    const results = await axe.run(target, {
      runOnly: {
        type: 'tag',
        values: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa']
      }
    });
    expect(results.violations).toEqual([]);
  });

  it('reports zero WCAG 2.1 AA violations on TelemetryHub component in light mode', async () => {
    component = mount(TelemetryHub, { target, props: { theme: 'light' } });
    const results = await axe.run(target, {
      runOnly: {
        type: 'tag',
        values: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa']
      }
    });
    expect(results.violations).toEqual([]);
  });

  it('reports zero WCAG 2.1 AA violations on TelemetryArticleView', async () => {
    component = mount(TelemetryArticleView, { target });
    const results = await axe.run(target, {
      runOnly: {
        type: 'tag',
        values: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa']
      }
    });
    expect(results.violations).toEqual([]);
  });

  it('reports zero WCAG 2.1 AA violations on TelemetryContactTerminalView', async () => {
    component = mount(TelemetryContactTerminalView, { target });
    const results = await axe.run(target, {
      runOnly: {
        type: 'tag',
        values: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa']
      }
    });
    expect(results.violations).toEqual([]);
  });
});
