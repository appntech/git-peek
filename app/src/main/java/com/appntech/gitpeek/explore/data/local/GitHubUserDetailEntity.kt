package com.appntech.gitpeek.explore.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.appntech.gitpeek.explore.data.model.GitHubUserDetail

@Entity(
    tableName = "github_user_detail",
    foreignKeys = [
        ForeignKey(
            entity = GitHubUserEntity::class,
            parentColumns = ["username"], // Matches the username field from GitHubUserEntity
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GitHubUserDetailEntity(
    @PrimaryKey val username: String,  // Same as GitHubUserEntity
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

fun GitHubUserDetailEntity.asExternalModel(): GitHubUserDetail {
    return GitHubUserDetail(
        username = username,
        id = id,
        name = name,
        profilePictureUrl = profilePictureUrl,
        location = location,
        bio = bio,
        followersCount = followersCount,
        followingCount = followingCount,
        publicReposCount = publicReposCount,
        publicGistsCount = publicGistsCount,
        blogUrl = blogUrl,
        twitterUsername = twitterUsername,
        githubProfileUrl = githubProfileUrl,
        accountCreatedAt = accountCreatedAt,
        lastUpdatedAt = lastUpdatedAt
    )
}


