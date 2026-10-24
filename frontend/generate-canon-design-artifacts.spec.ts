import { test, chromium, expect } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design gate verification artifacts for Canon UI', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-370a2e92-85b4-4daf-9b74-0dbfa0c15c23');
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

  // Start preview server on port 4173
  const previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
    cwd: path.resolve('.'),
    stdio: 'ignore'
  });

  // Wait for server to start
  await new Promise((resolve) => setTimeout(resolve, 2500));

  try {
    const browser = await chromium.launch();

    // 1440px Desktop Screenshot and Layout Check
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Click on [📜 CANON (TIER 1)] tab
    await desktopPage.click('#tab-btn-canon');
    await desktopPage.waitForTimeout(500);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      const elementsToTrack = [
        'telemetry-hub-root',
        'hero-section',
        'nav-tabs',
        'tab-btn-canon',
        'canon-view-root',
        'canon-filter-domain-all',
        'canon-filter-domain-auto-eng',
        'canon-filter-domain-agentic-econ',
        'canon-filter-domain-biz-strat',
        'canon-filter-domain-phil-mind',
        'canon-filter-type-all',
        'canon-filter-type-manifesto',
        'canon-filter-type-glossary',
        'canon-filter-type-ontology',
        'canon-navigation-list',
        'canon-item-architecture-of-autonomous-systems',
        'canon-reader-main',
        'canon-reader-article',
        'canon-content-body',
        'canon-cross-references',
        'main-footer'
      ];

      return elementsToTrack.map((id) => {
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

    await mobilePage.click('#tab-btn-canon');
    await mobilePage.waitForTimeout(500);

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
