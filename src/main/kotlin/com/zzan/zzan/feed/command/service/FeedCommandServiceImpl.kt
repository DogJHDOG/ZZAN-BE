package com.zzan.zzan.feed.command.service

import com.zzan.zzan.api.feed.dto.*
import com.zzan.zzan.common.exception.CustomException
import com.zzan.zzan.feed.command.domain.Feed
import com.zzan.zzan.feed.command.domain.FeedImage
import com.zzan.zzan.feed.command.event.FeedCreatedEvent
import com.zzan.zzan.feed.command.event.FeedUpdatedEvent
import com.zzan.zzan.feed.command.event.FeedDeletedEvent
import com.zzan.zzan.feed.command.repository.FeedImageRepository
import com.zzan.zzan.feed.command.repository.FeedRepository
import com.zzan.zzan.place.command.repository.PlaceRepository
import com.zzan.zzan.user.command.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class FeedCommandServiceImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val eventPublisher: ApplicationEventPublisher
) : FeedCommandService {

    override fun createFeed(request: CreateFeedRequest): String {
        // 1. 유효성 검사
        validateFeedCreation(request)

        // 2. Feed 엔티티 생성 및 저장
        val feed = Feed(
            userId = request.userId,
            imageUrl = request.imageUrl,
            score = request.score,
            text = request.text,
            placeId = request.placeId,
            buyPlaceId = request.buyPlaceId
        )

        val savedFeed = feedRepository.save(feed)

        // 3. FeedImage 엔티티들 생성 및 저장
        val feedImages = request.images.map { imageRequest ->
            FeedImage(
                feedId = savedFeed.id,
                imageUrl = imageRequest.imageUrl,
                orderNum = imageRequest.orderNum
            )
        }
        feedImageRepository.saveAll(feedImages)

        // 4. 이벤트 발행 (Query Side에서 처리할 수 있도록)
        eventPublisher.publishEvent(
            FeedCreatedEvent(
                feedId = savedFeed.id,
                userId = savedFeed.userId,
                placeId = savedFeed.placeId,
                buyPlaceId = savedFeed.buyPlaceId,
                imageIds = feedImages.map { it.id }
            )
        )

        return savedFeed.id
    }

    override fun updateFeed(feedId: String, request: UpdateFeedRequest) {
        // 1. 기존 피드 조회
        val existingFeed = feedRepository.findByIdAndDeletedAtIsNull(feedId)
            ?: throw CustomException(HttpStatus.NOT_FOUND, "해당 피드를 찾을 수 없습니다.")

        // 2. 구매 장소 유효성 검사
        request.buyPlaceId?.let { buyPlaceId ->
            if (!placeRepository.existsById(buyPlaceId)) {
                throw CustomException(HttpStatus.BAD_REQUEST, "존재하지 않는 구매 장소입니다.")
            }
        }

        // 3. 업데이트된 피드 생성
        val updatedFeed = existingFeed.copy(
            score = request.score ?: existingFeed.score,
            text = request.text ?: existingFeed.text,
            buyPlaceId = request.buyPlaceId ?: existingFeed.buyPlaceId
        )

        // 4. 저장 및 이벤트 발행
        feedRepository.save(updatedFeed)
        eventPublisher.publishEvent(
            FeedUpdatedEvent(
                feedId = feedId,
                updatedFields = mapOf(
                    "score" to request.score,
                    "text" to request.text,
                    "buyPlaceId" to request.buyPlaceId
                )
            )
        )
    }

    override fun deleteFeed(feedId: String) {
        // 1. 피드 존재 확인
        val feed = feedRepository.findByIdAndDeletedAtIsNull(feedId)
            ?: throw CustomException(HttpStatus.NOT_FOUND, "해당 피드를 찾을 수 없습니다.")

        // 2. 소프트 삭제
        val deletedFeed = feed.copy(deletedAt = LocalDateTime.now())
        feedRepository.save(deletedFeed)

        // 3. 관련 이미지들 삭제
        feedImageRepository.deleteByFeedId(feedId)

        // 4. 이벤트 발행
        eventPublisher.publishEvent(FeedDeletedEvent(feedId = feedId))
    }

    private fun validateFeedCreation(request: CreateFeedRequest) {
        // 유효성 검사 로직 (기존과 동일)
        if (!userRepository.existsByIdAndDeletedAtIsNull(request.userId)) {
            throw CustomException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다.")
        }

        if (!placeRepository.existsById(request.placeId)) {
            throw CustomException(HttpStatus.BAD_REQUEST, "존재하지 않는 장소입니다.")
        }

        request.buyPlaceId?.let { buyPlaceId ->
            if (!placeRepository.existsById(buyPlaceId)) {
                throw CustomException(HttpStatus.BAD_REQUEST, "존재하지 않는 구매 장소입니다.")
            }
        }

        val orderNums = request.images.map { it.orderNum }
        if (orderNums.size != orderNums.toSet().size) {
            throw CustomException(HttpStatus.BAD_REQUEST, "이미지 순서에 중복이 있습니다.")
        }

        request.score?.let { score ->
            if (score < 0.0 || score > 5.0) {
                throw CustomException(HttpStatus.BAD_REQUEST, "평점은 0.0에서 5.0 사이여야 합니다.")
            }
        }
    }
}
