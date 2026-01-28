package com.mobileshop.erp.ui.screens.customer

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobileshop.erp.data.entity.KhataTransaction
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
    val context = LocalContext.current
    
    var showAddUdhaarDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }
    
    val currencyFormatter = remember { 
        NumberFormat.getCurrencyInstance(Locale("en", "PK")).apply {
            maximumFractionDigits = 0
        }
    }
    
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val shortDateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    // Filter transactions based on date range
    val filteredTransactions = remember(uiState.transactions, uiState.fromDate, uiState.toDate) {
        uiState.transactions.filter { transaction ->
            val matchesFrom = uiState.fromDate?.let { transaction.createdAt >= it } ?: true
            val matchesTo = uiState.toDate?.let { transaction.createdAt <= it } ?: true
            matchesFrom && matchesTo
        }
    }

    // Calculate filtered totals
    val filteredTotalDebit = filteredTransactions
        .filter { it.type == TransactionType.UDHAAR_GIVEN }
        .sumOf { it.amount }
    val filteredTotalCredit = filteredTransactions
        .filter { it.type == TransactionType.PAYMENT_RECEIVED }
        .sumOf { it.amount }
    val filteredBalance = filteredTotalDebit - filteredTotalCredit

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
                },
                actions = {
                    // Clear filters button
                    if (uiState.fromDate != null || uiState.toDate != null) {
                        IconButton(onClick = { viewModel.clearDateFilters() }) {
                            Icon(Icons.Default.FilterAltOff, contentDescription = "Clear Filters")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Share Report FAB
                SmallFloatingActionButton(
                    onClick = {
                        shareKhataReport(
                            context = context,
                            customerName = uiState.customer?.customerName ?: "Customer",
                            transactions = filteredTransactions,
                            totalDebit = filteredTotalDebit,
                            totalCredit = filteredTotalCredit,
                            balance = filteredBalance,
                            currencyFormatter = currencyFormatter,
                            dateFormatter = dateFormatter,
                            fromDate = uiState.fromDate?.let { shortDateFormatter.format(Date(it)) },
                            toDate = uiState.toDate?.let { shortDateFormatter.format(Date(it)) }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share Report")
                }
                
                // Main Action Buttons
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
                    // Date Filter Buttons
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Filter by Date",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showFromDatePicker = true },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.DateRange, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.fromDate?.let { shortDateFormatter.format(Date(it)) } 
                                                ?: "Start Date"
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { showToDatePicker = true },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.DateRange, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.toDate?.let { shortDateFormatter.format(Date(it)) } 
                                                ?: "End Date"
                                        )
                                    }
                                }
                                
                                // Filter active indicator
                                if (uiState.fromDate != null || uiState.toDate != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Showing ${filteredTransactions.size} transactions",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            TextButton(
                                                onClick = { viewModel.clearDateFilters() }
                                            ) {
                                                Text("Clear")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

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

                    // Summary Row (Filtered if dates are set)
                    item {
                        val showFiltered = uiState.fromDate != null || uiState.toDate != null
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = if (showFiltered) "Filtered Udhaar" else "Total Udhaar",
                                value = currencyFormatter.format(if (showFiltered) filteredTotalDebit else customer.totalUdhaar),
                                color = UdhaarRed
                            )
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = if (showFiltered) "Filtered Paid" else "Total Paid",
                                value = currencyFormatter.format(if (showFiltered) filteredTotalCredit else customer.totalPaid),
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

                    if (filteredTransactions.isEmpty()) {
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
                                        text = if (uiState.fromDate != null || uiState.toDate != null)
                                            "No transactions in selected date range"
                                        else 
                                            "No transactions yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredTransactions) { transaction ->
                            TransactionCard(
                                transaction = transaction,
                                currencyFormatter = currencyFormatter,
                                dateFormatter = dateFormatter
                            )
                        }
                    }

                    // Bottom spacing for FAB
                    item {
                        Spacer(modifier = Modifier.height(140.dp))
                    }
                }
            }
        }
    }

    // From Date Picker
    if (showFromDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.fromDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showFromDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.setFromDate(it)
                        }
                        showFromDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFromDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // To Date Picker
    if (showToDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.toDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showToDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.setToDate(it + (24 * 60 * 60 * 1000) - 1)
                        }
                        showToDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showToDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
    transaction: KhataTransaction,
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

private fun shareKhataReport(
    context: Context,
    customerName: String,
    transactions: List<KhataTransaction>,
    totalDebit: Double,
    totalCredit: Double,
    balance: Double,
    currencyFormatter: NumberFormat,
    dateFormatter: SimpleDateFormat,
    fromDate: String?,
    toDate: String?
) {
    val reportText = buildString {
        appendLine("═══════════════════════════")
        appendLine("      KHATA LEDGER REPORT")
        appendLine("═══════════════════════════")
        appendLine()
        appendLine("Customer: $customerName")
        if (fromDate != null || toDate != null) {
            appendLine("Period: ${fromDate ?: "Beginning"} to ${toDate ?: "Now"}")
        }
        appendLine()
        appendLine("───────────────────────────")
        appendLine("SUMMARY")
        appendLine("───────────────────────────")
        appendLine("Total Udhaar: ${currencyFormatter.format(totalDebit)}")
        appendLine("Total Paid: ${currencyFormatter.format(totalCredit)}")
        appendLine("Balance: ${currencyFormatter.format(balance)}")
        appendLine("Status: ${if (balance > 0) "RECEIVABLE" else "CLEARED"}")
        appendLine()
        appendLine("───────────────────────────")
        appendLine("TRANSACTIONS (${transactions.size})")
        appendLine("───────────────────────────")
        
        transactions.forEach { transaction ->
            val isPayment = transaction.type == TransactionType.PAYMENT_RECEIVED
            val sign = if (isPayment) "+" else "-"
            val type = if (isPayment) "Payment" else "Udhaar"
            appendLine("$type: $sign${currencyFormatter.format(transaction.amount)}")
            if (transaction.description.isNotEmpty()) {
                appendLine("  Note: ${transaction.description}")
            }
            appendLine("  Date: ${dateFormatter.format(Date(transaction.createdAt))}")
            appendLine()
        }
        
        appendLine("═══════════════════════════")
        appendLine("Generated by Mobile Shop ERP")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, reportText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Khata Report"))
}
