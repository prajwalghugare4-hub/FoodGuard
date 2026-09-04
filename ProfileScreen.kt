package com.example.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  viewModel: ProfileViewModel,
  onLoggedOut: () -> Unit
) {
  val context = LocalContext.current
  val user by viewModel.currentUser.collectAsState()
  val notifications by viewModel.notifications.collectAsState()

  var showEditDialog by remember { mutableStateOf(false) }
  var showNotificationsDialog by remember { mutableStateOf(false) }
  var showLogoutConfirm by remember { mutableStateOf(false) }
  var showDeleteConfirm by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Profile & Settings",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))

        // Profile Identity Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                modifier = Modifier
                  .size(60.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = user?.fullName?.take(1)?.uppercase() ?: "U",
                  style = MaterialTheme.typography.headlineMedium,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = user?.fullName ?: "Provider Account",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = user?.email ?: "",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                      if (user?.role == UserRole.PROVIDER) EcoGreenPrimary.copy(alpha = 0.15f)
                      else MaterialTheme.colorScheme.tertiaryContainer
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = if (user?.role == UserRole.PROVIDER) "FOOD PROVIDER" else "FOOD RECEIVER / NGO",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (user?.role == UserRole.PROVIDER) EcoGreenPrimary else MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // User Info rows
            if (user?.role == UserRole.PROVIDER && user?.establishmentName != null) {
              InfoRow(icon = Icons.Default.Storefront, title = "Establishment", value = "${user?.establishmentName} (${user?.establishmentType})")
            }
            InfoRow(icon = Icons.Default.Phone, title = "Phone", value = user?.phoneNumber ?: "")
            InfoRow(icon = Icons.Default.Place, title = "Address", value = "${user?.address}, ${user?.city}, ${user?.state} - ${user?.pinCode}")

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
              onClick = { showEditDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_edit_profile"),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Edit Profile Details")
            }
          }
        }
      }

      // Quick Actions Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "SETTINGS & PREFERENCES",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Notifications trigger
            Button(
              onClick = { showNotificationsDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_view_notifications"),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  Spacer(modifier = Modifier.width(10.dp))
                  Text("Notifications & Alerts", style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                  text = "${notifications.size}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Appearance -> Theme Selection
            val currentThemeMode by viewModel.currentThemeMode.collectAsState()

            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "Appearance & Theme",
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Select visual mode. Persists across restarts.",
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  FilterChip(
                    selected = currentThemeMode == AppThemeMode.LIGHT,
                    onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                    label = {
                      Text(
                        "Light",
                        fontWeight = if (currentThemeMode == AppThemeMode.LIGHT) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.labelSmall
                      )
                    },
                    leadingIcon = {
                      Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("theme_chip_light"),
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                      selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  )

                  FilterChip(
                    selected = currentThemeMode == AppThemeMode.DARK,
                    onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                    label = {
                      Text(
                        "Dark",
                        fontWeight = if (currentThemeMode == AppThemeMode.DARK) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.labelSmall
                      )
                    },
                    leadingIcon = {
                      Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("theme_chip_dark"),
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                      selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  )

                  FilterChip(
                    selected = currentThemeMode == AppThemeMode.SYSTEM,
                    onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                    label = {
                      Text(
                        "System",
                        fontWeight = if (currentThemeMode == AppThemeMode.SYSTEM) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.labelSmall
                      )
                    },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("theme_chip_system"),
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                      selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Security & Food Safety
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = EcoGreenPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("Role & UID Security", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                  Text(
                    "User ID (${user?.userId?.take(8)}...) and account role are protected and immutable.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }

      // Logout and Danger Zone
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { showLogoutConfirm = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_logout"),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.ExitToApp, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Log Out")
            }

            OutlinedButton(
              onClick = { showDeleteConfirm = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_delete_account"),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.DeleteForever, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Delete Account")
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Edit Dialog
  if (showEditDialog && user != null) {
    EditProfileDialog(
      user = user!!,
      onDismiss = { showEditDialog = false },
      onSave = { name, phone, addr, city, state, pin, estName, estType ->
        viewModel.updateUser(name, phone, addr, city, state, pin, estName, estType) {
          showEditDialog = false
          Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        }
      }
    )
  }

  // Notifications Dialog
  if (showNotificationsDialog) {
    NotificationsDialog(
      notifications = notifications,
      onDismiss = { showNotificationsDialog = false },
      onNotificationRead = { viewModel.markNotificationAsRead(it) }
    )
  }

  // Logout Dialog
  if (showLogoutConfirm) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirm = false },
      title = { Text("Log Out?") },
      text = { Text("Are you sure you want to log out of FoodGuard?") },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirm = false
            viewModel.logout(onLoggedOut)
          },
          colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary, contentColor = OnEcoGreen)
        ) {
          Text("Log Out")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showLogoutConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Delete Account Dialog
  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text("Delete Account?", color = AlertRed) },
      text = { Text("This will permanently remove your account, meal history, and rescue listings. This action cannot be undone.") },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirm = false
            viewModel.deleteAccount(onLoggedOut)
          },
          colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = Color.White)
        ) {
          Text("Permanently Delete")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showDeleteConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun InfoRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  value: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = "$title: ",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
      color = MaterialTheme.colorScheme.onSurface,
      maxLines = 1
    )
  }
}
