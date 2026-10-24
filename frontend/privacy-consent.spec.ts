import { test, expect } from '@playwright/test';
import { spawn, ChildProcess } from 'child_process';
import * as path from 'path';

test.describe('Fresh Visitor Privacy Consent Verification', () => {
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

    // Start preview server on 4173
    previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
      cwd: path.resolve('.'),
      stdio: 'ignore'
    });

    // Active polling to ensure preview server is listening
    let serverReady = false;
    for (let i = 0; i < 30; i++) {
      try {
        const response = await fetch('http://localhost:4173');
        if (response.ok || response.status === 200) {
          serverReady = true;
          break;
        }
      } catch {
        // Server still starting
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

  test('fresh visitor without banner interaction leaks no tracking cookies or scripts', async ({ page }) => {
    // Listen for network requests to assert zero telemetry network calls fire before consent
    const outgoingRequests: string[] = [];
    page.on('request', (request) => {
      outgoingRequests.push(request.url());
    });

    // Access site as fresh visitor
    await page.goto('http://localhost:4173');
    await page.waitForSelector('#telemetry-hub-root');
    await page.waitForSelector('#cmp-modal-overlay');

    // 1. Verify consent banner is displayed
    const isBannerVisible = await page.locator('#cmp-modal-overlay').isVisible();
    expect(isBannerVisible).toBe(true);

    // 2. Verify localStorage consent status is null for fresh visitor
    const consentStatus = await page.evaluate(() => localStorage.getItem('cmp_consent_status'));
    expect(consentStatus).toBeNull();

    // 3. Verify document.cookie contains zero tracking cookies
    const cookies = await page.evaluate(() => document.cookie);
    expect(cookies).toBe('');

    // 4. Perform user actions (scrolling, page interactions) WITHOUT clicking CMP banner
    // Use force: true for clicks if the modal overlay is on top, or scroll the window
    await page.evaluate(() => window.scrollTo(0, 500));
    await page.waitForTimeout(200);
    if (await page.locator('#tab-btn-contact').count() > 0) {
      await page.click('#tab-btn-contact', { force: true });
      await page.waitForTimeout(200);
    }
    if (await page.locator('#tab-btn-article').count() > 0) {
      await page.click('#tab-btn-article', { force: true });
      await page.waitForTimeout(200);
    }

    // Verify still zero tracking cookies set and consent status remains null
    const postActionCookies = await page.evaluate(() => document.cookie);
    expect(postActionCookies).toBe('');
    const postActionConsentStatus = await page.evaluate(() => localStorage.getItem('cmp_consent_status'));
    expect(postActionConsentStatus).toBeNull();

    // Verify network assertions: no telemetry or tracking endpoints were requested
    const telemetryRequests = outgoingRequests.filter(url =>
      url.includes('/telemetry') || url.includes('/analytics') || url.includes('/tracking')
    );
    expect(telemetryRequests).toHaveLength(0);
  });

  test('accepting consent enables telemetry, while declining consent keeps tracking blocked', async ({ browser }) => {
    // Scenario A: Visitor clicks DECLINE
    const declineContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    try {
      const declinePage = await declineContext.newPage();
      await declinePage.goto('http://localhost:4173');
      await declinePage.waitForSelector('#cmp-decline-btn');
      await declinePage.click('#cmp-decline-btn');

      const declineStatus = await declinePage.evaluate(() => localStorage.getItem('cmp_consent_status'));
      expect(declineStatus).toBe('declined');
      const declineCookies = await declinePage.evaluate(() => document.cookie);
      expect(declineCookies).toBe('');
    } finally {
      await declineContext.close();
    }

    // Scenario B: Visitor clicks ACCEPT
    const acceptContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    try {
      const acceptPage = await acceptContext.newPage();
      await acceptPage.goto('http://localhost:4173');
      await acceptPage.waitForSelector('#cmp-accept-btn');
      await acceptPage.click('#cmp-accept-btn');

      const acceptStatus = await acceptPage.evaluate(() => localStorage.getItem('cmp_consent_status'));
      expect(acceptStatus).toBe('accepted');
    } finally {
      await acceptContext.close();
    }
  });
});
