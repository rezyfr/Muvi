package com.andriiginting.common_database

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MuviDatabaseHiltModule {
    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): MuviDatabase {
        val factory = SupportFactory(
            SQLiteDatabase.getBytes("muvi".toCharArray())
        )
        return Room
            .databaseBuilder(application, MuviDatabase::class.java, Constants.FAVORITE_DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    fun provideChannelDao(appDatabase: MuviDatabase): MuviFavoriteDAO {
        return appDatabase.theaterDAO()
    }
}