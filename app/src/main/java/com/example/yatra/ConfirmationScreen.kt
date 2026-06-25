package com.example.yatra

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun ConfirmationScreen(destination: Destination, onDismiss: () -> Unit) {
    var showCheckmark by remember { mutableStateOf(false) }
    var showSuccessText by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(false) }

    val currentDate = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(Unit) {
        delay(300)
        showCheckmark = true
        delay(600)
        showSuccessText = true
        delay(800)
        showCard = true
    }

    // New Checkmark Animation Scale
    val checkmarkScale by animateFloatAsState(
        targetValue = if (showCheckmark) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "checkmarkScale"
    )

    val bounceScale by animateFloatAsState(
        targetValue = if (showSuccessText) 1.0f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessLow),
        label = "bounceScale"
    )

    val cardOffset by animateFloatAsState(
        targetValue = if (showCard) 0f else 1000f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
    ) {
        // Confetti effect
        if (showSuccessText) {
            ConfettiEffect()
        }

        // Fix: Replaced Aeroplane with a clean Checkmark Animation
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(checkmarkScale)
                    .background(Color(0xFF4CAF50), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (showSuccessText) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .scale(bounceScale),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Yayy! Booking Confirmed! 🎉",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Booking Confirmation Card
        if (showCard) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = cardOffset.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = destination.emoji, fontSize = 32.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = destination.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            DetailColumn("Date", currentDate)
                            DetailColumn("Booking ID", "#YTR-${Random.nextInt(1000, 9999)}")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("View Ticket", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun ConfettiEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta)
    
    val particles = remember {
        List(30) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1f,
                speed = Random.nextFloat() * 0.02f + 0.01f,
                color = colors.random(),
                size = Random.nextFloat() * 10f + 5f
            )
        }
    }

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val yPos = (particle.y + progress * 2f) % 1.5f
            if (yPos in 0f..1f) {
                drawCircle(
                    color = particle.color,
                    radius = particle.size,
                    center = Offset(particle.x * size.width, yPos * size.height)
                )
            }
        }
    }
}

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val color: Color,
    val size: Float
)
