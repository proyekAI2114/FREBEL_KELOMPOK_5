package com.example.frebel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frebel.ui.component.DailyScheduleReminderCard
import com.example.frebel.ui.component.ExamScheduleCard
import com.example.frebel.ui.component.FrebelFlameGraphic
import com.example.frebel.ui.component.FrebelQuickNotesDialog
import com.example.frebel.ui.component.FrebelSettingsDialog
import com.example.frebel.ui.component.StudentFrequencyCard
import com.example.frebel.ui.component.StudentHeaderCard
import com.example.frebel.ui.component.StudySessionCompletionDialog
import com.example.frebel.ui.component.StudySessionHistoryCard
import com.example.frebel.ui.component.StudyStopwatchCard
import com.example.frebel.ui.component.StudyStreakCard
import com.example.frebel.ui.component.StudyTimerMode
import com.example.frebel.ui.theme.FrebelAnimatedThemeBackground
import com.example.frebel.ui.theme.FrebelSoundManager
import com.example.frebel.ui.theme.FrebelThemeSpec
import com.example.frebel.ui.theme.buildFrebelTypography
import com.example.frebel.ui.theme.getFrebelStrings
import com.example.frebel.ui.theme.resolveFrebelThemeSpec
import com.example.frebel.ui.viewmodel.FrebelViewModel
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StudyReminderHelper.createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            FrebelMainApp()
        }
    }
}

private data class FrebelNavTab(
    val title: String,
    val icon: ImageVector,
    val activeColor: Color
)

