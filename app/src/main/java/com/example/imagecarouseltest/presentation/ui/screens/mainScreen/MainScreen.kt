package com.example.imagecarouseltest.presentation.ui.screens.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.imagecarouseltest.R
import com.example.imagecarouseltest.data.model.CarouselImage
import com.example.imagecarouseltest.presentation.ui.components.CanvasArea
import com.example.imagecarouseltest.presentation.ui.components.ImageCarousel
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {

    val carouselImages by viewModel.carouselImages.collectAsState()
    val canvasImages by viewModel.canvasImages.collectAsState()

    var draggedImage by remember { mutableStateOf<CarouselImage?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            canvasPosition = coordinates.positionInRoot()
                            canvasSize = Offset(
                                coordinates.size.width.toFloat(),
                                coordinates.size.height.toFloat()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CanvasArea(
                        canvasImages = canvasImages,
                        onImagePositionUpdate = { imageId, position ->
                            viewModel.updateImagePositionWithinCanvas(imageId, position)
                        },
                        onImageScaleUpdate = { imageId, scale ->
                            viewModel.updateImageScale(imageId, scale)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.drag_image_explanation),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                ImageCarousel(
                    images = carouselImages,
                    onImageDragStart = { image, startOffset ->
                        draggedImage = image
                        dragPosition = startOffset
                        isDragging = true
                    },
                    onImageDrag = { newPosition ->
                        dragPosition = newPosition
                    },
                    onImageDragEnd = {
                        draggedImage?.let { image ->

                            val dropX = dragPosition.x
                            val dropY = dragPosition.y

                            // Checking if dropped on canvas
                            if (dropX >= canvasPosition.x &&
                                dropX <= canvasPosition.x + canvasSize.x &&
                                dropY >= canvasPosition.y &&
                                dropY <= canvasPosition.y + canvasSize.y) {

                                // Calculate position relative to canvas
                                val imageSize = with(density) { 100.dp.toPx() }
                                val canvasX = dropX - canvasPosition.x - (imageSize / 2f)
                                val canvasY = dropY - canvasPosition.y - (imageSize / 2f)

                                viewModel.addImageToCanvas(image, Offset(canvasX, canvasY))
                            }
                        }

                        // Reset drag state
                        draggedImage = null
                        isDragging = false
                        dragPosition = Offset.Zero
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Show the image when dragging
        if (isDragging && draggedImage != null) {
            val imageSize = with(density) { 100.dp.toPx() }

            AsyncImage(
                model = draggedImage!!.url,
                contentDescription = draggedImage!!.title,
                modifier = Modifier
                    .size(100.dp)
                    .offset {
                        IntOffset(
                            x = (dragPosition.x - imageSize / 2f).roundToInt(),
                            y = (dragPosition.y - imageSize / 2f).roundToInt()
                        )
                    }
                    .zIndex(1000f)
                    .shadow(12.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )
        }
    }
}