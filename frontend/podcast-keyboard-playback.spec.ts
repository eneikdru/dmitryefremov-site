import { test, expect } from '@playwright/test';

const MOCK_PODCASTS = [
  {
    id: 101,
    slug: 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura',
    philosopher: 'Дмитрий Ефремов',
    title: 'Выпуск 05: Расширенный разум, граф Канона и агентная экономика',
    thesis: 'Обсуждение того, как персональный сайт превращается во внешнюю память автора и платформу для ИИ-агентов.',
    description: 'В пятом выпуске Дмитрий Ефремов разбирает концепцию Энди Кларка, структуру Канона, связность эссе и подкастов.',
    audioUrl: 'https://dmitryefremov.com/media/podcast/ep05-extended-mind.mp3',
    youtubeUrl: 'https://www.youtube.com/watch?v=efremov_ep05',
    telegramUrl: 'https://t.me/efremov_mvp/1005',
    durationSeconds: 3600,
    publishedAt: '2026-10-23T15:00:00Z',
    timecodes: [
      { id: 501, timeOffsetSeconds: 0, title: 'Введение: Концепция Extended Mind и сайт как память', description: 'Почему сайт должен являться продолжением мышления.' },
      { id: 502, timeOffsetSeconds: 900, title: 'Канон как семантическое ядро системы взглядов', description: 'Связывание понятий с выпусками подкаста.' },
      { id: 503, timeOffsetSeconds: 2100, title: 'Деонтическая автономия и Agent-to-Agent протоколы', description: 'Как ИИ-агенты взаимодействуют с графом знаний.' }
    ]
  },
  {
    id: 102,
    slug: 'vypusk-04-deonticheskaya-avtonomiya',
    philosopher: 'Дмитрий Ефремов',
    title: 'Выпуск 04: Деонтическая автономия ИИ-агентов',
    thesis: 'Формализация обязательств, разрешений и запретов при выполнении автономных действий.',
    description: 'В четвёртом выпуске обсуждаются деонтические операторы O(A), P(A), F(A) в архитектуре.',
    audioUrl: 'https://dmitryefremov.com/media/podcast/ep04-deontic.mp3',
    youtubeUrl: 'https://www.youtube.com/watch?v=efremov_ep04',
    telegramUrl: 'https://t.me/efremov_mvp/1004',
    durationSeconds: 2700,
    publishedAt: '2026-10-20T12:00:00Z',
    timecodes: [
      { id: 401, timeOffsetSeconds: 0, title: 'Введение в деонтическую логику', description: 'Модальные операторы.' }
    ]
  }
];

test.describe('Podcast Media Hub Audio Playback Lifecycle & Keyboard Operability', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('/api/v1/podcasts', route => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(MOCK_PODCASTS) }));
  });

  test('seeded podcast catalog is accessible and controls are fully keyboard operable', async ({ page }) => {
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

    await page.goto('/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title').first()).toContainText('Выпуск 05');

    const playBtn = page.locator('#play-pause-btn');
    await expect(playBtn).toBeVisible();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    await playBtn.focus();
    await page.keyboard.press('Space');
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    await page.keyboard.press('Space');
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    const waveform = page.locator('#waveform-container');
    await expect(waveform).toBeVisible();

    await waveform.focus();
    await page.keyboard.press('ArrowRight');
    await expect(page.locator('#current-time-val')).toHaveText('00:05');

    await page.keyboard.press('ArrowRight');
    await expect(page.locator('#current-time-val')).toHaveText('00:10');

    await page.keyboard.press('ArrowLeft');
    await expect(page.locator('#current-time-val')).toHaveText('00:05');

    const secondTimecode = page.locator('#timecode-btn-502');
    await expect(secondTimecode).toBeVisible();

    await secondTimecode.click();

    await expect(page.locator('#current-time-val')).toHaveText('15:00');
    await expect(secondTimecode).toContainText('СЕЙЧАС ИГРАЕТ');

    const thirdTimecode = page.locator('#timecode-btn-503');
    await expect(thirdTimecode).toBeVisible();

    await thirdTimecode.focus();
    await page.keyboard.press('Enter');

    await expect(page.locator('#current-time-val')).toHaveText('35:00');
    await expect(thirdTimecode).toContainText('СЕЙЧАС ИГРАЕТ');

    const ep2Card = page.locator('#episode-card-102');
    await expect(ep2Card).toBeVisible();

    await ep2Card.focus();
    await page.keyboard.press('Enter');

    await expect(page.locator('#podcast-title')).toContainText('Выпуск 04');
    await expect(ep2Card).toContainText('АКТИВЕН');
  });
});
