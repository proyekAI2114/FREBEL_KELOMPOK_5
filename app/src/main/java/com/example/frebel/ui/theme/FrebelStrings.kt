package com.example.frebel.ui.theme

enum class FrebelLanguage(val code: String) {
    INDONESIAN("ID"),
    ENGLISH("EN");

    companion object {
        fun fromCode(code: String): FrebelLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: INDONESIAN
        }
    }
}

data class FrebelStrings(
    val tabHome: String,
    val tabStudy: String,
    val tabStats: String,
    val tabStreak: String,
    val quickNoteBtn: String,
    val settingsTitle: String,
    val uploadPhotoLabel: String,
    val profileNameLabel: String,
    val wishlistTitle: String,
    val wishlistHint: String,
    val darkModeLabel: String,
    val themeStyleLabel: String,
    val languageLabel: String,
    val playfulSoundLabel: String,
    val reminderRingtoneLabel: String,
    val saveSettingsBtn: String
)

fun getFrebelStrings(language: FrebelLanguage): FrebelStrings {
    return when (language) {
        FrebelLanguage.INDONESIAN -> FrebelStrings(
            tabHome = "Beranda",
            tabStudy = "Belajar",
            tabStats = "Statistik",
            tabStreak = "Streak",
            quickNoteBtn = "Catatan",
            settingsTitle = "Setelan Aplikasi",
            uploadPhotoLabel = "Ganti Foto Profil",
            profileNameLabel = "Nama Pelajar",
            wishlistTitle = "Wishlist / Impian",
            wishlistHint = "Tulis target kampus, nilai, atau impianmu...",
            darkModeLabel = "Mode Gelap",
            themeStyleLabel = "Gaya Tema Animasi",
            languageLabel = "Bahasa Aplikasi",
            playfulSoundLabel = "Suara Klik Tombol",
            reminderRingtoneLabel = "Nada Dering Pengingat",
            saveSettingsBtn = "Simpan Setelan"
        )

        FrebelLanguage.ENGLISH -> FrebelStrings(
            tabHome = "Home",
            tabStudy = "Study",
            tabStats = "Stats",
            tabStreak = "Streak",
            quickNoteBtn = "Notes",
            settingsTitle = "App Settings",
            uploadPhotoLabel = "Change Profile Photo",
            profileNameLabel = "Student Name",
            wishlistTitle = "Wishlist / Dreams",
            wishlistHint = "Write your dream school, target grade, or goal...",
            darkModeLabel = "Dark Mode",
            themeStyleLabel = "Animated Theme Style",
            languageLabel = "App Language",
            playfulSoundLabel = "Button Click Sound",
            reminderRingtoneLabel = "Reminder Ringtone",
            saveSettingsBtn = "Save Settings"
        )
    }
}