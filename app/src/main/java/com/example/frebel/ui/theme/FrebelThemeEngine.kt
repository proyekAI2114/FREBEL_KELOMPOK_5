package com.example.frebel.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class FrebelSubTheme(
    val id: String,
    val emoji: String,
    val labelId: String,
    val labelEn: String
) {
    SOLID("SOLID", "🎨", "Solid Minimal", "Solid Minimal"),
    CYBER("CYBER", "⚡", "Cyber Neon", "Cyber Neon"),
    NATURE("NATURE", "🍃", "Nature Forest", "Nature Forest"),
    SPACE("SPACE", "🌌", "Space Galaxy", "Space Galaxy"),
    OCEAN("OCEAN", "🌊", "Calm Ocean", "Calm Ocean"),
    LOVE("LOVE", "💖", "Love Pastel", "Love Pastel"),
    COOKIES("COOKIES", "🍪", "Choco Cookies", "Choco Cookies"),
    BEAR("BEAR", "🧸", "Cozy Bear", "Cozy Bear"),
    FAIRY("FAIRY", "🧚", "Fairy Glow", "Fairy Glow"),
    SKY("SKY", "☁️", "Soft Sky", "Soft Sky"),
    Y2K("Y2K", "💿", "Y2K Dream", "Y2K Dream"),
    VINTAGE("VINTAGE", "📜", "Warm Vintage", "Warm Vintage");

    companion object {
        fun fromId(id: String): FrebelSubTheme {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: SOLID
        }
    }
}

enum class FrebelSolidColorOption(
    val id: String,
    val label: String,
    val primaryLight: Color,
    val secondaryLight: Color,
    val bgLight: Color,
    val primaryDark: Color,
    val secondaryDark: Color,
    val bgDark: Color
) {
    SKY_MIST(
        id = "SKY_MIST",
        label = "Sky Mist",
        primaryLight = Color(0xFF0284C7),
        secondaryLight = Color(0xFF38BDF8),
        bgLight = Color(0xFFF0F8FF),
        primaryDark = Color(0xFF38BDF8),
        secondaryDark = Color(0xFF7DD3FC),
        bgDark = Color(0xFF0D1824)
    ),
    SAGE_MATCHA(
        id = "SAGE_MATCHA",
        label = "Sage Matcha",
        primaryLight = Color(0xFF2E7D5B),
        secondaryLight = Color(0xFF52B788),
        bgLight = Color(0xFFF2FAF5),
        primaryDark = Color(0xFF52B788),
        secondaryDark = Color(0xFF95D5B2),
        bgDark = Color(0xFF0F1D17)
    ),
    LAVENDER_CLOUD(
        id = "LAVENDER_CLOUD",
        label = "Lavender",
        primaryLight = Color(0xFF7C3AED),
        secondaryLight = Color(0xFFA78BFA),
        bgLight = Color(0xFFF6F3FF),
        primaryDark = Color(0xFFA78BFA),
        secondaryDark = Color(0xFFC4B5FD),
        bgDark = Color(0xFF161124)
    ),
    PEACH_CREAM(
        id = "PEACH_CREAM",
        label = "Peach Cream",
        primaryLight = Color(0xFFD97736),
        secondaryLight = Color(0xFFF59E6C),
        bgLight = Color(0xFFFFF7F0),
        primaryDark = Color(0xFFF59E6C),
        secondaryDark = Color(0xFFFDBA74),
        bgDark = Color(0xFF211610)
    ),
    ROSE_MILK(
        id = "ROSE_MILK",
        label = "Rose Milk",
        primaryLight = Color(0xFFD6457C),
        secondaryLight = Color(0xFFF472B6),
        bgLight = Color(0xFFFFF2F7),
        primaryDark = Color(0xFFF472B6),
        secondaryDark = Color(0xFFF9A8D4),
        bgDark = Color(0xFF211119)
    ),
    BUTTER_HONEY(
        id = "BUTTER_HONEY",
        label = "Butter Honey",
        primaryLight = Color(0xFFC2851C),
        secondaryLight = Color(0xFFEAB308),
        bgLight = Color(0xFFFEFCEB),
        primaryDark = Color(0xFFFACC15),
        secondaryDark = Color(0xFFFDE047),
        bgDark = Color(0xFF1E1A0E)
    ),
    OCEAN_MINT(
        id = "OCEAN_MINT",
        label = "Ocean Mint",
        primaryLight = Color(0xFF0D9488),
        secondaryLight = Color(0xFF2DD4BF),
        bgLight = Color(0xFFF0FDFA),
        primaryDark = Color(0xFF2DD4BF),
        secondaryDark = Color(0xFF5EEAD4),
        bgDark = Color(0xFF0D1E1D)
    ),
    PERIWINKLE(
        id = "PERIWINKLE",
        label = "Periwinkle",
        primaryLight = Color(0xFF4F46E5),
        secondaryLight = Color(0xFF818CF8),
        bgLight = Color(0xFFF1F4FF),
        primaryDark = Color(0xFF818CF8),
        secondaryDark = Color(0xFFA5B4FC),
        bgDark = Color(0xFF121426)
    );

    companion object {
        fun fromId(id: String): FrebelSolidColorOption {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: SKY_MIST
        }
    }
}

