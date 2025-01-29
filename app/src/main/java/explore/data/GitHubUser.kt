package explore.data

import kotlinx.serialization.SerialName

data class GitHubUser(
    val id: Int,
    val username: String,
    @SerialName("avatar_url") val profilePictureUrl:String,
    val userType: String
)


