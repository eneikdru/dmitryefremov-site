import { test, expect } from '@playwright/test';

test.describe('Podcast Media Hub Audio Playback Lifecycle & Keyboard Operability', () => {
  test('seeded podcast catalog is accessible and controls are fully keyboard operable', async ({ page }) => {
    // Inject HTMLMediaElement mock to allow synthetic audio playback in headless Chromium
    await page.addInitScript(() => {
      Object.defineProperty(window.HTMLMediaElement.prototype, 'paused', {
        get(this: any) {
          return this._paused !== undefined ? this._paused : true;
        },
        configurable: true
      });
      window.HTMLMediaElement.prototype.play = function (this: any) {
        this._paused = false;
        return Promise.resolve();
      };
      window.HTMLMediaElement.prototype.pause = function (this: any) {
        this._paused = true;
      };
    });

    // 1. Navigate to main page (Podcast Media Hub tab is active by default)
    await page.route('**/api/v1/podcasts', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: 101, slug: 'rasshirennyy-razum-ep5', philosopher: 'Рут Баркан Маркус', title: 'Рут Баркан Маркус', durationSeconds: 1200, timecodes: [ { id: 1011, timeOffsetSeconds: 0, title: 'Введение' }, { id: 102, timeOffsetSeconds: 340, title: 'Topic 2' }, { id: 103, timeOffsetSeconds: 920, title: 'Topic 3' } ] }, { id: 102, slug: 'paul-thagard-ep6', philosopher: 'Пол Таггард', title: 'Пол Таггард', durationSeconds: 1200, timecodes: [ { id: 1021, timeOffsetSeconds: 0, title: 'Введение' }, { id: 1022, timeOffsetSeconds: 340, title: 'Topic 2' } ] }, { id: 103, slug: 'susan-hurley-ep7', philosopher: 'Сюзан Хёрли', title: 'Сюзан Хёрли', durationSeconds: 1200, timecodes: [] }]) }); });    await page.route('**/api/v1/essays', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: '1', slug: 'high-end-editorial-minimalism', title: 'Эстетика инженерного молчания и редакционный минимализм', topic: 'systems', summary: 'Summary', content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"' }, { id: '2', slug: 'deontic-process-autonomy', title: 'Деонтическая логика и нормативные инварианты', topic: 'deontic', summary: 'Summary', content: 'Content' }, { id: '3', slug: 'deterministic-state', title: 'Deterministic State Determinism in Autonomous Loops', topic: 'autonomous', summary: 'Summary', content: 'Replayability Condition' }, { id: '4', slug: 'deontic-logic-autonomous-process', title: 'Deontic Logic in Autonomous Process Architecture', topic: 'deontic', summary: 'Summary', content: 'Content' }]) }); });    await page.route('**/api/v1/articles', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: '1', slug: 'high-end-editorial-minimalism', title: 'Эстетика инженерного молчания и редакционный минимализм', topic: 'systems', summary: 'Summary', content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"' }, { id: '2', slug: 'deontic-process-autonomy', title: 'Деонтическая логика и нормативные инварианты', topic: 'deontic', summary: 'Summary', content: 'Content' }, { id: '3', slug: 'deterministic-state', title: 'Deterministic State Determinism in Autonomous Loops', topic: 'autonomous', summary: 'Summary', content: 'Replayability Condition' }, { id: '4', slug: 'deontic-logic-autonomous-process', title: 'Deontic Logic in Autonomous Process Architecture', topic: 'deontic', summary: 'Summary', content: 'Content' }]) }); });    await page.route('**/api/v1/sparks', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: 'spk-1', content: 'Telegram spark content', domain: 'Systems', tags: '#systems' }]) }); });
    await page.goto('/');

    // Ensure Podcast player container is rendered
    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title').first()).toContainText('Рут Баркан Маркус');

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
    await expect(page.locator('#podcast-title')).toContainText('Пол Таггард');
    await expect(ep2Card).toContainText('АКТИВЕН');
  });
});
