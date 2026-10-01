package com.example.data.repository

import com.example.data.db.ClothFinanceDatabase
import com.example.data.model.Expense
import com.example.data.model.FinancialSummary
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.Product
import com.example.data.model.SourceProfitability
import com.example.data.model.Supplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class FinanceRepository(private val db: ClothFinanceDatabase) {

    private val supplierDao = db.supplierDao()
    private val productDao = db.productDao()
    private val orderDao = db.orderDao()
    private val expenseDao = db.expenseDao()

    // Suppliers
    val allSuppliers: Flow<List<Supplier>> = supplierDao.getAllSuppliers()
    suspend fun insertSupplier(supplier: Supplier): Long = supplierDao.insertSupplier(supplier)
    suspend fun updateSupplier(supplier: Supplier) = supplierDao.updateSupplier(supplier)
    suspend fun deleteSupplier(supplier: Supplier) = supplierDao.deleteSupplier(supplier)

    // Products
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts()
    fun getProductsBySupplier(supplierId: Long): Flow<List<Product>> = productDao.getProductsBySupplier(supplierId)
    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    // Orders
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    val allOrderItems: Flow<List<OrderItem>> = orderDao.getAllOrderItems()

    suspend fun insertOrderWithItems(order: Order, items: List<OrderItem>): Long {
        val orderId = orderDao.insertFullOrder(order, items)
        // Deduct stock for each item if order is not cancelled or returned
        if (order.status != "CANCELLED" && order.status != "RETURNED") {
            for (item in items) {
                productDao.deductStock(item.productId, item.quantity)
            }
        }
        return orderId
    }

    suspend fun updateOrderStatusWithInventory(orderId: Long, newStatus: String, isCollected: Boolean) {
        val oldOrder = orderDao.getOrderById(orderId) ?: return
        val items = orderDao.getOrderItemsSync(orderId)
        val oldStatus = oldOrder.status

        orderDao.updateOrderStatus(orderId, newStatus, isCollected)

        // Handle inventory adjustment
        if (oldStatus != "RETURNED" && newStatus == "RETURNED") {
            // Returned to stock
            for (item in items) {
                productDao.returnStock(item.productId, item.quantity)
            }
            // Add return shipping loss expense automatically if actualShippingCost > 0
            if (oldOrder.actualShippingCost > 0) {
                expenseDao.insertExpense(
                    Expense(
                        title = "تكلفة شحن مرتجع الطلب ${oldOrder.orderNumber}",
                        category = "شحن مرتجعات",
                        amount = oldOrder.actualShippingCost,
                        notes = "تكلفة شحن تكبدها المتجر بسبب رجوع الشحنة للعميل ${oldOrder.customerName}"
                    )
                )
            }
        } else if (oldStatus == "RETURNED" && newStatus != "RETURNED" && newStatus != "CANCELLED") {
            // Deduct again if moved back out of returned
            for (item in items) {
                productDao.deductStock(item.productId, item.quantity)
            }
        }
    }

    suspend fun deleteOrder(order: Order) {
        orderDao.deleteOrderItemsByOrderId(order.id)
        orderDao.deleteOrder(order)
    }

    // Expenses
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    // Per-Source Financial Analytics Flow
    val sourceProfitabilityList: Flow<List<SourceProfitability>> = combine(
        allSuppliers,
        allProducts,
        allOrders,
        allOrderItems
    ) { suppliers, products, orders, orderItems ->
        val ordersMap = orders.associateBy { it.id }

        suppliers.map { supplier ->
            val supplierProducts = products.filter { it.supplierId == supplier.id }
            val supplierProductIds = supplierProducts.map { it.id }.toSet()

            var unitsSold = 0
            var revenue = 0.0
            var cogs = 0.0
            var returnCount = 0

            for (item in orderItems) {
                val order = ordersMap[item.orderId]
                val belongsToSupplier = item.supplierId == supplier.id || supplierProductIds.contains(item.productId)

                if (belongsToSupplier && order != null) {
                    when (order.status) {
                        "DELIVERED" -> {
                            unitsSold += item.quantity
                            revenue += (item.unitPrice * item.quantity)
                            cogs += (item.unitCost * item.quantity)
                        }
                        "SHIPPED" -> {
                            // In-transit revenue
                            unitsSold += item.quantity
                            revenue += (item.unitPrice * item.quantity)
                            cogs += (item.unitCost * item.quantity)
                        }
                        "RETURNED" -> {
                            returnCount += item.quantity
                        }
                    }
                }
            }

            val grossProfit = revenue - cogs
            val marginPercent = if (revenue > 0) (grossProfit / revenue) * 100.0 else 0.0
            val totalProcessed = unitsSold + returnCount
            val returnRate = if (totalProcessed > 0) (returnCount.toDouble() / totalProcessed) * 100.0 else 0.0

            val currentInventoryValue = supplierProducts.sumOf { it.stockQuantity * it.effectiveCostPerUnit }
            val totalStockUnits = supplierProducts.sumOf { it.stockQuantity }

            val statusTag = when {
                marginPercent >= 50.0 && returnRate <= 10.0 -> "مصدر ذهبي 🌟"
                returnRate >= 15.0 -> "تنبيه مرتجعات ⚠️"
                marginPercent >= 40.0 -> "عالي الربحية 💰"
                unitsSold > 20 -> "دوران سريع ⚡"
                else -> "أداء قياسي 📊"
            }

            val recommendation = when {
                returnRate >= 15.0 -> "يجب مراجعة دقة جدول المقاسات وجودة التقفيل مع المصنع فوراً لتقليل خسائر الشحن."
                marginPercent >= 50.0 -> "مصدر استثنائي عالي الهامش؛ ينصح بزيادة ميزانية الإعلانات لهذه التشكيلة والتوسع فيها."
                currentInventoryValue > 5000 && unitsSold < 5 -> "يوجد مخزون راكد نسبيًا؛ قم بعمل عروض ترويجية لتسييل رأس المال المجمد."
                else -> "معدلات ربحية واستقرار تجاري جيدة؛ استمر في إعادة الطلب حسب مستويات المخزون."
            }

            SourceProfitability(
                supplierId = supplier.id,
                supplierName = supplier.name,
                supplierType = supplier.type,
                totalUnitsSold = unitsSold,
                totalRevenue = revenue,
                totalCogs = cogs,
                grossProfit = grossProfit,
                profitMarginPercent = marginPercent,
                returnCount = returnCount,
                returnRatePercent = returnRate,
                currentInventoryValue = currentInventoryValue,
                totalStockUnits = totalStockUnits,
                recommendation = recommendation,
                statusTag = statusTag
            )
        }.sortedByDescending { it.grossProfit }
    }

    // Overall Financial Summary Flow
    val financialSummary: Flow<FinancialSummary> = combine(
        allOrders,
        allOrderItems,
        allExpenses,
        allProducts
    ) { orders, orderItems, expenses, products ->
        val deliveredOrders = orders.filter { it.status == "DELIVERED" }
        val shippedOrders = orders.filter { it.status == "SHIPPED" }
        val returnedOrders = orders.filter { it.status == "RETURNED" }

        val activeOrders = orders.filter { it.status == "DELIVERED" || it.status == "SHIPPED" }
        val activeOrderIds = activeOrders.map { it.id }.toSet()

        val activeItems = orderItems.filter { activeOrderIds.contains(it.orderId) }

        val totalRevenue = activeItems.sumOf { it.unitPrice * it.quantity }
        val totalCogs = activeItems.sumOf { it.unitCost * it.quantity }
        val grossProfit = totalRevenue - totalCogs
        val grossMarginPercent = if (totalRevenue > 0) (grossProfit / totalRevenue) * 100.0 else 0.0

        val totalExpenses = expenses.sumOf { it.amount }
        val adsExpenses = expenses.filter { it.category == "إعلانات ممولة" }.sumOf { it.amount }
        val shippingLosses = expenses.filter { it.category == "شحن مرتجعات" }.sumOf { it.amount }
        val packagingExpenses = expenses.filter { it.category == "أكياس وتغليف" }.sumOf { it.amount }
        val otherExpenses = totalExpenses - adsExpenses - shippingLosses - packagingExpenses

        val netProfit = grossProfit - totalExpenses
        val netMarginPercent = if (totalRevenue > 0) (netProfit / totalRevenue) * 100.0 else 0.0

        val inventoryAssetValue = products.sumOf { it.stockQuantity * it.effectiveCostPerUnit }

        // Cash pending with courier companies (Shipped or Delivered but not collected yet)
        val pendingCourierCash = orders
            .filter { (it.status == "DELIVERED" || it.status == "SHIPPED") && !it.isCollected }
            .sumOf { it.totalAmount }

        val totalOrdersCount = orders.size
        val returnedOrdersCount = returnedOrders.size
        val overallReturnRate = if (totalOrdersCount > 0) (returnedOrdersCount.toDouble() / totalOrdersCount) * 100.0 else 0.0

        val deliveredCount = deliveredOrders.size
        val averageOrderValue = if (deliveredCount > 0) deliveredOrders.sumOf { it.totalAmount } / deliveredCount else 0.0
        val roas = if (adsExpenses > 0) totalRevenue / adsExpenses else 0.0

        FinancialSummary(
            totalRevenue = totalRevenue,
            totalCogs = totalCogs,
            grossProfit = grossProfit,
            grossMarginPercent = grossMarginPercent,
            totalExpenses = totalExpenses,
            adsExpenses = adsExpenses,
            shippingLosses = shippingLosses,
            packagingExpenses = packagingExpenses,
            otherExpenses = otherExpenses,
            netProfit = netProfit,
            netMarginPercent = netMarginPercent,
            inventoryAssetValue = inventoryAssetValue,
            pendingCourierCash = pendingCourierCash,
            deliveredOrdersCount = deliveredOrders.size,
            shippedOrdersCount = shippedOrders.size,
            returnedOrdersCount = returnedOrdersCount,
            totalOrdersCount = totalOrdersCount,
            overallReturnRate = overallReturnRate,
            averageOrderValue = averageOrderValue,
            roas = roas
        )
    }
}
