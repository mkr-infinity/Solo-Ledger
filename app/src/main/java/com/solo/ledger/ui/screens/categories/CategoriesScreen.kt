package com.solo.ledger.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.ui.components.CategoryIcons
import com.solo.ledger.ui.components.LedgerCard
import com.solo.ledger.ui.theme.LedgerTheme
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val PALETTE = listOf(
    0xFF16A34A, 0xFF0D9488, 0xFFCA8A04, 0xFFDC2626, 0xFF7C3AED,
    0xFFDB2777, 0xFF65A30D, 0xFFEA580C, 0xFF64748B, 0xFFE11D48
)

class CategoriesViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    val categories: StateFlow<List<CategoryEntity>> =
        repo.categories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun save(c: CategoryEntity) = viewModelScope.launch { repo.upsertCategory(c) }
    fun delete(c: CategoryEntity) = viewModelScope.launch { repo.deleteCategory(c) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(nav: NavController, vm: CategoriesViewModel = viewModel()) {
    val c = LedgerTheme.colors
    val cats by vm.categories.collectAsState()
    var editing by remember { mutableStateOf<CategoryEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = c.background,
        topBar = { TopAppBar(title = { Text("Categories") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Rounded.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.textPrimary, navigationIconContentColor = c.textPrimary)) },
        floatingActionButton = { FloatingActionButton(onClick = { editing = null; showEditor = true }, containerColor = c.primary, contentColor = c.onPrimary) { Icon(Icons.Rounded.Add, "Add category") } }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cats, key = { it.name }) { cat ->
                LedgerCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(CircleShape).background(Color(cat.colorArgb).copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
                            Icon(CategoryIcons.forKey(cat.iconKey), null, tint = Color(cat.colorArgb))
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(cat.name, style = MaterialTheme.typography.titleMedium, color = c.textPrimary, modifier = Modifier.weight(1f))
                        IconButton(onClick = { editing = cat; showEditor = true }) { Icon(Icons.Rounded.Edit, "Edit", tint = c.muted) }
                        if (!cat.isDefault) IconButton(onClick = { vm.delete(cat) }) { Icon(Icons.Rounded.Delete, "Delete", tint = c.error) }
                    }
                }
            }
        }
    }

    if (showEditor) CategoryEditor(editing, onDismiss = { showEditor = false }) { cat -> vm.save(cat); showEditor = false }
}

@Composable
private fun CategoryEditor(existing: CategoryEntity?, onDismiss: () -> Unit, onSave: (CategoryEntity) -> Unit) {
    val c = LedgerTheme.colors
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var iconKey by remember { mutableStateOf(existing?.iconKey ?: "category") }
    var color by remember { mutableStateOf(existing?.colorArgb ?: PALETTE.first()) }

    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onSave(CategoryEntity(name.trim(), iconKey, color, existing?.isDefault ?: false)) }, enabled = name.isNotBlank()) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text(if (existing == null) "New category" else "Edit category") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true,
                    enabled = existing == null, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(14.dp))
                Text("Icon", style = MaterialTheme.typography.labelLarge, color = c.muted)
                Spacer(Modifier.height(6.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.height(140.dp)) {
                    items(CategoryIcons.keys) { key ->
                        val sel = key == iconKey
                        Box(Modifier.padding(4.dp).size(40.dp).clip(CircleShape)
                            .background(if (sel) Color(color).copy(alpha = 0.18f) else c.surface)
                            .clickable { iconKey = key }, contentAlignment = Alignment.Center) {
                            Icon(CategoryIcons.forKey(key), null, tint = if (sel) Color(color) else c.muted, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("Color", style = MaterialTheme.typography.labelLarge, color = c.muted)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    PALETTE.forEach { col ->
                        val sel = col == color
                        Box(Modifier.padding(4.dp).size(30.dp).clip(CircleShape).background(Color(col))
                            .border(if (sel) 3.dp else 0.dp, c.textPrimary, CircleShape).clickable { color = col })
                    }
                }
            }
        })
}
