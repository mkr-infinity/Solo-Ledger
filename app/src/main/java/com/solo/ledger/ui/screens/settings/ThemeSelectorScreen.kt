package com.solo.ledger.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solo.ledger.ui.theme.AppTheme
import com.solo.ledger.ui.theme.getColorScheme
import com.solo.ledger.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectorScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val currentThemeKey by viewModel.currentTheme.collectAsStateWithLifecycle()

    val darkThemes = AppTheme.entries.filter { it.isDark }
    val lightThemes = AppTheme.entries.filter { !it.isDark }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Theme",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Dark Themes category
            item {
                Text(
                    text = "Dark Themes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                ThemeHorizontalRow(
                    themes = darkThemes,
                    currentThemeKey = currentThemeKey,
                    onThemeSelected = { viewModel.setTheme(it) }
                )
            }

            // Light Themes category
            item {
                Text(
                    text = "Light Themes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                ThemeHorizontalRow(
                    themes = lightThemes,
                    currentThemeKey = currentThemeKey,
                    onThemeSelected = { viewModel.setTheme(it) }
                )
            }

            // Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Square themes use 0px border radius on all elements for a brutalist, sharp aesthetic.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeHorizontalRow(
    themes: List<AppTheme>,
    currentThemeKey: String,
    onThemeSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        themes.forEach { theme ->
            val themeKey = AppTheme.toKey(theme)
            val isSelected = themeKey == currentThemeKey
            val colorScheme = getColorScheme(theme)
            val previewRadius = if (theme.isSquare) 0.dp else 14.dp

            Column(
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onThemeSelected(themeKey) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large preview card
                Card(
                    modifier = Modifier
                        .width(140.dp)
                        .height(100.dp)
                        .then(
                            if (isSelected) Modifier.border(
                                2.5.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(previewRadius)
                            ) else Modifier
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.background
                    ),
                    shape = RoundedCornerShape(previewRadius)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Simulated top bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(previewRadius / 3))
                                    .background(colorScheme.primaryContainer)
                            )
                            Box(
                                modifier = Modifier
                                    .size(width = 40.dp, height = 6.dp)
                                    .clip(RoundedCornerShape(previewRadius / 3))
                                    .background(colorScheme.onBackground.copy(alpha = 0.6f))
                            )
                        }
                        // Simulated card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .clip(RoundedCornerShape(previewRadius / 2))
                                .background(colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(previewRadius / 4))
                                        .background(colorScheme.primary.copy(alpha = 0.5f))
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(colorScheme.onSurface.copy(alpha = 0.3f))
                                )
                            }
                        }
                        // Simulated nav
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(previewRadius / 2))
                                .background(colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { i ->
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            if (i == 0) colorScheme.primary
                                            else colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1
                )

                if (isSelected) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
