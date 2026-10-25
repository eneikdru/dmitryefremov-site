import { test, expect } from '@playwright/test';
import { setupE2EMocks } from './src/lib/test-utils';

test.describe('Podcast Media Hub Audio Playback Lifecycle & Keyboard Operability', () => {
  test('seeded podcast catalog is accessible and controls are fully keyboard operable', async ({ page }) => {
    await setupE2EMocks(page);
    // Inject HTMLMediaElement mock to allow synthetic audio playback in headless Chromium

    // 1. Navigate to main page (Podcast Media Hub tab is active by default)
    await page.goto('/');
    if (await page.locator('#cmp-accept-btn').isVisible()) { await page.click('#cmp-accept-btn'); }

    // Ensure Podcast player container is rendered
    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title').first()).toContainText('Выпуск 05. Концепция расширенного разума');

    // 2. Test Play / Pause button via mouse click and keyboard Focus & Space
    const playBtn = page.locator('#play-pause-btn');
    await expect(playBtn).toBeVisible();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    // Click play button to start playback
    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    // Click play button again to pause playback
    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    // Focus play button and press Space to toggle
    await playBtn.focus();
    await page.keyboard.press('Space');
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    // Press Space again to Pause
    await page.keyboard.press('Space');
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    // 3. Test Waveform visualizer slider keyboard operability (ArrowRight / ArrowLeft / Space)
    const waveform = page.locator('#waveform-container');
    await expect(waveform).toBeVisible();

    await waveform.focus();
    // Press ArrowRight to seek forward (+5 seconds)
    await page.keyboard.press('ArrowRight');
    await expect(page.locator('#current-time-val')).toHaveText('00:05');

    // Press ArrowRight again (+5 seconds -> 10s)
    await page.keyboard.press('ArrowRight');
    await expect(page.locator('#current-time-val')).toHaveText('00:10');

    // Press ArrowLeft to seek backward (-5 seconds -> 5s)
    await page.keyboard.press('ArrowLeft');
    await expect(page.locator('#current-time-val')).toHaveText('00:05');

    // 4. Test Timecode / topic navigation selection via mouse click and keyboard
    const secondTimecode = page.locator('#timecode-btn-102');
    await expect(secondTimecode).toBeVisible();

    // Click topic timecode button directly
    await secondTimecode.click();

    // Timecode 102 offset is 340 seconds = 05:40
    await expect(page.locator('#current-time-val')).toHaveText('05:40');
    await expect(secondTimecode).toContainText('СЕЙЧАС ИГРАЕТ');

    // Click third topic timecode button
    const thirdTimecode = page.locator('#timecode-btn-103');
    await expect(thirdTimecode).toBeVisible();

    await thirdTimecode.focus();
    await page.keyboard.press('Enter');

    // Timecode 103 offset is 920 seconds = 15:20
    await expect(page.locator('#current-time-val')).toHaveText('15:20');
    await expect(thirdTimecode).toContainText('СЕЙЧАС ИГРАЕТ');

    // 5. Test switching episodes in catalog via keyboard focus & Space
    const ep2Card = page.locator('#episode-card-2');
    await expect(ep2Card).toBeVisible();

    await ep2Card.focus();
    await page.keyboard.press('Enter');

    // Verify active episode switched to Episode 2 (Пол Таггард)
    await expect(page.locator('#podcast-title')).toContainText('Выпуск 06. Ментальные модели и когнитивные когеренции');
    await expect(ep2Card).toContainText('АКТИВЕН');
  });
});
