package com.example.myloginapp.viewmodels
import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myloginapp.translate.TranslatorHelper
import kotlinx.coroutines.launch

class TranslateViewModel(application: Application) : AndroidViewModel(application) {
    private val translator = TranslatorHelper(application)

    var translatedText by mutableStateOf("")
        private set

    fun translate(text: String) {
        viewModelScope.launch {
            translatedText = translator.translateText(text)
        }
    }

    override fun onCleared() {
        super.onCleared()
        translator.close()
    }
}
