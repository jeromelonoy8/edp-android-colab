package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DishViewModel : ViewModel() {

    private val _dishes = MutableStateFlow(
        listOf(
            Dish(id = 1, name = "Chicken Adobo"),
            Dish(id = 2, name = "Sinigang na Baboy")
        )
    )
    val dishes: StateFlow<List<Dish>> = _dishes.asStateFlow()

    private var nextId = 100

    // DISH CRUD
    fun addDish(name: String) {
        if (name.isBlank()) return
        val newDish = Dish(id = nextId++, name = name.trim())
        _dishes.value = _dishes.value + newDish
    }

    fun getDish(dishId: Int): Dish? {
        return _dishes.value.find { it.id == dishId }
    }

    fun updateDish(dishId: Int, newName: String) {
        if (newName.isBlank()) return
        _dishes.value = _dishes.value.map { dish ->
            if (dish.id == dishId) dish.copy(name = newName.trim()) else dish
        }
    }

    fun deleteDish(dishId: Int) {
        _dishes.value = _dishes.value.filter { it.id != dishId }
    }

    // RECIPE CRUD
    fun addRecipe(dishId: Int, text: String) {
        if (text.isBlank()) return
        val newRecipe = Recipe(id = nextId++, text = text.trim())
        _dishes.value = _dishes.value.map { dish ->
            if (dish.id == dishId) {
                dish.copy(recipes = dish.recipes + newRecipe)
            } else {
                dish
            }
        }
    }

    fun updateRecipe(dishId: Int, recipeId: Int, newText: String) {
        if (newText.isBlank()) return
        _dishes.value = _dishes.value.map { dish ->
            if (dish.id == dishId) {
                val updatedRecipes = dish.recipes.map { recipe ->
                    if (recipe.id == recipeId) recipe.copy(text = newText.trim()) else recipe
                }
                dish.copy(recipes = updatedRecipes)
            } else {
                dish
            }
        }
    }

    fun deleteRecipe(dishId: Int, recipeId: Int) {
        _dishes.value = _dishes.value.map { dish ->
            if (dish.id == dishId) {
                val filteredRecipes = dish.recipes.filter { it.id != recipeId }
                dish.copy(recipes = filteredRecipes)
            } else {
                dish
            }
        }
    }
}