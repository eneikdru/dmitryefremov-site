import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

test.describe('Accessibility & Theme Preference Automated Verification', () => {
  test('Given the deployed site, When an automated accessibility scan runs, Then zero WCAG 2.1 AA violations are reported', async ({ page }) => {
    await page.goto('/');

    // Ensure main layout is rendered
    await expect(page.locator('#telemetry-hub-root')).toBeVisible();

    // Run Axe scan targeting WCAG 2.1 AA criteria
    const accessibilityScanResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();

    expect(accessibilityScanResults.violations).toEqual([]);
  });

  test('Given a browser with predefined "light" theme preference, When the site loads, Then the correct light theme is applied automatically', async ({ page }) => {
    await page.addInitScript(() => {
      window.localStorage.setItem('theme-preference', 'light');
    });

    await page.goto('/');

    const themeRoot = page.locator('#telemetry-hub-root');
    await expect(themeRoot).toBeVisible();
    await expect(themeRoot).toHaveClass(/bg-\[#fafafa\]/);

    const themeToggleBtn = page.locator('#theme-toggle-btn');
    await expect(themeToggleBtn).toContainText('Light Monochrome');
  });

  test('Given a browser with predefined "dark" theme preference, When the site loads, Then the correct dark theme is applied automatically', async ({ page }) => {
    await page.addInitScript(() => {
      window.localStorage.setItem('theme-preference', 'dark');
    });

    await page.goto('/');

    const themeRoot = page.locator('#telemetry-hub-root');
    await expect(themeRoot).toBeVisible();
    await expect(themeRoot).toHaveClass(/bg-\[#090a0f\]/);

    const themeToggleBtn = page.locator('#theme-toggle-btn');
    await expect(themeToggleBtn).toContainText('Dark Void');
  });

  test('When the theme toggle button is interacted with, Then the theme toggles and persists preference in localStorage', async ({ page }) => {
    await page.goto('/');

    const themeToggleBtn = page.locator('#theme-toggle-btn');
    const themeRoot = page.locator('#telemetry-hub-root');

    await expect(themeToggleBtn).toContainText('Dark Void');
    await expect(themeRoot).toHaveClass(/bg-\[#090a0f\]/);

    // Toggle to Light
    await themeToggleBtn.click();
    await expect(themeToggleBtn).toContainText('Light Monochrome');
    await expect(themeRoot).toHaveClass(/bg-\[#fafafa\]/);

    let storedTheme = await page.evaluate(() => window.localStorage.getItem('theme-preference'));
    expect(storedTheme).toBe('light');

    // Toggle back to Dark
    await themeToggleBtn.click();
    await expect(themeToggleBtn).toContainText('Dark Void');
    await expect(themeRoot).toHaveClass(/bg-\[#090a0f\]/);

    storedTheme = await page.evaluate(() => window.localStorage.getItem('theme-preference'));
    expect(storedTheme).toBe('dark');
  });
});
