import { describe, expect, it } from 'vitest';
import { getGeneratedTranslation } from './generated_localizations';

describe('generated localization manifest', () => {
  it('fails closed when a translation is absent', () => {
    expect(getGeneratedTranslation('missing', ['none'], 'ka')).toBeNull();
  });
});
