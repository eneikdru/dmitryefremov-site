import { test, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design gate verification artifacts for Black-Box Intake UI', async () => {
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

    // 1440px Desktop Screenshot and Layout Check
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Click on [✉️ КОНТАКТЫ & ИНТЕЙК] tab
    await desktopPage.click('#tab-btn-contact');
    await desktopPage.waitForTimeout(300);

    // Switch to [🔒 SECURE INTAKE FORM] section on contact terminal if available
    const secureIntakeTab = await desktopPage.$('#contact-tab-intake-btn');
    if (secureIntakeTab) {
      await secureIntakeTab.click();
      await desktopPage.waitForTimeout(300);
    }

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'telemetry-hub-root',
        'contact-terminal-root',
        'vault-intake-root',
        'label-classification',
        'submission-title-input',
        'client-email-input',
        'narrative-textarea',
        'toggle-discard-metadata',
        'toggle-recovery-phrase',
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

    // 375px Mobile Screenshot
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#telemetry-hub-root');

    await mobilePage.click('#tab-btn-[#tab-btn-contact]' ? '#tab-btn-contact' : '#tab-btn-contact');
    await mobilePage.waitForTimeout(300);

    const mobileSecureIntakeTab = await mobilePage.$('#contact-tab-intake-btn');
    if (mobileSecureIntakeTab) {
      await mobileSecureIntakeTab.click();
      await mobilePage.waitForTimeout(300);
    }

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
