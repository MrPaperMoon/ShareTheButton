package mrpapermoon.customebutton.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Primary accent in the dark scheme. */
val Purple80 = Color(0xFFD0BCFF)
/** Secondary accent in the dark scheme. */
val PurpleGrey80 = Color(0xFFCCC2DC)
/** Tertiary accent in the dark scheme. */
val Pink80 = Color(0xFFEFB8C8)

/** Primary accent in the light scheme. */
val Purple40 = Color(0xFF6650A4)
/** Secondary accent in the light scheme. */
val PurpleGrey40 = Color(0xFF625B71)
/** Tertiary accent in the light scheme. */
val Pink40 = Color(0xFF7D5260)

/** Dark foreground candidate for button labels. */
val ButtonContent = Color(0xFF302530)
/** Default translucent color of the shimmer band. */
val ShimmerHighlight = Color.White.copy(alpha = 0.35f)

/** Light foreground candidate for button labels. */
val ButtonLightContent = Color.White

/** Returns the light or dark button foreground with greater contrast against this RGB background. */
val Color.contentColor: Color
    get() {
        val backgroundLuminance = luminance()
        val darkContrast = (backgroundLuminance + 0.05f) / (ButtonContent.luminance() + 0.05f)
        val lightContrast = (ButtonLightContent.luminance() + 0.05f) / (backgroundLuminance + 0.05f)
        return if (darkContrast >= lightContrast) ButtonContent else ButtonLightContent
    }

/** Container color of the wizard’s Next control. */
val CreatorNextBlue = Color(0xFF1565C0)
/** Container color of the wizard’s confirmation control. */
val CreatorConfirmGreen = Color(0xFF237A3B)
/** Foreground color of the wizard’s primary controls. */
val CreatorActionContent = Color.White
