package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class SoundCue {
    CORRECT,
    WRONG,
    MISSION_COMPLETE,
    XP_GAIN,
    LEVEL_UP,
    MEDAL_UNLOCKED,
    STREAK_KEPT
}

/**
 * Gerador local de efeitos sonoros originais em PCM 16-bit (sem arquivos protegidos por direitos autorais).
 */
class SoundEffectsManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    var enabled: Boolean = true

    @Volatile
    var volume: Float = 0.7f

    fun play(cue: SoundCue) {
        if (!enabled || volume <= 0.01f) return
        val currentVol = volume.coerceIn(0f, 1f)
        scope.launch {
            try {
                when (cue) {
                    SoundCue.CORRECT -> playNotes(
                        listOf(523.25 to 90, 659.25 to 140),
                        currentVol
                    )
                    SoundCue.WRONG -> playNotes(
                        listOf(246.94 to 110, 220.00 to 160),
                        currentVol * 0.75f
                    )
                    SoundCue.XP_GAIN -> playNotes(
                        listOf(783.99 to 75, 1046.50 to 110),
                        currentVol * 0.8f
                    )
                    SoundCue.MISSION_COMPLETE -> playNotes(
                        listOf(523.25 to 90, 659.25 to 90, 783.99 to 180),
                        currentVol
                    )
                    SoundCue.LEVEL_UP -> playNotes(
                        listOf(523.25 to 85, 659.25 to 85, 783.99 to 85, 1046.50 to 230),
                        currentVol
                    )
                    SoundCue.MEDAL_UNLOCKED -> playNotes(
                        listOf(587.33 to 90, 739.99 to 90, 880.00 to 200),
                        currentVol
                    )
                    SoundCue.STREAK_KEPT -> playNotes(
                        listOf(440.00 to 80, 587.33 to 140),
                        currentVol * 0.9f
                    )
                }
            } catch (e: Exception) {
                Log.w("SoundEffectsManager", "Audio playback skipped: ${e.message}")
            }
        }
    }

    private fun playNotes(notes: List<Pair<Double, Int>>, vol: Float) {
        val sampleRate = 22050
        val totalSamples = notes.sumOf { (sampleRate * it.second) / 1000 }
        if (totalSamples <= 0) return

        val pcm = ShortArray(totalSamples)
        var offset = 0
        for ((freq, durationMs) in notes) {
            val count = (sampleRate * durationMs) / 1000
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                val envAttack = (i.toDouble() / (sampleRate * 0.012)).coerceAtMost(1.0)
                val envDecay = exp(-4.5 * (i.toDouble() / count))
                val wave = (sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t)) / 1.25
                val sample = (wave * envAttack * envDecay * vol * Short.MAX_VALUE * 0.55).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                pcm[offset + i] = sample.toShort()
            }
            offset += count
        }

        val bufferBytes = pcm.size * 2
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(bufferBytes)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBuf)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcm, 0, pcm.size)
        track.play()
        val totalMs = notes.sumOf { it.second }.toLong()
        Thread.sleep(totalMs + 40L)
        track.stop()
        track.release()
    }
}
