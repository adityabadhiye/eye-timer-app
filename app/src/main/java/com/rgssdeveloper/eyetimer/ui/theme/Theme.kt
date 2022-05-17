package com.rgssdeveloper.eyetimer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class CustomColors(
    val material: Colors,
    val customBackground:Color,
    val timerText:Color,
    val startButton:Color,
    val tipsBackground:Color,
    val tipsText:Color
) {
    val primary: Color get() = material.primary
    val primaryVariant: Color get() = material.primaryVariant
    val secondary: Color get() = material.secondary
    val secondaryVariant: Color get() = material.secondaryVariant
    val background: Color get() = material.background
    val surface: Color get() = material.surface
    val error: Color get() = material.error
    val onPrimary: Color get() = material.onPrimary
    val onSecondary: Color get() = material.onSecondary
    val onBackground: Color get() = material.onBackground
    val onSurface: Color get() = material.onSurface
    val onError: Color get() = material.onError
    val isLight: Boolean get() = material.isLight
}

private val DarkColorPalette = CustomColors(
    material = darkColors(),
    customBackground = DarkBackground,
    timerText = White,
    startButton = Yellow,
    tipsBackground = DarkForeground,
    tipsText = DarkText
)

private val LocalColors = staticCompositionLocalOf{ LightColorPalette }

private val LightColorPalette = CustomColors(
    material = lightColors(),
    customBackground = White,
    timerText = DarkForeground,
    startButton = Orange,
    tipsBackground = Concrete,
    tipsText = DarkForeground
)

@Composable
fun EyeTimerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    CompositionLocalProvider(LocalColors provides colors) {
        MaterialTheme(
            colors = colors.material,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }


    val systemUiController = rememberSystemUiController()
    if(darkTheme){
        systemUiController.setSystemBarsColor(
            color = DarkBackground
        )
    }else{
        systemUiController.setSystemBarsColor(
            color = White
        )
    }

}
val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current