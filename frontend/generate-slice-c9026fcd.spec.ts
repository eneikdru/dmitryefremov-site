import { test, chromium, expect } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('verify contact form UI, error text retention, social links, and generate design artifacts', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-11abaafa-3695-4623-99c2-5f53da1655c7');
  fs.mkdirSync(targetDir, { recursive: true });

  // 1. Build frontend
  const buildProcess = spawn('npx', ['vite', 'build'], {
    cwd: path.resolve('.'),
    stdio: 'ignore'
  });

  await new Promise<void>((resolve, reject) => {
    buildProcess.on('exit', (code) => {
      if (code === 0) resolve();
      else reject(new Error(`vite build failed with code ${code}`));
    });
  });

  // 2. Start preview server
  const previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
    cwd: path.resolve('.'),
    stdio: 'ignore'
  });

  // Poll server until ready
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
    throw new Error('Preview server failed to start on port 4173');
  }

  try {
    const browser = await chromium.launch();

    // 1440px Desktop Test & Screenshot
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const page = await desktopContext.newPage();
    await page.goto('http://localhost:4173');

    // Dismiss CMP if present
    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // Navigate to Contact tab
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');

    // Verify social links point to Dmitry Efremov without placeholders
    const tgLink = page.locator('#link-tg');
    await expect(tgLink).toHaveAttribute('href', 'https://t.me/efremov_mvp');

    const ytLink = page.locator('#link-yt');
    await expect(ytLink).toHaveAttribute('href', 'https://youtube.com/@dmitrii.efremov');

    const ghLink = page.locator('#link-gh');
    await expect(ghLink).toHaveAttribute('href', 'https://github.com/dmitryefremov');

    const liLink = page.locator('#link-li');
    await expect(liLink).toHaveAttribute('href', 'https://linkedin.com/in/dmitryefremov');

    // Verify Impressum is accessible
    await page.click('#imprint-toggle-btn');
    await expect(page.locator('#imprint-modal')).toBeVisible();
    await expect(page.locator('text=Дмитрий Ефремов (Dmitry Efremov)').first()).toBeVisible();
    await page.click('#imprint-close-btn');
    await expect(page.locator('#imprint-modal')).not.toBeVisible();

    // Test Validation Error: Typed text remains in form
    await page.fill('#contact-full-name', 'Test User');
    await page.fill('#contact-email-addr', 'invalid-email-format');
    await page.fill('#contact-message-text', 'Short text');
    await page.click('#contact-submit-btn');

    // Check error banner is visible
    await expect(page.locator('#contact-validation-error')).toBeVisible();

    // Confirm typed text is retained
    await expect(page.locator('#contact-full-name')).toHaveValue('Test User');
    await expect(page.locator('#contact-email-addr')).toHaveValue('invalid-email-format');
    await expect(page.locator('#contact-message-text')).toHaveValue('Short text');

    // Test Successful Submission: Immediate success message without redirection
    await page.fill('#contact-email-addr', 'testuser@example.com');
    await page.fill('#contact-message-text', 'Hello, this is a valid test message with sufficient length.');
    await page.click('#contact-submit-btn');

    // Check immediate success alert without redirect
    await expect(page.locator('#contact-success-alert')).toBeVisible();
    await expect(page.locator('#success-message-title')).toContainText('Transmission Complete!');
    expect(page.url()).toBe('http://localhost:4173/');

    // Desktop Screenshot
    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await page.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await page.evaluate(() => {
      const elementsToTrack = [
        'telemetry-hub-root',
        'hero-section',
        'nav-tabs',
        'contact-terminal-root',
        'core-identity-card',
        'contact-form-container',
        'contact-form-heading',
        'contact-success-alert',
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

    if (await mobilePage.locator('#cmp-accept-btn').isVisible()) {
      await mobilePage.click('#cmp-accept-btn');
    }

    await mobilePage.click('#tab-btn-contact');
    await mobilePage.waitForSelector('#contact-terminal-root');

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
