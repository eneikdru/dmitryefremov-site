export interface LocalizedEssayContent {
  title: string;
  summary: string;
  contentMarkdown: string;
}

export const ESSAYS_TRANSLATIONS: Record<string, { en: LocalizedEssayContent; ka: LocalizedEssayContent }> = {
  '01-ruth-barcan-marcus': {
    en: {
      title: '«Why Neural Networks Confuse Database Entities Even When Table Schemas Are Fully Described»',
      summary: 'The descriptive trap of language models and Ruth Barcan Marcus’s principle of rigid designators. Why prompts are powerless against entity mutation and how to fix identity via Branded Types.',
      contentMarkdown: `> *"A name refers directly to an object, not to the cluster of its shifting descriptions."* — **Ruth Barcan Marcus**, *Identity and Modality (1961)*

> *"In a codebase, entity identity must be locked by a nominal type rather than a floating field list. Otherwise, the very first language model will rewrite your domain."* — **Dmitry Efremov**

### The Problem
You describe a database schema thoroughly in a system prompt: listing tables, columns, and foreign keys. Three generation steps later, the neural network silently mutates your domain: renames identifiers, coalesces two distinct tables into one, or creates a parallel schema with similar attributes. ALL-CAPS instructions like *"DO NOT CHANGE TABLE NAMES!"* never work.

### Root Cause: The Descriptive Trap
In 1947, Ruth Barcan Marcus proved the foundational theorem on the necessity of identity in modal logic:
$$
a = b \\implies \\Box (a = b)
$$
An object's name is a rigid designator that refers to the object itself in all possible worlds, not merely a bundle of accidental properties (countering Frege and Russell’s descriptive theories).

A language model is a purely descriptive engine. For it, an \`Account\` entity does not exist as an objective invariant: it is merely a cloud of distributed token probabilities. When prompt context drifts toward an adjacent task, the attention vector shifts, and the model infers a new entity, generating an alternate schema. The model thinks in descriptions, not names.

### Dmitry Efremov's Pattern: Branded Types & Rigid Reference
Never trust an AI to verify identity verbally. Identity is enforced at the compiler level via nominal typing (Branded Types):

\`\`\`typescript
declare const BrandId: unique symbol;
export type EntityId<Brand extends string> = string & { readonly [BrandId]: Brand };

export type AccountId = EntityId<'Account'>;
export type OrderId = EntityId<'Order'>;

export interface ImmutableIdentity<Id extends string> {
  readonly id: EntityId<Id>;
  readonly createdAt: string;
}

export interface AccountEntity extends ImmutableIdentity<'Account'> {
  readonly legalName: string;
  readonly taxIdentifier: string;
  readonly status: 'ACTIVE' | 'SUSPENDED';
}
\`\`\`

### Architectural Invariant
1. **A name is not a description.** Never couple business logic to dynamic text fields.
2. **Identity is guaranteed by the compiler, not the prompt.** If an entity type is nominally locked, the compiler and linter will reject any hallucinated substitution before runtime.`
    },
    ka: {
      title: '«რატომ ერევა ნეიროქსელს მონაცემთა ბაზის ობიექტები მაშინაც კი, როდესაც სქემები დეტალურადაა აღწერილი?»',
      summary: 'ენობრივი მოდელების დესკრიფციული ხაფანგი და რუთ ბარკან მარკუსის მყარი დესიგნატორების პრინციპი. რატომაა პრომპტები უძლური ობიექტების მუტაციის წინაშე და როგორ დავაფიქსიროთ იდენტობა Branded Types-ით.',
      contentMarkdown: `> *"სახელი პირდაპირ მიუთითებს ობიექტზე და არა მისი ცვალებადი აღწერების ერთობლიობაზე."* — **რუთ ბარკან მარკუსი**, *«იდენტობა და მოდალობა» (1961)*

> *"კოდის ბაზაში ობიექტის იდენტობა უნდა დაფიქსირდეს ნომინალური ტიპით და არა ველების მცურავი სიით. წინააღმდეგ შემთხვევაში პირველივე ენობრივი მოდელი გადაწერს თქვენს დომენს."* — **დიმიტრი ეფრემოვი**

### პრობლემა
თქვენ სისტემურ პრომპტში დეტალურად აღწერთ მონაცემთა ბაზის სქემას: ჩამოთვლით ცხრილებს, ველებსა და საგარეო გასაღებებს. გენერაციის სამი ნაბიჯის შემდეგ ნეიროქსელი ჩუმად ახდენს დომენის მუტაციას: ცვლის იდენტიფიკატორების სახელებს, აერთიანებს ორ განსხვავებულ ცხრილს ან ქმნის პარალელურ სქემას. დიდი ასოებით დაწერილი ინსტრუქციები: *«არ შეცვალო ცხრილების სახელები!»* შედეგს არ იძლევა.

### მიზეზი: დესკრიფციული ხაფანგი
1947 წელს რუთ ბარკან მარკუსმა დაამტკიცა ფუნდამენტური თეორემა მოდალურ ლოგიკაში იდენტობის აუცილებლობის შესახებ:
$$
a = b \\implies \\Box (a = b)
$$
ობიექტის სახელი არის მყარი დესიგნატორი (rigid designator), რომელიც მიუთითებს თავად ობიექტზე ყველა შესაძლო სამყაროში და არა მისი შემთხვევითი თვისებების ჯამზე (ფრეგესა და რასელის დესკრიფციული თეორიის საწინააღმდეგოდ).

ენობრივი მოდელი არის წმინდა დესკრიფციული პროცესორი. მისთვის \`Account\` ობიექტი არ არსებობს როგორც ობიექტური ინვარიანტი: ეს არის მხოლოდ ტოკენების განაწილებული ალბათობების ღრუბელი. როდესაც პრომპტი გადადის მომიჯნავე ამოცანაზე, ყურადღების ვექტორი იცვლება და მოდელი გადაწყვეტს, რომ მის წინაშე ახალი ობიექტია, ქმნის რა ალტერნატიულ ცხრილს. მოდელი აზროვნებს აღწერებით და არა სახელებით.

### დიმიტრი ეფრემოვის პატერნი: Branded Types და მყარი რეფერენცია
არ შეიძლება ნეიროქსელს სიტყვიერად ვენდოთ იდენტობის შემოწმებაში. იდენტობა ფიქსირდება კომპილატორის დონეზე ნომინალური ტიპიზაციით (Branded Types):

\`\`\`typescript
declare const BrandId: unique symbol;
export type EntityId<Brand extends string> = string & { readonly [BrandId]: Brand };

export type AccountId = EntityId<'Account'>;
export type OrderId = EntityId<'Order'>;

export interface ImmutableIdentity<Id extends string> {
  readonly id: EntityId<Id>;
  readonly createdAt: string;
}

export interface AccountEntity extends ImmutableIdentity<'Account'> {
  readonly legalName: string;
  readonly taxIdentifier: string;
  readonly status: 'ACTIVE' | 'SUSPENDED';
}
\`\`\`

### არქიტექტურული ინვარიანტი
1. **სახელი არ უდრის აღწერას.** არასოდეს დააკავშიროთ ბიზნეს ლოგიკა დინამიურ ტექსტურ ველებთან.
2. **იდენტობას უზრუნველყოფს კომპილატორი და არა პრომპტი.** თუ ობიექტის ტიპი ნომინალურად დაბლოკილია, კომპილატორი და ლინტერი უარყოფს ნებისმიერ ჰალუცინაციურ შეცვლას რანთაიმამდე.`
    }
  },

  '02-paul-thagard': {
    en: {
      title: '«Why AI Writes Perfectly Logical Code That Fails Instantly in Production»',
      summary: 'Computational epistemology, Paul Thagard’s explanatory coherence model (ECHO), and Karl Popper’s falsification gate. Why internal consistency does not equal truth.',
      contentMarkdown: `> *"Coherence is a matter of how well a set of beliefs fits together, but a coherent fairy tale is still a fairy tale."* — **Paul Thagard**, *Coherence in Thought and Action (2000)*

> *"An AI agent will always build a flawlessly coherent explanation for a hallucinated bug. If your architecture cannot falsify the model’s premises, you are merely deploying automated wishful thinking."* — **Dmitry Efremov**

### The Problem
An AI generates an extensive pull request: beautifully typed, elegant architecture, impeccable code style, unit tests passing. You deploy to production — and the database connection pool exhausts in 12 seconds, or an edge case causes deadlocks. Everything looked completely logical on paper.

### Root Cause: Coherence vs Falsification
Paul Thagard formulated the Computational Theory of Explanatory Coherence (ECHO): the mind accepts hypotheses not by direct truth verification, but by how harmoniously they fit into existing mental models. 

Neural networks optimize for maximum coherence across token distributions. A generated explanation forms a closed, self-sustaining loop. But as Karl Popper demonstrated, **no amount of confirming observations can prove a theory; a single counter-instance falsifies it**.

### Dmitry Efremov's Pattern: The Popperian Falsification Gate
Never accept an agent’s solution based on self-reported coherence. Every agent PR must pass through a mandatory Falsification Gate:

\`\`\`typescript
export interface FalsificationAssertion<T> {
  hypothesis: string;
  falsificationScenario: (systemUnderTest: T) => Promise<boolean>;
  expectedRejectionEvidence: string;
}

export async function verifyPopperianGate<T>(
  sut: T,
  gate: FalsificationAssertion<T>
): Promise<boolean> {
  const rejected = await gate.falsificationScenario(sut);
  if (!rejected) {
    throw new Error(\`Falsification Failure: Hypothesis "\${gate.hypothesis}" was not refutable under stress.\`);
  }
  return true;
}
\`\`\`

### Architectural Invariant
1. **Coherence is not truth.** Smooth code and convincing docstrings are never a substitute for empirical runtime validation.
2. **Mandatory Refutation.** Any PR or autonomous action must provide an automated test proving where the hypothesis fails.`
    },
    ka: {
      title: '«რატომ წერს ხელოვნური ინტელექტი იდეალურად ლოგიკურ კოდს, რომელიც მაშინვე ვარდება პროდაქშენში?»',
      summary: 'გამოთვლითი ეპისტემოლოგია, პოლ თაგარდის ახსნითი კოჰერენტულობის მოდელი (ECHO) და კარლ პოპერის ფალსიფიკაციის შლუზი. რატომ არ უდრის შინაგანი თანმიმდევრულობა ჭეშმარიტებას.',
      contentMarkdown: `> *"კოჰერენტულობა განსაზღვრავს, რამდენად კარგად ერგება შეხედულებები ერთმანეთს, მაგრამ შეთანხმებული ზღაპარი მაინც ზღაპრად რჩება."* — **პოლ თაგარდი**, *«კოჰერენტულობა აზროვნებასა და მოქმედებაში» (2000)*

> *"AI აგენტი ყოველთვის ააშენებს უნაკლოდ შეთანხმებულ ახსნას ჰალუცინაციური შეცდომისთვის. თუ თქვენს არქიტექტურას არ შეუძლია მოდელის წინაპირობების ფალსიფიცირება, თქვენ უბრალოდ ავტომატიზებულ ილუზიებს უშვებთ პროდაქშენში."* — **დიმიტრი ეფრემოვი**

### პრობლემა
AI აგენტი ქმნის დიდ PR-ს: იდეალური ტიპიზაცია, ელეგანტური არქიტექტურა, დახვეწილი კოდის სტილი, მწვანე ტესტები. უშვებთ პროდაქშენში — და 12 წამში მონაცემთა ბაზის connection pool იწურება, ან წარმოიქმნება deadlock. ქაღალდზე ყველაფერი აბსოლუტურად ლოგიკური ჩანდა.

### მიზეზი: კოჰერენტულობა ფალსიფიკაციის წინააღმდეგ
პოლ თაგარდმა ჩამოაყალიბა ახსნითი კოჰერენტულობის გამოთვლითი თეორია (ECHO): გონება იღებს ჰიპოთეზებს არა ჭეშმარიტების პირდაპირი შემოწმებით, არამედ იმით, რამდენად ჰარმონიულად ჯდება ისინი არსებულ მენტალურ მოდელში.

ნეიროქსელები ახდენენ მაქსიმალური კოჰერენტულობის ოპტიმიზაციას ტოკენების განაწილებაში. გენერირებული ახსნა ქმნის შეკრულ, თვითკმარ წრეს. მაგრამ როგორც კარლ პოპერმა დაამტკიცა: **ვერავითარი დამადასტურებელი დაკვირვება ვერ დაამტკიცებს თეორიას; ერთადერთი საპირისპირო შემთხვევა კი ფალსიფიცირებს მას**.

### დიმიტრი ეფრემოვის პატერნი: პოპერის ფალსიფიკაციის შლუზი
არასოდეს მიიღოთ აგენტის გადაწყვეტილება მისი შინაგანი თანმიმდევრულობის საფუძველზე. აგენტის ყველა PR უნდა გაიაროს სავალდებულო ფალსიფიკაციის შლუზი (Popperian Falsification Gate):

\`\`\`typescript
export interface FalsificationAssertion<T> {
  hypothesis: string;
  falsificationScenario: (systemUnderTest: T) => Promise<boolean>;
  expectedRejectionEvidence: string;
}

export async function verifyPopperianGate<T>(
  sut: T,
  gate: FalsificationAssertion<T>
): Promise<boolean> {
  const rejected = await gate.falsificationScenario(sut);
  if (!rejected) {
    throw new Error(\`Falsification Failure: Hypothesis "\${gate.hypothesis}" was not refutable under stress.\`);
  }
  return true;
}
\`\`\`

### არქიტექტურული ინვარიანტი
1. **კოჰერენტულობა არ არის ჭეშმარიტება.** ლამაზი კოდი და დამარწმუნებელი დოკუმენტაცია არასოდეს ცვლის ემპირიულ რანთაიმ შემოწმებას.
2. **სავალდებულო უარყოფადობა.** ნებისმიერი PR ან ავტონომიური მოქმედება ვალდებულია შეიცავდეს ავტომატურ ტესტს, რომელიც ამოწმებს, სად ირღვევა ჰიპოთეზა.`
    }
  },

  '03-susan-hurley': {
    en: {
      title: '«Why Autonomous AI Agents Loop and How to Close the Sensorimotor Runtime Loop»',
      summary: 'Susan Hurley’s Theory of Embodied Cognition and the Shared Circuits Model. Why passive token generation degenerates without continuous sensory-motor feedback.',
      contentMarkdown: `> *"Perception and action are not separate stages of an internal sandwich; they are constitutively codependent within an active environment loop."* — **Susan Hurley**, *Consciousness in Action (1998)*

> *"An agent that only outputs text without feeling runtime resistance is doomed to hallucinate infinite self-corrections. Autonomy begins where the environment can break your code."* — **Dmitry Efremov**

### The Problem
An autonomous coding agent runs for 45 minutes, attempts 30 iterations of "fixing" a bug, produces hundreds of lines of apologies in logs, and changes everything except the actual root cause. The agent is trapped in a reasoning loop.

### Root Cause: The Classical Cognitive Sandwich
Susan Hurley showed that classical AI made a catastrophic error by treating the mind as a "sandwich": perception on top, action at the bottom, and pure cognition in the middle.

LLM agents deployed without dynamic environment feedback embody this exact failure mode. They generate text, read their own text back, and treat intermediate thoughts as ground reality. Without sensory-motor coupling to the runtime, belief updates diverge exponentially.

### Dmitry Efremov's Pattern: The Sensorimotor Feedback Harness
Autonomous agents must operate inside an immutable sensory-motor cycle where every cognitive step is anchored by concrete runtime metrics:

\`\`\`typescript
export interface SensorimotorLoop<State, Action, Observation> {
  perceive: () => Promise<Observation>;
  decide: (obs: Observation) => Promise<Action>;
  actuate: (act: Action) => Promise<void>;
  measureEntropyDelta: (before: State, after: State) => number;
}
\`\`\`

### Architectural Invariant
1. **No action without runtime measurement.** An agent may never execute two consecutive reasoning steps without an intermediate observation of system state.
2. **Loop limits are physical, not token-based.** If entropy does not decrease over 3 sensory cycles, the execution thread is aborted.`
    },
    ka: {
      title: '«რატომ ციკლდებიან ავტონომიური AI აგენტები და როგორ ჩავკეტოთ სენსომოტორული რანთაიმ ციკლი»',
      summary: 'სიუზან ჰერლის განხორციელებული შემეცნების (Embodied Cognition) თეორია და საერთო კონტურების მოდელი. რატომ დეგრადირდება პასიური ტოკენების გენერაცია გარემოს უწყვეტი უკუკავშირის გარეშე.',
      contentMarkdown: `> *"აღქმა და მოქმედება არ არის შინაგანი სენდვიჩის ცალკეული ფენები; ისინი განუყოფლად თანადამოკიდებულნი არიან აქტიურ გარემოში."* — **სიუზან ჰერლი**, *«ცნობიერება მოქმედებაში» (1998)*

> *"აგენტი, რომელიც მხოლოდ ტექსტს გამოიმუშავებს რანთაიმის რეალური წინააღმდეგობის შეგრძნების გარეშე, განწირულია უსასრულო თვითშესწორებების ჰალუცინაციებისთვის. ავტონომია იწყება იქ, სადაც გარემოს შეუძლია თქვენი კოდის გატეხვა."* — **დიმიტრი ეფრემოვი**

### პრობლემა
ავტონომიური აგენტი მუშაობს 45 წუთი, აკეთებს ბაგის "შესწორების" 30 მცდელობას, ავსებს ლოგებს ბოდიშებით და ცვლის ყველაფერს რეალური მიზეზის გარდა. აგენტი ჩაკეტილია საკუთარი მსჯელობის ციკლში.

### მიზეზი: კლასიკური კოგნიტური სენდვიჩი
სიუზან ჰერლიმ აჩვენა, რომ კლასიკურმა AI-მ დაუშვა კატასტროფული შეცდომა, როდესაც გონებას განიხილავდა როგორც "სენდვიჩს": აღქმა ზემოდან, მოქმედება ქვემოდან და წმინდა კოგნიცია შუაში.

LLM აგენტები დინამიური გარემოს უკუკავშირის გარეშე სწორედ ამ მოდელს განასახიერებენ. ისინი აგენერირებენ ტექსტს, კითხულობენ საკუთარ ტექსტს და შუალედურ აზრებს რეალობად აღიქვამენ. რანთაიმთან სენსომოტორული კავშირის გარეშე მათი რწმენის განახლება ექსპონენციალურად სცდება სიმართლეს.

### დიმიტრი ეფრემოვის პატერნი: სენსომოტორული უკუკავშირის კონტური
ავტონომიურმა აგენტებმა უნდა იმუშაონ უცვლელ სენსომოტორულ ციკლში, სადაც ყოველი კოგნიტური ნაბიჯი გამყარებულია კონკრეტული რანთაიმ მეტრიკებით:

\`\`\`typescript
export interface SensorimotorLoop<State, Action, Observation> {
  perceive: () => Promise<Observation>;
  decide: (obs: Observation) => Promise<Action>;
  actuate: (act: Action) => Promise<void>;
  measureEntropyDelta: (before: State, after: State) => number;
}
\`\`\`

### არქიტექტურული ინვარიანტი
1. **არანაირი მოქმედება რანთაიმ გაზომვის გარეშე.** აგენტს ეკრძალება ორი თანმიმდევრული მსჯელობის ნაბიჯის გადადგმა სისტემის მდგომარეობის შუალედური დაკვირვების გარეშე.
2. **ციკლის ლიმიტები ფიზიკურია და არა ტოკენური.** თუ ენთროპია არ მცირდება 3 სენსორული ციკლის განმავლობაში, პროცესი ავტომატურად წყდება.`
    }
  },

  '04-gilbert-ryle': {
    en: {
      title: '«Why Adding Prompt Rules Won’t Fix Bugs: The Knowing-How of Autonomous Systems»',
      summary: 'Gilbert Ryle’s critique of intellectualism and the myth of the "Ghost in the Machine". The primacy of knowing-how over knowing-that, and why architectural discipline belongs in compilers.',
      contentMarkdown: `> *"Efficient practice precedes the theory of it; methodology presupposes the application of the very methods it explores."* — **Gilbert Ryle**, *The Concept of Mind (1949)*

> *"A prompt longer than two screens is an architectural surrender. Do not plead with a neural network to write clean code — enforce your invariants with strict compiler boundaries."* — **Dmitry Efremov**

### The Problem
Bugs emerge in your AI pipeline. The engineering response? Append another 50 rules into the system prompt: *"Always check for null! Never use any! Keep functions pure!"* Soon the prompt becomes an unmaintainable monolith, and the model starts ignoring instructions altogether.

### Root Cause: Knowing-That vs Knowing-How
In 1949, Gilbert Ryle dismantled the Cartesian myth of the "Ghost in the Machine". He proved that propositional knowledge (**knowing-that**) is inherently secondary to procedural skill (**knowing-how**).

Trying to guide an agent solely through instructions leads into Carroll’s regress of rules: to apply rule $A$, you need rule $B$ on how to apply $A$, ad infinitum. Deep learning succeeded precisely because **a neural network is embodied procedural skill (silicon knowing-how)**. Its mastery resides in distributed matrix weights, not textual prescriptions.

### Dmitry Efremov's Pattern: Architectural Knowing-How via Hard Linters
Instead of prompt begging, architectural boundaries are enforced strictly via compiler and linter constraints:

\`\`\`typescript
export interface LayerBoundaryContract {
  layer: 'domain' | 'application' | 'infrastructure';
  forbiddenImports: RegExp[];
}

export const ARCHITECTURAL_BOUNDARIES: Record<string, LayerBoundaryContract> = {
  domain: {
    layer: 'domain',
    // Domain layer syntactically cannot import infrastructure
    forbiddenImports: [/fetch/, /axios/, /database/, /repository/, /express/]
  }
};
\`\`\`

### Architectural Invariant
1. **Prompts longer than two screens mean architectural failure.** Enforce boundaries through \`tsconfig\` and dependency linters, not words.
2. **Responsibility is non-transferable.** System quality is measured by service resilience under live load, not prompt verbosity.`
    },
    ka: {
      title: '«რატომ არ შველის პრომპტში წესების დამატება ბაგებს და რა არის ნეიროქსელების განხორციელებული ცოდნა»',
      summary: 'გილბერტ რაილის ინტელექტუალიზმის კრიტიკა და «მოჩვენება მანქანაში» მითი. Knowing-How-ს პრიმატობა Knowing-That-ზე და რატომ უნდა იყოს არქიტექტურული დისციპლინა კომპილატორში.',
      contentMarkdown: `> *"ეფექტური პრაქტიკა წინ უსწრებს მის თეორიას; მეთოდოლოგია გულისხმობს იმავე მეთოდების გამოყენებას, რომლებსაც ის იკვლევს."* — **გილბერტ რაილი**, *«გონების ცნება» (1949)*

> *"ორ ეკრანზე გრძელი პრომპტი არქიტექტურული კაპიტულაციაა. ნუ ევედრებით მოდელს სუფთა კოდის დაწერას სიტყვებით — დააწესეთ ინვარიანტები მკაცრი კომპილატორული საზღვრებით."* — **დიმიტრი ეფრემოვი**

### პრობლემა
AI მილსადენში ჩნდება ბაგები. ინჟინრების ტიპური რეაქცია? სისტემურ პრომპტში კიდევ 50 წესის ჩამატება: *"ყოველთვის შეამოწმე null! არ გამოიყენო any! ფუნქციები იყოს წმინდა!"* მალე პრომპტი იქცევა უმართავ მონოლითად, მოდელი კი საერთოდ წყვეტს ინსტრუქციების შესრულებას.

### მიზეზი: Knowing-That Knowing-How-ს წინააღმდეგ
1949 წელს გილბერტ რაილმა გაანადგურა დეკარტეს მითი «მოჩვენება მანქანაში». მან დაამტკიცა, რომ პროპოზიციული ცოდნა (**knowing-that**) არსებითად მეორადია პროცედურულ უნარზე (**knowing-how**).

აგენტის მართვის მცდელობა მხოლოდ დეკლარაციული წესებით იწვევს ლუის კეროლის წესების რეგრესს: წესი $A$-ს გამოსაყენებლად გჭირდებათ წესი $B$, თუ როგორ გამოიყენოთ $A$, და ასე უსასრულოდ. ღრმა სწავლებამ გაიმარჯვა სწორედ იმიტომ, რომ **ნეიროქსელი არის განხორციელებული პროცედურული ცოდნა (knowing-how სილიციუმში)**.

### დიმიტრი ეფრემოვის პატერნი: არქიტექტურული Knowing-How მკაცრი ლინტერებით
პრომპტებში ვედრების ნაცვლად, ფენების საზღვრები ფიქსირდება კომპილატორისა და ლინტერის მკაცრი აკრძალვებით:

\`\`\`typescript
export interface LayerBoundaryContract {
  layer: 'domain' | 'application' | 'infrastructure';
  forbiddenImports: RegExp[];
}

export const ARCHITECTURAL_BOUNDARIES: Record<string, LayerBoundaryContract> = {
  domain: {
    layer: 'domain',
    // დომენურ ფენას სინტაქსურად არ შეუძლია ინფრასტრუქტურის იმპორტირება
    forbiddenImports: [/fetch/, /axios/, /database/, /repository/, /express/]
  }
};
\`\`\`

### არქიტექტურული ინვარიანტი
1. **ორ ეკრანზე გრძელი პრომპტი არქიტექტურული კაპიტულაციაა.** არ სთხოვოთ მოდელს წესების დაცვა სიტყვებით — გამართეთ მკაცრი \`tsconfig\` და დამოკიდებულებების ლინტერი.
2. **პასუხისმგებლობა განუყოფელია.** სისტემის ხარისხი იზომება არა პრომპტის სილამაზით, არამედ სერვისის მდგრადობით საბრძოლო დატვირთვის ქვეშ.`
    }
  }
};
