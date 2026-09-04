package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("foodguard_session_prefs", Context.MODE_PRIVATE)

  fun getSessionUserId(): String? {
    return prefs.getString(KEY_USER_ID, null)
  }

  fun saveSession(userId: String) {
    prefs.edit().putString(KEY_USER_ID, userId).apply()
  }

  fun clearSession() {
    prefs.edit().remove(KEY_USER_ID).apply()
  }

  companion object {
    private const val KEY_USER_ID = "authenticated_session_user_id"
  }
}
