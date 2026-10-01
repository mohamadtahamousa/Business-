package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ClothFinanceViewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PromptGuideDialog
import com.example.ui.screens.SourceAnalyticsScreen
import com.example.ui.theme.ClothFinanceTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

class MainActivity : ComponentActivity() {

    private val viewModel: ClothFinanceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClothFinanceTheme {
                val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
                val summary by viewModel.financialSummary.collectAsStateWithLifecycle()
                val sources by viewModel.sourceProfitability.collectAsStateWithLifecycle()
                val orders by viewModel.orders.collectAsStateWithLifecycle()
                val products by viewModel.products.collectAsStateWithLifecycle()
                val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
                val expenses by viewModel.expenses.collectAsStateWithLifecycle()

                var showPromptGuideDialog by remember { mutableStateOf(false) }

                val navItems = listOf(
                    NavigationItem("الرئيسية", Icons.Default.Dashboard, "dashboard_tab"),
                    NavigationItem("أرباح المصانع", Icons.Default.Factory, "sources_tab"),
                    NavigationItem("الطلبات", Icons.Default.ReceiptLong, "orders_tab"),
                    NavigationItem("المخزون", Icons.Default.Inventory2, "inventory_tab"),
                    NavigationItem("المصروفات", Icons.Default.AccountBalanceWallet, "expenses_tab")
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = when (selectedTab) {
                                        0 -> "خبير مالية الملابس"
                                        1 -> "أرباح المصانع ومكاتب الجملة"
                                        2 -> "المبيعات وإدارة الشحن"
                                        3 -> "المخزون وتسعير القطع"
                                        4 -> "المصروفات والإعلانات"
                                        else -> "خبير مالية الملابس"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = { showPromptGuideDialog = true },
                                    modifier = Modifier.testTag("topbar_prompt_guide_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "دليل الخبير والبرومبت",
                                        tint = Color(0xFFFBBF24)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = NavyDark,
                                titleContentColor = Color.White,
                                actionIconContentColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            navItems.forEachIndexed { index, item ->
                                val isSelected = selectedTab == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.selectTab(index) },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyPrimary,
                                        selectedTextColor = NavyPrimary,
                                        indicatorColor = NavyPrimary.copy(alpha = 0.12f),
                                        unselectedIconColor = Color(0xFF64748B),
                                        unselectedTextColor = Color(0xFF64748B)
                                    ),
                                    modifier = Modifier.testTag(item.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> DashboardScreen(
                                summary = summary,
                                sources = sources,
                                recentOrders = orders,
                                onOpenPromptGuide = { showPromptGuideDialog = true },
                                onNavigateToSources = { viewModel.selectTab(1) },
                                onNavigateToOrders = { viewModel.selectTab(2) },
                                onNavigateToExpenses = { viewModel.selectTab(4) }
                            )
                            1 -> SourceAnalyticsScreen(
                                sources = sources,
                                suppliers = suppliers,
                                onAddSupplier = { name, type, phone, city, paymentTerms, notes ->
                                    viewModel.addSupplier(name, type, phone, city, paymentTerms, notes)
                                }
                            )
                            2 -> OrdersScreen(
                                orders = orders,
                                products = products,
                                onCreateOrder = { name, phone, city, address, shipCo, shipCust, shipActual, prod, qty, sz, clr, price, status, collected, notes ->
                                    viewModel.createOrder(name, phone, city, address, shipCo, shipCust, shipActual, prod, qty, sz, clr, price, status, collected, notes)
                                },
                                onUpdateOrderStatus = { orderId, newStatus, isCollected ->
                                    viewModel.updateOrderStatus(orderId, newStatus, isCollected)
                                }
                            )
                            3 -> InventoryScreen(
                                products = products,
                                suppliers = suppliers,
                                onAddProduct = { supplier, name, cat, sku, cost, landed, price, qty, alert, sz, clr ->
                                    viewModel.addProduct(supplier, name, cat, sku, cost, landed, price, qty, alert, sz, clr)
                                },
                                onUpdateProduct = { updatedProduct ->
                                    viewModel.updateProduct(updatedProduct)
                                }
                            )
                            4 -> ExpensesScreen(
                                expenses = expenses,
                                onAddExpense = { title, cat, amt, notes ->
                                    viewModel.addExpense(title, cat, amt, notes)
                                },
                                onDeleteExpense = { expense ->
                                    viewModel.deleteExpense(expense)
                                }
                            )
                        }
                    }

                    if (showPromptGuideDialog) {
                        PromptGuideDialog(
                            onDismiss = { showPromptGuideDialog = false }
                        )
                    }
                }
            }
        }
    }
}

private data class NavigationItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
