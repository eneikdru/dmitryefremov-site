// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';
import axe from 'axe-core';

describe('Accessibility & Theme Switching Verification', () => {
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

  it('runs axe accessibility scan on TelemetryHub with zero WCAG 2.1 AA violations', async () => {
    component = mount(TelemetryHub, { target });
    const results = await axe.run(target, {
      runOnly: {
        type: 'tag',
        values: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa']
      }
    });

    expect(results.violations).toEqual([]);
  });

  it('applies dark theme automatically when predefined dark theme preference is provided', () => {
    component = mount(TelemetryHub, { target, props: { theme: 'dark' } });
    const themeRoot = target.querySelector('#theme-root') as HTMLElement;
    expect(themeRoot.classList.contains('dark-void')).toBe(true);
  });

  it('applies light theme automatically when predefined light theme preference is provided', () => {
    component = mount(TelemetryHub, { target, props: { theme: 'light' } });
    const themeRoot = target.querySelector('#theme-root') as HTMLElement;
    expect(themeRoot.classList.contains('light-monochrome')).toBe(true);
  });
});
