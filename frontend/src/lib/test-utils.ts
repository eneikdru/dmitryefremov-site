import { Page } from '@playwright/test';

export async function setupE2EMocks(page: Page) {
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

  await page.route('**/api/v1/podcasts', async route => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([
        {
          id: 1,
          slug: 'rasshirennyy-razum-ep5',
          philosopher: 'Рут Баркан Маркус',
          title: 'Выпуск 05. Концепция расширенного разума и внешней памяти',
          thesis: 'Использование цифровой инфраструктуры как продолжения когнитивной системы.',
          description: 'Разбор взаимодействия мышления автора с цифровой рабочей памятью.',
          audioUrl: '/audio/ep5.mp3',
          youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
          telegramUrl: 'https://t.me/efremov_mvp',
          durationSeconds: 1200,
          publishedAt: '2026-10-18',
          timecodes: [
            { id: 1011, timeOffsetSeconds: 0, title: 'Введение в расширенный разум' },
            { id: 102, timeOffsetSeconds: 340, title: 'Графовая связность и Канон' },
            { id: 103, timeOffsetSeconds: 920, title: 'Автономный вывод и A2A' }
          ]
        },
        {
          id: 2,
          slug: 'paul-thagard-ep6',
          philosopher: 'Пол Таггард',
          title: 'Выпуск 06. Ментальные модели и когнитивные когеренции',
          thesis: 'Вычислительная философия сознания и проверка когерентности гипотез.',
          description: 'Моделирование когнитивных процессов и связности взглядов.',
          audioUrl: '/audio/ep6.mp3',
          youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
          telegramUrl: 'https://t.me/efremov_mvp',
          durationSeconds: 1500,
          publishedAt: '2026-10-19',
          timecodes: [
            { id: 1021, timeOffsetSeconds: 0, title: 'Когнитивная когерентность' }
          ]
        },
        {
          id: 3,
          slug: 'susan-hurley-ep7',
          philosopher: 'Сюзан Хёрли',
          title: 'Выпуск 07. Сознание и действие',
          thesis: 'Взаимосвязь восприятия и действия.',
          description: 'Взаимосвязь восприятия и действия.',
          audioUrl: '/audio/ep7.mp3',
          youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
          telegramUrl: 'https://t.me/efremov_mvp',
          durationSeconds: 1800,
          publishedAt: '2026-10-20',
          timecodes: [
            { id: 1031, timeOffsetSeconds: 0, title: 'Сознание и действие' }
          ]
        }
      ])
    });
  });

  const mockEssays = [
    {
      id: '1',
      slug: 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
      title: 'Эстетика инженерного молчания и редакционный минимализм',
      summary: 'Сшивка расширенного разума и публичного портала.',
      content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\n> Настоящая архитектура не заявляет о себе',
      authorName: 'Дмитрий Ефремов',
      topic: 'systems',
      publishedAt: '2026-10-18T10:00:00Z'
    },
    {
      id: '2',
      slug: 'deonticheskaya-avtonomiya-agentov',
      title: 'Деонтическая логика и нормативные инварианты',
      summary: 'Формализация обязательств, разрешений и запретов.',
      content: '# Деонтическая автономия ИИ-агентов\n\nНормативные инварианты и модальная логика.',
      authorName: 'Дмитрий Ефремов',
      topic: 'deontic',
      publishedAt: '2026-10-19T12:00:00Z'
    },
    {
      id: '3',
      slug: 'deterministic-state',
      title: 'Deterministic State Determinism in Autonomous Loops',
      summary: 'Формализация обязательств, разрешений и запретов.',
      content: '# Deterministic State Determinism in Autonomous Loops\n\n## Replayability Condition',
      authorName: 'Дмитрий Ефремов',
      topic: 'deontic',
      publishedAt: '2026-10-19T12:00:00Z'
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
          id: 's-101',
          telegramMessageId: 8001,
          author: 'Дмитрий Ефремов',
          content: 'Архитектура расширенного разума: короткая заметка из Telegram.',
          category: 'ARCHITECTURE',
          publishedAt: '2026-10-20T14:00:00Z'
        }
      ])
    });
  });

  await page.route('**/api/v1/canon', async route => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([
        {
          id: 'concept-deontic-logic',
          title: 'Деонтическая логика в автономии',
          type: 'concept',
          category: 'Канон · Логика',
          description: 'Формализация нормативных ограничений и модальных операторов должествования для автономных агентов.'
        }
      ])
    });
  });
}
