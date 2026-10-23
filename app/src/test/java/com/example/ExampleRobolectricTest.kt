package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.datasource.LessonDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Typing Master", appName)
  }

  @Test
  fun `verify lessons data source has both english and bangla curriculum`() {
    assertTrue(LessonDataSource.englishLessons.isNotEmpty())
    assertTrue(LessonDataSource.banglaLessons.isNotEmpty())
    assertTrue(LessonDataSource.speedTestParagraphsBangla.isNotEmpty())
    assertTrue(LessonDataSource.speedTestParagraphsEnglish.isNotEmpty())
  }

  @Test
  fun `verify bijoy layout keystroke converts j to bangla ko and shift-j to kho`() {
    val ko = com.example.data.bijoy.BijoyLayoutEngine.processKeystroke("", 'j')
    assertEquals("ক", ko)

    val kho = com.example.data.bijoy.BijoyLayoutEngine.processKeystroke("", 'J')
    assertEquals("খ", kho)
  }

  @Test
  fun `verify bijoy layout handles newline enter correctly`() {
    val textWithNewline = com.example.data.bijoy.BijoyLayoutEngine.processKeystroke("ক", '\n')
    assertEquals("ক\n", textWithNewline)
  }
}
