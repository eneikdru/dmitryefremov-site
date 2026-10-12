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

  it('renders a picture element with appropriate AVIF and WebP source variants and fallback img', () => {
    component = mount(ResponsivePicture, {
      target,
      props: {
        srcBase: '/images/dmitry-efremov-hero',
        alt: 'Portrait of Dmitry Efremov, Systems Architect and Content Creator',
        className: 'custom-hero-pic',
        width: 128,
        height: 128
      }
    });

    const pictureEl = target.querySelector('picture');
    expect(pictureEl).not.toBeNull();
    expect(pictureEl?.classList.contains('custom-hero-pic')).toBe(true);

    const sources = target.querySelectorAll('source');
    expect(sources.length).toBe(4);

    // Mobile sources (max-width: 640px)
    expect(sources[0].getAttribute('srcset')).toBe('/images/dmitry-efremov-hero-mobile.avif');
    expect(sources[0].getAttribute('type')).toBe('image/avif');
    expect(sources[0].getAttribute('media')).toBe('(max-width: 640px)');

    expect(sources[1].getAttribute('srcset')).toBe('/images/dmitry-efremov-hero-mobile.webp');
    expect(sources[1].getAttribute('type')).toBe('image/webp');
    expect(sources[1].getAttribute('media')).toBe('(max-width: 640px)');

    // Desktop sources (min-width: 641px)
    expect(sources[2].getAttribute('srcset')).toBe('/images/dmitry-efremov-hero-desktop.avif');
    expect(sources[2].getAttribute('type')).toBe('image/avif');
    expect(sources[2].getAttribute('media')).toBe('(min-width: 641px)');

    expect(sources[3].getAttribute('srcset')).toBe('/images/dmitry-efremov-hero-desktop.webp');
    expect(sources[3].getAttribute('type')).toBe('image/webp');
    expect(sources[3].getAttribute('media')).toBe('(min-width: 641px)');

    // Fallback img element
    const imgEl = target.querySelector('img');
    expect(imgEl).not.toBeNull();
    expect(imgEl?.getAttribute('src')).toBe('/images/dmitry-efremov-hero-desktop.jpg');
    expect(imgEl?.getAttribute('alt')).toBe('Portrait of Dmitry Efremov, Systems Architect and Content Creator');
    expect(imgEl?.getAttribute('width')).toBe('128');
    expect(imgEl?.getAttribute('height')).toBe('128');
  });

  it('enforces WCAG 2.1 level AA compliance with descriptive alt text', () => {
    const descriptiveAlt = 'Studio photograph of author Dmitry Efremov wearing dark attire';
    component = mount(ResponsivePicture, {
      target,
      props: {
        srcBase: '/images/sample',
        alt: descriptiveAlt
      }
    });

    const imgEl = target.querySelector('img');
    expect(imgEl?.getAttribute('alt')).toBe(descriptiveAlt);
    expect(imgEl?.getAttribute('alt')).not.toBe('');
  });
});
