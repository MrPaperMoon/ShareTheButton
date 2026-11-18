package mrpapermoon.customebutton.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.widget.ButtonTile
import mrpapermoon.customebutton.widget.RoundedButton

@Composable
fun ColumnScope.HomeScreenContent(state: HomeViewState,) {
    val buttons = state.buttons
    if (buttons.isEmpty()) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoundedButton(stringResource(R.string.heart_mark))
            Spacer(Modifier.height(24.dp))
            Text(
                stringResource(R.string.empty_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                stringResource(R.string.empty_description),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Spacer(Modifier.height(24.dp))
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
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
                    stringResource(R.string.open_action)
                ) { TODO() }
            }
        }
    }
}
