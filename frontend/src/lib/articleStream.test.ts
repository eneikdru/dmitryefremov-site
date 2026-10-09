// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import { parseMarkdown } from './markdown';
import ArticleStreamView from '../components/ArticleStreamView.svelte';
import { mount, unmount } from 'svelte';

describe('Markdown & Formula Parser (parseMarkdown)', () => {
  it('converts block formulas ($$) to visually distinct formula blocks', () => {
    const raw = 'Header\n$$\n\\mathcal{O}(\\varphi) \\iff \\neg \\mathcal{P}(\\neg \\varphi)\n$$';
    const result = parseMarkdown(raw);

    expect(result.hasFormulas).toBe(true);
    expect(result.html).toContain('class="formula-block');
    expect(result.html).toContain('[FORMULA_MATH]');
    expect(result.html).toContain('\\mathcal{O}(\\varphi)');
  });

  it('converts inline formulas ($) to inline code elements', () => {
    const raw = 'Formula $x + y = z$ is inline.';
    const result = parseMarkdown(raw);

    expect(result.hasFormulas).toBe(true);
    expect(result.html).toContain('class="formula-inline');
    expect(result.html).toContain('x + y = z');
  });

  it('converts blockquotes (>) to styled blockquote HTML elements', () => {
    const raw = '> "Your code and your decisions are who you truly are."';
    const result = parseMarkdown(raw);

    expect(result.hasQuotes).toBe(true);
    expect(result.html).toContain('class="article-blockquote');
    expect(result.html).toContain('Your code and your decisions');
  });

  it('handles empty or plain text correctly', () => {
    expect(parseMarkdown('')).toEqual({ html: '', hasFormulas: false, hasQuotes: false });

    const plain = 'Simple paragraph text';
    const result = parseMarkdown(plain);
    expect(result.hasFormulas).toBe(false);
    expect(result.hasQuotes).toBe(false);
    expect(result.html).toContain('<p class="my-3 leading-relaxed text-[#cbd5e1] font-serif text-base">Simple paragraph text</p>');
  });

  it('renders complex Markdown containing headers, quotes, formulas, code blocks, and lists accurately', () => {
    const complexMarkdown = `# Main Title Header

## Section Subtitle

### Subsection Title

> "Your code and your decisions are who you truly are."

Inline math $E = mc^2$ and block math formula:
$$
\\mathcal{O}(\\varphi) \\iff \\neg \\mathcal{P}(\\neg \\varphi)
$$

\`\`\`typescript
const status = "VERIFIED";
\`\`\`

- First list item
- Second list item`;

    const result = parseMarkdown(complexMarkdown);

    expect(result.hasFormulas).toBe(true);
    expect(result.hasQuotes).toBe(true);
    expect(result.html).toContain('<h1 class="text-2xl font-extrabold text-white mt-10 mb-4 tracking-tight">Main Title Header</h1>');
    expect(result.html).toContain('<h2 class="text-xl font-bold text-white mt-8 mb-3 tracking-tight border-b border-[#1f2433] pb-1">Section Subtitle</h2>');
    expect(result.html).toContain('<h3 class="text-lg font-bold text-white mt-6 mb-2 tracking-tight">Subsection Title</h3>');
    expect(result.html).toContain('class="article-blockquote');
    expect(result.html).toContain('Your code and your decisions are who you truly are.');
    expect(result.html).toContain('class="formula-inline');
    expect(result.html).toContain('E = mc^2');
    expect(result.html).toContain('class="formula-block');
    expect(result.html).toContain('\\mathcal{O}(\\varphi)');
    expect(result.html).toContain('class="code-block');
    expect(result.html).toContain('const status = &quot;VERIFIED&quot;;');
    expect(result.html).toContain('<ul class="list-disc list-inside my-2 space-y-1 text-[#cbd5e1]"><li class="ml-2">First list item</li></ul>');
  });
});

describe('ArticleStreamView Component', () => {
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

  it('renders list of available longreads and active article reader', () => {
    component = mount(ArticleStreamView, { target });

    expect(target.textContent).toContain('Articles & Thought Stream');
    expect(target.textContent).toContain('AVAILABLE_LONGREADS');
    expect(target.textContent).toContain('Deontic Logic in Autonomous Process Architecture');
    expect(target.textContent).toContain('Theory of Constraints in Distributed Pipeline Throughput');
    expect(target.textContent).toContain('Deterministic State Determinism in Autonomous Loops');
  });

  it('renders headers, quotes, and formulas as visually distinct HTML elements in active article view', () => {
    component = mount(ArticleStreamView, { target });

    const h1 = target.querySelector('#article-header h1');
    expect(h1).not.toBeNull();
    expect(h1?.textContent).toBe('Deontic Logic in Autonomous Process Architecture');

    const articleContainer = target.querySelector('#article-scroll-container');
    expect(articleContainer).not.toBeNull();

    const h2 = articleContainer?.querySelector('h2');
    expect(h2).not.toBeNull();
    expect(h2?.textContent).toBe('Mathematical Formulation of Normative Invariants');

    const formulaBlock = articleContainer?.querySelector('.formula-block');
    expect(formulaBlock).not.toBeNull();
    expect(formulaBlock?.textContent).toContain('[FORMULA_MATH]');

    const formulaInline = articleContainer?.querySelector('.formula-inline');
    expect(formulaInline).not.toBeNull();

    const quoteBlock = articleContainer?.querySelector('.article-blockquote');
    expect(quoteBlock).not.toBeNull();
    expect(quoteBlock?.textContent).toContain('Your code and your decisions are who you truly are.');
  });

  it('filters articles when topic filter button is clicked', async () => {
    component = mount(ArticleStreamView, { target });

    const buttons = target.querySelectorAll('button');
    const tocBtn = Array.from(buttons).find(b => b.textContent?.includes('Theory of Constraints'));
    expect(tocBtn).toBeDefined();

    tocBtn?.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('AVAILABLE_LONGREADS (1)');
    expect(target.textContent).toContain('Theory of Constraints in Distributed Pipeline Throughput');
  });
});
