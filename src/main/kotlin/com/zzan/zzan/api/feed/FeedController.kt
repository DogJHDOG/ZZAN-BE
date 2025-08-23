package com.zzan.zzan.api.feed

import com.zzan.zzan.api.feed.dto.*
import com.zzan.zzan.common.response.ApiResponse
import com.zzan.zzan.feed.command.service.FeedService
import com.zzan.zzan.feed.query.FeedQueryService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/feeds")
class FeedController(
    private val feedService: FeedService,
    private val feedQueryService: FeedQueryService
) {

    /**
     * 피드 생성
     */
    @PostMapping
    fun createFeed(
        @Valid @RequestBody request: CreateFeedRequest
    ): ApiResponse<FeedDetailResponse> {
        val feed = feedService.createFeed(request)
        return ApiResponse.ok(feed)
    }

    /**
     * 피드 상세 조회
     */
    @GetMapping("/{feedId}")
    fun getFeedById(@PathVariable feedId: String): ApiResponse<FeedDetailResponse> {
        val feed = feedQueryService.getFeedById(feedId)
        return ApiResponse.ok(feed)
    }

    /**
     * 피드 목록 조회 (페이징, 필터링)
     */
    @GetMapping
    fun getFeeds(
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) placeId: String?,
        @RequestParam(required = false) minScore: Double?,
        @RequestParam(required = false) maxScore: Double?,
        @RequestParam(required = false) fromDate: String?,
        @RequestParam(required = false) toDate: String?,
        @Valid pageRequest: PageRequest
    ): ApiResponse<PageResponse<FeedSummaryResponse>> {

        val criteria = FeedSearchCriteria(
            userId = userId,
            placeId = placeId,
            minScore = minScore,
            maxScore = maxScore,
            fromDate = fromDate?.let { LocalDateTime.parse(it) },
            toDate = toDate?.let { LocalDateTime.parse(it) }
        )

        val feeds = feedQueryService.getFeeds(criteria, pageRequest)
        return ApiResponse.ok(feeds)
    }

    /**
     * 내 피드 목록 조회
     */
    @GetMapping("/my/{userId}")
    fun getMyFeeds(
        @PathVariable userId: String,
        @Valid pageRequest: PageRequest
    ): ApiResponse<PageResponse<FeedSummaryResponse>> {
        val criteria = FeedSearchCriteria(userId = userId)
        val feeds = feedQueryService.getFeeds(criteria, pageRequest)
        return ApiResponse.ok(feeds)
    }

    /**
     * 피드 수정
     */
    @PutMapping("/{feedId}")
    fun updateFeed(
        @PathVariable feedId: String,
        @Valid @RequestBody request: UpdateFeedRequest
    ): ApiResponse<FeedDetailResponse> {
        val updatedFeed = feedService.updateFeed(feedId, request)
        return ApiResponse.ok(updatedFeed)
    }

    /**
     * 피드 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{feedId}")
    fun deleteFeed(@PathVariable feedId: String): ApiResponse<String> {
        feedService.deleteFeed(feedId)
        return ApiResponse.ok("피드가 성공적으로 삭제되었습니다.")
    }

    /**
     * 특정 장소의 피드 목록 조회
     */
    @GetMapping("/place/{placeId}")
    fun getFeedsByPlace(
        @PathVariable placeId: String,
        @Valid pageRequest: PageRequest
    ): ApiResponse<PageResponse<FeedSummaryResponse>> {
        val criteria = FeedSearchCriteria(placeId = placeId)
        val feeds = feedQueryService.getFeeds(criteria, pageRequest)
        return ApiResponse.ok(feeds)
    }
}
