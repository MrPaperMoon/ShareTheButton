package mrpapermoon.customebutton.entity

data class SharedButton(
    val id: String,
    val color: ButtonColor,
    val title: String,
    val actionText: String,
    val notificationText: String
)