// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';
import { resetConsentStatus } from './consent';

describe('Public Facade Isolation & WCAG Compliance (BARCAN-TAG-06)', () => {
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

  it('verifies absence of /admin routes, admin links, and studio setup elements', () => {
    component = mount(TelemetryHub, { target });

    // 1. Assert no anchor tags or buttons point to /admin or containing /admin in href
    const adminLinks = target.querySelectorAll('a[href*="/admin"], button[data-route*="admin"]');
    expect(adminLinks.length).toBe(0);

    // 2. Assert no link or button with id "admin-portal-link" or text "/admin" exists
    expect(target.querySelector('#admin-portal-link')).toBeNull();
    expect(target.textContent).not.toContain('/admin');
    expect(target.textContent).not.toContain('Admin Ops');

    // 3. Assert studio setup tab ("Студия и сетап") and cards are absent from public view
    expect(target.querySelector('#tab-btn-gallery')).toBeNull();
    expect(target.textContent).not.toContain('Студия и сетап');
    expect(target.textContent).not.toContain('Студийные кадры');
    expect(target.textContent).not.toContain('Студийное пространство');
  });

  it('guarantees black-box invariant: absence of internal system tags and mechanics', () => {
    component = mount(TelemetryHub, { target });

    const textContent = target.textContent || '';

    // Verify system tags are not rendered in public text
    expect(textContent).not.toContain('[SYS_CORE]');
    expect(textContent).not.toContain('Jules');
    expect(textContent).not.toContain('TOC');
    expect(textContent).not.toContain('[TELEMETRY_QUARANTINE_MONITOR]');

    // Verify no internal debug frames or recording schedules
    expect(textContent).not.toContain('расписание записи');
    expect(textContent).not.toContain('отладочные рамки');
    expect(textContent).not.toContain('внутренняя кухня');
  });

  it('adheres to strict monochromatic design without intrusive popups', () => {
    component = mount(TelemetryHub, { target });

    const rootElement = target.querySelector('#telemetry-hub-root') as HTMLElement;
    expect(rootElement).not.toBeNull();

    // Verify dark void background color class
    expect(rootElement.classList.contains('bg-[#090a0f]')).toBe(true);

    // Verify modal overlay is CMP consent dialog, not marketing popups or visual noise
    const modalOverlays = target.querySelectorAll('[role="dialog"]');
    modalOverlays.forEach((modal) => {
      // Modals must be either CMP consent manager or legal imprint
      const id = modal.id;
      expect(['cmp-modal-overlay', 'imprint-modal']).toContain(id);
    });
  });

  it('meets WCAG 2.1 AA accessibility guidelines: keyboard operability, labels, and focusability', () => {
    component = mount(TelemetryHub, { target });

    // 1. All interactive buttons have explicit aria-label or readable non-empty text content
    const buttons = Array.from(target.querySelectorAll('button'));
    buttons.forEach((btn) => {
      const hasAriaLabel = !!btn.getAttribute('aria-label');
      const hasTextContent = (btn.textContent || '').trim().length > 0;
      expect(hasAriaLabel || hasTextContent).toBe(true);
      expect(btn.hasAttribute('disabled')).toBe(false);
    });

    // 2. All interactive links have href and accessible text/label
    const links = Array.from(target.querySelectorAll('a'));
    links.forEach((link) => {
      expect(link.hasAttribute('href')).toBe(true);
      const hasAriaLabel = !!link.getAttribute('aria-label');
      const hasTextContent = (link.textContent || '').trim().length > 0;
      expect(hasAriaLabel || hasTextContent).toBe(true);
    });

    // 3. Navigation bar has accessible landmark label
    const nav = target.querySelector('nav#nav-tabs');
    expect(nav).not.toBeNull();
    expect(nav?.getAttribute('aria-label')).toBe('Main Navigation');

    // 4. Form inputs across active view have corresponding labels or aria-labels
    const inputs = Array.from(target.querySelectorAll('input, select, textarea'));
    inputs.forEach((input) => {
      const id = input.getAttribute('id');
      const ariaLabel = input.getAttribute('aria-label');
      const ariaLabelledBy = input.getAttribute('aria-labelledby');
      const hasAssociatedLabel = id ? target.querySelector(`label[for="${id}"]`) !== null : false;

      expect(ariaLabel || ariaLabelledBy || hasAssociatedLabel).toBeTruthy();
    });
  });
});
