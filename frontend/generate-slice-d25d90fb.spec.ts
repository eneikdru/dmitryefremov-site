import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design gate verification artifacts for slice d25d90fb', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-8ae149b3-a204-49e5-9f3e-8e5a9cb9e638');
  fs.mkdirSync(targetDir, { recursive: true });

  // Build frontend
  const buildProcess = spawn('npx', ['vite', 'build'], {
    cwd: path.resolve('.'),
    stdio: 'inherit'
  });

  await new Promise<void>((resolve, reject) => {
    buildProcess.on('exit', (code) => {
      if (code === 0) resolve();
      else reject(new Error(`vite build failed with code ${code}`));
    });
  });

  // Start preview server
  const previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
    cwd: path.resolve('.'),
    stdio: 'ignore'
  });

  await new Promise(resolve => setTimeout(resolve, 2500));

  try {
    const browser = await chromium.launch();

    // 1440px Desktop Screenshot
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173/#/canon-gateway');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Select Canon tab if not active
    const canonTab = await desktopPage.$('#tab-btn-canon');
    if (canonTab) {
      await canonTab.click();
      await desktopPage.waitForTimeout(300);
    }

    // Dismiss CMP modal if present
    const acceptBtn = await desktopPage.$('#cmp-accept-btn');
    if (acceptBtn) {
      await acceptBtn.click();
      await desktopPage.waitForTimeout(300);
    }

    await desktopPage.evaluate(() => window.scrollTo(0, 0));
    await desktopPage.waitForTimeout(200);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      window.scrollTo(0, 0);
      const elementsToTrack = [
        'canon-gateway-header',
        'gateway-header-title',
        'ingress-gateway-card',
        'ingress-card-title',
        'base-endpoint-box',
        'filter-pills-bar',
        'routes-list-section',
        'route-lore-summary',
        'route-wellknown-json',
        'route-agent-orders',
        'route-events-stream',
        'security-auth-card',
        'security-card-title',
        'main-footer'
      ];

      return elementsToTrack.map(id => {
        const el = document.getElementById(id);
        if (!el) return { id, left: 0, top: 0, width: 0, height: 0 };
        const rect = el.getBoundingClientRect();
        return {
          id,
          left: Math.round(rect.left + window.scrollX),
          top: Math.round(rect.top + window.scrollY),
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
    await mobilePage.goto('http://localhost:4173/#/canon-gateway');
    await mobilePage.waitForSelector('#telemetry-hub-root');

    // Select Canon tab if not active
    const mobileCanonTab = await mobilePage.$('#tab-btn-canon');
    if (mobileCanonTab) {
      await mobileCanonTab.click();
      await mobilePage.waitForTimeout(300);
    }

    // Dismiss CMP modal on mobile if present
    const mobileAcceptBtn = await mobilePage.$('#cmp-accept-btn');
    if (mobileAcceptBtn) {
      await mobileAcceptBtn.click();
      await mobilePage.waitForTimeout(300);
    }

    await mobilePage.evaluate(() => window.scrollTo(0, 0));
    await mobilePage.waitForTimeout(200);

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
