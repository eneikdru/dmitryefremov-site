import { test, chromium, expect } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts and image performance audit for responsive picture implementation', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-b19c1672-8bf2-423c-b267-6114dabe3779');
  const generalTargetDir = path.resolve('../.eneik/records/design-check');
  fs.mkdirSync(targetDir, { recursive: true });
  fs.mkdirSync(generalTargetDir, { recursive: true });

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

    // 1440px Desktop Screenshot and Audit
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#app-root');

    // Click on [PODCAST_MEDIA] tab
    await desktopPage.click('#tab-btn-podcast');
    await desktopPage.waitForTimeout(300);

    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });
    fs.copyFileSync(desktopScreenshotPath, path.join(generalTargetDir, 'desktop-1440.png'));

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
        'podcast-player-container',
        'podcast-episodes-section',
        'podcast-player-section',
        'podcast-player-card',
        'podcast-header',
        'podcast-cover-container',
        'podcast-cover-img',
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

    const layoutCheckPath = path.join(targetDir, 'layout-check.json');
    fs.writeFileSync(layoutCheckPath, JSON.stringify(layoutBoundingBoxes, null, 2));
    fs.copyFileSync(layoutCheckPath, path.join(generalTargetDir, 'layout-check.json'));

    // Desktop Image Audit (CLS, LCP, image loading verification)
    const desktopAudit = await desktopPage.evaluate(() => {
      const imgElements = Array.from(document.querySelectorAll('img'));
      const images = imgElements.map(img => {
        const rect = img.getBoundingClientRect();
        return {
          id: img.id,
          currentSrc: img.currentSrc || img.src,
          naturalWidth: img.naturalWidth,
          naturalHeight: img.naturalHeight,
          displayWidth: rect.width,
          displayHeight: rect.height,
          hasLoaded: img.complete && img.naturalWidth > 0
        };
      });

      let clsValue = 0;
      const entries = performance.getEntriesByType('layout-shift') as any[];
      entries.forEach(entry => {
        if (!entry.hadRecentInput) {
          clsValue += entry.value;
        }
      });

      return { clsValue, images };
    });

    expect(desktopAudit.clsValue).toBeLessThan(0.05);
    for (const img of desktopAudit.images) {
      expect(img.hasLoaded).toBe(true);
      expect(img.currentSrc).toMatch(/\.(webp|avif)/i);
    }

    await desktopContext.close();

    // 375px Mobile Screenshot and Audit
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#app-root');

    await mobilePage.click('#tab-btn-podcast');
    await mobilePage.waitForTimeout(300);

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });
    fs.copyFileSync(mobileScreenshotPath, path.join(generalTargetDir, 'mobile-375.png'));

    // Mobile Image Sizing Audit
    const mobileAudit = await mobilePage.evaluate(() => {
      const imgElements = Array.from(document.querySelectorAll('img'));
      return imgElements.map(img => {
        const rect = img.getBoundingClientRect();
        return {
          id: img.id,
          currentSrc: img.currentSrc || img.src,
          naturalWidth: img.naturalWidth,
          displayWidth: rect.width,
          hasLoaded: img.complete && img.naturalWidth > 0,
          servedVariantWidth: img.currentSrc ? parseInt((img.currentSrc.match(/_(\d+)w\./) || [])[1] || '0', 10) : 0
        };
      });
    });

    for (const img of mobileAudit) {
      expect(img.hasLoaded).toBe(true);
      if (img.servedVariantWidth > 0) {
        expect(img.servedVariantWidth).toBeLessThanOrEqual(768);
      }
    }

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
