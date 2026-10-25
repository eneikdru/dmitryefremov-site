export interface FrameworkStep {
  id: number;
  text: string;
  action?: string;
  isEliminated?: boolean;
}

export interface AudioSnippet {
  duration: string;
  transcript: string;
}

export interface TheoremContrast {
  linear: string;
  nonLinear: string;
}

export type SparkType = 'insight' | 'framework' | 'quote' | 'audio' | 'theorem';

export interface SparkItem {
  id: string;
  author: string;
  authorInitial: string;
  timestamp: string;
  tag: string;
  type: SparkType;
  sparksCount: number;
  readTime?: string;
  body?: string;
  title?: string;
  frameworkSteps?: FrameworkStep[];
  audioSnippet?: AudioSnippet;
  theoremContrast?: TheoremContrast;
  isFired?: boolean;
  isBookmarked?: boolean;
}

export type StreamMode = 'HYPER' | 'FOCUS';

export const SPARK_CATEGORIES = [
  'All Sparks',
  '#mental-models',
  '#system-design',
  '#ai-futures',
  '#philosophy',
  '#product-craft',
  '#heuristics'
] as const;

export const CANONICAL_SPARKS: SparkItem[] = [
  // --- РУТ БАРКАН МАРКУС (5 SPARKS) ---
  {
    id: 'spk-barcan-01',
    author: 'Рут Баркан Маркус',
    authorInitial: 'Р',
    timestamp: '2026-09-25 18:00',
    tag: '#mental-models',
    type: 'theorem',
    sparksCount: 142,
    readTime: '15s',
    title: 'Прямая референция: Имя не равно описанию',
    body: 'Сущность субъекта или сущность системы не сводится к перечню текущих признаков и контекстных ролей.',
    theoremContrast: {
      linear: 'Имя объекта — это просто сумма его текущих признаков, свойств и выполняемых функций.',
      nonLinear: 'Прямая референция: имя жестко привязано к объекту во всех возможных мирах. Если свойства меняются, субъект остаётся собой.'
    },
    isFired: true,
    isBookmarked: false
  },
  {
    id: 'spk-barcan-02',
    author: 'Рут Баркан Маркус',
    authorInitial: 'Р',
    timestamp: '2026-09-26 12:30',
    tag: '#system-design',
    type: 'framework',
    sparksCount: 98,
    readTime: '25s',
    title: 'Деонтическая архитектура контрактов',
    body: 'Протокол защиты системных обязательств от оппортунистического переписывания в рантайме.',
    frameworkSteps: [
      { id: 1, text: 'Фиксация жестких инвариантов контракта на входе без права контекстуального размывания.' },
      { id: 2, text: 'Запрет на ретроспективную ревизию условий при изменении внешней конъюнктуры.' },
      { id: 3, text: 'Вычисление неудаляемого остатка (moral remainder) при возникновении конфликта ветвей исполнения.' },
      { id: 4, text: 'Регистрация системного долга в журнал аудита вместо сокрытия факта компромисса.' }
    ],
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-barcan-03',
    author: 'Рут Баркан Маркус',
    authorInitial: 'Р',
    timestamp: '2026-09-27 09:15',
    tag: '#philosophy',
    type: 'insight',
    sparksCount: 184,
    readTime: '20s',
    title: 'Неизгладимый остаток моральной дилеммы',
    body: 'Моральная дилемма не решается обнулением обязательств. Если две обязанности конфликтуют, выбор одной не отменяет вину и ответственность за неисполнение второй. Зрелая архитектура регистрирует долг, а не делает вид, что его не существовало.',
    isFired: true,
    isBookmarked: true
  },
  {
    id: 'spk-barcan-04',
    author: 'Рут Баркан Маркус',
    authorInitial: 'Р',
    timestamp: '2026-09-28 15:40',
    tag: '#heuristics',
    type: 'quote',
    sparksCount: 215,
    readTime: '10s',
    title: 'Формула Баркан',
    body: '«Если нечто возможно необходимо, то оно необходимо возможно. Возможности не возникают из ниоткуда в середине выполнения — они должны быть заложены в фундаментальное пространство состояний системы.»',
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-barcan-05',
    author: 'Рут Баркан Маркус',
    authorInitial: 'Р',
    timestamp: '2026-09-29 11:00',
    tag: '#product-craft',
    type: 'insight',
    sparksCount: 76,
    readTime: '15s',
    title: 'Жесткая идентичность продукта',
    body: 'Пользователь доверяет не абстрактному набору фич, а несменяемой идентичности решения. Когда продукт начинает хаотично мимикрировать под чужие функции, он теряет десигнатор и растворяется в информационном шуме.',
    isFired: false,
    isBookmarked: false
  },

  // --- ПОЛ ТАГГАРД (5 SPARKS) ---
  {
    id: 'spk-thagard-01',
    author: 'Пол Таггард',
    authorInitial: 'П',
    timestamp: '2026-10-01 18:00',
    tag: '#mental-models',
    type: 'insight',
    sparksCount: 260,
    readTime: '15s',
    title: 'Связность не равна истине',
    body: 'Красивая ложь побеждает разрозненные факты именно потому, что она связна. Любая замкнутая конспирология или корпоративный самообман обладают безупречной внутренней гармонией, если из них методично вырезаны неудобные свидетельства.',
    isFired: true,
    isBookmarked: true
  },
  {
    id: 'spk-thagard-02',
    author: 'Пол Таггард',
    authorInitial: 'П',
    timestamp: '2026-10-02 14:20',
    tag: '#system-design',
    type: 'framework',
    sparksCount: 135,
    readTime: '30s',
    title: 'ECHO: Сеть объяснительной связности',
    body: 'Алгоритм взвешивания конкурирующих гипотез в условиях неполной информации.',
    frameworkSteps: [
      { id: 1, text: 'Симметрия связей: свидетельства подтверждают гипотезу симметрично, без статусного авторитета источника.' },
      { id: 2, text: 'Штраф за сложность: каждое ad-hoc допущение снижает общий вес объяснительной системы.' },
      { id: 3, text: 'Конкурентное подавление: взаимно исключающие гипотезы соединены отрицательными связями.' },
      { id: 4, text: 'Релаксация сети: истинным признается решение, выдерживающее наибольшее число внешних свидетельств.' }
    ],
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-thagard-03',
    author: 'Пол Таггард',
    authorInitial: 'П',
    timestamp: '2026-10-03 10:10',
    tag: '#heuristics',
    type: 'quote',
    sparksCount: 310,
    readTime: '12s',
    title: 'Критерий опровержимости в бизнесе',
    body: '«Главный проверочный вопрос к любой стратегии: "Что конкретно в наблюдаемом мире заставило бы меня признать ошибку?" Если такого факта нет — перед вами секта, а не стратегия.»',
    isFired: true,
    isBookmarked: false
  },
  {
    id: 'spk-thagard-04',
    author: 'Пол Таггард',
    authorInitial: 'П',
    timestamp: '2026-10-04 16:45',
    tag: '#ai-futures',
    type: 'insight',
    sparksCount: 195,
    readTime: '20s',
    title: 'Архитектура AIMS и яд цензуры',
    body: 'AIMS определяет четыре процесса информации: Acquisition, Inference, Mapping, Storage. Попытка ввести цензуру или идеологический фильтр на стадии сбора данных (Acquisition) необратимо разрушает способность системы к логическому выводу (Inference).',
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-thagard-05',
    author: 'Пол Таггард',
    authorInitial: 'П',
    timestamp: '2026-10-05 13:00',
    tag: '#product-craft',
    type: 'insight',
    sparksCount: 112,
    readTime: '15s',
    title: 'Борьба с эмоциональной когерентностью',
    body: 'Команды часто верят в продукт не потому, что есть метрики, а потому что инвестировали в него слишком много эмоций. Эмоциональная связность блокирует видение реального поведения пользователей.',
    isFired: false,
    isBookmarked: false
  },

  // --- СЬЮЗАН ХЁРЛИ (5 SPARKS) ---
  {
    id: 'spk-hurley-01',
    author: 'Сьюзан Хёрли',
    authorInitial: 'С',
    timestamp: '2026-10-08 18:00',
    tag: '#mental-models',
    type: 'insight',
    sparksCount: 228,
    readTime: '18s',
    title: 'Сознание действует в мире, а не в черепе',
    body: 'Сознание — это не пассивный экран между восприятием и действием. Это замкнутая динамическая петля: действие формирует восприятие ровно в той же степени, в какой восприятие направляет действие.',
    isFired: true,
    isBookmarked: true
  },
  {
    id: 'spk-hurley-02',
    author: 'Сьюзан Хёрли',
    authorInitial: 'С',
    timestamp: '2026-10-09 11:30',
    tag: '#system-design',
    type: 'framework',
    sparksCount: 167,
    readTime: '25s',
    title: 'Shared Circuits Model: Двухуровневая обратная связь',
    body: 'Архитектура агентного действия через упреждающее предсказание отклика среды.',
    frameworkSteps: [
      { id: 1, text: 'Генерация моторного импульса и формирование параллельной эфферентной копии (Forward Model).' },
      { id: 2, text: 'Симуляция ожидаемого отклика сенсорного окружения до физического завершения действия.' },
      { id: 3, text: 'Сравнение реального входящего сигнала с предсказанной моделью и вычисление ошибки дельты.' },
      { id: 4, text: 'Адаптация исполнительного паттерна в рантайме без прерывания целевой траектории.' }
    ],
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-hurley-03',
    author: 'Сьюзан Хёрли',
    authorInitial: 'С',
    timestamp: '2026-10-10 15:15',
    tag: '#heuristics',
    type: 'quote',
    sparksCount: 178,
    readTime: '12s',
    title: 'Подражание против слепого копирования',
    body: '«Подражание не равно механическому копированию. Настоящее действие рождается там, где агент считывает цель за жестом и способен воссоздать её в принципиально иных обстоятельствах.»',
    isFired: true,
    isBookmarked: false
  },
  {
    id: 'spk-hurley-04',
    author: 'Сьюзан Хёрли',
    authorInitial: 'С',
    timestamp: '2026-10-11 17:00',
    tag: '#philosophy',
    type: 'insight',
    sparksCount: 145,
    readTime: '20s',
    title: 'Несводимость ценностей к одной шкале',
    body: 'Причины человеческих поступков и инженерных решений принципиально не сводятся к единой скалярной метрике полезности. Попытка упаковать сложность в один KPI неизбежно порождает вырождение и слепые зоны системы.',
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-hurley-05',
    author: 'Сьюзан Хёрли',
    authorInitial: 'С',
    timestamp: '2026-10-12 12:45',
    tag: '#product-craft',
    type: 'insight',
    sparksCount: 89,
    readTime: '15s',
    title: 'Интерфейс как продолжение моторики',
    body: 'Пользователь понимает систему не через документацию, а через физический отклик на воздействие. Задержка в обратной связи разрушает ощущение авторства действия и превращает инструмент в барьер.',
    isFired: false,
    isBookmarked: false
  },

  // --- ГИЛБЕРТ РАЙЛ (5 SPARKS) ---
  {
    id: 'spk-ryle-01',
    author: 'Гилберт Райл',
    authorInitial: 'Г',
    timestamp: '2026-10-16 18:00',
    tag: '#mental-models',
    type: 'theorem',
    sparksCount: 295,
    readTime: '15s',
    title: 'Knowing-How первично по отношению к Knowing-That',
    body: 'Практический навык и способность действовать предшествуют формулированию инструкций и регламентов.',
    theoremContrast: {
      linear: 'Сначала агент усваивает формальные правила и инструкции, а затем механически исполняет их.',
      nonLinear: 'Навык действия первичен. Интеллект проявляется непосредственно в исполнении под нагрузкой, а не в созерцании регламентов.'
    },
    isFired: true,
    isBookmarked: true
  },
  {
    id: 'spk-ryle-02',
    author: 'Гилберт Райл',
    authorInitial: 'Г',
    timestamp: '2026-10-17 11:20',
    tag: '#system-design',
    type: 'insight',
    sparksCount: 210,
    readTime: '20s',
    title: 'Категориальная ошибка в архитектуре',
    body: 'Категориальная ошибка — искать «призрак в машине» отдельно от функционирующих деталей. Команда, сервисы и инфраструктура — это не абстрактная компания в довесок к коду, это и есть сама система в её реальном взаимодействии.',
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-ryle-03',
    author: 'Гилберт Райл',
    authorInitial: 'Г',
    timestamp: '2026-10-18 14:00',
    tag: '#heuristics',
    type: 'quote',
    sparksCount: 240,
    readTime: '12s',
    title: 'Бесконечный регресс инструкций',
    body: '«Если для применения каждого правила требуется отдельное мета-правило, действие никогда не начнется. Компетентность — это способность действовать безошибочно без бесконечной цепи надсмотрщиков.»',
    isFired: true,
    isBookmarked: false
  },
  {
    id: 'spk-ryle-04',
    author: 'Гилберт Райл',
    authorInitial: 'Г',
    timestamp: '2026-10-19 16:30',
    tag: '#product-craft',
    type: 'framework',
    sparksCount: 154,
    readTime: '25s',
    title: 'Насыщенное описание (Thick Description)',
    body: 'Метод различения механического движения и осознанного намерения в телеметрии.',
    frameworkSteps: [
      { id: 1, text: 'Тонкое описание: фиксация физического события (клик, вызов API, движение курсора).' },
      { id: 2, text: 'Контекстуализация: выявление цели, стоящей за паттерном поведения в текущей сессии.' },
      { id: 3, text: 'Диспозиционный анализ: оценка готовности пользователя совершить следующий связанный шаг.' },
      { id: 4, text: 'Интерпретация сбоя: распознавание ошибки как исследования границы возможностей интерфейса.' }
    ],
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-ryle-05',
    author: 'Гилберт Райл',
    authorInitial: 'Г',
    timestamp: '2026-10-20 10:45',
    tag: '#system-design',
    type: 'insight',
    sparksCount: 320,
    readTime: '20s',
    title: 'Код не бывает нейтральным',
    body: 'Любая строчка логики фиксирует диспозицию её создателя и перекладывает риск на пользователя. Алгоритм не несет моральной ответственности сам по себе: вся полнота риска всегда остается на архитекторе решения.',
    isFired: true,
    isBookmarked: true
  }
];

