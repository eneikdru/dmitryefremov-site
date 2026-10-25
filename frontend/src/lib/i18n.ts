import { writable } from 'svelte/store';

export type Language = 'ru' | 'en' | 'ka';

function getInitialLanguage(): Language {
  if (typeof window !== 'undefined') {
    const saved = localStorage.getItem('site_language') as Language;
    if (saved && (saved === 'ru' || saved === 'en' || saved === 'ka')) {
      return saved;
    }
  }
  return 'ru';
}

export const currentLanguage = writable<Language>(getInitialLanguage());

export function setLanguage(lang: Language) {
  currentLanguage.set(lang);
  if (typeof window !== 'undefined') {
    localStorage.setItem('site_language', lang);
  }
}

export interface Translations {
  // Navigation & Topbar
  nav_podcast: string;
  nav_article: string;
  nav_sparks: string;
  nav_contact: string;
  nav_canon: string;
  search_btn: string;
  search_cmd_k: string;
  theme_dark: string;
  theme_light: string;
  topbar_architecture: string;

  // Hero Section
  hero_name: string;
  hero_role: string;
  hero_badge: string;
  hero_tagline: string;
  hero_bio_1: string;
  hero_bio_2: string;
  hero_manifest: string;
  hero_stat_releases: string;
  hero_stat_invariants: string;
  hero_stat_compliance: string;
  hero_btn_podcast: string;
  hero_btn_contact: string;

  // Catalog & Media
  catalog_title: string;
  catalog_badge: string;
  catalog_active: string;
  catalog_listen: string;

  // Articles & Topics
  articles_title: string;
  articles_all_topics: string;
  articles_deontic: string;
  articles_toc: string;
  articles_autonomous: string;
  articles_systems: string;

  // Contact
  contact_title: string;
  contact_subtitle: string;
  contact_name_label: string;
  contact_name_placeholder: string;
  contact_email_label: string;
  contact_email_placeholder: string;
  contact_message_label: string;
  contact_message_placeholder: string;
  contact_submit_btn: string;
  contact_submitting: string;
  contact_success: string;
  contact_error: string;

  // Search Agent
  search_title: string;
  search_esc: string;
  search_placeholder: string;
  search_clear: string;
  search_examples_label: string;
  search_synthesis_label: string;
  search_agent_default_thought: string;
  search_agent_results_thought: (count: number, query: string, topTitle: string) => string;
  search_agent_not_found: (query: string) => string;
  search_empty_title: string;
  search_empty_subtitle: string;
  search_navigate_to: string;
  search_relevance: string;
  search_footer: string;

  // Footer & Legal
  footer_rights: string;
  footer_privacy: string;
  footer_imprint: string;
  imprint_title: string;
  imprint_owner_label: string;
  imprint_owner_val: string;
  imprint_domain_label: string;
  imprint_email_label: string;
  imprint_privacy_label: string;
  imprint_privacy_val: string;
  imprint_copyright_label: string;
  imprint_copyright_val: string;
}

