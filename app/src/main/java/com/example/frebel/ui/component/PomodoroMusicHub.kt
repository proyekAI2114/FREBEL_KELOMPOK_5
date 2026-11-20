package com.example.frebel.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frebel.ui.music.FrebelFocusAudioEngine
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSoundManager
import com.example.frebel.ui.theme.FrebelThemeSpec
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun PomodoroMusicHub(
    audioEngine: FrebelFocusAudioEngine,
    themeSpec: FrebelThemeSpec,
    language: FrebelLanguage = FrebelLanguage.INDONESIAN,
    soundEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isEn = language == FrebelLanguage.ENGLISH
    var customUrlInput by remember { mutableStateOf("") }

    val transition = rememberInfiniteTransition(label = "audioWaveTransition")
    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = themeSpec.cardSurface),
        border = BorderStroke(1.5.dp, themeSpec.primaryAccent.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Judul HUD & Status Sinkronisasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (audioEngine.isPlaying) Color(0xFF10B981) else Color(0xFF94A3B8))
                    )
                    Text(
                        text = "POMODORO MUSIC HUB",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = themeSpec.primaryAccent,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (audioEngine.isBreakDuckingActive) {
                        themeSpec.secondaryAccent.copy(alpha = 0.20f)
                    } else if (audioEngine.isPlaying) {
                        themeSpec.primaryAccent.copy(alpha = 0.16f)
                    } else {
                        themeSpec.cardBorder.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when {
                            audioEngine.isBreakDuckingActive -> if (isEn) "BREAK DUCKING (35%)" else "ISTIRAHAT (DUCKING 35%)"
                            audioEngine.isPlaying -> if (isEn) "SYNCED & PLAYING" else "FOKUS AKTIF"
                            else -> if (isEn) "PAUSED" else "TERJEDA"
                        },
                        style = TextStyle(fontFeatureSettings = "tnum", fontWeight = FontWeight.Black, fontSize = 10.sp),
                        color = if (audioEngine.isBreakDuckingActive) themeSpec.secondaryAccent else themeSpec.primaryAccent,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            // Visualizer Gelombang Audio Animasi (16 Frekuensi Bars)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(themeSpec.primaryAccent.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(34.dp)) {
                    val numBars = 16
                    val barWidth = 7f
                    val totalWidth = size.width
                    val spacing = (totalWidth - (numBars * barWidth)) / (numBars - 1)
                    val baseHeight = size.height

                    for (i in 0 until numBars) {
                        val x = i * (barWidth + spacing)
                        val amp = if (audioEngine.isPlaying) {
                            val wave1 = abs(sin(wavePhase + i * 0.45f))
                            val wave2 = abs(sin(wavePhase * 1.5f + i * 0.75f))
                            val combined = ((wave1 + wave2) / 2f).coerceIn(0.18f, 1f)
                            if (audioEngine.isBreakDuckingActive) combined * 0.45f else combined
                        } else {
                            0.12f
                        }

                        val h = (baseHeight * amp).coerceAtLeast(4f)
                        val y = (baseHeight - h) / 2f

                        val barBrush = Brush.verticalGradient(
                            listOf(themeSpec.secondaryAccent, themeSpec.primaryAccent),
                            startY = y,
                            endY = y + h
                        )

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, h),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.5f, 3.5f)
                        )
                    }
                }
            }

            // Track Metadata Row
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = themeSpec.primaryAccent.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, themeSpec.cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.horizontalGradient(listOf(themeSpec.primaryAccent, themeSpec.secondaryAccent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "☕", fontSize = 20.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rainy Lo-Fi Study Beats",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = themeSpec.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Frebel Ambient Station • Focus Loop",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeSpec.textSecondary
                        )
                    }

                    // Play/Pause Button
                    IconButton(
                        onClick = {
                            FrebelSoundManager.playTactileClick(soundEnabled)
                            if (audioEngine.isPlaying) audioEngine.pause() else audioEngine.play()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(themeSpec.primaryAccent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Volume Slider HUD
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🔊 VOLUME",
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeSpec.textSecondary)
                )
                Slider(
                    value = audioEngine.volume,
                    onValueChange = { audioEngine.volume = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = themeSpec.primaryAccent,
                        activeTrackColor = themeSpec.primaryAccent,
                        inactiveTrackColor = themeSpec.primaryAccent.copy(alpha = 0.2f)
                    )
                )
                Text(
                    text = "${(audioEngine.volume * 100).toInt()}%",
                    style = TextStyle(fontFeatureSettings = "tnum", fontWeight = FontWeight.Black, fontSize = 11.sp, color = themeSpec.primaryAccent)
                )
            }

            // Quick Paste URL Input (Spotify & YouTube)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customUrlInput,
                    onValueChange = { customUrlInput = it },
                    placeholder = {
                        Text(
                            "Tempel link Spotify/YouTube...",
                            fontSize = 12.sp,
                            color = themeSpec.textSecondary.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeSpec.primaryAccent,
                        unfocusedBorderColor = themeSpec.cardBorder
                    )
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = themeSpec.primaryAccent,
                    modifier = Modifier.clickable {
                        FrebelSoundManager.playTactileClick(soundEnabled)
                        // Load and sync quick URL
                    }
                ) {
                    Text(
                        text = "Putar",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}