package com.appntech.gitpeek.data.repository

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.NetworkType
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.appntech.gitpeek.data.network.FakeGitHubApiService
import com.appntech.gitpeek.data.repository.fake.FakeGitHubUserDao
import com.appntech.gitpeek.data.repository.fake.FakeGitHubUserDetailDao
import com.appntech.gitpeek.explore.data.network.GitHubUserDataSource
import com.appntech.gitpeek.explore.data.network.monitor.NetworkMonitor
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepository
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepositoryImpl
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GitHubUserRepositoryImplTest {

    // Mocks
    private lateinit var networkDataSource: GitHubUserDataSource
    private lateinit var localDataSource: FakeGitHubUserDao
    private lateinit var localDetailDataSource: FakeGitHubUserDetailDao
    private lateinit var gitHubApiService: FakeGitHubApiService
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var repository: GitHubUserRepository
    private lateinit var workManager: WorkManager
    private lateinit var context: Context

    @Before
    fun setup() = runBlocking {
        // Get the Instrumentation context
        context = InstrumentationRegistry.getInstrumentation().targetContext

        // Config SynchronousExecutor
        val config = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        // Get the WorkManager instance
        workManager = WorkManager.getInstance(context)

        // Initialize fake data sources and API service
        localDataSource = FakeGitHubUserDao()
        localDetailDataSource = FakeGitHubUserDetailDao()
        gitHubApiService = FakeGitHubApiService()

        // Mocks for networkDataSource
        networkMonitor = mockk(relaxed = true)
        networkDataSource = mockk()
        coEvery { networkDataSource.fetchGitHubUsers() } returns gitHubApiService.getUsers()

        // Initialize the repository with real context
        repository = GitHubUserRepositoryImpl(
            context = context,
            networkDataSource = networkDataSource,
            localDataSource = localDataSource,
            localDetailDataSource = localDetailDataSource,
            networkMonitor = networkMonitor
        )
    }

    //Given: A GitHubUserRepository instance
    //When: enqueueSyncWork is called
    //Then: The work should be enqueued

    @Test
    @Throws(Exception::class)
    fun enqueueSyncWorkShouldEnqueueWork() {

        // enqueueSyncWork is called
        repository.enqueueSyncWork(GitHubUserSyncWorker.WORK_NAME)

        // Get the WorkInfo for the enqueued work
        val workInfoList = workManager.
        getWorkInfosForUniqueWork(GitHubUserSyncWorker.WORK_NAME).get()

        // Assert that the work is enqueued and is in the correct state
        assert(workInfoList.isNotEmpty())
        assert(workInfoList[0].state == WorkInfo.State.ENQUEUED)
    }

    //Given: A GitHubUserRepository instance
    //When: enqueueSyncWork is called more than once
    //Then: Only the initial work should be kept in the queue

    @Test
    @Throws(Exception::class)
    fun enqueueSyncWorkShouldKeepExistingWork() {

        // Enqueue the first work
        repository.enqueueSyncWork(GitHubUserSyncWorker.WORK_NAME)

        // Get the WorkInfo and WorkRequestId for the first enqueued work
        val firstWorkInfoList = workManager.
        getWorkInfosForUniqueWork(GitHubUserSyncWorker.WORK_NAME).get()

        val firstWorkRequestId = firstWorkInfoList[0].id

        // Assert that the work is enqueued and is in the correct state
        assert(firstWorkInfoList.isNotEmpty())
        assert(firstWorkInfoList[0].state == WorkInfo.State.ENQUEUED)


        // Attempt to enqueue the second work
        repository.enqueueSyncWork(GitHubUserSyncWorker.WORK_NAME)

        // Get the WorkInfo and WorkRequestId for the second enqueued work
        val secondWorkInfoList = workManager.
        getWorkInfosForUniqueWork(GitHubUserSyncWorker.WORK_NAME).get()

        val secondWorkRequestId = secondWorkInfoList[0].id

        // Assert that only one work with the same ID is enqueued and is in the correct state
        assert(secondWorkInfoList.size == 1)
        assert(firstWorkRequestId == secondWorkRequestId)
        assert(secondWorkInfoList[0].state == WorkInfo.State.ENQUEUED)
    }

    //Given: A GitHubUserRepository instance
    //When: enqueueSyncWork is called
    //Then: The work should be enqueued with the correct constraints

    @Test
    @Throws(Exception::class)
    fun enqueueSyncWorkShouldSetCorrectConstraints() {

        // Enqueue the work
        repository.enqueueSyncWork(GitHubUserSyncWorker.WORK_NAME)

        // Get the WorkInfo for the enqueued work
        val workInfoList = workManager.
        getWorkInfosForUniqueWork(GitHubUserSyncWorker.WORK_NAME).get()

        // Assert that the work is enqueued with the correct constraints
        assert(workInfoList.isNotEmpty())
        assert(workInfoList[0].state == WorkInfo.State.ENQUEUED)
        assert(workInfoList[0].constraints.requiredNetworkType == NetworkType.CONNECTED)

    }
}