export function getInitialSparks(): SparkItem[] {
  return [];
}

export async function fetchSparksFromApi(customFetch = fetch): Promise<SparkItem[]> {
  try {
    const res = await customFetch('/api/v1/sparks');
    if (!res.ok) return CANONICAL_SPARKS;
    const data = await res.json();
    const rawList = Array.isArray(data) ? data : (data?.content || []);
    if (rawList.length === 0) return CANONICAL_SPARKS;
    return rawList.map((spk: any) => {
      const author = spk.author || '@efremov_mvp';
      const authorInitial = author.replace(/^@/, '').charAt(0).toUpperCase() || 'D';
      return {
        id: String(spk.id || spk.telegramMessageId || `spk-${Math.random()}`),
        telegramMessageId: spk.telegramMessageId,
        author,
        authorInitial,
        timestamp: spk.publishedAt ? String(spk.publishedAt).slice(0, 16).replace('T', ' ') : 'Запись',
        tag: spk.tags ? (spk.tags.startsWith('#') ? spk.tags : `#${spk.tags}`) : '#sparks',
        type: 'insight' as SparkType,
        sparksCount: 1,
        readTime: '1s',
        body: spk.content || '',
        domain: spk.domain || '',
        publishedAt: spk.publishedAt ? String(spk.publishedAt).slice(0, 16).replace('T', ' ') : '',
        isFired: false,
        isBookmarked: false
      };
    });
  } catch {
    return CANONICAL_SPARKS;
  }
}

