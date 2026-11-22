package mrpapermoon.customebutton.ui.create

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.color
import mrpapermoon.customebutton.resources.creator_review
import mrpapermoon.customebutton.resources.creator_text
import mrpapermoon.customebutton.ui.EventHandler
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val CreateButtonStep.labelRes: StringResource
    get() = when (this) {
        CreateButtonStep.Color -> Res.string.color
        CreateButtonStep.Text -> Res.string.creator_text
        CreateButtonStep.Confirm -> Res.string.creator_review
    }

/** Height of one step in the wizard drawer. */
val CreateButtonDrawerItemHeight = 56.dp

@Composable
fun CreateButtonDrawer(
    step: CreateButtonStep,
    eventHandler: EventHandler,
    modifier: Modifier = Modifier,
) {
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
