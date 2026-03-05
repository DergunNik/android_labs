package com.example.timer.presentation.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.domain.model.TimerState
import com.example.timer.domain.repository.TimerSequenceRepository
import com.example.timer.services.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: TimerSequenceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sequenceId: String? = savedStateHandle.get<String>("sequenceId")

    private var timerService: TimerService? = null

    // Внутренний стейт для UI, пока сервис не подключится
    private val _uiState = MutableStateFlow(TimerState())
    val uiState: StateFlow<TimerState> = _uiState.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()

            viewModelScope.launch {
                timerService?.timerState?.collect { state ->
                    _uiState.value = state
                }
            }

            startSequence()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
        }
    }

    init {
        val intent = Intent(context, TimerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun startSequence() {
        sequenceId?.let { id ->
            viewModelScope.launch {
                val sequence = repository.getSequenceById(id)
                sequence?.let { timerService?.setSequenceAndStart(it) }
            }
        }
    }

    fun togglePlayPause() {
        if (_uiState.value.isRunning) {
            timerService?.pauseTimer()
        } else {
            timerService?.resumeTimer()
        }
    }

    fun nextPhase() = timerService?.nextPhase()

    fun previousPhase() = timerService?.previousPhase()

    fun cancelTimer() {
        timerService?.stopTimer()
    }

    override fun onCleared() {
        super.onCleared()
        context.unbindService(serviceConnection)
    }
}