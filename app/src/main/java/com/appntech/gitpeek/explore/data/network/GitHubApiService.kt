package com.appntech.gitpeek.explore.data.network

import retrofit2.http.GET

interface GitHubApiService {

    @GET("users")
    suspend fun getUsers(): List<NetworkGitHubUser>
}