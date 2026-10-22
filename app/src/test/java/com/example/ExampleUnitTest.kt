package com.example

import com.example.data.bijoy.BijoyLayoutEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
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
}
