package mrpapermoon.customebutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import mrpapermoon.customebutton.di.viewModelFactory
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme

/**
 * Hosts the Android Compose application.
 *
 * The Activity holds the splash screen while its startup ViewModel reads local guest
 * state. It mounts the common navigation host using the factory built from
 * [ShareButtonApplication]'s application graph.
 */
class CustomButtonActivity : ComponentActivity() {
    private val screenViewModelFactory by lazy {
        (application as ShareButtonApplication).graph.viewModelFactory()
    }
    private val sharedViewModel: SharedViewModel by viewModels { screenViewModelFactory }

    /**
     * Installs the splash screen and mounts the common UI after the local session is available.
     *
     * @param savedInstanceState State from a prior Activity instance, if present.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { sharedViewModel.isLoggedIn.value == null }
        enableEdgeToEdge()
        setContent {
            CustomeButtonTheme {
                val isLoggedIn = sharedViewModel.isLoggedIn.collectAsState(null)
                isLoggedIn.value?.also { isLoggedIn ->
                    CustomeButtonNavigation(isLoggedIn, screenViewModelFactory)
                }
            }
        }
    }
}
