package com.appntech.gitpeek.data.work.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.appntech.gitpeek.explore.data.network.monitor.NetworkMonitor
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepository
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker

class TestGitHubUserSyncWorkerFactory(
    private val repository: GitHubUserRepository,
    private val networkMonitor: NetworkMonitor
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            GitHubUserSyncWorker::class.java.name ->
                GitHubUserSyncWorker(appContext, workerParameters, repository, networkMonitor)

            else -> null
        }
    }
}