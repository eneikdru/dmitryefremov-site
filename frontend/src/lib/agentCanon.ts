export interface ExposedRoute {
  id: string;
  path: string;
  method: string;
  rateLimit: string;
  description: string;
  bindIdentifier: string;
  scopes: string[];
  exposedInAppRouter: boolean;
  corsAgentOrigin: boolean;
  exposedToSdk: boolean;
}

export interface AgentCanonMetrics {
  activeEndpoints: number;
  authedAgents: number;
  canonOps24h: string;
}

export const MACHINE_READABLE_PATHS = [
  '/llms.txt',
  '/.well-known/llms.txt',
  '/api/v1/agent/llms.txt',
  '/ai-agent.json',
  '/.well-known/ai-agent.json'
];

export function isMachineReadablePath(pathname: string): boolean {
  return MACHINE_READABLE_PATHS.includes(pathname);
}

export const DEFAULT_EXPOSED_ROUTES: ExposedRoute[] = [
  {
    id: 'query',
    path: '/api/v1/canon/query',
    method: 'GET',
    rateLimit: '120 req/min/agent',
    description: 'Exposes semantic search & vector retrieval over canonical repository documents.',
    bindIdentifier: 'window.__EXPOSED_ROUTES.canon.query',
    scopes: ['read:canon_core', 'scope:vector_sim'],
    exposedInAppRouter: true,
    corsAgentOrigin: true,
    exposedToSdk: true
  },
  {
    id: 'entities',
    path: '/api/v1/canon/entities',
    method: 'GET',
    rateLimit: '60 req/min',
    description: 'Structured knowledge graph nodes & schema entities for contextual grounding.',
    bindIdentifier: 'window.__EXPOSED_ROUTES.canon.entities',
    scopes: ['read:canon_graph', 'scope:ontology'],
    exposedInAppRouter: true,
    corsAgentOrigin: true,
    exposedToSdk: true
  },
  {
    id: 'stream',
    path: '/api/v1/canon/stream',
    method: 'GET / SSE',
    rateLimit: 'Opt-in Beta',
    description: 'Server-Sent Events stream of real-time canon updates & revision history.',
    bindIdentifier: 'window.__EXPOSED_ROUTES.canon.stream',
    scopes: ['read:canon_stream'],
    exposedInAppRouter: true,
    corsAgentOrigin: true,
    exposedToSdk: false
  }
];

export function initializeExposedRoutes(): void {
  if (typeof window !== 'undefined') {
    window.__EXPOSED_ROUTES = {
      canon: {
        query: '/api/v1/canon/query',
        entities: '/api/v1/canon/entities',
        stream: '/api/v1/canon/stream'
      }
    };
  }
}

export async function fetchMachineReadableContent(path: string): Promise<string> {
  if (typeof window === 'undefined') return '';
  const response = await fetch(path);
  if (!response.ok) {
    throw new Error(`Failed to fetch machine-readable format from ${path}: ${response.statusText}`);
  }
  return await response.text();
}
