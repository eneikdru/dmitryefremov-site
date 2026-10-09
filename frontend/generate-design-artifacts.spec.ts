import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-11684031-d0fd-4bde-9f3d-086e62531ca8');
  fs.mkdirSync(targetDir, { recursive: true });

  // Build application
  const buildProcess = spawn('npx', ['vite', 'build'], {
    cwd: path.resolve('.'),
    stdio: 'ignore'
  });
  await new Promise(resolve => buildProcess.on('exit', resolve));

  // Start preview server
  const serverProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
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

    // Switch to contact terminal view
    await desktopPage.click('#tab-btn-contact');
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
        'contact-terminal-root',
        'contact-terminal-header',
        'contact-links-grid',
        'materials-catalogue-section',
        'materials-status-badge',
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

    await mobilePage.click('#tab-btn-contact');
    await mobilePage.waitForTimeout(500);

    await mobilePage.screenshot({ path: path.join(targetDir, 'mobile-375.png') });

    await mobileContext.close();
    await browser.close();
  } finally {
    serverProcess.kill();
  }
});
