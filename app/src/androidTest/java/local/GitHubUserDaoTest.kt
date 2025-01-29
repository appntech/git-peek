package local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import explore.data.local.GitHubUserEntity
import explore.data.local.GitPeekDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class GitHubUserDaoTest {

    private lateinit var database: GitPeekDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            GitPeekDatabase::class.java
        ).allowMainThreadQueries().build()
    }


    //Given: An empty database
    //When: Users are inserted and we start observing the users stream
    //Then: The items in the users stream should be the same as the items inserted

    @Test
    fun insertUsersAndGetUsers() = runTest {
        val users = listOf(
            GitHubUserEntity(
                1,
                "mojombo",
                "https://avatars.githubusercontent.com/u/1?v=4",
                "User"),
            GitHubUserEntity(
                2,
                "defunkt",
                "https://avatars.githubusercontent.com/u/2?v=4",
                "Organization")
        )

        database.gitHubUserDao().upsertAll(users)

        val retrievedUsers = database.gitHubUserDao().getAllUsers().first()
        assertEquals(users, retrievedUsers)
        assertEquals(2, retrievedUsers.size)
    }

    //Given: Users are inserted into the database
    //When: We delete all users
    //Then: The users stream should be empty

    @Test
    fun deleteUsersAndGetUsers() = runTest {
        val users = listOf(GitHubUserEntity(
            1,
            "mojombo",
            "https://avatars.githubusercontent.com/u/1?v=4",
            "User"))
        database.gitHubUserDao().upsertAll(users)
        database.gitHubUserDao().deleteAllUsers()

        val retrievedUsers = database.gitHubUserDao().getAllUsers().first()
        assertEquals(emptyList<GitHubUserEntity>(), retrievedUsers)
        assertEquals(0, retrievedUsers.size)

    }
}