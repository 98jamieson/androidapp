
package com.example.myloginapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myloginapp.models.Meal
import com.example.myloginapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf

class MealViewModel : ViewModel() {
    var meals = mutableStateOf<List<Meal>>(emptyList())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun searchMeals(query: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = RetrofitInstance.api.searchMeals(query)
                meals.value = response.meals
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}
