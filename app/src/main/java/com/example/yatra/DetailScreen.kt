package com.example.yatra

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun DetailScreen(destination: Destination, onBack: () -> Unit, onBook: () -> Unit) {
    val scrollState = rememberScrollState()
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    val heroScale by animateFloatAsState(
        targetValue = if (showContent) 1f else 1.1f,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        label = "heroScale"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) { // Background black for contrast
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .graphicsLayer {
                        alpha = 1f - (scrollState.value.toFloat() / 1500f).coerceIn(0f, 1f)
                        translationY = scrollState.value.toFloat() * 0.4f
                    }
                    .scale(heroScale)
            ) {
                // Fix 1: Use Coil for images
                AsyncImage(
                    model = destination.imageUrl,
                    contentDescription = destination.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black
                                )
                            )
                        )
                )
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInVertically(initialOffsetY = { 40 }) + fadeIn()
                ) {
                    Column {
                        // Fix 6: Ensure text is visible (using White)
                        Text(
                            text = destination.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📍 ${destination.location}",
                                color = Color.LightGray,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${destination.altitude}",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating Stars
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        var starVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(500 + (index * 100L))
                            starVisible = true
                        }
                        AnimatedVisibility(
                            visible = starVisible,
                            enter = scaleIn(initialScale = 0f) + fadeIn()
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (index < destination.rating.toInt()) Color(0xFFFFD700) else Color.DarkGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = destination.rating.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Fix 6: Weather values visibility
                WeatherRow(destination)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = destination.description,
                    color = Color.LightGray.copy(alpha = 0.9f),
                    lineHeight = 26.sp,
                    fontSize = 16.sp
                )

                // Fix 6: Increased padding at the bottom so description isn't covered by BookingBar
                Spacer(modifier = Modifier.height(150.dp)) 
            }
        }

        // Top Bar
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Bottom Booking Bar
        BookingBar(
            price = destination.price,
            onBook = onBook,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun WeatherRow(destination: Destination) {
    var animatedTemp by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(800)
        animate(0f, destination.temp.toFloat(), animationSpec = tween(1500)) { value, _ ->
            animatedTemp = value.toInt()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        WeatherItem("Temp", "$animatedTemp°C", "🌡️")
        WeatherItem("Humidity", "${destination.humidity}%", "💧")
        WeatherItem("Wind", "${destination.wind} km/h", "💨")
    }
}

@Composable
fun WeatherItem(label: String, value: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun BookingBar(price: Int, onBook: () -> Unit, modifier: Modifier = Modifier) {
    var animatedPrice by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(1000)
        animate(0f, price.toFloat(), animationSpec = tween(2000)) { value, _ ->
            animatedPrice = value.toInt()
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF121212), // Darker surface
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Total Price", color = Color.Gray, fontSize = 12.sp)
                Text(
                    text = "₹$animatedPrice",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Button(
                onClick = onBook,
                modifier = Modifier
                    .height(56.dp)
                    .width(160.dp)
                    .scale(pulseScale),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
