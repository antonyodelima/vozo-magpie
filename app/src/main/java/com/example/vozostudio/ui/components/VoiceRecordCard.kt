package com.example.vozostudio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozostudio.data.VoiceRecordItem
import com.example.vozostudio.data.VoiceStatus
import com.example.vozostudio.theme.VozoAccentAmber
import com.example.vozostudio.theme.VozoAccentGreen
import com.example.vozostudio.theme.VozoAccentRed
import com.example.vozostudio.theme.VozoBgDark
import com.example.vozostudio.theme.VozoBgSurface
import com.example.vozostudio.theme.VozoBgSurfaceElevated
import com.example.vozostudio.theme.VozoBorder
import com.example.vozostudio.theme.VozoBorderLight
import com.example.vozostudio.theme.VozoPrimary
import com.example.vozostudio.theme.VozoPrimaryLight
import com.example.vozostudio.theme.VozoSecondary
import com.example.vozostudio.theme.VozoTextMuted
import com.example.vozostudio.theme.VozoTextPrimary
import com.example.vozostudio.theme.VozoTextSecondary

@Composable
fun VoiceRecordCard(
    record: VoiceRecordItem,
    isPlaying: Boolean,
    isExpanded: Boolean,
    onTogglePlay: () -> Unit,
    onToggleExpand: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playButtonPressed by remember { mutableStateOf(false) }

    val playScale by animateFloatAsState(
        targetValue = if (playButtonPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "playScale"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = if (isPlaying) VozoPrimary else VozoBorder,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "cardBorderColor"
    )

    val cardElevationColor by animateColorAsState(
        targetValue = if (isPlaying) VozoBgSurfaceElevated else VozoBgSurface,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "cardElevation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardElevationColor)
            .border(1.2.dp, cardBorderColor, RoundedCornerShape(18.dp))
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = 0.82f,
                    stiffness = 350f
                )
            )
            .testTag("voice_record_card_${record.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Category tag, Status pill, Favorite, Expand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Status Badge
                    val (statusColor, statusBg, statusText) = when (record.status) {
                        VoiceStatus.COMPLETED -> Triple(VozoAccentGreen, VozoAccentGreen.copy(alpha = 0.15f), "Ready")
                        VoiceStatus.PROCESSING -> Triple(VozoAccentAmber, VozoAccentAmber.copy(alpha = 0.15f), "Rendering")
                        VoiceStatus.QUEUED -> Triple(VozoSecondary, VozoSecondary.copy(alpha = 0.15f), "Queued")
                        VoiceStatus.FAILED -> Triple(VozoAccentRed, VozoAccentRed.copy(alpha = 0.15f), "Failed")
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusBg)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }

                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VozoPrimary.copy(alpha = 0.12f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = record.category.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VozoPrimary
                        )
                    }

                    Text(
                        text = record.fileSize,
                        fontSize = 11.sp,
                        color = VozoTextMuted
                    )
                }

                // Actions: Favorite & Expand Chevron
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("fav_btn_${record.id}")
                    ) {
                        Icon(
                            imageVector = if (record.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (record.isFavorite) VozoPrimary else VozoTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("expand_btn_${record.id}")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = VozoTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Title & Voice Profile
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = record.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VozoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = record.voiceProfileName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VozoSecondary
                    )
                    Text(text = "•", fontSize = 10.sp, color = VozoTextMuted)
                    Text(
                        text = "${record.durationSeconds}s",
                        fontSize = 11.sp,
                        color = VozoTextMuted
                    )
                    Text(text = "•", fontSize = 10.sp, color = VozoTextMuted)
                    Text(
                        text = record.createdAtFormatted,
                        fontSize = 11.sp,
                        color = VozoTextMuted
                    )
                }
            }

            // Transcript Preview
            if (record.transcript.isNotEmpty()) {
                Text(
                    text = "\"${record.transcript}\"",
                    fontSize = 12.sp,
                    color = VozoTextSecondary,
                    maxLines = if (isExpanded) 6 else 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(VozoBgDark.copy(alpha = 0.6f))
                        .padding(8.dp)
                )
            }

            // Animated Waveform Display
            AnimatedWaveformVisualizer(
                isPlaying = isPlaying,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                barCount = 32,
                height = if (isExpanded) 40.dp else 24.dp
            )

            // Bottom Player Control Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spring Play Button
                Box(
                    modifier = Modifier
                        .scale(playScale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isPlaying) VozoPrimary else VozoBgSurfaceElevated
                        )
                        .border(
                            1.dp,
                            if (isPlaying) VozoPrimary else VozoBorderLight,
                            RoundedCornerShape(12.dp)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    playButtonPressed = true
                                    tryAwaitRelease()
                                    playButtonPressed = false
                                },
                                onTap = {
                                    onTogglePlay()
                                }
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("play_btn_${record.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = if (isPlaying) Color(0xFF130722) else VozoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isPlaying) "Playing" else "Preview",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPlaying) Color(0xFF130722) else VozoPrimary
                        )
                    }
                }

                // Clarity Badge & Delete Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VozoBgSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Clarity ${record.clarityScore}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VozoAccentGreen
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_btn_${record.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete record",
                            tint = VozoAccentRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expanded Details Section with Framer Motion Expand
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(spring(dampingRatio = 0.85f)) + expandVertically(spring(dampingRatio = 0.8f)),
                exit = fadeOut(tween(150)) + shrinkVertically(spring(dampingRatio = 0.8f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .border(1.dp, VozoBorder, RoundedCornerShape(12.dp))
                        .background(VozoBgDark)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Acoustic Modulation Parameters",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VozoTextPrimary
                    )

                    // Stability and Similarity Meters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Stability", fontSize = 11.sp, color = VozoTextSecondary)
                                Text("${(record.stability * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VozoPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(VozoBgSurfaceElevated)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(record.stability)
                                        .height(4.dp)
                                        .background(VozoPrimary)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Similarity", fontSize = 11.sp, color = VozoTextSecondary)
                                Text("${(record.similarity * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VozoSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(VozoBgSurfaceElevated)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(record.similarity)
                                        .height(4.dp)
                                        .background(VozoSecondary)
                                )
                            }
                        }
                    }

                    // Engine info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Engine: ${record.modelEngine}",
                            fontSize = 11.sp,
                            color = VozoTextMuted
                        )
                    }
                }
            }
        }
    }
}
