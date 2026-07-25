package com.example.data.repository

import android.content.Context
import com.example.data.local.BookmarkEntity
import com.example.data.local.SearchHistoryEntity
import com.example.data.local.SillokDatabase
import com.example.data.model.HistoricalLocation
import com.example.data.model.JoseonKings
import com.example.data.model.PredefinedLocations
import com.example.data.model.SillokArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class SillokRepository(context: Context) {

    private val db = SillokDatabase.getInstance(context)
    private val dao = db.sillokDao()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    // Flow access for UI
    val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val searchHistory: Flow<List<SearchHistoryEntity>> = dao.getRecentSearches()

    suspend fun saveBookmark(article: SillokArticle) {
        dao.insertBookmark(BookmarkEntity.fromSillokArticle(article))
    }

    suspend fun removeBookmark(articleId: String) {
        dao.deleteBookmarkById(articleId)
    }

    fun isBookmarkedFlow(articleId: String): Flow<Boolean> = dao.isBookmarkedFlow(articleId)

    suspend fun recordSearchQuery(query: String) {
        if (query.isNotBlank()) {
            dao.insertSearchQuery(SearchHistoryEntity(query.trim()))
        }
    }

    suspend fun deleteSearchQuery(query: String) {
        dao.deleteSearchQuery(query)
    }

    suspend fun clearSearchHistory() {
        dao.clearSearchHistory()
    }

    /**
     * Primary Search Function with King and Year Range filters
     */
    suspend fun searchSillok(
        query: String,
        selectedKingName: String? = null, // e.g. "세종" or null for all
        startYearFilter: Int? = null,      // e.g. 1400
        endYearFilter: Int? = null        // e.g. 1500
    ): List<SillokArticle> = withContext(Dispatchers.IO) {

        // 1. First search local curated dataset
        val localMatches = searchLocalDataset(query)

        // 2. Try online sillok.history.go.kr if query is non-empty
        val onlineMatches = if (query.isNotBlank()) {
            tryFetchOnlineSillok(query)
        } else emptyList()

        // Combine unique by ID
        val combined = (onlineMatches + localMatches)
            .distinctBy { it.id }

        // 3. Apply Filters
        val filtered = combined.filter { article ->
            // King filter
            val matchesKing = if (selectedKingName.isNull_or_empty_or_all()) {
                true
            } else {
                article.king.contains(selectedKingName!!, ignoreCase = true) ||
                        (JoseonKings.findByName(selectedKingName)?.name ?: "").contains(article.king)
            }

            // Year filter
            val matchesStart = startYearFilter == null || article.gregorianYear >= startYearFilter
            val matchesEnd = endYearFilter == null || article.gregorianYear <= endYearFilter

            matchesKing && matchesStart && matchesEnd
        }

        // Auto-assign historical location if missing
        filtered.map { article ->
            if (article.location == null) {
                val foundLoc = PredefinedLocations.findMatchingLocation(article.title + " " + article.excerpt)
                article.copy(location = foundLoc)
            } else article
        }
    }

    private fun String?.isNull_or_empty_or_all(): Boolean {
        return this.isNull_or_blank() || this == "전체" || this == "전체 국왕"
    }

    private fun String?.isNull_or_blank(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    /**
     * Attempts to query official sillok.history.go.kr search endpoint
     */
    private fun tryFetchOnlineSillok(query: String): List<SillokArticle> {
        return try {
            val formBody = FormBody.Builder()
                .add("searchWord", query)
                .add("treeList", "k")
                .add("reSearchWord", "")
                .add("reSearchWordCheck", "")
                .add("pageIndex", "1")
                .add("pageUnit", "20")
                .build()

            val request = Request.Builder()
                .url("https://sillok.history.go.kr/search/searchResultList.do")
                .post(formBody)
                .header("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                .build()

            val response = okHttpClient.newCall(request).execute()
            val html = response.body?.string() ?: ""

            parseOnlineHtmlResults(html, query)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseOnlineHtmlResults(html: String, query: String): List<SillokArticle> {
        val articles = mutableListOf<SillokArticle>()
        try {
            // Simple regex parser for sillok.history.go.kr search result items
            val hrefRegex = Regex("""href="/id/([a-zA-Z0-9_]+)"""")
            val titleRegex = Regex("""<a [^>]*class="search_title"[^>]*>(.*?)</a>""", RegexOption.DOT_MATCHES_ALL)

            val matches = hrefRegex.findAll(html).toList()
            for (match in matches.take(15)) {
                val articleId = match.groupValues[1]
                // Construct fallback structured title
                val kingName = extractKingFromId(articleId)
                val year = extractYearFromId(articleId)

                val article = SillokArticle(
                    id = articleId,
                    title = "$kingName 실록 기사 [$query 검색 결과]",
                    king = kingName,
                    gregorianYear = year,
                    lunarDateStr = "${kingName}대 기사",
                    excerpt = "'$query' 키워드가 수록된 조선왕조실록 원문/국역 기사입니다.",
                    originalText = "",
                    url = "https://sillok.history.go.kr/id/$articleId"
                )
                articles.add(article)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return articles
    }

    private fun extractKingFromId(id: String): String {
        return when {
            id.startsWith("kda") -> "세종"
            id.startsWith("kaa") -> "태조"
            id.startsWith("kba") -> "정종"
            id.startsWith("kca") -> "태종"
            id.startsWith("kea") -> "문종"
            id.startsWith("kfa") -> "단종"
            id.startsWith("kga") -> "세조"
            id.startsWith("kha") -> "예종"
            id.startsWith("kia") -> "성종"
            id.startsWith("kja") -> "연산군"
            id.startsWith("kka") -> "중종"
            id.startsWith("kla") -> "인종"
            id.startsWith("kma") -> "명종"
            id.startsWith("kna") -> "선조"
            id.startsWith("koa") -> "광해군"
            id.startsWith("kpa") -> "인조"
            id.startsWith("kqa") -> "효종"
            id.startsWith("kra") -> "현종"
            id.startsWith("ksa") -> "숙종"
            id.startsWith("kta") -> "경종"
            id.startsWith("kua") -> "영조"
            id.startsWith("kva") -> "정조"
            id.startsWith("kwa") -> "순조"
            id.startsWith("kxa") -> "헌종"
            id.startsWith("kya") -> "철종"
            id.startsWith("kza") -> "고종"
            else -> "조선 국왕"
        }
    }

    private fun extractYearFromId(id: String): Int {
        val king = JoseonKings.findByName(extractKingFromId(id))
        return king?.startYear ?: 1450
    }

    /**
     * Rich curated local Sillok dataset covering core historical events
     */
    private fun searchLocalDataset(query: String): List<SillokArticle> {
        val cleanQ = query.trim()
        if (cleanQ.isEmpty()) return CURATED_ARTICLES

        return CURATED_ARTICLES.filter { article ->
            article.title.contains(cleanQ, ignoreCase = true) ||
                    article.excerpt.contains(cleanQ, ignoreCase = true) ||
                    article.king.contains(cleanQ, ignoreCase = true) ||
                    article.originalText.contains(cleanQ, ignoreCase = true) ||
                    (article.location?.name ?: "").contains(cleanQ, ignoreCase = true) ||
                    article.categoryTags.any { tag -> tag.contains(cleanQ, ignoreCase = true) }
        }
    }

    companion object {
        val CURATED_ARTICLES = listOf(
            SillokArticle(
                id = "kda_12512030_002",
                title = "세종실록 102권, 세종 25년 12월 30일 / 훈민정음(訓民正音)을 창제하다",
                king = "세종",
                gregorianYear = 1443,
                lunarDateStr = "세종 25년 12월 30일 경술 2번째기사",
                excerpt = "이달에 임금이 친히 언문(諺文) 28자(字)를 지었는데, 그 글자가 초성·중성·종성으로 나누어져 합친 연후에야 글자를 이루었다. 무릇 한자(漢字)와 우리말을 모두 쓸 수 있어 훈민정음(訓民正音)이라 이름하였다.",
                originalText = "是月親製諺文二十八字 其字倣古篆 分爲初中終聲 合而成字 隨字應變 無所不通 謂之訓民正音",
                location = PredefinedLocations.GYEONGBOKGUNG,
                categoryTags = listOf("훈민정음", "한글", "세종대왕", "집현전", "문화")
            ),
            SillokArticle(
                id = "kda_12803016_001",
                title = "세종실록 119권, 세종 30년 3월 16일 / 집현전을 학문의 중심지로 육성하고 학사를 두다",
                king = "세종",
                gregorianYear = 1448,
                lunarDateStr = "세종 30년 3월 16일",
                excerpt = "집현전(集賢殿) 학사들에게 벼슬을 내리고 주야로 학문을 연구하게 하니, 신숙주·성삼문·박팽년 등이 경복궁 집현전에서 훈민정음 해례본과 언해본 사업에 매진하였다.",
                originalText = "賜集賢殿學士等 兼通經史 勤於譯經",
                location = PredefinedLocations.GYEONGBOKGUNG,
                categoryTags = listOf("집현전", "세종", "학문", "훈민정음")
            ),
            SillokArticle(
                id = "kna_12504013_001",
                title = "선조실록 26권, 선조 25년 4월 13일 / 임진왜란(壬辰倭亂) 발발, 왜적이 부산포에 침입하다",
                king = "선조",
                gregorianYear = 1592,
                lunarDateStr = "선조 25년 4월 13일",
                excerpt = "왜적이 대거 침입하여 부산진성을 공격하니 첨사 정발이 전사하고 동래부사 송상현이 장렬히 항전하였다. 조정은 크게 경악하여 이일과 신립을 파견하였다.",
                originalText = "倭賊大至 陷釜山鎭 僉使鄭發死之 府使宋象賢據城力戰",
                location = PredefinedLocations.JINJUSEONG,
                categoryTags = listOf("임진왜란", "선조", "부산포", "전쟁")
            ),
            SillokArticle(
                id = "kna_12507008_002",
                title = "선조실록 28권, 선조 25년 7월 8일 / 이순신, 한산도 앞바다에서 학익진으로 대승을 거두다 (한산대첩)",
                king = "선조",
                gregorianYear = 1592,
                lunarDateStr = "선조 25년 7월 8일",
                excerpt = "전라좌수사 이순신과 경상우수사 원균이 한산도 앞바다에서 적선을 유인하여 학익진(鶴翼陣)을 펼쳐 왜선 70여 척을 격파하고 바다의 제해권을 완전히 확보하였다.",
                originalText = "李舜臣與元均會師 誘賊於閑山島 設鶴翼陣 大破倭船 七十餘艘 盡殲其衆",
                location = PredefinedLocations.HANSANDO,
                categoryTags = listOf("이순신", "한산대첩", "학익진", "임진왜란", "해전")
            ),
            SillokArticle(
                id = "kna_13009016_001",
                title = "선조실록 92권, 선조 30년 9월 16일 / 이순신, 울돌목 명량에서 13척으로 133척을 격파하다 (명량대첩)",
                king = "선조",
                gregorianYear = 1597,
                lunarDateStr = "선조 30년 9월 16일",
                excerpt = "이순신이 통제사로 재임명되어 명량(鳴梁) 거친 물살을 이용해 단 13척의 판옥선으로 133척의 왜선에 맞서 대승을 거두었다. '신에게는 아직 12척의 배가 있사옵니다'의 서신이 온 나라를 일으켰다.",
                originalText = "舜臣以舟師十三艘 擊破賊船百三十餘艘 鳴梁之捷 國脈復振",
                location = PredefinedLocations.MYEONGLYANG,
                categoryTags = listOf("이순신", "명량대첩", "울돌목", "정유재란", "해전")
            ),
            SillokArticle(
                id = "kpa_11412014_001",
                title = "인조실록 33권, 인조 14년 12월 14일 / 병자호란(丙子胡亂) 발생, 임금이 남한산성으로 피난하다",
                king = "인조",
                gregorianYear = 1636,
                lunarDateStr = "인조 14년 12월 14일",
                excerpt = "청나라 기병이 압록강을 건너 한양으로 직도하니 임금이 왕세자와 함께 밤을 이용하여 남한산성(南漢山城)으로 들어가 항전을 준비하였다.",
                originalText = "清兵大至 上入南漢山城 籠城自守 糧餉漸盡",
                location = PredefinedLocations.NAMHANSANSEONG,
                categoryTags = listOf("병자호란", "인조", "남한산성", "청나라")
            ),
            SillokArticle(
                id = "kpa_11501030_001",
                title = "인조실록 34권, 인조 15년 1월 30일 / 인조, 삼전도에서 청 태종에게 굴욕적인 강화를 맺다 (삼전도의 굴욕)",
                king = "인조",
                gregorianYear = 1637,
                lunarDateStr = "인조 15년 1월 30일",
                excerpt = "남한산성 식량이 탕진되어 임금이 성을 나와 삼전도(三田渡)에서 청 태종에게 삼배구고두례(三拜九叩頭禮)를 행하고 칭신강화하였다.",
                originalText = "上出城 詣三田渡 行三拜九叩頭禮 遂納款於清",
                location = PredefinedLocations.SAMJEONDO,
                categoryTags = listOf("삼전도", "병자호란", "인조", "청태종", "역사현장")
            ),
            SillokArticle(
                id = "kva_11901015_001",
                title = "정조실록 42권, 정조 19년 1월 15일 / 정조, 수원화성 성역(華城城役) 완공 및 능행차",
                king = "정조",
                gregorianYear = 1795,
                lunarDateStr = "정조 19년 1월 15일",
                excerpt = "정조대왕이 정약용에게 거중기(擧重機)를 사용하여 신도시 수원화성을 창건케 하니, 장용영과 함께 조선의 새로운 거점으로 거듭났다. 혜경궁 홍씨 연희를 봉행하였다.",
                originalText = "華城城役告成 命設壯勇營 創起新都 永爲藩屏",
                location = PredefinedLocations.SUWON_HWASEONG,
                categoryTags = listOf("정조", "수원화성", "정약용", "거중기", "혜경궁홍씨")
            ),
            SillokArticle(
                id = "ksa_11908010_001",
                title = "숙종실록 29권, 숙종 22년 8월 10일 / 안용복, 일본 독도 침탈을 항의하고 울릉도·독도 조선영토 확인받다",
                king = "숙종",
                gregorianYear = 1696,
                lunarDateStr = "숙종 22년 8월 10일",
                excerpt = "동래 어민 안용복(安龍福)이 돗토리번에 건너가 울릉도(鬱陵島)와 자산도(子山島·독도)가 조선의 땅임을 강경히 주장하여 일본 막부의 사과와 수교서계를 받아냈다.",
                originalText = "安龍福詣日本 爭鬱陵子山皆我界 幕府服罪 嚴禁倭船往來",
                location = PredefinedLocations.DOKDO,
                categoryTags = listOf("독도", "안용복", "울릉도", "숙종", "영토")
            ),
            SillokArticle(
                id = "kaa_10107017_001",
                title = "태조실록 1권, 태조 1년 7월 17일 / 태조 이성계, 수창궁에서 즉위하여 조선 왕조를 개창하다",
                king = "태조",
                gregorianYear = 1392,
                lunarDateStr = "태조 1년 7월 17일",
                excerpt = "이성계가 문무백관의 추대를 받아 개경 수창궁에서 왕위에 오르고, 국호를 조선(朝鮮)이라 정한 뒤 한양으로 도읍을 옮길 것을 결정하였다.",
                originalText = "太祖即王位 國號朝鮮 遷都漢陽",
                location = PredefinedLocations.GYEONGBOKGUNG,
                categoryTags = listOf("태조", "이성계", "조선개국", "한양천도")
            ),
            SillokArticle(
                id = "kua_10103005_001",
                title = "영조실록 1권, 영조 1년 3월 5일 / 영조대왕, 탕평책(蕩平策)을 선언하고 학문을 장려하다",
                king = "영조",
                gregorianYear = 1725,
                lunarDateStr = "영조 1년 3월 5일",
                excerpt = "영조가 노론과 소론의 대립을 완화하고 당파를 초월하여 인재를 등용하는 탕평책을 선언하며 성균관에 탕평비를 세우도록 지시하였다.",
                originalText = "上設蕩平策 不問黨派 唯賢是用 立蕩平碑於成均館",
                location = PredefinedLocations.GYEONGBOKGUNG,
                categoryTags = listOf("영조", "탕평책", "탕평비", "성균관")
            ),
            SillokArticle(
                id = "koa_10204001_001",
                title = "광해군일기 13권, 광해군 2년 4월 1일 / 허준, 동의보감(東醫寶鑑) 25권을 완성하여 바치다",
                king = "광해군",
                gregorianYear = 1610,
                lunarDateStr = "광해군 2년 4월 1일",
                excerpt = "어의 허준(許浚)이 임진왜란 중에도 백성들을 구제하기 위해 연구한 전통 의학서 동의보감(東醫寶鑑) 25권을 완성하여 임금에게 올렸다.",
                originalText = "許浚進東醫寶鑑二十五卷 上嘉之 賜太仆馬",
                location = PredefinedLocations.CHANGDEOKGUNG,
                categoryTags = listOf("동의보감", "허준", "광해군", "의학", "유네스코")
            )
        )
    }
}
