package com.appntech.gitpeek.explore.data.network

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class GitHubUserDataSource @Inject constructor(
    private val gitHubApiService: GitHubApiService
) {
    // A mutex is used to ensure that reads are thread-safe
    private val accessMutex = Mutex()

    suspend fun fetchGitHubUsers(): List<NetworkGitHubUser> = accessMutex.withLock {
        return gitHubApiService.getUsers()
    }

    suspend fun fetchUserDetail(username: String): NetworkGitHubUserDetail = accessMutex.withLock {
        return gitHubApiService.getUserDetail(username)
    }
}