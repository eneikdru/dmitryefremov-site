// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { mount, unmount, flushSync } from 'svelte';
import PodcastPlayerView from '../components/PodcastPlayerView.svelte';
import ArticleStreamView from '../components/ArticleStreamView.svelte';
import { fetchPodcastsFromApi, PODCAST_EPISODES } from './podcast';
import { fetchEssaysFromApi } from './essays';
import { setLanguage } from './i18n';

vi.mock('./podcast', async importActual => ({...(await importActual<object>()),fetchPodcastsFromApi:vi.fn()}));
vi.mock('./essays', async importActual => ({...(await importActual<object>()),fetchEssaysFromApi:vi.fn()}));
vi.mock('./sparks', async importActual => ({...(await importActual<object>()),fetchSparksFromApi:vi.fn().mockResolvedValue([])}));

const episode={id:9999,slug:'test-api-new-podcast',philosopher:'Test author',title:'Live API episode',
  thesis:'Live thesis',description:'Live description',durationSeconds:60,publishedAt:'2026-11-20',
  audioUrl:'/audio/test-api.m4a',youtubeUrl:'https://youtu.be/iU-_vpKfCnY',telegramUrl:'https://t.me/efremov_mvp',timecodes:[]};
const essay={id:'test-api-new-essay',slug:'test-api-new-essay',title:'Live API essay',topic:'systems' as const,
  summary:'Live summary',contentMarkdown:'## Live content\n\nThe complete API body.',readTime:'3 min',publishedAt:'2026-11-20'};
let target:HTMLElement;let component:ReturnType<typeof mount>;
beforeEach(()=>{target=document.createElement('div');document.body.append(target);setLanguage('en');vi.clearAllMocks();
  vi.mocked(fetchPodcastsFromApi).mockResolvedValue([episode]);vi.mocked(fetchEssaysFromApi).mockResolvedValue([essay]);});
afterEach(async()=>{if(component)await unmount(component);target.remove();});

describe('Catalog refresh preserves deep links',()=>{
  it('loads an API-only podcast despite the populated bundled seed',async()=>{
    component=mount(PodcastPlayerView,{target,props:{selectedEpisodeId:episode.slug}});flushSync();
    await vi.waitFor(()=>{flushSync();expect(target.textContent).toContain(episode.title);});
    expect(fetchPodcastsFromApi).toHaveBeenCalledOnce();expect(target.querySelector('#podcast-not-found')).toBeNull();
    expect(target.querySelector('audio')?.getAttribute('src')).toBe(episode.audioUrl);
  });
  it('does not show not-found before the catalog request resolves',async()=>{
    let resolve!:(value:typeof episode[])=>void;
    vi.mocked(fetchPodcastsFromApi).mockReturnValue(new Promise(r=>{resolve=r;}));
    component=mount(PodcastPlayerView,{target,props:{selectedEpisodeId:episode.slug}});flushSync();
    expect(target.querySelector('[role="status"]')).not.toBeNull();expect(target.querySelector('#podcast-not-found')).toBeNull();
    resolve([episode]);await vi.waitFor(()=>{flushSync();expect(target.textContent).toContain(episode.title);});
  });
  it('keeps an unknown podcast as not-found after the API completes',async()=>{
    component=mount(PodcastPlayerView,{target,props:{selectedEpisodeId:'does-not-exist'}});flushSync();
    await vi.waitFor(()=>{flushSync();expect(target.querySelector('#podcast-not-found')).not.toBeNull();});
    expect(target.querySelector('audio')).toBeNull();
  });
  it('resolves a legacy numeric selection after API IDs change',async()=>{
    const legacy=PODCAST_EPISODES[0];vi.mocked(fetchPodcastsFromApi).mockResolvedValue([{...legacy,id:9999}]);
    component=mount(PodcastPlayerView,{target,props:{selectedEpisodeId:legacy.id}});flushSync();
    await vi.waitFor(()=>{flushSync();expect(target.querySelector('audio')).not.toBeNull();});
    expect(target.querySelector('#podcast-not-found')).toBeNull();
  });
  it('loads an API-only essay despite the populated bundled seed',async()=>{
    component=mount(ArticleStreamView,{target,props:{selectedArticleId:essay.slug}});flushSync();
    await vi.waitFor(()=>{flushSync();expect(target.textContent).toContain('The complete API body.');});
    expect(fetchEssaysFromApi).toHaveBeenCalledOnce();expect(target.querySelector('#article-not-found')).toBeNull();
  });
  it('keeps an unknown essay as not-found after the API completes',async()=>{
    component=mount(ArticleStreamView,{target,props:{selectedArticleId:'does-not-exist'}});flushSync();
    await vi.waitFor(()=>{flushSync();expect(target.querySelector('#article-not-found')).not.toBeNull();});
  });
  it('preserves explicitly supplied catalogs without fetching a replacement',()=>{
    component=mount(PodcastPlayerView,{target,props:{episodes:[episode],selectedEpisodeId:episode.id}});flushSync();
    expect(target.textContent).toContain(episode.title);expect(fetchPodcastsFromApi).not.toHaveBeenCalled();
  });
});
