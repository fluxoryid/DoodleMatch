package com.fluxoryid.doodlematch.ui.screens.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fluxoryid.doodlematch.data.drawing.DrawingAvailabilityRepository
import com.fluxoryid.doodlematch.data.progress.MatchProgressStore
import com.fluxoryid.doodlematch.domain.DoodleCatalog
import com.fluxoryid.doodlematch.domain.match.MatchBoardBuilder
import com.fluxoryid.doodlematch.domain.match.MatchBoardResult
import com.fluxoryid.doodlematch.domain.match.MatchGameReducer
import com.fluxoryid.doodlematch.domain.match.MatchGameState
import com.fluxoryid.doodlematch.domain.match.MatchSoundEffects
import com.fluxoryid.doodlematch.domain.match.NoOpMatchSoundEffects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val COMPARISON_DELAY_MS = 800L
private const val TIMER_TICK_MS = 1_000L

/**
 * Owns the Match game's state so it isn't scattered across remember{} calls in the Composable —
 * card generation, selection rules, the comparison delay, the elapsed-time ticker, and the
 * completion -> progress/reward write are all coordinated here.
 */
class MatchViewModel(
    private val categoryId: String,
    private val availabilityRepository: DrawingAvailabilityRepository,
    private val progressStore: MatchProgressStore,
    private val soundEffects: MatchSoundEffects = NoOpMatchSoundEffects,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchUiState>(MatchUiState.Loading)
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startNewGame()
    }

    /** Builds (or rebuilds, for "Play Again") a fresh shuffled board and starts the timer. */
    fun startNewGame() {
        timerJob?.cancel()
        _uiState.value = MatchUiState.Loading

        viewModelScope.launch {
            val objects = DoodleCatalog.objectsFor(categoryId)
            val completed = withContext(Dispatchers.IO) {
                availabilityRepository.completedObjectIds(categoryId, objects.map { it.id })
            }

            when (val result = MatchBoardBuilder.build(categoryId, objects, completed)) {
                is MatchBoardResult.NotEnoughDrawings -> {
                    _uiState.value = MatchUiState.NotEnoughDrawings(result.completedCount)
                }
                is MatchBoardResult.Board -> {
                    val gameState = MatchGameState(
                        categoryId = categoryId,
                        cards = result.cards,
                        totalPairs = result.cards.size / 2,
                    )
                    _uiState.value = MatchUiState.Playing(gameState)
                    startTimer()
                }
            }
        }
    }

    fun onCardTapped(cardId: String) {
        val playing = _uiState.value as? MatchUiState.Playing ?: return
        val before = playing.state
        val after = MatchGameReducer.selectCard(before, cardId)
        if (after === before) return // tap was ignored by the reducer (locked, matched, etc.)

        soundEffects.onCardFlipped()
        _uiState.value = playing.copy(state = after)

        if (after.secondSelectedCardId != null) {
            viewModelScope.launch {
                delay(COMPARISON_DELAY_MS)
                resolveCurrentComparison()
            }
        }
    }

    private fun resolveCurrentComparison() {
        val playing = _uiState.value as? MatchUiState.Playing ?: return
        val before = playing.state
        val resolved = MatchGameReducer.resolveComparison(before)
        if (resolved === before) return

        val wasMatch = resolved.matchedPairCount > before.matchedPairCount
        if (wasMatch) soundEffects.onMatchFound() else soundEffects.onMismatch()

        _uiState.value = playing.copy(state = resolved)

        if (resolved.isComplete) {
            onGameComplete(resolved)
        }
    }

    private fun onGameComplete(state: MatchGameState) {
        timerJob?.cancel()
        soundEffects.onGameCompleted()

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                progressStore.recordCompletion(categoryId, state.attempts, state.elapsedMillis)
            }
            val playing = _uiState.value as? MatchUiState.Playing ?: return@launch
            _uiState.value = playing.copy(rewardNewlyUnlocked = result.rewardNewlyUnlocked)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(TIMER_TICK_MS)
                val playing = _uiState.value as? MatchUiState.Playing ?: continue
                if (playing.state.isComplete) continue
                _uiState.value = playing.copy(
                    state = playing.state.copy(elapsedMillis = playing.state.elapsedMillis + TIMER_TICK_MS),
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
