package mrpapermoon.customebutton.ui.welcome

sealed class WelcomeEvent {
    data object Skip : WelcomeEvent()
}
