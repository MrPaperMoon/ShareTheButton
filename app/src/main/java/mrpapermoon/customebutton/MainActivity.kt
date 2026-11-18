package mrpapermoon.customebutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sharedViewModel: SharedViewModelImpl by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { sharedViewModel.isLoggedIn.value == null }
        enableEdgeToEdge()
        setContent {
            CustomeButtonTheme(dynamicColor = false) {
                val isLoggedIn = sharedViewModel.isLoggedIn.collectAsState(null)
                isLoggedIn.value?.also { isLoggedIn ->
                    CustomeButtonNavigation(isLoggedIn = isLoggedIn)
                }
            }

            LaunchedEffect(sharedViewModel) {
                sharedViewModel.onUiReady()
            }
        }
    }
}
