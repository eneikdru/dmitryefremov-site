import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts for unified media player UI slice', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-8d3d8209-4762-4794-ae4d-bb4d903baf94');
  fs.mkdirSync(targetDir, { recursive: true });

  console.log('Building frontend with Vite...');
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

  console.log('Starting preview server on port 4173...');
  const previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
    cwd: path.resolve('.'),
    stdio: 'ignore'
  });

  await new Promise(resolve => setTimeout(resolve, 3000));

  try {
    const browser = await chromium.launch();

    console.log('Generating 1440px desktop screenshot & layout geometry...');
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Dismiss CMP modal if present
    const cmpDecline = desktopPage.locator('#cmp-decline-btn');
    if (await cmpDecline.isVisible()) {
      await cmpDecline.click();
      await desktopPage.waitForTimeout(300);
    }

    // Click on podcast tab
    await desktopPage.click('#tab-btn-podcast');
    await desktopPage.waitForTimeout(500);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'telemetry-hub-root',
        'hero-section',
        'nav-tabs',
        'tab-btn-podcast',
        'podcast-player-container',
        'podcast-episodes-section',
        'podcast-hero-player',
        'podcast-title',
        'waveform-container',
        'play-pause-btn',
        'core-theses-section',
        'timecodes-section',
        'timecodes-list',
        'mini-player-bar'
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

    console.log('Generating 375px mobile screenshot...');
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#telemetry-hub-root');

    // Dismiss CMP modal if present
    const mobileCmpDecline = mobilePage.locator('#cmp-decline-btn');
    if (await mobileCmpDecline.isVisible()) {
      await mobileCmpDecline.click();
      await mobilePage.waitForTimeout(300);
    }

    await mobilePage.click('#tab-btn-podcast');
    await mobilePage.waitForTimeout(500);

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();

    console.log('Design check artifacts generated successfully in', targetDir);
  } finally {
    previewProcess.kill();
  }
});
