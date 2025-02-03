package com.appntech.gitpeek.explore.data.model

data class GitHubUserDetail(
    val username: String,
    val id: Int,
    val name: String?,
    val profilePictureUrl: String,
    val location: String?,
    val bio: String?,

    val followersCount: Int,
    val followingCount: Int,
    val publicReposCount: Int,
    val publicGistsCount: Int,

    val blogUrl: String?,
    val twitterUsername: String?,
    val githubProfileUrl: String,

    val accountCreatedAt: String,
    val lastUpdatedAt: String
)
