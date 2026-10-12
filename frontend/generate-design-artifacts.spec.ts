import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts for essay reader implementation', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-b15560ed-e980-46d1-a482-574ad3cbf54d');
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
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#app-root');

    // Click on [ARTICLE_STREAM] tab to ensure essay reader is visible
    await desktopPage.click('#tab-btn-article');
    await desktopPage.waitForTimeout(300);

    await desktopPage.screenshot({ path: path.join(targetDir, 'desktop-1440.png'), fullPage: true });

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
        'tab-btn-article',
        'tab-btn-podcast',
        'tab-btn-contact',
        'article-stream-container',
        'longreads-list-section',
        'article-reader-section',
        'article-reader-root',
        'article-header',
        'article-cover-container',
        'article-cover-img',
        'article-scroll-container',
        'article-telemetry-log',
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

    await mobilePage.click('#tab-btn-article');
    await mobilePage.waitForTimeout(300);

    await mobilePage.screenshot({ path: path.join(targetDir, 'mobile-375.png'), fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
