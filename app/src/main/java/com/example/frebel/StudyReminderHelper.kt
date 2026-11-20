package com.example.frebel

import android.Manifest
import android.R
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Calendar
import java.util.Locale

class StudyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val targetMinutes = intent.getIntExtra(StudyReminderHelper.EXTRA_TARGET_MINUTES, 60)
        val timeLabel = intent.getStringExtra(StudyReminderHelper.EXTRA_TIME_LABEL) ?: "Waktunya Belajar"

        StudyReminderHelper.showReminderNotification(
            context = context,
            title = "Waktunya Belajar di FREBEL!",
            message = "Jadwal $timeLabel telah tiba. Target belajarmu hari ini: $targetMinutes menit."
        )
    }
}

object StudyReminderHelper {
    private const val CHANNEL_ID = "frebel_study_reminder_channel"
    private const val CHANNEL_NAME = "Pengingat Jadwal Belajar"
    private const val REQUEST_CODE = 2026
    private const val NOTIFICATION_ID = 1001

    const val EXTRA_TARGET_MINUTES = "extra_target_minutes"
    const val EXTRA_TIME_LABEL = "extra_time_label"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat jadwal belajar harian FREBEL"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun convert12To24Hour(hour12: Int, isAm: Boolean): Int {
        val normalized = hour12.coerceIn(1, 12)
        return when {
            isAm && normalized == 12 -> 0
            isAm -> normalized
            !isAm && normalized == 12 -> 12
            else -> normalized + 12
        }
    }

    fun format12HourLabel(hour12: Int, minute: Int, isAm: Boolean): String {
        val period = if (isAm) "AM" else "PM"
        return String.format(Locale.US, "%02d:%02d %s", hour12, minute, period)
    }

    fun scheduleDailyReminder(
        context: Context,
        hour12: Int,
        minute: Int,
        isAm: Boolean,
        targetMinutes: Int
    ) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeLabel = format12HourLabel(hour12, minute, isAm)

        val intent = Intent(context, StudyReminderReceiver::class.java).apply {
            putExtra(EXTRA_TARGET_MINUTES, targetMinutes)
            putExtra(EXTRA_TIME_LABEL, timeLabel)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val hour24 = convert12To24Hour(hour12, isAm)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StudyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun showReminderNotification(
        context: Context,
        title: String,
        message: String
    ) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}