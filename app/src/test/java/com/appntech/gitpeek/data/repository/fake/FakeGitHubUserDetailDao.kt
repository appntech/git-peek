package com.appntech.gitpeek.data.repository.fake

import com.appntech.gitpeek.explore.data.local.GitHubUserDetailDao
import com.appntech.gitpeek.explore.data.local.GitHubUserDetailEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGitHubUserDetailDao : GitHubUserDetailDao {
    private var userDetails =
        GitHubUserDetailEntity(
            username = "user1",
            id = 1,
            name = "User One",
            profilePictureUrl = "https://avatars.githubusercontent.com/u/1?v=4",
            location = "Location 1",
            bio = "Bio of User One",
            followersCount = 100,
            followingCount = 50,
            publicReposCount = 20,
            publicGistsCount = 5,
            blogUrl = "https://user1.blog",
            twitterUsername = "user1_twitter",
            githubProfileUrl = "https://github.com/user1",
            accountCreatedAt = "2021-01-01T12:00:00Z",
            lastUpdatedAt = "2021-02-01T12:00:00Z"
        )

    override fun getUserDetailByUsername(username: String): Flow<GitHubUserDetailEntity> = flow {
        emit(userDetails)
    }

    override suspend fun upsertUserDetail(user: GitHubUserDetailEntity) {
        this.userDetails = user
    }

    override suspend fun deleteUserDetail(user: GitHubUserDetailEntity) {
        userDetails = GitHubUserDetailEntity(
            username = "",
            id = 0,
            name = "",
            profilePictureUrl = "",
            location = "",
            bio = "",
            followersCount = 0,
            followingCount = 0,
            publicReposCount = 0,
            publicGistsCount = 0,
            blogUrl = "",
            twitterUsername = "",
            githubProfileUrl = "",
            accountCreatedAt = "",
            lastUpdatedAt = ""
        )
    }
}
