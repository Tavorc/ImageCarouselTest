package com.example.imagecarouseltest.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

data class CanvasImage(
    val id: String,
    val carouselImage: CarouselImage,
    val position: Offset = Offset.Zero,
    val scale: Float = 1f,
    val size: IntSize = IntSize.Zero
)