export function filterSparks(sparks: SparkItem[], selectedCategory: string, searchQuery: string = ''): SparkItem[] {
  let result = [...sparks];

  if (selectedCategory && selectedCategory !== 'All Sparks') {
    const cleanCategory = selectedCategory.replace(/^#/, '').toLowerCase();
    result = result.filter(spark => {
      const sparkTag = spark.tag.replace(/^#/, '').toLowerCase();
      return sparkTag === cleanCategory || spark.tag.toLowerCase() === selectedCategory.toLowerCase();
    });
  }

  if (searchQuery.trim()) {
    const q = searchQuery.toLowerCase().trim();
    result = result.filter(spark => {
      const bodyText = spark.body || '';
      const titleText = spark.title || '';
      const authorText = spark.author || '';
      const tagText = spark.tag || '';
      const transcriptText = spark.audioSnippet?.transcript || '';
      const linearText = spark.theoremContrast?.linear || '';
      const nonLinearText = spark.theoremContrast?.nonLinear || '';

      return (
        bodyText.toLowerCase().includes(q) ||
        titleText.toLowerCase().includes(q) ||
        authorText.toLowerCase().includes(q) ||
        tagText.toLowerCase().includes(q) ||
        transcriptText.toLowerCase().includes(q) ||
        linearText.toLowerCase().includes(q) ||
        nonLinearText.toLowerCase().includes(q)
      );
    });
  }

  return result;
}

export function createSpark(text: string, author: string = '@dmitryefremov', tag: string = '#heuristics'): SparkItem {
  const trimmed = text.trim().slice(0, 180);
  const initial = author.replace(/^@/, '').charAt(0).toUpperCase() || 'D';

  return {
    id: `spk-user-${Date.now()}`,
    author,
    authorInitial: initial,
    timestamp: 'Just now',
    tag: tag.startsWith('#') ? tag : `#${tag}`,
    type: 'insight',
    sparksCount: 1,
    readTime: '1s',
    body: `"${trimmed}"`,
    isFired: true,
    isBookmarked: false
  };
}

export function toggleSparkFire(sparks: SparkItem[], id: string): SparkItem[] {
  return sparks.map(spark => {
    if (spark.id === id) {
      const isFired = !spark.isFired;
      return {
        ...spark,
        isFired,
        sparksCount: isFired ? spark.sparksCount + 1 : spark.sparksCount - 1
      };
    }
    return spark;
  });
}

export function toggleSparkBookmark(sparks: SparkItem[], id: string): SparkItem[] {
  return sparks.map(spark => {
    if (spark.id === id) {
      return {
        ...spark,
        isBookmarked: !spark.isBookmarked
      };
    }
    return spark;
  });
}

export function calculateTelemetry(sparks: SparkItem[]) {
  const totalCount = sparks.length;
  const totalSparksAccumulated = sparks.reduce((acc, s) => acc + (s.sparksCount || 1), 0);

  return {
    sparksPerMinute: totalCount > 0 ? Math.min(totalCount, 12) : 0,
    throughputWpm: totalCount > 0 ? totalCount * 18 : 0,
    consumedToday: totalCount,
    totalCount,
    totalSparksAccumulated
  };
}
