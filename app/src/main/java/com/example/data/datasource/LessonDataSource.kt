package com.example.data.datasource

import com.example.data.model.Difficulty
import com.example.data.model.Lesson
import com.example.data.model.TypingLanguage

object LessonDataSource {

    val englishLessons: List<Lesson> = listOf(
        Lesson(
            id = "en_lesson_1",
            language = TypingLanguage.ENGLISH,
            category = "Basics",
            categoryBn = "প্রাথমিক পাঠ",
            title = "Home Row Keys (ASDF JKL;)",
            titleBn = "হোম রো কি অনুশীলন",
            description = "Master your fingers resting on the home row keys: A S D F and J K L ;",
            descriptionBn = "আঙুলগুলো হোম রো কি-তে রেখে টাইপ করা শিখুন",
            targetText = "asdf jkl; asdf jkl; fads jads klas flak dads salads falls flash ask flask",
            difficulty = Difficulty.BEGINNER,
            targetWpm = 25
        ),
        Lesson(
            id = "en_lesson_2",
            language = TypingLanguage.ENGLISH,
            category = "Basics",
            categoryBn = "প্রাথমিক পাঠ",
            title = "Top Row Keys (QWERTY UIOP)",
            titleBn = "টপ রো কি অনুশীলন",
            description = "Reach upward to type letters on the upper keyboard row.",
            descriptionBn = "উপরের সারির অক্ষরগুলো নির্ভুলভাবে টাইপ করুন",
            targetText = "quiet power write pretty quote tree party query water route output report worry",
            difficulty = Difficulty.BEGINNER,
            targetWpm = 30
        ),
        Lesson(
            id = "en_lesson_3",
            language = TypingLanguage.ENGLISH,
            category = "Basics",
            categoryBn = "প্রাথমিক পাঠ",
            title = "Bottom Row Keys (ZXCV BNM)",
            titleBn = "বটম রো কি অনুশীলন",
            description = "Master reaching down to the lower keyboard row with precision.",
            descriptionBn = "নিচের সারির অক্ষরগুলো নিখুঁতভাবে টাইপ করুন",
            targetText = "zinc vanish marble combine bronze calm zebra vaccine maximize mix banner move",
            difficulty = Difficulty.BEGINNER,
            targetWpm = 30
        ),
        Lesson(
            id = "en_lesson_4",
            language = TypingLanguage.ENGLISH,
            category = "Intermediate",
            categoryBn = "মধ্যবর্তী পাঠ",
            title = "Numbers & Punctuation",
            titleBn = "সংখ্যা ও যতিচিহ্ন",
            description = "Practice shifting for symbols and pressing numeric keys quickly.",
            descriptionBn = "সংখ্যা এবং বিশেষ চিহ্ন দ্রুত টাইপ করা শিখুন",
            targetText = "Order #4092 cost $129.50 on 2026-10-22. Did you see the 100% discount rate?!",
            difficulty = Difficulty.INTERMEDIATE,
            targetWpm = 32
        ),
        Lesson(
            id = "en_lesson_5",
            language = TypingLanguage.ENGLISH,
            category = "Pangrams",
            categoryBn = "প্যানগ্রাম",
            title = "The Classic Pangram",
            titleBn = "ক্লাসিক প্যানগ্রাম অনুশীলন",
            description = "Sentences that contain every letter of the alphabet from A to Z.",
            descriptionBn = "ইংরেজি বর্ণমালার প্রতিটি অক্ষর যুক্ত বাক্য",
            targetText = "the quick brown fox jumps over the lazy dog while five boxing wizards jump quickly",
            difficulty = Difficulty.INTERMEDIATE,
            targetWpm = 40
        ),
        Lesson(
            id = "en_lesson_6",
            language = TypingLanguage.ENGLISH,
            category = "Speed Fluency",
            categoryBn = "গতি ও সাবলীলতা",
            title = "Continuous Rhythm & Flow",
            titleBn = "ধারাবাহিক ছন্দ ও সাবলীলতা",
            description = "Develop unbroken typing rhythm with commonly used English phrases.",
            descriptionBn = "নিয়মিত ছন্দ বজায় রেখে সাবলীল গতি অর্জন করুন",
            targetText = "Success in touch typing is built on muscle memory and persistent daily practice. Keep your posture straight, eyes on the screen, and let your fingers find their natural flow.",
            difficulty = Difficulty.ADVANCED,
            targetWpm = 50
        )
    )

