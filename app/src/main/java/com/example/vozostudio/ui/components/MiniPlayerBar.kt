package com.example.vozostudio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozostudio.data.VoiceRecordItem
import com.example.vozostudio.theme.VozoBgSurfaceElevated
import com.example.vozostudio.theme.VozoBorderLight
import com.example.vozostudio.theme.VozoPrimary
import com.example.vozostudio.theme.VozoSecondary
import com.example.vozostudio.theme.VozoTextMuted
import com.example.vozostudio.theme.VozoTextPrimary
import com.example.vozostudio.theme.VozoTextSecondary

@Composable
fun MiniPlayerBar(
    activeRecord: VoiceRecordItem?,
    isPlaying: Boolean,
    progress: Float,
    onTogglePlay: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = activeRecord != null,
        enter = fadeIn(spring(dampingRatio = 0.8f)) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            initialOffsetY = { fullHeight -> fullHeight }
        ),
        exit = fadeOut(spring(dampingRatio = 0.9f)) + slideOutVertically(
            animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f),
            targetOffsetY = { fullHeight -> fullHeight }
        ),
        modifier = modifier
    ) {
        if (activeRecord != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF20133A),
                                Color(0xFF140B26)
                            )
                        )
                    )
                    .border(1.2.dp, VozoPrimary.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("floating_mini_player")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title & Voice Name
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = activeRecord.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = VozoTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${activeRecord.voiceProfileName} • ${activeRecord.durationSeconds}s",
                                fontSize = 11.sp,
                                color = VozoSecondary,
                                maxLines = 1
                            )
                        }

                        // Mini Waveform
                        AnimatedWaveformVisualizer(
                            isPlaying = isPlaying,
                            modifier = Modifier
                                .weight(0.7f)
                                .padding(horizontal = 8.dp),
                            barCount = 18,
                            height = 22.dp
                        )

                        // Play/Pause Action
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(VozoPrimary)
                                .clickable { onTogglePlay() }
                                .testTag("mini_player_play_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF130722),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Close button
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("mini_player_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close player",
                                tint = VozoTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Progress Track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(VozoBgSurfaceElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (isPlaying) 0.65f else 0.25f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(VozoPrimary, VozoSecondary)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
