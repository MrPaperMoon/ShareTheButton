package mrpapermoon.customebutton.ui.resources

import androidx.annotation.StringRes
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.ui.common.ViewError

val ViewError.messageRes: Int
    @StringRes get() = when (this) {
        ViewError.LoadButton -> R.string.error_load_button
        ViewError.LoadButtons -> R.string.error_load_buttons
        ViewError.SaveButton -> R.string.error_save_button
    }
