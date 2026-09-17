package com.example.vozostudio.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SpatialAudioOff
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozostudio.data.VoiceProfile
import com.example.vozostudio.theme.VozoAccentGreen
import com.example.vozostudio.theme.VozoBgDark
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
fun VoiceLabView(
    profiles: List<VoiceProfile>,
    selectedProfile: VoiceProfile,
    onSelectProfile: (VoiceProfile) -> Unit,
    onNewCloneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
        StaggeredMotionItem(index = 0, baseDelay = 50) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF281347), Color(0xFF160A29))
                        )
                    )
                    .border(1.dp, VozoPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Zero-Shot Voice Cloning",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = VozoTextPrimary
                        )
                        Text(
                            text = "Train custom acoustic models with 10 seconds of clear speaker reference audio.",
                            fontSize = 12.sp,
                            color = VozoTextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(VozoPrimary)
                            .clickable { onNewCloneClick() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("clone_voice_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF130722),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Clone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF130722)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Active Neural Voice Library",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = VozoTextPrimary
        )

        profiles.forEachIndexed { idx, profile ->
            val isSelected = selectedProfile.id == profile.id
            StaggeredMotionItem(index = idx + 1, baseDelay = 60) {
                VoiceProfileCard(
                    profile = profile,
                    isSelected = isSelected,
                    onSelect = { onSelectProfile(profile) }
                )
            }
        }
    }
}

@Composable
private fun VoiceProfileCard(
    profile: VoiceProfile,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "profileCardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) VozoBgSurfaceElevated else VozoBgSurface)
            .border(
                1.2.dp,
                if (isSelected) VozoPrimary else VozoBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .padding(14.dp)
            .testTag("profile_card_${profile.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(profile.accentColor).copy(alpha = 0.2f))
                        .border(1.dp, Color(profile.accentColor).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = profile.name,
                        tint = Color(profile.accentColor),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = profile.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VozoTextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(profile.accentColor).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = profile.tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(profile.accentColor)
                            )
                        }
                    }

                    Text(
                        text = "${profile.gender} • ${profile.language} • ${profile.description}",
                        fontSize = 11.sp,
                        color = VozoTextMuted,
                        maxLines = 1
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(VozoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color(0xFF130722),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
