package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RoseLoss
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    orders: List<Order>,
    products: List<Product>,
    onCreateOrder: (
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
    ) -> Unit,
    onUpdateOrderStatus: (orderId: Long, newStatus: String, isCollected: Boolean) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showCreateOrderDialog by remember { mutableStateOf(false) }
    var orderToUpdateStatus by remember { mutableStateOf<Order?>(null) }

    val filteredOrders = when (selectedFilter) {
        "DELIVERED" -> orders.filter { it.status == "DELIVERED" }
        "SHIPPED" -> orders.filter { it.status == "SHIPPED" }
        "PROCESSING" -> orders.filter { it.status == "PROCESSING" }
        "RETURNED" -> orders.filter { it.status == "RETURNED" }
        else -> orders
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateOrderDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("create_order_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "طلب جديد")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "طلب بيع جديد", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("orders_screen")
        ) {
            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "الكل (${orders.size})",
                    "DELIVERED" to "تم التسليم (${orders.count { it.status == "DELIVERED" }})",
                    "SHIPPED" to "قيد الشحن (${orders.count { it.status == "SHIPPED" }})",
                    "PROCESSING" to "تجهيز (${orders.count { it.status == "PROCESSING" }})",
                    "RETURNED" to "مرتجع (${orders.count { it.status == "RETURNED" }})"
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) NavyPrimary else Color(0xFFE2E8F0),
                        modifier = Modifier.clickable { selectedFilter = key }
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ReceiptLong,
                    title = "لا توجد طلبات في هذا التصنيف",
                    description = "سجل طلبات البيع لحساب أرباح كل قطعة وتتبع التحصيل مع شركات الشحن.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        OrderCard(
                            order = order,
                            onChangeStatusClick = { orderToUpdateStatus = order }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    if (showCreateOrderDialog) {
        CreateOrderDialog(
            products = products,
            onDismiss = { showCreateOrderDialog = false },
            onCreate = { custName, phone, city, address, shippingCo, shipFee, actualShip, prod, qty, sz, clr, price, status, collected, notes ->
                onCreateOrder(custName, phone, city, address, shippingCo, shipFee, actualShip, prod, qty, sz, clr, price, status, collected, notes)
                showCreateOrderDialog = false
            }
        )
    }

    orderToUpdateStatus?.let { order ->
        UpdateStatusDialog(
            order = order,
            onDismiss = { orderToUpdateStatus = null },
            onUpdate = { newStatus, isCollected ->
                onUpdateOrderStatus(order.id, newStatus, isCollected)
                orderToUpdateStatus = null
            }
        )
    }
}

