export type CanonDomain =
  | 'AUTONOMOUS_ENGINEERING'
  | 'AGENTIC_ECONOMY'
  | 'PRAGMATIC_BUSINESS_STRATEGY'
  | 'PHILOSOPHY_OF_MIND';

export type CanonType = 'MANIFESTO' | 'GLOSSARY_TERM' | 'ONTOLOGY';

export type CanonRelationshipType =
  | 'REFERENCES'
  | 'SUPERSEDES'
  | 'RELATED'
  | 'DEPENDS_ON'
  | 'EXTENDS';

export interface CanonCrossReference {
  id?: number;
  sourceSlug: string;
  sourceTitle: string;
  targetSlug: string;
  targetTitle: string;
  relationshipType: CanonRelationshipType;
  description?: string;
  createdAt?: string;
}

export interface CanonItem {
  id?: number;
  slug: string;
  title: string;
  type: CanonType;
  domain: CanonDomain;
  summary?: string;
  content: string;
  publishedAt?: string;
  createdAt?: string;
  updatedAt?: string;
  outgoingReferences?: CanonCrossReference[];
  incomingReferences?: CanonCrossReference[];
}

export interface CanonPage {
  content: CanonItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export const DOMAIN_LABELS: Record<CanonDomain, string> = {
  AUTONOMOUS_ENGINEERING: 'Autonomous Engineering',
  AGENTIC_ECONOMY: 'Agentic Economy (A2A)',
  PRAGMATIC_BUSINESS_STRATEGY: 'Pragmatic Business Strategy',
  PHILOSOPHY_OF_MIND: 'Philosophy of Mind'
};

export const TYPE_LABELS: Record<CanonType, string> = {
  MANIFESTO: 'Evergreen Manifesto',
  GLOSSARY_TERM: 'Glossary Term',
  ONTOLOGY: 'Ontology Mapping'
};

export const INITIAL_CANON_ITEMS: CanonItem[] = [
  {
    id: 1,
    slug: 'manifesto-autonomous-process-sovereignty',
    title: 'Манифест суверенности автономных систем и процессов',
    type: 'MANIFESTO',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Основополагающие принципы построения отказоустойчивых автономных агентов с декрементной деонтической логикой.',
    content: `# Манифест суверенности автономных систем и процессов\n\n## 1. Преамбула\n\nВ современных распределенных вычислительных экосистемах переход от реактивных микросервисов к **суверенным автономным агентам** требует пересмотра базовых инженерных постулатов.\n\n> "Автономный агент — это не просто программа с внешним циклом while(true), а замкнутый когнитивный контур с формально проверенными деонтическими инвариантами."\n\n## 2. Ключевые принципы\n\n1. **Детерминизм и воспрозводимость** — Любая смена состояния должна определяться функцией от текущего состояния, входа и фиксированного семени (seed).\n2. **Деонтическая строгость** — Каждое действие квалифицируется как *Обязательное* $\\mathcal{O}(\\phi)$, *Разрешенное* $\\mathcal{P}(\\phi)$ или *Запрещенное* $\\mathcal{F}(\\phi)$.\n3. **Изоляция сбоев** — Отказ отдельного агента не приводит к деградации всего графа исполняющих узлов.\n\n## 3. Архитектурная деонтика\n\nСистема гарантирует нерушимость нормативных границ через проверяемые контракты. Смотрите связанный термин [deontic-invariants-term](deontic-invariants-term) для деталей.`,
    publishedAt: '2026-03-01T10:00:00Z',
    createdAt: '2026-03-01T10:00:00Z',
    outgoingReferences: [
      {
        sourceSlug: 'manifesto-autonomous-process-sovereignty',
        sourceTitle: 'Манифест суверенности автономных систем и процессов',
        targetSlug: 'deontic-invariants-term',
        targetTitle: 'Деонтический инвариант',
        relationshipType: 'REFERENCES',
        description: 'Ссылается на глоссарное определение деонтических инвариантов'
      },
      {
        sourceSlug: 'manifesto-autonomous-process-sovereignty',
        sourceTitle: 'Манифест суверенности автономных систем и процессов',
        targetSlug: 'ontology-extended-mind-agent-framework',
        targetTitle: 'Онтология расширенного разума и агентных систем',
        relationshipType: 'DEPENDS_ON',
        description: 'Опирается на онтологическую схему расширенного разума'
      }
    ],
    incomingReferences: []
  },
  {
    id: 2,
    slug: 'deontic-invariants-term',
    title: 'Деонтический инвариант (Deontic Invariant)',
    type: 'GLOSSARY_TERM',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Формально доказанное нормативное условие, ограничивающее допустимые переходы состояний автономной системы.',
    content: `# Деонтический инвариант (Deontic Invariant)\n\n**Категория:** Терминоведение и формальные методы\n**Предметная область:** Autonomous Engineering\n\n## Определение\n\n**Деонтический инвариант** — логическое утверждение над пространством состояний $S$, фиксирующее нормативный статус действий агента:\n- $\\mathcal{O}(\\phi)$: Обязанность выполнения условия $\\phi$.\n- $\\mathcal{P}(\\phi)$: Разрешение на переход $\\phi$.\n- $\\mathcal{F}(\\phi)$: Категорический запрет на состояние $\\phi$.\n\n> "Нарушение деонтического инварианта мгновенно блокирует транзакцию на уровне ядра системы."`,
    publishedAt: '2026-03-05T12:00:00Z',
    createdAt: '2026-03-05T12:00:00Z',
    outgoingReferences: [],
    incomingReferences: [
      {
        sourceSlug: 'manifesto-autonomous-process-sovereignty',
        sourceTitle: 'Манифест суверенности автономных систем и процессов',
        targetSlug: 'deontic-invariants-term',
        targetTitle: 'Деонтический инвариант',
        relationshipType: 'REFERENCES',
        description: 'Ссылается на глоссарное определение деонтических инвариантов'
      }
    ]
  },
  {
    id: 3,
    slug: 'manifesto-agentic-economy-a2a',
    title: 'Манифест агентной экономики (A2A Protocol & Value Exchange)',
    type: 'MANIFESTO',
    domain: 'AGENTIC_ECONOMY',
    summary: 'Протоколы экономического взаимодействия и взаимных расчетов между программными агентами без участия человека.',
    content: `# Манифест агентной экономики (A2A Protocol)\n\n## Введение\n\nАгентная экономика (Agent-to-Agent, A2A) описывает новую парадигму, в которой программные агенты выступают самостоятельными субъектами экономических отношений.\n\n## Основные аксиомы A2A\n\n1. **Микроплатежи за API-вызовы** — Каждая атомарная операция оценивается и оплачивается в режиме реального времени.\n2. **Алгоритмическая репутация** — Доверие формируется на основе криптографических доказательств и выполнения контрактов.\n3. **Автономное заключение сделок** — Агенты формируют смарт-контракты без человеческого вмешательства.\n\nСмотрите подробнее в онтологии [ontology-extended-mind-agent-framework](ontology-extended-mind-agent-framework).`,
    publishedAt: '2026-03-10T14:30:00Z',
    createdAt: '2026-03-10T14:30:00Z',
    outgoingReferences: [
      {
        sourceSlug: 'manifesto-agentic-economy-a2a',
        sourceTitle: 'Манифест агентной экономики (A2A Protocol)',
        targetSlug: 'ontology-extended-mind-agent-framework',
        targetTitle: 'Онтология расширенного разума и агентных систем',
        relationshipType: 'EXTENDS',
        description: 'Расширяет онтологию агентных систем в сторону экономики'
      }
    ],
    incomingReferences: []
  },
  {
    id: 4,
    slug: 'ontology-extended-mind-agent-framework',
    title: 'Онтология расширенного разума и агентных систем',
    type: 'ONTOLOGY',
    domain: 'PHILOSOPHY_OF_MIND',
    summary: 'Таксономия гипотезы расширенного познания (Clark & Chalmers) в приложении к современным LLM и агентным инструментам.',
    content: `# Онтология расширенного разума и агентных систем\n\n## Концептуальная рамка\n\nСогласно гипотезе **Расширенного Разума (Extended Mind Thesis)** Энди Кларка и Дэвида Чалмерса, внешние технологические артефакты становятся неотъемлемой частью когнитивной системы человека.\n\n\`\`\`\n[ Human Cognitive Core ] <---> [ Context Memory / Vector DB ] <---> [ Autonomous Execution Engine ]\n\`\`\`\n\n## Элементы онтологии\n\n- **Cognitive Coupling (Когнитивное сопряжение)** — Надежная интеграция внешнего инструмента в арсенал мыследеятельности.\n- **Epistemic Action (Эпистемическое действие)** — Действие в среде, предпринятое для облегчения ментальной нагрузки.\n- **Exogram (Эксограмма)** — Внешний след памяти, хранящийся вне биологического мозга.`,
    publishedAt: '2026-03-12T09:15:00Z',
    createdAt: '2026-03-12T09:15:00Z',
    outgoingReferences: [],
    incomingReferences: [
      {
        sourceSlug: 'manifesto-autonomous-process-sovereignty',
        sourceTitle: 'Манифест суверенности автономных систем и процессов',
        targetSlug: 'ontology-extended-mind-agent-framework',
        targetTitle: 'Онтология расширенного разума и агентных систем',
        relationshipType: 'DEPENDS_ON',
        description: 'Опирается на онтологическую схему расширенного разума'
      },
      {
        sourceSlug: 'manifesto-agentic-economy-a2a',
        sourceTitle: 'Манифест агентной экономики (A2A Protocol)',
        targetSlug: 'ontology-extended-mind-agent-framework',
        targetTitle: 'Онтология расширенного разума и агентных систем',
        relationshipType: 'EXTENDS',
        description: 'Расширяет онтологию агентных систем в сторону экономики'
      }
    ]
  },
  {
    id: 5,
    slug: 'pragmatic-toc-business-strategy',
    title: 'Прагматическая бизнес-стратегия на основе Теории Ограничений (TOC)',
    type: 'MANIFESTO',
    domain: 'PRAGMATIC_BUSINESS_STRATEGY',
    summary: 'Применение пяти направляющих шагов Голдратта к управлению продуктовыми инвестициями и техническим долгом.',
    content: `# Прагматическая бизнес-стратегия на основе Теории Ограничений (TOC)\n\n## Стратегический фокус\n\nПрежде чем развивать новые продуктовые направления, система должна определить **узкое место (Bottleneck)** и подчинить ему весь операционный ритм.\n\n### Пять фокусирующих шагов:\n1. **Идентифицировать** ограничение системы.\n2. **Максимально использовать** ограничение.\n3. **Подчинить** все остальные процессы принятому решению.\n4. **Расшить** ограничение системы.\n5. **Не допустить инерции** — вернуть фокус на новое ограничение.`,
    publishedAt: '2026-03-18T16:00:00Z',
    createdAt: '2026-03-18T16:00:00Z',
    outgoingReferences: [],
    incomingReferences: []
  }
];

export function filterCanonItems(
  items: CanonItem[],
  domainFilter: CanonDomain | 'ALL' = 'ALL',
  typeFilter: CanonType | 'ALL' = 'ALL'
): CanonItem[] {
  return items.filter(item => {
    const matchesDomain = domainFilter === 'ALL' || item.domain === domainFilter;
    const matchesType = typeFilter === 'ALL' || item.type === typeFilter;
    return matchesDomain && matchesType;
  });
}

export async function fetchCanonItems(
  domain?: CanonDomain,
  type?: CanonType
): Promise<CanonItem[]> {
  try {
    const params = new URLSearchParams();
    if (domain) params.append('domain', domain);
    if (type) params.append('type', type);

    const response = await fetch(`/api/v1/canon?${params.toString()}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch canon items: ${response.statusText}`);
    }
    const data: CanonPage = await response.json();
    return data.content && data.content.length > 0
      ? data.content
      : filterCanonItems(INITIAL_CANON_ITEMS, domain || 'ALL', type || 'ALL');
  } catch (_e) {
    // Fallback to in-memory initial canon items
    return filterCanonItems(INITIAL_CANON_ITEMS, domain || 'ALL', type || 'ALL');
  }
}
