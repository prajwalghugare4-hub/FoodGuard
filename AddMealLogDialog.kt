package com.example.ui.screens.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealLogDialog(
  onDismiss: () -> Unit,
  onSave: (
    date: String,
    dayOfWeek: String,
    mealType: String,
    foodItems: String,
    prepared: Double,
    consumed: Double,
    rescued: Double,
    wasted: Double,
    orders: Int,
    weather: String?
  ) -> Unit
) {
  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  val todayDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

  var dateText by remember { mutableStateOf(todayStr) }
  var mealType by remember { mutableStateOf("Lunch") }
  var foodItems by remember { mutableStateOf("Rice, Dal, Veg Curry, Roti") }
  var preparedText by remember { mutableStateOf("200") }
  var consumedText by remember { mutableStateOf("175") }
  var rescuedText by remember { mutableStateOf("20") }
  var wastedText by remember { mutableStateOf("5") }
  var ordersText by remember { mutableStateOf("170") }
  var weatherText by remember { mutableStateOf("Normal 27°C") }

  var mealTypeExpanded by remember { mutableStateOf(false) }
  val mealTypes = listOf("Lunch", "Dinner", "Breakfast", "Snack", "Special Event")

  val scrollState = rememberScrollState()

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Restaurant, contentDescription = null, tint = EcoGreenPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Log Meal Outcome (ML Loop)",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Every meal record refines the statistical moving average and safety variance calculations.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = dateText,
            onValueChange = { dateText = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )

          ExposedDropdownMenuBox(
            expanded = mealTypeExpanded,
            onExpandedChange = { mealTypeExpanded = it },
            modifier = Modifier.weight(1f)
          ) {
            OutlinedTextField(
              value = mealType,
              onValueChange = {},
              readOnly = true,
              label = { Text("Meal") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealTypeExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                    mealType = type
                    mealTypeExpanded = false
                  }
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = foodItems,
          onValueChange = { foodItems = it },
          label = { Text("Menu / Items Prepared") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = preparedText,
            onValueChange = { preparedText = it },
            label = { Text("Prepared (Plates)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )

          OutlinedTextField(
            value = consumedText,
            onValueChange = { consumedText = it },
            label = { Text("Consumed (Plates)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = rescuedText,
            onValueChange = { rescuedText = it },
            label = { Text("Rescued (Plates)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )

          OutlinedTextField(
            value = wastedText,
            onValueChange = { wastedText = it },
            label = { Text("Wasted (Plates)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = ordersText,
            onValueChange = { ordersText = it },
            label = { Text("Orders/Heads") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )

          OutlinedTextField(
            value = weatherText,
            onValueChange = { weatherText = it },
            label = { Text("Weather Context") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val prep = preparedText.toDoubleOrNull() ?: 200.0
          val cons = consumedText.toDoubleOrNull() ?: 175.0
          val resc = rescuedText.toDoubleOrNull() ?: 0.0
          val wast = wastedText.toDoubleOrNull() ?: 0.0
          val orders = ordersText.toIntOrNull() ?: 160
          onSave(dateText, todayDay, mealType, foodItems, prep, cons, resc, wast, orders, weatherText)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = EcoGreenPrimary,
          contentColor = OnEcoGreen
        ),
        modifier = Modifier.testTag("btn_save_meal_log")
      ) {
        Text("Save Record")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
