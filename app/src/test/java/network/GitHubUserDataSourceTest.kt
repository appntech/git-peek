package network

import explore.data.network.GitHubUserDataSource
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
        val users = dataSource.fetchGitHubUsers()
        assertEquals(2, users.size)
        assertEquals("user1", users[0].login)
        assertEquals("user2", users[1].login)

    }

}