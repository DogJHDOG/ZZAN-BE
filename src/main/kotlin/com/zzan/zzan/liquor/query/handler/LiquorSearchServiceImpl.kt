// src/main/kotlin/com/zzan/zzan/liquor/query/handler/LiquorSearchServiceImpl.kt
package com.zzan.zzan.liquor.query.handler

import com.zzan.zzan.api.liquor.dto.LiquorSearchResponse
import com.zzan.zzan.liquor.query.repository.LiquorSearchRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class LiquorSearchServiceImpl(
    private val liquorSearchRepository: LiquorSearchRepository
) : LiquorSearchService {

    @Cacheable("liquorSearch", key = "#keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    override fun searchLiquors(keyword: String, pageable: Pageable): Page<LiquorSearchResponse> {
        val trimmedKeyword = keyword.trim()
        return liquorSearchRepository.searchByKeyword(trimmedKeyword, pageable)
    }
}
