package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.model.HistoricalLocation
import com.example.data.model.PredefinedLocations
import com.example.data.model.SillokArticle
import com.example.data.repository.SillokRepository
import com.example.ui.components.GoogleMapView
import com.example.ui.components.SillokCard
import com.example.ui.theme.JoseonGold
import com.example.ui.theme.RoyalCrimson

@Composable
fun MapExplorerScreen(
    bookmarks: List<BookmarkEntity>,
    onToggleBookmark: (SillokArticle) -> Unit,
    onSnsShareClick: (SillokArticle) -> Unit,
    onArticleClick: (SillokArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLocation by remember { mutableStateOf(PredefinedLocations.GYEONGBOKGUNG) }

    // Find articles linked to selected location
    val locationArticles = remember(selectedLocation) {
        SillokRepository.CURATED_ARTICLES.filter { article ->
            article.location?.id == selectedLocation.id ||
                    (article.title + " " + article.excerpt).contains(selectedLocation.name) ||
                    (article.title + " " + article.excerpt).contains(selectedLocation.JoseonEraName)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Location Selector Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = RoyalCrimson,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "조선 주요 역사 현장 지점 선택",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Locations Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PredefinedLocations.ALL_LOCATIONS.forEach { loc ->
                    val isSelected = selectedLocation.id == loc.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) RoyalCrimson else MaterialTheme.colorScheme.surface)
                            .border(
                                1.dp,
                                if (isSelected) RoyalCrimson else JoseonGold.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedLocation = loc }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = loc.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Interactive Google Map View Component
        GoogleMapView(
            location = selectedLocation,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(16.dp),
            showControls = true,
            onShareLocation = {
                // Share location as pseudo-article for SNS
                val dummyArticle = SillokArticle(
                    id = "map_${selectedLocation.id}",
                    title = "조선 역사 현장: ${selectedLocation.name}",
                    king = "조선 시대",
                    gregorianYear = 1392,
                    lunarDateStr = selectedLocation.region,
                    excerpt = selectedLocation.historicalSignificance,
                    location = selectedLocation
                )
                onSnsShareClick(dummyArticle)
            }
        )

        // Associated Sillok Articles for Selected Location Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📍 ${selectedLocation.name} 관련 실록 기사 (${locationArticles.size}건)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RoyalCrimson
            )
        }

        // List of Sillok Articles
        if (locationArticles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "해당 역사 현장의 관련 기사를 로딩 중입니다...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(locationArticles, key = { it.id }) { article ->
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
