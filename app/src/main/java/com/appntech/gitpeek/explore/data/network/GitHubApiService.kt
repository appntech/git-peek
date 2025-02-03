package com.appntech.gitpeek.explore.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService {

    @GET("users")
    suspend fun getUsers(): List<NetworkGitHubUser>

    @GET("users/{username}")
    suspend fun getUserDetail(
        @Path("username") username: String
    ): NetworkGitHubUserDetail

}