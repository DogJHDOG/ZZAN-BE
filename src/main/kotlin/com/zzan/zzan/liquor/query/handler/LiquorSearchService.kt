// src/main/kotlin/com/zzan/zzan/liquor/query/handler/LiquorSearchService.kt
package com.zzan.zzan.liquor.query.handler

import com.zzan.zzan.api.liquor.dto.LiquorSearchResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LiquorSearchService {
    /**
     * 키워드로 전통주를 검색합니다.
     *
     * @param keyword 검색 키워드 (전통주 이름, 타입, 양조장명)
     * @param pageable 페이징 정보
     * @return Page<LiquorSearchResponse> 검색된 전통주 목록
     */
    fun searchLiquors(keyword: String, pageable: Pageable): Page<LiquorSearchResponse>
}
