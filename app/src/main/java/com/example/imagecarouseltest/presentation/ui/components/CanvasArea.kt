package com.example.imagecarouseltest.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import com.example.imagecarouseltest.data.model.CanvasImage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun CanvasArea(
    canvasImages: List<CanvasImage>,
    onImagePositionUpdate: (String, Offset) -> Unit,
    onImageScaleUpdate: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .onGloballyPositioned { coordinates ->
                canvasSize = coordinates.size
                canvasPosition = coordinates.positionInParent()
            }
    ) {
        canvasImages.forEach { canvasImage ->
            DraggableCanvasImage(
                canvasImage = canvasImage,
                onPositionUpdate = onImagePositionUpdate,
                onScaleUpdate = onImageScaleUpdate,
                canvasSize = canvasSize.toSize()
            )
        }
    }
}

@Composable
fun DraggableCanvasImage(
    canvasImage: CanvasImage,
    onPositionUpdate: (String, Offset) -> Unit,
    onScaleUpdate: (String, Float) -> Unit,
    canvasSize: Size,
    modifier: Modifier = Modifier
) {
    var position by remember(canvasImage.position) { mutableStateOf(canvasImage.position) }
    var scale by remember(canvasImage.scale) { mutableStateOf(canvasImage.scale) }
    val density = LocalDensity.current

    val baseImageSize = with(density) { 100.dp.toPx() }

    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    x = position.x.roundToInt(),
                    y = position.y.roundToInt()
                )
            }
            .size(100.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
            }
            .pointerInput(canvasImage.id) {
                detectTransformGestures(
                    panZoomLock = false
                ) { _, pan, zoom, _ ->
                    // Calculate new scale with limits
                    val newScale = max(0.3f, min(4f, scale * zoom))

                    // Calculate the current scaled size
                    val currentScaledSize = baseImageSize * scale
                    val newScaledSize = baseImageSize * newScale

                    // Calculate the center of the current image
                    val currentCenterX = position.x + currentScaledSize / 2f
                    val currentCenterY = position.y + currentScaledSize / 2f

                    // Calculate new position to keep the same center point
                    val newX = currentCenterX - newScaledSize / 2f
                    val newY = currentCenterY - newScaledSize / 2f

                    // Apply pan gesture
                    val panAdjustedX = newX + pan.x
                    val panAdjustedY = newY + pan.y

                    // Constrain to canvas bounds
                    val constrainedX = max(
                        -(newScaledSize - baseImageSize) / 2f,
                        min(
                            canvasSize.width - newScaledSize + (newScaledSize - baseImageSize) / 2f,
                            panAdjustedX
                        )
                    )

                    val constrainedY = max(
                        -(newScaledSize - baseImageSize) / 2f,
                        min(
                            canvasSize.height - newScaledSize + (newScaledSize - baseImageSize) / 2f,
                            panAdjustedY
                        )
                    )

                    position = Offset(constrainedX, constrainedY)
                    scale = newScale
                    onPositionUpdate(canvasImage.id, position)
                    onScaleUpdate(canvasImage.id, scale)
                }
            }
    ) {
        AsyncImage(
            model = canvasImage.carouselImage.url,
            contentDescription = canvasImage.carouselImage.title,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}