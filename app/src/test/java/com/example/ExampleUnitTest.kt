package com.example

import com.example.data.bijoy.BijoyLayoutEngine
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

  @Before
  fun setUp() {
    BijoyLayoutEngine.resetState()
  }

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testBijoyBasicConsonants() {
    assertEquals("ক", BijoyLayoutEngine.processKeystroke("", 'j'))
    assertEquals("খ", BijoyLayoutEngine.processKeystroke("", 'J'))
    assertEquals("ত", BijoyLayoutEngine.processKeystroke("", 'k'))
    assertEquals("থ", BijoyLayoutEngine.processKeystroke("", 'K'))
    assertEquals("দ", BijoyLayoutEngine.processKeystroke("", 'l'))
    assertEquals("ধ", BijoyLayoutEngine.processKeystroke("", 'L'))
    assertEquals("ব", BijoyLayoutEngine.processKeystroke("", 'h'))
    assertEquals("ভ", BijoyLayoutEngine.processKeystroke("", 'H'))
  }

  @Test
  fun testBijoyVowelsAndConjuncts() {
    // F is অ
    assertEquals("অ", BijoyLayoutEngine.processKeystroke("", 'F'))
    // g + f is আ
    var text = BijoyLayoutEngine.processKeystroke("", 'g')
    text = BijoyLayoutEngine.processKeystroke(text, 'f')
    assertEquals("আ", text)

    // k + g + k is ত্ত
    var conjunct = BijoyLayoutEngine.processKeystroke("", 'k')
    conjunct = BijoyLayoutEngine.processKeystroke(conjunct, 'g')
    conjunct = BijoyLayoutEngine.processKeystroke(conjunct, 'k')
    assertEquals("ত্ত", conjunct)
  }

  private fun typeString(keystrokes: String): String {
    BijoyLayoutEngine.resetState()
    var result = ""
    for (ch in keystrokes) {
      result = BijoyLayoutEngine.processKeystroke(result, ch)
    }
    return result
  }

  @Test
  fun testUserWordsFromBijoyGuide() {
    // 1. রাজনৈতিক
    assertEquals("রাজনৈতিক", typeString("vfuCbdkj"))
    // 2. ন্যায়বিচার
    assertEquals("ন্যায়বিচার", typeString("bZfWdhyfv"))
    // 3. হলো
    assertEquals("হলো", typeString("icVf"))
    // 4. পদ্ধতি
    assertEquals("পদ্ধতি", typeString("rlgLdk"))
    // 5. দলের
    assertEquals("দলের", typeString("lcVv"))
    // 6. যোগ্যতা
    assertEquals("যোগ্যতা", typeString("cwfoZkf"))
    // 7. ভিত্তিতে
    assertEquals("ভিত্তিতে", typeString("dHdkgkck"))
    // 8. নিশ্চিত
    assertEquals("নিশ্চিত", typeString("dbdMgyk"))
    // 9. হবে
    assertEquals("হবে", typeString("ich"))
    // 10. দ্বিতীয়ত
    assertEquals("দ্বিতীয়ত", typeString("dlghkDWk"))
    // 11. পাওয়ার
    assertEquals("পাওয়ার", typeString("rfxWfv"))
    // 12. নির্দিষ্ট
    assertEquals("নির্দিষ্ট", typeString("dbdlANgt"))
    // 13. কঠিন
    assertEquals("কঠিন", typeString("jdTb"))
    // 14. নির্ভয়ে
    assertEquals("নির্ভয়ে", typeString("dbHAcW"))
    // 15. আর্থিক
    assertEquals("আর্থিক", typeString("gfdKAj"))
    // 16. কার্যক্রমে
    assertEquals("কার্যক্রমে", typeString("jfwAjzcm"))
  }

  @Test
  fun testFullUserSentenceContinuousTyping() {
    val fullKeystrokes =
      "vfuCbdkj " +     // রাজনৈতিক
      "bZfWdhyfv " +    // ন্যায়বিচার
      "icVf " +         // হলো
      "rlgLdk " +       // পদ্ধতি
      "lcVv " +         // দলের
      "cwfoZkf " +      // যোগ্যতা
      "dHdkgkck " +     // ভিত্তিতে
      "dbdMgyk " +      // নিশ্চিত
      "ich " +          // হবে
      "dlghkDWk " +     // দ্বিতীয়ত
      "rfxWfv " +       // পাওয়ার
      "dbdlANgt " +     // নির্দিষ্ট
      "jdTb " +         // কঠিন
      "dbHAcW " +       // নির্ভয়ে
      "gfdKAj " +       // আর্থিক
      "jfwAjzcm "       // কার্যক্রমে

    val expected = "রাজনৈতিক ন্যায়বিচার হলো পদ্ধতি দলের যোগ্যতা ভিত্তিতে নিশ্চিত হবে দ্বিতীয়ত পাওয়ার নির্দিষ্ট কঠিন নির্ভয়ে আর্থিক কার্যক্রমে "
    assertEquals(expected, typeString(fullKeystrokes))
  }

  @Test
  fun testBackslashAndBisarga() {
    // Normal \ key -> ৎ
    assertEquals("ৎ", typeString("\\"))
    // Shift + \ key (|) -> ঃ
    assertEquals("ঃ", typeString("|"))
    // Alternative keys also work
    assertEquals("ৎ", typeString("/"))
    assertEquals("ঃ", typeString(":"))
  }

  @Test
  fun testDotAndDari() {
    // Dot (.) key -> . (Full stop)
    assertEquals(".", typeString("."))
    // Shift + G -> । (Dari)
    assertEquals("।", typeString("G"))
    // g + Shift + G -> ॥ (Double Dari)
    assertEquals("॥", typeString("gG"))
  }

  @Test
  fun testAuKarSequence() {
    // c (ে) + l (দ) + X (ৗ) -> দৌ
    assertEquals("দৌ", typeString("clX"))
    // c (ে) + j (ক) + X (ৗ) -> কৌ
    assertEquals("কৌ", typeString("cjX"))
    // Standalone Shift + X -> ৗ
    assertEquals("ৗ", typeString("X"))
    // Full vowel Au (g + X) -> ঔ
    assertEquals("ঔ", typeString("gX"))
  }

  @Test
  fun testPunctuationAndSymbols() {
    // Bengali Taka symbol
    assertEquals("৳", typeString("$"))
    // Chandrabindu
    assertEquals("ঁ", typeString("&"))
    assertEquals("ঁ", typeString("^"))
    // Bengali Digits
    assertEquals("১২৩৪৫৬৭৮৯০", typeString("1234567890"))
  }

  @Test
  fun testBangladeshGovtTypingExamRules() {
    // Rule 1: 5 keystrokes = 1 word
    val keystrokes = 100
    val wordsFromKeystrokes = keystrokes / 5
    assertEquals(20, wordsFromKeystrokes)

    // Speed calculation: 100 keystrokes in 1 minute = 20 WPM
    val durationMinutes = 1.0f
    val wpm = (keystrokes / 5.0f) / durationMinutes
    assertEquals(20.0f, wpm, 0.01f)

    // Rule 2: 5% Error Rule (২০২৪ গেজেট)
    // Case A: 100 words typed, 5 incorrect -> 5.0% error -> PASSES if speed >= 20 WPM
    val wordsTypedA = 100
    val incorrectA = 5
    val errorRateA = (incorrectA.toFloat() / wordsTypedA.toFloat()) * 100f
    assertEquals(5.0f, errorRateA, 0.01f)
    val isPassedA = (errorRateA <= 5.0f) && (wpm >= 20)
    assertEquals(true, isPassedA)

    // Case B: 100 words typed, 6 incorrect -> 6.0% error (> 5%) -> FAILS even if speed is 30 WPM
    val wordsTypedB = 100
    val incorrectB = 6
    val errorRateB = (incorrectB.toFloat() / wordsTypedB.toFloat()) * 100f
    assertEquals(6.0f, errorRateB, 0.01f)
    val speedB = 30
    val isPassedB = (errorRateB <= 5.0f) && (speedB >= 20)
    assertEquals(false, isPassedB) // Fails due to exceeding 5% error rate

    // Case C: 20 words typed, 1 incorrect -> 5.0% error -> PASSES if speed >= 20
    val wordsTypedC = 20
    val incorrectC = 1
    val errorRateC = (incorrectC.toFloat() / wordsTypedC.toFloat()) * 100f
    assertEquals(5.0f, errorRateC, 0.01f)
    val isPassedC = (errorRateC <= 5.0f) && (wpm >= 20)
    assertEquals(true, isPassedC)

    // Case D: 20 words typed, 2 incorrect -> 10.0% error -> FAILS
    val wordsTypedD = 20
    val incorrectD = 2
    val errorRateD = (incorrectD.toFloat() / wordsTypedD.toFloat()) * 100f
    assertEquals(10.0f, errorRateD, 0.01f)
    val isPassedD = (errorRateD <= 5.0f) && (wpm >= 20)
    assertEquals(false, isPassedD)
  }
}
