package com.appntech.gitpeek.explore.data.network

import com.appntech.gitpeek.explore.data.local.GitHubUserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGitHubUser(
    val login: String,
    val id: Int,
    @SerialName("node_id") val nodeId: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("gravatar") val gravatarId: String,
    val url: String,
    @SerialName("html_url") val githubProfileUrl: String,
    @SerialName("followers_url") val followersUrl: String,
    @SerialName("following_url") val followingUrl: String,
    @SerialName("gists_url") val gistsUrl: String,
    @SerialName("starred_url") val starredUrl: String,
    @SerialName("subscriptions_url") val subscriptionsUrl: String,
    @SerialName("organizations_url") val organizationsUrl: String,
    @SerialName("repos_url") val reposUrl: String,
    @SerialName("events_url") val eventsUrl: String,
    @SerialName("received_events_url") val receivedEventsUrl: String,
    @SerialName("type") val userType: String,
    @SerialName("user_view_type") val userViewType: String,
    @SerialName("site_admin") val siteAdmin: Boolean
)

fun NetworkGitHubUser.asEntity(): GitHubUserEntity {
    return GitHubUserEntity(
        id = id,
        username = login,
        avatarUrl = avatarUrl,
        userType = userType
    )
}

fun List<NetworkGitHubUser>.asEntities() = map(NetworkGitHubUser::asEntity)
