package com.example.vozostudio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun StudioHeader(
    remainingCredits: Int,
    totalCredits: Int,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHeaderVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isHeaderVisible = true
    }

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "createButtonScale"
    )

    AnimatedVisibility(
        visible = isHeaderVisible,
        enter = fadeIn(spring(dampingRatio = 0.8f)) + slideInVertically(
            spring(dampingRatio = 0.75f, stiffness = 400f)
        ) { -it / 2 },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(VozoBgDark)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Studio Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(VozoPrimary, Color(0xFF6B21A8))
                                )
                            )
                            .border(1.dp, VozoPrimaryLight.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Vozo Audio Studio Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Vozo Studio",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = VozoTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(VozoPrimary.copy(alpha = 0.2f))
                                    .border(0.8.dp, VozoPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "PRO",
                                        tint = VozoPrimary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "PRO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VozoPrimary
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Neural Voice Synthesis v4.2",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = VozoTextMuted
                        )
                    }
                }

                // Framer-Motion Style Spring Create Button
                Box(
                    modifier = Modifier
                        .scale(buttonScale)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(VozoPrimary, Color(0xFF9D34F0))
                            )
                        )
                        .border(1.dp, VozoPrimaryLight.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    tryAwaitRelease()
                                    isPressed = false
                                },
                                onTap = {
                                    onCreateClick()
                                }
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                        .testTag("create_voice_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Record",
                            tint = Color(0xFF130722),
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = "Synthesize",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF130722)
                        )
                    }
                }
            }
        }
    }
}
