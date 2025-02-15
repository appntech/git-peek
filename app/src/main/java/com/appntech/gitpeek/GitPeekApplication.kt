package com.appntech.gitpeek

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker.Companion.WORK_TYPE_KEY
import com.appntech.gitpeek.explore.data.work.factory.GitHubUserSyncWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class GitPeekApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var syncWorkerFactory: GitHubUserSyncWorkerFactory

    // Implemented the required property instead of the method (different from the android docs)
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(syncWorkerFactory)
            .setMinimumLoggingLevel(android.util.Log.VERBOSE)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Trigger the first sync immediately
        enqueueInitialSyncWork()
    }

    private fun enqueueInitialSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString(WORK_TYPE_KEY, GitHubUserSyncWorker.WORK_TYPE_USERS)  // Example input data
            .build()


        val workRequest = OneTimeWorkRequestBuilder<GitHubUserSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                MIN_BACKOFF_DELAY,
                TimeUnit.MILLISECONDS
            ).build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            GitHubUserSyncWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    companion object {
        // Minimum delay in milliseconds (1 minute) from GitHub API docs
        const val MIN_BACKOFF_DELAY = 60000L
    }

}