    val banglaLessons: List<Lesson> = listOf(
        Lesson(
            id = "bn_lesson_1",
            language = TypingLanguage.BANGLA,
            category = "স্বরবর্ণ",
            categoryBn = "স্বরবর্ণ",
            title = "বাংলা স্বরবর্ণ অনুশীলন",
            titleBn = "বাংলা স্বরবর্ণ অনুশীলন",
            description = "অ আ ই ঈ উ ঊ ঋ এ ঐ ও ঔ - স্বরবর্ণগুলোর ধারাবাহিক অনুশীলন।",
            descriptionBn = "অ আ ই ঈ উ ঊ ঋ এ ঐ ও ঔ - স্বরবর্ণগুলোর ধারাবাহিক অনুশীলন।",
            targetText = "অ আ ই ঈ উ ঊ ঋ এ ঐ ও ঔ অ আ ই ঈ উ ঊ এ ঐ ও ঔ আকাশ আলো আশা ইচ্ছে ঈগল উপবন ঋণ ঐক্য ওজন ঔষধ",
            difficulty = Difficulty.BEGINNER,
            targetWpm = 20
        ),
        Lesson(
            id = "bn_lesson_2",
            language = TypingLanguage.BANGLA,
            category = "ব্যঞ্জনবর্ণ",
            categoryBn = "ব্যঞ্জনবর্ণ ১",
            title = "ক খ গ ঘ ঙ চ ছ জ ঝ ঞ",
            titleBn = "ক বর্গ ও চ বর্গের বর্ণমালা",
            description = "ক থেকে ঞ পর্যন্ত ব্যঞ্জনবর্ণগুলোর সহজ টাইপিং অনুশীলন।",
            descriptionBn = "ক থেকে ঞ পর্যন্ত ব্যঞ্জনবর্ণগুলোর সহজ টাইপিং অনুশীলন।",
            targetText = "ক খ গ ঘ ঙ চ ছ জ ঝ ঞ কমল খাতা গোলাপ ঘর চার ছাতা জল ঝড় কলা গান ছবি চাঁদ জীবন",
            difficulty = Difficulty.BEGINNER,
            targetWpm = 22
        ),
        Lesson(
            id = "bn_lesson_3",
            language = TypingLanguage.BANGLA,
            category = "ব্যঞ্জনবর্ণ",
            categoryBn = "ব্যঞ্জনবর্ণ ২",
            title = "ট ঠ ড ঢ ণ ত থ দ ধ ন",
            titleBn = "ট বর্গ ও ত বর্গের বর্ণমালা",
            description = "ট থেকে ন পর্যন্ত ব্যঞ্জনবর্ণ দিয়ে শব্দ তৈরি ও টাইপিং।",
            descriptionBn = "ট থেকে ন পর্যন্ত ব্যঞ্জনবর্ণ দিয়ে শব্দ তৈরি ও টাইপিং।",
            targetText = "টাকা ঠাকুর ডালিম ঢাক তারা থালা নদী ধান নৌকা নদী দেশ নদীপথ বাতাস তারা দল",
            difficulty = Difficulty.BEGINNER,
            targetWpm = 24
        ),
        Lesson(
            id = "bn_lesson_4",
            language = TypingLanguage.BANGLA,
            category = "ব্যঞ্জনবর্ণ",
            categoryBn = "ব্যঞ্জনবর্ণ ৩",
            title = "প ফ ব ভ ম য র ল শ ষ স হ",
            titleBn = "প বর্গ এবং অন্যান্য ব্যঞ্জনবর্ণ",
            description = "প থেকে হ এবং ড় ঢ় য় সম্বলিত দৈনন্দিন শব্দের অনুশীলন।",
            descriptionBn = "প থেকে হ এবং ড় ঢ় য় সম্বলিত দৈনন্দিন শব্দের অনুশীলন।",
            targetText = "পাখি ফুল বই ভারত মেঘ নদী সূর্য পাহাড় হাসিখুশি আকাশ রঙ বৃষ্টি গাড়ি সময় সহজ সুন্দর",
            difficulty = Difficulty.INTERMEDIATE,
            targetWpm = 26
        ),
        Lesson(
            id = "bn_lesson_5",
            language = TypingLanguage.BANGLA,
            category = "কার-চিহ্ন",
            categoryBn = "কার-চিহ্ন ও মাত্রা",
            title = "আ-কার থেকে ঔ-কার অনুশীলন",
            titleBn = "কার-চিহ্ন ও মাত্রা অনুশীলন",
            description = "বিভিন্ন বর্ণের সাথে কার-চিহ্ন জুড়ে দ্রুত টাইপ করুন।",
            descriptionBn = "বিভিন্ন বর্ণের সাথে কার-চিহ্ন জুড়ে দ্রুত টাইপ করুন।",
            targetText = "মা মাটি মানুষ বাবা ভাই বোন সোনার বাংলা সবুজ ঘাস মিষ্টি ফল গভীর নদী খোলা আকাশ আলোর দিশারী",
            difficulty = Difficulty.INTERMEDIATE,
            targetWpm = 30
        ),
        Lesson(
            id = "bn_lesson_6",
            language = TypingLanguage.BANGLA,
            category = "যুক্তবর্ণ",
            categoryBn = "যুক্তবর্ণ ও কঠিন শব্দ",
            title = "যুক্তবর্ণ ও জ্ঞানচর্চা",
            titleBn = "যুক্তবর্ণ ও জ্ঞানচর্চা",
            description = "ক্ষ, জ্ঞ, ক্ত, ঙ্গ, চ্ছ, ষ্ণ ইত্যাদি যুক্তাক্ষরের সাবলীল টাইপিং।",
            descriptionBn = "ক্ষ, জ্ঞ, ক্ত, ঙ্গ, চ্ছ, ষ্ণ ইত্যাদি যুক্তাক্ষরের সাবলীল টাইপিং।",
            targetText = "বিজ্ঞান প্রযুক্তি শিক্ষা শৃঙ্খলা শ্রদ্ধা আনন্দ চমৎকার বিশ্বস্ত স্বাধীনতা উজ্জ্বল প্রজ্ঞা সাফল্য সন্ধান",
            difficulty = Difficulty.ADVANCED,
            targetWpm = 35
        )
    )

