package mrpapermoon.customebutton.ui.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.entity.Screen
import mrpapermoon.customebutton.navigation.NavigationDispatcher
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.app_name
import mrpapermoon.customebutton.resources.continue_apple
import mrpapermoon.customebutton.resources.continue_google
import mrpapermoon.customebutton.resources.heart_mark
import mrpapermoon.customebutton.resources.skip
import mrpapermoon.customebutton.resources.welcome_tagline
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme
import mrpapermoon.customebutton.widget.RoundedButton
import mrpapermoon.customebutton.widget.shimmer
import org.jetbrains.compose.resources.stringResource

@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel) {
    val state = viewModel.viewState.collectAsStateWithLifecycle()

    WelcomeScreen(state, viewModel)
}

@Composable
private fun WelcomeScreen(
    state: State<WelcomeViewState>,
    eventHandler: EventHandler
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.weight(1f))
        RoundedButton(
            text = stringResource(Res.string.heart_mark),
            shape = RoundedCornerShape(40.dp),
        )

        Spacer(Modifier.height(24.dp))

        Text(
            stringResource(Res.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(Res.string.welcome_tagline),
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
            Text(stringResource(Res.string.continue_google))
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(stringResource(Res.string.continue_apple))
        }

        Spacer(Modifier.height(36.dp))
        GuestEnterButton(state, eventHandler)
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun GuestEnterButton(
    state: State<WelcomeViewState>,
    eventHandler: EventHandler
) {
    var firstCycleCompleted by remember { mutableStateOf(false) }

    val state = state.value
    val showShimmer = state.progress || (state.entered && !firstCycleCompleted)

    val navigation = LocalNavigationEventHandler.current

    Button(
        onClick = { eventHandler.onEvent(WelcomeEvent.SignIn(WelcomeEnterMode.GUEST)) },
        enabled = !showShimmer && !state.entered && state.enabledEnterModes.contains(WelcomeEnterMode.GUEST),
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
        Text(stringResource(Res.string.skip))
    }

    LaunchedEffect(
        state.entered,
        firstCycleCompleted
    ) {
        if (state.entered && firstCycleCompleted) {
            navigation.onNavigationEvent(NavigationEvent.NavigateTo(Screen.Home))
        }
    }
}

@Preview(
    name = "Welcome Р /Р’В· Light",
    showBackground = true
)
@Composable
private fun WelcomePreview() = WelcomePreviewContent(darkTheme = false)

@Preview(
    name = "Welcome Р /Р’В· Dark",
    showBackground = true
)
@Composable
private fun WelcomePreviewDark() = WelcomePreviewContent(darkTheme = true)

@Composable
private fun WelcomePreviewContent(darkTheme: Boolean) {
    CustomeButtonTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationDispatcher {},
        ) {
            Surface(Modifier.fillMaxSize()) {
                WelcomeScreen(mutableStateOf(WelcomeViewState()), EventHandler)
            }
        }
    }
}

@Preview(
    name = "Welcome Р /Р’В· Shimmer",
    showBackground = true
)
@Composable
private fun WelcomeProgressPreview() = WelcomeProgressPreviewContent(darkTheme = false)

@Preview(
    name = "Welcome Р /Р’В· Shimmer Dark",
    showBackground = true
)
@Composable
private fun WelcomeProgressPreviewDark() = WelcomeProgressPreviewContent(darkTheme = true)

@Composable
private fun WelcomeProgressPreviewContent(darkTheme: Boolean) {
    CustomeButtonTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationDispatcher {},
        ) {
            Surface(Modifier.fillMaxSize()) {
                WelcomeScreen(mutableStateOf(WelcomeViewState(progress = true)), EventHandler)
            }
        }
    }
}
