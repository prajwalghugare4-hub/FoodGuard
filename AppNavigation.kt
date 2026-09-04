package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.model.NgoEntity
import com.example.data.model.UserEntity
import com.example.data.repository.FoodGuardRepository
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.auth.AuthViewModel
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dashboard.DashboardViewModel
import com.example.ui.screens.inventory.InventoryHistoryScreen
import com.example.ui.screens.inventory.InventoryViewModel
import com.example.ui.screens.ngos.NgoListScreen
import com.example.ui.screens.ngos.NgoViewModel
import com.example.ui.screens.profile.NotificationsDialog
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.ProfileViewModel
import com.example.ui.screens.rescue.CreateRescueScreen
import com.example.ui.screens.rescue.DonationMessageDialog
import com.example.ui.screens.rescue.RescueListScreen
import com.example.ui.screens.rescue.RescueViewModel
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen
import com.example.ui.theme.ThemeManager

@Composable
fun AppNavigation(
  repository: FoodGuardRepository,
  themeManager: ThemeManager? = null
) {
  val navController = rememberNavController()
  val currentUser by repository.currentUser.collectAsState()
  val isSessionLoaded by repository.isSessionLoaded.collectAsState()

  // ViewModels instances
  val authViewModel = remember { AuthViewModel(repository) }
  val dashboardViewModel = remember { DashboardViewModel(repository) }
  val rescueViewModel = remember { RescueViewModel(repository) }
  val inventoryViewModel = remember { InventoryViewModel(repository) }
  val ngoViewModel = remember { NgoViewModel(repository) }
  val profileViewModel = remember { ProfileViewModel(repository, themeManager) }

  // Root Navigation Host
  NavHost(
    navController = navController,
    startDestination = if (currentUser != null) Screen.Main.route else Screen.Auth.route
  ) {
    composable(Screen.Auth.route) {
      AuthScreen(
        viewModel = authViewModel,
        onAuthenticated = {
          navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Auth.route) { inclusive = true }
          }
        }
      )
    }

    composable(Screen.Main.route) {
      MainScreenContainer(
        currentUser = currentUser,
        dashboardViewModel = dashboardViewModel,
        rescueViewModel = rescueViewModel,
        inventoryViewModel = inventoryViewModel,
        ngoViewModel = ngoViewModel,
        profileViewModel = profileViewModel,
        onCreateRescueNav = { navController.navigate(Screen.CreateRescue.route) },
        onLoggedOut = {
          navController.navigate(Screen.Auth.route) {
            popUpTo(Screen.Main.route) { inclusive = true }
          }
        }
      )
    }

    composable(Screen.CreateRescue.route) {
      CreateRescueScreen(
        viewModel = rescueViewModel,
        onNavigateBack = { navController.popBackStack() },
        onRescuePublished = {
          navController.popBackStack()
        }
      )
    }
  }
}

@Composable
fun MainScreenContainer(
  currentUser: UserEntity?,
  dashboardViewModel: DashboardViewModel,
  rescueViewModel: RescueViewModel,
  inventoryViewModel: InventoryViewModel,
  ngoViewModel: NgoViewModel,
  profileViewModel: ProfileViewModel,
  onCreateRescueNav: () -> Unit,
  onLoggedOut: () -> Unit
) {
  var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }
  var selectedNgoForMessage by remember { mutableStateOf<NgoEntity?>(null) }
  var showNotificationsModal by remember { mutableStateOf(false) }

  val notifications by profileViewModel.notifications.collectAsState()

  Scaffold(
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        contentColor = MaterialTheme.colorScheme.onSurface
      ) {
        val tabs = listOf(
          Triple(NavigationTab.DASHBOARD, Icons.Default.Dashboard, "tab_nav_dashboard"),
          Triple(NavigationTab.RESCUE, Icons.Default.VolunteerActivism, "tab_nav_rescue"),
          Triple(NavigationTab.INVENTORY, Icons.Default.History, "tab_nav_inventory"),
          Triple(NavigationTab.NGOS, Icons.Default.LocationCity, "tab_nav_ngos"),
          Triple(NavigationTab.PROFILE, Icons.Default.Person, "tab_nav_profile")
        )

        tabs.forEach { (tab, icon, testTag) ->
          val selected = currentTab == tab
          NavigationBarItem(
            selected = selected,
            onClick = { currentTab = tab },
            icon = { Icon(icon, contentDescription = tab.title) },
            label = { Text(tab.title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = EcoGreenPrimary,
              selectedTextColor = EcoGreenPrimary,
              indicatorColor = EcoGreenPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag(testTag)
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        NavigationTab.DASHBOARD -> {
          DashboardScreen(
            viewModel = dashboardViewModel,
            onCreateRescueClick = onCreateRescueNav,
            onNgoContactClick = { ngo -> selectedNgoForMessage = ngo },
            onNotificationsClick = { showNotificationsModal = true },
            onProfileClick = { currentTab = NavigationTab.PROFILE }
          )
        }
        NavigationTab.RESCUE -> {
          RescueListScreen(
            viewModel = rescueViewModel,
            onCreateRescueClick = onCreateRescueNav
          )
        }
        NavigationTab.INVENTORY -> {
          InventoryHistoryScreen(
            viewModel = inventoryViewModel
          )
        }
        NavigationTab.NGOS -> {
          NgoListScreen(
            viewModel = ngoViewModel,
            onDonateToNgo = { ngo ->
              selectedNgoForMessage = ngo
            }
          )
        }
        NavigationTab.PROFILE -> {
          ProfileScreen(
            viewModel = profileViewModel,
            onLoggedOut = onLoggedOut
          )
        }
      }
    }
  }

  // Quick Donation Message dialog when contacting an NGO
  selectedNgoForMessage?.let { ngo ->
    val providerName = currentUser?.establishmentName ?: currentUser?.fullName ?: "Provider Kitchen"
    val providerPhone = currentUser?.phoneNumber ?: "+91 98765 43210"
    val msg = """
🍲 FOOD RESCUE INQUIRY for ${ngo.name}

Hello ${ngo.name} team,
We have fresh surplus food available for donation today at $providerName.

📍 Location: ${currentUser?.address ?: "Bangalore"}
📞 Phone: $providerPhone
⏰ Pickup Window: Immediate / Evening

Please let us know if your volunteer team can dispatch a pickup.
    """.trimIndent()

    DonationMessageDialog(
      rescue = com.example.data.model.RescueRequestEntity(
        rescueId = "temp",
        providerId = currentUser?.userId ?: "",
        providerName = providerName,
        providerPhone = providerPhone,
        foodItems = "Cooked Meal Surplus",
        mealType = "Fresh Cooked",
        quantity = "20 Plates",
        pickupDeadline = "Today",
        pickupDeadlineTimestamp = 0L,
        pickupAddress = currentUser?.address ?: "",
        verificationPin = "1700"
      ),
      ngo = ngo,
      messageContent = msg,
      onDismiss = { selectedNgoForMessage = null }
    )
  }

  if (showNotificationsModal) {
    NotificationsDialog(
      notifications = notifications,
      onDismiss = { showNotificationsModal = false },
      onNotificationRead = { profileViewModel.markNotificationAsRead(it) }
    )
  }
}
