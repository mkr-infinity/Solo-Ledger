package com.solo.ledger.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.solo.ledger.ui.AppViewModel
import com.solo.ledger.ui.components.LedgerCard
import com.solo.ledger.ui.theme.LedgerTheme

private val CURRENCIES = listOf("INR", "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "AED")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(nav: NavController, appVm: AppViewModel) {
    val c = LedgerTheme.colors
    val s by appVm.settings.collectAsState()
    var name by remember(s.userName) { mutableStateOf(s.userName) }
    var budget by remember(s.monthlyBudget) { mutableStateOf(if (s.monthlyBudget > 0) s.monthlyBudget.toString() else "") }
    var currencyMenu by remember { mutableStateOf(false) }

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) appVm.setAvatar(uri.toString())
    }

    Scaffold(
        containerColor = c.background,
        topBar = { TopAppBar(title = { Text("Profile") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Rounded.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.textPrimary, navigationIconContentColor = c.textPrimary)) }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(Modifier.size(110.dp).clip(CircleShape).background(c.primary.copy(alpha = 0.12f))
                    .clickable { avatarPicker.launch("image/*") }, contentAlignment = Alignment.Center) {
                    if (s.avatarUri.isNotBlank()) {
                        AsyncImage(model = s.avatarUri, contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Rounded.Person, null, tint = c.primary, modifier = Modifier.size(54.dp))
                    }
                }
                Box(Modifier.size(34.dp).clip(CircleShape).background(c.primary), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.PhotoCamera, "Change", tint = c.onPrimary, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
            LedgerCard(Modifier.fillMaxWidth()) {
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(budget, { budget = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Monthly budget") }, prefix = { Text(s.currency + " ") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(14.dp))
                ExposedDropdownMenuBox(expanded = currencyMenu, onExpandedChange = { currencyMenu = !currencyMenu }) {
                    OutlinedTextField(value = s.currency, onValueChange = {}, readOnly = true, label = { Text("Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(currencyMenu) },
                        modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = currencyMenu, onDismissRequest = { currencyMenu = false }) {
                        CURRENCIES.forEach { code ->
                            DropdownMenuItem(text = { Text(code) }, onClick = { appVm.setCurrency(code); currencyMenu = false })
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = {
                appVm.setName(name.ifBlank { "User" })
                appVm.setBudget(budget.toDoubleOrNull() ?: s.monthlyBudget)
                nav.popBackStack()
            }, modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary)) {
                Text("Save profile")
            }
        }
    }
}
