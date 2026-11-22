package mrpapermoon.customebutton.widget

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.add_symbol
import mrpapermoon.customebutton.resources.example_hug_title
import mrpapermoon.customebutton.resources.example_title
import mrpapermoon.customebutton.resources.home_tagline
import mrpapermoon.customebutton.resources.my_buttons
import mrpapermoon.customebutton.resources.open_action
import mrpapermoon.customebutton.resources.skip
import mrpapermoon.customebutton.ui.theme.ButtonContent
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme
import mrpapermoon.customebutton.ui.theme.ShimmerHighlight
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.tan

/**
 * Draws a repeating highlight over the modified content.
 *
 * @receiver Modifier to decorate with the highlight.
 * @param enabled Whether the effect is active.
 * @param highlightColor Center color of the moving band.
 * @param durationMillis Duration of a cycle in milliseconds, at least 300.
 * @param widthFraction Band width relative to the content, in `(0, 1]`.
 * @param tiltDegrees Band angle in degrees.
 * @param onCycleCompleted Invoked after each animated pass.
 * @return The modified drawing chain, or the receiver when disabled.
 * @throws IllegalArgumentException If an enabled effect has an invalid duration, width or angle.
 */
@Composable
fun Modifier.shimmer(
    enabled: Boolean = true,
    highlightColor: Color = ShimmerHighlight,
    @IntRange(from = 17) durationMillis: Int = 300,
    @FloatRange(from = .0, to = 1.0, fromInclusive = false) widthFraction: Float = 0.45f,
    tiltDegrees: Double = 20.0,
    onCycleCompleted: () -> Unit = {}
): Modifier {
    if (!enabled) return this
    require(durationMillis >= 17) { "durationMillis must be at least one frame for 60 fps long" }
    require(widthFraction > 0f && widthFraction <= 1f) { "widthFraction must be in range of 0 (exclusive) and 1 (inclusive)" }

    val isPreview = LocalInspectionMode.current
    val progress = remember { Animatable(if (isPreview) 0.5f else 0f) }
    val cycleCompleted = rememberUpdatedState(onCycleCompleted)

    LaunchedEffect(
        durationMillis,
        isPreview
    ) {
        if (isPreview) return@LaunchedEffect
        while (true) {
            progress.snapTo(0f)
            progress.animateTo(
                1f,
                tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing
                )
            )
            cycleCompleted.value()
        }
    }

    return graphicsLayer {
        // Isolate this element so SrcAtop cannot use the parent background as its mask.
        compositingStrategy = CompositingStrategy.Offscreen
    }
        .drawWithCache {
            if (size.isUnspecified || size.isEmpty()) {
                return@drawWithCache onDrawWithContent {
                    drawContent()
                }
            }

            val bandWidth = size.width * widthFraction
            val slope = tan(Math.toRadians(tiltDegrees)).toFloat()
            val tiltWidth = abs(size.height * slope)
            val gradientWidth = bandWidth / (1f + slope * slope)
            val brush = Brush.linearGradient(
                colors = listOf(
                    highlightColor.copy(alpha = 0f),
                    highlightColor,
                    highlightColor.copy(alpha = 0f)
                ),
                start = Offset.Zero,
                end = Offset(
                    gradientWidth,
                    -gradientWidth * slope
                )
            )

            onDrawWithContent {
                drawContent()

                val left = -bandWidth - tiltWidth + (size.width + bandWidth + 2f * tiltWidth) * progress.value
                clipRect {
                    translate(left = left) {
                        drawRect(
                            brush = brush,
                            topLeft = Offset(
                                -left,
                                0f
                            ),
                            size = size,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
            }
        }
}

@Composable
private fun ShimmerPreviewContainer(content: @Composable () -> Unit) {
    CustomeButtonTheme {
        Surface {
            Box(
                modifier = Modifier.padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ShimmerPreviewTile(
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4A7C1),
            contentColor = ButtonContent
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                stringResource(Res.string.open_action),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(
    name = "Shimmer - Wide Button",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360
)
@Composable
private fun ShimmerWideButtonPreview() {
    ShimmerPreviewContainer {
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shimmer()
        ) {
            Text(stringResource(Res.string.skip))
        }
    }
}

@Preview(
    name = "Shimmer - FAB",
    showBackground = true
)
@Composable
private fun ShimmerFabPreview() {
    ShimmerPreviewContainer {
        FloatingActionButton(
            onClick = {},
            modifier = Modifier.shimmer()
        ) {
            Text(stringResource(Res.string.add_symbol))
        }
    }
}

@Preview(
    name = "Shimmer - Card Tile",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ShimmerCardTilePreview() {
    ShimmerPreviewContainer {
        ShimmerPreviewTile(
            title = stringResource(Res.string.example_title),
            modifier = Modifier.shimmer()
        )
    }
}

@Preview(
    name = "Shimmer - Whole Screen",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
private fun ShimmerWholeScreenPreview() {
    CustomeButtonTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .shimmer(),
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Text(stringResource(Res.string.add_symbol))
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    stringResource(Res.string.my_buttons),
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(stringResource(Res.string.home_tagline))
                ShimmerPreviewTile(title = stringResource(Res.string.example_title))
                ShimmerPreviewTile(title = stringResource(Res.string.example_hug_title))
            }
        }
    }
}
