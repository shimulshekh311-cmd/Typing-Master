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

  @Test
  fun `verify subscription manager starts with 7-day trial active and can access app`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = com.example.data.repository.SubscriptionManager.getInstance(context)
    manager.resetForTesting()
    val state = manager.subscriptionState.value

    assertTrue("Trial should be active on fresh install", state.isTrialActive)
    assertTrue("Remaining trial days should be between 1 and 7", state.trialDaysLeft in 1..7)
    assertTrue("User can access app during trial", manager.canAccessApp())
  }

  @Test
  fun `verify subscription manager activation with trxId works correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = com.example.data.repository.SubscriptionManager.getInstance(context)
    manager.resetForTesting()

    // Invalid phone
    val failPhone = manager.activateSubscriptionWithTrxId("TRX123456", "01712", com.example.data.repository.PaymentMethod.BKASH)
    assertEquals(false, failPhone.first)

    // Invalid trxId
    val failTrx = manager.activateSubscriptionWithTrxId("123", "01712345678", com.example.data.repository.PaymentMethod.BKASH)
    assertEquals(false, failTrx.first)

    // Valid activation
    val success = manager.activateSubscriptionWithTrxId("BL99ABC123", "01984251178", com.example.data.repository.PaymentMethod.BKASH)
    assertEquals(true, success.first)

    val updatedState = manager.subscriptionState.value
    assertTrue("Should now be subscribed", updatedState.isSubscribed)
    assertEquals("BL99ABC123", updatedState.activeTrxId)

    // Prevent duplicate reuse of same TrxId
    val duplicate = manager.activateSubscriptionWithTrxId("BL99ABC123", "01984251178", com.example.data.repository.PaymentMethod.BKASH)
    assertEquals(false, duplicate.first)

    // Verify payment record was saved
    val records = manager.getPaymentRecords()
    assertTrue(records.isNotEmpty())
    assertEquals("BL99ABC123", records[0].trxId)
    assertEquals(com.example.data.repository.PaymentStatus.APPROVED, records[0].status)
  }

  @Test
  fun `verify sms parser extracts trxId and phone from bkash sms`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = com.example.data.repository.SubscriptionManager.getInstance(context)

    val bkashSms = "You have received Tk 45.00 from 01712345678. Fee Tk 0.00. Balance Tk 105.00. TrxID BL83K9M2P1 at 23/10/2026 12:30"
    val result = manager.parseSmsOrText(bkashSms)

    assertTrue(result.isRecognized)
    assertEquals("BL83K9M2P1", result.trxId)
    assertEquals("01712345678", result.phone)
  }

  @Test
  fun `verify sms parser extracts txnId from nagad sms`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = com.example.data.repository.SubscriptionManager.getInstance(context)

    val nagadSms = "Send Money Tk 45.00 to 01984251178 successful. TxnId: 72K9M1PQ. Balance: 120.00. Ref: Fee."
    val result = manager.parseSmsOrText(nagadSms)

    assertTrue(result.isRecognized)
    assertEquals("72K9M1PQ", result.trxId)
    assertEquals(com.example.data.repository.PaymentMethod.NAGAD, result.method)
  }
}


