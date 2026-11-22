package mrpapermoon.customebutton.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.entity.Screen
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.navigation.NavigationDispatcher
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.scan_qr
import mrpapermoon.customebutton.resources.add_button
import mrpapermoon.customebutton.resources.baseline_add_24
import mrpapermoon.customebutton.resources.example_action
import mrpapermoon.customebutton.resources.example_hug_action
import mrpapermoon.customebutton.resources.example_hug_notification
import mrpapermoon.customebutton.resources.example_hug_title
import mrpapermoon.customebutton.resources.example_notification
import mrpapermoon.customebutton.resources.example_title
import mrpapermoon.customebutton.resources.home_tagline
import mrpapermoon.customebutton.resources.my_buttons
import mrpapermoon.customebutton.resources.round_qr_code_scanner_24
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.theme.AppBarMedium
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state = viewModel.viewState.collectAsStateWithLifecycle()

    HomeScreen(state)
}

@Composable
private fun HomeScreen(state: State<HomeViewState>) {
    val navigationDispatcher = LocalNavigationEventHandler.current

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        stringResource(Res.string.my_buttons),
                        style = AppBarMedium.TitleFont,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        stringResource(Res.string.home_tagline),
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = AppBarMedium.SubtitleFont,
                    )
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    modifier = Modifier.padding(12.dp).size(24.dp),
                    painter = painterResource(Res.drawable.round_qr_code_scanner_24),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = stringResource(Res.string.scan_qr),
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigationDispatcher.onNavigationEvent(NavigationEvent.NavigateTo(Screen.Create)) },
            ) {
                Icon(painterResource(Res.drawable.baseline_add_24), stringResource(Res.string.add_button))
            }
        }
    ) { padding ->
        HomeScreenContent(modifier = Modifier.padding(padding), state = state)
    }
}

@Preview(
    name = "HomeEmpty Р вЂ™Р’В· Light",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun HomeEmptyPreview() = HomeEmptyPreviewContent(darkTheme = false)

@Preview(
    name = "HomeEmpty Р вЂ™Р’В· Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun HomeEmptyPreviewDark() = HomeEmptyPreviewContent(darkTheme = true)

@Composable
private fun HomeEmptyPreviewContent(darkTheme: Boolean) {
    CustomeButtonTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationDispatcher {},
        ) {
            Surface(Modifier.fillMaxSize()) {
                HomeScreen(mutableStateOf(HomeViewState()))
            }
        }
    }
}

@Preview(
    name = "HomeWithButtons Р вЂ™Р’В· Light",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun HomeWithButtonsPreview() = HomeWithButtonsPreviewContent(darkTheme = false)

@Preview(
    name = "HomeWithButtons Р вЂ™Р’В· Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun HomeWithButtonsPreviewDark() = HomeWithButtonsPreviewContent(darkTheme = true)

@Composable
private fun HomeWithButtonsPreviewContent(darkTheme: Boolean) {
    CustomeButtonTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(
            LocalNavigationEventHandler provides NavigationDispatcher {},
        ) {
            val viewState = HomeViewState(
                buttons = listOf(
                    SharedButton(
                        "preview-miss",
                        Color(0xFFF4A7C1),
                        stringResource(Res.string.example_title),
                        stringResource(Res.string.example_action),
                        stringResource(Res.string.example_notification)
                    ),
                    SharedButton(
                        "preview-hug",
                        Color(0xFF9DE0C3),
                        stringResource(Res.string.example_hug_title),
                        stringResource(Res.string.example_hug_action),
                        stringResource(Res.string.example_hug_notification)
                    )
                )
            )
            Surface(Modifier.fillMaxSize()) {
                HomeScreen(mutableStateOf(viewState))
            }
        }
    }
}
