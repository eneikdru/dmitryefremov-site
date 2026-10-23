import { test, chromium, expect } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts for Black-Box Intake UI', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-9a726ab5-fa90-43c3-9091-e66cc1f00078');
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

    // 1440px Desktop Viewport Screenshot and Layout Geometry Check
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Navigate to Contact & Intake section
    await desktopPage.click('#tab-btn-contact');
    await desktopPage.waitForTimeout(300);

    // Switch to Secure Intake Form tab
    const intakeTabBtn = desktopPage.locator('#contact-tab-intake-btn');
    if (await intakeTabBtn.isVisible()) {
      await intakeTabBtn.click();
      await desktopPage.waitForTimeout(300);
    }

    await desktopPage.waitForSelector('#vault-intake-root');

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'vault-intake-root',
        'intake-hero-header',
        'intake-main-title',
        'nda-status-card',
        'toggle-covenant-drawer-btn',
        'section-moniker',
        'submission-title-input',
        'section-archetype',
        'section-materiality',
        'section-narrative',
        'narrative-textarea',
        'section-email',
        'client-email-input',
        'section-confidentiality',
        'section-safeguards',
        'section-liaison',
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

    // 375px Mobile Viewport Screenshot
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#telemetry-hub-root');

    await mobilePage.click('#tab-btn-contact');
    await mobilePage.waitForTimeout(300);

    const mobileIntakeBtn = mobilePage.locator('#contact-tab-intake-btn');
    if (await mobileIntakeBtn.isVisible()) {
      await mobileIntakeBtn.click();
      await mobilePage.waitForTimeout(300);
    }

    await mobilePage.waitForSelector('#vault-intake-root');

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
