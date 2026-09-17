package com.example.vozostudio.data

enum class VoiceStatus {
    COMPLETED,
    PROCESSING,
    QUEUED,
    FAILED
}

enum class VoiceCategory(val displayName: String) {
    ALL("All"),
    VOICE_CLONE("Voice Clone"),
    TTS("Text-to-Speech"),
    ENHANCE("Enhancement"),
    ISOLATION("Vocal Isolation")
}

data class VoiceProfile(
    val id: String,
    val name: String,
    val description: String,
    val language: String,
    val gender: String,
    val tag: String,
    val accentColor: Long
)

data class VoiceRecordItem(
    val id: Long,
    val title: String,
    val category: VoiceCategory,
    val status: VoiceStatus,
    val voiceProfileName: String,
    val modelEngine: String,
    val durationSeconds: Int,
    val fileSize: String,
    val transcript: String,
    val clarityScore: Int = 98,
    val stability: Float = 0.85f,
    val similarity: Float = 0.92f,
    val createdAtFormatted: String,
    val isFavorite: Boolean = false
)

data class StudioStats(
    val totalProjects: Int,
    val completedCount: Int,
    val processingCount: Int,
    val totalAudioMinutes: Int,
    val creditsRemaining: Int,
    val creditsTotal: Int,
    val activeModelsCount: Int
)

val SampleVoiceProfiles = listOf(
    VoiceProfile(
        id = "aura-pro",
        name = "Aura Studio Pro",
        description = "Warm, natural narration with subtle breath dynamic",
        language = "EN (US)",
        gender = "Female",
        tag = "Studio Grade",
        accentColor = 0xFFBC5CFF
    ),
    VoiceProfile(
        id = "orion-deep",
        name = "Orion Deep Resonance",
        description = "Deep cinematic baritone suited for documentaries & trailers",
        language = "EN (UK)",
        gender = "Male",
        tag = "Cinematic",
        accentColor = 0xFF00E5FF
    ),
    VoiceProfile(
        id = "elena-spark",
        name = "Elena Spark Dynamic",
        description = "High-energy commercial voice with bright clarity",
        language = "ES / EN",
        gender = "Female",
        tag = "Commercial",
        accentColor = 0xFFFF2A85
    ),
    VoiceProfile(
        id = "marcus-neutral",
        name = "Marcus Tech Conversational",
        description = "Modern podcast and technical presentation cadence",
        language = "EN (US)",
        gender = "Male",
        tag = "Podcast",
        accentColor = 0xFF10B981
    )
)
