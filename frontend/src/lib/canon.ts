export interface CanonCrossReference {
  id: number;
  sourceSlug: string;
  sourceTitle: string;
  targetSlug: string;
  targetTitle: string;
  relationshipType: 'REFERENCES' | 'SUPERSEDES' | 'RELATED' | 'DEPENDS_ON' | 'EXTENDS';
  description?: string;
  createdAt?: string;
}

export interface CanonItem {
  id: number;
  slug: string;
  title: string;
  type: 'MANIFESTO' | 'GLOSSARY_TERM' | 'ONTOLOGY';
  domain: 'AUTONOMOUS_ENGINEERING' | 'AGENTIC_ECONOMY' | 'PRAGMATIC_BUSINESS_STRATEGY' | 'PHILOSOPHY_OF_MIND';
  summary?: string;
  content: string;
  publishedAt?: string;
  createdAt?: string;
  updatedAt?: string;
  outgoingReferences?: CanonCrossReference[];
  incomingReferences?: CanonCrossReference[];
}

export interface CorpusSearchResult {
  id: string;
  title: string;
  slug: string;
  type: 'canon' | 'podcast' | 'essay' | 'spark';
  domain?: string;
  snippet: string;
  backlinksCount?: number;
  url: string;
}

export const SEEDED_CANON_ITEMS: CanonItem[] = [
  {
    id: 101,
    slug: 'raft-protocol-state-machine',
    title: 'Протокол Raft и репликация состояний (SMR)',
    type: 'ONTOLOGY',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Архитектура распределенного консенсуса, декомпозированная на выбор лидера, репликацию логов и безопасность.',
    content: `Алгоритм консенсуса Raft предназначен для управления реплицированными логами в распределенном кластере.
    Он декомпозирует консенсус стейт-машины на три независимые проблемы: [[Выборы лидера]], репликация логов и временная безопасность.

    В штатном режиме избранный лидер обрабатывает команды клиентов, записывая их в локальный журнал перед рассылкой RPC-запросов активным фолловерам.
    При сбоях используется тайм-аут выборов для предотвращения состояний split-brain, работая строго в рамках crash-recovery семантики.

    Для предотвращения неограниченного роста логов применяется периодическое [[Уплотнение логов]]. Этот механизм снимков сбрасывает исторические состояния, сохраняя канонические снимки стейт-машины.`,
    publishedAt: '2024-10-24T10:00:00Z',
    outgoingReferences: [
      {
        id: 1,
        sourceSlug: 'raft-protocol-state-machine',
        sourceTitle: 'Протокол Raft и репликация состояний (SMR)',
        targetSlug: 'leader-election',
        targetTitle: 'Выборы лидера',
        relationshipType: 'DEPENDS_ON',
        description: 'Основа поддержания единства в кластере Raft'
      },
      {
        id: 2,
        sourceSlug: 'raft-protocol-state-machine',
        sourceTitle: 'Протокол Raft и репликация состояний (SMR)',
        targetSlug: 'log-compaction',
        targetTitle: 'Уплотнение логов',
        relationshipType: 'EXTENDS',
        description: 'Периодическое создание снимков для очистки WAL'
      }
    ],
    incomingReferences: [
      {
        id: 3,
        sourceSlug: 'state-machine-replication',
        sourceTitle: 'State Machine Replication (SMR)',
        targetSlug: 'raft-protocol-state-machine',
        targetTitle: 'Протокол Raft и репликация состояний (SMR)',
        relationshipType: 'REFERENCES',
        description: 'Raft является ключевой практической реализацией SMR'
      },
      {
        id: 4,
        sourceSlug: 'lamport-timestamps',
        sourceTitle: 'Логические часы Лампорта',
        targetSlug: 'raft-protocol-state-machine',
        targetTitle: 'Протокол Raft и репликация состояний (SMR)',
        relationshipType: 'DEPENDS_ON',
        description: 'Монотонные последовательности терминов для причинного порядка'
      }
    ]
  },
  {
    id: 102,
    slug: 'leader-election',
    title: 'Выборы лидера в распределенных системах',
    type: 'GLOSSARY_TERM',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Процесс достижения единогласного решения о назначении управляющего узла в распределенном кластере.',
    content: `Кандидаты инициируют случайные таймеры выборов в диапазоне 150мс-300мс.
    Кандидат, переходящий в роль лидера, должен получить кворум большинства голосов в термине T до начала отправки heartbeat-сигналов.`,
    publishedAt: '2024-10-20T12:00:00Z',
    outgoingReferences: [],
    incomingReferences: [
      {
        id: 5,
        sourceSlug: 'raft-protocol-state-machine',
        sourceTitle: 'Протокол Raft и репликация состояний (SMR)',
        targetSlug: 'leader-election',
        targetTitle: 'Выборы лидера в распределенных системах',
        relationshipType: 'DEPENDS_ON',
        description: 'Выборы лидера гарантируют безопасность термина'
      }
    ]
  },
  {
    id: 103,
    slug: 'log-compaction',
    title: 'Уплотнение логов и снимки состояний',
    type: 'GLOSSARY_TERM',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Инкрементальная рекультивация памяти и дискового пространства через сжатие журнала транзакций.',
    content: `Метод уплотнения логов сворачивает исторические цепочки транзакций в атомарные снимки состояния, сбрасывая базовый индекс журнала.`,
    publishedAt: '2024-10-18T14:00:00Z',
    outgoingReferences: [],
    incomingReferences: [
      {
        id: 6,
        sourceSlug: 'raft-protocol-state-machine',
        sourceTitle: 'Протокол Raft и репликация состояний (SMR)',
        targetSlug: 'log-compaction',
        targetTitle: 'Уплотнение логов и снимки состояний',
        relationshipType: 'EXTENDS',
        description: 'Предотвращение бесконечного роста WAL'
      }
    ]
  },
  {
    id: 104,
    slug: 'extended-mind-ontology',
    title: 'Онтология расширенного разума и A2A-экономика',
    type: 'MANIFESTO',
    domain: 'PHILOSOPHY_OF_MIND',
    summary: 'Манифест о представлении персонального портала как внешнего рабочего контура мышления автора.',
    content: `Согласно теории Энди Кларка, внешние инструменты и цифровые носители становятся неотъемлемой частью когнитивных процессов человека.
    Сайт перестает быть статичной витриной и превращается в живую внешнюю память. Каждая мысль, эссе и искры из Telegram сразу связываются в единый граф понятий Канона.`,
    publishedAt: '2024-10-15T09:00:00Z',
    outgoingReferences: [],
    incomingReferences: []
  }
];

