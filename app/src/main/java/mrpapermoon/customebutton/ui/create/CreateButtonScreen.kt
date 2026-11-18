package mrpapermoon.customebutton.ui.create

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.resources.messageRes
import mrpapermoon.customebutton.ui.theme.CreatorActionContent
import mrpapermoon.customebutton.ui.theme.CreatorConfirmGreen
import mrpapermoon.customebutton.ui.theme.CreatorNextBlue
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme
import mrpapermoon.customebutton.ui.theme.composeColor
import mrpapermoon.customebutton.ui.theme.contentColor
import mrpapermoon.customebutton.widget.RoundedButton
import kotlin.math.roundToInt

@Composable
fun CreateButtonScreen(viewModel: CreateButtonViewModelImpl = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val navigation = LocalNavigationEventHandler.current
    CreateButtonScreen(
        state,
        viewModel
    )
    LaunchedEffect(state.completed) {
        if (state.completed) navigation.onNavigationEvent(NavigationEvent.PopBackStack)
    }
}

@Composable
fun CreateButtonScreen(
    state: CreateButtonViewState,
    eventHandler: CreateButtonEventHandler,
) {
    val density = LocalDensity.current
    val draft = state.draft
    val step = state.step
    val createButtonDrawerItemHeight = with(density) {
        CreateButtonDrawerItemHeight.roundToPx()
    }
    val editable = !state.saving && !state.completed
    val confirming = step == CreateButtonStep.Confirm
    val event = if (confirming) CreateButtonEvent.Save else CreateButtonEvent.Next
    val label = if (confirming) R.string.creator_confirm else R.string.creator_next

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CreateButtonTopBar(
                state = state,
                eventHandler = eventHandler
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 24.dp
                    )
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = if (confirming) editable && state.canSave else state.canGoNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (confirming) CreatorConfirmGreen else CreatorNextBlue,
                    contentColor = CreatorActionContent
                ),
                onClick = {
                    eventHandler.onEvent(event)
                }
            ) {
                if (state.saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(label))
                }
            }
        },
        snackbarHost = {
            if (state.error != null) {
                Text(
                    state.error?.let { stringResource(it.messageRes) }.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    ) { padding ->
        if (step != CreateButtonStep.Confirm) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(.4f)
                    .padding(padding)
                    .clearAndSetSemantics {},
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(8f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    RoundedButton(
                        text = draft.actionText,
                        color = draft.color.contentColor,
                        containerColor = draft.color.composeColor
                    )
                }

                Spacer(Modifier.weight(2f))
            }
        }

        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(vertical = 2.dp),
            targetState = step,
            animationSpec = tween(easing = LinearOutSlowInEasing)
        ) {
            CreateButtonDrawer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                state = state,
                eventHandler = eventHandler
            )

            CreateButtonContent(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(top = CreateButtonDrawerItemHeight * (step.ordinal + 1))
                    .applyQuarterOffset(step != CreateButtonStep.Confirm),
                step = it,
                state = state,
                eventHandler = eventHandler
            )
        }
    }

    BackHandler {
        eventHandler.onEvent(if (state.canGoBack) CreateButtonEvent.Previous else CreateButtonEvent.Cancel)
    }
}

private fun Modifier.applyQuarterOffset(enabled: Boolean): Modifier {
    if (!enabled) return this

    return layout { measurable, constraints ->
        val maxWidth = (constraints.maxWidth * .75f).roundToInt()
        val placeable = measurable.measure(constraints.copy(maxWidth = maxWidth))
        layout(
            placeable.width,
            placeable.height
        ) {
            placeable.placeRelative(
                constraints.maxWidth - maxWidth,
                0
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
@Preview(
    name = "Creator - Color Dark",
    widthDp = 390,
    heightDp = 844,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CreatorColorPreview() = CreatorPreview(CreateButtonStep.Color)

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
private fun CreatorPreview(step: CreateButtonStep) {
    val initial = CreateButtonViewState(
        step = step,
        draft = ButtonUiState(
            ButtonColor.Pink,
            stringResource(R.string.example_title),
            stringResource(R.string.example_action),
            stringResource(R.string.example_notification)
        )
    )
    var state by remember { mutableStateOf(initial) }
    CustomeButtonTheme(dynamicColor = false) {
        CreateButtonScreen(
            state,
            eventHandler = { event -> state = state.reduce(event) }
        )
    }
}
