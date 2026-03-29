package com.psonnera.pastocho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.psonnera.pastocho.data.foodList
import com.psonnera.pastocho.domain.calculateAdjustedValues
import com.psonnera.pastocho.model.Course
import com.psonnera.pastocho.model.FoodItem
import com.psonnera.pastocho.model.MealItem
import com.psonnera.pastocho.model.ModifierOption
import com.psonnera.pastocho.ui.theme.PastoCHOTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PastoCHOTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MealPlannerUI(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CourseSelector(
    selectedCourse: Course,
    onCourseSelected: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Course.values().forEach { course ->
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .clickable { onCourseSelected(course) }
                    .background(
                        color = if (course == selectedCourse) Color(0xFF1976D2) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(course.emoji, fontSize = 20.sp)
                Text(
                    course.name,
                    fontSize = 8.sp,
                    color = if (course == selectedCourse) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun MealPlannerUI(modifier: Modifier = Modifier) {
    var selectedCourse by remember { mutableStateOf(Course.COLAZIONE) }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    var selectedModifiers by remember { mutableStateOf<Map<String, ModifierOption>>(emptyMap()) }

    var weightInput by remember { mutableStateOf("") }
    var carbsInput by remember { mutableStateOf("") }

    var weightValue by remember { mutableStateOf(0.0) }
    var carbsValue by remember { mutableStateOf(0.0) }

    val mealItems = remember { mutableStateListOf<MealItem>() }

    LaunchedEffect(selectedFood) {
        if (selectedFood != null) {
            val food = selectedFood!!
            weightInput = "%.0f".format(food.defaultWeight.toDouble())
            carbsInput = "%.1f".format(food.carbsPer100g.toDouble())
            weightValue = food.defaultWeight.toDouble()
            carbsValue = food.carbsPer100g.toDouble()
        }
    }

    fun toggleModifier(label: String, option: ModifierOption) {
        selectedModifiers = selectedModifiers.toMutableMap().apply {
            this[label] = option
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        CourseSelector(
            selectedCourse = selectedCourse,
            onCourseSelected = {
                selectedCourse = it
                selectedFood = null
                selectedModifiers = emptyMap()
            }
        )

        var pieceCount by remember { mutableStateOf(1) }

        if (selectedCourse == Course.ALTRO) {
            val totalCHO = weightValue * carbsValue / 100.0

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // 📝 Manual entry fields side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = weightInput,
                        onValueChange = { input ->
                            weightInput = input
                            val parsed = input.replace(",", ".").toDoubleOrNull()
                            if (parsed != null) weightValue = parsed
                        },
                        label = { Text("Peso (g)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    TextField(
                        value = carbsInput,
                        onValueChange = { input ->
                            carbsInput = input
                            val parsed = input.replace(",", ".").toDoubleOrNull()
                            if (parsed != null) carbsValue = parsed
                        },
                        label = { Text("CHO %") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ CHO preview
                Text(
                    text = "CHO totale: %.1f".format(totalCHO),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // ✅ Add to meal button
                Button(
                    onClick = {
                        val item = MealItem(
                            name = "Manuale",
                            weight = weightValue,
                            totalCHO = totalCHO
                        )
                        mealItems.add(item)
                        weightInput = ""
                        carbsInput = ""
                        weightValue = 0.0
                        carbsValue = 0.0
                    },
                    enabled = weightValue > 0 && carbsValue > 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aggiungi al pasto")
                }
            }
        } else
        {
            // 🍽️ Food selection for other courses

        FoodDropdown(
            selectedCourse = selectedCourse,
            selectedFood = selectedFood,
            onFoodSelected = { food ->
                selectedFood = food
                weightInput = food.defaultWeight.toString()

                // Auto-select first option for each modifier group
                val defaultModifiers = food.modifiers.associate { modifier ->
                    modifier.label to modifier.options.first()
                }
                selectedModifiers = defaultModifiers
            },
            selectedModifiers = selectedModifiers,
            onModifierToggle = { label, option -> toggleModifier(label, option) },
            weightInput = weightInput,
            onWeightChange = { weightInput = it }
        )

        selectedFood?.let { food ->
            val selectedOptions = selectedModifiers.values.toList()
            val (adjustedWeight, adjustedCarbsPerGram) = calculateAdjustedValues(
                food,
                selectedOptions
            )

            val effectiveWeight: Double
            val totalCHO: Double
            var carbsPer100gInput by remember(food) { mutableStateOf(food.carbsPer100g.toString()) }

            var weightPerPieceInput by remember(food) { mutableStateOf(food.defaultWeight.toString()) }
            //var carbsPer100gInput by remember(food) { mutableStateOf(food.carbsPer100g.toString()) }

            LaunchedEffect(food) {
                weightPerPieceInput = "%.1f".format(food.defaultWeight.toDouble())
                carbsPer100gInput = "%d".format(food.carbsPer100g)
            }

            if (food.isPieceBased) {
                val weightPerPiece =
                    weightPerPieceInput.toDoubleOrNull() ?: food.defaultWeight.toDouble()
                val carbsPer100g =
                    carbsPer100gInput.toDoubleOrNull() ?: food.carbsPer100g.toDouble()

                effectiveWeight = pieceCount * weightPerPiece
                totalCHO = effectiveWeight * carbsPer100g / 100.0
            } else {
                val weight = weightInput.toDoubleOrNull() ?: adjustedWeight
                effectiveWeight = weight
                totalCHO = effectiveWeight * adjustedCarbsPerGram / 100.0
            }

            ModifierSelector(
                food = food,
                selectedModifiers = selectedModifiers,
                onOptionSelected = { label, option -> toggleModifier(label, option) }
            )

            if (food.isPieceBased) {
                // 🍪 Piece-based card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        // 🍽️ Piece selector + CHO display
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(onClick = {
                                    pieceCount = (pieceCount - 1).coerceAtLeast(1)
                                }) {
                                    Text("–")
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1976D2), shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = pieceCount.toString(),
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(onClick = { pieceCount++ }) {
                                    Text("+")
                                }
                            }

                            Text(
                                text = "%d CHO".format(totalCHO.roundToInt()),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        // 🧮 Weight + CHO % inputs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //var weightPerPieceInput by remember { mutableStateOf("%.1f".format(food.defaultWeight)) }
                            var weightPerPieceValue by remember { mutableStateOf(food.defaultWeight) }

                            TextField(
                                value = weightPerPieceInput,
                                onValueChange = { input ->
                                    weightPerPieceInput = input
                                    val parsed = input.replace(",", ".").toDoubleOrNull()
                                    if (parsed != null) {
                                        weightPerPieceValue = parsed.roundToInt()
                                    }
                                },
                                label = { Text("Peso (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )

                            TextField(
                                value = carbsPer100gInput,
                                onValueChange = { input ->
                                    val number = input.replace(",", ".").toDoubleOrNull()
                                    carbsPer100gInput = if (number != null) "%d".format(number.toInt()) else input
                                },
                                label = { Text("CHO %") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val item = MealItem(
                            name = food.name,
                            weight = effectiveWeight,
                            totalCHO = totalCHO
                        )
                        mealItems.add(item)
                        selectedFood = null
                              },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text("Aggiungi al pasto", fontSize = 18.sp)
                        }

            } else {
                // ⚖️ Standard weight-based card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🧮 Weight input
                        TextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Peso (g)", fontSize = 18.sp) },
                            textStyle = LocalTextStyle.current.copy(fontSize = 24.sp),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )

                        // ➕➖ Buttons
                        Column(
                            modifier = Modifier
                                .weight(0.3f) // Give it some space
                                .padding(horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.size(36.dp)) {
                                Button(
                                    onClick = {
                                        val current = weightInput.toDoubleOrNull() ?: 0.0
                                        weightInput = "%.0f".format(current + 5)
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "+",
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(modifier = Modifier.size(36.dp)) {
                                Button(
                                    onClick = {
                                        val current = weightInput.toDoubleOrNull() ?: 0.0
                                        weightInput = "%.0f".format(current - 5)
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "-",
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // 🍬 CHO result
                        Text(
                            text = "${totalCHO.roundToInt()} CHO",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End)
                        )
                    }
                }

                Button(
                    onClick = {
                        val item = MealItem(
                            name = food.name,
                            weight = effectiveWeight,
                            totalCHO = totalCHO
                        )
                        mealItems.add(item)
                        selectedFood = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Aggiungi al pasto", fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Pasto corrente:", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Column(modifier = Modifier.fillMaxWidth()) {
            mealItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.name, fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("%d g".format(item.weight.roundToInt()), fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("%d CHO".format(item.totalCHO.roundToInt()), fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Rimuovi",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { mealItems.remove(item) }
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )
        Spacer(modifier = Modifier.height(12.dp))

        val totalMealCHO = mealItems.sumOf { it.totalCHO }

        Box(
            modifier = Modifier
                .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .align(Alignment.End)
        ) {
            Text(
                text = "Totale CHO: %d".format(totalMealCHO.roundToInt()),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }
    }
 }
}

@Composable
fun FoodDropdown(
    selectedCourse: Course,
    selectedFood: FoodItem?,
    onFoodSelected: (FoodItem) -> Unit,
    selectedModifiers: Map<String, ModifierOption>,
    onModifierToggle: (String, ModifierOption) -> Unit,
    weightInput: String,
    onWeightChange: (String) -> Unit) {
    val filteredFoods = foodList.filter { it.course == selectedCourse }
    var expanded by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .border(1.dp, Color.Gray)
                .padding(12.dp)
        ) {
            Text(text = selectedFood?.name ?: "Seleziona alimento")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filteredFoods.forEach { food ->
                DropdownMenuItem(
                    text = { Text(food.name) },
                    onClick = {
                        onFoodSelected(food)
                        expanded = false
                    }
                )
            }
        }
    }
 }

@Composable
fun ModifierSelector(
    food: FoodItem,
    selectedModifiers: Map<String, ModifierOption>,
    onOptionSelected: (String, ModifierOption) -> Unit
) {
    Column {
        food.modifiers.forEach { modifier ->
            Text(text = modifier.label, modifier = Modifier.padding(vertical = 4.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                modifier.options.forEach { option ->
                    val isSelected = selectedModifiers[modifier.label] == option

                    Button(
                        onClick = { onOptionSelected(modifier.label, option)
                             },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.Blue else Color.LightGray
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(option.name)
                    }
                }
            }
        }
    }
}