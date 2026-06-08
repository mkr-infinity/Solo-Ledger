package com.solo.ledger.ui.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solo.ledger.ui.components.shared.AppLogo
import com.solo.ledger.ui.components.shared.ThemedCard

private data class CurrencyOption(
    val symbol: String,
    val name: String,
    val label: String
)

private val builtInCurrencies = listOf(
    CurrencyOption("₹", "Indian Rupee", "₹ INR"),
    CurrencyOption("$", "US Dollar", "$ USD"),
    CurrencyOption("€", "Euro", "€ EUR"),
    CurrencyOption("£", "British Pound", "£ GBP"),
    CurrencyOption("¥", "Japanese Yen", "¥ JPY")
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "onboarding_page"
        ) { page ->
            when (page) {
                0 -> WelcomePage(
                    onGetStarted = { currentPage = 1 }
                )
                1 -> SetupPage(
                    userName = state.userName,
                    currencySymbol = state.currencySymbol,
                    currencyName = state.currencyName,
                    monthlyBudget = state.monthlyBudget,
                    onUserNameChange = viewModel::setUserName,
                    onMonthlyBudgetChange = viewModel::setMonthlyBudget,
                    onCurrencyChange = viewModel::setCurrency,
                    onContinue = { currentPage = 2 },
                    onSkip = { currentPage = 2 }
                )
                2 -> PermissionsPage(
                    onStoragePermissionDeferred = viewModel::setStoragePermissionDeferred,
                    onComplete = {
                        viewModel.completeOnboarding()
                        onComplete()
                    }
                )
            }
        }

        // Page indicator dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val isActive = index == currentPage
                val dotWidth by animateDpAsState(
                    targetValue = if (isActive) 24.dp else 8.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "dotWidth_$index"
                )
                Box(
                    modifier = Modifier
                        .width(dotWidth)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                        )
                )
            }
        }
    }
}

// ── Page 1: Welcome ───────────────────────────────────────────────────────────

@Composable
private fun WelcomePage(onGetStarted: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(primaryColor, secondaryColor)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLogo(
                size = 80.dp,
                modifier = Modifier.size(80.dp),
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Solo Ledger",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Your personal finance, beautifully tracked.",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // Fake balance card mockup
            FakeBalanceCard()

            Spacer(Modifier.height(32.dp))

            // Feature chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf("Fully Offline", "8 Themes", "PDF Export")
                items(chips) { label ->
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = primaryColor
                )
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FakeBalanceCard() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 8.dp)
    ) {
        val w = size.width
        val h = size.height
        val radius = 20.dp.toPx()

        // Card background
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.10f)
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            ),
            cornerRadius = CornerRadius(radius)
        )

        // Card border
        drawRoundRect(
            color = Color.White.copy(alpha = 0.3f),
            cornerRadius = CornerRadius(radius),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Decorative circle top-right
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = h * 0.7f,
            center = Offset(w * 0.85f, -h * 0.2f)
        )

        // "Balance" label line
        val labelPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.argb(180, 255, 255, 255)
            textSize = 11.sp.toPx()
        }
        drawContext.canvas.nativeCanvas.drawText("Total Balance", 24.dp.toPx(), 36.dp.toPx(), labelPaint)

        // Amount
        val amountPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            textSize = 28.sp.toPx()
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        drawContext.canvas.nativeCanvas.drawText("₹ 24,500.00", 24.dp.toPx(), 70.dp.toPx(), amountPaint)

        // Income / Expense row
        val subPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.argb(200, 255, 255, 255)
            textSize = 10.sp.toPx()
        }
        drawContext.canvas.nativeCanvas.drawText("Income  +₹32,000", 24.dp.toPx(), 100.dp.toPx(), subPaint)
        drawContext.canvas.nativeCanvas.drawText("Expense  -₹7,500", 24.dp.toPx(), 118.dp.toPx(), subPaint)
    }
}

// ── Page 2: Setup ─────────────────────────────────────────────────────────────

@Composable
private fun SetupPage(
    userName: String,
    currencySymbol: String,
    currencyName: String,
    monthlyBudget: Double,
    onUserNameChange: (String) -> Unit,
    onMonthlyBudgetChange: (Double) -> Unit,
    onCurrencyChange: (String, String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    var budgetText by remember { mutableStateOf(if (monthlyBudget > 0.0) monthlyBudget.toString() else "") }
    var showCustomCurrency by remember { mutableStateOf(false) }
    var customSymbol by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Skip button top-right
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
        ) {
            Text("Skip")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 72.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Set up your workspace",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Name field
            OutlinedTextField(
                value = userName,
                onValueChange = onUserNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your name") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Budget field
            OutlinedTextField(
                value = budgetText,
                onValueChange = { v ->
                    if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        budgetText = v
                        onMonthlyBudgetChange(v.toDoubleOrNull() ?: 0.0)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Monthly budget") },
                prefix = { Text(currencySymbol) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Currency picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Currency",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(builtInCurrencies) { option ->
                        val isSelected = currencySymbol == option.symbol && !showCustomCurrency
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable {
                                    showCustomCurrency = false
                                    onCurrencyChange(option.symbol, option.name)
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (showCustomCurrency)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { showCustomCurrency = !showCustomCurrency }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Custom...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (showCustomCurrency) FontWeight.Bold else FontWeight.Normal,
                                color = if (showCustomCurrency)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (showCustomCurrency) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customSymbol,
                            onValueChange = { customSymbol = it },
                            modifier = Modifier.width(80.dp),
                            label = { Text("Symbol") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Currency name") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    if (customSymbol.isNotBlank() && customName.isNotBlank()) {
                        TextButton(onClick = {
                            onCurrencyChange(customSymbol.trim(), customName.trim())
                        }) {
                            Text("Use ${customSymbol.trim()} ${customName.trim()}")
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Continue", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Page 3: Permissions ───────────────────────────────────────────────────────

@Composable
private fun PermissionsPage(
    onStoragePermissionDeferred: (Boolean) -> Unit,
    onComplete: () -> Unit
) {
    var storageGranted by remember { mutableStateOf(false) }

    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        storageGranted = granted
        if (!granted) {
            onStoragePermissionDeferred(true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 72.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "A few quick things",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Grant these permissions to unlock all features. You can always change them later in Settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )

        // Storage permission card
        ThemedCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (storageGranted)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (storageGranted) Icons.Outlined.Check else Icons.Outlined.FolderOpen,
                        contentDescription = null,
                        tint = if (storageGranted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "File Access",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Required for PDF export and JSON backup",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (!storageGranted) {
                    OutlinedButton(
                        onClick = { storagePermissionLauncher.launch(storagePermission) }
                    ) {
                        Text("Grant")
                    }
                } else {
                    Text(
                        text = "Granted",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Notifications placeholder card (disabled)
        Box {
            ThemedCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.45f)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Budget alerts and reminders",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    OutlinedButton(onClick = {}, enabled = false) {
                        Text("Grant")
                    }
                }
            }
            // "Coming Soon" badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("All done, let's go!", fontWeight = FontWeight.Bold)
        }

        TextButton(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Skip for now")
        }
    }
}
