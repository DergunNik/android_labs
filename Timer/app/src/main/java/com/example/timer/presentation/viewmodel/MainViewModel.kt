package com.example.timer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.domain.model.PhaseType
import com.example.timer.domain.model.TimerPhase
import com.example.timer.domain.model.TimerSequence
import com.example.timer.domain.repository.TimerSequenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TimerSequenceRepository
) : ViewModel() {

    val sequences: StateFlow<List<TimerSequence>> = repository.getAllSequences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteSequence(sequence: TimerSequence) {
        viewModelScope.launch {
            repository.deleteSequence(sequence)
        }
    }

    fun addTestSequence() {
        viewModelScope.launch {
            val testSequence = TimerSequence(
                name = "Тренировка ${sequences.value.size + 1}",
                color = 0xFFBB86FC.toInt(),
                phases = listOf(
                    TimerPhase(type = PhaseType.WARMUP, durationSeconds = 30),
                    TimerPhase(type = PhaseType.WORK, durationSeconds = 60),
                    TimerPhase(type = PhaseType.REST, durationSeconds = 15)
                )
            )
            repository.insertSequence(testSequence)
        }
    }
}