package com.example.timer.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.domain.model.PhaseType
import com.example.timer.domain.model.TimerPhase
import com.example.timer.domain.model.TimerSequence
import com.example.timer.domain.repository.TimerSequenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: TimerSequenceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sequenceId: String? = savedStateHandle.get<String>("sequenceId")

    private val _sequenceState = MutableStateFlow<TimerSequence?>(null)
    val sequenceState: StateFlow<TimerSequence?> = _sequenceState.asStateFlow()

    init {
        if (sequenceId != null) {
            viewModelScope.launch {
                _sequenceState.value = repository.getSequenceById(sequenceId)
            }
        } else {
            _sequenceState.value = TimerSequence(
                name = "Новый таймер",
                color = 0xFFBB86FC.toInt(),
                phases = emptyList()
            )
        }
    }

    fun updateName(newName: String) {
        _sequenceState.value = _sequenceState.value?.copy(name = newName)
    }

    fun addPhase(type: PhaseType) {
        val currentSeq = _sequenceState.value ?: return
        val newPhase = TimerPhase(type = type, durationSeconds = 30)
        _sequenceState.value = currentSeq.copy(phases = currentSeq.phases + newPhase)
    }

    fun updatePhaseDuration(phaseId: String, deltaSeconds: Int) {
        val currentSeq = _sequenceState.value ?: return
        val updatedPhases = currentSeq.phases.map { phase ->
            if (phase.id == phaseId) {
                val newDuration = (phase.durationSeconds + deltaSeconds).coerceAtLeast(5)
                phase.copy(durationSeconds = newDuration)
            } else phase
        }
        _sequenceState.value = currentSeq.copy(phases = updatedPhases)
    }

    fun setPhaseDuration(phaseId: String, secondsStr: String) {
        val currentSeq = _sequenceState.value ?: return
        val seconds = secondsStr.toIntOrNull() ?: 0

        val updatedPhases = currentSeq.phases.map { phase ->
            if (phase.id == phaseId) {
                phase.copy(durationSeconds = seconds)
            } else phase
        }
        _sequenceState.value = currentSeq.copy(phases = updatedPhases)
    }

    fun removePhase(phaseId: String) {
        val currentSeq = _sequenceState.value ?: return
        _sequenceState.value = currentSeq.copy(
            phases = currentSeq.phases.filter { it.id != phaseId }
        )
    }

    fun saveSequence(onSaved: () -> Unit) {
        val seqToSave = _sequenceState.value ?: return
        viewModelScope.launch {
            if (sequenceId != null) {
                repository.updateSequence(seqToSave)
            } else {
                repository.insertSequence(seqToSave)
            }
            onSaved()
        }
    }
}