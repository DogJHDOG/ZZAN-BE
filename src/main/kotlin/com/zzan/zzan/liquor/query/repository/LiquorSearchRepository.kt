// src/main/kotlin/com/zzan/zzan/liquor/query/repository/LiquorSearchRepository.kt
package com.zzan.zzan.liquor.query.repository

import com.zzan.zzan.api.liquor.dto.LiquorSearchResponse
import com.zzan.zzan.liquor.command.domain.Liquor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LiquorSearchRepository : JpaRepository<Liquor, String> {

    /**
     * 키워드로 전통주 검색 (이름, 타입, 양조장명 포함)
     */
    @Query("""
        SELECT new com.zzan.zzan.api.liquor.dto.LiquorSearchResponse(
            l.id, l.name, l.type, l.brewery, l.imageUrl
        )
        FROM Liquor l 
        WHERE (:keyword = '' OR 
               LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(l.type) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(l.brewery) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY l.name
    """)
    fun searchByKeyword(@Param("keyword") keyword: String, pageable: Pageable): Page<LiquorSearchResponse>
}
