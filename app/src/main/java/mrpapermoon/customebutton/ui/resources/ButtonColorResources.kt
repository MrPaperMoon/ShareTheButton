package mrpapermoon.customebutton.ui.resources

import androidx.annotation.StringRes
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.entity.ButtonColor

val ButtonColor.labelRes: Int
    @StringRes get() = when (this) {
        ButtonColor.Pink -> R.string.color_pink
        ButtonColor.Lavender -> R.string.color_lavender
        ButtonColor.Blue -> R.string.color_blue
        ButtonColor.Mint -> R.string.color_mint
        ButtonColor.Peach -> R.string.color_peach
        is ButtonColor.Custom -> R.string.color
    }
