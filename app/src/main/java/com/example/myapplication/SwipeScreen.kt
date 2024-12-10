package com.example.myapplication

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun SwipeToDismissScreen(items: MutableList<String>) {
    LazyColumn {
        items(items, key = { it }) { item ->
            SwipeToDismissItem(
                item = item,
                onDismissed = { items.remove(item) }
            )
        }
    }
}

@Composable
fun SwipeToDismissItem(
    item: String,
    onDismissed: () -> Unit
) {

    val offsetX = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .swipeToDismiss(onDismissed = onDismissed, offsetX = offsetX) // Pass offsetX here
            .background(Color.Green)
    ) {
        Text(
            text = item,
            modifier = Modifier.padding(16.dp),
        )

        // Delete icon will appear when swiped sufficiently
        if (offsetX.value < -150f) { // Show the delete icon when the item is swiped far enough
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                IconButton(
                    onClick = { onDismissed() } // Trigger delete action when clicked
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
@Composable
private fun Modifier.swipeToDismiss(
    onDismissed: () -> Unit,
    offsetX: Animatable<Float, *>, // Pass offsetX to track the swipe progress
): Modifier {
    val layoutDirection = LocalLayoutDirection.current // Get the current layout direction (LTR or RTL)
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() } // Convert screenWidthDp to pixels
    val dismissalThreshold = 0.7f * screenWidthPx // 70% of the screen width

    return pointerInput(Unit) {
        val decay = splineBasedDecay<Float>(this)
        coroutineScope {
            while (true) {
                awaitPointerEventScope {
                    val pointerId = awaitFirstDown().id
                    launch { offsetX.stop() }
                    val velocityTracker = VelocityTracker()

                    horizontalDrag(pointerId) { change ->
                        val horizontalDragOffset = when (layoutDirection) {
                            LayoutDirection.Ltr -> {
                                if (change.positionChange().x < 0) {
                                    offsetX.value + change.positionChange().x
                                } else {
                                    return@horizontalDrag
                                }
                            }
                            LayoutDirection.Rtl -> {
                                if (change.positionChange().x > 0) {
                                    offsetX.value - change.positionChange().x
                                } else {
                                    return@horizontalDrag
                                }
                            }
                            else -> return@horizontalDrag
                        }

                        launch { offsetX.snapTo(horizontalDragOffset) }
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }

                    val velocity = velocityTracker.calculateVelocity().x
                    offsetX.updateBounds(
                        lowerBound = -screenWidthPx,
                        upperBound = screenWidthPx
                    )

                    launch {
                        if (offsetX.value.absoluteValue >= dismissalThreshold) {
                            // Item is swiped sufficiently, dismiss it
                            offsetX.animateDecay(velocity, decay)
                            onDismissed() // Remove item after swipe completes
                        } else {
                            // If not swiped enough, animate back to the original position
                            offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                        }
                    }
                }
            }
        }
    }
        .offset { IntOffset(offsetX.value.roundToInt(), 0) } // Apply offsetX to move the row
}