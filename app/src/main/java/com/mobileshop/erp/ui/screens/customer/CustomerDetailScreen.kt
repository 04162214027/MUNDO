package com.mobileshop.erp.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobileshop.erp.data.entity.TransactionType
import com.mobileshop.erp.ui.theme.ProfitGreen
import com.mobileshop.erp.ui.theme.UdhaarRed
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    onNavigateBack: () -> Unit,
    viewModel: CustomerDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddUdhaarDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    
    val currencyFormatter = remember { 
        NumberFormat.getCurrencyInstance(Locale("en", "PK")).apply {
            maximumFractionDigits = 0
        }
    }
    
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(uiState.customer?.customerName ?: "Customer")
                        uiState.customer?.phoneNumber?.let { phone ->
                            if (phone.isNotEmpty()) {
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExtendedFloatingActionButton(
                    onClick = { showPaymentDialog = true },
                    containerColor = ProfitGreen,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Payment")
                }
                ExtendedFloatingActionButton(
                    onClick = { showAddUdhaarDialog = true },
                    containerColor = UdhaarRed,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Udhaar")
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            uiState.customer?.let { customer ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Balance Card
                    item {
                        val balance = customer.totalUdhaar - customer.totalPaid
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (balance > 0) UdhaarRed.copy(alpha = 0.1f)
                                               else ProfitGreen.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Current Balance",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currencyFormatter.format(balance),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (balance > 0) UdhaarRed else ProfitGreen
                                )
                                Text(
                                    text = if (balance > 0) "Amount Receivable" else "Cleared",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Summary Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = "Total Udhaar",
                                value = currencyFormatter.format(customer.totalUdhaar),
                                color = UdhaarRed
                            )
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = "Total Paid",
                                value = currencyFormatter.format(customer.totalPaid),
                                color = ProfitGreen
                            )
                        }
                    }

                    // Transaction History
                    item {
                        Text(
                            text = "Transaction History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (uiState.transactions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Receipt,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No transactions yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.transactions) { transaction ->
                            TransactionCard(
                                transaction = transaction,
                                currencyFormatter = currencyFormatter,
                                dateFormatter = dateFormatter
                            )
                        }
                    }

                    // Bottom spacing for FAB
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }

    // Add Udhaar Dialog
    if (showAddUdhaarDialog) {
        TransactionDialog(
            title = "Add Udhaar",
            buttonText = "Add Udhaar",
            buttonColor = UdhaarRed,
            onDismiss = { showAddUdhaarDialog = false },
            onConfirm = { amount, description ->
                viewModel.addUdhaar(amount, description)
                showAddUdhaarDialog = false
            }
        )
    }

    // Receive Payment Dialog
    if (showPaymentDialog) {
        TransactionDialog(
            title = "Receive Payment",
            buttonText = "Receive",
            buttonColor = ProfitGreen,
            onDismiss = { showPaymentDialog = false },
            onConfirm = { amount, description ->
                viewModel.receivePayment(amount, description)
                showPaymentDialog = false
            }
        )
    }
}

@Composable
private fun SummaryCard(
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun TransactionCard(
    transaction: com.mobileshop.erp.data.entity.KhataTransaction,
    currencyFormatter: NumberFormat,
    dateFormatter: SimpleDateFormat
) {
    val isPayment = transaction.type == TransactionType.PAYMENT_RECEIVED

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPayment) ProfitGreen.copy(alpha = 0.1f) 
                           else UdhaarRed.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = if (isPayment) Icons.Default.Add else Icons.Default.Remove,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = if (isPayment) ProfitGreen else UdhaarRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isPayment) "Payment Received" else "Udhaar Added",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (transaction.description.isNotEmpty()) {
                        Text(
                            text = transaction.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = dateFormatter.format(Date(transaction.createdAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "${if (isPayment) "+" else "-"}${currencyFormatter.format(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPayment) ProfitGreen else UdhaarRed
            )
        }
    }
}

@Composable
private fun TransactionDialog(
    title: String,
    buttonText: String,
    buttonColor: androidx.compose.ui.graphics.Color,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) {
                            amount = it
                        }
                    },
                    label = { Text("Amount (Rs.)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    amount.toDoubleOrNull()?.let { 
                        onConfirm(it, description) 
                    }
                },
                enabled = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text(buttonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
