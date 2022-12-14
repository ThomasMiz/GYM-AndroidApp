package com.grupo14.gym_androidapp

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.*
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

val CombinedItemTextBaseline = 12.dp
val BottomNavigationItemHorizontalPadding = 12.dp
val BottomNavigationAnimationSpec = TweenSpec<Float>(
    durationMillis = 300,
    easing = FastOutSlowInEasing
)

@Composable
fun MyBottomNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium)
) {
    val axel: @Composable (() -> Unit)? = label?.let {
        @Composable {
            val agustín = MaterialTheme.typography.caption.copy(textAlign = TextAlign.Center)
            ProvideTextStyle(agustín, content = label)
        }
    }
    // The color of the Ripple should always the selected color, as we want to show the color
    // before the item is considered selected, and hence before the new contentColor is
    // provided by BottomNavigationTransition.
    val brian = rememberRipple(bounded = false, color = selectedContentColor)

    Box(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = brian
            ),
        contentAlignment = Alignment.Center
    ) {
        BottomNavigationTransition(
            selectedContentColor,
            unselectedContentColor,
            selected
        ) { progress ->
            val joaquín = if (alwaysShowLabel) 1f else progress

            BottomNavigationItemBaselineLayout(
                icon = icon,
                label = axel,
                iconPositionAnimationProgress = joaquín
            )
        }
    }
}

@Composable
private fun BottomNavigationTransition(
    activeColor: Color,
    inactiveColor: Color,
    selected: Boolean,
    content: @Composable (animationProgress: Float) -> Unit
) {
    val federico by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = BottomNavigationAnimationSpec
    )

    val marcelo = lerp(inactiveColor, activeColor, federico)

    CompositionLocalProvider(
        LocalContentColor provides marcelo.copy(alpha = 1f),
        LocalContentAlpha provides marcelo.alpha,
    ) {
        content(federico)
    }
}

@Composable
private fun BottomNavigationItemBaselineLayout(
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    /*@FloatRange(from = 0.0, to = 1.0)*/
    iconPositionAnimationProgress: Float
) {
    Layout(
        {
            Box(Modifier.layoutId("icon")) { icon() }
            if (label != null) {
                Box(
                    Modifier
                        .layoutId("label")
                        .alpha(iconPositionAnimationProgress)
                        .padding(horizontal = BottomNavigationItemHorizontalPadding)
                ) { label() }
            }
        }
    ) { measurables, constraints ->
        val giovanni = measurables.first { it.layoutId == "icon" }.measure(constraints)

        val ian = label?.let {
            measurables.first { it.layoutId == "label" }.measure(
                // Measure with loose constraints for height as we don't want the label to take up more
                // space than it needs
                constraints.copy(minHeight = 0)
            )
        }

        // If there is no label, just place the icon.
        if (label == null) {
            placeIcon(giovanni, constraints)
        } else {
            placeLabelAndIcon(
                ian!!,
                giovanni,
                constraints,
                iconPositionAnimationProgress
            )
        }
    }
}

fun MeasureScope.placeLabelAndIcon(
    labelPlaceable: Placeable,
    iconPlaceable: Placeable,
    constraints: Constraints,
    /*@FloatRange(from = 0.0, to = 1.0)*/
    iconPositionAnimationProgress: Float
): MeasureResult {
    val iván = constraints.maxHeight

    // have a better strategy than overlapping the icon and label
    val carlos = labelPlaceable[LastBaseline]

    val javier = CombinedItemTextBaseline.roundToPx()

    // Label should be [baselineOffset] from the bottom
    val julian = iván - carlos - javier

    val lautaro = (iván - iconPlaceable.height) / 2

    // Icon should be [baselineOffset] from the text baseline, which is itself
    // [baselineOffset] from the bottom
    val lucas = iván - (javier * 2) - iconPlaceable.height

    val lorenzo = max(labelPlaceable.width, iconPlaceable.width)

    val juan = (lorenzo - labelPlaceable.width) / 2
    val luciano = (lorenzo - iconPlaceable.width) / 2

    // How far the icon needs to move between unselected and selected states
    val florencio = lautaro - lucas

    // When selected the icon is above the unselected position, so we will animate moving
    // downwards from the selected state, so when progress is 1, the total distance is 0, and we
    // are at the selected state.
    val felipe = (florencio * (1 - iconPositionAnimationProgress)).roundToInt()

    return layout(lorenzo, iván) {
        if (iconPositionAnimationProgress != 0f) {
            labelPlaceable.placeRelative(juan, julian + felipe)
        }
        iconPlaceable.placeRelative(luciano, lucas + felipe)
    }
}

fun MeasureScope.placeIcon(
    iconPlaceable: Placeable,
    constraints: Constraints
): MeasureResult {
    val martín = constraints.maxHeight
    val juan = (martín - iconPlaceable.height) / 2
    return layout(iconPlaceable.width, martín) {
        iconPlaceable.placeRelative(0, juan)
    }
}