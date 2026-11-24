#!/usr/bin/env node
const fs = require('fs');
const path = require('path');

const SITE_REPO = process.env.SITE_REPO ? path.resolve(process.env.SITE_REPO) : path.resolve(__dirname, '..');
const DIST_DIR = path.join(SITE_REPO, 'frontend', 'dist');
const BASE_INDEX = path.join(DIST_DIR, 'index.html');
const LOC_FILE = path.join(SITE_REPO, 'frontend', 'src', 'lib', 'generated_localizations.json');
const BASE_URL = 'https://dmitryefremov.com';
const LOCALES = ['ru', 'en', 'ka'];

function cleanHtml(text) {
  if (!text) return '';
  return text.replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\n/g, ' ');
}

function main() {
  if (!fs.existsSync(BASE_INDEX)) {
    console.error(`ERROR: ${BASE_INDEX} does not exist. Run npm run build first.`);
    process.exit(1);
  }

  const baseHtml = fs.readFileSync(BASE_INDEX, 'utf-8');
  const locData = JSON.parse(fs.readFileSync(LOC_FILE, 'utf-8')).content || {};
  const essays = locData.essays || {};
  const podcasts = locData.podcasts || {};

  // 1. Generate 404.html
  let html404 = baseHtml;
  html404 = html404.replace(/<title>.*?<\/title>/, '<title>404: Страница не найдена | Дмитрий Ефремов</title>');
  const html404Body = `
    <div style="min-height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #090a0f; color: #f8fafc; font-family: monospace; text-align: center; padding: 20px;">
        <div style="display: inline-block; padding: 4px 12px; background: rgba(239, 68, 68, 0.1); border: 1px solid rgba(239, 68, 68, 0.3); color: #f87171; border-radius: 4px; font-size: 14px; margin-bottom: 16px;">
            HTTP 404 • NOT FOUND
        </div>
        <h1 style="font-size: 32px; font-weight: 800; margin: 0 0 12px 0; color: #fff;">МАТЕРИАЛ НЕ НАЙДЕН</h1>
        <p style="color: #94a3b8; max-width: 480px; font-size: 14px; line-height: 1.6; margin-bottom: 24px;">
            Запрошенный адрес отсутствует в публичном реестре публикаций или был перемещен.
        </p>
        <a href="/" style="color: #fbbf24; text-decoration: none; font-size: 13px; border: 1px solid rgba(251, 191, 36, 0.3); padding: 8px 16px; border-radius: 4px; transition: background 0.2s;">
            ← Вернуться на главную
        </a>
    </div>
  `;
  html404 = html404.replace(/<div id="app">.*?<\/div>/s, `<div id="app">${html404Body}</div>`);
  fs.writeFileSync(path.join(DIST_DIR, '404.html'), html404, 'utf-8');
  console.log('✓ Generated dist/404.html');

  const routes = [];

  // Root & Locales
  routes.push({
    path: '',
    locale: 'ru',
    title: 'Дмитрий Ефремов — Архитектура систем, логика мышления и автономные процессы',
    desc: 'Суверенный интеллектуальный узел Дмитрия Ефремова. Выпуски подкаста «Системы и реальность», онтология автономии, архитектура агентских систем и деонтическая логика.',
    marker: 'root:home',
    rel_ru: '/ru',
    rel_en: '/en',
    rel_ka: '/ka',
    x_default: '/'
  });

  const localeRoots = [
    { loc: 'ru', title: 'Дмитрий Ефремов — Архитектура систем, логика мышления и автономные процессы', desc: 'Суверенный интеллектуальный узел Дмитрия Ефремова. Выпуски подкаста «Системы и реальность».' },
    { loc: 'en', title: 'Dmitry Efremov — Systems Architecture, Logic of Thought, and Autonomous Processes', desc: 'Sovereign intellectual node of Dmitry Efremov. \'Systems and Reality\' podcast episodes, ontology of autonomy, multi-agent systems, and deontic logic.' },
    { loc: 'ka', title: 'დიმიტრი ეფრემოვი — სისტემების არქიტექტურა, აზროვნების ლოგიკა და ავტონომიური პროცესები', desc: 'დიმიტრი ეფრემოვის სუვერენული ინტელექტუალური კვანძი. პოდკასტის «სისტემები და რეალობა» გამოშვებები, ავტონომიის ონტოლოგია.' }
  ];

  for (const lr of localeRoots) {
    routes.push({
      path: lr.loc,
      locale: lr.loc,
      title: lr.title,
      desc: lr.desc,
      marker: `root:${lr.loc}`,
      rel_ru: '/ru',
      rel_en: '/en',
      rel_ka: '/ka',
      x_default: '/'
    });
  }

  // Tabs
  const tabNames = ['essays', 'podcasts', 'podcast', 'canon', 'canon-gateway', 'sparks', 'contact', 'support', 'telemetry-hud'];
  for (const tab of tabNames) {
    routes.push({
      path: tab,
      locale: 'ru',
      title: `${tab.charAt(0).toUpperCase() + tab.slice(1)} | Дмитрий Ефремов`,
      desc: 'Материалы Дмитрия Ефремова: архитектура систем, логика мышления и автономные процессы.',
      marker: `tab:${tab}`,
      rel_ru: `/ru/${tab}`,
      rel_en: `/en/${tab}`,
      rel_ka: `/ka/${tab}`,
      x_default: `/${tab}`
    });
    for (const loc of LOCALES) {
      let tTitle = `${tab.charAt(0).toUpperCase() + tab.slice(1)} | Дмитрий Ефремов`;
      let tDesc = 'Материалы Дмитрия Ефремова: архитектура систем, логика мышления.';
      if (loc === 'en') {
        tTitle = `${tab.charAt(0).toUpperCase() + tab.slice(1)} | Dmitry Efremov`;
        tDesc = 'Dmitry Efremov systems architecture, logic of thought, and autonomous systems.';
      } else if (loc === 'ka') {
        tTitle = `${tab.charAt(0).toUpperCase() + tab.slice(1)} | დიმიტრი ეფრემოვი`;
        tDesc = 'დიმიტრი ეფრემოვის სისტემური არქიტექტურა და კვლევები.';
      }
      routes.push({
        path: `${loc}/${tab}`,
        locale: loc,
        title: tTitle,
        desc: tDesc,
        marker: `tab:${loc}:${tab}`,
        rel_ru: `/ru/${tab}`,
        rel_en: `/en/${tab}`,
        rel_ka: `/ka/${tab}`,
        x_default: `/${tab}`
      });
    }
  }

  // Essays
  for (const [slug, item] of Object.entries(essays)) {
    const tr = item.translations || {};
    const ruTitle = cleanHtml(tr.ru?.title || slug);
    const enTitle = cleanHtml(tr.en?.title || ruTitle);
    const kaTitle = cleanHtml(tr.ka?.title || ruTitle);

    const ruDesc = cleanHtml(tr.ru?.summary || ruTitle);
    const enDesc = cleanHtml(tr.en?.summary || enTitle);
    const kaDesc = cleanHtml(tr.ka?.summary || kaTitle);

    // Default route
    routes.push({
      path: `essays/${slug}`,
      locale: 'ru',
      title: `${ruTitle} | Дмитрий Ефремов`,
      desc: ruDesc,
      marker: `essay:${slug}`,
      rel_ru: `/ru/essays/${slug}`,
      rel_en: `/en/essays/${slug}`,
      rel_ka: `/ka/essays/${slug}`,
      x_default: `/essays/${slug}`
    });
    routes.push({
      path: `article/${slug}`,
      locale: 'ru',
      title: `${ruTitle} | Дмитрий Ефремов`,
      desc: ruDesc,
      marker: `essay:${slug}`,
      rel_ru: `/ru/essays/${slug}`,
      rel_en: `/en/essays/${slug}`,
      rel_ka: `/ka/essays/${slug}`,
      x_default: `/essays/${slug}`
    });

    for (const loc of LOCALES) {
      const locTitle = loc === 'en' ? enTitle : (loc === 'ka' ? kaTitle : ruTitle);
      const locDesc = loc === 'en' ? enDesc : (loc === 'ka' ? kaDesc : ruDesc);
      const suffix = loc === 'en' ? 'Dmitry Efremov' : (loc === 'ka' ? 'დიმიტრი ეფრემოვი' : 'Дмитрий Ефремов');
      routes.push({
        path: `${loc}/essays/${slug}`,
        locale: loc,
        title: `${locTitle} | ${suffix}`,
        desc: locDesc,
        marker: `essay:${loc}:${slug}`,
        rel_ru: `/ru/essays/${slug}`,
        rel_en: `/en/essays/${slug}`,
        rel_ka: `/ka/essays/${slug}`,
        x_default: `/essays/${slug}`
      });
    }
  }

  // Podcasts
  for (const [slug, item] of Object.entries(podcasts)) {
    const tr = item.translations || {};
    const ruTitle = cleanHtml(tr.ru?.title || slug);
    const enTitle = cleanHtml(tr.en?.title || ruTitle);
    const kaTitle = cleanHtml(tr.ka?.title || ruTitle);

    const ruDesc = cleanHtml(tr.ru?.thesis || tr.ru?.description || ruTitle);
    const enDesc = cleanHtml(tr.en?.thesis || tr.en?.description || enTitle);
    const kaDesc = cleanHtml(tr.ka?.thesis || tr.ka?.description || kaTitle);

    for (const prefix of ['podcast', 'podcasts']) {
      routes.push({
        path: `${prefix}/${slug}`,
        locale: 'ru',
        title: `${ruTitle} | Дмитрий Ефремов`,
        desc: ruDesc,
        marker: `podcast:${slug}`,
        rel_ru: `/ru/podcasts/${slug}`,
        rel_en: `/en/podcasts/${slug}`,
        rel_ka: `/ka/podcasts/${slug}`,
        x_default: `/podcasts/${slug}`
      });
      for (const loc of LOCALES) {
        const locTitle = loc === 'en' ? enTitle : (loc === 'ka' ? kaTitle : ruTitle);
        const locDesc = loc === 'en' ? enDesc : (loc === 'ka' ? kaDesc : ruDesc);
        const suffix = loc === 'en' ? 'Dmitry Efremov' : (loc === 'ka' ? 'დიმიტრი ეფრემოვი' : 'Дмитрий Ефремов');
        routes.push({
          path: `${loc}/${prefix}/${slug}`,
          locale: loc,
          title: `${locTitle} | ${suffix}`,
          desc: locDesc,
          marker: `podcast:${loc}:${slug}`,
          rel_ru: `/ru/podcasts/${slug}`,
          rel_en: `/en/podcasts/${slug}`,
          rel_ka: `/ka/podcasts/${slug}`,
          x_default: `/podcasts/${slug}`
        });
      }
    }
  }

  // Smart companies
  const scSlug = 'smart-companies-foolish-speeches';
  routes.push({
    path: scSlug,
    locale: 'ru',
    title: 'Умные компании, глупые речи | Дмитрий Ефремов',
    desc: 'Как корпоративный язык скрывает отсутствие мысли и разрушает обратную связь.',
    marker: `essay:${scSlug}`,
    rel_ru: `/ru/${scSlug}`,
    rel_en: `/en/${scSlug}`,
    rel_ka: `/ka/${scSlug}`,
    x_default: `/${scSlug}`
  });
  for (const loc of LOCALES) {
    const scTitle = loc === 'en' ? 'Smart Companies, Foolish Speeches | Dmitry Efremov' : (loc === 'ka' ? 'ჭკვიანი კომპანიები, სულელური სიტყვები | დიმიტრი ეფრემოვი' : 'Умные компании, глупые речи | Дмитрий Ефремов');
    const scDesc = loc === 'en' ? 'Corporate language analysis.' : 'კორპორაციული ენის ანალიზი.';
    routes.push({
      path: `${loc}/${scSlug}`,
      locale: loc,
      title: scTitle,
      desc: scDesc,
      marker: `essay:${loc}:${scSlug}`,
      rel_ru: `/ru/${scSlug}`,
      rel_en: `/en/${scSlug}`,
      rel_ka: `/ka/${scSlug}`,
      x_default: `/${scSlug}`
    });
  }

  console.log(`Writing ${routes.length} prerendered routes...`);
  for (const r of routes) {
    const targetDir = r.path ? path.join(DIST_DIR, r.path) : DIST_DIR;
    fs.mkdirSync(targetDir, { recursive: true });
    const targetFile = path.join(targetDir, 'index.html');

    const curUrl = r.path ? `${BASE_URL}/${r.path}` : BASE_URL;
    const ruUrl = `${BASE_URL}${r.rel_ru}`;
    const enUrl = `${BASE_URL}${r.rel_en}`;
    const kaUrl = `${BASE_URL}${r.rel_ka}`;
    const xUrl = `${BASE_URL}${r.x_default}`;
    const ogLocale = r.locale === 'ru' ? 'ru_RU' : (r.locale === 'ka' ? 'ka_GE' : 'en_US');

    const seoBlock = `
    <title>${r.title}</title>
    <meta name="description" content="${r.desc}" />
    <meta name="dmitryefremov-material" content="${r.marker}" />
    <link rel="canonical" href="${curUrl}" />
    <link rel="alternate" hreflang="ru" href="${ruUrl}" />
    <link rel="alternate" hreflang="en" href="${enUrl}" />
    <link rel="alternate" hreflang="ka" href="${kaUrl}" />
    <link rel="alternate" hreflang="x-default" href="${xUrl}" />
    <meta property="og:title" content="${r.title}" />
    <meta property="og:description" content="${r.desc}" />
    <meta property="og:url" content="${curUrl}" />
    <meta property="og:locale" content="${ogLocale}" />
    `.trim();

    let modified = baseHtml;
    modified = modified.replace(/<title>.*?<\/title>/, '');
    modified = modified.replace('</head>', `${seoBlock}\n  </head>`);
    modified = modified.replace(/<html lang="[^"]*"/, `<html lang="${r.locale}"`);

    const markerDiv = `<div id="prerender-marker" data-material="${r.marker}" data-locale="${r.locale}" style="display:none"></div>`;
    modified = modified.replace('<div id="app"></div>', `<div id="app">${markerDiv}</div>`);

    fs.writeFileSync(targetFile, modified, 'utf-8');
  }

  console.log(`✓ All ${routes.length} prerendered routes successfully written to ${DIST_DIR}`);
}

main();
