import { test, chromium, expect } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts for canon reader implementation', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-370a2e92-85b4-4daf-9b74-0dbfa0c15c23');
  fs.mkdirSync(targetDir, { recursive: true });

  // Build frontend first
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

  // Wait for server to start
  await new Promise(resolve => setTimeout(resolve, 2500));

  try {
    const browser = await chromium.launch();

    // 1440px Desktop Screenshot
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.addInitScript(() => {
      window.localStorage.setItem('cmp_consent_status', 'accepted');
    });
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Click on [CANON] tab
    await desktopPage.click('#tab-btn-canon');
    await desktopPage.waitForTimeout(500);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'telemetry-hub-root',
        'canon-reader-root',
        'canon-header',
        'canon-domain-nav',
        'domain-tab-all',
        'domain-tab-autonomous_engineering',
        'domain-tab-agentic_economy',
        'domain-tab-pragmatic_business_strategy',
        'domain-tab-philosophy_of_mind',
        'canon-filters-section',
        'type-filter-all',
        'canon-search-input',
        'canon-catalog-aside',
        'canon-item-list',
        'canon-reader-article-container',
        'canon-reader-article',
        'canon-article-header',
        'canon-article-title',
        'canon-article-summary',
        'canon-article-content',
        'canon-references-footer',
        'outgoing-references-block',
        'incoming-references-block',
        'main-footer'
      ];

      return elementsToTrack.map(id => {
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
    await mobilePage.addInitScript(() => {
      window.localStorage.setItem('cmp_consent_status', 'accepted');
    });
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#telemetry-hub-root');

    await mobilePage.click('#tab-btn-canon');
    await mobilePage.waitForTimeout(500);

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
