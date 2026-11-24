import russianContent from '../content/smart-companies-foolish-speeches.ru.md?raw';
import englishContent from '../content/smart-companies-foolish-speeches.en.md?raw';

export const SMART_COMPANIES_ESSAY_ID = 'smart-companies-foolish-speeches';
export const SMART_COMPANIES_EN_PATH = '/essays/smart-companies-foolish-speeches';
export const SMART_COMPANIES_RU_PATH = '/ru/essays/umnye-kompanii-glupye-rechi';

export const SMART_COMPANIES_ESSAY = {
  id: SMART_COMPANIES_ESSAY_ID,
  slug: SMART_COMPANIES_ESSAY_ID,
  title: 'Умные компании, глупые речи',
  topic: 'systems' as const,
  readTime: '14 min read',
  publishedAt: '2026-10-31',
  summary: 'Почему технологические системы часто мыслят точнее людей, назначенных их символами: Альтман, Маск, Хуанг и Гейтс через Деннета и Сьюзен Хёрли.',
  contentMarkdown: russianContent,
  imageUrl: 'https://dmitryefremov.com/assets/images/smart-companies/cover-ru.png'
};

export const SMART_COMPANIES_ESSAY_EN = {
  title: 'Smart Companies, Foolish Speeches',
  summary: 'Why technology systems often think better than the people chosen to symbolize them: Altman, Musk, Huang and Gates through Dennett and Susan Hurley.',
  contentMarkdown: englishContent,
  imageUrl: 'https://dmitryefremov.com/assets/images/smart-companies/cover-en.png'
};
