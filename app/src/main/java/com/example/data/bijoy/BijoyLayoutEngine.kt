package com.example.data.bijoy

/**
 * Bijoy (বিজয়) Keyboard Layout Engine for External Physical Keyboards.
 * Converts standard physical QWERTY keystrokes into Bengali Unicode characters
 * strictly following the official Bijoy / National (জাতীয় প্রমিত) keyboard layout
 * specified in the official Bijoy Typing Guide (বিজয় নির্দেশিকা) by Mustafa Jabbar.
 *
 * Fully supports:
 * - Traditional Bijoy typewriter pre-kar order (ি, ে, ৈ typed before consonants)
 * - Multi-letter conjuncts with pre-kars (e.g. দ্ব + ি = দ্বি, ত্ত + ি = ত্তি, শ্চ + ি = নিশ্চিত)
 * - Circumfix O-kar (ে + consonant + া = ো, e.g. হলো, যোগ্যতা) and OU-kar (ে + consonant + ৌ = ৌ)
 * - Ref (Shift + A) typed either after or before consonant/syllable (e.g. নির্দিষ্ট, নির্ভয়ে, আর্থিক, কার্যক্রমে)
 * - Link key 'g' for full vowels (আ, ই, ঈ, উ, ঊ, ঋ, এ, ঐ, ও, ঔ) and conjuncts
 * - Dari (Shift + G বা .), Double Dari (g + Shift+G), Taka (Shift + 4 = ৳), Chandrabindu (Shift+7 = ঁ), Khanda-Ta (/ = ৎ)
 */
object BijoyLayoutEngine {

    // Track pending pre-vowel state for traditional typewriter typing
    private var pendingPreKar: String? = null
    private var clusterKar: String? = null

    fun resetState() {
        pendingPreKar = null
        clusterKar = null
    }

    fun onBackspace() {
        pendingPreKar = null
        clusterKar = null
    }

    // Direct consonant & basic character mapping for unshifted keys
    val unshiftedMap: Map<Char, String> = mapOf(
        'q' to "ঙ",
        'w' to "য",
        'e' to "ড",
        'r' to "প",
        't' to "ট",
        'y' to "চ",
        'u' to "জ",
        'i' to "হ",
        'o' to "গ",
        'p' to "ড়",
        'a' to "ৃ",
        's' to "ু",
        'd' to "ি",
        'f' to "া",
        'g' to "্", // Hasant / Link key
        'h' to "ব",
        'j' to "ক",
        'k' to "ত",
        'l' to "দ",
        ';' to ";",
        '\'' to "'",
        'z' to "্র", // Ra-phala shortcut
        'x' to "ও", // Svara-O according to official Bijoy layout (Page 2 & 4)
        'c' to "ে",
        'v' to "র",
        'b' to "ন",
        'n' to "স",
        'm' to "ম",
        '.' to ".", // Full stop (ফুল স্টপ)
        ',' to ",",
        '\\' to "ৎ", // Khanda-ta on \ key
        '/' to "ৎ", // Khanda-ta alternative
        '1' to "১",
        '2' to "২",
        '3' to "৩",
        '4' to "৪",
        '5' to "৫",
        '6' to "৬",
        '7' to "৭",
        '8' to "৮",
        '9' to "৯",
        '0' to "০",
        '`' to "‘",
        '-' to "-",
        '=' to "="
    )

    // Direct consonant & vowel mapping for shifted keys
    val shiftedMap: Map<Char, String> = mapOf(
        'Q' to "ং", // Anusvara
        'W' to "য়", // Antastha-ya
        'E' to "ঢ",
        'R' to "ফ",
        'T' to "ঠ",
        'Y' to "ছ",
        'U' to "ঝ",
        'I' to "ঞ",
        'O' to "ঘ",
        'P' to "ঢ়",
        'A' to "র্", // Ref (রেফ) - Page 2 & 11 of Bijoy Nirdeshika!
        'S' to "ূ",
        'D' to "ী",
        'F' to "অ", // Svara-A
        'G' to "।", // Dari (দাঁড়ি) - Shift+G as per Bijoy Nirdeshika
        'H' to "ভ",
        'J' to "খ",
        'K' to "থ",
        'L' to "ধ",
        ':' to "ঃ", // Bisarga
        '"' to "\"",
        'Z' to "্য", // Ya-phala shortcut (্ + য)
        'X' to "ৗ", // Au length mark (ঔ-কারের পরের অংশ: ে + অক্ষর + ৗ = দৌ)
        'C' to "ৈ", // OI-kar
        'V' to "ল",
        'B' to "ণ",
        'N' to "ষ",
        'M' to "শ",
        '<' to "<",
        '>' to ">",
        '?' to "?",
        '!' to "!",
        '@' to "@",
        '#' to "#",
        '$' to "৳", // Bengali Taka symbol
        '%' to "%",
        '^' to "ঁ", // Chandrabindu
        '&' to "ঁ", // Chandrabindu (Shift+7 in Bijoy Nirdeshika)
        '*' to "*",
        '(' to "(",
        ')' to ")",
        '_' to "_",
        '+' to "+",
        '~' to "~",
        '|' to "ঃ"  // Bisarga on Shift + \
    )

