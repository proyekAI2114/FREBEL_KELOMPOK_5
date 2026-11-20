package com.example.frebel.ui.component

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.frebel.ui.theme.FrebelFontStyle
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSolidColorOption
import com.example.frebel.ui.theme.FrebelSoundManager
import com.example.frebel.ui.theme.FrebelSubTheme
import com.example.frebel.ui.theme.FrebelTextColorOption
import com.example.frebel.ui.theme.FrebelThemeSpec
import com.example.frebel.ui.theme.resolveFrebelThemeSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FrebelSettingsDialog(
    currentName: String,
    currentProfileUri: Uri?,
    currentWishlistText: String,
    currentWishlistUri: Uri?,
    currentIsDark: Boolean,
    currentSubTheme: FrebelSubTheme,
    currentSolidColor: FrebelSolidColorOption,
    currentFontStyle: FrebelFontStyle,
    currentTextColor: FrebelTextColorOption,
    currentLanguage: FrebelLanguage,
    currentSoundEnabled: Boolean,
    currentRingtoneUri: String,
    themeSpec: FrebelThemeSpec,
    onDismiss: () -> Unit,
    onSaveSettings: (
        name: String,
        profileUri: Uri?,
        wishlistText: String,
        wishlistUri: Uri?,
        isDark: Boolean,
        subTheme: FrebelSubTheme,
        solidColor: FrebelSolidColorOption,
        fontStyle: FrebelFontStyle,
        textColor: FrebelTextColorOption,
        language: FrebelLanguage,
        soundEnabled: Boolean,
        ringtoneUri: String
    ) -> Unit
) {
    val context = LocalContext.current

    var nameInput by remember { mutableStateOf(currentName) }
    var profileUriInput by remember { mutableStateOf(currentProfileUri) }
    var wishlistTextInput by remember { mutableStateOf(currentWishlistText) }
    var wishlistUriInput by remember { mutableStateOf(currentWishlistUri) }
    var isDarkInput by remember { mutableStateOf(currentIsDark) }
    var subThemeInput by remember { mutableStateOf(currentSubTheme) }
    var solidColorInput by remember { mutableStateOf(currentSolidColor) }
    var fontStyleInput by remember { mutableStateOf(currentFontStyle) }
    var textColorInput by remember { mutableStateOf(currentTextColor) }
    var languageInput by remember { mutableStateOf(currentLanguage) }
    var soundEnabledInput by remember { mutableStateOf(currentSoundEnabled) }
    var ringtoneUriInput by remember {
        mutableStateOf(currentRingtoneUri.takeIf { it.isNotBlank() }?.let { Uri.parse(it) })
    }

    val isEn = languageInput == FrebelLanguage.ENGLISH

    val liveSpec = remember(
        isDarkInput,
        subThemeInput,
        solidColorInput,
        fontStyleInput,
        textColorInput
    ) {
        resolveFrebelThemeSpec(
            isDarkMode = isDarkInput,
            subTheme = subThemeInput,
            solidColor = solidColorInput,
            fontStyle = fontStyleInput,
            textColorOption = textColorInput
        )
    }

    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            profileUriInput = uri
        }
    }

    val wishlistPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            wishlistUriInput = uri
        }
    }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            @Suppress("DEPRECATION")
            val pickedUri: Uri? = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            ringtoneUriInput = pickedUri
        }
    }

    val ringtoneTitle = remember(ringtoneUriInput, isEn) {
        val uri = ringtoneUriInput
        if (uri == null) {
            if (isEn) "System Default Notification" else "Nada Bawaan Perangkat"
        } else {
            runCatching {
                RingtoneManager.getRingtone(context, uri)?.getTitle(context)
            }.getOrNull() ?: if (isEn) "Custom Ringtone" else "Nada Dering Kustom"
        }
    }

    val cozyFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = liveSpec.primaryAccent,
        unfocusedBorderColor = liveSpec.cardBorder,
        focusedTextColor = liveSpec.textPrimary,
        unfocusedTextColor = liveSpec.textPrimary,
        cursorColor = liveSpec.primaryAccent
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 680.dp),
            shape = RoundedCornerShape(32.dp),
            color = liveSpec.cardSurface,
            border = BorderStroke(1.dp, liveSpec.cardBorder),
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
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
                    Text(
                        text = if (isEn) "⚙️ FREBEL Settings" else "⚙️ Setelan FREBEL",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = liveSpec.textPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(liveSpec.primaryAccent.copy(alpha = 0.14f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = liveSpec.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = liveSpec.cardBorder)

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Profil & Target Impian (Wishlist)
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = liveSpec.primaryAccent.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isEn) "👤 Profile & Study Wishlist" else "👤 Profil & Target Impian",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = liveSpec.textPrimary
                            )

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = cozyFieldColors,
                                label = { Text(if (isEn) "Student Name" else "Nama Pelajar") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        FrebelSoundManager.playClick(soundEnabledInput)
                                        profilePhotoPicker.launch(arrayOf("image/*"))
                                    },
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, liveSpec.primaryAccent.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp), tint = liveSpec.primaryAccent)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEn) "Profile Photo" else "Foto Profil",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = liveSpec.primaryAccent
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        FrebelSoundManager.playClick(soundEnabledInput)
                                        wishlistPhotoPicker.launch(arrayOf("image/*"))
                                    },
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, liveSpec.secondaryAccent.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp), tint = liveSpec.secondaryAccent)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEn) "Wishlist Photo" else "Foto Impian",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = liveSpec.secondaryAccent
                                    )
                                }
                            }

                            val pUri = profileUriInput
                            if (pUri != null) {
                                FrebelUriImagePreview(
                                    uri = pUri,
                                    contentDescription = "Foto Profil",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                )
                            }

                            OutlinedTextField(
                                value = wishlistTextInput,
                                onValueChange = { wishlistTextInput = it },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = cozyFieldColors,
                                label = { Text(if (isEn) "Dream Goal / Wishlist" else "Target Impian / Motivasi") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            val wUri = wishlistUriInput
                            if (wUri != null) {
                                FrebelUriImagePreview(
                                    uri = wUri,
                                    contentDescription = "Foto Impian",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(84.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }

                    // 2. Mode Tampilan & Tema Dekoratif
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = liveSpec.secondaryAccent.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isEn) "🎨 Cozy Theme & Palette" else "🎨 Tema Cozy & Warna",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = liveSpec.textPrimary
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .background(liveSpec.primaryAccent.copy(alpha = 0.10f))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    false to (if (isEn) "☀️ Light" else "☀️ Mode Terang"),
                                    true to (if (isEn) "🌙 Dark" else "🌙 Mode Gelap")
                                ).forEach { (darkVal, label) ->
                                    val selected = isDarkInput == darkVal
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(CircleShape)
                                            .clickable {
                                                FrebelSoundManager.playClick(soundEnabledInput)
                                                isDarkInput = darkVal
                                            },
                                        shape = CircleShape,
                                        color = if (selected) liveSpec.primaryAccent else Color.Transparent
                                    ) {
                                        Text(
                                            text = label,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (selected) Color.White else liveSpec.textPrimary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = if (isEn) "Theme Style:" else "Gaya Tema:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = liveSpec.textSecondary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FrebelSubTheme.values().forEach { sub ->
                                    val selected = subThemeInput == sub
                                    val previewAccent = resolveFrebelThemeSpec(
                                        isDarkInput,
                                        sub,
                                        solidColorInput,
                                        fontStyleInput,
                                        textColorInput
                                    ).primaryAccent

                                    Surface(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                FrebelSoundManager.playClick(soundEnabledInput)
                                                subThemeInput = sub
                                            },
                                        shape = CircleShape,
                                        color = if (selected) previewAccent else previewAccent.copy(alpha = 0.14f)
                                    ) {
                                        Text(
                                            text = formatEnumDisplayName(sub.name),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else liveSpec.textPrimary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = if (isEn) "Accent & Solid Color:" else "Warna Aksen / Solid:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = liveSpec.textSecondary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FrebelSolidColorOption.values().forEach { solidOpt ->
                                    val selected = solidColorInput == solidOpt
                                    val swatch = resolveFrebelThemeSpec(
                                        isDarkInput,
                                        FrebelSubTheme.SOLID,
                                        solidOpt,
                                        fontStyleInput,
                                        textColorInput
                                    ).primaryAccent

                                    Surface(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                FrebelSoundManager.playClick(soundEnabledInput)
                                                solidColorInput = solidOpt
                                                subThemeInput = FrebelSubTheme.SOLID
                                            },
                                        shape = CircleShape,
                                        color = if (selected) swatch else swatch.copy(alpha = 0.14f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(if (selected) Color.White else swatch)
                                            )
                                            Text(
                                                text = formatEnumDisplayName(solidOpt.name),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) Color.White else liveSpec.textPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Gaya Font & Warna Teks
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = liveSpec.primaryAccent.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isEn) "🔤 Typography & Text Color" else "🔤 Gaya Font & Warna Teks",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = liveSpec.textPrimary
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FrebelFontStyle.values().forEach { fStyle ->
                                    val selected = fontStyleInput == fStyle
                                    Surface(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                FrebelSoundManager.playClick(soundEnabledInput)
                                                fontStyleInput = fStyle
                                            },
                                        shape = CircleShape,
                                        color = if (selected) liveSpec.primaryAccent else liveSpec.cardSurface
                                    ) {
                                        Text(
                                            text = formatEnumDisplayName(fStyle.name),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else liveSpec.textPrimary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = if (isEn) "Text Color:" else "Warna Teks:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = liveSpec.textSecondary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FrebelTextColorOption.values().forEach { txtOpt ->
                                    val selected = textColorInput == txtOpt
                                    val txtSwatch = resolveFrebelThemeSpec(
                                        isDarkInput,
                                        subThemeInput,
                                        solidColorInput,
                                        fontStyleInput,
                                        txtOpt
                                    ).textPrimary

                                    Surface(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                FrebelSoundManager.playClick(soundEnabledInput)
                                                textColorInput = txtOpt
                                            },
                                        shape = CircleShape,
                                        color = if (selected) liveSpec.secondaryAccent else liveSpec.cardSurface
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(if (selected) Color.White else txtSwatch)
                                            )
                                            Text(
                                                text = formatEnumDisplayName(txtOpt.name),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) Color.White else liveSpec.textPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Bahasa, Efek Suara & Nada Pengingat
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = liveSpec.secondaryAccent.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isEn) "🌐 Language & Sound" else "🌐 Bahasa & Efek Suara",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = liveSpec.textPrimary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    FrebelLanguage.INDONESIAN to "🇮🇩 Indonesia",
                                    FrebelLanguage.ENGLISH to "🇬🇧 English"
                                ).forEach { (langOpt, label) ->
                                    val selected = languageInput == langOpt
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(CircleShape)
                                            .clickable {
                                                FrebelSoundManager.playClick(soundEnabledInput)
                                                languageInput = langOpt
                                            },
                                        shape = CircleShape,
                                        color = if (selected) liveSpec.primaryAccent else liveSpec.cardSurface
                                    ) {
                                        Text(
                                            text = label,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else liveSpec.textPrimary
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "Playful Button Sound" else "Efek Suara Tombol",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = liveSpec.textPrimary
                                )
                                Switch(
                                    checked = soundEnabledInput,
                                    onCheckedChange = {
                                        soundEnabledInput = it
                                        if (it) FrebelSoundManager.playClick(true)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = liveSpec.primaryAccent
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        FrebelSoundManager.playClick(soundEnabledInput)
                                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUriInput)
                                        }
                                        ringtonePickerLauncher.launch(intent)
                                    },
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, liveSpec.primaryAccent.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = liveSpec.primaryAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = ringtoneTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = liveSpec.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        FrebelSoundManager.playPreviewRingtone(
                                            context,
                                            ringtoneUriInput?.toString().orEmpty()
                                        )
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(liveSpec.primaryAccent.copy(alpha = 0.14f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Tes Nada",
                                        tint = liveSpec.primaryAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                FrebelTactileGameButton(
                    text = if (isEn) "Save Settings" else "Simpan Setelan",
                    icon = Icons.Default.Check,
                    onClick = {
                        FrebelSoundManager.playClick(soundEnabledInput)
                        onSaveSettings(
                            nameInput.trim().ifEmpty { currentName },
                            profileUriInput,
                            wishlistTextInput.trim(),
                            wishlistUriInput,
                            isDarkInput,
                            subThemeInput,
                            solidColorInput,
                            fontStyleInput,
                            textColorInput,
                            languageInput,
                            soundEnabledInput,
                            ringtoneUriInput?.toString().orEmpty()
                        )
                    },
                    gradientColors = listOf(liveSpec.primaryAccent, liveSpec.secondaryAccent),
                    bevelDarkColor = liveSpec.primaryAccent.copy(alpha = 0.75f),
                    glowColor = liveSpec.primaryAccent.copy(alpha = 0.35f),
                    height = 48.dp,
                    bevelDepth = 3.dp,
                    soundEnabled = soundEnabledInput
                )
            }
        }
    }
}

@Composable
fun FrebelUriImagePreview(
    uri: Uri,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val boundsOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, boundsOptions)
                }

                val maxDim = 1024
                var sampleSize = 1
                while (boundsOptions.outWidth / sampleSize > maxDim ||
                    boundsOptions.outHeight / sampleSize > maxDim
                ) {
                    sampleSize *= 2
                }

                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, decodeOptions)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    val loaded = imageBitmap
    if (loaded != null) {
        Image(
            bitmap = loaded,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun formatEnumDisplayName(raw: String): String {
    return raw.split("_").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}