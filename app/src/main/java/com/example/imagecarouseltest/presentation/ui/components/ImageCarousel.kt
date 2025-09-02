package com.example.imagecarouseltest.presentation.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.imagecarouseltest.data.model.CarouselImage

@Composable
fun ImageCarousel(
    images: List<CarouselImage>,
    onImageDragStart: (CarouselImage, Offset) -> Unit,
    onImageDrag: (Offset) -> Unit,
    onImageDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        ) {
            items(images) { image ->
                DraggableCarouselItem(
                    image = image,
                    onDragStart = onImageDragStart,
                    onDrag = onImageDrag,
                    onDragEnd = onImageDragEnd,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

@Composable
fun DraggableCarouselItem(
    image: CarouselImage,
    onDragStart: (CarouselImage, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }

    Card(
        modifier = modifier
            .size(120.dp)
            .onGloballyPositioned { coordinates ->
                globalPosition = coordinates.positionInRoot()
            }
            .pointerInput(image.id) {
                detectDragGestures(
                    onDragStart = { localOffset ->
                        isDragging = true
                        // Store the local touch offset within the image
                        dragStartOffset = localOffset

                        // Calculate the exact global position where the touch occurred
                        val exactTouchPosition = Offset(
                            x = globalPosition.x + localOffset.x,
                            y = globalPosition.y + localOffset.y
                        )
                        onDragStart(image, exactTouchPosition)
                    },
                    onDrag = { change, _ ->
                        // Update position based on the exact touch point and drag movement
                        val newPosition = Offset(
                            x = globalPosition.x + dragStartOffset.x + change.position.x - dragStartOffset.x,
                            y = globalPosition.y + dragStartOffset.y + change.position.y - dragStartOffset.y
                        )
                        onDrag(newPosition)
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnd()
                    }
                )
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        AsyncImage(
            model = image.url,
            contentDescription = image.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}