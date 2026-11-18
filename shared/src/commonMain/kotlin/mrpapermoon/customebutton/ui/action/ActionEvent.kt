package mrpapermoon.customebutton.ui.action


sealed class ActionEvent {
    data object Press : ActionEvent()
    data object DismissNotice : ActionEvent()
    data object Retry : ActionEvent()
}
