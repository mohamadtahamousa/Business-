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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.data.model.Product
import com.example.data.model.Supplier
import com.example.ui.components.EmptyStateView
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
import java.util.Locale

@Composable
fun InventoryScreen(
    products: List<Product>,
    suppliers: List<Supplier>,
    onAddProduct: (
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
    ) -> Unit,
    onUpdateProduct: (Product) -> Unit
) {
    var selectedSupplierId by remember { mutableStateOf<Long?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredProducts = if (selectedSupplierId == null) {
        products
    } else {
        products.filter { it.supplierId == selectedSupplierId }
    }

    val totalStockUnits = products.sumOf { it.stockQuantity }
    val totalInventoryValue = products.sumOf { it.stockQuantity * it.effectiveCostPerUnit }
    val lowStockCount = products.count { it.stockQuantity <= it.minStockAlert }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة منتج")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "إضافة صنف ملابس", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("inventory_screen")
        ) {
            // Inventory KPIs Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "قيمة المخزون الإجمالية (بسعر التكلفة)", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format(Locale.US, "%,.0f", totalInventoryValue)} ج.م",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "إجمالي القطع المتوفرة", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalStockUnits قطعة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Low Stock Alert if any
            if (lowStockCount > 0) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AmberLight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنبيه: يوجد $lowStockCount أصناف اقتربت من النفاذ وتحتاج لإعادة طلب من المصنع!",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            // Supplier Filters Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isSelected = selectedSupplierId == null
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) NavyPrimary else Color(0xFFE2E8F0),
                        modifier = Modifier.clickable { selectedSupplierId = null }
                    ) {
                        Text(
                            text = "جميع المصانع (${products.size})",
                            color = if (isSelected) Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                items(suppliers) { supplier ->
                    val isSelected = selectedSupplierId == supplier.id
                    val count = products.count { it.supplierId == supplier.id }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) NavyPrimary else Color(0xFFE2E8F0),
                        modifier = Modifier.clickable { selectedSupplierId = supplier.id }
                    ) {
                        Text(
                            text = "${supplier.name.take(15)} ($count)",
                            color = if (isSelected) Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Inventory2,
                    title = "لا توجد منتجات مسجلة في هذا المخزن",
                    description = "أضف منتجات الملابس وتكلفة كل قطعة وهوامش الربح لتتبع المخزون والأرباح بدقة.",
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
                    items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            onStockChange = { delta ->
                                val newQty = (product.stockQuantity + delta).coerceAtLeast(0)
                                onUpdateProduct(product.copy(stockQuantity = newQty))
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            suppliers = suppliers,
            onDismiss = { showAddDialog = false },
            onAdd = { supplier, name, cat, sku, cost, landed, price, qty, alert, sz, clr ->
                onAddProduct(supplier, name, cat, sku, cost, landed, price, qty, alert, sz, clr)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onStockChange: (delta: Int) -> Unit
) {
    val isLowStock = product.stockQuantity <= product.minStockAlert

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name, SKU, Stock Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "كود: ${product.sku} • من: ${product.supplierName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isLowStock) RoseLight else EmeraldLight
                ) {
                    Text(
                        text = "${product.stockQuantity} قطعة بالمخزن",
                        color = if (isLowStock) RoseLoss else EmeraldProfit,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Margins Matrix
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
                        Text(text = "سعر الشراء: ${product.purchaseCost.toInt()} ج.م", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                        Text(text = "تكلفة التجهيز: +${product.landedCostExtra.toInt()} ج.م", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                        Text(text = "التكلفة الكلية: ${product.effectiveCostPerUnit.toInt()} ج.م", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "سعر البيع: ${product.sellingPrice.toInt()} ج.م", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text(
                            text = "ربح القطعة: +${product.profitPerUnit.toInt()} ج.م",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldProfit
                        )
                        Text(
                            text = "هامش ${String.format(Locale.US, "%.1f", product.marginPercentage)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sizes, Colors, and Sold Units
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "المقاسات: ${product.sizes}", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                    Text(text = "الألوان: ${product.colors}", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                }

                // Quick stock +/- buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onStockChange(-1) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "خصم قطعة", modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${product.stockQuantity}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = NavyPrimary.copy(alpha = 0.1f),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onStockChange(+1) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة قطعة", tint = NavyPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    suppliers: List<Supplier>,
    onDismiss: () -> Unit,
    onAdd: (
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
    ) -> Unit
) {
    var selectedSupplierIndex by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("شتوي / كاجوال") }
    var sku by remember { mutableStateOf("") }
    var purchaseCostStr by remember { mutableStateOf("") }
    var landedCostStr by remember { mutableStateOf("10") }
    var sellingPriceStr by remember { mutableStateOf("") }
    var stockQuantityStr by remember { mutableStateOf("20") }
    var minAlertStr by remember { mutableStateOf("5") }
    var sizes by remember { mutableStateOf("M, L, XL, 2XL") }
    var colors by remember { mutableStateOf("أسود, كحلي, بيج") }

    val activeSupplier = suppliers.getOrNull(selectedSupplierIndex)

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
                        Text(text = "إضافة صنف ملابس جديد", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SlateBorder)
                }

                if (suppliers.isNotEmpty()) {
                    item {
                        Text(text = "اختر المصنع أو مكتب الجملة المورد:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(suppliers.indices.toList()) { index ->
                                val s = suppliers[index]
                                val isSelected = index == selectedSupplierIndex
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) NavyPrimary else Color(0xFFF1F5F9),
                                    modifier = Modifier.clickable { selectedSupplierIndex = index }
                                ) {
                                    Text(
                                        text = s.name,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم الموديل / القطعة *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("التصنيف") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = sku,
                            onValueChange = { sku = it },
                            label = { Text("كود SKU") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = purchaseCostStr,
                            onValueChange = { purchaseCostStr = it },
                            label = { Text("سعر الشراء *") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = landedCostStr,
                            onValueChange = { landedCostStr = it },
                            label = { Text("تكاليف تجهيز") },
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
                            value = sellingPriceStr,
                            onValueChange = { sellingPriceStr = it },
                            label = { Text("سعر البيع المقترح *") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = stockQuantityStr,
                            onValueChange = { stockQuantityStr = it },
                            label = { Text("الكمية المتاحة") },
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
                            value = sizes,
                            onValueChange = { sizes = it },
                            label = { Text("المقاسات") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = colors,
                            onValueChange = { colors = it },
                            label = { Text("الألوان") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            val supplier = activeSupplier ?: return@Button
                            val cost = purchaseCostStr.toDoubleOrNull() ?: return@Button
                            val landed = landedCostStr.toDoubleOrNull() ?: 0.0
                            val price = sellingPriceStr.toDoubleOrNull() ?: return@Button
                            val qty = stockQuantityStr.toIntOrNull() ?: 10
                            val alert = minAlertStr.toIntOrNull() ?: 5

                            onAdd(supplier, name, category, sku, cost, landed, price, qty, alert, sizes, colors)
                        },
                        enabled = name.isNotBlank() && purchaseCostStr.isNotBlank() && sellingPriceStr.isNotBlank() && activeSupplier != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_product_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(text = "حفظ وإدراج في المخزون", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
