package com.example.data.model

data class HistoricalLocation(
    val id: String,
    val name: String,           // e.g. "경복궁 집현전"
    val region: String,         // e.g. "한성부 (서울 종로구)"
    val latitude: Double,       // e.g. 37.5796
    val longitude: Double,      // e.g. 126.9770
    val historicalSignificance: String, // e.g. "조선 으뜸 궁궐, 훈민정음 창제 및 집현전 소재지"
    val JoseonEraName: String = name
) {
    fun getGoogleMapsUrl(): String {
        return "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
    }

    fun getGoogleMapsEmbedUrl(): String {
        return "https://maps.google.com/maps?q=$latitude,$longitude&z=15&output=embed"
    }

    fun getGeoUri(): String {
        return "geo:$latitude,$longitude?q=$latitude,$longitude($name)"
    }
}

object PredefinedLocations {
    val GYEONGBOKGUNG = HistoricalLocation(
        "gyeongbokgung", "경복궁", "한성부 (서울 종로구)", 37.5796, 126.9770,
        "조선 왕조의 으뜸 궁궐, 집현전·근정전·경회루 소재지"
    )
    val CHANGDEOKGUNG = HistoricalLocation(
        "changdeokgung", "창덕궁", "한성부 (서울 종로구)", 37.5794, 126.9910,
        "조선 국왕들이 가장 오랜 기간 거주한 유네스코 세계유산 궁궐"
    )
    val CHANGGYEONGGUNG = HistoricalLocation(
        "changgyeonggung", "창경궁", "한성부 (서울 종로구)", 37.5788, 126.9948,
        "성종 대 대비들을 위해 건립한 궁궐, 영조 대 임오화변 현장"
    )
    val DEOKSUGUNG = HistoricalLocation(
        "deoksugung", "덕수궁 (경운궁)", "한성부 (서울 중구)", 37.5658, 126.9751,
        "고종 황제가 대한제국 선포 후 거주한 황궁"
    )
    val JONGMYO = HistoricalLocation(
        "jongmyo", "종묘", "한성부 (서울 종로구)", 37.5747, 126.9940,
        "조선 역대 왕과 왕비의 신위를 모신 유교 사당"
    )
    val SUWON_HWASEONG = HistoricalLocation(
        "suwon_hwaseong", "수원화성", "경기도 수원시", 37.2858, 127.0142,
        "정조 대 정약용의 거중기로 건립한 조선 후기 첨단 성곽"
    )
    val NAMHANSANSEONG = HistoricalLocation(
        "namhansanseong", "남한산성", "경기도 광주시", 37.4791, 127.1843,
        "병자호란 당시 인조가 피난하여 항전한 산성"
    )
    val GANGHWADO = HistoricalLocation(
        "ganghwado", "강화 고려궁지 및 외규장각", "인천광역시 강화군", 37.7475, 126.4862,
        "병인양요·신미양요 현장 및 조선 왕실 의궤 보관 외규장각"
    )
    val JEONJU_GYEONGGIJEON = HistoricalLocation(
        "jeonju", "전주 경기전 및 사고", "전라북도 전주시", 35.8152, 127.1498,
        "태조 이성계 어진 모신 곳 및 조선왕조실록 보관 전주사고"
    )
    val SAMJEONDO = HistoricalLocation(
        "samjeondo", "삼전도비 (삼전도)", "서울 송파구 잠실동", 37.5080, 127.1022,
        "병자호란 후 인조가 청 태종에게 굴욕적 강화를 맺은 삼전도의 구비"
    )
    val HAENGJUSANSEONG = HistoricalLocation(
        "haengju", "행주산성", "경기도 고양시", 37.5956, 126.8172,
        "임진왜란 3대 대첩 중 하나인 권율 장군 행주대첩 현장"
    )
    val JINJUSEONG = HistoricalLocation(
        "jinjuseong", "진주성 (촉석루)", "경상남도 진주시", 35.1895, 128.0805,
        "임진왜란 김시민 장군의 진주대첩 및 논개 장사 현장"
    )
    val HANSANDO = HistoricalLocation(
        "hansando", "한산도 제승당", "경상남도 통영시", 34.7891, 128.4231,
        "이순신 장군의 한산대첩 승전지 및 삼도수군통제영 본영"
    )
    val MYEONGLYANG = HistoricalLocation(
        "myeongnyang", "울돌목 (명량)", "전라남도 진도군·해남군", 34.5683, 126.3072,
        "13척으로 133척 왜선을 격파한 이순신 장군의 명량대첩 해전지"
    )
    val NORYANG = HistoricalLocation(
        "noryang", "노량해전지", "경상남도 남해군·하동군", 34.8012, 127.8654,
        "임진왜란 최후의 해전 노량대첩 및 이순신 장군 순국지"
    )
    val PYONGYANG = HistoricalLocation(
        "pyongyang", "평양성", "평안도 평양부", 39.0392, 125.7625,
        "임진왜란 당시 조명연합군의 평양성 탈환전 현장"
    )
    val UIJU = HistoricalLocation(
        "uiju", "의주 통군정", "평안도 의주목", 40.1983, 124.5322,
        "임진왜란 시 선조 파천지 및 의주 국경 요충지"
    )
    val DOKDO = HistoricalLocation(
        "dokdo", "독도 (우산도)", "경상도 울릉도 동쪽", 37.2427, 131.8667,
        "숙종 대 안용복의 활약으로 조선 영토로 재확인된 독도"
    )

    val ALL_LOCATIONS = listOf(
        GYEONGBOKGUNG, CHANGDEOKGUNG, CHANGGYEONGGUNG, DEOKSUGUNG, JONGMYO,
        SUWON_HWASEONG, NAMHANSANSEONG, GANGHWADO, JEONJU_GYEONGGIJEON,
        SAMJEONDO, HAENGJUSANSEONG, JINJUSEONG, HANSANDO, MYEONGLYANG,
        NORYANG, PYONGYANG, UIJU, DOKDO
    )

    fun findMatchingLocation(text: String): HistoricalLocation? {
        return ALL_LOCATIONS.firstOrNull { loc ->
            text.contains(loc.name) || text.contains(loc.JoseonEraName) ||
                    loc.name.split(" ").any { part -> part.length >= 2 && text.contains(part) }
        } ?: when {
            text.contains("집현전") || text.contains("근정전") || text.contains("경회루") -> GYEONGBOKGUNG
            text.contains("규장각") || text.contains("인정전") -> CHANGDEOKGUNG
            text.contains("임오화변") || text.contains("사도세자") -> CHANGGYEONGGUNG
            text.contains("화성") || text.contains("수원") || text.contains("거중기") -> SUWON_HWASEONG
            text.contains("병자호란") || text.contains("남한산성") -> NAMHANSANSEONG
            text.contains("삼전도") -> SAMJEONDO
            text.contains("강화") || text.contains("외규장각") -> GANGHWADO
            text.contains("전주") || text.contains("실록전") || text.contains("사고") -> JEONJU_GYEONGGIJEON
            text.contains("이순신") || text.contains("한산") -> HANSANDO
            text.contains("명량") || text.contains("울돌목") -> MYEONGLYANG
            text.contains("노량") -> NORYANG
            text.contains("독도") || text.contains("우산도") || text.contains("안용복") -> DOKDO
            text.contains("한양") || text.contains("도성") -> GYEONGBOKGUNG
            else -> null
        }
    }
}
