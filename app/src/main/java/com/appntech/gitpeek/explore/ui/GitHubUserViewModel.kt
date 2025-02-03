package com.appntech.gitpeek.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepository
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GitHubUserViewModel @Inject constructor(
    private val gitHubUserRepository: GitHubUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GitHubUsersUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchJob: Job? = null
    private var syncJob: Job? = null

    init {
        fetchGitHubUsers() // Fetch cached data from Room immediately.
    }

    private fun syncGitHubUsers() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            gitHubUserRepository.enqueueSyncWork(GitHubUserSyncWorker.WORK_TYPE_USERS)
                .filter { it?.state?.isFinished ?: false } // Only trigger fetch when work is done
                .collect {
                    fetchGitHubUsers() // Fetch updated users from Room
                }
        }
    }

    private fun fetchGitHubUsers() {

        // Cancel any previous fetch before starting a new one.
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            gitHubUserRepository.fetchUsers()
                .onStart {
                    // Reset error state
                    _uiState.update {
                        it.copy(
                            isError = false,
                            userMessage = null
                        )
                    }

                    // For user to see the last updated timestamp (for demo purposes only)
                    delay(1500)
                }
                .catch { exception ->
                    // Handle exceptions and set the error state.
                    val errorMsg = when (exception) {
                        is IOException -> getMessagesFromException(exception)
                        else -> "Error fetching users: ${exception.message}"
                    }
                    _uiState.update { it.copy(isError = true, userMessage = errorMsg) }
                }
                .collectLatest { users ->
                    _uiState.update {
                        it.copy(
                            userItems = users,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    // IOException message handler
    private fun getMessagesFromException(ioe: IOException): String {
        return when (ioe.message) {
            "No internet" -> "No internet"
            else -> ioe.message ?: "Unknown error"
        }
    }

    fun onPullToRefreshTrigger() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, currentTimestamp = getCurrentTimestamp()) }
            try {
                // For user to see the last updated timestamp (for demo purposes only)
                delay(1500)
                syncGitHubUsers() // Force a fresh sync
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isError = true,
                        userMessage = "Refresh failed: ${e.message}"
                    )
                }
            }

            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

}
