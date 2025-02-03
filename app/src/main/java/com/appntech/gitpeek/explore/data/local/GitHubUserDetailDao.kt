package com.appntech.gitpeek.explore.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubUserDetailDao {

    @Query("SELECT * FROM github_user_detail WHERE username = :username LIMIT 1")
    fun getUserDetailByUsername(username: String): Flow<GitHubUserDetailEntity>

    @Upsert
    suspend fun upsertUserDetail(user: GitHubUserDetailEntity)

    @Delete
    suspend fun deleteUserDetail(user: GitHubUserDetailEntity)
}