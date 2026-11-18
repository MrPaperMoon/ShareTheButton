package mrpapermoon.customebutton.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.ui.theme.composeColor
import mrpapermoon.customebutton.ui.theme.contentColor
import mrpapermoon.customebutton.widget.ButtonTile
import mrpapermoon.customebutton.widget.RoundedButton

@Composable
fun CreateButtonContent(
    step: CreateButtonStep,
    state: CreateButtonViewState,
    eventHandler: CreateButtonEventHandler,
    modifier: Modifier = Modifier,
) {
    val draft = state.draft
    val editable = !state.saving && !state.completed

    val initialColor = remember { draft.color.composeColor }
    val colorDescription = stringResource(R.string.color)

    val controller = rememberColorPickerController()
    val focus = LocalFocusManager.current

    when (step) {
        CreateButtonStep.Color -> {
            HsvColorPicker(
                modifier = modifier
                    .semantics { contentDescription = colorDescription }
                    .layout { measurable, constraints ->
                        val fixed = Constraints.fixed(
                            constraints.maxWidth,
                            constraints.maxWidth
                        )
                        val placeable = measurable.measure(fixed)
                        layout(
                            placeable.width,
                            placeable.height
                        ) {
                            placeable.placeRelative(
                                0,
                                0
                            )
                        }
                    },
                controller = controller,
                initialColor = initialColor,
                onColorChanged = { envelope ->
                    if (editable && envelope.fromUser) {
                        val color = ButtonColor.Custom(envelope.color.toArgb())
                        eventHandler.onEvent(CreateButtonEvent.ColorChanged(color))
                    }
                }
            )
        }

        CreateButtonStep.Text -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DraftField(
                    draft.title,
                    { eventHandler.onEvent(CreateButtonEvent.TitleChanged(it)) },
                    R.string.button_name,
                    BUTTON_LIMIT,
                    editable
                )
                DraftField(
                    draft.actionText,
                    { eventHandler.onEvent(CreateButtonEvent.ActionChanged(it)) },
                    R.string.action_name,
                    BUTTON_LIMIT,
                    editable
                )
                DraftField(
                    draft.notificationText,
                    { eventHandler.onEvent(CreateButtonEvent.NotificationChanged(it)) },
                    R.string.notification_text,
                    NOTIFICATION_LIMIT,
                    editable,
                    singleLine = false
                )
            }
        }

        CreateButtonStep.Confirm -> {
            Column(
                modifier = modifier.fillMaxHeight(),
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
                    color = draft.color.contentColor,
                    containerColor = draft.color.composeColor
                )
                if (!state.canSave) {
                    Text(
                        stringResource(R.string.creator_complete_text),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    SideEffect {
        controller.enabled = editable && step == CreateButtonStep.Color
    }

    LaunchedEffect(step) {
        focus.clearFocus()
        if (step == CreateButtonStep.Color) {
            withFrameNanos { }
            controller.selectByColor(
                draft.color.composeColor,
                fromUser = false
            )
        }
    }
}

@Composable
private fun DraftField(
    value: String,
    onChange: (String) -> Unit,
    label: Int,
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
                    R.string.character_count,
                    value.length,
                    limit
                )
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    )
}
