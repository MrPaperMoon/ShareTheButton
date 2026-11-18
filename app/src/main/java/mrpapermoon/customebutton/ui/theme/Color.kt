package mrpapermoon.customebutton.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import mrpapermoon.customebutton.entity.ButtonColor

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

val ButtonPink = Color(0xFFF4A7C1)
val ButtonLavender = Color(0xFFC4B5FD)
val ButtonBlue = Color(0xFFA5D8FF)
val ButtonMint = Color(0xFF9DE0C3)
val ButtonPeach = Color(0xFFFFC69D)
val ButtonContent = Color(0xFF302530)
val ShimmerHighlight = Color.White.copy(alpha = 0.35f)

val ButtonColor.composeColor: Color
    get() = when (this) {
        ButtonColor.Pink -> ButtonPink
        ButtonColor.Lavender -> ButtonLavender
        ButtonColor.Blue -> ButtonBlue
        ButtonColor.Mint -> ButtonMint
        ButtonColor.Peach -> ButtonPeach
        is ButtonColor.Custom -> Color(argb)
    }


val ButtonLightContent = Color.White

val ButtonColor.contentColor: Color
    get() {
        val backgroundLuminance = composeColor.luminance()
        val darkContrast = (backgroundLuminance + 0.05f) / (ButtonContent.luminance() + 0.05f)
        val lightContrast = (ButtonLightContent.luminance() + 0.05f) / (backgroundLuminance + 0.05f)
        return if (darkContrast >= lightContrast) ButtonContent else ButtonLightContent
    }

val CreatorNextBlue = Color(0xFF1565C0)
val CreatorConfirmGreen = Color(0xFF237A3B)
val CreatorActionContent = Color.White