@Composable
fun OrderCard(
    order: Order,
    onChangeStatusClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(order.createdAt))
    val netProfit = order.totalAmount - order.totalCogs - (order.actualShippingCost - order.shippingFeeToCustomer)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = order.status)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (order.isCollected) EmeraldLight else AmberLight
                ) {
                    Text(
                        text = if (order.isCollected) "تم توريد الأموال ✓" else "معلق لدى الشحن ⏳",
                        color = if (order.isCollected) EmeraldProfit else AmberWarning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Customer & Delivery Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SlateTextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${order.customerName} • ${order.customerPhone}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = SlateTextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${order.customerCity} • شركة: ${order.shippingCompany}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Breakdown
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "إجمالي البيع: ${String.format(Locale.US, "%,.0f", order.totalAmount)} ج.م", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                        Text(text = "تكلفة البضاعة: ${String.format(Locale.US, "%,.0f", order.totalCogs)} ج.م", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (order.status == "RETURNED") "خسارة شحن مرتجع" else "ربح الطلب",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextSecondary
                        )
                        Text(
                            text = if (order.status == "RETURNED") "-${String.format(Locale.US, "%,.0f", order.actualShippingCost)} ج.م" else "+${String.format(Locale.US, "%,.0f", netProfit)} ج.م",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (order.status == "RETURNED") RoseLoss else EmeraldProfit
                        )
                    }
                }
            }

            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ملاحظات: ${order.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Change Status / Update Collection
            OutlinedButton(
                onClick = onChangeStatusClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "تعديل حالة الشحن والتحصيل", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UpdateStatusDialog(
    order: Order,
    onDismiss: () -> Unit,
    onUpdate: (newStatus: String, isCollected: Boolean) -> Unit
) {
    var status by remember { mutableStateOf(order.status) }
    var isCollected by remember { mutableStateOf(order.isCollected) }

    val statusOptions = listOf(
        "DELIVERED" to "تم التسليم للعميل ✓",
        "SHIPPED" to "قيد الشحن مع المندوب 🚚",
        "PROCESSING" to "قيد التجهيز في المخزن ⏳",
        "RETURNED" to "مرتجع (إرجاع للمخزن وتسجيل تكلفة الشحن) ↩",
        "CANCELLED" to "ملغي ✕"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "تحديث حالة الطلب ${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateBorder)

                Text(text = "اختر الحالة الجديدة:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                statusOptions.forEach { (key, label) ->
                    val isSelected = status == key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) NavyPrimary.copy(alpha = 0.1f) else Color(0xFFF8FAFC),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                status = key
                                if (key == "DELIVERED") isCollected = true
                                if (key == "RETURNED") isCollected = false
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NavyPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Collected Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "تم توريد الأموال لحسابك البنكي", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "قم بالتفعيل إذا سلمتك شركة الشحن المبلغ", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                    }
                    Switch(checked = isCollected, onCheckedChange = { isCollected = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onUpdate(status, isCollected) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text(text = "تطبيق التحديث وإعادة حساب الأرباح", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderDialog(
    products: List<Product>,
    onDismiss: () -> Unit,
    onCreate: (
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
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerCity by remember { mutableStateOf("القاهرة") }
    var customerAddress by remember { mutableStateOf("") }
    var shippingCompany by remember { mutableStateOf("بوسطة Bosta") }
    var shippingFeeToCustomerStr by remember { mutableStateOf("50") }
    var actualShippingCostStr by remember { mutableStateOf("45") }

    var selectedProductIndex by remember { mutableStateOf(0) }
    var quantityStr by remember { mutableStateOf("1") }
    var selectedSize by remember { mutableStateOf("L") }
    var selectedColor by remember { mutableStateOf("أسود") }
    var unitPriceStr by remember { mutableStateOf(products.firstOrNull()?.sellingPrice?.toInt()?.toString() ?: "300") }
    var status by remember { mutableStateOf("DELIVERED") }
    var isCollected by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    val activeProduct = products.getOrNull(selectedProductIndex)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "إنشاء طلب بيع جديد", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = SlateBorder)
                }

                item {
                    Text(text = "بيانات العميل والشحن", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = NavyPrimary)
                }

                item {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("اسم العميل *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("رقم الهاتف *") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = customerCity,
                            onValueChange = { customerCity = it },
                            label = { Text("المحافظة") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("العنوان بالتفصيل") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = shippingCompany,
                            onValueChange = { shippingCompany = it },
                            label = { Text("شركة الشحن") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = shippingFeeToCustomerStr,
                            onValueChange = { shippingFeeToCustomerStr = it },
                            label = { Text("شحن العميل") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                item {
                    Text(text = "تفاصيل منتج الملابس والمصدر", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = NavyPrimary)
                }

                if (products.isNotEmpty()) {
                    item {
                        Text(text = "اختر الصنف:", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(products.indices.toList()) { index ->
                                val p = products[index]
                                val isSelected = index == selectedProductIndex
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) NavyPrimary else Color(0xFFF1F5F9),
                                    modifier = Modifier.clickable {
                                        selectedProductIndex = index
                                        unitPriceStr = p.sellingPrice.toInt().toString()
                                    }
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                        Text(text = p.name, color = if (isSelected) Color.White else Color(0xFF1E293B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(text = "من: ${p.supplierName} • مخزون: ${p.stockQuantity}", color = if (isSelected) Color(0xFFE2E8F0) else SlateTextSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quantityStr,
                            onValueChange = { quantityStr = it },
                            label = { Text("الكمية") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = unitPriceStr,
                            onValueChange = { unitPriceStr = it },
                            label = { Text("سعر البيع (ج.م)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = selectedSize,
                            onValueChange = { selectedSize = it },
                            label = { Text("المقاس (M, L, XL..)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = selectedColor,
                            onValueChange = { selectedColor = it },
                            label = { Text("اللون") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات إضافية") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    Button(
                        onClick = {
                            val product = activeProduct ?: return@Button
                            val qty = quantityStr.toIntOrNull() ?: 1
                            val price = unitPriceStr.toDoubleOrNull() ?: product.sellingPrice
                            val shipFeeCust = shippingFeeToCustomerStr.toDoubleOrNull() ?: 50.0
                            val actualShip = actualShippingCostStr.toDoubleOrNull() ?: 45.0

                            onCreate(
                                customerName,
                                customerPhone,
                                customerCity,
                                customerAddress,
                                shippingCompany,
                                shipFeeCust,
                                actualShip,
                                product,
                                qty,
                                selectedSize,
                                selectedColor,
                                price,
                                status,
                                isCollected,
                                notes
                            )
                        },
                        enabled = customerName.isNotBlank() && activeProduct != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_order_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(text = "حفظ الطلب وخصم الكمية من المخزن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
