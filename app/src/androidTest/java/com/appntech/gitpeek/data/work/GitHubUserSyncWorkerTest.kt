package com.appntech.gitpeek.data.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.appntech.gitpeek.data.work.factory.TestGitHubUserSyncWorkerFactory
import com.appntech.gitpeek.explore.data.network.monitor.NetworkMonitor
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepository
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker.Companion.WORK_TYPE_KEY
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class GitHubUserSyncWorkerTest {
    private lateinit var context: Context
    private val repository: GitHubUserRepository = mockk(relaxed = true) // Mocked repository
    private val networkMonitor: NetworkMonitor = mockk(relaxed = true) // Mocked network monitor

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is connected
    //Then: The worker should sync users

    @Test
    fun testGitHubUserSyncWorker_successfulSync() = runBlocking {
        // Simulate that the network is connected
        every { networkMonitor.isConnected.value } returns true
        coEvery { repository.refreshUsers() } just Runs // Simulate successful API call

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context).setWorkerFactory(
            workerFactory
        ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior and if result is success
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.success()::class.java)
        )
        coVerify(exactly = 1) { repository.refreshUsers() } // Verify refreshUsers was called

    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is not connected
    //Then: The worker should not sync users

    @Test
    fun testGitHubUserSyncWorker_retryWhenNetworkIsDown() = runBlocking {
        // Simulate no network connection
        every { networkMonitor.isConnected.value } returns false

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context).setWorkerFactory(
            workerFactory
        ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.retry()::class.java)
        )
        coVerify(exactly = 0) { repository.refreshUsers() } // Verify refreshUsers was NOT called
    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is connected but API call fails with an Unauthorized error
    //Then: The worker should not retry the API call

    @Test
    fun testGitHubUserSyncWorker_failureOnUnauthorizedError() = runBlocking {
        // Simulate network connected but API fails
        every { networkMonitor.isConnected.value } returns true
        // Simulate HttpException with status code 401 (Unauthorized)
        coEvery { repository.refreshUsers() } throws
                HttpException(
                    Response.error<Any>(
                        401, "Unauthorized".toResponseBody(null)
                    )
                )

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context)
            .setWorkerFactory(
                workerFactory
            ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.failure()::class.java)
        )
        coVerify(exactly = 1) { repository.refreshUsers() } // Verify refreshUsers was called
    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is connected but API call fails with an Forbidden error
    //Then: The worker should not retry the API call

    @Test
    fun testGitHubUserSyncWorker_failureOnForbiddenError() = runBlocking {
        // Simulate network connected but API fails
        every { networkMonitor.isConnected.value } returns true
        // Simulate HttpException with status code 403 (Forbidden)
        coEvery { repository.refreshUsers() } throws
                HttpException(
                    Response.error<Any>(
                        403, "Forbidden".toResponseBody(null)
                    )
                )

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context)
            .setWorkerFactory(
                workerFactory
            ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.failure()::class.java)
        )
        coVerify(exactly = 1) { repository.refreshUsers() } // Verify refreshUsers was called
    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is connected but API call fails with a Rate limit exceeded error
    //Then: The worker should not retry the API call

    @Test
    fun testGitHubUserSyncWorker_failureOnRateLimitError() = runBlocking {
        // Simulate network connected but API fails
        every { networkMonitor.isConnected.value } returns true
        // Simulate HttpException with status code 429 (Rate limit exceeded)
        coEvery { repository.refreshUsers() } throws
                HttpException(
                    Response.error<Any>(
                        429, "Rate limit exceeded".toResponseBody(null)
                    )
                )

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context)
            .setWorkerFactory(
                workerFactory
            ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.failure()::class.java)
        )
        coVerify(exactly = 1) { repository.refreshUsers() } // Verify refreshUsers was called
    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is connected but API call fails with a client error (400-499)
    //Then: The worker should retry the API call

    @Test
    fun testGitHubUserSyncWorker_retryOnClientError() = runBlocking {
        // Simulate network connected but API fails
        every { networkMonitor.isConnected.value } returns true
        // Simulate HttpException with status code within 400 - 499 (Client error)
        coEvery { repository.refreshUsers() } throws
                HttpException(
                    Response.error<Any>(
                        499, "Client error".toResponseBody(null)
                    )
                )

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context)
            .setWorkerFactory(
                workerFactory
            ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.retry()::class.java)
        )
        coVerify(atLeast = 1) { repository.refreshUsers() } // Verify refreshUsers was called
    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is connected but API call fails with a server error (500-599)
    //Then: The worker should retry the API call

    @Test
    fun testGitHubUserSyncWorker_retryOnServerError() = runBlocking {
        // Simulate network connected but API fails
        every { networkMonitor.isConnected.value } returns true
        // Simulate HttpException with status code within 500 - 599 (Server errors)
        coEvery { repository.refreshUsers() } throws
                HttpException(
                    Response.error<Any>(
                        599, "Server error".toResponseBody(null)
                    )
                )

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context)
            .setWorkerFactory(
                workerFactory
            ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.retry()::class.java)
        )
        coVerify(atLeast = 1) { repository.refreshUsers() } // Verify refreshUsers was called
    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is connected but API call fails with an unexpected error
    //Then: The worker should return failure

    @Test
    fun testGitHubUserSyncWorker_failureOnUnexpectedError() = runBlocking {
        // Simulate network connected but API fails
        every { networkMonitor.isConnected.value } returns true
        // Simulate Unexpected error
        coEvery { repository.refreshUsers() } throws Exception("Unexpected error")

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context)
            .setWorkerFactory(
                workerFactory
            ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.failure()::class.java)
        )
        coVerify(exactly = 1) { repository.refreshUsers() } // Verify refreshUsers was called
    }

    //Given: A GitHubUserSyncWorker instance
    //When: The network is connected but API call fails with an IOException
    //Then: The worker should retry the API call


    @Test
    fun testGitHubUserSyncWorker_retryOnNetworkError() = runBlocking {
        // Simulate network connected but API fails
        every { networkMonitor.isConnected.value } returns true
        // Simulate IOException
        coEvery { repository.refreshUsers() } throws IOException("Network error")

        // Create test worker factory with mocks
        val workerFactory = TestGitHubUserSyncWorkerFactory(
            repository,
            networkMonitor
        )

        // Create input data
        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)
            .build()

        // Build the worker using TestListenableWorkerBuilder and custom factory
        val worker = TestListenableWorkerBuilder<GitHubUserSyncWorker>(context)
            .setWorkerFactory(
                workerFactory
            ).setInputData(inputData).build()

        // Perform work
        val result = worker.doWork()
        // Verify behavior
        assertThat(
            result,
            instanceOf(ListenableWorker.Result.retry()::class.java)
        )
    }

}