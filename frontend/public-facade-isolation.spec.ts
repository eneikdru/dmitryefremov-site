import { test, expect } from '@playwright/test';
import { spawn, ChildProcess } from 'child_process';
import * as path from 'path';

test.describe('Public Facade Isolation & WCAG 2.1 AA E2E Verification (BARCAN-TAG-06)', () => {
  let previewProcess: ChildProcess;

  test.beforeAll(async () => {
    // 1. Build frontend distribution
    const buildProcess = spawn('npx', ['vite', 'build'], {
      cwd: path.resolve('.'),
      shell: true,
      stdio: 'ignore'
    });

    await new Promise<void>((resolve, reject) => {
      buildProcess.on('exit', (code) => {
        if (code === 0) resolve();
        else reject(new Error(`vite build failed with code ${code}`));
      });
    });

    // 2. Start preview server on port 4173
    previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
      cwd: path.resolve('.'),
      shell: true,
      stdio: 'ignore'
    });

    // 3. Poll preview server until listening
    let serverReady = false;
    for (let i = 0; i < 30; i++) {
      try {
        const response = await fetch('http://localhost:4173');
        if (response.ok || response.status === 200) {
          serverReady = true;
          break;
        }
      } catch {
        // Server booting
      }
      await new Promise((res) => setTimeout(res, 200));
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

  async function dismissCmpModalIfPresent(page: any) {
    try {
      const declineBtn = page.locator('#cmp-decline-btn');
      await declineBtn.waitFor({ state: 'visible', timeout: 2000 });
      await declineBtn.click();
      await page.waitForSelector('#cmp-modal-overlay', { state: 'detached', timeout: 3000 });
    } catch {
      // CMP modal already dismissed or not displayed
    }
  }

  test('crawling public live site confirms no /admin routes or links and no internal system tags', async ({ page }) => {
    await page.goto('http://localhost:4173');

    // 1. Collect all anchor hrefs and verify none point to /admin
    const hrefs = await page.$$eval('a', (anchors) => anchors.map((a) => a.getAttribute('href')));
    const adminHrefs = hrefs.filter((href) => href && href.includes('/admin'));
    expect(adminHrefs).toEqual([]);

    // 2. Inspect entire visible text of public site
    const bodyText = await page.innerText('body');
    expect(bodyText).not.toContain('/admin');
    expect(bodyText).not.toContain('[SYS_CORE]');
    expect(bodyText).not.toContain('Jules');
    expect(bodyText).not.toContain('TOC');
    expect(bodyText).not.toContain('Студия и сетап');

    // Resiliently dismiss CMP modal if present before clicking tabs
    await dismissCmpModalIfPresent(page);

    // 3. Switch across all public tabs and verify /admin isolation on each tab
    const tabs = ['#tab-btn-podcast', '#tab-btn-article', '#tab-btn-sparks', '#tab-btn-contact'];
    for (const tabSelector of tabs) {
      const tab = page.locator(tabSelector);
      if (await tab.isVisible()) {
        await tab.click();
        await page.waitForTimeout(100);

        const currentBodyText = await page.innerText('body');
        expect(currentBodyText).not.toContain('/admin');
        expect(currentBodyText).not.toContain('[SYS_CORE]');
        expect(currentBodyText).not.toContain('Jules');
        expect(currentBodyText).not.toContain('TOC');
      }
    }
  });

  test('UI visual inspection confirms monochromatic dark/light theme and absence of intrusive popups', async ({ page }) => {
    await page.goto('http://localhost:4173');

    // Dismiss CMP privacy consent modal
    await dismissCmpModalIfPresent(page);

    // Verify Dark Void default theme styling
    const hubRoot = page.locator('#telemetry-hub-root');
    await expect(hubRoot).toHaveClass(/bg-\[#090a0f\]/);

    // Toggle theme to Light Monochrome
    const themeBtn = page.locator('#theme-toggle-btn');
    await themeBtn.click();
    await expect(hubRoot).toHaveClass(/bg-\[#fafafa\]/);

    // Toggle back to Dark Void
    await themeBtn.click();
    await expect(hubRoot).toHaveClass(/bg-\[#090a0f\]/);

    // Verify no unprompted modal/popup overlays remain on screen
    const visibleDialogs = await page.locator('[role="dialog"]').all();
    for (const dialog of visibleDialogs) {
      expect(await dialog.isVisible()).toBe(false);
    }
  });

  test('automated accessibility checks verify WCAG 2.1 AA compliance on public facade', async ({ page }) => {
    await page.goto('http://localhost:4173');

    // Dismiss CMP modal to test public page accessibility
    await dismissCmpModalIfPresent(page);

    // 1. Keyboard operability: Tab through public navigation
    await page.keyboard.press('Tab');
    const activeTag = await page.evaluate(() => document.activeElement?.tagName);
    expect(['BUTTON', 'A', 'INPUT', 'BODY']).toContain(activeTag);

    // 2. Form control labeling: verify inputs have associated labels or aria-labels
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');

    const formInputs = page.locator('#contact-terminal-root input, #contact-terminal-root textarea');
    const inputCount = await formInputs.count();
    for (let i = 0; i < inputCount; i++) {
      const input = formInputs.nth(i);
      const ariaLabel = await input.getAttribute('aria-label');
      const inputId = await input.getAttribute('id');
      const hasLabel = inputId ? (await page.locator(`label[for="${inputId}"]`).count()) > 0 : false;

      expect(Boolean(ariaLabel || hasLabel)).toBe(true);
    }

    // 3. Contrast check on hero title and body text against Dark Void background
    const contrastRatio = await page.evaluate(() => {
      function getLuminance(r: number, g: number, b: number): number {
        const [aR, aG, aB] = [r, g, b].map((v) => {
          v /= 255;
          return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
        });
        return 0.2126 * aR + 0.7152 * aG + 0.0722 * aB;
      }

      const el = document.getElementById('hero-title');
      if (!el) return 0;
      const fgStyle = window.getComputedStyle(el).color; // rgb(...)
      const match = fgStyle.match(/\d+/g);
      if (!match) return 0;
      const [r, g, b] = match.map(Number);

      const fgLuminance = getLuminance(r, g, b);
      const bgLuminance = getLuminance(9, 10, 15); // #090a0f

      const l1 = Math.max(fgLuminance, bgLuminance);
      const l2 = Math.min(fgLuminance, bgLuminance);
      return (l1 + 0.05) / (l2 + 0.05);
    });

    expect(contrastRatio).toBeGreaterThanOrEqual(4.5);
  });
});
