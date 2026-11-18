package mrpapermoon.customebutton

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mrpapermoon.customebutton.entity.Screen
import mrpapermoon.customebutton.navigation.NavigationActor
import mrpapermoon.customebutton.navigation.NavigationState
import mrpapermoon.customebutton.navigation.NavigationStore
import mrpapermoon.customebutton.ui.action.ActionScreen
import mrpapermoon.customebutton.ui.create.CreateButtonScreen
import mrpapermoon.customebutton.ui.home.HomeScreen
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.welcome.WelcomeScreen

val LocalNavigationEventHandler = staticCompositionLocalOf<NavigationActor> {
    error("CompositionLocal LocalNavigationEventHandler not present")
}

@Composable
fun CustomeButtonNavigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val navigationStore = rememberNavigationStore(navController)

    CompositionLocalProvider(LocalNavigationEventHandler provides navigationStore) {
        Surface(Modifier.fillMaxSize()) {
            NavHost(
                navController,
                startDestination = Screen.Home.path
            ) {
                composable(Screen.Home.path) {
                    if (isLoggedIn) HomeScreen() else WelcomeScreen()
                }
                dialog(
                    Screen.Create.path,
                    dialogProperties = androidx.compose.ui.window.DialogProperties(
                        usePlatformDefaultWidth = false,
                        dismissOnClickOutside = false,
                        dismissOnBackPress = false
                    )
                ) {
                    CreateButtonScreen()
                }
                composable(
                    Screen.Action.route,
                    arguments = listOf(navArgument("buttonId") { type = NavType.StringType })
                ) {
                    ActionScreen()
                }
            }
        }
    }
}

@Composable
fun rememberNavigationStore(navController: NavHostController): NavigationStore {
    return remember(navController) {
        NavigationStore { event ->
            when (event) {
                NavigationEvent.FinishGraph -> {
                    navController.popBackStack(
                        navController.graph.id,
                        true
                    )
                }

                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.screen.path) {
                        launchSingleTop = event.screen.singleInstance
                    }
                }

                NavigationEvent.PopBackStack -> {
                    navController.popBackStack()
                }
            }

            NavigationState(
                navController.currentDestination?.route ?: Screen.Home.path,
            )
        }
    }
}
