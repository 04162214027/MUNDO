package com.mobileshop.erp.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobileshop.erp.ui.theme.Blue40
import com.mobileshop.erp.ui.theme.Teal40

@Composable
fun PinAuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: PinAuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthSuccess()
        }
    }

    // Shake animation for error
    val shakeOffset by animateFloatAsState(
        targetValue = if (uiState.isError) 10f else 0f,
        animationSpec = if (uiState.isError) {
            spring(dampingRatio = 0.3f, stiffness = 500f)
        } else {
            tween(0)
        },
        label = "shake"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Blue40.copy(alpha = 0.1f),
                        Teal40.copy(alpha = 0.05f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shop Icon & Name
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = viewModel.shopName.ifEmpty { "Mobile Shop ERP" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter Your PIN",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            // PIN Dots
            Row(
                modifier = Modifier.offset(x = shakeOffset.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                repeat(4) { index ->
                    PinDot(
                        isFilled = index < uiState.pin.length,
                        isError = uiState.isError
                    )
                }
            }

            // Error Message
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Number Pad
            NumberPad(
                onDigitClick = viewModel::appendDigit,
                onDeleteClick = viewModel::deleteDigit,
                onClearClick = viewModel::clearPin
            )
        }
    }
}

@Composable
private fun PinDot(
    isFilled: Boolean,
    isError: Boolean
) {
    val color = when {
        isError -> MaterialTheme.colorScheme.error
        isFilled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val scale by animateFloatAsState(
        targetValue = if (isFilled) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size((20 * scale).dp)
            .clip(CircleShape)
            .background(if (isFilled) color else MaterialTheme.colorScheme.surface)
            .border(2.dp, color, CircleShape)
    )
}

@Composable
private fun NumberPad(
    onDigitClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val numbers = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9'),
        listOf('C', '0', '⌫')
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        numbers.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                row.forEach { char ->
                    NumberButton(
                        char = char,
                        onClick = {
                            when (char) {
                                'C' -> onClearClick()
                                '⌫' -> onDeleteClick()
                                else -> onDigitClick(char)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NumberButton(
    char: Char,
    onClick: () -> Unit
) {
    val isSpecial = char == 'C' || char == '⌫'

    Surface(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        shape = CircleShape,
        color = if (isSpecial) 
            MaterialTheme.colorScheme.surfaceVariant 
        else 
            MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSpecial) 0.dp else 2.dp,
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (char) {
                '⌫' -> Icon(
                    Icons.Default.Backspace,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                'C' -> Text(
                    text = "C",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> Text(
                    text = char.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
