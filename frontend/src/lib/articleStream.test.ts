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

  it('renders formulas and blockquotes as visually distinct HTML elements in active article', () => {
    component = mount(ArticleStreamView, { target });

    const formulaBlock = target.querySelector('.formula-block');
    expect(formulaBlock).not.toBeNull();
    expect(formulaBlock?.textContent).toContain('[FORMULA_MATH]');

    const quoteBlock = target.querySelector('.article-blockquote');
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
