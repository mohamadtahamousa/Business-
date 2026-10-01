package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // مصنع ملابس, مكتب جملة, مستورد
    val phone: String = "",
    val city: String = "",
    val paymentTerms: String = "كاش", // كاش, آجل 15 يوم, آجل 30 يوم
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supplierId: Long,
    val supplierName: String,
    val name: String,
    val category: String, // رجالي, حريمي, أطفال, رياضي, شتوي, صيفي
    val sku: String,
    val purchaseCost: Double, // سعر الشراء من المصنع للقطعة
    val landedCostExtra: Double = 0.0, // تكلفة إضافية (مكواة، نقل، تغليف خاص)
    val sellingPrice: Double, // سعر بيع القطعة للعميل
    val stockQuantity: Int, // الكمية المتوفرة
    val minStockAlert: Int = 5,
    val sizes: String = "M, L, XL",
    val colors: String = "أسود, أبيض",
    val totalUnitsSold: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    val effectiveCostPerUnit: Double get() = purchaseCost + landedCostExtra
    val profitPerUnit: Double get() = sellingPrice - effectiveCostPerUnit
    val marginPercentage: Double get() = if (sellingPrice > 0) (profitPerUnit / sellingPrice) * 100 else 0.0
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val customerCity: String,
    val customerAddress: String = "",
    val shippingCompany: String = "شركة الشحن السريع",
    val shippingFeeToCustomer: Double = 50.0, // رسوم الشحن المحصلة من العميل
    val actualShippingCost: Double = 50.0, // تكلفة الشحن الفعلية لشركة الشحن
    val status: String = "DELIVERED", // DELIVERED, SHIPPED, PROCESSING, RETURNED, CANCELLED
    val isCollected: Boolean = true, // هل تم تحصيل الأموال وتوريدها للتاجر؟
    val totalAmount: Double = 0.0,
    val totalCogs: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val supplierId: Long,
    val supplierName: String,
    val quantity: Int,
    val unitCost: Double,
    val unitPrice: Double,
    val selectedSize: String = "",
    val selectedColor: String = ""
) {
    val totalRevenue: Double get() = unitPrice * quantity
    val totalCost: Double get() = unitCost * quantity
    val itemProfit: Double get() = totalRevenue - totalCost
}

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // إعلانات ممولة, أكياس وتغليف, شحن مرتجعات, إيجار وتخزين, رواتب وعمولات, برمجيات وأدوات, أخرى
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

data class SourceProfitability(
    val supplierId: Long,
    val supplierName: String,
    val supplierType: String,
    val totalUnitsSold: Int,
    val totalRevenue: Double,
    val totalCogs: Double,
    val grossProfit: Double,
    val profitMarginPercent: Double,
    val returnCount: Int,
    val returnRatePercent: Double,
    val currentInventoryValue: Double,
    val totalStockUnits: Int,
    val recommendation: String,
    val statusTag: String
)

data class FinancialSummary(
    val totalRevenue: Double = 0.0,
    val totalCogs: Double = 0.0,
    val grossProfit: Double = 0.0,
    val grossMarginPercent: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val adsExpenses: Double = 0.0,
    val shippingLosses: Double = 0.0,
    val packagingExpenses: Double = 0.0,
    val otherExpenses: Double = 0.0,
    val netProfit: Double = 0.0,
    val netMarginPercent: Double = 0.0,
    val inventoryAssetValue: Double = 0.0,
    val pendingCourierCash: Double = 0.0,
    val deliveredOrdersCount: Int = 0,
    val shippedOrdersCount: Int = 0,
    val returnedOrdersCount: Int = 0,
    val totalOrdersCount: Int = 0,
    val overallReturnRate: Double = 0.0,
    val averageOrderValue: Double = 0.0,
    val roas: Double = 0.0
)
