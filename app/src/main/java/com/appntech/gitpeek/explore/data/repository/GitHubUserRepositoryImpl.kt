package com.appntech.gitpeek.explore.data.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.appntech.gitpeek.explore.data.local.GitHubUserDao
import com.appntech.gitpeek.explore.data.local.GitHubUserDetailDao
import com.appntech.gitpeek.explore.data.local.asExternalModel
import com.appntech.gitpeek.explore.data.model.GitHubUser
import com.appntech.gitpeek.explore.data.model.GitHubUserDetail
import com.appntech.gitpeek.explore.data.network.GitHubUserDataSource
import com.appntech.gitpeek.explore.data.network.asEntities
import com.appntech.gitpeek.explore.data.network.asEntity
import com.appntech.gitpeek.explore.data.network.monitor.NetworkMonitor
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GitHubUserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkDataSource: GitHubUserDataSource,
    private val localDataSource: GitHubUserDao,
    private val localDetailDataSource: GitHubUserDetailDao,
    private val networkMonitor: NetworkMonitor
): GitHubUserRepository {

    override fun fetchUsers(): Flow<List<GitHubUser>> {
        // Return the local users from the database
        return localDataSource.getAllUsers().map { users ->
            users.asExternalModel()
        }
    }

    override fun fetchUserDetail(username: String): Flow<GitHubUserDetail> {
        return localDetailDataSource.getUserDetailByUsername(username).map { userDetail ->
            userDetail.asExternalModel()
        }
    }

    override suspend fun refreshUsers() {

        if (!networkMonitor.isConnected.first()) {
            //"No internet, skipping network fetch"
            return
        }

        // Fetch users from the network
        val networkUsers = networkDataSource.fetchGitHubUsers()

        // Update the local database with users from the network
        localDataSource.upsertAll(networkUsers.asEntities())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun refreshUserDetail(username: String) {
        if(!networkMonitor.isConnected.first()) {
            //"No internet, skipping network fetch"
            return
        }

        val networkUserDetail = networkDataSource.fetchUserDetail(username)

        localDetailDataSource.upsertUserDetail(networkUserDetail.asEntity())

    }

    override fun enqueueSyncWork(workType: String, username: String?): Flow<WorkInfo?> {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<GitHubUserSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                MIN_BACKOFF_DELAY,
                TimeUnit.MILLISECONDS
            ).setInputData(
                workDataOf(
                    GitHubUserSyncWorker.WORK_TYPE_KEY to workType,
                    GitHubUserSyncWorker.USERNAME_KEY to username
                )
            )
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            GitHubUserSyncWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        // Returns Flow<WorkInfo>
        return workManager.getWorkInfosForUniqueWorkFlow(
            GitHubUserSyncWorker.WORK_NAME
        ).map {
            workInfoList -> workInfoList.firstOrNull() // Extract only the first WorkInfo
        }


    }

    companion object {
        // Minimum delay in milliseconds (1 minute) from GitHub API docs
        const val MIN_BACKOFF_DELAY = 60000L
    }

}