package com.zzan.zzan.feed.command.domain

import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "feeds")
@EntityListeners(AuditingEntityListener::class)
class Feed(
    @Id
    @Column(length = 26)
    val id: String = UlidCreator.getUlid().toString(),

    @Column(name = "user_id", length = 26)
    val userId: String, // 작성자 ID

    @Column(name = "image_url")
    val imageUrl: String, // 피드 대표 이미지 URL

    val score: Double? = null, // 피드 평점 (0~5)

    @Column(columnDefinition = "TEXT")
    val text: String? = null, // 피드 내용

    @Column(name = "place_id", length = 26)
    val placeId: String, // 피드 장소 ID

    @Column(name = "buy_place_id", length = 26)
    val buyPlaceId: String? = null, // 전통주 구매 장소 ID

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: LocalDateTime? = null,

    @Column(name = "deleted_at")
    val deletedAt: LocalDateTime? = null
)
