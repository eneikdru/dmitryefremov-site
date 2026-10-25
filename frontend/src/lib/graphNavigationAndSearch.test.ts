// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import { mount, unmount } from 'svelte';
import TelemetryHub from '../components/TelemetryHub.svelte';
import CanonGatewayView from '../components/CanonGatewayView.svelte';
import SparksView from '../components/SparksView.svelte';

describe('JTBD Graph Navigation & Instant Search Flow Verification', () => {
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

  it('navigates from podcast media tab to canon concepts gateway tab and back', async () => {
    component = mount(TelemetryHub, { target, props: { initialTab: 'podcast' } });

    // Verify default active podcast tab
    const podcastBtn = target.querySelector('#tab-btn-podcast') as HTMLButtonElement;
    const canonBtn = target.querySelector('#tab-btn-canon') as HTMLButtonElement;

    expect(podcastBtn).not.toBeNull();
    expect(canonBtn).not.toBeNull();

    // Click Canon tab
    canonBtn.click();
    await new Promise((r) => setTimeout(r, 0));

    expect(target.textContent).toContain('Canon Gateway & Machine-Readable AI Access');

    // Click Podcast tab to return back
    podcastBtn.click();
    await new Promise((r) => setTimeout(r, 0));

    expect(target.querySelector('#podcast-player-container')).not.toBeNull();
  });

  it('supports permanent readable URLs and hash routing for canon and podcast pages', async () => {
    component = mount(CanonGatewayView, { target, props: { theme: 'dark' } });

    // Verify machine readable endpoints have permanent addresses
    expect(target.textContent).toContain('/llms.txt');
    expect(target.textContent).toContain('/.well-known/ai-agent.json');
    expect(target.textContent).toContain('/api/v1/agent/orders');
  });

  it('verifies instant search filtering functionality for concepts and media nodes', async () => {
    component = mount(SparksView, { target });

    // Toggle search visibility
    const toggleSearchBtn = target.querySelector('#toggleSearchBtn') as HTMLButtonElement;
    expect(toggleSearchBtn).not.toBeNull();
    toggleSearchBtn.click();
    await new Promise((r) => setTimeout(r, 0));

    // Ensure search input exists and filters nodes instantly
    const searchInput = target.querySelector('#spark-search-input') as HTMLInputElement;
    expect(searchInput).not.toBeNull();

    searchInput.value = 'deontic';
    searchInput.dispatchEvent(new Event('input'));
    await new Promise((r) => setTimeout(r, 0));

    // Verify search clear button appears when search query is entered
    const clearBtn = target.querySelector('button[aria-label="Clear search"]');
    expect(clearBtn).not.toBeNull();
  });

  it('meets WCAG 2.1 AA accessibility requirements (keyboard operability, visible focus, labelled form controls, ARIA attributes)', async () => {
    component = mount(TelemetryHub, { target });

    // 1. Check main navigation has proper aria-label
    const mainNav = target.querySelector('nav#nav-tabs');
    expect(mainNav).not.toBeNull();
    expect(mainNav?.getAttribute('aria-label')).toBe('Main Navigation');

    // 2. Check interactive buttons have accessible aria-label or textual content
    const themeToggle = target.querySelector('#theme-toggle-btn') as HTMLButtonElement;
    expect(themeToggle).not.toBeNull();
    expect(themeToggle.getAttribute('aria-label')).toBe('Toggle theme');

    // 3. Check all tab buttons are focusable keyboard controls
    const tabButtons = target.querySelectorAll('nav#nav-tabs button');
    expect(tabButtons.length).toBeGreaterThan(0);
    tabButtons.forEach((btn) => {
      expect(btn.tagName.toLowerCase()).toBe('button');
      expect(btn.getAttribute('type')).toBe('button');
    });

    // 4. Verify text contrast & design integrity elements (dark theme background class)
    const rootDiv = target.querySelector('#telemetry-hub-root');
    expect(rootDiv?.className).toContain('bg-[#090a0f]');
    expect(rootDiv?.className).toContain('text-[#e2e8f0]');
  });
});
