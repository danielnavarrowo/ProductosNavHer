package com.navher.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import com.navher.myapplication.R

// Default Material 3 typography values
val TYPOGRAPHY = Typography()

@OptIn(ExperimentalTextApi::class)
val googleFlexDisplay = FontFamily(
    Font(
        R.font.googlesansflex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),      // Bold
            FontVariation.width(125f),      // Wider
            FontVariation.slant(-10f),       // Slight slant
            FontVariation.grade(50),// Higher grade for impact
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val googleFlexHeadline = FontFamily(
    Font(
        R.font.googlesansflex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(250),      // Bold
            FontVariation.width(125f),      // Wider
            FontVariation.slant(-6f),       // Slight slant
            FontVariation.grade(50)// Higher grade for impact
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val googleFlexTitle = FontFamily(
    Font(
        R.font.googlesansflex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(250),      // Bold
            FontVariation.width(125f),      // Wider
            FontVariation.slant(-6f),       // Slight slant
            FontVariation.grade(50)// Higher grade for impact
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val googleFlexBody = FontFamily(
    Font(
        R.font.googlesansflex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(450),      // Regular
            FontVariation.width(70f),
            FontVariation.opticalSizing(16.sp)
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val googleFlexLabel = FontFamily(
    Font(
        R.font.googlesansflex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),      // Medium
            FontVariation.width(95f),       // Slightly condensed for labels
            FontVariation.grade(10)
        )
    )
)

// Helper function para crear variaciones específicas en componentes
@OptIn(ExperimentalTextApi::class)
fun getGoogleSansFlex(
    weight: Int = 400,
    width: Float = 100f,
    slant: Float = 0f,
    grade: Int = 0
): FontFamily = FontFamily(
    Font(
        R.font.googlesansflex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.width(width),
            FontVariation.slant(slant),
            FontVariation.grade(grade)
        )
    )
)

val AppTypography = Typography(
    displayLarge = TYPOGRAPHY.displayLarge.copy(
        fontFamily = googleFlexDisplay,
    ),
    displayMedium = TYPOGRAPHY.displayMedium.copy(
        fontFamily = googleFlexDisplay,
    ),
    displaySmall = TYPOGRAPHY.displaySmall.copy(
        fontFamily = googleFlexDisplay,
    ),
    headlineLarge = TYPOGRAPHY.headlineLarge.copy(
        fontFamily = googleFlexHeadline,
    ),
    headlineMedium = TYPOGRAPHY.headlineMedium.copy(
        fontFamily = googleFlexHeadline,
    ),
    headlineSmall = TYPOGRAPHY.headlineSmall.copy(
        fontFamily = googleFlexHeadline,
    ),
    titleLarge = TYPOGRAPHY.titleLarge.copy(
        fontFamily = googleFlexTitle,
    ),
    titleMedium = TYPOGRAPHY.titleMedium.copy(
        fontFamily = googleFlexTitle,
    ),
    titleSmall = TYPOGRAPHY.titleSmall.copy(
        fontFamily = googleFlexTitle,
    ),
    bodyLarge = TYPOGRAPHY.bodyLarge.copy(
        fontFamily = googleFlexBody,
    ),
    bodyMedium = TYPOGRAPHY.bodyMedium.copy(
        fontFamily = googleFlexBody,
    ),
    bodySmall = TYPOGRAPHY.bodySmall.copy(
        fontFamily = googleFlexBody,
    ),
    labelLarge = TYPOGRAPHY.labelLarge.copy(
        fontFamily = googleFlexLabel,
    ),
    labelMedium = TYPOGRAPHY.labelMedium.copy(
        fontFamily = googleFlexLabel,
    ),
    labelSmall = TYPOGRAPHY.labelSmall.copy(
        fontFamily = googleFlexLabel,
    )
)