    // Full vowels and special characters initiated with Link Key 'g' in Bijoy:
    // g + f = আ, g + d = ই, g + D = ঈ, g + s = উ, g + S = ঊ, g + a = ঋ, g + c = এ, g + C = ঐ, g + x = ও, g + X = ঔ
    val gVowelMap: Map<Char, String> = mapOf(
        'f' to "আ",
        'F' to "আ",
        'd' to "ই",
        'D' to "ঈ",
        's' to "উ",
        'S' to "ঊ",
        'a' to "ঋ",
        'A' to "ঋ",
        'c' to "এ",
        'C' to "ঐ",
        'x' to "ও",
        'X' to "ঔ",
        'G' to "॥", // Double Dari (ডবল দাঁড়ি)
        '|' to "॥",
        '.' to "॥",
        '1' to "৴", // এক আনা
        '2' to "৵", // দুই আনা
        '3' to "৶", // তিন আনা
        '4' to "৷", // চার আনা
        '5' to "৸", // আট আনা
        '6' to "৹", // মুদ্রা চিহ্ন
        '7' to "৺", // ঈশ্বর চিহ্ন
        '8' to "৲", // টাকা চিহ্ন
        '-' to "—", // এম-ড্যাশ
        ',' to "‘",
        ';' to "“",
        '\'' to "”"
    )

    data class BijoyKeyInfo(
        val normal: String,
        val shifted: String
    )

    val keyGuideMap: Map<Char, BijoyKeyInfo> = mapOf(
        'Q' to BijoyKeyInfo("ঙ", "ং"),
        'W' to BijoyKeyInfo("য", "য়"),
        'E' to BijoyKeyInfo("ড", "ঢ"),
        'R' to BijoyKeyInfo("প", "ফ"),
        'T' to BijoyKeyInfo("ট", "ঠ"),
        'Y' to BijoyKeyInfo("চ", "ছ"),
        'U' to BijoyKeyInfo("জ", "ঝ"),
        'I' to BijoyKeyInfo("হ", "ঞ"),
        'O' to BijoyKeyInfo("গ", "ঘ"),
        'P' to BijoyKeyInfo("ড়", "ঢ়"),
        'A' to BijoyKeyInfo("ৃ", "র্"), // Normal: ৃ, Shift: র্ (Ref)
        'S' to BijoyKeyInfo("ু", "ূ"),
        'D' to BijoyKeyInfo("ি", "ী"),
        'F' to BijoyKeyInfo("া", "অ"),
        'G' to BijoyKeyInfo("্", "।"), // Normal: ্, Shift: । (দাঁড়ি)
        'H' to BijoyKeyInfo("ব", "ভ"),
        'J' to BijoyKeyInfo("ক", "খ"),
        'K' to BijoyKeyInfo("ত", "থ"),
        'L' to BijoyKeyInfo("দ", "ধ"),
        'Z' to BijoyKeyInfo("্র", "্য"),
        'X' to BijoyKeyInfo("ও", "ৗ"), // Normal: ও, Shift: ৗ (ঔ-কারের অংশ)
        'C' to BijoyKeyInfo("ে", "ৈ"),
        'V' to BijoyKeyInfo("র", "ল"),
        'B' to BijoyKeyInfo("ন", "ণ"),
        'N' to BijoyKeyInfo("স", "ষ"),
        'M' to BijoyKeyInfo("ম", "শ"),
        '4' to BijoyKeyInfo("৪", "৳"),
        '7' to BijoyKeyInfo("৭", "ঁ")
    )

