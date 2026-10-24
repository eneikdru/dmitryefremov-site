import { describe, it, expect, beforeEach, vi } from 'vitest';
import {
  isMachineReadablePath,
  DEFAULT_EXPOSED_ROUTES,
  initializeExposedRoutes,
  fetchMachineReadableContent,
  MACHINE_READABLE_PATHS
} from './agentCanon';

describe('Agent Canon Library Logic', () => {
  beforeEach(() => {
    delete (window as any).__EXPOSED_ROUTES;
    vi.restoreAllMocks();
  });

  it('correctly identifies machine-readable format endpoints', () => {
    expect(isMachineReadablePath('/llms.txt')).toBe(true);
    expect(isMachineReadablePath('/.well-known/llms.txt')).toBe(true);
    expect(isMachineReadablePath('/api/v1/agent/llms.txt')).toBe(true);
    expect(isMachineReadablePath('/ai-agent.json')).toBe(true);
    expect(isMachineReadablePath('/.well-known/ai-agent.json')).toBe(true);
    expect(isMachineReadablePath('/random-path')).toBe(false);
  });

  it('contains expected default exposed routes and binding definitions', () => {
    expect(DEFAULT_EXPOSED_ROUTES.length).toBeGreaterThanOrEqual(3);
    const queryRoute = DEFAULT_EXPOSED_ROUTES.find((r) => r.id === 'query');
    expect(queryRoute).toBeDefined();
    expect(queryRoute?.path).toBe('/api/v1/canon/query');
    expect(queryRoute?.bindIdentifier).toBe('window.__EXPOSED_ROUTES.canon.query');
  });

  it('initializes window.__EXPOSED_ROUTES correctly', () => {
    expect((window as any).__EXPOSED_ROUTES).toBeUndefined();
    initializeExposedRoutes();
    expect((window as any).__EXPOSED_ROUTES).toBeDefined();
    expect((window as any).__EXPOSED_ROUTES.canon.query).toBe('/api/v1/canon/query');
    expect((window as any).__EXPOSED_ROUTES.canon.entities).toBe('/api/v1/canon/entities');
  });

  it('fetches machine-readable format content successfully', async () => {
    const mockContent = '# Dmitry Efremov Canon Index\n> Machine readable index';
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      text: async () => mockContent
    }));

    const result = await fetchMachineReadableContent('/llms.txt');
    expect(result).toBe(mockContent);
    expect(fetch).toHaveBeenCalledWith('/llms.txt');
  });

  it('throws an error when fetching non-ok response for machine-readable format', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: false,
      statusText: 'Not Found'
    }));

    await expect(fetchMachineReadableContent('/llms.txt')).rejects.toThrow(
      'Failed to fetch machine-readable format from /llms.txt: Not Found'
    );
  });
});
