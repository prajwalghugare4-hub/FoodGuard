package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.HistoricalLogEntity
import com.example.data.model.NgoEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.RescueRequestEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    UserEntity::class,
    HistoricalLogEntity::class,
    RescueRequestEntity::class,
    NgoEntity::class,
    NotificationEntity::class
  ],
  version = 3,
  exportSchema = false
)
abstract class FoodGuardDatabase : RoomDatabase() {

  abstract fun foodGuardDao(): FoodGuardDao

  companion object {
    @Volatile
    private var INSTANCE: FoodGuardDatabase? = null

    fun getDatabase(context: Context): FoodGuardDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          FoodGuardDatabase::class.java,
          "foodguard_database.db"
        )
          .fallbackToDestructiveMigration()
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.foodGuardDao()?.insertNgos(SeedData.getVerifiedNgos())
              }
            }
          })
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
