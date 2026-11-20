package com.example.frebel.ui.component

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSoundManager
import com.example.frebel.ui.theme.FrebelSubTheme
import com.example.frebel.ui.theme.FrebelThemeSpec
import com.example.frebel.ui.theme.resolveFrebelThemeSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class StudyTargetItem(
    val id: String,
    val subjectLabel: String,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentHeaderCard(
    studentName: String,
    activeSubject: String,
    completedMinutesToday: Int,
    dailyTargetMinutes: Int,
    freezeQuota: Int,
    targetItems: List<String>,
    onToggleTarget: (String, Boolean) -> Unit,
    onAddTarget: (String) -> Unit,
    onEditTarget: (String, String) -> Unit,
    onDeleteTarget: (String) -> Unit,
    onStartStudySession: () -> Unit,
    profilePhotoUri: String? = null,
    wishlistText: String = "",
    wishlistPhotoUri: String? = null,
    themeSpec: FrebelThemeSpec = resolveFrebelThemeSpec(false, FrebelSubTheme.SOLID),
    language: FrebelLanguage = FrebelLanguage.INDONESIAN,
    soundEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {}
) {
    val isEn = language == FrebelLanguage.ENGLISH

    var showAddTargetDialog by remember { mutableStateOf(false) }
    var newTargetInput by remember { mutableStateOf("") }
    var editingTargetOriginal by remember { mutableStateOf<String?>(null) }
    var editingTargetInput by remember { mutableStateOf("") }
    var completedChipTitles by remember { mutableStateOf(setOf<String>()) }

    val profileBitmap = rememberUriImageBitmap(profilePhotoUri)
    val wishlistBitmap = rememberUriImageBitmap(wishlistPhotoUri)

    val rawProgress = (completedMinutesToday.toFloat() / dailyTargetMinutes.coerceAtLeast(1).toFloat())
        .coerceIn(0f, 1f)
    val animatedDailyProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        label = "studentHeaderDailyProgress"
    )
    val progressPercent = (rawProgress * 100f).toInt()

    val infiniteTransition = rememberInfiniteTransition(label = "studentAvatarPulse")
    val avatarHaloScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatarHaloScale"
    )

    val dialogFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = themeSpec.primaryAccent,
        unfocusedBorderColor = themeSpec.cardBorder,
        focusedTextColor = themeSpec.textPrimary,
        unfocusedTextColor = themeSpec.textPrimary,
        cursorColor = themeSpec.primaryAccent
    )

    if (showAddTargetDialog) {
        AlertDialog(
            onDismissRequest = { showAddTargetDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = themeSpec.cardSurface,
            title = {
                Text(
                    text = if (isEn) "Add Daily Study Target" else "Tambah Target Belajar",
                    fontWeight = FontWeight.Black,
                    color = themeSpec.textPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = newTargetInput,
                    onValueChange = { newTargetInput = it },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = dialogFieldColors,
                    placeholder = {
                        Text(
                            text = if (isEn) "e.g. Finish 2 Math chapters" else "Contoh: Selesaikan 2 bab Matematika",
                            color = themeSpec.textSecondary.copy(alpha = 0.65f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newTargetInput.trim()
                        if (trimmed.isNotEmpty()) {
                            FrebelSoundManager.playClick(soundEnabled)
                            onAddTarget(trimmed)
                            newTargetInput = ""
                        }
                        showAddTargetDialog = false
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                ) {
                    Text(if (isEn) "Add" else "Tambah", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTargetDialog = false }) {
                    Text(if (isEn) "Cancel" else "Batal", color = themeSpec.textSecondary)
                }
            }
        )
    }

    if (editingTargetOriginal != null) {
        AlertDialog(
            onDismissRequest = { editingTargetOriginal = null },
            shape = RoundedCornerShape(28.dp),
            containerColor = themeSpec.cardSurface,
            title = {
                Text(
                    text = if (isEn) "Edit Study Target" else "Edit Target Belajar",
                    fontWeight = FontWeight.Black,
                    color = themeSpec.textPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = editingTargetInput,
                    onValueChange = { editingTargetInput = it },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = dialogFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val original = editingTargetOriginal
                        val trimmed = editingTargetInput.trim()
                        if (original != null && trimmed.isNotEmpty()) {
                            FrebelSoundManager.playClick(soundEnabled)
                            onEditTarget(original, trimmed)
                        }
                        editingTargetOriginal = null
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                ) {
                    Text(if (isEn) "Save" else "Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTargetOriginal = null }) {
                    Text(if (isEn) "Cancel" else "Batal", color = themeSpec.textSecondary)
                }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = themeSpec.cardSurface),
        border = BorderStroke(1.2.dp, themeSpec.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            themeSpec.primaryAccent.copy(alpha = 0.11f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .graphicsLayer {
                            scaleX = avatarHaloScale
                            scaleY = avatarHaloScale
                        }
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(themeSpec.primaryAccent, themeSpec.secondaryAccent)
                            )
                        )
                        .padding(3.dp)
                        .clickable {
                            FrebelSoundManager.playClick(soundEnabled)
                            onOpenSettings()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(themeSpec.cardSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap,
                                contentDescription = studentName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val initial = studentName.trim().firstOrNull()?.uppercase() ?: "F"
                            Text(
                                text = initial,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = themeSpec.primaryAccent
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isEn) "Welcome back," else "Semangat belajar,",
                        style = MaterialTheme.typography.labelMedium,
                        color = themeSpec.textSecondary
                    )
                    Text(
                        text = studentName.ifBlank { if (isEn) "Student" else "Pelajar FREBEL" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = themeSpec.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = themeSpec.primaryAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "📚 $activeSubject",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = themeSpec.primaryAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = themeSpec.secondaryAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "❄️ $freezeQuota",
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = themeSpec.secondaryAccent,
                                maxLines = 1
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        FrebelSoundManager.playClick(soundEnabled)
                        onOpenSettings()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(themeSpec.primaryAccent.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = if (isEn) "Settings" else "Pengaturan",
                        tint = themeSpec.primaryAccent,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = themeSpec.primaryAccent.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, themeSpec.primaryAccent.copy(alpha = 0.18f)),
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
                            text = if (isEn) "Daily Focus XP" else "Progres Target Harian",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = themeSpec.textPrimary
                        )
                        Surface(
                            shape = CircleShape,
                            color = themeSpec.primaryAccent
                        ) {
                            Text(
                                text = "$completedMinutesToday / ${dailyTargetMinutes}m ($progressPercent%)",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { animatedDailyProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = themeSpec.primaryAccent,
                        trackColor = themeSpec.primaryAccent.copy(alpha = 0.16f)
                    )
                }
            }

            if (wishlistText.isNotBlank() || wishlistBitmap != null) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = themeSpec.secondaryAccent.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, themeSpec.secondaryAccent.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (wishlistBitmap != null) {
                            Image(
                                bitmap = wishlistBitmap,
                                contentDescription = wishlistText,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        1.dp,
                                        themeSpec.secondaryAccent.copy(alpha = 0.4f),
                                        RoundedCornerShape(14.dp)
                                    )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(themeSpec.secondaryAccent.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = themeSpec.secondaryAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEn) "DREAM GOAL / WISHLIST" else "TARGET IMPIAN / WISHLIST",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = themeSpec.secondaryAccent
                            )
                            Text(
                                text = wishlistText.ifBlank {
                                    if (isEn) "Keep studying to unlock your goal!" else "Terus konsisten belajar untuk meraih impianmu!"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = themeSpec.textPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = themeSpec.cardBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEn) "Today's Quest Targets" else "Misi Belajar Hari Ini",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = themeSpec.textPrimary
                )

                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            FrebelSoundManager.playClick(soundEnabled)
                            showAddTargetDialog = true
                        },
                    shape = CircleShape,
                    color = themeSpec.primaryAccent.copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = themeSpec.primaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isEn) "Target" else "Target",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.primaryAccent
                        )
                    }
                }
            }

            if (targetItems.isEmpty()) {
                Text(
                    text = if (isEn) {
                        "No study targets yet. Tap '+ Target' to set today's mission!"
                    } else {
                        "Belum ada misi belajar hari ini. Tekan '+ Target' untuk menambahkan."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = themeSpec.textSecondary
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    targetItems.forEach { targetTitle ->
                        val isChecked = targetTitle in completedChipTitles
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    FrebelSoundManager.playClick(soundEnabled)
                                    val nextChecked = !isChecked
                                    completedChipTitles = if (nextChecked) {
                                        completedChipTitles + targetTitle
                                    } else {
                                        completedChipTitles - targetTitle
                                    }
                                    onToggleTarget(targetTitle, nextChecked)
                                },
                            shape = CircleShape,
                            color = if (isChecked) {
                                themeSpec.primaryAccent
                            } else {
                                themeSpec.primaryAccent.copy(alpha = 0.09f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = targetTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isChecked) Color.White else themeSpec.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = if (isChecked) Color.White.copy(alpha = 0.85f) else themeSpec.textSecondary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            FrebelSoundManager.playClick(soundEnabled)
                                            editingTargetOriginal = targetTitle
                                            editingTargetInput = targetTitle
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hapus",
                                    tint = if (isChecked) Color.White.copy(alpha = 0.85f) else themeSpec.textSecondary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            FrebelSoundManager.playClick(soundEnabled)
                                            onDeleteTarget(targetTitle)
                                        }
                                )
                            }
                        }
                    }
                }
            }

            FrebelTactileGameButton(
                text = if (isEn) "Start Study Session" else "Mulai Sesi Belajar",
                icon = Icons.Default.PlayArrow,
                primaryColor = themeSpec.primaryAccent,
                secondaryColor = themeSpec.secondaryAccent,
                soundEnabled = soundEnabled,
                onClick = {
                    FrebelSoundManager.playClick(soundEnabled)
                    onStartStudySession()
                }
            )
        }
    }
}

@Composable
private fun rememberUriImageBitmap(uriString: String?): ImageBitmap? {
    val context = LocalContext.current
    val bitmapState by produceState<ImageBitmap?>(initialValue = null, key1 = uriString) {
        value = if (uriString.isNullOrBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val uri = Uri.parse(uriString)
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
    return bitmapState
}