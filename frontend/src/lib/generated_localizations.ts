import generated from './generated_localizations.json';

export type GeneratedLocale = 'ru' | 'en' | 'ka';
export type GeneratedFields = Record<string, unknown>;

type GeneratedItem = {
  source_hash?: string;
  source_language?: GeneratedLocale;
  translations?: Partial<Record<GeneratedLocale, GeneratedFields>>;
};

type GeneratedManifest = {
  schema_version: number;
  generated_at: string | null;
  content: Record<string, Record<string, GeneratedItem>>;
};

const manifest = generated as GeneratedManifest;

export function getGeneratedTranslation(
  contentType: string,
  keys: Array<string | number | null | undefined>,
  locale: GeneratedLocale
): GeneratedFields | null {
  const bucket = manifest.content?.[contentType] || {};
  for (const key of keys) {
    if (key === null || key === undefined) continue;
    const item = bucket[String(key)];
    const fields = item?.translations?.[locale];
    if (fields) return fields;
  }
  return null;
}
