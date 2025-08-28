
// src/test/kotlin/com/zzan/zzan/api/liquor/LiquorSearchControllerTest.kt
package com.zzan.zzan.api.liquor

import com.zzan.zzan.api.liquor.dto.LiquorSearchResponse
import com.zzan.zzan.common.annotation.UnitTest
import com.zzan.zzan.liquor.query.handler.LiquorSearchService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@UnitTest
@WebMvcTest(LiquorController::class)
@WithMockUser
class LiquorSearchControllerTest {

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun mockLiquorSearchService(): LiquorSearchService = mockk()
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var liquorSearchService: LiquorSearchService

    @Test
    fun `전통주 검색 API 성공 테스트`() {
        // Given
        val keyword = "참이슬"
        val searchResults = listOf(
            LiquorSearchResponse(
                id = "liquor-001",
                name = "참이슬",
                type = "증류주",
                brewery = "하이트진로",
                imageUrl = "https://example.com/chamisul.jpg"
            )
        )
        val pageResult = PageImpl(searchResults, PageRequest.of(0, 10), 1)

        every {
            liquorSearchService.searchLiquors(keyword, any())
        } returns pageResult

        // When & Then
        mockMvc.perform(
            get("/api/liquors/search")
                .param("q", keyword)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray)
            .andExpect(jsonPath("$.data.content[0].id").value("liquor-001"))
            .andExpect(jsonPath("$.data.content[0].name").value("참이슬"))
            .andExpect(jsonPath("$.data.totalElements").value(1))
    }
}
