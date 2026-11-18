package mrpapermoon.customebutton.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.entity.Screen
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.navigation.NavigationStore
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme

@Composable
fun HomeScreen(viewModel: HomeViewModelImpl = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    HomeScreen(
        state,
        viewModel
    )
}

@Composable
fun HomeScreen(
    state: HomeViewState,
    eventHandler: HomeEventHandler
) {
    val addButtonDescription = stringResource(R.string.add_button)
    val navigationActor = LocalNavigationEventHandler.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigationActor.onNavigationEvent(NavigationEvent.NavigateTo(Screen.Create)) },
                modifier = Modifier.semantics { contentDescription = addButtonDescription }
            ) {
                Text(
                    stringResource(R.string.add_symbol),
                    fontSize = 32.sp
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                stringResource(R.string.my_buttons),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.home_tagline),
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HomeScreenContent(state)
        }
    }
}

@Preview(
    name = "HomeEmpty Р’В· Light",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Preview(
    name = "HomeEmpty Р’В· Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeEmptyPreview() {
    CustomeButtonTheme(dynamicColor = false) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationStore.stub,
        ) {
            Surface(Modifier.fillMaxSize()) {
                HomeScreen(HomeViewState()) {}
            }
        }
    }
}

@Preview(
    name = "HomeWithButtons Р’В· Light",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Preview(
    name = "HomeWithButtons Р’В· Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeWithButtonsPreview() {
    CustomeButtonTheme(dynamicColor = false) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationStore.stub,
        ) {
            Surface(Modifier.fillMaxSize()) {
                HomeScreen(
                    HomeViewState(
                        buttons = listOf(
                            SharedButton(
                                "preview-miss",
                                ButtonColor.Pink,
                                stringResource(R.string.example_title),
                                stringResource(R.string.example_action),
                                stringResource(R.string.example_notification)
                            ),
                            SharedButton(
                                "preview-hug",
                                ButtonColor.Mint,
                                stringResource(R.string.example_hug_title),
                                stringResource(R.string.example_hug_action),
                                stringResource(R.string.example_hug_notification)
                            )
                        )
                    )
                ) {}
            }
        }
    }
}
