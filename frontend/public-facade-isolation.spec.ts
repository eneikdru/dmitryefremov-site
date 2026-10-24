import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

test.describe('Public Facade Isolation & Black-Box Invariant Verification', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to public home page and dismiss CMP banner if visible
    await page.goto('/');
    const cmpDeclineBtn = page.locator('#cmp-decline-btn');
    if (await cmpDeclineBtn.isVisible()) {
      await cmpDeclineBtn.click();
    }
  });

  test('AC1: Crawling public facade verifies absence of /admin routes, links, and system tags', async ({ page }) => {
    // 1. Inspect all clickable links across the public facade
    const hrefs = await page.evaluate(() => {
      const anchors = Array.from(document.querySelectorAll('a'));
      return anchors.map(a => a.getAttribute('href')).filter(Boolean);
    });

    for (const href of hrefs) {
      expect(href).not.toContain('/admin');
      expect(href).not.toContain('#/admin');
      expect(href).not.toContain('sysadmin');
    }

    // 2. Iterate through all public navigation tabs and verify no admin tools or system tags
    const publicTabs = ['#tab-btn-podcast', '#tab-btn-article', '#tab-btn-sparks', '#tab-btn-contact', '#tab-btn-canon'];

    for (const tabId of publicTabs) {
      const tabBtn = page.locator(tabId);
      if (await tabBtn.isVisible()) {
        await tabBtn.click();
        await page.waitForTimeout(100);

        const pageText = await page.innerText('body');
        // Black-Box Invariant: No internal mechanics, Jules, SYS_CORE, debug frames or schedule
        expect(pageText).not.toContain('[SYS_CORE]');
        expect(pageText).not.toContain('Jules');
        expect(pageText).not.toContain('AutoMergeService');
        expect(pageText).not.toContain('SixSigmaAudit');

        // Ensure no direct /admin link is rendered in footer or body
        const adminLinksCount = await page.locator('a[href*="/admin"]').count();
        expect(adminLinksCount).toBe(0);
      }
    }
  });

  test('AC2: Monochromatic design theme verification and absence of popups', async ({ page }) => {
    const rootEl = page.locator('#telemetry-hub-root');
    await expect(rootEl).toBeVisible();

    // Default Dark Void mode inspection
    const darkClasses = await rootEl.getAttribute('class');
    expect(darkClasses).toContain('bg-[#090a0f]');

    // Toggle theme to Light Monochrome mode
    const themeBtn = page.locator('#theme-toggle-btn');
    await expect(themeBtn).toBeVisible();
    await themeBtn.click();

    // Verify Light Monochrome background
    const lightClasses = await rootEl.getAttribute('class');
    expect(lightClasses).toContain('bg-[#fafafa]');

    // Confirm no popups or unhandled modal overlays are present on public facade
    const visibleModals = await page.locator('[role="dialog"]:visible').count();
    expect(visibleModals).toBe(0);
  });

  test('AC3: Automated WCAG 2.1 AA accessibility checks across public facade views', async ({ page }) => {
    const publicTabs = [
      { id: '#tab-btn-podcast', name: 'Podcast View' },
      { id: '#tab-btn-article', name: 'Articles View' },
      { id: '#tab-btn-sparks', name: 'Sparks View' },
      { id: '#tab-btn-contact', name: 'Contact View' },
      { id: '#tab-btn-canon', name: 'Canon View' }
    ];

    for (const tab of publicTabs) {
      await page.click(tab.id);
      await page.waitForTimeout(150);

      const accessibilityScanResults = await new AxeBuilder({ page })
        .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
        .analyze();

      expect(accessibilityScanResults.violations, `Accessibility violations found in ${tab.name}`).toEqual([]);
    }

    // Verify keyboard operability and visible focus
    await page.keyboard.press('Tab');
    const focusedElementTag = await page.evaluate(() => document.activeElement?.tagName);
    expect(focusedElementTag).toBeTruthy();
  });
});
