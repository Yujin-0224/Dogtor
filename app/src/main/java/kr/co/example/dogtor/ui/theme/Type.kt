package kr.co.example.dogtor.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kr.co.example.dogtor.R

val KCCGanpan = FontFamily(
    Font(R.font.kcc_ganpan, FontWeight.Normal)
)

val Typography = Typography(
    headlineSmall = TextStyle(
        fontFamily = KCCGanpan,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = KCCGanpan,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = KCCGanpan,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = KCCGanpan,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    )
)