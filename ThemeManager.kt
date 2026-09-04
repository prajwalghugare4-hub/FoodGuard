package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val title: String) {
  LIGHT("Light"),
  DARK("Dark"),
  SYSTEM("System")
}

class ThemeManager(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("foodguard_theme_preferences", Context.MODE_PRIVATE)

  private val _themeMode = MutableStateFlow(loadThemeMode())
  val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

  private fun loadThemeMode(): AppThemeMode {
    val saved = prefs.getString(KEY_THEME_MODE, AppThemeMode.LIGHT.name)
    return try {
      AppThemeMode.valueOf(saved ?: AppThemeMode.LIGHT.name)
    } catch (e: Exception) {
      AppThemeMode.LIGHT
    }
  }

  fun setThemeMode(mode: AppThemeMode) {
    _themeMode.value = mode
    prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
  }

  companion object {
    private const val KEY_THEME_MODE = "app_theme_mode_setting"
  }
}
