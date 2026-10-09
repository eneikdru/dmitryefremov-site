import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts for podcast media UI', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-67c6d186-4f25-4d6b-87a6-393a9d5e67d9');
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

    // Click on [PODCAST_MEDIA] tab
    await desktopPage.click('#tab-btn-podcast');
    await desktopPage.waitForTimeout(300);

    // Click on timecode button to demonstrate precise timestamp seek
    const secondTimecodeBtn = await desktopPage.locator('[id^="timecode-btn-"]').nth(1);
    if (await secondTimecodeBtn.isVisible()) {
      await secondTimecodeBtn.click();
      await desktopPage.waitForTimeout(300);
    }

    await desktopPage.screenshot({ path: path.join(targetDir, 'desktop-1440.png'), fullPage: true });

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
        'tab-btn-podcast',
        'tab-btn-contact',
        'podcast-player-container',
        'podcast-episodes-section',
        'podcast-player-section',
        'podcast-player-card',
        'podcast-header',
        'podcast-title',
        'audio-controls-block',
        'play-pause-btn',
        'time-display',
        'waveform-container',
        'timecodes-section',
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

    // Navigate to podcast media tab on mobile
    await mobilePage.click('#tab-btn-podcast');
    await mobilePage.waitForTimeout(300);

    await mobilePage.screenshot({ path: path.join(targetDir, 'mobile-375.png'), fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
