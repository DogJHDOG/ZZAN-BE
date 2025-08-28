// src/test/kotlin/com/zzan/zzan/feed/command/service/FeedCommandServiceImplTest.kt
package com.zzan.zzan.feed.command.service

import com.zzan.zzan.api.feed.dto.*
import com.zzan.zzan.common.annotation.UnitTest
import com.zzan.zzan.common.exception.CustomException
import com.zzan.zzan.feed.command.domain.Feed
import com.zzan.zzan.feed.command.domain.FeedImage
import com.zzan.zzan.feed.command.repository.FeedImageRepository
import com.zzan.zzan.feed.command.repository.FeedRepository
import com.zzan.zzan.place.command.repository.PlaceRepository
import com.zzan.zzan.user.command.repository.UserRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import kotlin.test.assertEquals

@UnitTest
class FeedCommandServiceImplTest {

    private lateinit var feedRepository: FeedRepository
    private lateinit var feedImageRepository: FeedImageRepository
    private lateinit var userRepository: UserRepository
    private lateinit var placeRepository: PlaceRepository
    private lateinit var eventPublisher: ApplicationEventPublisher
    private lateinit var feedCommandService: FeedCommandServiceImpl

    @BeforeEach
    fun setUp() {
        feedRepository = mockk()
        feedImageRepository = mockk()
        userRepository = mockk()
        placeRepository = mockk()
        eventPublisher = mockk()

        feedCommandService = FeedCommandServiceImpl(
            feedRepository,
            feedImageRepository,
            userRepository,
            placeRepository,
            eventPublisher
        )
    }

    @Test
    fun `피드 생성 성공 테스트`() {
        // Given
        val request = CreateFeedRequest(
            userId = "user123",
            imageUrl = "https://example.com/image.jpg",
            score = 4.5,
            text = "맛있는 전통주!",
            placeId = "place123",
            buyPlaceId = null,
            images = listOf(
                FeedImageRequest("https://example.com/image1.jpg", 1)
            )
        )

        // Mock 설정
        every { userRepository.existsByIdAndDeletedAtIsNull("user123") } returns true
        every { placeRepository.existsById("place123") } returns true
        every { feedRepository.save(any<Feed>()) } answers { firstArg() }
        every { feedImageRepository.saveAll(any<List<FeedImage>>()) } returns emptyList()
        every { eventPublisher.publishEvent(any()) } just Runs

        // When
        val result = feedCommandService.createFeed(request)

        // Then
        assertEquals(26, result.length) // ULID는 26자리
        verify(exactly = 1) { userRepository.existsByIdAndDeletedAtIsNull("user123") }
        verify(exactly = 1) { placeRepository.existsById("place123") }
        verify(exactly = 1) { feedRepository.save(any<Feed>()) }
        verify(exactly = 1) { feedImageRepository.saveAll(any<List<FeedImage>>()) }
        verify(exactly = 1) { eventPublisher.publishEvent(any()) }
    }

    @Test
    fun `존재하지 않는 사용자로 피드 생성시 예외 발생`() {
        // Given
        val request = CreateFeedRequest(
            userId = "nonexistent",
            imageUrl = "https://example.com/image.jpg",
            score = 4.5,
            text = "테스트",
            placeId = "place123",
            buyPlaceId = null,
            images = listOf(FeedImageRequest("https://example.com/image.jpg", 1))
        )

        every { userRepository.existsByIdAndDeletedAtIsNull("nonexistent") } returns false

        // When & Then
        val exception = assertThrows<CustomException> {
            feedCommandService.createFeed(request)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.status)
        assertEquals("존재하지 않는 사용자입니다.", exception.message)

        verify(exactly = 1) { userRepository.existsByIdAndDeletedAtIsNull("nonexistent") }
        verify(exactly = 0) { feedRepository.save(any<Feed>()) }
    }

    @Test
    fun `피드 수정 성공 테스트`() {
        // Given
        val feedId = "feed123"
        val updateRequest = UpdateFeedRequest(
            score = 5.0,
            text = "수정된 내용",
            buyPlaceId = "buyPlace123"
        )

        val existingFeed = Feed(
            id = feedId,
            userId = "user123",
            imageUrl = "https://example.com/image.jpg",
            score = 4.0,
            text = "원본 내용",
            placeId = "place123",
            buyPlaceId = null,
            createdAt = LocalDateTime.now()
        )

        every { feedRepository.findByIdAndDeletedAtIsNull(feedId) } returns existingFeed
        every { placeRepository.existsById("buyPlace123") } returns true
        every { feedRepository.save(any<Feed>()) } answers { firstArg() }
        every { eventPublisher.publishEvent(any()) } just Runs

        // When
        feedCommandService.updateFeed(feedId, updateRequest)

        // Then
        verify(exactly = 1) { feedRepository.findByIdAndDeletedAtIsNull(feedId) }
        verify(exactly = 1) { placeRepository.existsById("buyPlace123") }
        verify(exactly = 1) { feedRepository.save(any<Feed>()) }
        verify(exactly = 1) { eventPublisher.publishEvent(any()) }
    }

    @Test
    fun `피드 삭제 성공 테스트`() {
        // Given
        val feedId = "feed123"
        val existingFeed = Feed(
            id = feedId,
            userId = "user123",
            imageUrl = "https://example.com/image.jpg",
            score = 4.0,
            text = "테스트 피드",
            placeId = "place123",
            createdAt = LocalDateTime.now()
        )

        every { feedRepository.findByIdAndDeletedAtIsNull(feedId) } returns existingFeed
        every { feedRepository.save(any<Feed>()) } answers { firstArg() }
        every { feedImageRepository.deleteByFeedId(feedId) } just Runs
        every { eventPublisher.publishEvent(any()) } just Runs

        // When
        feedCommandService.deleteFeed(feedId)

        // Then
        verify(exactly = 1) { feedRepository.findByIdAndDeletedAtIsNull(feedId) }
        verify(exactly = 1) { feedRepository.save(any<Feed>()) }
        verify(exactly = 1) { feedImageRepository.deleteByFeedId(feedId) }
        verify(exactly = 1) { eventPublisher.publishEvent(any()) }
    }
}