export async function fetchCanonItems(): Promise<CanonItem[]> {
  try {
    const res = await fetch('/api/v1/canon');
    if (res.ok) {
      const data = await res.json();
      if (data && Array.isArray(data.content) && data.content.length > 0) {
        return data.content;
      }
    }
  } catch {
    // Return seeded fallback
  }
  return SEEDED_CANON_ITEMS;
}

export async function fetchCanonItemBySlug(slug: string): Promise<CanonItem | null> {
  try {
    const res = await fetch(`/api/v1/canon/${encodeURIComponent(slug)}`);
    if (res.ok) {
      const data = await res.json();
      if (data && data.slug) {
        return data;
      }
    }
  } catch {
    // Fallback
  }
  return SEEDED_CANON_ITEMS.find(item => item.slug === slug) || SEEDED_CANON_ITEMS[0];
}

export async function searchCorpus(query: string): Promise<CorpusSearchResult[]> {
  const normalized = query.trim().toLowerCase();

  const allResults: CorpusSearchResult[] = [
    // Canon Items
    ...SEEDED_CANON_ITEMS.map(item => ({
      id: `canon-${item.id}`,
      title: item.title,
      slug: item.slug,
      type: 'canon' as const,
      domain: item.domain,
      snippet: item.summary || item.content.slice(0, 120),
      backlinksCount: (item.incomingReferences?.length || 0) + (item.outgoingReferences?.length || 0),
      url: `#/canon/${item.slug}`
    })),
    // Podcast Episodes
    {
      id: 'podcast-ep1',
      title: 'Выпуск #01: Онтология автономии и деонтическая логика',
      slug: 'episode-01',
      type: 'podcast' as const,
      snippet: 'Разбор нормативных систем, субъектности и принципов деонтического контроля в автономных процессах.',
      backlinksCount: 18,
      url: '#/podcast'
    },
    {
      id: 'podcast-ep2',
      title: 'Выпуск #02: Тезис Баркан и модальная логика агентов',
      slug: 'episode-02',
      type: 'podcast' as const,
      snippet: 'Разбор формулы Рут Баркан Маркус и её применения в спецификации многоагентных систем.',
      backlinksCount: 24,
      url: '#/podcast'
    },
    // Essays
    {
      id: 'essay-1',
      title: 'Деонтическая логика в архитектуре автономных процессов',
      slug: 'deontic-logic-architecture',
      type: 'essay' as const,
      snippet: 'Как строгая формализация нормативных ограничений защищает распределенные системы от сбоев.',
      backlinksCount: 31,
      url: '#/article'
    },
    {
      id: 'essay-2',
      title: 'A2A-экономика: Суверенные агенты и микротранзакции',
      slug: 'a2a-economy-sovereign-agents',
      type: 'essay' as const,
      snippet: 'Архитектурный фреймворк для взаимодействия автономных ИИ-агентов на основе смарт-контрактов.',
      backlinksCount: 15,
      url: '#/article'
    },
    // Sparks
    {
      id: 'spark-1',
      title: 'Иискра: Принцип изоляции суфлёра подкаста',
      slug: 'spark-raw-teleprompter-isolation',
      type: 'spark' as const,
      snippet: 'Сырые суфлеры никогда не публикуются напрямую. Только отредактированные тезисы входят в граф.',
      backlinksCount: 9,
      url: '#/sparks'
    }
  ];

  if (!normalized) {
    return allResults;
  }

  return allResults.filter(item =>
    item.title.toLowerCase().includes(normalized) ||
    item.snippet.toLowerCase().includes(normalized) ||
    item.slug.toLowerCase().includes(normalized)
  );
}
