package com.example.vozostudio.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
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
import com.example.vozostudio.ui.components.AnalyticsView
import com.example.vozostudio.ui.components.CategoryFilterRow
import com.example.vozostudio.ui.components.CreateVoiceDialog
import com.example.vozostudio.ui.components.MetricsRow
import com.example.vozostudio.ui.components.MiniPlayerBar
import com.example.vozostudio.ui.components.MotionSpecs
import com.example.vozostudio.ui.components.StaggeredMotionItem
import com.example.vozostudio.ui.components.StudioHeader
import com.example.vozostudio.ui.components.VoiceLabView
import com.example.vozostudio.ui.components.VoiceRecordCard

@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: VozoStudioViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activeRecord = state.records.find { it.id == state.playingRecordId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VozoBgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            StudioHeader(
                remainingCredits = state.stats.creditsRemaining,
                totalCredits = state.stats.creditsTotal,
                onCreateClick = { viewModel.setCreateModalVisible(true) }
            )

            // Studio Navigation Segment Tabs with Framer Motion Spring
            StudioTabSelector(
                activeTab = state.activeTab,
                onTabSelect = { viewModel.selectTab(it) }
            )

            // Animated Tab Content Container
            AnimatedContent(
                targetState = state.activeTab,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally(
                                animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f)
                            ) { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally(
                                animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f)
                            ) { width -> width } + fadeOut()
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "tabContent"
            ) { targetTab ->
                when (targetTab) {
                    StudioTab.RECORDS -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Metrics Row
                            item {
                                MetricsRow(stats = state.stats)
                            }

                            // Filter & Search Controls
                            item {
                                CategoryFilterRow(
                                    selectedCategory = state.selectedCategory,
                                    onCategorySelect = { viewModel.selectCategory(it) },
                                    searchQuery = state.searchQuery,
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) }
                                )
                            }

                            // Header Label for Record List
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Voice Synthesis Library",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VozoTextPrimary
                                    )
                                    Text(
                                        text = "${state.records.size} tracks",
                                        fontSize = 12.sp,
                                        color = VozoTextMuted
                                    )
                                }
                            }

                            // Empty State
                            if (state.records.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(VozoBgSurface)
                                            .border(1.dp, VozoBorder, RoundedCornerShape(18.dp))
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MicOff,
                                                contentDescription = null,
                                                tint = VozoBorderLight,
                                                modifier = Modifier.size(44.dp)
                                            )
                                            Text(
                                                text = "No recordings match your filter",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VozoTextPrimary
                                            )
                                            Text(
                                                text = "Try searching a different keyword or synthesize a new voice track.",
                                                fontSize = 12.sp,
                                                color = VozoTextMuted
                                            )
                                        }
                                    }
                                }
                            } else {
                                itemsIndexed(
                                    items = state.records,
                                    key = { _, item -> item.id }
                                ) { index, record ->
                                    StaggeredMotionItem(
                                        index = index,
                                        baseDelay = 40
                                    ) {
                                        VoiceRecordCard(
                                            record = record,
                                            isPlaying = state.playingRecordId == record.id && state.isPlaybackActive,
                                            isExpanded = state.expandedRecordId == record.id,
                                            onTogglePlay = { viewModel.togglePlayback(record.id) },
                                            onToggleExpand = { viewModel.toggleCardExpansion(record.id) },
                                            onToggleFavorite = { viewModel.toggleFavorite(record.id) },
                                            onDelete = { viewModel.deleteRecord(record.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    StudioTab.VOICE_LAB -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                VoiceLabView(
                                    profiles = state.availableProfiles,
                                    selectedProfile = state.selectedProfile,
                                    onSelectProfile = { viewModel.selectVoiceProfile(it) },
                                    onNewCloneClick = { viewModel.setCreateModalVisible(true) }
                                )
                            }
                        }
                    }

                    StudioTab.ANALYTICS -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                AnalyticsView(stats = state.stats)
                            }
                        }
                    }
                }
            }
        }

        // Floating Bottom Docked Mini Player
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            MiniPlayerBar(
                activeRecord = activeRecord,
                isPlaying = state.isPlaybackActive,
                progress = state.playbackProgress,
                onTogglePlay = {
                    if (activeRecord != null) viewModel.togglePlayback(activeRecord.id)
                },
                onClose = { viewModel.stopPlayback() }
            )
        }

        // Synthesis Creation Dialog / Modal
        CreateVoiceDialog(
            isOpen = state.isCreateModalOpen,
            isSynthesizing = state.isSynthesizing,
            synthesisProgress = state.synthesisProgress,
            selectedProfile = state.selectedProfile,
            onSelectProfile = { viewModel.selectVoiceProfile(it) },
            onDismiss = { viewModel.setCreateModalVisible(false) },
            onSynthesize = { title, category, profile, script ->
                viewModel.generateVoiceSynthesis(title, category, profile, script)
            }
        )

        // Animated Toast Banner
        AnimatedVisibility(
            visible = state.toastMessage != null,
            enter = fadeIn(spring(dampingRatio = 0.7f)) + slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                initialOffsetY = { -it }
            ),
            exit = fadeOut(tween(200)) + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 70.dp)
        ) {
            state.toastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(VozoBgSurfaceElevated)
                        .border(1.dp, VozoPrimary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = msg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VozoPrimaryLight
                    )
                }
            }
        }
    }
}

@Composable
private fun StudioTabSelector(
    activeTab: StudioTab,
    onTabSelect: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(VozoBgSurface)
            .border(1.dp, VozoBorder, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StudioTab.values().forEach { tab ->
            val isSelected = activeTab == tab

            val tabBg by animateColorAsState(
                targetValue = if (isSelected) VozoPrimary else Color.Transparent,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                label = "tabBg"
            )

            val tabTextColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF130722) else VozoTextSecondary,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                label = "tabTextColor"
            )

            val icon = when (tab) {
                StudioTab.RECORDS -> Icons.Default.GraphicEq
                StudioTab.VOICE_LAB -> Icons.Default.Psychology
                StudioTab.ANALYTICS -> Icons.Default.Analytics
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tabBg)
                    .clickable { onTabSelect(tab) }
                    .padding(vertical = 8.dp)
                    .testTag("tab_${tab.name}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label,
                        tint = tabTextColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = tab.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = tabTextColor
                    )
                }
            }
        }
    }
}
