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
        "Focus and Discipline" to "The art of keyboard typing requires both patience and deliberate practice. When you develop accuracy first, speed naturally follows. Every keystroke strengthens neural pathways in your brain, allowing you to transform thoughts directly into words without looking at the keyboard.",
        "Technology and Future" to "Modern technology empowers millions of people to connect, learn, and build extraordinary solutions. Mastery of typing elevates your productivity and lets your ideas flow effortlessly across digital canvases.",
        "Nature's Harmony" to "Morning sunlight filters softly through green leaves as birds chirp melodiously in the gentle autumn breeze. The serene atmosphere invites a calm state of mind, perfect for focused typing practice and deep creative thinking.",
        "The Journey of Learning" to "Great accomplishments are never born overnight. They are the sum of persistent, small daily efforts. Keep your wrists steady, relax your shoulders, and celebrate every milestone you achieve along the way."
    )

    val speedTestParagraphsBangla: List<Pair<String, String>> = listOf(
        "রবীন্দ্রনাথ ঠাকুরের বাণী" to "চিত্ত যেথা ভয়শূন্য, উচ্চ যেথা শির, জ্ঞান যেথা মুক্ত, যেথা গৃহের প্রাচীর আপন প্রাঙ্গণতলে দিবসশর্বরী বসুধারে রাখে নাই খণ্ড ক্ষুদ্র করি, যেথা বাক্য হৃদয়ের উৎসমুখ হতে উচ্ছ্বসিয়া উঠে, যেথা নির্বারিত স্রোতে দেশে দেশে দিশে দিশে কর্মধারা ধায় অজস্র সহস্রবিধ চরিতার্থতায়।",
        "কাজী নজরুল ইসলামের উক্তি" to "বল বীর— বল উন্নত মম শির! শির নেহারি আমারি নতশির ওই শিখর হিমাদ্রির! আমি চির-উন্নত শির, আমি চিরদুর্দম, দুর্বিনীত, নৃশংস, মহা-প্রলয়ের আমি নটরাজ, আমি সাইক্লোন, আমি ধ্বংস! আমি মহাভয়, আমি অভিশাপ পৃথ্বীর, আমি দুর্বার, আমি ভেঙে করি সব চুরমার!",
        "আমাদের সোনার বাংলা" to "আমাদের প্রিয় মাতৃভূমি বাংলাদেশ। রূপসী বাংলার নদ-নদী, শ্যামল ফসলের মাঠ আর মিষ্টি বাতাসের পরশ আমাদের মনকে শান্ত করে। মাতৃভাষার মর্যাদা রক্ষা করতে বাঙালি জাতি যে আত্মত্যাগ করেছে, তা পৃথিবীর ইতিহাসে চিরকাল অম্লান হয়ে থাকবে।",
        "টাইপিং দক্ষতা ও সাফল্য" to "বাংলা ও ইংরেজি উভয় ভাষায় দ্রুত এবং নির্ভুল টাইপ করার দক্ষতা বর্তমান তথ্যপ্রযুক্তির যুগে অত্যন্ত প্রয়োজনীয়। প্রতিদিন অল্প সময় ধরে মনোযোগ দিয়ে অনুশীলন করলে আঙুলের পেশী স্মৃতি তৈরি হয় এবং গতি ক্রমশ বৃদ্ধি পায়।"
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
