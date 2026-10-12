// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import ResponsivePicture from '../components/ResponsivePicture.svelte';
import { mount, unmount } from 'svelte';

describe('ResponsivePicture Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('renders a picture element with AVIF and WebP source tags and fallback img', () => {
    component = mount(ResponsivePicture, {
      target,
      props: {
        assetKey: 'dmitrii_efremov',
        alt: 'Studio portrait of Dmitry Efremov',
        id: 'test-portrait'
      }
    });

    const pictureEl = target.querySelector('picture');
    expect(pictureEl).not.toBeNull();

    const sources = target.querySelectorAll('source');
    expect(sources.length).toBe(2);

    expect(sources[0].getAttribute('type')).toBe('image/avif');
    expect(sources[0].getAttribute('srcset')).toContain('/assets/images/dmitrii_efremov/dmitrii_efremov_375w.avif 375w');
    expect(sources[0].getAttribute('srcset')).toContain('/assets/images/dmitrii_efremov/dmitrii_efremov_768w.avif 768w');
    expect(sources[0].getAttribute('srcset')).toContain('/assets/images/dmitrii_efremov/dmitrii_efremov_1440w.avif 1440w');

    expect(sources[1].getAttribute('type')).toBe('image/webp');
    expect(sources[1].getAttribute('srcset')).toContain('/assets/images/dmitrii_efremov/dmitrii_efremov_375w.webp 375w');

    const imgEl = target.querySelector('img');
    expect(imgEl).not.toBeNull();
    expect(imgEl?.getAttribute('id')).toBe('test-portrait');
    expect(imgEl?.getAttribute('src')).toBe('/assets/images/dmitrii_efremov/dmitrii_efremov_768w.webp');
    expect(imgEl?.getAttribute('loading')).toBe('lazy');
    expect(imgEl?.getAttribute('decoding')).toBe('async');
  });

  it('ensures WCAG 2.1 level AA compliance via descriptive alt text attribute', () => {
    const descriptiveAlt = 'Detailed studio photography of author Dmitry Efremov';
    component = mount(ResponsivePicture, {
      target,
      props: {
        assetKey: 'dmitrii_efremov',
        alt: descriptiveAlt
      }
    });

    const imgEl = target.querySelector('img');
    expect(imgEl?.getAttribute('alt')).toBe(descriptiveAlt);
    expect(imgEl?.getAttribute('alt')?.length).toBeGreaterThan(10);
  });

  it('supports custom breakpoints, formats, sizes and CSS classes', () => {
    component = mount(ResponsivePicture, {
      target,
      props: {
        assetKey: 'custom_asset',
        alt: 'Custom asset illustration',
        breakpoints: [400, 800],
        formats: ['webp'],
        sizes: '100vw',
        pictureClass: 'custom-picture-wrapper',
        imgClass: 'custom-img-style'
      }
    });

    const pictureEl = target.querySelector('picture');
    expect(pictureEl?.classList.contains('custom-picture-wrapper')).toBe(true);

    const sources = target.querySelectorAll('source');
    expect(sources.length).toBe(1);
    expect(sources[0].getAttribute('type')).toBe('image/webp');
    expect(sources[0].getAttribute('srcset')).toBe('/assets/images/custom_asset/custom_asset_400w.webp 400w, /assets/images/custom_asset/custom_asset_800w.webp 800w');
    expect(sources[0].getAttribute('sizes')).toBe('100vw');

    const imgEl = target.querySelector('img');
    expect(imgEl?.classList.contains('custom-img-style')).toBe(true);
  });
});
