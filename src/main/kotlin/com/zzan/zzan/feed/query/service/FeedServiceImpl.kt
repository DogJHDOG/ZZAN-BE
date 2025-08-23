package com.zzan.zzan.feed.command.service

import com.zzan.zzan.api.feed.dto.*
import com.zzan.zzan.common.exception.CustomException
import com.zzan.zzan.feed.command.domain.Feed
import com.zzan.zzan.feed.command.domain.FeedImage
import com.zzan.zzan.feed.command.repository.FeedImageRepository
import com.zzan.zzan.feed.command.repository.FeedRepository
import com.zzan.zzan.feed.query.FeedQueryService
import com.zzan.zzan.place.command.repository.PlaceRepository
import com.zzan.zzan.user.command.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class FeedServiceImpl(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val feedQueryService: FeedQueryService
) : FeedService {

    override fun createFeed(request: CreateFeedRequest): FeedDetailResponse {
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

        // 4. 상세 정보 조회하여 반환
        return feedQueryService.getFeedById(savedFeed.id)
    }

    override fun updateFeed(feedId: String, request: UpdateFeedRequest): FeedDetailResponse {
        // 1. 기존 피드 조회
        val existingFeed = feedRepository.findByIdAndDeletedAtIsNull(feedId)
            ?: throw CustomException(HttpStatus.NOT_FOUND, "해당 피드를 찾을 수 없습니다.")

        // 2. 구매 장소 유효성 검사
        request.buyPlaceId?.let { buyPlaceId ->
            if (!placeRepository.existsById(buyPlaceId)) {
                throw CustomException(HttpStatus.BAD_REQUEST, "존재하지 않는 구매 장소입니다.")
            }
        }

        // 3. 업데이트된 피드 생성 (불변 객체이므로 새로 생성)
        val updatedFeed = existingFeed.copy(
            score = request.score ?: existingFeed.score,
            text = request.text ?: existingFeed.text,
            buyPlaceId = request.buyPlaceId ?: existingFeed.buyPlaceId
        )

        // 4. 저장 및 결과 반환
        feedRepository.save(updatedFeed)
        return feedQueryService.getFeedById(feedId)
    }

    override fun deleteFeed(feedId: String) {
        // 1. 피드 존재 확인
        val feed = feedRepository.findByIdAndDeletedAtIsNull(feedId)
            ?: throw CustomException(HttpStatus.NOT_FOUND, "해당 피드를 찾을 수 없습니다.")

        // 2. 소프트 삭제 (deletedAt 설정)
        val deletedFeed = feed.copy(deletedAt = LocalDateTime.now())
        feedRepository.save(deletedFeed)

        // 3. 관련 이미지들도 삭제 (물리적 삭제)
        feedImageRepository.deleteByFeedId(feedId)
    }

    private fun validateFeedCreation(request: CreateFeedRequest) {
        // 1. 사용자 존재 여부 확인
        if (!userRepository.existsByIdAndDeletedAtIsNull(request.userId)) {
            throw CustomException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다.")
        }

        // 2. 장소 존재 여부 확인
        if (!placeRepository.existsById(request.placeId)) {
            throw CustomException(HttpStatus.BAD_REQUEST, "존재하지 않는 장소입니다.")
        }

        // 3. 구매 장소 존재 여부 확인 (선택사항)
        request.buyPlaceId?.let { buyPlaceId ->
            if (!placeRepository.existsById(buyPlaceId)) {
                throw CustomException(HttpStatus.BAD_REQUEST, "존재하지 않는 구매 장소입니다.")
            }
        }

        // 4. 이미지 순서 중복 검사
        val orderNums = request.images.map { it.orderNum }
        if (orderNums.size != orderNums.toSet().size) {
            throw CustomException(HttpStatus.BAD_REQUEST, "이미지 순서에 중복이 있습니다.")
        }

        // 5. 평점 범위 검사 (annotation 검사 외 추가 검사)
        request.score?.let { score ->
            if (score < 0.0 || score > 5.0) {
                throw CustomException(HttpStatus.BAD_REQUEST, "평점은 0.0에서 5.0 사이여야 합니다.")
            }
        }
    }
}
