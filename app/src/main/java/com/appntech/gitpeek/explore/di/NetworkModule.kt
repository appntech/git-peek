package com.appntech.gitpeek.explore.di

import android.content.Context
import com.appntech.gitpeek.explore.data.network.GitHubApiService
import com.appntech.gitpeek.explore.data.network.monitor.NetworkConnectivityProvider
import com.appntech.gitpeek.explore.data.network.monitor.NetworkConnectivityProviderImpl
import com.appntech.gitpeek.explore.data.network.monitor.NetworkMonitor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/vnd.github+json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(Json.asConverterFactory(contentType)) // Using Kotlinx Serialization
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApiService(retrofit: Retrofit): GitHubApiService {
        return retrofit.create(GitHubApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityProvider(@ApplicationContext context: Context): NetworkConnectivityProvider {
        return NetworkConnectivityProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(connectivityProvider: NetworkConnectivityProvider): NetworkMonitor {
        return NetworkMonitor(connectivityProvider)
    }

}

