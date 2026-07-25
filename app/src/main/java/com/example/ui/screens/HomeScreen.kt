package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SnsShareModal
import com.example.ui.theme.JoseonGold
import com.example.ui.theme.RoyalCrimson
import com.example.ui.viewmodel.SillokViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    viewModel: SillokViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    // If an article is selected for full detail screen
    if (uiState.selectedDetailArticle != null) {
        val detailArticle = uiState.selectedDetailArticle!!
        val isBookmarked = bookmarks.any { it.id == detailArticle.id }

        DetailScreen(
            article = detailArticle,
            isBookmarked = isBookmarked,
            onToggleBookmark = { viewModel.toggleBookmark(detailArticle) },
            onSnsShareClick = { viewModel.setShareArticle(detailArticle) },
            onBackClick = { viewModel.setDetailArticle(null) }
        )

        // Show SNS Share Modal if active
        if (uiState.activeShareArticle != null) {
            SnsShareModal(
                article = uiState.activeShareArticle!!,
                onDismiss = { viewModel.setShareArticle(null) }
            )
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "조선왕조실록 (朝鮮王朝實錄)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "키워드 검색 · 구글맵 위치 · 연표 필터 · SNS 연동",
                            style = MaterialTheme.typography.labelSmall,
                            color = JoseonGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RoyalCrimson
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "실록 검색"
                        )
                    },
                    label = { Text("실록 검색", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalCrimson,
                        selectedTextColor = RoyalCrimson,
                        indicatorColor = JoseonGold.copy(alpha = 0.3f)
                    )
                )

                NavigationBarItem(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "역사 지도"
                        )
                    },
                    label = { Text("역사 지도", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalCrimson,
                        selectedTextColor = RoyalCrimson,
                        indicatorColor = JoseonGold.copy(alpha = 0.3f)
                    )
                )

                NavigationBarItem(
                    selected = uiState.activeTab == 2,
                    onClick = { viewModel.setActiveTab(2) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "보관함"
                        )
                    },
                    label = { Text("보관함 (${bookmarks.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalCrimson,
                        selectedTextColor = RoyalCrimson,
                        indicatorColor = JoseonGold.copy(alpha = 0.3f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.activeTab,
                transitionSpec = { fadeIn() with fadeOut() }
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> SearchScreen(
                        query = uiState.query,
                        onQueryChange = { viewModel.onQueryChange(it) },
                        onSearchSubmit = { viewModel.performSearch(it) },
                        searchResults = uiState.searchResults,
                        isLoading = uiState.isLoading,
                        selectedKing = uiState.selectedKing,
                        startYear = uiState.startYear,
                        endYear = uiState.endYear,
                        onKingSelected = { viewModel.setKingFilter(it) },
                        onYearRangeChanged = { s, e -> viewModel.setYearRange(s, e) },
                        onResetFilters = { viewModel.resetFilters() },
                        bookmarks = bookmarks,
                        recentSearches = recentSearches,
                        onToggleBookmark = { viewModel.toggleBookmark(it) },
                        onSnsShareClick = { viewModel.setShareArticle(it) },
                        onArticleClick = { viewModel.setDetailArticle(it) },
                        onDeleteRecentSearch = { viewModel.deleteRecentSearch(it) },
                        onClearRecentSearches = { viewModel.clearRecentSearches() }
                    )

                    1 -> MapExplorerScreen(
                        bookmarks = bookmarks,
                        onToggleBookmark = { viewModel.toggleBookmark(it) },
                        onSnsShareClick = { viewModel.setShareArticle(it) },
                        onArticleClick = { viewModel.setDetailArticle(it) }
                    )

                    2 -> BookmarksScreen(
                        bookmarks = bookmarks,
                        onToggleBookmark = { viewModel.toggleBookmark(it) },
                        onSnsShareClick = { viewModel.setShareArticle(it) },
                        onArticleClick = { viewModel.setDetailArticle(it) }
                    )
                }
            }

            // SNS Share BottomSheet Modal
            if (uiState.activeShareArticle != null) {
                SnsShareModal(
                    article = uiState.activeShareArticle!!,
                    onDismiss = { viewModel.setShareArticle(null) }
                )
            }
        }
    }
}
