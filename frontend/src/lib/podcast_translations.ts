export interface LocalizedPodcastEpisode {
  title: string;
  thesis: string;
  description: string;
  timecodes: { id: number | string; timeOffsetSeconds: number; title: string }[];
}

export const PODCAST_TRANSLATIONS: Record<number, { en: LocalizedPodcastEpisode; ka: LocalizedPodcastEpisode }> = {
  1: {
    en: {
      title: 'A Name Is Not a Description. Why a Person’s Name Is Not Their Attribute List?',
      thesis: 'A name connects directly to the entity itself, not through a fluctuating bundle of descriptive traits. Principles cannot be reduced to momentary convenience.',
      description: 'An inquiry into modal logic, rigid reference, and invariant contracts. Why reducing the identity of an individual or an autonomous system’s contractual duty to a temporary description destroys the continuity of responsibility.',
      timecodes: [
        { id: 101, timeOffsetSeconds: 0, title: 'Introduction: A name is not a description' },
        { id: 102, timeOffsetSeconds: 255, title: 'Direct reference and rigid designators' },
        { id: 103, timeOffsetSeconds: 510, title: 'Necessity and possibility: Modal logic' },
        { id: 104, timeOffsetSeconds: 765, title: 'Modal logic of contracts and systemic obligations' },
        { id: 105, timeOffsetSeconds: 920, title: 'Moral dilemmas and the indelible residue of duties' },
        { id: 106, timeOffsetSeconds: 1270, title: 'Why an agent cannot rewrite its own commitments' }
      ]
    },
    ka: {
      title: 'სახელი არ უდრის აღწერას. რატომ არ არის ადამიანის სახელი მისი ნიშან-თვისებების სია?',
      thesis: 'სახელი პირდაპირ უკავშირდება ობიექტს და არა თვისებათა ცვალებად ერთობლიობას. პრინციპები არ დაიყვანება წამიერ სარგებელზე.',
      description: 'მოდალური ლოგიკის, მყარი რეფერენციისა და კონტრაქტების უცვლელობის გარჩევა. რატომ ანადგურებს პასუხისმგებლობის უწყვეტობას ადამიანის ან სისტემის მოვალეობის დროებით აღწერაზე დაყვანა.',
      timecodes: [
        { id: 101, timeOffsetSeconds: 0, title: 'შესავალი: სახელი არ უდრის აღწერას' },
        { id: 102, timeOffsetSeconds: 255, title: 'პირდაპირი რეფერენცია და მყარი დესიგნატორები' },
        { id: 103, timeOffsetSeconds: 510, title: 'აუცილებლობა და შესაძლებლობა: მოდალური ლოგიკა' },
        { id: 104, timeOffsetSeconds: 765, title: 'კონტრაქტებისა და სისტემური ვალდებულებების მოდალური ლოგიკა' },
        { id: 105, timeOffsetSeconds: 920, title: 'მორალური დილემები და ვალდებულებათა წარუშლელი ნაშთი' },
        { id: 106, timeOffsetSeconds: 1270, title: 'რატომ არ შეუძლია აგენტს საკუთარი დაპირებების გადაწერა' }
      ]
    }
  },

  2: {
    en: {
      title: 'Why Does a Logical Lie Defeat Facts?',
      thesis: 'Internal coherence and elegance do not guarantee truth. A seductive falsehood triumphs over empirical facts when inconvenient evidence is methodically eliminated from view.',
      description: 'Computational epistemology, Thagard’s ECHO coherence network, and the AIMS architecture. How to differentiate scientific hypotheses from closed propaganda frameworks, and why asking "What could falsify my belief?" is the ultimate test of integrity.',
      timecodes: [
        { id: 201, timeOffsetSeconds: 36, title: 'The right to verify your worldview' },
        { id: 202, timeOffsetSeconds: 215, title: 'ECHO: Computational network of explanations' },
        { id: 203, timeOffsetSeconds: 300, title: 'Coherence does not equal truth' },
        { id: 204, timeOffsetSeconds: 383, title: 'AIMS: The four information processes' },
        { id: 205, timeOffsetSeconds: 535, title: 'Censorship disconnects observation' },
        { id: 206, timeOffsetSeconds: 982, title: 'Propaganda cements falsehood' },
        { id: 207, timeOffsetSeconds: 1138, title: 'Emotions hold belief captive' },
        { id: 208, timeOffsetSeconds: 1331, title: 'Dominance and power networks' },
        { id: 209, timeOffsetSeconds: 1666, title: 'What could falsify my position?' },
        { id: 210, timeOffsetSeconds: 1760, title: 'Competing hypotheses in live business' }
      ]
    },
    ka: {
      title: 'რატომ ამარცხებს ლოგიკური სიცრუე ფაქტებს?',
      thesis: 'ჰიპოთეზის სინატიფე და შინაგანი თანმიმდევრულობა არ იძლევა ჭეშმარიტების გარანტიას. ლამაზი სიცრუე ამარცხებს ფაქტებს, როდესაც უხერხული მტკიცებულებები განზრახ ამოჭრილია სურათიდან.',
      description: 'გამოთვლითი ეპისტემოლოგია, ახსნითი კოჰერენტულობის ECHO ქსელი და AIMS არქიტექტურა. როგორ გავარჩიოთ მეცნიერული ჰიპოთეზა დახურული პროპაგანდისტული მოდელისგან და რატომაა შეკითხვა «რა უარყოფდა ჩემს რწმენას?» პატიოსნების მთავარი ტესტი.',
      timecodes: [
        { id: 201, timeOffsetSeconds: 36, title: 'უფლება შეამოწმო საკუთარი მსოფლმხედველობა' },
        { id: 202, timeOffsetSeconds: 215, title: 'ECHO: ახსნათა გამოთვლითი ქსელი' },
        { id: 203, timeOffsetSeconds: 300, title: 'კოჰერენტულობა არ უდრის ჭეშმარიტებას' },
        { id: 204, timeOffsetSeconds: 383, title: 'AIMS: ინფორმაციის ოთხი პროცესი' },
        { id: 205, timeOffsetSeconds: 535, title: 'ცენზურა თიშავს დაკვირვებას' },
        { id: 206, timeOffsetSeconds: 982, title: 'პროპაგანდა აწებებს სიცრუეს' },
        { id: 207, timeOffsetSeconds: 1138, title: 'ემოციები აკავებს რწმენას' },
        { id: 208, timeOffsetSeconds: 1331, title: 'დომინირებისა და ძალაუფლების ქსელები' },
        { id: 209, timeOffsetSeconds: 1666, title: 'რა შეძლებდა ჩემს უარყოფას?' },
        { id: 210, timeOffsetSeconds: 1760, title: 'კონკურენტული ჰიპოთეზები რეალურ ბიზნესში' }
      ]
    }
  },

  3: {
    en: {
      title: 'Why Do We Copy Others? Where Does Mimicry End and Your Own Decision Begin?',
      thesis: 'Mind acts in the world rather than residing locked in a cranial box. Action learns continuously from the environment’s sensory-motor response, not from passive contemplation of rules.',
      description: 'Embodied cognition, the Shared Circuits Model, and the critical distinction between mechanical copying and authentic authorship of action. Why agency emerges in the feedback loop with reality.',
      timecodes: [
        { id: 301, timeOffsetSeconds: 60, title: 'The right to author your own thoughts' },
        { id: 302, timeOffsetSeconds: 250, title: 'Reasons cannot be flattened into a single scale' },
        { id: 303, timeOffsetSeconds: 605, title: 'Consciousness acts out in the world' },
        { id: 304, timeOffsetSeconds: 700, title: 'Action learns through runtime response' },
        { id: 305, timeOffsetSeconds: 975, title: 'Imitation is not identical to duplication' },
        { id: 306, timeOffsetSeconds: 1170, title: 'Where does your own decision originate?' },
        { id: 307, timeOffsetSeconds: 1520, title: 'Propaganda erases authorship' },
        { id: 308, timeOffsetSeconds: 1680, title: 'When institutions degrade into crowds' },
        { id: 309, timeOffsetSeconds: 1825, title: 'Distribution of social roles' },
        { id: 310, timeOffsetSeconds: 1920, title: 'Organization trains mindless repetition' }
      ]
    },
    ka: {
      title: 'რატომ ვაკოპირებთ სხვებს? სად მთავრდება მიბაძვა და იწყება საკუთარი გადაწყვეტილება?',
      thesis: 'ცნობიერება მოქმედებს სამყაროში და არაა ჩაკეტილი ქალას შიგნით. მოქმედება უწყვეტად სწავლობს გარემოს სენსომოტორული უკუკავშირისგან და არა წესების პასიური ჭვრეტისგან.',
      description: 'განხორციელებული შემეცნების თეორია, საერთო კონტურების მოდელი (Shared Circuits Model) და განსხვავება მექანიკურ კოპირებასა და მოქმედების ჭეშმარიტ ავტორობას შორის. რატომ იბადება ნების თავისუფლება რეალობასთან უკუკავშირის მარყუჟში.',
      timecodes: [
        { id: 301, timeOffsetSeconds: 60, title: 'უფლება იყო საკუთარი აზრის ავტორი' },
        { id: 302, timeOffsetSeconds: 250, title: 'მიზეზები არ დაიყვანება ერთ შკალაზე' },
        { id: 303, timeOffsetSeconds: 605, title: 'ცნობიერება მოქმედებს სამყაროში' },
        { id: 304, timeOffsetSeconds: 700, title: 'მოქმედება სწავლობს გარემოს პასუხისგან' },
        { id: 305, timeOffsetSeconds: 975, title: 'მიბაძვა არ უდრის კოპირებას' },
        { id: 306, timeOffsetSeconds: 1170, title: 'სად იწყება საკუთარი გადაწყვეტილება?' },
        { id: 307, timeOffsetSeconds: 1520, title: 'პროპაგანდა შლის ავტორობას' },
        { id: 308, timeOffsetSeconds: 1680, title: 'როცა ინსტიტუტები დეგრადირდებიან ბრბოდ' },
        { id: 309, timeOffsetSeconds: 1825, title: 'სოციალური როლების განაწილება' },
        { id: 310, timeOffsetSeconds: 1920, title: 'ორგანიზაცია ასწავლის გამეორებას' }
      ]
    }
  },

  4: {
    en: {
      title: 'To Know Is Not to Be Able. Why Instructions Do Not Save You?',
      thesis: 'The categorical mistake is looking for the "Ghost in the Machine". Practical competence (knowing-how) is fundamentally primary to formulaic regulations (knowing-that).',
      description: 'Refusal of Cartesian dualism and the infinite regress of meta-rules. Why compliance checklists fail businesses during crisis, and how intelligence manifests directly in execution under live load.',
      timecodes: [
        { id: 401, timeOffsetSeconds: 74, title: 'Thinking takes place in action' },
        { id: 402, timeOffsetSeconds: 128, title: 'The categorical mistake' },
        { id: 403, timeOffsetSeconds: 299, title: 'Skill precedes instruction' },
        { id: 404, timeOffsetSeconds: 523, title: 'Infinite regress of rules' },
        { id: 405, timeOffsetSeconds: 652, title: 'An algorithm bears no skin in the game' },
        { id: 406, timeOffsetSeconds: 880, title: 'Weight of evidence over institutional status' },
        { id: 407, timeOffsetSeconds: 1200, title: 'Properties reveal themselves under load' },
        { id: 408, timeOffsetSeconds: 1370, title: 'Checklists cannot save business' },
        { id: 409, timeOffsetSeconds: 1650, title: 'Thick descriptions of reality' },
        { id: 410, timeOffsetSeconds: 1858, title: 'Code is never neutral' }
      ]
    },
    ka: {
      title: 'იცოდე — არ ნიშნავს შეგეძლოს. რატომ არ გვიშველის ინსტრუქციები?',
      thesis: 'კატეგორიული შეცდომაა «მოჩვენების» ძებნა მანქანაში. პრაქტიკული უნარი (knowing-how) პირველადია ფორმულირებებისა და რეგლამენტების ცოდნაზე (knowing-that).',
      description: 'უარის თქმა მეტა-წესების უსასრულო რეგრესზე. რატომ ვერ იხსნის რეგლამენტები ბიზნესს კრიზისის დროს და როგორ ვლინდება ინტელექტი უშუალოდ საბრძოლო დატვირთვის ქვეშ.',
      timecodes: [
        { id: 401, timeOffsetSeconds: 74, title: 'აზროვნება ხდება მოქმედებაში' },
        { id: 402, timeOffsetSeconds: 128, title: 'კატეგორიული შეცდომა' },
        { id: 403, timeOffsetSeconds: 299, title: 'უნარი ინსტრუქციაზე ადრე' },
        { id: 404, timeOffsetSeconds: 523, title: 'წესების უსასრულო რეგრესი' },
        { id: 405, timeOffsetSeconds: 652, title: 'ალგორითმს არ აქვს პირადი რისკი' },
        { id: 406, timeOffsetSeconds: 880, title: 'არგუმენტის წონა სტატუსზე მნიშვნელოვანია' },
        { id: 407, timeOffsetSeconds: 1200, title: 'თვისებები ჩანს დატვირთვის ქვეშ' },
        { id: 408, timeOffsetSeconds: 1370, title: 'რეგლამენტები ბიზნესს ვერ შველის' },
        { id: 409, timeOffsetSeconds: 1650, title: 'რეალობის გაჯერებული აღწერა' },
        { id: 410, timeOffsetSeconds: 1858, title: 'კოდი არასოდესაა ნეიტრალური' }
      ]
    }
  }
};
