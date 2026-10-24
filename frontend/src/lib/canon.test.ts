import { describe, it, expect, vi } from 'vitest';
import {
  CANON_ROUTES,
  fetchLlmsTxt,
  fetchWellKnownLlmsTxt,
  fetchWellKnownAiAgentJson,
  fetchAgentTelemetry,
  submitAgentOrder
} from './canon';

describe('Canon API and Helper Functions', () => {
  it('defines canon routes matching system endpoints', () => {
    expect(CANON_ROUTES.length).toBeGreaterThanOrEqual(4);
    const paths = CANON_ROUTES.map(r => r.path);
    expect(paths).toContain('/llms.txt');
    expect(paths).toContain('/.well-known/ai-agent.json');
    expect(paths).toContain('/api/v1/agent/orders');
  });

  it('fetchLlmsTxt fetches text from /llms.txt', async () => {
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      text: () => Promise.resolve('# Dmitry Efremov Personal Site & Service API')
    });

    const result = await fetchLlmsTxt(mockFetch as any);
    expect(mockFetch).toHaveBeenCalledWith('/llms.txt');
    expect(result).toContain('Dmitry Efremov Personal Site');
  });

  it('fetchWellKnownLlmsTxt fetches text from /.well-known/llms.txt', async () => {
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      text: () => Promise.resolve('# Dmitry Efremov Canon Index')
    });

    const result = await fetchWellKnownLlmsTxt(mockFetch as any);
    expect(mockFetch).toHaveBeenCalledWith('/.well-known/llms.txt');
    expect(result).toContain('Dmitry Efremov Canon Index');
  });

  it('fetchWellKnownAiAgentJson fetches JSON index from /.well-known/ai-agent.json', async () => {
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ articles: [], essays: [], podcasts: [] })
    });

    const result = await fetchWellKnownAiAgentJson(mockFetch as any);
    expect(mockFetch).toHaveBeenCalledWith('/.well-known/ai-agent.json');
    expect(result).toHaveProperty('articles');
  });

  it('submitAgentOrder sends POST request to /api/v1/agent/orders', async () => {
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ orderId: 'ORD-123', status: 'NEW' })
    });

    const orderData = {
      clientName: 'TestAgent',
      clientEmail: 'agent@test.com',
      serviceType: 'MVP',
      requirements: 'Build MVP'
    };

    const res = await submitAgentOrder(orderData, mockFetch as any);
    expect(mockFetch).toHaveBeenCalledWith('/api/v1/agent/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(orderData)
    });
    expect(res.success).toBe(true);
    expect(res.data.orderId).toBe('ORD-123');
  });
});
