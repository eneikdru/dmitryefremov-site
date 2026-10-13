import { test, expect } from '@playwright/test';
import { spawn, ChildProcess } from 'child_process';
import * as path from 'path';

test.describe('End-to-End Content Verification & Primary Paths Traversal', () => {
  let previewProcess: ChildProcess;

  test.beforeAll(async () => {
    // Build frontend for production preview
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

    // Start preview server on port 4173
    previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
      cwd: path.resolve('.'),
      stdio: 'ignore'
    });

    // Active health polling to confirm server readiness
    let serverReady = false;
    for (let i = 0; i < 30; i++) {
      try {
        const response = await fetch('http://localhost:4173');
        if (response.ok || response.status === 200) {
          serverReady = true;
          break;
        }
      } catch {
        // Server initializing...
      }
      await new Promise(res => setTimeout(res, 200));
    }
    if (!serverReady) {
      throw new Error('Vite preview server failed to start on http://localhost:4173');
    }
  });

  test.afterAll(() => {
    if (previewProcess) {
      previewProcess.kill();
    }
  });

  test('traverses all primary paths and verifies content renders without placeholder lorem ipsum', async ({ page }) => {
    await page.goto('http://localhost:4173');
    await page.waitForSelector('#telemetry-hub-root');

    // Dismiss CMP modal if present
    const cmpAccept = page.locator('#cmp-accept-btn');
    if (await cmpAccept.isVisible()) {
      await cmpAccept.click();
    }

    // --- 1. Hero & Branding Verification ---
    await expect(page.locator('#hero-title')).toHaveText('Дмитрий Ефремов');
    await expect(page.locator('#hero-subtitle')).toContainText('Системы, логика мышления и автономные процессы');
    await expect(page.locator('#hero-manifest')).toContainText('«Ваш код и ваши решения — это то, кто вы есть на самом деле.»');

    // --- 2. Path: Podcast Hub (default / podcast tab) ---
    await page.click('#tab-btn-podcast');
    await page.waitForSelector('#podcast-episodes-section');

    // Verify catalog renders 9 episodes
    const episodeCards = page.locator('.episode-card');
    await expect(episodeCards).toHaveCount(9);

    // Verify active episode title and timecodes render
    await expect(page.locator('#podcast-title')).toContainText('Рут Баркан Маркус');
    await expect(page.locator('#timecodes-list')).toBeVisible();

    // Verify external podcast links resolve with valid HTTP/HTTPS URLs
    const podcastYtLink = page.locator('#podcast-player-container a[aria-label="YouTube Channel"]');
    await expect(podcastYtLink).toHaveAttribute('href', /^https:\/\/(www\.)?(youtube\.com|youtu\.be)/);
    const podcastTgLink = page.locator('#podcast-player-container a[aria-label="Telegram Channel"]');
    await expect(podcastTgLink).toHaveAttribute('href', /^https:\/\/t\.me/);

    // Assert zero lorem ipsum in Podcast view
    const podcastPageText = await page.locator('#telemetry-hub-root').innerText();
    expect(podcastPageText.toLowerCase()).not.toContain('lorem');
    expect(podcastPageText.toLowerCase()).not.toContain('ipsum');

    // --- 3. Path: Essays & Thought Stream (article tab) ---
    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');

    // Check longreads list and active article reader
    await expect(page.locator('#article-header h1')).toBeVisible();
    await expect(page.locator('#article-scroll-container')).toContainText('Эстетика инженерного молчания');

    // Filter topic tabs
    await page.click('button:has-text("Deontic Logic")');
    await page.waitForTimeout(200);
    const deonticArticles = page.locator('.article-card');
    await expect(deonticArticles).toHaveCount(2);

    // Select second article and check rendering
    await deonticArticles.nth(1).click();
    await page.waitForTimeout(200);
    await expect(page.locator('#article-header h1')).toContainText('Deontic Logic in Autonomous Process Architecture');

    // Reset topic filter to All
    await page.click('button:has-text("All")');

    // Assert zero lorem ipsum in Articles view
    const articlePageText = await page.locator('#telemetry-hub-root').innerText();
    expect(articlePageText.toLowerCase()).not.toContain('lorem');
    expect(articlePageText.toLowerCase()).not.toContain('ipsum');

    // --- 4. Path: Studio & Workspace Gallery (gallery tab) ---
    await page.click('#tab-btn-gallery');
    await page.waitForSelector('#content-view h2');

    await expect(page.locator('#content-view h2')).toContainText('Студийное пространство & Процесс записи');
    const studioCards = page.locator('#content-view .grid > div');
    await expect(studioCards).toHaveCount(6);

    // Verify pictures and images render correctly
    const galleryImages = page.locator('#content-view img');
    const imgCount = await galleryImages.count();
    expect(imgCount).toBeGreaterThanOrEqual(6);

    // Assert zero lorem ipsum in Gallery view
    const galleryPageText = await page.locator('#telemetry-hub-root').innerText();
    expect(galleryPageText.toLowerCase()).not.toContain('lorem');
    expect(galleryPageText.toLowerCase()).not.toContain('ipsum');

    // --- 5. Path: Contacts & Intake (contact tab) ---
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#core-identity-card');

    await expect(page.locator('#identity-name')).toHaveText('Дмитрий Ефремов');
    const contactLinks = page.locator('#identity-links-grid a');
    await expect(contactLinks).toHaveCount(5);

    // Verify all outbound channel links have non-empty, valid URLs
    for (let i = 0; i < 5; i++) {
      const href = await contactLinks.nth(i).getAttribute('href');
      expect(href).not.toBeNull();
      expect(href?.length).toBeGreaterThan(5);
      expect(href).toMatch(/^(https:\/\/|mailto:)/);
    }

    // Assert zero lorem ipsum in Contacts view
    const contactPageText = await page.locator('#telemetry-hub-root').innerText();
    expect(contactPageText.toLowerCase()).not.toContain('lorem');
    expect(contactPageText.toLowerCase()).not.toContain('ipsum');

    // --- 6. Path: Telemetry GQM (gqm tab) ---
    await page.click('#tab-btn-gqm');
    await page.waitForSelector('#gqm-telemetry-root');

    await expect(page.locator('#gqm-telemetry-root h1')).toBeVisible();

    // Assert zero lorem ipsum in Telemetry GQM view
    const gqmPageText = await page.locator('#telemetry-hub-root').innerText();
    expect(gqmPageText.toLowerCase()).not.toContain('lorem');
    expect(gqmPageText.toLowerCase()).not.toContain('ipsum');

    // --- 7. Legal Modals & Footers ---
    await page.click('#imprint-toggle-btn');
    await page.waitForSelector('#imprint-modal');

    await expect(page.locator('#imprint-details-content')).toContainText('Дмитрий Ефремов (Dmitry Efremov)');
    await expect(page.locator('#imprint-details-content')).toContainText('dmitryefremov.com');

    await page.click('#imprint-close-btn');
    await expect(page.locator('#imprint-modal')).toBeHidden();
  });
});
