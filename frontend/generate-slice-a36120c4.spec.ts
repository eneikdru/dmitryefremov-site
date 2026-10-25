import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design gate verification artifacts for slice a36120c4', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-e53926bd-8197-42a4-9eff-f4a6c28b2e0e');
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
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Dismiss CMP modal if present
    const acceptBtn = await desktopPage.$('#cmp-accept-btn');
    if (acceptBtn) {
      await acceptBtn.click();
      await desktopPage.waitForTimeout(300);
    }

    // Switch to Canon Gateway tab
    await desktopPage.click('#tab-btn-canon');
    await desktopPage.waitForTimeout(300);

    // Click cross-link to podcast
    const crosslinkBtn = await desktopPage.$('#crosslink-to-podcast-1');
    if (crosslinkBtn) {
      await crosslinkBtn.click();
      await desktopPage.waitForTimeout(200);
    }

    // Open Cmd+K search modal
    await desktopPage.click('#cmd-k-trigger-btn');
    await desktopPage.waitForTimeout(300);

    // Type query into search modal
    await desktopPage.type('#canon-search-input', 'Деонтическая');
    await desktopPage.waitForTimeout(300);

    await desktopPage.evaluate(() => window.scrollTo(0, 0));
    await desktopPage.waitForTimeout(200);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      window.scrollTo(0, 0);
      const elementsToTrack = [
        'telemetry-hub-root',
        'sys-core-badge',
        'cmd-k-trigger-btn',
        'theme-toggle-btn',
        'hero-section',
        'hero-avatar-container',
        'hero-avatar-img',
        'hero-title',
        'nav-tabs',
        'tab-btn-podcast',
        'tab-btn-article',
        'tab-btn-sparks',
        'tab-btn-contact',
        'tab-btn-canon',
        'canon-gateway-root',
        'canon-gateway-header',
        'canon-graph-section',
        'crosslink-to-podcast-1',
        'crosslink-to-essay-1',
        'crosslink-to-podcast-2',
        'crosslink-to-spark-1',
        'ingress-gateway-card',
        'filter-pills-bar',
        'routes-list-section',
        'cmd-k-search-modal',
        'canon-search-input',
        'canon-search-results',
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
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#telemetry-hub-root');

    // Dismiss CMP modal on mobile
    const mobileAcceptBtn = await mobilePage.$('#cmp-accept-btn');
    if (mobileAcceptBtn) {
      await mobileAcceptBtn.click();
      await mobilePage.waitForTimeout(300);
    }

    // Switch to Canon Gateway tab
    await mobilePage.click('#tab-btn-canon');
    await mobilePage.waitForTimeout(300);

    // Open Cmd+K search modal on mobile
    await mobilePage.click('#cmd-k-trigger-btn');
    await mobilePage.waitForTimeout(300);

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
