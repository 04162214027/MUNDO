package com.mobileshop.erp.ui.screens.auth

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.mobileshop.erp.ui.theme.Blue40
import com.mobileshop.erp.ui.theme.Teal40
import kotlinx.coroutines.delay

enum class BiometricStatus {
    AVAILABLE,
    NOT_AVAILABLE,
    NOT_ENROLLED,
    ERROR
}

@Composable
fun LoginScreen(
    onAuthSuccess: () -> Unit,
    onFallbackToPin: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    
    var biometricStatus by remember { mutableStateOf(BiometricStatus.AVAILABLE) }
    var authMessage by remember { mutableStateOf("") }
    var showRetryButton by remember { mutableStateOf(false) }
    var isAuthenticating by remember { mutableStateOf(false) }

    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var fingerprintVisible by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }

    // Pulsating animation for fingerprint
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Sequential animations on launch
    LaunchedEffect(Unit) {
        delay(100)
        logoVisible = true
        delay(300)
        titleVisible = true
        delay(300)
        fingerprintVisible = true
        delay(300)
        buttonsVisible = true
    }

    // Check biometric availability
    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        biometricStatus = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            else -> BiometricStatus.ERROR
        }
    }

    // Auto-trigger biometric prompt when animations complete
    LaunchedEffect(biometricStatus, buttonsVisible) {
        if (biometricStatus == BiometricStatus.AVAILABLE && buttonsVisible && activity != null && !isAuthenticating) {
            delay(500)
            isAuthenticating = true
            showBiometricPrompt(
                activity = activity,
                onSuccess = onAuthSuccess,
                onError = { message ->
                    isAuthenticating = false
                    authMessage = message
                    showRetryButton = true
                    if (message.isNotEmpty()) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        Blue40.copy(alpha = 0.08f),
                        Teal40.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        // Animated background circles
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = 150.dp, y = (-100).dp)
                    .scale(pulseScale * 0.8f)
                    .alpha(0.1f)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(colors = listOf(Blue40, Color.Transparent)))
            )
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-80).dp, y = 50.dp)
                    .scale(pulseScale * 0.9f)
                    .alpha(0.08f)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(colors = listOf(Teal40, Color.Transparent)))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo
            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn(animationSpec = tween(600)) + 
                        scaleIn(initialScale = 0.5f, animationSpec = tween(600, easing = EaseOutBack))
            ) {
                Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(140.dp).scale(pulseScale),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {}
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 12.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Animated Title
            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(animationSpec = tween(600)) + 
                        slideInVertically(initialOffsetY = { 30 }, animationSpec = tween(600))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Mobile Shop ERP",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "🔒 Secure Access Required",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Animated Fingerprint Section
            AnimatedVisibility(
                visible = fingerprintVisible,
                enter = fadeIn(animationSpec = tween(600)) + 
                        scaleIn(initialScale = 0.3f, animationSpec = tween(600, easing = EaseOutBack))
            ) {
                when (biometricStatus) {
                    BiometricStatus.AVAILABLE -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                                Surface(
                                    modifier = Modifier.size(120.dp).scale(pulseScale).alpha(pulseAlpha * 0.3f),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondary
                                ) {}
                                Surface(
                                    modifier = Modifier.size(100.dp).scale(if (isAuthenticating) pulseScale * 0.95f else 1f),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = if (isAuthenticating) pulseAlpha else 1f
                                    ),
                                    shadowElevation = 8.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            modifier = Modifier.size(56.dp),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            AnimatedContent(
                                targetState = when {
                                    isAuthenticating -> "Scanning..."
                                    authMessage.isEmpty() -> "Touch sensor to unlock"
                                    else -> authMessage
                                },
                                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                                label = "statusText"
                            ) { text ->
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (authMessage.isNotEmpty() && !isAuthenticating) 
                                        MaterialTheme.colorScheme.error 
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    BiometricStatus.NOT_AVAILABLE -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = MaterialTheme.colorScheme.errorContainer) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Warning, null, Modifier.size(40.dp), MaterialTheme.colorScheme.error)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Fingerprint not available\non this device", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        }
                    }
                    BiometricStatus.NOT_ENROLLED -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PersonOff, null, Modifier.size(40.dp), MaterialTheme.colorScheme.tertiary)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No fingerprint enrolled\nPlease add in device settings", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        }
                    }
                    BiometricStatus.ERROR -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = MaterialTheme.colorScheme.errorContainer) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.ErrorOutline, null, Modifier.size(40.dp), MaterialTheme.colorScheme.error)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Biometric authentication error", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Animated Buttons
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(600))
            ) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnimatedVisibility(
                        visible = showRetryButton && biometricStatus == BiometricStatus.AVAILABLE,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Button(
                            onClick = {
                                showRetryButton = false
                                authMessage = ""
                                isAuthenticating = true
                                activity?.let {
                                    showBiometricPrompt(
                                        activity = it,
                                        onSuccess = onAuthSuccess,
                                        onError = { message ->
                                            isAuthenticating = false
                                            authMessage = message
                                            showRetryButton = true
                                            if (message.isNotEmpty()) {
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
                        ) {
                            Icon(Icons.Default.Fingerprint, null, Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Unlock with Fingerprint", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    OutlinedButton(
                        onClick = onFallbackToPin,
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Pin, null, Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Enter PIN Instead", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Bottom branding
        AnimatedVisibility(
            visible = buttonsVisible,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
            enter = fadeIn(animationSpec = tween(800, delayMillis = 400))
        ) {
            Text(
                text = "Developed by Waqar • +92 302 7761313",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                    errorCode != BiometricPrompt.ERROR_CANCELED) {
                    onError("Authentication Failed: $errString")
                } else {
                    onError("")
                }
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Fingerprint not recognized")
            }
        }
    )
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("🔐 Unlock Mobile Shop ERP")
        .setSubtitle("Use your fingerprint to access the app")
        .setNegativeButtonText("Use PIN")
        .build()
    biometricPrompt.authenticate(promptInfo)
}
