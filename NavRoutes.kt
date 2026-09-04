package com.example.ui.navigation

sealed class Screen(val route: String) {
  data object Auth : Screen("auth")
  data object Main : Screen("main")
  data object CreateRescue : Screen("create_rescue")
}

enum class NavigationTab(val route: String, val title: String) {
  DASHBOARD("tab_dashboard", "Dashboard"),
  RESCUE("tab_rescue", "Rescue"),
  INVENTORY("tab_inventory", "Inventory"),
  NGOS("tab_ngos", "NGOs"),
  PROFILE("tab_profile", "Profile")
}
