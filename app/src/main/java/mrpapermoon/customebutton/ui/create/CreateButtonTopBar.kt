package mrpapermoon.customebutton.ui.create

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import mrpapermoon.customebutton.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateButtonTopBar(
    state: CreateButtonViewState,
    eventHandler: CreateButtonEventHandler
) {
    val draft = state.draft
    val editable = !state.saving && !state.completed

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
            AppbarButton(
                R.drawable.round_close_24,
                R.string.creator_close_symbol,
                editable
            ) {
                eventHandler.onEvent(CreateButtonEvent.Cancel)
            }
        },
        title = {
            Text(
                draft.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            AppbarButton(
                R.drawable.baseline_chevron_left_24,
                R.string.creator_previous,
                state.canGoBack,
            ) {
                eventHandler.onEvent(CreateButtonEvent.Previous)
            }
            AppbarButton(
                R.drawable.baseline_chevron_right_24,
                R.string.creator_next,
                state.canGoNext
            ) {
                eventHandler.onEvent(CreateButtonEvent.Next)
            }
        }
    )
}

@Composable
private fun AppbarButton(
    @DrawableRes iconRes: Int,
    @StringRes contentDescription: Int,
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
