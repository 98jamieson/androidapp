package com.example.myloginapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myloginapp.viewmodels.MealViewModel
import com.example.myloginapp.viewmodels.MealTranslatorViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MealScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealDetailScreen()
        }
    }
}

@Composable
fun MealDetailScreen(
    viewModel: MealViewModel = viewModel(),
    translatorVM: MealTranslatorViewModel = viewModel()
) {


    val context = LocalContext.current

    // Intercept system back button
    BackHandler {
        val intent = Intent(context, MainScreenActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        context.startActivity(intent)
    }



    val meals = viewModel.meals.value
    val isLoading = viewModel.isLoading.value
    val translatedTexts by translatorVM.translatedTexts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.searchMeals("caramel")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(7.dp)
    ) {
        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .clip(RoundedCornerShape(20.dp)) // 👈 Rounds the corners
                .background(Color(0xFFD9D9D9))   // Background color

        ){







        if (isLoading) {
            Text("Cargando platillos...", color = Color.White)
        } else {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(meals) { meal ->

                    val mealName = meal.strMeal ?: "Sin nombre"
                    translatorVM.translateText(mealName, "meal_${meal.idMeal}_name")
                    val translatedName = translatedTexts["meal_${meal.idMeal}_name"] ?: mealName

                    val category = meal.strCategory ?: "Sin categoría"
                    translatorVM.translateText(category, "meal_${meal.idMeal}_cat")
                    val translatedCategory = translatedTexts["meal_${meal.idMeal}_cat"] ?: category

                    val ingredient1 = meal.strIngredient1 ?: "Ingrediente 1"
                    val ingredient2 = meal.strIngredient2 ?: "Ingrediente 2"
                    val ingredient3 = meal.strIngredient3 ?: "Ingrediente 3"

                    val instructions = meal.strInstructions ?: "Sin instrucciones"
                    translatorVM.translateText(instructions, "meal_${meal.idMeal}_inst")
                    val translatedInstructions =
                        translatedTexts["meal_${meal.idMeal}_inst"] ?: instructions

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp)
                    ) {
                        // 🔹 Nombre del plato
                        Text(
                            text = translatedName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 🔹 Imagen circular
                        AsyncImage(
                            model = meal.strMealThumb,
                            contentDescription = translatedName,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape) // 👈 This clips the image content to a circle
                                .background(Color.White, CircleShape) // optional white border background
                                .padding(4.dp),
                            contentScale = ContentScale.Crop
                        )


                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔹 Categoría
                        Text(
                            text = "Categoría: $translatedCategory",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 🔹 Lista de ingredientes
                        Text(
                            text = "Lista de ingredientes",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Column(
                            Modifier.padding(start = 16.dp, top = 4.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text("• $ingredient1", color = Color.White, fontSize = 16.sp)
                            Text("• $ingredient2", color = Color.White, fontSize = 16.sp)
                            Text("• $ingredient3", color = Color.White, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 🔹 Instrucciones
                        Text(
                            text = "Instrucciones",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = translatedInstructions,
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
        }
    }
}

