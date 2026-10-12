<script lang="ts">
  /**
   * ResponsivePicture Svelte Component
   * Supports AVIF and WebP modern image formats with adaptive media queries (mobile vs desktop scales)
   * and a fallback fallback img element.
   * Ensures WCAG 2.1 level AA compliance via mandatory descriptive alt text.
   */
  export let srcBase: string; // Base URL/path without extension (e.g. '/images/dmitry-efremov-hero')
  export let alt: string; // Descriptive alt text (mandatory for WCAG compliance)
  export let className: string = ''; // Custom CSS classes
  export let width: number | string | undefined = undefined;
  export let height: number | string | undefined = undefined;
  export let loading: 'lazy' | 'eager' = 'lazy';
  export let decoding: 'async' | 'auto' | 'sync' = 'async';

  $: avifMobile = `${srcBase}-mobile.avif`;
  $: webpMobile = `${srcBase}-mobile.webp`;
  $: avifDesktop = `${srcBase}-desktop.avif`;
  $: webpDesktop = `${srcBase}-desktop.webp`;
  $: fallbackSrc = `${srcBase}-desktop.jpg`;
</script>

<picture class="responsive-picture {className}">
  <!-- Mobile AVIF variant (max-width: 640px) -->
  <source srcset={avifMobile} type="image/avif" media="(max-width: 640px)" />
  <!-- Mobile WebP variant (max-width: 640px) -->
  <source srcset={webpMobile} type="image/webp" media="(max-width: 640px)" />

  <!-- Desktop AVIF variant (min-width: 641px) -->
  <source srcset={avifDesktop} type="image/avif" media="(min-width: 641px)" />
  <!-- Desktop WebP variant (min-width: 641px) -->
  <source srcset={webpDesktop} type="image/webp" media="(min-width: 641px)" />

  <!-- Fallback Image for legacy browsers / default rendering -->
  <img
    src={fallbackSrc}
    {alt}
    {width}
    {height}
    {loading}
    {decoding}
    class="responsive-picture-img w-full h-auto object-cover rounded-lg"
  />
</picture>
