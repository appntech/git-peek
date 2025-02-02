package com.appntech.gitpeek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.appntech.gitpeek.explore.ui.GitHubUserScreen
import com.appntech.gitpeek.explore.ui.GitHubUserViewModel
import com.appntech.gitpeek.explore.ui.theme.GitPeekTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: GitHubUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitPeekTheme {
                GitHubUserScreen(viewModel)
            }
        }
    }
}