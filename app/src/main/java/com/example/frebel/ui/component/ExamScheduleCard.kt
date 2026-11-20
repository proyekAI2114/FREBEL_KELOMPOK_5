package com.example.frebel.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
import com.example.frebel.ui.data.ExamScheduleItem
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSoundManager
import com.example.frebel.ui.theme.FrebelThemeSpec

@Composable
fun ExamScheduleCard(
    exams: List<ExamScheduleItem>,
    subjectSuggestions: List<String>,
    themeSpec: FrebelThemeSpec,
    language: FrebelLanguage,
    soundEnabled: Boolean,
    onAddExam: (subject: String, dayName: String, dayOfMonth: Int, monthName: String) -> Unit,
    onToggleMarked: (String) -> Unit,
    onDeleteExam: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var subjectInput by remember { mutableStateOf("") }
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var selectedDateNum by remember { mutableIntStateOf(20) }
    var selectedMonthIndex by remember { mutableIntStateOf(10) } // November

    val isEn = language == FrebelLanguage.ENGLISH

    val dayOptions = remember(isEn) {
        if (isEn) {
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        } else {
            listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        }
    }

    val monthOptions = remember(isEn) {
        if (isEn) {
            listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        } else {
            listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = if (isEn) "📅 Add Exam Schedule" else "📅 Tambah Jadwal Ulangan",
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = subjectInput,
                        onValueChange = { subjectInput = it },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        label = { Text(if (isEn) "Subject Name" else "Mata Pelajaran") },
                        placeholder = { Text(if (isEn) "e.g. Mathematics" else "Contoh: Matematika / C++") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (subjectSuggestions.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            subjectSuggestions.forEach { suggestion ->
                                Surface(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            FrebelSoundManager.playClick(soundEnabled)
                                            subjectInput = suggestion
                                        },
                                    shape = CircleShape,
                                    color = themeSpec.primaryAccent.copy(alpha = 0.14f)
                                ) {
                                    Text(
                                        text = suggestion,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = themeSpec.primaryAccent
                                    )
                                }
                            }
                        }
                    }

                    // Pilih Hari
                    Text(
                        text = if (isEn) "Day" else "Pilih Hari",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        dayOptions.forEachIndexed { idx, day ->
                            val selected = idx == selectedDayIndex
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        FrebelSoundManager.playClick(soundEnabled)
                                        selectedDayIndex = idx
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) themeSpec.primaryAccent else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = day,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }

                    // Pilih Tanggal (1 - 31)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isEn) "Date" else "Tanggal",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        FrebelSoundManager.playClick(soundEnabled)
                                        selectedDateNum = if (selectedDateNum <= 1) 31 else selectedDateNum - 1
                                    },
                                shape = CircleShape,
                                color = themeSpec.primaryAccent.copy(alpha = 0.16f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("-", fontWeight = FontWeight.Black, color = themeSpec.primaryAccent)
                                }
                            }

                            Text(
                                text = selectedDateNum.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )

                            Surface(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        FrebelSoundManager.playClick(soundEnabled)
                                        selectedDateNum = if (selectedDateNum >= 31) 1 else selectedDateNum + 1
                                    },
                                shape = CircleShape,
                                color = themeSpec.primaryAccent.copy(alpha = 0.16f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("+", fontWeight = FontWeight.Black, color = themeSpec.primaryAccent)
                                }
                            }
                        }
                    }

                    // Pilih Bulan
                    Text(
                        text = if (isEn) "Month" else "Pilih Bulan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        monthOptions.forEachIndexed { idx, month ->
                            val selected = idx == selectedMonthIndex
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        FrebelSoundManager.playClick(soundEnabled)
                                        selectedMonthIndex = idx
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) themeSpec.secondaryAccent else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = month,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        FrebelSoundManager.playClick(soundEnabled)
                        val targetSubject = subjectInput.trim().ifEmpty {
                            subjectSuggestions.firstOrNull() ?: if (isEn) "General Exam" else "Ulangan Harian"
                        }
                        onAddExam(
                            targetSubject,
                            dayOptions[selectedDayIndex],
                            selectedDateNum,
                            monthOptions[selectedMonthIndex]
                        )
                        subjectInput = ""
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                ) {
                    Text(if (isEn) "Save Schedule" else "Simpan Jadwal", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    FrebelSoundManager.playClick(soundEnabled)
                    showAddDialog = false
                }) {
                    Text(if (isEn) "Cancel" else "Batal")
                }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = themeSpec.cardSurface),
        border = BorderStroke(1.5.dp, themeSpec.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = themeSpec.primaryAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEn) "Exam Schedule" else "Jadwal Ulangan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = themeSpec.textPrimary
                    )
                    Text(
                        text = if (isEn) {
                            "Mark exams once you're prepared"
                        } else {
                            "Tandai ulangan yang sudah siap atau selesai"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = themeSpec.textSecondary
                    )
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            FrebelSoundManager.playClick(soundEnabled)
                            showAddDialog = true
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = themeSpec.primaryAccent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Jadwal",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isEn) "Add" else "Jadwal",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            if (exams.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = themeSpec.primaryAccent.copy(alpha = 0.07f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEn) {
                            "No upcoming exams yet. Tap '+ Add' to pin your test dates."
                        } else {
                            "Belum ada jadwal ulangan. Tekan '+ Jadwal' untuk menandai tanggal ujianmu."
                        },
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = themeSpec.textSecondary
                    )
                }
            } else {
                exams.forEach { exam ->
                    val badgeColor = if (exam.isMarkedDone) Color(0xFF16A34A) else themeSpec.primaryAccent
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = badgeColor.copy(alpha = 0.10f),
                        border = BorderStroke(1.2.dp, badgeColor.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Kotak Tanggal & Bulan
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(badgeColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = exam.dayOfMonth.toString().padStart(2, '0'),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = exam.monthName.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }

                            // Mata Pelajaran & Hari
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exam.subject,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = themeSpec.textPrimary,
                                    textDecoration = if (exam.isMarkedDone) TextDecoration.LineThrough else TextDecoration.None,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${exam.dayName}, ${exam.dayOfMonth} ${exam.monthName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = themeSpec.textSecondary
                                )
                            }

                            // Tombol Tandai
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        FrebelSoundManager.playClick(soundEnabled)
                                        onToggleMarked(exam.id)
                                    },
                                shape = CircleShape,
                                color = if (exam.isMarkedDone) Color(0xFF16A34A) else themeSpec.secondaryAccent.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, if (exam.isMarkedDone) Color(0xFF16A34A) else themeSpec.secondaryAccent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Tandai",
                                        tint = if (exam.isMarkedDone) Color.White else themeSpec.secondaryAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (exam.isMarkedDone) {
                                            if (isEn) "Marked" else "Ditandai"
                                        } else {
                                            if (isEn) "Mark" else "Tandai"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (exam.isMarkedDone) Color.White else themeSpec.secondaryAccent
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    FrebelSoundManager.playClick(soundEnabled)
                                    onDeleteExam(exam.id)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}