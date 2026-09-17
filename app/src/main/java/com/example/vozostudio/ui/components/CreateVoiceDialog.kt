package com.example.vozostudio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozostudio.data.SampleVoiceProfiles
import com.example.vozostudio.data.VoiceCategory
import com.example.vozostudio.data.VoiceProfile
import com.example.vozostudio.theme.VozoAccentGreen
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
fun CreateVoiceDialog(
    isOpen: Boolean,
    isSynthesizing: Boolean,
    synthesisProgress: Float,
    selectedProfile: VoiceProfile,
    onSelectProfile: (VoiceProfile) -> Unit,
    onDismiss: () -> Unit,
    onSynthesize: (title: String, category: VoiceCategory, profile: VoiceProfile, script: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var script by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(VoiceCategory.VOICE_CLONE) }
    var stabilityValue by remember { mutableFloatStateOf(0.85f) }
    var similarityValue by remember { mutableFloatStateOf(0.92f) }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(spring(dampingRatio = 0.8f)),
        exit = fadeOut(spring(dampingRatio = 0.8f))
    ) {
        // Semi-transparent Backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable { if (!isSynthesizing) onDismiss() }
                .imePadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Dialog Sheet Card with Spring Slide Up & Scale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(VozoBgSurface)
                    .border(1.dp, VozoBorderLight, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp)
                    .testTag("create_voice_modal")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VozoPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Synthesize",
                                    tint = VozoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Neural Voice Synthesis",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VozoTextPrimary
                                )
                                Text(
                                    text = "Vozo High-Fidelity Studio Studio Engine",
                                    fontSize = 11.sp,
                                    color = VozoTextMuted
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            enabled = !isSynthesizing,
                            modifier = Modifier.testTag("modal_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = VozoTextMuted
                            )
                        }
                    }

                    // Project Title Input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Project Title",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VozoTextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(VozoBgDark)
                                .border(1.dp, VozoBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            if (title.isEmpty()) {
                                Text(
                                    text = "e.g., Cyberpunk Teaser Voiceover",
                                    fontSize = 13.sp,
                                    color = VozoTextMuted
                                )
                            }
                            BasicTextField(
                                value = title,
                                onValueChange = { title = it },
                                textStyle = TextStyle(
                                    color = VozoTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(VozoPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("modal_title_input"),
                                singleLine = true
                            )
                        }
                    }

                    // Select Category Pills
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Synthesis Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VozoTextSecondary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                VoiceCategory.VOICE_CLONE,
                                VoiceCategory.TTS,
                                VoiceCategory.ENHANCE,
                                VoiceCategory.ISOLATION
                            ).forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) VozoPrimary else VozoBgDark)
                                        .border(1.dp, if (isSelected) VozoPrimary else VozoBorder, RoundedCornerShape(14.dp))
                                        .clickable { selectedCategory = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF130722) else VozoTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Voice Profile Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Target Voice Model",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VozoTextSecondary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SampleVoiceProfiles.forEach { profile ->
                                val isSelected = selectedProfile.id == profile.id
                                Box(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) VozoBgSurfaceElevated else VozoBgDark)
                                        .border(
                                            1.2.dp,
                                            if (isSelected) VozoPrimary else VozoBorder,
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onSelectProfile(profile) }
                                        .padding(10.dp)
                                        .testTag("voice_profile_${profile.id}")
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = profile.tag,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(profile.accentColor)
                                            )
                                            Text(
                                                text = profile.language,
                                                fontSize = 9.sp,
                                                color = VozoTextMuted
                                            )
                                        }

                                        Text(
                                            text = profile.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VozoTextPrimary,
                                            maxLines = 1
                                        )

                                        Text(
                                            text = profile.description,
                                            fontSize = 10.sp,
                                            color = VozoTextMuted,
                                            maxLines = 2,
                                            lineHeight = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Script / Text Prompt
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Script & Acoustic Prompt",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VozoTextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(VozoBgDark)
                                .border(1.dp, VozoBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            if (script.isEmpty()) {
                                Text(
                                    text = "Enter script lines, monologue or voice cloning script...",
                                    fontSize = 13.sp,
                                    color = VozoTextMuted
                                )
                            }
                            BasicTextField(
                                value = script,
                                onValueChange = { script = it },
                                textStyle = TextStyle(
                                    color = VozoTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 18.sp
                                ),
                                cursorBrush = SolidColor(VozoPrimary),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("modal_script_input")
                            )
                        }
                    }

                    // Acoustic Modulation Sliders
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Acoustic Stability", fontSize = 11.sp, color = VozoTextSecondary)
                            Text("${(stabilityValue * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VozoPrimary)
                        }
                        Slider(
                            value = stabilityValue,
                            onValueChange = { stabilityValue = it },
                            valueRange = 0.5f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = VozoPrimary,
                                activeTrackColor = VozoPrimary,
                                inactiveTrackColor = VozoBgDark
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }

                    // Render / Synthesize Button with Live Spring Progress
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(VozoPrimary, Color(0xFF9333EA))
                                )
                            )
                            .clickable(enabled = !isSynthesizing) {
                                onSynthesize(
                                    title.ifBlank { "${selectedProfile.name} Session" },
                                    selectedCategory,
                                    selectedProfile,
                                    script
                                )
                            }
                            .padding(vertical = 14.dp)
                            .testTag("modal_synthesize_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSynthesizing) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { synthesisProgress },
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFF130722),
                                    strokeWidth = 2.5.dp
                                )
                                Text(
                                    text = "Synthesizing Neural Audio (${(synthesisProgress * 100).toInt()}%)...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF130722)
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = Color(0xFF130722),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Generate Voice Track",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF130722)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
