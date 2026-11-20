package com.example.frebel.ui.data

import android.content.Context
import android.net.Uri
import com.example.frebel.ui.component.CompletedStudySession
import com.example.frebel.ui.component.StreakCommitmentItem
import com.example.frebel.ui.component.StudyMotivationNote
import com.example.frebel.ui.component.StudyTargetItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ExamScheduleItem(
    val id: String,
    val subject: String,
    val dayName: String,
    val dayOfMonth: Int,
    val monthName: String,
    val isMarkedDone: Boolean = false
)

data class DecoratedQuickNote(
    val id: String,
    val title: String,
    val content: String,
    val cardStyleIndex: Int = 0,
    val stickerEmoji: String = "✨",
    val photoUri: Uri? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class GradeScoreRecord(
    val id: String,
    val label: String,
    val score: Double,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class SubjectGradeTrack(
    val subjectName: String,
    val dailyScores: List<GradeScoreRecord> = emptyList(),
    val midtermScores: List<GradeScoreRecord> = emptyList()
)

data class FrebelPersistedState(
    val studentName: String = "Pelajar Frebel",
    val profilePhotoUri: Uri? = null,
    val wishlistText: String = "",
    val wishlistPhotoUri: Uri? = null,
    val isDarkMode: Boolean = false,
    val subThemeId: String = "SOLID",
    val solidColorId: String = "SKY_MIST",
    val fontStyleId: String = "DEFAULT",
    val textColorId: String = "AUTO",
    val languageCode: String = "ID",
    val isPlayfulSoundEnabled: Boolean = true,
    val reminderRingtoneUri: String = "",
    val completedMinutesToday: Int = 0,
    val dailyTargetMinutes: Int = 60,
    val freezeQuota: Int = 0,
    val streakDays: Int = 0,
    val bestStreakDays: Int = 0,
    val isTodaySubmitted: Boolean = false,
    val selectedBluePalette: Int = 0,
    val reminderHour12: Int = 7,
    val reminderMinute: Int = 30,
    val isReminderAm: Boolean = false,
    val isReminderEnabled: Boolean = false,
    val subjectOptions: List<String> = emptyList(),
    val activeSubject: String = "Belum dipilih",
    val targets: List<StudyTargetItem> = emptyList(),
    val notes: List<StudyMotivationNote> = emptyList(),
    val commitments: List<StreakCommitmentItem> = emptyList(),
    val heatmapIntensities: List<Int> = List(35) { 0 },
    val completedSessions: List<CompletedStudySession> = emptyList(),
    val examSchedules: List<ExamScheduleItem> = emptyList(),
    val quickNotes: List<DecoratedQuickNote> = emptyList(),
    val gradeTracks: List<SubjectGradeTrack> = emptyList()
)

class FrebelStorageRepository(context: Context) {

    private val prefs = context.getSharedPreferences("frebel_app_prefs", Context.MODE_PRIVATE)

    private fun dateKeyForOffset(daysOffset: Int): String {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, daysOffset)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    fun loadState(): FrebelPersistedState {
        val today = dateKeyForOffset(0)
        val yesterday = dateKeyForOffset(-1)

        val savedActiveDate = prefs.getString("last_active_date", "") ?: ""
        val lastStudyDate = prefs.getString("last_study_date", "") ?: ""
        val isNewDay = savedActiveDate.isNotEmpty() && savedActiveDate != today

        val completedMinutes = if (lastStudyDate == today) {
            prefs.getInt("completed_minutes_today", 0)
        } else {
            0
        }

        val studiedToday = lastStudyDate == today && completedMinutes > 0
        val isStreakStillAlive = lastStudyDate == today || lastStudyDate == yesterday

        val savedStreak = prefs.getInt("streak_days", 0)
        val effectiveStreak = if (isStreakStillAlive) savedStreak else 0

        val subjects = decodeStringList(prefs.getString("subject_options", null))
        val activeSubj = prefs.getString("active_subject", null)
            ?: subjects.firstOrNull()
            ?: "Belum dipilih"

        val rawHeatmap = decodeIntList(prefs.getString("heatmap_intensities", null))
        val heatmap = if (rawHeatmap.size == 35) {
            if (isNewDay) rawHeatmap.drop(1) + 0 else rawHeatmap
        } else {
            List(35) { 0 }
        }

        val profileUriStr = prefs.getString("profile_photo_uri", "") ?: ""
        val wishlistUriStr = prefs.getString("wishlist_photo_uri", "") ?: ""

        return FrebelPersistedState(
            studentName = prefs.getString("student_name", "Pelajar Frebel") ?: "Pelajar Frebel",
            profilePhotoUri = if (profileUriStr.isNotBlank()) Uri.parse(profileUriStr) else null,
            wishlistText = prefs.getString("wishlist_text", "") ?: "",
            wishlistPhotoUri = if (wishlistUriStr.isNotBlank()) Uri.parse(wishlistUriStr) else null,
            isDarkMode = prefs.getBoolean("is_dark_mode", false),
            subThemeId = prefs.getString("sub_theme_id", "SOLID") ?: "SOLID",
            solidColorId = prefs.getString("solid_color_id", "SKY_MIST") ?: "SKY_MIST",
            fontStyleId = prefs.getString("font_style_id", "DEFAULT") ?: "DEFAULT",
            textColorId = prefs.getString("text_color_id", "AUTO") ?: "AUTO",
            languageCode = prefs.getString("language_code", "ID") ?: "ID",
            isPlayfulSoundEnabled = prefs.getBoolean("is_playful_sound_enabled", true),
            reminderRingtoneUri = prefs.getString("reminder_ringtone_uri", "") ?: "",
            completedMinutesToday = completedMinutes,
            dailyTargetMinutes = prefs.getInt("daily_target_minutes", 60).coerceIn(30, 120),
            freezeQuota = prefs.getInt("freeze_quota", 0),
            streakDays = effectiveStreak,
            bestStreakDays = prefs.getInt("best_streak_days", 0),
            isTodaySubmitted = studiedToday,
            selectedBluePalette = prefs.getInt("selected_blue_palette", 0),
            reminderHour12 = prefs.getInt("reminder_hour_12", 7).coerceIn(1, 12),
            reminderMinute = prefs.getInt("reminder_minute", 30).coerceIn(0, 59),
            isReminderAm = prefs.getBoolean("is_reminder_am", false),
            isReminderEnabled = prefs.getBoolean("is_reminder_enabled", false),
            subjectOptions = subjects,
            activeSubject = activeSubj,
            targets = decodeTargets(prefs.getString("targets_json", null)),
            notes = decodeNotes(prefs.getString("notes_json", null)),
            commitments = decodeCommitments(prefs.getString("commitments_json", null)),
            heatmapIntensities = heatmap,
            completedSessions = decodeSessions(prefs.getString("sessions_json", null)),
            examSchedules = decodeExams(prefs.getString("exams_json", null)),
            quickNotes = decodeQuickNotes(prefs.getString("quick_notes_json", null)),
            gradeTracks = decodeGradeTracks(prefs.getString("grade_tracks_json", null))
        )
    }

    fun saveState(state: FrebelPersistedState) {
        val today = dateKeyForOffset(0)
        val existingLastStudyDate = prefs.getString("last_study_date", "") ?: ""
        val updatedLastStudyDate = if (state.isTodaySubmitted && state.completedMinutesToday > 0) {
            today
        } else {
            existingLastStudyDate
        }

        prefs.edit()
            .putString("last_active_date", today)
            .putString("last_study_date", updatedLastStudyDate)
            .putString("student_name", state.studentName)
            .putString("profile_photo_uri", state.profilePhotoUri?.toString() ?: "")
            .putString("wishlist_text", state.wishlistText)
            .putString("wishlist_photo_uri", state.wishlistPhotoUri?.toString() ?: "")
            .putBoolean("is_dark_mode", state.isDarkMode)
            .putString("sub_theme_id", state.subThemeId)
            .putString("solid_color_id", state.solidColorId)
            .putString("font_style_id", state.fontStyleId)
            .putString("text_color_id", state.textColorId)
            .putString("language_code", state.languageCode)
            .putBoolean("is_playful_sound_enabled", state.isPlayfulSoundEnabled)
            .putString("reminder_ringtone_uri", state.reminderRingtoneUri)
            .putInt("completed_minutes_today", state.completedMinutesToday)
            .putInt("daily_target_minutes", state.dailyTargetMinutes)
            .putInt("freeze_quota", state.freezeQuota)
            .putInt("streak_days", state.streakDays)
            .putInt("best_streak_days", state.bestStreakDays)
            .putBoolean("is_today_submitted", state.isTodaySubmitted)
            .putInt("selected_blue_palette", state.selectedBluePalette)
            .putInt("reminder_hour_12", state.reminderHour12)
            .putInt("reminder_minute", state.reminderMinute)
            .putBoolean("is_reminder_am", state.isReminderAm)
            .putBoolean("is_reminder_enabled", state.isReminderEnabled)
            .putString("subject_options", encodeStringList(state.subjectOptions))
            .putString("active_subject", state.activeSubject)
            .putString("targets_json", encodeTargets(state.targets))
            .putString("notes_json", encodeNotes(state.notes))
            .putString("commitments_json", encodeCommitments(state.commitments))
            .putString("heatmap_intensities", encodeIntList(state.heatmapIntensities))
            .putString("sessions_json", encodeSessions(state.completedSessions))
            .putString("exams_json", encodeExams(state.examSchedules))
            .putString("quick_notes_json", encodeQuickNotes(state.quickNotes))
            .putString("grade_tracks_json", encodeGradeTracks(state.gradeTracks))
            .apply()
    }

    private fun encodeStringList(list: List<String>): String {
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        return arr.toString()
    }

    private fun decodeStringList(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { arr.getString(it) }
        }.getOrDefault(emptyList())
    }

    private fun encodeIntList(list: List<Int>): String {
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        return arr.toString()
    }

    private fun decodeIntList(raw: String?): List<Int> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { arr.getInt(it) }
        }.getOrDefault(emptyList())
    }

    private fun encodeTargets(list: List<StudyTargetItem>): String {
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("subjectLabel", item.subjectLabel)
                    put("isCompleted", item.isCompleted)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeTargets(raw: String?): List<StudyTargetItem> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                StudyTargetItem(
                    id = obj.getString("id"),
                    subjectLabel = obj.getString("subjectLabel"),
                    isCompleted = obj.getBoolean("isCompleted")
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeNotes(list: List<StudyMotivationNote>): String {
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("content", item.content)
                    put("isApplied", item.isApplied)
                    put("autoFeedback", item.autoFeedback)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeNotes(raw: String?): List<StudyMotivationNote> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                StudyMotivationNote(
                    id = obj.getString("id"),
                    content = obj.getString("content"),
                    isApplied = obj.getBoolean("isApplied"),
                    autoFeedback = obj.optString("autoFeedback", "")
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeCommitments(list: List<StreakCommitmentItem>): String {
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("isChecked", item.isChecked)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeCommitments(raw: String?): List<StreakCommitmentItem> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                StreakCommitmentItem(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    isChecked = obj.getBoolean("isChecked")
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeSessions(list: List<CompletedStudySession>): String {
        val arr = JSONArray()
        list.forEach { session ->
            arr.put(
                JSONObject().apply {
                    put("id", session.id)
                    put("subject", session.subject)
                    put("durationMinutes", session.durationMinutes)
                    put("focusRating", session.focusRating)
                    put("note", session.note)
                    put("photoUri", session.photoUri?.toString() ?: "")
                    put("modeLabel", session.modeLabel)
                    put("timestampMillis", session.timestampMillis)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeSessions(raw: String?): List<CompletedStudySession> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                val uriStr = obj.optString("photoUri", "")
                CompletedStudySession(
                    id = obj.getString("id"),
                    subject = obj.getString("subject"),
                    durationMinutes = obj.getInt("durationMinutes"),
                    focusRating = obj.getInt("focusRating"),
                    note = obj.optString("note", ""),
                    photoUri = if (uriStr.isNotBlank()) Uri.parse(uriStr) else null,
                    modeLabel = obj.optString("modeLabel", "Stopwatch"),
                    timestampMillis = obj.optLong("timestampMillis", System.currentTimeMillis())
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeExams(list: List<ExamScheduleItem>): String {
        val arr = JSONArray()
        list.forEach { exam ->
            arr.put(
                JSONObject().apply {
                    put("id", exam.id)
                    put("subject", exam.subject)
                    put("dayName", exam.dayName)
                    put("dayOfMonth", exam.dayOfMonth)
                    put("monthName", exam.monthName)
                    put("isMarkedDone", exam.isMarkedDone)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeExams(raw: String?): List<ExamScheduleItem> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                ExamScheduleItem(
                    id = obj.getString("id"),
                    subject = obj.getString("subject"),
                    dayName = obj.getString("dayName"),
                    dayOfMonth = obj.getInt("dayOfMonth"),
                    monthName = obj.getString("monthName"),
                    isMarkedDone = obj.optBoolean("isMarkedDone", false)
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeQuickNotes(list: List<DecoratedQuickNote>): String {
        val arr = JSONArray()
        list.forEach { note ->
            arr.put(
                JSONObject().apply {
                    put("id", note.id)
                    put("title", note.title)
                    put("content", note.content)
                    put("cardStyleIndex", note.cardStyleIndex)
                    put("stickerEmoji", note.stickerEmoji)
                    put("photoUri", note.photoUri?.toString() ?: "")
                    put("timestampMillis", note.timestampMillis)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeQuickNotes(raw: String?): List<DecoratedQuickNote> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                val uriStr = obj.optString("photoUri", "")
                DecoratedQuickNote(
                    id = obj.getString("id"),
                    title = obj.optString("title", "Catatan"),
                    content = obj.optString("content", ""),
                    cardStyleIndex = obj.optInt("cardStyleIndex", 0),
                    stickerEmoji = obj.optString("stickerEmoji", "✨"),
                    photoUri = if (uriStr.isNotBlank()) Uri.parse(uriStr) else null,
                    timestampMillis = obj.optLong("timestampMillis", System.currentTimeMillis())
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeGradeTracks(list: List<SubjectGradeTrack>): String {
        val arr = JSONArray()
        list.forEach { track ->
            val dailyArr = JSONArray()
            track.dailyScores.forEach { rec ->
                dailyArr.put(
                    JSONObject().apply {
                        put("id", rec.id)
                        put("label", rec.label)
                        put("score", rec.score)
                        put("timestampMillis", rec.timestampMillis)
                    }
                )
            }
            val midArr = JSONArray()
            track.midtermScores.forEach { rec ->
                midArr.put(
                    JSONObject().apply {
                        put("id", rec.id)
                        put("label", rec.label)
                        put("score", rec.score)
                        put("timestampMillis", rec.timestampMillis)
                    }
                )
            }
            arr.put(
                JSONObject().apply {
                    put("subjectName", track.subjectName)
                    put("dailyScores", dailyArr)
                    put("midtermScores", midArr)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeGradeTracks(raw: String?): List<SubjectGradeTrack> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                val dailyArr = obj.optJSONArray("dailyScores") ?: JSONArray()
                val midArr = obj.optJSONArray("midtermScores") ?: JSONArray()

                val dailyList = List(dailyArr.length()) { j ->
                    val r = dailyArr.getJSONObject(j)
                    GradeScoreRecord(
                        id = r.getString("id"),
                        label = r.getString("label"),
                        score = r.getDouble("score"),
                        timestampMillis = r.optLong("timestampMillis", System.currentTimeMillis())
                    )
                }
                val midList = List(midArr.length()) { j ->
                    val r = midArr.getJSONObject(j)
                    GradeScoreRecord(
                        id = r.getString("id"),
                        label = r.getString("label"),
                        score = r.getDouble("score"),
                        timestampMillis = r.optLong("timestampMillis", System.currentTimeMillis())
                    )
                }

                SubjectGradeTrack(
                    subjectName = obj.getString("subjectName"),
                    dailyScores = dailyList,
                    midtermScores = midList
                )
            }
        }.getOrDefault(emptyList())
    }
}