package com.example.data.model

data class SillokArticle(
    val id: String,                    // e.g. "kda_12512030_002"
    val title: String,                 // e.g. "세종실록 102권, 세종 25년 12월 30일 경술 2번째기사 / 훈민정음을 창제하다"
    val king: String,                  // e.g. "세종"
    val gregorianYear: Int,            // e.g. 1443
    val lunarDateStr: String,          // e.g. "세종 25년 12월 30일"
    val excerpt: String,               // 국역 요약 / 내용
    val originalText: String = "",     // 원문 (한문)
    val url: String = "https://sillok.history.go.kr/id/$id",
    val location: HistoricalLocation? = null,
    val categoryTags: List<String> = emptyList()
) {
    val displayKingAndYear: String get() = "$king (${gregorianYear}년 / $lunarDateStr)"
}
