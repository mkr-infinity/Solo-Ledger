package com.solo.ledger.data.repository

object CategoryDetector {

    private val categoryKeywords = mapOf(
        "Food" to listOf(
            "pizza", "burger", "food", "lunch", "dinner", "breakfast", "snack",
            "restaurant", "cafe", "coffee", "tea", "juice", "biryani", "noodles",
            "chicken", "paneer", "dosa", "idli", "samosa", "momos", "thali",
            "sweets", "ice cream", "cake", "bakery", "zomato", "swiggy", "dominos",
            "mcdonalds", "kfc", "subway", "starbucks", "chaiwala", "mess", "canteen",
            "maggi", "chips", "biscuit", "chocolate", "fruit", "milk", "bread"
        ),
        "Travel" to listOf(
            "uber", "ola", "cab", "taxi", "auto", "rickshaw", "bus", "train",
            "metro", "petrol", "diesel", "fuel", "gas", "parking", "toll",
            "flight", "airline", "airport", "rapido", "bike", "ride"
        ),
        "Shopping" to listOf(
            "amazon", "flipkart", "myntra", "ajio", "clothes", "shoes", "shirt",
            "jeans", "dress", "watch", "bag", "accessories", "mall", "market",
            "meesho", "nykaa", "cosmetics", "perfume", "gift"
        ),
        "Bills" to listOf(
            "electricity", "water", "gas bill", "rent", "emi", "loan", "insurance",
            "tax", "maintenance", "wifi", "broadband", "phone bill", "recharge",
            "jio", "airtel", "vi", "bsnl", "postpaid", "prepaid"
        ),
        "Education" to listOf(
            "book", "course", "udemy", "coursera", "tuition", "fees", "college",
            "school", "exam", "study", "notes", "stationery", "pen", "notebook",
            "library", "coaching", "class", "tutorial"
        ),
        "Entertainment" to listOf(
            "movie", "netflix", "prime", "hotstar", "spotify", "youtube", "gaming",
            "game", "concert", "show", "theatre", "cinema", "pvr", "inox",
            "party", "club", "pub", "bar", "hangout", "outing"
        ),
        "Groceries" to listOf(
            "grocery", "vegetables", "fruits", "rice", "dal", "oil", "spices",
            "sugar", "salt", "flour", "atta", "blinkit", "zepto", "bigbasket",
            "dmart", "reliance", "supermarket", "kirana", "soap", "shampoo",
            "detergent", "tissue", "toothpaste"
        ),
        "Subscription" to listOf(
            "subscription", "premium", "plan", "membership", "annual", "monthly",
            "renewal", "spotify", "netflix", "prime", "icloud", "storage",
            "google one", "notion", "figma", "canva"
        )
    )

    fun detectCategory(title: String, availableCategories: List<com.solo.ledger.data.model.Category>): com.solo.ledger.data.model.Category? {
        val lowerTitle = title.lowercase().trim()
        if (lowerTitle.isBlank()) return null

        for ((categoryName, keywords) in categoryKeywords) {
            for (keyword in keywords) {
                if (lowerTitle.contains(keyword)) {
                    return availableCategories.find { it.name.equals(categoryName, ignoreCase = true) }
                }
            }
        }
        return null
    }
}
