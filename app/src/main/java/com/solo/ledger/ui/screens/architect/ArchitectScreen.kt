package com.solo.ledger.ui.screens.architect

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solo.ledger.ui.components.shared.ThemedCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchitectScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Avatar ────────────────────────────────────────────────────────
            AvatarBox()

            Spacer(Modifier.height(16.dp))

            // ── "SOLO LEDGER PRO" chip ────────────────────────────────────────
            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = "SOLO LEDGER PRO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                border = null
            )

            Spacer(Modifier.height(12.dp))

            // ── Name ─────────────────────────────────────────────────────────
            Text(
                text = "Mohammad Kaif Raja",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(12.dp))

            // ── Quote ─────────────────────────────────────────────────────────
            Text(
                text = "\"Crafting digital legacies with architectural precision and code.\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(32.dp))

            // ── Link cards grid (2 per row) ───────────────────────────────────
            val links = listOf(
                LinkItem(
                    category = "PORTFOLIO",
                    title = "Website",
                    icon = Icons.Outlined.Language,
                    url = "https://mkr-infinity.github.io/"
                ),
                LinkItem(
                    category = "VISUALS",
                    title = "Instagram",
                    icon = Icons.Outlined.CameraAlt,
                    url = "https://instagram.com/mkr_infinity"
                ),
                LinkItem(
                    category = "CHAT",
                    title = "Telegram",
                    icon = Icons.Outlined.Send,
                    url = "https://t.me/mkr_infinity"
                ),
                LinkItem(
                    category = "SOURCE",
                    title = "GitHub",
                    icon = Icons.Outlined.Code,
                    url = "https://github.com/mkr-infinity"
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                links.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { link ->
                            LinkCard(
                                item = link,
                                modifier = Modifier.weight(1f),
                                onOpen = {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                                    )
                                },
                                onCopy = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("link", link.url))
                                    Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        // If odd number of items in last row, fill the remaining slot
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Footer ────────────────────────────────────────────────────────
            Text(
                text = "Built by an independent developer",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AvatarBox() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Diagonal gradient background
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )

            // Draw "MKR" text via native canvas
            drawIntoCanvas { canvas ->
                val paint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = onPrimaryColor.toArgb()
                    textSize = size.width * 0.30f
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT_BOLD,
                        android.graphics.Typeface.BOLD
                    )
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val textY = size.height / 2f - (paint.descent() + paint.ascent()) / 2f
                canvas.nativeCanvas.drawText("MKR", size.width / 2f, textY, paint)
            }
        }
    }
}

private data class LinkItem(
    val category: String,
    val title: String,
    val icon: ImageVector,
    val url: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LinkCard(
    item: LinkItem,
    modifier: Modifier = Modifier,
    onOpen: () -> Unit,
    onCopy: () -> Unit
) {
    ThemedCard(
        modifier = modifier.combinedClickable(
            onClick = onOpen,
            onLongClick = onCopy
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon circle
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Text column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Icon(
                imageVector = Icons.Outlined.OpenInNew,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
