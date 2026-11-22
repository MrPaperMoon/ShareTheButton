package mrpapermoon.customebutton.ui.welcome

import mrpapermoon.customebutton.ui.Event

/** Actions supported by the welcome screen. */
sealed class WelcomeEvent : Event {
    data class SignIn(val enterMode: WelcomeEnterMode) : WelcomeEvent()
}