    val speedTestParagraphsEnglish: List<Pair<String, String>> = listOf(
        "Govt Job 1: Public Administration, Secretariat Instructions, and Citizen Services" to "Public administration serves as the indispensable structural backbone of democratic governance and socio-economic transformation in Bangladesh. The primary purpose of government machinery is to deliver quality, timely, transparent, and citizen-centric services with uncompromising integrity and accountability. According to official Secretariat Instructions, civil servants are mandated to meticulously examine official notes, cross-reference statutory enactments, and execute state policies with steadfast dedication. Rapid advancements in information and communication technology have made paperless file processing, automated archival retrieval, and digital decision-making an everyday institutional standard across all ministries and directorates. Every aspiring computer operator, administrative officer, and executive assistant must cultivate exceptional keyboarding accuracy, sharp editorial judgment, and continuous analytical capability to discharge state responsibilities smoothly. Diligent documentation, prompt disposal of public petitions, courteous public interface, and strict adherence to administrative hierarchies form the hallmark of an exemplary civil servant dedicated to national welfare and institutional excellence.",
        "Govt Job 2: Constitutional Governance, Fundamental Rights, and the Rule of Law" to "The Constitution of the People's Republic of Bangladesh represents the supreme organic law of the sovereign republic, enshrining equality of opportunity, democratic accountability, and universal human dignity. Fundamental rights guaranteed in Part Three of the Constitution ensure that every citizen enjoys equal protection before the law without any discrimination based on religion, race, caste, sex, or birthplace. A flourishing parliamentary democracy demands mutual institutional harmony and robust checks and balances among the executive branch, legislature, and an independent judiciary. Legal certainty, procedural fairness, and unhindered access to speedy justice empower vulnerable communities, eliminate arbitrary exercise of state power, and preserve systemic stability. Upholding constitutional supremacy is not merely a legal mandate but a sacred patriotic obligation for public servants at all administrative tiers to foster national solidarity and sustainable social progress.",
        "Govt Job 3: National Economic Expansion, Revenue Mobilization, and Fiscal Prudence" to "Sustainable domestic economic self-reliance rests predominantly on robust revenue collection, prudent public expenditure management, and long-term fiscal discipline. The National Board of Revenue plays a crucial catalytic role in mobilizing direct income taxes, value-added taxes, and customs duties to finance progressive social development programs and transformative infrastructure megaprojects. Digitalized tax return filing systems, electronic fiscal devices, and interoperable automated treasury systems significantly reduce systemic leakages, eliminate cumbersome bureaucratic bottlenecks, and cultivate voluntary compliance among corporate and individual taxpayers. Reducing chronic dependence on external borrowing and optimizing sovereign creditworthiness fosters vibrant investment climates and generates meaningful employment for millions of talented young graduates nationwide. Equitable resource redistribution and sound macroeconomic planning ensure inclusive prosperity across rural and urban landscapes.",
        "Govt Job 4: Smart Bangladesh Roadmap and Digital Public Infrastructure" to "The visionary blueprint of Smart Bangladesh is firmly anchored upon four strategic foundations: Smart Citizens, Smart Government, Smart Economy, and Smart Society. Government ministries, subordinate directorates, and statutory autonomous bodies have integrated comprehensive e-governance solutions, national single-window platforms, and automated public procurement portals to deliver citizen-centric public utilities rapidly. Cloud infrastructure, high-speed optical fiber connectivity, and decentralized digital service centers bridge geographic disparities, connecting peripheral rural villages directly to central secretariat networks. Office assistants, stenographers, and database managers must acquire superior digital dexterity and high-speed typing acumen to meet the burgeoning demands of contemporary automated workplaces. Embracing artificial intelligence, secure data governance, and paperless administrative workflows paves the way for a knowledge-driven, prosperous, and globally competitive nation.",
        "Govt Job 5: The Historic Liberation War and Democratic Nation Building" to "The glorious Liberation War of nineteen seventy-one remains the perennial source of collective inspiration, supreme pride, and patriotic commitment for the Bengali nation. Through immense sacrifices, profound devotion, and the unyielding heroism of freedom fighters, Bangladesh emerged as an independent sovereign republic on the global map. Preserving the historical sanctity of our struggle, honoring immortal martyrs, and transmitting our egalitarian constitutional ethos to incoming generations constitute sacred civil obligations. Public servants must consistently emulate the spirit of selfless sacrifice, absolute loyalty to the republic, and non-partisan devotion to building a peaceful, inclusive, and self-sufficient motherland. National unity, secular values, social justice, and collective dignity form the enduring pillars upon which modern Bangladesh continues its march toward comprehensive socio-economic development.",
        "Govt Job 6: Human Capital Development, Technical Education, and Youth Empowerment" to "Nurturing an agile, technically capable, and ethically grounded human workforce is fundamental to harnessing Bangladesh's demographic dividend over the coming decades. Reorienting primary, secondary, and tertiary educational curricula toward applied science, vocational skills, artificial intelligence, and multilingual communication ensures international competitive standing. Modernized technical training institutes, subsidized polytechnic apprenticeships, and specialized university research incubators bridge lingering mismatches between classroom instruction and rapidly evolving industrial requirements. Investing comprehensively in young talents catalyzes domestic entrepreneurial ventures and elevates national labor productivity across diverse industrial sectors. Equipping the youth with cutting-edge analytical and professional competencies ensures long-term industrial resilience and equitable socioeconomic advancement.",
        "Govt Job 7: Climate Change Resilience, Environmental Governance, and Delta Plan 2100" to "Bangladesh stands prominently on the global frontline of environmental fragility, facing intensified cyclones, erratic monsoons, coastal salinity intrusion, and rapid riverbank erosion. Mitigating climate vulnerabilities necessitates aggressive afforestation, wetland preservation, circular waste management, and decisive transitions toward clean renewable energy sources. National initiatives like the Bangladesh Delta Plan twenty-one hundred establish comprehensive hydrological frameworks to ensure sustainable water governance, robust ecological preservation, and disaster risk reduction. Sustainable development demands balanced equilibrium between rapid industrial manufacturing and uncompromising environmental stewardship to safeguard future generations. Strengthening institutional coordination and fostering grassroots environmental consciousness remain central to achieving environmental sustainability and ecological harmony across all river basins.",
        "Govt Job 8: Judicial Independence, Legal Access, and Dispute Resolution" to "An impartial, transparent, and accessible judicial hierarchy is the definitive guarantee for preserving citizen freedoms and constitutional equilibrium. Clear separation of powers enables magistrates, appellate judges, and legal officers to interpret statutory provisions without fear, favor, or external influence. Judicial reforms, alternative dispute resolution mechanisms, and modern digital court proceedings expedite backlog disposal, reduce litigation expenses for impoverished citizens, and strengthen institutional trust. When justice is administered with punctuality, fairness, and unwavering impartiality, public faith in governmental legitimacy remains steadfast and secure. Upholding legal integrity and ensuring equal legal protection under all circumstances protects the dignity and human rights of every individual member of society.",
        "Govt Job 9: Agricultural Modernization, Food Sovereignty, and Rural Economy" to "Agricultural self-sufficiency forms the cornerstone of national sovereign stability and rural socio-economic well-being across Bangladesh. Innovative agronomic research, flood-resilient crop varieties, digitized weather forecasting, and subsidized mechanized harvesting tools have dramatically enhanced agricultural yields despite shrinking arable land. Strengthening cold storage infrastructure, competitive supply-chain logistics, and farmer-oriented credit facilities mitigates post-harvest wastage and protects domestic producer margins. Ensuring a well-nourished, physically resilient population through diverse food production constitutes the fundamental bedrock of enduring public prosperity. Sustainable agricultural practices combined with agro-processing enterprises empower rural communities and stimulate robust economic vitality nationwide.",
        "Govt Job 10: Civil Service Ethics, Administrative Integrity, and Good Governance" to "The prestige, efficacy, and moral legitimacy of civil administration depend unconditionally on the professional ethos, absolute impartiality, and personal probity of its civil servants. Official codes of conduct mandate that public resources be utilized scrupulously, decision-making rationales be thoroughly documented, and citizen complaints be addressed with empathy and urgency. Implementing the Right to Information legislation and citizen charters fosters genuine public participation, curbs bureaucratic malpractices, and reinforces institutional accountability. Dedicated public servants who conduct themselves with humility, steadfast honor, and patriotic zeal inspire widespread civic trust and elevate national honor. Maintaining strict discipline, procedural transparency, and unswerving commitment to the rule of law guarantees lasting peace, public confidence, and institutional progress."
    )

