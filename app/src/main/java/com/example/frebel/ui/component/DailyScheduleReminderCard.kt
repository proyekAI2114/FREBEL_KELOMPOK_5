package com.example.frebel.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSubTheme
import com.example.frebel.ui.theme.FrebelThemeSpec
import com.example.frebel.ui.theme.resolveFrebelThemeSpec
import java.util.Locale

@Composable
fun DailyScheduleReminderCard(
    dailyTargetMinutes: Int,
    reminderHour12: Int,
    reminderMinute: Int,
    isAm: Boolean,
    isReminderEnabled: Boolean,
    onDailyTargetChange: (Int) -> Unit,
    onTimeChange: (Int, Int, Boolean) -> Unit,
    onToggleReminder: (Boolean) -> Unit,
    onTestNotification: () -> Unit,
    themeSpec: FrebelThemeSpec = resolveFrebelThemeSpec(false, FrebelSubTheme.SOLID),
    language: FrebelLanguage = FrebelLanguage.INDONESIAN,
    modifier: Modifier = Modifier
) {
    val isEn = language == FrebelLanguage.ENGLISH
    val presetTargets = listOf(30, 45, 60, 90, 120)

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(themeSpec.primaryAccent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = themeSpec.primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEn) "Daily Target & Reminder" else "Target Harian & Pengingat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = themeSpec.textPrimary
                    )
                    Text(
                        text = if (isEn) {
                            "Set cozy study duration & gentle alarm"
                        } else {
                            "Atur durasi target harian & jadwal pengingat"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = themeSpec.textSecondary
                    )
                }
            }

            // 1. Pilihan Target Durasi Belajar Harian
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = themeSpec.primaryAccent.copy(alpha = 0.08f),
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
                            text = if (isEn) "Daily Study Target" else "Target Belajar Harian",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.textPrimary
                        )
                        Surface(
                            shape = CircleShape,
                            color = themeSpec.primaryAccent
                        ) {
                            Text(
                                text = if (isEn) "$dailyTargetMinutes min" else "$dailyTargetMinutes menit",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetTargets.forEach { mins ->
                            val selected = dailyTargetMinutes == mins
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .clickable { onDailyTargetChange(mins) },
                                shape = CircleShape,
                                color = if (selected) themeSpec.primaryAccent else themeSpec.cardSurface
                            ) {
                                Text(
                                    text = "${mins}m",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else themeSpec.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = themeSpec.cardBorder)

            // 2. Pengatur Jam Pengingat (12-Jam AM/PM)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEn) "Daily Study Alarm" else "Alarm Pengingat Belajar",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = themeSpec.textPrimary
                    )
                    Text(
                        text = if (isReminderEnabled) {
                            if (isEn) "Active every day" else "Aktif setiap hari"
                        } else {
                            if (isEn) "Currently off" else "Saat ini nonaktif"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = themeSpec.textSecondary
                    )
                }
                Switch(
                    checked = isReminderEnabled,
                    onCheckedChange = onToggleReminder,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = themeSpec.primaryAccent
                    )
                )
            }

            // Kontrol Jam & Menit Lembut
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = themeSpec.secondaryAccent.copy(alpha = 0.09f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Jam
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val nextH = if (reminderHour12 >= 12) 1 else reminderHour12 + 1
                                    onTimeChange(nextH, reminderMinute, isAm)
                                },
                            shape = CircleShape,
                            color = themeSpec.primaryAccent.copy(alpha = 0.16f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("+", fontWeight = FontWeight.Black, color = themeSpec.primaryAccent)
                            }
                        }

                        Text(
                            text = String.format(Locale.US, "%02d", reminderHour12),
                            style = TextStyle(
                                fontFeatureSettings = "tnum",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = themeSpec.textPrimary
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val prevH = if (reminderHour12 <= 1) 12 else reminderHour12 - 1
                                    onTimeChange(prevH, reminderMinute, isAm)
                                },
                            shape = CircleShape,
                            color = themeSpec.primaryAccent.copy(alpha = 0.16f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("-", fontWeight = FontWeight.Black, color = themeSpec.primaryAccent)
                            }
                        }
                    }

                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = themeSpec.primaryAccent
                    )

                    // Menit (kelipatan 5 menit)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val nextM = (reminderMinute + 5) % 60
                                    onTimeChange(reminderHour12, nextM, isAm)
                                },
                            shape = CircleShape,
                            color = themeSpec.primaryAccent.copy(alpha = 0.16f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("+", fontWeight = FontWeight.Black, color = themeSpec.primaryAccent)
                            }
                        }

                        Text(
                            text = String.format(Locale.US, "%02d", reminderMinute),
                            style = TextStyle(
                                fontFeatureSettings = "tnum",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = themeSpec.textPrimary
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val prevM = if (reminderMinute - 5 < 0) 55 else reminderMinute - 5
                                    onTimeChange(reminderHour12, prevM, isAm)
                                },
                            shape = CircleShape,
                            color = themeSpec.primaryAccent.copy(alpha = 0.16f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("-", fontWeight = FontWeight.Black, color = themeSpec.primaryAccent)
                            }
                        }
                    }

                    // Pill AM / PM
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(true to "AM", false to "PM").forEach { (amVal, label) ->
                            val selected = isAm == amVal
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { onTimeChange(reminderHour12, reminderMinute, amVal) },
                                shape = CircleShape,
                                color = if (selected) themeSpec.secondaryAccent else themeSpec.cardSurface
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (selected) Color.White else themeSpec.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Tombol Tes Nada & Notifikasi Tactile Game Button
            FrebelTactileGameButton(
                text = if (isEn) "Test Reminder Sound & Notification" else "Tes Nada Dering & Notifikasi",
                icon = Icons.Default.PlayArrow,
                onClick = onTestNotification,
                gradientColors = listOf(themeSpec.primaryAccent, themeSpec.secondaryAccent),
                bevelDarkColor = themeSpec.primaryAccent.copy(alpha = 0.75f),
                glowColor = themeSpec.primaryAccent.copy(alpha = 0.35f),
                height = 46.dp,
                bevelDepth = 3.dp
            )
        }
    }
}