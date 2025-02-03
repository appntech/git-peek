package com.appntech.gitpeek.explore.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.appntech.gitpeek.explore.data.model.GitHubUser

@Entity(
    tableName = "github_users",
    indices = [Index(value = ["username"], unique = true)] // Ensure uniqueness
)
data class GitHubUserEntity(
    @PrimaryKey val username: String,
    val id: Int,
    val avatarUrl: String,
    val userType: String
)

fun GitHubUserEntity.asExternalModel() = GitHubUser(
    username = username,
    id = id,
    profilePictureUrl = avatarUrl,
    userType = userType
)

fun List<GitHubUserEntity>.asExternalModel() = map(GitHubUserEntity::asExternalModel)
