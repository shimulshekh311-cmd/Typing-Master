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
}
