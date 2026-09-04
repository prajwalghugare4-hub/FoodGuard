package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SeedData
import com.example.data.local.SessionManager
import com.example.data.model.HistoricalLogEntity
import com.example.data.model.NgoEntity
import com.example.data.model.PredictionResult
import com.example.data.model.SurplusRiskLevel
import com.example.data.model.VerificationStatus
import com.example.ml.SurplusPredictionEngine
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ThemeManager
import com.example.ui.util.FoodRescueAlertHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    assertEquals("FoodGuard", appName)
  }

  @Test
  fun `test surplus prediction calculation with sufficient historical data`() {
    val sampleLogs = SeedData.getInitialHistoricalLogs("test_provider")
    val result = SurplusPredictionEngine.calculatePrediction(
      historicalLogs = sampleLogs,
      mealType = "Lunch",
      currentPlannedQuantity = 210
    )

    assertTrue("Prediction should be Success", result is PredictionResult.Success)
    val success = result as PredictionResult.Success
    assertTrue("Expected demand should be reasonable positive number", success.expectedDemand > 100)
    assertTrue("Recommended prep should include safety margin", success.recommendedPreparation >= success.expectedDemand)
    assertEquals(210, success.currentPlanned)
    assertTrue("Expected surplus should be >= 0", success.expectedSurplus >= 0)
    assertEquals(SurplusRiskLevel.HIGH, success.surplusRisk)
  }

  @Test
  fun `test session manager save get clear`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val sessionManager = SessionManager(context)

    sessionManager.clearSession()
    assertNull(sessionManager.getSessionUserId())

    sessionManager.saveSession("usr_test_12345")
    assertEquals("usr_test_12345", sessionManager.getSessionUserId())

    sessionManager.clearSession()
    assertNull(sessionManager.getSessionUserId())
  }

  @Test
  fun `test theme manager persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val themeManager = ThemeManager(context)

    themeManager.setThemeMode(AppThemeMode.DARK)
    assertEquals(AppThemeMode.DARK, themeManager.themeMode.value)

    themeManager.setThemeMode(AppThemeMode.LIGHT)
    assertEquals(AppThemeMode.LIGHT, themeManager.themeMode.value)
  }

  @Test
  fun `test verified ngo directory contains verified organizations`() {
    val ngos = SeedData.getVerifiedNgos()
    assertTrue("Verified NGO list should not be empty", ngos.isNotEmpty())
    val bandh = ngos.find { it.ngoId == "ngo_bandh_foundation" }
    assertTrue("Bandh foundation should be in the directory", bandh != null)
    assertEquals("+91 83290 55881", bandh?.phone)
    assertEquals("Accepts Cooked & Packaged Food", bandh?.foodAcceptanceInfo)
    assertEquals(VerificationStatus.VERIFIED, bandh?.verificationStatus)
  }

  @Test
  fun `test room ngo entity schema fields`() {
    val sampleNgo = NgoEntity(
      ngoId = "ngo_test_101",
      name = "Kolhapur Food Relief",
      description = "Providing surplus food relief",
      address = "Station Road, Shahupuri",
      city = "Kolhapur",
      state = "Maharashtra",
      latitude = 16.7050,
      longitude = 74.2400,
      serviceArea = "Kolhapur City",
      contactMethod = "Phone / WhatsApp",
      phone = "+91 98765 00000",
      email = "contact@kolhapurfoodrelief.org",
      website = "https://kolhapurfoodrelief.org",
      foodAcceptanceInfo = "Cooked meals and packaged groceries",
      verificationStatus = VerificationStatus.VERIFIED
    )

    assertEquals("ngo_test_101", sampleNgo.ngoId)
    assertEquals("Kolhapur Food Relief", sampleNgo.name)
    assertEquals("+91 98765 00000", sampleNgo.phone)
    assertEquals("Station Road, Shahupuri", sampleNgo.address)
    assertEquals("Kolhapur", sampleNgo.city)
    assertEquals("Maharashtra", sampleNgo.state)
    assertEquals(16.7050, sampleNgo.latitude, 0.0001)
    assertEquals(74.2400, sampleNgo.longitude, 0.0001)
    assertEquals("https://kolhapurfoodrelief.org", sampleNgo.website)
    assertEquals("Cooked meals and packaged groceries", sampleNgo.foodAcceptanceInfo)
    assertEquals(VerificationStatus.VERIFIED, sampleNgo.verificationStatus)
  }

  @Test
  fun `test food rescue dynamic alert message generator`() {
    val testNgo = NgoEntity(
      ngoId = "ngo_bandh_foundation",
      name = "Bandh Foundation",
      description = "Community relief NGO",
      address = "Shahupuri, Kolhapur",
      city = "Kolhapur",
      state = "Maharashtra",
      serviceArea = "Kolhapur",
      contactMethod = "Phone",
      phone = "+91 83290 55881",
      email = "info@bandhfoundation.org",
      website = "https://bandhfoundation.org",
      foodAcceptanceInfo = "Cooked & Packaged Food",
      verificationStatus = VerificationStatus.VERIFIED
    )

    val alert = FoodRescueAlertHelper.generateFoodRescueAlertMessage(
      providerName = "City Catering Hub",
      foodItems = "Paneer Butter Masala, Jeera Rice, Rotis",
      quantity = "30 Portions",
      mealType = "Dinner",
      pickupDeadline = "09:30 PM",
      pickupAddress = "12 Station Road, Kolhapur",
      verificationPin = "4892",
      providerPhone = "+91 91234 56789",
      selectedNgo = testNgo
    )

    assertTrue("Must include provider name", alert.contains("City Catering Hub"))
    assertTrue("Must include food items", alert.contains("Paneer Butter Masala, Jeera Rice, Rotis"))
    assertTrue("Must include quantity", alert.contains("30 Portions"))
    assertTrue("Must include pickup deadline", alert.contains("09:30 PM"))
    assertTrue("Must include verification PIN", alert.contains("4892"))
    assertTrue("Must include provider phone", alert.contains("+91 91234 56789"))
    assertTrue("Must include NGO name", alert.contains("Bandh Foundation"))
    assertTrue("Must include NGO phone", alert.contains("+91 83290 55881"))
  }

  @Test
  fun `test messaging app launcher returns message ready to send status`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val result = FoodRescueAlertHelper.launchDefaultMessagingApp(
      context = context,
      recipientPhoneNumber = "+91 83290 55881",
      prefilledAlertMessage = "Test Food Rescue Alert"
    )

    assertNotNull(result)
    assertEquals("Message Ready to Send", result.userFacingStatus)
    assertEquals("+918329055881", result.recipientPhone)
  }
}
