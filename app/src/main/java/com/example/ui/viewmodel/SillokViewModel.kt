package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.local.SearchHistoryEntity
import com.example.data.model.HistoricalLocation
import com.example.data.model.SillokArticle
import com.example.data.repository.SillokRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SillokUiState(
    val query: String = "훈민정음",
    val selectedKing: String? = null,
    val startYear: Int = 1392,
    val endYear: Int = 1910,
    val searchResults: List<SillokArticle> = emptyList(),
    val isLoading: Boolean = false,
    val activeShareArticle: SillokArticle? = null,
    val selectedDetailArticle: SillokArticle? = null,
    val activeTab: Int = 0 // 0: 검색, 1: 지도 탐색, 2: 즐겨찾기
)

@OptIn(FlowPreview::class)
class SillokViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SillokRepository(application)

    private val _uiState = MutableStateFlow(SillokUiState())
    val uiState: StateFlow<SillokUiState> = _uiState.asStateFlow()

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentSearches: StateFlow<List<SearchHistoryEntity>> = repository.searchHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Initial search
        performSearch()
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun performSearch(customQuery: String? = null) {
        viewModelScope.launch {
            val q = customQuery ?: _uiState.value.query
            if (customQuery != null) {
                _uiState.update { it.copy(query = customQuery) }
            }

            _uiState.update { it.copy(isLoading = true) }

            repository.recordSearchQuery(q)

            val results = repository.searchSillok(
                query = q,
                selectedKingName = _uiState.value.selectedKing,
                startYearFilter = _uiState.value.startYear,
                endYearFilter = _uiState.value.endYear
            )

            _uiState.update {
                it.copy(
                    searchResults = results,
                    isLoading = false
                )
            }
        }
    }

    fun setKingFilter(kingName: String?) {
        _uiState.update { it.copy(selectedKing = kingName) }
        performSearch()
    }

    fun setYearRange(start: Int, end: Int) {
        _uiState.update { it.copy(startYear = start, endYear = end) }
        performSearch()
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                selectedKing = null,
                startYear = 1392,
                endYear = 1910
            )
        }
        performSearch()
    }

    fun toggleBookmark(article: SillokArticle) {
        viewModelScope.launch {
            val isBookmarked = bookmarks.value.any { it.id == article.id }
            if (isBookmarked) {
                repository.removeBookmark(article.id)
            } else {
                repository.saveBookmark(article)
            }
        }
    }

    fun setShareArticle(article: SillokArticle?) {
        _uiState.update { it.copy(activeShareArticle = article) }
    }

    fun setDetailArticle(article: SillokArticle?) {
        _uiState.update { it.copy(selectedDetailArticle = article) }
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }

    fun deleteRecentSearch(q: String) {
        viewModelScope.launch {
            repository.deleteSearchQuery(q)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }
}
