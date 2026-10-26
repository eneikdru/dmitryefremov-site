<script lang="ts">
  import { currentLanguage, t } from '../lib/i18n';

  export let theme: 'dark' | 'light' = 'dark';

  // Stripe Checkout links (configurable)
  export let stripeOneTimeTier1: string = 'https://buy.stripe.com/cNieVdbJv61a94Y6ErcIE0X';
  export let stripeOneTimeTier2: string = 'https://buy.stripe.com/9B69AT7tf75ea920g3cIE0W';
  export let stripeMonthlyTier1: string = 'https://buy.stripe.com/4gMaEXaFr75e4OI3sfcIE0V';
  export let stripeMonthlyTier2: string = 'https://buy.stripe.com/fZueVdbJvexG94Y8MzcIE0U';

  const supportTiers = [
    {
      id: 'onetime-25',
      type: 'onetime',
      price: '€25',
      periodRu: 'Разовый взнос',
      periodEn: 'One-time contribution',
      periodKa: 'ერთჯერადი შენატანი',
      titleRu: 'Исследовательский взнос',
      titleEn: 'Research Contribution',
      titleKa: 'კვლევითი შენატანი',
      descRu: 'Поддержка вычислительных ресурсов и тестирования архитектурных моделей.',
      descEn: 'Support for computing power and testing architectural models.',
      descKa: 'გამომთვლელი რესურსებისა და არქიტექტურული მოდელების ტესტირების მხარდაჭერა.',
      url: stripeOneTimeTier1,
      badgeRu: 'Разово',
      badgeEn: 'One-off',
      badgeKa: 'ერთჯერადი',
      featured: false
    },
    {
      id: 'onetime-100',
      type: 'onetime',
      price: '€100',
      periodRu: 'Разовый взнос',
      periodEn: 'One-time contribution',
      periodKa: 'ერთჯერადი შენატანი',
      titleRu: 'Инженерный грант',
      titleEn: 'Engineering Grant',
      titleKa: 'საინჟინრო გრანტი',
      descRu: 'Целевое финансирование стендов автономности и экспериментов с агентами.',
      descEn: 'Targeted funding for autonomy testbeds and agent experiments.',
      descKa: 'ავტონომიის სტენდებისა და აგენტური ექსპერიმენტების მიზნობრივი დაფინანსება.',
      url: stripeOneTimeTier2,
      badgeRu: 'Разово',
      badgeEn: 'One-off',
      badgeKa: 'ერთჯერადი',
      featured: true
    },
    {
      id: 'sub-20',
      type: 'subscription',
      price: '€20',
      periodRu: '/ месяц',
      periodEn: '/ month',
      periodKa: '/ თვე',
      titleRu: 'Патрон исследований',
      titleEn: 'Research Patron',
      titleKa: 'კვლევების პატრონი',
      descRu: 'Ежемесячный вклад в развитие открытых стандартов надежности и философии систем.',
      descEn: 'Monthly contribution to open reliability standards and systems philosophy.',
      descKa: 'ყოველთვიური წვლილი საიმედოობის ღია სტანდარტებისა და სისტემური ფილოსოფიის განვითარებაში.',
      url: stripeMonthlyTier1,
      badgeRu: 'Подписка',
      badgeEn: 'Monthly',
      badgeKa: 'გამოწერა',
      featured: false
    },
    {
      id: 'sub-50',
      type: 'subscription',
      price: '€50',
      periodRu: '/ месяц',
      periodEn: '/ month',
      periodKa: '/ თვე',
      titleRu: 'Партнер лаборатории',
      titleEn: 'Lab Partner',
      titleKa: 'ლაბორატორიის პარტნიორი',
      descRu: 'Прямое участие в развитии инженерных экспериментов и приоритетный диалог.',
      descEn: 'Direct participation in engineering experiments and priority dialogue.',
      descKa: 'პირდაპირი მონაწილეობა საინჟინრო ექსპერიმენტების განვითარებაში და პრიორიტეტული დიალოგი.',
      url: stripeMonthlyTier2,
      badgeRu: 'Подписка',
      badgeEn: 'Monthly',
      badgeKa: 'გამოწერა',
      featured: true
    }
  ];

  function getTitle(tier: typeof supportTiers[0], lang: string): string {
    if (lang === 'en') return tier.titleEn;
    if (lang === 'ka') return tier.titleKa;
    return tier.titleRu;
  }

  function getDesc(tier: typeof supportTiers[0], lang: string): string {
    if (lang === 'en') return tier.descEn;
    if (lang === 'ka') return tier.descKa;
    return tier.descRu;
  }

  function getPeriod(tier: typeof supportTiers[0], lang: string): string {
    if (lang === 'en') return tier.periodEn;
    if (lang === 'ka') return tier.periodKa;
    return tier.periodRu;
  }

  function getBadge(tier: typeof supportTiers[0], lang: string): string {
    if (lang === 'en') return tier.badgeEn;
    if (lang === 'ka') return tier.badgeKa;
    return tier.badgeRu;
  }
</script>

