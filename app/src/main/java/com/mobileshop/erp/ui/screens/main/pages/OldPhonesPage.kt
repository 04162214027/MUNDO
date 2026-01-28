package com.mobileshop.erp.ui.screens.main.pages

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mobileshop.erp.data.entity.Sale
import com.mobileshop.erp.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OldPhonesPage(
    soldPhones: List<Sale>,
    shopName: String,
    onNavigateToTransactionHistory: () -> Unit
) {
    val context = LocalContext.current
    val currencyFormatter = remember { 
        NumberFormat.getCurrencyInstance(Locale("en", "PK")).apply {
            maximumFractionDigits = 0
        }
    }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search/Filter Button
        Card(
            onClick = onNavigateToTransactionHistory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Advanced Search",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Search by IMEI or Date Range",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val totalSold = soldPhones.size
            val totalRevenue = soldPhones.sumOf { it.totalAmount }
            val totalProfit = soldPhones.sumOf { it.profit }

            StatMiniCard(
                modifier = Modifier.weight(1f),
                title = "Sold",
                value = "$totalSold",
                color = MaterialTheme.colorScheme.primary
            )
            StatMiniCard(
                modifier = Modifier.weight(1f),
                title = "Revenue",
                value = currencyFormatter.format(totalRevenue),
                color = CashBlue
            )
            StatMiniCard(
                modifier = Modifier.weight(1f),
                title = "Profit",
                value = currencyFormatter.format(totalProfit),
                color = ProfitGreen
            )
        }

        // Sold Phones List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (soldPhones.isEmpty()) {
                item {
                    EmptyOldPhonesState()
                }
            } else {
                items(soldPhones, key = { it.id }) { sale ->
                    SoldPhoneCard(
                        sale = sale,
                        shopName = shopName,
                        currencyFormatter = currencyFormatter,
                        dateFormatter = dateFormatter,
                        context = context
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun StatMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoldPhoneCard(
    sale: Sale,
    shopName: String,
    currencyFormatter: NumberFormat,
    dateFormatter: SimpleDateFormat,
    context: Context
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sale.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dateFormatter.format(Date(sale.soldAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Share Button
                IconButton(
                    onClick = {
                        shareReceipt(
                            context = context,
                            shopName = shopName,
                            sale = sale,
                            dateFormatter = dateFormatter,
                            currencyFormatter = currencyFormatter
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share Receipt",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Sold Price",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currencyFormatter.format(sale.sellingPrice),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Profit",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currencyFormatter.format(sale.profit),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = ProfitGreen
                    )
                }
            }

            // Payment Status
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (sale.isPaid) ProfitGreen.copy(alpha = 0.1f) else UdhaarRed.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (sale.isPaid) Icons.Default.CheckCircle else Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (sale.isPaid) ProfitGreen else UdhaarRed
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (sale.isPaid) "Paid" else "Udhaar",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (sale.isPaid) ProfitGreen else UdhaarRed
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyOldPhonesState() {
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
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.PhoneAndroid,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Sold Phones Yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sold phones will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

private fun shareReceipt(
    context: Context,
    shopName: String,
    sale: Sale,
    dateFormatter: SimpleDateFormat,
    currencyFormatter: NumberFormat
) {
    val receiptText = buildString {
        appendLine("═══════════════════════")
        appendLine("       SALES RECEIPT")
        appendLine("═══════════════════════")
        appendLine()
        appendLine("Shop: ${shopName.ifEmpty { "Mobile Shop" }}")
        appendLine("Date: ${dateFormatter.format(Date(sale.soldAt))}")
        appendLine()
        appendLine("───────────────────────")
        appendLine("Item: ${sale.productName}")
        appendLine("Qty: ${sale.quantity}")
        appendLine("Price: ${currencyFormatter.format(sale.sellingPrice)}")
        appendLine("───────────────────────")
        appendLine("Total: ${currencyFormatter.format(sale.totalAmount)}")
        appendLine("Status: ${if (sale.isPaid) "PAID" else "UDHAAR"}")
        appendLine("───────────────────────")
        appendLine()
        appendLine("Thank you for your purchase!")
        appendLine("═══════════════════════")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, receiptText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Receipt"))
}
