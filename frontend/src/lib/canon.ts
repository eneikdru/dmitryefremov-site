export interface CanonRoute {
  id: string;
  method: 'GET' | 'POST' | 'SSE';
  path: string;
  scope: string;
  status: 'Active' | 'Paused' | 'Standby';
  description: string;
  rateLimit: string;
  category: 'read' | 'query' | 'webhooks';
  schema?: string;
}

export interface CanonPattern {
  id: string;
  philosopher: string;
  title: string;
  directiveName: string;
  directivePrompt: string;
  useCase: string;
}

export const CANON_PATTERNS: CanonPattern[] = [
  {
    id: 'pattern-barcan-rigid-identity',
    philosopher: 'Рут Баркан Маркус',
    title: 'Rigid Contract Identity (Жесткая референция контрактов)',
    directiveName: 'RIGID_CONTRACT_IDENTITY',
    useCase: 'Защита системных контрактов от контекстуального размывания и непрерывный учет неудаляемых обязательств (moral remainder).',
    directivePrompt: `[SYSTEM DIRECTIVE: RIGID_CONTRACT_IDENTITY]
You must enforce rigid designation across all possible system states. Once an entity or contract is bound to a symbolic name, its core obligations cannot be diluted or reinterpreted due to subsequent environmental shifts or cost trade-offs. If obligations conflict, preserve the unfulfilled branch as an explicit debt (moral remainder) in telemetry rather than silently dropping invariants.`
  },
  {
    id: 'pattern-thagard-coherence-falsification',
    philosopher: 'Пол Таггард',
    title: 'Explanatory Coherence & Falsification (ECHO & Фальсификация)',
    directiveName: 'EXPLANATORY_COHERENCE_ECHO',
    useCase: 'Взвешивание конкурирующих гипотез, защита от корпоративной пропаганды и красивой лжи через жесткий вопрос фальсификации.',
    directivePrompt: `[SYSTEM DIRECTIVE: EXPLANATORY_COHERENCE_ECHO]
Evaluate competing hypotheses using strict coherence criteria: (1) Symmetrical evidentiary support, (2) Penalize ad-hoc auxiliary explanations, (3) Require explicit falsification criteria. For every major proposed conclusion, explicitly state: "What verifiable evidence would definitively disprove this assertion?" Reject any narrative that achieves coherence solely by ignoring conflicting signals.`
  },
  {
    id: 'pattern-hurley-sensorimotor-loop',
    philosopher: 'Сьюзан Хёрли',
    title: 'Sensorimotor Loop & Shared Circuits (Действие в мире)',
    directiveName: 'SENSORIMOTOR_LOOP_EXECUTION',
    useCase: 'Двухуровневая обратная связь с упреждающей моделью отклика среды (Forward Model) вместо слепого одноразового вывода.',
    directivePrompt: `[SYSTEM DIRECTIVE: SENSORIMOTOR_LOOP_EXECUTION]
Do not treat reasoning as an isolated deliberation step preceding execution. Implement an active sensorimotor loop: emit exploratory actions, compare predicted environmental feedback with observed deltas, and refine internal models incrementally in runtime. Reject monolithic one-shot command generation without closed-loop grounding.`
  },
  {
    id: 'pattern-ryle-knowing-how',
    philosopher: 'Гилберт Райл',
    title: 'Knowing-How & Anti-Category Error (Навык против призраков)',
    directiveName: 'KNOWING_HOW_ANTI_REGRESS',
    useCase: 'Предотвращение бесконечного регресса инструкций и отказ от поиска вымышленных скрытых сущностей (призрак в машине).',
    directivePrompt: `[SYSTEM DIRECTIVE: KNOWING_HOW_ANTI_REGRESS]
Prioritize operational competence (knowing-how) over recursive procedural rules (knowing-that). Avoid category errors: do not hypothesize unseen centralized controllers when distributed behavioral dispositions explain the outcome. Terminate infinite regresses of instructions by grounding decisions in observable dispositions under stress.`
  }
];

