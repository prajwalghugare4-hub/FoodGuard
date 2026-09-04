package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  viewModel: AuthViewModel,
  onAuthenticated: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()
  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .imePadding(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // App Branding Header
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 24.dp)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.VolunteerActivism,
            contentDescription = "FoodGuard",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(26.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "FOODGUARD",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Surplus Prediction & Rescue System",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Main Glass Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Toggle Login / Register
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .padding(4.dp)
          ) {
            Row {
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(
                    if (uiState.isLoginMode) MaterialTheme.colorScheme.surface
                    else Color.Transparent
                  )
                  .clickable { viewModel.toggleMode(true) }
                  .padding(horizontal = 28.dp, vertical = 8.dp)
                  .testTag("tab_login"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Login",
                  style = MaterialTheme.typography.labelLarge,
                  color = if (uiState.isLoginMode) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(
                    if (!uiState.isLoginMode) MaterialTheme.colorScheme.surface
                    else Color.Transparent
                  )
                  .clickable { viewModel.toggleMode(false) }
                  .padding(horizontal = 28.dp, vertical = 8.dp)
                  .testTag("tab_register"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Register",
                  style = MaterialTheme.typography.labelLarge,
                  color = if (!uiState.isLoginMode) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Error Message Banner
          if (uiState.errorMessage != null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(12.dp)
            ) {
              Text(
                text = uiState.errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
            }
            Spacer(modifier = Modifier.height(16.dp))
          }

          // Content Switching
          if (uiState.isLoginMode) {
            // --- LOGIN VIEW ---
            Text(
              text = "Welcome Back",
              style = MaterialTheme.typography.headlineSmall,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.fillMaxWidth()
            )
            Text(
              text = "Log in to manage food surplus and rescue operations.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp)
            )

            OutlinedTextField(
              value = uiState.email,
              onValueChange = { viewModel.onEmailChange(it) },
              label = { Text("Email Address") },
              placeholder = { Text("contact@establishment.com") },
              leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_email"),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = uiState.password,
              onValueChange = { viewModel.onPasswordChange(it) },
              label = { Text("Password") },
              placeholder = { Text("••••••••") },
              leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
              },
              visualTransformation = PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_password"),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = { viewModel.login(onAuthenticated) },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_login_submit"),
              colors = ButtonDefaults.buttonColors(
                containerColor = EcoGreenPrimary,
                contentColor = OnEcoGreen
              ),
              shape = RoundedCornerShape(10.dp),
              enabled = !uiState.isLoading
            ) {
              if (uiState.isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(24.dp),
                  color = OnEcoGreen,
                  strokeWidth = 2.dp
                )
              } else {
                Text("Log In", style = MaterialTheme.typography.labelLarge)
              }
            }
          } else {
            // --- REGISTRATION VIEW ---
            Text(
              text = "Register Establishment",
              style = MaterialTheme.typography.headlineSmall,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.fillMaxWidth()
            )
            Text(
              text = "Create a provider account to start predicting demand and managing food rescue.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp)
            )

            // Establishment Name
            OutlinedTextField(
              value = uiState.establishmentName,
              onValueChange = { viewModel.onEstablishmentNameChange(it) },
              label = { Text("Establishment Name *") },
              placeholder = { Text("e.g. Mom's Mess & Kitchen") },
              leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reg_establishment_name"),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            var typeMenuExpanded by remember { mutableStateOf(false) }
            val establishmentTypes = listOf("Mess", "Canteen", "Hotel", "Restaurant", "Other")

            ExposedDropdownMenuBox(
              expanded = typeMenuExpanded,
              onExpandedChange = { typeMenuExpanded = it },
              modifier = Modifier.fillMaxWidth()
            ) {
              OutlinedTextField(
                value = uiState.establishmentType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Establishment Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                modifier = Modifier
                  .menuAnchor()
                  .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
              )
              ExposedDropdownMenu(
                expanded = typeMenuExpanded,
                onDismissRequest = { typeMenuExpanded = false }
              ) {
                establishmentTypes.forEach { type ->
                  DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                      viewModel.onEstablishmentTypeChange(type)
                      typeMenuExpanded = false
                    }
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Full Name & Mobile
            OutlinedTextField(
              value = uiState.fullName,
              onValueChange = { viewModel.onFullNameChange(it) },
              label = { Text("Manager / Contact Person *") },
              placeholder = { Text("e.g. Rajesh Sharma") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reg_fullname"),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = uiState.phoneNumber,
              onValueChange = { viewModel.onPhoneChange(it) },
              label = { Text("Mobile Number *") },
              placeholder = { Text("+91 98765 43210") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reg_phone"),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = uiState.email,
              onValueChange = { viewModel.onEmailChange(it) },
              label = { Text("Email Address *") },
              placeholder = { Text("contact@establishment.com") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reg_email"),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = uiState.address,
              onValueChange = { viewModel.onAddressChange(it) },
              label = { Text("Establishment Address *") },
              placeholder = { Text("4th Cross, Industrial Area, City") },
              leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reg_address"),
              maxLines = 2,
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = uiState.password,
              onValueChange = { viewModel.onPasswordChange(it) },
              label = { Text("Password *") },
              placeholder = { Text("••••••••") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
              visualTransformation = PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reg_password"),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = { viewModel.register(onAuthenticated) },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_register_submit"),
              colors = ButtonDefaults.buttonColors(
                containerColor = EcoGreenPrimary,
                contentColor = OnEcoGreen
              ),
              shape = RoundedCornerShape(10.dp),
              enabled = !uiState.isLoading
            ) {
              if (uiState.isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(24.dp),
                  color = OnEcoGreen,
                  strokeWidth = 2.dp
                )
              } else {
                Text("Create Provider Account", style = MaterialTheme.typography.labelLarge)
              }
            }
          }
        }
      }
    }
  }
}
