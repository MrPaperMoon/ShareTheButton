package mrpapermoon.customebutton

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mrpapermoon.customebutton.entity.Screen
import mrpapermoon.customebutton.navigation.NavigationDispatcher
import mrpapermoon.customebutton.ui.create.CreateButtonScreen
import mrpapermoon.customebutton.ui.create.CreateButtonViewModel
import mrpapermoon.customebutton.ui.home.HomeScreen
import mrpapermoon.customebutton.ui.home.HomeViewModel
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.share.ShareButtonScreen
import mrpapermoon.customebutton.ui.share.ShareButtonViewModel
import mrpapermoon.customebutton.ui.welcome.WelcomeScreen
import mrpapermoon.customebutton.ui.welcome.WelcomeViewModel

private const val MAIN_GRAPH = "buttons"

@Composable
fun CustomeButtonNavigation(
    isLoggedIn: Boolean,
    factory: ViewModelProvider.Factory
) {
    val navController = rememberNavController()
    val navigationDispatcher = rememberNavigationDispatcher(navController)

    CompositionLocalProvider(LocalNavigationEventHandler provides navigationDispatcher) {
        Surface(Modifier.fillMaxSize()) {
            NavHost(
                modifier = Modifier.safeDrawingPadding(),
                navController = navController,
                startDestination = Screen.Home.path,
                route = MAIN_GRAPH
            ) {
                composable(Screen.Home.path) {
                    if (isLoggedIn) {
                        HomeScreen(viewModel<HomeViewModel>(factory = factory))
                    } else {
                        WelcomeScreen(viewModel<WelcomeViewModel>(factory = factory))
                    }
                }
                composable(Screen.Create.path) {
                    CreateButtonScreen(viewModel<CreateButtonViewModel>(factory = factory))
                }
                composable(
                    Screen.Share.route,
                    arguments = listOf(navArgument("buttonId") { type = NavType.StringType })
                ) {
                    ShareButtonScreen(viewModel<ShareButtonViewModel>(factory = factory))
                }
            }
        }
    }
}

@Composable
private fun rememberNavigationDispatcher(navController: NavHostController): NavigationDispatcher {
    var currentScreen: Screen by remember {
        mutableStateOf(Screen.Home)
    }
    return remember(navController) {
        NavigationDispatcher { event ->
            when (event) {
                NavigationEvent.FinishGraph -> {
                    navController.popBackStack(
                        MAIN_GRAPH,
                        true
                    )
                }

                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.screen.path) {
                        if (event.replaceCurrent) {
                            popUpTo(currentScreen.path) { inclusive = true }
                        }
                        launchSingleTop = event.screen.singleInstance
                    }
                    currentScreen = event.screen
                }

                NavigationEvent.PopBackStack -> {
                    navController.popBackStack()
                }
            }

        }
    }
}
