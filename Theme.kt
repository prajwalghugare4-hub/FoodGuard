package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
  primary = EcoGreenPrimary,
  onPrimary = OnEcoGreen,
  primaryContainer = EcoGreenContainer,
  onPrimaryContainer = OnEcoGreenContainer,
  inversePrimary = EcoGreenFixedDim,

  secondary = RescueOrange,
  onSecondary = OnRescueOrange,
  secondaryContainer = RescueOrangeContainer,
  onSecondaryContainer = OnRescueOrangeContainer,

  surface = SurfaceLight,
  onSurface = OnSurfaceLight,
  surfaceVariant = SurfaceContainerHighestLight,
  onSurfaceVariant = OnSurfaceVariantLight,
  surfaceContainerLowest = SurfaceContainerLowestLight,
  surfaceContainerLow = SurfaceContainerLowLight,
  surfaceContainer = SurfaceContainerLight,
  surfaceContainerHigh = SurfaceContainerHighLight,
  surfaceContainerHighest = SurfaceContainerHighestLight,
  surfaceDim = SurfaceDimLight,
  surfaceBright = SurfaceBrightLight,

  background = SurfaceLight,
  onBackground = OnSurfaceLight,

  outline = OutlineLight,
  outlineVariant = OutlineVariantLight,

  error = AlertRed,
  onError = OnAlertRed,
  errorContainer = AlertRedContainer,
  onErrorContainer = OnAlertRedContainer,
)

private val DarkColorScheme = darkColorScheme(
  primary = EcoGreenFixedDim,
  onPrimary = EcoGreenPrimary,
  primaryContainer = EcoGreenContainer,
  onPrimaryContainer = EcoGreenFixed,
  inversePrimary = EcoGreenPrimary,

  secondary = RescueOrangeFixedDim,
  onSecondary = OnRescueOrangeContainer,
  secondaryContainer = RescueOrange,
  onSecondaryContainer = RescueOrangeFixed,

  surface = SurfaceDark,
  onSurface = OnSurfaceDark,
  surfaceVariant = SurfaceContainerHighestDark,
  onSurfaceVariant = OnSurfaceVariantDark,
  surfaceContainerLowest = SurfaceContainerLowestDark,
  surfaceContainerLow = SurfaceContainerLowDark,
  surfaceContainer = SurfaceContainerDark,
  surfaceContainerHigh = SurfaceContainerHighDark,
  surfaceContainerHighest = SurfaceContainerHighestDark,

  background = SurfaceDark,
  onBackground = OnSurfaceDark,

  outline = OutlineDark,
  outlineVariant = OutlineVariantDark,

  error = AlertRed,
  onError = OnAlertRed,
  errorContainer = AlertRedContainer,
  onErrorContainer = OnAlertRedContainer,
)

@Composable
fun FoodGuardTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
