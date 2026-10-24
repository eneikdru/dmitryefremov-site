import { test, expect } from '@playwright/test';
import { spawn, ChildProcess } from 'child_process';
import * as path from 'path';

test.describe('Acceptance Flow QA Verification - End-to-End User Journeys', () => {
  let previewProcess: ChildProcess;

  test.beforeAll(async () => {
    // 1. Build frontend distribution
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

    // 2. Start local preview server on port 4173
    previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
      cwd: path.resolve('.'),
      stdio: 'ignore'
    });

    // 3. Active polling to ensure preview server is listening
    let serverReady = false;
    for (let i = 0; i < 30; i++) {
      try {
        const response = await fetch('http://localhost:4173');
        if (response.ok || response.status === 200) {
          serverReady = true;
          break;
        }
      } catch {
        // Wait for server boot
      }
      await new Promise(res => setTimeout(res, 200));
    }

    if (!serverReady) {
      throw new Error('Preview server failed to start on port 4173');
    }
  });

  test.afterAll(() => {
    if (previewProcess) {
      previewProcess.kill();
    }
  });

  test.beforeEach(async ({ page }) => {
    await page.route('**/api/v1/podcasts', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 1,
            slug: 'ruth-barcan-marcus',
            philosopher: 'Рут Баркан Маркус',
            title: 'Рут Баркан Маркус — Формула Баркан и деонтическая модальная логика',
            thesis: 'Инвариантность квантификации в модальных контекстах.',
            description: 'Разбор модальной логики и формулы Баркан.',
            audioUrl: '/audio/ep1.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 1200,
            publishedAt: '2026-10-18',
            timecodes: [
              { id: 101, timeOffsetSeconds: 0, title: 'Введение в деонтическую логику' },
              { id: 102, timeOffsetSeconds: 340, title: 'Формула Баркан и квантификация' },
              { id: 103, timeOffsetSeconds: 920, title: 'Модальные инварианты' }
            ]
          },
          {
            id: 2,
            slug: 'paul-thagard',
            philosopher: 'Пол Таггард',
            title: 'Пол Таггард — Когнитивная когерентность',
            thesis: 'Вычислительная теория сознания и когерентность гипотез.',
            description: 'Разбор когнитивных архитектур.',
            audioUrl: '/audio/ep2.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 1500,
            publishedAt: '2026-10-19',
            timecodes: [
              { id: 201, timeOffsetSeconds: 0, title: 'Когерентность гипотез' }
            ]
          },
          {
            id: 3,
            slug: 'susan-hurley',
            philosopher: 'Сюзан Хёрли',
            title: 'Сюзан Хёрли — Сознание в действии',
            thesis: 'Динамические петли восприятия и действия.',
            description: 'Теория действия и восприятия.',
            audioUrl: '/audio/ep3.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 1800,
            publishedAt: '2026-10-20',
            timecodes: [
              { id: 301, timeOffsetSeconds: 0, title: 'Восприятие и действие' }
            ]
          }
        ])
      });
    });

    const mockEssays = [
      {
        id: '1',
        slug: 'estetika-inzhenernogo-molchaniya',
        title: 'Эстетика инженерного молчания и редакционный минимализм',
        summary: 'Минимализм в архитектуре интерфейсов.',
        content: '## Введение\n\n> Настоящая архитектура не заявляет о себе',
        authorName: 'Дмитрий Ефремов',
        topic: 'deontic',
        readTime: '5 мин',
        publishedAt: '2026-10-18'
      },
      {
        id: '2',
        slug: 'deontic-logic-in-autonomous-process-architecture',
        title: 'Deontic Logic in Autonomous Process Architecture',
        summary: 'Deontic logic invariants and autonomous loops.',
        content: '# Deontic Logic\n\nFormalization of duties and permissions.',
        authorName: 'Дмитрий Ефремов',
        topic: 'deontic',
        readTime: '8 мин',
        publishedAt: '2026-10-19'
      }
    ];

    await page.route('**/api/v1/essays', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mockEssays)
      });
    });

    await page.route('**/api/v1/articles', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mockEssays)
      });
    });

    await page.route('**/api/v1/sparks', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 's-1',
            telegramMessageId: 101,
            body: 'Telegram spark message.',
            domain: 'General',
            publishedAt: '2026-10-20'
          }
        ])
      });
    });
  });

  test('podcast player loads canonical seeded episodes and provides interactive audio playback controls', async ({ page }) => {
    // Inject mock HTMLMediaElement behavior for synthetic audio playback in headless browser
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

    await page.goto('http://localhost:4173');

    // Dismiss CMP privacy consent modal if visible
    if (await page.locator('#cmp-decline-btn').isVisible()) {
      await page.click('#cmp-decline-btn');
    } else if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. Ensure Podcast Media Hub tab is active by default and player is loaded
    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title')).toContainText('Рут Баркан Маркус');

    // 2. Verify canonical episodes in catalog list (Ep 01 Ruth Barcan Marcus, Ep 02 Paul Thagard, Ep 03 Susan Hurley)
    const catalog = page.locator('#podcast-episodes-section');
    await expect(catalog.locator('#episode-card-1')).toContainText('Рут Баркан Маркус');
    await expect(catalog.locator('#episode-card-2')).toContainText('Пол Таггард');
    await expect(catalog.locator('#episode-card-3')).toContainText('Сюзан Хёрли');

    // 3. Test Play/Pause audio control
    const playBtn = page.locator('#play-pause-btn');
    await expect(playBtn).toBeVisible();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    // 4. Test seeking via timecode selection
    const timecodeBtn = page.locator('#timecode-btn-102');
    await expect(timecodeBtn).toBeVisible();
    await timecodeBtn.click();

    // Timecode 102 corresponds to 340s = "05:40"
    await expect(page.locator('#current-time-val')).toHaveText('05:40');
    await expect(timecodeBtn).toContainText('СЕЙЧАС ИГРАЕТ');

    // 5. Test switching to Episode 02 (Пол Таггард)
    const ep2Card = page.locator('#episode-card-2');
    await ep2Card.click();

    await expect(page.locator('#podcast-title')).toContainText('Пол Таггард');
    await expect(ep2Card).toContainText('АКТИВЕН');
  });

  test('seeded essays render according to Swiss Editorial aesthetic with clean typography and topic navigation', async ({ page }) => {
    await page.goto('http://localhost:4173');

    if (await page.locator('#cmp-decline-btn').isVisible()) {
      await page.click('#cmp-decline-btn');
    } else if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // Switch to Articles & Thought Stream tab
    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');

    // 1. Verify longread articles list is rendered
    await expect(page.locator('text=Эстетика инженерного молчания и редакционный минимализм').first()).toBeVisible();
    await expect(page.locator('text=Deontic Logic in Autonomous Process Architecture').first()).toBeVisible();

    // 2. Verify active article reader header and Swiss Editorial typography elements
    const articleHeader = page.locator('#article-header');
    await expect(articleHeader.locator('h1')).toHaveText('Эстетика инженерного молчания и редакционный минимализм');

    const articleScroll = page.locator('#article-scroll-container');
    await expect(articleScroll.locator('h2').first()).toHaveText('Введение');
    await expect(articleScroll).toContainText('Настоящая архитектура не заявляет о себе');

    // 3. Test topic filtering (Deontic Logic)
    await page.click('button:has-text("Deontic Logic")');
    await expect(page.locator('text=AVAILABLE_LONGREADS (2)')).toBeVisible();

    // 4. Verify selecting another longread
    await page.click('text=Deontic Logic in Autonomous Process Architecture');
    await expect(articleHeader.locator('h1')).toHaveText('Deontic Logic in Autonomous Process Architecture');
  });

  test('confidential client intake gateway and telegram sparks stream are accessible', async ({ page }) => {
    await page.goto('http://localhost:4173');

    if (await page.locator('#cmp-decline-btn').isVisible()) {
      await page.click('#cmp-decline-btn');
    } else if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. Check Telegram Sparks Stream on Articles tab
    await page.click('#tab-btn-article');
    await page.waitForSelector('#sparks-stream-section');
    await expect(page.locator('text=[TELEGRAM_SPARKS_STREAM]')).toBeVisible();

    // 2. Switch to Contact / Gateway tab
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');
    await expect(page.locator('#contact-terminal-root')).toBeVisible();
  });

  test('guarantees strict raw teleprompter script isolation across live site', async ({ page }) => {
    await page.goto('http://localhost:4173');

    // Check DOM content for raw internal teleprompter directory paths
    const bodyText = await page.innerText('body');
    expect(bodyText).not.toContain('/mnt/HC_Volume_106932004/podcast/episodes');
  });
});
