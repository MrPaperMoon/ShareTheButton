package mrpapermoon.customebutton.ui.create

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrpapermoon.customebutton.R

private val CreateButtonStep.labelRes: Int
    get() = when (this) {
        CreateButtonStep.Color -> R.string.color
        CreateButtonStep.Text -> R.string.creator_text
        CreateButtonStep.Confirm -> R.string.creator_review
    }

val CreateButtonDrawerItemHeight = 56.dp

@Composable
fun CreateButtonDrawer(
    state: CreateButtonViewState,
    eventHandler: CreateButtonEventHandler,
    modifier: Modifier = Modifier,
) {
    val step = state.step
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Open,
        confirmStateChange = { false }
    )

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = false,
        scrimColor = Color.Transparent,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.Transparent,
            ) {
                CreateButtonStep.entries.forEach { item ->
                    val selected = item == step
                    NavigationDrawerItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(CreateButtonDrawerItemHeight),
                        label = {
                            Text(
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                text = stringResource(item.labelRes),
                                style = if (selected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center
                            )
                        },
                        selected = selected,
                        onClick = {
                            eventHandler.onEvent(CreateButtonEvent.SelectStep(item))
                        }
                    )
                }
            }
        },
        content = { }
    )
}
