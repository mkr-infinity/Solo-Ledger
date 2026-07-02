package com.solo.ledger.ui.screens.expense

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Expense
import com.solo.ledger.ui.screens.home.getCategoryIcon
import com.solo.ledger.ui.viewmodel.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var notes by remember { mutableStateOf("") }
    var attachmentPath by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedTime by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val dir = File(context.filesDir, "attachments")
                dir.mkdirs()
                val file = File(dir, "img_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                attachmentPath = file.absolutePath
            } catch (_: Exception) {}
        }
    }

    // Set default category
    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) {
            selectedCategory = categories.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Expense",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Field (prominent)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { value ->
                            if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
                                amount = value
                            }
                        },
                        prefix = {
                            Text(
                                currencySymbol,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { newTitle ->
                    title = newTitle
                    // Auto-detect category from title
                    val detected = com.solo.ledger.data.repository.CategoryDetector.detectCategory(newTitle, categories)
                    if (detected != null) {
                        selectedCategory = detected
                    }
                },
                label = { Text("Title (auto-detects category)") },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Category
            ExposedDropdownMenuBox(
                expanded = showCategoryDropdown,
                onExpandedChange = { showCategoryDropdown = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    leadingIcon = {
                        selectedCategory?.let {
                            Icon(
                                imageVector = getCategoryIcon(it.icon),
                                contentDescription = null,
                                tint = Color(it.color)
                            )
                        }
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            leadingIcon = {
                                Icon(
                                    imageVector = getCategoryIcon(category.icon),
                                    contentDescription = null,
                                    tint = Color(category.color)
                                )
                            },
                            onClick = {
                                selectedCategory = category
                                showCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            // Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date picker
                OutlinedTextField(
                    value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = selectedDate
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(year, month, day, 0, 0, 0)
                                    newCal.set(Calendar.MILLISECOND, 0)
                                    selectedDate = newCal.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                )

                // Time picker
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") },
                    leadingIcon = { Icon(Icons.Filled.AccessTime, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val parts = selectedTime.split(":")
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    selectedTime = String.format("%02d:%02d", hour, minute)
                                },
                                parts[0].toIntOrNull() ?: 12,
                                parts[1].toIntOrNull() ?: 0,
                                true
                            ).show()
                        }
                )
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                leadingIcon = { Icon(Icons.Filled.Notes, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // Image attachment
            if (attachmentPath != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = File(attachmentPath!!),
                            contentDescription = "Attachment",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { attachmentPath = null },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove", tint = Color.White)
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (attachmentPath != null) "Change Image" else "Attach Receipt")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save button
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    if (amountValue > 0 && selectedCategory != null) {
                        val finalTitle = title.trim().ifBlank { selectedCategory!!.name }
                        viewModel.addExpense(
                            Expense(
                                title = finalTitle,
                                amount = amountValue,
                                categoryId = selectedCategory!!.id,
                                date = selectedDate,
                                time = selectedTime,
                                notes = notes.trim(),
                                attachmentPath = attachmentPath
                            )
                        )
                        onNavigateBack()
                    }
                },
                enabled = amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    "Save Expense",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
