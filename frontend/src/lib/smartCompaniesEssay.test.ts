import { describe, expect, it } from 'vitest';
import {
  SMART_COMPANIES_EN_PATH,
  SMART_COMPANIES_ESSAY,
  SMART_COMPANIES_ESSAY_EN,
  SMART_COMPANIES_ESSAY_ID,
  SMART_COMPANIES_RU_PATH
} from './smart_companies_essay';

describe('smart companies essay', () => {
  it('keeps stable language-specific routes and identity', () => {
    expect(SMART_COMPANIES_ESSAY.id).toBe(SMART_COMPANIES_ESSAY_ID);
    expect(SMART_COMPANIES_EN_PATH).toBe('/essays/smart-companies-foolish-speeches');
    expect(SMART_COMPANIES_RU_PATH).toBe('/ru/essays/umnye-kompanii-glupye-rechi');
  });

  it('ships complete independent Russian and English editions', () => {
    expect(SMART_COMPANIES_ESSAY.title).toBe('Умные компании, глупые речи');
    expect(SMART_COMPANIES_ESSAY.contentMarkdown).toContain('## Эпилог');
    expect(SMART_COMPANIES_ESSAY.contentMarkdown).toContain('Сьюзен Хёрли');
    expect(SMART_COMPANIES_ESSAY.imageUrl).toEndWith('/cover-ru.png');

    expect(SMART_COMPANIES_ESSAY_EN.title).toBe('Smart Companies, Foolish Speeches');
    expect(SMART_COMPANIES_ESSAY_EN.contentMarkdown).toContain('## Epilogue');
    expect(SMART_COMPANIES_ESSAY_EN.contentMarkdown).toContain('Susan Hurley');
    expect(SMART_COMPANIES_ESSAY_EN.imageUrl).toEndWith('/cover-en.png');
  });
});
