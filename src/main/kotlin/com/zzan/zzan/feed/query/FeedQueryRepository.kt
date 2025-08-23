package com.zzan.zzan.feed.query.repository

import com.zzan.zzan.api.feed.dto.*
import com.zzan.zzan.feed.command.domain.Feed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FeedQueryRepository : JpaRepository<Feed, String> {

    /**
     * 피드 상세 조회 - 이미지 정보는 별도 메서드로 분리
     */
    @Query("""
        SELECT new com.zzan.zzan.api.feed.dto.FeedDetailResponse(
            f.id, f.userId, u.nickname, u.profileImageUrl, f.imageUrl, f.score, f.text,
            new com.zzan.zzan.api.feed.dto.PlaceInfo(p.id, p.name, p.address, p.latitude, p.longitude),
            CASE WHEN bp IS NOT NULL THEN 
                new com.zzan.zzan.api.feed.dto.PlaceInfo(bp.id, bp.name, bp.address, bp.latitude, bp.longitude)
            ELSE NULL END,
            f.createdAt
        )
        FROM Feed f
        JOIN User u ON f.userId = u.id
        JOIN Place p ON f.placeId = p.id
        LEFT JOIN Place bp ON f.buyPlaceId = bp.id
        WHERE f.id = :feedId AND f.deletedAt IS NULL AND u.deletedAt IS NULL
    """)
    fun getFeedDetailByIdWithoutImages(feedId: String): FeedDetailResponseWithoutImages?

    /**
     * 특정 피드의 이미지 목록 조회
     */
    @Query("""
        SELECT new com.zzan.zzan.api.feed.dto.FeedImageInfo(fi.id, fi.imageUrl, fi.orderNum)
        FROM FeedImage fi 
        WHERE fi.feedId = :feedId 
        ORDER BY fi.orderNum
    """)
    fun getFeedImages(feedId: String): List<FeedImageInfo>

    /**
     * 피드 목록 조회 (요약 정보)
     */
    @Query("""
        SELECT new com.zzan.zzan.api.feed.dto.FeedSummaryResponse(
            f.id, f.userId, u.nickname, u.profileImageUrl, f.imageUrl, f.score, f.text, p.name, f.createdAt
        )
        FROM Feed f
        JOIN User u ON f.userId = u.id
        JOIN Place p ON f.placeId = p.id
        WHERE f.deletedAt IS NULL AND u.deletedAt IS NULL
        AND (:#{#criteria.userId} IS NULL OR f.userId = :#{#criteria.userId})
        AND (:#{#criteria.placeId} IS NULL OR f.placeId = :#{#criteria.placeId})
        AND (:#{#criteria.minScore} IS NULL OR f.score >= :#{#criteria.minScore})
        AND (:#{#criteria.maxScore} IS NULL OR f.score <= :#{#criteria.maxScore})
        AND (:#{#criteria.fromDate} IS NULL OR f.createdAt >= :#{#criteria.fromDate})
        AND (:#{#criteria.toDate} IS NULL OR f.createdAt <= :#{#criteria.toDate})
    """)
    fun getFeeds(criteria: FeedSearchCriteria, pageable: Pageable): Page<FeedSummaryResponse>

    /**
     * 단순 엔티티 조회 후 변환하는 방식 (대안)
     */
    @Query("""
        SELECT f FROM Feed f
        JOIN FETCH f.user u
        JOIN FETCH f.place p
        LEFT JOIN FETCH f.buyPlace bp
        WHERE f.id = :feedId AND f.deletedAt IS NULL AND u.deletedAt IS NULL
    """)
    fun getFeedEntityById(feedId: String): Feed?
}
