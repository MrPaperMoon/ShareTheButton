package mrpapermoon.customebutton.ui.welcome

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.entity.Screen
import mrpapermoon.customebutton.navigation.NavigationStore
import mrpapermoon.customebutton.widget.shimmer
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme
import mrpapermoon.customebutton.widget.RoundedButton

@Composable
fun WelcomeScreen(viewModel: WelcomeViewModelImpl = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    WelcomeScreen(
        state,
        viewModel
    )
}

@Composable
fun WelcomeScreen(
    state: WelcomeViewState,
    eventHandler: WelcomeEventHandler
) {
    val navigation = LocalNavigationEventHandler.current
    var firstCycleCompleted by remember { mutableStateOf(false) }
    val showShimmer = state.progress || (state.entered && !firstCycleCompleted)

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.weight(1f))
        RoundedButton(
            text = stringResource(R.string.heart_mark),
            shape = RoundedCornerShape(40.dp),
        )

        Spacer(Modifier.height(24.dp))

        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.welcome_tagline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.weight(2f))

        OutlinedButton(
            onClick = {},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(stringResource(R.string.continue_google))
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(stringResource(R.string.continue_apple))
        }

        Spacer(Modifier.height(36.dp))
        Button(
            onClick = { eventHandler.onEvent(WelcomeEvent.Skip) },
            enabled = !showShimmer && !state.entered,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shimmer(
                    enabled = showShimmer,
                    onCycleCompleted = { firstCycleCompleted = true }
                )
                .semantics {
                    if (showShimmer) progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
                }
        ) {
            Text(stringResource(R.string.skip))
        }
        Spacer(Modifier.height(48.dp))
    }

    LaunchedEffect(
        state.entered,
        firstCycleCompleted
    ) {
        if (state.entered && firstCycleCompleted) navigation.onNavigationEvent(NavigationEvent.NavigateTo(Screen.Home))
    }
}

@Preview(
    name = "Welcome Р/В· Light",
    showBackground = true
)
@Preview(
    name = "Welcome Р/В· Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun WelcomePreview() {
    CustomeButtonTheme(dynamicColor = false) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationStore.stub,
        ) {
            Surface(Modifier.fillMaxSize()) {
                WelcomeScreen(WelcomeViewState()) { }
            }
        }
    }
}

@Preview(
    name = "Welcome Р/В· Shimmer",
    showBackground = true
)
@Preview(
    name = "Welcome Р/В· Shimmer Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun WelcomeProgressPreview() {
    CustomeButtonTheme(dynamicColor = false) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationStore.stub,
        ) {
            Surface(Modifier.fillMaxSize()) {
                WelcomeScreen(WelcomeViewState(progress = true)) {}
            }
        }
    }
}
