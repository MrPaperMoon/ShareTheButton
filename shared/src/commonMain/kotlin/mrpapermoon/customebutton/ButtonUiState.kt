package mrpapermoon.customebutton

import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.entity.SharedButton

data class ButtonUiState(
    val color: ButtonColor,
    val title: String,
    val actionText: String,
    val notificationText: String,
)