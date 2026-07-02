package com.solo.ledger.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solo.ledger.data.model.Category
import com.solo.ledger.ui.screens.home.getCategoryIcon
import com.solo.ledger.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Category")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(category.color).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(category.icon),
                                contentDescription = null,
                                tint = Color(category.color),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (!category.isDefault) {
                            IconButton(onClick = { editingCategory = category }) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.deleteCategory(category) }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddDialog) {
        CategoryDialog(
            title = "Add Category",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, icon, color ->
                viewModel.addCategory(
                    Category(
                        name = name,
                        icon = icon,
                        color = color
                    )
                )
                showAddDialog = false
            }
        )
    }

    // Edit Category Dialog
    if (editingCategory != null) {
        CategoryDialog(
            title = "Edit Category",
            initialName = editingCategory!!.name,
            initialIcon = editingCategory!!.icon,
            initialColor = editingCategory!!.color,
            onDismiss = { editingCategory = null },
            onConfirm = { name, icon, color ->
                viewModel.updateCategory(editingCategory!!.copy(name = name, icon = icon, color = color))
                editingCategory = null
            }
        )
    }
}

@Composable
private fun CategoryDialog(
    title: String,
    initialName: String = "",
    initialIcon: String = "category",
    initialColor: Long = 0xFF90A4AE,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var icon by remember { mutableStateOf(initialIcon) }
    var selectedColor by remember { mutableLongStateOf(initialColor) }

    val iconOptions = listOf(
        "restaurant", "directions_car", "shopping_bag", "receipt_long",
        "school", "movie", "local_grocery_store", "subscriptions",
        "home", "fitness_center", "flight", "pets", "more_horiz",
        "local_cafe", "local_hospital"
    )

    val colorOptions = listOf(
        0xFFE57373, 0xFF81C784, 0xFF64B5F6, 0xFFFFB74D,
        0xFF9575CD, 0xFF4DD0E1, 0xFFA5D6A7, 0xFFFF8A65,
        0xFF90A4AE, 0xFFF06292, 0xFFAED581, 0xFF7986CB
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colorOptions.take(6).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .then(
                                    if (selectedColor == color) Modifier.border(
                                        2.dp, MaterialTheme.colorScheme.onSurface, CircleShape
                                    ) else Modifier
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colorOptions.drop(6).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .then(
                                    if (selectedColor == color) Modifier.border(
                                        2.dp, MaterialTheme.colorScheme.onSurface, CircleShape
                                    ) else Modifier
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }

                Text(
                    text = "Icon",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    iconOptions.take(5).forEach { iconName ->
                        IconButton(
                            onClick = { icon = iconName },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .then(
                                    if (icon == iconName) Modifier.background(
                                        MaterialTheme.colorScheme.primaryContainer
                                    ) else Modifier
                                )
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(iconName),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), icon, selectedColor) },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
