package com.appntech.gitpeek.data.repository

import androidx.work.Operation
import androidx.work.WorkManager
import com.appntech.gitpeek.explore.data.local.GitHubUserEntity
import com.appntech.gitpeek.explore.data.local.asExternalModel
import com.appntech.gitpeek.explore.data.network.GitHubUserDataSource
import com.appntech.gitpeek.explore.data.network.asEntities
import com.appntech.gitpeek.explore.data.network.monitor.NetworkMonitor
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepository
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepositoryImpl
import com.appntech.gitpeek.data.network.fake.FakeGitHubApiService
import com.appntech.gitpeek.data.repository.fake.FakeGitHubUserDao
import com.appntech.gitpeek.data.repository.fake.FakeGitHubUserDetailDao
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GitHubUserRepositoryImplTest {

    private lateinit var networkDataSource: GitHubUserDataSource
    private lateinit var localDataSource: FakeGitHubUserDao
    private lateinit var localDetailDataSource: FakeGitHubUserDetailDao
    private lateinit var gitHubApiService: FakeGitHubApiService
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var workManager: WorkManager
    private lateinit var operation: Operation

    private lateinit var repository: GitHubUserRepository


    @Before
    fun setup() = runBlocking {
        // Create a fake DAO
        localDataSource = FakeGitHubUserDao()
        localDetailDataSource = FakeGitHubUserDetailDao()

        // Create the fake GitHub API service with predefined data
        gitHubApiService = FakeGitHubApiService()

        // Create mocks
        workManager = mockk(relaxed = true)
        operation = mockk(relaxed = true)
        networkMonitor = mockk(relaxed = true) // relaxed mode allows default return values
        networkDataSource = mockk()
        coEvery { networkDataSource.fetchGitHubUsers() } returns gitHubApiService.getUsers()

        // Create repository instance with mocks
        repository = GitHubUserRepositoryImpl(
            context = mockk {
                every { applicationContext } returns mockk() // Inject the mock WorkManager into the repository
            },
            networkDataSource = networkDataSource,
            localDataSource = localDataSource,
            localDetailDataSource = localDetailDataSource,
            networkMonitor = networkMonitor
        )
    }

    // Given: Local database has users
    // When: Calling fetchUsers
    // Then: Ensure correct users are returned

    @Test
    fun fetchUsersShouldReturnUsersLocalDatabase() = runTest {
        // Given: Local database has users
        val users = listOf(
            GitHubUserEntity(
                "user1",
                1,
                "https://avatars.githubusercontent.com/u/1?v=4",
                "User"),
            GitHubUserEntity(
                "user2",
                2,
                "https://avatars.githubusercontent.com/u/2?v=4",
                "User")
        )

        // When: Calling fetchUsers
        val result = repository.fetchUsers().first()

        // Then: Ensure correct users are returned
        assertEquals(users.asExternalModel(), result)
    }

    // Given: Network is available
    // When: Calling refreshUsers
    // Then: Verify that data from networkDataSource was inserted into localDataSource

    @Test
    fun refreshUsersShouldFetchFromNetworkUpdateLocalDatabase() = runTest {
        // Given: Network is available
        coEvery { networkMonitor.isConnected } returns MutableStateFlow(true)

        // When: Calling refreshUsers
        repository.refreshUsers()

        // Then: Verify that data from networkDataSource was inserted into localDataSource
        val localUsers = localDataSource.getAllUsers().first()
        assertEquals(gitHubApiService.getUsers().asEntities(), localUsers)
    }

    // Given: Network is unavailable
    // When: Calling refreshUsers
    // Then: Ensure that local database is not updated

    @Test
    fun refreshUsersShouldNotFetchFromNetworkWhenOffline() = runTest {
        // Given: Network is unavailable and local database is empty
        coEvery { networkMonitor.isConnected } returns MutableStateFlow(false)
        localDataSource.deleteAllUsers()

        // When: Calling refreshUsers
        repository.refreshUsers()

        // Then: Local database should remain empty
        val localUsers = localDataSource.getAllUsers().first()
        assertTrue(localUsers.isEmpty())
    }

}
