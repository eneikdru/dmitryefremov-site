<script lang="ts">
  export let assetKey: string;
  export let alt: string;
  export let breakpoints: number[] = [375, 768, 1440];
  export let formats: string[] = ['avif', 'webp'];
  export let basePath: string = '/assets/images';
  export let fallbackFormat: string = 'webp';
  export let fallbackBreakpoint: number = 768;
  export let sizes: string = '(max-width: 640px) 375px, (max-width: 1024px) 768px, 1440px';
  export let className: string = '';
  export let imgClass: string = 'w-full h-auto object-cover rounded-md';
  export let width: number | string | undefined = undefined;
  export let height: number | string | undefined = undefined;
  export let loading: 'lazy' | 'eager' = 'lazy';
  export let decoding: 'async' | 'sync' | 'auto' = 'async';
  export let id: string | undefined = undefined;

  // Build srcset string for a given format
  function buildSrcset(fmt: string): string {
    const sortedBps = [...breakpoints].sort((a, b) => a - b);
    return sortedBps
      .map(bp => `${basePath}/${assetKey}/${assetKey}_${bp}w.${fmt.toLowerCase()} ${bp}w`)
      .join(', ');
  }

  // Get MIME type for format
  function getMimeType(fmt: string): string {
    const lower = fmt.toLowerCase();
    if (lower === 'avif') return 'image/avif';
    if (lower === 'webp') return 'image/webp';
    if (lower === 'png') return 'image/png';
    if (lower === 'jpg' || lower === 'jpeg') return 'image/jpeg';
    return `image/${lower}`;
  }

  $: fallbackSrc = `${basePath}/${assetKey}/${assetKey}_${fallbackBreakpoint}w.${fallbackFormat.toLowerCase()}`;
</script>

<picture class="responsive-picture {className}">
  {#each formats as fmt}
    <source
      type={getMimeType(fmt)}
      srcset={buildSrcset(fmt)}
      {sizes}
    />
  {/each}
  <img
    {id}
    src={fallbackSrc}
    {alt}
    {loading}
    {decoding}
    {width}
    {height}
    class={imgClass}
  />
</picture>
