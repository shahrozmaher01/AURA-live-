package com.example.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

class BismaAudioManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var recordJob: Job? = null
    private var sfxJob: Job? = null

    private val _isMicrophoneMuted = MutableStateFlow(false)
    val isMicrophoneMuted: StateFlow<Boolean> = _isMicrophoneMuted.asStateFlow()

    private val _isSpeakerMuted = MutableStateFlow(false)
    val isSpeakerMuted: StateFlow<Boolean> = _isSpeakerMuted.asStateFlow()

    private val _currentVoiceLevel = MutableStateFlow(0f)
    val currentVoiceLevel: StateFlow<Float> = _currentVoiceLevel.asStateFlow()

    private val _currentBgmTrack = MutableStateFlow<String?>(null)
    val currentBgmTrack: StateFlow<String?> = _currentBgmTrack.asStateFlow()

    private val _isBgmPlaying = MutableStateFlow(false)
    val isBgmPlaying: StateFlow<Boolean> = _isBgmPlaying.asStateFlow()

    private val _activeSfxEffect = MutableStateFlow<String?>(null)
    val activeSfxEffect: StateFlow<String?> = _activeSfxEffect.asStateFlow()

    fun toggleMicrophone(onSeat: Boolean) {
        if (!onSeat) {
            _isMicrophoneMuted.value = true
            return
        }
        _isMicrophoneMuted.value = !_isMicrophoneMuted.value
    }

    fun setMicrophoneMuted(muted: Boolean) {
        _isMicrophoneMuted.value = muted
    }

    fun toggleSpeaker() {
        _isSpeakerMuted.value = !_isSpeakerMuted.value
    }

    fun playSoundEffect(name: String) {
        _activeSfxEffect.value = name
        sfxJob?.cancel()
        sfxJob = scope.launch {
            delay(2500)
            _activeSfxEffect.value = null
        }
    }

    fun toggleBgm(trackName: String) {
        if (_currentBgmTrack.value == trackName && _isBgmPlaying.value) {
            _isBgmPlaying.value = false
            _currentBgmTrack.value = null
        } else {
            _currentBgmTrack.value = trackName
            _isBgmPlaying.value = true
        }
    }

    fun stopBgm() {
        _isBgmPlaying.value = false
        _currentBgmTrack.value = null
    }

    fun startVoiceCapture(hasMicPermission: Boolean) {
        stopVoiceCapture()
        recordJob = scope.launch {
            var audioRecord: AudioRecord? = null
            if (hasMicPermission) {
                try {
                    val bufferSize = AudioRecord.getMinBufferSize(
                        16000,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT
                    )
                    if (bufferSize > 0) {
                        audioRecord = AudioRecord(
                            MediaRecorder.AudioSource.MIC,
                            16000,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            bufferSize
                        )
                        audioRecord.startRecording()
                    }
                } catch (_: Exception) {
                    audioRecord = null
                }
            }

            val buffer = ShortArray(512)
            while (isActive) {
                if (_isMicrophoneMuted.value) {
                    _currentVoiceLevel.value = 0f
                } else if (audioRecord != null && audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val read = audioRecord.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        var sum = 0L
                        for (i in 0 until read) {
                            sum += abs(buffer[i].toInt())
                        }
                        val avg = sum / read.toFloat()
                        // Normalize 0..1
                        val level = (avg / 3000f).coerceIn(0f, 1f)
                        _currentVoiceLevel.value = level
                    }
                } else {
                    // Safe development/simulation mode with gentle organic fluctuation when speaking
                    val simulated = (Random.nextFloat() * 0.7f + 0.15f)
                    _currentVoiceLevel.value = simulated
                }
                delay(120)
            }

            try {
                audioRecord?.stop()
                audioRecord?.release()
            } catch (_: Exception) {}
        }
    }

    fun stopVoiceCapture() {
        recordJob?.cancel()
        recordJob = null
        _currentVoiceLevel.value = 0f
    }
}
