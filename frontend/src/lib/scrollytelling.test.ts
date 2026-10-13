// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import ScrollytellingView from '../components/ScrollytellingView.svelte';
import { mount, unmount } from 'svelte';

describe('ScrollytellingView Component UI Interaction & Accessibility', () => {
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

  it('renders kinetic typography headline and axiom without studio photos', () => {
    component = mount(ScrollytellingView, { target });

    expect(target.textContent).toContain('CH.01 // ORIGIN MATRIX');
    expect(target.textContent).toContain('RADICAL');
    expect(target.textContent).toContain('FORM //');
    expect(target.textContent).toContain('PURE ESSENCE');
    expect(target.textContent).toContain('AXIOM: ARCHITECTURE WITHOUT MIMICRY');

    // Confirm zero <img> tags exist in scrollytelling view
    const images = target.querySelectorAll('img');
    expect(images.length).toBe(0);
  });

  it('contains interactive JSON inspect expand toggle', async () => {
    component = mount(ScrollytellingView, { target });

    const expandBtn = target.querySelector('#inspect-expand-btn') as HTMLButtonElement;
    expect(expandBtn).not.toBeNull();
    expect(expandBtn.textContent).toContain('EXPAND');

    expandBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(expandBtn.textContent).toContain('COLLAPSE');
    expect(target.textContent).toContain('"photos_count": 0');
    expect(target.textContent).toContain('"typography_mode": "kinetic_brutalism"');
  });

  it('opens and closes Imprint legal modal with contrast and operator details', async () => {
    component = mount(ScrollytellingView, { target });

    expect(target.querySelector('#imprint-modal')).toBeNull();

    const imprintBtn = target.querySelector('#scrolly-imprint-toggle-btn') as HTMLButtonElement;
    expect(imprintBtn).not.toBeNull();

    imprintBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#imprint-modal')).not.toBeNull();
    expect(target.textContent).toContain('Site Operator: Dmitry Efremov');
    expect(target.textContent).toContain('Scrollytelling Interface without studio photos');

    const closeBtn = target.querySelector('#imprint-close-btn') as HTMLButtonElement;
    closeBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#imprint-modal')).toBeNull();
  });
});
