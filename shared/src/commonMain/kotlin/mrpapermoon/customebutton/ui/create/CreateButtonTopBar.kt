package mrpapermoon.customebutton.ui.create

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.DrawableResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextOverflow
import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.baseline_chevron_left_24
import mrpapermoon.customebutton.resources.baseline_chevron_right_24
import mrpapermoon.customebutton.resources.creator_close
import mrpapermoon.customebutton.resources.creator_next
import mrpapermoon.customebutton.resources.creator_previous
import mrpapermoon.customebutton.resources.round_close_24
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.navigation.NavigationEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateButtonTopBar(
    viewState: State<CreateButtonViewState>,
    draftState: State<ButtonUiState>,
    eventHandler: EventHandler
) {
    val navigationDispatcher = LocalNavigationEventHandler.current
    val viewState = viewState.value
    val draftState = draftState.value

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
            AppbarButton(
                Res.drawable.round_close_24,
                Res.string.creator_close,
                viewState.enabled
            ) {
                navigationDispatcher.onNavigationEvent(NavigationEvent.PopBackStack)
            }
        },
        title = {
            Text(
                draftState.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            AppbarButton(
                Res.drawable.baseline_chevron_left_24,
                Res.string.creator_previous,
                viewState.canGoBack,
            ) {
                eventHandler.onEvent(CreateButtonEvent.Previous)
            }
            AppbarButton(
                Res.drawable.baseline_chevron_right_24,
                Res.string.creator_next,
                viewState.canGoNext
            ) {
                eventHandler.onEvent(CreateButtonEvent.Next)
            }
        }
    )
}

@Composable
private fun AppbarButton(
    iconRes: DrawableResource,
    contentDescription: StringResource,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescription)
        )
    }
}
