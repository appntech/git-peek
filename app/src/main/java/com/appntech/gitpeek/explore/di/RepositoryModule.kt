package com.appntech.gitpeek.explore.di

import com.appntech.gitpeek.explore.data.repository.GitHubUserRepository
import com.appntech.gitpeek.explore.data.repository.GitHubUserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindGitHubUserRepository(
        gitHubUserRepositoryImpl: GitHubUserRepositoryImpl
    ): GitHubUserRepository
}