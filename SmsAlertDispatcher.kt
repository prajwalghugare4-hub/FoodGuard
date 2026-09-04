package com.example.ui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

object SmsAlertDispatcher {

  data class DispatchResult(
    val isAppOpened: Boolean,
    val statusMessage: String
  )

  /**
   * Directly opens the default Messaging/SMS app on the device
   * with the recipient's phone number and complete pre-filled Food Rescue Alert body.
   */
  fun openSmsApp(
    context: Context,
    recipientPhone: String,
    alertMessage: String
  ): DispatchResult {
    val cleanPhone = recipientPhone.replace(Regex("[^0-9+]"), "")

    // Intent 1: ACTION_SENDTO with smsto: scheme (standard Android SMS intent)
    try {
      val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$cleanPhone")
        putExtra("sms_body", alertMessage)
        putExtra(Intent.EXTRA_TEXT, alertMessage)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
        return DispatchResult(
          isAppOpened = true,
          statusMessage = "Message Ready to Send"
        )
      }
    } catch (e: Exception) {
      // Continue to fallback
    }

    // Intent 2: ACTION_VIEW with sms: scheme
    try {
      val viewSmsIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("sms:$cleanPhone")
        putExtra("sms_body", alertMessage)
        putExtra(Intent.EXTRA_TEXT, alertMessage)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(viewSmsIntent)
      return DispatchResult(
        isAppOpened = true,
        statusMessage = "Message Ready to Send"
      )
    } catch (e: Exception) {
      // Continue to fallback
    }

    // Fallback 3: Generic text send chooser
    try {
      val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, alertMessage)
        putExtra("address", cleanPhone)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      val chooser = Intent.createChooser(sendIntent, "Open Messaging App").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(chooser)
      return DispatchResult(
        isAppOpened = true,
        statusMessage = "Message Ready to Send"
      )
    } catch (e: Exception) {
      // Fallback 4: Copy to clipboard if no messaging activity is found
      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clip = ClipData.newPlainText("Food Rescue Alert", alertMessage)
      clipboard.setPrimaryClip(clip)
      return DispatchResult(
        isAppOpened = false,
        statusMessage = "SMS app unavailable. Alert copied to clipboard."
      )
    }
  }
}
