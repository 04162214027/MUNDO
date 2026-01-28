package com.mobileshop.erp.ui.screens.main

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobileshop.erp.ui.screens.main.pages.DashboardPage
import com.mobileshop.erp.ui.screens.main.pages.InventoryPage
import com.mobileshop.erp.ui.screens.main.pages.KhataPage
import com.mobileshop.erp.ui.screens.main.pages.OldPhonesPage
import kotlinx.coroutines.launch

data class TabItem(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToCustomerDetail: (Long) -> Unit,
    onNavigateToAddProduct: (String) -> Unit,
    onNavigateToSellProduct: (Long) -> Unit,
    onNavigateToTransactionHistory: () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()
    var showAddCustomerDialog by remember { mutableStateOf(false) }

    val tabs = listOf(
        TabItem("Dashboard", Icons.Default.Dashboard),
        TabItem("Khata", Icons.Default.AccountBalance),
        TabItem("Inventory", Icons.Default.Inventory2),
        TabItem("Old Phones", Icons.Default.PhoneAndroid)
    )

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setCurrentPage(pagerState.currentPage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.shopName.ifEmpty { "Mobile Shop ERP" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tabs[pagerState.currentPage].title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = pagerState.currentPage == 1 || pagerState.currentPage == 2,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                when (pagerState.currentPage) {
                    1 -> { // Khata - Add Customer
                        ExtendedFloatingActionButton(
                            onClick = { showAddCustomerDialog = true },
                            icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                            text = { Text("Add Customer") },
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    }
                    2 -> { // Inventory - Add Stock
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            FloatingActionButton(
                                onClick = { expanded = true },
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Stock")
                            }
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Add Handset") },
                                    onClick = {
                                        expanded = false
                                        onNavigateToAddProduct("HANDSET")
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.PhoneAndroid, contentDescription = null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add Accessory") },
                                    onClick = {
                                        expanded = false
                                        onNavigateToAddProduct("ACCESSORY")
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Headphones, contentDescription = null)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scrollable Tab Row with Underline Indicator
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                divider = {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = tab.title,
                                    fontWeight = if (pagerState.currentPage == index) 
                                        FontWeight.SemiBold 
                                    else 
                                        FontWeight.Normal
                                )
                            }
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Horizontal Pager Content - Swipeable
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> DashboardPage(
                        stats = uiState.stats,
                        shopName = uiState.shopName,
                        onNavigateToTransactionHistory = onNavigateToTransactionHistory
                    )
                    1 -> KhataPage(
                        stats = uiState.stats,
                        customers = uiState.customers,
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onCustomerClick = onNavigateToCustomerDetail,
                        onDeleteCustomer = viewModel::deleteCustomer
                    )
                    2 -> InventoryPage(
                        handsets = uiState.handsets,
                        accessories = uiState.accessories,
                        onProductClick = onNavigateToSellProduct,
                        onDeleteProduct = viewModel::deleteProduct,
                        onAddHandset = { onNavigateToAddProduct("HANDSET") },
                        onAddAccessory = { onNavigateToAddProduct("ACCESSORY") }
                    )
                    3 -> OldPhonesPage(
                        soldPhones = uiState.soldPhones,
                        shopName = uiState.shopName,
                        onNavigateToTransactionHistory = onNavigateToTransactionHistory
                    )
                }
            }
        }
    }

    // Add Customer Dialog
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, phone ->
                viewModel.addCustomer(name, phone)
                showAddCustomerDialog = false
            }
        )
    }
}

@Composable
private fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, phone) },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
