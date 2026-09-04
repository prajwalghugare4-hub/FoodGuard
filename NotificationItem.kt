package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: String,
  val title: String,
  val message: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false,
  val type: String = "ALERT" // "NGO_RESPONSE", "PICKUP_SCHEDULED", "PICKUP_COMPLETED", "EXPIRATION_ALERT", "RISK_ALERT"
)
