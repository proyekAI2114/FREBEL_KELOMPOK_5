package com.example.frebel.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSubTheme
import com.example.frebel.ui.theme.FrebelThemeSpec
import com.example.frebel.ui.theme.resolveFrebelThemeSpec

data class StudyMotivationNote(
    val id: String,
    val content: String,
    val isApplied: Boolean,
    val autoFeedback: String = ""
)

@Composable
fun StudentFrequencyCard(
    weeklyHoursText: String,
    monthlyHoursText: String,
    peakFocusWindow: String,
    heatmapIntensities: List<Int>,
    notes: List<StudyMotivationNote>,
    onToggleNoteApplied: (String, Boolean) -> Unit,
    onAddNote: (String) -> Unit,
    onEditNote: (StudyMotivationNote, String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onOpenWeeklyReview: () -> Unit,
    themeSpec: FrebelThemeSpec = resolveFrebelThemeSpec(false, FrebelSubTheme.SOLID),
    language: FrebelLanguage = FrebelLanguage.INDONESIAN,
    modifier: Modifier = Modifier
) {
    var newNoteInput by remember { mutableStateOf("") }
    var editingNote by remember { mutableStateOf<StudyMotivationNote?>(null) }
    var editingNoteText by remember { mutableStateOf("") }

    val isEn = language == FrebelLanguage.ENGLISH
    val normalizedHeatmap = remember(heatmapIntensities) {
        if (heatmapIntensities.size >= 35) {
            heatmapIntensities.takeLast(35)
        } else {
            List(35 - heatmapIntensities.size) { 0 } + heatmapIntensities
        }
    }

    val cozyFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = themeSpec.primaryAccent,
        unfocusedBorderColor = themeSpec.cardBorder,
        focusedTextColor = themeSpec.textPrimary,
        unfocusedTextColor = themeSpec.textPrimary,
        cursorColor = themeSpec.primaryAccent
    )

    if (editingNote != null) {
        AlertDialog(
            onDismissRequest = { editingNote = null },
            shape = RoundedCornerShape(28.dp),
            containerColor = themeSpec.cardSurface,
            title = {
                Text(
                    text = if (isEn) "Edit Reflection Note" else "Edit Catatan Evaluasi",
                    fontWeight = FontWeight.Bold,
                    color = themeSpec.textPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = editingNoteText,
                    onValueChange = { editingNoteText = it },
                    minLines = 2,
                    shape = RoundedCornerShape(18.dp),
                    colors = cozyFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = editingNote
                        if (target != null && editingNoteText.trim().isNotEmpty()) {
                            onEditNote(target, editingNoteText.trim())
                        }
                        editingNote = null
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                ) {
                    Text(if (isEn) "Save" else "Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNote = null }) {
                    Text(if (isEn) "Cancel" else "Batal", color = themeSpec.textSecondary)
                }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = themeSpec.cardSurface),
        border = BorderStroke(1.dp, themeSpec.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column {
                Text(
                    text = if (isEn) "Study Frequency & Heatmap" else "Frekuensi Belajar & Heatmap",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = themeSpec.textPrimary
                )
                Text(
                    text = peakFocusWindow,
                    style = MaterialTheme.typography.bodySmall,
                    color = themeSpec.textSecondary
                )
            }

            // Ringkasan Durasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = themeSpec.primaryAccent.copy(alpha = 0.10f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isEn) "Weekly Total" else "Total Minggu Ini",
                            style = MaterialTheme.typography.labelSmall,
                            color = themeSpec.textSecondary
                        )
                        Text(
                            text = weeklyHoursText,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = themeSpec.primaryAccent
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = themeSpec.secondaryAccent.copy(alpha = 0.10f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isEn) "Monthly Total" else "Total Bulan Ini",
                            style = MaterialTheme.typography.labelSmall,
                            color = themeSpec.textSecondary
                        )
                        Text(
                            text = monthlyHoursText,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = themeSpec.secondaryAccent
                        )
                    }
                }
            }

            // Heatmap 35 Hari (5 Minggu) Mengikuti Warna Tema Aktif
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = themeSpec.primaryAccent.copy(alpha = 0.06f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "Last 35 Days Activity" else "Aktivitas 35 Hari Terakhir",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.textPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isEn) "Less" else "Rendah",
                                style = MaterialTheme.typography.labelSmall,
                                color = themeSpec.textSecondary
                            )
                            listOf(0.10f, 0.30f, 0.55f, 0.78f, 1.0f).forEach { alphaLevel ->
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(themeSpec.primaryAccent.copy(alpha = alphaLevel))
                                )
                            }
                            Text(
                                text = if (isEn) "More" else "Tinggi",
                                style = MaterialTheme.typography.labelSmall,
                                color = themeSpec.textSecondary
                            )
                        }
                    }

                    for (row in 0 until 5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (col in 0 until 7) {
                                val idx = row * 7 + col
                                val intensity = normalizedHeatmap[idx].coerceIn(0, 4)
                                val cellAlpha = when (intensity) {
                                    0 -> 0.10f
                                    1 -> 0.32f
                                    2 -> 0.55f
                                    3 -> 0.78f
                                    else -> 1.0f
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(themeSpec.primaryAccent.copy(alpha = cellAlpha))
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = themeSpec.cardBorder)

            // Catatan Evaluasi & Refleksi Mandiri + Feedback Otomatis
            Text(
                text = if (isEn) "Self-Evaluation & Reflection" else "Evaluasi & Refleksi Belajar",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = themeSpec.textPrimary
            )

            notes.forEach { note ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = themeSpec.primaryAccent.copy(alpha = 0.07f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = note.isApplied,
                                onCheckedChange = { onToggleNoteApplied(note.id, it) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = themeSpec.primaryAccent,
                                    uncheckedColor = themeSpec.textSecondary
                                )
                            )
                            Text(
                                text = note.content,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                color = themeSpec.textPrimary,
                                textDecoration = if (note.isApplied) TextDecoration.LineThrough else TextDecoration.None,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            IconButton(
                                onClick = {
                                    editingNote = note
                                    editingNoteText = note.content
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = themeSpec.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteNote(note.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (note.autoFeedback.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = themeSpec.secondaryAccent.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 6.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = "💡 ${note.autoFeedback}",
                                    modifier = Modifier.padding(10.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = themeSpec.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newNoteInput,
                    onValueChange = { newNoteInput = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = cozyFieldColors,
                    placeholder = {
                        Text(
                            text = if (isEn) "Write study reflection..." else "Tulis evaluasi belajarmu...",
                            style = MaterialTheme.typography.bodySmall,
                            color = themeSpec.textSecondary.copy(alpha = 0.65f)
                        )
                    }
                )
                IconButton(
                    onClick = {
                        val trimmed = newNoteInput.trim()
                        if (trimmed.isNotEmpty()) {
                            onAddNote(trimmed)
                            newNoteInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(themeSpec.primaryAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Evaluasi",
                        tint = Color.White
                    )
                }
            }

            FrebelTactileGameButton(
                text = if (isEn) "Check Weekly Summary" else "Cek Rekap Minggu Ini",
                icon = Icons.Default.CheckCircle,
                onClick = onOpenWeeklyReview,
                gradientColors = listOf(themeSpec.secondaryAccent, themeSpec.primaryAccent),
                bevelDarkColor = themeSpec.secondaryAccent.copy(alpha = 0.75f),
                glowColor = themeSpec.secondaryAccent.copy(alpha = 0.35f),
                height = 46.dp,
                bevelDepth = 3.dp
            )
        }
    }
}