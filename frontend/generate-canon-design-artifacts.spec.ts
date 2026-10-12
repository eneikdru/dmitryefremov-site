import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate canon design verification artifacts', async () => {
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

    // 1440px Desktop Screenshot and Audit
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#app-root');

    // Dismiss CMP modal if present
    if (await desktopPage.isVisible('#cmp-decline-btn')) {
      await desktopPage.click('#cmp-decline-btn');
      await desktopPage.waitForTimeout(200);
    }

    // Make sure [CANON_READER] tab is active
    await desktopPage.click('#tab-btn-canon');
    await desktopPage.waitForTimeout(300);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'main-header',
        'hero-section',
        'hero-avatar-container',
        'hero-avatar-img',
        'hero-title',
        'hero-subtitle',
        'hero-manifest',
        'theme-toggle-btn',
        'nav-tabs',
        'tab-btn-canon',
        'tab-btn-article',
        'tab-btn-podcast',
        'tab-btn-contact',
        'canon-reader-container',
        'canon-list-section',
        'canon-reader-section',
        'canon-reader-article',
        'canon-header',
        'canon-scroll-container',
        'canon-cross-references',
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
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#app-root');

    if (await mobilePage.isVisible('#cmp-decline-btn')) {
      await mobilePage.click('#cmp-decline-btn');
      await mobilePage.waitForTimeout(200);
    }

    await mobilePage.click('#tab-btn-canon');
    await mobilePage.waitForTimeout(300);

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