export const CANON_ROUTES: CanonRoute[] = [
  {
    id: 'route-lore-summary',
    method: 'GET',
    path: '/llms.txt',
    scope: 'read:canon',
    status: 'Active',
    description: 'Provides summarized core world canon, service endpoints, and discovery documentation for LLM system prompts.',
    rateLimit: '60 req/min · 0ms auth cache',
    category: 'read',
    schema: 'Text (Markdown)'
  },
  {
    id: 'route-wellknown-json',
    method: 'GET',
    path: '/.well-known/ai-agent.json',
    scope: 'read:entities',
    status: 'Active',
    description: 'Dynamic index of articles, essays, and podcast episodes exported in machine-readable JSON format.',
    rateLimit: '30 req/min · Context: Full',
    category: 'read',
    schema: JSON.stringify(
      {
        articles: [{ title: "Деонтическая логика в автономии", slug: "deontic-logic", url: "/api/v1/articles/deontic-logic" }],
        essays: [{ title: "Онтология автономии", slug: "ontology-autonomy", url: "/api/v1/essays/ontology-autonomy" }],
        podcasts: [
          { title: "Рут Баркан Маркус: Имя не равно описанию", slug: "01-ruth-barcan-marcus", url: "/api/v1/podcasts/01-ruth-barcan-marcus" },
          { title: "Пол Таггард: Почему логичная ложь побеждает факты?", slug: "02-paul-thagard", url: "/api/v1/podcasts/02-paul-thagard" },
          { title: "Сьюзан Хёрли: Почему мы копируем других?", slug: "03-susan-hurley", url: "/api/v1/podcasts/03-susan-hurley" },
          { title: "Гилберт Райл: Знать — не значит уметь", slug: "04-gilbert-ryle", url: "/api/v1/podcasts/04-gilbert-ryle" }
        ]
      },
      null,
      2
    )
  },
  {
    id: 'route-agent-orders',
    method: 'POST',
    path: '/api/v1/agent/orders',
    scope: 'query:vector',
    status: 'Active',
    description: 'Allows external AI agents to submit service order requests (MVP development, audit, automation).',
    rateLimit: '120 req/min · Schema v1',
    category: 'query'
  },
  {
    id: 'route-events-stream',
    method: 'GET',
    path: '/api/v1/agent/telemetry',
    scope: 'stream:events',
    status: 'Paused',
    description: 'Real-time telemetry and interaction volume metrics for AI agents.',
    rateLimit: 'Event-Driven Channel',
    category: 'webhooks'
  }
];

export async function fetchLlmsTxt(customFetch = fetch): Promise<string> {
  try {
    const res = await customFetch('/llms.txt');
    if (!res.ok) return '# Dmitry Efremov Personal Site & Service API for AI Agents\n\nEndpoint unavailable.';
    return await res.text();
  } catch {
    return '# Dmitry Efremov Personal Site & Service API for AI Agents\n\nError connecting to API.';
  }
}

export async function fetchWellKnownLlmsTxt(customFetch = fetch): Promise<string> {
  try {
    const res = await customFetch('/.well-known/llms.txt');
    if (!res.ok) return '# Dmitry Efremov Canon Index\n\nEndpoint unavailable.';
    return await res.text();
  } catch {
    return '# Dmitry Efremov Canon Index\n\nError connecting to API.';
  }
}

export async function fetchWellKnownAiAgentJson(customFetch = fetch): Promise<Record<string, any>> {
  try {
    const res = await customFetch('/.well-known/ai-agent.json');
    if (!res.ok) return { error: 'Endpoint unavailable' };
    return await res.json();
  } catch {
    return { error: 'Error connecting to API' };
  }
}

export async function fetchAgentTelemetry(customFetch = fetch): Promise<Record<string, any>> {
  try {
    const res = await customFetch('/api/v1/agent/telemetry');
    if (!res.ok) return { totalInvocations: 0, status: 'offline' };
    return await res.json();
  } catch {
    return { totalInvocations: 0, status: 'error' };
  }
}

export async function submitAgentOrder(
  order: { clientName: string; clientEmail: string; serviceType: string; requirements?: string },
  customFetch = fetch
): Promise<any> {
  try {
    const res = await customFetch('/api/v1/agent/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(order)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: 'Failed to submit order' }));
      return { success: false, error: err.message || 'Submission error' };
    }
    const data = await res.json();
    return { success: true, data };
  } catch (e: any) {
    return { success: false, error: e.message || 'Network error' };
  }
}
