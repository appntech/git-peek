package com.appntech.gitpeek.explore.data.repository

import com.appntech.gitpeek.explore.data.model.GitHubUser
import kotlinx.coroutines.flow.Flow

interface GitHubUserRepository {
    fun fetchUsers(): Flow<List<GitHubUser>>
    suspend fun refreshUsers()
    fun enqueueSyncWork()
}