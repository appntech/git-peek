import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.appntech.gitpeek.explore.data.model.GitHubUserDetail
import com.appntech.gitpeek.explore.ui.GitHubUserDetailViewModel
import com.example.gitpeek.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubUserDetailScreen(
    username: String,
    navController: NavController,
    viewModel: GitHubUserDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = uiState.userDetail?.username ?: "User Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.isError -> {
                    Text(
                        text = uiState.errorMessage ?: "Error loading user details",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.userDetail != null -> {
                    UserDetailContent(userDetail = uiState.userDetail)
                }
            }
        }
    }
}

@Composable
fun UserDetailContent(userDetail: GitHubUserDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            AsyncImage(
                model = userDetail.profilePictureUrl,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.onPrimaryContainer, CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Name and Username
            Text(
                text = userDetail.name ?: userDetail.username,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Location
            userDetail.location?.let { location ->
                Text(
                    text = location,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            // Bio
            userDetail.bio?.let { bio ->
                DetailRow(label = "", value = bio)
            }
        }

        // Followers, Following, Repos, Gists
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                Modifier.padding(top = 8.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    DetailStat(label = "Followers", value = userDetail.followersCount)
                    DetailStat(label = "Following", value = userDetail.followingCount)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    DetailStat(label = "Repos", value = userDetail.publicReposCount)
                    DetailStat(label = "Gists", value = userDetail.publicGistsCount)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        // Blog, Twitter, GitHub Profile
        userDetail.blogUrl?.takeIf { it.isNotBlank() }?.let {
            DetailIcon({
                // Twitter Icon
                Image(
                    painter = painterResource(R.drawable.ic_website),
                    contentDescription = "Twitter Icon",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }, value = it)
        }
        userDetail.twitterUsername?.let {
            DetailIcon({
                // Twitter Icon
                Image(
                    painter = painterResource(R.drawable.ic_x),
                    contentDescription = "Twitter Icon",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }, value = it)
        }
        DetailIcon({
            Image(
                painter = painterResource(R.drawable.ic_github),
                contentDescription = "Twitter Icon",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        }, value = userDetail.githubProfileUrl)
        // Created and Updated Timestamps
        Spacer(modifier = Modifier.height(32.dp))
        DetailRow(label = "Joined on", value = userDetail.accountCreatedAt)
        DetailRow(label = "Last updated on", value = userDetail.lastUpdatedAt)
    }
}

@Composable
fun DetailIcon(icon: @Composable () -> Unit, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(24.dp))
        Text(text = value)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun DetailStat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}