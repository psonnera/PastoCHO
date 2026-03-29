package com.psonnera.pastocho.data

import com.psonnera.pastocho.model.Course
import com.psonnera.pastocho.model.FoodItem
import com.psonnera.pastocho.model.FoodModifier
import com.psonnera.pastocho.model.ModifierOption
import com.psonnera.pastocho.model.ModifierTarget

val foodList = listOf(
    FoodItem("Latte", Course.COLAZIONE, 5, 200),
    FoodItem(
        name = "Biscotti",
        course = Course.COLAZIONE,
        carbsPer100g = 70,
        defaultWeight = 10,
        isPieceBased = true,
    ),
    FoodItem(
        name = "Fette Biscottate",
        course = Course.COLAZIONE,
        carbsPer100g = 75,
        defaultWeight = 9,
        isPieceBased = true,
    ),
    FoodItem(
        name = "Nutella (cucchiaino)",
        course = Course.COLAZIONE,
        carbsPer100g = 58,
        defaultWeight = 13,
        isPieceBased = true,
    ),
    FoodItem("Pasta Cruda", Course.PASTO, 70, 80),
    FoodItem(
        name = "Pasta Cotta",
        course = Course.PASTO,
        carbsPer100g = 29,
        defaultWeight = 200,
        modifiers = listOf(
            FoodModifier(
                label = "Sugo",
                options = listOf(
                    ModifierOption("Senza", 0, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Poco", percent = -10, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Tanto", -20, appliesTo = ModifierTarget.CarbsPerGram)
                )
            )
        )
    ),
    FoodItem("Pasta Fresca all'Uovo", Course.PASTO, 50, 125),
    FoodItem("Lasagne", Course.PASTO, 15, 250),
    FoodItem("Riso Crudo", Course.PASTO, 76, 80),
    FoodItem(
        name = "Riso Cotto",
        course = Course.PASTO,
        carbsPer100g = 36,
        defaultWeight = 200,
        modifiers = listOf(
            FoodModifier(
                label = "Sugo",
                options = listOf(
                    ModifierOption("Senza", 0, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Poco", -15, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Tanto", -25, appliesTo = ModifierTarget.CarbsPerGram)
                )
            )
        )
    ),
    FoodItem(
        "Risotto",
        course = Course.PASTO,
        carbsPer100g = 36,
        defaultWeight = 200,
        modifiers = listOf(
            FoodModifier(
                label = "Sugo",
                options = listOf(
                    ModifierOption("Senza", 0, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Poco", -5, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Tanto", -10, appliesTo = ModifierTarget.CarbsPerGram)
                )
            )
        )
    ),
    FoodItem("Gnocchi Freschi", Course.PASTO, 35, 125),
    FoodItem(
        name = "Gnocchi Cotti",
        course = Course.PASTO,
        carbsPer100g = 28,
        defaultWeight = 160,
        modifiers = listOf(
            FoodModifier(
                label = "Sugo",
                options = listOf(
                    ModifierOption("Senza", 0, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Poco", -10, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Tanto", -20, appliesTo = ModifierTarget.CarbsPerGram)
                )
            )
        )
    ),
    FoodItem("Pane", Course.PASTO, 50, 50),
    FoodItem("Patate", Course.PASTO, 20, 150),
    FoodItem("Patatine Fritte", Course.PASTO, 30, 150),
    FoodItem("Pizza", Course.PASTO, 50, 300),
    FoodItem(
        name = "Gelato da Banco",
        course = Course.MERENDA,
        carbsPer100g = 25,
        defaultWeight = 100,
        modifiers = listOf(
            FoodModifier(
                label = "Porzione (Cono)",
                options = listOf(
                    ModifierOption("1 Gusto", -20, appliesTo = ModifierTarget.Weight),
                    ModifierOption("2 Gusti", 0, appliesTo = ModifierTarget.Weight),
                    ModifierOption("3 Gusti", 25, appliesTo = ModifierTarget.Weight)
                )
            )
        )
    ),
    FoodItem(
        name = "Crepes",
        course = Course.MERENDA,
        carbsPer100g = 30,
        defaultWeight = 110,
        modifiers = listOf(
            FoodModifier(
                label = "Nutella",
                options = listOf(
                    ModifierOption("Senza", -40, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Poco", 0, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Tanto", +20, appliesTo = ModifierTarget.CarbsPerGram)
                )
            )
        )
    ),
    FoodItem("Crostata Marmellata (circa)", Course.MERENDA, 64, 100),
    FoodItem("Torta Cioccolato (circa)", Course.MERENDA, 54, 100),
    FoodItem("Torta di Mele (circa)", Course.MERENDA, 40, 100),
    FoodItem("Tiramisù (circa)", Course.MERENDA, 25, 100),
    FoodItem("Torta alla Crema (circa)", Course.MERENDA, 25, 100),
    FoodItem("Anguria/Melone", Course.FRUTTA, 8, 200),
    FoodItem(
        name = "Albicocca",
        course = Course.FRUTTA,
        carbsPer100g = 7,
        defaultWeight = 40,
        isPieceBased = true,
        modifiers = listOf(
            FoodModifier(
                label = "Dimensione",
                options = listOf(
                    ModifierOption("Media", 0, appliesTo = ModifierTarget.Weight),
                    ModifierOption("Grossa", +80, appliesTo = ModifierTarget.Weight)
                )
            )
        )
    ),
    FoodItem(
        name = "Prugna",
        course = Course.FRUTTA,
        carbsPer100g = 9,
        defaultWeight = 37,
        isPieceBased = true,
        modifiers = listOf(
            FoodModifier(
                label = "Dimensione",
                options = listOf(
                    ModifierOption("Piccola", -10, appliesTo = ModifierTarget.Weight),
                    ModifierOption("Media", 0, appliesTo = ModifierTarget.Weight),
                    ModifierOption("Grossa", +50, appliesTo = ModifierTarget.Weight)
                )
            )
        )
    ),
    FoodItem(
        name = "Pesca",
        course = Course.FRUTTA,
        carbsPer100g = 10,
        defaultWeight = 100,
        modifiers = listOf(
            FoodModifier(
                label = "Dimensione",
                options = listOf(
                    ModifierOption("Media", -10, appliesTo = ModifierTarget.Weight),
                    ModifierOption("Grossa", +10, appliesTo = ModifierTarget.Weight)
                )
            )
        )
    ),
    FoodItem(
        name = "Mandarino",
        course = Course.FRUTTA,
        carbsPer100g = 13,
        defaultWeight = 90,
        modifiers = listOf(
            FoodModifier(
                label = "Dimensione",
                options = listOf(
                    ModifierOption("Piccolo", -15, appliesTo = ModifierTarget.Weight),
                    ModifierOption("Medio", 0, appliesTo = ModifierTarget.Weight),
                    ModifierOption("Grosso", +15, appliesTo = ModifierTarget.Weight)
                )
            )
        )
    ),
    FoodItem("Banana", Course.FRUTTA, 20, 125),
    FoodItem("Kiwi", Course.FRUTTA, 10, 100),
    FoodItem("Pera", Course.FRUTTA, 12, 150),
    FoodItem("Uva", Course.FRUTTA, 15, 100),
    FoodItem("Mela", Course.FRUTTA, 12, 150),
    FoodItem("Fragole", Course.FRUTTA, 8, 150),
    FoodItem(
        name = "Macedonia di Frutta",
        course = Course.FRUTTA,
        carbsPer100g = 12,
        defaultWeight = 100,
        modifiers = listOf(
            FoodModifier(
                label = "Zuccherata",
                options = listOf(
                    ModifierOption("No", 0, appliesTo = ModifierTarget.CarbsPerGram),
                    ModifierOption("Si", +25, appliesTo = ModifierTarget.CarbsPerGram)
                )
            )
        )
    ),
)