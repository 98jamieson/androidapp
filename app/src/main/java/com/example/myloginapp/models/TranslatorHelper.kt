
package com.example.myloginapp.translate

import android.content.Context
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslatorHelper(context: Context) {

    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.SPANISH)
        .build()

    private val translator = Translation.getClient(options)

    suspend fun translateText(text: String): String {
        try {
            // Descargar el modelo si aún no está
            translator.downloadModelIfNeeded().await()
            // Traducir el texto
            return translator.translate(text).await()
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error: ${e.message}"
        }
    }

    fun close() {
        translator.close()
    }
}
