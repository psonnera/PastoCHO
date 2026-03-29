package com.psonnera.pastocho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.psonnera.pastocho.ui.theme.PastoCHOTheme
import com.psonnera.pastocho.ui.meal.MealPlannerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PastoCHOTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MealPlannerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}