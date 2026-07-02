package com.solo.ledger.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.solo.ledger.ui.viewmodel.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val monthlyBudget by viewModel.monthlyBudget.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val avatarPath by viewModel.avatarPath.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var editName by remember(userName) { mutableStateOf(userName) }
    var editBudget by remember(monthlyBudget) {
        mutableStateOf(
            if (monthlyBudget == monthlyBudget.toLong().toDouble()) monthlyBudget.toLong().toString()
            else monthlyBudget.toString()
        )
    }
    var editCurrencyCode by remember(currencyCode) { mutableStateOf(currencyCode) }
    var editCurrencySymbol by remember(currencySymbol) { mutableStateOf(currencySymbol) }
    var hasChanges by remember { mutableStateOf(false) }
    var localAvatarUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Copy to app's internal storage
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val avatarDir = File(context.filesDir, "avatar")
                avatarDir.mkdirs()
                val avatarFile = File(avatarDir, "profile.jpg")
                inputStream?.use { input ->
                    avatarFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                localAvatarUri = Uri.fromFile(avatarFile)
                viewModel.updateAvatarPath(avatarFile.absolutePath)
                hasChanges = true
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
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
            // Avatar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        val displayUri = localAvatarUri ?: if (avatarPath.isNotBlank()) Uri.fromFile(File(avatarPath)) else null

                        if (displayUri != null) {
                            AsyncImage(
                                model = displayUri,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = userName.firstOrNull()?.uppercase() ?: "U",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Photo")
                    }

                    Text(
                        text = userName.ifBlank { "User" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Name
            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it; hasChanges = true },
                label = { Text("Name") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Budget
            OutlinedTextField(
                value = editBudget,
                onValueChange = { value ->
                    if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
                        editBudget = value; hasChanges = true
                    }
                },
                label = { Text("Monthly Budget") },
                leadingIcon = {
                    Text(editCurrencySymbol, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = 12.dp))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Currency
            Text(
                text = "Currency",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = editCurrencySymbol,
                    onValueChange = { editCurrencySymbol = it; hasChanges = true },
                    label = { Text("Symbol") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = editCurrencyCode,
                    onValueChange = { editCurrencyCode = it.uppercase(); hasChanges = true },
                    label = { Text("Code") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            // Quick select
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("\u20B9" to "INR", "$" to "USD", "\u20AC" to "EUR", "\u00A3" to "GBP").forEach { (sym, code) ->
                    FilterChip(
                        selected = editCurrencyCode == code,
                        onClick = { editCurrencyCode = code; editCurrencySymbol = sym; hasChanges = true },
                        label = { Text("$sym $code") },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save
            Button(
                onClick = {
                    viewModel.updateUserName(editName.trim())
                    viewModel.updateMonthlyBudget(editBudget.toDoubleOrNull() ?: monthlyBudget)
                    viewModel.updateCurrency(editCurrencyCode, editCurrencySymbol)
                    hasChanges = false
                    onNavigateBack()
                },
                enabled = hasChanges,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
