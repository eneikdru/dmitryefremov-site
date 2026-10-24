import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design gate verification artifacts for slice 3cc4e56c', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-1b87300e-8ae1-438f-9d9b-11664b2c8419');
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

    // 1440px Desktop Screenshot with Cmd+K Search Modal Open
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

    // Open search modal via trigger button or key combo
    const searchBtn = await desktopPage.$('#cmd-k-search-trigger');
    if (searchBtn) {
      await searchBtn.click();
      await desktopPage.waitForTimeout(500);
    }

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
        'cmd-k-search-trigger',
        'theme-toggle-btn',
        'hero-section',
        'hero-title',
        'nav-tabs',
        'tab-btn-podcast',
        'tab-btn-article',
        'tab-btn-sparks',
        'content-cross-links-card',
        'corpus-search-modal',
        'corpus-search-input',
        'search-results-header',
        'search-prev-btn',
        'search-next-btn',
        'search-open-btn',
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

    // Open search modal on mobile
    const mobileSearchBtn = await mobilePage.$('#cmd-k-search-trigger');
    if (mobileSearchBtn) {
      await mobileSearchBtn.click();
      await mobilePage.waitForTimeout(500);
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
