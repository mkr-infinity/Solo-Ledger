package com.solo.ledger.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

/** Maps a stored category iconKey to a Material vector. Used by category chips, rows and the editor. */
object CategoryIcons {
    val keys = listOf(
        "restaurant", "flight", "shopping_bag", "receipt", "school", "movie",
        "local_grocery", "subscriptions", "category", "savings", "home", "pets",
        "fitness", "coffee", "fuel", "gift", "phone", "health"
    )

    fun forKey(key: String): ImageVector = when (key) {
        "restaurant" -> Icons.Rounded.Restaurant
        "flight" -> Icons.Rounded.Flight
        "shopping_bag" -> Icons.Rounded.ShoppingBag
        "receipt" -> Icons.Rounded.ReceiptLong
        "school" -> Icons.Rounded.School
        "movie" -> Icons.Rounded.Movie
        "local_grocery" -> Icons.Rounded.LocalGroceryStore
        "subscriptions" -> Icons.Rounded.Subscriptions
        "savings" -> Icons.Rounded.Savings
        "home" -> Icons.Rounded.Home
        "pets" -> Icons.Rounded.Pets
        "fitness" -> Icons.Rounded.FitnessCenter
        "coffee" -> Icons.Rounded.Coffee
        "fuel" -> Icons.Rounded.LocalGasStation
        "gift" -> Icons.Rounded.CardGiftcard
        "phone" -> Icons.Rounded.Smartphone
        "health" -> Icons.Rounded.Favorite
        else -> Icons.Rounded.Category
    }
}
