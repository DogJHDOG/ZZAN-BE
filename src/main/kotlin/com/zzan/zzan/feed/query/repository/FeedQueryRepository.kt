package com.zzan.zzan.feed.query.repository

import com.zzan.zzan.api.feed.dto.*
import com.zzan.zzan.feed.command.domain.Feed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FeedQueryRepository : JpaRepository<Feed, String> {

    /**
     * 피드 상세 조회 - Native Query나 직접 매핑으로 처리
     */
    @Query("""
        SELECT f FROM Feed f
        WHERE f.id = :feedId AND f.deletedAt IS NULL
    """)
    fun getFeedDetailByIdWithoutImages(@Param("feedId") feedId: String): Feed?

    /**
     * 특정 피드의 이미지 목록 조회
     */
    @Query("""
        SELECT fi FROM FeedImage fi 
        WHERE fi.feedId = :feedId 
        ORDER BY fi.orderNum
    """)
    fun getFeedImages(@Param("feedId") feedId: String): List<com.zzan.zzan.feed.command.domain.FeedImage>

    /**
     * 피드 목록 조회 (요약 정보) - 엔티티 조회 후 서비스에서 변환
     */
    @Query("""
        SELECT f FROM Feed f
        WHERE f.deletedAt IS NULL
        AND (:#{#criteria.userId} IS NULL OR f.userId = :#{#criteria.userId})
        AND (:#{#criteria.placeId} IS NULL OR f.placeId = :#{#criteria.placeId})
        AND (:#{#criteria.minScore} IS NULL OR f.score >= :#{#criteria.minScore})
        AND (:#{#criteria.maxScore} IS NULL OR f.score <= :#{#criteria.maxScore})
        AND (:#{#criteria.fromDate} IS NULL OR f.createdAt >= :#{#criteria.fromDate})
        AND (:#{#criteria.toDate} IS NULL OR f.createdAt <= :#{#criteria.toDate})
    """)
    fun getFeeds(@Param("criteria") criteria: FeedSearchCriteria, pageable: Pageable): Page<Feed>

    /**
     * 단순한 존재 여부 확인용 메서드
     */
    fun existsByIdAndDeletedAtIsNull(feedId: String): Boolean
}
