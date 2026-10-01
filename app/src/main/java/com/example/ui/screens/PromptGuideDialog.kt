package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextSecondary

@Composable
fun PromptGuideDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val financialPromptText = """
# برومبت الخبير المالي والاقتصادي لتجارة الملابس أونلاين (Multi-Source E-Commerce)

بصفتك خبير استشارات مالية واقتصادية متخصص في تجارة التجزئة الإلكترونية (Apparel & Fashion E-Commerce)، أريدك أن تبني وتدير نظاماً مالياً ومحاسبياً متكاملاً لعلامتي التجارية للملابس التي تعتمد على التوريد من (مصانع متعددة + مكاتب جملة).

## 1. الهيكل المالي ووحدة الاقتصاد (Unit Economics per Garment):
لكل قطعة ملابس يتم تسعيرها، يجب أن يتبع النظام المعادلة الاقتصادية التالية:
- التكلفة الحقيقية الكاملة (True Landed Cost) = سعر الشراء من المصنع/المكتب + تكلفة النقل للشحنة مقسومة على القطع + تكلفة كيس الشحن واللوجو والملصقات + مخصص احتياطي هالك المرتجعات (يقدر بـ 5-8% من سعر التكلفة).
- صافي الربح المساهم (Contribution Margin) = سعر البيع للعميل - (True Landed Cost + تكلفة الإعلانات الممولة لكل طلب CPA + صافي خسارة الشحن إن وجدت).

## 2. مصفوفة تتبع أرباح المصادر (Per-Supplier Profitability Matrix):
- ربط كل كود منتج (SKU) بالمصنع أو مكتب الجملة المورد له.
- حساب (إجمالي الإيرادات، تكلفة البضاعة COGS، مجمل الربح، هامش الربح %) لكل مصنع على حدة.
- رصد معدل المرتجعات (Return Rate %) لكل مورد: إذا تجاوز معدل المرتجع 15% لمصنع معين، يقوم النظام بإطلاق تنبيه فوري لفحص جدول المقاسات وجودة التقفيل.
- رصد رأس المال المجمد في المخزون (Tied-up Capital) لكل مصنع لتفادي تراكم بضاعة راكدة غير قابلة للتسييل السريع.

## 3. إدارة التدفقات النقدية وشركات الشحن (Cash Flow & Courier Receivables):
- تتبع حالة التحصيل مع شركات الشحن بدقة: أموال محصلة وموردة للبنك مقابل أموال معلقة تحت التحصيل في الطريق (Cash in Transit).
- إدارة تكلفة الشحن المرتجع: في حال رفض العميل استلام الأوردر، يتم تسجيل تكلفة شحن البوليصة تلقائياً كـ (خسائر شحن مرتجعات) ضمن المصروفات التشغيلية، مع إعادة القطعة لمخزون المتجر بعد المعاينة.

## 4. تقارير الأداء المالي والأولويات التنفيذية:
- تقرير الأرباح الصافية الحقيقية (Net Profit) بعد خصم ميزانيات الإعلانات الممولة (Meta, TikTok) والتكاليف التشغيلية.
- مقياس كفاءة الإعلانات: ROAS (العائد على الإنفاق الإعلاني) و MER (Marketing Efficiency Ratio).
- توصية التوريد الأسبوعية: تحديد (المصنع الذهبي) الذي يحقق أعلى صافي ربح بأقل مرتجعات لمضاعفة التوريد منه، وتحديد الأصناف البطيئة لعرض خصومات تصفية عليها فوراً.
    """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EmeraldProfit,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "دليل الخبير الاقتصادي والبرومبت المالي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "الهيكل المالي والنموذج الاحترافي المخصص لمشروعك",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateBorder)

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Quick Action Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = EmeraldProfit,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "هذا البرومبت تم صياغته بناءً على أفضل الممارسات المحاسبية لمشاريع الملابس لتتبع أرباح المصانع ومصروفات الإعلانات بدقة متناهية.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF166534),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Core Financial Rules
                    Text(
                        text = "القواعد الاقتصادية الأربع لنجاح تجارة الملابس أونلاين:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    EconomicRuleItem(
                        number = "١",
                        title = "معادلة التكلفة الحقيقية (Landed Cost)",
                        desc = "لا تحسب تكلفة القطعة بسعر الفاتورة فقط، بل أضف إليها (نولون النقل + كيس الشحن الفاخر + استيكر اللوجو + مخصص هالك المرتجعات 6%)."
                    )

                    EconomicRuleItem(
                        number = "٢",
                        title = "معدل المرتجعات هو القاتل الصامت للأرباح",
                        desc = "في الملابس أونلاين، 60% من المرتجعات سببها عدم دقة المقاسات؛ تتبع المرتجع لكل مصنع لعزل المصانع التي تسبب لك خسائر شحن رايح جاي."
                    )

                    EconomicRuleItem(
                        number = "٣",
                        title = "حجز سيولة الشحن (Cash in Transit)",
                        desc = "الأموال مع شركات الشحن ليست أرباحاً حتى يتم تحصيلها وتوريدها لحسابك البنكي، لذا يفصل التطبيق الأوردرات المحصلة عن المعلقة."
                    )

                    EconomicRuleItem(
                        number = "٤",
                        title = "التنويع والتفاوض مع المصانع",
                        desc = "استخدم شاشة 'تحليل المصادر' لإبراز حجم مبيعاتك مع المصنع والتفاوض على خصم كمية إضافي 5-10% أو شروط دفع مؤجلة."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prompt Preview Card
                    Text(
                        text = "نص البرومبت المالي المرجعي الجاهز للنسخ:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = financialPromptText,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(14.dp),
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom Action Button: Copy Prompt
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Clothing Finance Prompt", financialPromptText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ البرومبت المالي الاقتصادي بنجاح!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("copy_prompt_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نسخ البرومبت المالي المتكامل إلى الحافظة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EconomicRuleItem(
    number: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = NavyPrimary.copy(alpha = 0.1f),
            modifier = Modifier.size(26.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
