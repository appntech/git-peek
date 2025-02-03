package com.appntech.gitpeek.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.appntech.gitpeek.explore.data.local.GitHubUserEntity
import com.appntech.gitpeek.explore.data.local.GitPeekDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
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

    @After
    fun closeDb() {
        database.close()
    }

    //Given: An empty database
    //When: Users are inserted and we start observing the users stream
    //Then: The items in the users stream should be the same as the items inserted

    @Test
    fun insertUsersAndGetUsers() = runTest {
        val users = listOf(
            GitHubUserEntity(
                "mojombo",
                1,
                "https://avatars.githubusercontent.com/u/1?v=4",
                "User"),
            GitHubUserEntity(
                "defunkt",
                2,
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
        val users = listOf(
            GitHubUserEntity(
            "mojombo",
            1,
            "https://avatars.githubusercontent.com/u/1?v=4",
            "User")
        )
        database.gitHubUserDao().upsertAll(users)
        database.gitHubUserDao().deleteAllUsers()

        val retrievedUsers = database.gitHubUserDao().getAllUsers().first()
        assertEquals(emptyList<GitHubUserEntity>(), retrievedUsers)
        assertEquals(0, retrievedUsers.size)

    }
}