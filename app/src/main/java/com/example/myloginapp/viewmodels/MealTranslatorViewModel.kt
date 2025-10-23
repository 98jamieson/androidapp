package com.example.myloginapp.viewmodels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myloginapp.translate.TranslatorHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MealTranslatorViewModel(application: Application) : AndroidViewModel(application) {
    private val translator = TranslatorHelper(application)

    private val _translatedTexts = MutableStateFlow<Map<String, String>>(emptyMap())
    val translatedTexts: StateFlow<Map<String, String>> = _translatedTexts

    fun translateText(original: String, id: String) {
        // Evita repetir traducciones ya realizadas
        if (_translatedTexts.value.containsKey(id)) return

        viewModelScope.launch {
            val translated = translator.translateText(original)
            _translatedTexts.value = _translatedTexts.value + (id to translated)
        }
    }

    override fun onCleared() {
        super.onCleared()
        translator.close()
    }
}
