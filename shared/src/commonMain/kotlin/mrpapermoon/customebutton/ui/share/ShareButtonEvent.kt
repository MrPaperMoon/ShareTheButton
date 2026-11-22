package mrpapermoon.customebutton.ui.share

import mrpapermoon.customebutton.ui.Event

sealed interface ShareButtonEvent : Event {
    data object Retry : ShareButtonEvent
    data class OnShareClick(val link: String) : ShareButtonEvent
    data class OnCopyClick(val link: String) : ShareButtonEvent
}