<div class="space-y-8 font-sans">
  <!-- Header Plate -->
  <div class="rounded-xl border p-6 sm:p-8 space-y-4 transition-all {theme === 'dark' ? 'bg-[#111318] border-white/10 shadow-2xl' : 'bg-white border-slate-300 shadow-sm'}">
    <div class="flex items-center gap-2">
      <span class="w-2 h-2 rounded-full bg-[#f2ca50] animate-pulse"></span>
      <span class="font-mono text-xs font-bold uppercase tracking-widest text-[#f2ca50]">
        STRIPE // {$currentLanguage === 'en' ? 'FINANCIAL SUPPORT' : ($currentLanguage === 'ka' ? 'ფინანსური მხარდაჭერა' : 'ФИНАНСОВАЯ ПОДДЕРЖКА')}
      </span>
    </div>

    <h2 class="text-2xl sm:text-3xl font-extrabold tracking-tight {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
      {$currentLanguage === 'en' 
        ? 'Continuation of Engineering Research & Experiments' 
        : ($currentLanguage === 'ka' 
          ? 'საინჟინრო კვლევებისა და ექსპერიმენტების გაგრძელება' 
          : 'Продолжение инженерных исследований и экспериментов')}
    </h2>

    <p class="font-mono text-xs sm:text-sm leading-relaxed max-w-3xl {theme === 'dark' ? 'text-slate-300' : 'text-slate-700'}">
      {$currentLanguage === 'en'
        ? 'Your support funds independent server infrastructure, autonomous AI agent experiments, and philosophical inquiries into systems reliability. Every contribution goes directly into engineering research.'
        : ($currentLanguage === 'ka'
          ? 'თქვენი მხარდაჭერა უზრუნველყოფს დამოუკიდებელ სასერვერო ინფრასტრუქტურას, ავტონომიური AI აგენტების ექსპერიმენტებს და სისტემების საიმედოობის ფილოსოფიურ კვლევებს.'
          : 'Ваша поддержка обеспечивает независимую серверную инфраструктуру, эксперименты с автономными агентами и фундаментальные исследования надежности систем. Каждый взнос направляется напрямую на инженерные исследования.')}
    </p>
  </div>

  <!-- 4 Tiers Grid (2 One-Off, 2 Subscription) -->
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
    {#each supportTiers as tier}
      <div class="rounded-xl border p-6 flex flex-col justify-between transition-all {tier.featured ? (theme === 'dark' ? 'bg-[#1e2024] border-[#f2ca50]/50 shadow-xl ring-1 ring-[#f2ca50]/30' : 'bg-amber-50/50 border-amber-300 shadow-md') : (theme === 'dark' ? 'bg-[#111318] border-white/10 hover:border-white/20' : 'bg-white border-slate-300 hover:border-slate-400')}">
        <div class="space-y-4">
          <!-- Badge & Type -->
          <div class="flex items-center justify-between">
            <span class="font-mono text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded border {tier.featured ? 'bg-[#f2ca50]/15 text-[#f2ca50] border-[#f2ca50]/40' : 'bg-white/5 text-slate-400 border-white/10'}">
              {getBadge(tier, $currentLanguage)}
            </span>
            <span class="font-mono text-[10px] text-slate-400">
              {tier.type === 'subscription' ? 'Stripe Sub' : 'Stripe Pay'}
            </span>
          </div>

          <!-- Price -->
          <div class="flex items-baseline gap-1.5">
            <span class="font-grotesk text-3xl sm:text-4xl font-extrabold text-[#f2ca50]">
              {tier.price}
            </span>
            <span class="font-mono text-xs text-slate-400">
              {getPeriod(tier, $currentLanguage)}
            </span>
          </div>

          <!-- Title -->
          <h3 class="font-bold text-base {theme === 'dark' ? 'text-white' : 'text-slate-900'}">
            {getTitle(tier, $currentLanguage)}
          </h3>

          <!-- Description -->
          <p class="font-mono text-xs leading-relaxed {theme === 'dark' ? 'text-slate-400' : 'text-slate-600'}">
            {getDesc(tier, $currentLanguage)}
          </p>
        </div>

        <!-- Stripe Button -->
        <div class="pt-6">
          <a
            href={tier.url}
            target="_blank"
            rel="noopener noreferrer"
            class="w-full py-3 px-4 rounded-lg font-mono text-xs font-bold uppercase tracking-wider transition-all flex items-center justify-center gap-2 cursor-pointer shadow-md {tier.featured ? 'bg-[#f2ca50] text-[#0c0e12] hover:bg-[#e7c364]' : (theme === 'dark' ? 'bg-[#1e2024] text-white border border-white/15 hover:border-[#f2ca50] hover:text-[#f2ca50]' : 'bg-slate-900 text-white hover:bg-slate-800')}"
          >
            <span>STRIPE</span>
            
          </a>
        </div>
      </div>
    {/each}
  </div>

  <!-- Trust & Transparency Note -->
  <div class="p-4 rounded-lg border font-mono text-xs text-center {theme === 'dark' ? 'bg-[#0c0e12] border-white/5 text-slate-400' : 'bg-slate-100 border-slate-200 text-slate-600'}">
    <span>{$currentLanguage === 'en' ? 'Payments are securely processed by Stripe. You can cancel subscriptions anytime.' : ($currentLanguage === 'ka' ? 'გადახდები დაცულად მუშავდება Stripe-ით. გამოწერის გაუქმება ნებისმიერ დროსაა შესაძლებელი.' : 'Платежи безопасно обрабатываются через Stripe. Подписку можно отменить в любой момент в 1 клик.')}</span>
  </div>
</div>