@Composable
fun FrebelMainApp() {
    val context = LocalContext.current
    val viewModel = remember { FrebelViewModel(context.applicationContext) }

    val themeSpec = remember(
        viewModel.isDarkMode,
        viewModel.selectedSubTheme,
        viewModel.solidColorOption,
        viewModel.fontStyle,
        viewModel.textColorOption
    ) {
        resolveFrebelThemeSpec(
            isDarkMode = viewModel.isDarkMode,
            subTheme = viewModel.selectedSubTheme,
            solidColor = viewModel.solidColorOption,
            fontStyle = viewModel.fontStyle,
            textColorOption = viewModel.textColorOption
        )
    }

    val customTypography = remember(viewModel.fontStyle) {
        buildFrebelTypography(viewModel.fontStyle)
    }

    val strings = remember(viewModel.language) {
        getFrebelStrings(viewModel.language)
    }

    val materialColorScheme = if (viewModel.isDarkMode) {
        darkColorScheme(
            primary = themeSpec.primaryAccent,
            secondary = themeSpec.secondaryAccent,
            surface = themeSpec.cardSurface,
            background = themeSpec.backgroundTop,
            onSurface = themeSpec.textPrimary,
            onBackground = themeSpec.textPrimary,
            onSurfaceVariant = themeSpec.textSecondary
        )
    } else {
        lightColorScheme(
            primary = themeSpec.primaryAccent,
            secondary = themeSpec.secondaryAccent,
            surface = themeSpec.cardSurface,
            background = themeSpec.backgroundTop,
            onSurface = themeSpec.textPrimary,
            onBackground = themeSpec.textPrimary,
            onSurfaceVariant = themeSpec.textSecondary
        )
    }

    val navTabs = remember(strings, themeSpec) {
        listOf(
            FrebelNavTab(strings.tabHome, Icons.Default.Home, themeSpec.primaryAccent),
            FrebelNavTab(strings.tabStudy, Icons.Default.PlayArrow, themeSpec.secondaryAccent),
            FrebelNavTab(strings.tabStats, Icons.Default.DateRange, themeSpec.primaryAccent),
            FrebelNavTab(strings.tabStreak, Icons.Default.Star, themeSpec.secondaryAccent)
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(
        viewModel.isStopwatchRunning,
        viewModel.isBreakActive,
        viewModel.timerMode,
        viewModel.pomodoroTargetMinutes,
        viewModel.breakTargetMinutes
    ) {
        while (viewModel.isStopwatchRunning) {
            delay(1000L)
            if (viewModel.isBreakActive) {
                if (viewModel.breakRemainingSeconds > 1) {
                    viewModel.breakRemainingSeconds -= 1
                } else {
                    val finishedBreakMins = viewModel.breakTargetMinutes
                    viewModel.finishIntegratedBreak(autoResumeStudy = true)
                    if (viewModel.timerMode == StudyTimerMode.POMODORO && viewModel.showCompletionDialog) {
                        snackbarHostState.showSnackbar(
                            "Siklus Pomodoro + istirahat $finishedBreakMins menit selesai! Simpan hasil belajarmu."
                        )
                    } else {
                        snackbarHostState.showSnackbar(
                            "Istirahat $finishedBreakMins menit selesai! Timer belajar otomatis lanjut."
                        )
                    }
                }
            } else {
                viewModel.elapsedSeconds += 1
                if (viewModel.timerMode == StudyTimerMode.POMODORO &&
                    viewModel.elapsedSeconds >= viewModel.pomodoroTargetMinutes * 60
                ) {
                    viewModel.startPomodoroAutoBreak()
                    snackbarHostState.showSnackbar(
                        "Fokus Pomodoro ${viewModel.pomodoroTargetMinutes}m selesai! Otomatis lanjut istirahat ${viewModel.breakTargetMinutes}m."
                    )
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = materialColorScheme,
        typography = customTypography
    ) {
        if (viewModel.showSettingsDialog) {
            FrebelSettingsDialog(
                currentName = viewModel.studentName,
                currentProfileUri = viewModel.profilePhotoUri,
                currentWishlistText = viewModel.wishlistText,
                currentWishlistUri = viewModel.wishlistPhotoUri,
                currentIsDark = viewModel.isDarkMode,
                currentSubTheme = viewModel.selectedSubTheme,
                currentSolidColor = viewModel.solidColorOption,
                currentFontStyle = viewModel.fontStyle,
                currentTextColor = viewModel.textColorOption,
                currentLanguage = viewModel.language,
                currentSoundEnabled = viewModel.isPlayfulSoundEnabled,
                currentRingtoneUri = viewModel.reminderRingtoneUri,
                themeSpec = themeSpec,
                onDismiss = {
                    viewModel.triggerClickSound()
                    viewModel.showSettingsDialog = false
                },
                onSaveSettings = { name, pUri, wText, wUri, isDark, subTheme, solidCol, fontSt, txtCol, lang, sound, ringtone ->
                    viewModel.updateSettings(
                        newName = name,
                        newProfilePhoto = pUri,
                        newWishlistText = wText,
                        newWishlistPhoto = wUri,
                        darkMode = isDark,
                        subTheme = subTheme,
                        newSolidColor = solidCol,
                        newFontStyle = fontSt,
                        newTextColor = txtCol,
                        newLanguage = lang,
                        soundEnabled = sound,
                        ringtoneUri = ringtone
                    )
                }
            )
        }

        if (viewModel.showQuickNotesSheet) {
            FrebelQuickNotesDialog(
                notes = viewModel.quickNotes,
                language = viewModel.language,
                themeSpec = themeSpec,
                soundEnabled = viewModel.isPlayfulSoundEnabled,
                onDismiss = {
                    viewModel.triggerClickSound()
                    viewModel.showQuickNotesSheet = false
                },
                onAddNote = { title, content, styleIdx, sticker, photoUri ->
                    viewModel.addDecoratedQuickNote(title, content, styleIdx, sticker, photoUri)
                },
                onDeleteNote = { id ->
                    viewModel.deleteDecoratedQuickNote(id)
                }
            )
        }

        if (viewModel.showCompletionDialog) {
            val previewMinutes = (viewModel.pendingCompletionSeconds / 60).coerceAtLeast(1)
            StudySessionCompletionDialog(
                subjectName = viewModel.activeSubject,
                durationMinutes = previewMinutes,
                isPomodoroMode = viewModel.timerMode == StudyTimerMode.POMODORO,
                onDismiss = {
                    viewModel.triggerClickSound()
                    viewModel.showCompletionDialog = false
                },
                onConfirmSave = { rating, note, photoUri ->
                    val addedMinutes = viewModel.finalizeSessionSave(rating, note, photoUri)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            "Sesi tersimpan (+$addedMinutes mnt) & streak otomatis menyala!"
                        )
                    }
                },
                themeSpec = themeSpec,
                language = viewModel.language
            )
        }

        val totalSavedMinutes = viewModel.completedSessions.sumOf { it.durationMinutes }
            .coerceAtLeast(viewModel.completedMinutesToday)
        val formattedStudyDuration = "${totalSavedMinutes / 60}j ${totalSavedMinutes % 60}m"
        val peakFocusText = if (viewModel.completedSessions.isEmpty()) {
            "Belum ada sesi tercatat"
        } else {
            "Terakhir: ${viewModel.completedSessions.first().subject}"
        }

        val liveClockLabel = remember(
            viewModel.elapsedSeconds,
            viewModel.breakRemainingSeconds,
            viewModel.isBreakActive,
            viewModel.timerMode,
            viewModel.pomodoroTargetMinutes
        ) {
            val secs = when {
                viewModel.isBreakActive -> viewModel.breakRemainingSeconds
                viewModel.timerMode == StudyTimerMode.POMODORO ->
                    (viewModel.pomodoroTargetMinutes * 60 - viewModel.elapsedSeconds).coerceAtLeast(0)
                else -> viewModel.elapsedSeconds
            }
            String.format(Locale.US, "%02d:%02d", secs / 60, secs % 60)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            FrebelAnimatedThemeBackground(themeSpec = themeSpec)
            FrebelLiveAmbientOverlay(themeSpec = themeSpec)

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    FrebelCozyTopBar(
                        streakDays = viewModel.streakDays,
                        isTodaySubmitted = viewModel.isTodaySubmitted,
                        completedMinutesToday = viewModel.completedMinutesToday,
                        dailyTargetMinutes = viewModel.dailyTargetMinutes,
                        selectedBluePaletteIndex = viewModel.selectedBluePalette,
                        isTimerRunning = viewModel.isStopwatchRunning,
                        liveTimerLabel = liveClockLabel,
                        themeSpec = themeSpec,
                        onLiveTimerClick = {
                            viewModel.triggerClickSound()
                            viewModel.selectedTabIndex = 1
                        },
                        onStreakBadgeClick = {
                            viewModel.triggerClickSound()
                            viewModel.selectedTabIndex = 3
                        },
                        onSettingsClick = {
                            viewModel.triggerClickSound()
                            viewModel.showSettingsDialog = true
                        }
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = {
                            viewModel.triggerClickSound()
                            viewModel.showQuickNotesSheet = true
                        },
                        shape = CircleShape,
                        containerColor = themeSpec.primaryAccent,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (viewModel.quickNotes.isNotEmpty()) {
                                        Badge(
                                            containerColor = Color(0xFFEF4444),
                                            contentColor = Color.White
                                        ) {
                                            Text("${viewModel.quickNotes.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = strings.quickNoteBtn
                                )
                            }
                        },
                        text = {
                            Text(
                                text = strings.quickNoteBtn,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    )
                },
                bottomBar = {
                    FrebelCozyBottomBar(
                        tabs = navTabs,
                        selectedIndex = viewModel.selectedTabIndex,
                        isTimerRunning = viewModel.isStopwatchRunning,
                        themeSpec = themeSpec,
                        onSelectTab = { idx ->
                            viewModel.triggerClickSound()
                            viewModel.selectedTabIndex = idx
                        }
                    )
                }
            ) { innerPadding ->
                AnimatedContent(
                    targetState = viewModel.selectedTabIndex,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    label = "frebelPageTransition"
                ) { currentTab ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (currentTab) {
                            0 -> {
                                StudentHeaderCard(
                                    studentName = viewModel.studentName,
                                    activeSubject = viewModel.activeSubject,
                                    completedMinutesToday = viewModel.completedMinutesToday,
                                    dailyTargetMinutes = viewModel.dailyTargetMinutes,
                                    freezeQuota = viewModel.freezeQuota,
                                    targetItems = viewModel.targets.map { it.subjectLabel },
                                    onToggleTarget = { targetLabel, checked ->
                                        viewModel.targets.find {
                                            it.subjectLabel == targetLabel || it.id == targetLabel
                                        }?.let { target ->
                                            viewModel.toggleTarget(target.id, checked)
                                        }
                                    },
                                    onAddTarget = { text -> viewModel.addTarget(text) },
                                    onEditTarget = { oldLabel, newText ->
                                        viewModel.targets.find {
                                            it.subjectLabel == oldLabel || it.id == oldLabel
                                        }?.let { target ->
                                            viewModel.editTarget(target, newText)
                                        }
                                    },
                                    onDeleteTarget = { targetLabel ->
                                        viewModel.targets.find {
                                            it.subjectLabel == targetLabel || it.id == targetLabel
                                        }?.let { target ->
                                            viewModel.deleteTarget(target.id)
                                        }
                                    },
                                    onStartStudySession = {
                                        viewModel.triggerClickSound()
                                        if (viewModel.isBreakActive) {
                                            viewModel.finishIntegratedBreak(autoResumeStudy = true)
                                        }
                                        viewModel.selectedTabIndex = 1
                                        viewModel.isStopwatchRunning = true
                                    },
                                    profilePhotoUri = viewModel.profilePhotoUri?.toString(),
                                    wishlistText = viewModel.wishlistText,
//                                  wishlistPhotoUri = viewModel.wishlistPhotoUri?.toString(),
                                    themeSpec = themeSpec,
                                    language = viewModel.language,
                                    onOpenSettings = {
                                        viewModel.triggerClickSound()
                                        viewModel.showSettingsDialog = true
                                    }
                                )

                                ExamScheduleCard(
                                    exams = viewModel.examSchedules,
                                    subjectSuggestions = viewModel.subjectOptions,
                                    themeSpec = themeSpec,
                                    language = viewModel.language,
                                    soundEnabled = viewModel.isPlayfulSoundEnabled,
                                    onAddExam = { subj, day, date, month ->
                                        viewModel.addExamSchedule(subj, day, date, month)
                                    },
                                    onToggleMarked = { id -> viewModel.toggleExamMarked(id) },
                                    onDeleteExam = { id -> viewModel.deleteExamSchedule(id) }
                                )

                                DailyScheduleReminderCard(
                                    dailyTargetMinutes = viewModel.dailyTargetMinutes,
                                    reminderHour12 = viewModel.reminderHour12,
                                    reminderMinute = viewModel.reminderMinute,
                                    isAm = viewModel.isReminderAm,
                                    isReminderEnabled = viewModel.isReminderEnabled,
                                    onDailyTargetChange = { viewModel.updateDailyTarget(it) },
                                    onTimeChange = { h12, min, am ->
                                        viewModel.updateReminderTime(h12, min, am)
                                    },
                                    onToggleReminder = { enabled ->
                                        val statusMsg = viewModel.toggleReminder(enabled)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(statusMsg)
                                        }
                                    },
                                    onTestNotification = {
                                        viewModel.triggerClickSound()
                                        FrebelSoundManager.playPreviewRingtone(
                                            context,
                                            viewModel.reminderRingtoneUri
                                        )
                                        val label = StudyReminderHelper.format12HourLabel(
                                            viewModel.reminderHour12,
                                            viewModel.reminderMinute,
                                            viewModel.isReminderAm
                                        )
                                        StudyReminderHelper.showReminderNotification(
                                            context = context,
                                            title = "Pengingat Belajar FREBEL ($label)",
                                            message = "Target belajarmu hari ini ${viewModel.dailyTargetMinutes} menit. Ayo mulai sesi fokus!"
                                        )
                                    },
                                    themeSpec = themeSpec,
                                    language = viewModel.language
                                )
                            }

                            1 -> {
                                StudyStopwatchCard(
                                    elapsedSeconds = viewModel.elapsedSeconds,
                                    breakRemainingSeconds = viewModel.breakRemainingSeconds,
                                    isRunning = viewModel.isStopwatchRunning,
                                    timerMode = viewModel.timerMode,
                                    pomodoroTargetMinutes = viewModel.pomodoroTargetMinutes,
                                    breakTargetMinutes = viewModel.breakTargetMinutes,
                                    isBreakActive = viewModel.isBreakActive,
                                    selectedSubject = viewModel.activeSubject,
                                    subjectOptions = viewModel.subjectOptions,
                                    onSelectMode = { newMode ->
                                        viewModel.triggerClickSound()
                                        viewModel.isStopwatchRunning = false
                                        viewModel.isBreakActive = false
                                        viewModel.breakRemainingSeconds = 0
                                        viewModel.elapsedSeconds = 0
                                        viewModel.timerMode = newMode
                                    },
                                    onSelectPomodoroMinutes = { minutes ->
                                        viewModel.triggerClickSound()
                                        viewModel.isStopwatchRunning = false
                                        viewModel.isBreakActive = false
                                        viewModel.breakRemainingSeconds = 0
                                        viewModel.elapsedSeconds = 0
                                        viewModel.pomodoroTargetMinutes = minutes
                                    },
                                    onSelectBreakMinutes = { minutes ->
                                        viewModel.triggerClickSound()
                                        viewModel.breakTargetMinutes = minutes
                                        if (viewModel.isBreakActive) {
                                            viewModel.breakRemainingSeconds = minutes * 60
                                        }
                                    },
                                    onStartBreak = { minutes ->
                                        viewModel.startIntegratedBreak(minutes)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Istirahat $minutes menit dimulai!")
                                        }
                                    },
                                    onFinishBreak = {
                                        viewModel.triggerClickSound()
                                        viewModel.finishIntegratedBreak(autoResumeStudy = true)
                                        if (!viewModel.showCompletionDialog) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Kembali melanjutkan sesi belajar!")
                                            }
                                        }
                                    },
                                    onSelectSubject = { viewModel.selectSubject(it) },
                                    onAddSubject = { viewModel.addSubject(it) },
                                    onDeleteSubject = { viewModel.deleteSubject(it) },
                                    onToggleStartPause = {
                                        viewModel.triggerClickSound()
                                        viewModel.isStopwatchRunning = !viewModel.isStopwatchRunning
                                    },
                                    onReset = {
                                        viewModel.triggerClickSound()
                                        viewModel.isStopwatchRunning = false
                                        viewModel.isBreakActive = false
                                        viewModel.breakRemainingSeconds = 0
                                        viewModel.elapsedSeconds = 0
                                    },
                                    onSubmitStudyComplete = { seconds ->
                                        viewModel.openCompletionDialog(seconds)
                                    },
                                    themeSpec = themeSpec,
                                    language = viewModel.language
                                )
                            }

                            2 -> {
                                StudentFrequencyCard(
                                    weeklyHoursText = formattedStudyDuration,
                                    monthlyHoursText = formattedStudyDuration,
                                    peakFocusWindow = peakFocusText,
                                    heatmapIntensities = viewModel.heatmapIntensities,
                                    notes = viewModel.notes,
                                    onToggleNoteApplied = { id, applied -> viewModel.toggleNote(id, applied) },
                                    onAddNote = { text ->
                                        val feedback = viewModel.addNote(text)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Feedback otomatis: $feedback")
                                        }
                                    },
                                    onEditNote = { item, newText ->
                                        val feedback = viewModel.editNote(item, newText)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Feedback diperbarui: $feedback")
                                        }
                                    },
                                    onDeleteNote = { id -> viewModel.deleteNote(id) },
                                    onOpenWeeklyReview = {
                                        viewModel.triggerClickSound()
                                        coroutineScope.launch {
                                            val msg = if (viewModel.completedSessions.isEmpty()) {
                                                "Belum ada sesi selesai minggu ini. Ayo mulai belajar!"
                                            } else {
                                                "Total ${viewModel.completedSessions.size} sesi selesai ($formattedStudyDuration)!"
                                            }
                                            snackbarHostState.showSnackbar(msg)
                                        }
                                    },
                                    themeSpec = themeSpec,
                                    language = viewModel.language
                                )

                                StudySessionHistoryCard(
                                    sessions = viewModel.completedSessions,
                                    onDeleteSession = { id -> viewModel.deleteSession(id) },
                                    themeSpec = themeSpec,
                                    language = viewModel.language
                                )
                            }

                            3 -> {
                                StudyStreakCard(
                                    streakDays = viewModel.streakDays,
                                    bestStreakDays = viewModel.bestStreakDays,
                                    isTodaySubmitted = viewModel.isTodaySubmitted,
                                    selectedBluePaletteIndex = viewModel.selectedBluePalette,
                                    commitments = viewModel.commitments,
                                    onSelectBluePalette = { viewModel.selectBluePalette(it) },
                                    onToggleCommitment = { id, checked ->
                                        viewModel.toggleCommitment(id, checked)
                                    },
                                    onAddCommitment = { text -> viewModel.addCommitment(text) },
                                    onEditCommitment = { item, newText ->
                                        viewModel.editCommitment(item, newText)
                                    },
                                    onDeleteCommitment = { id -> viewModel.deleteCommitment(id) },
                                    onSubmitStudyComplete = {
                                        viewModel.triggerClickSound()
                                        if (viewModel.elapsedSeconds > 0 && !viewModel.isBreakActive) {
                                            viewModel.openCompletionDialog(viewModel.elapsedSeconds)
                                        } else {
                                            viewModel.selectedTabIndex = 1
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "Selesaikan minimal 1 sesi belajar untuk menyalakan streak!"
                                                )
                                            }
                                        }
                                    },
                                    completedSessions = viewModel.completedSessions,
                                    gradeTracks = viewModel.gradeTracks,
                                    latestGradeFeedback = viewModel.latestGradeFeedback,
                                    onAddGradeSubject = { subj -> viewModel.addGradeSubject(subj) },
                                    onDeleteGradeSubject = { subj -> viewModel.deleteGradeSubject(subj) },
                                    onAddDailyScore = { subj, lbl, sc ->
                                        viewModel.addDailyTestScore(subj, lbl, sc)
                                    },
                                    onAddMidtermScore = { subj, lbl, sc ->
                                        viewModel.addMidtermScore(subj, lbl, sc)
                                    },
                                    themeSpec = themeSpec,
                                    language = viewModel.language,
                                    soundEnabled = viewModel.isPlayfulSoundEnabled
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(68.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FrebelLiveAmbientOverlay(themeSpec: FrebelThemeSpec) {
    val transition = rememberInfiniteTransition(label = "frebelAmbientDrift")
    val driftPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambientOrbPhase"
    )
    val twinkleAlpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientTwinkle"
    )

    val orbAlpha = if (themeSpec.isDark) 0.13f else 0.09f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val orb1Center = Offset(
            x = w * (0.82f + 0.08f * sin(driftPhase)),
            y = h * (0.16f + 0.04f * cos(driftPhase))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(themeSpec.primaryAccent.copy(alpha = orbAlpha), Color.Transparent),
                center = orb1Center,
                radius = w * 0.48f
            ),
            radius = w * 0.48f,
            center = orb1Center
        )

        val orb2Center = Offset(
            x = w * (0.15f + 0.07f * cos(driftPhase + 1.2f)),
            y = h * (0.52f + 0.05f * sin(driftPhase + 1.2f))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(themeSpec.secondaryAccent.copy(alpha = orbAlpha), Color.Transparent),
                center = orb2Center,
                radius = w * 0.44f
            ),
            radius = w * 0.44f,
            center = orb2Center
        )

        val orb3Center = Offset(
            x = w * (0.76f + 0.06f * sin(driftPhase + 2.5f)),
            y = h * (0.84f + 0.04f * cos(driftPhase + 2.5f))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(themeSpec.primaryAccent.copy(alpha = orbAlpha * 0.85f), Color.Transparent),
                center = orb3Center,
                radius = w * 0.42f
            ),
            radius = w * 0.42f,
            center = orb3Center
        )

        val sparkles = listOf(
            Offset(w * 0.22f, h * 0.24f) to 3.5.dp.toPx(),
            Offset(w * 0.78f, h * 0.36f) to 2.8.dp.toPx(),
            Offset(w * 0.34f, h * 0.72f) to 3.2.dp.toPx(),
            Offset(w * 0.86f, h * 0.66f) to 2.5.dp.toPx()
        )
        sparkles.forEachIndexed { idx, (pos, rad) ->
            val alphaMult = if (idx % 2 == 0) twinkleAlpha else (1.1f - twinkleAlpha)
            drawCircle(
                color = themeSpec.primaryAccent.copy(alpha = 0.32f * alphaMult),
                radius = rad,
                center = Offset(pos.x + 6f * sin(driftPhase + idx), pos.y)
            )
        }
    }
}

