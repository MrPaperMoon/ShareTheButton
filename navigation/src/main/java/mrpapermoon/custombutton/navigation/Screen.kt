package mrpapermoon.custombutton.navigation

sealed class Screen {
    class Welcome : Screen("welcome")
    class Home : Screen("home")
    class Create : Screen("create")
    class Action(buttonId: String) : Screen("action", buttonId)

    constructor(name: String, vararg arg: String) {
        path = name + arg.joinToString(separator = "&", prefix = "?")
    }

    val path: String
}

infix fun String.queryWith(value: String): String = "$this=$value"
