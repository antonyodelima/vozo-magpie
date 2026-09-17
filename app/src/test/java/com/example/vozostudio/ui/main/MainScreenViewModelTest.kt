package com.example.vozostudio.ui.main

import com.example.vozostudio.data.VoiceCategory
import com.example.vozostudio.data.VoiceRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VozoStudioViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initialRecordsLoaded() = runTest(testDispatcher) {
        val repository = VoiceRepository()
        val viewModel = VozoStudioViewModel(repository)
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value
        assertNotNull(state)
        assertTrue(state.records.isNotEmpty())
    }

    @Test
    fun uiState_categoryFilterWorks() = runTest(testDispatcher) {
        val repository = VoiceRepository()
        val viewModel = VozoStudioViewModel(repository)
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }

        viewModel.selectCategory(VoiceCategory.VOICE_CLONE)

        val state = viewModel.uiState.value
        assertTrue(state.records.isNotEmpty())
        assertTrue(state.records.all { it.category == VoiceCategory.VOICE_CLONE })
    }

    @Test
    fun uiState_togglePlayback() = runTest(testDispatcher) {
        val repository = VoiceRepository()
        val viewModel = VozoStudioViewModel(repository)
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }

        val recordId = repository.records.value.first().id
        viewModel.togglePlayback(recordId)

        val state = viewModel.uiState.value
        assertEquals(recordId, state.playingRecordId)
        assertTrue(state.isPlaybackActive)
    }
}
