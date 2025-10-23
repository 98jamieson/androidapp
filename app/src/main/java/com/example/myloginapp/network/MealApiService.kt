package com.example.myloginapp.network

// MealApiService.kt

import com.example.myloginapp.models.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    //@GET("api/json/v1/1/search.php")
    @GET("api/json/v1/1/random.php")
    suspend fun searchMeals(@Query("") query: String): MealResponse

}
