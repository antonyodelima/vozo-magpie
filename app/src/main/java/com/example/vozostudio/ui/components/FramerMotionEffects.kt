package com.example.vozostudio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vozostudio.theme.VozoAccentGreen
import com.example.vozostudio.theme.VozoPrimary
import com.example.vozostudio.theme.VozoSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Framer-Motion style spring physics specifications
 */
object MotionSpecs {
    val FramerSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val FramerSpringSnappy = spring<Float>(
        dampingRatio = 0.7f,
        stiffness = 400f
    )

    val FramerSpringGentle = spring<Float>(
        dampingRatio = 0.85f,
        stiffness = 250f
    )

    fun staggeredEntry(delayMillis: Int): EnterTransition {
        return fadeIn(
            animationSpec = tween(durationMillis = 350, delayMillis = delayMillis, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = 0.75f,
                stiffness = 350f
            ),
            initialOffsetY = { fullHeight -> fullHeight / 3 }
        ) + expandVertically(
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
        )
    }

    val gracefulExit: ExitTransition =
        fadeOut(animationSpec = tween(durationMillis = 200, easing = LinearEasing)) +
                slideOutVertically(
                    animationSpec = spring(dampingRatio = 0.9f, stiffness = 400f),
                    targetOffsetY = { fullHeight -> -fullHeight / 4 }
                ) + shrinkVertically(animationSpec = spring(dampingRatio = 0.8f, stiffness = 350f))
}

/**
 * Interactive Framer Motion style spring scale on click / tap
 */
fun Modifier.springPressEffect(
    pressedScale: Float = 0.94f,
    onClick: () -> Unit
): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                // Handled in composable scale state
            },
            onTap = {
                onClick()
            }
        )
    }
)

/**
 * Staggered Entrance Container for list items
 */
@Composable
fun StaggeredMotionItem(
    index: Int,
    baseDelay: Int = 40,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay((index * baseDelay).toLong().coerceAtMost(500L))
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = MotionSpecs.staggeredEntry(0),
        exit = MotionSpecs.gracefulExit,
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Animated Waveform Canvas with dynamic sine wave audio pulsations
 */
@Composable
fun AnimatedWaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 28,
    primaryColor: Color = VozoPrimary,
    secondaryColor: Color = VozoSecondary,
    height: Dp = 32.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) (2 * Math.PI).toFloat() else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val playbackScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.0f else 0.25f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "playbackScale"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val totalWidth = size.width
        val canvasHeight = size.height
        val barSpacing = totalWidth / barCount
        val barWidth = (barSpacing * 0.55f).coerceAtLeast(2.5f)

        for (i in 0 until barCount) {
            val normalizedX = i.toFloat() / barCount
            // Harmonic wave formula
            val wave1 = sin((normalizedX * 3.5 + phase).toDouble()).toFloat()
            val wave2 = sin((normalizedX * 6.0 - phase * 1.5).toDouble()).toFloat() * 0.4f
            val baseNoise = ((i % 5) * 0.12f)

            val rawHeightFactor = ((wave1 + wave2 + baseNoise).coerceIn(-1f, 1f) + 1.2f) / 2.2f
            val barHeight = ((rawHeightFactor * canvasHeight * 0.85f) * playbackScale + (canvasHeight * 0.15f))
                .coerceIn(4f, canvasHeight)

            val x = i * barSpacing + (barSpacing - barWidth) / 2f
            val y = (canvasHeight - barHeight) / 2f

            val color = if (i % 2 == 0) primaryColor else secondaryColor

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(color, color.copy(alpha = 0.45f)),
                    startY = y,
                    endY = y + barHeight
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

/**
 * Animated Pulse Ring for recording or active studio mode
 */
@Composable
fun StudioPulseRing(
    isRecording: Boolean,
    color: Color = VozoPrimary,
    modifier: Modifier = Modifier
) {
    if (!isRecording) return

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ring")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Canvas(modifier = modifier) {
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = (size.minDimension / 2) * scale
        )
    }
}
