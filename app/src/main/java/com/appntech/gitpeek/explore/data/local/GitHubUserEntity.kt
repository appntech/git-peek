package com.appntech.gitpeek.explore.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appntech.gitpeek.explore.data.model.GitHubUser

@Entity(
    tableName = "github_users"
)
data class GitHubUserEntity(
    @PrimaryKey val id: Int,
    val username: String,
    val avatarUrl: String,
    val userType: String
)

fun GitHubUserEntity.asExternalModel() = GitHubUser(
    id = id,
    username = username,
    profilePictureUrl = avatarUrl,
    userType = userType
)

fun List<GitHubUserEntity>.asExternalModel() = map(GitHubUserEntity::asExternalModel)
