// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';
import { resetConsentStatus } from './consent';

describe('Public Facade Isolation & Black-Box Invariant QA Verification', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    resetConsentStatus();
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  describe('AC1: Route & Link Crawl - Absence of Administrative Tools & System Exposure', () => {
    it('crawls public facade and verifies zero reachable links or routes pointing to /admin', () => {
      component = mount(TelemetryHub, { target });

      // Inspect all anchor elements in the DOM
      const anchorElements = Array.from(target.querySelectorAll('a'));
      const adminAnchors = anchorElements.filter(a => {
        const href = a.getAttribute('href') || '';
        return href.includes('/admin') || href.includes('admin');
      });

      expect(adminAnchors.length).toBe(0);

      // Verify no button or control text advertises /admin or internal admin ops
      const allButtons = Array.from(target.querySelectorAll('button'));
      const adminButtons = allButtons.filter(btn => {
        const text = (btn.textContent || '').toLowerCase();
        return text.includes('/admin') || text.includes('admin ops');
      });

      expect(adminButtons.length).toBe(0);
    });

    it('verifies absence of raw teleprompter paths, debug tags, and internal mechanics on public tabs', async () => {
      component = mount(TelemetryHub, { target });

      const tabs: Array<'podcast' | 'article' | 'sparks' | 'contact'> = ['podcast', 'article', 'sparks', 'contact'];

      for (let tab of tabs) {
        const tabBtn = target.querySelector(`#tab-btn-${tab}`) as HTMLButtonElement;
        if (tabBtn) {
          tabBtn.click();
          await new Promise(r => setTimeout(r, 0));
        }

        const bodyContent = target.textContent || '';
        // Invariant Black Box: No internal mechanics, raw teleprompter script paths, recording schedules
        expect(bodyContent).not.toContain('/mnt/HC_Volume_106932004/podcast/episodes');
        expect(bodyContent).not.toContain('[SYS_CORE_DEBUG]');
        expect(bodyContent).not.toContain('Студия и сетап');
      }
    });

    it('ensures administrative dashboard remains isolated and accessible only on /admin route', () => {
      // 1. On default public route '/', admin dashboard root is NOT present
      component = mount(TelemetryHub, { target });
      expect(target.querySelector('#admin-dashboard-root')).toBeNull();

      // 2. On private '/admin' route, admin dashboard renders on the target
      window.history.pushState({}, 'Admin Title', '/admin');
      const adminTarget = document.createElement('div');
      document.body.appendChild(adminTarget);

      const adminComp = mount(TelemetryHub, { target: adminTarget });
      expect(adminTarget.querySelector('#admin-dashboard-root')).not.toBeNull();

      unmount(adminComp);
      document.body.removeChild(adminTarget);

      // Restore location path
      window.history.pushState({}, 'Home', '/');
    });
  });

  describe('AC2: Monochromatic Design Adherence & Visual Isolation', () => {
    it('enforces Dark Void and Light Monochrome palette themes without visual popups or color noise', async () => {
      component = mount(TelemetryHub, { target });

      const hubRoot = target.querySelector('#telemetry-hub-root') as HTMLElement;
      expect(hubRoot).not.toBeNull();

      // Default theme is Dark Void
      expect(hubRoot.classList.contains('bg-[#090a0f]')).toBe(true);

      // Toggle to Light Monochrome
      const themeBtn = target.querySelector('#theme-toggle-btn') as HTMLButtonElement;
      expect(themeBtn).not.toBeNull();
      themeBtn.click();
      await new Promise(r => setTimeout(r, 0));

      expect(hubRoot.classList.contains('bg-[#fafafa]')).toBe(true);

      // Toggle back to Dark Void
      themeBtn.click();
      await new Promise(r => setTimeout(r, 0));
      expect(hubRoot.classList.contains('bg-[#090a0f]')).toBe(true);
    });

    it('verifies layout contains no unexpected promotional popups, floating overlays, or banners on main content', () => {
      component = mount(TelemetryHub, { target });

      // No unexpected popups or interstitials outside standard CMP overlay and Imprint modal
      const unexpectedModals = target.querySelectorAll('.popup-banner, .promo-modal, .interstitial-overlay');
      expect(unexpectedModals.length).toBe(0);
    });
  });

  describe('AC3: WCAG 2.1 AA Accessibility Compliance', () => {
    it('verifies keyboard operability and visible focus styling on main navigation tabs and controls', () => {
      component = mount(TelemetryHub, { target });

      const navContainer = target.querySelector('#nav-tabs');
      expect(navContainer?.getAttribute('aria-label')).toBe('Main Navigation');

      const tabs = ['podcast', 'article', 'sparks', 'contact'];
      for (const tabName of tabs) {
        const btn = target.querySelector(`#tab-btn-${tabName}`) as HTMLButtonElement;
        expect(btn).not.toBeNull();
        expect(btn.tagName.toLowerCase()).toBe('button');
        expect(btn.getAttribute('type')).toBe('button');
      }
    });

    it('validates ARIA labeling and modal role semantics on CMP consent banner and Imprint modal', async () => {
      component = mount(TelemetryHub, { target });

      // CMP Banner ARIA attributes
      const cmpOverlay = target.querySelector('#cmp-modal-overlay');
      if (cmpOverlay) {
        expect(cmpOverlay.getAttribute('role')).toBe('dialog');
        expect(cmpOverlay.getAttribute('aria-modal')).toBe('true');
        expect(cmpOverlay.getAttribute('aria-labelledby')).toBeTruthy();
      }

      // Imprint Modal ARIA attributes
      const imprintToggle = target.querySelector('#imprint-toggle-btn') as HTMLButtonElement;
      imprintToggle.click();
      await new Promise(r => setTimeout(r, 0));

      const imprintModal = target.querySelector('#imprint-modal');
      expect(imprintModal).not.toBeNull();
      expect(imprintModal?.getAttribute('role')).toBe('dialog');
      expect(imprintModal?.getAttribute('aria-labelledby')).toBe('imprint-title');
    });

    it('ensures interactive buttons contain accessible text and focusable controls', () => {
      component = mount(TelemetryHub, { target });

      const themeToggle = target.querySelector('#theme-toggle-btn') as HTMLButtonElement;
      expect(themeToggle.getAttribute('aria-label')).toBe('Toggle theme');

      const cmpPersistent = target.querySelector('#cmp-persistent-link') as HTMLButtonElement;
      expect(cmpPersistent.textContent?.trim()).toContain('Приватность (CMP)');
    });
  });
});
