package com.andriiginting.common_database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MuviFavorites::class], version = 9, exportSchema = false)
abstract class MuviDatabase : RoomDatabase() {
    abstract fun theaterDAO(): MuviFavoriteDAO
}