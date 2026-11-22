package mrpapermoon.customebutton.ui.create.draft

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.select_button_color
import mrpapermoon.customebutton.resources.action_name
import mrpapermoon.customebutton.resources.button_name
import mrpapermoon.customebutton.resources.character_count
import mrpapermoon.customebutton.resources.color
import mrpapermoon.customebutton.resources.creator_complete_text
import mrpapermoon.customebutton.resources.notification_text
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.create.BUTTON_LIMIT
import mrpapermoon.customebutton.ui.create.CreateButtonDrawerItemHeight
import mrpapermoon.customebutton.ui.create.CreateButtonStep
import mrpapermoon.customebutton.ui.create.NOTIFICATION_LIMIT
import mrpapermoon.customebutton.ui.theme.contentColor
import mrpapermoon.customebutton.widget.ButtonTile
import mrpapermoon.customebutton.widget.RoundedButton
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun CreateButtonDraft(
    state: State<ButtonUiState>,
    step: CreateButtonStep,
    enabled: Boolean,
    eventHandler: EventHandler,
    modifier: Modifier = Modifier,
) {
    val draft = state.value

    val initialColor = remember { draft.color }
    val colorDescription = stringResource(Res.string.color)

    val controller = rememberColorPickerController()
    val focus = LocalFocusManager.current

    controller.enabled = enabled

    when (step) {
        CreateButtonStep.Color -> {
            Column(
                modifier
                    .padding(top = CreateButtonDrawerItemHeight * CreateButtonStep.entries.size)
                    .fillMaxWidth()
                    .semantics { contentDescription = colorDescription }
            ) {
                Box(
                    Modifier
                        .height(CreateButtonDrawerItemHeight)
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(draft.color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(Res.string.select_button_color), color = draft.color.contentColor)
                }

                HsvColorPicker(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            val width = (constraints.maxWidth * .8f).roundToInt()
                            val padding = constraints.maxWidth - width
                            val fixed = Constraints.fixed(width, width)
                            val placeable = measurable.measure(fixed)

                            layout(placeable.width, placeable.height) {
                                placeable.placeRelative(padding / 2, padding / 2)
                            }
                        },
                    controller = controller,
                    initialColor = initialColor,
                    onColorChanged = { envelope ->
                        if (envelope.fromUser) {
                            eventHandler.onEvent(DraftButtonEvent.ColorChanged(envelope.color))
                        }
                    }
                )
            }
        }

        CreateButtonStep.Text -> {
            Column(
                modifier = modifier.padding(top = CreateButtonDrawerItemHeight * (CreateButtonStep.Text.ordinal + 1)),
            ) {
                Column(
                    Modifier.applyQuarterOffset(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DraftField(
                        draft.title,
                        { eventHandler.onEvent(DraftButtonEvent.TitleChanged(it)) },
                        Res.string.button_name,
                        BUTTON_LIMIT,
                        enabled
                    )
                    DraftField(
                        draft.actionText,
                        { eventHandler.onEvent(DraftButtonEvent.ActionChanged(it)) },
                        Res.string.action_name,
                        BUTTON_LIMIT,
                        enabled
                    )
                    DraftField(
                        draft.notificationText,
                        { eventHandler.onEvent(DraftButtonEvent.NotificationChanged(it)) },
                        Res.string.notification_text,
                        NOTIFICATION_LIMIT,
                        enabled,
                        singleLine = false
                    )
                }

                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .alpha(.4f)
                        .clearAndSetSemantics {},
                    contentAlignment = Alignment.Center
                ) {
                    RoundedButton(
                        text = draft.actionText,
                        containerColor = draft.color
                    )
                }
            }
        }

        CreateButtonStep.Confirm -> {
            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .padding(top = CreateButtonDrawerItemHeight * CreateButtonStep.entries.size),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ButtonTile(
                    title = draft.title,
                    color = draft.color,
                    subtitle = draft.notificationText
                ) {}
                RoundedButton(
                    text = draft.actionText,
                    containerColor = draft.color
                )
                if (!draft.dataIsValid) {
                    Text(
                        stringResource(Res.string.creator_complete_text),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    SideEffect {
        controller.enabled = enabled && step == CreateButtonStep.Color
    }

    LaunchedEffect(step) {
        focus.clearFocus()
        if (step == CreateButtonStep.Color) {
            withFrameNanos { }
            controller.selectByColor(draft.color, fromUser = false)
        }
    }
}

@Composable
private fun DraftField(
    value: String,
    onChange: (String) -> Unit,
    label: StringResource,
    limit: Int,
    enabled: Boolean,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(stringResource(label)) },
        enabled = enabled,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 4,
        isError = value.length > limit,
        supportingText = {
            Text(
                stringResource(
                    Res.string.character_count,
                    value.length,
                    limit
                )
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    )
}

private fun Modifier.applyQuarterOffset(): Modifier {
    return layout { measurable, constraints ->
        val maxWidth = (constraints.maxWidth * .75f).roundToInt()
        val placeable = measurable.measure(constraints.copy(maxWidth = maxWidth))
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(constraints.maxWidth - maxWidth, 0)
        }
    }
}
