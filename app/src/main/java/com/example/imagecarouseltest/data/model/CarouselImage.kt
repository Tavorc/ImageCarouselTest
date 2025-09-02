package com.example.imagecarouseltest.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarouselImage(
    val id: String,
    val url: String,
    val title: String
): Parcelable
