package com.appntech.gitpeek.explore.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubUserScreen(viewModel: GitHubUserViewModel = hiltViewModel()) {

    // Observe the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // The search query state
    var searchQuery by remember { mutableStateOf("") }

    // Determines if the search bar is expanded
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Create a filtered list based on the search query.
    // Adjust "name" to whatever property you need to filter on.
    val filteredUserItems = if (searchQuery.isEmpty()) {
        uiState.userItems
    } else {
        uiState.userItems.filter { user ->
            user.username.contains(searchQuery, ignoreCase = true)
        }
    }

    // Create a LazyListState to control the LazyColumn scrolling.
    val listState = rememberLazyListState()
    // Create a coroutine scope to call animateScrollToItem.
    val coroutineScope = rememberCoroutineScope()

    val refreshOffset by animateDpAsState(
        targetValue = if (uiState.isRefreshing) 56.dp else 0.dp,
        label = "refreshOffset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(
            Modifier
                .fillMaxSize()
                .semantics { isTraversalGroup = true }) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = { newQuery ->
                            // Update the search query which in turn updates the filtered list
                            searchQuery = newQuery
                        },
                        onSearch = { expanded = false },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text("Search users") },
                        leadingIcon = {
                            if (!expanded) {
                                androidx.compose.material3.Icon(
                                    Icons.Default.Search,
                                    contentDescription = null
                                )
                            } else {
                                androidx.compose.material3.Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        expanded = false
                                    })
                            }
                        },
                        trailingIcon = {
                            if (expanded && searchQuery.isNotEmpty()) {
                                androidx.compose.material3.Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        searchQuery = ""
                                    }
                                )
                            }
                        }
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    filteredUserItems.take(4).forEach { item ->
                        ListItem(
                            headlineContent = { Text(item.username) },
                            supportingContent = { Text(item.userType) },
                            leadingContent = {
                                AsyncImage(
                                    model = item.profilePictureUrl,
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier =
                            Modifier
                                .clickable {
                                    // When a suggestion is clicked, update the search field and close suggestions
                                    searchQuery = item.username
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                    expanded = false
                                }
                                .fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display loading or error if present
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.isError -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${uiState.userMessage}", color = Color.Red)
                    }
                }
                else -> {
                    // If the list is empty, show a message or loading indicator
                    if (uiState.userItems.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No users found.")
                            Text(
                                text = "Please check your network and pull to refresh.",
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = viewModel::onPullToRefreshTrigger,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = refreshOffset)
                    ) {

                        //Last updated timestamp
                        if (uiState.isRefreshing) {
                            Text(
                                "Last updated: ${getCurrentTimestamp()}",
                                modifier = Modifier
                                    .padding(top = 80.dp)
                                    .align(Alignment.TopCenter)
                            )
                        }

                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = 96.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .semantics { traversalIndex = 1f }
                                .fillMaxSize()
                        ) {
                            items(filteredUserItems) { user ->
                                GitHubUserItem(user = user, onItemClick = { clickedUser ->
                                    viewModel.onGitHubUserClick(clickedUser)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCurrentTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}