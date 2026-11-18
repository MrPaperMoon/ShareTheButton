package mrpapermoon.customebutton.entity

@Suppress("ConstPropertyName")
sealed class Screen(val path: String) {
    open val singleInstance = false

    data object Home : Screen("home") {
        override val singleInstance: Boolean
            get() = true
    }

    data object Create : Screen("create")
    data class Action(val buttonId: String) : Screen("action/$buttonId") {
        companion object {
            const val route = "action/{buttonId}"
        }
    }
}
