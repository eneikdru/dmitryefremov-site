// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import ResponsivePicture from '../components/ResponsivePicture.svelte';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';
import * as fs from 'fs';
import * as path from 'path';

describe('ResponsivePicture Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    component = null;
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
        component = null;
      }
      if (target.parentNode) {
        document.body.removeChild(target);
      }
    };
  });

  it('renders a picture element with AVIF and WebP source tags and fallback img', () => {
    component = mount(ResponsivePicture, {
      target,
      props: {
        assetKey: 'dmitry_efremov_studio',
        alt: 'Dmitry Efremov Studio Portrait'
      }
    });

    const picture = target.querySelector('picture');
    expect(picture).not.toBeNull();
    expect(picture?.classList.contains('responsive-picture')).toBe(true);

    const sources = target.querySelectorAll('source');
    expect(sources.length).toBe(2);

    const avifSource = sources[0];
    expect(avifSource.getAttribute('type')).toBe('image/avif');
    expect(avifSource.getAttribute('srcset')).toContain('/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_375w.avif 375w');
    expect(avifSource.getAttribute('srcset')).toContain('/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_768w.avif 768w');
    expect(avifSource.getAttribute('srcset')).toContain('/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_1440w.avif 1440w');

    const webpSource = sources[1];
    expect(webpSource.getAttribute('type')).toBe('image/webp');
    expect(webpSource.getAttribute('srcset')).toContain('/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_375w.webp 375w');

    const img = target.querySelector('img');
    expect(img).not.toBeNull();
    expect(img?.getAttribute('alt')).toBe('Dmitry Efremov Studio Portrait');
    expect(img?.getAttribute('src')).toBe('/assets/images/dmitry_efremov_studio/dmitry_efremov_studio_768w.webp');
  });

  it('includes descriptive alt text for WCAG 2.1 Level AA compliance', () => {
    const descriptiveAlt = 'Author Dmitry Efremov recorded live in high definition studio setting';
    component = mount(ResponsivePicture, {
      target,
      props: {
        assetKey: 'dmitry_efremov_studio',
        alt: descriptiveAlt
      }
    });

    const img = target.querySelector('img');
    expect(img).not.toBeNull();
    expect(img?.getAttribute('alt')).toBe(descriptiveAlt);
    expect(img?.getAttribute('alt')?.length).toBeGreaterThan(10);
  });

  it('supports custom breakpoints, formats, sizes, loading, and decoding options', () => {
    component = mount(ResponsivePicture, {
      target,
      props: {
        assetKey: 'podcast_studio_cover',
        alt: 'Podcast Studio Cover Art',
        breakpoints: [320, 640],
        formats: ['avif', 'webp'],
        sizes: '(max-width: 480px) 100vw, 50vw',
        loading: 'eager',
        decoding: 'sync',
        fallbackFormat: 'avif',
        fallbackBreakpoint: 640
      }
    });

    const avifSource = target.querySelector('source[type="image/avif"]');
    expect(avifSource).not.toBeNull();
    expect(avifSource?.getAttribute('srcset')).toBe(
      '/assets/images/podcast_studio_cover/podcast_studio_cover_320w.avif 320w, /assets/images/podcast_studio_cover/podcast_studio_cover_640w.avif 640w'
    );
    expect(avifSource?.getAttribute('sizes')).toBe('(max-width: 480px) 100vw, 50vw');

    const img = target.querySelector('img');
    expect(img?.getAttribute('loading')).toBe('eager');
    expect(img?.getAttribute('decoding')).toBe('sync');
    expect(img?.getAttribute('src')).toBe('/assets/images/podcast_studio_cover/podcast_studio_cover_640w.avif');
  });

  it('renders ResponsivePicture inside TelemetryHub hero section', () => {
    component = mount(TelemetryHub, { target });

    const heroAvatarContainer = target.querySelector('#hero-avatar-container');
    expect(heroAvatarContainer).not.toBeNull();

    const picture = heroAvatarContainer?.querySelector('picture');
    expect(picture).not.toBeNull();

    const img = picture?.querySelector('img');
    expect(img?.getAttribute('alt')).toBe('Официальный студийный портрет автора Дмитрия Ефремова');
  });

  it('successfully resolves all AVIF and WebP source paths to existing physical files in public assets', () => {
    const assets = ['dmitry_efremov_studio', 'podcast_studio_cover', 'article_deontic_studio'];
    const bps = [375, 768, 1440];
    const formats = ['avif', 'webp'];

    for (const assetKey of assets) {
      const container = document.createElement('div');
      target.appendChild(container);

      const comp = mount(ResponsivePicture, {
        target: container,
        props: {
          assetKey,
          alt: `Test asset ${assetKey}`,
          breakpoints: bps,
          formats
        }
      });

      const sources = container.querySelectorAll('source');
      expect(sources.length).toBe(formats.length);

      sources.forEach((source) => {
        const srcset = source.getAttribute('srcset') || '';
        const paths = srcset.split(',').map((s) => s.trim().split(' ')[0]);
        expect(paths.length).toBe(bps.length);

        for (const relPath of paths) {
          const fsPath = path.resolve(process.cwd(), 'public', relPath.replace(/^\//, ''));
          expect(fs.existsSync(fsPath)).toBe(true);
        }
      });

      unmount(comp);
      if (container.parentNode) {
        target.removeChild(container);
      }
    }
  });
});
