import { test, expect, chromium } from '@playwright/test';
import { spawn, ChildProcess } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test.describe('Admin Dashboard, Consent Banner & GDPR Compliance Verification', () => {
  let previewProcess: ChildProcess;

  test.beforeAll(async () => {
    // Build frontend
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

    // Start preview server
    previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
      cwd: path.resolve('.'),
      stdio: 'ignore'
    });

    // Poll for server start
    let serverReady = false;
    for (let i = 0; i < 30; i++) {
      try {
        const response = await fetch('http://localhost:4173');
        if (response.ok || response.status === 200) {
          serverReady = true;
          break;
        }
      } catch {
        // Wait
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

  test('Given site loads for first time, When navigated via keyboard, Then consent banner is focused, operable, and rejecting is one click', async ({ page }) => {
    await page.goto('http://localhost:4173');
    await page.waitForSelector('#cmp-modal-overlay');

    // Consent banner is visible
    const bannerVisible = await page.locator('#cmp-modal-overlay').isVisible();
    expect(bannerVisible).toBe(true);

    // Verify initial focus is on #cmp-decline-btn or container
    const activeElementId = await page.evaluate(() => document.activeElement?.id);
    expect(['cmp-decline-btn', 'cmp-container']).toContain(activeElementId);

    // Test rejecting via single click on [DECLINE_TRACKERS] button
    await page.click('#cmp-decline-btn');

    // Modal closes immediately and consent status is recorded as declined
    await page.waitForSelector('#cmp-modal-overlay', { state: 'detached' });
    const consentStatus = await page.evaluate(() => localStorage.getItem('cmp_consent_status'));
    expect(consentStatus).toBe('declined');
  });

  test('Given private /admin route, When accessed via keyboard, controls are focusable and destructive deletion confirmation is presented', async ({ page }) => {
    await page.goto('http://localhost:4173/admin');
    await page.waitForSelector('#admin-dashboard-root');

    // Verify admin header & metrics are rendered
    await expect(page.locator('#admin-header')).toContainText('Trust & Compliance');
    await expect(page.locator('#statutory-benchmark-banner')).toContainText('GDPR / CCPA Compliant: 99.4%');

    // Tab through key controls to verify focusability
    await page.keyboard.press('Tab');
    const firstFocusId = await page.evaluate(() => document.activeElement?.id);
    expect(firstFocusId).toBeTruthy();

    // Verify Execute Deletion opens destructive confirmation modal
    const deleteBtn = page.locator('#dsar-8821-delete-btn');
    await expect(deleteBtn).toBeVisible();
    await deleteBtn.click();

    // Confirmation modal opens
    await page.waitForSelector('#delete-confirmation-overlay');
    await expect(page.locator('#delete-modal-title')).toContainText('Confirm Destructive Erasure');

    // Confirm button is disabled until DELETE is typed
    const confirmBtn = page.locator('#confirm-deletion-btn');
    await expect(confirmBtn).toBeDisabled();

    await page.fill('#delete-confirm-input', 'DELETE');
    await expect(confirmBtn).toBeEnabled();

    // Confirm deletion
    await confirmBtn.click();
    await page.waitForSelector('#delete-confirmation-overlay', { state: 'detached' });

    // Item status updates to DELETED / PURGED
    await expect(page.locator('#dsar-item-8821')).toContainText('DELETED / PURGED');
  });

  test('generate required design gate verification artifacts', async () => {
    const targetDir = path.resolve('../.eneik/records/design-check-c295629c-8e57-43c8-9601-d833bdefe591');
    fs.mkdirSync(targetDir, { recursive: true });

    const browser = await chromium.launch();

    // 1440px Desktop Screenshot
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173/admin');
    await desktopPage.waitForSelector('#admin-dashboard-root');
    await desktopPage.waitForTimeout(300);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'admin-header',
        'statutory-benchmark-banner',
        'admin-metrics-grid',
        'dsar-queue-section',
        'dsar-item-8821',
        'dsar-item-8819',
        'dsar-item-8814',
        'system-isolation-section',
        'admin-bottom-nav'
      ];

      return elementsToTrack.map((id) => {
        const el = document.getElementById(id);
        if (!el) return { id, left: 0, top: 0, width: 0, height: 0 };
        const rect = el.getBoundingClientRect();
        return {
          id,
          left: Math.round(rect.left),
          top: Math.round(rect.top),
          width: Math.round(rect.width),
          height: Math.round(rect.height)
        };
      });
    });

    const layoutCheckPath = path.join(targetDir, 'layout-check.json');
    fs.writeFileSync(layoutCheckPath, JSON.stringify(layoutBoundingBoxes, null, 2));
    await desktopContext.close();

    // 375px Mobile Screenshot
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173/admin');
    await mobilePage.waitForSelector('#admin-dashboard-root');
    await mobilePage.waitForTimeout(300);

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();

    expect(fs.existsSync(desktopScreenshotPath)).toBe(true);
    expect(fs.existsSync(path.join(targetDir, 'mobile-375.png'))).toBe(true);
    expect(fs.existsSync(layoutCheckPath)).toBe(true);
  });
});
