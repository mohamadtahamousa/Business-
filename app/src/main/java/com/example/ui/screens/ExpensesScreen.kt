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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.Expense
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesScreen(
    expenses: List<Expense>,
    onAddExpense: (title: String, category: String, amount: Double, notes: String) -> Unit,
    onDeleteExpense: (Expense) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val totalExpenses = expenses.sumOf { it.amount }
    val adsAmount = expenses.filter { it.category == "إعلانات ممولة" }.sumOf { it.amount }
    val packagingAmount = expenses.filter { it.category == "أكياس وتغليف" }.sumOf { it.amount }
    val returnsLossAmount = expenses.filter { it.category == "شحن مرتجعات" }.sumOf { it.amount }
    val otherAmount = totalExpenses - adsAmount - packagingAmount - returnsLossAmount

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "مصروف جديد")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "تسجيل مصروف", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("expenses_screen")
        ) {
            // Header: Total Expenses Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "إجمالي المصروفات التشغيلية والتسويقية", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format(Locale.US, "%,.0f", totalExpenses)} ج.م",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF87171)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress breakdown
                    ExpenseProgressBar(label = "إعلانات ممولة (Meta, TikTok)", amount = adsAmount, total = totalExpenses, color = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.height(6.dp))
                    ExpenseProgressBar(label = "أكياس شحن وبوليصات وتغليف", amount = packagingAmount, total = totalExpenses, color = Color(0xFFFBBF24))
                    Spacer(modifier = Modifier.height(6.dp))
                    ExpenseProgressBar(label = "خسائر شحن مرتجعات (لم تستلم)", amount = returnsLossAmount, total = totalExpenses, color = Color(0xFFF43F5E))
                }
            }

            if (expenses.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ReceiptLong,
                    title = "لا توجد مصروفات مسجلة",
                    description = "سجل مصروفات الإعلانات، أكياس الشحن، والتخزين لحساب صافي الربح الحقيقي بدقة.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(expenses) { expense ->
                        ExpenseItemCard(expense = expense, onDelete = { onDeleteExpense(expense) })
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, cat, amt, notes ->
                onAddExpense(title, cat, amt, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ExpenseProgressBar(
    label: String,
    amount: Double,
    total: Double,
    color: Color
) {
    val fraction = if (total > 0) (amount / total).toFloat().coerceIn(0f, 1f) else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text(text = "${String.format(Locale.US, "%,.0f", amount)} ج.م", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF334155)
        )
    }
}

@Composable
fun ExpenseItemCard(
    expense: Expense,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(expense.date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoseLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            expense.category.contains("إعلانات") -> Icons.Default.Campaign
                            expense.category.contains("تغليف") -> Icons.Default.LocalMall
                            expense.category.contains("شحن") -> Icons.Default.LocalShipping
                            else -> Icons.Default.ReceiptLong
                        },
                        contentDescription = null,
                        tint = RoseLoss,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = expense.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(text = "${expense.category} • $dateStr", style = MaterialTheme.typography.bodySmall, color = SlateTextSecondary)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-${String.format(Locale.US, "%,.0f", expense.amount)} ج.م",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = RoseLoss
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = SlateTextSecondary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, category: String, amount: Double, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("إعلانات ممولة") }
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("إعلانات ممولة", "أكياس وتغليف", "شحن مرتجعات", "إيجار وتخزين", "رواتب وعمولات", "أخرى")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "تسجيل مصروف جديد", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SlateBorder)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("بند المصروف (مثال: إعلانات تيك توك) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text(text = "التصنيف:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.chunked(2).forEach { rowCats ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowCats.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) NavyPrimary else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedCategory = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("المبلغ (ج.م) *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull() ?: return@Button
                        if (title.isNotBlank()) {
                            onAdd(title, selectedCategory, amount, notes)
                        }
                    },
                    enabled = title.isNotBlank() && amountStr.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_expense_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text(text = "تسجيل وخصم من صافي الأرباح", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
