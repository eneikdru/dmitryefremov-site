import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-b14eb987-2a05-46aa-8c44-c7a9076a46ac');
  fs.mkdirSync(targetDir, { recursive: true });

  // Start preview server
  const previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
    cwd: path.resolve('.'),
    stdio: 'ignore'
  });

  // Wait for server to start
  await new Promise(resolve => setTimeout(resolve, 2000));

  try {
    const browser = await chromium.launch();

    // 1440px Desktop Screenshot
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#app-root');

    // Toggle theme to verify interaction
    await desktopPage.click('#theme-toggle-btn');
    await desktopPage.waitForTimeout(300);
    await desktopPage.click('#theme-toggle-btn');
    await desktopPage.waitForTimeout(300);

    // Scroll article container to trigger telemetry
    await desktopPage.evaluate(() => {
      const el = document.getElementById('article-scroll-container');
      if (el) el.scrollTop = 200;
    });
    await desktopPage.waitForTimeout(500);

    await desktopPage.screenshot({ path: path.join(targetDir, 'desktop-1440.png') });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'main-header',
        'hero-section',
        'hero-title',
        'hero-subtitle',
        'hero-manifest',
        'theme-toggle-btn',
        'nav-tabs',
        'tab-btn-article',
        'tab-btn-contact',
        'article-reader-root',
        'main-footer',
        'imprint-toggle-btn'
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

    fs.writeFileSync(
      path.join(targetDir, 'layout-check.json'),
      JSON.stringify(layoutBoundingBoxes, null, 2)
    );

    await desktopContext.close();

    // 375px Mobile Screenshot
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#app-root');

    // Click Imprint to show modal on mobile
    await mobilePage.click('#imprint-toggle-btn');
    await mobilePage.waitForTimeout(300);

    await mobilePage.screenshot({ path: path.join(targetDir, 'mobile-375.png') });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
