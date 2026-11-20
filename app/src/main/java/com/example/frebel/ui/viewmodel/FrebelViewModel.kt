package com.example.frebel.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.frebel.StudyReminderHelper
import com.example.frebel.ui.component.CompletedStudySession
import com.example.frebel.ui.component.StreakCommitmentItem
import com.example.frebel.ui.component.StudyMotivationNote
import com.example.frebel.ui.component.StudyTargetItem
import com.example.frebel.ui.component.StudyTimerMode
import com.example.frebel.ui.data.DecoratedQuickNote
import com.example.frebel.ui.data.ExamScheduleItem
import com.example.frebel.ui.data.FrebelPersistedState
import com.example.frebel.ui.data.FrebelStorageRepository
import com.example.frebel.ui.data.GradeScoreRecord
import com.example.frebel.ui.data.SubjectGradeTrack
import com.example.frebel.ui.theme.FrebelFontStyle
import com.example.frebel.ui.theme.FrebelLanguage
import com.example.frebel.ui.theme.FrebelSolidColorOption
import com.example.frebel.ui.theme.FrebelSoundManager
import com.example.frebel.ui.theme.FrebelSubTheme
import com.example.frebel.ui.theme.FrebelTextColorOption
import java.util.Locale

class FrebelViewModel(
    private val appContext: Context
) {

    private val repository = FrebelStorageRepository(appContext)
    private val initial = repository.loadState()

    var selectedTabIndex by mutableIntStateOf(0)
    var elapsedSeconds by mutableIntStateOf(0)
    var breakRemainingSeconds by mutableIntStateOf(0)
    var isStopwatchRunning by mutableStateOf(false)
    var timerMode by mutableStateOf(StudyTimerMode.STOPWATCH)
    var pomodoroTargetMinutes by mutableIntStateOf(25)
    var breakTargetMinutes by mutableIntStateOf(5)
    var isBreakActive by mutableStateOf(false)

    var pendingCompletionSeconds by mutableIntStateOf(0)
    var showCompletionDialog by mutableStateOf(false)

    // Pop-up Setelan & Pop-up Catatan Hias
    var showSettingsDialog by mutableStateOf(false)
    var showQuickNotesSheet by mutableStateOf(false)

    // Profil, Tema, Solid Color, Font, Warna Teks, Bahasa & Suara
    var studentName by mutableStateOf(initial.studentName)
    var profilePhotoUri by mutableStateOf(initial.profilePhotoUri)
    var wishlistText by mutableStateOf(initial.wishlistText)
    var wishlistPhotoUri by mutableStateOf(initial.wishlistPhotoUri)
    var isDarkMode by mutableStateOf(initial.isDarkMode)
    var selectedSubTheme by mutableStateOf(FrebelSubTheme.fromId(initial.subThemeId))
    var solidColorOption by mutableStateOf(FrebelSolidColorOption.fromId(initial.solidColorId))
    var fontStyle by mutableStateOf(FrebelFontStyle.fromId(initial.fontStyleId))
    var textColorOption by mutableStateOf(FrebelTextColorOption.fromId(initial.textColorId))
    var language by mutableStateOf(FrebelLanguage.fromCode(initial.languageCode))
    var isPlayfulSoundEnabled by mutableStateOf(initial.isPlayfulSoundEnabled)
    var reminderRingtoneUri by mutableStateOf(initial.reminderRingtoneUri)

    // Statistik & Streak
    var completedMinutesToday by mutableIntStateOf(initial.completedMinutesToday)
    var dailyTargetMinutes by mutableIntStateOf(initial.dailyTargetMinutes)
    var freezeQuota by mutableIntStateOf(initial.freezeQuota)
    var streakDays by mutableIntStateOf(initial.streakDays)
    var bestStreakDays by mutableIntStateOf(initial.bestStreakDays)
    var isTodaySubmitted by mutableStateOf(initial.isTodaySubmitted)
    var selectedBluePalette by mutableIntStateOf(initial.selectedBluePalette)

    // Pengingat
    var reminderHour12 by mutableIntStateOf(initial.reminderHour12)
    var reminderMinute by mutableIntStateOf(initial.reminderMinute)
    var isReminderAm by mutableStateOf(initial.isReminderAm)
    var isReminderEnabled by mutableStateOf(initial.isReminderEnabled)

    // Konten Belajar
    var subjectOptions by mutableStateOf(initial.subjectOptions)
    var activeSubject by mutableStateOf(initial.activeSubject)
    var targets by mutableStateOf(initial.targets)
    var notes by mutableStateOf(initial.notes)
    var commitments by mutableStateOf(initial.commitments)
    var heatmapIntensities by mutableStateOf(initial.heatmapIntensities)
    var completedSessions by mutableStateOf(initial.completedSessions)

    // Jadwal Ulangan, Catatan Hias + Foto, & Track Nilai (UH + UTS)
    var examSchedules by mutableStateOf(initial.examSchedules)
    var quickNotes by mutableStateOf(initial.quickNotes)
    var gradeTracks by mutableStateOf(initial.gradeTracks)
    var latestGradeFeedback by mutableStateOf("")

    private fun persist() {
        repository.saveState(
            FrebelPersistedState(
                studentName = studentName,
                profilePhotoUri = profilePhotoUri,
                wishlistText = wishlistText,
                wishlistPhotoUri = wishlistPhotoUri,
                isDarkMode = isDarkMode,
                subThemeId = selectedSubTheme.id,
                solidColorId = solidColorOption.id,
                fontStyleId = fontStyle.id,
                textColorId = textColorOption.id,
                languageCode = language.code,
                isPlayfulSoundEnabled = isPlayfulSoundEnabled,
                reminderRingtoneUri = reminderRingtoneUri,
                completedMinutesToday = completedMinutesToday,
                dailyTargetMinutes = dailyTargetMinutes,
                freezeQuota = freezeQuota,
                streakDays = streakDays,
                bestStreakDays = bestStreakDays,
                isTodaySubmitted = isTodaySubmitted,
                selectedBluePalette = selectedBluePalette,
                reminderHour12 = reminderHour12,
                reminderMinute = reminderMinute,
                isReminderAm = isReminderAm,
                isReminderEnabled = isReminderEnabled,
                subjectOptions = subjectOptions,
                activeSubject = activeSubject,
                targets = targets,
                notes = notes,
                commitments = commitments,
                heatmapIntensities = heatmapIntensities,
                completedSessions = completedSessions,
                examSchedules = examSchedules,
                quickNotes = quickNotes,
                gradeTracks = gradeTracks
            )
        )
    }

    private fun takePersistablePermission(uri: Uri?) {
        if (uri != null) {
            runCatching {
                appContext.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }

    fun triggerClickSound() {
        FrebelSoundManager.playClick(isPlayfulSoundEnabled)
    }

    fun updateSettings(
        newName: String,
        newProfilePhoto: Uri?,
        newWishlistText: String,
        newWishlistPhoto: Uri?,
        darkMode: Boolean,
        subTheme: FrebelSubTheme,
        newSolidColor: FrebelSolidColorOption,
        newFontStyle: FrebelFontStyle,
        newTextColor: FrebelTextColorOption,
        newLanguage: FrebelLanguage,
        soundEnabled: Boolean,
        ringtoneUri: String
    ) {
        takePersistablePermission(newProfilePhoto)
        takePersistablePermission(newWishlistPhoto)

        studentName = newName.trim().ifEmpty { "Pelajar Frebel" }
        profilePhotoUri = newProfilePhoto
        wishlistText = newWishlistText.trim()
        wishlistPhotoUri = newWishlistPhoto
        isDarkMode = darkMode
        selectedSubTheme = subTheme
        solidColorOption = newSolidColor
        fontStyle = newFontStyle
        textColorOption = newTextColor
        language = newLanguage
        isPlayfulSoundEnabled = soundEnabled
        reminderRingtoneUri = ringtoneUri

        FrebelSoundManager.playPopSuccess(isPlayfulSoundEnabled)
        persist()
    }

    // --- Jadwal Ulangan (Beranda) ---
    fun addExamSchedule(subject: String, dayName: String, dayOfMonth: Int, monthName: String) {
        triggerClickSound()
        val newExam = ExamScheduleItem(
            id = System.currentTimeMillis().toString(),
            subject = subject.trim(),
            dayName = dayName.trim(),
            dayOfMonth = dayOfMonth.coerceIn(1, 31),
            monthName = monthName.trim(),
            isMarkedDone = false
        )
        examSchedules = listOf(newExam) + examSchedules
        persist()
    }

    fun toggleExamMarked(id: String) {
        triggerClickSound()
        examSchedules = examSchedules.map {
            if (it.id == id) it.copy(isMarkedDone = !it.isMarkedDone) else it
        }
        persist()
    }

    fun deleteExamSchedule(id: String) {
        triggerClickSound()
        examSchedules = examSchedules.filterNot { it.id == id }
        persist()
    }

    // --- Pop-up Catatan Hias + Foto ---
    fun addDecoratedQuickNote(
        title: String,
        content: String,
        styleIndex: Int,
        sticker: String,
        photoUri: Uri?
    ) {
        takePersistablePermission(photoUri)
        FrebelSoundManager.playPopSuccess(isPlayfulSoundEnabled)
        val note = DecoratedQuickNote(
            id = System.currentTimeMillis().toString(),
            title = title.trim().ifEmpty { "Catatan Belajar" },
            content = content.trim(),
            cardStyleIndex = styleIndex,
            stickerEmoji = sticker,
            photoUri = photoUri,
            timestampMillis = System.currentTimeMillis()
        )
        quickNotes = listOf(note) + quickNotes
        persist()
    }

    fun deleteDecoratedQuickNote(id: String) {
        triggerClickSound()
        quickNotes = quickNotes.filterNot { it.id == id }
        persist()
    }

    // --- Track Nilai (Ulangan Harian & UTS Sinkron Mapel) ---
    fun addGradeSubject(subjectName: String) {
        val clean = subjectName.trim()
        if (clean.isEmpty()) return
        triggerClickSound()
        if (gradeTracks.none { it.subjectName.equals(clean, ignoreCase = true) }) {
            gradeTracks = gradeTracks + SubjectGradeTrack(subjectName = clean)
            persist()
        }
    }

    fun deleteGradeSubject(subjectName: String) {
        triggerClickSound()
        gradeTracks = gradeTracks.filterNot { it.subjectName == subjectName }
        persist()
    }

    fun addDailyTestScore(subjectName: String, label: String, score: Double): String {
        val clampedScore = score.coerceIn(0.0, 100.0)
        val targetTrack = gradeTracks.firstOrNull { it.subjectName == subjectName } ?: return ""
        val previousScore = targetTrack.dailyScores.lastOrNull()?.score

        val newRecord = GradeScoreRecord(
            id = System.currentTimeMillis().toString(),
            label = label.trim().ifEmpty { "UH ${targetTrack.dailyScores.size + 1}" },
            score = clampedScore
        )

        gradeTracks = gradeTracks.map { track ->
            if (track.subjectName == subjectName) {
                track.copy(dailyScores = track.dailyScores + newRecord)
            } else {
                track
            }
        }

        val feedback = buildGradeValidationFeedback(
            subjectName = subjectName,
            examType = "Ulangan Harian",
            newScore = clampedScore,
            previousScore = previousScore
        )
        latestGradeFeedback = feedback
        FrebelSoundManager.playPopSuccess(isPlayfulSoundEnabled)
        persist()
        return feedback
    }

    fun addMidtermScore(subjectName: String, label: String, score: Double): String {
        val clampedScore = score.coerceIn(0.0, 100.0)
        val targetTrack = gradeTracks.firstOrNull { it.subjectName == subjectName } ?: return ""
        val previousScore = targetTrack.midtermScores.lastOrNull()?.score

        val newRecord = GradeScoreRecord(
            id = System.currentTimeMillis().toString(),
            label = label.trim().ifEmpty { "UTS ${targetTrack.midtermScores.size + 1}" },
            score = clampedScore
        )

        gradeTracks = gradeTracks.map { track ->
            if (track.subjectName == subjectName) {
                track.copy(midtermScores = track.midtermScores + newRecord)
            } else {
                track
            }
        }

        val feedback = buildGradeValidationFeedback(
            subjectName = subjectName,
            examType = "UTS",
            newScore = clampedScore,
            previousScore = previousScore
        )
        latestGradeFeedback = feedback
        FrebelSoundManager.playPopSuccess(isPlayfulSoundEnabled)
        persist()
        return feedback
    }

    private fun buildGradeValidationFeedback(
        subjectName: String,
        examType: String,
        newScore: Double,
        previousScore: Double?
    ): String {
        val scoreStr = if (newScore % 1.0 == 0.0) newScore.toInt().toString() else String.format(Locale.US, "%.1f", newScore)
        if (previousScore == null) {
            return when {
                newScore >= 85.0 ->
                    "Pencapaian awal hebat di $examType $subjectName ($scoreStr)! Pertahankan konsistensi latihanmu."
                newScore >= 75.0 ->
                    "Awal yang baik di $examType $subjectName ($scoreStr). Sedikit latihan ekstra bisa menembus 85+!"
                else ->
                    "Nilai awal $examType $subjectName ($scoreStr) perlu penguatan. Solusi: review materi dasar & buat ringkasan poin penting."
            }
        }

        val diff = newScore - previousScore
        val diffStr = if (kotlin.math.abs(diff) % 1.0 == 0.0) {
            kotlin.math.abs(diff).toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", kotlin.math.abs(diff))
        }

        return when {
            diff > 0 ->
                "Pencapaian Keren! Nilai $examType $subjectName NAIK +$diffStr poin menjadi $scoreStr. Bukti kerja kerasmu membuahkan hasil!"
            diff < 0 ->
                "Perhatian: Nilai $examType $subjectName TURUN -$diffStr poin ke $scoreStr. Koreksi & Solusi: evaluasi soal yang salah, tandai jadwal ulangan berikutnya di Beranda, dan fokuskan sesi Pomodoro 25 menit pada bab yang masih lemah."
            else ->
                "Nilai $examType $subjectName stabil di $scoreStr. Solusi: tambah variasi latihan soal agar skor berikutnya meningkat!"
        }
    }

    // --- Timer Terpadu (Stopwatch & Pomodoro + Istirahat 5-15 Menit) ---
    fun startIntegratedBreak(minutes: Int) {
        triggerClickSound()
        val validMinutes = minutes.coerceIn(5, 15)
        breakTargetMinutes = validMinutes
        breakRemainingSeconds = validMinutes * 60
        isBreakActive = true
        isStopwatchRunning = true
    }

    fun startPomodoroAutoBreak() {
        breakRemainingSeconds = breakTargetMinutes.coerceIn(5, 15) * 60
        isBreakActive = true
        isStopwatchRunning = true
    }

    fun finishIntegratedBreak(autoResumeStudy: Boolean) {
        isBreakActive = false
        breakRemainingSeconds = 0

        if (timerMode == StudyTimerMode.POMODORO &&
            elapsedSeconds >= pomodoroTargetMinutes * 60
        ) {
            isStopwatchRunning = false
            openCompletionDialog(elapsedSeconds)
        } else {
            isStopwatchRunning = autoResumeStudy
        }
    }

    fun updateDailyTarget(minutes: Int) {
        triggerClickSound()
        dailyTargetMinutes = minutes.coerceIn(30, 120)
        if (isReminderEnabled) {
            StudyReminderHelper.scheduleDailyReminder(
                appContext, reminderHour12, reminderMinute, isReminderAm, dailyTargetMinutes
            )
        }
        persist()
    }

    fun updateReminderTime(hour12: Int, minute: Int, isAm: Boolean) {
        triggerClickSound()
        reminderHour12 = hour12
        reminderMinute = minute
        isReminderAm = isAm
        if (isReminderEnabled) {
            StudyReminderHelper.scheduleDailyReminder(
                appContext, hour12, minute, isAm, dailyTargetMinutes
            )
        }
        persist()
    }

    fun toggleReminder(enabled: Boolean): String {
        triggerClickSound()
        isReminderEnabled = enabled
        val label = StudyReminderHelper.format12HourLabel(reminderHour12, reminderMinute, isReminderAm)
        if (enabled) {
            StudyReminderHelper.scheduleDailyReminder(
                appContext, reminderHour12, reminderMinute, isReminderAm, dailyTargetMinutes
            )
        } else {
            StudyReminderHelper.cancelDailyReminder(appContext)
        }
        persist()
        return if (enabled) "Pengingat belajar aktif setiap pukul $label!" else "Pengingat belajar dinonaktifkan."
    }

    fun toggleTarget(id: String, checked: Boolean) {
        triggerClickSound()
        targets = targets.map { if (it.id == id) it.copy(isCompleted = checked) else it }
        persist()
    }

    fun addTarget(label: String) {
        triggerClickSound()
        targets = targets + StudyTargetItem(System.currentTimeMillis().toString(), label, false)
        persist()
    }

    fun editTarget(item: StudyTargetItem, newText: String) {
        triggerClickSound()
        targets = targets.map { if (it.id == item.id) it.copy(subjectLabel = newText) else it }
        persist()
    }

    fun deleteTarget(id: String) {
        triggerClickSound()
        targets = targets.filterNot { it.id == id }
        persist()
    }

    fun selectSubject(subject: String) {
        triggerClickSound()
        activeSubject = subject
        persist()
    }

    fun addSubject(newSubject: String) {
        triggerClickSound()
        if (newSubject !in subjectOptions) {
            subjectOptions = subjectOptions + newSubject
        }
        activeSubject = newSubject
        persist()
    }

    fun deleteSubject(subject: String) {
        triggerClickSound()
        subjectOptions = subjectOptions.filterNot { it == subject }
        if (activeSubject == subject) {
            activeSubject = subjectOptions.firstOrNull() ?: "Belum dipilih"
        }
        persist()
    }

    fun openCompletionDialog(seconds: Int) {
        triggerClickSound()
        isStopwatchRunning = false
        isBreakActive = false
        breakRemainingSeconds = 0
        pendingCompletionSeconds = seconds
        showCompletionDialog = true
    }

    fun finalizeSessionSave(rating: Int, note: String, photoUri: Uri?): Int {
        takePersistablePermission(photoUri)
        FrebelSoundManager.playPopSuccess(isPlayfulSoundEnabled)

        val addedMinutes = (pendingCompletionSeconds / 60).coerceAtLeast(1)
        completedMinutesToday += addedMinutes

        val todayLevel = when {
            completedMinutesToday >= dailyTargetMinutes -> 4
            completedMinutesToday >= (dailyTargetMinutes * 0.66).toInt() -> 3
            completedMinutesToday >= (dailyTargetMinutes * 0.33).toInt() -> 2
            else -> 1
        }
        heatmapIntensities = heatmapIntensities.mapIndexed { index, level ->
            if (index == 34) todayLevel else level
        }

        val newSession = CompletedStudySession(
            id = System.currentTimeMillis().toString(),
            subject = activeSubject,
            durationMinutes = addedMinutes,
            focusRating = rating,
            note = note,
            photoUri = photoUri,
            modeLabel = if (timerMode == StudyTimerMode.POMODORO) "Pomodoro" else "Stopwatch",
            timestampMillis = System.currentTimeMillis()
        )
        completedSessions = listOf(newSession) + completedSessions

        showCompletionDialog = false
        pendingCompletionSeconds = 0
        elapsedSeconds = 0
        isBreakActive = false
        breakRemainingSeconds = 0

        if (!isTodaySubmitted) {
            isTodaySubmitted = true
            streakDays += 1
            if (streakDays > bestStreakDays) {
                bestStreakDays = streakDays
            }
        }

        persist()
        return addedMinutes
    }

    fun deleteSession(id: String) {
        triggerClickSound()
        completedSessions = completedSessions.filterNot { it.id == id }
        persist()
    }

    fun generateAutoFeedback(rawText: String): String {
        val text = rawText.lowercase(Locale.getDefault())
        return when {
            listOf("hp", "distraksi", "malas", "ngantuk", "capek", "lelah", "bosan", "susah fokus", "terganggu")
                .any { it in text } -> {
                "Evaluasi yang jujur! Coba gunakan siklus Pomodoro + istirahat 5-15 menit dan jauhkan notifikasi HP sejenak agar fokus kembali pulih."
            }
            listOf("evaluasi", "kurang", "perbaiki", "revisi", "besok", "tingkatkan", "belum paham", "kesulitan")
                .any { it in text } -> {
                "Refleksi yang sangat bagus! Menyadari bagian yang perlu diperbaiki adalah langkah kunci agar sesi belajar berikutnya jauh lebih efektif."
            }
            listOf("paham", "berhasil", "selesai", "lancar", "mantap", "bagus", "fokus", "meningkat", "tuntas")
                .any { it in text } -> {
                "Keren! Progres nyata seperti ini layak diapresiasi. Pertahankan pola belajarmu hari ini agar streak tetap menyala!"
            }
            listOf("ujian", "uts", "uas", "nilai", "lulus", "target", "juara", "impian", "semangat", "konsisten")
                .any { it in text } -> {
                "Target yang kuat bikin langkahmu terarah! Konsistensi belajar $dailyTargetMinutes menit/hari akan membawamu mendekati tujuan itu."
            }
            else -> {
                "Catatan tersimpan! Jadikan pengingat ini sebagai bahan bakar fokus sebelum kamu memulai sesi belajar berikutnya."
            }
        }
    }

    fun toggleNote(id: String, applied: Boolean) {
        triggerClickSound()
        notes = notes.map { if (it.id == id) it.copy(isApplied = applied) else it }
        persist()
    }

    fun addNote(text: String): String {
        FrebelSoundManager.playPopSuccess(isPlayfulSoundEnabled)
        val feedback = generateAutoFeedback(text)
        notes = listOf(
            StudyMotivationNote(
                id = System.currentTimeMillis().toString(),
                content = text,
                isApplied = false,
                autoFeedback = feedback
            )
        ) + notes
        persist()
        return feedback
    }

    fun editNote(item: StudyMotivationNote, newText: String): String {
        triggerClickSound()
        val updatedFeedback = generateAutoFeedback(newText)
        notes = notes.map {
            if (it.id == item.id) {
                it.copy(content = newText, autoFeedback = updatedFeedback)
            } else {
                it
            }
        }
        persist()
        return updatedFeedback
    }

    fun deleteNote(id: String) {
        triggerClickSound()
        notes = notes.filterNot { it.id == id }
        persist()
    }

    fun selectBluePalette(index: Int) {
        triggerClickSound()
        selectedBluePalette = index
        persist()
    }

    fun toggleCommitment(id: String, checked: Boolean) {
        triggerClickSound()
        commitments = commitments.map { if (it.id == id) it.copy(isChecked = checked) else it }
        persist()
    }

    fun addCommitment(text: String) {
        triggerClickSound()
        commitments = commitments + StreakCommitmentItem(System.currentTimeMillis().toString(), text, false)
        persist()
    }

    fun editCommitment(item: StreakCommitmentItem, newText: String) {
        triggerClickSound()
        commitments = commitments.map { if (it.id == item.id) it.copy(title = newText) else it }
        persist()
    }

    fun deleteCommitment(id: String) {
        triggerClickSound()
        commitments = commitments.filterNot { it.id == id }
        persist()
    }
}