@Composable
private fun FrebelCozyTopBar(
    streakDays: Int,
    isTodaySubmitted: Boolean,
    completedMinutesToday: Int,
    dailyTargetMinutes: Int,
    selectedBluePaletteIndex: Int,
    isTimerRunning: Boolean,
    liveTimerLabel: String,
    themeSpec: FrebelThemeSpec,
    onLiveTimerClick: () -> Unit,
    onStreakBadgeClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val (bluePrimary, blueSecondary) = when (selectedBluePaletteIndex) {
        1 -> Color(0xFF2563EB) to Color(0xFF93C5FD)
        2 -> Color(0xFF0891B2) to Color(0xFF67E8F9)
        3 -> Color(0xFF4F46E5) to Color(0xFFA5B4FC)
        else -> Color(0xFF0284C7) to Color(0xFF7DD3FC)
    }
    val isFlameLit = isTodaySubmitted && streakDays > 0
    val (flamePrimary, flameSecondary) = when {
        !isFlameLit -> Color(0xFF64748B) to Color(0xFFCBD5E1)
        streakDays <= 7 -> Color(0xFFD97706) to Color(0xFFFDE047)
        streakDays <= 15 -> Color(0xFFEA580C) to Color(0xFFFDBA74)
        streakDays <= 30 -> Color(0xFFE11D48) to Color(0xFFFDA4AF)
        streakDays <= 60 -> Color(0xFF7C3AED) to Color(0xFFDDD6FE)
        streakDays <= 100 -> Color(0xFF059669) to Color(0xFF6EE7B7)
        else -> bluePrimary to blueSecondary
    }

    val dailyProgress by animateFloatAsState(
        targetValue = (completedMinutesToday.toFloat() / dailyTargetMinutes.coerceAtLeast(1).toFloat())
            .coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "topBarDailyProgress"
    )

    Surface(
        color = themeSpec.cardSurface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = "FREBEL",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = themeSpec.primaryAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                AnimatedVisibility(visible = isTimerRunning) {
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onLiveTimerClick() },
                        shape = CircleShape,
                        color = themeSpec.secondaryAccent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Text(
                                text = liveTimerLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onStreakBadgeClick() },
                    shape = CircleShape,
                    color = flamePrimary.copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FrebelFlameGraphic(
                            primaryColor = flamePrimary,
                            secondaryColor = flameSecondary,
                            isAnimated = isFlameLit,
                            modifier = Modifier.size(19.dp)
                        )
                        Text(
                            text = "$streakDays",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = flamePrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = themeSpec.primaryAccent.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = "${completedMinutesToday}/${dailyTargetMinutes}m",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = themeSpec.primaryAccent,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(themeSpec.primaryAccent.copy(alpha = 0.14f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Setelan",
                        tint = themeSpec.primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { dailyProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = themeSpec.primaryAccent,
                trackColor = themeSpec.cardBorder
            )
        }
    }
}

@Composable
private fun FrebelCozyBottomBar(
    tabs: List<FrebelNavTab>,
    selectedIndex: Int,
    isTimerRunning: Boolean,
    themeSpec: FrebelThemeSpec,
    onSelectTab: (Int) -> Unit
) {
    Surface(
        color = themeSpec.cardSurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == selectedIndex
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 0.96f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "cozyTabSpring"
                    )
                    val interactionSource = remember { MutableInteractionSource() }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onSelectTab(index) },
                        shape = CircleShape,
                        color = if (isSelected) tab.activeColor.copy(alpha = 0.16f) else Color.Transparent
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) tab.activeColor else themeSpec.textSecondary,
                                    modifier = Modifier.size(23.dp)
                                )
                                if (index == 1 && isTimerRunning) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(themeSpec.primaryAccent)
                                    )
                                }
                            }

                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) tab.activeColor else themeSpec.textSecondary,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}