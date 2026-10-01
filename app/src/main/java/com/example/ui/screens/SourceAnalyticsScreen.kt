package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SourceProfitability
import com.example.data.model.Supplier
import com.example.ui.components.EmptyStateView
import com.example.ui.components.SourceTagBadge
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
fun SourceAnalyticsScreen(
    sources: List<SourceProfitability>,
    suppliers: List<Supplier>,
    onAddSupplier: (name: String, type: String, phone: String, city: String, paymentTerms: String, notes: String) -> Unit
) {
    var showAddSupplierDialog by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("PROFIT") } // PROFIT, MARGIN, UNITS
    var selectedTypeFilter by remember { mutableStateOf("ALL") } // ALL, FACTORY, WHOLESALE
    var searchQuery by remember { mutableStateOf("") }

    val totalProfitAllSources = sources.sumOf { it.grossProfit }.coerceAtLeast(1.0)
    val totalRevenueAllSources = sources.sumOf { it.totalRevenue }
    val overallMargin = if (totalRevenueAllSources > 0) (totalProfitAllSources / totalRevenueAllSources) * 100 else 0.0

    // Filter and sort sources
    val filteredSources = sources.filter { item ->
        val matchesSearch = item.supplierName.contains(searchQuery, ignoreCase = true) ||
                item.supplierType.contains(searchQuery, ignoreCase = true)
        val matchesType = when (selectedTypeFilter) {
            "FACTORY" -> item.supplierType.contains("مصنع")
            "WHOLESALE" -> !item.supplierType.contains("مصنع")
            else -> true
        }
        matchesSearch && matchesType
    }.let { list ->
        when (selectedSort) {
            "MARGIN" -> list.sortedByDescending { it.profitMarginPercent }
            "UNITS" -> list.sortedByDescending { it.totalUnitsSold }
            else -> list.sortedByDescending { it.grossProfit }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSupplierDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_supplier_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة مورد جديد")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "إضافة مصنع / مورد", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        if (sources.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Factory,
                title = "لا توجد بيانات أرباح مسجلة للمصادر",
                description = "أضف المصانع ومكاتب الجملة وسجل المبيعات لترى توزيع الأرباح والهوامش بوضوح.",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("source_analytics_screen"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Executive Material 3 Summary Hero Card
                item {
                    ProfitDistributionSummaryCard(
                        totalProfit = totalProfitAllSources,
                        overallMargin = overallMargin,
                        sources = sources
                    )
                }

                // Section 2: Clean Search & Material 3 Filter Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("بحث عن مصنع أو مكتب جملة...") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "بحث", tint = SlateTextSecondary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "مسح")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyPrimary,
                                unfocusedBorderColor = SlateBorder,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_supplier_input")
                        )

                        // Material 3 Sorting & Filtering Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedSort == "PROFIT",
                                    onClick = { selectedSort = "PROFIT" },
                                    label = { Text("الأعلى ربحاً 💰", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary.copy(alpha = 0.15f),
                                        selectedLabelColor = NavyPrimary
                                    )
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedSort == "MARGIN",
                                    onClick = { selectedSort = "MARGIN" },
                                    label = { Text("أعلى هامش %", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary.copy(alpha = 0.15f),
                                        selectedLabelColor = NavyPrimary
                                    )
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedSort == "UNITS",
                                    onClick = { selectedSort = "UNITS" },
                                    label = { Text("الأكثر مبيعاً ⚡", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary.copy(alpha = 0.15f),
                                        selectedLabelColor = NavyPrimary
                                    )
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedTypeFilter == "FACTORY",
                                    onClick = { selectedTypeFilter = if (selectedTypeFilter == "FACTORY") "ALL" else "FACTORY" },
                                    label = { Text("مصانع فقط", fontSize = 12.sp) }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedTypeFilter == "WHOLESALE",
                                    onClick = { selectedTypeFilter = if (selectedTypeFilter == "WHOLESALE") "ALL" else "WHOLESALE" },
                                    label = { Text("مكاتب جملة", fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }

                // Section 3: Cards List - Clean Material 3 Profit Cards
                items(filteredSources, key = { it.supplierId }) { source ->
                    val profitShare = if (totalProfitAllSources > 0) (source.grossProfit / totalProfitAllSources).toFloat().coerceIn(0f, 1f) else 0f
                    SimpleSupplierProfitCard(
                        source = source,
                        profitShare = profitShare
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    if (showAddSupplierDialog) {
        AddSupplierDialog(
            onDismiss = { showAddSupplierDialog = false },
            onSave = { name, type, phone, city, paymentTerms, notes ->
                onAddSupplier(name, type, phone, city, paymentTerms, notes)
                showAddSupplierDialog = false
            }
        )
    }
}

/**
 * Top Material 3 Hero Card: Displays aggregate gross profit and the visual contribution bar
 */
@Composable
private fun ProfitDistributionSummaryCard(
    totalProfit: Double,
    overallMargin: Double,
    sources: List<SourceProfitability>
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = NavyDark),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ملخص أرباح مصادر التوريد",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "موزعة بدقة حسب المصنع ومكتب الجملة",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PieChart, contentDescription = null, tint = EmeraldProfit, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${sources.size} مصادر",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Numbers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "إجمالي أرباح التوريد",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "${String.format(Locale.US, "%,.0f", totalProfit)} ج.م",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF34D399)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldLight
                ) {
                    Text(
                        text = "متوسط الهامش: ${String.format(Locale.US, "%.1f", overallMargin)}%",
                        color = EmeraldProfit,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Segmented Profit Contribution Bar
            Text(
                text = "نسبة مساهمة كل مصدر في إجمالي الأرباح:",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stacked Distribution Row
            val palette = listOf(
                Color(0xFF10B981), // Emerald
                Color(0xFF38BDF8), // Sky Blue
                Color(0xFFF59E0B), // Amber
                Color(0xFFA78BFA), // Purple
                Color(0xFFF43F5E)  // Rose
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF334155))
            ) {
                sources.forEachIndexed { index, source ->
                    val weight = if (totalProfit > 0) (source.grossProfit / totalProfit).toFloat().coerceAtLeast(0.01f) else 1f
                    val color = palette[index % palette.size]
                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .height(10.dp)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend labels
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(sources.indices.toList()) { index ->
                    val source = sources[index]
                    val color = palette[index % palette.size]
                    val pct = if (totalProfit > 0) (source.grossProfit / totalProfit * 100) else 0.0
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${source.supplierName.take(12)} (${String.format(Locale.US, "%.0f", pct)}%)",
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clean & Simple Material 3 Supplier Profit Card
 * Displays:
 * 1. Supplier Name & Type
 * 2. Gross Profit & Profit Margin %
 * 3. Relative Share of Total Store Profit (Progress bar)
 * 4. Revenue vs Cost (COGS)
 * 5. Expandable details with unit economics & actionable recommendation
 */
@Composable
fun SimpleSupplierProfitCard(
    source: SourceProfitability,
    profitShare: Float
) {
    var isExpanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("supplier_profit_card_${source.supplierId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name, Type, Tag, and Expand Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = NavyPrimary.copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (source.supplierType.contains("مصنع")) Icons.Default.Factory else Icons.Default.Store,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = source.supplierName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = source.supplierType,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            SourceTagBadge(tag = source.statusTag)
                        }
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "عرض التفاصيل",
                        tint = SlateTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Profit Display Row (Highlighting Net Gross Profit & Margin)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "صافي ربح المصدر",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateTextSecondary
                        )
                        Text(
                            text = "+${String.format(Locale.US, "%,.0f", source.grossProfit)} ج.م",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldProfit
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (source.profitMarginPercent >= 45) EmeraldLight else NavyPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "هامش ${String.format(Locale.US, "%.1f", source.profitMarginPercent)}%",
                                color = if (source.profitMarginPercent >= 45) EmeraldProfit else NavyPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${source.totalUnitsSold} قطعة مباعة",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Profit Contribution Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "المساهمة في أرباح المتجر الكلية",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextSecondary
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.0f", profitShare * 100)}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { profitShare },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldProfit,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            // Expandable Detailed Breakdown Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(animationSpec = spring()),
                exit = fadeOut() + shrinkVertically(animationSpec = spring())
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = SlateBorder, modifier = Modifier.padding(vertical = 8.dp))

                    // Dual Metric Grid: Revenue vs COGS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "إجمالي المبيعات", style = MaterialTheme.typography.labelSmall, color = SlateTextSecondary)
                            Text(
                                text = "${String.format(Locale.US, "%,.0f", source.totalRevenue)} ج.م",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "تكلفة الشراء (COGS)", style = MaterialTheme.typography.labelSmall, color = SlateTextSecondary)
                            Text(
                                text = "${String.format(Locale.US, "%,.0f", source.totalCogs)} ج.م",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "معدل المرتجعات", style = MaterialTheme.typography.labelSmall, color = SlateTextSecondary)
                            Text(
                                text = "${String.format(Locale.US, "%.1f", source.returnRatePercent)}% (${source.returnCount})",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (source.returnRatePercent >= 15) RoseLoss else EmeraldProfit
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stock Capital Info
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Inventory2, contentDescription = null, tint = SlateTextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "المخزون المتبقي:", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                            }
                            Text(
                                text = "${source.totalStockUnits} قطعة (رأس مال: ${String.format(Locale.US, "%,.0f", source.currentInventoryValue)} ج.م)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Economic Recommendation Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (source.returnRatePercent >= 15) RoseLight else EmeraldLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (source.returnRatePercent >= 15) Icons.Default.Warning else Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = if (source.returnRatePercent >= 15) RoseLoss else EmeraldProfit,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = source.recommendation,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (source.returnRatePercent >= 15) Color(0xFF9F1239) else Color(0xFF166534),
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddSupplierDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, phone: String, city: String, paymentTerms: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("مصنع ملابس") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var paymentTerms by remember { mutableStateOf("كاش") }
    var notes by remember { mutableStateOf("") }

    val supplierTypes = listOf("مصنع ملابس", "مكتب جملة", "مستورد جملة")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إضافة مورد جديد (مصنع / مكتب جملة)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SlateBorder)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المصنع أو مكتب الجملة *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supplier_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "نوع المصدر:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    supplierTypes.forEach { type ->
                        val isSelected = selectedType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NavyPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Button(
                                onClick = { selectedType = type },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) NavyPrimary else Color(0xFFF1F5F9),
                                    contentColor = if (isSelected) Color.White else Color(0xFF334155)
                                ),
                                contentPadding = PaddingValues(4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = type, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("الهاتف") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("المدينة / المنطقة") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = paymentTerms,
                    onValueChange = { paymentTerms = it },
                    label = { Text("شروط السداد (كاش / آجل)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات الجودة وشروط الاستبدال") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(name, selectedType, phone, city, paymentTerms, notes)
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_supplier_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text(text = "حفظ المصدر وتفعيل التتبع المالي", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
