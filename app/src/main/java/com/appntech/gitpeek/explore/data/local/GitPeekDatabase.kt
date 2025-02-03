package com.appntech.gitpeek.explore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GitHubUserEntity::class, GitHubUserDetailEntity::class], version = 2, exportSchema = false)
abstract class GitPeekDatabase: RoomDatabase() {
    abstract fun gitHubUserDao(): GitHubUserDao
    abstract fun userDetailDao(): GitHubUserDetailDao
}