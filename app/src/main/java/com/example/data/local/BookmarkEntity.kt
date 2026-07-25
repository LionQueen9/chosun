package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.HistoricalLocation
import com.example.data.model.SillokArticle

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val title: String,
    val king: String,
    val gregorianYear: Int,
    val lunarDateStr: String,
    val excerpt: String,
    val originalText: String,
    val url: String,
    val locationName: String?,
    val locationLat: Double?,
    val locationLng: Double?,
    val locationRegion: String?,
    val locationSignificance: String?,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toSillokArticle(): SillokArticle {
        val loc = if (locationName != null && locationLat != null && locationLng != null) {
            HistoricalLocation(
                id = "loc_${id}",
                name = locationName,
                region = locationRegion ?: "",
                latitude = locationLat,
                longitude = locationLng,
                historicalSignificance = locationSignificance ?: ""
            )
        } else null

        return SillokArticle(
            id = id,
            title = title,
            king = king,
            gregorianYear = gregorianYear,
            lunarDateStr = lunarDateStr,
            excerpt = excerpt,
            originalText = originalText,
            url = url,
            location = loc
        )
    }

    companion object {
        fun fromSillokArticle(article: SillokArticle): BookmarkEntity {
            return BookmarkEntity(
                id = article.id,
                title = article.title,
                king = article.king,
                gregorianYear = article.gregorianYear,
                lunarDateStr = article.lunarDateStr,
                excerpt = article.excerpt,
                originalText = article.originalText,
                url = article.url,
                locationName = article.location?.name,
                locationLat = article.location?.latitude,
                locationLng = article.location?.longitude,
                locationRegion = article.location?.region,
                locationSignificance = article.location?.historicalSignificance
            )
        }
    }
}
