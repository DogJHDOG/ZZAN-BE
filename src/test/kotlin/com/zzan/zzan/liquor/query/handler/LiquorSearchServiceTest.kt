// src/test/kotlin/com/zzan/zzan/liquor/query/handler/LiquorSearchServiceTest.kt
package com.zzan.zzan.liquor.query.handler

import com.zzan.zzan.api.liquor.dto.LiquorSearchResponse
import com.zzan.zzan.common.annotation.UnitTest
import com.zzan.zzan.liquor.query.repository.LiquorSearchRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@UnitTest
class LiquorSearchServiceTest {

    private val liquorSearchRepository = mockk<LiquorSearchRepository>()
    private val liquorSearchService = LiquorSearchServiceImpl(liquorSearchRepository)

    @Test
    fun `전통주 이름으로 검색 성공 테스트`() {
        // Given
        val keyword = "참이슬"
        val pageRequest = PageRequest.of(0, 10)
        val expectedResults = listOf(
            LiquorSearchResponse(
                id = "liquor-001",
                name = "참이슬",
                type = "증류주",
                brewery = "하이트진로",
                imageUrl = "https://example.com/chamisul.jpg"
            )
        )
        val pageResult = PageImpl(expectedResults, pageRequest, 1)

        every { liquorSearchRepository.searchByKeyword(keyword, pageRequest) } returns pageResult

        // When
        val result = liquorSearchService.searchLiquors(keyword, pageRequest)

        // Then
        assertEquals(1, result.totalElements)
        assertEquals("참이슬", result.content[0].name)
        verify(exactly = 1) { liquorSearchRepository.searchByKeyword(keyword, pageRequest) }
    }

    @Test
    fun `빈 키워드로 검색시 전체 목록 반환 테스트`() {
        // Given
        val keyword = ""
        val pageRequest = PageRequest.of(0, 10)
        val allResults = listOf(
            LiquorSearchResponse("liquor-001", "참이슬", "증류주", "하이트진로", null),
            LiquorSearchResponse("liquor-002", "막걸리", "탁주", "국순당", null)
        )
        val pageResult = PageImpl(allResults, pageRequest, 2)

        every { liquorSearchRepository.searchByKeyword("", pageRequest) } returns pageResult

        // When
        val result = liquorSearchService.searchLiquors(keyword, pageRequest)

        // Then
        assertEquals(2, result.totalElements)
        verify(exactly = 1) { liquorSearchRepository.searchByKeyword("", pageRequest) }
    }

    @Test
    fun `검색 결과 없음 테스트`() {
        // Given
        val keyword = "존재하지않는술"
        val pageRequest = PageRequest.of(0, 10)
        val emptyResult = PageImpl<LiquorSearchResponse>(emptyList(), pageRequest, 0)

        every { liquorSearchRepository.searchByKeyword(keyword, pageRequest) } returns emptyResult

        // When
        val result = liquorSearchService.searchLiquors(keyword, pageRequest)

        // Then
        assertEquals(0, result.totalElements)
        assertTrue(result.content.isEmpty())
        verify(exactly = 1) { liquorSearchRepository.searchByKeyword(keyword, pageRequest) }
    }
}
