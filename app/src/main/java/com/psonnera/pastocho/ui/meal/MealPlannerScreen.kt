package com.psonnera.pastocho.ui.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.psonnera.pastocho.domain.calculateAdjustedValues
import com.psonnera.pastocho.model.Course
import com.psonnera.pastocho.model.FoodItem
import com.psonnera.pastocho.model.ModifierOption
import kotlin.math.roundToInt

@Composable
fun MealPlannerScreen(
    modifier: Modifier = Modifier,
    viewModel: MealPlannerViewModel = viewModel()
) {
    val state = viewModel.state
    val totalMealCHO by remember { derivedStateOf { state.totalMealCho } }

    Column(modifier = modifier.padding(16.dp)) {
        CourseSelector(
            selectedCourse = state.selectedCourse,
            onCourseSelected = state::selectCourse
        )

        if (state.selectedCourse == Course.ALTRO) {
            val totalCHO = state.manualTotalCho

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val manualWeightError = state.manualWeightErrorMessage()
                    val manualCarbsError = state.manualCarbsErrorMessage()

                    TextField(
                        value = state.weightInput,
                        onValueChange = state::updateManualWeightInput,
                        label = { Text("Peso (g)") },
                        isError = manualWeightError != null,
                        supportingText = {
                            if (manualWeightError != null) {
                                Text(manualWeightError)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual-weight-input")
                            .padding(end = 8.dp)
                    )

                    TextField(
                        value = state.carbsInput,
                        onValueChange = state::updateManualCarbsInput,
                        label = { Text("CHO %") },
                        isError = manualCarbsError != null,
                        supportingText = {
                            if (manualCarbsError != null) {
                                Text(manualCarbsError)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual-carbs-input")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "CHO totale: %.1f".format(totalCHO),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .testTag("manual-cho-preview")
                )

                Button(
                    onClick = state::addManualMealItem,
                    enabled = state.isManualInputValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual-add-button")
                ) {
                    Text("Aggiungi al pasto")
                }
            }
        } else {
            FoodDropdown(
                foods = state.catalog,
                selectedCourse = state.selectedCourse,
                selectedFood = state.selectedFood,
                onFoodSelected = state::selectFood
            )

            state.selectedFood?.let { food ->
                val selectedOptions = state.selectedModifiers.values.toList()
                val (adjustedWeight, adjustedCarbsPerGram) = calculateAdjustedValues(
                    food,
                    selectedOptions
                )

                val computedValues = state.computeSelectedValues(
                    food = food,
                    adjustedWeight = adjustedWeight,
                    adjustedCarbsPer100g = adjustedCarbsPerGram
                )
                val effectiveWeight = computedValues?.first ?: 0.0
                val totalCHO = computedValues?.second ?: 0.0

                ModifierSelector(
                    food = food,
                    selectedModifiers = state.selectedModifiers,
                    onOptionSelected = state::toggleModifier
                )

                if (food.isPieceBased) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(onClick = {
                                        state.decrementPieceCount()
                                    }) {
                                        Text("–")
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF1976D2), shape = RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = state.pieceCount.toString(),
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(onClick = state::incrementPieceCount) {
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

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val pieceWeightError = state.pieceWeightErrorMessage()
                                val pieceCarbsError = state.pieceCarbsErrorMessage()

                                TextField(
                                    value = state.pieceWeightInput,
                                    onValueChange = state::updatePieceWeightInput,
                                    label = { Text("Peso (g)") },
                                    isError = pieceWeightError != null,
                                    supportingText = {
                                        if (pieceWeightError != null) {
                                            Text(pieceWeightError)
                                        }
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("piece-weight-input")
                                        .padding(end = 8.dp)
                                )

                                TextField(
                                    value = state.pieceCarbsPer100gInput,
                                    onValueChange = state::updatePieceCarbsInput,
                                    label = { Text("CHO %") },
                                    isError = pieceCarbsError != null,
                                    supportingText = {
                                        if (pieceCarbsError != null) {
                                            Text(pieceCarbsError)
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("piece-carbs-input")
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { state.addSelectedFoodItem(effectiveWeight, totalCHO) },
                        enabled = computedValues != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("selected-food-add-button")
                            .padding(top = 12.dp)
                    ) {
                        Text("Aggiungi al pasto", fontSize = 18.sp)
                    }
                } else {
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
                            val selectedWeightError = state.selectedWeightErrorMessage(adjustedWeight)

                            TextField(
                                value = state.weightInput,
                                onValueChange = state::updateSelectedWeightInput,
                                label = { Text("Peso (g)", fontSize = 18.sp) },
                                textStyle = LocalTextStyle.current.copy(fontSize = 24.sp),
                                isError = selectedWeightError != null,
                                supportingText = {
                                    if (selectedWeightError != null) {
                                        Text(selectedWeightError)
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("selected-weight-input")
                                    .padding(end = 8.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(0.3f)
                                    .padding(horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.size(36.dp)) {
                                    Button(
                                        onClick = state::incrementSelectedWeight,
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
                                        onClick = state::decrementSelectedWeight,
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

                            Text(
                                text = if (computedValues != null) {
                                    "${totalCHO.roundToInt()} CHO"
                                } else {
                                    "-- CHO"
                                },
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
                        onClick = { state.addSelectedFoodItem(effectiveWeight, totalCHO) },
                        enabled = computedValues != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("selected-food-add-button")
                            .padding(top = 12.dp)
                    ) {
                        Text("Aggiungi al pasto", fontSize = 18.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Pasto corrente:", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
                .testTag("meal-items-list"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.mealItems) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            .clickable { state.removeMealItem(item) }
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
                color = Color(0xFF2E7D32),
                modifier = Modifier.testTag("meal-total")
            )
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
        Course.entries.forEach { course ->
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .testTag("course-${course.name.lowercase()}")
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
fun FoodDropdown(
    foods: List<FoodItem>,
    selectedCourse: Course,
    selectedFood: FoodItem?,
    onFoodSelected: (FoodItem) -> Unit,
) {
    val filteredFoods = remember(foods, selectedCourse) {
        foods.filter { it.course == selectedCourse }
    }
    var expanded by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("food-dropdown-trigger")
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
                        onClick = { onOptionSelected(modifier.label, option) },
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