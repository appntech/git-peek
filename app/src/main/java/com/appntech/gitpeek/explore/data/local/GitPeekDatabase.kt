package com.appntech.gitpeek.explore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GitHubUserEntity::class], version = 1, exportSchema = false)
abstract class GitPeekDatabase: RoomDatabase() {
    abstract fun gitHubUserDao(): GitHubUserDao
}