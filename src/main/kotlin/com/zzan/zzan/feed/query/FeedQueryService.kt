package com.zzan.zzan.feed.query

import com.zzan.zzan.api.feed.dto.*

interface FeedQueryService {
    /**
     * 특정 ID에 해당하는 피드 상세 정보를 조회합니다.
     *
     * @param feedId 조회할 피드의 ID
     * @return FeedDetailResponse 피드 상세 정보 응답 객체
     */
    fun getFeedById(feedId: String): FeedDetailResponse

    /**
     * 검색 조건과 페이징 정보에 따라 피드 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @param pageRequest 페이징 정보
     * @return PageResponse<FeedSummaryResponse> 페이징된 피드 목록
     */
    fun getFeeds(criteria: FeedSearchCriteria, pageRequest: PageRequest): PageResponse<FeedSummaryResponse>
}
