import { Page } from '@playwright/test';

export async function setupE2EMocks(page: Page) {
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
          durationSeconds: 1200,
          timecodes: [
            { id: 102, timeOffsetSeconds: 340, title: 'Topic 2' },
            { id: 103, timeOffsetSeconds: 920, title: 'Topic 3' }
          ]
        },
        {
          id: 2,
          slug: 'paul-thagard-ep6',
          philosopher: 'Пол Таггард',
          title: 'Пол Таггард',
          durationSeconds: 1200,
          timecodes: [
            { id: 102, timeOffsetSeconds: 340, title: 'Topic 2' }
          ]
        },
        {
          id: 3,
          slug: 'susan-hurley-ep7',
          philosopher: 'Сюзан Хёрли',
          title: 'Сюзан Хёрли',
          durationSeconds: 1200,
          timecodes: []
        }
      ])
    });
  });

  await page.route('**/api/v1/essays', async route => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([
        {
          id: 'high-end-editorial-minimalism',
          slug: 'high-end-editorial-minimalism',
          title: 'Эстетика инженерного молчания и редакционный минимализм',
          topic: 'systems',
          summary: 'Summary',
          content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\n> Настоящая архитектура не заявляет о себе\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"'
        },
        {
          id: 'deontic-process-autonomy',
          slug: 'deontic-process-autonomy',
          title: 'Деонтическая логика и нормативные инварианты',
          topic: 'deontic',
          summary: 'Summary',
          content: 'Content'
        },
        {
          id: 'deterministic-state',
          slug: 'deterministic-state',
          title: 'Deterministic State Determinism in Autonomous Loops',
          topic: 'autonomous',
          summary: 'Summary',
          content: 'Replayability Condition'
        },
        {
          id: 'deontic-logic-autonomous-process',
          slug: 'deontic-logic-autonomous-process',
          title: 'Deontic Logic in Autonomous Process Architecture',
          topic: 'deontic',
          summary: 'Summary',
          content: 'Content'
        }
      ])
    });
  });

  await page.route('**/api/v1/sparks', async route => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([
        {
          id: 'spk-1',
          content: 'Telegram spark content',
          domain: 'Systems',
          tags: '#systems'
        }
      ])
    });
  });
}
