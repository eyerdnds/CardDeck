package com.example.murycs.model

import com.google.gson.annotations.SerializedName

data class Car(
    @SerializedName("Make_ID") val id: Int,
    @SerializedName("Make_Name") val name: String,
    var imageUrl: String? = null,
    var category: String? = null
)
