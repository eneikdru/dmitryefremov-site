// Canonical Podcast Episodes with verified audio streams
// Source: Hetzner production volume /mnt/HC_Volume_106932004/podcast/episodes/

export interface PodcastTimecode {
  id: number | string;
  timeOffsetSeconds: number;
  title: string;
  description?: string;
  displayOrder?: number;
}

export interface PodcastEpisode {
  id: number | string;
  slug: string;
  philosopher: string;
  title: string;
  thesis: string;
  description: string;
  audioUrl: string;
  mediaLink?: string;
  youtubeUrl?: string;
  telegramUrl?: string;
  durationSeconds: number;
  publishedAt: string;
  timecodes: PodcastTimecode[];
}

export const PODCAST_EPISODES: PodcastEpisode[] = [
  {
    "id": 1,
    "slug": "01-ruth-barcan-marcus",
    "philosopher": "Рут Баркан Маркус",
    "title": "Рут Баркан Маркус: Модальная логика сущностей и деонтические инварианты",
    "thesis": "Формула Баркан как математический мост между возможными мирами и инвариантами автономных систем. Если нечто возможно необходимо, оно существует.",
    "description": "Разбор модальной логики квантификации, деонтических операторов долженствования и формулы Баркан (BF). Почему архитектура автономных агентов требует строгого запрета противоречивых состояний.",
    "durationSeconds": 2690,
    "publishedAt": "2026-09-20",
    "audioUrl": "/audio/01-ruth-barcan-marcus.m4a",
    "youtubeUrl": "https://youtu.be/1qiPT9UxKNM",
    "mediaLink": "https://youtu.be/1qiPT9UxKNM",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 101,
        "timeOffsetSeconds": 0,
        "title": "Введение: Забытый архитектор модальной логики",
        "description": "Кто такая Рут Баркан и почему аналитическая философия задолжала ей полвека признания."
      },
      {
        "id": 102,
        "timeOffsetSeconds": 340,
        "title": "Формула Баркан: Сущность и возможное существование",
        "description": "Анализ формулы diamond exists x Fx -> exists x diamond Fx в контексте надежности распределенных систем."
      },
      {
        "id": 103,
        "timeOffsetSeconds": 920,
        "title": "Деонтические дилеммы и неразрешимость",
        "description": "Когда система сталкивается с конфликтом обязательств: O(p) and O(~p)."
      },
      {
        "id": 104,
        "timeOffsetSeconds": 1640,
        "title": "Архитектурные инварианты автономного контура",
        "description": "Перенос логических законов необходимости в код и валидаторы состояния."
      },
      {
        "id": 105,
        "timeOffsetSeconds": 2310,
        "title": "Финал: Мышление как предел строгости",
        "description": "Итоги и связь с практикой проектирования программных агентов."
      }
    ]
  },
  {
    "id": 2,
    "slug": "02-paul-thagard",
    "philosopher": "Пол Таггард",
    "title": "Пол Таггард: Вычислительная философия и когнитивная когерентность",
    "thesis": "Объяснительная когерентность (ECHO) как математическая модель преодоления галлюцинаций в сложных информационных сетях.",
    "description": "Коннекционистские сети, расчет локальной и глобальной когерентности доказательств. Как машины могут взвешивать конкурирующие гипотезы без произвольных суждений.",
    "durationSeconds": 1860,
    "publishedAt": "2026-09-22",
    "audioUrl": "/audio/02-paul-thagard.m4a",
    "youtubeUrl": "https://youtu.be/jUbOF_YvkfI",
    "mediaLink": "https://youtu.be/jUbOF_YvkfI",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 201,
        "timeOffsetSeconds": 0,
        "title": "Философия как вычислительная наука",
        "description": "Переход от умозрительной эпистемологии к алгоритмическим моделям."
      },
      {
        "id": 202,
        "timeOffsetSeconds": 280,
        "title": "Сеть ECHO: Конкуренция гипотез",
        "description": "Как узлы активируют и подавляют друг друга при поиске наилучшего объяснения."
      },
      {
        "id": 203,
        "timeOffsetSeconds": 750,
        "title": "Принцип простоты и фальсификации",
        "description": "Штрафы за ad-hoc гипотезы и избыточную сложность аргументации."
      },
      {
        "id": 204,
        "timeOffsetSeconds": 1320,
        "title": "Устранение галлюцинаций в LLM через когерентность",
        "description": "Применение модели Таггарда для верификации выходов генеративного ИИ."
      },
      {
        "id": 205,
        "timeOffsetSeconds": 1710,
        "title": "Выводы: Инженерия когнитивной надежности",
        "description": "Резюме и принципы построения непротиворечивого контекста."
      }
    ]
  },
  {
    "id": 3,
    "slug": "03-susan-hurley",
    "philosopher": "Сюзан Хёрли",
    "title": "Сюзан Хёрли: Сознание в действии и обратная связь среды",
    "thesis": "Двухуровневая петля сенсомоторной обратной связи: сознание рождается не в изоляции мозга, а в динамическом взаимодействии действия и восприятия.",
    "description": "Критика классической сэндвич-модели (восприятие -> обработка -> действие). Почему автономия невозможна без непрерывного сопротивления физической и цифровой среды.",
    "durationSeconds": 2430,
    "publishedAt": "2026-09-25",
    "audioUrl": "/audio/03-susan-hurley.m4a",
    "youtubeUrl": "https://youtu.be/mLJm6YKm0ds",
    "mediaLink": "https://youtu.be/mLJm6YKm0ds",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 301,
        "timeOffsetSeconds": 0,
        "title": "Крах классического когнитивного сэндвича",
        "description": "Почему разделение перцепции и моторного действия ложно."
      },
      {
        "id": 302,
        "timeOffsetSeconds": 390,
        "title": "Теория динамической петли (Shared Circuits Model)",
        "description": "Зеркальные структуры, симуляция чужого действия и предиктивный контроль."
      },
      {
        "id": 303,
        "timeOffsetSeconds": 1050,
        "title": "Сознание как интерфейс сопротивления среды",
        "description": "Без среды и трения агент теряет калибровку и деградирует."
      },
      {
        "id": 304,
        "timeOffsetSeconds": 1780,
        "title": "Автономные контуры в IT: Сенсомоторная петля агентов",
        "description": "Реализация концепции Хёрли в feedback loops программной фабрики."
      },
      {
        "id": 305,
        "timeOffsetSeconds": 2240,
        "title": "Заключение: Архитектура действия",
        "description": "Код, проверяемый реальностью."
      }
    ]
  },
  {
    "id": 4,
    "slug": "04-gilbert-ryle",
    "philosopher": "Гилберт Райл",
    "title": "Гилберт Райл: Призрак в машине и процедурное знание",
    "thesis": "Категориальная ошибка Декарта: разум — это не скрытая субстанция внутри шестеренок, а слаженная диспозиция поведения и способность действовать безошибочно.",
    "description": "Различие между Знанием-Что (декларативные факты) и Знанием-Как (процедурное мастерство). Почему терабайты промптов бесполезны без жестких пайплайнов исполнения.",
    "durationSeconds": 2565,
    "publishedAt": "2026-09-28",
    "audioUrl": "/audio/04-gilbert-ryle.m4a",
    "youtubeUrl": "https://youtube.com/@dmitrii.efremov",
    "mediaLink": "https://youtube.com/@dmitrii.efremov",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 401,
        "timeOffsetSeconds": 0,
        "title": "Миф о картезианском дуализме",
        "description": "Как идея тайного гомункула в голове запутала три века европейской мысли."
      },
      {
        "id": 402,
        "timeOffsetSeconds": 410,
        "title": "Категориальная ошибка на практике",
        "description": "Поиск «университета» среди зданий колледжей и поиск «интеллекта» среди токенов."
      },
      {
        "id": 403,
        "timeOffsetSeconds": 1100,
        "title": "Знать-Как против Знать-Что (Knowing-How vs Knowing-That)",
        "description": "Почему мастерство не сводится к перечню пропозиций."
      },
      {
        "id": 404,
        "timeOffsetSeconds": 1820,
        "title": "Процедурные инварианты автономных систем",
        "description": "Перевод диспозиций Райла в автоматизированные тесты и Poka-Yoke."
      },
      {
        "id": 405,
        "timeOffsetSeconds": 2390,
        "title": "Итог: Ликвидация призраков в архитектуре",
        "description": "Чистая инженерия наблюдаемого поведения."
      }
    ]
  },
  {
    "id": 5,
    "slug": "05-daniel-dennett",
    "philosopher": "Дэниел Деннет",
    "title": "Дэниел Деннет: Интенциональная установка и модель многократных набросков",
    "thesis": "Сознание как фабрика параллельных черновиков: нет единого центрального кинотеатра в голове, есть постоянная селекция конкурирующих интерпретаций.",
    "description": "Физическая, проектная и интенциональная установки. Разбор знаменитого понятия Multiple Drafts и его зеркального отражения в архитектуре мультиагентных систем.",
    "durationSeconds": 4250,
    "publishedAt": "2026-10-01",
    "audioUrl": "/audio/05-daniel-dennett.m4a",
    "youtubeUrl": "https://youtube.com/@dmitrii.efremov",
    "mediaLink": "https://youtube.com/@dmitrii.efremov",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 501,
        "timeOffsetSeconds": 0,
        "title": "Картезианский театр и его иллюзорность",
        "description": "Почему в мозгу нет точки сборки, где зритель смотрит фильм жизни."
      },
      {
        "id": 502,
        "timeOffsetSeconds": 580,
        "title": "Три уровня объяснения: Physical, Design, Intentional Stance",
        "description": "Как мы приписываем цели машинам, программам и людям."
      },
      {
        "id": 503,
        "timeOffsetSeconds": 1490,
        "title": "Модель множественных набросков (Multiple Drafts Model)",
        "description": "Потоки правок, переписывание памяти на лету и эффект постдикции."
      },
      {
        "id": 504,
        "timeOffsetSeconds": 2720,
        "title": "Мультиагентный ансамбль как модель Деннета",
        "description": "Параллельные гипотезы агентов и консенсусный отбор без супервайзера."
      },
      {
        "id": 505,
        "timeOffsetSeconds": 3890,
        "title": "Финал: Эволюция мысли и семантическая ясность",
        "description": "Демистификация субъективности в программной среде."
      }
    ]
  },
  {
    "id": 6,
    "slug": "06-andy-clark",
    "philosopher": "Энди Кларк",
    "title": "Энди Кларк: Расширенный разум и мозг как машина прогнозирования",
    "thesis": "Гипотеза расширенного разума: ноутбук, записная книжка или база данных являются буквальной частью вашей когнитивной системы, если они функционально сопряжены.",
    "description": "Предиктивная обработка (Predictive Processing) и минимизация ошибки прогноза. Почему агент с внешней памятью умнее изолированной сверхмощной модели.",
    "durationSeconds": 3850,
    "publishedAt": "2026-10-04",
    "audioUrl": "/audio/06-andy-clark.m4a",
    "youtubeUrl": "https://youtube.com/@dmitrii.efremov",
    "mediaLink": "https://youtube.com/@dmitrii.efremov",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 601,
        "timeOffsetSeconds": 0,
        "title": "Где заканчивается кожа и череп?",
        "description": "Кларк и Чалмерс: мысленный эксперимент с Отто и его записной книжкой."
      },
      {
        "id": 602,
        "timeOffsetSeconds": 620,
        "title": "Критерии паритета: Надежность, доступность, доверие",
        "description": "Когда внешний инструмент становится неотделимым органом мышления."
      },
      {
        "id": 603,
        "timeOffsetSeconds": 1540,
        "title": "Предиктивный разум: Сверху вниз и снизу вверх",
        "description": "Мозг как оптимизатор байесовской ошибки рассогласования."
      },
      {
        "id": 604,
        "timeOffsetSeconds": 2580,
        "title": "Внешние базы данных как расширенный когнитивный контур",
        "description": "Проектирование памяти агентов на основе принципов Кларка."
      },
      {
        "id": 605,
        "timeOffsetSeconds": 3490,
        "title": "Выводы: Человек-симбионт и суверенные технологии",
        "description": "Будущее персональных интеллектуальных узлов."
      }
    ]
  },
  {
    "id": 7,
    "slug": "07-david-chalmers",
    "philosopher": "Дэвид Чалмерс",
    "title": "Дэвид Чалмерс: Трудная проблема сознания и метафизика виртуальности",
    "thesis": "Разделение легких проблем обработки информации и трудной проблемы субъективного опыта (квалиа). Виртуальные миры не иллюзорны — они столь же онтологически реальны.",
    "description": "Философские зомби, аргумент мыслимости и дуализм свойств. Разбор фундаментальной границы между функциональным вычислением и внутренним светом феноменологии.",
    "durationSeconds": 3040,
    "publishedAt": "2026-10-06",
    "audioUrl": "/audio/07-david-chalmers.m4a",
    "youtubeUrl": "https://youtube.com/@dmitrii.efremov",
    "mediaLink": "https://youtube.com/@dmitrii.efremov",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 701,
        "timeOffsetSeconds": 0,
        "title": "Трудная проблема против легких задач",
        "description": "Почему распознавание паттернов не объясняет субъективное ощущение красного."
      },
      {
        "id": 702,
        "timeOffsetSeconds": 480,
        "title": "Философский зомби: Мыслим ли функциональный дубликат без опыта?",
        "description": "Онтологический аргумент Чалмерса против чистого физикализма."
      },
      {
        "id": 703,
        "timeOffsetSeconds": 1210,
        "title": "Reality+: Виртуальная реальность как подлинное бытие",
        "description": "Цифровые объекты реальны, цифровые события истинны, цифровой опыт подлинен."
      },
      {
        "id": 704,
        "timeOffsetSeconds": 2100,
        "title": "Границы функционализма в искусственном интеллекте",
        "description": "Может ли программный агент обладать квалиа или остается идеальным автоматом."
      },
      {
        "id": 705,
        "timeOffsetSeconds": 2810,
        "title": "Эпилог: Инженерная честность перед тайной сознания",
        "description": "Строгость терминологии и отказ от псевдо-научных спекуляций."
      }
    ]
  },
  {
    "id": 8,
    "slug": "08-shaun-gallagher",
    "philosopher": "Шон Галлахер",
    "title": "Шон Галлахер: Энактивное познание и телесный опыт",
    "thesis": "4E-познание (Embodied, Embedded, Enacted, Extended): мышление укоренено в телесной схеме и предвосхищении двигательных актов.",
    "description": "Различие между схемой тела (Body Schema) и образом тела (Body Image). Почему абстрактные языковые модели не имеют якоря без энактивного взаимодействия.",
    "durationSeconds": 3755,
    "publishedAt": "2026-10-08",
    "audioUrl": "/audio/08-shaun-gallagher.m4a",
    "youtubeUrl": "https://youtube.com/@dmitrii.efremov",
    "mediaLink": "https://youtube.com/@dmitrii.efremov",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 801,
        "timeOffsetSeconds": 0,
        "title": "Манифест 4E-когнитивистики",
        "description": "Четыре измерения познания: воплощенное, встроенное, энактивное, расширенное."
      },
      {
        "id": 802,
        "timeOffsetSeconds": 540,
        "title": "Body Schema vs Body Image",
        "description": "Бессознательный моторный регулятор против осознаваемого представления о себе."
      },
      {
        "id": 803,
        "timeOffsetSeconds": 1420,
        "title": "Первичная интерсубъективность",
        "description": "Как телесная мимикрия и резонанс предшествуют абстрактным концепциям ума."
      },
      {
        "id": 804,
        "timeOffsetSeconds": 2490,
        "title": "Якорение агентов: Энактивность в программной среде",
        "description": "Создание телесной схемы исполнения для цифровых ассистентов."
      },
      {
        "id": 805,
        "timeOffsetSeconds": 3410,
        "title": "Итог: Познание как непрерывное освоение мира",
        "description": "Практические выводы для системного инжиниринга."
      }
    ]
  },
  {
    "id": 9,
    "slug": "09-alva-noe",
    "philosopher": "Альва Ноэ",
    "title": "Альва Ноэ: Восприятие в действии и феноменология сенсомоторики",
    "thesis": "Мы не пассивно смотрим на мир, как через объектив камеры — мы активно исследуем его движением глаз, тела и рук. Видение — это моторный навык.",
    "description": "Сенсомоторные контингенции и преодоление визуального иллюзионизма. «Мы не являемся нашими мозгами»: сознание как танец между мозгом, телом и окружением.",
    "durationSeconds": 3975,
    "publishedAt": "2026-10-10",
    "audioUrl": "/audio/09-alva-noe.m4a",
    "youtubeUrl": "https://youtube.com/@dmitrii.efremov",
    "mediaLink": "https://youtube.com/@dmitrii.efremov",
    "telegramUrl": "https://t.me/efremov_mvp",
    "timecodes": [
      {
        "id": 901,
        "timeOffsetSeconds": 0,
        "title": "Видение как осязание глазом",
        "description": "Почему зрительный опыт является активным навыком, а не внутренней картинкой."
      },
      {
        "id": 902,
        "timeOffsetSeconds": 610,
        "title": "Сенсомоторные контингенции",
        "description": "Законы того, как меняется сенсорный приток при изменении движения наблюдателя."
      },
      {
        "id": 903,
        "timeOffsetSeconds": 1580,
        "title": "Out of Our Heads: Мы — это не наш мозг",
        "description": "Критика нейроцентризма и попыток свести личность к томограмме нейронов."
      },
      {
        "id": 904,
        "timeOffsetSeconds": 2650,
        "title": "Искусственное зрение и активное зондирование",
        "description": "Как роботы и агенты должны воспринимать данные через активное действие."
      },
      {
        "id": 905,
        "timeOffsetSeconds": 3620,
        "title": "Финал канонического цикла: Суверенная ясность",
        "description": "Синтез идей 9 мыслителей в целостную философию действия Дмитрия Ефремова."
      }
    ]
  }
];

export function formatTime(seconds: number): string {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}

export function generateWaveformHeights(barsCount: number = 40, seed: string = 'default'): number[] {
  let h = 0;
  for (let i = 0; i < seed.length; i++) {
    h = (Math.imul(31, h) + seed.charCodeAt(i)) | 0;
  }
  const heights: number[] = [];
  for (let i = 0; i < barsCount; i++) {
    h = Math.sin(i * 0.45 + (h % 100)) * 10000;
    const norm = Math.abs(h - Math.floor(h));
    heights.push(Math.max(16, Math.floor(norm * 92)));
  }
  return heights;
}
