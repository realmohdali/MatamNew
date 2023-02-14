package com.matamapp.matam.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteData::class], exportSchema = false, version = 1)
abstract class DatabaseHelper : RoomDatabase() {
    companion object {
        private const val DB_NAME = "favoritedb"
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            synchronized(this) {
                if (instance == null) {
                    instance =
                        Room.databaseBuilder(context, DatabaseHelper::class.java, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return instance!!
        }
    }

    abstract fun favoriteDao(): FavoriteDao
}