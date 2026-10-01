package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RoseLoss
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextSecondary

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = NavyPrimary,
    iconBg: Color = iconTint.copy(alpha = 0.12f),
    modifier: Modifier = Modifier,
    isPositiveTrend: Boolean? = null
) {
    Card(
        modifier = modifier.testTag("metric_card_${title.replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPositiveTrend != null) {
                        Text(
                            text = if (isPositiveTrend) "▲ " else "▼ ",
                            color = if (isPositiveTrend) EmeraldProfit else RoseLoss,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPositiveTrend == true) EmeraldProfit else if (isPositiveTrend == false) RoseLoss else SlateTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        "DELIVERED" -> Triple(EmeraldLight, EmeraldProfit, "تم التسليم ✓")
        "SHIPPED" -> Triple(Color(0xFFE0F2FE), Color(0xFF0284C7), "قيد الشحن 🚚")
        "PROCESSING" -> Triple(AmberLight, AmberWarning, "قيد التجهيز ⏳")
        "RETURNED" -> Triple(RoseLight, RoseLoss, "مرتجع ↩")
        "CANCELLED" -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), "ملغي ✕")
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF334155), status)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SlateTextSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = SlateTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SourceTagBadge(
    tag: String,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when {
        tag.contains("ذهبي") -> Pair(AmberLight, Color(0xFFB45309))
        tag.contains("مرتجع") -> Pair(RoseLight, RoseLoss)
        tag.contains("ربحية") -> Pair(EmeraldLight, EmeraldProfit)
        tag.contains("سريع") -> Pair(Color(0xFFE0E7FF), Color(0xFF4338CA))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bg
    ) {
        Text(
            text = tag,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
