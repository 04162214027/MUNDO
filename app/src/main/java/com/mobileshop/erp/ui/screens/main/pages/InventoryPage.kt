package com.mobileshop.erp.ui.screens.main.pages

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mobileshop.erp.data.entity.Product
import com.mobileshop.erp.data.entity.ProductType
import com.mobileshop.erp.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryPage(
    handsets: List<Product>,
    accessories: List<Product>,
    onProductClick: (Long) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    onAddHandset: () -> Unit,
    onAddAccessory: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Handsets", "Accessories")
    
    val currencyFormatter = remember { 
        NumberFormat.getCurrencyInstance(Locale("en", "PK")).apply {
            maximumFractionDigits = 0
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(horizontal = 16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.PhoneAndroid 
                                             else Icons.Default.Headphones,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(title)
                            Badge(
                                containerColor = if (selectedTab == index) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (index == 0) "${handsets.size}" else "${accessories.size}",
                                    color = if (selectedTab == index) 
                                        MaterialTheme.colorScheme.onPrimary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )
            }
        }

        // Content
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn() + slideInHorizontally { if (targetState > initialState) it else -it } togetherWith
                fadeOut() + slideOutHorizontally { if (targetState > initialState) -it else it }
            },
            label = "tab_content"
        ) { tab ->
            when (tab) {
                0 -> ProductList(
                    products = handsets,
                    currencyFormatter = currencyFormatter,
                    onProductClick = onProductClick,
                    onDeleteProduct = onDeleteProduct,
                    emptyIcon = Icons.Default.PhoneAndroid,
                    emptyMessage = "No Handsets in Stock",
                    emptySubtitle = "Add handsets with IMEI tracking",
                    onAddClick = onAddHandset
                )
                1 -> ProductList(
                    products = accessories,
                    currencyFormatter = currencyFormatter,
                    onProductClick = onProductClick,
                    onDeleteProduct = onDeleteProduct,
                    emptyIcon = Icons.Default.Headphones,
                    emptyMessage = "No Accessories in Stock",
                    emptySubtitle = "Add accessories with quantity tracking",
                    onAddClick = onAddAccessory
                )
            }
        }
    }
}

@Composable
private fun ProductList(
    products: List<Product>,
    currencyFormatter: NumberFormat,
    onProductClick: (Long) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    emptyIcon: ImageVector,
    emptyMessage: String,
    emptySubtitle: String,
    onAddClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (products.isEmpty()) {
            item {
                EmptyInventoryState(
                    icon = emptyIcon,
                    message = emptyMessage,
                    subtitle = emptySubtitle,
                    onAddClick = onAddClick
                )
            }
        } else {
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    currencyFormatter = currencyFormatter,
                    onClick = { onProductClick(product.id) },
                    onDelete = { onDeleteProduct(product) }
                )
            }
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductCard(
    product: Product,
    currencyFormatter: NumberFormat,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val potentialProfit = product.sellingPrice - product.purchasePrice

    Card(
        onClick = onClick,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (product.type == ProductType.HANDSET) 
                            Blue40.copy(alpha = 0.1f) 
                        else 
                            Teal40.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = if (product.type == ProductType.HANDSET) 
                                Icons.Default.PhoneAndroid 
                            else 
                                Icons.Default.Headphones,
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp),
                            tint = if (product.type == ProductType.HANDSET) Blue40 else Teal40
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (product.imeiNumber != null) {
                            Text(
                                text = "IMEI: ${product.imeiNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (product.type == ProductType.ACCESSORY) {
                            Text(
                                text = "Qty: ${product.quantity}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider()
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriceInfo(
                    label = "Purchase",
                    value = currencyFormatter.format(product.purchasePrice),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PriceInfo(
                    label = "Selling",
                    value = currencyFormatter.format(product.sellingPrice),
                    color = MaterialTheme.colorScheme.primary
                )
                PriceInfo(
                    label = "Profit",
                    value = currencyFormatter.format(potentialProfit),
                    color = ProfitGreen
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Product?") },
            text = { Text("Are you sure you want to delete ${product.name}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PriceInfo(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun EmptyInventoryState(
    icon: ImageVector,
    message: String,
    subtitle: String,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(24.dp)
                    .size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Now")
        }
    }
}
