package com.zzan.zzan.feed.query.service

import com.zzan.zzan.api.feed.dto.*
import com.zzan.zzan.common.exception.CustomException
import com.zzan.zzan.feed.query.repository.FeedQueryRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class FeedQueryServiceImpl(
    private val feedQueryRepository: FeedQueryRepository
) : FeedQueryService {

    override fun getFeedById(feedId: String): FeedDetailResponse {
        // 1. 기본 피드 정보 조회
        val feedWithoutImages = feedQueryRepository.getFeedDetailByIdWithoutImages(feedId)
            ?: throw CustomException(HttpStatus.NOT_FOUND, "해당 피드를 찾을 수 없습니다.")

        // 2. 이미지 정보 별도 조회
        val images = feedQueryRepository.getFeedImages(feedId)

        // 3. 최종 응답 생성
        return FeedDetailResponse(
            id = feedWithoutImages.id,
            userId = feedWithoutImages.userId,
            userNickname = feedWithoutImages.userNickname,
            userProfileImageUrl = feedWithoutImages.userProfileImageUrl,
            imageUrl = feedWithoutImages.imageUrl,
            score = feedWithoutImages.score,
            text = feedWithoutImages.text,
            place = feedWithoutImages.place,
            buyPlace = feedWithoutImages.buyPlace,
            images = images,
            createdAt = feedWithoutImages.createdAt
        )
    }

    override fun getFeeds(
        criteria: FeedSearchCriteria,
        pageRequest: PageRequest
    ): PageResponse<FeedSummaryResponse> {

        val sort = Sort.by(
            if (pageRequest.sortDirection.uppercase() == "ASC")
                Sort.Direction.ASC
            else
                Sort.Direction.DESC,
            pageRequest.sortBy
        )

        val springPageRequest = SpringPageRequest.of(pageRequest.page, pageRequest.size, sort)
        val result = feedQueryRepository.getFeeds(criteria, springPageRequest)

        return PageResponse(
            content = result.content,
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            hasNext = result.hasNext(),
            hasPrevious = result.hasPrevious()
        )
    }
}
