package com.babeena.myapplication.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.babeena.myapplication.data.local.AppDatabase
import com.babeena.myapplication.data.local.BookMarkDao
import com.babeena.myapplication.data.local.EventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
private const val DATABASE_NAME = "local_events_database"

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `bookmarked_events` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `location` TEXT,
                `dateTime` TEXT,
                `imageUrl` TEXT,
                `latitude` REAL,
                `longitude` REAL,
                `description` TEXT,
                 PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, DATABASE_NAME
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    fun provideEventDao(appDatabase: AppDatabase): EventDao {
        return appDatabase.eventDao()
    }

    @Provides
    fun provideBookMarkDao(appDatabase: AppDatabase): BookMarkDao {
        return appDatabase.bookmarkDao()
    }
}