enum class FrebelFontStyle(
    val id: String,
    val labelId: String,
    val labelEn: String,
    val fontFamily: FontFamily
) {
    DEFAULT("DEFAULT", "Cozy Bulat", "Cozy Round", FontFamily.Default),
    SANS("SANS", "Modern Rapi", "Clean Sans", FontFamily.SansSerif),
    SERIF("SERIF", "Klasik Buku", "Book Serif", FontFamily.Serif),
    MONO("MONO", "Mesin Ketik", "Typewriter", FontFamily.Monospace),
    CURSIVE("CURSIVE", "Tulis Tangan", "Handwritten", FontFamily.Cursive);

    companion object {
        fun fromId(id: String): FrebelFontStyle {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: DEFAULT
        }
    }
}

enum class FrebelTextColorOption(
    val id: String,
    val labelId: String,
    val labelEn: String,
    val swatchColor: Color,
    val lightPrimary: Color,
    val lightSecondary: Color,
    val darkPrimary: Color,
    val darkSecondary: Color
) {
    AUTO(
        id = "AUTO",
        labelId = "Otomatis",
        labelEn = "Auto Theme",
        swatchColor = Color(0xFF64748B),
        lightPrimary = Color.Unspecified,
        lightSecondary = Color.Unspecified,
        darkPrimary = Color.Unspecified,
        darkSecondary = Color.Unspecified
    ),
    CHARCOAL(
        id = "CHARCOAL",
        labelId = "Soft Slate",
        labelEn = "Soft Slate",
        swatchColor = Color(0xFF334155),
        lightPrimary = Color(0xFF1E293B),
        lightSecondary = Color(0xFF475569),
        darkPrimary = Color(0xFFF1F5F9),
        darkSecondary = Color(0xFFCBD5E1)
    ),
    COCOA(
        id = "COCOA",
        labelId = "Warm Cocoa",
        labelEn = "Warm Cocoa",
        swatchColor = Color(0xFF6D4C41),
        lightPrimary = Color(0xFF3E2723),
        lightSecondary = Color(0xFF6D4C41),
        darkPrimary = Color(0xFFFFECB3),
        darkSecondary = Color(0xFFD7CCC8)
    ),
    NAVY(
        id = "NAVY",
        labelId = "Deep Ocean",
        labelEn = "Deep Ocean",
        swatchColor = Color(0xFF0369A1),
        lightPrimary = Color(0xFF0C4A6E),
        lightSecondary = Color(0xFF0284C7),
        darkPrimary = Color(0xFFBAE6FD),
        darkSecondary = Color(0xFF7DD3FC)
    ),
    MATCHA(
        id = "MATCHA",
        labelId = "Forest Sage",
        labelEn = "Forest Sage",
        swatchColor = Color(0xFF2E7D5B),
        lightPrimary = Color(0xFF14532D),
        lightSecondary = Color(0xFF2E7D5B),
        darkPrimary = Color(0xFFBBF7D0),
        darkSecondary = Color(0xFF86EFAC)
    ),
    PLUM(
        id = "PLUM",
        labelId = "Soft Plum",
        labelEn = "Soft Plum",
        swatchColor = Color(0xFF6D28D9),
        lightPrimary = Color(0xFF4C1D95),
        lightSecondary = Color(0xFF6D28D9),
        darkPrimary = Color(0xFFE9D5FF),
        darkSecondary = Color(0xFFD8B4FE)
    ),
    ROSEWOOD(
        id = "ROSEWOOD",
        labelId = "Rose Berry",
        labelEn = "Rose Berry",
        swatchColor = Color(0xFFBE185D),
        lightPrimary = Color(0xFF831843),
        lightSecondary = Color(0xFFBE185D),
        darkPrimary = Color(0xFFFBCFE8),
        darkSecondary = Color(0xFFF9A8D4)
    );

    companion object {
        fun fromId(id: String): FrebelTextColorOption {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: AUTO
        }
    }
}

