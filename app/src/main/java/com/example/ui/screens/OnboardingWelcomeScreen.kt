package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.TypingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OnboardingSlideItem(
    val title: String,
    val subtitle: String,
    val description: String,
    val badge: String,
    val imageResId: Int
)

@Composable
fun OnboardingWelcomeScreen(
    viewModel: TypingViewModel,
    onDismissOrContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isSigningIn by viewModel.isSigningIn.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val authMessage by viewModel.authMessage.collectAsStateWithLifecycle()

    val slides = listOf(
        OnboardingSlideItem(
            title = "বাংলা ও ইংরেজি টাইপিং শিক্ষা",
            subtitle = "সহজে ও দ্রুত টাইপিং শিখুন",
            description = "বর্ণমালা, যুক্তবর্ণ, শব্দ ও পূর্ণ বাক্যের ধাপে ধাপে ইন্টারঅ্যাকটিভ পাঠ এবং স্ক্রিন কি-বোর্ড গাইড।",
            badge = "ইন্টারেক্টিভ লেসন",
            imageResId = R.drawable.img_slide_typing
        ),
        OnboardingSlideItem(
            title = "লাইভ টাইপিং স্পিড টেস্ট",
            subtitle = "WPM ও নির্ভুলতা পরিমাপ করুন",
            description = "প্রতি মিনিটের শব্দ গতি (WPM), অ্যাকুরেসি হার এবং লাইভ ভুল সংশোধন দিয়ে টাইপিং স্পিড দ্রুত বাড়ান।",
            badge = "রিয়েল-টাইম মেট্রিক্স",
            imageResId = R.drawable.img_slide_speedtest
        ),
        OnboardingSlideItem(
            title = "অনলাইন গ্লোবাল লিডারবোর্ড",
            subtitle = "সবার সাথে প্রতিযোগিতা করুন",
            description = "গুগল একাউন্টে লগইন করে নিজের সেরা টাইপিং রেকর্ড জাতীয় অনলাইন লিডারবোর্ডে যুক্ত করুন ও র‍্যাঙ্কিং দেখুন।",
            badge = "লাইভ লিডারবোর্ড",
            imageResId = R.drawable.img_slide_leaderboard
        ),
        OnboardingSlideItem(
            title = "ব্যক্তিগত ও মাসিক প্রগ্রেস রিপোর্ট",
            subtitle = "আপনার টাইপিং ট্র্যাক করুন",
            description = "ক্লাউড অটো-সিঙ্ক সহ প্রতিটি টেস্টের ফলাফল ও বিগত দিনগুলোর অগ্রগতির গ্রাফিক্যাল রিপোর্ট।",
            badge = "ক্লাউড ব্যাকআপ ও রিপোর্ট",
            imageResId = R.drawable.img_slide_report
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto-advance slides every 4 seconds
    LaunchedEffect(pagerState.currentPage) {
        delay(4000)
        coroutineScope.launch {
            val nextPage = (pagerState.currentPage + 1) % slides.size
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(650))
        }
    }

    // When signed in successfully, dismiss onboarding screen immediately
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onDismissOrContinue()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF07090E))
            .testTag("onboarding_welcome_screen")
    ) {
        // Full screen Carousel Slider - image occupies almost the entire screen
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("onboarding_feature_pager")
        ) { page ->
            val slide = slides[page]
            Box(modifier = Modifier.fillMaxSize()) {
                // Feature Picture taking virtually full screen
                Image(
                    painter = painterResource(id = slide.imageResId),
                    contentDescription = slide.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Smooth dark gradient overlay from top to bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color.Black.copy(alpha = 0.5f),
                                0.25f to Color.Transparent,
                                0.55f to Color(0xFF07090E).copy(alpha = 0.85f),
                                0.82f to Color(0xFF07090E),
                                1.0f to Color(0xFF07090E)
                            )
                        )
                )

                // Feature details anchored above bottom action panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 140.dp), // Space reserved for indicators and Log in with Google button
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Feature Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyanAccent.copy(alpha = 0.95f),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = slide.badge,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle
                    Text(
                        text = slide.subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // Top App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.65f),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Logo",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Typing Master",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // Bottom Controls: Pager Dots + Log in with Google Button (No Skip, No Guest)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pager Indicators
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(slides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) CyanAccent else Color(0xFF475569))
                    )
                }
            }

            // Auth Message or Error Notice if any
            AnimatedVisibility(visible = !authMessage.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text = authMessage.orEmpty(),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            // Prominent "Log in with Google" Button
            Button(
                onClick = {
                    viewModel.signInWithGoogle(context)
                },
                enabled = !isSigningIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_google_login_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isSigningIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = IndigoPrimary,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "গুগল একাউন্টে যুক্ত হচ্ছে...",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                } else {
                    // Official 4-color Google Logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Log in with Google",
                        color = Color(0xFF1F2937),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
