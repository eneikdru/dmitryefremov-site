/**
 * Markdown & Math Formula Parser for Dmitry Efremov's Thought Stream.
 * Converts raw Markdown string containing formulas ($$, $) and blockquotes (>) into visually distinct HTML.
 */

export interface ParsedMarkdown {
  html: string;
  hasFormulas: boolean;
  hasQuotes: boolean;
}

export function parseMarkdown(markdown: string): ParsedMarkdown {
  if (!markdown) {
    return { html: '', hasFormulas: false, hasQuotes: false };
  }

  let hasFormulas = false;
  let hasQuotes = false;

  // Store pre-rendered blocks to avoid inner line processing
  const blockPlaceholders: string[] = [];
  const createPlaceholder = (htmlBlock: string): string => {
    const idx = blockPlaceholders.length;
    blockPlaceholders.push(htmlBlock);
    return `___BLOCK_PLACEHOLDER_${idx}___`;
  };

  // Normalize line endings
  let text = markdown.replace(/\r\n/g, '\n');

  // 1. Process Block Formulas ($$ ... $$)
  text = text.replace(/\$\$\n?([\s\S]*?)\n?\$\$/g, (_, mathContent) => {
    hasFormulas = true;
    const cleanMath = escapeHtml(mathContent.trim());
    const block = `<div class="formula-block bg-[#12151e] border border-[#1f2433] text-[#38bdf8] p-4 rounded-lg my-4 font-mono text-sm overflow-x-auto shadow-inner" role="region" aria-label="Mathematical Formula"><div class="text-xs text-[#94a3b8] font-mono mb-1 select-none">[FORMULA_MATH]</div><div class="font-mono text-base font-semibold tracking-wide">${cleanMath}</div></div>`;
    return createPlaceholder(block);
  });

  // 2. Process Code Blocks (``` ... ```)
  text = text.replace(/```([a-zA-Z0-9]*)\n?([\s\S]*?)\n?```/g, (_, lang, codeContent) => {
    const cleanCode = escapeHtml(codeContent.trim());
    const langBadge = lang ? `<div class="text-xs text-[#64748b] font-mono mb-1 select-none">[CODE_${lang.toUpperCase()}]</div>` : '';
    const block = `<pre class="code-block bg-[#12151e] text-[#e2e8f0] p-4 rounded-lg my-4 border border-[#1f2433] overflow-x-auto font-mono text-sm">${langBadge}<code>${cleanCode}</code></pre>`;
    return createPlaceholder(block);
  });

  // Split into lines for block-level processing (blockquotes, headers, lists, paragraphs)
  const lines = text.split('\n');
  const resultBlocks: string[] = [];
  let inBlockquote = false;
  let blockquoteBuffer: string[] = [];

  const flushBlockquote = () => {
    if (inBlockquote && blockquoteBuffer.length > 0) {
      hasQuotes = true;
      const quoteText = blockquoteBuffer.map(l => parseInline(l)).join('<br />');
      resultBlocks.push(
        `<blockquote class="article-blockquote border-l-4 border-[#38bdf8] bg-[#12151e]/60 text-[#cbd5e1] italic my-4 p-4 rounded-r-lg shadow-sm"><p class="m-0">${quoteText}</p></blockquote>`
      );
      blockquoteBuffer = [];
      inBlockquote = false;
    }
  };

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];

    // Placeholder check
    if (line.includes('___BLOCK_PLACEHOLDER_')) {
      flushBlockquote();
      resultBlocks.push(line);
      continue;
    }

    // Blockquote (line starting with >)
    if (line.trim().startsWith('>')) {
      inBlockquote = true;
      blockquoteBuffer.push(line.trim().replace(/^>\s?/, ''));
      continue;
    } else {
      flushBlockquote();
    }

    // Headers
    if (line.startsWith('### ')) {
      resultBlocks.push(`<h3 class="text-lg font-bold text-white mt-6 mb-2 tracking-tight">${parseInline(line.slice(4))}</h3>`);
      continue;
    }
    if (line.startsWith('## ')) {
      resultBlocks.push(`<h2 class="text-xl font-bold text-white mt-8 mb-3 tracking-tight border-b border-[#1f2433] pb-1">${parseInline(line.slice(3))}</h2>`);
      continue;
    }
    if (line.startsWith('# ')) {
      resultBlocks.push(`<h1 class="text-2xl font-extrabold text-white mt-10 mb-4 tracking-tight">${parseInline(line.slice(2))}</h1>`);
      continue;
    }

    // Empty line
    if (!line.trim()) {
      continue;
    }

    // Unordered List item
    if (line.trim().startsWith('- ') || line.trim().startsWith('* ')) {
      const listContent = parseInline(line.trim().slice(2));
      resultBlocks.push(`<ul class="list-disc list-inside my-2 space-y-1 text-[#cbd5e1]"><li class="ml-2">${listContent}</li></ul>`);
      continue;
    }

    // Standard Paragraph
    const inlineParsed = parseInline(line);
    if (inlineParsed.trim()) {
      if (inlineParsed.includes('class="formula-inline')) {
        hasFormulas = true;
      }
      resultBlocks.push(`<p class="my-3 leading-relaxed text-[#cbd5e1] font-serif text-base">${inlineParsed}</p>`);
    }
  }

  flushBlockquote();

  // Restore placeholders
  let html = resultBlocks.join('\n');
  blockPlaceholders.forEach((blockHtml, idx) => {
    html = html.replace(`___BLOCK_PLACEHOLDER_${idx}___`, blockHtml);
  });

  return {
    html,
    hasFormulas,
    hasQuotes
  };
}

function parseInline(str: string): string {
  let res = escapeHtml(str);

  // Inline Math $ ... $
  res = res.replace(/\$([^$]+)\$/g, (_, mathStr) => {
    return `<code class="formula-inline bg-[#12151e] text-[#38bdf8] px-1.5 py-0.5 rounded font-mono text-sm border border-[#1f2433]">${mathStr}</code>`;
  });

  // Bold **...**
  res = res.replace(/\*\*([^*]+)\*\*/g, '<strong class="font-bold text-white">$1</strong>');

  // Italic *...*
  res = res.replace(/\*([^*]+)\*/g, '<em class="italic text-[#e2e8f0]">$1</em>');

  // Inline code `...`
  res = res.replace(/`([^`]+)`/g, '<code class="bg-[#12151e] text-[#a7f3d0] px-1 py-0.5 rounded font-mono text-xs border border-[#1f2433]">$1</code>');

  return res;
}

function escapeHtml(str: string): string {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}