export const DICTIONARY: Record<Language, Translations> = {
  ru: {
    nav_podcast: 'Подкаст',
    nav_article: 'Эссе и статьи',
    nav_sparks: 'Sparks',
    nav_contact: 'Контакты',
    nav_canon: 'База знаний & API',
    search_btn: 'ПОИСК',
    search_cmd_k: 'Cmd+K',
    theme_dark: 'Тёмная',
    theme_light: 'Светлая',
    topbar_architecture: 'АРХИТЕКТУРА ENEIK PRODUCTION SYSTEM',

    hero_name: 'Дмитрий Ефремов',
    hero_role: 'Архитектор Eneik Production System & Мыслитель',
    hero_badge: 'ENEIK PRODUCTION SYSTEM • АВТОНОМНЫЕ СИСТЕМЫ И ДЕЙСТВИЕ',
    hero_tagline: 'Системы, логика мышления и автономные процессы',
    hero_bio_1: 'Архитектор Eneik Production System (Brand-OS), исследователь онтологии решений и автор подкаста «Системы и Реальность».',
    hero_bio_2: 'Исследую пересечение аналитической философии (модальная логика, онтология, теория познания) и прикладной инженерии распределенных агентских систем высокой надежности.',
    hero_manifest: '«Ваш код и ваши решения — это то, кто вы есть на самом деле.»',
    hero_stat_releases: 'Верифицированные выпуски',
    hero_stat_invariants: 'Деонтические инварианты',
    hero_stat_compliance: 'Zero-Cookie Стандарт',
    hero_btn_podcast: 'СЛУШАТЬ ПОДКАСТ',
    hero_btn_contact: 'ПРЯМАЯ СВЯЗЬ & ЗАЯВКИ',

    catalog_title: 'КАТАЛОГ ВЫПУСКОВ',
    catalog_badge: 'ВЫПУСК',
    catalog_active: 'АКТИВЕН',
    catalog_listen: 'СЛУШАТЬ →',

    articles_title: 'Эссе и статьи',
    articles_all_topics: 'Все темы',
    articles_deontic: 'Деонтическая логика',
    articles_toc: 'Теория ограничений (TOC)',
    articles_autonomous: 'Автономные агенты',
    articles_systems: 'Системная инженерия',

    contact_title: 'ТЕРМИНАЛ ПРЯМОЙ СВЯЗИ',
    contact_subtitle: 'Отправка сообщений напрямую в защищённый журнал связи автора без сторонних трекеров.',
    contact_name_label: 'ВАШЕ ИМЯ ИЛИ ПОЗЫВНОЙ',
    contact_name_placeholder: 'Как к вам обращаться',
    contact_email_label: 'КОНТАКТНЫЙ EMAIL',
    contact_email_placeholder: 'Для ответа автора',
    contact_message_label: 'СУТЬ СООБЩЕНИЯ ИЛИ ВОПРОСА',
    contact_message_placeholder: 'Опишите ваш архитектурный вопрос, гипотезу или предложение по интеграции...',
    contact_submit_btn: 'ОТПРАВИТЬ В ЖУРНАЛ',
    contact_submitting: 'ОТПРАВКА...',
    contact_success: 'Сообщение доставлено и зарегистрировано с присвоением тикета.',
    contact_error: 'Ошибка соединения при отправке сообщения. Черновик сохранён локально, повторите отправку через минуту.',

    search_title: 'Контекстный Агент-Навигатор Дмитрия Ефремова',
    search_esc: 'ESC закрыть',
    search_placeholder: 'Спросите агента своими словами: "где послушать про модальную логику?" или "критерий Поппера"...',
    search_clear: 'Очистить',
    search_examples_label: 'Примеры:',
    search_synthesis_label: 'Синтез Агента',
    search_agent_default_thought: 'Агент-навигатор готов к контекстным запросам. Задайте вопрос в свободной форме или выберите быстрый сценарий выше.',
    search_agent_results_thought: (count, query, topTitle) => `По запросу «${query}» подобрано ${count} материалов. Рекомендую начать с «${topTitle}».`,
    search_agent_not_found: (query) => `По запросу «${query}» точных прямых связей не обнаружено. Попробуйте сформулировать вопрос через понятия: логика, Поппер, подкаст, эссе или A2A.`,
    search_empty_title: 'Агент не нашёл прямых соответствий.',
    search_empty_subtitle: 'Попробуйте задать вопрос иначе или кликните один из примеров выше.',
    search_navigate_to: 'Перейти к материалу ↗',
    search_relevance: 'Релевантность',
    search_footer: 'Навигация: ↑↓ выбор · Enter переход · ESC закрыть',

    footer_rights: 'Дмитрий Ефремов (Dmitry Efremov). Все права защищены.',
    footer_privacy: 'Конфиденциальность',
    footer_imprint: 'Реквизиты',
    imprint_title: 'ЮРИДИЧЕСКИЕ РЕКВИЗИТЫ',
    imprint_owner_label: 'Владелец сайта',
    imprint_owner_val: 'Дмитрий Ефремов (Dmitry Efremov)',
    imprint_domain_label: 'Домен',
    imprint_email_label: 'Email',
    imprint_privacy_label: 'Стандарт конфиденциальности',
    imprint_privacy_val: 'Нулевые сторонние трекеры до явного согласия (Zero-Cookie Telemetry).',
    imprint_copyright_label: 'Авторские права',
    imprint_copyright_val: 'Все материалы, манифесты и формулировки процессов являются интеллектуальной собственностью автора.'
  },

  en: {
    nav_podcast: 'Podcast',
    nav_article: 'Essays & Articles',
    nav_sparks: 'Sparks',
    nav_contact: 'Contact',
    nav_canon: 'Knowledge Base & API',
    search_btn: 'SEARCH',
    search_cmd_k: 'Cmd+K',
    theme_dark: 'Dark',
    theme_light: 'Light',
    topbar_architecture: 'ENEIK PRODUCTION SYSTEM ARCHITECTURE',

    hero_name: 'Dmitry Efremov',
    hero_role: 'Eneik Production System Architect & Thinker',
    hero_badge: 'ENEIK PRODUCTION SYSTEM • AUTONOMOUS SYSTEMS & ACTION',
    hero_tagline: 'Systems, logic of thought, and autonomous processes',
    hero_bio_1: 'Architect of Eneik Production System (Brand-OS), researcher of ontology of decisions and creator of "Systems & Reality" podcast.',
    hero_bio_2: 'Researching the intersection of analytical philosophy (modal logic, ontology, epistemology) and applied engineering of high-reliability distributed agent architectures.',
    hero_manifest: '«Your code and your decisions are who you truly are.»',
    hero_stat_releases: 'Verified Episodes',
    hero_stat_invariants: 'Deontic Invariants',
    hero_stat_compliance: 'Zero-Cookie Standard',
    hero_btn_podcast: 'LISTEN TO PODCAST',
    hero_btn_contact: 'DIRECT CONTACT & INQUIRIES',

    catalog_title: 'EPISODES CATALOG',
    catalog_badge: 'EPISODE',
    catalog_active: 'ACTIVE',
    catalog_listen: 'LISTEN →',

    articles_title: 'Essays & Articles',
    articles_all_topics: 'All Topics',
    articles_deontic: 'Deontic Logic',
    articles_toc: 'Theory of Constraints (TOC)',
    articles_autonomous: 'Autonomous Agents',
    articles_systems: 'Systems Engineering',

    contact_title: 'DIRECT CONTACT TERMINAL',
    contact_subtitle: 'Direct message dispatch into the author’s secure communication journal with zero third-party trackers.',
    contact_name_label: 'YOUR NAME OR CALLSIGN',
    contact_name_placeholder: 'How should we address you',
    contact_email_label: 'CONTACT EMAIL',
    contact_email_placeholder: 'For the author’s reply',
    contact_message_label: 'MESSAGE ESSENCE OR QUERY',
    contact_message_placeholder: 'Describe your architectural question, hypothesis, or integration proposal...',
    contact_submit_btn: 'DISPATCH TO JOURNAL',
    contact_submitting: 'SENDING...',
    contact_success: 'Message delivered and registered with an intake ticket.',
    contact_error: 'Connection error while dispatching message. Draft is saved locally, please retry in a minute.',

    search_title: 'Dmitry Efremov’s Contextual Agent Navigator',
    search_esc: 'ESC to close',
    search_placeholder: 'Ask the agent in natural language: "where to listen to modal logic?" or "Popper criterion"...',
    search_clear: 'Clear',
    search_examples_label: 'Examples:',
    search_synthesis_label: 'Agent Synthesis',
    search_agent_default_thought: 'The Navigator Agent is ready for contextual queries. Ask any question or select a scenario above.',
    search_agent_results_thought: (count, query, topTitle) => `Found ${count} materials for "${query}". I recommend starting with "${topTitle}".`,
    search_agent_not_found: (query) => `No direct semantic links found for "${query}". Try concepts: logic, Popper, podcast, essay, or A2A.`,
    search_empty_title: 'Agent found no direct matches.',
    search_empty_subtitle: 'Try rephrasing your question or click one of the quick prompts above.',
    search_navigate_to: 'Open Material ↗',
    search_relevance: 'Relevance',
    search_footer: 'Navigation: ↑↓ select · Enter open · ESC close',

    footer_rights: 'Dmitry Efremov. All rights reserved.',
    footer_privacy: 'Privacy',
    footer_imprint: 'Legal Details',
    imprint_title: 'LEGAL INFORMATION',
    imprint_owner_label: 'Site Owner',
    imprint_owner_val: 'Dmitry Efremov',
    imprint_domain_label: 'Domain',
    imprint_email_label: 'Email',
    imprint_privacy_label: 'Privacy Standard',
    imprint_privacy_val: 'Zero third-party trackers prior to explicit consent (Zero-Cookie Telemetry).',
    imprint_copyright_label: 'Copyright',
    imprint_copyright_val: 'All materials, manifests, and system formulations are intellectual property of the author.'
  },

  ka: {
    nav_podcast: 'პოდკასტი',
    nav_article: 'ესეები და სტატიები',
    nav_sparks: 'Sparks',
    nav_contact: 'კონტაქტი',
    nav_canon: 'ცოდნის ბაზა და API',
    search_btn: 'ძიება',
    search_cmd_k: 'Cmd+K',
    theme_dark: 'მუქი',
    theme_light: 'ნათელი',
    topbar_architecture: 'ENEIK PRODUCTION SYSTEM არქიტექტურა',

    hero_name: 'დიმიტრი ეფრემოვი',
    hero_role: 'Eneik Production System-ის არქიტექტორი და მოაზროვნე',
    hero_badge: 'ENEIK PRODUCTION SYSTEM • ავტონომიური სისტემები და მოქმედება',
    hero_tagline: 'სისტემები, აზროვნების ლოგიკა და ავტონომიური პროცესები',
    hero_bio_1: 'Eneik Production System-ის (Brand-OS) არქიტექტორი, გადაწყვეტილებათა ონტოლოგიის მკვლევარი და პოდკასტის «სისტემები და რეალობა» ავტორი.',
    hero_bio_2: 'ვიკვლევ ანალიტიკური ფილოსოფიის (მოდალური ლოგიკა, ონტოლოგია, შემეცნების თეორია) და მაღალი საიმედოობის განაწილებული აგენტური სისტემების საინჟინრო გადაკვეთას.',
    hero_manifest: '«თქვენი კოდი და თქვენი გადაწყვეტილებები — ეს არის ის, რაც სინამდვილეში ხართ.»',
    hero_stat_releases: 'ვერიფიცირებული გამოშვებები',
    hero_stat_invariants: 'დეონტური ინვარიანტები',
    hero_stat_compliance: 'Zero-Cookie სტანდარტი',
    hero_btn_podcast: 'პოდკასტის მოსმენა',
    hero_btn_contact: 'პირდაპირი კავშირი და განაცხადები',

    catalog_title: 'გამოშვებების კატალოგი',
    catalog_badge: 'გამოშვება',
    catalog_active: 'აქტიური',
    catalog_listen: 'მოსმენა →',

    articles_title: 'ესეები და სტატიები',
    articles_all_topics: 'ყველა თემა',
    articles_deontic: 'დეონტური ლოგიკა',
    articles_toc: 'შეზღუდვების თეორია (TOC)',
    articles_autonomous: 'ავტონომიური აგენტები',
    articles_systems: 'სისტემური ინჟინერია',

    contact_title: 'პირდაპირი კავშირის ტერმინალი',
    contact_subtitle: 'შეტყობინებების გაგზავნა პირდაპირ ავტორის დაცულ ჟურნალში მესამე მხარის თრექერების გარეშე.',
    contact_name_label: 'თქვენი სახელი',
    contact_name_placeholder: 'როგორ მოგმართოთ',
    contact_email_label: 'საკონტაქტო EMAIL',
    contact_email_placeholder: 'ავტორის პასუხისთვის',
    contact_message_label: 'შეტყობინების არსი ან შეკითხვა',
    contact_message_placeholder: 'აღწერეთ თქვენი არქიტექტურული შეკითხვა, ჰიპოთეზა ან ინტეგრაციის წინადადება...',
    contact_submit_btn: 'ჟურნალში გაგზავნა',
    contact_submitting: 'იგზავნება...',
    contact_success: 'შეტყობინება წარმატებით ჩაბარდა და დარეგისტრირდა ტიპური ნომრით.',
    contact_error: 'კავშირის შეცდომა გაგზავნისას. შავი ვერსია შენახულია ლოკალურად, გთხოვთ სცადოთ წუთში.',

    search_title: 'დიმიტრი ეფრემოვის კონტექსტური აგენტ-ნავიგატორი',
    search_esc: 'ESC დახურვა',
    search_placeholder: 'ჰკითხეთ აგენტს თავისუფალი ენით: "სად მოვუსმინო მოდალურ ლოგიკაზე?" ან "პოპერის კრიტერიუმი"...',
    search_clear: 'გასუფთავება',
    search_examples_label: 'მაგალითები:',
    search_synthesis_label: 'აგენტის სინთეზი',
    search_agent_default_thought: 'ნავიგატორი აგენტი მზადაა კონტექსტური მოთხოვნებისთვის. დასვით შეკითხვა თავისუფალ ფორმაში ან აირჩიეთ მაგალითი ზემოთ.',
    search_agent_results_thought: (count, query, topTitle) => `მოთხოვნაზე «${query}» ნაპოვნია ${count} მასალა. გირჩევთ დაიწყოთ «${topTitle}»-ით.`,
    search_agent_not_found: (query) => `მოთხოვნაზე «${query}» პირდაპირი ბმები ვერ მოიძებნა. სცადეთ ცნებები: ლოგიკა, პოპერი, პოდკასტი, ესე ან A2A.`,
    search_empty_title: 'აგენტმა პირდაპირი შესაბამისობა ვერ იპოვა.',
    search_empty_subtitle: 'სცადეთ შეკითხვის სხვაგვარად ფორმულირება ან აირჩიეთ მაგალითი ზემოთ.',
    search_navigate_to: 'მასალაზე გადასვლა ↗',
    search_relevance: 'რელევანტურობა',
    search_footer: 'ნავიგაცია: ↑↓ არჩევა · Enter გადასვლა · ESC დახურვა',

    footer_rights: 'დიმიტრი ეფრემოვი (Dmitry Efremov). ყველა უფლება დაცულია.',
    footer_privacy: 'კონფიდენციალურობა',
    footer_imprint: 'რეკვიზიტები',
    imprint_title: 'იურიდიული რეკვიზიტები',
    imprint_owner_label: 'საიტის მფლობელი',
    imprint_owner_val: 'დიმიტრი ეფრემოვი (Dmitry Efremov)',
    imprint_domain_label: 'დომენი',
    imprint_email_label: 'Email',
    imprint_privacy_label: 'კონფიდენციალურობის სტანდარტი',
    imprint_privacy_val: 'ნულოვანი მესამე მხარის თრექერები თანხმობის გარეშე (Zero-Cookie Telemetry).',
    imprint_copyright_label: 'საავტორო უფლებები',
    imprint_copyright_val: 'ყველა მასალა, მანიფესტი და სისტემური ფორმულირება წარმოადგენს ავტორის ინტელექტუალურ საკუთრებას.'
  }
};

export function t(lang: Language): Translations {
  return DICTIONARY[lang] || DICTIONARY.ru;
}
