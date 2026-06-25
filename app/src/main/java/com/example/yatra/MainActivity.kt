package com.example.yatra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yatra.ui.theme.YatraTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YatraTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    var isCarAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home",
            enterTransition = { 
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) + fadeIn() 
            },
            exitTransition = { 
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(500)) + fadeOut() 
            },
            popEnterTransition = { 
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(500)) + fadeIn() 
            },
            popExitTransition = { 
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) + fadeOut() 
            }
        ) {
            composable("home") {
                HomeScreen(onDestinationClick = { destination ->
                    scope.launch {
                        isCarAnimating = true
                        delay(2000) // Longer duration for slower car
                        isCarAnimating = false
                        navController.navigate("detail/${destination.id}")
                    }
                })
            }
            composable(
                route = "detail/{destinationId}",
                arguments = listOf(navArgument("destinationId") { type = NavType.IntType })
            ) { backStackEntry ->
                val destId = backStackEntry.arguments?.getInt("destinationId")
                val destination = destinations.find { it.id == destId }
                destination?.let {
                    DetailScreen(
                        destination = it,
                        onBack = { navController.popBackStack() },
                        onBook = { navController.navigate("confirmation/${it.id}") }
                    )
                }
            }
            composable(
                route = "confirmation/{destinationId}",
                arguments = listOf(navArgument("destinationId") { type = NavType.IntType })
            ) { backStackEntry ->
                val destId = backStackEntry.arguments?.getInt("destinationId")
                val destination = destinations.find { it.id == destId }
                destination?.let {
                    ConfirmationScreen(
                        destination = it,
                        onDismiss = {
                            navController.popBackStack("home", inclusive = false)
                        }
                    )
                }
            }
        }

        // The Car Animation Transition Overlay
        if (isCarAnimating) {
            CarTransitionOverlay()
        }
    }
}

@Composable
fun CarTransitionOverlay() {
    var startAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnim = true
    }
    
    val carX by animateFloatAsState(
        targetValue = if (startAnim) 1.5f else -0.5f,
        animationSpec = tween(2000, easing = LinearEasing), // Slower speed (2000ms)
        label = "carX"
    )

    // Wheel/Friction animation
    val infiniteTransition = rememberInfiniteTransition(label = "friction")
    val frictionScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "frictionScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)) // Slightly more opaque for focus
    ) {
        // Road animation removed as per request #3

        // The 🚗 driving across
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = (carX * 600 - 200).dp)
                    .scale(scaleX = -1f, scaleY = 1f) // Flip the car emoji horizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🚗",
                        fontSize = 45.sp // Smaller car as per request #2
                    )
                    // Friction/Speed effect near wheels - minimized accordingly
                    Row {
                        Text(text = "💨", fontSize = 10.sp, modifier = Modifier.scale(frictionScale).alpha(0.7f))
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = "💨", fontSize = 10.sp, modifier = Modifier.scale(frictionScale).alpha(0.7f))
                    }
                }
            }
        }
    }
}
