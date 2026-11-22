package mrpapermoon.customebutton.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.entity.Screen
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.empty_description
import mrpapermoon.customebutton.resources.empty_title
import mrpapermoon.customebutton.resources.heart_mark
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.theme.ButtonContent
import mrpapermoon.customebutton.widget.ButtonTile
import mrpapermoon.customebutton.widget.RoundedButton

@Composable
fun HomeScreenContent(state: State<HomeViewState>, modifier: Modifier = Modifier) {
    val navigationDispatcher = LocalNavigationEventHandler.current

    val buttons = state.value.buttons
    if (buttons.isEmpty()) {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoundedButton(stringResource(Res.string.heart_mark), textColor = ButtonContent)
            Spacer(Modifier.height(24.dp))
            Text(
                stringResource(Res.string.empty_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                stringResource(Res.string.empty_description),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(
                buttons,
                key = { it.id }
            ) { button ->
                ButtonTile(
                    button.title,
                    button.color,
                    button.actionText
                ) {
//                    navigationDispatcher.onNavigationEvent(NavigationEvent.NavigateTo(Screen.Action(button.id)))
                }
            }
        }
    }
}
