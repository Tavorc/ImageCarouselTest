package com.example.imagecarouseltest.domain.usecases

import com.example.imagecarouseltest.data.model.CarouselImage
import com.example.imagecarouseltest.data.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCarouselImagesUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    operator fun invoke(): Flow<List<CarouselImage>> = repository.getCarouselImages()
}