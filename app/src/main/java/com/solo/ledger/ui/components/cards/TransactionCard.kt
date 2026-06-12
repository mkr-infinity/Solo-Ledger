package com.solo.ledger.ui.components.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import com.solo.ledger.ui.components.shared.ThemedCard
import com.solo.ledger.util.CurrencyFormatter
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TransactionCard(
    transaction: Transaction,
    category: Category?,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "txnCardScale"
    )

    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor  = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
    val amountPrefix = if (isIncome) "+ " else "- "
    val tintColor    = if (isIncome) Color(0xFF4CAF50).copy(alpha = 0.04f)
                       else Color(0xFFF44336).copy(alpha = 0.04f)

    val tags = transaction.tags
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

    val dateFmt = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH) }
    val timeFmt = remember { DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH) }

    ThemedCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(tintColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryIconCircle(category = category)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.title?.ifBlank { null } ?: category?.name ?: "Transaction",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = transaction.date.format(dateFmt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        )
                        Text(
                            text = transaction.time.format(timeFmt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    DayOfWeekChip(dayName = transaction.dayOfWeek)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$amountPrefix${CurrencyFormatter.format(transaction.amount, currencySymbol)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = amountColor
                    )
                    if (tags.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            tags.take(2).forEach { tag -> TagChip(tag = tag) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryIconCircle(
    category: Category?,
    size: Int = 44
) {
    val bgColor = if (category != null) {
        runCatching {
            Color(android.graphics.Color.parseColor(category.colorHex))
        }.getOrElse { Color(0xFF9E9E9E) }
    } else {
        Color(0xFF9E9E9E)
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (category != null) {
            val icon = iconForName(category.iconName)
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = category.name,
                    tint = Color.White,
                    modifier = Modifier.size((size * 0.52f).dp)
                )
            } else {
                Text(
                    text = category.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = (size * 0.42f).sp
                    ),
                    color = Color.White
                )
            }
        } else {
            Icon(
                imageVector = Icons.Filled.Category,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((size * 0.52f).dp)
            )
        }
    }
}

fun iconForName(name: String): ImageVector? = when (name.lowercase()) {
    "food", "fastfood", "restaurant", "dining"    -> Icons.Filled.Fastfood
    "transport", "bus", "commute", "travel"        -> Icons.Filled.DirectionsBus
    "shopping", "cart", "shop"                     -> Icons.Filled.ShoppingCart
    "home", "house", "rent", "housing"             -> Icons.Filled.Home
    "health", "hospital", "medical", "medicine"    -> Icons.Filled.LocalHospital
    "entertainment", "movie", "movies", "cinema"   -> Icons.Filled.Movie
    "fitness", "gym", "sport", "exercise"          -> Icons.Filled.FitnessCenter
    "education", "school", "study", "book"         -> Icons.Filled.School
    "salary", "work", "income", "job"              -> Icons.Filled.Work
    "gift", "gifts"                                -> Icons.Filled.CardGiftcard
    "savings", "wallet", "bank"                    -> Icons.Filled.AccountBalanceWallet
    "investment", "invest", "stocks", "finance"    -> Icons.Filled.BarChart
    "money", "cash"                                -> Icons.Filled.AttachMoney
    "other", "misc", "miscellaneous"               -> Icons.Filled.Star
    else                                           -> null
}

@Composable
private fun DayOfWeekChip(dayName: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
            .padding(horizontal = 6.dp, vertical = 1.dp)
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun TagChip(tag: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "#$tag",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}
