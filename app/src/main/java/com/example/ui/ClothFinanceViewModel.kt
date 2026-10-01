package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ClothFinanceDatabase
import com.example.data.model.Expense
import com.example.data.model.FinancialSummary
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.Product
import com.example.data.model.SourceProfitability
import com.example.data.model.Supplier
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClothFinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository

    init {
        val database = ClothFinanceDatabase.getDatabase(application, viewModelScope)
        repository = FinanceRepository(database)
    }

    val suppliers: StateFlow<List<Supplier>> = repository.allSuppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<Product>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sourceProfitability: StateFlow<List<SourceProfitability>> = repository.sourceProfitabilityList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialSummary: StateFlow<FinancialSummary> = repository.financialSummary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    // Selected navigation tab
    // 0: Dashboard, 1: Sources, 2: Orders, 3: Inventory, 4: Expenses
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    // Suppliers actions
    fun addSupplier(name: String, type: String, phone: String, city: String, paymentTerms: String, notes: String) {
        viewModelScope.launch {
            repository.insertSupplier(
                Supplier(
                    name = name.trim(),
                    type = type.trim(),
                    phone = phone.trim(),
                    city = city.trim(),
                    paymentTerms = paymentTerms.trim(),
                    notes = notes.trim()
                )
            )
        }
    }

    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
        }
    }

    // Products actions
    fun addProduct(
        supplier: Supplier,
        name: String,
        category: String,
        sku: String,
        purchaseCost: Double,
        landedCostExtra: Double,
        sellingPrice: Double,
        stockQuantity: Int,
        minAlert: Int,
        sizes: String,
        colors: String
    ) {
        viewModelScope.launch {
            repository.insertProduct(
                Product(
                    supplierId = supplier.id,
                    supplierName = supplier.name,
                    name = name.trim(),
                    category = category.trim(),
                    sku = sku.ifBlank { "SKU-${System.currentTimeMillis().toString().takeLast(5)}" },
                    purchaseCost = purchaseCost,
                    landedCostExtra = landedCostExtra,
                    sellingPrice = sellingPrice,
                    stockQuantity = stockQuantity,
                    minStockAlert = minAlert,
                    sizes = sizes.ifBlank { "M, L, XL" },
                    colors = colors.ifBlank { "متعدد" }
                )
            )
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // Orders actions
    fun createOrder(
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        shippingCompany: String,
        shippingFeeToCustomer: Double,
        actualShippingCost: Double,
        selectedProduct: Product,
        quantity: Int,
        selectedSize: String,
        selectedColor: String,
        unitSellingPrice: Double,
        status: String,
        isCollected: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            val orderNumber = "ORD-${(1000..9999).random()}"
            val totalAmount = unitSellingPrice * quantity
            val totalCogs = selectedProduct.effectiveCostPerUnit * quantity

            val order = Order(
                orderNumber = orderNumber,
                customerName = customerName.trim(),
                customerPhone = customerPhone.trim(),
                customerCity = customerCity.trim(),
                customerAddress = customerAddress.trim(),
                shippingCompany = shippingCompany.trim(),
                shippingFeeToCustomer = shippingFeeToCustomer,
                actualShippingCost = actualShippingCost,
                status = status,
                isCollected = isCollected,
                totalAmount = totalAmount,
                totalCogs = totalCogs,
                notes = notes.trim()
            )

            val orderItem = OrderItem(
                orderId = 0, // Assigned inside insertFullOrder
                productId = selectedProduct.id,
                productName = selectedProduct.name,
                supplierId = selectedProduct.supplierId,
                supplierName = selectedProduct.supplierName,
                quantity = quantity,
                unitCost = selectedProduct.effectiveCostPerUnit,
                unitPrice = unitSellingPrice,
                selectedSize = selectedSize,
                selectedColor = selectedColor
            )

            repository.insertOrderWithItems(order, listOf(orderItem))
        }
    }

    fun updateOrderStatus(orderId: Long, newStatus: String, isCollected: Boolean) {
        viewModelScope.launch {
            repository.updateOrderStatusWithInventory(orderId, newStatus, isCollected)
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            repository.deleteOrder(order)
        }
    }

    // Expenses actions
    fun addExpense(title: String, category: String, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.insertExpense(
                Expense(
                    title = title.trim(),
                    category = category,
                    amount = amount,
                    notes = notes.trim()
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}
