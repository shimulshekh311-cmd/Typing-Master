package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OnboardingSlideItem(
    val title: String,
    val subtitle: String,
    val description: String,
    val badge: String,
    val imageResId: Int?
)

@Composable
fun OnboardingWelcomeScreen(
    viewModel: TypingViewModel,
    onDismissOrContinue: () -> Unit,
    canDismiss: Boolean = true,
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
            description = "স্বাভাবিক গতি বাড়ানোর জন্য বর্ণমালা, যুক্তবর্ণ, শব্দ ও পূর্ণ বাক্যের ধাপে ধাপে পাঠ এবং ভিজ্যুয়াল কি-বোর্ড গাইড।",
            badge = "ইন্টারেক্টিভ লেসন",
            imageResId = R.drawable.img_slide_typing
        ),
        OnboardingSlideItem(
            title = "লাইভ টাইপিং স্পিড টেস্ট",
            subtitle = "WPM ও নির্ভুলতা পরিমাপ করুন",
            description = "প্রতি মিনিটের শব্দ গতি (WPM), নির্ভুলতা হার (Accuracy) এবং রিয়েল-টাইম ভুল ডিটেকশন দিয়ে নিজের স্পিড বাড়ান।",
            badge = "রিয়েল-টাইম মেট্রিক্স",
            imageResId = R.drawable.img_slide_speedtest
        ),
        OnboardingSlideItem(
            title = "অনলাইন গ্লোবাল লিডারবোর্ড",
            subtitle = "সবার সাথে প্রতিযোগিতা করুন",
            description = "গুগল একাউন্টে লগইন করে নিজের সেরা টাইপিং রেকর্ড জাতীয় অনলাইন লিডারবোর্ডে যুক্ত করুন এবং র‍্যাঙ্কিং দেখুন।",
            badge = "লাইভ লিডারবোর্ড",
            imageResId = R.drawable.img_slide_leaderboard
        ),
        OnboardingSlideItem(
            title = "বিস্তারিত অগ্রগতি ও রিপোর্ট",
            subtitle = "আপনার টাইপিং ট্র্যাক করুন",
            description = "প্রতিটি টেস্টের ফলাফল, দৈনিক অনুশীলন সময় এবং বিগত দিনগুলোর অগ্রগতির রিপোর্ট এক জায়গায় পর্যবেক্ষণ করুন।",
            badge = "পারফরম্যান্স ট্র্যাক",
            imageResId = R.drawable.img_slide_speedtest
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto-advance slides every 4.5 seconds
    LaunchedEffect(pagerState.currentPage) {
        delay(4500)
        coroutineScope.launch {
            val nextPage = (pagerState.currentPage + 1) % slides.size
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(600))
        }
    }

    // When signed in successfully, dismiss automatically
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onDismissOrContinue()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0B0F19),
                        Color(0xFF07090E)
                    )
                )
            )
            .testTag("onboarding_welcome_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Skip / Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyanAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TM",
                            color = CyanAccent,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Typing Master",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (canDismiss) {
                    TextButton(
                        onClick = onDismissOrContinue,
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "বাদ দিন",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Carousel Slider
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("onboarding_feature_pager")
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Feature Image Banner Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (slide.imageResId != null) {
                                Image(
                                    painter = painterResource(id = slide.imageResId),
                                    contentDescription = slide.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Dark Gradient Overlay for text contrast
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.75f)
                                            )
                                        )
                                    )
                            )

                            // Feature Badge
                            Surface(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .align(Alignment.TopStart),
                                shape = RoundedCornerShape(12.dp),
                                color = CyanAccent.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = slide.badge,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle
                    Text(
                        text = slide.subtitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description
                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 21.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // Pager Indicators
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(slides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) CyanAccent else Color(0xFF475569))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prominent "Log in with Google" Button
            Button(
                onClick = {
                    viewModel.signInWithGoogle(context)
                },
                enabled = !isSigningIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_google_login_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                if (isSigningIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = IndigoPrimary,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "গুগল একাউন্টে যুক্ত হচ্ছে...",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                } else {
                    // Google G Icon
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        color = Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Log in with Google",
                        color = Color(0xFF1F2937),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guest Continue Button
            Button(
                onClick = onDismissOrContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("onboarding_guest_continue_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = CyanAccent
                )
            ) {
                Text(
                    text = "গেস্ট হিসেবে সরাসরি শুরু করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Auth Message or Notice if present
            if (!authMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = authMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
