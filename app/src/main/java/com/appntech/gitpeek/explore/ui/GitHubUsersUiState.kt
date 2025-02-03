package com.appntech.gitpeek.explore.ui

import com.appntech.gitpeek.explore.data.model.GitHubUser

/**
 * UiState for the GitHub user list screen.
 */

data class GitHubUsersUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    val userItems: List<GitHubUser> = emptyList(),
    val userMessage: String? = null,
    val currentTimestamp: String? = null
)