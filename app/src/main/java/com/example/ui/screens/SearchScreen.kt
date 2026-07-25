package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.local.SearchHistoryEntity
import com.example.data.model.SillokArticle
import com.example.ui.components.FilterSection
import com.example.ui.components.SillokCard
import com.example.ui.theme.JoseonGold
import com.example.ui.theme.RoyalCrimson

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    searchResults: List<SillokArticle>,
    isLoading: Boolean,
    selectedKing: String?,
    startYear: Int,
    endYear: Int,
    onKingSelected: (String?) -> Unit,
    onYearRangeChanged: (Int, Int) -> Unit,
    onResetFilters: () -> Unit,
    bookmarks: List<BookmarkEntity>,
    recentSearches: List<SearchHistoryEntity>,
    onToggleBookmark: (SillokArticle) -> Unit,
    onSnsShareClick: (SillokArticle) -> Unit,
    onArticleClick: (SillokArticle) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFilterVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Header Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("검색어 입력 (예: 훈민정음, 임진왜란, 화성, 이순신)", fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = RoyalCrimson
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "지우기")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                        onSearchSubmit(query)
                    }),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalCrimson,
                        unfocusedBorderColor = JoseonGold.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Filter Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isFilterVisible || selectedKing != null) RoyalCrimson else JoseonGold
                        )
                        .clickable { isFilterVisible = !isFilterVisible }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "필터",
                        tint = if (isFilterVisible || selectedKing != null) Color.White else Color.Black
                    )
                }
            }

            // Recent Searches Quick Chips
            if (recentSearches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "최근 검색어:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        recentSearches.take(8).forEach { item ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        onQueryChange(item.query)
                                        onSearchSubmit(item.query)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = item.query,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Animated Collapsible Filter Section
        AnimatedVisibility(
            visible = isFilterVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            FilterSection(
                selectedKing = selectedKing,
                startYear = startYear,
                endYear = endYear,
                onKingSelected = onKingSelected,
                onYearRangeChanged = onYearRangeChanged,
                onResetFilters = onResetFilters,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Active Filter Summary Bar
        if (selectedKing != null || startYear != 1392 || endYear != 1910) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JoseonGold.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "적용된 필터: " +
                            (if (selectedKing != null) "국왕 [$selectedKing] " else "") +
                            "연표 [${startYear}년 ~ ${endYear}년]",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalCrimson
                )

                TextButton(
                    onClick = onResetFilters,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text("해제", fontSize = 11.sp, color = RoyalCrimson, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Results Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "실록 검색 결과 (${searchResults.size}건)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Main Results List or Loading/Empty
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = RoyalCrimson)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "조선왕조실록 사료 검색 중...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📜",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "검색 조건에 해당되는 실록 기사가 없습니다",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "다른 키워드나 연표/국왕 필터를 조정해 보세요",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = searchResults,
                    key = { it.id }
                ) { article ->
                    val isBookmarked = bookmarks.any { it.id == article.id }
                    SillokCard(
                        article = article,
                        isBookmarked = isBookmarked,
                        onBookmarkToggle = { onToggleBookmark(article) },
                        onSnsShareClick = { onSnsShareClick(article) },
                        onCardClick = { onArticleClick(article) }
                    )
                }
            }
        }
    }
}
