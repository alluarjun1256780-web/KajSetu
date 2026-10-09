package com.example

import com.example.ui.components.formatBdt
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testBdtCurrencyFormatting() {
    val amount = 1250.50
    val formatted = formatBdt(amount)
    assertEquals("৳1,250.50", formatted)
  }

  @Test
  fun testWithdrawalFeeCalculation() {
    val amount = 1000.0
    val feePercent = 2.0
    val fee = (amount * feePercent) / 100.0
    val netAmount = amount - fee
    assertEquals(20.0, fee, 0.001)
    assertEquals(980.0, netAmount, 0.001)
  }

  @Test
  fun testBangladeshiPhoneFormat() {
    val validPhone1 = "01712345678"
    val validPhone2 = "01987654321"
    val invalidPhone = "01234567890" // invalid operator prefix in BD
    val regex = Regex("^(?:\\+?88)?01[3-9]\\d{8}$")

    assertTrue(validPhone1.matches(regex))
    assertTrue(validPhone2.matches(regex))
    assertFalse(invalidPhone.matches(regex))
  }

  @Test
  fun testTaskSlotFullCondition() {
    val maxSubmissions = 100
    val currentSubmissionsUnder = 99
    val currentSubmissionsExact = 100
    val currentSubmissionsOver = 101

    val isFullUnder = currentSubmissionsUnder >= maxSubmissions
    val isFullExact = currentSubmissionsExact >= maxSubmissions
    val isFullOver = currentSubmissionsOver >= maxSubmissions

    assertFalse(isFullUnder)
    assertTrue(isFullExact)
    assertTrue(isFullOver)
  }

  @Test
  fun testDuplicateSubmissionDetection() {
    val submittedTaskIds = setOf("task_001", "task_002")
    
    // User attempts to check if already submitted
    assertTrue(submittedTaskIds.contains("task_001"))
    assertTrue(submittedTaskIds.contains("task_002"))
    assertFalse(submittedTaskIds.contains("task_003"))
  }

  @Test
  fun testTaskProofValidation() {
    val emptyProof = "   "
    val emptyScreenshot = ""
    val hasTextProof = "আমার ফেসবুক ইউজারনেম: @rahim_bd"
    val hasScreenshotProof = "content://media/external/images/media/42"

    // Both empty should be invalid
    val isValidEmpty = emptyProof.trim().isNotBlank() || emptyScreenshot.isNotBlank()
    assertFalse(isValidEmpty)

    // With text should be valid
    val isValidWithText = hasTextProof.trim().isNotBlank() || emptyScreenshot.isNotBlank()
    assertTrue(isValidWithText)

    // With screenshot should be valid
    val isValidWithScreenshot = emptyProof.trim().isNotBlank() || hasScreenshotProof.isNotBlank()
    assertTrue(isValidWithScreenshot)
  }

  @Test
  fun testGmailValidation() {
    val validGmail = "fresh.user123@gmail.com"
    val invalidDomain = "user123@yahoo.com"
    val shortMail = "a@gmail.com"

    fun isValidGmail(email: String): Boolean {
      return email.endsWith("@gmail.com", ignoreCase = true) && email.substringBefore("@").length >= 6
    }

    assertTrue(isValidGmail(validGmail))
    assertFalse(isValidGmail(invalidDomain))
    assertFalse(isValidGmail(shortMail))
  }

  @Test
  fun testAccountStatusChecks() {
    val activeStatus = "active"
    val inactiveStatus = "inactive"

    assertTrue(activeStatus == "active")
    assertFalse(inactiveStatus == "active")
  }

  @Test
  fun testGmailSaleRewardCalculation() {
    val initialBalance = 100.0
    val pricePerMail = 15.0
    val approvedCount = 5
    val totalEarnings = initialBalance + (pricePerMail * approvedCount)

    assertEquals(175.0, totalEarnings, 0.001)
  }

  @Test
  fun testMicroTaskEntityStructureAndFields() {
    val deadlineTime = System.currentTimeMillis() + 86400000L
    val microTask = com.example.data.entity.MicroTaskEntity(
      id = "micro_task_test_01",
      title = "ইউটিউব সাবস্ক্রাইব করুন",
      description = "চ্যানেলে সাবস্ক্রাইব করে স্ক্রিনশট দিন",
      rewardAmount = 15.50,
      deadline = deadlineTime,
      category = "সোশ্যাল মিডিয়া"
    )

    assertEquals("ইউটিউব সাবস্ক্রাইব করুন", microTask.title)
    assertEquals("ইউটিউব সাবস্ক্রাইব করুন", microTask.taskTitle)
    assertEquals("চ্যানেলে সাবস্ক্রাইব করে স্ক্রিনশট দিন", microTask.description)
    assertEquals(15.50, microTask.rewardAmount, 0.001)
    assertEquals(15.50, microTask.reward, 0.001)
    assertEquals(deadlineTime, microTask.deadline)
    assertEquals("ACTIVE", microTask.status)
    assertFalse(microTask.isCompleted)
  }

  @Test
  fun testMicroTaskDeadlineValidation() {
    val now = System.currentTimeMillis()
    val futureDeadline = now + 100000L
    val pastDeadline = now - 100000L

    val validTask = com.example.data.entity.MicroTaskEntity(
      title = "ভ্যালিড টাস্ক",
      description = "সময় বাকি আছে",
      rewardAmount = 20.0,
      deadline = futureDeadline
    )

    val expiredTask = com.example.data.entity.MicroTaskEntity(
      title = "মেয়াদোত্তীর্ণ টাস্ক",
      description = "সময় শেষ",
      rewardAmount = 20.0,
      deadline = pastDeadline
    )

    assertTrue(validTask.deadline > now)
    assertFalse(expiredTask.deadline > now)
  }
}
