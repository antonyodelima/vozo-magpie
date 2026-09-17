package com.example.vozostudio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozostudio.data.VoiceCategory
import com.example.vozostudio.theme.VozoBgDark
import com.example.vozostudio.theme.VozoBgSurface
import com.example.vozostudio.theme.VozoBgSurfaceElevated
import com.example.vozostudio.theme.VozoBorder
import com.example.vozostudio.theme.VozoBorderLight
import com.example.vozostudio.theme.VozoPrimary
import com.example.vozostudio.theme.VozoTextMuted
import com.example.vozostudio.theme.VozoTextPrimary
import com.example.vozostudio.theme.VozoTextSecondary

@Composable
fun CategoryFilterRow(
    selectedCategory: VoiceCategory,
    onCategorySelect: (VoiceCategory) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VozoBgSurface)
                .border(1.dp, VozoBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .testTag("search_bar_container")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = VozoTextMuted,
                    modifier = Modifier.size(18.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search voice tracks, transcripts, profiles...",
                            fontSize = 13.sp,
                            color = VozoTextMuted
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        textStyle = TextStyle(
                            color = VozoTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(VozoPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_input"),
                        singleLine = true
                    )
                }

                AnimatedVisibility(
                    visible = searchQuery.isNotEmpty(),
                    enter = fadeIn() + scaleIn(spring(dampingRatio = 0.7f)),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(VozoBgSurfaceElevated)
                            .clickable { onSearchQueryChange("") }
                            .testTag("search_clear_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = VozoTextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // Category Pills with Framer Motion spring indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VoiceCategory.values().forEach { category ->
                val isSelected = selectedCategory == category

                val pillBackground by animateColorAsState(
                    targetValue = if (isSelected) VozoPrimary else VozoBgSurface,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                    label = "pillBg"
                )

                val pillBorder by animateColorAsState(
                    targetValue = if (isSelected) VozoPrimary else VozoBorder,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                    label = "pillBorder"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF130722) else VozoTextSecondary,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                    label = "pillTextColor"
                )

                val pillScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.04f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
                    label = "pillScale"
                )

                Box(
                    modifier = Modifier
                        .scale(pillScale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(pillBackground)
                        .border(1.dp, pillBorder, RoundedCornerShape(20.dp))
                        .clickable { onCategorySelect(category) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("category_chip_${category.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}