    val speedTestParagraphsBangla: List<Pair<String, String>> = listOf(
        "সরকারি চাকরি ১ : জনপ্রশাসন মন্ত্রণালয় ও সচিবালয় নির্দেশিকা" to "গণপ্রজাতন্ত্রী বাংলাদেশ সরকারের জনপ্রশাসন মন্ত্রণালয় দেশের সামগ্রিক প্রশাসনিক ব্যবস্থাপনার নীতি নির্ধারণ ও শৃঙ্খলা নিশ্চিতকরণের কেন্দ্রীয় প্রতিষ্ঠান। সরকারি কর্মচারীগণের কর্তব্যনিষ্ঠা, সততা, সময়ানুবর্তিতা এবং স্বচ্ছতার ওপর নির্ভর করে একটি কার্যকর ও জনবান্ধব প্রশাসনিক ব্যবস্থার সাফল্য। সচিবালয় নির্দেশিকা অনুযায়ী প্রতিটি সরকারি নথি উপস্থাপন, পত্র যোগাযোগ এবং সারসংক্ষেপ প্রণয়নে নির্ভুল ভাষা, তথ্যনিষ্ঠ বক্তব্য এবং যথাযথ নিয়মানুবর্তিতা অনুসরণ করা বাধ্যতামূলক। আধুনিক তথ্যপ্রযুক্তির যুগে ডিজিটাল নথি ব্যবস্থাপনা বা ই-ফাইলিং ব্যবস্থার সফল বাস্তবায়নের ফলে সরকারি সেবা জনগণের দোরগোড়ায় পৌঁছে দেওয়া সম্ভব হচ্ছে। কম্পিউটার অপারেটর, সাঁট-মুদ্রাক্ষরিক এবং অফিস সহায়কদের দ্রুত ও নির্ভুল টাইপিং দক্ষতা এবং দাপ্তরিক নির্দেশিকা সম্পর্কে পরিষ্কার ধারণা থাকা অতীব জরুরি। জনগণের ট্যাক্সের টাকায় পরিচালিত সরকারি অফিসসমূহে প্রতিটি নাগরিককে বিনম্র আচরণ ও আন্তরিকতার সহিত সেবা প্রদান করাই সরকারি কর্মচারীদের পরম পবিত্র সাংবিধানিক দায়িত্ব। নিয়মানুবর্তিতা ও জবাবদিহিতা নিশ্চিতকরণের মাধ্যমে একটি আধুনিক ও গণমুখী প্রশাসন গড়ে তোলা সম্ভব।",
        "সরকারি চাকরি ২ : গণপ্রজাতন্ত্রী বাংলাদেশের সংবিধান ও মৌলিক অধিকার" to "গণপ্রজাতন্ত্রী বাংলাদেশের সংবিধান রাষ্ট্রের সর্বোচ্চ আইন এবং গণতান্ত্রিক শাসনব্যবস্থার মূল ভিত্তি। সংবিধানে আইনের দৃষ্টিতে সকল নাগরিকের সমতা, মৌলিক মানবাধিকার এবং ব্যক্তি স্বাধীনতার সুস্পষ্ট নিশ্চয়তা প্রদান করা হয়েছে। একটি শোষণমুক্ত, ন্যায়ভিত্তিক এবং কল্যাণমুখী রাষ্ট্র বিনির্মাণে আইনের শাসন প্রতিষ্ঠা অপরিহার্য শর্ত। রাষ্ট্রের তিনটি প্রধান অঙ্গ—নির্বাহী বিভাগ, আইনসভা এবং বিচার বিভাগের পারস্পরিক ভারসাম্য ও দায়িত্বশীলতার মাধ্যমে দেশ পরিচালিত হয়। সংবিধানে উল্লেখিত রাষ্ট্র পরিচালনার মূলনীতিসমূহ অর্থনৈতিক সমতা, সার্বজনীন শিক্ষা, জনস্বাস্থ্য সুরক্ষা এবং পরিবেশ সংরক্ষণের দিকনির্দেশনা প্রদান করে। বিচার বিভাগের স্বাধীনতা এবং আইনের নিরপেক্ষ প্রয়োগ প্রতিটি নাগরিকের অবিচ্ছেদ্য অধিকার। নাগরিকের অধিকার সুরক্ষার পাশাপাশি রাষ্ট্রের প্রতি নাগরিকের কর্তব্যসমূহ যথাযথভাবে পালন করা প্রতিটি দেশপ্রেমিক মানুষের নৈতিক দায়িত্ব। সাংবিধানিক মূল্যবোধ সমুন্নত রেখে দেশপ্রেমে উদ্বুদ্ধ হয়ে কাজ করলে জাতীয় সংহতি ও সমৃদ্ধি নিশ্চিত হবে।",
        "সরকারি চাকরি ৩ : জাতীয় রাজস্ব বোর্ড ও টেকসই অর্থনৈতিক প্রবৃদ্ধি" to "অর্থনৈতিক স্বনির্ভরতা অর্জন এবং উন্নয়ন বাজেট অর্থায়নে অভ্যন্তরীণ সম্পদ আহরণের গুরুত্ব অপরিসীম। জাতীয় রাজস্ব বোর্ড প্রত্যক্ষ আয়কর, মূল্য সংযোজন কর এবং কাস্টমস শুল্ক আদায়ের মাধ্যমে রাষ্ট্রীয় ব্যয় নির্বাহে প্রধানতম ভূমিকা পালন করে থাকে। আধুনিক স্বয়ংক্রিয় অনলাইন ট্যাক্স রিটার্ন দাখিল এবং ইলেকট্রনিক ফিসকাল ডিভাইসের প্রবর্তন করদাতাদের সময় ও খরচ সাশ্রয় করে করবান্ধব পরিবেশ সৃষ্টি করেছে। কর ফাঁকি রোধ, স্বচ্ছতা নিশ্চিতকরণ এবং করের আওতা সম্প্রসারণের ফলে বিদেশি ঋণের ওপর নির্ভরতা অনেকাংশেই হ্রাস পেয়েছে। পদ্মা সেতু, মেট্রোরেল, রূপপুর পারমাণবিক বিদ্যুৎ কেন্দ্র ও গভীর সমুদ্র বন্দরের মতো বৃহৎ মেগা প্রকল্পসমূহ বাস্তবায়নে দেশীয় রাজস্বের অবদান অনস্বীকার্য। প্রতিটি সচেতন নাগরিকের নিয়মিত কর পরিশোধের মানসিকতা গড়ে উঠলে দেশ দ্রুত সমৃদ্ধির শিখরে পৌঁছাবে। রাজস্ব আহরণ প্রক্রিয়ায় প্রযুক্তির সর্বোচ্চ ব্যবহার দুর্নীতি প্রতিরোধে এবং সুশাসন প্রতিষ্ঠায় অগ্রণী ভূমিকা পালন করছে।",
        "সরকারি চাকরি ৪ : স্মার্ট বাংলাদেশ বিনির্মাণ ও ই-গভর্ন্যান্স" to "স্মার্ট সিটিজেন, স্মার্ট গভর্নমেন্ট, স্মার্ট ইকোনমি এবং স্মার্ট সোসাইটি—এই চারটি মূল স্তম্ভের ওপর ভিত্তি করে স্মার্ট বাংলাদেশ গড়ার মহাপরিকল্পনা গৃহীত হয়েছে। আধুনিক বিশ্বে তথ্য ও যোগাযোগ প্রযুক্তির দ্রুত বিকাশ প্রশাসনিক কাজের গতিশীলতা বহুলাংশে বৃদ্ধি করেছে। ই-টেন্ডারিং, অনলাইন ভূমিসেবা, জাতীয় পরিচয়পত্র যাচাই এবং ডিজিটাল পেমেন্ট গেটওয়ের মাধ্যমে নাগরিকরা ঘরে বসেই সেবা গ্রহণ করছেন। সরকারি দপ্তরে কর্মরত কম্পিউটার অপারেটরদের প্রযুক্তিগত দক্ষতা, সাইবার নিরাপত্তা সচেতনতা এবং দ্রুত টাইপিং পারদর্শিতা এখন সময়ের সর্বশ্রেষ্ঠ দাবি। ইউনিয়ন ডিজিটাল সেন্টার থেকে শুরু করে কেন্দ্রীয় সচিবালয় পর্যন্ত সংযুক্ত উচ্চগতির ফাইবার অপটিক নেটওয়ার্ক গ্রামীণ ও শহুরে বৈষম্য নিরসনে বৈপ্লবিক পরিবর্তন এনেছে। আগামীর চতুর্থ শিল্পবিপ্লবের চ্যালেঞ্জ মোকাবিলায় আমাদের তরুণ প্রজন্মকে কৃত্রিম বুদ্ধিমত্তা ও প্রোগ্রামিং বিদ্যায় পারদর্শী হতে হবে। প্রযুক্তিভিত্তিক জনকল্যাণমূলক সমাজ ব্যবস্থাপনাই হবে আধুনিক উন্নত বাংলাদেশের চালিকাশক্তি।",
        "সরকারি চাকরি ৫ : মুক্তিযুদ্ধ, স্বাধীনতা ও জাতীয় চেতনার ভিত্তি" to "১৯৭১ সালের মহান মুক্তিযুদ্ধ আমাদের জাতীয় ইতিহাসের সর্বশ্রেষ্ঠ অধ্যায় এবং অহংকারের প্রতীক। জাতির পিতা বঙ্গবন্ধু শেখ মুজিবুর রহমানের আহ্বানে সাড়া দিয়ে বাংলার সর্বস্তরের কৃষক, শ্রমিক, ছাত্র ও সাধারণ মানুষ মরণপণ সংগ্রামে ঝাঁপিয়ে পড়েছিলেন। দীর্ঘ নয় মাসের রক্তক্ষয়ী যুদ্ধ, ত্রিশ লাখ শহীদের আত্মদান এবং দুই লাখ মা-বোনের সম্ভ্রমের বিনিময়ে অর্জিত হয়েছে আমাদের প্রিয় লাল-সবুজের পতাকা। বীর মুক্তিযোদ্ধাদের অসীম সাহস ও আত্মত্যাগ পৃথিবীর মুক্তিকামী মানুষের জন্য এক চিরন্তন প্রেরণার উৎস। মুক্তিযুদ্ধের মূল চেতনা ছিল একটি শোষণহীন, অসাম্প্রদায়িক ও গণতান্ত্রিক বাংলাদেশ গড়া। সেই আদর্শকে বুকে ধারণ করে দেশপ্রেম ও নিষ্ঠার সাথে দায়িত্ব পালন করা প্রত্যেক নাগরিকের নৈতিক কর্তব্য। বিজয়ের এই গৌরবোজ্জ্বল ইতিহাসকে নবপ্রজন্মের অন্তরে চিরজাগরুক রাখতে হবে। জাতীয় ঐক্য ও সার্বভৌমত্ব রক্ষায় মুক্তিযুদ্ধের চেতনাকে সকল কর্মকাণ্ডের কেন্দ্রবিন্দুতে রাখা উচিত।",
        "সরকারি চাকরি ৬ : প্রাথমিক ও গণশিক্ষা এবং মানবসম্পদ উন্নয়ন" to "একটি দেশের প্রকৃত উন্নয়ন নির্ভর করে দক্ষ ও সুশিক্ষিত মানবসম্পদ গঠনের ওপর। প্রাথমিক স্তর থেকেই প্রতিটি শিশুর মেধা, মনন, নৈতিকতা এবং সৃজনশীলতার বিকাশ নিশ্চিত করা রাষ্ট্রের অন্যতম প্রধান দায়িত্ব। কারিগরি ও বৃত্তিমূলক শিক্ষার সম্প্রসারণ, আধুনিক বিজ্ঞানসম্মত পাঠ্যক্রম এবং মাল্টিমিডিয়া ক্লাসরুমের ব্যবহার দেশের শিক্ষা ব্যবস্থায় ইতিবাচক রূপান্তর এনেছে। নারীশিক্ষার বিস্তার এবং উপবৃত্তি কার্যক্রমের সফলতার কারণে শিক্ষাক্ষেত্রে লিঙ্গ সমতা অর্জিত হয়েছে। শিক্ষকদের নিয়মিত আধুনিক প্রশিক্ষণ এবং পাঠদানে আন্তরিকতা শিক্ষার্থীদের বাস্তবমুখী দক্ষতায় গড়ে তোলে। মেধা ও শ্রমের সঠিক সমন্বয়ে গড়ে ওঠা শিক্ষিত যুবসমাজই আমাদের অর্থনৈতিক প্রবৃদ্ধির মূল চালিকাশক্তি হিসেবে কাজ করবে। যুবসমাজকে উপযুক্ত প্রশিক্ষণে স্বাবলম্বী করে কর্মসংস্থানের নতুন দিগন্ত উন্মোচন করা অত্যন্ত জরুরি।",
        "সরকারি চাকরি ৭ : পরিবেশ দূষণ রোধ ও জলবায়ু সহনশীলতা" to "ভৌগোলিক অবস্থানের কারণে বাংলাদেশ জলবায়ু পরিবর্তনের অন্যতম ঝুঁকিপূর্ণ দেশ হিসেবে চিহ্নিত। বৈশ্বিক উষ্ণতা বৃদ্ধি, সমুদ্রপৃষ্ঠের উচ্চতা বৃদ্ধি, ঘুর্ণিঝড় এবং লবণাক্ততার আগ্রাসন আমাদের পরিবেশ ও জীববৈচিত্র্যকে হুমকির মুখে ঠেলে দিচ্ছে। পরিবেশের ভারসাম্য রক্ষায় নির্বিচারে বন উজাড় বন্ধ করা, ব্যাপক বৃক্ষরোপণ এবং শিল্পবর্জ্য পরিশোধন প্ল্যান্ট বা ইটিপির ব্যবহার নিশ্চিত করা আবশ্যক। বাংলাদেশ ডেল্টা প্ল্যান ২১০০ বাস্তবায়নের মাধ্যমে নদীশাসন, বন্যা নিয়ন্ত্রণ এবং টেকসই পানিসম্পদ ব্যবস্থাপনার স্থায়ী রূপরেখা প্রণয়ন করা হয়েছে। প্লাস্টিক দূষণ নিয়ন্ত্রণ ও নবায়নযোগ্য সৌরশক্তির সম্প্রসারণ পরিবেশবান্ধব সবুজ অর্থনীতি বিনির্মাণে সহায়ক ভূমিকা রাখছে। প্রকৃতির সুরক্ষা নিশ্চিত না হলে টেকসই ভবিষ্যৎ নির্মাণ কখনোই সম্ভব নয়। পরিবেশের ভারসাম্য রক্ষায় সামাজিক আন্দোলন গড়ে তোলা নাগরিক সমাজের অবশ্য কর্তব্য।",
        "সরকারি চাকরি ৮ : বিচার বিভাগ, আইনের শাসন ও নাগরিক সেবা" to "আইনের শাসন প্রতিষ্ঠা এবং ন্যায়বিচার প্রাপ্তি যেকোনো সভ্য সমাজের মৌল ভিত্তি। স্বাধীন বিচার ব্যবস্থা নাগরিকদের মৌলিক অধিকারের প্রধান অভিভাবক হিসেবে কাজ করে। বিচারপ্রার্থী সাধারণ মানুষের ভোগান্তি লাঘব এবং মামলা দ্রুত নিষ্পত্তির লক্ষ্যে ই-জুডিশিয়ারি এবং ভার্চুয়াল কোর্ট ব্যবস্থার প্রবর্তন করা হয়েছে। লিগ্যাল এইড বা সরকারি আইনি সহায়তা কার্যক্রমের মাধ্যমে দরিদ্র ও অসচ্ছল জনগোষ্ঠী বিনা খরচে আইনি সেবা গ্রহণ করতে পারছে। বিকল্প বিরোধ নিষ্পত্তি বা এডিআর পদ্ধতির প্রসার সমাজে শান্তি ও সম্প্রীতি রক্ষায় বিশেষ ভূমিকা পালন করে। নিরপেক্ষ, স্বচ্ছ এবং দ্রুত বিচার কার্যক্রম দেশের সার্বিক স্থিতিশীলতা ও অর্থনৈতিক অগ্রগতির জন্য অপরিহার্য। আইনের চোখে সবাই সমান—এই নীতি অক্ষুণ্ণ রেখে সকল নাগরিকের ন্যায়বিচার পাওয়ার অধিকার নিশ্চিত করতে হবে।",
        "সরকারি চাকরি ৯ : কৃষি অর্থনীতি, খাদ্য নিরাপত্তা ও গ্রামীণ রূপান্তর" to "বাংলাদেশ কৃষিপ্রধান দেশ এবং দেশের খাদ্য নিরাপত্তার ভিত্তি দাঁড়িয়ে আছে কৃষকের অক্লান্ত পরিশ্রমের ওপর। উচ্চফলনশীল জাতের উদ্ভাবন, সাশ্রয়ী মূল্যে সার ও সেচ সুবিধা এবং কৃষি যান্ত্রিকীকরণের ফলে খাদ্য উৎপাদনে দেশ আজ স্বয়ংসম্পূর্ণ। আধুনিক কৃষি গবেষণার মাধ্যমে খরা, বন্যা ও লবণাক্ততা সহনশীল ধানের জাত তৈরি করে বিজ্ঞানীদের সাফল্য বিশ্বজুড়ে প্রশংসিত হয়েছে। কৃষিপণ্যের ন্যায্যমূল্য নিশ্চিতকরণ, আধুনিক হিমাগার স্থাপন এবং কৃষিবাজারের আধুনিকায়ন গ্রামীণ অর্থনীতির চিত্র আমূল বদলে দিচ্ছে। নিরাপদ ও পুষ্টিকর খাদ্য সরবরাহ নিশ্চিত করে একটি সুস্থ ও কর্মক্ষম জাতি গঠনে কৃষি খাতের অবদান অপরিসীম। কৃষিজাত পণ্যের বাণিজ্যিক প্রক্রিয়াজাতকরণ এবং রফতানি বৃদ্ধির মাধ্যমে গ্রামীণ অর্থনীতিতে নবজাগরণ সৃষ্টি হচ্ছে।",
        "সরকারি চাকরি ১০ : সরকারি কর্মচারী (আচরণ) বিধিমালা ও সুশাসন" to "সরকারি কর্মচারী (আচরণ) বিধিমালা ১৯৭৯ অনুযায়ী প্রজাতন্ত্রের কর্মচারীদের সর্বদা সততা, নিরপেক্ষতা এবং পেশাদারিত্বের চরম পরাকাষ্ঠা প্রদর্শন করতে হয়। ব্যক্তিগত স্বার্থের ঊর্ধ্বে থেকে রাষ্ট্রীয় স্বার্থকে প্রাধান্য দেওয়া এবং রাজনৈতিক নিরপেক্ষতা বজায় রাখা প্রতিটি কর্মকর্তার আইনগত বাধ্যবাধকতা। তথ্য অধিকার আইন ২০০৯ অনুযায়ী নির্ধারিত তথ্যাবলি নাগরিকদের সরবরাহ করে স্বচ্ছতা বৃদ্ধি করা সুশাসনের প্রধান শর্ত। নাগরিক সনদ বা সিটিজেন চার্টারে প্রদত্ত প্রতিশ্রুতি মোতাবেক নির্দিষ্ট সময়ের মধ্যে সেবা প্রদান দাপ্তরিক শৃঙ্খলার অঙ্গ। দুর্নীতিমুক্ত, কর্মঠ ও দায়িত্বশীল প্রশাসন গড়ে তোলার মাধ্যমেই জনগণের আস্থা অর্জন এবং উন্নত সমৃদ্ধ দেশ বিনির্মাণ সম্ভব। সরকারি নথিপত্রের গোপনীয়তা রক্ষা এবং জাতীয় স্বার্থে নিবেদিতপ্রাণ হয়ে কাজ করাই একজন সরকারি কর্মচারীর প্রধান ধর্ম।"
    )

