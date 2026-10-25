// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount, unmount } from 'svelte';
import CanonGatewayView from '../components/CanonGatewayView.svelte';
import CanonSearchModal from '../components/CanonSearchModal.svelte';

describe('Canon Gateway View & Search Modal UI', () => {
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

  describe('CanonGatewayView Component', () => {
    it('renders header, graph cross-links, and route matrix', () => {
      component = mount(CanonGatewayView, { target, props: { theme: 'dark' } });

      expect(target.textContent).toContain('Canon Gateway & Machine-Readable AI Access');
      expect(target.textContent).toContain('Единый граф понятий Канона (Canon Cross-Links)');
      expect(target.textContent).toContain('Деонтическая логика');
      expect(target.textContent).toContain('Теория ограничений (TOC)');
      expect(target.textContent).toContain('A2A Экономика');
    });

    it('updates active graph node when cross-link buttons are clicked', async () => {
      component = mount(CanonGatewayView, { target, props: { theme: 'dark' } });

      const crosslinkBtn = target.querySelector('#crosslink-to-podcast-1') as HTMLButtonElement;
      expect(crosslinkBtn).not.toBeNull();

      crosslinkBtn.click();
      await new Promise((r) => setTimeout(r, 20));

      expect(target.textContent).toContain('ACTIVE_NODE: podcast-ep-01');
    });

    it('filters routes when active filter pills are clicked', async () => {
      component = mount(CanonGatewayView, { target, props: { theme: 'dark' } });

      const filterPills = target.querySelectorAll('#filter-pills-bar button');
      expect(filterPills.length).toBeGreaterThan(0);

      // Click "Read-Only" filter pill
      const readOnlyPill = Array.from(filterPills).find(b => b.textContent?.includes('Read-Only')) as HTMLButtonElement;
      expect(readOnlyPill).not.toBeNull();

      readOnlyPill.click();
      await new Promise((r) => setTimeout(r, 20));

      expect(target.textContent).toContain('/llms.txt');
    });
  });

  describe('CanonSearchModal Component', () => {
    it('does not render when isOpen is false', () => {
      component = mount(CanonSearchModal, { target, props: { isOpen: false, theme: 'dark' } });
      const modal = target.querySelector('#cmd-k-search-modal');
      expect(modal).toBeNull();
    });

    it('renders search modal and input when isOpen is true', () => {
      component = mount(CanonSearchModal, { target, props: { isOpen: true, theme: 'dark' } });
      const modal = target.querySelector('#cmd-k-search-modal');
      const input = target.querySelector('#canon-search-input') as HTMLInputElement;

      expect(modal).not.toBeNull();
      expect(input).not.toBeNull();
      expect(input.getAttribute('placeholder')).toContain('Cmd+K');
    });

    it('filters search results dynamically when input query changes', async () => {
      component = mount(CanonSearchModal, { target, props: { isOpen: true, theme: 'dark' } });
      const input = target.querySelector('#canon-search-input') as HTMLInputElement;

      input.value = 'деонтическая';
      input.dispatchEvent(new Event('input'));
      await new Promise((r) => setTimeout(r, 20));

      const results = target.querySelectorAll('#canon-search-results button');
      expect(results.length).toBeGreaterThan(0);
      expect(results[0].textContent).toContain('Деонтическая логика');
    });

    it('handles keyboard navigation with ArrowDown, ArrowUp, and Enter key', async () => {
      const selectSpy = vi.fn();
      component = mount(CanonSearchModal, {
        target,
        props: { isOpen: true, theme: 'dark', onSelectNode: selectSpy }
      });

      const input = target.querySelector('#canon-search-input') as HTMLInputElement;

      // Press ArrowDown
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
      await new Promise((r) => setTimeout(r, 20));

      // Press Enter to select first item
      input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true }));
      await new Promise((r) => setTimeout(r, 20));

      expect(selectSpy).toHaveBeenCalled();
    });
  });
});
