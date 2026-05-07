package com.example.tictactoe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.R

// ---- Design System Tokens (from Stitch project) ----
private val LightColors = lightColorScheme(
    primary = Color(0xFF3525CD),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E7FF),
    onPrimaryContainer = Color(0xFF3525CD),
    secondary = Color(0xFF006A61),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2F1),
    onSecondaryContainer = Color(0xFF006A61),
    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF0B1C30),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0B1C30),
    outlineVariant = Color(0xFFE0E0E0)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3525CD),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF86F2E4),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF006A61),
    onSecondaryContainer = Color.White,
    background = Color(0xFF0B121B),
    onBackground = Color(0xFFEAF1FF),
    surface = Color(0xFF161E2A),
    onSurface = Color(0xFFEAF1FF),
    outlineVariant = Color(0xFF2B3A4F)
)

private val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold)
)

@Composable
fun TicTacToeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.Typography(
            displayLarge = androidx.compose.ui.text.TextStyle(
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 40.sp
            ),
            headlineMedium = androidx.compose.ui.text.TextStyle(
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            bodyMedium = androidx.compose.ui.text.TextStyle(
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            labelLarge = androidx.compose.ui.text.TextStyle(
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        ),
        shapes = androidx.compose.material3.Shapes(
            small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
        ),
        content = content
    )
}
