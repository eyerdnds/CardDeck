package com.example.murycs.model

import com.google.gson.annotations.SerializedName

data class CarResponse(
    @SerializedName("Count") val count: Int,
    @SerializedName("Message") val message: String,
    @SerializedName("Results") val results: List<Car>
)
