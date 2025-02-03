package com.appntech.gitpeek.explore.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepository
import com.appntech.gitpeek.explore.data.work.GitHubUserSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitHubUserDetailViewModel @Inject constructor(
    private val gitHubUserRepository: GitHubUserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GitHubUserDetailUiState(isLoading = true))
    val uiState: StateFlow<GitHubUserDetailUiState> = _uiState

    private var fetchJob: Job? = null
    private var syncJob: Job? = null

    init {
        val username = savedStateHandle.get<String>("username")
        if (username != null) {
            syncUserDetails(username)
        } else {
            _uiState.value = GitHubUserDetailUiState(
                isLoading = false,
                isError = true,
                errorMessage = "Username not provided"
            )
        }
    }

    private fun syncUserDetails(username: String) {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            gitHubUserRepository.enqueueSyncWork(
                GitHubUserSyncWorker.WORK_TYPE_USER_DETAIL,
                username
            )
                .filter { it?.state?.isFinished ?: false } // Only trigger fetch when work is done
                .collect {
                    fetchUserDetail(username) // Fetch updated user's details from Room
                }
        }
    }


    private fun fetchUserDetail(username: String) {
        // Cancel any previous fetch before starting a new one.
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {

                gitHubUserRepository.fetchUserDetail(username)
                    .catch { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = e.message
                            )
                        }
                    }
                    .collectLatest { detail ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = false,
                                userDetail = detail
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}
