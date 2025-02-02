package com.appntech.gitpeek.data.network

import com.appntech.gitpeek.explore.data.network.GitHubUserDataSource
import com.appntech.gitpeek.explore.data.network.asEntities
import com.appntech.gitpeek.data.network.fake.FakeGitHubApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GitHubUserDataSourceTest {

    private lateinit var apiService: FakeGitHubApiService
    private lateinit var dataSource: GitHubUserDataSource

    @Before
    fun setup() {
        apiService = FakeGitHubApiService()
        dataSource = GitHubUserDataSource(apiService)
    }

    //Given: An instance of GitHubUserDataSource
    //When: We fetch GitHub users
    //Then: The returned list should contain the expected users

    @Test
    fun fetchGitHubUsers() = runTest {
        val networkUsers = dataSource.fetchGitHubUsers()
        assertEquals(2, networkUsers.size)
        assertEquals("user1", networkUsers[0].login)
        assertEquals("user2", networkUsers[1].login)
    }

    //Given: An instance of GitHubUserDataSource
    //When: We fetch GitHub users
    //Then: The returned list should be able to be converted to a list of UserEntities

    @Test
    fun fetchGitHubUsersAndConvertToEntities() = runTest {
        val networkUsers = dataSource.fetchGitHubUsers()
        val localUsers = networkUsers.toList().asEntities()

        assertEquals(2, localUsers.size)
        assertEquals("user1", localUsers[0].username)
        assertEquals("user2", localUsers[1].username)
    }


}