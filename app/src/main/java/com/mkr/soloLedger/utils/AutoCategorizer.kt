package com.mkr.soloLedger.utils

object AutoCategorizer {

    private val foodKeywords = setOf(
        "pizza", "burger", "restaurant", "cafe", "coffee", "lunch", "dinner",
        "breakfast", "food", "eat", "drink", "sushi", "taco", "snack", "bakery",
        "diner", "grill", "bbq", "sandwich", "salad", "soup", "noodle", "rice",
        "biryani", "curry", "kfc", "mcdonalds", "starbucks", "dominos", "swiggy", "zomato"
    )

    private val travelKeywords = setOf(
        "uber", "ola", "taxi", "bus", "train", "flight", "petrol", "fuel",
        "transport", "metro", "cab", "lyft", "rapido", "auto", "rickshaw",
        "ticket", "travel", "trip", "journey", "airfare", "railway", "subway"
    )

    private val shoppingKeywords = setOf(
        "amazon", "flipkart", "mall", "shop", "clothes", "buy", "purchase",
        "meesho", "myntra", "ajio", "fashion", "store", "market", "bazaar",
        "apparel", "shirt", "shoes", "dress", "jeans", "accessories", "jewellery"
    )

    private val entertainmentKeywords = setOf(
        "movie", "netflix", "spotify", "game", "cinema", "theatre", "concert",
        "show", "event", "park", "ride", "hotstar", "prime", "youtube",
        "gaming", "arcade", "bowling", "fun", "outing", "party"
    )

    private val billsKeywords = setOf(
        "electricity", "water", "internet", "phone", "bill", "rent", "wifi",
        "broadband", "gas", "maintenance", "recharge", "postpaid", "prepaid",
        "insurance", "emi", "loan", "mortgage", "tax"
    )

    private val educationKeywords = setOf(
        "school", "college", "university", "course", "book", "tuition",
        "coaching", "exam", "fee", "education", "class", "training",
        "workshop", "seminar", "certification", "udemy", "coursera"
    )

    private val groceriesKeywords = setOf(
        "vegetables", "fruits", "grocery", "milk", "bread", "eggs", "butter",
        "cheese", "flour", "sugar", "rice", "dal", "oil", "spices", "supermarket",
        "bigbasket", "jiomart", "dmart", "zepto", "blinkit", "instamart"
    )

    private val subscriptionsKeywords = setOf(
        "subscription", "monthly", "annual", "plan", "premium",
        "membership", "adobe", "microsoft", "apple", "google",
        "icloud", "dropbox", "linkedin", "zoom", "slack"
    )

    fun categorize(title: String): String {
        val lower = title.lowercase().trim()
        return when {
            foodKeywords.any { lower.contains(it) } -> "Food"
            travelKeywords.any { lower.contains(it) } -> "Travel"
            shoppingKeywords.any { lower.contains(it) } -> "Shopping"
            entertainmentKeywords.any { lower.contains(it) } -> "Entertainment"
            billsKeywords.any { lower.contains(it) } -> "Bills"
            educationKeywords.any { lower.contains(it) } -> "Education"
            groceriesKeywords.any { lower.contains(it) } -> "Groceries"
            subscriptionsKeywords.any { lower.contains(it) } -> "Subscriptions"
            else -> "Other"
        }
    }
}
