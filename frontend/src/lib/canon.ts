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
        podcasts: [{ title: "Выпуск 01: Системы и реальность", slug: "episode-01", url: "/api/v1/podcasts/episode-01" }]
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
