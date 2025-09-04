package com.yash.kagitham.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yash.kagitham.R

val BitcountFont = FontFamily(
    Font(R.font.bitcount)
)

val CustomTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = BitcountFont,  // use your font here
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = BitcountFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )
)

val DarkColorScheme = darkColorScheme(
    primary = Color.Red,       // Vibrant red
    onPrimary = Color.White,           // Text/icons on red
    background = Color(0xFF121212),    // Dark gray/black background
    onBackground = Color.White,        // Text on background
    surface = Color(0xFF1E1E1E),       // Card/surface color
    onSurface = Color.White,           // Text/icons on surface
    secondary = Color(0xFFB71C1C),     // Darker red
    onSecondary = Color.White,         // Text/icons on secondary
    tertiary = Color(0xFF880E4F),      // Optional: red-purple accent
    onTertiary = Color.White,
    error = Color(0xFFFF1744),         // Bright error red
    onError = Color.White,
)

@Composable
fun MBTTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = CustomTypography,
        content = content
    )
}





@Composable
fun KagithamTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = CustomTypography,
        content = content
    )
}