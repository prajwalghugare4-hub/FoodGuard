package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.data.local.FoodGuardDatabase
import com.example.data.local.SessionManager
import com.example.data.repository.FoodGuardRepository
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.FoodGuardTheme
import com.example.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private lateinit var repository: FoodGuardRepository
  private lateinit var sessionManager: SessionManager
  private lateinit var themeManager: ThemeManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    sessionManager = SessionManager(applicationContext)
    themeManager = ThemeManager(applicationContext)

    val database = FoodGuardDatabase.getDatabase(applicationContext)
    repository = FoodGuardRepository(database.foodGuardDao(), sessionManager)

    // Run expiration check for outdated food rescues
    lifecycleScope.launch(Dispatchers.IO) {
      repository.checkAndExpireRescues()
    }

    setContent {
      val themeMode by themeManager.themeMode.collectAsState()
      val systemDark = isSystemInDarkTheme()
      val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
      }

      FoodGuardTheme(darkTheme = isDark) {
        AppNavigation(
          repository = repository,
          themeManager = themeManager
        )
      }
    }
  }
}

