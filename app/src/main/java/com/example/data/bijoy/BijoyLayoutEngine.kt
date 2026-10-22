package com.example.data.bijoy

/**
 * Bijoy (বিজয়) Keyboard Layout Engine for External and On-Screen Keyboards.
 * Converts standard physical QWERTY keystrokes into Bengali Unicode characters
 * following the standard Bijoy / National (জাতীয়) keyboard layout.
 */
object BijoyLayoutEngine {

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
        'z' to "্র", // Ra-phala shortcut in Bijoy
        'x' to "ো",
        'c' to "ে",
        'v' to "র",
        'b' to "ন",
        'n' to "স",
        'm' to "ম",
        '.' to "।",
        ',' to ",",
        '/' to "ৎ",
        '1' to "১",
        '2' to "২",
        '3' to "৩",
        '4' to "৪",
        '5' to "৫",
        '6' to "৬",
        '7' to "৭",
        '8' to "৮",
        '9' to "৯",
        '0' to "০"
    )

    // Direct consonant & vowel mapping for shifted keys
    val shiftedMap: Map<Char, String> = mapOf(
        'Q' to "ং",
        'W' to "য়",
        'E' to "ঢ",
        'R' to "ফ",
        'T' to "ঠ",
        'Y' to "ছ",
        'U' to "ঝ",
        'I' to "ঞ",
        'O' to "ঘ",
        'P' to "ঢ়",
        'A' to "ঋ",
        'S' to "ূ",
        'D' to "ী",
        'F' to "অ",
        'G' to "্",
        'H' to "ভ",
        'J' to "খ",
        'K' to "থ",
        'L' to "ধ",
        ':' to "ঃ",
        '"' to "\"",
        'Z' to "্য", // Ya-phala shortcut in Bijoy
        'X' to "ৌ",
        'C' to "ৈ",
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
        '$' to "$",
        '%' to "%",
        '^' to "ঁ", // Chandrabindu
        '&' to "&",
        '*' to "*",
        '(' to "(",
        ')' to ")"
    )

    // Full vowels initiated with Link Key 'g' in Bijoy:
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
        'X' to "ঔ"
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
        'A' to BijoyKeyInfo("ৃ", "ঋ"),
        'S' to BijoyKeyInfo("ু", "ূ"),
        'D' to BijoyKeyInfo("ি", "ী"),
        'F' to BijoyKeyInfo("া", "অ"),
        'G' to BijoyKeyInfo("্", "্"),
        'H' to BijoyKeyInfo("ব", "ভ"),
        'J' to BijoyKeyInfo("ক", "খ"),
        'K' to BijoyKeyInfo("ত", "থ"),
        'L' to BijoyKeyInfo("দ", "ধ"),
        'Z' to BijoyKeyInfo("্র", "্য"),
        'X' to BijoyKeyInfo("ো", "ৌ"),
        'C' to BijoyKeyInfo("ে", "ৈ"),
        'V' to BijoyKeyInfo("র", "ল"),
        'B' to BijoyKeyInfo("ন", "ণ"),
        'N' to BijoyKeyInfo("স", "ষ"),
        'M' to BijoyKeyInfo("ম", "শ")
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
            'ৎ' -> "/"
            'ং' -> "Shift + Q"
            'ঃ' -> "Shift + ;"
            'ঁ' -> "Shift + 6"
            '্' -> "G (হসন্ত)"
            'া' -> "F"
            'ি' -> "D"
            'ী' -> "Shift + D"
            'ু' -> "S"
            'ূ' -> "Shift + S"
            'ৃ' -> "A"
            'ে' -> "C"
            'ৈ' -> "Shift + C"
            'ো' -> "X"
            'ৌ' -> "Shift + X"
            'অ' -> "Shift + F"
            'আ' -> "G + F"
            'ই' -> "G + D"
            'ঈ' -> "G + Shift + D"
            'উ' -> "G + S"
            'ঊ' -> "G + Shift + S"
            'ঋ' -> "G + A"
            'এ' -> "G + C"
            'ঐ' -> "G + Shift + C"
            'ও' -> "G + X"
            'ঔ' -> "G + Shift + X"
            '।', '.' -> "."
            ',' -> ","
            ' ' -> "Space"
            else -> char.toString()
        }
    }

    fun processKeystroke(currentText: String, keyChar: Char): String {
        if (keyChar == ' ') {
            return currentText + " "
        }

        val lastChar = currentText.lastOrNull()
        val isLastHasant = lastChar == '্'

        if (isLastHasant && gVowelMap.containsKey(keyChar)) {
            val beforeHasant = if (currentText.length >= 2) currentText[currentText.length - 2] else null
            val isBeforeConsonant = beforeHasant != null && isBengaliConsonant(beforeHasant)

            if (!isBeforeConsonant) {
                return currentText.dropLast(1) + gVowelMap[keyChar]!!
            }
        }

        val mapped = if (keyChar in 'A'..'Z' || keyChar in shiftedMap.keys) {
            shiftedMap[keyChar] ?: keyChar.toString()
        } else {
            unshiftedMap[keyChar] ?: keyChar.toString()
        }

        return currentText + mapped
    }

    private fun isBengaliConsonant(c: Char): Boolean {
        return c in '\u0995'..'\u09B9' || c in listOf('ড়', 'ঢ়', 'য়')
    }
}
