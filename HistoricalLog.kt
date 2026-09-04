package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historical_logs")
data class HistoricalLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val providerId: String,
  val date: String, // "YYYY-MM-DD"
  val dayOfWeek: String, // "Monday", "Tuesday", etc.
  val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack", "Produce"
  val foodItems: String, // "Rice, Dal, Paneer, Chapati"
  val quantityPrepared: Double, // in plates or kg
  val quantityConsumed: Double,
  val quantityRemaining: Double,
  val quantityRescued: Double,
  val quantityWasted: Double,
  val ordersCount: Int,
  val isHoliday: Boolean = false,
  val isWeekend: Boolean = false,
  val specialEvent: String? = null,
  val weatherContext: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)
