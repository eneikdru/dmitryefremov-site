<script lang="ts">
  import { onMount } from 'svelte';
  import { updateHeadMeta, type SeoMetaProps } from '../lib/seo';

  export let title: string = 'Dmitry Efremov - Telemetry Implementation';
  export let description: string = 'Autonomous Systems, Deontic Logic, and High-Traffic Machine Layer Architecture';
  export let url: string = 'https://dmitryefremov.com';
  export let type: 'article' | 'website' = 'website';
  export let imageUrl: string = 'https://dmitryefremov.com/assets/og-cover.png';
  export let imageAlt: string = 'Dmitry Efremov Autonomous Process Architecture';
  export let publishedAt: string | undefined = undefined;
  export let authorName: string = 'Dmitry Efremov';
  export let keywords: string[] = ['Deontic Logic', 'Autonomous Systems', 'AI Crawlers', 'GPTBot', 'PerplexityBot'];
  export let locale: string = 'en_US';
  export let siteName: string = 'Dmitry Efremov Terminal';
  export let gptBotAllowed: boolean = true;
  export let perplexityBotAllowed: boolean = true;
  export let claudeBotAllowed: boolean = true;

  $: config = {
    title,
    description,
    url,
    type,
    imageUrl,
    imageAlt,
    publishedAt,
    authorName,
    keywords,
    locale,
    siteName,
    aiCrawlerPolicy: {
      gptBotAllowed,
      perplexityBotAllowed,
      claudeBotAllowed
    }
  } satisfies SeoMetaProps;

  $: if (typeof document !== 'undefined') {
    updateHeadMeta(config);
  }

  onMount(() => {
    updateHeadMeta(config);
  });
</script>

<svelte:head>
  <title>{title}</title>
  <meta name="description" content={description} />
  <meta name="robots" content="index, follow, max-snippet:-1, max-image-preview:large" />
  <meta name="googlebot" content="index, follow, max-snippet:-1, max-image-preview:large" />
  <meta name="GPTBot" content={gptBotAllowed ? 'index, follow, max-snippet:-1' : 'noindex'} />
  <meta name="PerplexityBot" content={perplexityBotAllowed ? 'index, follow, max-snippet:-1' : 'noindex'} />
  <meta name="ClaudeBot" content={claudeBotAllowed ? 'index, follow, max-snippet:-1' : 'noindex'} />

  <!-- Open Graph -->
  <meta property="og:title" content={title} />
  <meta property="og:description" content={description} />
  <meta property="og:type" content={type} />
  <meta property="og:url" content={url} />
  <meta property="og:image" content={imageUrl} />
  {#if imageAlt}
    <meta property="og:image:alt" content={imageAlt} />
  {/if}
  <meta property="og:locale" content={locale} />
  <meta property="og:site_name" content={siteName} />

  <!-- Twitter -->
  <meta name="twitter:card" content="summary_large_image" />
  <meta name="twitter:title" content={title} />
  <meta name="twitter:description" content={description} />
  <meta name="twitter:image" content={imageUrl} />
  {#if imageAlt}
    <meta name="twitter:image:alt" content={imageAlt} />
  {/if}
</svelte:head>