data class FrebelThemeSpec(
    val isDark: Boolean,
    val subTheme: FrebelSubTheme,
    val solidColor: FrebelSolidColorOption,
    val fontStyle: FrebelFontStyle,
    val textColorOption: FrebelTextColorOption,
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val cardSurface: Color,
    val cardBorder: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

fun buildFrebelTypography(fontStyle: FrebelFontStyle): Typography {
    val family = fontStyle.fontFamily
    val tnum = "tnum"
    return Typography(
        displayLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Black, fontSize = 44.sp, lineHeight = 52.sp, fontFeatureSettings = tnum),
        displayMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, lineHeight = 44.sp, fontFeatureSettings = tnum),
        displaySmall = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 38.sp, fontFeatureSettings = tnum),
        headlineLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, lineHeight = 34.sp, fontFeatureSettings = tnum),
        headlineMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp, fontFeatureSettings = tnum),
        headlineSmall = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp, fontFeatureSettings = tnum),
        titleLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, lineHeight = 25.sp, fontFeatureSettings = tnum),
        titleMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp, fontFeatureSettings = tnum),
        titleSmall = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp, fontFeatureSettings = tnum),
        bodyLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
        bodyMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
        labelLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp, fontFeatureSettings = tnum),
        labelMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, fontFeatureSettings = tnum),
        labelSmall = TextStyle(fontFamily = family, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, fontFeatureSettings = tnum)
    )
}

