package com.solo.ledger.ui.screens.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.solo.ledger.ui.theme.LedgerTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(nav: NavController, id: Long, vm: EditExpenseViewModel = viewModel()) {
    val c = LedgerTheme.colors
    val categories by vm.categories.collectAsState()
    val expense by vm.expense.collectAsState()
    LaunchedEffect(id) { vm.load(id) }

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var attachment by remember { mutableStateOf<String?>(null) }
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    LaunchedEffect(expense) {
        expense?.let {
            title = it.title; amount = it.amount.toString(); selected = it.category; notes = it.notes
            date = LocalDate.ofEpochDay(it.dateEpochDay); time = LocalTime.of(it.timeMinutes / 60, it.timeMinutes % 60)
            attachment = it.attachmentUri
        }
    }
    val attachPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) attachment = uri.toString() }

    Scaffold(
        containerColor = c.background,
        topBar = { TopAppBar(title = { Text("Edit expense") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Rounded.ArrowBack, "Back") } },
            actions = { IconButton(onClick = { vm.delete(id) { nav.popBackStack() } }) { Icon(Icons.Rounded.DeleteOutline, "Delete", tint = c.error) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.textPrimary, navigationIconContentColor = c.textPrimary)) }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(amount, { amount = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Amount") }, prefix = { Text("₹ ") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Text("Category", style = MaterialTheme.typography.labelLarge, color = c.muted)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    FilterChip(selected = cat.name == selected, onClick = { selected = cat.name }, label = { Text(cat.name) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.primary, selectedLabelColor = c.onPrimary))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PickerField(Modifier.weight(1f), Icons.Rounded.CalendarToday, "Date", date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))) { showDate = true }
                PickerField(Modifier.weight(1f), Icons.Rounded.Schedule, "Time", time.format(DateTimeFormatter.ofPattern("hh:mm a"))) { showTime = true }
            }
            OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Text("Attachment", style = MaterialTheme.typography.labelLarge, color = c.muted)
            if (attachment != null) {
                Box {
                    AsyncImage(model = attachment, contentDescription = "Attachment", contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(14.dp)))
                    IconButton(onClick = { attachment = null }, modifier = Modifier.align(Alignment.TopEnd)) { Icon(Icons.Rounded.Close, "Remove", tint = c.error) }
                }
            } else {
                OutlinedButton(onClick = { attachPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.AttachFile, null); Spacer(Modifier.width(8.dp)); Text("Attach receipt or bill")
                }
            }
            Button(onClick = {
                expense?.let { e -> vm.update(e.copy(title = title, amount = amount.toDoubleOrNull() ?: e.amount, category = selected, notes = notes,
                    dateEpochDay = date.toEpochDay(), timeMinutes = time.hour * 60 + time.minute, attachmentUri = attachment)) { nav.popBackStack() } }
            }, enabled = expense != null && (amount.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary), shape = RoundedCornerShape(16.dp)) {
                Text("Save changes", style = MaterialTheme.typography.labelLarge)
            }
        }
    }

    if (showDate) {
        val dpState = rememberDatePickerState(initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(onDismissRequest = { showDate = false },
            confirmButton = { TextButton(onClick = { dpState.selectedDateMillis?.let { date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }; showDate = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("Cancel") } }) { DatePicker(state = dpState) }
    }
    if (showTime) {
        val tpState = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute)
        AlertDialog(onDismissRequest = { showTime = false },
            confirmButton = { TextButton(onClick = { time = LocalTime.of(tpState.hour, tpState.minute); showTime = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showTime = false }) { Text("Cancel") } },
            text = { TimePicker(state = tpState) })
    }
}

@Composable
private fun PickerField(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, onClick: () -> Unit) {
    val c = LedgerTheme.colors
    Surface(color = c.surface, shape = RoundedCornerShape(14.dp), modifier = modifier.clickable { onClick() }) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = c.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = c.muted)
                Text(value, style = MaterialTheme.typography.bodyMedium, color = c.textPrimary)
            }
        }
    }
}
