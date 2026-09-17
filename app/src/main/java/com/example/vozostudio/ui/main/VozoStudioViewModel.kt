package com.example.vozostudio.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vozostudio.data.SampleVoiceProfiles
import com.example.vozostudio.data.StudioStats
import com.example.vozostudio.data.VoiceCategory
import com.example.vozostudio.data.VoiceProfile
import com.example.vozostudio.data.VoiceRecordItem
import com.example.vozostudio.data.VoiceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VozoStudioUiState(
    val records: List<VoiceRecordItem> = emptyList(),
    val stats: StudioStats = StudioStats(0, 0, 0, 0, 840, 1000, 4),
    val selectedCategory: VoiceCategory = VoiceCategory.ALL,
    val searchQuery: String = "",
    val playingRecordId: Long? = null,
    val isPlaybackActive: Boolean = false,
    val playbackProgress: Float = 0f,
    val expandedRecordId: Long? = null,
    val isCreateModalOpen: Boolean = false,
    val isSynthesizing: Boolean = false,
    val synthesisProgress: Float = 0f,
    val availableProfiles: List<VoiceProfile> = SampleVoiceProfiles,
    val selectedProfile: VoiceProfile = SampleVoiceProfiles.first(),
    val activeTab: StudioTab = StudioTab.RECORDS,
    val toastMessage: String? = null
)

enum class StudioTab(val label: String) {
    RECORDS("Creations"),
    VOICE_LAB("Voice Models"),
    ANALYTICS("Analytics")
}

class VozoStudioViewModel(
    private val repository: VoiceRepository = VoiceRepository()
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(VoiceCategory.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _playingRecordId = MutableStateFlow<Long?>(null)
    private val _isPlaybackActive = MutableStateFlow(false)
    private val _playbackProgress = MutableStateFlow(0f)
    private val _expandedRecordId = MutableStateFlow<Long?>(null)
    private val _isCreateModalOpen = MutableStateFlow(false)
    private val _isSynthesizing = MutableStateFlow(false)
    private val _synthesisProgress = MutableStateFlow(0f)
    private val _selectedProfile = MutableStateFlow(SampleVoiceProfiles.first())
    private val _activeTab = MutableStateFlow(StudioTab.RECORDS)
    private val _toastMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<VozoStudioUiState> = combine(
        repository.records,
        repository.stats,
        _selectedCategory,
        _searchQuery,
        _playingRecordId,
        _isPlaybackActive,
        _playbackProgress,
        _expandedRecordId,
        _isCreateModalOpen,
        _isSynthesizing,
        _synthesisProgress,
        _selectedProfile,
        _activeTab,
        _toastMessage
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val records = params[0] as List<VoiceRecordItem>
        val stats = params[1] as StudioStats
        val category = params[2] as VoiceCategory
        val query = params[3] as String
        val playingId = params[4] as Long?
        val isPlaying = params[5] as Boolean
        val playProgress = params[6] as Float
        val expandedId = params[7] as Long?
        val isCreateOpen = params[8] as Boolean
        val isSynth = params[9] as Boolean
        val synthProgress = params[10] as Float
        val profile = params[11] as VoiceProfile
        val tab = params[12] as StudioTab
        val toast = params[13] as String?

        val filtered = records.filter { item ->
            val matchCategory = (category == VoiceCategory.ALL || item.category == category)
            val matchSearch = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.transcript.contains(query, ignoreCase = true) ||
                    item.voiceProfileName.contains(query, ignoreCase = true)
            matchCategory && matchSearch
        }

        VozoStudioUiState(
            records = filtered,
            stats = stats,
            selectedCategory = category,
            searchQuery = query,
            playingRecordId = playingId,
            isPlaybackActive = isPlaying,
            playbackProgress = playProgress,
            expandedRecordId = expandedId,
            isCreateModalOpen = isCreateOpen,
            isSynthesizing = isSynth,
            synthesisProgress = synthProgress,
            selectedProfile = profile,
            activeTab = tab,
            toastMessage = toast
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = VozoStudioUiState(
            records = repository.records.value,
            stats = repository.stats.value
        )
    )

    fun selectCategory(category: VoiceCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectTab(tab: StudioTab) {
        _activeTab.value = tab
    }

    fun selectVoiceProfile(profile: VoiceProfile) {
        _selectedProfile.value = profile
    }

    fun setCreateModalVisible(visible: Boolean) {
        _isCreateModalOpen.value = visible
    }

    fun toggleCardExpansion(id: Long) {
        _expandedRecordId.update { current -> if (current == id) null else id }
    }

    fun togglePlayback(id: Long) {
        if (_playingRecordId.value == id) {
            _isPlaybackActive.value = !_isPlaybackActive.value
        } else {
            _playingRecordId.value = id
            _isPlaybackActive.value = true
            _playbackProgress.value = 0.25f
        }
    }

    fun stopPlayback() {
        _playingRecordId.value = null
        _isPlaybackActive.value = false
        _playbackProgress.value = 0f
    }

    fun toggleFavorite(id: Long) {
        repository.toggleFavorite(id)
    }

    fun deleteRecord(id: Long) {
        if (_playingRecordId.value == id) {
            stopPlayback()
        }
        repository.deleteRecord(id)
        showToast("Recording removed from library")
    }

    fun generateVoiceSynthesis(
        title: String,
        category: VoiceCategory,
        profile: VoiceProfile,
        script: String
    ) {
        viewModelScope.launch {
            _isSynthesizing.value = true
            _synthesisProgress.value = 0.1f

            // Smooth spring loading steps
            delay(350)
            _synthesisProgress.value = 0.45f
            delay(400)
            _synthesisProgress.value = 0.82f
            delay(350)
            _synthesisProgress.value = 1.0f
            delay(200)

            repository.addRecord(
                title = title.ifBlank { "Untitled Voice Synthesis" },
                category = category,
                voiceProfileName = profile.name,
                transcript = script.ifBlank { "Generated speech output using ${profile.name} neural architecture." },
                durationSeconds = (30..80).random()
            )

            _isSynthesizing.value = false
            _synthesisProgress.value = 0f
            _isCreateModalOpen.value = false
            showToast("✨ Voice rendered successfully!")
        }
    }

    private fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.value = msg
            delay(2500)
            _toastMessage.value = null
        }
    }
}
