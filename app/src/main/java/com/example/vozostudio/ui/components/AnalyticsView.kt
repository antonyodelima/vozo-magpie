package com.example.vozostudio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozostudio.data.StudioStats
import com.example.vozostudio.theme.VozoAccentAmber
import com.example.vozostudio.theme.VozoAccentGreen
import com.example.vozostudio.theme.VozoBgDark
import com.example.vozostudio.theme.VozoBgSurface
import com.example.vozostudio.theme.VozoBgSurfaceElevated
import com.example.vozostudio.theme.VozoBorder
import com.example.vozostudio.theme.VozoPrimary
import com.example.vozostudio.theme.VozoSecondary
import com.example.vozostudio.theme.VozoTextMuted
import com.example.vozostudio.theme.VozoTextPrimary
import com.example.vozostudio.theme.VozoTextSecondary

@Composable
fun AnalyticsView(
    stats: StudioStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StaggeredMotionItem(index = 0, baseDelay = 50) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VozoBgSurface)
                    .border(1.dp, VozoBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Acoustic Quality Metrics",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = VozoTextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = VozoAccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Mean MOS Score", fontSize = 11.sp, color = VozoTextMuted)
                            Text("4.85 / 5.0", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = VozoAccentGreen)
                        }
                        Column {
                            Text("Latency", fontSize = 11.sp, color = VozoTextMuted)
                            Text("180ms", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = VozoSecondary)
                        }
                        Column {
                            Text("Render Resolution", fontSize = 11.sp, color = VozoTextMuted)
                            Text("48kHz 24-bit", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = VozoPrimary)
                        }
                    }
                }
            }
        }

        StaggeredMotionItem(index = 1, baseDelay = 60) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VozoBgSurface)
                    .border(1.dp, VozoBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Compute & Quota Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VozoTextPrimary
                    )

                    listOf(
                        Triple("Voice Cloning", "420 credits", 0.5f),
                        Triple("Text-to-Speech Synthesis", "280 credits", 0.35f),
                        Triple("Vocal Isolation & Denoise", "140 credits", 0.15f)
                    ).forEach { (label, creditStr, ratio) ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, fontSize = 12.sp, color = VozoTextSecondary)
                                Text(creditStr, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VozoPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(VozoBgSurfaceElevated)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio)
                                        .height(5.dp)
                                        .background(VozoPrimary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
