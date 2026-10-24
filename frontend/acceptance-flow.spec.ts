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
  }
];

const MOCK_ESSAYS = [
  {
    id: 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
    slug: 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
    title: 'Сайт как внешняя рабочая память: от плоских заметок к графу понятий',
    topic: 'systems',
    readTime: '7 min read',
    publishedAt: '2026-10-22',
    summary: 'Как превратить персональный сайт в динамический граф знаний и продолжение естественного мышления.',
    content: '# Сайт как внешняя рабочая память: от плоских заметок к графу понятий\n\nТрадиционные персональные сайты представляют собой статичные витрины. В концепции расширенного разума site становится внешней рабочей памятью.'
  },
  {
    id: 'deonticheskie-invarianty-v-agentnyh-systemah',
    slug: 'deonticheskie-invarianty-v-agentnyh-systemah',
    title: 'Деонтические инварианты и безопасность автономных агентов',
    topic: 'deontic',
    readTime: '10 min read',
    publishedAt: '2026-10-23',
    summary: 'Применение модальной логики и деонтических операторов в проектировании автономных систем.',
    content: '# Деонтические инварианты и безопасность автономных агентов\n\nПри проектировании ИИ-агентов с высокой степенью автономии критически важно гарантировать соблюдение деонтических ограничений.'
  }
];

const MOCK_SPARKS = [
  {
    id: 8001,
    telegramMessageId: 8001,
    body: 'Архитектура расширенного разума начинается с отказа от незафиксированных мыслей. Каждая искра должна становиться узлом графа.',
    domain: 'PHILOSOPHY_OF_MIND',
    tag: '#extendedmind #canon #architecture',
    author: 'Дмитрий Ефремов',
    publishedAt: '2026-10-24 08:00'
  }
];

const MOCK_CANON = [
  {
    id: 1,
    slug: 'rasshirennyy-razum-i-vneshnyaya-pamyat',
    title: 'Концепция расширенного разума и внешней рабочей памяти',
    type: 'MANIFESTO',
    domain: 'PHILOSOPHY_OF_MIND',
    summary: 'Использование цифровой инфраструктуры как непосредственного продолжения человеческой когнитивной системы по Энди Кларку.',
    content: 'Цифровая среда, персональная база знаний и графовые связи являются не просто внешними хранилищами, а полноценными элементами расширенной когнитивной архитектуры.'
  }
];

async function dismissCmpModal(page: any) {
  try {
    const cmpBtn = page.locator('#cmp-accept-btn');
    if (await cmpBtn.isVisible({ timeout: 1500 })) {
      await cmpBtn.click();
    }
  } catch {
    // Modal not present or already dismissed
  }
}

test.describe('Acceptance Flow QA Verification - End-to-End User Journeys', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('/api/v1/podcasts', route => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(MOCK_PODCASTS) }));
    await page.route('/api/v1/essays', route => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(MOCK_ESSAYS) }));
    await page.route('/api/v1/sparks', route => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(MOCK_SPARKS) }));
    await page.route('/api/v1/canon', route => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(MOCK_CANON) }));
  });

  test('podcast player loads canonical seeded episodes and provides interactive audio playback controls', async ({ page }) => {
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

    await dismissCmpModal(page);

    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title')).toContainText('Выпуск 05');

    const catalog = page.locator('#podcast-episodes-section');
    await expect(catalog.locator('#episode-card-101')).toContainText('Дмитрий Ефремов');

    const playBtn = page.locator('#play-pause-btn');
    await expect(playBtn).toBeVisible();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    const timecodeBtn = page.locator('#timecode-btn-502');
    await expect(timecodeBtn).toBeVisible();
    await timecodeBtn.click();

    await expect(page.locator('#current-time-val')).toHaveText('15:00');
    await expect(timecodeBtn).toContainText('СЕЙЧАС ИГРАЕТ');
  });

  test('seeded essays render according to Swiss Editorial aesthetic with clean typography and topic navigation', async ({ page }) => {
    await page.goto('/');

    await dismissCmpModal(page);

    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');

    await expect(page.locator('text=Сайт как внешняя рабочая память').first()).toBeVisible();
    await expect(page.locator('text=Деонтические инварианты и безопасность').first()).toBeVisible();

    const articleHeader = page.locator('#article-header');
    await expect(articleHeader.locator('h1')).toHaveText('Сайт как внешняя рабочая память: от плоских заметок к графу понятий');

    await page.click('button:has-text("Deontic Logic")');
    await expect(page.locator('text=AVAILABLE_LONGREADS (1)')).toBeVisible();

    await page.click('text=Деонтические инварианты и безопасность автономных агентов');
    await expect(articleHeader.locator('h1')).toHaveText('Деонтические инварианты и безопасность автономных агентов');
  });

  test('confidential client intake gateway and telegram sparks stream are accessible', async ({ page }) => {
    await page.goto('/');

    await dismissCmpModal(page);

    await page.click('#tab-btn-article');
    await page.waitForSelector('#sparks-stream-section');
    await expect(page.locator('text=[TELEGRAM_SPARKS_STREAM]')).toBeVisible();

    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');
    await expect(page.locator('#contact-terminal-root')).toBeVisible();
  });

  test('guarantees strict raw teleprompter script isolation across live site', async ({ page }) => {
    await page.goto('/');

    const bodyText = await page.innerText('body');
    expect(bodyText).not.toContain('/mnt/HC_Volume_106932004/podcast/episodes');
  });
});
