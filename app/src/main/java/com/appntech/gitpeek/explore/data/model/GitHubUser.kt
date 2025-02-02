package com.appntech.gitpeek.explore.data.model

import kotlinx.serialization.SerialName

data class GitHubUser(
    val id: Int,
    val username: String,
    @SerialName("avatar_url") val profilePictureUrl:String,
    val userType: String
)


