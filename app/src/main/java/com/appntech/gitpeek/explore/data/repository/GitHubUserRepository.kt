package com.appntech.gitpeek.explore.data.repository

import androidx.work.WorkInfo
import com.appntech.gitpeek.explore.data.model.GitHubUser
import com.appntech.gitpeek.explore.data.model.GitHubUserDetail
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import kotlin.jvm.Throws

interface GitHubUserRepository {
    fun fetchUsers(): Flow<List<GitHubUser>>
    @Throws(IOException::class)
    suspend fun refreshUsers()
    fun fetchUserDetail(username: String): Flow<GitHubUserDetail>
    suspend fun refreshUserDetail(username: String)
    fun enqueueSyncWork(workType: String, username: String? = null): Flow<WorkInfo?>
}