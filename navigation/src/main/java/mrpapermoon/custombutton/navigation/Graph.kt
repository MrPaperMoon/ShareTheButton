package mrpapermoon.custombutton.navigation

import java.io.Serializable

abstract class Graph(private val startScreen: Screen) {

    private val root: Graph? = null
    private val screenFlow: MutableSet<Screen> = mutableSetOf(startScreen)

    val deepLink: String
        get() = screenFlow.joinToString(separator = "/", prefix = root?.deepLink ?: appPath) { it.path }

    fun navigateTo(screen: Screen) {
        screenFlow += screen
    }

//    fun pop(): Screen {
//        screenFlow.remove(screenFlow.last())
//        return if (screenFlow.isEmpty()) root ?: screenFlow
//    }

    companion object {
        const val appPath = "customebutton://"
    }
}