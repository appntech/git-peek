package network

import explore.data.network.NetworkGitHubUser
import explore.data.network.GitHubApiService

class FakeGitHubApiService: GitHubApiService {
    override suspend fun getUsers(): List<NetworkGitHubUser> {
        return listOf(
            NetworkGitHubUser(
                login = "user1",
                id = 1,
                nodeId = "MDQ6VXNlcjE=",
                avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
                gravatarId = "",
                url = "https://api.github.com/users/user1",
                githubProfileUrl = "https://github.com/user1",
                followersUrl = "https://api.github.com/users/user1/followers",
                followingUrl = "https://api.github.com/users/user1/following{/other_user}",
                gistsUrl = "https://api.github.com/users/user1/gists{/gist_id}",
                starredUrl = "https://api.github.com/users/user1/starred{/owner}{/repo}",
                subscriptionsUrl = "https://api.github.com/users/user1/subscriptions",
                organizationsUrl = "https://api.github.com/users/user1/orgs",
                reposUrl = "https://api.github.com/users/user1/repos",
                eventsUrl = "https://api.github.com/users/user1/events{/privacy}",
                receivedEventsUrl = "https://api.github.com/users/user1/received_events",
                userViewType = "Public",
                userType = "User",
                siteAdmin = false
            ),
            NetworkGitHubUser(
                login = "user2",
                id = 2,
                nodeId = "MDQ6VXNlcjI=",
                avatarUrl = "https://avatars.githubusercontent.com/u/2?v=4",
                gravatarId = "",
                url = "https://api.github.com/users/user2",
                githubProfileUrl = "https://github.com/user2",
                followersUrl = "https://api.github.com/users/user2/followers",
                followingUrl = "https://api.github.com/users/user2/following{/other_user}",
                gistsUrl = "https://api.github.com/users/user2/gists{/gist_id}",
                starredUrl = "https://api.github.com/users/user2/starred{/owner}{/repo}",
                subscriptionsUrl = "https://api.github.com/users/user2/subscriptions",
                organizationsUrl = "https://api.github.com/users/user2/orgs",
                reposUrl = "https://api.github.com/users/user2/repos",
                eventsUrl = "https://api.github.com/users/user2/events{/privacy}",
                receivedEventsUrl = "https://api.github.com/users/user2/received_events",
                userViewType = "Public",
                userType = "User",
                siteAdmin = false
            )
        )

    }
}
