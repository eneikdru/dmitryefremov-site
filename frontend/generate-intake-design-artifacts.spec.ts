import { test, chromium, expect } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts for vault intake UI', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-6d9649a1-69e4-4f1a-8eb1-5c0ee2eccfd7');
  fs.mkdirSync(targetDir, { recursive: true });

  // Build frontend first
  const buildProcess = spawn('npx', ['vite', 'build'], {
    cwd: path.resolve('.'),
    shell: true,
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
    shell: true,
    stdio: 'ignore'
  });

  // Wait for server to start
  await new Promise(resolve => setTimeout(resolve, 2500));

  try {
    const browser = await chromium.launch();

    // 1. 1440px Desktop Screenshot
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');

    // Dismiss CMP if open
    if (await desktopPage.locator('#cmp-accept-btn').isVisible()) {
      await desktopPage.click('#cmp-accept-btn');
    }

    // Switch to Contacts & Intake tab
    await desktopPage.waitForSelector('#tab-btn-contact');
    await desktopPage.click('#tab-btn-contact');
    await desktopPage.waitForSelector('#contact-terminal-root');

    // Switch to SECURE INTAKE FORM sub-tab
    await desktopPage.click('#contact-tab-intake-btn');
    await desktopPage.waitForSelector('#vault-intake-root');

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'vault-intake-root',
        'intake-header',
        'confidentiality-banner',
        'section-mandate',
        'codename',
        'tier-selector',
        'scope-selector',
        'execution-window',
        'client-email-input',
        'section-ip-repository',
        'upload-zone',
        'section-ratification',
        'nda-agree',
        'submit-intake-btn'
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

    // 2. 375px Mobile Screenshot
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173');

    if (await mobilePage.locator('#cmp-accept-btn').isVisible()) {
      await mobilePage.click('#cmp-accept-btn');
    }

    await mobilePage.waitForSelector('#tab-btn-contact');
    await mobilePage.click('#tab-btn-contact');
    await mobilePage.waitForSelector('#contact-terminal-root');

    await mobilePage.click('#contact-tab-intake-btn');
    await mobilePage.waitForSelector('#vault-intake-root');

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
