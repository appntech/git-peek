package com.appntech.gitpeek.data.repository.fake

import com.appntech.gitpeek.explore.data.local.GitHubUserDao
import com.appntech.gitpeek.explore.data.local.GitHubUserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGitHubUserDao : GitHubUserDao {
    private val users = mutableListOf(
        GitHubUserEntity(
            "user1",
            1,
            "https://avatars.githubusercontent.com/u/1?v=4",
            "User"),
        GitHubUserEntity(
            "user2",
            2,
            "https://avatars.githubusercontent.com/u/2?v=4",
            "User")
    )

    override fun getAllUsers(): Flow<List<GitHubUserEntity>> = flow {
        emit(users)
    }

    override suspend fun upsertAll(users: List<GitHubUserEntity>) {
        this.users.clear()  // Clear existing data
        this.users.addAll(users)
    }

    override suspend fun deleteAllUsers() {
        users.clear()
    }
}
