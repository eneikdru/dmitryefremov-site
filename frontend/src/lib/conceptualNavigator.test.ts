// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import ConceptualNavigatorView from '../components/ConceptualNavigatorView.svelte';
import { mount, unmount } from 'svelte';

describe('ConceptualNavigatorView Component', () => {
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

  it('renders section header, search input and default manifestos', () => {
    component = mount(ConceptualNavigatorView, { target });

    expect(target.textContent).toContain('Conceptual Navigator & Epistemic Archive');
    expect(target.querySelector('#omni-search-input')).not.toBeNull();
    expect(target.textContent).toContain('A Cyborg Manifesto');
    expect(target.textContent).toContain('The Solarpunk Manifesto');
  });

  it('filters manifestos dynamically on search input', async () => {
    component = mount(ConceptualNavigatorView, { target });

    const searchInput = target.querySelector('#omni-search-input') as HTMLInputElement;
    searchInput.value = 'Solarpunk';
    searchInput.dispatchEvent(new Event('input'));
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('The Solarpunk Manifesto');
    expect(target.textContent).not.toContain('A Cyborg Manifesto');
  });

  it('renders clear empty state and reset filters option when no results match', async () => {
    component = mount(ConceptualNavigatorView, { target });

    const searchInput = target.querySelector('#omni-search-input') as HTMLInputElement;
    searchInput.value = 'NonExistentTermXYZ123';
    searchInput.dispatchEvent(new Event('input'));
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('No matching manifestos found');
    const resetButton = target.querySelector('#reset-filters-btn') as HTMLButtonElement;
    expect(resetButton).not.toBeNull();

    resetButton.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).not.toContain('No matching manifestos found');
    expect(target.textContent).toContain('A Cyborg Manifesto');
  });

  it('filters manifestos by domain pill', async () => {
    component = mount(ConceptualNavigatorView, { target });

    const domainPill = target.querySelector('#domain-pill-Ecological\\ Futures') as HTMLButtonElement;
    expect(domainPill).not.toBeNull();

    domainPill.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('The Solarpunk Manifesto');
    expect(target.textContent).not.toContain('A Cyborg Manifesto');
  });

  it('toggles 2D Graph and List view in Radar field', async () => {
    component = mount(ConceptualNavigatorView, { target });

    const toggleListBtn = target.querySelector('#toggle-list') as HTMLButtonElement;
    toggleListBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('Cyberfeminism (Center Node)');

    const toggle2dBtn = target.querySelector('#toggle-2d') as HTMLButtonElement;
    toggle2dBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('COORDS: [0.72x, -0.41y]');
  });
});
