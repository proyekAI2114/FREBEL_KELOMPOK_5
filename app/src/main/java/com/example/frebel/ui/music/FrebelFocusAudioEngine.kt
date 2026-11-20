package com.example.frebel.ui.music

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class MusicSource(val label: String, val iconEmoji: String) {
    BUILT_IN("Ambient Lo-Fi", "🎧"),
    SPOTIFY("Spotify Sync", "🟢"),
    YOUTUBE("YouTube Music", "🔴")
}

data class FocusTrack(
    val id: String,
    val title: String,
    val artist: String,
    val source: MusicSource,
    val url: String = "",
    val baseFreq: Double = 220.0
)

class FrebelFocusAudioEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var synthesisJob: Job? = null
    private var audioTrack: AudioTrack? = null

    var isPlaying by mutableStateOf(false)
    var volume by mutableFloatStateOf(0.75f)
    var isMuted by mutableStateOf(false)
    var isAutoSyncEnabled by mutableStateOf(true)
    var isBreakDuckingActive by mutableStateOf(false)
    var currentSource by mutableStateOf(MusicSource.BUILT_IN)
    var currentTrackIndex by mutableIntStateOf(0)

    var spotifyAccountConnected by mutableStateOf(false)
    var youtubeAccountConnected by mutableStateOf(false)
    var activeCustomUrl by mutableStateOf("")

    val tracks = mutableStateListOf(
        FocusTrack("1", "Cyber Synthwave Alpha (14Hz)", "Frebel Cyber Soundscape", MusicSource.BUILT_IN, baseFreq = 220.0),
        FocusTrack("2", "Cozy Forest Leaves & Rain", "Frebel Nature Ambience", MusicSource.BUILT_IN, baseFreq = 261.63),
        FocusTrack("3", "Cosmic Space Nebula 432Hz", "Frebel Galaxy Focus", MusicSource.BUILT_IN, baseFreq = 216.0),
        FocusTrack("4", "Calm Ocean Waves & Piano", "Frebel Aqua Flow", MusicSource.BUILT_IN, baseFreq = 174.61),
        FocusTrack("5", "Zen Minimalist White Chords", "Frebel Pure Focus", MusicSource.BUILT_IN, baseFreq = 196.0)
    )

    val currentTrack: FocusTrack
        get() = tracks.getOrElse(currentTrackIndex) { tracks.first() }

    fun play() {
        if (isPlaying) return
        isPlaying = true
        startAudioSynthesis()
    }

    fun pause() {
        if (!isPlaying) return
        isPlaying = false
        stopAudioSynthesis()
    }

    fun togglePlayPause() {
        if (isPlaying) pause() else play()
    }

    fun nextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % tracks.size
        if (isPlaying) {
            startAudioSynthesis()
        }
    }

    fun previousTrack() {
        currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.size - 1
        if (isPlaying) {
            startAudioSynthesis()
        }
    }

    fun setMasterVolume(newVol: Float) {
        volume = newVol.coerceIn(0f, 1f)
        applyVolumeToTrack()
    }

    fun toggleMuteState() {
        isMuted = !isMuted
        applyVolumeToTrack()
    }

    private fun getEffectiveVolume(): Float {
        if (isMuted) return 0f
        val duckFactor = if (isBreakDuckingActive) 0.35f else 1.0f
        return (volume * duckFactor).coerceIn(0f, 1f)
    }

    private fun applyVolumeToTrack() {
        runCatching {
            val eff = getEffectiveVolume()
            audioTrack?.setVolume(eff)
        }
    }

    /**
     * Sinkronisasi Otomatis dengan Pomodoro Timer:
     * - Pomodoro Start -> Musik otomatis mulai diputar dengan fade-in
     * - Pomodoro Pause -> Musik otomatis ikut terhenti
     * - Pomodoro Break -> Musik otomatis mengecil (ducking 35%)
     */
    fun onPomodoroStateChanged(isTimerRunning: Boolean, isBreak: Boolean) {
        if (!isAutoSyncEnabled) return

        isBreakDuckingActive = isBreak
        applyVolumeToTrack()

        if (isTimerRunning) {
            if (!isPlaying) {
                play()
            }
        } else {
            if (isPlaying) {
                pause()
            }
        }
    }

    fun connectSpotify(playlistUrl: String = "https://open.spotify.com/playlist/0vvXsWCC9xrXsKd4FyS8kM", playlistTitle: String = "Spotify: Lofi Girl Study Beats") {
        spotifyAccountConnected = true
        currentSource = MusicSource.SPOTIFY
        activeCustomUrl = playlistUrl
        val customTrack = FocusTrack(
            id = "spotify_active",
            title = playlistTitle,
            artist = "Spotify Playback Sync",
            source = MusicSource.SPOTIFY,
            url = playlistUrl,
            baseFreq = 220.0
        )
        tracks.removeAll { it.id == "spotify_active" }
        tracks.add(0, customTrack)
        currentTrackIndex = 0
        if (isPlaying) {
            startAudioSynthesis()
        }
    }

    fun connectYouTube(videoUrl: String = "https://www.youtube.com/watch?v=jfKfPfyJRdk", videoTitle: String = "YouTube: Lofi Girl 24/7 Focus Stream") {
        youtubeAccountConnected = true
        currentSource = MusicSource.YOUTUBE
        activeCustomUrl = videoUrl
        val customTrack = FocusTrack(
            id = "youtube_active",
            title = videoTitle,
            artist = "YouTube Music Hub",
            source = MusicSource.YOUTUBE,
            url = videoUrl,
            baseFreq = 261.63
        )
        tracks.removeAll { it.id == "youtube_active" }
        tracks.add(0, customTrack)
        currentTrackIndex = 0
        if (isPlaying) {
            startAudioSynthesis()
        }
    }

    fun launchExternalApp(url: String) {
        val targetUrl = url.ifBlank { activeCustomUrl }
        if (targetUrl.isBlank()) return
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    private fun startAudioSynthesis() {
        stopAudioSynthesis()
        val trackIndex = currentTrackIndex
        val targetTrack = tracks.getOrElse(trackIndex) { tracks.first() }

        synthesisJob = scope.launch(Dispatchers.IO) {
            runCatching {
                val sampleRate = 22050
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(4096)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                applyVolumeToTrack()
                track.play()

                val freq1 = targetTrack.baseFreq
                val freq2 = freq1 * 1.25 // Major 3rd
                val freq3 = freq1 * 1.50 // Perfect 5th

                val buffer = ShortArray(bufferSize / 2)
                var sampleIndex = 0L

                while (isActive && isPlaying) {
                    val effVol = getEffectiveVolume()
                    for (i in buffer.indices) {
                        val t = sampleIndex.toDouble() / sampleRate.toDouble()
                        // Chord arpeggio envelope lembut
                        val cycle = (t % 4.0) / 4.0
                        val env = sin(cycle * PI) * 0.85
                        val wave1 = sin(2.0 * PI * freq1 * t)
                        val wave2 = sin(2.0 * PI * freq2 * t) * 0.7
                        val wave3 = sin(2.0 * PI * freq3 * t) * 0.5
                        val combined = (wave1 + wave2 + wave3) / 2.2 * env * effVol
                        buffer[i] = (combined * Short.MAX_VALUE * 0.28).toInt().toShort()
                        sampleIndex++
                    }
                    val written = track.write(buffer, 0, buffer.size)
                    if (written < 0) break
                }

                runCatching {
                    track.stop()
                    track.release()
                }
            }
        }
    }

    private fun stopAudioSynthesis() {
        val oldJob = synthesisJob
        synthesisJob = null
        val oldTrack = audioTrack
        audioTrack = null

        scope.launch(Dispatchers.IO) {
            oldJob?.cancel()
            runCatching {
                oldTrack?.pause()
                oldTrack?.flush()
                oldTrack?.stop()
                oldTrack?.release()
            }
        }
    }

    fun release() {
        isPlaying = false
        stopAudioSynthesis()
    }
}
