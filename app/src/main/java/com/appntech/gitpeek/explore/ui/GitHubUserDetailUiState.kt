package com.appntech.gitpeek.explore.ui

import com.appntech.gitpeek.explore.data.model.GitHubUserDetail

data class GitHubUserDetailUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val userDetail: GitHubUserDetail? = null,
    val errorMessage: String? = null
)
