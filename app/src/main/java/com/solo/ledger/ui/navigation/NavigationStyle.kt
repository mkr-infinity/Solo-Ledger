package com.solo.ledger.ui.navigation

enum class NavigationStyle(val displayName: String, val key: String) {
    CAPSULE("Capsule", "capsule"),
    FLOATING("Floating Bar", "floating"),
    MINIMAL_FLAT("Minimal Flat", "minimal_flat"),
    ELEVATED("Elevated", "elevated"),
    ROUNDED_PILL("Rounded Pill", "rounded_pill"),
    COMPACT("Compact", "compact"),
    MATERIAL_STANDARD("Material Standard", "material_standard");

    companion object {
        fun fromKey(key: String): NavigationStyle =
            entries.find { it.key == key } ?: CAPSULE
    }
}
