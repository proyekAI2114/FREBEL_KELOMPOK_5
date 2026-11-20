package com.example.frebel.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.frebel.ui.data.DecoratedQuickNote
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSoundManager
import com.example.frebel.ui.theme.FrebelThemeSpec
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class NoteStationeryStyle(
    val name: String,
    val bgColor: Color,
    val borderColor: Color,
    val textColor: Color
)

private val NoteStationeryPalettes = listOf(
    NoteStationeryStyle("Sky Pastel", Color(0xFFE0F2FE), Color(0xFF38BDF8), Color(0xFF0C4A6E)),
    NoteStationeryStyle("Pink Love", Color(0xFFFFE4E6), Color(0xFFF472B6), Color(0xFF831843)),
    NoteStationeryStyle("Honey Bear", Color(0xFFFEF3C7), Color(0xFFF59E0B), Color(0xFF78350F)),
    NoteStationeryStyle("Fairy Lilac", Color(0xFFF3E8FF), Color(0xFFC084FC), Color(0xFF4C1D95)),
    NoteStationeryStyle("Y2K Cyber", Color(0xFF1E1B4B), Color(0xFF22D3EE), Color(0xFFF8FAFC)),
    NoteStationeryStyle("Vintage Paper", Color(0xFFF5EFE6), Color(0xFFA47148), Color(0xFF3D2B1F))
)

private val NoteStickers = listOf("✨", "💖", "🍪", "🧸", "🧚", "🌊", "💿", "📌", "🏆", "📚")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FrebelQuickNotesDialog(
    notes: List<DecoratedQuickNote>,
    language: FrebelLanguage,
    themeSpec: FrebelThemeSpec,
    soundEnabled: Boolean,
    onDismiss: () -> Unit,
    onAddNote: (title: String, content: String, styleIndex: Int, sticker: String, photoUri: Uri?) -> Unit,
    onDeleteNote: (String) -> Unit
) {
    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var selectedStyleIdx by remember { mutableIntStateOf(0) }
    var selectedSticker by remember { mutableStateOf("✨") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val isEn = language == FrebelLanguage.ENGLISH
    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) selectedPhotoUri = uri
    }

    val activePreviewPalette = NoteStationeryPalettes.getOrElse(selectedStyleIdx) {
        NoteStationeryPalettes.first()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp),
            shape = RoundedCornerShape(28.dp),
            color = if (themeSpec.isDark) Color(0xFF1E293B) else Color.White,
            border = BorderStroke(1.5.dp, themeSpec.primaryAccent.copy(alpha = 0.5f)),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEn) "📝 Decorated Notes" else "📝 Catatan Hias & Foto",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = if (themeSpec.isDark) Color.White else Color(0xFF0F172A)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = if (themeSpec.isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    }
                }

                HorizontalDivider(color = themeSpec.cardBorder.copy(alpha = 0.5f))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Kotak Editor Catatan Hias
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = activePreviewPalette.bgColor,
                        border = BorderStroke(2.dp, activePreviewPalette.borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = selectedSticker,
                                    fontSize = 24.sp
                                )
                                OutlinedTextField(
                                    value = titleInput,
                                    onValueChange = { titleInput = it },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    placeholder = {
                                        Text(
                                            text = if (isEn) "Note title..." else "Judul catatan...",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = contentInput,
                                onValueChange = { contentInput = it },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                placeholder = {
                                    Text(
                                        text = if (isEn) "Write study summary, formulas, or ideas..." else "Tulis ringkasan materi, rumus, atau ide...",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Pemilih Warna / Gaya Kertas Hias
                            Text(
                                text = if (isEn) "Card Style:" else "Hiasan Kartu:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = activePreviewPalette.textColor
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NoteStationeryPalettes.forEachIndexed { idx, palette ->
                                    val selected = idx == selectedStyleIdx
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(palette.bgColor)
                                            .border(
                                                width = if (selected) 3.dp else 1.5.dp,
                                                color = if (selected) themeSpec.primaryAccent else palette.borderColor,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                FrebelSoundManager.playClick(soundEnabled)
                                                selectedStyleIdx = idx
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = palette.borderColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Pemilih Stiker
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                NoteStickers.forEach { emoji ->
                                    val isChosen = emoji == selectedSticker
                                    Surface(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                FrebelSoundManager.playClick(soundEnabled)
                                                selectedSticker = emoji
                                            },
                                        shape = CircleShape,
                                        color = if (isChosen) {
                                            activePreviewPalette.borderColor.copy(alpha = 0.25f)
                                        } else {
                                            Color.White.copy(alpha = 0.55f)
                                        }
                                    ) {
                                        Text(
                                            text = emoji,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Upload & Preview Foto Catatan
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        FrebelSoundManager.playClick(soundEnabled)
                                        photoLauncher.launch(arrayOf("image/*"))
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isEn) "Attach Photo" else "Tambah Foto",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                if (selectedPhotoUri != null) {
                                    IconButton(
                                        onClick = { selectedPhotoUri = null },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus Foto",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            val previewPhoto = selectedPhotoUri
                            if (previewPhoto != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(115.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    FrebelUriImagePreview(
                                        uri = previewPhoto,
                                        contentDescription = "Lampiran Foto",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    FrebelSoundManager.playClick(soundEnabled)
                                    val finalTitle = titleInput.trim().ifEmpty {
                                        if (isEn) "Quick Study Memo" else "Catatan Belajar Cepat"
                                    }
                                    onAddNote(
                                        finalTitle,
                                        contentInput.trim(),
                                        selectedStyleIdx,
                                        selectedSticker,
                                        selectedPhotoUri
                                    )
                                    titleInput = ""
                                    contentInput = ""
                                    selectedPhotoUri = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
                            ) {
                                Text(
                                    text = if (isEn) "Save Decorated Note" else "Simpan Catatan Hias",
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    // Daftar Catatan Hias yang Tersimpan
                    if (notes.isNotEmpty()) {
                        Text(
                            text = if (isEn) "Saved Notes (${notes.size})" else "Koleksi Catatan (${notes.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (themeSpec.isDark) Color.White else Color(0xFF0F172A)
                        )

                        notes.forEach { item ->
                            val style = NoteStationeryPalettes.getOrElse(item.cardStyleIndex) {
                                NoteStationeryPalettes.first()
                            }
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = style.bgColor,
                                border = BorderStroke(1.5.dp, style.borderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = item.stickerEmoji, fontSize = 20.sp)
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = style.textColor
                                            )
                                            Text(
                                                text = dateFormatter.format(Date(item.timestampMillis)),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = style.textColor.copy(alpha = 0.7f)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteNote(item.id) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Hapus",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    if (item.content.isNotBlank()) {
                                        Text(
                                            text = item.content,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = style.textColor
                                        )
                                    }

                                    val savedUri = item.photoUri
                                    if (savedUri != null) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(130.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        ) {
                                            FrebelUriImagePreview(
                                                uri = savedUri,
                                                contentDescription = item.title,
                                                modifier = Modifier.fillMaxSize()
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
}