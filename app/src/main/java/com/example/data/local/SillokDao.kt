package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SillokDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :articleId)")
    fun isBookmarkedFlow(articleId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :articleId)")
    suspend fun isBookmarked(articleId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :articleId")
    suspend fun deleteBookmarkById(articleId: String)

    // Search History
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 15")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearchQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
}
