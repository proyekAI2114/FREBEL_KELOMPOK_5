package com.example.frebel.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frebel.ui.theme.FrebelThemeSpec

@Composable
fun FrebelTactileGameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color = Color(0xFF0284C7),
    contentColor: Color = Color.White,
    bevelDarkColor: Color? = null,
    bevelLightColor: Color? = null,
    height: Dp = 50.dp,
    bevelDepth: Dp = 4.dp,
    gradientColors: List<Color>? = null,
    primaryColor: Color? = null,
    secondaryColor: Color? = null,
    glowColor: Color? = null,
    enabled: Boolean = true,
    soundEnabled: Boolean = true,
    themeSpec: FrebelThemeSpec? = null
) {
    val resolvedPrimary = primaryColor ?: themeSpec?.primaryAccent ?: containerColor
    val resolvedSecondary = secondaryColor
        ?: bevelLightColor
        ?: themeSpec?.secondaryAccent
        ?: resolvedPrimary.copy(alpha = 0.85f)
    val resolvedBevelDark = bevelDarkColor ?: resolvedPrimary.copy(alpha = 0.68f)
    val resolvedColors = gradientColors ?: listOf(resolvedPrimary, resolvedSecondary)
    val resolvedGlow = glowColor ?: resolvedPrimary.copy(alpha = 0.40f)

    val density = LocalDensity.current
    val maxPressPx = with(density) { bevelDepth.toPx() }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tactileGameBtnScale"
    )

    val pressOffsetY by animateFloatAsState(
        targetValue = if (isPressed && enabled) maxPressPx else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "tactileGameBtnDepth"
    )

    val shimmerTransition = rememberInfiniteTransition(label = "tactileBtnShimmer")
    val shimmerX by shimmerTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tactileShimmerPos"
    )

    val effectiveAlpha = if (enabled) 1f else 0.45f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height + bevelDepth)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
                alpha = effectiveAlpha
            }
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = CircleShape,
                ambientColor = resolvedGlow,
                spotColor = resolvedGlow
            )
            .clip(CircleShape)
            .background(resolvedBevelDark)
            .padding(bottom = bevelDepth)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = pressOffsetY
                }
                .clip(CircleShape)
                .background(Brush.horizontalGradient(resolvedColors))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            if (enabled) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val sweepCenter = size.width * shimmerX
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.20f), Color.Transparent),
                            center = Offset(sweepCenter, size.height * 0.5f),
                            radius = size.width * 0.30f
                        ),
                        radius = size.width * 0.30f,
                        center = Offset(sweepCenter, size.height * 0.5f)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}