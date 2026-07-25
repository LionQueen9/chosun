package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoseonKings
import com.example.ui.theme.HanjiPaperDark
import com.example.ui.theme.JoseonGold
import com.example.ui.theme.RoyalCrimson

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    selectedKing: String?,
    startYear: Int,
    endYear: Int,
    onKingSelected: (String?) -> Unit,
    onYearRangeChanged: (Int, Int) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, JoseonGold.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = null,
                        tint = RoyalCrimson,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "왕대 및 연표 상세 필터",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(
                    onClick = onResetFilters,
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("초기화", fontSize = 12.sp, color = RoyalCrimson)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. King Filter Chips Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HistoryEdu,
                    contentDescription = null,
                    tint = JoseonGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "조선 국왕 선택 (${selectedKing ?: "전체"})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Kings Row
            val kingsScrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(kingsScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "전체" Chip
                KingChip(
                    label = "전체 국왕",
                    isSelected = selectedKing == null,
                    onClick = { onKingSelected(null) }
                )

                // Key Kings
                val featuredKings = listOf(
                    "태조", "태종", "세종", "세조", "성종", "연산군",
                    "중종", "선조", "광해군", "인조", "효종", "숙종",
                    "영조", "정조", "순조", "고종"
                )

                featuredKings.forEach { kingName ->
                    KingChip(
                        label = kingName,
                        isSelected = selectedKing == kingName,
                        onClick = {
                            if (selectedKing == kingName) onKingSelected(null)
                            else onKingSelected(kingName)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Century / Timeline Quick Pills
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = JoseonGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "연표/세기 탐색 (${startYear}년 ~ ${endYear}년)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Century Shortcuts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CenturyPill("전체 연도", 1392, 1910, startYear, endYear, onYearRangeChanged)
                CenturyPill("14세기 (개국)", 1392, 1400, startYear, endYear, onYearRangeChanged)
                CenturyPill("15세기 (세종·성종)", 1401, 1500, startYear, endYear, onYearRangeChanged)
                CenturyPill("16세기 (선조·임진왜란)", 1501, 1600, startYear, endYear, onYearRangeChanged)
                CenturyPill("17세기 (광해·인조·숙종)", 1601, 1700, startYear, endYear, onYearRangeChanged)
                CenturyPill("18세기 (영조·정조·탕평)", 1701, 1800, startYear, endYear, onYearRangeChanged)
                CenturyPill("19세기 (고종·대한제국)", 1801, 1910, startYear, endYear, onYearRangeChanged)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Year Range Slider
            RangeSlider(
                value = startYear.toFloat()..endYear.toFloat(),
                onValueChange = { range ->
                    onYearRangeChanged(range.start.toInt(), range.endInclusive.toInt())
                },
                valueRange = 1392f..1910f,
                steps = 51,
                colors = SliderDefaults.colors(
                    thumbColor = RoyalCrimson,
                    activeTrackColor = RoyalCrimson,
                    inactiveTrackColor = JoseonGold.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun KingChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) RoyalCrimson else MaterialTheme.colorScheme.surface
            )
            .border(
                1.dp,
                if (isSelected) RoyalCrimson else JoseonGold.copy(alpha = 0.5f),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CenturyPill(
    label: String,
    sYear: Int,
    eYear: Int,
    currentStart: Int,
    currentEnd: Int,
    onSelect: (Int, Int) -> Unit
) {
    val isSelected = currentStart == sYear && currentEnd == eYear

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) JoseonGold else MaterialTheme.colorScheme.surface)
            .clickable { onSelect(sYear, eYear) }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
