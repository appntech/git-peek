package com.appntech.gitpeek.explore.data.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.appntech.gitpeek.explore.data.network.monitor.NetworkMonitor
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@HiltWorker
class GitHubUserSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: GitHubUserRepository,
    private val networkMonitor: NetworkMonitor
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (!networkMonitor.isConnected.value) {
                return Result.retry() // Retry when network is available
            }

            when (inputData.getString(WORK_TYPE_KEY)) {
                WORK_TYPE_USERS -> repository.refreshUsers()
                WORK_TYPE_USER_DETAIL -> {
                    val username = inputData.getString(USERNAME_KEY) ?: return Result.failure()
                    repository.refreshUserDetail(username)
                }
                else -> return Result.failure() // Invalid work type
            }

            Result.success()
        }
        catch (e: Exception) {
            Log.e(WORK_NAME, "Error syncing GitHub users")
            Log.e(WORK_NAME, e.toString())

            return when (e) {
                is HttpException -> {
                    when (e.code()) {
                        401 -> {
                            Log.e(WORK_NAME, "Unauthorized: ${e.message} - Failed to sync GitHub users")
                            Result.failure() // Unauthorized - no retry
                        }
                        403 -> {
                            Log.e(WORK_NAME, "Forbidden: ${e.message} - Failed to sync GitHub users")
                            Result.failure() // Forbidden - no retry
                        }
                        429 -> {
                            Log.e(WORK_NAME, "Rate limit exceeded: ${e.message} - Failed to sync GitHub users")
                            Result.failure() // Rate limit exceeded - no retry
                        }
                        in 400..499 -> {
                            Log.e(WORK_NAME, "Client error: ${e.message} - Retry syncing GitHub users")
                            Result.retry() // Retry for client errors
                        }
                        in 500..599 -> {
                            Log.e(WORK_NAME, "Server error: ${e.message} - Retry syncing GitHub users")
                            Result.retry() // Retry for server errors
                        }
                        else -> {
                            Log.e(WORK_NAME, "HTTP error: ${e.message} - Retry syncing GitHub users")
                            Result.retry() // Retry other errors
                        }
                    }
                }
                is IOException -> {
                    Log.e(WORK_NAME, "Network error: ${e.message} retry syncing GitHub users")
                    Result.retry() // Retry for network issues
                }
                else -> {
                    Log.e(WORK_NAME, "Unexpected error: ${e.message} + ${e.javaClass} - marking as failed request")
                    Result.failure() // Only return failure when exception is completely unhandled
                }
            }
        }
    }

    companion object {
        const val WORK_NAME = "GitHubUserSyncWorker"
        const val WORK_TYPE_KEY = "WORK_TYPE"
        const val USERNAME_KEY = "USERNAME"
        const val WORK_TYPE_USERS = "USERS"
        const val WORK_TYPE_USER_DETAIL = "USER_DETAIL"
    }
}
