package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.model.NgoEntity
import com.example.data.model.RescueRequestEntity

object FoodRescueAlertHelper {

  data class AlertLaunchResult(
    val success: Boolean,
    val userFacingStatus: String,
    val formattedMessage: String,
    val recipientPhone: String
  )

  /**
   * Helper function to generate the dynamic Food Rescue alert message string
   * using the currently selected NGO and rescue request data, ensuring all required
   * fields (Provider name, Food items, Quantity, Pickup deadline, Verification PIN, Provider phone)
   * are included.
   */
  fun generateFoodRescueAlertMessage(
    providerName: String,
    foodItems: String,
    quantity: String,
    mealType: String,
    pickupDeadline: String,
    pickupAddress: String,
    verificationPin: String,
    providerPhone: String,
    selectedNgo: NgoEntity? = null
  ): String {
    val cleanDeadline = if (pickupDeadline.contains("Today", ignoreCase = true)) {
      pickupDeadline
    } else {
      "$pickupDeadline, Today"
    }

    return buildString {
      appendLine("🍲 FOOD RESCUE & DONATION ALERT")
      if (selectedNgo != null) {
        appendLine("🏢 Recipient NGO: ${selectedNgo.name}")
      }
      appendLine("🏪 Provider: $providerName")
      appendLine()
      appendLine("🍛 Surplus Food Details:")
      appendLine("• Items: $foodItems")
      appendLine("• Quantity: $quantity")
      appendLine("• Meal Type: $mealType")
      appendLine()
      appendLine("📍 Pickup Address:")
      appendLine(pickupAddress)
      appendLine()
      appendLine("⏰ Pickup Deadline:")
      appendLine(cleanDeadline)
      appendLine()
      appendLine("🔐 Verification PIN:")
      appendLine(verificationPin)
      appendLine()
      appendLine("📞 Provider Contact Number:")
      appendLine(providerPhone)
      if (selectedNgo != null) {
        appendLine()
        appendLine("📞 NGO Helpline: ${selectedNgo.phone}")
        val ngoLocation = if (selectedNgo.address.isNotBlank()) {
          selectedNgo.address
        } else {
          "${selectedNgo.city}, ${selectedNgo.state}"
        }
        append("📍 NGO Address: $ngoLocation")
      }
    }.trimIndent()
  }

  /**
   * Overloaded generator taking a RescueRequestEntity and optional NgoEntity.
   */
  fun generateFromRescueEntity(
    rescue: RescueRequestEntity,
    providerName: String,
    providerPhone: String,
    selectedNgo: NgoEntity? = null
  ): String {
    return generateFoodRescueAlertMessage(
      providerName = providerName,
      foodItems = rescue.foodItems,
      quantity = rescue.quantity,
      mealType = rescue.mealType,
      pickupDeadline = rescue.pickupDeadline,
      pickupAddress = rescue.pickupAddress,
      verificationPin = rescue.verificationPin,
      providerPhone = providerPhone,
      selectedNgo = selectedNgo
    )
  }

  /**
   * Implements the function to launch the device's default messaging app
   * using an Intent with the NGO's phone number and the pre-filled rescue alert message,
   * returning the user-facing label 'Message Ready to Send' instead of a fake confirmation.
   */
  fun launchDefaultMessagingApp(
    context: Context,
    recipientPhoneNumber: String,
    prefilledAlertMessage: String
  ): AlertLaunchResult {
    val cleanPhone = recipientPhoneNumber.replace(Regex("[^0-9+]"), "")

    // Attempt 1: Standard SMS URI scheme (smsto:<phone>)
    try {
      val smsUri = Uri.parse("smsto:$cleanPhone")
      val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
        putExtra("sms_body", prefilledAlertMessage)
        putExtra(Intent.EXTRA_TEXT, prefilledAlertMessage)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      if (smsIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(smsIntent)
        return AlertLaunchResult(
          success = true,
          userFacingStatus = "Message Ready to Send",
          formattedMessage = prefilledAlertMessage,
          recipientPhone = cleanPhone
        )
      }
    } catch (e: Exception) {
      // Fall through to next intent method
    }

    // Attempt 2: ACTION_VIEW with sms: scheme
    try {
      val viewSmsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$cleanPhone")).apply {
        putExtra("sms_body", prefilledAlertMessage)
        putExtra(Intent.EXTRA_TEXT, prefilledAlertMessage)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(viewSmsIntent)
      return AlertLaunchResult(
        success = true,
        userFacingStatus = "Message Ready to Send",
        formattedMessage = prefilledAlertMessage,
        recipientPhone = cleanPhone
      )
    } catch (e: Exception) {
      // Fall through to chooser
    }

    // Attempt 3: ACTION_SEND text chooser
    try {
      val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, prefilledAlertMessage)
        putExtra("address", cleanPhone)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      val chooser = Intent.createChooser(sendIntent, "Open Messaging App").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(chooser)
      return AlertLaunchResult(
        success = true,
        userFacingStatus = "Message Ready to Send",
        formattedMessage = prefilledAlertMessage,
        recipientPhone = cleanPhone
      )
    } catch (e: Exception) {
      return AlertLaunchResult(
        success = false,
        userFacingStatus = "SMS app unavailable. Please copy message manually.",
        formattedMessage = prefilledAlertMessage,
        recipientPhone = cleanPhone
      )
    }
  }
}
