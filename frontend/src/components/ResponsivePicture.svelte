<script lang="ts">
  export let assetKey: string = 'dmitrii_efremov';
  export let alt: string;
  export let baseUrl: string = '/assets/images';
  export let breakpoints: number[] = [375, 768, 1440];
  export let formats: string[] = ['avif', 'webp'];
  export let sizes: string = '(max-width: 640px) 375px, (max-width: 1024px) 768px, 1440px';
  export let fallbackBreakpoint: number = 768;
  export let fallbackFormat: string = 'webp';
  export let loading: 'lazy' | 'eager' = 'lazy';
  export let decoding: 'async' | 'auto' | 'sync' = 'async';
  export let width: number | string | undefined = undefined;
  export let height: number | string | undefined = undefined;
  export let pictureClass: string = '';
  export let imgClass: string = '';
  export let id: string | undefined = undefined;

  function buildSrcset(fmt: string): string {
    const sortedBps = [...breakpoints].sort((a, b) => a - b);
    return sortedBps
      .map(bp => `${baseUrl}/${assetKey}/${assetKey}_${bp}w.${fmt.toLowerCase()} ${bp}w`)
      .join(', ');
  }

  $: sortedFormats = [...formats].map(f => f.toLowerCase());
  $: fallbackSrc = `${baseUrl}/${assetKey}/${assetKey}_${fallbackBreakpoint}w.${fallbackFormat.toLowerCase()}`;
</script>

<picture class={pictureClass}>
  {#each sortedFormats as fmt}
    <source
      type="image/{fmt === 'jpg' ? 'jpeg' : fmt}"
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
