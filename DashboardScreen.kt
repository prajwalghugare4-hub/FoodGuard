package com.example.ui.screens.dashboard

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NgoEntity
import com.example.data.model.PredictionResult
import com.example.data.model.SurplusRiskLevel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnAlertRedContainer
import com.example.ui.theme.OnEcoGreen
import com.example.ui.theme.OnRescueOrangeContainer
import com.example.ui.theme.RescueOrangeContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  viewModel: DashboardViewModel,
  onCreateRescueClick: () -> Unit,
  onNgoContactClick: (NgoEntity) -> Unit,
  onNotificationsClick: () -> Unit,
  onProfileClick: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "FOODGUARD",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
          )
        },
        actions = {
          IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier.testTag("btn_dashboard_notifications")
          ) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Notifications",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .padding(end = 12.dp)
              .size(36.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer)
              .clickable { onProfileClick() }
              .testTag("avatar_profile"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = uiState.user?.fullName?.take(1)?.uppercase() ?: "U",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              fontWeight = FontWeight.Bold
            )
          }
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
        // Header Section
        Text(
          text = "TODAY",
          style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Real-time demand and surplus forecasting overview.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Create Food Rescue Action Button
        Button(
          onClick = onCreateRescueClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("btn_create_food_rescue"),
          colors = ButtonDefaults.buttonColors(
            containerColor = EcoGreenPrimary,
            contentColor = OnEcoGreen
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.VolunteerActivism,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "CREATE FOOD RESCUE",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          )
        }
      }

      // Prediction State Rendering
      when (val prediction = uiState.prediction) {
        is PredictionResult.InsufficientData -> {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
              ),
              shape = RoundedCornerShape(16.dp),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(20.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "FOOD FORECAST",
                    style = MaterialTheme.typography.labelMedium.copy(
                      letterSpacing = 1.2.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                  text = "Building estimate — more data needed",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Log your daily preparation and consumption history in Food Logs. As more records are collected, demand and surplus forecasts will automatically become more data-informed.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        is PredictionResult.Success -> {
          // Warning Banner if High Surplus Risk
          if (prediction.surplusRisk == SurplusRiskLevel.HIGH) {
            item {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("banner_high_surplus_risk"),
                colors = CardDefaults.cardColors(containerColor = AlertRedContainer),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AlertRed.copy(alpha = 0.3f)))
              ) {
                Row(
                  modifier = Modifier.padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AlertRed,
                    modifier = Modifier.size(28.dp)
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = "High Surplus Risk Detected",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = OnAlertRedContainer
                    )
                    Text(
                      text = prediction.riskReason,
                      style = MaterialTheme.typography.bodySmall,
                      color = OnAlertRedContainer
                    )
                  }
                }
              }
            }
          }

          // Main Professional Food Forecast Card
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("card_food_forecast"),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
              ),
              border = CardDefaults.outlinedCardBorder(),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(20.dp)
              ) {
                // Top Header Row
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "FOOD FORECAST",
                      style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.ExtraBold
                      ),
                      color = MaterialTheme.colorScheme.primary
                    )
                  }

                  // Surplus Risk Badge in Header
                  val (riskLabel, riskBadgeBg, riskBadgeColor) = when (prediction.surplusRisk) {
                    SurplusRiskLevel.LOW -> Triple("LOW RISK", EcoGreenPrimary.copy(alpha = 0.12f), EcoGreenPrimary)
                    SurplusRiskLevel.MEDIUM -> Triple("MEDIUM RISK", Color(0xFFF59E0B).copy(alpha = 0.15f), Color(0xFFD97706))
                    SurplusRiskLevel.HIGH -> Triple("HIGH RISK", AlertRed.copy(alpha = 0.12f), AlertRed)
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(riskBadgeBg)
                      .border(1.dp, riskBadgeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                      .padding(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = riskLabel,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = riskBadgeColor
                    )
                  }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Metric 1: Expected Plates
                ForecastMetricRow(
                  title = "Expected Plates",
                  value = "${prediction.expectedDemand} plates",
                  supportingText = "Estimated demand for today",
                  icon = Icons.AutoMirrored.Filled.TrendingUp,
                  valueColor = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Metric 2: Recommended Preparation
                ForecastMetricRow(
                  title = "Recommended Preparation",
                  value = "${prediction.recommendedPreparation} plates",
                  supportingText = "Suggested quantity to prepare",
                  icon = Icons.Default.Restaurant,
                  valueColor = EcoGreenPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Metric 3: Expected Surplus
                ForecastMetricRow(
                  title = "Expected Surplus",
                  value = "${prediction.expectedSurplus} plates",
                  supportingText = "Estimated remaining plates",
                  icon = Icons.Default.Inventory2,
                  valueColor = if (prediction.expectedSurplus > 20) AlertRed else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Metric 4: Surplus Risk status row
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "Surplus Risk",
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Calculated from expected surplus vs prep",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Text(
                    text = prediction.surplusRisk.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = when (prediction.surplusRisk) {
                      SurplusRiskLevel.LOW -> EcoGreenPrimary
                      SurplusRiskLevel.MEDIUM -> Color(0xFFD97706)
                      SurplusRiskLevel.HIGH -> AlertRed
                    }
                  )
                }
              }
            }
          }

          // Planned Quantity Live Adjustment Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
              shape = RoundedCornerShape(14.dp),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "CURRENT PLANNED PREP",
                    style = MaterialTheme.typography.labelSmall.copy(
                      letterSpacing = 1.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                      text = "${prediction.currentPlanned}",
                      style = MaterialTheme.typography.headlineMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = " plates",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
                    )
                  }
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  IconButton(
                    onClick = { viewModel.updatePlannedQuantity(prediction.currentPlanned - 10) },
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                  ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
                  }
                  IconButton(
                    onClick = { viewModel.updatePlannedQuantity(prediction.currentPlanned + 10) },
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
                  }
                }
              }
            }
          }
        }
      }

      // Nearby Rescue Organizations Header
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Nearby Rescue Organizations",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${uiState.nearbyNgos.size} Available",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Nearby NGO Cards
      items(uiState.nearbyNgos) { ngo ->
        NgoDashboardCard(
          ngo = ngo,
          onContactClick = { onNgoContactClick(ngo) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun NgoDashboardCard(
  ngo: NgoEntity,
  onContactClick: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
    border = CardDefaults.outlinedCardBorder(),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column {
      // Header Image / Banner with Distance badge
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
      ) {
        // Use generated shelter or pantry asset
        val imageRes = if (ngo.name.contains("Robin", ignoreCase = true)) {
          R.drawable.img_ngo_shelter
        } else {
          R.drawable.img_ngo_pantry
        }

        Image(
          painter = painterResource(id = imageRes),
          contentDescription = ngo.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Distance Tag
        Box(
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(6.dp))
            .background(EcoGreenPrimary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${ngo.distanceKm} km away",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Body info
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = ngo.name,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = ngo.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
          maxLines = 2
        )

        OutlinedButton(
          onClick = onContactClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Contact", style = MaterialTheme.typography.labelLarge)
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
fun ForecastMetricRow(
  title: String,
  value: String,
  supportingText: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  valueColor: Color
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
        color = valueColor
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = supportingText,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (valueColor != MaterialTheme.colorScheme.onSurface) valueColor else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(22.dp)
      )
    }
  }
}

