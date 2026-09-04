package com.example.ui.screens.rescue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen
import com.example.ui.util.SmsAlertDispatcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRescueScreen(
  viewModel: RescueViewModel,
  onNavigateBack: () -> Unit,
  onRescuePublished: (String) -> Unit
) {
  val state by viewModel.createState.collectAsState()
  val nearbyNgos by viewModel.nearbyNgos.collectAsState()
  val context = LocalContext.current
  var viewingNgoDetails by remember { mutableStateOf<com.example.data.model.NgoEntity?>(null) }
  var activeAlertMessage by remember { mutableStateOf<String?>(null) }
  var activeAlertNgo by remember { mutableStateOf<com.example.data.model.NgoEntity?>(null) }
  val scrollState = rememberScrollState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "FOODGUARD",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "/ New Rescue",
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("btn_back_create_rescue")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header Section
      Text(
        text = "Create Rescue Listing",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Provide accurate details to ensure safe and efficient food recovery.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (state.errorMessage != null) {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = AlertRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = state.errorMessage ?: "",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onErrorContainer
            )
          }
        }
      }

      // Card 1: Food Details
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder(),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "FOOD DETAILS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = state.foodItems,
            onValueChange = { viewModel.onFoodItemsChange(it) },
            label = { Text("Food Items *") },
            placeholder = { Text("e.g., Rice, Dal Tadka, Matar Paneer, Chapati") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_food_items"),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = state.quantity,
              onValueChange = { viewModel.onQuantityChange(it) },
              label = { Text("Quantity *") },
              placeholder = { Text("e.g. 18 Plates") },
              modifier = Modifier
                .weight(1f)
                .testTag("input_food_quantity"),
              singleLine = true,
              shape = RoundedCornerShape(8.dp)
            )

            // Meal Type dropdown
            var mealTypeExpanded by remember { mutableStateOf(false) }
            val mealTypes = listOf("Lunch", "Dinner", "Breakfast", "Snack", "Bakery", "Produce")

            ExposedDropdownMenuBox(
              expanded = mealTypeExpanded,
              onExpandedChange = { mealTypeExpanded = it },
              modifier = Modifier.weight(1f)
            ) {
              OutlinedTextField(
                value = state.mealType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Meal Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealTypeExpanded) },
                modifier = Modifier
                  .menuAnchor()
                  .fillMaxWidth()
                  .testTag("dropdown_meal_type"),
                shape = RoundedCornerShape(8.dp)
              )
              ExposedDropdownMenu(
                expanded = mealTypeExpanded,
                onDismissRequest = { mealTypeExpanded = false }
              ) {
                mealTypes.forEach { type ->
                  DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                      viewModel.onMealTypeChange(type)
                      mealTypeExpanded = false
                    }
                  )
                }
              }
            }
          }
        }
      }

      // Card 2: Security PIN (Dark green container matching Image 7)
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = EcoGreenPrimary),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "VERIFICATION PIN",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = Color.White.copy(alpha = 0.9f)
              )
            }

            IconButton(
              onClick = { viewModel.regeneratePin() },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Regenerate PIN",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = state.generatedPin,
            style = MaterialTheme.typography.displayLarge.copy(
              fontSize = 42.sp,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 4.sp,
              fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            modifier = Modifier.testTag("text_generated_pin")
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Share this 4-digit PIN only with the verified volunteer or NGO collector upon physical pickup.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
          )
        }
      }

      // Card 3: Logistics
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder(),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "LOGISTICS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = state.pickupDeadlineTime,
            onValueChange = { viewModel.onPickupDeadlineChange(it) },
            label = { Text("Pickup Deadline *") },
            placeholder = { Text("04:30 PM") },
            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_pickup_deadline"),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Food will be marked as expired if not collected by this time.",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = AlertRed
          )
        }
      }

      // Card 4: Location & Instructions
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder(),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "LOCATION & INSTRUCTIONS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = state.pickupAddress,
            onValueChange = { viewModel.onPickupAddressChange(it) },
            label = { Text("Pickup Address *") },
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { /* use default current location */ }) {
                Icon(
                  Icons.Default.MyLocation,
                  contentDescription = "Current Location",
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_pickup_address"),
            maxLines = 2,
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = state.additionalInstructions,
            onValueChange = { viewModel.onInstructionsChange(it) },
            label = { Text("Additional Instructions (Optional)") },
            placeholder = { Text("e.g. Ask for Raj at back gate. Bring clean containers.") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_pickup_instructions"),
            maxLines = 2,
            shape = RoundedCornerShape(8.dp)
          )
        }
      }

      // Card 5: Nearby NGOs Directory & Alert Dispatch (Sorted by distance)
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder(),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "SELECT NEARBY NGO RECIPIENT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Sorted by distance",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = EcoGreenPrimary
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Select a verified rescue organization to automatically dispatch a structured donation alert with your current rescue details and verification PIN.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          nearbyNgos.take(5).forEach { ngo ->
            val isSelected = state.selectedNgo?.ngoId == ngo.ngoId
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable {
                  viewModel.onSelectNgo(if (isSelected) null else ngo)
                },
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) EcoGreenPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceContainerLow
              ),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EcoGreenPrimary) else null,
              shape = RoundedCornerShape(10.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Text(
                      text = ngo.name,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(EcoGreenPrimary)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "${ngo.distanceKm} km",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                      color = Color.White
                    )
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = if (ngo.address.isNotEmpty()) "📍 ${ngo.address}" else "📍 ${ngo.serviceArea}",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (ngo.foodAcceptanceStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = ngo.foodAcceptanceStatus,
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                      color = if (ngo.foodAcceptanceStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.primary
                    )
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (ngo.pickupAvailabilityStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.tertiaryContainer else EcoGreenPrimary.copy(alpha = 0.12f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = ngo.pickupAvailabilityStatus,
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                      color = if (ngo.pickupAvailabilityStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.onTertiaryContainer else EcoGreenPrimary
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = { viewingNgoDetails = ngo },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text("View Info", style = MaterialTheme.typography.labelSmall)
                  }

                  Button(
                    onClick = {
                      viewModel.onSelectNgo(ngo)
                      val generatedAlert = viewModel.generateDynamicAlertFromCurrentState(ngo)
                      activeAlertMessage = generatedAlert
                      activeAlertNgo = ngo
                      val result = SmsAlertDispatcher.openSmsApp(context, ngo.phone, generatedAlert)
                      Toast.makeText(context, result.statusMessage, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                      .weight(1.3f)
                      .testTag("btn_donate_send_alert_${ngo.ngoId}"),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = EcoGreenPrimary,
                      contentColor = OnEcoGreen
                    ),
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text("Donate / Send Alert", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                  }
                }
              }
            }
          }
        }
      }

      // Card 6: Mandatory Food Safety Declaration
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.onSafetyDeclarationToggle(!state.isSafetyConfirmed) }
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = state.isSafetyConfirmed,
            onCheckedChange = { viewModel.onSafetyDeclarationToggle(it) },
            colors = CheckboxDefaults.colors(checkedColor = EcoGreenPrimary),
            modifier = Modifier.testTag("checkbox_safety_declaration")
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "I confirm that the food is fit for human consumption, has been stored safely, and complies with local health guidelines for donation.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // Bottom Action Buttons (Cancel / Publish Rescue)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("btn_cancel_rescue"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Cancel", style = MaterialTheme.typography.labelLarge)
        }

        Button(
          onClick = {
            viewModel.publishRescue { created ->
              onRescuePublished(created.rescueId)
            }
          },
          modifier = Modifier
            .weight(1.5f)
            .height(50.dp)
            .testTag("btn_publish_rescue"),
          colors = ButtonDefaults.buttonColors(
            containerColor = EcoGreenPrimary,
            contentColor = OnEcoGreen
          ),
          shape = RoundedCornerShape(10.dp),
          enabled = !state.isPublishing && state.isSafetyConfirmed
        ) {
          if (state.isPublishing) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = OnEcoGreen,
              strokeWidth = 2.dp
            )
          } else {
            Text("Publish Rescue", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
          }
        }
      }
    }
  }

  viewingNgoDetails?.let { ngo ->
    com.example.ui.screens.ngos.NgoDetailsDialog(
      ngo = ngo,
      onDismiss = { viewingNgoDetails = null },
      onDonateClick = {
        viewModel.onSelectNgo(it)
        val generatedAlert = viewModel.generateDynamicAlertFromCurrentState(it)
        activeAlertMessage = generatedAlert
        activeAlertNgo = it
        viewingNgoDetails = null
      }
    )
  }

  activeAlertMessage?.let { msg ->
    val user = viewModel.currentUser.collectAsState().value
    val dummyRescue = com.example.data.model.RescueRequestEntity(
      rescueId = "draft",
      providerId = user?.userId ?: "",
      providerName = user?.establishmentName ?: user?.fullName ?: "Provider",
      providerPhone = user?.phoneNumber ?: "+91 98765 43210",
      foodItems = state.foodItems,
      mealType = state.mealType,
      quantity = state.quantity,
      pickupDeadline = state.pickupDeadlineTime,
      pickupDeadlineTimestamp = 0L,
      pickupAddress = state.pickupAddress,
      verificationPin = state.generatedPin
    )

    DonationMessageDialog(
      rescue = dummyRescue,
      ngo = activeAlertNgo,
      messageContent = msg,
      onDismiss = {
        activeAlertMessage = null
        activeAlertNgo = null
      }
    )
  }
}
