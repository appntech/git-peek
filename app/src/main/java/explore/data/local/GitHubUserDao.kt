package explore.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubUserDao {

    @Query("SELECT * FROM github_users")
    fun getAllUsers(): Flow<List<GitHubUserEntity>>

    @Upsert
    suspend fun upsertAll(users: List<GitHubUserEntity>)

    @Query("DELETE From github_users")
    suspend fun deleteAllUsers()
}