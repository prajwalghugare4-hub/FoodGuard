package com.example.ui.screens.profile

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
import androidx.compose.material.icons.filled.Edit
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
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
  user: UserEntity,
  onDismiss: () -> Unit,
  onSave: (
    fullName: String,
    phone: String,
    address: String,
    city: String,
    state: String,
    pinCode: String,
    establishmentName: String?,
    establishmentType: String?
  ) -> Unit
) {
  var fullName by remember { mutableStateOf(user.fullName) }
  var phone by remember { mutableStateOf(user.phoneNumber) }
  var address by remember { mutableStateOf(user.address) }
  var city by remember { mutableStateOf(user.city) }
  var state by remember { mutableStateOf(user.state) }
  var pinCode by remember { mutableStateOf(user.pinCode) }
  var establishmentName by remember { mutableStateOf(user.establishmentName ?: "") }
  var establishmentType by remember { mutableStateOf(user.establishmentType ?: "Mess") }

  var typeMenuExpanded by remember { mutableStateOf(false) }
  val establishmentTypes = listOf("Mess", "Canteen", "Hotel", "Restaurant", "Other")

  val scrollState = rememberScrollState()

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Edit, contentDescription = null, tint = EcoGreenPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Edit Profile",
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
        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = { Text("Full Name *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Mobile Number *") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Address *") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2,
          shape = RoundedCornerShape(8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )
          OutlinedTextField(
            value = pinCode,
            onValueChange = { pinCode = it },
            label = { Text("PIN Code") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )
        }

        if (user.role == UserRole.PROVIDER) {
          OutlinedTextField(
            value = establishmentName,
            onValueChange = { establishmentName = it },
            label = { Text("Establishment Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )

          ExposedDropdownMenuBox(
            expanded = typeMenuExpanded,
            onExpandedChange = { typeMenuExpanded = it },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = establishmentType,
              onValueChange = {},
              readOnly = true,
              label = { Text("Establishment Type") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth(),
              shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(
              expanded = typeMenuExpanded,
              onDismissRequest = { typeMenuExpanded = false }
            ) {
              establishmentTypes.forEach { type ->
                DropdownMenuItem(
                  text = { Text(type) },
                  onClick = {
                    establishmentType = type
                    typeMenuExpanded = false
                  }
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(
            fullName,
            phone,
            address,
            city,
            state,
            pinCode,
            if (user.role == UserRole.PROVIDER) establishmentName else null,
            if (user.role == UserRole.PROVIDER) establishmentType else null
          )
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = EcoGreenPrimary,
          contentColor = OnEcoGreen
        ),
        modifier = Modifier.testTag("btn_save_profile")
      ) {
        Text("Save Changes")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
