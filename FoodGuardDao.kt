package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.HistoricalLogEntity
import com.example.data.model.NgoEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.RescueRequestEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodGuardDao {

  // Users
  @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
  suspend fun getUserById(userId: String): UserEntity?

  @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
  fun getUserByIdSync(userId: String): UserEntity?

  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("DELETE FROM users WHERE userId = :userId")
  suspend fun deleteUserById(userId: String)

  // Historical Logs
  @Query("SELECT * FROM historical_logs WHERE providerId = :providerId ORDER BY timestamp DESC")
  fun getLogsForProvider(providerId: String): Flow<List<HistoricalLogEntity>>

  @Query("SELECT * FROM historical_logs WHERE providerId = :providerId ORDER BY timestamp DESC")
  suspend fun getLogsForProviderSync(providerId: String): List<HistoricalLogEntity>

  @Query("SELECT * FROM historical_logs WHERE providerId = :providerId AND mealType = :mealType ORDER BY timestamp DESC")
  suspend fun getLogsForProviderAndMeal(providerId: String, mealType: String): List<HistoricalLogEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistoricalLog(log: HistoricalLogEntity)

  @Query("DELETE FROM historical_logs WHERE providerId = :providerId")
  suspend fun deleteLogsForProvider(providerId: String)

  // Rescue Requests
  @Query("SELECT * FROM rescue_requests WHERE providerId = :providerId ORDER BY createdAt DESC")
  fun getRescuesForProvider(providerId: String): Flow<List<RescueRequestEntity>>

  @Query("SELECT * FROM rescue_requests ORDER BY createdAt DESC")
  fun getAllRescues(): Flow<List<RescueRequestEntity>>

  @Query("SELECT * FROM rescue_requests ORDER BY createdAt DESC")
  suspend fun getAllRescuesSync(): List<RescueRequestEntity>

  @Query("SELECT * FROM rescue_requests WHERE rescueId = :rescueId LIMIT 1")
  suspend fun getRescueById(rescueId: String): RescueRequestEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRescue(rescue: RescueRequestEntity)

  @Update
  suspend fun updateRescue(rescue: RescueRequestEntity)

  @Query("DELETE FROM rescue_requests WHERE rescueId = :rescueId")
  suspend fun deleteRescueById(rescueId: String)

  @Query("DELETE FROM rescue_requests WHERE providerId = :providerId")
  suspend fun deleteRescuesForProvider(providerId: String)

  // NGOs
  @Query("SELECT * FROM ngos ORDER BY distanceKm ASC")
  fun getAllNgos(): Flow<List<NgoEntity>>

  @Query("SELECT * FROM ngos ORDER BY distanceKm ASC")
  suspend fun getAllNgosSync(): List<NgoEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNgos(ngos: List<NgoEntity>)

  // Notifications
  @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
  fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity)

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markNotificationAsRead(id: Long)

  @Query("DELETE FROM notifications WHERE userId = :userId")
  suspend fun deleteNotificationsForUser(userId: String)
}
