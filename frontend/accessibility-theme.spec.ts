import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';
import { spawn, ChildProcess } from 'child_process';
import * as path from 'path';

test.describe('Accessibility & Theme Switching QA Verification', () => {
  let previewProcess: ChildProcess;

  test.beforeAll(async () => {
    // 1. Build frontend distribution
    const buildProcess = spawn('npx', ['vite', 'build'], {
      cwd: path.resolve('.'),
      stdio: 'ignore'
    });

    await new Promise<void>((resolve, reject) => {
      buildProcess.on('exit', (code) => {
        if (code === 0) resolve();
        else reject(new Error(`vite build failed with code ${code}`));
      });
    });

    // 2. Start local preview server on port 4173
    previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
      cwd: path.resolve('.'),
      stdio: 'ignore'
    });

    // 3. Active polling to ensure preview server is listening
    let serverReady = false;
    for (let i = 0; i < 30; i++) {
      try {
        const response = await fetch('http://localhost:4173');
        if (response.ok || response.status === 200) {
          serverReady = true;
          break;
        }
      } catch {
        // Wait for server boot
      }
      await new Promise(res => setTimeout(res, 200));
    }

    if (!serverReady) {
      throw new Error('Preview server failed to start on port 4173');
    }
  });

  test.afterAll(() => {
    if (previewProcess) {
      previewProcess.kill();
    }
  });

  test('automated accessibility scan reports zero WCAG 2.1 AA violations on deployed site', async ({ page }) => {
    await page.goto('http://localhost:4173');

    // Run axe accessibility scan for WCAG 2.1 AA standards
    const accessibilityScanResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();

    expect(accessibilityScanResults.violations).toEqual([]);
  });

  test('predefined system theme preference (light) is applied automatically upon load', async ({ page }) => {
    await page.emulateMedia({ colorScheme: 'light' });
    await page.goto('http://localhost:4173');

    const themeBtn = page.locator('#theme-toggle-btn');
    await expect(themeBtn).toContainText('Light Monochrome');

    const htmlThemeAttr = await page.getAttribute('html', 'data-theme');
    expect(htmlThemeAttr).toBe('light');
  });

  test('predefined system theme preference (dark) is applied automatically upon load', async ({ page }) => {
    await page.emulateMedia({ colorScheme: 'dark' });
    await page.goto('http://localhost:4173');

    const themeBtn = page.locator('#theme-toggle-btn');
    await expect(themeBtn).toContainText('Dark Void');

    const htmlThemeAttr = await page.getAttribute('html', 'data-theme');
    expect(htmlThemeAttr).toBe('dark');
  });

  test('predefined localStorage theme preference automatically overrides system preference', async ({ page }) => {
    await page.addInitScript(() => {
      localStorage.setItem('theme-preference', 'light');
    });
    await page.emulateMedia({ colorScheme: 'dark' });
    await page.goto('http://localhost:4173');

    const themeBtn = page.locator('#theme-toggle-btn');
    await expect(themeBtn).toContainText('Light Monochrome');

    const htmlThemeAttr = await page.getAttribute('html', 'data-theme');
    expect(htmlThemeAttr).toBe('light');
  });
});
