export interface EssayArticleItem {
  id: string;
  slug: string;
  title: string;
  topic: 'deontic' | 'toc' | 'autonomous' | 'systems';
  readTime: string;
  publishedAt: string;
  summary: string;
  contentMarkdown: string;
}

export async function fetchEssaysFromApi(customFetch = fetch): Promise<EssayArticleItem[]> {
  try {
    const res = await customFetch('/api/v1/essays');
    if (!res.ok) return [];
    const data = await res.json();
    const rawList = Array.isArray(data) ? data : (data?.content || []);
    return rawList
      .filter((item: any) => {
        const title = (item.title || '').toLowerCase();
        const slug = (item.slug || '').toLowerCase();
        if (title.includes('teleprompter') || slug.includes('teleprompter') || title.includes('raw_') || title.includes('суфлёр')) {
          return false;
        }
        return true;
      })
      .map((item: any) => {
        return {
          id: item.slug || String(item.id),
          slug: item.slug || String(item.id),
          title: item.title || 'Отредактированное эссе',
          topic: (item.topic || 'systems') as any,
          readTime: item.readTime || '5 min read',
          publishedAt: item.publishedAt ? String(item.publishedAt).slice(0, 10) : '',
          summary: item.summary || '',
          contentMarkdown: item.content || item.summary || ''
        };
      });
  } catch {
    return [];
  }
}

export async function fetchCanonFromApi(customFetch = fetch): Promise<any[]> {
  try {
    const res = await customFetch('/api/v1/canon');
    if (!res.ok) return [];
    const data = await res.json();
    return Array.isArray(data) ? data : (data?.content || []);
  } catch {
    return [];
  }
}
