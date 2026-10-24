import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

test('generate design gate verification artifacts for slice 8fd3dd2a', async () => {
  const targetDir = path.join(__dirname, '..', '.eneik', 'records', 'design-check-0055f6d1-e6d5-4bdf-a70b-10548f5f7686');
  fs.mkdirSync(targetDir, { recursive: true });

  // Build frontend
  const buildProcess = spawn('npx', ['vite', 'build'], {
    cwd: __dirname,
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
    cwd: __dirname,
    stdio: 'ignore'
  });

  await new Promise((resolve) => setTimeout(resolve, 2500));

  try {
    const browser = await chromium.launch();

    // 1440px Desktop Screenshot
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#canon-graph-telemetry-root');

    // Dismiss CMP modal if present
    const acceptBtn = await desktopPage.$('#cmp-accept-btn');
    if (acceptBtn) {
      await acceptBtn.click();
      await desktopPage.waitForTimeout(300);
    }

    // Trigger a cross-link click to show live event trace log
    const crossLinkBtn = await desktopPage.$('#crosslink-concept_ontological_autonomy-to-podcast_ep03');
    if (crossLinkBtn) {
      await crossLinkBtn.click();
      await desktopPage.waitForTimeout(200);
    }

    await desktopPage.evaluate(() => window.scrollTo(0, 0));
    await desktopPage.waitForTimeout(200);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      window.scrollTo(0, 0);
      const elementsToTrack = [
        'main-header',
        'cmd-k-trigger-btn',
        'nav-tabs',
        'tab-btn-graph',
        'kpi-grid',
        'kpi-card-graph-nav',
        'kpi-card-canon-search',
        'kpi-card-pivot-rate',
        'kpi-card-active-explorers',
        'graph-crosslink-section',
        'audit-event-trace-section',
        'audit-trace-log',
        'main-footer'
      ];

      return elementsToTrack.map((id) => {
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
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#canon-graph-telemetry-root');

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