    fun getBijoyKeyHint(char: Char): String {
        return when (char) {
            'ক' -> "J"
            'খ' -> "Shift + J"
            'গ' -> "O"
            'ঘ' -> "Shift + O"
            'ঙ' -> "Q"
            'চ' -> "Y"
            'ছ' -> "Shift + Y"
            'জ' -> "U"
            'ঝ' -> "Shift + U"
            'ঞ' -> "Shift + I"
            'ট' -> "T"
            'ঠ' -> "Shift + T"
            'ড' -> "E"
            'ঢ' -> "Shift + E"
            'ণ' -> "Shift + B"
            'ত' -> "K"
            'থ' -> "Shift + K"
            'দ' -> "L"
            'ধ' -> "Shift + L"
            'ন' -> "B"
            'প' -> "R"
            'ফ' -> "Shift + R"
            'ব' -> "H"
            'ভ' -> "Shift + H"
            'ম' -> "M"
            'য' -> "W"
            'র' -> "V"
            'ল' -> "Shift + V"
            'শ' -> "Shift + M"
            'ষ' -> "Shift + N"
            'স' -> "N"
            'হ' -> "I"
            'ড়' -> "P"
            'ঢ়' -> "Shift + P"
            'য়' -> "Shift + W"
            'ৎ' -> "\\ (বা /)"
            'ং' -> "Shift + Q"
            'ঃ' -> "Shift + \\ (বা Shift + ;)"
            'ঁ' -> "Shift + 7 বা Shift + 6"
            '্' -> "G (হসন্ত)"
            'া' -> "F"
            'ি' -> "D"
            'ী' -> "Shift + D"
            'ু' -> "S"
            'ূ' -> "Shift + S"
            'ৃ' -> "A"
            'ে' -> "C"
            'ৈ' -> "Shift + C"
            'ো' -> "C + ব্যঞ্জনবর্ণ + F"
            'ৌ' -> "C + ব্যঞ্জনবর্ণ + Shift+X"
            'ৗ' -> "Shift + X"
            'অ' -> "Shift + F"
            'আ' -> "G + F"
            'ই' -> "G + D"
            'ঈ' -> "G + Shift + D"
            'উ' -> "G + S"
            'ঊ' -> "G + Shift + S"
            'ঋ' -> "G + A"
            'এ' -> "G + C"
            'ঐ' -> "G + Shift + C"
            'ও' -> "X (বা G + X)"
            'ঔ' -> "G + Shift + X"
            '।' -> "Shift + G"
            '॥' -> "G + Shift + G"
            '৳' -> "Shift + 4"
            '.' -> "."
            ',' -> ","
            ' ' -> "Space"
            else -> char.toString()
        }
    }

