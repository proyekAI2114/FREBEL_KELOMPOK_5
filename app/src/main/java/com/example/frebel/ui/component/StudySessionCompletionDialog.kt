package com.example.frebel.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSubTheme
import com.example.frebel.ui.theme.FrebelThemeSpec
import com.example.frebel.ui.theme.resolveFrebelThemeSpec
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CompletedStudySession(
    val id: String,
    val subject: String,
    val durationMinutes: Int,
    val focusRating: Int,
    val note: String = "",
    val photoUri: Uri? = null,
    val modeLabel: String = "Stopwatch",
    val timestampMillis: Long = System.currentTimeMillis()
)

@Composable
fun StudySessionCompletionDialog(
    subjectName: String,
    durationMinutes: Int,
    isPomodoroMode: Boolean,
    onDismiss: () -> Unit,
    onConfirmSave: (focusRating: Int, note: String, photoUri: Uri?) -> Unit,
    themeSpec: FrebelThemeSpec = resolveFrebelThemeSpec(false, FrebelSubTheme.SOLID),
    language: FrebelLanguage = FrebelLanguage.INDONESIAN
) {
    val isEn = language == FrebelLanguage.ENGLISH
    var focusRating by remember { mutableIntStateOf(5) }
    var noteInput by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) selectedPhotoUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            color = themeSpec.cardSurface,
            border = BorderStroke(1.dp, themeSpec.cardBorder),
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEn) "🎉 Session Complete!" else "🎉 Sesi Belajar Selesai!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = themeSpec.textPrimary
                        )
                        Text(
                            text = "$subjectName • $durationMinutes ${if (isEn) "min" else "menit"} (${if (isPomodoroMode) "Pomodoro" else "Stopwatch"})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.primaryAccent
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(themeSpec.primaryAccent.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = themeSpec.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = themeSpec.primaryAccent.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isEn) "How was your focus?" else "Seberapa fokus kamu tadi?",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.textPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            (1..5).forEach { star ->
                                val active = star <= focusRating
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating $star",
                                    tint = if (active) Color(0xFFF59E0B) else themeSpec.textSecondary.copy(alpha = 0.35f),
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .clickable { focusRating = star }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    minLines = 2,
                    maxLines = 3,
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
                            text = if (isEn) "What did you learn? (optional)" else "Apa yang kamu pelajari hari ini? (opsional)",
                            color = themeSpec.textSecondary.copy(alpha = 0.65f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = { photoPicker.launch(arrayOf("image/*")) },
                    shape = CircleShape,
                    border = BorderStroke(1.dp, themeSpec.primaryAccent.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = themeSpec.primaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedPhotoUri != null) {
                            if (isEn) "Change Study Photo" else "Ganti Foto Catatan / Belajar"
                        } else {
                            if (isEn) "Attach Study Photo (Optional)" else "Tambah Foto Bukti Belajar (Opsional)"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = themeSpec.primaryAccent
                    )
                }

                val currentPhoto = selectedPhotoUri
                if (currentPhoto != null) {
                    FrebelUriImagePreview(
                        uri = currentPhoto,
                        contentDescription = "Preview Foto Belajar",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )
                }

                FrebelTactileGameButton(
                    text = if (isEn) "Save & Ignite Streak" else "Simpan & Nyalakan Streak",
                    icon = Icons.Default.Check,
                    onClick = { onConfirmSave(focusRating, noteInput.trim(), selectedPhotoUri) },
                    gradientColors = listOf(themeSpec.primaryAccent, themeSpec.secondaryAccent),
                    bevelDarkColor = themeSpec.primaryAccent.copy(alpha = 0.8f),
                    glowColor = themeSpec.primaryAccent.copy(alpha = 0.4f),
                    height = 50.dp,
                    bevelDepth = 4.dp
                )
            }
        }
    }
}

@Composable
fun StudySessionHistoryCard(
    sessions: List<CompletedStudySession>,
    onDeleteSession: (String) -> Unit,
    themeSpec: FrebelThemeSpec = resolveFrebelThemeSpec(false, FrebelSubTheme.SOLID),
    language: FrebelLanguage = FrebelLanguage.INDONESIAN,
    modifier: Modifier = Modifier
) {
    val isEn = language == FrebelLanguage.ENGLISH
    val formatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isEn) "Completed Sessions & Photo Archive" else "Arsip Sesi Belajar & Foto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = themeSpec.textPrimary
            )

            if (sessions.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = themeSpec.primaryAccent.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEn) {
                            "No completed study sessions archived yet."
                        } else {
                            "Belum ada riwayat sesi belajar yang tersimpan."
                        },
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = themeSpec.textSecondary
                    )
                }
            } else {
                sessions.forEach { session ->
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = themeSpec.primaryAccent.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = themeSpec.primaryAccent
                                ) {
                                    Text(
                                        text = session.subject,
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = themeSpec.secondaryAccent.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = "${session.durationMinutes}m • ${session.modeLabel}",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = themeSpec.secondaryAccent
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(
                                    onClick = { onDeleteSession(session.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus Sesi",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = "★".repeat(session.focusRating.coerceIn(1, 5)) +
                                        "  •  " + formatter.format(Date(session.timestampMillis)),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = themeSpec.textSecondary
                            )

                            if (session.note.isNotBlank()) {
                                Text(
                                    text = session.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = themeSpec.textPrimary
                                )
                            }

                            val sessionPhoto = session.photoUri
                            if (sessionPhoto != null) {
                                FrebelUriImagePreview(
                                    uri = sessionPhoto,
                                    contentDescription = session.subject,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}