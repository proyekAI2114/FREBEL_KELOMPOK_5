package com.example.frebel.ui.component

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frebel.ui.data.GradeScoreRecord
import com.example.frebel.ui.data.SubjectGradeTrack
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSoundManager
import com.example.frebel.ui.theme.FrebelSubTheme
import com.example.frebel.ui.theme.FrebelThemeSpec
import com.example.frebel.ui.theme.resolveFrebelThemeSpec
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.sin

data class FrebelBluePalette(
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

val DefaultFrebelBluePalettes = listOf(
    FrebelBluePalette("Electric Blue", Color(0xFF0284C7), Color(0xFF7DD3FC)),
    FrebelBluePalette("Cobalt Neon", Color(0xFF2563EB), Color(0xFF93C5FD)),
    FrebelBluePalette("Arctic Cyan", Color(0xFF0891B2), Color(0xFF67E8F9)),
    FrebelBluePalette("Sapphire Deep", Color(0xFF4F46E5), Color(0xFFA5B4FC))
)

data class StreakCommitmentItem(
    val id: String,
    val title: String,
    val isChecked: Boolean
)

private data class PeriodTrackSummary(
    val periodTitle: String,
    val periodSubtitle: String,
    val totalMinutes: Int,
    val highestMinutes: Int,
    val lowestMinutes: Int,
    val accentColor: Color
)

private data class StreakBadgeTier(
    val badgeEmoji: String,
    val title: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val nextTargetDays: Int
)

@Composable
fun StudyStreakCard(
    streakDays: Int,
    bestStreakDays: Int,
    isTodaySubmitted: Boolean,
    selectedBluePaletteIndex: Int,
    commitments: List<StreakCommitmentItem>,
    onSelectBluePalette: (Int) -> Unit,
    onToggleCommitment: (String, Boolean) -> Unit,
    onAddCommitment: (String) -> Unit,
    onEditCommitment: (StreakCommitmentItem, String) -> Unit,
    onDeleteCommitment: (String) -> Unit,
    onSubmitStudyComplete: () -> Unit,
    completedSessions: List<CompletedStudySession> = emptyList(),
    gradeTracks: List<SubjectGradeTrack> = emptyList(),
    latestGradeFeedback: String = "",
    onAddGradeSubject: (String) -> Unit = {},
    onDeleteGradeSubject: (String) -> Unit = {},
    onAddDailyScore: (subjectName: String, label: String, score: Double) -> String = { _, _, _ -> "" },
    onAddMidtermScore: (subjectName: String, label: String, score: Double) -> String = { _, _, _ -> "" },
    themeSpec: FrebelThemeSpec = resolveFrebelThemeSpec(false, FrebelSubTheme.SOLID),
    language: FrebelLanguage = FrebelLanguage.INDONESIAN,
    soundEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var newCommitmentText by remember { mutableStateOf("") }
    var editingItem by remember { mutableStateOf<StreakCommitmentItem?>(null) }
    var editingText by remember { mutableStateOf("") }
    var flameBoostActive by remember { mutableStateOf(false) }

    LaunchedEffect(flameBoostActive) {
        if (flameBoostActive) {
            delay(500L)
            flameBoostActive = false
        }
    }

    val flameBounceScale by animateFloatAsState(
        targetValue = if (flameBoostActive) 1.16f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "flameInteractiveBounce"
    )

    var gradeSubTab by remember { mutableIntStateOf(0) }
    var selectedGradeSubject by remember { mutableStateOf(gradeTracks.firstOrNull()?.subjectName ?: "") }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var newGradeSubjectInput by remember { mutableStateOf("") }
    var showAddScoreDialog by remember { mutableStateOf(false) }
    var scoreLabelInput by remember { mutableStateOf("") }
    var scoreValueInput by remember { mutableStateOf("") }
    var activeFeedbackBanner by remember(latestGradeFeedback) { mutableStateOf(latestGradeFeedback) }

    val isEn = language == FrebelLanguage.ENGLISH
    val activeBlue = DefaultFrebelBluePalettes.getOrElse(selectedBluePaletteIndex) {
        DefaultFrebelBluePalettes.first()
    }

    val cozyTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = themeSpec.primaryAccent,
        unfocusedBorderColor = themeSpec.cardBorder,
        focusedTextColor = themeSpec.textPrimary,
        unfocusedTextColor = themeSpec.textPrimary,
        cursorColor = themeSpec.primaryAccent,
        focusedLabelColor = themeSpec.primaryAccent,
        unfocusedLabelColor = themeSpec.textSecondary
    )

    val effectiveSelectedSubject = remember(gradeTracks, selectedGradeSubject) {
        if (gradeTracks.any { it.subjectName == selectedGradeSubject }) {
            selectedGradeSubject
        } else {
            gradeTracks.firstOrNull()?.subjectName ?: ""
        }
    }

    val isFlameLit = isTodaySubmitted && streakDays > 0
    val badgeTier = when {
        !isFlameLit -> StreakBadgeTier(
            badgeEmoji = "🩶",
            title = if (isEn) "Unlit Spark" else "Api Belum Menyala",
            primaryColor = Color(0xFF64748B),
            secondaryColor = Color(0xFFCBD5E1),
            nextTargetDays = 1
        )
        streakDays <= 7 -> StreakBadgeTier(
            badgeEmoji = "🥉",
            title = if (isEn) "Bronze Spark Starter" else "Perintis Api (Lv.1)",
            primaryColor = Color(0xFFD97706),
            secondaryColor = Color(0xFFFDE047),
            nextTargetDays = 8
        )
        streakDays <= 15 -> StreakBadgeTier(
            badgeEmoji = "🥈",
            title = if (isEn) "Silver Focus Keeper" else "Penjaga Fokus (Lv.2)",
            primaryColor = Color(0xFFEA580C),
            secondaryColor = Color(0xFFFDBA74),
            nextTargetDays = 16
        )
        streakDays <= 30 -> StreakBadgeTier(
            badgeEmoji = "🥇",
            title = if (isEn) "Gold Consistent Warrior" else "Pejuang Konsisten (Lv.3)",
            primaryColor = Color(0xFFE11D48),
            secondaryColor = Color(0xFFFDA4AF),
            nextTargetDays = 31
        )
        streakDays <= 60 -> StreakBadgeTier(
            badgeEmoji = "💎",
            title = if (isEn) "Diamond Discipline Master" else "Master Disiplin (Lv.4)",
            primaryColor = Color(0xFF7C3AED),
            secondaryColor = Color(0xFFDDD6FE),
            nextTargetDays = 61
        )
        streakDays <= 100 -> StreakBadgeTier(
            badgeEmoji = "👑",
            title = if (isEn) "Crown Scholar Grandmaster" else "Grandmaster Pelajar (Lv.5)",
            primaryColor = Color(0xFF059669),
            secondaryColor = Color(0xFF6EE7B7),
            nextTargetDays = 101
        )
        else -> StreakBadgeTier(
            badgeEmoji = "🔥",
            title = if (isEn) "Blue Flame Legend" else "Legenda Api Biru (MAX)",
            primaryColor = activeBlue.primaryColor,
            secondaryColor = activeBlue.secondaryColor,
            nextTargetDays = streakDays
        )
    }

    val rawTierProgress = if (!isFlameLit) {
        0f
    } else if (streakDays > 100) {
        1f
    } else {
        (streakDays.toFloat() / badgeTier.nextTargetDays.coerceAtLeast(1)).coerceIn(0f, 1f)
    }

    val tierProgress by animateFloatAsState(
        targetValue = rawTierProgress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animatedTierProgress"
    )

    val flameSize = if (!isFlameLit) 52.dp else (54 + (streakDays.coerceAtMost(120) / 5)).dp

    val trackRecords = remember(completedSessions, isEn) {
        calculatePeriodTrackRecords(completedSessions, isEn)
    }

    if (editingItem != null) {
        AlertDialog(
            onDismissRequest = { editingItem = null },
            shape = RoundedCornerShape(28.dp),
            containerColor = themeSpec.cardSurface,
            title = {
                Text(
                    text = if (isEn) "Edit Daily Commitment" else "Edit Komitmen Harian",
                    color = themeSpec.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = editingText,
                    onValueChange = { editingText = it },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = cozyTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val current = editingItem
                        if (current != null && editingText.trim().isNotEmpty()) {
                            onEditCommitment(current, editingText.trim())
                        }
                        editingItem = null
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                ) { Text(if (isEn) "Save" else "Simpan", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { editingItem = null }) {
                    Text(if (isEn) "Cancel" else "Batal", color = themeSpec.textSecondary)
                }
            }
        )
    }

    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = themeSpec.cardSurface,
            title = {
                Text(
                    text = if (isEn) "Add Exam Subject" else "Tambah Mata Pelajaran",
                    fontWeight = FontWeight.Black,
                    color = themeSpec.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isEn) {
                            "This subject will automatically sync between Daily Tests and Midterm (UTS)."
                        } else {
                            "Mata pelajaran ini otomatis tersinkronisasi antara Ulangan Harian dan UTS."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = themeSpec.textSecondary
                    )
                    OutlinedTextField(
                        value = newGradeSubjectInput,
                        onValueChange = { newGradeSubjectInput = it },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = cozyTextFieldColors,
                        placeholder = {
                            Text(
                                text = if (isEn) "e.g. Mathematics, Physics" else "Contoh: Matematika, Pemrograman C++",
                                color = themeSpec.textSecondary.copy(alpha = 0.65f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = newGradeSubjectInput.trim()
                        if (clean.isNotEmpty()) {
                            onAddGradeSubject(clean)
                            selectedGradeSubject = clean
                            newGradeSubjectInput = ""
                            showAddSubjectDialog = false
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                ) {
                    Text(if (isEn) "Add Subject" else "Tambah Mapel", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text(if (isEn) "Cancel" else "Batal", color = themeSpec.textSecondary)
                }
            }
        )
    }

    if (showAddScoreDialog && effectiveSelectedSubject.isNotBlank()) {
        val examTypeLabel = if (gradeSubTab == 0) {
            if (isEn) "Daily Test" else "Ulangan Harian"
        } else {
            if (isEn) "Midterm (UTS)" else "UTS"
        }

        AlertDialog(
            onDismissRequest = { showAddScoreDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = themeSpec.cardSurface,
            title = {
                Text(
                    text = "$examTypeLabel • $effectiveSelectedSubject",
                    fontWeight = FontWeight.Black,
                    color = themeSpec.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = scoreLabelInput,
                        onValueChange = { scoreLabelInput = it },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = cozyTextFieldColors,
                        label = { Text(if (isEn) "Test Name / Chapter" else "Keterangan / Bab") },
                        placeholder = {
                            Text(
                                text = if (gradeSubTab == 0) "Contoh: UH 1 - Aljabar" else "Contoh: UTS Semester 1",
                                color = themeSpec.textSecondary.copy(alpha = 0.65f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = scoreValueInput,
                        onValueChange = { scoreValueInput = it },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = cozyTextFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        label = { Text(if (isEn) "Score (0 - 100)" else "Nilai (0 - 100)") },
                        placeholder = {
                            Text("Contoh: 88", color = themeSpec.textSecondary.copy(alpha = 0.65f))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = scoreValueInput.replace(",", ".").toDoubleOrNull()
                        if (parsed != null && parsed in 0.0..100.0) {
                            val fb = if (gradeSubTab == 0) {
                                onAddDailyScore(effectiveSelectedSubject, scoreLabelInput, parsed)
                            } else {
                                onAddMidtermScore(effectiveSelectedSubject, scoreLabelInput, parsed)
                            }
                            activeFeedbackBanner = fb
                            scoreLabelInput = ""
                            scoreValueInput = ""
                            showAddScoreDialog = false
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                ) {
                    Text(if (isEn) "Save Score" else "Simpan Nilai", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddScoreDialog = false }) {
                    Text(if (isEn) "Cancel" else "Batal", color = themeSpec.textSecondary)
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = themeSpec.cardSurface),
            border = BorderStroke(
                width = 1.dp,
                color = if (isFlameLit) badgeTier.primaryColor.copy(alpha = 0.35f) else themeSpec.cardBorder
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .graphicsLayer {
                                scaleX = flameBounceScale
                                scaleY = flameBounceScale
                            }
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        badgeTier.primaryColor.copy(alpha = 0.22f),
                                        badgeTier.secondaryColor.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .clickable {
                                FrebelSoundManager.playClick(soundEnabled)
                                flameBoostActive = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        FrebelFlameGraphic(
                            primaryColor = badgeTier.primaryColor,
                            secondaryColor = badgeTier.secondaryColor,
                            isAnimated = isFlameLit || flameBoostActive,
                            modifier = Modifier.size(flameSize)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = badgeTier.primaryColor.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = "${badgeTier.badgeEmoji} ${badgeTier.title}",
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = badgeTier.primaryColor
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = if (isEn) "$streakDays Day Streak" else "$streakDays Hari Beruntun",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = if (isFlameLit) badgeTier.primaryColor else themeSpec.textPrimary
                        )
                        Text(
                            text = if (isEn) {
                                "Best record: $bestStreakDays days • Tap flame"
                            } else {
                                "Rekor terbaik: $bestStreakDays hari • Ketuk api"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = themeSpec.textSecondary
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "Next Badge Upgrade" else "Progres Upgrade Badge",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.textSecondary
                        )
                        Text(
                            text = if (streakDays > 100) "MAX" else "$streakDays / ${badgeTier.nextTargetDays} hari",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeTier.primaryColor
                        )
                    }
                    LinearProgressIndicator(
                        progress = { tierProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(CircleShape),
                        color = badgeTier.primaryColor,
                        trackColor = badgeTier.primaryColor.copy(alpha = 0.15f)
                    )
                }

                val statusBg = if (isFlameLit) {
                    if (themeSpec.isDark) Color(0xFF14532D).copy(alpha = 0.45f) else Color(0xFFDCFCE7)
                } else {
                    if (themeSpec.isDark) Color(0xFF78350F).copy(alpha = 0.40f) else Color(0xFFFEF3C7)
                }
                val statusTextCol = if (isFlameLit) {
                    if (themeSpec.isDark) Color(0xFF86EFAC) else Color(0xFF14532D)
                } else {
                    if (themeSpec.isDark) Color(0xFFFDE68A) else Color(0xFF78350F)
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = statusBg
                ) {
                    Text(
                        text = if (isFlameLit) {
                            if (isEn) {
                                "Active! Today's study session lit up your streak badge automatically."
                            } else {
                                "Menyala! Sesi belajarmu hari ini otomatis menyalakan dan menaikkan level badge streak."
                            }
                        } else {
                            if (isEn) {
                                "Auto-rule: Complete at least 1 study session today to ignite your streak."
                            } else {
                                "Syarat otomatis: Selesaikan minimal 1 sesi belajar hari ini agar badge streak menyala."
                            }
                        },
                        modifier = Modifier.padding(13.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = statusTextCol
                    )
                }

                if (streakDays > 100) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DefaultFrebelBluePalettes.forEachIndexed { index, palette ->
                            val selected = index == selectedBluePaletteIndex
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .clickable { onSelectBluePalette(index) },
                                shape = CircleShape,
                                color = if (selected) palette.primaryColor else palette.primaryColor.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = palette.name.substringBefore(" "),
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else palette.primaryColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Text(
                    text = if (isEn) "Daily Streak Commitments" else "Komitmen Menjaga Streak",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = themeSpec.textPrimary
                )

                commitments.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(themeSpec.primaryAccent.copy(alpha = 0.07f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = { onToggleCommitment(item.id, it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = themeSpec.primaryAccent,
                                uncheckedColor = themeSpec.textSecondary
                            )
                        )
                        Text(
                            text = item.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeSpec.textPrimary,
                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = {
                                editingItem = item
                                editingText = item.title
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = themeSpec.textSecondary, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { onDeleteCommitment(item.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCommitmentText,
                        onValueChange = { newCommitmentText = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = cozyTextFieldColors,
                        placeholder = {
                            Text(
                                text = if (isEn) "Add commitment..." else "Tambah komitmen harian...",
                                style = MaterialTheme.typography.bodySmall,
                                color = themeSpec.textSecondary.copy(alpha = 0.65f)
                            )
                        }
                    )
                    IconButton(
                        onClick = {
                            val trimmed = newCommitmentText.trim()
                            if (trimmed.isNotEmpty()) {
                                onAddCommitment(trimmed)
                                newCommitmentText = ""
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(themeSpec.primaryAccent)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
                    }
                }

                FrebelGlowyButton(
                    text = if (isFlameLit) {
                        if (isEn) "Start Another Study Session" else "Tambah Sesi Belajar Lagi"
                    } else {
                        if (isEn) "Study Now to Light Streak" else "Mulai Belajar untuk Nyalakan Streak"
                    },
                    icon = if (isFlameLit) Icons.Default.Check else Icons.Default.PlayArrow,
                    gradientColors = if (isFlameLit) {
                        listOf(themeSpec.primaryAccent, themeSpec.secondaryAccent)
                    } else {
                        listOf(Color(0xFFEA580C), Color(0xFFF59E0B))
                    },
                    glowColor = if (isFlameLit) themeSpec.primaryAccent.copy(alpha = 0.45f) else Color(0xFFF97316).copy(alpha = 0.45f),
                    onClick = onSubmitStudyComplete
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = themeSpec.cardSurface),
            border = BorderStroke(1.dp, themeSpec.cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = themeSpec.primaryAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEn) "Academic Grade Tracker" else "Track Nilai Ulangan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = themeSpec.textPrimary
                        )
                        Text(
                            text = if (isEn) {
                                "Synced subjects across Daily Tests & UTS"
                            } else {
                                "Mapel otomatis sinkron antara Ulangan Harian & UTS"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = themeSpec.textSecondary
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                FrebelSoundManager.playClick(soundEnabled)
                                showAddSubjectDialog = true
                            },
                        shape = CircleShape,
                        color = themeSpec.primaryAccent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text(
                                text = if (isEn) "Subject" else "Mapel",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(themeSpec.primaryAccent.copy(alpha = 0.09f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        0 to (if (isEn) "Daily Tests (UH)" else "Ulangan Harian"),
                        1 to (if (isEn) "Midterm (UTS)" else "Ulangan Tengah Semester")
                    ).forEach { (tabIdx, title) ->
                        val selected = gradeSubTab == tabIdx
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .clickable {
                                    FrebelSoundManager.playClick(soundEnabled)
                                    gradeSubTab = tabIdx
                                },
                            shape = CircleShape,
                            color = if (selected) themeSpec.primaryAccent else Color.Transparent
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(vertical = 9.dp, horizontal = 6.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (selected) Color.White else themeSpec.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "90-100" to Color(0xFF0284C7),
                        "80-89" to Color(0xFF16A34A),
                        "70-79" to Color(0xFFD97706),
                        "<70" to Color(0xFFDC2626)
                    ).forEach { (range, color) ->
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = CircleShape,
                            color = color.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = range,
                                modifier = Modifier.padding(vertical = 4.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = color
                            )
                        }
                    }
                }

                if (activeFeedbackBanner.isNotBlank()) {
                    val isDropWarning = "TURUN" in activeFeedbackBanner || "Perhatian" in activeFeedbackBanner
                    val bannerBg = if (isDropWarning) {
                        if (themeSpec.isDark) Color(0xFF7F1D1D).copy(alpha = 0.45f) else Color(0xFFFEF2F2)
                    } else {
                        if (themeSpec.isDark) Color(0xFF064E3B).copy(alpha = 0.45f) else Color(0xFFECFDF5)
                    }
                    val bannerTitleCol = if (isDropWarning) {
                        if (themeSpec.isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
                    } else {
                        if (themeSpec.isDark) Color(0xFF6EE7B7) else Color(0xFF047857)
                    }
                    val bannerBodyCol = if (isDropWarning) {
                        if (themeSpec.isDark) Color(0xFFFECACA) else Color(0xFF7F1D1D)
                    } else {
                        if (themeSpec.isDark) Color(0xFFA7F3D0) else Color(0xFF065F46)
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = bannerBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(13.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isDropWarning) {
                                    "⚠️ Koreksi & Solusi Belajar"
                                } else {
                                    "🏆 Validasi Pencapaian"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = bannerTitleCol
                            )
                            Text(
                                text = activeFeedbackBanner,
                                style = MaterialTheme.typography.bodySmall,
                                color = bannerBodyCol
                            )
                        }
                    }
                }

                if (gradeTracks.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = themeSpec.primaryAccent.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isEn) {
                                "No subjects added yet. Tap '+ Subject' to start tracking Daily Tests and UTS scores."
                            } else {
                                "Belum ada mata pelajaran. Tekan '+ Mapel' untuk mulai mencatat nilai Ulangan Harian & UTS."
                            },
                            modifier = Modifier.padding(13.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = themeSpec.textSecondary
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gradeTracks.forEach { track ->
                            val isSelected = track.subjectName == effectiveSelectedSubject
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        FrebelSoundManager.playClick(soundEnabled)
                                        selectedGradeSubject = track.subjectName
                                    },
                                shape = CircleShape,
                                color = if (isSelected) themeSpec.secondaryAccent else themeSpec.secondaryAccent.copy(alpha = 0.14f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 13.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = track.subjectName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else themeSpec.textPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus Mapel",
                                        tint = if (isSelected) Color.White.copy(alpha = 0.85f) else themeSpec.textSecondary,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .clickable { onDeleteGradeSubject(track.subjectName) }
                                    )
                                }
                            }
                        }
                    }

                    val currentTrack = gradeTracks.firstOrNull { it.subjectName == effectiveSelectedSubject }
                    if (currentTrack != null) {
                        val activeScores: List<GradeScoreRecord> = if (gradeSubTab == 0) {
                            currentTrack.dailyScores
                        } else {
                            currentTrack.midtermScores
                        }

                        val avgScore = if (activeScores.isEmpty()) 0.0 else activeScores.map { it.score }.average()
                        val avgColor = scoreTierColor(avgScore)

                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = avgColor.copy(alpha = 0.10f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(13.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${currentTrack.subjectName} • ${if (gradeSubTab == 0) "Ulangan Harian" else "UTS"}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = themeSpec.textPrimary
                                    )
                                    val trendText = buildTrendSummary(activeScores, isEn)
                                    Text(
                                        text = trendText,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = avgColor
                                    )
                                }

                                Button(
                                    onClick = {
                                        FrebelSoundManager.playClick(soundEnabled)
                                        showAddScoreDialog = true
                                    },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = avgColor)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEn) "Score" else "Nilai",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (activeScores.isEmpty()) {
                            Text(
                                text = if (isEn) {
                                    "No scores recorded yet for this section."
                                } else {
                                    "Belum ada nilai di bagian ini. Tekan '+ Nilai' untuk memasukkan skor."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = themeSpec.textSecondary
                            )
                        } else {
                            activeScores.forEachIndexed { idx, rec ->
                                val itemColor = scoreTierColor(rec.score)
                                val prevScore = if (idx > 0) activeScores[idx - 1].score else null
                                val deltaBadge = when {
                                    prevScore == null -> "Awal"
                                    rec.score > prevScore -> "▲ +${formatScoreNumber(rec.score - prevScore)}"
                                    rec.score < prevScore -> "▼ -${formatScoreNumber(prevScore - rec.score)}"
                                    else -> "● Stabil"
                                }
                                val deltaColor = when {
                                    prevScore == null -> themeSpec.textSecondary
                                    rec.score > prevScore -> Color(0xFF16A34A)
                                    rec.score < prevScore -> Color(0xFFDC2626)
                                    else -> Color(0xFFD97706)
                                }

                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = itemColor.copy(alpha = 0.09f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = rec.label,
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = themeSpec.textPrimary
                                        )

                                        Surface(
                                            shape = CircleShape,
                                            color = deltaColor.copy(alpha = 0.14f)
                                        ) {
                                            Text(
                                                text = deltaBadge,
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = deltaColor
                                            )
                                        }

                                        Surface(
                                            shape = CircleShape,
                                            color = itemColor
                                        ) {
                                            Text(
                                                text = formatScoreNumber(rec.score),
                                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = themeSpec.cardSurface),
            border = BorderStroke(1.dp, themeSpec.cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isEn) "Study Hours Track Record" else "Track Record Jam Belajar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeSpec.textPrimary
                )
                Text(
                    text = if (isEn) {
                        "Weekly, monthly & semester totals with highest/lowest daily peaks"
                    } else {
                        "Rekap durasi belajar beserta rekor harian tertinggi & terendah"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = themeSpec.textSecondary
                )

                trackRecords.forEach { record ->
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = record.accentColor.copy(alpha = 0.09f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = record.periodTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = record.accentColor
                                    )
                                    Text(
                                        text = record.periodSubtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = themeSpec.textSecondary
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = record.accentColor
                                ) {
                                    Text(
                                        text = formatMinutesToHours(record.totalMinutes),
                                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }

                            HorizontalDivider(color = record.accentColor.copy(alpha = 0.16f))

                            val highBg = if (themeSpec.isDark) Color(0xFF14532D).copy(alpha = 0.45f) else Color(0xFFDCFCE7)
                            val highCol = if (themeSpec.isDark) Color(0xFF86EFAC) else Color(0xFF15803D)
                            val lowBg = if (themeSpec.isDark) Color(0xFF78350F).copy(alpha = 0.40f) else Color(0xFFFEF3C7)
                            val lowCol = if (themeSpec.isDark) Color(0xFFFDE68A) else Color(0xFFB45309)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    color = highBg
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp)) {
                                        Text(
                                            text = if (isEn) "▲ Highest" else "▲ Tertinggi",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = highCol
                                        )
                                        Text(
                                            text = "${formatMinutesToHours(record.highestMinutes)} /hari",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = highCol
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    color = lowBg
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp)) {
                                        Text(
                                            text = if (isEn) "▼ Lowest" else "▼ Terendah",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = lowCol
                                        )
                                        Text(
                                            text = "${formatMinutesToHours(record.lowestMinutes)} /hari",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = lowCol
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun scoreTierColor(score: Double): Color {
    return when {
        score >= 90.0 -> Color(0xFF0284C7)
        score >= 80.0 -> Color(0xFF16A34A)
        score >= 70.0 -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }
}

private fun formatScoreNumber(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }
}

private fun buildTrendSummary(scores: List<GradeScoreRecord>, isEn: Boolean): String {
    if (scores.isEmpty()) {
        return if (isEn) "Average: - (No scores)" else "Rata-rata: - (Belum ada nilai)"
    }
    val avg = formatScoreNumber(scores.map { it.score }.average())
    if (scores.size == 1) {
        return if (isEn) "Average: $avg (1 score)" else "Rata-rata: $avg (1 nilai)"
    }
    val last = scores.last().score
    val prev = scores[scores.size - 2].score
    val trend = when {
        last > prev -> if (isEn) "▲ Rising (+${formatScoreNumber(last - prev)})" else "▲ Naik (+${formatScoreNumber(last - prev)})"
        last < prev -> if (isEn) "▼ Dropping (-${formatScoreNumber(prev - last)})" else "▼ Turun (-${formatScoreNumber(prev - last)})"
        else -> if (isEn) "● Steady" else "● Stabil"
    }
    return if (isEn) "Avg: $avg • $trend" else "Rata-rata: $avg • $trend"
}

private fun calculatePeriodTrackRecords(
    sessions: List<CompletedStudySession>,
    isEn: Boolean
): List<PeriodTrackSummary> {
    val now = System.currentTimeMillis()
    val dayMillis = 24L * 60L * 60L * 1000L

    fun buildSummary(
        title: String,
        subtitle: String,
        daysBack: Int,
        color: Color
    ): PeriodTrackSummary {
        val cutoff = now - (daysBack * dayMillis)
        val filtered = sessions.filter { it.timestampMillis >= cutoff }
        val total = filtered.sumOf { it.durationMinutes }

        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dailyTotals = filtered
            .groupBy { fmt.format(Date(it.timestampMillis)) }
            .values
            .map { daySessions -> daySessions.sumOf { it.durationMinutes } }

        val highest = dailyTotals.maxOrNull() ?: 0
        val lowest = if (dailyTotals.size >= 2) (dailyTotals.minOrNull() ?: 0) else 0

        return PeriodTrackSummary(
            periodTitle = title,
            periodSubtitle = subtitle,
            totalMinutes = total,
            highestMinutes = highest,
            lowestMinutes = lowest,
            accentColor = color
        )
    }

    return listOf(
        buildSummary(
            title = if (isEn) "Weekly" else "Per Minggu",
            subtitle = if (isEn) "Last 7 days" else "7 hari terakhir",
            daysBack = 7,
            color = Color(0xFF0284C7)
        ),
        buildSummary(
            title = if (isEn) "Monthly" else "Per Bulan",
            subtitle = if (isEn) "Last 30 days" else "30 hari terakhir",
            daysBack = 30,
            color = Color(0xFF7C3AED)
        ),
        buildSummary(
            title = if (isEn) "Semester" else "Per Semester",
            subtitle = if (isEn) "Last 6 months (180 days)" else "6 bulan (180 hari) terakhir",
            daysBack = 180,
            color = Color(0xFFEA580C)
        )
    )
}

private fun formatMinutesToHours(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}j ${minutes}m" else "${minutes}m"
}

@Composable
fun FrebelFlameGraphic(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier,
    isAnimated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "frebelAnimatedFlame")

    val swayPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flameSway"
    )

    val flickerScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameFlicker"
    )

    val emberRise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flameEmbers"
    )

    val sway = if (isAnimated) sin(swayPhase) else 0f
    val counterSway = if (isAnimated) sin(swayPhase + 1.35f) else 0f
    val pulse = if (isAnimated) flickerScale else 1f

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        if (isAnimated) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.34f * pulse),
                        secondaryColor.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.50f, h * 0.58f),
                    radius = w * 0.54f
                ),
                radius = w * 0.54f,
                center = Offset(w * 0.50f, h * 0.58f)
            )
        }

        val tipX = w * (0.50f + sway * 0.085f)
        val tipY = h * (0.06f + (1f - pulse) * 0.08f)

        val outerFlame = Path().apply {
            moveTo(tipX, tipY)
            cubicTo(
                w * (0.76f + sway * 0.04f), h * 0.28f,
                w * 0.90f, h * 0.62f,
                w * 0.72f, h * 0.85f
            )
            cubicTo(
                w * 0.60f, h * 0.96f,
                w * 0.40f, h * 0.96f,
                w * 0.28f, h * 0.85f
            )
            cubicTo(
                w * 0.10f, h * 0.62f,
                w * (0.24f + sway * 0.04f), h * 0.28f,
                tipX, tipY
            )
            close()
        }

        drawPath(
            path = outerFlame,
            brush = Brush.verticalGradient(
                colors = listOf(
                    secondaryColor,
                    primaryColor,
                    primaryColor.copy(alpha = 0.92f)
                ),
                startY = tipY,
                endY = h * 0.95f
            )
        )

        val midTipX = w * (0.50f + counterSway * 0.07f)
        val midTipY = h * (0.25f + (1f - pulse) * 0.06f)

        val midFlame = Path().apply {
            moveTo(midTipX, midTipY)
            cubicTo(
                w * (0.70f + counterSway * 0.03f), h * 0.44f,
                w * 0.78f, h * 0.68f,
                w * 0.64f, h * 0.85f
            )
            cubicTo(
                w * 0.55f, h * 0.92f,
                w * 0.45f, h * 0.92f,
                w * 0.36f, h * 0.85f
            )
            cubicTo(
                w * 0.22f, h * 0.68f,
                w * (0.30f + counterSway * 0.03f), h * 0.44f,
                midTipX, midTipY
            )
            close()
        }

        drawPath(
            path = midFlame,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.55f),
                    secondaryColor,
                    primaryColor.copy(alpha = 0.82f)
                ),
                startY = midTipY,
                endY = h * 0.92f
            )
        )

        val coreTipX = w * (0.50f + sway * 0.035f)
        val coreTipY = h * 0.46f

        val innerCore = Path().apply {
            moveTo(coreTipX, coreTipY)
            cubicTo(
                w * 0.62f, h * 0.60f,
                w * 0.65f, h * 0.77f,
                w * 0.50f, h * 0.88f
            )
            cubicTo(
                w * 0.35f, h * 0.77f,
                w * 0.38f, h * 0.60f,
                coreTipX, coreTipY
            )
            close()
        }

        drawPath(
            path = innerCore,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (isAnimated) 0.92f else 0.55f),
                    secondaryColor.copy(alpha = 0.90f),
                    secondaryColor.copy(alpha = 0.35f)
                ),
                startY = coreTipY,
                endY = h * 0.88f
            )
        )

        if (isAnimated) {
            val sparkAlpha = (1f - emberRise).coerceIn(0f, 1f) * 0.85f
            val spark1X = w * (0.34f + sway * 0.08f)
            val spark1Y = h * (0.28f - emberRise * 0.22f)
            drawCircle(
                color = secondaryColor.copy(alpha = sparkAlpha),
                radius = w * 0.032f,
                center = Offset(spark1X, spark1Y)
            )

            val spark2Phase = (emberRise + 0.45f) % 1f
            val spark2Alpha = (1f - spark2Phase).coerceIn(0f, 1f) * 0.80f
            val spark2X = w * (0.66f + counterSway * 0.08f)
            val spark2Y = h * (0.32f - spark2Phase * 0.24f)
            drawCircle(
                color = Color.White.copy(alpha = spark2Alpha),
                radius = w * 0.025f,
                center = Offset(spark2X, spark2Y)
            )
        }
    }
}

@Composable
fun FrebelGlowyButton(
    text: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    glowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "glowyBtnScale"
    )

    val shimmerTransition = rememberInfiniteTransition(label = "glowyShimmer")
    val shimmerShift by shimmerTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .clip(CircleShape)
            .background(Brush.horizontalGradient(gradientColors))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 14.dp, horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val sweepX = size.width * shimmerShift
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(sweepX, size.height * 0.5f),
                    radius = size.width * 0.32f
                ),
                radius = size.width * 0.32f,
                center = Offset(sweepX, size.height * 0.5f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}