fun resolveFrebelThemeSpec(
    isDarkMode: Boolean,
    subTheme: FrebelSubTheme,
    solidColor: FrebelSolidColorOption = FrebelSolidColorOption.SKY_MIST,
    fontStyle: FrebelFontStyle = FrebelFontStyle.DEFAULT,
    textColorOption: FrebelTextColorOption = FrebelTextColorOption.AUTO
): FrebelThemeSpec {
    val base = when (subTheme) {
        FrebelSubTheme.SOLID -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true,
                subTheme = subTheme,
                solidColor = solidColor,
                fontStyle = fontStyle,
                textColorOption = textColorOption,
                backgroundTop = solidColor.bgDark,
                backgroundBottom = solidColor.bgDark.copy(alpha = 0.92f),
                cardSurface = Color(0xFF161E2E).copy(alpha = 0.92f),
                cardBorder = solidColor.primaryDark.copy(alpha = 0.28f),
                primaryAccent = solidColor.primaryDark,
                secondaryAccent = solidColor.secondaryDark,
                textPrimary = Color(0xFFF1F5F9),
                textSecondary = Color(0xFF94A3B8)
            )
        } else {
            FrebelThemeSpec(
                isDark = false,
                subTheme = subTheme,
                solidColor = solidColor,
                fontStyle = fontStyle,
                textColorOption = textColorOption,
                backgroundTop = solidColor.bgLight,
                backgroundBottom = Color(0xFFFFFFFF),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f),
                cardBorder = solidColor.primaryLight.copy(alpha = 0.22f),
                primaryAccent = solidColor.primaryLight,
                secondaryAccent = solidColor.secondaryLight,
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF475569)
            )
        }

        FrebelSubTheme.CYBER -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true,
                subTheme = subTheme,
                solidColor = solidColor,
                fontStyle = fontStyle,
                textColorOption = textColorOption,
                backgroundTop = Color(0xFF070B14),
                backgroundBottom = Color(0xFF020409),
                cardSurface = Color(0xFF0E1626).copy(alpha = 0.92f),
                cardBorder = Color(0xFF00F0FF).copy(alpha = 0.38f),
                primaryAccent = Color(0xFF00F0FF),
                secondaryAccent = Color(0xFFFF007F),
                textPrimary = Color(0xFFF0FDFF),
                textSecondary = Color(0xFF7DD3FC)
            )
        } else {
            FrebelThemeSpec(
                isDark = false,
                subTheme = subTheme,
                solidColor = solidColor,
                fontStyle = fontStyle,
                textColorOption = textColorOption,
                backgroundTop = Color(0xFFF0F9FF),
                backgroundBottom = Color(0xFFE0F2FE),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f),
                cardBorder = Color(0xFF0284C7).copy(alpha = 0.28f),
                primaryAccent = Color(0xFF0284C7),
                secondaryAccent = Color(0xFFE11D48),
                textPrimary = Color(0xFF082F49),
                textSecondary = Color(0xFF0369A1)
            )
        }

        FrebelSubTheme.NATURE -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true,
                subTheme = subTheme,
                solidColor = solidColor,
                fontStyle = fontStyle,
                textColorOption = textColorOption,
                backgroundTop = Color(0xFF0A1810),
                backgroundBottom = Color(0xFF050E09),
                cardSurface = Color(0xFF11261B).copy(alpha = 0.92f),
                cardBorder = Color(0xFF10B981).copy(alpha = 0.32f),
                primaryAccent = Color(0xFF10B981),
                secondaryAccent = Color(0xFFF59E0B),
                textPrimary = Color(0xFFECFDF5),
                textSecondary = Color(0xFFA7F3D0)
            )
        } else {
            FrebelThemeSpec(
                isDark = false,
                subTheme = subTheme,
                solidColor = solidColor,
                fontStyle = fontStyle,
                textColorOption = textColorOption,
                backgroundTop = Color(0xFFF0FDF4),
                backgroundBottom = Color(0xFFDCFCE7),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f),
                cardBorder = Color(0xFF059669).copy(alpha = 0.24f),
                primaryAccent = Color(0xFF059669),
                secondaryAccent = Color(0xFFD97706),
                textPrimary = Color(0xFF022C22),
                textSecondary = Color(0xFF047857)
            )
        }

        FrebelSubTheme.SPACE -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true,
                subTheme = subTheme,
                solidColor = solidColor,
                fontStyle = fontStyle,
                textColorOption = textColorOption,
                backgroundTop = Color(0xFF0A0A1C),
                backgroundBottom = Color(0xFF04040E),
                cardSurface = Color(0xFF141432).copy(alpha = 0.92f),
                cardBorder = Color(0xFF818CF8).copy(alpha = 0.35f),
                primaryAccent = Color(0xFF818CF8),
                secondaryAccent = Color(0xFFC084FC),
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFFC7D2FE)
            )
        } else {
            FrebelThemeSpec(
                isDark = false,
                subTheme = subTheme,
                solidColor = solidColor,
                fontStyle = fontStyle,
                textColorOption = textColorOption,
                backgroundTop = Color(0xFFF5F3FF),
                backgroundBottom = Color(0xFFEDE9FE),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f),
                cardBorder = Color(0xFF6366F1).copy(alpha = 0.24f),
                primaryAccent = Color(0xFF4F46E5),
                secondaryAccent = Color(0xFF7C3AED),
                textPrimary = Color(0xFF1E1B4B),
                textSecondary = Color(0xFF4338CA)
            )
        }

        FrebelSubTheme.OCEAN -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFF091A23), backgroundBottom = Color(0xFF040E14),
                cardSurface = Color(0xFF0E2530).copy(alpha = 0.92f), cardBorder = Color(0xFF22D3EE).copy(alpha = 0.32f),
                primaryAccent = Color(0xFF22D3EE), secondaryAccent = Color(0xFF38BDF8),
                textPrimary = Color(0xFFECFEFF), textSecondary = Color(0xFFA5F3FC)
            )
        } else {
            FrebelThemeSpec(
                isDark = false, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFFECFEFF), backgroundBottom = Color(0xFFE0F2FE),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f), cardBorder = Color(0xFF0891B2).copy(alpha = 0.22f),
                primaryAccent = Color(0xFF0891B2), secondaryAccent = Color(0xFF0284C7),
                textPrimary = Color(0xFF042F2E), textSecondary = Color(0xFF155E75)
            )
        }

        FrebelSubTheme.LOVE -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFF23131D), backgroundBottom = Color(0xFF140910),
                cardSurface = Color(0xFF2E1726).copy(alpha = 0.92f), cardBorder = Color(0xFFF472B6).copy(alpha = 0.32f),
                primaryAccent = Color(0xFFF472B6), secondaryAccent = Color(0xFFFB7185),
                textPrimary = Color(0xFFFDF2F8), textSecondary = Color(0xFFF9A8D4)
            )
        } else {
            FrebelThemeSpec(
                isDark = false, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFFFFF1F6), backgroundBottom = Color(0xFFFFE4EF),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f), cardBorder = Color(0xFFF472B6).copy(alpha = 0.24f),
                primaryAccent = Color(0xFFDB2777), secondaryAccent = Color(0xFFF43F5E),
                textPrimary = Color(0xFF4C0519), textSecondary = Color(0xFF9D174D)
            )
        }

        FrebelSubTheme.COOKIES -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFF211710), backgroundBottom = Color(0xFF120B07),
                cardSurface = Color(0xFF2D1E16).copy(alpha = 0.92f), cardBorder = Color(0xFFF59E0B).copy(alpha = 0.32f),
                primaryAccent = Color(0xFFF59E0B), secondaryAccent = Color(0xFFD97706),
                textPrimary = Color(0xFFFEF3C7), textSecondary = Color(0xFFD6B99C)
            )
        } else {
            FrebelThemeSpec(
                isDark = false, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFFFCF6EE), backgroundBottom = Color(0xFFF6EAD9),
                cardSurface = Color(0xFFFFFDF9).copy(alpha = 0.95f), cardBorder = Color(0xFFB45309).copy(alpha = 0.22f),
                primaryAccent = Color(0xFFB45309), secondaryAccent = Color(0xFFD97706),
                textPrimary = Color(0xFF3E2723), textSecondary = Color(0xFF78553A)
            )
        }

        FrebelSubTheme.BEAR -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFF1E1712), backgroundBottom = Color(0xFF100B07),
                cardSurface = Color(0xFF2A1E17).copy(alpha = 0.92f), cardBorder = Color(0xFFE09F67).copy(alpha = 0.32f),
                primaryAccent = Color(0xFFE09F67), secondaryAccent = Color(0xFFC08457),
                textPrimary = Color(0xFFFAF3EB), textSecondary = Color(0xFFD6C2B0)
            )
        } else {
            FrebelThemeSpec(
                isDark = false, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFFFAF3EA), backgroundBottom = Color(0xFFF3E5D5),
                cardSurface = Color(0xFFFFFCF8).copy(alpha = 0.95f), cardBorder = Color(0xFF9C6644).copy(alpha = 0.22f),
                primaryAccent = Color(0xFF9C6644), secondaryAccent = Color(0xFFC08457),
                textPrimary = Color(0xFF3D2B1F), textSecondary = Color(0xFF7F5539)
            )
        }

        FrebelSubTheme.FAIRY -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFF181125), backgroundBottom = Color(0xFF0D0817),
                cardSurface = Color(0xFF241838).copy(alpha = 0.92f), cardBorder = Color(0xFFC084FC).copy(alpha = 0.32f),
                primaryAccent = Color(0xFFC084FC), secondaryAccent = Color(0xFF67E8F9),
                textPrimary = Color(0xFFFAF5FF), textSecondary = Color(0xFFD8B4FE)
            )
        } else {
            FrebelThemeSpec(
                isDark = false, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFFF7F2FF), backgroundBottom = Color(0xFFEDE9FE),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f), cardBorder = Color(0xFFA855F7).copy(alpha = 0.22f),
                primaryAccent = Color(0xFF9333EA), secondaryAccent = Color(0xFF06B6D4),
                textPrimary = Color(0xFF3B0764), textSecondary = Color(0xFF6B21A8)
            )
        }

        FrebelSubTheme.SKY -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFF0F1A2A), backgroundBottom = Color(0xFF080F1B),
                cardSurface = Color(0xFF15253C).copy(alpha = 0.92f), cardBorder = Color(0xFF60A5FA).copy(alpha = 0.32f),
                primaryAccent = Color(0xFF60A5FA), secondaryAccent = Color(0xFF93C5FD),
                textPrimary = Color(0xFFEFF6FF), textSecondary = Color(0xFFBFDBFE)
            )
        } else {
            FrebelThemeSpec(
                isDark = false, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFFEFF6FF), backgroundBottom = Color(0xFFDBEAFE),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f), cardBorder = Color(0xFF3B82F6).copy(alpha = 0.20f),
                primaryAccent = Color(0xFF2563EB), secondaryAccent = Color(0xFF38BDF8),
                textPrimary = Color(0xFF1E3A8A), textSecondary = Color(0xFF3B82F6)
            )
        }

        FrebelSubTheme.Y2K -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFF141029), backgroundBottom = Color(0xFF0B081A),
                cardSurface = Color(0xFF20193E).copy(alpha = 0.92f), cardBorder = Color(0xFFF472B6).copy(alpha = 0.35f),
                primaryAccent = Color(0xFFF472B6), secondaryAccent = Color(0xFF22D3EE),
                textPrimary = Color(0xFFF8FAFC), textSecondary = Color(0xFFC7D2FE)
            )
        } else {
            FrebelThemeSpec(
                isDark = false, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFFF5F3FF), backgroundBottom = Color(0xFFFCE7F3),
                cardSurface = Color(0xFFFFFFFF).copy(alpha = 0.95f), cardBorder = Color(0xFFEC4899).copy(alpha = 0.22f),
                primaryAccent = Color(0xFFEC4899), secondaryAccent = Color(0xFF06B6D4),
                textPrimary = Color(0xFF311042), textSecondary = Color(0xFF6D28D9)
            )
        }

        FrebelSubTheme.VINTAGE -> if (isDarkMode) {
            FrebelThemeSpec(
                isDark = true, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFF1E1812), backgroundBottom = Color(0xFF110C07),
                cardSurface = Color(0xFF2B2118).copy(alpha = 0.92f), cardBorder = Color(0xFFD4A373).copy(alpha = 0.32f),
                primaryAccent = Color(0xFFD4A373), secondaryAccent = Color(0xFFE9C46A),
                textPrimary = Color(0xFFFAF0E6), textSecondary = Color(0xFFD6C7B2)
            )
        } else {
            FrebelThemeSpec(
                isDark = false, subTheme = subTheme, solidColor = solidColor, fontStyle = fontStyle, textColorOption = textColorOption,
                backgroundTop = Color(0xFFF8F3EA), backgroundBottom = Color(0xFFEFE6D5),
                cardSurface = Color(0xFFFFFCF7).copy(alpha = 0.95f), cardBorder = Color(0xFF8C6239).copy(alpha = 0.22f),
                primaryAccent = Color(0xFF8C6239), secondaryAccent = Color(0xFFB08968),
                textPrimary = Color(0xFF3B2F2F), textSecondary = Color(0xFF6F5E53)
            )
        }
    }

    return if (textColorOption == FrebelTextColorOption.AUTO) {
        base
    } else {
        base.copy(
            textPrimary = if (isDarkMode) textColorOption.darkPrimary else textColorOption.lightPrimary,
            textSecondary = if (isDarkMode) textColorOption.darkSecondary else textColorOption.lightSecondary
        )
    }
}

