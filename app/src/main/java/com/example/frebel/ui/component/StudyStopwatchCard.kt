package com.example.frebel.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSubTheme
import com.example.frebel.ui.theme.FrebelThemeSpec
import com.example.frebel.ui.theme.resolveFrebelThemeSpec
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class StudyTimerMode {
    STOPWATCH,
    POMODORO
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudyStopwatchCard(
    elapsedSeconds: Int,
    breakRemainingSeconds: Int,
    isRunning: Boolean,
    timerMode: StudyTimerMode,
    pomodoroTargetMinutes: Int,
    breakTargetMinutes: Int,
    isBreakActive: Boolean,
    selectedSubject: String,
    subjectOptions: List<String>,
    onSelectMode: (StudyTimerMode) -> Unit,
    onSelectPomodoroMinutes: (Int) -> Unit,
    onSelectBreakMinutes: (Int) -> Unit,
    onStartBreak: (Int) -> Unit,
    onFinishBreak: () -> Unit,
    onSelectSubject: (String) -> Unit,
    onAddSubject: (String) -> Unit,
    onDeleteSubject: (String) -> Unit,
    onToggleStartPause: () -> Unit,
    onReset: () -> Unit,
    onSubmitStudyComplete: (Int) -> Unit,
    themeSpec: FrebelThemeSpec = resolveFrebelThemeSpec(false, FrebelSubTheme.SOLID),
    language: FrebelLanguage = FrebelLanguage.INDONESIAN,
    modifier: Modifier = Modifier
) {
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectInput by remember { mutableStateOf("") }

    val isEn = language == FrebelLanguage.ENGLISH
    val breakDurations = listOf(5, 10, 15)
    val pomodoroDurations = listOf(15, 25, 45, 60)

    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = themeSpec.cardSurface,
            title = {
                Text(
                    text = if (isEn) "Add Study Subject" else "Tambah Mata Pelajaran",
                    fontWeight = FontWeight.Bold,
                    color = themeSpec.textPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = newSubjectInput,
                    onValueChange = { newSubjectInput = it },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeSpec.primaryAccent,
                        unfocusedBorderColor = themeSpec.cardBorder,
                        focusedTextColor = themeSpec.textPrimary,
                        unfocusedTextColor = themeSpec.textPrimary,
                        cursorColor = themeSpec.primaryAccent
                    ),
                    placeholder = {
                        Text(
                            text = if (isEn) "e.g. Mathematics, C++ Coding" else "Contoh: Matematika, Pemrograman C++",
                            color = themeSpec.textSecondary.copy(alpha = 0.65f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newSubjectInput.trim()
                        if (trimmed.isNotEmpty()) {
                            onAddSubject(trimmed)
                            newSubjectInput = ""
                        }
                        showAddSubjectDialog = false
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                ) {
                    Text(if (isEn) "Add" else "Tambah", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text(if (isEn) "Cancel" else "Batal", color = themeSpec.textSecondary)
                }
            }
        )
    }

    val rawModeColor = if (timerMode == StudyTimerMode.POMODORO) {
        themeSpec.secondaryAccent
    } else {
        themeSpec.primaryAccent
    }
    val rawDisplayColor = if (isBreakActive) {
        themeSpec.secondaryAccent
    } else {
        rawModeColor
    }

    val modeBaseColor by animateColorAsState(
        targetValue = rawModeColor,
        animationSpec = tween(350),
        label = "modeBaseColorAnim"
    )
    val activeDisplayColor by animateColorAsState(
        targetValue = rawDisplayColor,
        animationSpec = tween(350),
        label = "activeDisplayColorAnim"
    )

    val mainClockSeconds = when {
        isBreakActive -> breakRemainingSeconds
        timerMode == StudyTimerMode.POMODORO -> (pomodoroTargetMinutes * 60 - elapsedSeconds).coerceAtLeast(0)
        else -> elapsedSeconds
    }

    val targetRingProgress = when {
        isBreakActive -> {
            val totalBreak = (breakTargetMinutes * 60).coerceAtLeast(1)
            (breakRemainingSeconds.toFloat() / totalBreak.toFloat()).coerceIn(0f, 1f)
        }
        timerMode == StudyTimerMode.POMODORO -> {
            val totalPomo = (pomodoroTargetMinutes * 60).coerceAtLeast(1)
            (mainClockSeconds.toFloat() / totalPomo.toFloat()).coerceIn(0f, 1f)
        }
        else -> {
            ((elapsedSeconds % 3600).toFloat() / 3600f).coerceIn(0f, 1f)
        }
    }

    val animatedRingProgress by animateFloatAsState(
        targetValue = targetRingProgress,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "timerRingProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "timerLiveOrbital")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = -90f,
        targetValue = 270f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stopwatchOrbitAngle"
    )
    val pulseFactor by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timerPulseFactor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = themeSpec.cardSurface),
        border = BorderStroke(1.dp, themeSpec.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEn) "Focus Subject" else "Mata Pelajaran Fokus",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeSpec.textPrimary
                    )
                    Text(
                        text = if (isEn) "Active: $selectedSubject" else "Aktif: $selectedSubject",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeSpec.textSecondary
                    )
                }

                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .bouncyPress { showAddSubjectDialog = true },
                    shape = CircleShape,
                    color = themeSpec.primaryAccent.copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Mapel",
                            modifier = Modifier.size(16.dp),
                            tint = themeSpec.primaryAccent
                        )
                        Text(
                            text = if (isEn) "Subject" else "Mapel",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.primaryAccent
                        )
                    }
                }
            }

            if (subjectOptions.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    subjectOptions.forEach { subject ->
                        val isSelected = subject == selectedSubject
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .bouncyPress { onSelectSubject(subject) },
                            shape = CircleShape,
                            color = if (isSelected) {
                                modeBaseColor
                            } else {
                                themeSpec.primaryAccent.copy(alpha = 0.08f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 13.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = subject,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else themeSpec.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus $subject",
                                    tint = if (isSelected) Color.White.copy(alpha = 0.85f) else themeSpec.textSecondary,
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clip(CircleShape)
                                        .clickable { onDeleteSubject(subject) }
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = themeSpec.cardBorder)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(themeSpec.primaryAccent.copy(alpha = 0.09f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    StudyTimerMode.STOPWATCH to "Stopwatch",
                    StudyTimerMode.POMODORO to "Pomodoro"
                ).forEach { (mode, label) ->
                    val selected = timerMode == mode
                    val tabColor = if (mode == StudyTimerMode.POMODORO) {
                        themeSpec.secondaryAccent
                    } else {
                        themeSpec.primaryAccent
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .bouncyPress { onSelectMode(mode) },
                        shape = CircleShape,
                        color = if (selected) tabColor else Color.Transparent
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selected) Color.White else themeSpec.textSecondary
                        )
                    }
                }
            }

            if (timerMode == StudyTimerMode.POMODORO) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = themeSpec.secondaryAccent.copy(alpha = 0.10f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEn) "Pomodoro Cycle:" else "Siklus Pomodoro:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = themeSpec.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = themeSpec.secondaryAccent.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = if (isEn) {
                                        "Focus ${pomodoroTargetMinutes}m ➔ Break ${breakTargetMinutes}m"
                                    } else {
                                        "Fokus ${pomodoroTargetMinutes}m ➔ Jeda ${breakTargetMinutes}m"
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = themeSpec.secondaryAccent
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEn) "Focus:" else "Fokus:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = themeSpec.textSecondary,
                                modifier = Modifier.width(46.dp)
                            )
                            pomodoroDurations.forEach { mins ->
                                val isSelected = pomodoroTargetMinutes == mins
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CircleShape)
                                        .bouncyPress { onSelectPomodoroMinutes(mins) },
                                    shape = CircleShape,
                                    color = if (isSelected) {
                                        themeSpec.secondaryAccent
                                    } else {
                                        themeSpec.cardSurface
                                    }
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color.White else themeSpec.textPrimary
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEn) "Break:" else "Jeda:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = themeSpec.textSecondary,
                                modifier = Modifier.width(46.dp)
                            )
                            breakDurations.forEach { mins ->
                                val isSelected = breakTargetMinutes == mins
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CircleShape)
                                        .bouncyPress { onSelectBreakMinutes(mins) },
                                    shape = CircleShape,
                                    color = if (isSelected) {
                                        themeSpec.primaryAccent
                                    } else {
                                        themeSpec.cardSurface
                                    }
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color.White else themeSpec.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = activeDisplayColor.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(226.dp)
                            .bouncyPress { onToggleStartPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        val secondaryGlow = if (timerMode == StudyTimerMode.POMODORO) {
                            themeSpec.primaryAccent
                        } else {
                            themeSpec.secondaryAccent
                        }

                        Canvas(modifier = Modifier.size(226.dp)) {
                            val strokeWidthPx = 12.dp.toPx()
                            val radius = (size.minDimension - strokeWidthPx * 2.2f) / 2f
                            val centerOffset = Offset(size.width / 2f, size.height / 2f)
                            val arcTopLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)
                            val arcSize = Size(radius * 2f, radius * 2f)

                            if (isRunning) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            activeDisplayColor.copy(alpha = 0.22f),
                                            secondaryGlow.copy(alpha = 0.08f),
                                            Color.Transparent
                                        ),
                                        center = centerOffset,
                                        radius = radius * pulseFactor * 1.15f
                                    ),
                                    radius = radius * pulseFactor * 1.15f,
                                    center = centerOffset
                                )
                            }

                            drawCircle(
                                color = activeDisplayColor.copy(alpha = 0.14f),
                                radius = radius,
                                center = centerOffset,
                                style = Stroke(width = strokeWidthPx)
                            )

                            val startDeg: Float
                            val sweepDeg: Float
                            if (timerMode == StudyTimerMode.STOPWATCH && !isBreakActive) {
                                startDeg = if (isRunning) orbitAngle else -90f
                                sweepDeg = if (isRunning) 115f else (animatedRingProgress * 360f).coerceAtLeast(18f)
                            } else {
                                startDeg = -90f
                                sweepDeg = 360f * animatedRingProgress
                            }

                            if (sweepDeg > 0.5f) {
                                drawArc(
                                    brush = Brush.linearGradient(
                                        colors = listOf(activeDisplayColor, secondaryGlow)
                                    ),
                                    startAngle = startDeg,
                                    sweepAngle = sweepDeg,
                                    useCenter = false,
                                    topLeft = arcTopLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                                )

                                val tipRad = ((startDeg + sweepDeg) * PI / 180.0).toFloat()
                                val tipX = centerOffset.x + radius * cos(tipRad)
                                val tipY = centerOffset.y + radius * sin(tipRad)
                                drawCircle(
                                    color = activeDisplayColor.copy(alpha = 0.35f),
                                    radius = strokeWidthPx * 0.95f,
                                    center = Offset(tipX, tipY)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = strokeWidthPx * 0.42f,
                                    center = Offset(tipX, tipY)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = activeDisplayColor.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = when {
                                        isBreakActive ->
                                            if (isEn) "BREAK • ${breakTargetMinutes}M" else "JEDA • ${breakTargetMinutes}M"
                                        timerMode == StudyTimerMode.POMODORO ->
                                            if (isEn) "POMODORO • ${pomodoroTargetMinutes}M" else "FOKUS • ${pomodoroTargetMinutes}M"
                                        else -> "STOPWATCH"
                                    },
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = activeDisplayColor
                                )
                            }

                            Text(
                                text = formatTimerClock(mainClockSeconds),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = activeDisplayColor,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = if (isRunning) {
                                    if (isEn) "Tap ring to pause" else "Ketuk ring untuk jeda"
                                } else {
                                    if (isEn) "Tap ring to start" else "Ketuk ring untuk mulai"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = themeSpec.textSecondary
                            )
                        }
                    }

                    Text(
                        text = when {
                            isBreakActive && elapsedSeconds > 0 ->
                                if (isEn) {
                                    "Study time safely paused at ${formatTimerClock(elapsedSeconds)}"
                                } else {
                                    "Waktu belajar terjeda aman di ${formatTimerClock(elapsedSeconds)}"
                                }
                            isBreakActive ->
                                if (isEn) {
                                    "Break time is not counted toward study hours"
                                } else {
                                    "Waktu istirahat tidak dihitung ke jam belajarmu"
                                }
                            isRunning && timerMode == StudyTimerMode.POMODORO ->
                                if (isEn) {
                                    "Auto-switches to $breakTargetMinutes min break when focus ends"
                                } else {
                                    "Otomatis lanjut istirahat $breakTargetMinutes menit setelah fokus selesai"
                                }
                            isRunning ->
                                if (isEn) "Recording your cozy focus session..." else "Sedang mencatat durasi belajarmu..."
                            elapsedSeconds > 0 ->
                                if (isEn) "Paused • ready to resume or complete" else "Timer dijeda • siap dilanjutkan atau diselesaikan"
                            else ->
                                if (isEn) "Tap Start to focus or pick a quick break" else "Tekan Mulai untuk belajar atau pilih jeda istirahat"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = themeSpec.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "Break:" else "Istirahat:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.textSecondary
                        )
                        breakDurations.forEach { mins ->
                            val isSelectedBreak = isBreakActive && breakTargetMinutes == mins
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .bouncyPress { onStartBreak(mins) },
                                shape = CircleShape,
                                color = if (isSelectedBreak) {
                                    activeDisplayColor
                                } else {
                                    themeSpec.cardSurface
                                }
                            ) {
                                Text(
                                    text = "${mins}m",
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelectedBreak) Color.White else activeDisplayColor
                                )
                            }
                        }
                    }
                }
            }

            if (isBreakActive) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onToggleStartPause,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = activeDisplayColor)
                    ) {
                        Text(
                            text = if (isRunning) {
                                if (isEn) "Pause" else "Jeda"
                            } else {
                                if (isEn) "Resume" else "Lanjut"
                            },
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    OutlinedButton(
                        onClick = onFinishBreak,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.2.dp, activeDisplayColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = if (timerMode == StudyTimerMode.POMODORO && elapsedSeconds >= pomodoroTargetMinutes * 60) {
                                if (isEn) "Finish & Save" else "Selesai & Simpan"
                            } else {
                                if (isEn) "Resume Study" else "Lanjut Belajar"
                            },
                            fontWeight = FontWeight.Bold,
                            color = activeDisplayColor
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onToggleStartPause,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = modeBaseColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRunning) {
                                if (isEn) "Pause" else "Jeda"
                            } else if (elapsedSeconds > 0) {
                                if (isEn) "Resume" else "Lanjut"
                            } else {
                                if (isEn) "Start" else "Mulai"
                            },
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    OutlinedButton(
                        onClick = onReset,
                        enabled = elapsedSeconds > 0,
                        modifier = Modifier.height(50.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, themeSpec.cardBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            modifier = Modifier.size(18.dp),
                            tint = themeSpec.textSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset",
                            color = themeSpec.textSecondary
                        )
                    }
                }
            }

            if (elapsedSeconds > 0) {
                FrebelGlowyButton(
                    text = if (isEn) {
                        "Complete Session (${(elapsedSeconds / 60).coerceAtLeast(1)} Min)"
                    } else {
                        "Belajar Selesai (${(elapsedSeconds / 60).coerceAtLeast(1)} Menit)"
                    },
                    icon = Icons.Default.Check,
                    gradientColors = listOf(themeSpec.primaryAccent, themeSpec.secondaryAccent),
                    glowColor = themeSpec.primaryAccent.copy(alpha = 0.45f),
                    onClick = { onSubmitStudyComplete(elapsedSeconds) }
                )
            }
        }
    }
}

private fun Modifier.bouncyPress(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cozyBouncyScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

private fun formatTimerClock(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}