package com.example.murycs.network

import com.example.murycs.model.CarResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CarApi {
    @GET("api/vehicles/GetAllMakes")
    fun getAllMakes(@Query("format") format: String = "json"): Call<CarResponse>
}
