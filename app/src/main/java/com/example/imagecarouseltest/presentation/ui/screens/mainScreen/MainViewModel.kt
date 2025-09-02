package com.example.imagecarouseltest.presentation.ui.screens.mainScreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagecarouseltest.data.model.CanvasImage
import com.example.imagecarouseltest.data.model.CarouselImage
import com.example.imagecarouseltest.domain.usecases.GetCarouselImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCarouselImagesUseCase: GetCarouselImagesUseCase
) : ViewModel() {

    private val _carouselImages = MutableStateFlow<List<CarouselImage>>(emptyList())
    val carouselImages: StateFlow<List<CarouselImage>> = _carouselImages.asStateFlow()

    private val _canvasImages = MutableStateFlow<List<CanvasImage>>(emptyList())
    val canvasImages: StateFlow<List<CanvasImage>> = _canvasImages.asStateFlow()


    init {
        loadCarouselImages()
    }

    private fun loadCarouselImages() {
        viewModelScope.launch {
            getCarouselImagesUseCase().collect { images ->
                _carouselImages.value = images
            }
        }
    }

    fun addImageToCanvas(carouselImage: CarouselImage, position: Offset) {
        val canvasImage = CanvasImage(
            id = UUID.randomUUID().toString(),
            carouselImage = carouselImage,
            position = position,
            scale = 1f,
            size = IntSize(200, 200)
        )
        _canvasImages.value += canvasImage
    }

    fun updateImagePositionWithinCanvas(imageId: String, newPosition: Offset) {
        _canvasImages.value = _canvasImages.value.map { canvasImage ->
            if (canvasImage.id == imageId) {
                canvasImage.copy(position = newPosition)
            } else {
                canvasImage
            }
        }
    }

    fun updateImageScale(imageId: String, newScale: Float) {
        _canvasImages.value = _canvasImages.value.map { canvasImage ->
            if (canvasImage.id == imageId) {
                canvasImage.copy(scale = newScale)
            } else {
                canvasImage
            }
        }
    }
}