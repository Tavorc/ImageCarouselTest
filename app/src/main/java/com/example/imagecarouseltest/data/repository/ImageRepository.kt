package com.example.imagecarouseltest.data.repository

import com.example.imagecarouseltest.data.model.CarouselImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageRepository {

    fun getCarouselImages(): Flow<List<CarouselImage>> = flow {
        val images = listOf(
            CarouselImage("1", "https://picsum.photos/300/300?random=1", "Nature 1"),
            CarouselImage("2", "https://picsum.photos/300/300?random=2", "Nature 2"),
            CarouselImage("3", "https://picsum.photos/300/300?random=3", "Nature 3"),
            CarouselImage("4", "https://picsum.photos/300/300?random=4", "Nature 4"),
            CarouselImage("5", "https://picsum.photos/300/300?random=5", "Nature 5"),
        )
        emit(images)
    }
}