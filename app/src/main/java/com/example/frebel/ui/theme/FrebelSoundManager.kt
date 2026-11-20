package com.example.frebel.ui.theme

import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import java.util.concurrent.Executors

object FrebelSoundManager {

    private val soundExecutor = Executors.newSingleThreadExecutor()
    private var toneGenerator: ToneGenerator? = null
    private var activePreviewRingtone: Ringtone? = null

    private fun getToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            toneGenerator = runCatching {
                ToneGenerator(AudioManager.STREAM_SYSTEM, 60)
            }.getOrNull()
        }
        return toneGenerator
    }

    fun playClick(enabled: Boolean) {
        if (!enabled) return
        soundExecutor.execute {
            runCatching {
                getToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
            }
        }
    }

    fun playTactileClick(enabled: Boolean) {
        if (!enabled) return
        soundExecutor.execute {
            runCatching {
                getToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 25)
            }
        }
    }

    fun playPopSuccess(enabled: Boolean) {
        if (!enabled) return
        soundExecutor.execute {
            runCatching {
                getToneGenerator()?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
            }
        }
    }

    fun playLevelUp(enabled: Boolean) {
        if (!enabled) return
        soundExecutor.execute {
            runCatching {
                getToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
            }
        }
    }

    fun playAchievementFanfare(enabled: Boolean) {
        if (!enabled) return
        soundExecutor.execute {
            runCatching {
                val gen = getToneGenerator() ?: return@runCatching
                gen.startTone(ToneGenerator.TONE_CDMA_PIP, 60)
                Thread.sleep(70)
                gen.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
                Thread.sleep(85)
                gen.startTone(ToneGenerator.TONE_PROP_ACK, 160)
            }
        }
    }

    fun getRingtoneTitle(context: Context, ringtoneUriStr: String): String {
        return runCatching {
            val uri = if (ringtoneUriStr.isBlank()) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            } else {
                Uri.parse(ringtoneUriStr)
            }
            if (uri != null) {
                RingtoneManager.getRingtone(context, uri)?.getTitle(context) ?: "Nada Default"
            } else {
                "Nada Default"
            }
        }.getOrDefault("Nada Default")
    }

    fun playPreviewRingtone(context: Context, ringtoneUriStr: String) {
        runCatching {
            activePreviewRingtone?.stop()
            val uri = if (ringtoneUriStr.isBlank()) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            } else {
                Uri.parse(ringtoneUriStr)
            }
            if (uri != null) {
                val ringtone = RingtoneManager.getRingtone(context, uri)
                activePreviewRingtone = ringtone
                ringtone?.play()
            }
        }
    }
}
