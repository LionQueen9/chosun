package com.example.data.model

data class JoseonKing(
    val id: String,
    val name: String,         // e.g. "세종"
    val sillokTitle: String, // e.g. "세종실록"
    val startYear: Int,       // e.g. 1418
    val endYear: Int,         // e.g. 1450
    val reignNumber: Int,     // e.g. 4 (4대)
    val description: String   // e.g. "훈민정음 창제, 집현전 설치, 과학기술 발전"
) {
    val displayRange: String get() = "$startYear - $endYear (${reignNumber}대)"
}

object JoseonKings {
    val ALL_KINGS = listOf(
        JoseonKing("taejo", "태조", "태조실록", 1392, 1398, 1, "조선 개국, 한양 도읍 지정"),
        JoseonKing("jeongjong", "정종", "정종실록", 1398, 1400, 2, "개경 환도, 도평의사사 개편"),
        JoseonKing("taejong", "태종", "태종실록", 1400, 1418, 3, "6조 직계제, 신문고 설치, 한양 재도읍"),
        JoseonKing("sejong", "세종", "세종실록", 1418, 1450, 4, "훈민정음 창제, 집현전 확장, 4군 6진 개척"),
        JoseonKing("munjong", "문종", "문종실록", 1450, 1452, 5, "동국병감 편찬, 화차 제작"),
        JoseonKing("danjong", "단종", "단종실록", 1452, 1455, 6, "계유정난으로 수양대군에 상위"),
        JoseonKing("seojo", "세조", "세조실록", 1455, 1468, 7, "직전법 실시, 경국대전 착수, 이시애의 난"),
        JoseonKing("yeojong", "예종", "예종실록", 1468, 1469, 8, "삼포개항, 남이의 옥"),
        JoseonKing("seongjong", "성종", "성종실록", 1469, 1494, 9, "경국대전 완성, 홍문관 설치, 사림파 등용"),
        JoseonKing("yeonsangun", "연산군", "연산군일기", 1494, 1506, 10, "무오사화, 갑자사화, 중종반정으로 폐위"),
        JoseonKing("jungjong", "중종", "중종실록", 1506, 1544, 11, "조광조 개혁정치, 기묘사화, 삼포왜란"),
        JoseonKing("injong", "인종", "인종실록", 1544, 1545, 12, "조광조 복권, 동궁의 화재"),
        JoseonKing("myeongjong", "명종", "명종실록", 1545, 1567, 13, "을사사화, 임꺽정의 난, 을묘왜변"),
        JoseonKing("seonjo", "선조", "선조실록", 1567, 1608, 14, "임진왜란, 정유재란, 붕당정치 시작"),
        JoseonKing("gwanghaegun", "광해군", "광해군일기", 1608, 1623, 15, "중립외교, 동의보감 완성, 인조반정"),
        JoseonKing("injo", "인조", "인조실록", 1623, 1649, 16, "정묘호란, 병자호란, 삼전도의 굴욕"),
        JoseonKing("hyojong", "효종", "효종실록", 1649, 1659, 17, "북벌론 추진, 나선정벌 가담"),
        JoseonKing("hyeonjong", "현종", "현종실록", 1659, 1674, 18, "예송논쟁 (기해예송, 갑인예송)"),
        JoseonKing("sukjong", "숙종", "숙종실록", 1674, 1720, 19, "환국정치(경신·기사·갑술), 대동법 전국 확대, 독도 조선영토 확인"),
        JoseonKing("gyeongjong", "경종", "경종실록", 1720, 1724, 20, "신임사화, 연잉군(영조) 세제 책봉"),
        JoseonKing("yeongjo", "영조", "영조실록", 1724, 1776, 21, "탕평책, 균역법, 속대전, 임오화변(사도세자)"),
        JoseonKing("jeongjo", "정조", "정조실록", 1776, 1800, 22, "규장각 강화, 수원화성 축조, 장용영, 대전통편"),
        JoseonKing("sunjo", "순조", "순조실록", 1800, 1834, 23, "세도정치 시작, 홍경래의 난, 신유박해"),
        JoseonKing("heonjong", "헌종", "헌종실록", 1834, 1849, 24, "풍양 조씨 세도정치, 기해박해"),
        JoseonKing("cheoljong", "철종", "철종실록", 1849, 1863, 25, "안동 김씨 세도정치, 임술농민봉기"),
        JoseonKing("gojong", "고종", "고종실록", 1863, 1907, 26, "흥선대원군 집권, 강화도조약, 대한제국 선포"),
        JoseonKing("sunjong", "순종", "순종실록", 1907, 1910, 27, "대한제국 마지막 황제, 한일병합")
    )

    fun findByName(name: String): JoseonKing? {
        val cleanName = name.trim().removeSuffix("실록").removeSuffix("일기")
        return ALL_KINGS.firstOrNull { it.name == cleanName }
    }
}
