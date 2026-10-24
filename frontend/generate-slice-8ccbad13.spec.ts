import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design gate verification artifacts for slice 8ccbad13', async () => {
  const repoRoot = fs.existsSync('.git') ? process.cwd() : path.resolve(process.cwd(), '..');
  const targetDir = path.join(repoRoot, '.eneik/records/design-check-84d3ec12-0001-4a22-8abf-c6d1832419fd');
  fs.mkdirSync(targetDir, { recursive: true });

  const frontendDir = fs.existsSync('frontend') ? path.resolve('frontend') : process.cwd();

  // Build frontend
  const buildProcess = spawn('npx', ['vite', 'build'], {
    cwd: frontendDir,
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
    cwd: frontendDir,
    stdio: 'ignore'
  });

  // Poll for server readiness
  let serverReady = false;
  for (let i = 0; i < 30; i++) {
    try {
      const res = await fetch('http://localhost:4173');
      if (res.ok) {
        serverReady = true;
        break;
      }
    } catch {
      // wait
    }
    await new Promise(r => setTimeout(r, 200));
  }

  if (!serverReady) {
    previewProcess.kill();
    throw new Error('Preview server on port 4173 did not start in time');
  }

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
        'sys-domain',
        'theme-toggle-btn',
        'hero-section',
        'hero-avatar-container',
        'hero-avatar-img',
        'hero-title',
        'hero-subtitle',
        'hero-manifest',
        'nav-tabs',
        'tab-btn-podcast',
        'tab-btn-article',
        'tab-btn-sparks',
        'tab-btn-contact',
        'content-view',
        'podcast-player-container',
        'podcast-episodes-section',
        'podcast-player-section',
        'podcast-player-card',
        'main-footer',
        'cmp-persistent-link',
        'imprint-toggle-btn'
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
