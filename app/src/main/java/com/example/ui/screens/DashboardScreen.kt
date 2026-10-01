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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialSummary
import com.example.data.model.Order
import com.example.data.model.SourceProfitability
import com.example.ui.components.MetricCard
import com.example.ui.components.SourceTagBadge
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
import java.util.Locale

@Composable
fun DashboardScreen(
    summary: FinancialSummary,
    sources: List<SourceProfitability>,
    recentOrders: List<Order>,
    onOpenPromptGuide: () -> Unit,
    onNavigateToSources: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToExpenses: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & AI/Economic Guide Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "لوحة القيادة المالية المتكاملة",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تجارة الملابس أونلاين • تتبع أرباح المصانع والجملة",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = EmeraldProfit,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Net Profit Spotlight
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "صافي الربح الحقيقي (بعد كل المصاريف)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${String.format(Locale.US, "%,.1f", summary.netProfit)} ج.م",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (summary.netProfit >= 0) Color(0xFF34D399) else Color(0xFFF87171)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (summary.netMarginPercent >= 20) EmeraldLight else AmberLight
                                ) {
                                    Text(
                                        text = "هامش صافي: ${String.format(Locale.US, "%.1f", summary.netMarginPercent)}%",
                                        color = if (summary.netMarginPercent >= 20) EmeraldProfit else AmberWarning,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "مجمل الربح: ${String.format(Locale.US, "%,.0f", summary.grossProfit)} ج.م",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Button to open Economic Prompt Guide
                    Button(
                        onClick = onOpenPromptGuide,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_prompt_guide_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "عرض نموذج البرومبت المالي المرجعي ودليل الخبير",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Primary Financial Metrics 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "إجمالي المبيعات",
                        value = "${String.format(Locale.US, "%,.0f", summary.totalRevenue)} ج.م",
                        subtitle = "${summary.totalOrdersCount} طلب مسجل",
                        icon = Icons.Default.MonetizationOn,
                        iconTint = NavyPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "تكلفة البضاعة المباعة",
                        value = "${String.format(Locale.US, "%,.0f", summary.totalCogs)} ج.م",
                        subtitle = "سعر الشراء من المصانع",
                        icon = Icons.Default.PriceCheck,
                        iconTint = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "المصروفات والإعلانات",
                        value = "${String.format(Locale.US, "%,.0f", summary.totalExpenses)} ج.م",
                        subtitle = "إعلانات: ${String.format(Locale.US, "%,.0f", summary.adsExpenses)} ج.م",
                        icon = Icons.Default.Campaign,
                        iconTint = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "معلق لدى شركات الشحن",
                        value = "${String.format(Locale.US, "%,.0f", summary.pendingCourierCash)} ج.م",
                        subtitle = "سيولة بالطريق تحت التحصيل",
                        icon = Icons.Default.LocalShipping,
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Key E-Commerce Clothing Health KPIs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "مؤشرات الصحة المالية لمتجر الملابس (KPIs)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        KPIColumn(
                            title = "قيمة المخزون بالتكلفة",
                            value = "${String.format(Locale.US, "%,.0f", summary.inventoryAssetValue)} ج.م",
                            note = "رأس مال متوفر بضاعة"
                        )
                        KPIColumn(
                            title = "معدل المرتجعات",
                            value = "${String.format(Locale.US, "%.1f", summary.overallReturnRate)}%",
                            note = if (summary.overallReturnRate > 15) "⚠️ مرتفع" else "✓ في الحدود الآمنة",
                            isWarning = summary.overallReturnRate > 15
                        )
                        KPIColumn(
                            title = "العائد الإعلاني (ROAS)",
                            value = if (summary.roas > 0) "${String.format(Locale.US, "%.1f", summary.roas)}X" else "-",
                            note = if (summary.roas >= 3) "ممتاز 🚀" else "يحتاج تحسين"
                        )
                    }
                }
            }
        }

        // Source Profitability Quick Spotlight
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ترتيب المصانع ومكاتب الجملة حسب الربحية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "عرض الكل",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    modifier = Modifier.clickable { onNavigateToSources() }
                )
            }
        }

        items(sources.take(3)) { source ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSources() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = source.supplierName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            SourceTagBadge(tag = source.statusTag)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${source.supplierType} • تم بيع ${source.totalUnitsSold} قطعة • متبقي ${source.totalStockUnits} بالمخزن",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+${String.format(Locale.US, "%,.0f", source.grossProfit)} ج.م",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldProfit
                        )
                        Text(
                            text = "هامش ${String.format(Locale.US, "%.1f", source.profitMarginPercent)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        }

        // Recent Orders Spotlight
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آخر الطلبات والمبيعات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "إدارة الطلبات",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    modifier = Modifier.clickable { onNavigateToOrders() }
                )
            }
        }

        items(recentOrders.take(3)) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = order.orderNumber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(status = order.status)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${order.customerName} (${order.customerCity})",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format(Locale.US, "%,.0f", order.totalAmount)} ج.م",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (order.isCollected) "تم التحصيل ✓" else "قيد التوريد ⏳",
                            fontSize = 11.sp,
                            color = if (order.isCollected) EmeraldProfit else AmberWarning
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KPIColumn(
    title: String,
    value: String,
    note: String,
    isWarning: Boolean = false
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = SlateTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isWarning) RoseLoss else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = note,
            fontSize = 11.sp,
            color = if (isWarning) RoseLoss else EmeraldProfit,
            fontWeight = FontWeight.Medium
        )
    }
}
