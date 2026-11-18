package mrpapermoon.customebutton.ui.action

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.navigation.NavigationStore
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.resources.messageRes
import mrpapermoon.customebutton.ui.theme.contentColor
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme
import mrpapermoon.customebutton.ui.theme.composeColor

@Composable
fun ActionScreen(viewModel: ActionViewModelImpl = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    ActionScreen(
        state,
        viewModel
    )
}

@Composable
fun ActionScreen(
    state: ActionViewState,
    eventHandler: ActionEventHandler
) {
    val navigation = LocalNavigationEventHandler.current

    if (state.loading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
        return
    }

    val button = state.button
    if (button == null || state.error != null) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(state.error?.messageRes ?: R.string.button_not_found))
            TextButton(onClick = { eventHandler.onEvent(ActionEvent.Retry) }) { Text(stringResource(R.string.retry)) }
            TextButton(onClick = { navigation.onNavigationEvent(NavigationEvent.PopBackStack) }) { Text(stringResource(R.string.back)) }
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = { navigation.onNavigationEvent(NavigationEvent.PopBackStack) },
            modifier = Modifier.align(Alignment.Start)
        ) { Text(stringResource(R.string.back_to_buttons)) }
        Spacer(Modifier.height(48.dp))
        Text(
            button.title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.action_tagline),
            modifier = Modifier.padding(
                top = 12.dp,
                bottom = 48.dp
            )
        )
        Surface(
            onClick = { eventHandler.onEvent(ActionEvent.Press) },
            shape = CircleShape,
            color = button.color.composeColor,
            contentColor = button.color.contentColor,
            modifier = Modifier.size(220.dp)
        ) {
            Box(
                Modifier.padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    button.actionText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        Text(
            button.notificationText,
            modifier = Modifier.padding(top = 32.dp),
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.sharing_coming_soon),
            modifier = Modifier.padding(top = 24.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    if (state.showNotice) {
        AlertDialog(
            onDismissRequest = { eventHandler.onEvent(ActionEvent.DismissNotice) },
            title = { Text(stringResource(R.string.notification_preview)) },
            text = {
                Text(
                    stringResource(
                        R.string.notification_demo,
                        button.notificationText
                    )
                )
            },
            confirmButton = { TextButton(onClick = { eventHandler.onEvent(ActionEvent.DismissNotice) }) { Text(stringResource(R.string.got_it)) } })
    }

    BackHandler { navigation.onNavigationEvent(NavigationEvent.PopBackStack) }
}

@Preview(
    name = "Action Р’В· Light",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Preview(
    name = "Action Р’В· Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ActionPreview() {
    CustomeButtonTheme(dynamicColor = false) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationStore.stub,
        ) {
            val actionViewState = ActionViewState(
                loading = false,
                button = SharedButton(
                    "preview-miss",
                    ButtonColor.Pink,
                    stringResource(R.string.example_title),
                    stringResource(R.string.example_action),
                    stringResource(R.string.example_notification)
                )
            )
            Surface(Modifier.fillMaxSize()) {
                ActionScreen(actionViewState) {}
            }
        }
    }
}
