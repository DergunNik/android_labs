package com.example.timer.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.timer.MainActivity
import com.example.timer.R
import com.example.timer.domain.model.TimerPhase
import com.example.timer.domain.model.TimerSequence
import com.example.timer.domain.model.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerService : LifecycleService() {

    private val binder = TimerBinder()
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var flatPhases: List<TimerPhase> = emptyList()
    private var currentIndex = 0

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    fun setSequenceAndStart(sequence: TimerSequence) {
        flatPhases = sequence.phases.flatMap { phase ->
            List(phase.repetitions) { phase }
        }
        currentIndex = 0

        _timerState.update {
            it.copy(
                sequenceName = sequence.name,
                totalPhases = flatPhases.size,
                isFinished = false
            )
        }

        startForegroundService()
        startPhase()
    }

    private fun startPhase() {
        if (currentIndex >= flatPhases.size) {
            finishSequence()
            return
        }

        val phase = flatPhases[currentIndex]
        timerJob?.cancel()

        timerJob = lifecycleScope.launch {
            _timerState.update {
                it.copy(
                    isRunning = true,
                    isFinished = false,
                    currentPhase = phase,
                    currentPhaseTotalSeconds = phase.durationSeconds
                )
            }

            for (seconds in phase.durationSeconds downTo 0) {
                _timerState.update {
                    it.copy(
                        timeLeftSeconds = seconds,
                        currentPhaseIndex = currentIndex + 1,
                        upcomingPhases = flatPhases.drop(currentIndex + 1)
                    )
                }

                updateNotification("Фаза: ${phase.type.name}", "Осталось: $seconds сек")

                if (seconds == 0) playSignal()
                if (seconds > 0) delay(1000)
            }
            currentIndex++
            startPhase()
        }
    }

    fun pauseTimer() {
        if (_timerState.value.isFinished) return
        timerJob?.cancel()
        _timerState.update { it.copy(isRunning = false) }
        updateNotification("Таймер на паузе", "")
    }

    fun resumeTimer() {
        if (_timerState.value.isFinished) return
        val currentSeconds = _timerState.value.timeLeftSeconds
        val phase = flatPhases.getOrNull(currentIndex) ?: return

        timerJob = lifecycleScope.launch {
            _timerState.update { it.copy(isRunning = true) }
            for (seconds in currentSeconds downTo 0) {
                _timerState.update { it.copy(timeLeftSeconds = seconds) }
                updateNotification("Фаза: ${phase.type.name}", "Осталось: $seconds сек")
                if (seconds == 0) playSignal()
                if (seconds > 0) delay(1000)
            }
            currentIndex++
            startPhase()
        }
    }

    fun nextPhase() {
        if (currentIndex < flatPhases.size - 1) {
            currentIndex++
            startPhase()
        } else {
            finishSequence()
        }
    }

    fun previousPhase() {
        if (currentIndex > 0) {
            currentIndex--
            startPhase()
        }
    }

    private fun finishSequence() {
        timerJob?.cancel()
        _timerState.update {
            it.copy(
                isRunning = false,
                isFinished = true,
                currentPhase = null,
                timeLeftSeconds = 0,
                upcomingPhases = emptyList()
            )
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun stopTimer() {
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun playSignal() {
        try {
            val mediaPlayer = MediaPlayer.create(this, R.raw.timer_beep_sound)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { it.release() }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun startForegroundService() {
        val channelId = "timer_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Таймер", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Таймер запущен")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun updateNotification(title: String, text: String) {
        // Логика обновления уведомления
    }
}