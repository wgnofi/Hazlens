package com.example.hazlens.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hazlens.R

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val hazlensFont = FontFamily(
    Font(R.font.barrio)
)

val pixelRotFont = FontFamily(
    Font(R.font.jolly)
)

val bitmapDecayFont = FontFamily(
    Font(R.font.vt)
)

val mutatedBlurFont = FontFamily(
    Font(R.font.rubik)
)

