package mrpapermoon.customebutton.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.entity.SharedButton

@Entity(tableName = "buttons")
data class ButtonEntity(
    @PrimaryKey val id: String,
    val color: String,
    val title: String,
    val actionText: String,
    val notificationText: String,
    val isOwned: Boolean = true,
    val shareToken: String? = null
) {
    fun toButton(): SharedButton = SharedButton(id, ButtonColor.valueOf(color), title, actionText, notificationText)
}

fun SharedButton.toEntity(isOwned: Boolean = true, shareToken: String? = null): ButtonEntity =
    ButtonEntity(id, color.name, title, actionText, notificationText, isOwned, shareToken)
