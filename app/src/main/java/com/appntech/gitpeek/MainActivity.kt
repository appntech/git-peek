package com.appntech.gitpeek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.appntech.gitpeek.explore.ui.GitHubUserViewModel
import com.appntech.gitpeek.explore.ui.navigation.GitPeekNavGraph
import com.appntech.gitpeek.explore.ui.theme.GitPeekTheme
import com.example.gitpeek.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitPeekTheme {
                SplashScreen()
                GitPeekNavGraph()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    var showGit by remember { mutableStateOf(true) }
    var showPeek by remember { mutableStateOf(false) }
    var fadeOut by remember { mutableStateOf(false) }

    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    val gitFontName = GoogleFont("Roboto Mono")
    val peekFontName = GoogleFont("Poppins")

    val gitFontFamily = FontFamily(
        Font(googleFont = gitFontName, fontProvider = provider)
    )

    val peekFontFamily = FontFamily(
        Font(googleFont = peekFontName, fontProvider = provider)
    )

    // Animating the letters of "Git"
    val gitText = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Typewriter effect for "Git" (2 seconds)
        for (i in 0..5) {
            delay(500)  // Delay between each letter
            gitText.value += when (i) {
                0 -> "G"
                1 -> "i"
                2 -> "t"
                else -> "."
            }
        }

        delay(2000) // Wait for 2 seconds (Git complete)

        gitText.value = gitText.value.replaceFirst("...","")
        showPeek = true // Show "Peek" text after Git

        delay(1000) // Wait for 1 second before fading out "Peek"

        fadeOut = true
    }

    // Create fade animation for the entire splash screen
    val fadeAnim = animateFloatAsState(
        targetValue = if (fadeOut) 0f else 1f,
        animationSpec = tween(1000),
        label = "FadeAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize() // Fills the entire screen
            .alpha(fadeAnim.value) // Apply the fade out to the entire screen
            .zIndex(1f)
            .background(Color.Gray)
            .padding(16.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .alpha(fadeAnim.value) // Apply the fade out to the entire screen
        ) {
            // Git Text with Typewriter effect
            AnimatedVisibility(visible = showGit) {
                Text(
                    text = gitText.value,
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontFamily = gitFontFamily,
                        color = Color.Black
                    )
                )
            }

            // Peek Text with Fade-in effect
            AnimatedVisibility(visible = showPeek) {
                Text(
                    text = "Peek",
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontFamily = peekFontFamily,
                        color = Color.White


                    ),
                    modifier = Modifier.alpha(1f) // Fade in effect, stays visible
                )
            }
        }
    }
}