    /**
     * Converts a physical keyboard keystroke into Bengali text strictly matching Bijoy guidelines.
     * Accurately handles:
     * - Pre-vowels (ি, ে, ৈ) typed before single consonants or conjuncts
     * - Conjunct building with pre-vowels (e.g. দ্বি, ত্তি, শ্চি, ক্ষি)
     * - Ref (Shift+A) typed before or after the consonant cluster
     * - Circumfix O-kar (ে + consonant + া = ো) and OU-kar (ে + consonant + ৌ = ৌ)
     * - Ya-phala normalization (্ + য় -> ্য)
     */
    fun processKeystroke(currentText: String, keyChar: Char): String {
        if (keyChar == ' ') {
            resetState()
            return currentText + " "
        }
        if (keyChar == '\n') {
            resetState()
            return currentText + "\n"
        }

        val lastChar = currentText.lastOrNull()

        // 1. Full vowels / special combinations with link key 'g' (্)
        if (lastChar == '্' && gVowelMap.containsKey(keyChar)) {
            resetState()
            return currentText.dropLast(1) + gVowelMap[keyChar]!!
        }

        // 2. Ref (Shift + A = র্)
        // In Bijoy, typists type Ref either after the base consonant/syllable (classic typewriter)
        // or before the consonant (Unicode mode). We support both seamlessly!
        if (keyChar == 'A') {
            resetState()
            val clusterStart = findClusterStart(currentText)
            return if (clusterStart != -1) {
                // Move Ref before the cluster (e.g. কায -> কার্য, নিদি -> নির্দি, আথি -> আর্থিক)
                currentText.substring(0, clusterStart) + "র্" + currentText.substring(clusterStart)
            } else {
                currentText + "র্"
            }
        }

        // 3. Resolve key mapping
        val mapped = when {
            keyChar in shiftedMap.keys -> shiftedMap[keyChar]!!
            keyChar in unshiftedMap.keys -> unshiftedMap[keyChar]!!
            else -> keyChar.toString()
        }

        // 4. Pre-kar keys (d = ি, c = ে, C = ৈ)
        // In Bijoy typewriter typing, pre-kars are typed BEFORE the consonant or conjunct.
        if (keyChar in listOf('d', 'c', 'C')) {
            pendingPreKar = mapped
            clusterKar = null
            return currentText + mapped
        }

        // 5. Hasanta 'g' (্)
        // If typed during a conjunct where a pre-kar was already attached (e.g. দ্বি = d + l + g + h):
        // Move the pre-kar after hasanta so the next consonant can join the conjunct!
        if (mapped == "্") {
            if (clusterKar != null && lastChar.toString() == clusterKar) {
                val kar = clusterKar!!
                return currentText.dropLast(1) + "্" + kar
            }
            pendingPreKar = null
            return currentText + "্"
        }

        // 6. Phala shortcuts (z = ্র, Z = ্য)
        if (mapped in listOf("্র", "্য")) {
            if (clusterKar != null && lastChar.toString() == clusterKar) {
                val kar = clusterKar!!
                return currentText.dropLast(1) + mapped + kar
            }
            pendingPreKar = null
            return currentText + mapped
        }

        // 7. Consonant typing
        if (mapped.length == 1 && isBengaliConsonant(mapped.first())) {
            // Case 7A: User had just typed a pre-kar (ি, ে, ৈ) which is pending at the end of text:
            if (pendingPreKar != null && lastChar.toString() == pendingPreKar) {
                val kar = pendingPreKar!!
                pendingPreKar = null
                clusterKar = kar
                return currentText.dropLast(1) + mapped + kar
            }

            // Case 7B: Conjunct continuation where text ends with '্' + clusterKar (e.g. দ্ + ি + ব -> দ্বি)
            if (currentText.length >= 2 && currentText.last() in listOf('ি', 'ে', 'ৈ') && currentText[currentText.length - 2] == '্') {
                val kar = currentText.last().toString()
                clusterKar = kar
                return currentText.dropLast(1) + mapped + kar
            }

            // Normal consonant (no pre-kar)
            resetState()
            return currentText + mapped
        }

        // 8. Circumfix O-kar: 'ে' + 'া' -> 'ো'
        // In Bijoy, typing E-kar (c), consonant, and then A-kar (f) forms O-kar (ো), e.g. হলো, যোগ্যতা
        if (mapped == "া" && (lastChar == 'ে' || clusterKar == "ে")) {
            resetState()
            if (lastChar == 'ে') {
                return currentText.dropLast(1) + "ো"
            }
            return currentText + "া"
        }

        // 9. AU length mark 'ৗ' (Shift + X): e.g. ে + দ + ৗ = দৌ
        if (mapped == "ৗ") {
            resetState()
            return currentText + "ৗ"
        }

        // 10. Normalize Ya-phala: hasant + য় -> ্য
        if (lastChar == '্' && mapped == "য়") {
            resetState()
            return currentText.dropLast(1) + "্য"
        }

        resetState()
        return currentText + mapped
    }

    fun isBengaliConsonant(c: Char): Boolean {
        return c in '\u0995'..'\u09B9' || c in listOf('ড়', 'ঢ়', 'য়')
    }

    fun isBengaliVowelSign(c: Char): Boolean {
        return c in "ািীুূৃেৈোৌৗ"
    }

    /**
     * Finds the start index of the preceding consonant cluster for Ref placement.
     */
    fun findClusterStart(text: String): Int {
        if (text.isEmpty()) return -1
        var i = text.length - 1

        // Skip any trailing vowel signs or chandrabindu
        while (i >= 0 && (isBengaliVowelSign(text[i]) || text[i] == 'ঁ')) {
            i--
        }
        if (i < 0 || !isBengaliConsonant(text[i])) return -1

        // Walk back over any (consonant + ্) elements of a conjunct
        while (i >= 2 && text[i - 1] == '্' && isBengaliConsonant(text[i - 2])) {
            i -= 2
        }
        return i
    }
}