    // Bengali Avro / Phonetic Hints for learners
    val banglaKeyHints: Map<Char, String> = mapOf(
        'অ' to "o",
        'আ' to "a",
        'ই' to "i",
        'ঈ' to "I",
        'উ' to "u",
        'ঊ' to "U",
        'ঋ' to "rri",
        'এ' to "e",
        'ঐ' to "OI",
        'ও' to "O",
        'ঔ' to "OU",
        'ক' to "k",
        'খ' to "kh",
        'গ' to "g",
        'ঘ' to "gh",
        'ঙ' to "Ng",
        'চ' to "c",
        'ছ' to "ch",
        'জ' to "j",
        'ঝ' to "jh",
        'ঞ' to "NG",
        'ট' to "T",
        'ঠ' to "Th",
        'ড' to "D",
        'ঢ' to "Dh",
        'ণ' to "N",
        'ত' to "t",
        'থ' to "th",
        'দ' to "d",
        'ধ' to "dh",
        'ন' to "n",
        'প' to "p",
        'ফ' to "f / ph",
        'ব' to "b",
        'ভ' to "v / bh",
        'ম' to "m",
        'য' to "z",
        'র' to "r",
        'ল' to "l",
        'শ' to "S / sh",
        'ষ' to "Sh",
        'স' to "s",
        'হ' to "h",
        'ড়' to "R",
        'ঢ়' to "Rh",
        'য়' to "y",
        'া' to "a",
        'ি' to "i",
        'ী' to "I",
        'ু' to "u",
        'ূ' to "U",
        'ৃ' to "rri",
        'ে' to "e",
        'ৈ' to "OI",
        'ো' to "O",
        'ৌ' to "OU"
    )
}
