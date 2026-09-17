package com.example.vozostudio.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VoiceRepository {
    private val _records = MutableStateFlow<List<VoiceRecordItem>>(initialRecords)
    val records: StateFlow<List<VoiceRecordItem>> = _records.asStateFlow()

    private val _stats = MutableStateFlow(
        StudioStats(
            totalProjects = initialRecords.size,
            completedCount = initialRecords.count { it.status == VoiceStatus.COMPLETED },
            processingCount = initialRecords.count { it.status == VoiceStatus.PROCESSING },
            totalAudioMinutes = 48,
            creditsRemaining = 840,
            creditsTotal = 1000,
            activeModelsCount = 4
        )
    )
    val stats: StateFlow<StudioStats> = _stats.asStateFlow()

    fun addRecord(
        title: String,
        category: VoiceCategory,
        voiceProfileName: String,
        transcript: String,
        durationSeconds: Int = 45
    ) {
        val newRecord = VoiceRecordItem(
            id = System.currentTimeMillis(),
            title = title,
            category = category,
            status = VoiceStatus.COMPLETED,
            voiceProfileName = voiceProfileName,
            modelEngine = "Vozo Neural Engine v4.2",
            durationSeconds = durationSeconds,
            fileSize = "${(durationSeconds * 0.08).toInt() + 1}.2 MB",
            transcript = transcript,
            clarityScore = (94..99).random(),
            stability = 0.88f,
            similarity = 0.94f,
            createdAtFormatted = "Just now",
            isFavorite = false
        )

        _records.update { listOf(newRecord) + it }
        updateStats()
    }

    fun deleteRecord(id: Long) {
        _records.update { current -> current.filterNot { it.id == id } }
        updateStats()
    }

    fun toggleFavorite(id: Long) {
        _records.update { current ->
            current.map {
                if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
            }
        }
    }

    private fun updateStats() {
        val currentList = _records.value
        _stats.update {
            it.copy(
                totalProjects = currentList.size,
                completedCount = currentList.count { r -> r.status == VoiceStatus.COMPLETED },
                processingCount = currentList.count { r -> r.status == VoiceStatus.PROCESSING },
                totalAudioMinutes = (currentList.sumOf { r -> r.durationSeconds } / 60) + 12
            )
        }
    }

    companion object {
        private val initialRecords = listOf(
            VoiceRecordItem(
                id = 101L,
                title = "Podcast Ep 12 - Intro & Teaser",
                category = VoiceCategory.VOICE_CLONE,
                status = VoiceStatus.COMPLETED,
                voiceProfileName = "Aura Studio Pro",
                modelEngine = "Vozo Neural v4.2",
                durationSeconds = 64,
                fileSize = "4.8 MB",
                transcript = "Welcome to another deep dive episode into next-generation generative media and interactive experiences.",
                clarityScore = 99,
                stability = 0.92f,
                similarity = 0.96f,
                createdAtFormatted = "10 min ago",
                isFavorite = true
            ),
            VoiceRecordItem(
                id = 102L,
                title = "Commercial Voiceover - Electric Velocity",
                category = VoiceCategory.TTS,
                status = VoiceStatus.COMPLETED,
                voiceProfileName = "Orion Deep Resonance",
                modelEngine = "Vozo UltraCinematic",
                durationSeconds = 38,
                fileSize = "3.1 MB",
                transcript = "Unleash uncharted horsepower. Built with raw acoustic precision and carbon fiber engineering.",
                clarityScore = 97,
                stability = 0.89f,
                similarity = 0.91f,
                createdAtFormatted = "1 hour ago",
                isFavorite = false
            ),
            VoiceRecordItem(
                id = 103L,
                title = "Audio Cleaning & Vocal Isolation",
                category = VoiceCategory.ENHANCE,
                status = VoiceStatus.COMPLETED,
                voiceProfileName = "Acoustic Denoise Filter",
                modelEngine = "Vozo CleanWave AI",
                durationSeconds = 92,
                fileSize = "7.4 MB",
                transcript = "Removed background fan hum, room echo, and clipped peaks while preserving vocal warmth.",
                clarityScore = 98,
                stability = 0.95f,
                similarity = 0.98f,
                createdAtFormatted = "3 hours ago",
                isFavorite = true
            ),
            VoiceRecordItem(
                id = 104L,
                title = "Spanish Narration Chapter 3",
                category = VoiceCategory.TTS,
                status = VoiceStatus.COMPLETED,
                voiceProfileName = "Elena Spark Dynamic",
                modelEngine = "Vozo Polyglot Neural",
                durationSeconds = 120,
                fileSize = "9.6 MB",
                transcript = "El viaje comenzó en las montañas nevadas, donde el silencio solo era interrumpido por el viento.",
                clarityScore = 96,
                stability = 0.87f,
                similarity = 0.93f,
                createdAtFormatted = "Yesterday",
                isFavorite = false
            ),
            VoiceRecordItem(
                id = 105L,
                title = "Live Interview Cleanup (Crowd Noise)",
                category = VoiceCategory.ISOLATION,
                status = VoiceStatus.PROCESSING,
                voiceProfileName = "Marcus Tech Conversational",
                modelEngine = "Vozo Spectral Split",
                durationSeconds = 45,
                fileSize = "3.8 MB",
                transcript = "Separating foreground speaker voice from conference room chatter and reverberation.",
                clarityScore = 92,
                stability = 0.84f,
                similarity = 0.88f,
                createdAtFormatted = "Just now",
                isFavorite = false
            )
        )
    }
}
