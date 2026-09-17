package com.example.vozostudio.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozostudio.data.StudioStats
import com.example.vozostudio.theme.VozoAccentAmber
import com.example.vozostudio.theme.VozoAccentGreen
import com.example.vozostudio.theme.VozoBgSurface
import com.example.vozostudio.theme.VozoBgSurfaceElevated
import com.example.vozostudio.theme.VozoBorder
import com.example.vozostudio.theme.VozoBorderLight
import com.example.vozostudio.theme.VozoPrimary
import com.example.vozostudio.theme.VozoSecondary
import com.example.vozostudio.theme.VozoTextMuted
import com.example.vozostudio.theme.VozoTextPrimary
import com.example.vozostudio.theme.VozoTextSecondary

@Composable
fun MetricsRow(
    stats: StudioStats,
    modifier: Modifier = Modifier
) {
    var hasAnimated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasAnimated = true
    }

    val creditProgress by animateFloatAsState(
        targetValue = if (hasAnimated) stats.creditsRemaining.toFloat() / stats.creditsTotal.toFloat() else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "creditProgress"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Quick Stat Tiles with Staggered Entrance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StaggeredMotionItem(
                index = 0,
                baseDelay = 60,
                modifier = Modifier.weight(1f)
            ) {
                MetricCard(
                    title = "Projects",
                    value = "${stats.totalProjects}",
                    subtitle = "${stats.completedCount} ready • ${stats.processingCount} rendering",
                    icon = Icons.Default.GraphicEq,
                    accentColor = VozoPrimary,
                    testTag = "metric_total_projects"
                )
            }

            StaggeredMotionItem(
                index = 1,
                baseDelay = 60,
                modifier = Modifier.weight(1f)
            ) {
                MetricCard(
                    title = "Audio Synthesized",
                    value = "${stats.totalAudioMinutes}m",
                    subtitle = "HD 48kHz Master",
                    icon = Icons.Default.Mic,
                    accentColor = VozoAccentGreen,
                    testTag = "metric_audio_minutes"
                )
            }
        }

        // Voice Credits Banner with Progress Gauge
        StaggeredMotionItem(
            index = 2,
            baseDelay = 70,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VozoBgSurface)
                    .border(1.dp, VozoBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("credits_overview_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(VozoAccentAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Credits",
                                    tint = VozoAccentAmber,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = "Neural Synthesis Quota",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VozoTextPrimary
                            )
                        }

                        Text(
                            text = "${stats.creditsRemaining} / ${stats.creditsTotal} credits",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VozoPrimary
                        )
                    }

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(VozoBgSurfaceElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(creditProgress)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(VozoPrimary, VozoSecondary)
                                    )
                                )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${stats.activeModelsCount} cloned voice models active",
                            fontSize = 11.sp,
                            color = VozoTextMuted
                        )
                        Text(
                            text = "Auto-refills in 12d",
                            fontSize = 11.sp,
                            color = VozoTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VozoBgSurface)
            .border(1.dp, VozoBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VozoTextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = VozoTextPrimary
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = VozoTextMuted,
                maxLines = 1
            )
        }
    }
}
