@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package mrpapermoon.customebutton.ui.create

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.entity.Screen
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.example_action
import mrpapermoon.customebutton.resources.example_notification
import mrpapermoon.customebutton.resources.example_title
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.create.draft.CreateButtonDraft
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateButtonScreen(viewModel: CreateButtonViewModel) {
    val viewState = viewModel.viewState.collectAsStateWithLifecycle()
    val draftState = viewModel.draftState.collectAsStateWithLifecycle()

    CreateButtonScreen(
        viewState,
        draftState,
        viewModel
    )
}

@Composable
private fun CreateButtonScreen(
    viewState: State<CreateButtonViewState>,
    draftState: State<ButtonUiState>,
    eventHandler: EventHandler,
) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CreateButtonTopBar(
                viewState = viewState,
                draftState = draftState,
                eventHandler = eventHandler
            )
        },
        bottomBar = {
            CreateButtonActionButton(viewState, draftState, eventHandler)
        },
        snackbarHost = {
//            if (state.error != null) {
//                Text(
//                    state.error?.let { stringResource(it.messageRes) }.orEmpty(),
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
        }
    ) { padding ->
        CreateButtonContent(viewState, draftState, padding, eventHandler)
    }
}

@Composable
private fun CreateButtonContent(
    viewState: State<CreateButtonViewState>,
    draftState: State<ButtonUiState>,
    paddingValues: PaddingValues,
    eventHandler: EventHandler,
) {
    val viewState = viewState.value
    val step = viewState.step
    val navigationDispatcher = LocalNavigationEventHandler.current

    Crossfade(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(vertical = 2.dp),
        targetState = step,
        animationSpec = tween(easing = LinearOutSlowInEasing)
    ) {
        CreateButtonDrawer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            step = step,
            eventHandler = eventHandler
        )

        CreateButtonDraft(
            modifier = Modifier
                .padding(8.dp),
            step = it,
            enabled = viewState.enabled,
            state = draftState,
            eventHandler = eventHandler,
        )
    }

    BackHandler {
        if (viewState.canGoBack) {
            eventHandler.onEvent(CreateButtonEvent.Previous)
        } else {
            navigationDispatcher.onNavigationEvent(NavigationEvent.PopBackStack)
        }
    }

    LaunchedEffect(viewState.createdButtonId) {
        val buttonId = viewState.createdButtonId
        if (buttonId != null) {
            navigationDispatcher.onNavigationEvent(
                NavigationEvent.NavigateTo(Screen.Share(buttonId), replaceCurrent = true)
            )
        }
    }
}

@Preview(
    name = "Creator - Color",
    widthDp = 390,
    heightDp = 844,
    showBackground = true
)
@Composable
private fun CreatorColorPreview() = CreatorPreview(CreateButtonStep.Color)

@Preview(
    name = "Creator - Color Dark",
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun CreatorColorDarkPreview() = CreatorPreview(CreateButtonStep.Color, darkTheme = true)

@Preview(
    name = "Creator - Text",
    widthDp = 390,
    heightDp = 844,
    showBackground = true
)
@Composable
private fun CreatorTextPreview() = CreatorPreview(CreateButtonStep.Text)

@Preview(
    name = "Creator - Confirm",
    widthDp = 390,
    heightDp = 844,
    showBackground = true
)
@Preview(
    name = "Creator - Compact",
    widthDp = 320,
    heightDp = 568,
    showBackground = true
)
@Composable
private fun CreatorConfirmPreview() = CreatorPreview(CreateButtonStep.Confirm)

@Composable
private fun CreatorPreview(step: CreateButtonStep, darkTheme: Boolean = false) {
    val buttonUiState = ButtonUiState(
        Color(0xFFF4A7C1),
        stringResource(Res.string.example_title),
        stringResource(Res.string.example_action),
        stringResource(Res.string.example_notification)
    )
    val initial = CreateButtonViewState(step = step)
    CustomeButtonTheme(darkTheme = darkTheme) {
        CreateButtonScreen(
            viewState = mutableStateOf(initial),
            draftState = mutableStateOf(buttonUiState),
            eventHandler = EventHandler,
        )
    }
}
