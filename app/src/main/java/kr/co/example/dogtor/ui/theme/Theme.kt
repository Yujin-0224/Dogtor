package kr.co.example.dogtor.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ✅ [수정] Color.kt에 새로 정의한 색상들로 테마를 구성합니다.
private val LightColorScheme = lightColorScheme(
    primary = ButtonBorder,         // 앱의 주요 색상 (버튼 테두리, 강조색 등으로 활용)
    background = MainBackground,    // 화면 전체 배경색
    surface = ButtonBackground,     // 카드, 버튼 등 UI 요소의 표면색
    onPrimary = TextColor,          // primary 색상 위의 텍스트 색상
    onBackground = TextColor,       // background 위의 텍스트 색상
    onSurface = TextColor           // surface 위의 텍스트 색상
)

// 다크 모드는 별도로 설정하지 않았으므로, 라이트 테마를 그대로 사용합니다.
private val DarkColorScheme = lightColorScheme(
    primary = ButtonBorder,
    background = MainBackground,
    surface = ButtonBackground,
    onPrimary = TextColor,
    onBackground = TextColor,
    onSurface = TextColor
)

@Composable
fun DogtorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // ✅ 다크모드 여부와 관계없이 항상 LightColorScheme을 사용하도록 설정
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
