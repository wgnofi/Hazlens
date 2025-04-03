package com.example.hazlens.data

import androidx.annotation.StringRes
import androidx.compose.ui.text.font.FontFamily
import com.example.hazlens.ui.theme.bitmapDecayFont
import com.example.hazlens.ui.theme.mutatedBlurFont
import com.example.hazlens.ui.theme.pixelRotFont
import java.util.logging.Filter

data class FilterOp(
    @StringRes val filterTypeRes: Int,
    val filterType: Int
)

fun FilterOp.getFont(filterType: Int): FontFamily {
    return when(filterType) {
        1 -> bitmapDecayFont
        2 -> mutatedBlurFont
        3 -> pixelRotFont
        else -> {
            FontFamily()
        }
    }
}