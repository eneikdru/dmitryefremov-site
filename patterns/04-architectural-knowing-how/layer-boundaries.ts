/**
 * Pattern 04: Architectural Knowing-How & Layer Boundaries
 * Epistemic source: Gilbert Ryle (Knowing-How vs Knowing-That, Dispositions & Category Mistake)
 * Architectural synthesis: Dmitry Efremov
 */

export interface LayerBoundaryContract {
  readonly layer: 'domain' | 'application' | 'infrastructure' | 'presentation';
  readonly allowedImports: ReadonlyArray<'domain' | 'application' | 'infrastructure' | 'presentation'>;
  readonly forbiddenPatterns: readonly RegExp[];
}

export const ARCHITECTURAL_BOUNDARIES: Record<string, LayerBoundaryContract> = {
  domain: {
    layer: 'domain',
    allowedImports: ['domain'],
    // Доменный слой не может знать об инфраструктуре или фреймворках
    forbiddenPatterns: [/fetch/, /axios/, /database/, /repository/, /express/, /react/, /svelte/]
  },
  application: {
    layer: 'application',
    allowedImports: ['domain', 'application'],
    forbiddenPatterns: [/express/, /fastify/, /koa/, /react/, /svelte/]
  },
  infrastructure: {
    layer: 'infrastructure',
    allowedImports: ['domain', 'application', 'infrastructure'],
    forbiddenPatterns: [/presentation/, /react/, /svelte/]
  }
};

export function validateModuleImport(
  sourceLayer: 'domain' | 'application' | 'infrastructure',
  targetImportPath: string
): { valid: boolean; violation?: string } {
  const boundary = ARCHITECTURAL_BOUNDARIES[sourceLayer];
  if (!boundary) return { valid: true };

  for (const pattern of boundary.forbiddenPatterns) {
    if (pattern.test(targetImportPath)) {
      return {
        valid: false,
        violation: `Layer [${sourceLayer}] is forbidden from importing [${targetImportPath}] matching rule ${pattern}`
      };
    }
  }

  return { valid: true };
}