@Composable
fun FrebelAnimatedThemeBackground(
    themeSpec: FrebelThemeSpec,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "frebelThemeAnim")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "frebelPhaseLoop"
    )
    val cyberGridProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cyberGridProgress"
    )
    val meteorProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "meteorProgress"
    )
    val glowPulse by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(themeSpec.backgroundTop, themeSpec.backgroundBottom)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (themeSpec.subTheme) {
                FrebelSubTheme.CYBER -> {
                    // 1. Cyber Perspective Grid moving along the horizon
                    val horizonY = h * 0.62f
                    val vpX = w * 0.5f

                    // Perspective vertical rays
                    val numRays = 14
                    for (i in -numRays..numRays) {
                        val bottomX = vpX + i * (w * 0.085f)
                        drawLine(
                            color = themeSpec.primaryAccent.copy(alpha = if (themeSpec.isDark) 0.16f else 0.10f),
                            start = Offset(vpX, horizonY),
                            end = Offset(bottomX, h),
                            strokeWidth = 1.2f
                        )
                    }

                    // Perspective horizontal moving grid lines
                    val numHorizLines = 9
                    for (i in 0 until numHorizLines) {
                        val frac = ((i.toFloat() + cyberGridProgress) / numHorizLines.toFloat()) % 1f
                        val easedFrac = frac * frac // non-linear distance compression
                        val lineY = horizonY + (h - horizonY) * easedFrac
                        val alpha = (0.24f * frac).coerceIn(0.02f, 0.24f) * (if (themeSpec.isDark) 1f else 0.7f)
                        drawLine(
                            color = themeSpec.primaryAccent.copy(alpha = alpha),
                            start = Offset(0f, lineY),
                            end = Offset(w, lineY),
                            strokeWidth = 1.5f + (frac * 1.5f)
                        )
                    }

                    // Pulsing horizontal scanline beam
                    val scanlineY = ((phase / (2 * PI.toFloat())) * h)
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                themeSpec.secondaryAccent.copy(alpha = 0.32f * glowPulse),
                                Color.Transparent
                            )
                        ),
                        start = Offset(0f, scanlineY),
                        end = Offset(w, scanlineY),
                        strokeWidth = 2.5f
                    )

                    // Floating glowing cyber energy bits
                    for (i in 0..9) {
                        val sparkPhase = (i * 0.1f + phase / (2 * PI.toFloat())) % 1f
                        val sparkY = h - (sparkPhase * h)
                        val sparkX = w * (0.08f + (i * 0.11f) % 0.84f) + sin(phase + i) * 12f
                        val sparkSize = 3.5f + (i % 3) * 2f
                        val color = if (i % 2 == 0) themeSpec.primaryAccent else themeSpec.secondaryAccent
                        drawRect(
                            color = color.copy(alpha = (1f - sparkPhase) * 0.45f),
                            topLeft = Offset(sparkX - sparkSize / 2, sparkY - sparkSize / 2),
                            size = Size(sparkSize, sparkSize)
                        )
                    }
                }

                FrebelSubTheme.NATURE, FrebelSubTheme.BEAR -> {
                    // 1. Warm ambient sunbeam from top left
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                themeSpec.secondaryAccent.copy(alpha = if (themeSpec.isDark) 0.16f else 0.22f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.1f, 0f),
                            radius = w * 0.85f
                        ),
                        radius = w * 0.85f,
                        center = Offset(w * 0.1f, 0f)
                    )

                    // 2. Soft drifting leaf silhouettes swaying gently
                    for (i in 0..7) {
                        val leafPhase = ((i * 0.14f + phase / (2 * PI.toFloat())) % 1f)
                        val leafY = leafPhase * (h + 80f) - 40f
                        val leafX = w * (0.10f + (i * 0.13f) % 0.80f) + sin(phase + i) * 26f
                        val leafRotation = sin(phase * 1.2f + i) * 32f

                        rotate(degrees = leafRotation, pivot = Offset(leafX, leafY)) {
                            val leafPath = Path().apply {
                                moveTo(leafX, leafY - 14f)
                                quadraticTo(leafX + 11f, leafY, leafX, leafY + 14f)
                                quadraticTo(leafX - 11f, leafY, leafX, leafY - 14f)
                                close()
                            }
                            drawPath(
                                path = leafPath,
                                color = themeSpec.primaryAccent.copy(alpha = if (themeSpec.isDark) 0.18f else 0.14f)
                            )
                        }
                    }

                    // 3. Glowing fireflies drifting with pulsating brightness
                    for (j in 0..7) {
                        val fx = w * (0.12f + (j * 0.21f) % 0.76f) + cos(phase * 0.8f + j) * 32f
                        val fy = h * (0.22f + (j * 0.18f) % 0.60f) + sin(phase * 0.8f + j) * 28f
                        val pulse = ((sin(phase * 2.2f + j * 1.5f) + 1f) * 0.5f)
                        val fAlpha = (0.20f + pulse * 0.55f) * (if (themeSpec.isDark) 0.85f else 0.65f)

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(themeSpec.secondaryAccent.copy(alpha = fAlpha), Color.Transparent),
                                center = Offset(fx, fy),
                                radius = 22f
                            ),
                            radius = 22f,
                            center = Offset(fx, fy)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = fAlpha * 0.9f),
                            radius = 3f,
                            center = Offset(fx, fy)
                        )
                    }
                }

                FrebelSubTheme.SPACE, FrebelSubTheme.SKY -> {
                    // 1. Soft cosmic nebula clouds
                    val nebulaCenter1 = Offset(w * 0.25f, h * 0.28f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(themeSpec.secondaryAccent.copy(alpha = if (themeSpec.isDark) 0.18f else 0.12f), Color.Transparent),
                            center = nebulaCenter1,
                            radius = w * 0.55f
                        ),
                        radius = w * 0.55f,
                        center = nebulaCenter1
                    )
                    val nebulaCenter2 = Offset(w * 0.80f, h * 0.72f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(themeSpec.primaryAccent.copy(alpha = if (themeSpec.isDark) 0.15f else 0.10f), Color.Transparent),
                            center = nebulaCenter2,
                            radius = w * 0.50f
                        ),
                        radius = w * 0.50f,
                        center = nebulaCenter2
                    )

                    // 2. Twinkling multi-depth star field
                    val starPositions = listOf(
                        0.12f to 0.15f, 0.85f to 0.12f, 0.44f to 0.22f, 0.68f to 0.28f,
                        0.22f to 0.38f, 0.92f to 0.42f, 0.35f to 0.54f, 0.76f to 0.58f,
                        0.15f to 0.66f, 0.52f to 0.70f, 0.88f to 0.80f, 0.38f to 0.86f,
                        0.62f to 0.18f, 0.28f to 0.78f, 0.74f to 0.92f, 0.08f to 0.48f
                    )
                    starPositions.forEachIndexed { idx, (px, py) ->
                        val twinkle = ((sin(phase * 2.5f + idx * 1.3f) + 1f) * 0.5f)
                        val starAlpha = (0.25f + twinkle * 0.65f) * (if (themeSpec.isDark) 1f else 0.7f)
                        val radius = if (idx % 3 == 0) 3.5f else 2.2f
                        drawCircle(
                            color = Color.White.copy(alpha = starAlpha),
                            radius = radius,
                            center = Offset(w * px, h * py)
                        )
                    }

                    // 3. Periodic shooting star (meteor)
                    if (meteorProgress < 0.38f) {
                        val mFrac = meteorProgress / 0.38f
                        val mStartX = w * (0.05f + mFrac * 0.80f)
                        val mStartY = h * (0.06f + mFrac * 0.36f)
                        val tailLength = 80f
                        val tailX = mStartX - tailLength * 0.85f
                        val tailY = mStartY - tailLength * 0.52f
                        val mAlpha = (sin(mFrac * PI.toFloat()) * 0.90f).coerceIn(0f, 1f)

                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Transparent, Color.White.copy(alpha = mAlpha)),
                                start = Offset(tailX, tailY),
                                end = Offset(mStartX, mStartY)
                            ),
                            start = Offset(tailX, tailY),
                            end = Offset(mStartX, mStartY),
                            strokeWidth = 2.8f,
                            cap = StrokeCap.Round
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = mAlpha),
                            radius = 3.5f,
                            center = Offset(mStartX, mStartY)
                        )
                    }
                }

                FrebelSubTheme.OCEAN -> {
                    // 1. Ambient caustic light waves along top and bottom
                    val wavePathTop = Path()
                    wavePathTop.moveTo(0f, 40f)
                    var x = 0f
                    while (x <= w) {
                        val y = 40f + sin((x / w) * 4 * PI.toFloat() + phase) * 16f
                        wavePathTop.lineTo(x, y)
                        x += 20f
                    }
                    drawPath(
                        path = wavePathTop,
                        color = themeSpec.primaryAccent.copy(alpha = if (themeSpec.isDark) 0.18f else 0.12f),
                        style = Stroke(width = 3f)
                    )

                    // 2. Rising translucent bubbles with sinusoidal wobble
                    for (i in 0..11) {
                        val bFrac = ((i * 0.085f + phase / (2 * PI.toFloat())) % 1f)
                        val by = h - (bFrac * (h + 60f))
                        val bx = w * (0.08f + (i * 0.16f) % 0.84f) + sin(phase * 2.2f + i) * 14f
                        val bRad = 7f + (i % 4) * 4.5f
                        val bAlpha = (1f - bFrac * 0.6f) * (if (themeSpec.isDark) 0.35f else 0.28f)

                        drawCircle(
                            color = themeSpec.primaryAccent.copy(alpha = bAlpha),
                            radius = bRad,
                            center = Offset(bx, by),
                            style = Stroke(width = 1.8f)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = bAlpha * 0.85f),
                            radius = bRad * 0.28f,
                            center = Offset(bx - bRad * 0.35f, by - bRad * 0.35f)
                        )
                    }
                }

                FrebelSubTheme.SOLID -> {
                    // Minimalist abstract geometric floating shapes with parallax drift
                    for (i in 0..5) {
                        val angle = phase + (i * 1.05f)
                        val cx = w * (0.16f + (i % 3) * 0.34f) + sin(angle) * 24f
                        val cy = h * (0.18f + (i / 3) * 0.46f) + cos(angle) * 28f
                        val shapeSize = 42f + (i % 3) * 18f
                        val color = if (i % 2 == 0) themeSpec.primaryAccent else themeSpec.secondaryAccent
                        val alpha = if (themeSpec.isDark) 0.10f else 0.07f

                        rotate(degrees = (angle * 180f / PI.toFloat()) * 0.5f, pivot = Offset(cx, cy)) {
                            if (i % 3 == 0) {
                                // Hollow diamond
                                drawRect(
                                    color = color.copy(alpha = alpha),
                                    topLeft = Offset(cx - shapeSize / 2, cy - shapeSize / 2),
                                    size = Size(shapeSize, shapeSize),
                                    style = Stroke(width = 2.2f)
                                )
                            } else if (i % 3 == 1) {
                                // Floating circular halo
                                drawCircle(
                                    color = color.copy(alpha = alpha),
                                    radius = shapeSize * 0.55f,
                                    center = Offset(cx, cy),
                                    style = Stroke(width = 2.2f)
                                )
                            } else {
                                // Filled soft pill
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(color.copy(alpha = alpha * 1.4f), Color.Transparent),
                                        center = Offset(cx, cy),
                                        radius = shapeSize * 0.75f
                                    ),
                                    radius = shapeSize * 0.75f,
                                    center = Offset(cx, cy)
                                )
                            }
                        }
                    }
                }

                FrebelSubTheme.LOVE -> {
                    // Floating glowing hearts
                    for (i in 0..6) {
                        val hFrac = ((i * 0.15f + phase / (2 * PI.toFloat())) % 1f)
                        val hy = h - (hFrac * (h + 40f))
                        val hx = w * (0.12f + (i * 0.15f) % 0.78f) + sin(phase + i) * 18f
                        val hScale = 1.0f + (i % 3) * 0.35f
                        val hAlpha = (1f - hFrac * 0.5f) * (if (themeSpec.isDark) 0.38f else 0.30f)

                        val heartPath = Path().apply {
                            moveTo(hx, hy)
                            cubicTo(hx - 12f * hScale, hy - 14f * hScale, hx - 24f * hScale, hy + 4f * hScale, hx, hy + 20f * hScale)
                            cubicTo(hx + 24f * hScale, hy + 4f * hScale, hx + 12f * hScale, hy - 14f * hScale, hx, hy)
                            close()
                        }
                        drawPath(
                            path = heartPath,
                            color = themeSpec.primaryAccent.copy(alpha = hAlpha)
                        )
                    }
                }

                else -> {
                    // FAIRY, COOKIES, Y2K, VINTAGE: ambient sparkling motes
                    val particleAlpha = if (themeSpec.isDark) 0.22f else 0.18f
                    for (i in 0..8) {
                        val angle = phase + (i * 0.85f)
                        val cx = (w * (0.12f + (i % 4) * 0.25f)) + sin(angle) * 26f
                        val cy = (h * (0.14f + (i / 4) * 0.48f)) + cos(angle) * 32f
                        val radius = (28f + (i % 3) * 18f)
                        val color = if (i % 2 == 0) themeSpec.primaryAccent else themeSpec.secondaryAccent

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(color.copy(alpha = particleAlpha), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = radius
                            ),
                            radius = radius,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }
    }
}
