package explore.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "github_users"
)
data class GitHubUserEntity(
    @PrimaryKey val id: Int,
    val username: String,
    val avatarUrl: String,
    val userType: String
)
