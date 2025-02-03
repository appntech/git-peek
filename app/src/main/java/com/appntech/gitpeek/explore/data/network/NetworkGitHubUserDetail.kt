package com.appntech.gitpeek.explore.data.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.appntech.gitpeek.explore.data.local.GitHubUserDetailEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@JsonIgnoreUnknownKeys
@Serializable
data class NetworkGitHubUserDetail(
    val login: String,
    val id: Int,
    @SerialName("avatar_url") val avatarUrl: String,
    val name: String? = null,
    val location: String? = null,
    val bio: String? = null,

    @SerialName("followers") val followers: Int,
    @SerialName("following") val following: Int,
    @SerialName("public_repos") val publicRepos: Int,
    @SerialName("public_gists") val publicGists: Int,

    val blog: String? = null,
    @SerialName("twitter_username") val twitterUsername: String? = null,
    @SerialName("html_url") val githubProfileUrl: String,

    @SerialName("created_at") val accountCreatedAt: String,
    @SerialName("updated_at") val lastUpdatedAt: String
)

@RequiresApi(Build.VERSION_CODES.O)
fun NetworkGitHubUserDetail.asEntity(): GitHubUserDetailEntity {
    return GitHubUserDetailEntity(
        username = login,  // Mapping login (username)
        id = id,
        name = name,
        profilePictureUrl = avatarUrl,
        location = location,
        bio = bio,
        followersCount = followers,
        followingCount = following,
        publicReposCount = publicRepos,
        publicGistsCount = publicGists,
        blogUrl = blog,
        twitterUsername = twitterUsername,
        githubProfileUrl = githubProfileUrl,
        accountCreatedAt = formatGitHubDate(accountCreatedAt),
        lastUpdatedAt = formatGitHubDate(lastUpdatedAt)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatGitHubDate(dateString: String, pattern: String = "dd MMM yyyy, HH:mm"): String {
    val instant = Instant.parse(dateString) // Parses "2007-10-20T05:24:19Z"

    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        .withZone(ZoneId.systemDefault()) // Adjusts to local timezone

    return formatter.format(instant)
}
